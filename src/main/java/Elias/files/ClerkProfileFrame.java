package Elias.files;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * ClerkProfileFrame — lets a logged-in clerk view and update their own profile.
 *
 * Use-case: Clerk Modify Own Profile
 *   1. Clerk views current profile information (pre-populated fields).
 *   2. Clerk edits any fields they want to change.
 *   3. System validates and saves; shows inline errors on failure.
 *
 * Fields the clerk can change: Full Name, Password (with confirm).
 * Username is shown read-only (it is the clerk's identity key).
 * Role and permissions are shown read-only (admin-managed).
 */
public class ClerkProfileFrame extends JFrame {

    // The row array returned by DatabaseHelper:
    // [0] id  [1] fullName  [2] username  [3] password
    // [4] role  [5] permissions  [6] createdAt
    private String[] clerkData;

    // Editable fields
    private JTextField     tfFullName;
    private JPasswordField tfNewPassword;
    private JPasswordField tfConfirmPassword;

    // Read-only display labels
    private JLabel valUsername;
    private JLabel valRole;
    private JLabel valPermissions;
    private JLabel valCreatedAt;

    // Inline error / success labels
    private JLabel errFullName;
    private JLabel errPassword;
    private JLabel errConfirm;
    private JLabel lblSuccess;

    public ClerkProfileFrame(String[] clerkData) {
        this.clerkData = clerkData;
        UITheme.applyDefaults(this, "My Profile — Aura Hotel", 500, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        add(buildNav(),  BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    // ── Top nav ───────────────────────────────────────────────────────────────
    private JPanel buildNav() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(UITheme.SURFACE);
        nav.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
                new EmptyBorder(10, 20, 10, 20)
        ));

        JLabel brand = UITheme.logoLabel("Aura Hotel");

        JLabel roleTag = new JLabel("Clerk Portal");
        roleTag.setFont(UITheme.FONT_SMALL);
        roleTag.setForeground(UITheme.TEXT_SEC);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setBackground(UITheme.SURFACE);
        left.add(brand);
        left.add(new JLabel("|") {{ setForeground(UITheme.BORDER); }});
        left.add(roleTag);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setBackground(UITheme.SURFACE);

        JLabel userName = new JLabel(clerkData[1]);   // fullName
        userName.setFont(UITheme.FONT_BODY);
        userName.setForeground(UITheme.TEXT_SEC);

        JButton btnLogout = UITheme.ghostButton("Sign Out");
        btnLogout.setPreferredSize(new Dimension(90, 32));
        btnLogout.addActionListener(e -> { dispose(); new ClerkLoginFrame(); });

        right.add(userName);
        right.add(btnLogout);

        nav.add(left,  BorderLayout.WEST);
        nav.add(right, BorderLayout.EAST);
        return nav;
    }

    // ── Main body ─────────────────────────────────────────────────────────────
    private JPanel buildBody() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UITheme.BG);
        outer.setBorder(new EmptyBorder(28, 32, 28, 32));

        JScrollPane scroll = new JScrollPane(buildCard());
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(10);

        outer.add(scroll, new GridBagConstraints() {{
            fill = BOTH; weightx = 1; weighty = 1;
        }});
        return outer;
    }

    // ── Profile card ──────────────────────────────────────────────────────────
    private JPanel buildCard() {
        JPanel card = UITheme.card(28, 24);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Title
        JLabel title = new JLabel("My Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(UITheme.TEXT_PRI);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Update your name or password below. Username, role, and permissions are managed by an admin.");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_SEC);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(18));
        card.add(UITheme.divider());
        card.add(Box.createVerticalStrut(20));

        // ── Read-only info section ────────────────────────────────────────────
        card.add(sectionHeader("Account Info"));
        card.add(Box.createVerticalStrut(12));
        card.add(readOnlyRow("Username",    clerkData[2]));
        card.add(Box.createVerticalStrut(8));
        card.add(readOnlyRow("Role",        clerkData[4]));
        card.add(Box.createVerticalStrut(8));
        card.add(readOnlyRow("Permissions", formatPermissions(clerkData[5])));
        card.add(Box.createVerticalStrut(8));
        card.add(readOnlyRow("Member Since", clerkData[6]));
        card.add(Box.createVerticalStrut(20));
        card.add(UITheme.divider());
        card.add(Box.createVerticalStrut(20));

        // ── Editable section ──────────────────────────────────────────────────
        card.add(sectionHeader("Edit Profile"));
        card.add(Box.createVerticalStrut(14));

        // Full Name
        card.add(fieldLabel("Full Name"));
        card.add(Box.createVerticalStrut(5));
        tfFullName = UITheme.textField("Your full name");
        tfFullName.setText(clerkData[1]);
        stretch(tfFullName);
        card.add(tfFullName);
        errFullName = UITheme.errorLabel();
        errFullName.setAlignmentX(LEFT_ALIGNMENT);
        card.add(errFullName);
        card.add(Box.createVerticalStrut(10));

        // New Password
        card.add(fieldLabel("New Password"));
        card.add(Box.createVerticalStrut(5));
        tfNewPassword = UITheme.passwordField("Leave blank to keep current password");
        stretch(tfNewPassword);
        card.add(tfNewPassword);
        errPassword = UITheme.errorLabel();
        errPassword.setAlignmentX(LEFT_ALIGNMENT);
        card.add(errPassword);
        card.add(Box.createVerticalStrut(10));

        // Confirm Password
        card.add(fieldLabel("Confirm New Password"));
        card.add(Box.createVerticalStrut(5));
        tfConfirmPassword = UITheme.passwordField("Re-enter new password");
        stretch(tfConfirmPassword);
        card.add(tfConfirmPassword);
        errConfirm = UITheme.errorLabel();
        errConfirm.setAlignmentX(LEFT_ALIGNMENT);
        card.add(errConfirm);
        card.add(Box.createVerticalStrut(20));

        // Success banner (hidden until save succeeds)
        lblSuccess = new JLabel(" ");
        lblSuccess.setFont(UITheme.FONT_SMALL);
        lblSuccess.setForeground(UITheme.SUCCESS);
        lblSuccess.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lblSuccess);
        card.add(Box.createVerticalStrut(6));

        // Buttons
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setBackground(UITheme.SURFACE);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton btnSave   = UITheme.primaryButton("Save Changes");
        JButton btnCancel = UITheme.ghostButton("Reset");
        btnSave.addActionListener(e -> handleSave());
        btnCancel.addActionListener(e -> resetForm());
        btnRow.add(btnSave);
        btnRow.add(btnCancel);
        card.add(btnRow);

        return card;
    }

    // ── Save handler ──────────────────────────────────────────────────────────
    private void handleSave() {
        clearErrors();

        String fullName   = tfFullName.getText().trim();
        String newPass    = new String(tfNewPassword.getPassword());
        String confirmPass = new String(tfConfirmPassword.getPassword());

        boolean ok = true;

        // Validate full name (required)
        if (fullName.isEmpty()) {
            errFullName.setText("Full name is required.");
            ok = false;
        }

        // Validate password — only checked when the clerk types something
        boolean changingPassword = !newPass.isEmpty();
        if (changingPassword) {
            if (newPass.length() < 6) {
                errPassword.setText("Password must be at least 6 characters.");
                ok = false;
            }
            if (!newPass.equals(confirmPass)) {
                errConfirm.setText("Passwords do not match.");
                ok = false;
            }
        } else if (!confirmPass.isEmpty()) {
            // Confirm filled but new password is blank
            errPassword.setText("Please enter a new password first.");
            ok = false;
        }

        if (!ok) return;

        // Determine final password value
        String finalPassword = changingPassword ? newPass : clerkData[3];

        // Persist the update
        DatabaseHelper.updateClerk(
                clerkData[0],   // id
                fullName,
                finalPassword,
                clerkData[4],   // role — unchanged
                clerkData[5]    // permissions — unchanged
        );

        // Refresh local copy so nav bar name updates on next repaint
        clerkData[1] = fullName;
        clerkData[3] = finalPassword;

        lblSuccess.setText("✓  Profile updated successfully.");
        tfNewPassword.setText("");
        tfConfirmPassword.setText("");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void resetForm() {
        tfFullName.setText(clerkData[1]);
        tfNewPassword.setText("");
        tfConfirmPassword.setText("");
        clearErrors();
    }

    private void clearErrors() {
        errFullName.setText(" ");
        errPassword.setText(" ");
        errConfirm.setText(" ");
        lblSuccess.setText(" ");
    }

    private JLabel sectionHeader(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(UITheme.TEXT_PRI);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
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

    /** A labelled read-only row showing a key → value pair. */
    private JPanel readOnlyRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(UITheme.SURFACE);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_BODY);
        lbl.setForeground(UITheme.TEXT_SEC);
        lbl.setPreferredSize(new Dimension(110, 20));

        JLabel val = new JLabel(value);
        val.setFont(UITheme.FONT_BODY);
        val.setForeground(UITheme.TEXT_PRI);

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.CENTER);
        return row;
    }

    /** Converts pipe-separated permission keys into a readable string. */
    private String formatPermissions(String raw) {
        if (raw == null || raw.isBlank()) return "None";
        return raw.replace("|", ", ")
                  .replace("_", " ");
    }
}
