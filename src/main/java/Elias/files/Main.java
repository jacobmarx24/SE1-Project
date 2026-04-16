package Elias.files;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        DatabaseHelper.init();

        SwingUtilities.invokeLater(() -> {
            new GuestLoginFrame();   // Guest login (with register link)
            new ClerkLoginFrame();   // Clerk login
            new AdminLoginFrame();   // Admin login
        });
    }
}
