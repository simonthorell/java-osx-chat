package client.java.ui;

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

    public LoginWindow() {
        this.setLayout(new BorderLayout());

        JPanel top = appLogoPanel();
        JPanel center = inputFieldsPanel();
        JPanel bottom = bottomPanel();

        top.setBorder(BorderFactory.createEmptyBorder(60, 0, 5, 0));
        center.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        bottom.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        // Add components to LoginWindow
        this.add(top, BorderLayout.NORTH);
        this.add(center, BorderLayout.CENTER);
        this.add(bottom, BorderLayout.SOUTH);

        // Set preferred size
        this.setPreferredSize(new Dimension(280, 400));

        // Component listener to focus on username field
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                usernameField.requestFocusInWindow();
            }
        });
    }

    private JPanel inputFieldsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 5));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField(15);
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JTextField(15);
        panel.add(passwordField);

        // Listeners for pressing Enter key
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

    private JPanel bottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.add(loginBtn());
        return panel;
    }

    private JLabel appLogo() {
        ImageIcon imageIcon = null;
        URL imageURL = getClass().getClassLoader().getResource("icon.png");
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
        JButton switchButton = new JButton("Login to Chat");
        switchButton.putClientProperty("JButton.buttonType", "roundRect");
        switchButton.setBackground(UIConstants.PRIMARY_COLOR);
        switchButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        switchButton.setForeground(Color.WHITE);
        switchButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim(); // Assuming password handling
            if (!username.isEmpty() && !password.isEmpty()) {
                MainWindow mainWindow = (MainWindow) SwingUtilities.getWindowAncestor(this);
                mainWindow.setUsername(username);  // Assuming setting username
                mainWindow.switchPanel("ChatWindow");
            } else {
                JOptionPane.showMessageDialog(this, "Username and password cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        return switchButton;
    }
}
