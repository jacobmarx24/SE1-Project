package Shop;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CartUI {

    private static DefaultTableModel cartTableModel;

    public static void createUI(ShoppingCart cart) {
        JFrame frame = new JFrame("Shopping Cart");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 400);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton backButton = new JButton("<-- Back to Products");
        backButton.addActionListener(e -> {
            frame.dispose();
            ShopUI.createUI();
        });

        JButton remove = new JButton("Remove Item");
        remove.addActionListener(e->{
            for (int i = 0; i < cartTableModel.getRowCount(); i++) {
                Boolean selected = (Boolean) cartTableModel.getValueAt(i, 0);
                if (selected != null && selected) {
                    cartTableModel.setValueAt(false, i, 0);
                    cart.deleteProduct(i);
                }
                updateTable(cart.getProducts());
        }    
        });

        JButton getTotal = new JButton("Get Total");
        getTotal.addActionListener(e->{
            double total = cart.calculateTotal();
            JOptionPane.showMessageDialog(frame, "Your total is: $" + Double.toString(total));
        });

        topPanel.add(backButton);
         JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(remove);
        bottomPanel.add(getTotal);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(bottomPanel,BorderLayout.SOUTH);

        String[] columns = {"Select", "ID", "Name", "Price", "Availability"};
               
        cartTableModel = new DefaultTableModel(columns, 0) {
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





        JTable cartTable = new JTable(cartTableModel);
        cartTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(cartTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        loadCart(cart);

        frame.setVisible(true);
    }

    private static void loadCart(ShoppingCart cart) {
        cartTableModel.setRowCount(0);

        for (Product product : cart.getProducts()) {
            cartTableModel.addRow(new Object[]{
                    false,
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.isAvailable()
            });
        }
    }
        private static void updateTable(List<Product> products) {
        cartTableModel.setRowCount(0);

        for (Product product : products) {
            cartTableModel.addRow(new Object[]{
                    false,
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.isAvailable()
            });
        }
    }
}
