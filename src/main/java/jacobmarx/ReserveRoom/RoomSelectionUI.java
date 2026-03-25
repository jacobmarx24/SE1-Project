package jacobmarx.ReserveRoom;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RoomSelectionUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RoomSelectionUI::createUI);
    }

    private static void createUI() {
        JFrame frame = new JFrame("Room Reservation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Top panel (Back button and register button)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

        JButton backButton = new JButton("<--");
        topPanel.add(backButton);
        topPanel.add(Box.createHorizontalGlue()); //force to right
        JButton reserveButton = new JButton("Reserve Selected Rooms");
        topPanel.add(reserveButton);
        frame.add(topPanel, BorderLayout.NORTH);


        //TODO: replace below with the csv reading logic

        // Table columns
        String[] columnNames = {
                "Select", "Room Name", "Room Type", "Price", "Available Date"
        };

        // Sample data (editable checkbox column)
        Object[][] data = {
                {false, "Ocean View Suite", "Suite", "$250", "2026-04-01"},
                {false, "Standard Room", "Single", "$120", "2026-03-28"},
                {false, "Deluxe King", "Double", "$180", "2026-03-30"},
                {false, "Family Room", "Family", "$220", "2026-04-05"},
                {false, "Penthouse", "Luxury", "$500", "2026-04-10"}
        };

        // Table model
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Boolean.class; // checkbox column
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // only checkbox editable
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel (search features)
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));

        JTextField roomTypeBar = new JTextField();
        roomTypeBar.setText("Room Type");
        bottomPanel.add(roomTypeBar);
        JTextField priceBar = new JTextField();
        priceBar.setText("Price Maximum");
        bottomPanel.add(priceBar);
        JTextField startDateBar = new JTextField();
        startDateBar.setText("Start Date (MM/DD/YYYY)");
        bottomPanel.add(startDateBar);
        JTextField endDateBar = new JTextField();
        endDateBar.setText("End Date (MM/DD/YYYY)");
        bottomPanel.add(endDateBar);
        JButton searchButton = new JButton("Search");
        bottomPanel.add(searchButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
}