package de.linusgke.fritzdialer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Bootstrap {

    public static void main(final String[] args) {
        final FritzDialerApplication application = new FritzDialerApplication();

        // Shutdown hook for doing important things on CTRL + C
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                application.shutdown();
            } catch (final Exception e) {
                log.error("Unhandled exception occurred on shutdown", e);
            }
        }));

        // Run application
        try {
            application.startup(args);
        } catch (final Exception e) {
            log.error("Unhandled exception occurred on startup", e);
        }
    }
}
