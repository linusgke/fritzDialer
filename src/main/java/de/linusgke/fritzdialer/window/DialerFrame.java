package de.linusgke.fritzdialer.window;

import de.linusgke.fritzdialer.FritzDialerApplication;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class DialerFrame extends JFrame {

    private final FritzDialerApplication application;

    public DialerFrame(final FritzDialerApplication application) {
        this.application = application;

        setTitle("FritzDialer");
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(400, 200);
        setLocationRelativeTo(null);

        final Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png"));
        setIconImage(image);

        final JMenuBar menuBar = new JMenuBar();

        // File menu
        final JMenu fileMenu = new JMenu("Datei");
        fileMenu.setMnemonic('D');

        // About menu
        final JMenu aboutMenu = new JMenu("?");
        aboutMenu.setMnemonic('?');

        final JMenuItem infoItem = new JMenuItem("Info...");
        infoItem.setMnemonic('I');
        infoItem.addActionListener(e -> {
        });
        aboutMenu.add(infoItem);

        menuBar.add(fileMenu);
        menuBar.add(aboutMenu);

        // Add menubar to frame
        setJMenuBar(menuBar);

        setVisible(true);
    }
}
