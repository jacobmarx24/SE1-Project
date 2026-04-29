package Shop;

import kennethfalato.MainMenu.MainMenuUI;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.io.*;

public class ShopUI {

    private static final Color PRIMARY = new Color(2450411);
    private static final Color PRIMARY_HOVER = new Color(1920728);
    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color CARD = Color.WHITE;
    private static final Color TEXT_DARK = new Color(35, 35, 35);
    private static final Color BORDER = new Color(220, 225, 235);

    private static JButton styledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(PRIMARY_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(PRIMARY);
            }
        });

        return button;
    }

    private static JTextField styledTextField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(130, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return field;
    }

    private static DefaultTableModel tableModel;
    private static JTextField productId;
    private static JTextField productName;
    private static JTextField minPrice;
    private static JTextField maxPrice;
    public static ShoppingCart cart = new ShoppingCart();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ShopUI::createUI);
    }

    private static List<Product> allProducts = new ArrayList<>();

    public static void createUI() {
        JFrame frame = new JFrame("Shop");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(950, 600);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(BACKGROUND);

        String[] columnNames = {"Select", "ID", "Name", "Price", "Stock"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0)
                    return Boolean.class;
                if (column == 3)
                    return Double.class;
                if (column == 4)
                    return Integer.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(230, 238, 255));
        table.setSelectionForeground(TEXT_DARK);
        table.setGridColor(new Color(235, 235, 235));
        table.setShowVerticalLines(false);

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        loadCSV("products.xml");
        updateTable(allProducts);

        JPanel mainWrapper = new JPanel(new BorderLayout(0, 20));
        mainWrapper.setBackground(BACKGROUND);
        mainWrapper.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(CARD);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(18, 22, 18, 22)));

        JLabel title = new JLabel("Browse Products");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_DARK);

        JButton viewCartButton = styledButton("View Cart");
        viewCartButton.addActionListener(e -> {
            frame.dispose();
            CartUI.createUI(cart, allProducts);
        });

        JButton toMenu = styledButton("Main Menu");
        toMenu.addActionListener(e -> {
            for (Product cartProduct : cart.getProducts()) {
                for (Product shopProduct : allProducts) {
                    if (cartProduct.getId() == shopProduct.getId()) {
                        shopProduct.setInStock(shopProduct.getInStock() + 1);
                    }
                }
            }
            cart.getProducts().clear();
            saveCSV("products.xml");
            frame.dispose();
            MainMenuUI.createUI();
        });


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(viewCartButton);
        buttonPanel.add(toMenu);
        topBar.add(new JPanel(new FlowLayout()),BorderLayout.EAST);
        topBar.add(title, BorderLayout.WEST);
        topBar.add(buttonPanel);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        searchPanel.setBackground(CARD);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        productId = styledTextField("ID");
        productName = styledTextField("Name");
        minPrice = styledTextField("Min Price");
        maxPrice = styledTextField("Max Price");

        JButton searchButton = styledButton("Search");
        searchButton.addActionListener(e -> search());

        JButton resetButton = styledButton("Reset");
        resetButton.addActionListener(e -> {
            productId.setText("ID");
            productName.setText("Name");
            minPrice.setText("Min Price");
            maxPrice.setText("Max Price");
            updateTable(allProducts);
        });

        JButton addToCart = styledButton("Add Cart");
        addToCart.addActionListener(e -> {
            boolean added = false;

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                boolean selected = (boolean) tableModel.getValueAt(i, 0);
                Integer stockNum = Integer.parseInt(tableModel.getValueAt(i, 4).toString());

                if (selected) {
                    if (stockNum == 0) {
                        JOptionPane.showMessageDialog(frame, "Item is not in stock");
                    } 
                    else {
                        int id = Integer.parseInt(tableModel.getValueAt(i, 1).toString());
                        String name = tableModel.getValueAt(i, 2).toString();
                        double price = Double.parseDouble(tableModel.getValueAt(i, 3).toString());
                        int inStock = Integer.parseInt(tableModel.getValueAt(i, 4).toString());

                        Product product = new Product(id, name, price, inStock);

                        for (Product p : allProducts) {
                            if (id == p.getId()) {
                                p.setInStock(inStock - 1);
                            }
                        }

                        tableModel.setValueAt(false, i, 0);
                        cart.addProduct(product);
                        added = true;
                    }
                }
            }

            updateTable(allProducts);

            if (added) {
                JOptionPane.showMessageDialog(frame, "Items successfully added to cart");
            }
        });

        searchPanel.add(productId);
        searchPanel.add(productName);
        searchPanel.add(minPrice);
        searchPanel.add(maxPrice);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);
        searchPanel.add(addToCart);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(CARD);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
                tableCard.add(scrollPane, BorderLayout.CENTER);

        JPanel centerContent = new JPanel(new BorderLayout(0, 18));
        centerContent.setBackground(BACKGROUND);
        centerContent.add(searchPanel, BorderLayout.NORTH);
        centerContent.add(tableCard, BorderLayout.CENTER);

        mainWrapper.add(topBar, BorderLayout.NORTH);
        mainWrapper.add(centerContent, BorderLayout.CENTER);

        frame.add(mainWrapper, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static void loadCSV(String filePath) {
        allProducts.clear();
        try {
            File file = new File(filePath);
            if (!file.exists()) return;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("product");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    int id = Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent());
                    String name = element.getElementsByTagName("name").item(0).getTextContent();
                    double price = Double.parseDouble(element.getElementsByTagName("price").item(0).getTextContent());
                    int stock = Integer.parseInt(element.getElementsByTagName("stock").item(0).getTextContent());

                    allProducts.add(new Product(id, name, price, stock));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void search() {
        String idSearch = productId.getText();
        if ("ID".equals(idSearch)) {
            idSearch = "";
        }

        String nameSearch = productName.getText();
        if ("Name".equals(nameSearch)) {
            nameSearch = "";
        }

        String minPriceText = minPrice.getText();
        if ("Min Price".equals(minPriceText)) {
            minPriceText = "";
        }

        String maxPriceText = maxPrice.getText();
        if ("Max Price".equals(maxPriceText)) {
            maxPriceText = "";
        }

        Double min = null;
        Double max = null;

        try {
            if (!minPriceText.isEmpty()) {
                if(Double.parseDouble(minPriceText)<0.0){
                    throw new NumberFormatException();
                }                
                min = Double.parseDouble(minPriceText);
            }

            if (!maxPriceText.isEmpty()) {
                if(Double.parseDouble(maxPriceText)<0.0){
                    throw new NumberFormatException();
                }
                max = Double.parseDouble(maxPriceText);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Price must be a positive number.");
            return;
        }

        List<Product> filteredProducts = new ArrayList<>();

        for (Product product : allProducts) {
            boolean matchesId = idSearch.isEmpty() || product.getId() == (Integer.parseInt(idSearch));

            boolean matchesName = nameSearch.isEmpty() || product.getName().toLowerCase().contains(nameSearch.toLowerCase());

            boolean matchesMin = min == null || product.getPrice() >= min;

            boolean matchesMax = max == null || product.getPrice() <= max;

            if (matchesId && matchesName && matchesMin && matchesMax) {
                filteredProducts.add(product);
            }
        }

        updateTable(filteredProducts);
        if(filteredProducts.size() == 0){
            JOptionPane.showMessageDialog(null, "No items found");
        }
    }

    private static void updateTable(List<Product> products) {
        tableModel.setRowCount(0);

        for (Product product : products) {
            tableModel.addRow(new Object[] {false, product.getId(),product.getName(),product.getPrice(),
                    product.getInStock()});
        }
        saveCSV("products.xml");
    }

    private static void saveCSV(String filePath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element rootElement = doc.createElement("products");
            doc.appendChild(rootElement);

            for (Product p : allProducts) {
                Element productElement = doc.createElement("product");
                rootElement.appendChild(productElement);

                Element id = doc.createElement("id");
                id.setTextContent(String.valueOf(p.getId()));
                productElement.appendChild(id);

                Element name = doc.createElement("name");
                name.setTextContent(p.getName());
                productElement.appendChild(name);

                Element price = doc.createElement("price");
                price.setTextContent(String.valueOf(p.getPrice()));
                productElement.appendChild(price);

                Element stock = doc.createElement("stock");
                stock.setTextContent(String.valueOf(p.getInStock()));
                productElement.appendChild(stock);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "XML save failed.");
        }
    }

}
