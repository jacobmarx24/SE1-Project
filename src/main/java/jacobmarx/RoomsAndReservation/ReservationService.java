package jacobmarx.RoomsAndReservation;

import jacobmarx.RoomsAndReservation.Reservation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class ReservationService {
    private List<Reservation> reservations;

    public ReservationService(String filename){
        reservations = new ArrayList<>();

        //Read in the csv to fill reservations
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        scanner.nextLine(); //skip first line
        //FORMAT: STARTDATE, ENDDATE, USERNAME, ROOM#s...
        while (scanner.hasNext()){
            String[] parts = scanner.nextLine().split(",");
            //turn the start and endDate into Date objects
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            Date beginDate = new Date();
            Date endDate = new Date();
            try {
                beginDate = format.parse(parts[0]);
                endDate = format.parse(parts[1]);
            } catch (ParseException e){
                //csv was formatted incorrectly if this runs
                e.printStackTrace();
                System.exit(0);
            }
            String username = parts[2];
            List<Integer> roomIds = new ArrayList<>();
            for (int i = 3; i < parts.length; i++){
                roomIds.add(Integer.parseInt(parts[i].trim()));
            }
            addReservation(username, beginDate, endDate, roomIds);
        }

    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void addReservation(String username, Date startDate, Date endDate, List<Integer> roomNums){
        reservations.add(new Reservation(username, startDate, endDate, roomNums));
    }

    public void addReservation(Reservation Reservation){
        reservations.add(Reservation);
    }

    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
    }

    public List<Reservation> getReservationsByUsername(String username) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation res : reservations) {
            if (res.getUsername().equals(username)) {
                result.add(res);
            }
        }
        return result;
    }

    public boolean isReserved(int roomId, Date startDate, Date endDate) {
        for (Reservation res : reservations) {
            if (res.getRoomNums().contains(roomId)) {
                // Check if the requested range overlaps with the reservation range
                // Two ranges [s1, e1] and [s2, e2] overlap if s1 <= e2 AND e1 >= s2
                if (startDate.compareTo(res.getEndDate()) <= 0 && endDate.compareTo(res.getStartDate()) >= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isReserved(int roomId, Date d){
        for (Reservation res : reservations) {
            if (res.getRoomNums().contains(roomId)) {
                if (!d.before(res.getStartDate()) && !d.after(res.getEndDate())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void saveAllReservations() {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        try (PrintWriter pw = new PrintWriter(new FileWriter("reservations.csv"))) {
            pw.println("STARTDATE, ENDDATE, USERNAME, ROOM#s");
            for (Reservation res : reservations) {
                StringBuilder sb = new StringBuilder();
                sb.append(format.format(res.getStartDate())).append(",")
                  .append(format.format(res.getEndDate())).append(",")
                  .append(res.getUsername());
                for (Integer roomId : res.getRoomNums()) {
                    sb.append(",").append(roomId);
                }
                pw.println(sb.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isReservedExcluding(int roomId, Date startDate, Date endDate, Reservation excluding) {
        for (Reservation res : reservations) {
            if (res.equals(excluding)) continue;
            if (res.getRoomNums().contains(roomId)) {
                if (startDate.compareTo(res.getEndDate()) <= 0 && endDate.compareTo(res.getStartDate()) >= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateReservationsCSV(String username, Date startDate, Date endDate, List<Integer> roomIds) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        String startDateStr = format.format(startDate);
        String endDateStr = format.format(endDate);

        StringBuilder sb = new StringBuilder();
        sb.append(startDateStr).append(",").append(endDateStr).append(",").append(username);
        for (Integer roomId : roomIds) {
            sb.append(",").append(roomId);
        }

        try (FileWriter fw = new FileWriter("reservations.csv", true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
