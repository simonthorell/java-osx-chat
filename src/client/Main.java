package client;

import client.ui.MainWindow;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.swing.*;

public class Main {
    public Main() {
        FlatMacDarkLaf.setup(); // https://github.com/JFormDesigner/FlatLaf/tree/main/flatlaf-intellij-themes#themes
        SwingUtilities.invokeLater(MainWindow::new);
//        new MainWindow();
    }

    public static void main(String[] args) {
        new Main();
    }
}