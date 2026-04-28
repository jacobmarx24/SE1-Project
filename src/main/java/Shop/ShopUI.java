package Shop;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.ArrayList;




public class ShopUI {


  private static DefaultTableModel tableModel;
    private static JTextField productId;
    private static JTextField productName;
    private static JTextField productPrice;
    private static JTextField productAvailability;
    private static JTextField minPrice;
    private static JTextField maxPrice;
    private static JComboBox<String> availabilityBox;
    public static ShoppingCart cart = new ShoppingCart();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ShopUI::createUI);
    }



    private static List<Product> allProducts = new ArrayList<>();

    public static void createUI() {
        JFrame frame = new JFrame("Product");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        String[] columnNames = {
                "Select", "ID", "Name", "Price", "Availability"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0 || column == 4) return Boolean.class;
                if (column == 3) return Double.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        loadCSV("products.csv");
        updateTable(allProducts);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));

        productId = new JTextField();
        productId.setText("ID");

        productName = new JTextField();
        productName.setText("Name");

        minPrice = new JTextField();
        minPrice.setText("Min Price");

        maxPrice = new JTextField();
        maxPrice.setText("Max Price");

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            productId.setText("ID");
            productName.setText("Name");
            minPrice.setText("Min Price");
            maxPrice.setText("Max Price");
            availabilityBox.setSelectedIndex(0);
            updateTable(allProducts);
        });

        JButton addToCart = new JButton("Add");
            addToCart.addActionListener(e -> {
        
                int addedCount = 0;
                for (int i = 0; i < tableModel.getRowCount(); i++) {
        Boolean selected = (Boolean) tableModel.getValueAt(i, 0);

        if (selected != null && selected) {
            int id = Integer.parseInt(tableModel.getValueAt(i, 1).toString());
            String name = tableModel.getValueAt(i, 2).toString();
            double price = Double.parseDouble(tableModel.getValueAt(i, 3).toString());
            boolean available = Boolean.parseBoolean(tableModel.getValueAt(i, 4).toString());

            Product product = new Product(id, name, price, available);


            tableModel.setValueAt(false, i, 0);
            addedCount++;
            cart.addProduct(product);
        }
    }
        });

        bottomPanel.add(productId);
        bottomPanel.add(productName);
        bottomPanel.add(minPrice);
        bottomPanel.add(maxPrice);
        bottomPanel.add(searchButton);
        bottomPanel.add(resetButton);
        bottomPanel.add(addToCart);

        JButton viewCartButton = new JButton("View Cart");

        viewCartButton.addActionListener(e -> {
            frame.dispose();
            CartUI.createUI(cart);
        });
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(viewCartButton);

        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.add(topPanel,BorderLayout.NORTH);

        frame.setVisible(true);


    }

    private static void loadCSV(String filePath) {
        allProducts.clear();

        try (
            BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] data = line.split(",");

                int id = Integer.parseInt(data[0]);
                String name = data[1];
                double price = Double.parseDouble(data[2]);
                boolean availability = Boolean.parseBoolean(data[3]);

                allProducts.add(new Product(id, name, price, availability));
            }

        } catch (IOException e) {
            e.printStackTrace();
    }
}

    private static void performSearch() {
        String idSearch = productId.getText();
        if ("ID".equals(idSearch)) idSearch = "";

        String nameSearch = productName.getText();
        if ("Name".equals(nameSearch)) nameSearch = "";

        String minPriceText = minPrice.getText();
        if ("Min Price".equals(minPriceText)) minPriceText = "";

        String maxPriceText = maxPrice.getText();
        if ("Max Price".equals(maxPriceText)) maxPriceText = "";

        Double min = null;
        Double max = null;

        try {
            if (!minPriceText.isEmpty()) {
                min = Double.parseDouble(minPriceText);
            }

            if (!maxPriceText.isEmpty()) {
                max = Double.parseDouble(maxPriceText);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Price must be a number.");
            return;
        }

        String availabilityChoice = availabilityBox.getSelectedItem().toString();

        List<Product> filteredProducts = new ArrayList<>();

        for (Product product : allProducts) {
            boolean matchesId =
                    idSearch.isEmpty() ||
                    String.valueOf(product.getId()).equals(idSearch);

            boolean matchesName =
                    nameSearch.isEmpty() ||
                    product.getName().toLowerCase().contains(nameSearch.toLowerCase());

            boolean matchesMin =
                    min == null ||
                    product.getPrice() >= min;

            boolean matchesMax =
                    max == null ||
                    product.getPrice() <= max;

            boolean matchesAvailability =
                    availabilityChoice.equals("Any Availability") ||
                    availabilityChoice.equals("Available") && product.isAvailable() ||
                    availabilityChoice.equals("Unavailable") && !product.isAvailable();

            if (matchesId && matchesName && matchesMin && matchesMax && matchesAvailability) {
                filteredProducts.add(product);
            }
        }

        updateTable(filteredProducts);
    }

    private static void updateTable(List<Product> products) {
        tableModel.setRowCount(0);

        for (Product product : products) {
            tableModel.addRow(new Object[]{
                    false,
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.isAvailable()
            });
        }
    }


}


