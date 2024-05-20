package client.java.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;

public class ChatWindow extends JPanel {
    private final JTextArea msgArea = new JTextArea(10, 45);
    private final JTextField msgField = new JTextField(30);
    private String username;

    public ChatWindow() {
        // Set layout for ChatWindow
        this.setLayout(new BorderLayout());

        // Add ChatWindow components
        JPanel top = topPanel();
        JSplitPane center = centerPanel();
        JPanel bottom = bottomPanel();

        // Adding padding around sub-panels
        top.setBorder(BorderFactory.createEmptyBorder(7, 0, 7, 0));
        // center.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        bottom.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        // Add components to ChatWindow
        this.add(top, BorderLayout.NORTH);
        this.add(center, BorderLayout.CENTER);
        this.add(bottom, BorderLayout.SOUTH);

        // Set preferred window size
        this.setPreferredSize(new Dimension(600, 400));

        // Add component listener to request focus on msgField when the panel is shown
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                msgField.requestFocusInWindow();
            }
        });
    }

    //====================================================================================================
    // MAIN CHAT WINDOW PANELS
    //====================================================================================================
    private JPanel topPanel() {
        JPanel northPanel = new JPanel(new BorderLayout());

        JButton signOutButton = new JButton();

        // Load the icon and resize it
        URL imageUrl = getClass().getClassLoader().getResource("logout.png");
        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            Image image = icon.getImage();
            Image newimg = image.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
            icon = new ImageIcon(newimg);
            signOutButton.setIcon(icon);
        } else {
            System.err.println("Resource not found: logout.png");
        }

        signOutButton.setContentAreaFilled(false);
        signOutButton.setBorderPainted(false);
        signOutButton.setFocusPainted(false);
        signOutButton.setBorder(null);

        signOutButton.addActionListener(e -> {
            MainWindow mainWindow = (MainWindow) SwingUtilities.getWindowAncestor(this);
            mainWindow.switchPanel("LoginWindow");
            mainWindow.resizeAndCenterWindow();
        });

        // App name label
        JLabel appNameLabel = new JLabel("ChatUp", SwingConstants.CENTER);
        appNameLabel.setForeground(Color.WHITE);
        appNameLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Larger and bold font

        // Set alignment and add components
        northPanel.add(appNameLabel, BorderLayout.CENTER);
        northPanel.add(signOutButton, BorderLayout.EAST);

        return northPanel;
    }

    private JSplitPane centerPanel() {
        // Setup message list panel
        JPanel msgPanel = new JPanel(new BorderLayout());
        msgArea.setEditable(false);
        JScrollPane msgScrollPane = new JScrollPane(msgArea);
        msgScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        msgScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        msgPanel.add(msgScrollPane, BorderLayout.CENTER);

        // Setup user list panel
        JPanel usersPanel = new JPanel(new BorderLayout());
        JList<String> usersList = new JList<>(new String[]{"User 1", "User 2", "User 3"});
        JScrollPane userScrollPane = new JScrollPane(usersList);
        usersPanel.add(userScrollPane, BorderLayout.CENTER);
        setFixedCellWidth(usersList);
        // Set minimum size for users panel based on the longest username
        Dimension minSize = usersPanel.getPreferredSize();
        usersPanel.setMinimumSize(new Dimension(minSize.width, minSize.height));

        // Split pane for message and user lists
        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, msgPanel, usersPanel);
        centerSplitPane.setResizeWeight(0.9);

        return centerSplitPane;
    }

    private JPanel bottomPanel() {
        // Setup message input panel
        JPanel inputPanel = new JPanel(new BorderLayout());

        msgField.putClientProperty("JComponent.roundRect", true);
        inputPanel.add(msgField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
        sendButton.putClientProperty("JButton.buttonType", "roundRect");
        sendButton.setBackground(UIConstants.PRIMARY_COLOR);
        sendButton.setForeground(Color.WHITE);
        sendButton.addActionListener(e -> {
            String msg = msgField.getText();
            if (!msg.isEmpty()) {
                msgArea.append(username + ": " + msg + "\n");
                msgField.setText("");
            }
        });
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Add key listener to message field to trigger button click on Enter key
        msgField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendButton.doClick();
                }
            }
        });

        return inputPanel;
    }

    //====================================================================================================
    // HELPER METHODS
    //====================================================================================================
    private void setFixedCellWidth(JList<String> usersList) {
        FontMetrics metrics = usersList.getFontMetrics(usersList.getFont());
        int maxWidth = 0;
        for (int i = 0; i < usersList.getModel().getSize(); i++) {
            String element = usersList.getModel().getElementAt(i);
            int width = metrics.stringWidth(element);
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        // Add some padding to the width
        maxWidth += 40; // Adjust padding as needed
        usersList.setFixedCellWidth(maxWidth);
    }

    //====================================================================================================
    // GETTERS & SETTERS
    //====================================================================================================
    public void setUsername(String username) {
        this.username = username;
    }
}