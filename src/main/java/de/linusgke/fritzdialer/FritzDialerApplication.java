package de.linusgke.fritzdialer;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.linusgke.fritzdialer.config.DialerConfiguration;
import de.linusgke.fritzdialer.fritz.FritzBox;
import de.linusgke.fritzdialer.hotkey.HotkeyListener;
import de.linusgke.fritzdialer.tray.DialerTrayIcon;
import de.linusgke.fritzdialer.window.DialerFrame;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
@Slf4j
public class FritzDialerApplication {

    private static final Path CONFIGURATION_PATH = Path.of(System.getenv("APPDATA") + "\\FritzDialer\\config.json");

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private DialerConfiguration configuration;
    private FritzBox fritzBox;
    private DialerFrame frame;
    private DialerTrayIcon trayIcon;

    public void startup(final String[] args) {
        // Register native hotkey hook
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new HotkeyListener(this));
        } catch (final Exception e) {
            log.error("Error while registering native hook", e);
            System.exit(1);
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception e) {
            log.error("Error while setting look and feel", e);
            System.exit(1);
        }

        try {
            configuration = readConfiguration();
        } catch (final IOException e) {
            log.error("Error loading configuration", e);
        }

        // Create FRITZ!Box connection
        fritzBox = new FritzBox(this);

        // Create window
        frame = new DialerFrame(this);

        // Create tray icon
        trayIcon = new DialerTrayIcon(this);

        // Connect to FRITZ!Box
        fritzBox.connect();
    }

    public void shutdown() {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (final NativeHookException e) {
            log.error("Error while unregistering native hook", e);
        }
    }

    private DialerConfiguration readConfiguration() throws IOException {
        if (!Files.exists(CONFIGURATION_PATH)) {
            log.warn("Using default configuration since configuration file does not exist");
            return DialerConfiguration.DEFAULT_CONFIGURATION;
        }

        final DialerConfiguration config;
        try (final FileReader reader = new FileReader(CONFIGURATION_PATH.toFile(), StandardCharsets.UTF_8)) {
            config = gson.fromJson(reader, DialerConfiguration.class);
        }
        return config;
    }

    public void saveConfiguration() throws IOException {
        if (!Files.exists(CONFIGURATION_PATH)) {
            Files.createDirectories(CONFIGURATION_PATH.getParent());
            Files.createFile(CONFIGURATION_PATH);
        }

        try (final FileWriter writer = new FileWriter(CONFIGURATION_PATH.toFile(), StandardCharsets.UTF_8)) {
            gson.toJson(configuration, DialerConfiguration.class, writer);
        } catch (final IOException e) {
            log.error("Error while saving configuration", e);
        }
    }
}
