package de.linusgke.fritzdialer.window;

import de.linusgke.fritzdialer.FritzDialerApplication;
import de.linusgke.fritzdialer.fritz.Phone;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public class PhoneSelectionDialog extends JDialog {

    public PhoneSelectionDialog(final FritzDialerApplication application, final Consumer<Phone> callback) {
        super(application.getFrame(), true);

        setTitle("Telefon wählen...");
        setSize(200, 100);
        setResizable(false);
        setLocationRelativeTo(null);

        final JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);

        final JComboBox<Phone> phoneInput = new JComboBox<>();
        for (final Phone phone : application.getFritzBox().getApplication().getFritzBox().getPhones()) {
            phoneInput.addItem(phone);
        }

        final JButton confirmButton = new JButton("Anrufen");
        confirmButton.addActionListener(e -> {
            dispose();
            callback.accept((Phone) phoneInput.getSelectedItem());
        });

        final JButton cancelButton = new JButton("Abbrechen");
        cancelButton.addActionListener(e -> dispose());

        contentPane.add(phoneInput, BorderLayout.NORTH);
        contentPane.add(confirmButton, BorderLayout.WEST);
        contentPane.add(cancelButton, BorderLayout.EAST);
        setVisible(true);
    }
}
