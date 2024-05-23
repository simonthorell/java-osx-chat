package client.java.ui;

import javax.swing.*;

public class MenuBar extends JMenuBar {
  // Menus
  private final JMenu editMenu = new JMenu("Edit");
  private final JMenu viewMenu = new JMenu("View");
  private final JMenu windowMenu = new JMenu("Window");
  private final JMenu helpMenu = new JMenu("Help");

  // Menu items for Edit menu
  private final JMenuItem cutMenuItem = new JMenuItem("Cut");
  private final JMenuItem copyMenuItem = new JMenuItem("Copy");
  private final JMenuItem pasteMenuItem = new JMenuItem("Paste");
  private final JMenuItem undoMenuItem = new JMenuItem("Undo");
  private final JMenuItem redoMenuItem = new JMenuItem("Redo");

  // Menu items for View menu
  private final JMenuItem zoomInMenuItem = new JMenuItem("Zoom In");
  private final JMenuItem zoomOutMenuItem = new JMenuItem("Zoom Out");
  private final JMenuItem resetZoomMenuItem = new JMenuItem("Reset Zoom");

  // Menu items for Window menu
  private final JMenuItem minimizeMenuItem = new JMenuItem("Minimize");
  private final JMenuItem maximizeMenuItem = new JMenuItem("Maximize");

  // Menu items for Help menu
  private final JMenuItem aboutMenuItem = new JMenuItem("About ChatUp");

  // Constructor
  public MenuBar() {
    // Setup menus
    setupEditMenu();
    setupViewMenu();
    setupHelpMenu();

    // Add menus to the menu bar
    this.add(editMenu);
    this.add(viewMenu);
    this.add(windowMenu);
    this.add(helpMenu);
  }

  private void setupEditMenu() {
    editMenu.add(undoMenuItem);
    editMenu.add(redoMenuItem);
    editMenu.addSeparator(); // Adds a separator line
    editMenu.add(cutMenuItem);
    editMenu.add(copyMenuItem);
    editMenu.add(pasteMenuItem);
  }

  private void setupViewMenu() {
    viewMenu.add(zoomInMenuItem);
    viewMenu.add(zoomOutMenuItem);
    viewMenu.add(resetZoomMenuItem);

    // Example actions for zoom features
    zoomInMenuItem.addActionListener(
        e -> {
          System.out.println("Zoom In Action");
        });
    zoomOutMenuItem.addActionListener(
        e -> {
          System.out.println("Zoom Out Action");
        });
    resetZoomMenuItem.addActionListener(
        e -> {
          System.out.println("Reset Zoom Action");
        });
  }

  private void setupWindowMenu() {
    windowMenu.add(minimizeMenuItem);
    windowMenu.add(maximizeMenuItem);
  }

  private void setupHelpMenu() {
    helpMenu.add(aboutMenuItem);

    // Setup about action
    aboutMenuItem.addActionListener(
        e ->
            JOptionPane.showMessageDialog(
                null, "Your Application\nVersion 1.0", "About", JOptionPane.INFORMATION_MESSAGE));
  }
}
