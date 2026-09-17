package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;


public class POSFrame extends JFrame {

    static class Product {
        String id, name;
        double price;

        public Product(String id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }
    }

    static class CartItem {
        Product product;
        int quantity;

        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public double getTotal() {
            return product.price * quantity;
        }
    }

    private final List<CartItem> cart = new ArrayList<>();
    private final DefaultTableModel cartTableModel;
    private final JLabel totalLabel;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    public POSFrame() {
        setTitle("Pharmacy Management System - Point of Sale");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel posPanel = new JPanel(new BorderLayout());
        posPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel productPanel = new JPanel(new BorderLayout());
        posPanel.add(productPanel, BorderLayout.CENTER);

        String[] columns = {"Product ID", "Product Name", "Price", "Quantity"};
        cartTableModel = new DefaultTableModel(columns, 0);

        totalLabel = new JLabel("Total: ₱0.00");
        
        JPanel cartPanel = createCartPanel();
        posPanel.add(cartPanel, BorderLayout.EAST);

        JPanel productGridPanel = createProductGridPanel();
        productPanel.add(productGridPanel, BorderLayout.CENTER);

        mainPanel.add (posPanel, "pos");

        JPanel checkoutPanel = createCheckoutPanel();
        mainPanel.add(checkoutPanel, "checkout");

        add(mainPanel);

    };

private JPanel createProductGridPanel() {
    JPanel productGridPanel = new JPanel(new GridLayout(0, 3, 10, 10));
    productGridPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

    Product [] products = {
        new Product("P001", "Paracetamol", 50.0),
        new Product("P002", "Ibuprofen", 75.0),
        new Product("P003", "Amoxicillin", 120.0),
        new Product("P004", "Cough Syrup", 90.0),
        new Product("P005", "Vitamin C", 30.0),
        new Product("P006", "Antacid", 40.0)
    };

    for (Product product : products) {
        JButton productButton = new JButton("<html>" + product.name + "<br/>₱" + product.price + "</html>");
            productButton.addActionListener(e -> addToCart(product));
            productGridPanel.add(productButton);
        }
        return productGridPanel;
}

private JPanel createCartPanel() {

    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setPreferredSize(new Dimension(380, 0));
    panel.setBorder(BorderFactory.createTitledBorder("Current Order"));

    JTable cartTable = new JTable(cartTableModel);
    cartTable.setFillsViewportHeight(true);
    cartTable.getColumnModel().getColumn(0).setPreferredWidth(140);

    JPanel actionPanel = new JPanel(new GridLayout(1, 2, 5, 5));
    JButton removeBtn = new JButton("Remove Selected");
    JButton clearBtn = new JButton("Clear Cart");

    removeBtn.addActionListener(e -> {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow != -1){
            cart.remove(selectedRow);
            updateCartUI();
        }
    });

    clearBtn.addActionListener(e -> {
        cart.clear();
        updateCartUI();
    });

    actionPanel.add(removeBtn);
    actionPanel.add(clearBtn);

    JPanel bottomContainer = new JPanel(new BorderLayout(5, 5));   
    bottomContainer.add(actionPanel, BorderLayout.CENTER);
    bottomContainer.add(totalLabel, BorderLayout.SOUTH);

    JButton checkoutBtn = new JButton("Checkout");
    checkoutBtn.setPreferredSize(new Dimension(0, 45));
    checkoutBtn.addActionListener(this::processCheckout);

    panel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
    panel.add(bottomContainer, BorderLayout.SOUTH);

    return panel;
}

    private JPanel createCheckoutPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JLabel statusLabel = new JLabel("Processing checkout...");
        panel.add(statusLabel);
        return panel;
    }

    private void addToCart(Product product) {
        for (CartItem item : cart) {
            if (item.product.id.equals(product.id)) {
                item.quantity++;
                updateCartUI();
                return;
            }
        }
        cart.add(new CartItem(product, 1));
        updateCartUI();
    }

    private void updateCartUI() {
        cartTableModel.setRowCount(0);
        double total = 0.0;
        for (CartItem item : cart) {
            cartTableModel.addRow(new Object[]{item.product.id, item.product.name, item.product.price, item.quantity});
            total += item.getTotal();
        }
        totalLabel.setText("Total: ₱" + String.format("%.2f", total));
    }

    private void processCheckout(ActionEvent e) {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!", "Checkout Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        cardLayout.show(mainPanel, "checkout");

        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(2000); 
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            cart.clear();
            updateCartUI();
            cardLayout.show(mainPanel, "pos");
            JOptionPane.showMessageDialog(this, "Checkout successful!");
        });
    } 

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new POSFrame().setVisible(true));
    }

}
