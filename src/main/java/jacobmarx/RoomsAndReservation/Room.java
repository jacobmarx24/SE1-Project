package jacobmarx.RoomsAndReservation;

import java.util.Objects;

public class Room {
    private int number;
    private String roomType;
    private double price;
    private boolean smoking;

    public Room(int num, String rt, double p, boolean smoking) {
        number = num;
        roomType = rt;
        price = p;
        this.smoking = smoking;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isSmoking() {
        return smoking;
    }

    public void setSmoking(boolean smoking) {
        this.smoking = smoking;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        Room room = (Room) o;
        return number == room.number &&
                Double.compare(room.price, price) == 0 &&
                smoking == room.smoking &&
                Objects.equals(roomType, room.roomType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}