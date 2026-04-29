package kennethfalato.MainMenu;


import Elias.files.BillUI;
import Elias.files.GuestLoginFrame;
import jacobmarx.ReserveRoom.RoomSelectionUI;
import Shop.ShopUI;

import javax.swing.*;
import java.awt.*;

public class MainMenuUI {
    //public static void main(String[] args) {
   //     SwingUtilities.invokeLater(MainMenuUI::createUI);
    //}
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
