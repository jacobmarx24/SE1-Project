package Elias.files;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class ClerkLoginFrame extends JFrame {

    private JTextField     tfUsername;
    private JPasswordField tfPassword;
    private JLabel         lblError;

    public ClerkLoginFrame() {
        UITheme.applyDefaults(this, "Clerk Login — Aura Hotel", 420, 460);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UITheme.BG);
        outer.setBorder(new EmptyBorder(30, 32, 30, 32));

        JPanel card = UITheme.card(32, 30);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // ── Header ────────────────────────────────────────────────────────────
        JLabel logo = UITheme.logoLabel("Aura Hotel");
        logo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Clerk Portal");
        subtitle.setFont(UITheme.FONT_SMALL);
        subtitle.setForeground(UITheme.TEXT_SEC);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel heading = new JLabel("Clerk Sign In");
        heading.setFont(UITheme.FONT_TITLE);
        heading.setForeground(UITheme.TEXT_PRI);
        heading.setAlignmentX(LEFT_ALIGNMENT);

        // Clerk badge
        JLabel badge = new JLabel("  Staff Access  ");
        badge.setFont(UITheme.FONT_SMALL);
        badge.setForeground(new Color(0x065F46));
        badge.setBackground(new Color(0xD1FAE5));
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(3, 8, 3, 8));
        badge.setAlignmentX(LEFT_ALIGNMENT);

        card.add(logo);
        card.add(Box.createVerticalStrut(2));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(20));
        card.add(UITheme.divider());
        card.add(Box.createVerticalStrut(20));
        card.add(heading);
        card.add(Box.createVerticalStrut(8));
        card.add(badge);
        card.add(Box.createVerticalStrut(22));

        // ── Username ──────────────────────────────────────────────────────────
        card.add(fieldLabel("Clerk Username"));
        card.add(Box.createVerticalStrut(5));
        tfUsername = UITheme.textField("Enter your username");
        tfUsername.setAlignmentX(LEFT_ALIGNMENT);
        tfUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(tfUsername);
        card.add(Box.createVerticalStrut(14));

        // ── Password ──────────────────────────────────────────────────────────
        card.add(fieldLabel("Password"));
        card.add(Box.createVerticalStrut(5));
        tfPassword = UITheme.passwordField("Enter your password");
        tfPassword.setAlignmentX(LEFT_ALIGNMENT);
        tfPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(tfPassword);
        card.add(Box.createVerticalStrut(6));

        // ── Error label ───────────────────────────────────────────────────────
        lblError = UITheme.errorLabel();
        lblError.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lblError);
        card.add(Box.createVerticalStrut(16));

        // ── Sign In button ────────────────────────────────────────────────────
        JButton btnLogin = UITheme.primaryButton("Sign In");
        btnLogin.setAlignmentX(LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogin.addActionListener(e -> handleLogin());
        card.add(btnLogin);

        getRootPane().setDefaultButton(btnLogin);

        outer.add(card, new GridBagConstraints());
        add(outer, BorderLayout.CENTER);
    }

    private JLabel fieldLabel(String text) {
        JLabel l = UITheme.sectionLabel(text);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private void handleLogin() {
        String username = tfUsername.getText().trim();
        String password = new String(tfPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Please fill in all fields.");
            return;
        }

        String[] clerk = DatabaseHelper.findClerk(username, password);

        if (clerk == null) {
            lblError.setText("Incorrect username or password.");
            tfPassword.setText("");
            return;
        }

        // Prevent admins from logging in through the clerk portal
        String role = clerk.length > 4 ? clerk[4] : "clerk";
        if (role.equals("admin")) {
            lblError.setText("Please use the Admin portal to sign in.");
            tfPassword.setText("");
            return;
        }

        lblError.setText(" ");
        dispose();
        new ClerkProfileFrame(clerk);
    }
}

// Test Change
