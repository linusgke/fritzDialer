package de.linusgke.fritzdialer.fritz;

import de.linusgke.fritzdialer.FritzDialerApplication;
import de.linusgke.fritzdialer.fritz.tr064.FritzConnection;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jfritz.fboxlib.exceptions.FirmwareNotDetectedException;
import org.jfritz.fboxlib.exceptions.PageNotFoundException;
import org.jfritz.fboxlib.fritzbox.FritzBoxCommunication;

import java.io.IOException;

@Slf4j
@Getter
public class FritzBox {

    private final FritzDialerApplication application;

    private FritzConnection connection;
    private FritzBoxCommunication communication;
    private boolean connected;

    public FritzBox(final FritzDialerApplication application) {
        this.application = application;

        connect();
    }

    public void connect() {
        try {
            this.connection = new FritzConnection(
                    application.getConfiguration().getFritzBox().getAddress(),
                    application.getConfiguration().getFritzBox().getUsername(),
                    application.getConfiguration().getFritzBox().getPassword()
            );
            this.connection.init(null);
        } catch (Exception e) {
            log.error("Error establishing TR064 connection with FRITZ!Box", e);
            return;
        }

        try {
            this.communication = new FritzBoxCommunication(
                    "http",
                    application.getConfiguration().getFritzBox().getAddress(),
                    Integer.toString(application.getConfiguration().getFritzBox().getPort())
            );
            this.communication.detectLoginMethod();
            this.communication.setUserName(application.getConfiguration().getFritzBox().getUsername());
            this.communication.setPassword(application.getConfiguration().getFritzBox().getPassword());
            this.communication.login();
        } catch (Exception e) {
            log.error("Error establishing HTTP connection with FRITZ!Box", e);
            return;
        }

        connected = true;

        log.info("Connected to FRITZ!Box");
        try {
            log.info("Firmware detected: {}", this.communication.getFirmwareVersion());
        } catch (IOException | FirmwareNotDetectedException | PageNotFoundException e) {
            log.warn("Error detecting firmware", e);
        }
    }
}
