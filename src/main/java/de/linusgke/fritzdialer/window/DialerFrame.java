package de.linusgke.fritzdialer.window;

import de.linusgke.fritzdialer.FritzDialerApplication;
import de.linusgke.fritzdialer.config.DialerConfiguration;
import de.linusgke.fritzdialer.fritz.FritzBox;
import de.linusgke.fritzdialer.fritz.Phone;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

@Slf4j
public class DialerFrame extends JFrame {

    private final FritzDialerApplication application;

    private JTextField addressInput;
    private JTextField portInput;
    private JTextField usernameInput;
    private JPasswordField passwordInput;
    private JComboBox<Phone> phoneInput;
    private JTextField callClipboardHotkeyInput;
    private JTextField callSelectionHotkeyInput;
    private JCheckBox autostartCheckBox;
    private JCheckBox startMinimizedCheckBox;
    private JLabel statusLabel;

    public DialerFrame(final FritzDialerApplication application) {
        this.application = application;

        setTitle("FritzDialer");
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setSize(400, 370);
        setResizable(false);
        setLocationRelativeTo(null);

        final Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/icon.png"));
        setIconImage(image);

        // Add top menu bar
        addMenuBar();

        final JPanel contentPane = new JPanel();
        contentPane.setLayout(null);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        fillContentPane(contentPane);

        // Load data from configuration
        update();

        setContentPane(contentPane);
        setVisible(!application.getConfiguration().isStartMinimized());
    }

    public void update() {
        final DialerConfiguration configuration = application.getConfiguration();

        if (application.getFritzBox().isReady()) {
            statusLabel.setText("Bereit");
            phoneInput.removeAllItems();
            phoneInput.addItem(FritzBox.NO_SELECTION_PHONE);
            for (final Phone phone : application.getFritzBox().getPhones()) {
                phoneInput.addItem(phone);
            }

            final int selectedPhonePort = configuration.getPhone();
            final Phone selectedPhone = application.getFritzBox().getPhoneByPort(selectedPhonePort);
            if (selectedPhone == null) {
                phoneInput.setSelectedIndex(0);
            } else {
                phoneInput.setSelectedItem(selectedPhone);
            }

            phoneInput.setEnabled(true);
        } else {
            statusLabel.setText("Nicht bereit");
            phoneInput.removeAllItems();
            phoneInput.setEnabled(false);
        }

        callClipboardHotkeyInput.setText(configuration.getDialClipboardHotkey());
        callSelectionHotkeyInput.setText(configuration.getDialSelectionHotkey());
        autostartCheckBox.setSelected(configuration.isAutostart());
        startMinimizedCheckBox.setSelected(configuration.isStartMinimized());

        final DialerConfiguration.FritzBoxConfiguration fritzBoxConfiguration = configuration.getFritzBox();
        addressInput.setText(fritzBoxConfiguration.getAddress());
        portInput.setText(Integer.toString(fritzBoxConfiguration.getPort()));
        usernameInput.setText(fritzBoxConfiguration.getUsername());
        passwordInput.setText(fritzBoxConfiguration.getPassword());
    }

