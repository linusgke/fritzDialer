package de.linusgke.fritzdialer.window;

import de.linusgke.fritzdialer.FritzDialerApplication;
import de.linusgke.fritzdialer.fritz.Phone;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;

@Slf4j
public class InfoDialog extends JDialog {

    public InfoDialog(final FritzDialerApplication application) {
        super(application.getFrame(), true);

        setTitle("Info");
        setSize(500, 300);
        setResizable(false);
        setLocationRelativeTo(null);

        final JPanel contentPane = new JPanel();
        contentPane.setLayout(null);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);

        final JLabel logoLabel = new JLabel();
        logoLabel.setBounds(20, 11, 128, 128);
        Image logo = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/icon.png"));
        logo = logo.getScaledInstance(logoLabel.getWidth(), logoLabel.getHeight(), Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(logo));
        contentPane.add(logoLabel);

        final JLabel titleLabel = new JLabel("FRITZ! Dialer");
        titleLabel.setBounds(168, 11, 207, 37);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
        contentPane.add(titleLabel);

        final JLabel versionLabel = new JLabel("Version " + FritzDialerApplication.CURRENT_VERSION);
        versionLabel.setBounds(178, 47, 197, 14);
        contentPane.add(versionLabel);

        final JPanel licenseDisclaimerPanel = new JPanel();
        licenseDisclaimerPanel.setBounds(168, 72, 306, 178);

        final TitledBorder titledBorder = new TitledBorder("License and disclaimer");
        titledBorder.setTitleJustification(TitledBorder.CENTER);
        licenseDisclaimerPanel.setBorder(titledBorder);
        licenseDisclaimerPanel.setLayout(null);
        contentPane.add(licenseDisclaimerPanel);

        final JTextArea licenseDisclaimerText = new JTextArea();
        licenseDisclaimerText.setBounds(10, 20, 286, 147);
        licenseDisclaimerText.setEditable(false);
        licenseDisclaimerText.setLineWrap(true);
        licenseDisclaimerText.setWrapStyleWord(true);
        licenseDisclaimerText.setBackground(SystemColor.menu);
        licenseDisclaimerText.setFont(new Font("Tahoma", Font.PLAIN, 11));
        licenseDisclaimerText.setText("This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or at your option any later version.\r\n\r\nThis program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. \r\n\r\nYou should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.\r\n\r\nDisclaimer: This program is not affiliated, associated, authorized, endorsed by, or in any way officially connected with AVM.");
        licenseDisclaimerText.setCaretPosition(0);
        licenseDisclaimerText.setFocusable(false);

        final JScrollPane licenseDisclaimerScrollPane = new JScrollPane(licenseDisclaimerText);
        licenseDisclaimerScrollPane.setBounds(10, 20, 286, 147);
        licenseDisclaimerPanel.add(licenseDisclaimerScrollPane);

        final JLabel githubLogoLabel = new JLabel();
        githubLogoLabel.setBounds(53, 158, 64, 64);
        Image github = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/github.png"));
        github = github.getScaledInstance(githubLogoLabel.getWidth(), githubLogoLabel.getHeight(), Image.SCALE_SMOOTH);
        githubLogoLabel.setIcon(new ImageIcon(github));
        addGitHubClickHandler(githubLogoLabel);
        contentPane.add(githubLogoLabel);

        final JLabel githubTextLabel = new JLabel("Open on GitHub");
        githubTextLabel.setBounds(43, 223, 87, 14);
        githubTextLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
        addGitHubClickHandler(githubTextLabel);
        contentPane.add(githubTextLabel);

        setVisible(true);
    }

    private void addGitHubClickHandler(final JComponent component) {
        component.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(FritzDialerApplication.GITHUB_URI);
                } catch (final IOException ignored) {
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

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
    }
}
