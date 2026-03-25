package jacobmarx.ReserveRoom;

public class Room {
    enum RoomType {
        Executive,
        Business,
        Comfort,
        Economy
    }

    // Class Variables
    private int number;
    private RoomType roomType;
    private double price;
    // TODO: a list of reservations

    Room(int num, RoomType rt, double p){
        number = num;
        roomType = rt;
        price = p;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}