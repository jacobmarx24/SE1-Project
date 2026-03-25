package jacobmarx.RoomsAndReservation;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Room {

    // Class Variables
    private int number;
    private String roomType;
    private double price;
    private boolean smoking;

    public boolean isSmoking() {
        return smoking;
    }

    public void setSmoking(boolean smoking) {
        this.smoking = smoking;
    }

    Room(int num, String rt, double p, boolean smoking){
        number = num;
        roomType = rt;
        price = p;
        this.smoking = smoking;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return number == room.number && Double.compare(price, room.price) == 0 && smoking == room.smoking && Objects.equals(roomType, room.roomType);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(number);
    }
}