package jacobmarx.ReserveRoom;

import jacobmarx.RoomsAndReservation.Room;
import jacobmarx.RoomsAndReservation.RoomService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RoomSelectionUI {

    public static void createUI() {
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


        // Table columns
        String[] columnNames = {
                "Select", "Room #", "Room Type", "Price", "Earliest Date", "Smoking"
        };

        //TODO: replace below with the csv reading logic
        // Sample data (editable checkbox column)

        //a room has reservation objects. search will give more detailed date windows
        Object[][] data = loadCols();

        // Table model
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0 || column == 5) return Boolean.class; // checkbox column
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

    //Helper method which reads rooms.csv and reservations.csv, updating the cols accordingly
    private static Object[][] loadCols(){
        //PLACEHOLDER
        Object[][] d = {
                {false, "100", "Executive", "$250", "2026-04-01", false},
                {false, "101", "Business", "$120", "2026-03-28", false},
                {false, "102", "Comfort", "$180", "2026-03-30", true},
                {false, "103", "Economy", "$220", "2026-04-05", false},
                {false, "104", "Economy", "$500", "2026-04-10", true}
        };

        RoomService rs = new RoomService("rooms.csv");
        List<Room> rooms = rs.getRooms();
        //Turn this room list into a Object[][]
        Object[][] data =  new Object[rooms.size()][6];
        for (int i = 0; i < rooms.size(); i++) {
            data[i][0] = false;
            data[i][1] = rooms.get(i).getNumber();
            data[i][2] = rooms.get(i).getRoomType();
            data[i][3] = rooms.get(i).getPrice();
            //Determine the earliest date based off of the reservations
            data[i][4] = "test";
            data[i][5] = rooms.get(i).isSmoking();
        }

        return data;
    }
}
