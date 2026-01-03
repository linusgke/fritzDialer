package de.linusgke.fritzdialer.window;

import de.linusgke.fritzdialer.FritzDialerApplication;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

@Slf4j
public class LogDialog extends JDialog {

    public LogDialog(final FritzDialerApplication application) {
        super(application.getFrame(), true);

        setTitle("Protokoll");
        setSize(700, 700);
        setResizable(false);

        setLocationRelativeTo(null);

        final File latestLogFile = new File(FileSystems.getDefault().getPath("").toAbsolutePath() + "/logs/latest.log");
        String logText;
        try {
            logText = Files.readString(latestLogFile.toPath());
        } catch (final IOException e) {
            logText = "Protokolldatei konnte nicht gelesen werden.";
            log.error("Could not read latest log file", e);
        }

        final JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        final JTextArea logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        logTextArea.setLineWrap(true);
        logTextArea.setWrapStyleWord(true);
        logTextArea.setBackground(SystemColor.menu);
        logTextArea.setFont(new Font("Tahoma", Font.PLAIN, 11));
        logTextArea.setText(logText);
        logTextArea.setCaretPosition(0);
        logTextArea.setFocusable(false);

        final JScrollPane licenseDisclaimerScrollPane = new JScrollPane(logTextArea);
        contentPane.add(licenseDisclaimerScrollPane, BorderLayout.CENTER);

        setVisible(true);
    }
}
