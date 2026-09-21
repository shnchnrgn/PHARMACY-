package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class POSFrame extends JPanel {

    private JTable medicineTable;
    private JTable cartTable;
    private DefaultTableModel medicineModel;
    private DefaultTableModel cartModel;
    private JTextField searchField;
    private JLabel lblSubtotalVal;

    public POSFrame() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Point of Sales (POS)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(40, 40, 40));
        add(lblTitle, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230)),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setOpaque(false);
        searchField = new JTextField();
        JButton btnSearch = new JButton("Search");
        searchPanel.add(new JLabel("Search Medicine: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);
        leftPanel.add(searchPanel, BorderLayout.NORTH);

        String[] medColumns = {"ID", "Name", "Price", "Stock"};
        Object[][] medData = {
            {"M001", "Biogesic", "5.00", "150"},
            {"M002", "Neozep", "7.50", "80"},
            {"M003", "Alaxan FR", "10.00", "45"},
            {"M004", "Enat 400", "18.00", "60"}
        };
        medicineModel = new DefaultTableModel(medData, medColumns);
        medicineTable = new JTable(medicineModel);
        JScrollPane medScroll = new JScrollPane(medicineTable);
        leftPanel.add(medScroll, BorderLayout.CENTER);

        JButton btnAddToCart = new JButton("Add to Cart");
        btnAddToCart.setBackground(new Color(41, 128, 185));
        btnAddToCart.setForeground(Color.WHITE);
        btnAddToCart.setFocusPainted(false);
        leftPanel.add(btnAddToCart, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230)),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblCartTitle = new JLabel("Current Cart & Checkout");
        lblCartTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCartTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        rightPanel.add(lblCartTitle, BorderLayout.NORTH);

        String[] cartColumns = {"Item Name", "Price", "Qty", "Total"};
        cartModel = new DefaultTableModel(new Object[][]{}, cartColumns);
        cartTable = new JTable(cartModel);
        JScrollPane cartScroll = new JScrollPane(cartTable);
        rightPanel.add(cartScroll, BorderLayout.CENTER);

        JPanel bottomCartPanel = new JPanel(new BorderLayout());
        bottomCartPanel.setOpaque(false);
        bottomCartPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel subtotalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        subtotalPanel.setOpaque(false);
        JLabel lblSubtotalText = new JLabel("Subtotal: ");
        lblSubtotalText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSubtotalVal = new JLabel("₱ 0.00");
        lblSubtotalVal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSubtotalVal.setForeground(new Color(39, 174, 96));
        subtotalPanel.add(lblSubtotalText);
        subtotalPanel.add(lblSubtotalVal);
        bottomCartPanel.add(subtotalPanel, BorderLayout.NORTH);

        JPanel actionButtons = new JPanel(new GridLayout(1, 2, 10, 0));
        actionButtons.setOpaque(false);
        JButton btnRemove = new JButton("Remove Item");
        btnRemove.setBackground(new Color(231, 76, 60));
        btnRemove.setForeground(Color.WHITE);
        btnRemove.setFocusPainted(false);

        JButton btnCheckout = new JButton("Checkout");
        btnCheckout.setBackground(new Color(39, 174, 96));
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setFocusPainted(false);

        actionButtons.add(btnRemove);
        actionButtons.add(btnCheckout);
        bottomCartPanel.add(actionButtons, BorderLayout.SOUTH);

        rightPanel.add(bottomCartPanel, BorderLayout.SOUTH);

        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);
        add(contentPanel, BorderLayout.CENTER);

        btnAddToCart.addActionListener(e -> {
            int selectedRow = medicineTable.getSelectedRow();
            if (selectedRow != -1) {
                String name = (String) medicineModel.getValueAt(selectedRow, 1);
                String priceStr = (String) medicineModel.getValueAt(selectedRow, 2);
                cartModel.addRow(new Object[]{name, priceStr, "1", priceStr});
                updateSubtotal();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a medicine from the list first.");
            }
        });

        btnRemove.addActionListener(e -> {
            int selectedCartRow = cartTable.getSelectedRow();
            if (selectedCartRow != -1) {
                cartModel.removeRow(selectedCartRow);
                updateSubtotal();
            } else {
                JOptionPane.showMessageDialog(this, "Please select an item from the cart to remove.");
            }
        });

        btnCheckout.addActionListener(e -> {
            if (cartModel.getRowCount() > 0) {
                String customerName = JOptionPane.showInputDialog(this, "Enter Customer Name:", "Customer Details", JOptionPane.QUESTION_MESSAGE);
                if (customerName != null && !customerName.trim().isEmpty()) {
                    double totalAmount = calculateTotal();
                    String orderNo = "ORD-" + (System.currentTimeMillis() % 10000);
                    String currentDate = LocalDate.now().toString();

                    // I-save sa Shared Data para makita sa Dashboard
                    SharedData.addSale(orderNo, currentDate, String.format("%.2f", totalAmount), customerName.trim());

                    JOptionPane.showMessageDialog(this, "Checkout successful! Transferred to Dashboard.");
                    cartModel.setRowCount(0);
                    updateSubtotal();
                } else {
                    JOptionPane.showMessageDialog(this, "Checkout cancelled. Customer name is required.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "The cart is empty.");
            }
        });
    }

    private double calculateTotal() {
        double total = 0;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            total += Double.parseDouble((String) cartModel.getValueAt(i, 3));
        }
        return total;
    }

    private void updateSubtotal() {
        lblSubtotalVal.setText("₱ " + String.format("%.2f", calculateTotal()));
    }
}