package jacobmarx.RoomsAndReservation;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RoomService {
    private final List<Room> rooms;
    private final String filename;

    public RoomService(String filename) {
        this.filename = filename.replace(".csv", ".xml");
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
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("room");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    int number = Integer.parseInt(element.getElementsByTagName("number").item(0).getTextContent());
                    String roomType = element.getElementsByTagName("roomType").item(0).getTextContent();
                    double price = Double.parseDouble(element.getElementsByTagName("price").item(0).getTextContent());
                    boolean smoking = Boolean.parseBoolean(element.getElementsByTagName("smoking").item(0).getTextContent());

                    rooms.add(new Room(number, roomType, price, smoking));
                }
            }
        } catch (Exception e) {
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
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element rootElement = doc.createElement("rooms");
            doc.appendChild(rootElement);

            for (Room room : rooms) {
                Element roomElement = doc.createElement("room");
                rootElement.appendChild(roomElement);

                Element number = doc.createElement("number");
                number.setTextContent(String.valueOf(room.getNumber()));
                roomElement.appendChild(number);

                Element roomType = doc.createElement("roomType");
                roomType.setTextContent(room.getRoomType());
                roomElement.appendChild(roomType);

                Element price = doc.createElement("price");
                price.setTextContent(String.valueOf(room.getPrice()));
                roomElement.appendChild(price);

                Element smoking = doc.createElement("smoking");
                smoking.setTextContent(String.valueOf(room.isSmoking()));
                roomElement.appendChild(smoking);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filename));
            transformer.transform(source, result);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save rooms to XML: " + filename, e);
        }
    }
}