package Elias.files;

import jacobmarx.RoomsAndReservation.Reservation;
import jacobmarx.RoomsAndReservation.ReservationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ClerkMenuFrame extends JFrame {
    private String[] clerkData;
    private ReservationService reservationService;
    private JTable table;
    private DefaultTableModel tableModel;

    public ClerkMenuFrame() {
        this(null);
    }

    public ClerkMenuFrame(String[] clerkData) {
        this.clerkData = clerkData;
        this.reservationService = new ReservationService("reservations.csv");
        UITheme.applyDefaults(this, "Clerk Menu — Aura Hotel", 900, 600);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.BG);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton backBtn = UITheme.ghostButton("Back");
        backBtn.addActionListener(e -> {
            dispose();
            kennethfalato.MainMenu.ClerkMenu.createUI(clerkData);
        });
        topPanel.add(backBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Guest", "Room #s", "Start Date", "End Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        
        refreshTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Actions
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(UITheme.BG);

        JButton checkInBtn = UITheme.primaryButton("Check Guest In");
        checkInBtn.setPreferredSize(new Dimension(180, 40));
        checkInBtn.addActionListener(e -> handleCheckIn());
        
        JButton modifyBtn = UITheme.ghostButton("Modify Reservation");
        modifyBtn.setPreferredSize(new Dimension(180, 40));
        modifyBtn.addActionListener(e -> handleModify());

        JButton checkOutBtn = UITheme.ghostButton("Check Out Guest");
        checkOutBtn.setPreferredSize(new Dimension(180, 40));
        checkOutBtn.addActionListener(e -> handleCheckOut());

        bottomPanel.add(checkInBtn);
        bottomPanel.add(checkOutBtn);
        bottomPanel.add(modifyBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        for (Reservation res : reservationService.getReservations()) {
            String rooms = res.getRoomNums().stream().map(Object::toString).collect(Collectors.joining(", "));
            tableModel.addRow(new Object[]{
                    res.getUsername(),
                    rooms,
                    sdf.format(res.getStartDate()),
                    sdf.format(res.getEndDate()),
                    res.isCheckedIn() ? "Checked In" : "Reserved"
            });
        }
    }

    private void handleCheckIn() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to check in.");
            return;
        }

        Reservation res = reservationService.getReservations().get(selectedRow);
        if (res.isCheckedIn()) {
            JOptionPane.showMessageDialog(this, "Guest is already checked in.");
            return;
        }

        res.setCheckedIn(true);
        reservationService.saveAllReservations();
        refreshTable();
        JOptionPane.showMessageDialog(this, "Guest checked in successfully!");
    }

    private void handleCheckOut() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to check out.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to check out this guest? This will remove the reservation.",
                "Confirm Check Out",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Reservation res = reservationService.getReservations().get(selectedRow);
            reservationService.removeReservation(res);
            reservationService.saveAllReservations();
            refreshTable();
            JOptionPane.showMessageDialog(this, "Guest checked out successfully!");
        }
    }

    private void handleModify() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to modify.");
            return;
        }

        Reservation res = reservationService.getReservations().get(selectedRow);
        
        // Custom dialog for editing
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        
        JTextField usernameField = new JTextField(res.getUsername());
        JTextField roomsField = new JTextField(res.getRoomNums().stream().map(Object::toString).collect(Collectors.joining(",")));
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        JTextField startField = new JTextField(sdf.format(res.getStartDate()));
        JTextField endField = new JTextField(sdf.format(res.getEndDate()));

        panel.add(new JLabel("Guest Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Room #s (comma separated):"));
        panel.add(roomsField);
        panel.add(new JLabel("Start Date (MM/dd/yyyy):"));
        panel.add(startField);
        panel.add(new JLabel("End Date (MM/dd/yyyy):"));
        panel.add(endField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Modify Reservation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String newUsername = usernameField.getText().trim();
                List<Integer> newRooms = Arrays.stream(roomsField.getText().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                Date newStart = sdf.parse(startField.getText().trim());
                Date newEnd = sdf.parse(endField.getText().trim());

                if (newUsername.isEmpty() || newRooms.isEmpty()) {
                    throw new Exception("Username and Rooms cannot be empty.");
                }

                // Availability check
                for (int roomId : newRooms) {
                    if (reservationService.isReservedExcluding(roomId, newStart, newEnd, res)) {
                        JOptionPane.showMessageDialog(this, "Room " + roomId + " is already reserved for these dates.");
                        return;
                    }
                }

                res.setUsername(newUsername);
                res.setRoomNums(newRooms);
                res.setStartDate(newStart);
                res.setEndDate(newEnd);

                reservationService.saveAllReservations();
                refreshTable();
                JOptionPane.showMessageDialog(this, "Reservation modified successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating reservation: " + e.getMessage());
            }
        }
    }
}
