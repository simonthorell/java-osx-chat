package client.java.ui;

import client.java.IChatClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow extends JFrame {
  private final JPanel mainPanel = new JPanel();
  private final CardLayout cardLayout = new CardLayout();
  private final LoginWindow loginWindow = new LoginWindow();
  private ChatWindow chatWindow;

  public MainWindow() {
    this.setTitle("ChatUp"); // Set Window title (App Name)
    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    // Set the layout manager for the main panel
    mainPanel.setLayout(cardLayout); // Set the layout manager & add panels to it
    mainPanel.add(loginWindow, "LoginWindow"); // Add sub-panels to the main panel

    // Create an outer panel for padding
    JPanel outerPanel = new JPanel(new BorderLayout());
    outerPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
    outerPanel.add(mainPanel, BorderLayout.CENTER);
    this.add(outerPanel);

    // Show login window by default & resize window to fit
    cardLayout.show(mainPanel, "LoginWindow");
    resizeWindow();

    // Center the window on the screen
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (screenSize.width - this.getWidth()) / 2;
    int y = (screenSize.height - this.getHeight()) / 2;
    this.setLocation(x, y);

    // Add a window listener using an anonymous inner class
    this.addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            try {
              // Disconnect the client when the window is closing
              chatWindow.client.disconnect(chatWindow);
              MainWindow.this.dispose();
            } catch (Exception ex) {
              // Connection already disconnected, so just close the window
              MainWindow.this.dispose();
            }
          }
        });

    this.setVisible(true);
  }

  // Method to add an action listener to the login window
  //  @Override
  //  public void actionPerformed(ActionEvent e) {
  //    if (e.getActionCommand().equals("button")) {
  //      System.out.println("Button clicked!");
  //      cardLayout.show(mainPanel, "ChatWindow");
  //    }
  //  }

  // Method to switch to the chat window (after successful login)
  public void switchToChatWindow(IChatClient client) {
    // Remove the previous chatWindow if it exists
    if (chatWindow != null) {
      mainPanel.remove(chatWindow);
    }

    // Create a new ChatWindow with the provided client
    chatWindow = new ChatWindow(client);
    chatWindow.setUsername(loginWindow.getUsername());

    // Add the ChatWindow to the main panel and switch to it
    mainPanel.add(chatWindow, "ChatWindow");
    cardLayout.show(mainPanel, "ChatWindow");
    resizeWindow();
  }

  // Method to switch panels
  public void switchPanel(String panelName) {
    cardLayout.show(mainPanel, panelName);
    resizeWindow();
  }

  // Method to resize and center the window when switching cards
  public void resizeWindow() {
    // Resize window based on the preferred size of the current panel
    Component currentPanel = getCurrentPanel();
    if (currentPanel != null) {
      Dimension preferredSize = currentPanel.getPreferredSize();
      this.setSize(preferredSize);
      this.setMinimumSize(preferredSize);
    }

    // Set resizable window based on the current panel (LoginWindow is not resizable)
    this.setResizable(!(currentPanel instanceof LoginWindow));
  }

  // Method to get the MainWindow JFrame object (used in main method)
  public JFrame getFrame() {
    return this;
  }

  private Component getCurrentPanel() {
    for (Component comp : mainPanel.getComponents()) {
      if (comp.isVisible()) {
        return comp;
      }
    }
    return null;
  }
}
