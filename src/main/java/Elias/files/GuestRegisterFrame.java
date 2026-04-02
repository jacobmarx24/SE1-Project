package Elias.files;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class GuestRegisterFrame extends JFrame {

    private JTextField     tfFullName;
    private JTextField     tfUsername;
    private JPasswordField tfPassword;
    private JPasswordField tfConfirm;
    private JTextField     tfEmail;
    private JTextField     tfPhone;

    // Per-field error labels
    private JLabel errFullName, errUsername, errPassword, errConfirm, errEmail, errPhone;

    public GuestRegisterFrame() {
        UITheme.applyDefaults(this, "Create Account — Aura Hotel", 460, 700);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UITheme.BG);
        outer.setBorder(new EmptyBorder(24, 32, 24, 32));

        JPanel card = UITheme.card(32, 28);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // ── Header ────────────────────────────────────────────────────────────
        JLabel logo = UITheme.logoLabel("Aura Hotel");
        logo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Guest Portal");
        subtitle.setFont(UITheme.FONT_SMALL);
        subtitle.setForeground(UITheme.TEXT_SEC);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel heading = new JLabel("Create your account");
        heading.setFont(UITheme.FONT_TITLE);
        heading.setForeground(UITheme.TEXT_PRI);
        heading.setAlignmentX(LEFT_ALIGNMENT);

        card.add(logo);
        card.add(Box.createVerticalStrut(2));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(20));
        card.add(UITheme.divider());
        card.add(Box.createVerticalStrut(20));
        card.add(heading);
        card.add(Box.createVerticalStrut(22));

        // ── Full Name ─────────────────────────────────────────────────────────
        card.add(fieldLabel("Full Name"));
        card.add(Box.createVerticalStrut(5));
        tfFullName = UITheme.textField("e.g. Maria Santos");
        stretch(tfFullName); card.add(tfFullName);
        errFullName = UITheme.errorLabel(); errFullName.setAlignmentX(LEFT_ALIGNMENT);
        card.add(errFullName);
        card.add(Box.createVerticalStrut(10));

        // ── Username ──────────────────────────────────────────────────────────
        card.add(fieldLabel("Username"));
        card.add(Box.createVerticalStrut(5));
        tfUsername = UITheme.textField("Choose a username");
        stretch(tfUsername); card.add(tfUsername);
        errUsername = UITheme.errorLabel(); errUsername.setAlignmentX(LEFT_ALIGNMENT);
        card.add(errUsername);
        card.add(Box.createVerticalStrut(10));

        // ── Password ──────────────────────────────────────────────────────────
        card.add(fieldLabel("Password"));
        card.add(Box.createVerticalStrut(5));
        tfPassword = UITheme.passwordField("Min. 6 characters");
        stretch(tfPassword); card.add(tfPassword);
        errPassword = UITheme.errorLabel(); errPassword.setAlignmentX(LEFT_ALIGNMENT);
        card.add(errPassword);
        card.add(Box.createVerticalStrut(10));

        // ── Confirm Password ──────────────────────────────────────────────────
        card.add(fieldLabel("Confirm Password"));
        card.add(Box.createVerticalStrut(5));
        tfConfirm = UITheme.passwordField("Re-enter password");
        stretch(tfConfirm); card.add(tfConfirm);
        errConfirm = UITheme.errorLabel(); errConfirm.setAlignmentX(LEFT_ALIGNMENT);
        card.add(errConfirm);
        card.add(Box.createVerticalStrut(10));

        // ── Email ─────────────────────────────────────────────────────────────
        card.add(fieldLabel("Email Address"));
        card.add(Box.createVerticalStrut(5));
        tfEmail = UITheme.textField("e.g. maria@email.com");
        stretch(tfEmail); card.add(tfEmail);
        errEmail = UITheme.errorLabel(); errEmail.setAlignmentX(LEFT_ALIGNMENT);
        card.add(errEmail);
        card.add(Box.createVerticalStrut(10));

        // ── Phone ─────────────────────────────────────────────────────────────
        card.add(fieldLabel("Phone Number"));
        card.add(Box.createVerticalStrut(5));
        tfPhone = UITheme.textField("e.g. +1 555 000 1234");
        stretch(tfPhone); card.add(tfPhone);
        errPhone = UITheme.errorLabel(); errPhone.setAlignmentX(LEFT_ALIGNMENT);
        card.add(errPhone);
        card.add(Box.createVerticalStrut(18));

        // ── Register button ───────────────────────────────────────────────────
        JButton btnRegister = UITheme.primaryButton("Create Account");
        btnRegister.setAlignmentX(LEFT_ALIGNMENT);
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnRegister.addActionListener(e -> handleRegister());
        card.add(btnRegister);
        card.add(Box.createVerticalStrut(16));

        // ── Back to login link ────────────────────────────────────────────────
        JPanel linkRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linkRow.setBackground(UITheme.SURFACE);
        linkRow.setAlignmentX(LEFT_ALIGNMENT);

        JLabel already = new JLabel("Already have an account? ");
        already.setFont(UITheme.FONT_BODY);
        already.setForeground(UITheme.TEXT_SEC);

        JButton btnBack = UITheme.linkButton("Sign in");
        btnBack.addActionListener(e -> { dispose(); new GuestLoginFrame(); });

        linkRow.add(already);
        linkRow.add(btnBack);
        card.add(linkRow);

        // ── Scroll support for smaller screens ───────────────────────────────
        JScrollPane scroll = new JScrollPane(outer);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(10);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        outer.add(card, new GridBagConstraints());
        add(scroll, BorderLayout.CENTER);
    }

    private JLabel fieldLabel(String text) {
        JLabel l = UITheme.sectionLabel(text);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private void stretch(JComponent c) {
        c.setAlignmentX(LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
    }

    private void clearErrors() {
        errFullName.setText(" ");
        errUsername.setText(" ");
        errPassword.setText(" ");
        errConfirm.setText(" ");
        errEmail.setText(" ");
        errPhone.setText(" ");
    }

    private boolean validate(String fullName, String username,
                              String password, String confirm,
                              String email, String phone) {
        boolean ok = true;

        if (fullName.isEmpty()) {
            errFullName.setText("Full name is required."); ok = false;
        }
        if (username.isEmpty()) {
            errUsername.setText("Username is required."); ok = false;
        } else if (CSVHelper.guestUsernameExists(username)) {
            errUsername.setText("This username is already taken."); ok = false;
        }
        if (password.isEmpty()) {
            errPassword.setText("Password is required."); ok = false;
        } else if (password.length() < 6) {
            errPassword.setText("Password must be at least 6 characters."); ok = false;
        }
        if (!password.equals(confirm)) {
            errConfirm.setText("Passwords do not match."); ok = false;
        }
        if (email.isEmpty()) {
            errEmail.setText("Email is required."); ok = false;
        } else if (!email.contains("@") || !email.contains(".")) {
            errEmail.setText("Enter a valid email address."); ok = false;
        }
        if (phone.isEmpty()) {
            errPhone.setText("Phone number is required."); ok = false;
        }

        return ok;
    }

    private void handleRegister() {
        clearErrors();

        String fullName = tfFullName.getText().trim();
        String username = tfUsername.getText().trim();
        String password = new String(tfPassword.getPassword());
        String confirm  = new String(tfConfirm.getPassword());
        String email    = tfEmail.getText().trim();
        String phone    = tfPhone.getText().trim();

        if (!validate(fullName, username, password, confirm, email, phone)) return;

        CSVHelper.addGuest(fullName, username, password, email, phone);

        JOptionPane.showMessageDialog(this,
            "Account created! You can now sign in.",
            "Registration Successful", JOptionPane.INFORMATION_MESSAGE);

        dispose();
        new GuestLoginFrame();
    }
}
