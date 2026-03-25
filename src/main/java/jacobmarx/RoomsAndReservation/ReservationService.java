package jacobmarx.RoomsAndReservation;

import jacobmarx.RoomsAndReservation.Reservation;

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
        Scanner scanner = new Scanner(filename);
        scanner.nextLine(); //skip first line
        //FORMAT: StartDate, EndDate, ROOM#s...
        while (scanner.hasNext()){
            String[] parts = scanner.nextLine().split(",");
            //turn the start and endDate into Date objects
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy)");
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
            List<Integer> roomIds = new ArrayList<>();
            for (int i = 2; i < parts.length; i++){
                roomIds.add(Integer.parseInt(parts[i]));
            }
            addReservation(beginDate, endDate, roomIds);
        }

    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void addReservation(Date startDate, Date endDate, List<Integer> roomNums){
        reservations.add(new Reservation(startDate, endDate, roomNums));
    }

    public void addReservation(Reservation Reservation){
        reservations.add(Reservation);
    }

    public boolean isReserved(int roomId, Date d){
        for (int i = 0; i < reservations.size(); i++){
            for (int j = 0; j < reservations.get(i).getRoomNums().size(); j++){
                if (reservations.get(i).getRoomNums().get(j) == roomId){
                    return true;
                }
            }
        }
        return false;
    }
}
