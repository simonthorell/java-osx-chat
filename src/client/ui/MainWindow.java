package client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame implements ActionListener {
    private final JPanel mainPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    private final ChatWindow chatWindow = new ChatWindow();
    private final LoginWindow loginWindow = new LoginWindow();

    public MainWindow() {
        // Set Window title
        this.setTitle("Java TCP Chat Client");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set the layout manager & add panels to it
        mainPanel.setLayout(cardLayout);

        // Add sub-panels to the main panel
        mainPanel.add(loginWindow, "LoginWindow");
        mainPanel.add(chatWindow, "ChatWindow");

        // Create an outer panel for padding
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        outerPanel.add(mainPanel, BorderLayout.CENTER);
        this.add(outerPanel);

        // Show login window by default & resize window to fit
        cardLayout.show(mainPanel, "LoginWindow");
        resizeAndCenterWindow();
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("button")) {
            System.out.println("Button clicked!");
            cardLayout.show(mainPanel, "ChatWindow");
        }
    }

    //====================================================================================================
    // HELPER METHODS
    //====================================================================================================
    // Method to switch panels
    public void switchPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
        resizeAndCenterWindow();
    }

    public void setUsername(String username) {
        chatWindow.setUsername(username);
    }

    // Method to resize and center the window when switching cards
    public void resizeAndCenterWindow() {
        // Resize window based on the preferred size of the current panel
        Component currentPanel = getCurrentPanel();
        if (currentPanel != null) {
            Dimension preferredSize = currentPanel.getPreferredSize();
            this.setSize(preferredSize);
            this.setMinimumSize(preferredSize);
        }
        // Center the window on the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - this.getWidth()) / 2;
        int y = (screenSize.height - this.getHeight()) / 2;
        this.setLocation(x, y);
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