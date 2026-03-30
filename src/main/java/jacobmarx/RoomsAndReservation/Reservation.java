package jacobmarx.RoomsAndReservation;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Reservation {
    private List<Integer> roomNum;
    private Date startDate;
    private Date endDate;

    public Reservation(Date startDate, Date endDate, List<Integer> roomNum) {
        this.endDate = endDate;
        this.startDate = startDate;
        this.roomNum = roomNum;
    }

    public List<Integer> getRoomNums() {
        return roomNum;
    }

    public void setRoomNums(List<Integer> roomNums) {
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return roomNum == that.roomNum && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(roomNum);
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
