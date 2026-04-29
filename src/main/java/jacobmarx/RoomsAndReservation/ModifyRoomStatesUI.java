package jacobmarx.RoomsAndReservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ModifyRoomStatesUI {
    private final RoomService roomService;
    private final JFrame frame;
    private final JTable table;
    private final DefaultTableModel model;

    private final JTextField roomNumberField;
    private final JTextField roomTypeField;
    private final JTextField priceField;
    private final JCheckBox smokingBox;

    private int selectedOriginalRoomNumber = -1;
    private String[] clerkData;

    public ModifyRoomStatesUI(String csvFileName) {
        this(csvFileName, null);
    }

    public ModifyRoomStatesUI(String csvFileName, String[] clerkData) {
        this.clerkData = clerkData;
        roomService = new RoomService(csvFileName);

        frame = new JFrame("Modify Room States");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(850, 500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        String[] columnNames = {"Room Number", "Room Type", "Price", "Smoking"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        refreshTable();

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        roomNumberField = new JTextField();
        roomTypeField = new JTextField();
        priceField = new JTextField();
        smokingBox = new JCheckBox("Smoking Allowed");

        formPanel.add(new JLabel("Room Number:"));
        formPanel.add(roomNumberField);
        formPanel.add(new JLabel("Room Type:"));
        formPanel.add(roomTypeField);
        formPanel.add(new JLabel("Price:"));
        formPanel.add(priceField);
        formPanel.add(new JLabel("Smoking:"));
        formPanel.add(smokingBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton loadButton = new JButton("Load Selected");
        JButton addButton = new JButton("Add Room");
        JButton updateButton = new JButton("Update Room");
        JButton deleteButton = new JButton("Delete Room");
        JButton clearButton = new JButton("Clear");
        JButton saveButton = new JButton("Save to CSV");
        JButton backButton = new JButton("Back");

        buttonPanel.add(loadButton);
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(southPanel, BorderLayout.SOUTH);

        loadButton.addActionListener(e -> loadSelectedRoomIntoForm());
        addButton.addActionListener(e -> addRoom());
        updateButton.addActionListener(e -> updateRoom());
        deleteButton.addActionListener(e -> deleteRoom());
        clearButton.addActionListener(e -> clearForm());
        saveButton.addActionListener(e -> saveRooms());
        backButton.addActionListener(e -> {
            frame.dispose();
            kennethfalato.MainMenu.ClerkMenu.createUI(clerkData);
        });

        frame.setVisible(true);
    }

    public static void openWindow(String csvFileName) {
        SwingUtilities.invokeLater(() -> new ModifyRoomStatesUI(csvFileName));
    }

    private void refreshTable() {
        model.setRowCount(0);

        for (Room room : roomService.getRooms()) {
            model.addRow(new Object[]{
                    room.getNumber(),
                    room.getRoomType(),
                    room.getPrice(),
                    room.isSmoking()
            });
        }
    }

    private void loadSelectedRoomIntoForm() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a room from the table.");
            return;
        }

        selectedOriginalRoomNumber = Integer.parseInt(model.getValueAt(row, 0).toString());
        roomNumberField.setText(model.getValueAt(row, 0).toString());
        roomTypeField.setText(model.getValueAt(row, 1).toString());
        priceField.setText(model.getValueAt(row, 2).toString());
        smokingBox.setSelected(Boolean.parseBoolean(model.getValueAt(row, 3).toString()));
    }

    private void addRoom() {
        try {
            Room room = buildRoomFromForm();

            if (!roomService.addRoom(room)) {
                JOptionPane.showMessageDialog(frame, "Room number already exists.");
                return;
            }

            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(frame, "Room added successfully.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage());
        }
    }

    private void updateRoom() {
        if (selectedOriginalRoomNumber == -1) {
            JOptionPane.showMessageDialog(frame, "Load a room first before updating.");
            return;
        }

        try {
            Room updatedRoom = buildRoomFromForm();

            if (!roomService.updateRoom(selectedOriginalRoomNumber, updatedRoom)) {
                JOptionPane.showMessageDialog(frame, "Update failed. Room number may already exist.");
                return;
            }

            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(frame, "Room updated successfully.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage());
        }
    }

    private void deleteRoom() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a room to delete.");
            return;
        }

        int roomNumber = Integer.parseInt(model.getValueAt(row, 0).toString());

        int choice = JOptionPane.showConfirmDialog(
                frame,
                "Delete room " + roomNumber + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            if (roomService.removeRoom(roomNumber)) {
                refreshTable();
                clearForm();
                JOptionPane.showMessageDialog(frame, "Room deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(frame, "Delete failed.");
            }
        }
    }

    private void saveRooms() {
        roomService.saveRooms();
        JOptionPane.showMessageDialog(frame, "Changes saved to rooms.xml.");
    }

    private Room buildRoomFromForm() {
        String roomNumberText = roomNumberField.getText().trim();
        String roomType = roomTypeField.getText().trim();
        String priceText = priceField.getText().trim();
        boolean smoking = smokingBox.isSelected();

        if (roomNumberText.isEmpty() || roomType.isEmpty() || priceText.isEmpty()) {
            throw new IllegalArgumentException("All fields except smoking must be filled in.");
        }

        int roomNumber;
        double price;

        try {
            roomNumber = Integer.parseInt(roomNumberText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Room number must be an integer.");
        }

        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Price must be a number.");
        }

        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }

        return new Room(roomNumber, roomType, price, smoking);
    }

    private void clearForm() {
        roomNumberField.setText("");
        roomTypeField.setText("");
        priceField.setText("");
        smokingBox.setSelected(false);
        selectedOriginalRoomNumber = -1;
        table.clearSelection();
    }
}