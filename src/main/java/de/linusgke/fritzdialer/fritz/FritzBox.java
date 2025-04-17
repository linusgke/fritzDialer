package de.linusgke.fritzdialer.fritz;

import de.linusgke.fritzdialer.FritzDialerApplication;
import de.linusgke.fritzdialer.fritz.tr064.TR064Connection;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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

    private TR064Connection connection;
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
            connection = new TR064Connection(address, username, password);
            connection.init(null);
        } catch (Exception e) {
            log.error("Error establishing TR064 connection with FRITZ!Box", e);
            application.getFrame().updateStatus("Anmeldung fehlgeschlagen: TR064 Fehler");
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
    }
}
