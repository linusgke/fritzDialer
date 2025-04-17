package de.linusgke.fritzdialer.fritz;

import de.linusgke.fritzdialer.FritzDialerApplication;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jfritz.fboxlib.exceptions.*;
import org.jfritz.fboxlib.fritzbox.FirmwareVersion;
import org.jfritz.fboxlib.fritzbox.FritzBoxCommunication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Slf4j
@Getter
public class FritzBox {

    private final FritzDialerApplication application;
    private final List<Phone> phones = new ArrayList<>();

    private FritzBoxCommunication communication;
    private Thread communicationThread;
    private boolean connected;

    public FritzBox(final FritzDialerApplication application) {
        this.application = application;
    }

    public void connect() {
        communicationThread = new Thread(this::initialize);
        communicationThread.start();
    }

    public void call(final String phoneNumber) {
        try {
            final List<NameValuePair> postData = new ArrayList<>();
            postData.add(new BasicNameValuePair("clicktodial", "on"));
            postData.add(new BasicNameValuePair("port", "51")); // port.getDialPort()
            postData.add(new BasicNameValuePair("btn_apply", ""));
            postData.add(new BasicNameValuePair("sid", communication.getSid()));
            postData.add(new BasicNameValuePair("page", "telDial"));
            communication.postToPageAndGetAsString("/data.lua", postData);

            String dial_query = "useajax=1&xhr=1&dial=" + phoneNumber;
            dial_query = dial_query.replace("#", "%23"); // # %23
            dial_query = dial_query.replace("*", "%2A"); // * %2A
            communication.getPageAsString("/fon_num/foncalls_list.lua?" + dial_query);
        } catch (Exception e) {
            log.error("Error while connecting to FRITZ!Box", e);
        }
    }

    private void initialize() {
        final String address = application.getConfiguration().getFritzBox().getAddress();
        final String username = application.getConfiguration().getFritzBox().getUsername();
        final String password = application.getConfiguration().getFritzBox().getPassword();

        log.info("Connecting to FRITZ!Box at {} ...", address);
        application.getFrame().updateStatus("Verbinden mit " + address + " ...");

        if (address.isEmpty() || username.isEmpty() || password.isEmpty()) {
            log.warn("Credentials incomplete. Skipping login");
            application.getFrame().updateStatus("Anmeldung fehlgeschlagen: Ungültige Zugangsdaten");
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
            application.getFrame().updateStatus("Anmeldung fehlgeschlagen: HTTP Fehler");
            return;
        }

        final FirmwareVersion firmwareVersion;
        try {
            firmwareVersion = communication.getFirmwareVersion();
        } catch (IOException | FirmwareNotDetectedException | PageNotFoundException e) {
            log.warn("Error detecting firmware", e);
            application.getFrame().updateStatus("Anmeldung fehlgeschlagen: Firmware konnte nicht festgestellt werden");
            return;
        }

        connected = true;
        log.info("Connected to FRITZ!Box ({})", firmwareVersion);
        application.getFrame().updateStatus("Verbunden mit " + address + ": " + firmwareVersion);

        requestPhones();
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

    private void requestPhones() {
        // Remove all existing phones
        phones.clear();

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

                phones.add(new Phone(firstDialPort + i, name));
            }
        }

        log.info("Found {} phones", phones.size());
        application.getFrame().updatePhones(phones);
    }
}
