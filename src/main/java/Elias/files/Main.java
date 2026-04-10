package Elias.files;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Use system look and feel as a base, then override with UITheme
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Init CSV files (creates them if they don't exist, seeds admin)
        DatabaseHelper.init();

        // Launch both login windows on the EDT
        SwingUtilities.invokeLater(() -> {
            new GuestLoginFrame();   // Guest login (with register link)
            new AdminLoginFrame();   // Staff / admin login
        });
    }
}
