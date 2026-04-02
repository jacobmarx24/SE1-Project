package kennethfalato;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class GuestLogInUI {
    public static void createUI() {
        Guest g1 = new Guest("kenny1", "pass123", "Kenneth Falato", "123 Maple St");
        Guest g2 = new Guest("jane22", "qwerty", "Jane Smith", "456 Oak Ave");
        Guest g3 = new Guest("bob_guest", "hotel789", "Bob Johnson", "789 Pine Rd");
        Guest g4 = new Guest("aliceJ", "alicepw", "Alice Jones", "321 Elm St");
        ArrayList<User> users = new ArrayList<User>();
        users.add(g1);
        users.add(g2);
        users.add(g3);
        users.add(g4);
        JFrame frame = new JFrame("Main Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        //Buttons
        JButton LogIn = new JButton("Log In");
        JButton Register = new JButton("Register");

        JTextField userName = new JTextField("UserName: ", 20);
         JTextField password = new JTextField("password: ", 20);

        //Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        frame.add(topPanel, BorderLayout.NORTH);

        //Grid Panel for buttons
        JPanel centralPanel = new JPanel(new GridLayout(4, 1, 20, 20));
        centralPanel.add(LogIn);
        centralPanel.add(userName);
        centralPanel.add(password);
        centralPanel.add(Register);
        JPanel gidL = new JPanel(new GridBagLayout());
        gidL.add(centralPanel);

        frame.add(gidL, BorderLayout.CENTER);
        frame.setVisible(true);

        //TODO: add logic for buttons
        LogIn.addActionListener(e -> {
            String user = userName.getText();
            String pass = password.getText();
            for(User g: users){
                if(g.getUserName().equals(user) && g.getPassword().equals(pass)){
                    Guest.setCurrentGuest(g);
                }
            }
        });
    }
}
