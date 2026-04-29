package jacobmarx.RoomsAndReservation;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RoomService {
    private final List<Room> rooms;
    private final String filename;

    public RoomService(String filename) {
        this.filename = filename;
        this.rooms = new ArrayList<>();
        loadRooms();
    }

    private void loadRooms() {
        rooms.clear();

        File file = new File(filename);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 4) {
                    continue;
                }

                int number = Integer.parseInt(parts[0].trim());
                String roomType = parts[1].trim();
                double price = Double.parseDouble(parts[2].trim());
                boolean smoking = Boolean.parseBoolean(parts[3].trim());

                rooms.add(new Room(number, roomType, price, smoking));
            }
        } catch (IOException | NumberFormatException e) {
            throw new RuntimeException("Failed to load rooms from CSV: " + filename, e);
        }
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public Room getRoom(int roomNumber) {
        for (Room room : rooms) {
            if (room.getNumber() == roomNumber) {
                return room;
            }
        }
        return null;
    }

    public boolean addRoom(Room room) {
        if (getRoom(room.getNumber()) != null) {
            return false;
        }
        rooms.add(room);
        return true;
    }

    public boolean updateRoom(int originalRoomNumber, Room updatedRoom) {
        for (int i = 0; i < rooms.size(); i++) {
            Room current = rooms.get(i);

            if (current.getNumber() == originalRoomNumber) {
                if (originalRoomNumber != updatedRoom.getNumber()
                        && getRoom(updatedRoom.getNumber()) != null) {
                    return false;
                }

                rooms.set(i, updatedRoom);
                return true;
            }
        }
        return false;
    }

    public boolean removeRoom(int roomNumber) {
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getNumber() == roomNumber) {
                rooms.remove(i);
                return true;
            }
        }
        return false;
    }

    public void saveRooms() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println("NUMBER, ROOMTYPE, PRICE, SMOKING");

            for (Room room : rooms) {
                pw.printf("%d, %s, %.2f, %b%n",
                        room.getNumber(),
                        room.getRoomType(),
                        room.getPrice(),
                        room.isSmoking());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save rooms to CSV: " + filename, e);
        }
    }
}