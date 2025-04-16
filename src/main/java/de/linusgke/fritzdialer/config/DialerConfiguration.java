package de.linusgke.fritzdialer.config;

import lombok.Data;

@Data
public class DialerConfiguration {

    public static DialerConfiguration DEFAULT_CONFIGURATION;

    static {
        DEFAULT_CONFIGURATION = new DialerConfiguration();
        final FritzBoxConfiguration fritzBoxConfiguration = new FritzBoxConfiguration();
        fritzBoxConfiguration.setAddress("fritz.box");
        fritzBoxConfiguration.setPort(80);
        fritzBoxConfiguration.setUsername("");
        fritzBoxConfiguration.setPassword("");
        DEFAULT_CONFIGURATION.setFritzBox(fritzBoxConfiguration);
    }

    private FritzBoxConfiguration fritzBox;

    @Data
    public static class FritzBoxConfiguration {
        private String address;
        private int port;
        private String username;
        private String password;
    }
}
