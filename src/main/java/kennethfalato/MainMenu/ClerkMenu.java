package kennethfalato.MainMenu;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.util.*;

public class ClerkMenu {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClerkMenu::createUI);
    }

    private static JButton buttonUI(JButton button){
        button.setBackground(new Color(2450411));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(true);



        button.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(1920728));
            } // hover color


            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(2450411)); // original color
            }

        });

        button.getModel().addChangeListener(e -> {
            ButtonModel model = button.getModel();

            if (model.isPressed()) {
                button.setBackground(new Color(93,135,235));
            } else {
                button.setBackground(new Color(2450411));
            }
        });
        return button;

    }

    public static void createUI() {
        JFrame frame = new JFrame("Clerk Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        //Buttons
        JButton roomMGR = new JButton("Room Manager");
        JButton guestMGR = new JButton("Guest Manager");
        JButton checkInOut = new JButton("Check In/Out");
        JButton logOut = new JButton("Log Out");

        roomMGR = buttonUI(roomMGR);
        guestMGR = buttonUI(guestMGR);
        checkInOut = buttonUI(checkInOut);
        logOut = buttonUI(logOut);

        //Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.add(logOut,BorderLayout.EAST);
        JLabel Title = new JLabel("Clerk Menu", SwingConstants.CENTER);
        Title.setFont(new Font("Arial", Font.BOLD, 24));
        Title.setForeground(Color.BLACK);

        topPanel.add(Title,BorderLayout.CENTER);
        topPanel.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, Color.BLACK));
        frame.add(topPanel, BorderLayout.NORTH);

        //Grid Panel for buttons
        JPanel centralPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        centralPanel.add(roomMGR);
        centralPanel.add(guestMGR);
        centralPanel.add(checkInOut);
        JPanel gidL = new JPanel(new GridBagLayout());

        gidL.add(centralPanel);


        frame.add(gidL, BorderLayout.CENTER);
        frame.setVisible(true);

        //TODO: add logic for buttons

    }
}