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
    private JTextField txtName, txtContact, txtDate;

    public CustomerPanel() {
        setLayout(new BorderLayout());

        String[] columns = {"ID", "Name", "Contact", "Last Purchase Date"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        loadTableData();

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new FlowLayout());
        txtName = new JTextField(10);
        txtContact = new JTextField(10);
        txtDate = new JTextField(8);
        JButton btnAdd = new JButton("Add Customer");

        formPanel.add(new JLabel("Name:"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("Contact:"));
        formPanel.add(txtContact);
        formPanel.add(new JLabel("Last Purchase (YYYY-MM-DD):"));
        formPanel.add(txtDate);
        formPanel.add(btnAdd);

        add(formPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> {
            Customer cust = new Customer(txtName.getText(), txtContact.getText(), txtDate.getText());
            CustomerDAO.insertCustomer(cust);
            loadTableData();
            txtName.setText("");
            txtContact.setText("");
            txtDate.setText("");
        });
    }

    public void loadTableData() {
        tableModel.setRowCount(0);
        List<Customer> list = CustomerDAO.getAllCustomers();
        for (Customer c : list) {
            tableModel.addRow(new Object[]{c.getId(), c.getName(), c.getContact(), c.getLastPurchaseDate()});
        }
    }
}