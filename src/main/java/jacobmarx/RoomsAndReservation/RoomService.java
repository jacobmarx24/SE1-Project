package jacobmarx.RoomsAndReservation;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class RoomService {
    private final List<Room> rooms;
    private final String filename;

    public RoomService(String filename) {
        this.filename = filename;
        this.rooms = new ArrayList<>();
        loadRooms();
    }

    private void loadRooms() {
        rooms.clear();

        File file = new File(filename);
        if (!file.exists()) {
            return;
        }

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            NodeList roomNodes = document.getElementsByTagName("room");
            for (int i = 0; i < roomNodes.getLength(); i++) {
                Element roomElement = (Element) roomNodes.item(i);

                int number = Integer.parseInt(getText(roomElement, "number"));
                String roomType = getText(roomElement, "roomType");
                double price = Double.parseDouble(getText(roomElement, "price"));
                boolean smoking = Boolean.parseBoolean(getText(roomElement, "smoking"));

                rooms.add(new Room(number, roomType, price, smoking));
            }
        } catch (IOException | NumberFormatException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException("Failed to load rooms from XML: " + filename, e);
        }
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public Room getRoom(int roomNumber) {
        for (Room room : rooms) {
            if (room.getNumber() == roomNumber) {
                return room;
            }
        }
        return null;
    }

    public boolean addRoom(Room room) {
        if (getRoom(room.getNumber()) != null) {
            return false;
        }
        rooms.add(room);
        return true;
    }

    public boolean updateRoom(int originalRoomNumber, Room updatedRoom) {
        for (int i = 0; i < rooms.size(); i++) {
            Room current = rooms.get(i);

            if (current.getNumber() == originalRoomNumber) {
                if (originalRoomNumber != updatedRoom.getNumber()
                        && getRoom(updatedRoom.getNumber()) != null) {
                    return false;
                }

                rooms.set(i, updatedRoom);
                return true;
            }
        }
        return false;
    }

    public boolean removeRoom(int roomNumber) {
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getNumber() == roomNumber) {
                rooms.remove(i);
                return true;
            }
        }
        return false;
    }

    public void saveRooms() {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.newDocument();
            Element root = document.createElement("rooms");
            document.appendChild(root);

            for (Room room : rooms) {
                Element roomElement = document.createElement("room");
                root.appendChild(roomElement);

                appendTextElement(document, roomElement, "number", String.valueOf(room.getNumber()));
                appendTextElement(document, roomElement, "roomType", room.getRoomType());
                appendTextElement(document, roomElement, "price", String.format(Locale.US, "%.2f", room.getPrice()));
                appendTextElement(document, roomElement, "smoking", String.valueOf(room.isSmoking()));
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(new DOMSource(document), new StreamResult(new File(filename)));
        } catch (ParserConfigurationException | TransformerException e) {
            throw new RuntimeException("Failed to save rooms to XML: " + filename, e);
        }
    }

    private static String getText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            throw new IllegalArgumentException("Missing XML element: " + tagName);
        }
        return nodes.item(0).getTextContent().trim();
    }

    private static void appendTextElement(Document document, Element parent, String tagName, String value) {
        Element child = document.createElement(tagName);
        child.appendChild(document.createTextNode(value));
        parent.appendChild(child);
    }
}
