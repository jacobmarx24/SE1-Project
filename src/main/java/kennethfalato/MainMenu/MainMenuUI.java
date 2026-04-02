package kennethfalato.MainMenu;


import javax.swing.*;
import java.awt.*;

public class MainMenuUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenuUI::createUI);
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

        //TODO: add logic for buttons

    }

}
