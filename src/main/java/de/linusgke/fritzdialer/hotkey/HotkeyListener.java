package de.linusgke.fritzdialer.hotkey;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HotkeyListener implements NativeKeyListener {

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        log.info(nativeEvent.paramString() + " pressed");
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
    }
}
