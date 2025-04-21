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

        contentPane.add(phoneInput, BorderLayout.NORTH);

        final JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BorderLayout());

        final JButton callButton = new JButton("Anrufen");
        callButton.addActionListener(e -> {
            dispose();
            callback.accept((Phone) phoneInput.getSelectedItem());
        });
        buttonPane.add(callButton, BorderLayout.WEST);

        final JButton cancelButton = new JButton("Abbrechen");
        buttonPane.add(cancelButton, BorderLayout.CENTER);
        cancelButton.addActionListener(e -> dispose());

        contentPane.add(buttonPane, BorderLayout.SOUTH);
        setVisible(true);
    }
}
