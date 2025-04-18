package de.linusgke.fritzdialer.tray;

import de.linusgke.fritzdialer.FritzDialerApplication;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

@Slf4j
public class DialerTrayIcon {

    private final FritzDialerApplication application;

    public DialerTrayIcon(final FritzDialerApplication application) {
        this.application = application;

        initialize();
    }

    private void initialize() {
        final SystemTray tray = SystemTray.getSystemTray();
        final Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png"));
        final TrayIcon trayIcon = new TrayIcon(image, "FritzDialer");

        trayIcon.setImageAutoSize(true);
        trayIcon.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    application.getFrame().setVisible(!application.getFrame().isVisible());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        final PopupMenu menu = new PopupMenu("N/A");
        menu.add(createItem("Beenden", e -> System.exit(0)));
        trayIcon.setPopupMenu(menu);

        try {
            tray.add(trayIcon);
        } catch (final AWTException e) {
            log.error("Error adding tray icon", e);
        }
    }

    private MenuItem createItem(final String label, final ActionListener listener) {
        final MenuItem item = new MenuItem(label);
        item.addActionListener(listener);
        return item;
    }
}
