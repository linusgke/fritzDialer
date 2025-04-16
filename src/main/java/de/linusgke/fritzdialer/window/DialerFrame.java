package de.linusgke.fritzdialer.window;

import de.linusgke.fritzdialer.FritzDialerApplication;
import de.linusgke.fritzdialer.config.DialerConfiguration;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

@Slf4j
public class DialerFrame extends JFrame {

    private final FritzDialerApplication application;

    public DialerFrame(final FritzDialerApplication application) {
        this.application = application;

        setTitle("FritzDialer");
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setSize(400, 370);
        setResizable(false);
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

        final JPanel contentPane = new JPanel();
        contentPane.setLayout(null);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        final JSeparator separator = new JSeparator();
        separator.setBounds(0, 272, 384, 2);
        contentPane.add(separator);

        final JLabel statusLabel = new JLabel("Verbunden mit 192.168.1.1");
        statusLabel.setBounds(10, 285, 143, 14);
        contentPane.add(statusLabel);

        final JLabel addressLabel = new JLabel("IP-Adresse");
        addressLabel.setBounds(40, 14, 71, 14);
        contentPane.add(addressLabel);

        final JTextField addressInput = new JTextField();
        addressInput.setBounds(121, 11, 185, 20);
        addressInput.setColumns(10);
        addressInput.setText(application.getConfiguration().getFritzBox().getAddress());
        contentPane.add(addressInput);

        final JLabel portLabel = new JLabel("Port");
        portLabel.setBounds(40, 42, 71, 14);
        contentPane.add(portLabel);

        final JTextField portInput = new JTextField();
        portInput.setBounds(121, 39, 185, 20);
        portInput.setColumns(10);
        portInput.setText(Integer.toString(application.getConfiguration().getFritzBox().getPort()));
        contentPane.add(portInput);

        final JLabel usernameLabel = new JLabel("Benutzername");
        usernameLabel.setBounds(40, 70, 71, 14);
        contentPane.add(usernameLabel);

        final JTextField usernameInput = new JTextField();
        usernameInput.setBounds(121, 67, 185, 20);
        usernameInput.setColumns(10);
        usernameInput.setText(application.getConfiguration().getFritzBox().getUsername());
        contentPane.add(usernameInput);

        final JLabel passwordLabel = new JLabel("Passwort");
        passwordLabel.setBounds(40, 98, 71, 14);
        contentPane.add(passwordLabel);

        final JPasswordField passwordInput = new JPasswordField();
        passwordInput.setBounds(121, 95, 185, 20);
        passwordInput.setText(application.getConfiguration().getFritzBox().getPassword());
        contentPane.add(passwordInput);

        final JButton saveButton = new JButton("Speichern");
        saveButton.setBounds(217, 238, 89, 23);
        saveButton.addActionListener(e -> {
            final DialerConfiguration.FritzBoxConfiguration fritzBoxConfiguration = application.getConfiguration().getFritzBox();

            fritzBoxConfiguration.setAddress(addressInput.getText());
            fritzBoxConfiguration.setPort(Integer.parseInt(portInput.getText()));
            fritzBoxConfiguration.setUsername(usernameInput.getText());
            fritzBoxConfiguration.setPassword(String.valueOf(passwordInput.getPassword()));

            try {
                application.saveConfiguration();
            } catch (final IOException ex) {
                JOptionPane.showMessageDialog(application.getFrame(), "Fehler beim Speichern der Konfiguration: " + ex, "Konfiguration speichern", JOptionPane.ERROR_MESSAGE);
            }
        });
        contentPane.add(saveButton);

        final JComboBox phoneComboBox = new JComboBox();
        phoneComboBox.setBounds(121, 123, 185, 22);
        contentPane.add(phoneComboBox);

        final JLabel phoneLabel = new JLabel("Telefon");
        phoneLabel.setBounds(40, 127, 71, 14);
        contentPane.add(phoneLabel);

        final JCheckBox startMinimizedCheckbox = new JCheckBox("Minimiert starten");
        startMinimizedCheckbox.setBounds(193, 208, 113, 23);
        contentPane.add(startMinimizedCheckbox);

        final JCheckBox autostartCheckbox = new JCheckBox("Mit Windows starten");
        autostartCheckbox.setBounds(40, 208, 128, 23);
        contentPane.add(autostartCheckbox);

        final JLabel callClipboardHotkeyLabel = new JLabel("Zwischenablage wählen");
        callClipboardHotkeyLabel.setBounds(40, 159, 113, 14);
        contentPane.add(callClipboardHotkeyLabel);

        final JLabel callSelectionHotkeyLabel = new JLabel("Markierung wählen");
        callSelectionHotkeyLabel.setBounds(40, 187, 95, 14);
        contentPane.add(callSelectionHotkeyLabel);

        final JTextField callClipboardHotkeyInput = new JTextField();
        callClipboardHotkeyInput.setBounds(163, 156, 143, 20);
        contentPane.add(callClipboardHotkeyInput);

        final JTextField callSelectionHotkeyInput = new JTextField();
        callSelectionHotkeyInput.setBounds(163, 184, 143, 20);
        contentPane.add(callSelectionHotkeyInput);

        setContentPane(contentPane);
        setVisible(true);
    }
}
