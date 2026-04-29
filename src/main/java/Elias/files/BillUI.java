package Elias.files;

import jacobmarx.ReserveRoom.RoomSelectionUI;
import kennethfalato.MainMenu.MainMenuUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BillUI extends JFrame {

    private String username;
    private JLabel lblTotal;

    public BillUI(String username) {
        this.username = username;
        UITheme.applyDefaults(this, "Your Bill — Aura Hotel", 400, 450);
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

        JLabel logo = UITheme.logoLabel("Aura Hotel");
        logo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Guest Billing");
        subtitle.setFont(UITheme.FONT_SMALL);
        subtitle.setForeground(UITheme.TEXT_SEC);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel heading = new JLabel("Current Outstanding Balance");
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

        double currentBill = BillService.getGuestBill(username);
        lblTotal = new JLabel(String.format("$%.2f", currentBill));
        lblTotal.setFont(new Font("Inter", Font.BOLD, 36));
        lblTotal.setForeground(UITheme.ACCENT);
        lblTotal.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lblTotal);

        card.add(Box.createVerticalStrut(30));

        JButton btnPay = UITheme.primaryButton("Pay Bill Now");
        btnPay.setAlignmentX(LEFT_ALIGNMENT);
        btnPay.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnPay.addActionListener(e -> handlePayment());
        card.add(btnPay);

        card.add(Box.createVerticalStrut(12));

        JButton btnBack = UITheme.ghostButton("Back to Menu");
        btnBack.setAlignmentX(LEFT_ALIGNMENT);
        btnBack.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnBack.addActionListener(e -> {
            dispose();
            MainMenuUI.createUI();
        });
        card.add(btnBack);

        outer.add(card, new GridBagConstraints());
        add(outer, BorderLayout.CENTER);
    }

    private void handlePayment() {
        double currentBill = BillService.getGuestBill(username);
        if (currentBill <= 0) {
            JOptionPane.showMessageDialog(this, "Your bill is already clear!", "No Balance", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this, 
            String.format("Confirm payment of $%.2f?", currentBill),
            "Confirm Payment", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            BillService.payBill(username);
            lblTotal.setText("$0.00");
            JOptionPane.showMessageDialog(this, "Payment successful! Your bill has been cleared.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void createUI() {
        new BillUI(RoomSelectionUI.currentUsername);
    }
}
