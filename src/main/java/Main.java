import jacobmarx.ReserveRoom.RoomSelectionUI;
import jacobmarx.RoomsAndReservation.ReservationService;
import jacobmarx.RoomsAndReservation.RoomService;
import javax.swing.*;

public class Main {
    public static void main(){
        //Create the ReservationService and RoomService
        //TODO: UI controller class
        //line below decides the opening UI. currently set to RoomSelection, change to main menu at some point
        SwingUtilities.invokeLater(RoomSelectionUI::createUI);
    }
}