    private void save() {
        final DialerConfiguration configuration = application.getConfiguration();

        Phone selectedPhone = (Phone) phoneInput.getSelectedItem();
        if (selectedPhone == null)
            selectedPhone = FritzBox.NO_SELECTION_PHONE;

        configuration.setPhone(selectedPhone.getPort());
        configuration.setDialClipboardHotkey(callClipboardHotkeyInput.getText());
        configuration.setDialSelectionHotkey(callSelectionHotkeyInput.getText());
        configuration.setAutostart(autostartCheckBox.isSelected());
        configuration.setStartMinimized(startMinimizedCheckBox.isSelected());

        boolean fritzBoxConfigurationChanged = false;
        final DialerConfiguration.FritzBoxConfiguration fritzBoxConfiguration = configuration.getFritzBox();

        final String address = addressInput.getText();
        if (!address.equals(fritzBoxConfiguration.getAddress())) {
            fritzBoxConfiguration.setAddress(address);
            fritzBoxConfigurationChanged = true;
        }

        final int port = Integer.parseInt(portInput.getText());
        if (port != fritzBoxConfiguration.getPort()) {
            fritzBoxConfiguration.setPort(port);
            fritzBoxConfigurationChanged = true;
        }

        final String username = usernameInput.getText();
        if (!username.equals(fritzBoxConfiguration.getUsername())) {
            fritzBoxConfiguration.setUsername(username);
            fritzBoxConfigurationChanged = true;
        }

        final String password = String.valueOf(passwordInput.getPassword());
        if (!password.equals(fritzBoxConfiguration.getPassword())) {
            fritzBoxConfiguration.setPassword(password);
            fritzBoxConfigurationChanged = true;
        }

        try {
            application.saveConfiguration();
        } catch (final IOException ex) {
            JOptionPane.showMessageDialog(application.getFrame(), "Fehler beim Speichern der Konfiguration: " + ex, "Konfiguration speichern", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (fritzBoxConfigurationChanged) {
            application.getFritzBox().connect();
        }
    }

    private void addMenuBar() {
        final JMenuBar menuBar = new JMenuBar();

        // File menu
        final JMenu fileMenu = new JMenu("Datei");
        fileMenu.setMnemonic('D');

        final JMenuItem hideItem = new JMenuItem("Ausblenden");
        hideItem.setMnemonic('A');
        hideItem.addActionListener(e -> setVisible(false));
        fileMenu.add(hideItem);

        final JMenuItem quitItem = new JMenuItem("Beenden");
        quitItem.setMnemonic('B');
        quitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(quitItem);

        // About menu
        final JMenu aboutMenu = new JMenu("Info");
        aboutMenu.setMnemonic('I');
        aboutMenu.addActionListener(e -> {

        });

        menuBar.add(fileMenu);
        menuBar.add(aboutMenu);

        // Add menubar to frame
        setJMenuBar(menuBar);
    }

    private void fillContentPane(final JPanel contentPane) {
        final JSeparator separator = new JSeparator();
        separator.setBounds(0, 272, 384, 2);
        contentPane.add(separator);

        final JLabel addressLabel = new JLabel("IP-Adresse");
        addressLabel.setBounds(40, 14, 71, 14);
        contentPane.add(addressLabel);

        addressInput = new JTextField();
        addressInput.setBounds(121, 11, 185, 20);
        addressInput.setColumns(10);
        contentPane.add(addressInput);

        final JLabel portLabel = new JLabel("Port");
        portLabel.setBounds(40, 42, 71, 14);
        contentPane.add(portLabel);

        portInput = new JTextField();
        portInput.setBounds(121, 39, 185, 20);
        portInput.setColumns(10);
        contentPane.add(portInput);

        final JLabel usernameLabel = new JLabel("Benutzername");
        usernameLabel.setBounds(40, 70, 71, 14);
        contentPane.add(usernameLabel);

        usernameInput = new JTextField();
        usernameInput.setBounds(121, 67, 185, 20);
        usernameInput.setColumns(10);
        contentPane.add(usernameInput);

        final JLabel passwordLabel = new JLabel("Passwort");
        passwordLabel.setBounds(40, 98, 71, 14);
        contentPane.add(passwordLabel);

        passwordInput = new JPasswordField();
        passwordInput.setBounds(121, 95, 185, 20);
        contentPane.add(passwordInput);

        phoneInput = new JComboBox<>();
        phoneInput.setBounds(121, 123, 185, 22);
        contentPane.add(phoneInput);

        final JLabel phoneLabel = new JLabel("Telefon");
        phoneLabel.setBounds(40, 127, 71, 14);
        contentPane.add(phoneLabel);

        final JLabel callClipboardHotkeyLabel = new JLabel("Zwischenablage wählen");
        callClipboardHotkeyLabel.setBounds(40, 159, 113, 14);
        contentPane.add(callClipboardHotkeyLabel);

        final JLabel callSelectionHotkeyLabel = new JLabel("Markierung wählen");
        callSelectionHotkeyLabel.setBounds(40, 187, 95, 14);
        contentPane.add(callSelectionHotkeyLabel);

        callClipboardHotkeyInput = new JTextField();
        callClipboardHotkeyInput.setBounds(163, 156, 143, 20);
        contentPane.add(callClipboardHotkeyInput);

        callSelectionHotkeyInput = new JTextField();
        callSelectionHotkeyInput.setBounds(163, 184, 143, 20);
        contentPane.add(callSelectionHotkeyInput);

        startMinimizedCheckBox = new JCheckBox("Minimiert starten");
        startMinimizedCheckBox.setBounds(193, 208, 113, 23);
        contentPane.add(startMinimizedCheckBox);

        autostartCheckBox = new JCheckBox("Mit Windows starten");
        autostartCheckBox.setBounds(40, 208, 128, 23);
        contentPane.add(autostartCheckBox);

        final JButton saveButton = new JButton("Speichern");
        saveButton.setBounds(217, 238, 89, 23);
        saveButton.addActionListener(e -> {
            SwingUtilities.invokeLater(this::save);
        });
        contentPane.add(saveButton);

        statusLabel = new JLabel("...");
        statusLabel.setBounds(10, 285, 300, 14);
        contentPane.add(statusLabel);
    }
}
