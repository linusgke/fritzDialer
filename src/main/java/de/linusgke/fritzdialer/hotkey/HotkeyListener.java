package de.linusgke.fritzdialer.hotkey;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import de.linusgke.fritzdialer.FritzDialerApplication;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;

@Slf4j
public class HotkeyListener implements NativeKeyListener {

    private final FritzDialerApplication application;
    private long lastClipboardTimestamp;

    public HotkeyListener(final FritzDialerApplication application) {
        this.application = application;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        final String keyText = NativeKeyEvent.getKeyText(nativeEvent.getKeyCode());
        final String modifiersExText = NativeKeyEvent.getModifiersText(nativeEvent.getModifiers());
        final String fullKeyText = modifiersExText.isEmpty() ? keyText : modifiersExText + "+" + keyText;

        if (fullKeyText.equals(application.getConfiguration().getDialClipboardHotkey()) && System.currentTimeMillis() - lastClipboardTimestamp > 5000) {
            final String data = retrieveClipboardString();
            if (data != null) {
                application.getFritzBox().placeCall(data);
                lastClipboardTimestamp = System.currentTimeMillis();
            }
        }

        if (fullKeyText.equals(application.getConfiguration().getDialSelectionHotkey())) {
            final Transferable clipboardContents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

            try {
                final Robot robot = new Robot();
                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_C);
                robot.keyRelease(KeyEvent.VK_C);
                robot.keyRelease(KeyEvent.VK_CONTROL);
                Thread.sleep(100);
            } catch (final AWTException | InterruptedException e) {
                log.error("Error simulating copy shortcut", e);
            }

            final String data = retrieveClipboardString();
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(clipboardContents, null);

            if (data != null) {
                application.getFritzBox().placeCall(data);
            }
        }
    }

    private String retrieveClipboardString() {
        try {
            return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (final UnsupportedFlavorException | IOException e) {
            log.error("Error retrieving clipboard contents", e);
            return null;
        }
    }
}
