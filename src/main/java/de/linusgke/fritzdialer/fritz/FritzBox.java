package de.linusgke.fritzdialer.fritz;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import de.linusgke.fritzdialer.FritzDialerApplication;
import de.linusgke.fritzdialer.window.PhoneSelectionDialog;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jfritz.fboxlib.exceptions.*;
import org.jfritz.fboxlib.fritzbox.FirmwareVersion;
import org.jfritz.fboxlib.fritzbox.FritzBoxCommunication;

import java.io.IOException;
import java.util.*;

@Slf4j
@Getter
public class FritzBox {

    public static final Phone NO_SELECTION_PHONE = new Phone(-1, "Bei Anruf fragen");

    private final FritzDialerApplication application;
    private final Map<Integer, Phone> phoneMap = new HashMap<>();

    private FritzBoxCommunication communication;
    private Thread communicationThread;
    private boolean ready;

    public FritzBox(final FritzDialerApplication application) {
        this.application = application;
    }

    public void connect() {
        communicationThread = new Thread(this::initialize);
        communicationThread.start();
    }

    public void placeCall(final String rawPhoneNumber) {
        final int phonePort = application.getConfiguration().getPhone();
        placeCall(rawPhoneNumber, phonePort);
    }

    private void placeCall(final String rawPhoneNumber, final int phonePort) {
        final String phoneNumber;
        try {
            phoneNumber = parseNumber(rawPhoneNumber).replaceAll("\\+", "00");
        } catch (final NumberParseException e) {
            log.warn("Invalid phone number '{}' given", rawPhoneNumber, e);
            return;
        }

        if (!application.getFritzBox().isReady()) {
            log.warn("Cancelled call to '{}': connection not ready", phoneNumber);
            return;
        }

        log.info("Placing call to '{}'", phoneNumber);

        if (phonePort == NO_SELECTION_PHONE.getPort()) {
            log.info("Opening phone selection dialog");
            new PhoneSelectionDialog(application, selectedPhone -> placeCall(phoneNumber, selectedPhone.getPort()));
            return;
        }

        try {
            final List<NameValuePair> postData = new ArrayList<>();
            postData.add(new BasicNameValuePair("clicktodial", "on"));
            postData.add(new BasicNameValuePair("port", Integer.toString(phonePort)));
            postData.add(new BasicNameValuePair("btn_apply", ""));
            postData.add(new BasicNameValuePair("sid", communication.getSid()));
            postData.add(new BasicNameValuePair("page", "telDial"));
            communication.postToPageAndGetAsString("/data.lua", postData);

            String dial_query = "useajax=1&xhr=1&dial=" + phoneNumber;
            dial_query = dial_query.replace("#", "%23");
            dial_query = dial_query.replace("*", "%2A");
            communication.getPageAsString("/fon_num/foncalls_list.lua?" + dial_query);
        } catch (Exception e) {
            log.error("Error while connecting to FRITZ!Box", e);
        }
    }

    public Phone getPhoneByPort(final int port) {
        return phoneMap.get(port);
    }

    public Collection<Phone> getPhones() {
        return phoneMap.values();
    }

    private void initialize() {
        ready = false;
        application.getFrame().update();

        final String address = application.getConfiguration().getFritzBox().getAddress();
        final String username = application.getConfiguration().getFritzBox().getUsername();
        final String password = application.getConfiguration().getFritzBox().getPassword();

        log.info("Connecting to FRITZ!Box at {} ...", address);

        if (address.isEmpty() || username.isEmpty() || password.isEmpty()) {
            log.warn("Credentials incomplete. Skipping login");
            return;
        }

        try {
            communication = new FritzBoxCommunication("http", application.getConfiguration().getFritzBox().getAddress(), Integer.toString(application.getConfiguration().getFritzBox().getPort()));
            communication.detectLoginMethod();
            communication.setUserName(application.getConfiguration().getFritzBox().getUsername());
            communication.setPassword(application.getConfiguration().getFritzBox().getPassword());
            communication.login();
        } catch (Exception e) {
            log.error("Error establishing HTTP connection with FRITZ!Box", e);
            return;
        }

        final FirmwareVersion firmwareVersion;
        try {
            firmwareVersion = communication.getFirmwareVersion();
        } catch (IOException | FirmwareNotDetectedException | PageNotFoundException e) {
            log.warn("Error detecting firmware", e);
            return;
        }

        log.info("Connected to FRITZ!Box ({})", firmwareVersion);

        // Remove all existing phones
        phoneMap.clear();

        for (final PhoneType type : PhoneType.values()) {
            final Vector<String> countResponse = query(type.getCountQuery());
            if (countResponse == null || countResponse.size() != 1) {
                log.error("Invalid or null response querying '{}' phone count", type);
                continue;
            }

            final int firstDialPort = type.getFirstDialPort();
            final int count = Integer.parseInt(countResponse.getFirst());
            for (int i = 0; i < count; i++) {
                final Vector<String> nameResponse = query(String.format(type.getNameQuery(), i));
                if (nameResponse == null || nameResponse.size() != 1) {
                    log.error("Invalid or null response querying '{} {}' phone name", type, i);
                    continue;
                }

                final String name = nameResponse.getFirst();
                if (name.isEmpty()) {
                    continue;
                }

                final int port = firstDialPort + i;
                phoneMap.put(port, new Phone(port, name));
            }
        }

        ready = true;

        log.info("Registered {} phones", phoneMap.size());
        application.getFrame().update();
    }

    private Vector<String> query(final String query) {
        try {
            return getCommunication().getQuery(new Vector<>(List.of(query)));
        } catch (IOException | LoginBlockedException | InvalidCredentialsException | PageNotFoundException |
                 InvalidSessionIdException e) {
            log.error("Error querying FRITZ!Box with query '{}'", query, e);
            return null;
        }
    }

    private String parseNumber(final String number) throws NumberParseException {
        final PhoneNumberUtil instance = PhoneNumberUtil.getInstance();
        final Phonenumber.PhoneNumber phoneNumber = instance.parse(number, Locale.getDefault().getCountry());
        return instance.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
    }
}
