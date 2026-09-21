package ui;

import db.CustomerDAO;
import models.Customer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtDays, txtSearch, txtName, txtContact;
    private JButton btnFilterInactive, btnShowAll, btnSearch, btnAdd, btnEdit, btnDelete;
    private int selectedCustomerId = -1;

    public CustomerPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(15);
        btnSearch = new JButton("Search");
        searchPanel.add(new JLabel("Search Name:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtDays = new JTextField("30", 5);
        btnFilterInactive = new JButton("Filter Inactive");
        btnShowAll = new JButton("Show All");
        filterPanel.add(new JLabel("Inactive (Days):"));
        filterPanel.add(txtDays);
        filterPanel.add(btnFilterInactive);
        filterPanel.add(btnShowAll);

        topPanel.add(searchPanel);
        topPanel.add(filterPanel);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Contact", "Last Purchase Date"};
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
        txtContact = new JTextField(10);
        btnAdd = new JButton("Add Customer");
        btnEdit = new JButton("Update");
        btnDelete = new JButton("Delete");

        bottomPanel.add(new JLabel("Name:"));
        bottomPanel.add(txtName);
        bottomPanel.add(new JLabel("Contact:"));
        bottomPanel.add(txtContact);
        bottomPanel.add(btnAdd);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnDelete);

        add(bottomPanel, BorderLayout.SOUTH);

        loadAllCustomers();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                selectedCustomerId = (int) tableModel.getValueAt(row, 0);
                txtName.setText((String) tableModel.getValueAt(row, 1));
                txtContact.setText((String) tableModel.getValueAt(row, 2));
            }
        });

        btnAdd.addActionListener(e -> {
            String name = txtName.getText().trim();
            String contact = txtContact.getText().trim();

            if (name.isEmpty() || contact.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Customer c = new Customer();
            c.setName(name);
            c.setContact(contact);
            c.setLastPurchaseDate("N/A");
            CustomerDAO.addCustomer(c);

            clearForm();
            loadAllCustomers();
            JOptionPane.showMessageDialog(this, "Customer added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        btnEdit.addActionListener(e -> {
            if (selectedCustomerId == -1) {
                JOptionPane.showMessageDialog(this, "Please select a customer to update.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String name = txtName.getText().trim();
            String contact = txtContact.getText().trim();

            if (name.isEmpty() || contact.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Customer c = new Customer();
            c.setId(selectedCustomerId);
            c.setName(name);
            c.setContact(contact);
            CustomerDAO.updateCustomer(c);

            clearForm();
            loadAllCustomers();
            JOptionPane.showMessageDialog(this, "Customer updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        btnDelete.addActionListener(e -> {
            if (selectedCustomerId == -1) {
                JOptionPane.showMessageDialog(this, "Please select a customer to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this customer?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                CustomerDAO.deleteCustomer(selectedCustomerId);
                clearForm();
                loadAllCustomers();
                JOptionPane.showMessageDialog(this, "Customer deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText().trim();
            if (keyword.isEmpty()) {
                loadAllCustomers();
                return;
            }
            List<Customer> list = CustomerDAO.searchCustomers(keyword);
            populateTable(list);
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
            loadAllCustomers();
        });
    }

    public void loadAllCustomers() {
        List<Customer> allCustomers = CustomerDAO.getAllCustomers();
        populateTable(allCustomers);
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
        txtName.setText("");
        txtContact.setText("");
        selectedCustomerId = -1;
        table.clearSelection();
    }
}