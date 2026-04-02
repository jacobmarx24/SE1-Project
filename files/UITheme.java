import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class UITheme {

    // ── Palette ───────────────────────────────────────────────────────────────
    public static final Color BG         = new Color(0xF7F8FA);
    public static final Color SURFACE    = Color.WHITE;
    public static final Color ACCENT     = new Color(0x2563EB);   // blue
    public static final Color ACCENT_HOV = new Color(0x1D4ED8);
    public static final Color DANGER     = new Color(0xEF4444);
    public static final Color SUCCESS    = new Color(0x16A34A);
    public static final Color TEXT_PRI   = new Color(0x111827);
    public static final Color TEXT_SEC   = new Color(0x6B7280);
    public static final Color TEXT_HINT  = new Color(0x9CA3AF);
    public static final Color BORDER     = new Color(0xE5E7EB);
    public static final Color BORDER_FOC = new Color(0x2563EB);
    public static final Color TAG_BG     = new Color(0xEFF6FF);
    public static final Color TAG_FG     = new Color(0x1D4ED8);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,   22);
    public static final Font FONT_LABEL  = new Font("Segoe UI", Font.BOLD,   11);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN,  13);
    public static final Font FONT_INPUT  = new Font("Segoe UI", Font.PLAIN,  13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN,  11);
    public static final Font FONT_BTN    = new Font("Segoe UI", Font.BOLD,   13);

    // ── Window setup ──────────────────────────────────────────────────────────
    public static void applyDefaults(JFrame frame, String title, int w, int h) {
        frame.setTitle(title);
        frame.setSize(w, h);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        frame.getContentPane().setBackground(BG);
    }

    // ── Card panel ────────────────────────────────────────────────────────────
    public static JPanel card(int padH, int padV) {
        JPanel p = new JPanel();
        p.setBackground(SURFACE);
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(padV, padH, padV, padH)
        ));
        return p;
    }

    // ── Section label ─────────────────────────────────────────────────────────
    public static JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT_SEC);
        return l;
    }

    // ── Styled text field ─────────────────────────────────────────────────────
    public static JTextField textField(String placeholder) {
        JTextField tf = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(TEXT_HINT);
                    g2.setFont(FONT_INPUT);
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left + 2, getHeight() / 2 + 5);
                }
            }
        };
        styleInput(tf);
        return tf;
    }

    // ── Styled password field ─────────────────────────────────────────────────
    public static JPasswordField passwordField(String placeholder) {
        JPasswordField pf = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(TEXT_HINT);
                    g2.setFont(FONT_INPUT);
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left + 2, getHeight() / 2 + 5);
                }
            }
        };
        styleInput(pf);
        return pf;
    }

    private static void styleInput(JComponent tf) {
        tf.setFont(FONT_INPUT);
        tf.setForeground(TEXT_PRI);
        tf.setBackground(SURFACE);
        tf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        tf.setPreferredSize(new Dimension(0, 38));
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER_FOC, 2, true),
                    new EmptyBorder(7, 9, 7, 9)
                ));
                tf.repaint();
            }
            public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER, 1, true),
                    new EmptyBorder(8, 10, 8, 10)
                ));
                tf.repaint();
            }
        });
    }

    // ── Primary button ────────────────────────────────────────────────────────
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? ACCENT_HOV : ACCENT);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(Color.WHITE);
                g2.setFont(FONT_BTN);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()  - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(0, 40));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Ghost / outline button ────────────────────────────────────────────────
    public static JButton ghostButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? new Color(0xF3F4F6) : SURFACE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 8, 8));
                g2.setColor(TEXT_SEC);
                g2.setFont(FONT_BTN);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()  - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(0, 40));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Hyperlink-style button ────────────────────────────────────────────────
    public static JButton linkButton(String text) {
        JButton btn = new JButton("<html><u>" + text + "</u></html>");
        btn.setFont(FONT_BODY);
        btn.setForeground(ACCENT);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(ACCENT_HOV); }
            public void mouseExited (MouseEvent e) { btn.setForeground(ACCENT); }
        });
        return btn;
    }

    // ── Error label ───────────────────────────────────────────────────────────
    public static JLabel errorLabel() {
        JLabel l = new JLabel(" ");
        l.setFont(FONT_SMALL);
        l.setForeground(DANGER);
        return l;
    }

    // ── Success banner ────────────────────────────────────────────────────────
    public static JPanel successBanner(String msg) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(0xF0FDF4));
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(0xBBF7D0), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        JLabel l = new JLabel(msg);
        l.setFont(FONT_BODY);
        l.setForeground(SUCCESS);
        p.add(l);
        return p;
    }

    // ── Divider line ──────────────────────────────────────────────────────────
    public static JSeparator divider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        return sep;
    }

    // ── Logo label ────────────────────────────────────────────────────────────
    public static JLabel logoLabel(String hotelName) {
        JLabel l = new JLabel(hotelName);
        l.setFont(new Font("Segoe UI", Font.BOLD, 20));
        l.setForeground(TEXT_PRI);
        return l;
    }
}
