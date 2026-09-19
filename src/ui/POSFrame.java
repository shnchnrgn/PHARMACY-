package ui;

import db.MedicineDAO;
import models.Medicine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class POSFrame extends JFrame {

    static class CartItem {
        Medicine medicine;
        int quantity;

        public CartItem(Medicine medicine, int quantity) {
            this.medicine = medicine;
            this.quantity = quantity;
        }

        public double getTotal() {
            return medicine.getPrice() * quantity;
        }
    }

    private final List<CartItem> cart = new ArrayList<>();
    private final DefaultTableModel cartTableModel;
    private final JLabel totalLabel;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    public POSFrame() {
        setTitle("Pharmacy Management System - Point of Sale");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel posPanel = new JPanel(new BorderLayout());
        posPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel productPanel = new JPanel(new BorderLayout());
        posPanel.add(productPanel, BorderLayout.CENTER);

        String[] columns = {"ID", "Name", "Price", "Qty", "Subtotal"};
        cartTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        totalLabel = new JLabel("Total Amount: ₱0.00");

        JPanel cartPanel = createCartPanel();
        posPanel.add(cartPanel, BorderLayout.EAST);

        JScrollPane productScrollPane = new JScrollPane(createProductGridPanel());
        productScrollPane.setBorder(BorderFactory.createTitledBorder("Available Medicines"));
        productScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        productPanel.add(productScrollPane, BorderLayout.CENTER);

        mainPanel.add(posPanel, "pos");

        JPanel checkoutPanel = createCheckoutPanel();
        mainPanel.add(checkoutPanel, "checkout");

        add(mainPanel);
    }

    private JPanel createProductGridPanel() {
        JPanel productGridPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        productGridPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        List<Medicine> medicines = MedicineDAO.getAllMedicines();

        if (medicines.isEmpty()) {
            productGridPanel.add(new JLabel("No medicines found in database."));
        } else {
            for (Medicine medicine : medicines) {
                JButton productButton = new JButton(medicine.getName() + " - ₱" + String.format("%.2f", medicine.getPrice()) + " (Stock: " + medicine.getStock() + ")");
                productButton.setHorizontalAlignment(SwingConstants.CENTER);
                productButton.setVerticalAlignment(SwingConstants.CENTER);

                if (medicine.getStock() <= 0) {
                    productButton.setEnabled(false);
                }

                productButton.addActionListener(e -> addToCart(medicine));
                productGridPanel.add(productButton);
            }
        }
        return productGridPanel;
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(380, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Current Order"));

        JTable cartTable = new JTable(cartTableModel);
        cartTable.setFillsViewportHeight(true);

        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        JButton removeBtn = new JButton("Remove Item");
        JButton clearBtn = new JButton("Clear Cart");

        removeBtn.addActionListener(e -> {
            int selectedRow = cartTable.getSelectedRow();
            if (selectedRow != -1) {
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

        JButton checkoutBtn = new JButton("Checkout");
        checkoutBtn.setPreferredSize(new Dimension(0, 40));
        checkoutBtn.addActionListener(this::processCheckout);

        JPanel bottomContainer = new JPanel(new BorderLayout(5, 5));
        bottomContainer.add(actionPanel, BorderLayout.NORTH);
        bottomContainer.add(totalLabel, BorderLayout.CENTER);
        bottomContainer.add(checkoutBtn, BorderLayout.SOUTH);

        panel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        panel.add(bottomContainer, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCheckoutPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel statusLabel = new JLabel("Processing transaction...");
        panel.add(statusLabel);
        return panel;
    }

    private void addToCart(Medicine medicine) {
        for (CartItem item : cart) {
            if (item.medicine.getId() == medicine.getId()) {
                if (item.quantity < medicine.getStock()) {
                    item.quantity++;
                    updateCartUI();
                } else {
                    JOptionPane.showMessageDialog(this, "Cannot add more. Reached available stock limit!");
                }
                return;
            }
        }
        cart.add(new CartItem(medicine, 1));
        updateCartUI();
    }

    private void updateCartUI() {
        cartTableModel.setRowCount(0);
        double total = 0.0;
        for (CartItem item : cart) {
            cartTableModel.addRow(new Object[]{
                item.medicine.getId(),
                item.medicine.getName(),
                String.format("₱%.2f", item.medicine.getPrice()),
                item.quantity,
                String.format("₱%.2f", item.getTotal())
            });
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

        Timer timer = new Timer(1500, evt -> {
            cart.clear();
            updateCartUI();
            cardLayout.show(mainPanel, "pos");
            JOptionPane.showMessageDialog(this, "Checkout successful!");
        });
        timer.setRepeats(false);
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new POSFrame().setVisible(true));
    }

}