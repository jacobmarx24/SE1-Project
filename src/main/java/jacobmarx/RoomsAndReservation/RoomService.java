package jacobmarx.RoomsAndReservation;

import java.util.*;

public class RoomService {
    private List<Room> rooms;

    public RoomService(String filename){
        rooms = new ArrayList<>();

        //Read in the csv to fill rooms
        Scanner scanner = new Scanner(filename);
        scanner.nextLine(); //skip first line
        //FORMAT: NUMBER, ROOMTYPE, PRICE, SMOKING
        while (scanner.hasNext()){
            String[] parts = scanner.nextLine().split(",");
            addRoom(Integer.parseInt(parts[0]), parts[1], Double.parseDouble(parts[2]), Boolean.parseBoolean(parts[3]));
        }
    }

    public Room getRoom(int roomNumber){
        for (int i = 0; i < rooms.size(); i++){
            if (rooms.get(i).getNumber() == roomNumber){
                return rooms.get(i);
            }
        }
        return null;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void addRoom(int roomID, String roomType, double price, boolean smoking){
        rooms.add(new Room(roomID, roomType, price, smoking));
    }

    public void addRoom(Room room){
        rooms.add(room);
    }

    public void removeRoom(int roomID){
        for (int i = 0; i < rooms.size(); i++){
            if (rooms.get(i).getNumber() == roomID){
                rooms.remove(i);
                i--;
            }
        }
    }

    public void removeRoom(Room room){
        for (int i = 0; i < rooms.size(); i++){
            if (rooms.get(i).equals(room)){
                rooms.remove(i);
                i--;
            }
        }
    }
}
