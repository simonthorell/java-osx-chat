package client.java;

import client.java.ui.MainWindow;
import client.java.ui.MenuBar;
import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;
import com.formdev.flatlaf.util.SystemInfo;
import java.awt.*;
import java.net.URL;
import javax.swing.*;

public class Main {
  private MainWindow mainWindow;
  private JFrame frame;

  public Main() {
    // Set App Name for macOS menu bar & dock
    setupMacOS();

    // Set the FlatLaf theme to override the default Swing theme
    FlatOneDarkIJTheme.setup();

    // Create the main GUI window
    SwingUtilities.invokeLater(
        () -> {
          mainWindow = new MainWindow();
          frame = mainWindow.getFrame();

          // Setup macOS specific properties
          setPropsMacOS();

          // Override the OS menubar with the app menubar
          MenuBar menuBar = new MenuBar();
          frame.setJMenuBar(menuBar);

          // Show the main window
          frame.setVisible(true);
        });
  }

  // Main Method
  public static void main(String[] args) {
    new Main();
  }

  // MacOS specific settings
  public void setupMacOS() {
    // Set the app menu bar name & replace the macOS menu bar with app menu bar
    System.setProperty("apple.awt.application.name", "ChatUp");
    System.setProperty("apple.laf.useScreenMenuBar", "true");
  }

  public void setPropsMacOS() {
    // Hide the app title bar defaulted in macOS & set the app icon in the dock
    if (SystemInfo.isMacFullWindowContentSupported) {
      frame.getRootPane().putClientProperty("apple.awt.fullWindowContent", true);
      frame.getRootPane().putClientProperty("apple.awt.transparentTitleBar", true);
      frame.getRootPane().putClientProperty("apple.awt.windowTitleVisible", false);
    }

    // Set the application icon for macOS dock
    Taskbar taskbar = Taskbar.getTaskbar();
    try {
      URL imageURL = getClass().getClassLoader().getResource("icon.png");
      if (imageURL != null) {
        Image image = new ImageIcon(imageURL).getImage();
        taskbar.setIconImage(image);
      } else {
        System.err.println("Failed to load image: icon.png");
      }
    } catch (UnsupportedOperationException e) {
      System.out.println("The OS does not support: 'taskbar.setIconImage'");
    } catch (SecurityException e) {
      System.out.println("There was a security exception for: 'taskbar.setIconImage'");
    }
  }
}
