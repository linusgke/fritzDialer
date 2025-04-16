package de.linusgke.fritzdialer.config;

import lombok.Data;

@Data
public class DialerConfiguration {

    private FritzBoxConfiguration fritzBox;

    @Data
    public static class FritzBoxConfiguration {
        private String address;
        private int port;
        private String username;
        private String password;
    }
}
