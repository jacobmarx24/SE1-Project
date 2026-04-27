package jacobmarx.RoomsAndReservation;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Reservation {
    private String username;
    private List<Integer> roomNum;
    private Date startDate;
    private Date endDate;
    private boolean checkedIn;

    public Reservation(String username, Date startDate, Date endDate, List<Integer> roomNum) {
        this(username, startDate, endDate, roomNum, false);
    }

    public Reservation(String username, Date startDate, Date endDate, List<Integer> roomNum, boolean checkedIn) {
        this.username = username;
        this.endDate = endDate;
        this.startDate = startDate;
        this.roomNum = roomNum;
        this.checkedIn = checkedIn;
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return checkedIn == that.checkedIn && Objects.equals(roomNum, that.roomNum) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate) && Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomNum, startDate, endDate, username, checkedIn);
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
