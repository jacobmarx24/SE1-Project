package Elias.files;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * UITheme — Centralized styling for the Aura Hotel application.
 * Provides colors, fonts, and component factories for a modern look.
 */
public class UITheme {

    // ── Colors ─────────────────────────────────────────────────────────────
    public static final Color BG         = new Color(0xF9FAFB); // Light gray background
    public static final Color SURFACE    = Color.WHITE;         // Card background
    public static final Color TEXT_PRI   = new Color(0x111827); // Dark gray (Slate 900)
    public static final Color TEXT_SEC   = new Color(0x4B5563); // Medium gray (Slate 600)
    public static final Color ACCENT     = new Color(0x2563EB); // Modern Blue (Blue 600)
    public static final Color ACCENT_HOV = new Color(0x1D4ED8); // Darker Blue (Blue 700)
    public static final Color ERR        = new Color(0xDC2626); // Red (Red 600)
    public static final Color SUCCESS    = new Color(0x059669); // Green (Emerald 600)
    public static final Color BORDER     = new Color(0xE5E7EB); // Gray 200

    // ── Fonts ──────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_BODY  = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BOLD  = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_INPUT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 12);

    // ── Factory Methods ────────────────────────────────────────────────────

    public static void applyDefaults(JFrame frame, String title, int width, int height) {
        frame.setTitle(title);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Set standard icon if available
    }

    public static JPanel card(int px, int py) {
        JPanel p = new JPanel();
        p.setBackground(SURFACE);
        p.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1),
            new EmptyBorder(py, px, py, px)
        ));
        return p;
    }

    public static JLabel logoLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 24));
        l.setForeground(ACCENT);
        return l;
    }

    public static JComponent divider() {
        JSeparator s = new JSeparator();
        s.setForeground(BORDER);
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return s;
    }

    public static JTextField textField(String placeholder) {
        JTextField tf = new JTextField();
        styleField(tf, placeholder);
        return tf;
    }

    public static JPasswordField passwordField(String placeholder) {
        JPasswordField pf = new JPasswordField();
        styleField(pf, placeholder);
        return pf;
    }

    private static void styleField(JTextField f, String placeholder) {
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        // Simple placeholder logic could be added here if needed
    }

    public static JLabel errorLabel() {
        JLabel l = new JLabel(" ");
        l.setFont(FONT_SMALL);
        l.setForeground(ERR);
        return l;
    }

    public static JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BOLD);
        l.setForeground(TEXT_PRI);
        return l;
    }

    public static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BOLD);
        b.setForeground(Color.WHITE);
        b.setBackground(ACCENT);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 20, 10, 20));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Basic hover effect
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(ACCENT_HOV); }
            public void mouseExited(MouseEvent e) { b.setBackground(ACCENT); }
        });
        
        return b;
    }

    public static JButton ghostButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BOLD);
        b.setForeground(TEXT_SEC);
        b.setBackground(SURFACE);
        b.setFocusPainted(false);
        b.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1),
            new EmptyBorder(10, 20, 10, 20)
        ));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(BG); }
            public void mouseExited(MouseEvent e) { b.setBackground(SURFACE); }
        });

        return b;
    }

    public static JButton linkButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BODY);
        b.setForeground(ACCENT);
        b.setBorder(null);
        b.setContentAreaFilled(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setText("<html><u>" + text + "</u></html>");
            }
            public void mouseExited(MouseEvent e) {
                b.setText(text);
            }
        });
        return b;
    }
}
