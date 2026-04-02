import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class GuestLoginFrame extends JFrame {

    private JTextField     tfUsername;
    private JPasswordField tfPassword;
    private JLabel         lblError;

    public GuestLoginFrame() {
        UITheme.applyDefaults(this, "Guest Login — Aura Hotel", 420, 520);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        // ── Outer padding wrapper ─────────────────────────────────────────────
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UITheme.BG);
        outer.setBorder(new EmptyBorder(30, 32, 30, 32));

        JPanel card = UITheme.card(32, 30);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // ── Header ───────────────────────────────────────────────────────────
        JLabel logo = UITheme.logoLabel("Aura Hotel");
        logo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Guest Portal");
        subtitle.setFont(UITheme.FONT_SMALL);
        subtitle.setForeground(UITheme.TEXT_SEC);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel heading = new JLabel("Sign in to your account");
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
        card.add(Box.createVerticalStrut(24));

        // ── Username ──────────────────────────────────────────────────────────
        card.add(fieldLabel("Username"));
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
        card.add(Box.createVerticalStrut(20));

        // ── Register link ─────────────────────────────────────────────────────
        JPanel linkRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linkRow.setBackground(UITheme.SURFACE);
        linkRow.setAlignmentX(LEFT_ALIGNMENT);

        JLabel noAccount = new JLabel("Don't have an account? ");
        noAccount.setFont(UITheme.FONT_BODY);
        noAccount.setForeground(UITheme.TEXT_SEC);

        JButton btnRegister = UITheme.linkButton("Create one here");
        btnRegister.addActionListener(e -> openRegister());

        linkRow.add(noAccount);
        linkRow.add(btnRegister);
        card.add(linkRow);

        // ── Allow Enter key to submit ─────────────────────────────────────────
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

        String[] guest = CSVHelper.findGuest(username, password);
        if (guest == null) {
            lblError.setText("Incorrect username or password.");
            tfPassword.setText("");
        } else {
            lblError.setText(" ");
            JOptionPane.showMessageDialog(this,
                "Welcome back, " + guest[1] + "!",
                "Login Successful", JOptionPane.INFORMATION_MESSAGE);
            // TODO: open guest dashboard
            dispose();
        }
    }

    private void openRegister() {
        dispose();
        new GuestRegisterFrame();
    }
}
