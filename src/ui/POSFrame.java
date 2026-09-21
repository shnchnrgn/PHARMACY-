package ui;

import db.DatabaseHelper;
import db.MedicineDAO;
import models.Medicine;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class POSFrame extends JFrame {
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JLabel lblTotal;
    private JPanel productGridPanel;
    private double grandTotal = 0.0;

    public POSFrame() {
        setTitle("Pharmacy Management System - Point of Sale");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Available Medicines"));
        leftPanel.setPreferredSize(new Dimension(500, 0));

        productGridPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        productGridPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        leftPanel.add(new JScrollPane(productGridPanel), BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Current Order"));

        String[] cartColumns = {"ID", "Name", "Price", "Qty", "Subtotal"};
        cartTableModel = new DefaultTableModel(cartColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartTableModel);
        rightPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel bottomCartPanel = new JPanel(new BorderLayout(5, 5));
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal = new JLabel("Total: ₱0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        totalPanel.add(lblTotal);
        bottomCartPanel.add(totalPanel, BorderLayout.NORTH);

        JPanel cartActionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnRemoveItem = new JButton("Remove Item");
        JButton btnClearCart = new JButton("Clear Cart");
        JButton btnCheckout = new JButton("Checkout");

        cartActionPanel.add(btnRemoveItem);
        cartActionPanel.add(btnClearCart);
        cartActionPanel.add(btnCheckout);
        bottomCartPanel.add(cartActionPanel, BorderLayout.SOUTH);

        rightPanel.add(bottomCartPanel, BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.CENTER);

        loadAvailableMedicines();

        btnRemoveItem.addActionListener(e -> {
            int selectedRow = cartTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an item in the cart to remove.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            cartTableModel.removeRow(selectedRow);
            computeTotal();
        });

        btnClearCart.addActionListener(e -> {
            cartTableModel.setRowCount(0);
            computeTotal();
        });

        btnCheckout.addActionListener(e -> {
            if (cartTableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "The cart is empty.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String customerName = JOptionPane.showInputDialog(this, "Enter Customer Name for this transaction:", "Customer Checkout", JOptionPane.QUESTION_MESSAGE);
            if (customerName == null || customerName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Customer name is required for checkout.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            for (int i = 0; i < cartTableModel.getRowCount(); i++) {
                int medId = (int) cartTableModel.getValueAt(i, 0);
                int qtyBought = (int) cartTableModel.getValueAt(i, 3);

                List<Medicine> allMeds = MedicineDAO.getAllMedicines();
                for (Medicine m : allMeds) {
                    if (m.getId() == medId) {
                        int newStock = m.getStock() - qtyBought;
                        m.setStock(Math.max(0, newStock));
                        MedicineDAO.updateMedicine(m);
                        break;
                    }
                }
            }

            saveOrUpdateCustomer(customerName.trim());

            Timer timer = new Timer(1000, event -> {
                cartTableModel.setRowCount(0);
                computeTotal();
                loadAvailableMedicines();
                JOptionPane.showMessageDialog(this, "Checkout successful! Inventory and Customer records updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
            });
            timer.setRepeats(false);
            timer.start();
        });
    }

    private void saveOrUpdateCustomer(String name) {
        String today = LocalDate.now().toString();
        String sql = "INSERT INTO customers (name, contact, last_purchase_date) VALUES (?, 'N/A', ?)";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, today);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadAvailableMedicines() {
        productGridPanel.removeAll();
        List<Medicine> medicines = MedicineDAO.getAllMedicines();

        for (Medicine medicine : medicines) {
            JButton productButton = new JButton("<html><center><b>" + medicine.getName() + "</b><br/>₱" + medicine.getPrice() + "<br/>Stock: " + medicine.getStock() + "</center></html>");
            productButton.setPreferredSize(new Dimension(140, 80));
            
            productButton.addActionListener(e -> {
                String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity for " + medicine.getName() + ":", "1");
                if (qtyStr == null) return;

                try {
                    int qty = Integer.parseInt(qtyStr.trim());
                    if (qty <= 0) {
                        JOptionPane.showMessageDialog(this, "Quantity must be greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (qty > medicine.getStock()) {
                        JOptionPane.showMessageDialog(this, "Insufficient stock available.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    double subtotal = medicine.getPrice() * qty;
                    boolean foundInCart = false;

                    for (int i = 0; i < cartTableModel.getRowCount(); i++) {
                        int cartId = (int) cartTableModel.getValueAt(i, 0);
                        if (cartId == medicine.getId()) {
                            int currentQty = (int) cartTableModel.getValueAt(i, 3);
                            int newQty = currentQty + qty;
                            if (newQty > medicine.getStock()) {
                                JOptionPane.showMessageDialog(this, "Total quantity exceeds available stock.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            cartTableModel.setValueAt(newQty, i, 3);
                            cartTableModel.setValueAt(newQty * medicine.getPrice(), i, 4);
                            foundInCart = true;
                            break;
                        }
                    }

                    if (!foundInCart) {
                        cartTableModel.addRow(new Object[]{
                            medicine.getId(),
                            medicine.getName(),
                            medicine.getPrice(),
                            qty,
                            subtotal
                        });
                    }

                    computeTotal();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            productGridPanel.add(productButton);
        }

        productGridPanel.revalidate();
        productGridPanel.repaint();
    }

    private void computeTotal() {
        grandTotal = 0.0;
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            grandTotal += (double) cartTableModel.getValueAt(i, 4);
        }
        lblTotal.setText(String.format("Total: ₱%.2f", grandTotal));
    }
}