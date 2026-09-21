package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class MedicinePanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch, txtId, txtName, txtCategory, txtPrice, txtStock, txtExpiry;

    public MedicinePanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Medicine Inventory Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(40, 40, 40));
        add(lblTitle, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(15, 0));
        mainContent.setOpaque(false);

        JPanel leftContainer = new JPanel(new BorderLayout(0, 10));
        leftContainer.setBackground(Color.WHITE);
        leftContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        leftContainer.setPreferredSize(new Dimension(340, 0));

        JPanel leftForm = new JPanel(new GridLayout(6, 2, 5, 12));
        leftForm.setOpaque(false);

        txtId = new JTextField();
        txtName = new JTextField();
        txtCategory = new JTextField();
        txtPrice = new JTextField();
        txtStock = new JTextField();
        txtExpiry = new JTextField();

        leftForm.add(new JLabel("Medicine ID:"));
        leftForm.add(txtId);
        leftForm.add(new JLabel("Brand Name:"));
        leftForm.add(txtName);
        leftForm.add(new JLabel("Category:"));
        leftForm.add(txtCategory);
        leftForm.add(new JLabel("Price:"));
        leftForm.add(txtPrice);
        leftForm.add(new JLabel("Stock:"));
        leftForm.add(txtStock);
        leftForm.add(new JLabel("Expiry Date:"));
        leftForm.add(txtExpiry);

        leftContainer.add(leftForm, BorderLayout.CENTER);

        JPanel btnFormPanel = new JPanel(new GridLayout(1, 3, 8, 0));
        btnFormPanel.setOpaque(false);
        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");

        btnAdd.setBackground(new Color(39, 174, 96));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnEdit.setBackground(new Color(41, 128, 185));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFocusPainted(false);
        btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnFormPanel.add(btnAdd);
        btnFormPanel.add(btnEdit);
        btnFormPanel.add(btnDelete);

        leftContainer.add(btnFormPanel, BorderLayout.SOUTH);

        mainContent.add(leftContainer, BorderLayout.WEST);

        JPanel rightTablePanel = new JPanel(new BorderLayout(0, 10));
        rightTablePanel.setBackground(Color.WHITE);
        rightTablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel searchBarPanel = new JPanel(new BorderLayout(10, 0));
        searchBarPanel.setOpaque(false);
        txtSearch = new JTextField();
        JButton btnSearch = new JButton("Search");
        searchBarPanel.add(new JLabel("Search Medicine:"), BorderLayout.WEST);
        searchBarPanel.add(txtSearch, BorderLayout.CENTER);
        searchBarPanel.add(btnSearch, BorderLayout.EAST);
        rightTablePanel.add(searchBarPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Brand Name", "Category", "Price", "Stock", "Expiry", "Status"};
        Object[][] data = {
            {"M001", "Biogesic", "Tablet", "5.00", "150", "2028-12-01", "Normal"},
            {"M002", "Neozep", "Tablet", "7.50", "5", "2027-06-15", "Low Stock"},
            {"M003", "Alaxan FR", "Capsule", "10.00", "45", "2027-09-20", "Normal"}
        };

        tableModel = new DefaultTableModel(data, columns);
        table = new JTable(tableModel);
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String stockStr = (String) table.getValueAt(row, 4);
                try {
                    int stock = Integer.parseInt(stockStr);
                    if (stock < 10) {
                        table.setValueAt("Low Stock", row, 6);
                        if (!isSelected) {
                            c.setBackground(new Color(253, 237, 236));
                            setForeground(new Color(192, 57, 43));
                        }
                    } else {
                        table.setValueAt("Normal", row, 6);
                        if (!isSelected) {
                            c.setBackground(Color.WHITE);
                            setForeground(Color.BLACK);
                        }
                    }
                } catch (Exception e) {
                }
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        rightTablePanel.add(scrollPane, BorderLayout.CENTER);

        mainContent.add(rightTablePanel, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String category = txtCategory.getText().trim();
            String price = txtPrice.getText().trim();
            String stockInput = txtStock.getText().trim();
            String expiry = txtExpiry.getText().trim();

            if (!id.isEmpty() && !name.isEmpty()) {
                boolean found = false;
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String existingId = (String) tableModel.getValueAt(i, 0);
                    if (existingId.equalsIgnoreCase(id)) {
                        try {
                            int currentStock = Integer.parseInt((String) tableModel.getValueAt(i, 4));
                            int addedStock = Integer.parseInt(stockInput);
                            int totalStock = currentStock + addedStock;
                            
                            tableModel.setValueAt(String.valueOf(totalStock), i, 4);
                            if (!price.isEmpty()) tableModel.setValueAt(price, i, 3);
                            if (!expiry.isEmpty()) tableModel.setValueAt(expiry, i, 5);
                            
                            found = true;
                            break;
                        } catch (Exception ex) {
                        }
                    }
                }

                if (!found) {
                    String status = "Normal";
                    try {
                        if (Integer.parseInt(stockInput) < 10) {
                            status = "Low Stock";
                        }
                    } catch (Exception ex) {
                    }
                    tableModel.addRow(new Object[]{id, name, category, price, stockInput, expiry, status});
                }

                clearForm();
                table.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in Medicine ID and Brand Name.");
            }
        });

        btnEdit.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.setValueAt(txtId.getText(), selectedRow, 0);
                tableModel.setValueAt(txtName.getText(), selectedRow, 1);
                tableModel.setValueAt(txtCategory.getText(), selectedRow, 2);
                tableModel.setValueAt(txtPrice.getText(), selectedRow, 3);
                tableModel.setValueAt(txtStock.getText(), selectedRow, 4);
                tableModel.setValueAt(txtExpiry.getText(), selectedRow, 5);
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to edit.");
            }
        });

        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            }
        });

        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText().toLowerCase();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String name = ((String) tableModel.getValueAt(i, 1)).toLowerCase();
                if (name.contains(keyword)) {
                    table.setRowSelectionInterval(i, i);
                    break;
                }
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                txtId.setText((String) tableModel.getValueAt(selectedRow, 0));
                txtName.setText((String) tableModel.getValueAt(selectedRow, 1));
                txtCategory.setText((String) tableModel.getValueAt(selectedRow, 2));
                txtPrice.setText((String) tableModel.getValueAt(selectedRow, 3));
                txtStock.setText((String) tableModel.getValueAt(selectedRow, 4));
                txtExpiry.setText((String) tableModel.getValueAt(selectedRow, 5));
            }
        });
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtCategory.setText("");
        txtPrice.setText("");
        txtStock.setText("");
        txtExpiry.setText("");
    }
}