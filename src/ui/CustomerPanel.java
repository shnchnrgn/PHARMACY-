package ui;

import db.CustomerDAO;
import models.Customer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerPanel extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField txtDays;
    private final JTextField txtName, txtContact, txtLastPurchase, txtSearch;
    private int selectedId = -1;

    public CustomerPanel() {
        setLayout(new BorderLayout());

        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(12);
        JButton btnSearch = new JButton("Search");
        txtDays = new JTextField("30", 5);
        JButton btnFilterInactive = new JButton("Filter Inactive");
        JButton btnShowAll = new JButton("Show All");

        filterPanel.add(new JLabel("Search Name/Contact:"));
        filterPanel.add(txtSearch);
        filterPanel.add(btnSearch);
        filterPanel.add(Box.createHorizontalStrut(15));
        filterPanel.add(new JLabel("Inactive (Days):"));
        filterPanel.add(txtDays);
        filterPanel.add(btnFilterInactive);
        filterPanel.add(btnShowAll);

        add(filterPanel, BorderLayout.NORTH);

        
        String[] columns = {"ID", "Name", "Contact", "Last Purchase Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        
        JPanel formPanel = new JPanel(new FlowLayout());
        txtName = new JTextField(10);
        txtContact = new JTextField(10);
        txtLastPurchase = new JTextField(10);
        JButton btnAdd = new JButton("Add Customer");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");

        formPanel.add(new JLabel("Name:"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("Contact:"));
        formPanel.add(txtContact);
        formPanel.add(new JLabel("Last Purchase (YYYY-MM-DD):"));
        formPanel.add(txtLastPurchase);
        formPanel.add(btnAdd);
        formPanel.add(btnUpdate);
        formPanel.add(btnDelete);
        formPanel.add(btnClear);

        add(formPanel, BorderLayout.SOUTH);

        populateTable(CustomerDAO.getAllCustomers());

        
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtContact.setText(tableModel.getValueAt(row, 2).toString());
                Object lastPurchase = tableModel.getValueAt(row, 3);
                txtLastPurchase.setText(lastPurchase == null ? "" : lastPurchase.toString());
            }
        });

        
        btnAdd.addActionListener(e -> {
            String name = txtName.getText().trim();
            String contact = txtContact.getText().trim();
            String lastPurchase = txtLastPurchase.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pakilagay ang pangalan ng customer.");
                return;
            }

            Customer c = new Customer(name, contact, lastPurchase);
            boolean success = CustomerDAO.addCustomer(c);
            if (success) {
                populateTable(CustomerDAO.getAllCustomers());
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Hindi na-add ang customer.");
            }
        });

        
        btnUpdate.addActionListener(e -> {
            if (selectedId == -1) {
                JOptionPane.showMessageDialog(this, "Pumili muna ng customer sa listahan na ie-edit.");
                return;
            }
            String name = txtName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pakilagay ang pangalan ng customer.");
                return;
            }
            String contact = txtContact.getText().trim();
            String lastPurchase = txtLastPurchase.getText().trim();

            Customer c = new Customer(selectedId, name, contact, lastPurchase);
            boolean success = CustomerDAO.updateCustomer(c);
            if (success) {
                populateTable(CustomerDAO.getAllCustomers());
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Hindi na-update ang customer.");
            }
        });

        
        btnDelete.addActionListener(e -> {
            if (selectedId == -1) {
                JOptionPane.showMessageDialog(this, "Pumili muna ng customer sa listahan na tatanggalin.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Sigurado ka bang gusto mong tanggalin ang customer na ito?",
                    "Kumpirmahin ang Pagtanggal", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                CustomerDAO.deleteCustomer(selectedId);
                populateTable(CustomerDAO.getAllCustomers());
                clearForm();
            }
        });

        
        btnClear.addActionListener(e -> clearForm());

        
        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText().trim();
            List<Customer> results = keyword.isEmpty()
                    ? CustomerDAO.getAllCustomers()
                    : CustomerDAO.searchCustomers(keyword);
            populateTable(results);
        });

        
        btnFilterInactive.addActionListener(e -> {
            try {
                int days = Integer.parseInt(txtDays.getText().trim());
                List<Customer> inactiveList = CustomerDAO.getInactiveCustomers(days);
                populateTable(inactiveList);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Mangyaring maglagay ng valid na numero.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnShowAll.addActionListener(e -> {
            txtSearch.setText("");
            populateTable(CustomerDAO.getAllCustomers());
        });
    }

    private void populateTable(List<Customer> list) {
        tableModel.setRowCount(0);
        for (Customer c : list) {
            tableModel.addRow(new Object[]{
                c.getId(),
                c.getName(),
                c.getContact(),
                c.getLastPurchaseDate()
            });
        }
    }

    private void clearForm() {
        selectedId = -1;
        txtName.setText("");
        txtContact.setText("");
        txtLastPurchase.setText("");
        table.clearSelection();
    }
}