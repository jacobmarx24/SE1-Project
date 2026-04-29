package jacobmarx.RoomsAndReservation;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservationService {
    private List<Reservation> reservations;
    private final String filename;

    public ReservationService(String filename) {
        this.filename = filename.replace(".csv", ".xml");
        this.reservations = new ArrayList<>();

        loadReservations();
    }

    private void loadReservations() {
        File file = new File(filename);
        if (!file.exists()) return;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("reservation");
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    Date beginDate = format.parse(element.getElementsByTagName("startDate").item(0).getTextContent());
                    Date endDate = format.parse(element.getElementsByTagName("endDate").item(0).getTextContent());
                    String username = element.getElementsByTagName("username").item(0).getTextContent();
                    boolean checkedIn = Boolean.parseBoolean(element.getElementsByTagName("checkedIn").item(0).getTextContent());

                    List<Integer> roomIds = new ArrayList<>();
                    NodeList roomNodes = element.getElementsByTagName("roomNumber");
                    for (int j = 0; j < roomNodes.getLength(); j++) {
                        roomIds.add(Integer.parseInt(roomNodes.item(j).getTextContent()));
                    }
                    addReservation(new Reservation(username, beginDate, endDate, roomIds, checkedIn));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load reservations from XML: " + filename, e);
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
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element rootElement = doc.createElement("reservations");
            doc.appendChild(rootElement);

            for (Reservation res : reservations) {
                Element resElement = doc.createElement("reservation");
                rootElement.appendChild(resElement);

                Element startDate = doc.createElement("startDate");
                startDate.setTextContent(format.format(res.getStartDate()));
                resElement.appendChild(startDate);

                Element endDate = doc.createElement("endDate");
                endDate.setTextContent(format.format(res.getEndDate()));
                resElement.appendChild(endDate);

                Element username = doc.createElement("username");
                username.setTextContent(res.getUsername());
                resElement.appendChild(username);

                Element checkedIn = doc.createElement("checkedIn");
                checkedIn.setTextContent(String.valueOf(res.isCheckedIn()));
                resElement.appendChild(checkedIn);

                Element rooms = doc.createElement("rooms");
                resElement.appendChild(rooms);

                for (Integer roomId : res.getRoomNums()) {
                    Element roomNumber = doc.createElement("roomNumber");
                    roomNumber.setTextContent(String.valueOf(roomId));
                    rooms.appendChild(roomNumber);
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filename));
            transformer.transform(source, result);
        } catch (Exception e) {
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
        addReservation(new Reservation(username, startDate, endDate, roomIds, false));
        saveAllReservations();
    }
}
