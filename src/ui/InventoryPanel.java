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
    private JTextField txtName, txtPrice, txtStock;

    public InventoryPanel() {
        setLayout(new BorderLayout());

        // Table Setup
        String[] columns = {"ID", "Name", "Price", "Stock"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        loadTableData();

        add(new JScrollPane(table), BorderLayout.CENTER);

    
        JPanel formPanel = new JPanel(new FlowLayout());
        txtName = new JTextField(10);
        txtPrice = new JTextField(6);
        txtStock = new JTextField(6);
        JButton btnAdd = new JButton("Add Medicine");

        formPanel.add(new JLabel("Name:"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("Price:"));
        formPanel.add(txtPrice);
        formPanel.add(new JLabel("Stock:"));
        formPanel.add(txtStock);
        formPanel.add(btnAdd);

        add(formPanel, BorderLayout.SOUTH);

        
        btnAdd.addActionListener(e -> {
            try {
                String name = txtName.getText();
                double price = Double.parseDouble(txtPrice.getText());
                int stock = Integer.parseInt(txtStock.getText());

                Medicine med = new Medicine(name, price, stock);
                MedicineDAO.insertMedicine(med);

                loadTableData(); // Refresh table
                txtName.setText("");
                txtPrice.setText("");
                txtStock.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid values!");
            }
        });
    }

    public void loadTableData() {
        tableModel.setRowCount(0);
        List<Medicine> list = MedicineDAO.getAllMedicines();
        for (Medicine m : list) {
            tableModel.addRow(new Object[]{m.getId(), m.getName(), m.getPrice(), m.getStock()});
        }
    }
}