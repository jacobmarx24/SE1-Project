package jacobmarx.ReserveRoom;

import jacobmarx.RoomsAndReservation.ReservationService;
import jacobmarx.RoomsAndReservation.Room;
import jacobmarx.RoomsAndReservation.RoomService;
import kennethfalato.MainMenu.MainMenuUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class RoomSelectionUI {

    private static DefaultTableModel tableModel;
    private static JTextField roomTypeBar;
    private static JTextField startDateBar;
    private static JTextField endDateBar;

    public static String currentUsername = "guest";

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
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                MainMenuUI.createUI();
            }
        });
        topPanel.add(backButton);
        topPanel.add(Box.createHorizontalGlue()); //force to right
        JButton reserveButton = new JButton("Reserve Selected Rooms");
        reserveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reserveRoom();
            }
        });
        topPanel.add(reserveButton);
        frame.add(topPanel, BorderLayout.NORTH);


        // Table columns
        String[] columnNames = {
                "Select", "Room #", "Room Type", "Price", "Earliest Date", "Smoking"
        };

        //a room has reservation objects. search will give more detailed date windows
        Object[][] data = loadCols();

        tableModel = new DefaultTableModel(data, columnNames) {
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

        JTable table = new JTable(tableModel);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel (search features)
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));

        roomTypeBar = new JTextField();
        roomTypeBar.setText("Room Type");
        bottomPanel.add(roomTypeBar);
        startDateBar = new JTextField();
        startDateBar.setText("Start Date (MM/DD/YYYY)");
        bottomPanel.add(startDateBar);
        endDateBar = new JTextField();
        endDateBar.setText("End Date (MM/DD/YYYY)");
        bottomPanel.add(endDateBar);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        bottomPanel.add(searchButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static void performSearch() {
        String roomType = roomTypeBar.getText();
        if ("Room Type".equals(roomType)) roomType = "";
        
        String startDateStr = startDateBar.getText();
        if ("Start Date (MM/DD/YYYY)".equals(startDateStr)) startDateStr = "";
        
        String endDateStr = endDateBar.getText();
        if ("End Date (MM/DD/YYYY)".equals(endDateStr)) endDateStr = "";

        RoomService rs = new RoomService("rooms.xml");
        ReservationService resService = new ReservationService("reservations.xml");
        List<Room> allRooms = rs.getRooms();
        List<Room> filteredRooms = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        Date startDate = null;
        Date endDate = null;

        try {
            if (!startDateStr.isEmpty()) startDate = format.parse(startDateStr);
            if (!endDateStr.isEmpty()) endDate = format.parse(endDateStr);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Invalid date format. Please use MM/DD/YYYY.");
            return;
        }

        for (Room room : allRooms) {
            boolean matchesType = roomType.isEmpty() || room.getRoomType().equalsIgnoreCase(roomType);
            boolean isAvailable = true;
            if (startDate != null && endDate != null) {
                isAvailable = !resService.isReserved(room.getNumber(), startDate, endDate);
            } else if (startDate != null) {
                isAvailable = !resService.isReserved(room.getNumber(), startDate);
            }

            if (matchesType && isAvailable) {
                filteredRooms.add(room);
            }
        }

        // Update table data
        Object[][] newData = new Object[filteredRooms.size()][6];
        for (int i = 0; i < filteredRooms.size(); i++) {
            Room room = filteredRooms.get(i);
            newData[i][0] = false;
            newData[i][1] = room.getNumber();
            newData[i][2] = room.getRoomType();
            newData[i][3] = room.getPrice();
            
            Date earliest = new Date();
            while (resService.isReserved(room.getNumber(), earliest)) {
                earliest.setTime(earliest.getTime() + (24 * 60 * 60 * 1000)); // Add one day
            }
            newData[i][4] = format.format(earliest);
            newData[i][5] = room.isSmoking();
        }

        tableModel.setDataVector(newData, new String[]{
                "Select", "Room #", "Room Type", "Price", "Earliest Date", "Smoking"
        });
    }

    private static void reserveRoom() {
        String startText = startDateBar.getText();
        if ("Start Date (MM/DD/YYYY)".equals(startText)) startText = "";
        String endText = endDateBar.getText();
        if ("End Date (MM/DD/YYYY)".equals(endText)) endText = "";

        JTextField startField = new JTextField(startText, 10);
        JTextField endField = new JTextField(endText, 10);

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Start Date (MM/dd/yyyy):"));
        myPanel.add(startField);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("End Date (MM/dd/yyyy):"));
        myPanel.add(endField);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Please Enter Begin and End Dates", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            Date startDate = null;
            Date endDate = null;
            Date[] validatedDates = validateDates(startField.getText(), endField.getText());
            if (validatedDates == null) {
                return;
            }
            startDate = validatedDates[0];
            endDate = validatedDates[1];

            List<Integer> selectedRoomIds = new ArrayList<>();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Boolean isSelected = (Boolean) tableModel.getValueAt(i, 0);
                if (isSelected != null && isSelected) {
                    selectedRoomIds.add((Integer) tableModel.getValueAt(i, 1));
                }
            }

            if (selectedRoomIds.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No rooms selected.");
                return;
            }

            ReservationService resService = new ReservationService("reservations.xml");
            resService.updateReservationsCSV(currentUsername, startDate, endDate, selectedRoomIds);
            JOptionPane.showMessageDialog(null, "Reservation successful!");
        }
    }

    private static Date[] validateDates(String startStr, String endStr) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        format.setLenient(false);
        Date startDate;
        Date endDate;
        try {
            startDate = format.parse(startStr);
            endDate = format.parse(endStr);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Invalid date format. Please use MM/dd/yyyy.");
            return null;
        }

        Date today = new Date();
        // Set today to start of day for comparison
        SimpleDateFormat dayOnly = new SimpleDateFormat("MM/dd/yyyy");
        try {
            today = dayOnly.parse(dayOnly.format(today));
        } catch (ParseException ignored) {}

        if (startDate.before(today)) {
            JOptionPane.showMessageDialog(null, "Start date cannot be in the past.");
            return null;
        }

        if (endDate.before(startDate)) {
            JOptionPane.showMessageDialog(null, "End date must be after or equal to start date.");
            return null;
        }

        return new Date[]{startDate, endDate};
    }

    //Helper method which reads rooms.xml and reservations.xml, updating the cols accordingly
    private static Object[][] loadCols(){
        RoomService rs = new RoomService("rooms.xml");
        ReservationService resService = new ReservationService("reservations.xml");
        List<Room> rooms = rs.getRooms();
        
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        Date today = new Date();
        
        //Turn this room list into a Object[][]
        Object[][] data =  new Object[rooms.size()][6];
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            data[i][0] = false;
            data[i][1] = room.getNumber();
            data[i][2] = room.getRoomType();
            data[i][3] = room.getPrice();
            
            //Determine the earliest date based off of the reservations
            Date earliest = new Date(today.getTime());
            while (resService.isReserved(room.getNumber(), earliest)) {
                earliest.setTime(earliest.getTime() + (24 * 60 * 60 * 1000)); // Add one day
            }
            data[i][4] = format.format(earliest);
            data[i][5] = room.isSmoking();
        }

        return data;
    }
}