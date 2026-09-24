package ui;

import db.CustomerDAO;
import models.Customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerPanel extends JPanel {

    private JTable customerTable;
    private JTextField txtCustomerId, txtFullName, txtContactNumber, txtAddress, txtDateRegistered, txtSearch;
    private DefaultTableModel customerModel;

    public CustomerPanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Customer Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(60, 65, 70));
        add(lblTitle, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        contentPanel.setOpaque(false);

        // ================= FORM =================

        JPanel leftFormContainer = new JPanel(new BorderLayout());
        leftFormContainer.setBackground(Color.WHITE);
        leftFormContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel formFieldsPanel = new JPanel(new GridLayout(5, 2, 10, 12));
        formFieldsPanel.setOpaque(false);

        txtCustomerId = createStyledTextField();
        txtFullName = createStyledTextField();
        txtContactNumber = createStyledTextField();
        txtAddress = createStyledTextField();
        txtDateRegistered = createStyledTextField();

        txtCustomerId.setEditable(false);
        txtDateRegistered.setEditable(false);

        addFormRow(formFieldsPanel, "Customer ID", txtCustomerId);
        addFormRow(formFieldsPanel, "Full Name", txtFullName);
        addFormRow(formFieldsPanel, "Contact Number", txtContactNumber);
        addFormRow(formFieldsPanel, "Address", txtAddress);
        addFormRow(formFieldsPanel, "Date Registered", txtDateRegistered);

        leftFormContainer.add(formFieldsPanel, BorderLayout.CENTER);

        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionButtonPanel.setOpaque(false);
        actionButtonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton btnAdd = createStyledButton("Add", new Color(40, 167, 69));
        JButton btnEdit = createStyledButton("Edit", new Color(51, 122, 183));
        JButton btnDelete = createStyledButton("Delete", new Color(220, 53, 69));
        JButton btnInactive = createStyledButton("Inactive Customers", new Color(108, 117, 125));
 
        actionButtonPanel.add(btnAdd);
        actionButtonPanel.add(btnEdit);
        actionButtonPanel.add(btnDelete);
        actionButtonPanel.add(btnInactive);

        btnAdd.addActionListener(e -> addCustomer());
        btnEdit.addActionListener(e -> editCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnInactive.addActionListener(e -> loadInactiveCustomers());

        leftFormContainer.add(actionButtonPanel, BorderLayout.SOUTH);

          


        JPanel rightTableContainer = new JPanel(new BorderLayout(0, 10));
        rightTableContainer.setBackground(Color.WHITE);
        rightTableContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel searchBarPanel = new JPanel(new BorderLayout(8, 0));
        searchBarPanel.setOpaque(false);

        JLabel lblSearch = new JLabel("Search Customer:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSearch.setForeground(new Color(70, 75, 80));

        txtSearch = createStyledTextField();

        JButton btnSearch = createStyledButton("Search", new Color(51, 122, 183));
        JButton btnShowAll = createStyledButton("Show All", new Color(108, 117, 125));
        JButton btnHistory = createStyledButton("Purchase History", new Color(40, 167, 69));

        JPanel searchButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchButtons.setOpaque(false);
        searchButtons.add(btnSearch);
        searchButtons.add(btnShowAll);
        searchButtons.add(btnHistory);

        searchBarPanel.add(lblSearch, BorderLayout.WEST);
        searchBarPanel.add(txtSearch, BorderLayout.CENTER);
        searchBarPanel.add(searchButtons, BorderLayout.EAST);

        rightTableContainer.add(searchBarPanel, BorderLayout.NORTH);

        String[] columns = {
                "Customer ID",
                "Full Name",
                "Contact Number",
                "Last Purchase"
        };

        customerModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customerTable = new JTable(customerModel);
        customerTable.setRowHeight(32);
        customerTable.setShowVerticalLines(false);
        customerTable.setShowHorizontalLines(true);
        customerTable.setGridColor(new Color(235, 238, 242));
        customerTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        customerTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        customerTable.getTableHeader().setBackground(new Color(248, 249, 250));
        customerTable.getTableHeader().setForeground(new Color(80, 85, 90));

        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));

        rightTableContainer.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(leftFormContainer);
        contentPanel.add(rightTableContainer);

        add(contentPanel, BorderLayout.CENTER);


        customerTable.getSelectionModel().addListSelectionListener(e -> {
          if (!e.getValueIsAdjusting()) {
           loadSelectedCustomer();
    }
         });

        btnShowAll.addActionListener(e -> loadCustomers());
        btnSearch.addActionListener(e -> searchCustomers());
        txtSearch.addActionListener(e -> searchCustomers());
        btnHistory.addActionListener(e -> showPurchaseHistory());


        loadCustomers();
    }

   

    private void loadCustomers() {
        customerModel.setRowCount(0);

        try {
            List<Customer> customers = CustomerDAO.getAllCustomers();

            if (customers.isEmpty()) {
                customerModel.addRow(new Object[]{
                        "",
                        "No customers found.",
                        "",
                        ""
                });
                return;
            }

            for (Customer customer : customers) {
                customerModel.addRow(new Object[]{
                        customer.getId(),
                        customer.getName(),
                        customer.getContact(),
                        formatLastPurchase(customer.getLastPurchaseDate())
                });
            }

        } catch (Exception e) {
            showError("Unable to load customers.\n" + e.getMessage());
        }
    }

    // ================= SEARCH =================

    private void searchCustomers() {
        String keyword = txtSearch.getText().trim();

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a customer name to search.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        customerModel.setRowCount(0);

        try {
            List<Customer> customers = CustomerDAO.searchCustomers(keyword);

            if (customers.isEmpty()) {
                customerModel.addRow(new Object[]{
                        "",
                        "No customer found.",
                        "",
                        ""
                });
                return;
            }

            for (Customer customer : customers) {
                customerModel.addRow(new Object[]{
                        customer.getId(),
                        customer.getName(),
                        customer.getContact(),
                        formatLastPurchase(customer.getLastPurchaseDate())
                });
            }

        } catch (Exception e) {
            showError("Search failed.\n" + e.getMessage());
        }
    }

    // ================= INACTIVE CUSTOMERS =================

    private void loadInactiveCustomers() {
        String input = JOptionPane.showInputDialog(
                this,
                "Show customers inactive for how many days?",
                "Inactive Customers",
                JOptionPane.QUESTION_MESSAGE
        );

        if (input == null) {
            return;
        }

        input = input.trim();

        if (input.isEmpty() || !input.matches("\\d+")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid number of days.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int days = Integer.parseInt(input);

        if (days <= 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Days must be greater than 0.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        customerModel.setRowCount(0);

        try {
            List<Customer> customers = CustomerDAO.getInactiveCustomers(days);

            if (customers.isEmpty()) {
                customerModel.addRow(new Object[]{
                        "",
                        "No inactive customers found.",
                        "",
                        ""
                });
                return;
            }

            for (Customer customer : customers) {
                customerModel.addRow(new Object[]{
                        customer.getId(),
                        customer.getName(),
                        customer.getContact(),
                        formatLastPurchase(customer.getLastPurchaseDate())
                });
            }

        } catch (Exception e) {
            showError("Unable to load inactive customers.\n" + e.getMessage());
        }
    }

    // ================= PURCHASE HISTORY =================

    private void showPurchaseHistory() {
        int selectedRow = customerTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a customer first.",
                    "No Customer Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Object idValue = customerModel.getValueAt(selectedRow, 0);

        if (!(idValue instanceof Integer)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a valid customer.",
                    "Invalid Selection",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int customerId = (Integer) idValue;
        String customerName = customerModel.getValueAt(selectedRow, 1).toString();

        List<String[]> history = CustomerDAO.getPurchaseHistory(customerId);

        DefaultTableModel historyModel = new DefaultTableModel(
                new String[]{"Order No.", "Purchase Date", "Amount"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (String[] purchase : history) {
            historyModel.addRow(purchase);
        }

        JTable historyTable = new JTable(historyModel);
        historyTable.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setPreferredSize(new Dimension(500, 250));

        if (history.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No purchase history found for " + customerName + ".",
                    "Purchase History",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Purchase History - " + customerName,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ================= FORM ACTIONS =================

    private void addCustomer() {
        String name = txtFullName.getText().trim();
        String contact = txtContactNumber.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Full Name is required.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!name.matches("[a-zA-Z .'-]+")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Full Name should contain letters only.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (contact.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Contact Number is required.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!contact.matches("\\d{11}")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Contact Number must contain exactly 11 digits.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            Customer customer = new Customer(
                    0,
                    name,
                    contact,
                    "N/A"
            );

            CustomerDAO.addCustomer(customer);

            JOptionPane.showMessageDialog(
                    this,
                    "Customer added successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearForm();
            loadCustomers();

        } catch (Exception e) {
            showError("Unable to add customer.\n" + e.getMessage());
        }
    }

    private void editCustomer() {
        int selectedRow = customerTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a customer to edit.",
                    "No Customer Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Object idValue = customerModel.getValueAt(selectedRow, 0);

        if (!(idValue instanceof Integer)) {
            return;
        }

        String name = txtFullName.getText().trim();
        String contact = txtContactNumber.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Full Name is required.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!name.matches("[a-zA-Z .'-]+")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Full Name should contain letters only.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!contact.matches("\\d{11}")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Contact Number must contain exactly 11 digits.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            Customer customer = new Customer(
                    (Integer) idValue,
                    name,
                    contact,
                    txtDateRegistered.getText()
            );

            CustomerDAO.updateCustomer(customer);

            JOptionPane.showMessageDialog(
                    this,
                    "Customer updated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadCustomers();

        } catch (Exception e) {
            showError("Unable to update customer.\n" + e.getMessage());
        }
    }

    private void deleteCustomer() {
        int selectedRow = customerTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a customer to delete.",
                    "No Customer Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Object idValue = customerModel.getValueAt(selectedRow, 0);

        if (!(idValue instanceof Integer)) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this customer?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            CustomerDAO.deleteCustomer((Integer) idValue);

            JOptionPane.showMessageDialog(
                    this,
                    "Customer deleted successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearForm();
            loadCustomers();

        } catch (Exception e) {
            showError("Unable to delete customer.\n" + e.getMessage());
        }
    }

    // ================= SELECTED CUSTOMER =================

    private void loadSelectedCustomer() {
        int row = customerTable.getSelectedRow();

        if (row == -1) {
            return;
        }

        Object id = customerModel.getValueAt(row, 0);

        if (!(id instanceof Integer)) {
            return;
        }

        txtCustomerId.setText(String.valueOf(id));
        txtFullName.setText(String.valueOf(customerModel.getValueAt(row, 1)));
        txtContactNumber.setText(String.valueOf(customerModel.getValueAt(row, 2)));
        txtDateRegistered.setText("Not available");
        txtAddress.setText("Not available");
    }

    private void clearForm() {
        txtCustomerId.setText("");
        txtFullName.setText("");
        txtContactNumber.setText("");
        txtAddress.setText("");
        txtDateRegistered.setText("");
    }

    private String formatLastPurchase(String date) {
        if (date == null || date.trim().isEmpty() || date.equalsIgnoreCase("N/A")) {
            return "No purchase yet";
        }

        return date;
    }

    // ================= UI HELPERS =================

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setForeground(new Color(70, 75, 80));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        return tf;
    }

    private JButton createStyledButton(String text, Color bgCol) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bgCol);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void addFormRow(JPanel panel, String labelText, JComponent field) {
        JLabel lbl = new JLabel("<html><b>" + labelText + ":</b></html>");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(70, 75, 80));
        panel.add(lbl);
        panel.add(field);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
    
        );
    }
}