package jacobmarx.ReserveRoom;

import java.util.Date;

public class Reservation {
    private int roomNum;
    private Date startDate;
    private Date endDate;

    public Reservation(Date endDate, Date startDate, int roomNum) {
        this.endDate = endDate;
        this.startDate = startDate;
        this.roomNum = roomNum;
    }

    public int getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(int roomNum) {
        this.roomNum = roomNum;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
