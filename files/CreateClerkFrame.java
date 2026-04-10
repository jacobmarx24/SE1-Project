import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class CreateClerkFrame extends JFrame {

    private final String[] adminUser;

    // Form fields
    private JTextField     tfFullName;
    private JTextField     tfUsername;
    private JPasswordField tfPassword;
    private JPasswordField tfConfirm;
    private JComboBox<String> cbRole;
    private JCheckBox[]    permBoxes;

    // Per-field errors
    private JLabel errFullName, errUsername, errPassword, errConfirm, errPerm;

    // Accounts table
    private DefaultTableModel tableModel;

    // Permission definitions
    private static final String[][] PERMISSIONS = {
            { "view_bookings",    "View Bookings"    },
            { "manage_bookings",  "Manage Bookings"  },
            { "check_in_out",     "Check In / Out"   },
            { "view_reports",     "View Reports"     },
            { "manage_rooms",     "Manage Rooms"     },
    };

    private static final String[] ROLES = { "clerk", "senior_clerk", "supervisor" };

    public CreateClerkFrame(String[] adminUser) {
        this.adminUser = adminUser;
        UITheme.applyDefaults(this, "Admin Panel — Create Clerk Account", 860, 720);
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

    // ── Top nav bar ───────────────────────────────────────────────────────────
    private JPanel buildNav() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(UITheme.SURFACE);
        nav.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
                new EmptyBorder(10, 20, 10, 20)
        ));

        JLabel brand = UITheme.logoLabel("Aura Hotel");
        JLabel role  = new JLabel("Admin Panel");
        role.setFont(UITheme.FONT_SMALL);
        role.setForeground(UITheme.TEXT_SEC);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setBackground(UITheme.SURFACE);
        left.add(brand);
        left.add(new JLabel("|") {{ setForeground(UITheme.BORDER); }});
        left.add(role);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setBackground(UITheme.SURFACE);

        JLabel userName = new JLabel(adminUser[1]);
        userName.setFont(UITheme.FONT_BODY);
        userName.setForeground(UITheme.TEXT_SEC);

        JButton btnLogout = UITheme.ghostButton("Sign Out");
        btnLogout.setPreferredSize(new Dimension(90, 32));
        btnLogout.addActionListener(e -> { dispose(); new AdminLoginFrame(); });

        right.add(userName);
        right.add(btnLogout);

        nav.add(left,  BorderLayout.WEST);
        nav.add(right, BorderLayout.EAST);
        return nav;
    }

    // ── Main body ─────────────────────────────────────────────────────────────
    private JPanel buildBody() {
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(UITheme.BG);
        body.setBorder(new EmptyBorder(24, 24, 24, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 16);
        gbc.weightx = 0.55; gbc.weighty = 1.0; gbc.gridx = 0; gbc.gridy = 0;
        JScrollPane formScroll = new JScrollPane(buildForm());
        formScroll.setBorder(null);
        formScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        formScroll.getVerticalScrollBar().setUnitIncrement(10);
        body.add(formScroll, gbc);

        gbc.weightx = 0.45; gbc.gridx = 1; gbc.insets = new Insets(0, 0, 0, 0);
        body.add(buildSidebar(), gbc);

        return body;
    }

    // ── Form card ─────────────────────────────────────────────────────────────
    private JPanel buildForm() {
        JPanel card = UITheme.card(24, 24);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Create Clerk Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(UITheme.TEXT_PRI);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Fill in the details to register a new staff member.");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_SEC);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(18));
        card.add(UITheme.divider());
        card.add(Box.createVerticalStrut(18));

        // Two-column row: Full Name | Username
        card.add(twoCol(
                labeled("Full Name",  tfFullName = UITheme.textField("e.g. Maria Santos"),   errFullName = UITheme.errorLabel()),
                labeled("Username",   tfUsername  = UITheme.textField("e.g. msantos"),        errUsername  = UITheme.errorLabel())
        ));

        // Two-column row: Password | Confirm
        tfPassword = UITheme.passwordField("Min. 6 characters");
        tfConfirm  = UITheme.passwordField("Re-enter password");
        card.add(twoCol(
                labeled("Password",         tfPassword, errPassword = UITheme.errorLabel()),
                labeled("Confirm Password", tfConfirm,  errConfirm  = UITheme.errorLabel())
        ));

        // Role
        cbRole = new JComboBox<>(ROLES);
        cbRole.setFont(UITheme.FONT_INPUT);
        cbRole.setBackground(UITheme.SURFACE);
        cbRole.setPreferredSize(new Dimension(0, 36));
        card.add(labeled("Role", cbRole, new JLabel(" ")));
        card.add(Box.createVerticalStrut(6));

        // Permissions
        card.add(fieldLabel("Permissions"));
        card.add(Box.createVerticalStrut(6));
        JPanel permPanel = new JPanel(new GridLayout(0, 2, 8, 6));
        permPanel.setBackground(UITheme.SURFACE);
        permPanel.setAlignmentX(LEFT_ALIGNMENT);
        permBoxes = new JCheckBox[PERMISSIONS.length];
        for (int i = 0; i < PERMISSIONS.length; i++) {
            permBoxes[i] = new JCheckBox(PERMISSIONS[i][1]);
            permBoxes[i].setFont(UITheme.FONT_BODY);
            permBoxes[i].setForeground(UITheme.TEXT_PRI);
            permBoxes[i].setBackground(UITheme.SURFACE);
            permPanel.add(permBoxes[i]);
        }
        permPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.add(permPanel);
        errPerm = UITheme.errorLabel();
        errPerm.setAlignmentX(LEFT_ALIGNMENT);
        card.add(errPerm);
        card.add(Box.createVerticalStrut(20));

        // Buttons
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setBackground(UITheme.SURFACE);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton btnCreate = UITheme.primaryButton("Create Account");
        JButton btnCancel = UITheme.ghostButton("Cancel");
        btnCreate.addActionListener(e -> handleCreate());
        btnCancel.addActionListener(e -> clearForm());
        btnRow.add(btnCreate);
        btnRow.add(btnCancel);
        card.add(btnRow);

        return card;
    }

    // ── Sidebar: accounts table ───────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel card = UITheme.card(16, 16);
        card.setLayout(new BorderLayout(0, 12));

        JLabel title = new JLabel("Staff Accounts");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(UITheme.TEXT_PRI);
        card.add(title, BorderLayout.NORTH);

        String[] cols = { "Name", "Username", "Role" };
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setFont(UITheme.FONT_BODY);
        table.setForeground(UITheme.TEXT_PRI);
        table.setBackground(UITheme.SURFACE);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setFont(UITheme.FONT_LABEL);
        table.getTableHeader().setForeground(UITheme.TEXT_SEC);
        table.getTableHeader().setBackground(UITheme.SURFACE);
        table.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, UITheme.BORDER));
        table.setSelectionBackground(new Color(0xEFF6FF));

        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? UITheme.SURFACE : UITheme.BG);
                c.setForeground(UITheme.TEXT_PRI);
                ((JLabel)c).setBorder(new EmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        refreshTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(UITheme.BORDER, 1, true));
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private JPanel labeled(String labelText, JComponent field, JLabel err) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UITheme.SURFACE);
        p.add(fieldLabel(labelText));
        p.add(Box.createVerticalStrut(4));
        field.setAlignmentX(LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        p.add(field);
        err.setAlignmentX(LEFT_ALIGNMENT);
        p.add(err);
        p.add(Box.createVerticalStrut(10));
        return p;
    }

    private JLabel fieldLabel(String text) {
        JLabel l = UITheme.sectionLabel(text);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JPanel twoCol(JPanel a, JPanel b) {
        JPanel row = new JPanel(new GridLayout(1, 2, 12, 0));
        row.setBackground(UITheme.SURFACE);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        row.add(a); row.add(b);
        return row;
    }

    private void clearErrors() {
        errFullName.setText(" "); errUsername.setText(" ");
        errPassword.setText(" "); errConfirm.setText(" ");
        errPerm.setText(" ");
    }

    private void clearForm() {
        tfFullName.setText(""); tfUsername.setText("");
        tfPassword.setText(""); tfConfirm.setText("");
        cbRole.setSelectedIndex(0);
        for (JCheckBox cb : permBoxes) cb.setSelected(false);
        clearErrors();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<String[]> rows = DatabaseHelper.readAll("clerks.csv");
        for (String[] r : rows) {
            if (r.length > 4)
                tableModel.addRow(new Object[]{ r[1], r[2], r[4] });
        }
    }

    private void handleCreate() {
        clearErrors();
        boolean ok = true;

        String fullName = tfFullName.getText().trim();
        String username = tfUsername.getText().trim();
        String password = new String(tfPassword.getPassword());
        String confirm  = new String(tfConfirm.getPassword());
        String role     = (String) cbRole.getSelectedItem();

        if (fullName.isEmpty()) { errFullName.setText("Full name is required."); ok = false; }
        if (username.isEmpty()) {
            errUsername.setText("Username is required."); ok = false;
        } else if (DatabaseHelper.clerkUsernameExists(username)) {
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

        StringBuilder perms = new StringBuilder();
        for (int i = 0; i < permBoxes.length; i++) {
            if (permBoxes[i].isSelected()) {
                if (perms.length() > 0) perms.append("|");
                perms.append(PERMISSIONS[i][0]);
            }
        }
        if (perms.length() == 0) {
            errPerm.setText("Select at least one permission."); ok = false;
        }

        if (!ok) return;

        DatabaseHelper.addClerk(fullName, username, password, role, perms.toString());
        refreshTable();
        clearForm();

        JOptionPane.showMessageDialog(this,
                "Clerk account for \"" + fullName + "\" created successfully.",
                "Account Created", JOptionPane.INFORMATION_MESSAGE);
    }
}