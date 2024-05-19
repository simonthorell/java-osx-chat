package client.java.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginWindow extends JPanel {
    private final JTextField usernameField;

    public LoginWindow() {
        this.setLayout(new BorderLayout());

        // Username input panel
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Enter Username:"));
        usernameField = new JTextField(20);
        inputPanel.add(usernameField);
        this.add(inputPanel, BorderLayout.CENTER);

        // Switch Panel button
        JButton switchButton = loginBtn();
        switchButton.putClientProperty("JButton.buttonType", "roundRect");
        switchButton.setBackground(UIConstants.PRIMARY_COLOR);
        this.add(switchButton, BorderLayout.SOUTH);

        // Add key listener to usernameField to trigger button click on Enter key
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    switchButton.doClick();
                }
            }
        });

        // Set preferred size
        this.setPreferredSize(new Dimension(370, 115));

        // Add component listener to request focus on msgField when the panel is shown
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                usernameField.requestFocusInWindow();
            }
        });
    }

    private JButton loginBtn() {
        JButton switchButton = new JButton("Enter Chat Room");
        switchButton.setActionCommand("button");
        switchButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            if (!username.isEmpty()) {
                MainWindow mainWindow = (MainWindow) SwingUtilities.getWindowAncestor(this);
                mainWindow.setUsername(username);
                mainWindow.switchPanel("ChatWindow");
            } else {
                JOptionPane.showMessageDialog(this, "Username cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            }
            // Make sure the window is re-painted correctly
            MainWindow mainWindow = (MainWindow) SwingUtilities.getWindowAncestor(this);
            mainWindow.resizeAndCenterWindow();
        });
        return switchButton;
    }
}