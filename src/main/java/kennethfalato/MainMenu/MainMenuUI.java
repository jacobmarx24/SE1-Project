package kennethfalato.MainMenu;


import Elias.files.BillUI;
import Elias.files.GuestLoginFrame;
import jacobmarx.ReserveRoom.RoomSelectionUI;
import Shop.ShopUI;

import javax.swing.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.*;

public class MainMenuUI {
    //public static void main(String[] args) {
   //     SwingUtilities.invokeLater(MainMenuUI::createUI);
    //}

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
        JFrame frame = new JFrame("Main Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        //Buttons
        JButton logOut = new JButton("Log Out");
        JButton searchRoom = new JButton("Search Room");
        JButton resInfo = new JButton("Reservation Info");
        JButton shop = new JButton("Shop");
        JButton bill = new JButton("View Bill");

        logOut = buttonUI(logOut);
        searchRoom = buttonUI(searchRoom);
        resInfo = buttonUI(resInfo);
        shop = buttonUI(shop);
        bill = buttonUI(bill);

        //Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(logOut);
        frame.add(topPanel, BorderLayout.NORTH);

        //Grid Panel for buttons
        JPanel centralPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        centralPanel.add(searchRoom);
        centralPanel.add(resInfo);
        centralPanel.add(shop);
        centralPanel.add(bill);
        JPanel gidL = new JPanel(new GridBagLayout());
        gidL.add(centralPanel);

        frame.add(gidL, BorderLayout.CENTER);
        frame.setVisible(true);

        resInfo.addActionListener(e -> {
            frame.dispose();
            jacobmarx.ReserveRoom.ReservationInfoUI.createUI();
        });

        logOut.addActionListener(e -> {
            frame.dispose();
            new GuestLoginFrame();
        });

        searchRoom.addActionListener(e -> {
            frame.dispose();
            RoomSelectionUI.createUI();
        });

        shop.addActionListener(e -> {
            frame.dispose();
            ShopUI.createUI();
        });

        bill.addActionListener(e -> {
            frame.dispose();
            BillUI.createUI();
        });
    }

}
