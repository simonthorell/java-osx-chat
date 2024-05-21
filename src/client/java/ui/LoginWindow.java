package client.java.ui;

import client.java.IChatClient;
import client.java.net.UDPClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;

public class LoginWindow extends JPanel {
    private JTextField usernameField;
    private JTextField passwordField;
    private JRadioButton tcpButton;
    private JRadioButton udpButton;

    public LoginWindow() {
        this.setLayout(new BorderLayout());

        JPanel top = appLogoPanel();
        JPanel center = inputFieldsPanel();
        JPanel bottom = buttonPanel();

        top.setBorder(BorderFactory.createEmptyBorder(60, 0, 10, 0));
        center.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        bottom.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));

        // Add components to LoginWindow
        this.add(top, BorderLayout.NORTH);
        this.add(center, BorderLayout.CENTER);
        this.add(bottom, BorderLayout.SOUTH);

        // Set preferred size
        this.setPreferredSize(new Dimension(280, 420));

        // Component listener to focus on username field
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                usernameField.requestFocusInWindow();
            }
        });
    }

    //====================================================================================================
    // PRIVATE PANELS
    //====================================================================================================
    private JPanel inputFieldsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 5));

        // Add username field
        panel.add(new JLabel("Username:"));
        usernameField = new JTextField(15);
        panel.add(usernameField);
        // Add password field
        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(15);
        panel.add(passwordField);

        // Listeners for pressing Enter key
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginBtn().doClick();
                }
            }
        });
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginBtn().doClick();
                }
            }
        });

        return panel;
    }

    private JPanel appLogoPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setLayout(new GridLayout(2, 1, 10, 5));
        JLabel appName = new JLabel("ChatUp", JLabel.CENTER);
        appName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel imageLabel = appLogo();
        panel.add(imageLabel);
        panel.add(appName);
        return panel;
    }

    private JPanel buttonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Add protocol selection
        JPanel protocolPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tcpButton = new JRadioButton("TCP");
        udpButton = new JRadioButton("UDP");
        ButtonGroup group = new ButtonGroup();
        group.add(tcpButton);
        group.add(udpButton);
        // Set UDP as the default selected protocol
        udpButton.setSelected(true);
        // Add the buttons to the protocol panel
        protocolPanel.add(tcpButton);
        protocolPanel.add(udpButton);
        panel.add(protocolPanel);

        // Add spacing
        panel.add(Box.createVerticalStrut(10));

        // Add Login Button
        JButton loginButton = loginBtn();
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(loginButton);

        return panel;
    }

    //====================================================================================================
    // HELPER METHODS
    //====================================================================================================
    private JLabel appLogo() {
        ImageIcon imageIcon = null;
        URL imageURL = getClass().getClassLoader().getResource("icon.png");
        // Try to load the image
        if (imageURL != null) {
            imageIcon = new ImageIcon(imageURL);
            Image image = imageIcon.getImage();
            Image scaledImage = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(scaledImage);
        } else {
            System.err.println("Failed to load image: icon.png");
        }
        return new JLabel(imageIcon != null ? imageIcon : new ImageIcon(), JLabel.CENTER);
    }

    private JButton loginBtn() {
        // Setup Login button
        JButton loginBtn = new JButton("Login to Chat");
        loginBtn.putClientProperty("JButton.buttonType", "roundRect");
        loginBtn.setBackground(UIConstants.PRIMARY_COLOR);
        loginBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loginBtn.setForeground(Color.WHITE);

        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim(); // Assuming password handling

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Username and password cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!tcpButton.isSelected() && !udpButton.isSelected()) {
                JOptionPane.showMessageDialog(this,
                        "Please select a protocol", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            IChatClient client = null;
            if (tcpButton.isSelected()) {
                // Show Dialog (Remove when TCP is implemented)
                JOptionPane.showMessageDialog(this,
                        "TCP protocol is not yet supported", "Error", JOptionPane.ERROR_MESSAGE);
                // client = new TCPClient(getUsername());
            } else {
                client = new UDPClient(getUsername());
            }

            // If client has been initialized, switch to ChatWindow
            if (client != null) {
                MainWindow mainWindow = (MainWindow) SwingUtilities.getWindowAncestor(this);
                mainWindow.switchToChatWindow(client);
            }
        });

        return loginBtn;
    }

    // Getters
    public String getUsername() {
        return usernameField.getText();
    }
}
