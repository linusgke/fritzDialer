package de.linusgke.fritzdialer.hotkey;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import de.linusgke.fritzdialer.FritzDialerApplication;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Locale;

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
            try {
                final String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                final String formattedNumber = parseNumber(data);
                application.getFritzBox().placeCall(formattedNumber);
                lastClipboardTimestamp = System.currentTimeMillis();
            } catch (final UnsupportedFlavorException | IOException | NumberParseException e) {
                throw new RuntimeException(e);
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
                throw new RuntimeException(e);
            }

            try {
                final String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                final String formattedNumber = parseNumber(data);
                application.getFritzBox().placeCall(formattedNumber);
            } catch (final UnsupportedFlavorException | IOException | NumberParseException e) {
                throw new RuntimeException(e);
            }

            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(clipboardContents, null);
        }
    }

    private String parseNumber(final String number) throws NumberParseException {
        final PhoneNumberUtil instance = PhoneNumberUtil.getInstance();
        final Phonenumber.PhoneNumber phoneNumber = instance.parse(number, Locale.getDefault().getCountry());
        return instance.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
    }
}
