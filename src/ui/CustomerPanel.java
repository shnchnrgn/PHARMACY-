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
    private JTextField txtDays;

    public CustomerPanel() {
        setLayout(new BorderLayout());

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtDays = new JTextField("30", 5);
        JButton btnFilterInactive = new JButton("Filter Inactive");
        JButton btnShowAll = new JButton("Show All");

        filterPanel.add(new JLabel("Inactive (Days):"));
        filterPanel.add(txtDays);
        filterPanel.add(btnFilterInactive);
        filterPanel.add(btnShowAll);

        add(filterPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Contact", "Last Purchase Date"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadAllCustomers();

        btnFilterInactive.addActionListener(e -> {
            try {
                int days = Integer.parseInt(txtDays.getText().trim());
                List<Customer> inactiveList = CustomerDAO.getInactiveCustomers(days);
                populateTable(inactiveList);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Mangyaring maglagay ng valid na numero.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnShowAll.addActionListener(e -> loadAllCustomers());
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
}