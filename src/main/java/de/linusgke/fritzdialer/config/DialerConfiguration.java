package de.linusgke.fritzdialer.config;

import de.linusgke.fritzdialer.fritz.FritzBox;
import lombok.Data;

@Data
public class DialerConfiguration {

    public static DialerConfiguration DEFAULT_CONFIGURATION;

    static {
        DEFAULT_CONFIGURATION = new DialerConfiguration();
        DEFAULT_CONFIGURATION.setPhone(FritzBox.NO_SELECTION_PHONE.getPort());
        DEFAULT_CONFIGURATION.setDialClipboardHotkey("Strg+B");
        DEFAULT_CONFIGURATION.setDialSelectionHotkey("Strg+Y");
        DEFAULT_CONFIGURATION.setStartMinimized(false);

        final FritzBoxConfiguration fritzBoxConfiguration = new FritzBoxConfiguration();
        fritzBoxConfiguration.setAddress("fritz.box");
        fritzBoxConfiguration.setPort(80);
        fritzBoxConfiguration.setUsername("");
        fritzBoxConfiguration.setPassword("");
        DEFAULT_CONFIGURATION.setFritzBox(fritzBoxConfiguration);
    }

    private FritzBoxConfiguration fritzBox;
    private int phone;
    private String dialClipboardHotkey;
    private String dialSelectionHotkey;
    private boolean startMinimized;

    @Data
    public static class FritzBoxConfiguration {
        private String address;
        private int port;
        private String username;
        private String password;
    }
}
