package client.java;

import client.java.ui.MainWindow;
import client.java.ui.MenuBar;
import javax.swing.*;

// https://github.com/JFormDesigner/FlatLaf/tree/main/flatlaf-intellij-themes#themes
import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;
import com.formdev.flatlaf.util.SystemInfo;

import java.awt.*;
import java.net.URL;

public class Main {
    private MainWindow mainWindow;
    private JFrame frame;

    public Main() {
        // Set the application name for macOS menu bar & dock
        setupMacOS();

        // Setup FlatLaf theme & Swing components
        FlatOneDarkIJTheme.setup();
        SwingUtilities.invokeLater(() -> {
            mainWindow = new MainWindow();
            frame = mainWindow.getFrame();

            // Setup app for macOS (TODO: add cross-platform support)
            setPropsMacOS();

            // Setup the system menubar from MenuBar UI class
            MenuBar menuBar = new MenuBar();
            frame.setJMenuBar(menuBar);

            // Show the frame
            frame.setVisible(true);
        });
    }

    public void setupMacOS() {
        // Set the application name for macOS menu bar
        System.setProperty("apple.awt.application.name", "ChatUp");
        // Replace the macOS menu bar with app menu bar
        System.setProperty( "apple.laf.useScreenMenuBar", "true" );
    }

    public void setPropsMacOS() {
        // Hide the app menu bar defaulted in macOS
        if (SystemInfo.isMacFullWindowContentSupported) {
            frame.getRootPane().putClientProperty("apple.awt.fullWindowContent", true);
            frame.getRootPane().putClientProperty("apple.awt.transparentTitleBar", true);
            frame.getRootPane().putClientProperty( "apple.awt.windowTitleVisible", false );
        }

        // Set the application icon for macOS dock (Java 9+)
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

    // Main
    public static void main(String[] args) {
        new Main();
    }
}