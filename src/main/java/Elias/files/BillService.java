package Elias.files;

import jacobmarx.RoomsAndReservation.Reservation;
import jacobmarx.RoomsAndReservation.ReservationService;
import jacobmarx.RoomsAndReservation.Room;
import jacobmarx.RoomsAndReservation.RoomService;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BillService {

    public static double getGuestBill(String username) {
        return DatabaseHelper.getGuestBill(username);
    }

    public static void payBill(String username) {
        DatabaseHelper.clearGuestBill(username);
    }

    public static void addToBill(String username, double amount) {
        DatabaseHelper.updateGuestBill(username, amount);
    }

    public static void applyCancellationPenalty(String username) {
        addToBill(username, 10.0);
    }

    public static void processRoomCheckout(String username, Reservation reservation, RoomService roomService) {
        double roomTotal = 0;
        for (int roomNum : reservation.getRoomNums()) {
            Room room = roomService.getRoom(roomNum);
            if (room != null) {
                roomTotal += calculateRoomCost(reservation, room.getPrice());
            }
        }
        
        // Add penalty if late checkout
        double penalty = calculateLatePenalty(reservation);
        
        addToBill(username, roomTotal + penalty);
    }

    private static double calculateRoomCost(Reservation res, double pricePerNight) {
        long diffInMillies = Math.abs(res.getEndDate().getTime() - res.getStartDate().getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        if (diff == 0) diff = 1; // Minimum 1 night
        return diff * pricePerNight;
    }

    private static double calculateLatePenalty(Reservation res) {
        // Simple logic: if today is after end date, add a flat penalty of $50
        Date today = new Date();
        if (today.after(res.getEndDate())) {
            // Check if it's actually a different day
            long diffInMillies = today.getTime() - res.getEndDate().getTime();
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            if (diff > 0) {
                return 50.0; // Flat penalty
            }
        }
        return 0.0;
    }
}
