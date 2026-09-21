package ui;

import db.MedicineDAO;
import models.Medicine;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InventoryPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtName, txtPrice, txtStock, txtSearch;
    private JButton btnAdd, btnEdit, btnDelete, btnSearch, btnShowAll;
    private JLabel lblLowStock;
    private int selectedMedicineId = -1;

    public InventoryPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(15);
        btnSearch = new JButton("Search");
        btnShowAll = new JButton("Show All");
        lblLowStock = new JLabel();
        lblLowStock.setForeground(Color.RED);

        topPanel.add(new JLabel("Search Name:"));
        topPanel.add(txtSearch);
        topPanel.add(btnSearch);
        topPanel.add(btnShowAll);
        topPanel.add(lblLowStock);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Price", "Stock"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        txtName = new JTextField(12);
        txtPrice = new JTextField(6);
        txtStock = new JTextField(6);
        btnAdd = new JButton("Add Medicine");
        btnEdit = new JButton("Update");
        btnDelete = new JButton("Delete");

        bottomPanel.add(new JLabel("Name:"));
        bottomPanel.add(txtName);
        bottomPanel.add(new JLabel("Price:"));
        bottomPanel.add(txtPrice);
        bottomPanel.add(new JLabel("Stock:"));
        bottomPanel.add(txtStock);
        bottomPanel.add(btnAdd);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnDelete);

        add(bottomPanel, BorderLayout.SOUTH);

        loadTableData();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                selectedMedicineId = (int) tableModel.getValueAt(row, 0);
                txtName.setText((String) tableModel.getValueAt(row, 1));
                txtPrice.setText(String.valueOf(tableModel.getValueAt(row, 2)));
                txtStock.setText(String.valueOf(tableModel.getValueAt(row, 3)));
            }
        });

        btnAdd.addActionListener(e -> {
            try {
                String name = txtName.getText().trim();
                double price = Double.parseDouble(txtPrice.getText().trim());
                int stock = Integer.parseInt(txtStock.getText().trim());

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter medicine name.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Medicine m = new Medicine();
                m.setName(name);
                m.setPrice(price);
                m.setStock(stock);
                MedicineDAO.addMedicine(m);

                clearForm();
                loadTableData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for price and stock.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEdit.addActionListener(e -> {
            if (selectedMedicineId == -1) {
                JOptionPane.showMessageDialog(this, "Please select a medicine to update.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                String name = txtName.getText().trim();
                double price = Double.parseDouble(txtPrice.getText().trim());
                int stock = Integer.parseInt(txtStock.getText().trim());

                Medicine m = new Medicine();
                m.setId(selectedMedicineId);
                m.setName(name);
                m.setPrice(price);
                m.setStock(stock);
                MedicineDAO.updateMedicine(m);

                clearForm();
                loadTableData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for price and stock.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnDelete.addActionListener(e -> {
            if (selectedMedicineId == -1) {
                JOptionPane.showMessageDialog(this, "Please select a medicine to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this medicine?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                MedicineDAO.deleteMedicine(selectedMedicineId);
                clearForm();
                loadTableData();
            }
        });

        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText().trim();
            if (keyword.isEmpty()) {
                loadTableData();
                return;
            }
            tableModel.setRowCount(0);
            List<Medicine> list = MedicineDAO.getAllMedicines();
            for (Medicine m : list) {
                if (m.getName().toLowerCase().contains(keyword.toLowerCase())) {
                    tableModel.addRow(new Object[]{m.getId(), m.getName(), m.getPrice(), m.getStock()});
                }
            }
        });

        btnShowAll.addActionListener(e -> {
            txtSearch.setText("");
            loadTableData();
        });
    }

    public void loadTableData() {
        tableModel.setRowCount(0);
        List<Medicine> list = MedicineDAO.getAllMedicines();

        if (list == null || list.isEmpty()) {
            addDefaultItem("Paracetamol", 50.0, 15);
            addDefaultItem("Ibuprofen", 75.0, 7);
            addDefaultItem("Amoxicillin", 120.0, 20);
            list = MedicineDAO.getAllMedicines();
        }

        for (Medicine m : list) {
            tableModel.addRow(new Object[]{
                m.getId(),
                m.getName(),
                m.getPrice(),
                m.getStock()
            });
        }

        int lowStockCount = MedicineDAO.getLowStockCount();
        if (lowStockCount > 0) {
            lblLowStock.setText(" ⚠️ " + lowStockCount + " medicine(s) have low stock!");
        } else {
            lblLowStock.setText("");
        }
    }

    private void addDefaultItem(String name, double price, int stock) {
        Medicine m = new Medicine();
        m.setName(name);
        m.setPrice(price);
        m.setStock(stock);
        MedicineDAO.addMedicine(m);
    }

    private void clearForm() {
        txtName.setText("");
        txtPrice.setText("");
        txtStock.setText("");
        selectedMedicineId = -1;
        table.clearSelection();
    }
}