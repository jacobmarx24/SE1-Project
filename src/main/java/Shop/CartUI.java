package Shop;

import kennethfalato.MainMenu.MainMenuUI;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CartUI {

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

    private static DefaultTableModel cartTableModel;

    public static void createUI(ShoppingCart cart, List<Product> allProducts) {
        JFrame frame = new JFrame("Shopping Cart");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(950, 600);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(BACKGROUND);

        JPanel mainWrapper = new JPanel(new BorderLayout(0, 20));
        mainWrapper.setBackground(BACKGROUND);
        mainWrapper.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(CARD);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(18, 22, 18, 22)));

        JLabel title = new JLabel("Shopping Cart");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_DARK);

        JButton backButton = styledButton("← Back to Products");
        backButton.addActionListener(e -> {
            frame.dispose();
            ShopUI.createUI();
        });

        JButton toMenu = styledButton("Main Menu");
        toMenu.addActionListener(e -> {
            for (Product p : cart.getProducts()) {
                for (Product shopP : allProducts) {
                    if (p.getId() == shopP.getId()) {
                        shopP.setInStock(shopP.getInStock() + 1);
                    }
                }
            }
            cart.getProducts().clear();
            saveCSV("products.csv", allProducts);
            frame.dispose();
            MainMenuUI.createUI();
        });

        JPanel topBarButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topBarButtons.setBackground(CARD);
        topBarButtons.add(backButton);
        topBarButtons.add(toMenu);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(topBarButtons, BorderLayout.EAST);

        String[] columns = { "Select", "ID", "Name", "Price" };

        cartTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0)
                    return Boolean.class;
                if (column == 1)
                    return Integer.class;
                if (column == 3)
                    return Double.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        JTable cartTable = new JTable(cartTableModel);
        cartTable.setRowHeight(34);
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cartTable.setSelectionBackground(new Color(230, 238, 255));
        cartTable.setSelectionForeground(TEXT_DARK);
        cartTable.setGridColor(new Color(235, 235, 235));
        cartTable.setShowVerticalLines(false);

        cartTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        cartTable.getTableHeader().setBackground(PRIMARY);
        cartTable.getTableHeader().setForeground(Color.WHITE);
        cartTable.getTableHeader().setPreferredSize(new Dimension(0, 38));

        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(CARD);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        JButton remove = styledButton("Remove Selected");
        JButton emptyCart = styledButton("Empty Cart");
        emptyCart.addActionListener(e -> {
            for (Product p : cart.getProducts()) {
                for (Product shopP : allProducts) {
                    if (p.getId() == shopP.getId()) {
                        shopP.setInStock(shopP.getInStock() + 1);
                    }
                }
            }
            cart.getProducts().clear();
            saveCSV("products.csv", allProducts);
            updateTable(cart.getProducts());
            JOptionPane.showMessageDialog(frame, "Cart emptied and stock restored");
        });

        remove.addActionListener(e -> {

            for (int i = cartTableModel.getRowCount() - 1; i >= 0; i--) {
                boolean selected = (boolean) cartTableModel.getValueAt(i, 0);
                int id = Integer.parseInt(cartTableModel.getValueAt(i, 1).toString());

                if (selected) {
                    for (Product p : allProducts) {
                        if (id == p.getId()) {
                            p.setInStock(p.getInStock() + 1);
                        }
                    }

                    cart.deleteProduct(i);

                }
            }

            saveCSV("products.csv", allProducts);
            updateTable(cart.getProducts());

            JOptionPane.showMessageDialog(frame, "Item(s) successfully removed from cart");
        });

        JButton getTotal = styledButton("Get Total");
        getTotal.addActionListener(e -> {
            double total = cart.calculateTotal();
            JOptionPane.showMessageDialog(frame, String.format("Your total is: $%.2f", total));
        });

        JButton purchase = styledButton("Buy Items");
        purchase.addActionListener(e -> {
            double total = cart.calculateTotal();
            if (total <= 0) {
                JOptionPane.showMessageDialog(frame, "Your cart is empty!");
                return;
            }
            int choice = JOptionPane.showConfirmDialog(frame, 
                String.format("Purchase items for $%.2f and add to your bill?", total),
                "Confirm Purchase", JOptionPane.YES_NO_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                Elias.files.BillService.addToBill(jacobmarx.ReserveRoom.RoomSelectionUI.currentUsername, total);
                cart.getProducts().clear();
                saveCSV("products.csv", allProducts);
                updateTable(cart.getProducts());
                JOptionPane.showMessageDialog(frame, "Items purchased! The cost has been added to your bill.");
            }
        });

        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        actionBar.setBackground(CARD);
        actionBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        actionBar.add(remove);
        actionBar.add(emptyCart);
        actionBar.add(getTotal);
        actionBar.add(purchase);

        JPanel centerContent = new JPanel(new BorderLayout(0, 18));
        centerContent.setBackground(BACKGROUND);
        centerContent.add(tableCard, BorderLayout.CENTER);
        centerContent.add(actionBar, BorderLayout.SOUTH);

        mainWrapper.add(topBar, BorderLayout.NORTH);
        mainWrapper.add(centerContent, BorderLayout.CENTER);

        frame.add(mainWrapper, BorderLayout.CENTER);

        loadCart(cart);

        frame.setVisible(true);
    }

    private static void loadCart(ShoppingCart cart) {
        cartTableModel.setRowCount(0);

        for (Product product : cart.getProducts()) {
            cartTableModel.addRow(new Object[] {
                    false,
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
            });
        }
    }

    private static void updateTable(List<Product> products) {
        cartTableModel.setRowCount(0);

        for (Product product : products) {
            cartTableModel.addRow(new Object[] { false, product.getId(), product.getName(),
                    product.getPrice(), });
        }
    }

    private static void saveCSV(String filePath, List<Product> allProducts) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("id,name,price,stock");

            for (Product product : allProducts) {
                writer.println(
                        product.getId() + "," + product.getName() + "," + product.getPrice() + "," + product.getInStock());
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not save products to CSV.");
        }
    }

}
