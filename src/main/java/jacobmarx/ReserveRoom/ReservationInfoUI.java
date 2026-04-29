package jacobmarx.ReserveRoom;

import jacobmarx.RoomsAndReservation.Reservation;
import jacobmarx.RoomsAndReservation.ReservationService;
import Elias.files.BillService;
import kennethfalato.MainMenu.MainMenuUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReservationInfoUI {

    private static DefaultTableModel tableModel;
    private static JTable table;
    private static ReservationService resService;
    private static String username;

    public static void createUI() {
        username = RoomSelectionUI.currentUsername;
        resService = new ReservationService("reservations.xml");
        List<Reservation> userReservations = resService.getReservationsByUsername(username);

        JFrame frame = new JFrame("Your Reservations");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("<--");
        backButton.addActionListener(e -> {
            frame.dispose();
            MainMenuUI.createUI();
        });
        topPanel.add(backButton, BorderLayout.WEST);
        JLabel titleLabel = new JLabel("Reservations for " + username, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(titleLabel, BorderLayout.CENTER);
        frame.add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Start Date", "End Date", "Rooms"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        for (Reservation res : userReservations) {
            tableModel.addRow(new Object[]{
                    format.format(res.getStartDate()),
                    format.format(res.getEndDate()),
                    res.getRoomNums().toString()
            });
        }

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel();
        JButton cancelButton = new JButton("Cancel Reservation");
        JButton updateButton = new JButton("Change Date Range");

        cancelButton.addActionListener(e -> cancelReservation(userReservations));
        updateButton.addActionListener(e -> updateDateRange(userReservations));

        bottomPanel.add(cancelButton);
        bottomPanel.add(updateButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static void cancelReservation(List<Reservation> userReservations) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a reservation to cancel.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to cancel this reservation?", "Confirm Cancel", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Reservation res = userReservations.get(selectedRow);
            resService.removeReservation(res);
            resService.saveAllReservations();
            BillService.applyCancellationPenalty(username);
            tableModel.removeRow(selectedRow);
            userReservations.remove(selectedRow);
            JOptionPane.showMessageDialog(null, "Reservation canceled. A $10 penalty has been added to your bill.");
        }
    }

    private static void updateDateRange(List<Reservation> userReservations) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a reservation to update.");
            return;
        }

        Reservation res = userReservations.get(selectedRow);

        JTextField startField = new JTextField(new SimpleDateFormat("MM/dd/yyyy").format(res.getStartDate()), 10);
        JTextField endField = new JTextField(new SimpleDateFormat("MM/dd/yyyy").format(res.getEndDate()), 10);

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Start Date (MM/dd/yyyy):"));
        myPanel.add(startField);
        myPanel.add(Box.createHorizontalStrut(15));
        myPanel.add(new JLabel("End Date (MM/dd/yyyy):"));
        myPanel.add(endField);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Enter New Date Range", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            Date[] validatedDates = validateDates(startField.getText(), endField.getText());
            if (validatedDates == null) return;

            Date newStart = validatedDates[0];
            Date newEnd = validatedDates[1];

            // Check availability
            for (Integer roomId : res.getRoomNums()) {
                if (resService.isReservedExcluding(roomId, newStart, newEnd, res)) {
                    JOptionPane.showMessageDialog(null, "Room " + roomId + " is not available for the selected dates.");
                    return;
                }
            }

            // Update reservation
            res.setStartDate(newStart);
            res.setEndDate(newEnd);
            resService.saveAllReservations();

            // Update table
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            tableModel.setValueAt(format.format(newStart), selectedRow, 0);
            tableModel.setValueAt(format.format(newEnd), selectedRow, 1);

            JOptionPane.showMessageDialog(null, "Reservation updated successfully!");
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
}
