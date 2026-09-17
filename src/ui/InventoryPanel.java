package ui;

import db.MedicineDAO;
import models.Medicine;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class InventoryPanel extends JPanel {

    private static final int LOW_STOCK_THRESHOLD = MedicineDAO.LOW_STOCK_THRESHOLD;

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField txtName, txtPrice, txtStock, txtSearch;
    private final JLabel lblLowStock;
    private int selectedId = -1;

    public InventoryPanel() {
        setLayout(new BorderLayout());

        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(15);
        JButton btnSearch = new JButton("Search");
        JButton btnShowAll = new JButton("Show All");
        lblLowStock = new JLabel(" ");
        lblLowStock.setForeground(new Color(200, 0, 0));
        lblLowStock.setFont(lblLowStock.getFont().deriveFont(Font.BOLD));

        searchPanel.add(new JLabel("Search Name:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnShowAll);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(lblLowStock);

        add(searchPanel, BorderLayout.NORTH);

       
        String[] columns = {"ID", "Name", "Price", "Stock"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

       
        DefaultTableCellRenderer lowStockRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                int stock = Integer.parseInt(tbl.getValueAt(row, 3).toString());
                if (isSelected) {
                    c.setBackground(tbl.getSelectionBackground());
                } else if (stock <= LOW_STOCK_THRESHOLD) {
                    c.setBackground(new Color(255, 205, 210)); // light red
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(lowStockRenderer);
        }

        add(new JScrollPane(table), BorderLayout.CENTER);

       
        JPanel formPanel = new JPanel(new FlowLayout());
        txtName = new JTextField(10);
        txtPrice = new JTextField(6);
        txtStock = new JTextField(6);
        JButton btnAdd = new JButton("Add Medicine");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");

        formPanel.add(new JLabel("Name:"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("Price:"));
        formPanel.add(txtPrice);
        formPanel.add(new JLabel("Stock:"));
        formPanel.add(txtStock);
        formPanel.add(btnAdd);
        formPanel.add(btnUpdate);
        formPanel.add(btnDelete);
        formPanel.add(btnClear);

        add(formPanel, BorderLayout.SOUTH);

        loadTableData();

        
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtPrice.setText(tableModel.getValueAt(row, 2).toString());
                txtStock.setText(tableModel.getValueAt(row, 3).toString());
            }
        });

        
        btnAdd.addActionListener(e -> {
            try {
                String name = txtName.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Pakilagay ang pangalan ng gamot.");
                    return;
                }
                double price = Double.parseDouble(txtPrice.getText().trim());
                int stock = Integer.parseInt(txtStock.getText().trim());

                Medicine med = new Medicine(name, price, stock);
                MedicineDAO.insertMedicine(med);

                loadTableData();
                clearForm();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid values!");
            }
        });

        
        btnUpdate.addActionListener(e -> {
            if (selectedId == -1) {
                JOptionPane.showMessageDialog(this, "Pumili muna ng gamot sa listahan na ie-edit.");
                return;
            }
            try {
                String name = txtName.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Pakilagay ang pangalan ng gamot.");
                    return;
                }
                double price = Double.parseDouble(txtPrice.getText().trim());
                int stock = Integer.parseInt(txtStock.getText().trim());

                Medicine med = new Medicine();
                med.setId(selectedId);
                med.setName(name);
                med.setPrice(price);
                med.setStock(stock);

                boolean success = MedicineDAO.updateMedicine(med);
                if (success) {
                    loadTableData();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Hindi na-update ang gamot.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid values!");
            }
        });

        
        btnDelete.addActionListener(e -> {
            if (selectedId == -1) {
                JOptionPane.showMessageDialog(this, "Pumili muna ng gamot sa listahan na tatanggalin.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Sigurado ka bang gusto mong tanggalin ang gamot na ito?",
                    "Kumpirmahin ang Pagtanggal", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                MedicineDAO.deleteMedicine(selectedId);
                loadTableData();
                clearForm();
            }
        });

        
        btnClear.addActionListener(e -> clearForm());

        
        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText().trim();
            List<Medicine> results = keyword.isEmpty()
                    ? MedicineDAO.getAllMedicines()
                    : MedicineDAO.searchMedicines(keyword);
            populateTable(results);
        });

        btnShowAll.addActionListener(e -> {
            txtSearch.setText("");
            loadTableData();
        });
    }

    private void loadTableData() {
        populateTable(MedicineDAO.getAllMedicines());
    }

    private void populateTable(List<Medicine> list) {
        tableModel.setRowCount(0);
        int lowStockCount = 0;
        for (Medicine m : list) {
            tableModel.addRow(new Object[]{m.getId(), m.getName(), m.getPrice(), m.getStock()});
            if (m.getStock() <= LOW_STOCK_THRESHOLD) {
                lowStockCount++;
            }
        }
        if (lowStockCount > 0) {
            lblLowStock.setText("\u26A0 " + lowStockCount + " gamot ang mababa na ang stock!");
        } else {
            lblLowStock.setText(" ");
        }
    }

    private void clearForm() {
        selectedId = -1;
        txtName.setText("");
        txtPrice.setText("");
        txtStock.setText("");
        table.clearSelection();
    }
}