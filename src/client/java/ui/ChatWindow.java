package client.java.ui;

import client.java.IChatClient;
import client.java.net.IMessageHandler;
import client.java.net.IUserListObserver;
import common.ChatMessage;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.swing.*;

public class ChatWindow extends JPanel implements IMessageHandler, IUserListObserver {
  public final IChatClient client;
  private final JTextArea msgArea = new JTextArea(10, 45);
  private final JTextField msgField = new JTextField(30);
  private String username;
  private JList<String> usersList;

  // Constructor
  public ChatWindow(IChatClient client) {
    this.client = client;

    // Set the message handler and user list observer for the client
    client.setMessageHandler(this);
    client.addUserListObserver(this);

    // Set layout for ChatWindow
    this.setLayout(new BorderLayout());

    // Add ChatWindow components
    JPanel top = topPanel();
    JSplitPane center = centerPanel();
    JPanel bottom = bottomPanel();

    // Adding padding around sub-panels
    top.setBorder(BorderFactory.createEmptyBorder(7, 0, 7, 0));
    bottom.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

    // Add components to ChatWindow
    this.add(top, BorderLayout.NORTH);
    this.add(center, BorderLayout.CENTER);
    this.add(bottom, BorderLayout.SOUTH);

    // Set preferred window size
    this.setPreferredSize(new Dimension(600, 400));

    // Add component listener to request focus on msgField when the panel is shown
    this.addComponentListener(
        new ComponentAdapter() {
          @Override
          public void componentShown(ComponentEvent e) {
            msgField.requestFocusInWindow();
          }
        });

    // Connect to the chat server
    try {
      client.connect();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(
          this,
          "ChatWindow (Line63): Unable to connect to the chat server",
          "Connection Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  // Top panel hold the app name and action buttons
  private JPanel topPanel() {
    // Create a panel for the top section with BorderLayout
    JPanel northPanel = new JPanel(new BorderLayout());
    northPanel.setOpaque(false);

    // Create a sub-panel for buttons with FlowLayout for horizontal alignment
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    // Add Action buttons
    JButton addBotBtn = createActionButton("add_bot.png");
    JButton rmBotBtn = createActionButton("remove_bot.png");
    JButton signOutButton = createActionButton("logout.png");

    // Add action listeners to buttons
    addBotBtn.addActionListener(
        e -> {
          // TODO: Add AI bot action
        });
    rmBotBtn.addActionListener(
        e -> {
          // TODO: Remove AI bot action
        });
    signOutButton.addActionListener(
        e -> {
          try {
            // Disconnect and remove the user list observer
            client.disconnect(this);
          } catch (IOException ioException) {
            System.out.println("Error disconnecting from the server");
          }

          // Switch back to the login window
          MainWindow mainWindow = (MainWindow) SwingUtilities.getWindowAncestor(this);
          mainWindow.switchPanel("LoginWindow");
          mainWindow.resizeWindow();
        });

    // Add buttons to the button panel
    buttonPanel.add(addBotBtn);
    buttonPanel.add(Box.createHorizontalStrut(5));
    buttonPanel.add(rmBotBtn);
    buttonPanel.add(Box.createHorizontalStrut(5));
    buttonPanel.add(signOutButton);

    // App name label
    JLabel appNameLabel = new JLabel("ChatUp");
    appNameLabel.setForeground(Color.WHITE);
    appNameLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Larger and bold font

    // Set alignment and add components
    northPanel.add(appNameLabel, BorderLayout.CENTER);
    northPanel.add(buttonPanel, BorderLayout.EAST);

    // Adjust label position on resize
    northPanel.addComponentListener(
        new ComponentAdapter() {
          @Override
          public void componentResized(ComponentEvent e) {
            int panelWidth = northPanel.getWidth();
            int labelWidth = appNameLabel.getPreferredSize().width;
            int newLabelPosX =
                (panelWidth - labelWidth) / 2; // Calculate the new X position for the label

            appNameLabel.setBounds(
                newLabelPosX, appNameLabel.getY(), labelWidth, appNameLabel.getHeight());
          }
        });

    return northPanel;
  }

  // Center panel holds the message area and user list
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
    usersList = new JList<>(new DefaultListModel<>());
    JScrollPane userScrollPane = new JScrollPane(usersList);
    usersPanel.add(userScrollPane, BorderLayout.CENTER);

    // Set a fixed minimum size for the user list panel
    usersPanel.setMinimumSize(new Dimension(120, 1));

    // Split pane for message and user lists
    JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, msgPanel, usersPanel);
    centerSplitPane.setResizeWeight(1);

    return centerSplitPane;
  }

  // Bottom panel holds the message input field and send button
  private JPanel bottomPanel() {
    // Setup message input panel
    JPanel inputPanel = new JPanel(new BorderLayout());

    msgField.putClientProperty("JComponent.roundRect", true);
    inputPanel.add(msgField, BorderLayout.CENTER);

    JButton sendButton = sendMessage();
    inputPanel.add(sendButton, BorderLayout.EAST);

    // Add key listener to message field to trigger button click on Enter key
    msgField.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
              sendButton.doClick();
            }
          }
        });

    return inputPanel;
  }

  // Send message button
  private JButton sendMessage() {
    JButton sendButton = new JButton("Send");
    sendButton.putClientProperty("JButton.buttonType", "roundRect");
    sendButton.setBackground(UIConstants.PRIMARY_COLOR);
    sendButton.setForeground(Color.WHITE);

    sendButton.addActionListener(
        e -> {
          String msg = msgField.getText();
          if (!msg.isEmpty()) {
            ChatMessage chatMessage = new ChatMessage(username, msg);
            try {
              client.sendMessage(chatMessage);
            } catch (IOException ioException) {
              JOptionPane.showMessageDialog(
                  this, "Unable to send message", " Error", JOptionPane.ERROR_MESSAGE);
            }
            msgField.setText("");
          }
        });
    return sendButton;
  }

  // General method to create action buttons with icons (path as parameter)
  private JButton createActionButton(String iconPath) {
    JButton btn = new JButton();

    // Load the icon and resize it
    URL imageUrl = getClass().getClassLoader().getResource(iconPath);
    if (imageUrl != null) {
      ImageIcon icon = new ImageIcon(imageUrl);
      Image image = icon.getImage();
      Image newimg = image.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
      icon = new ImageIcon(newimg);
      btn.setIcon(icon);
    } else {
      System.err.println("Resource not found: " + iconPath);
    }

    // Set button properties to only show the icon
    btn.setContentAreaFilled(false);
    btn.setBorderPainted(false);
    btn.setFocusPainted(false);
    btn.setBorder(null);

    return btn;
  }

  // Handle incoming chat messages
  @Override
  public void handleMessage(ChatMessage message) {
    msgArea.append(
        message.getUser() + ": " + message.getMessage() + " (" + message.getTimestamp() + ")\n");
  }

  // Update the user list when a new user joins or leaves
  @Override
  public void userListUpdated(List<String> newUsers) {
    SwingUtilities.invokeLater(
        () -> {
          DefaultListModel<String> model = (DefaultListModel<String>) usersList.getModel();
          model.removeAllElements();
          for (String user : newUsers) {
            model.addElement(user);
          }
        });
  }

  // Set the username for the chat window
  public void setUsername(String username) {
    this.username = username;
  }
}
