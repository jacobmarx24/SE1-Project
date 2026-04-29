package jacobmarx.RoomsAndReservation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceTest {

    @TempDir
    Path tempDir;

    private String testReservationsFile;
    private ReservationService reservationService;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    @BeforeEach
    void setUp() throws IOException {
        Path file = tempDir.resolve("test_reservations.csv");
        testReservationsFile = file.toString();
        
        try (PrintWriter pw = new PrintWriter(new FileWriter(testReservationsFile))) {
            pw.println("STARTDATE, ENDDATE, USERNAME, CHECKEDIN, ROOM#s");
        }
        
        reservationService = new ReservationService(testReservationsFile);
    }
    @Test
    void testSuccessfulReservation() throws ParseException {
        // Case 1: Making a simple reservation for an available room
        String username = "testuser";
        Date start = dateFormat.parse("06/01/2026");
        Date end = dateFormat.parse("06/05/2026");
        List<Integer> roomNums = Arrays.asList(101);

        // Verify room is not reserved initially
        assertFalse(reservationService.isReserved(101, start, end), "Room should be available initially");

        // Add reservation
        reservationService.addReservation(username, start, end, roomNums);

        // Verify it's now reserved
        assertTrue(reservationService.isReserved(101, start, end), "Room should be reserved after adding reservation");
        
        List<Reservation> userReservations = reservationService.getReservationsByUsername(username);
        assertEquals(1, userReservations.size());
        assertEquals(roomNums, userReservations.get(0).getRoomNums());
    }

    @Test
    void testOverlappingReservation() throws ParseException {
        // Case 2: Detecting an overlap with an existing reservation
        Date existingStart = dateFormat.parse("06/10/2026");
        Date existingEnd = dateFormat.parse("06/15/2026");
        reservationService.addReservation("user1", existingStart, existingEnd, Arrays.asList(102));

        // Attempting to reserve same room for overlapping dates (06/12 to 06/18)
        Date newStart = dateFormat.parse("06/12/2026");
        Date newEnd = dateFormat.parse("06/18/2026");

        assertTrue(reservationService.isReserved(102, newStart, newEnd), "Should detect overlap with existing reservation");
        
        // Attempting to reserve same room for non-overlapping dates (06/16 to 06/20)
        Date nonOverlappingStart = dateFormat.parse("06/16/2026");
        Date nonOverlappingEnd = dateFormat.parse("06/20/2026");
        
        assertFalse(reservationService.isReserved(102, nonOverlappingStart, nonOverlappingEnd), "Should NOT detect overlap for separate dates");
    }

    @Test
    void testMultipleRoomsReservation() throws ParseException {
        // Case 3: Reserving multiple rooms at once
        String username = "multiuser";
        Date start = dateFormat.parse("07/01/2026");
        Date end = dateFormat.parse("07/03/2026");
        List<Integer> roomNums = Arrays.asList(201, 202, 203);

        reservationService.addReservation(username, start, end, roomNums);

        // Verify all rooms are reserved for those dates
        assertTrue(reservationService.isReserved(201, start, end), "Room 201 should be reserved");
        assertTrue(reservationService.isReserved(202, start, end), "Room 202 should be reserved");
        assertTrue(reservationService.isReserved(203, start, end), "Room 203 should be reserved");
        
        // Verify another room is NOT reserved
        assertFalse(reservationService.isReserved(204, start, end), "Room 204 should still be available");
    }
}
