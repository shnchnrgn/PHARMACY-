package ui;

import db.CustomerDAO;
import models.Customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class CustomerPanel extends JPanel {

    private static final Color COLOR_GREEN   = new Color(40, 167, 69);
    private static final Color COLOR_RED     = new Color(220, 53, 69);
    private static final Color COLOR_BLUE    = new Color(51, 122, 183);
    private static final Color COLOR_GRAY    = new Color(108, 117, 125);
    private static final Color COLOR_BORDER  = new Color(200, 205, 210);
    private static final Color COLOR_BORDER_ERROR = new Color(220, 53, 69);
    private static final Color COLOR_TEXT    = new Color(60, 65, 70);
    private static final Color COLOR_ACTIVE  = new Color(30, 130, 76);
    private static final Color COLOR_INACTIVE = new Color(193, 47, 47);

    private static final int DEFAULT_INACTIVE_DAYS = 90;
    private static final int LABEL_WIDTH = 140;

    private JTable customerTable;
    private DefaultTableModel customerModel;

    private JTextField txtCustomerId, txtFullName, txtContactNumber, txtAddress, txtDateRegistered, txtSearch;
    private JLabel lblFullNameError, lblContactError, lblAddressError;
    private JButton btnSave;
    private JComboBox<String> cmbFilter;
    private JSpinner spinnerDays;

    private List<Customer> displayedCustomers;

    public CustomerPanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Customer Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(COLOR_TEXT);
        add(lblTitle, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        contentPanel.setOpaque(false);

        contentPanel.add(buildFormPanel());
        contentPanel.add(buildTablePanel());

        add(contentPanel, BorderLayout.CENTER);

        loadCustomers();
    }


    private JPanel buildFormPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        int row = 0;

        txtCustomerId = createStyledTextField();
        txtCustomerId.setEditable(false);
        txtCustomerId.setBackground(new Color(240, 240, 240));
        row = addFieldRow(fieldsPanel, gbc, row, "Customer ID", false, txtCustomerId, null);

        txtFullName = createStyledTextField();
        lblFullNameError = createErrorLabel();
        row = addFieldRow(fieldsPanel, gbc, row, "Full Name", true, txtFullName, lblFullNameError);

        txtContactNumber = createStyledTextField();
        lblContactError = createErrorLabel();
        row = addFieldRow(fieldsPanel, gbc, row, "Contact Number", true, txtContactNumber, lblContactError);

        txtAddress = createStyledTextField();
        lblAddressError = createErrorLabel();
        row = addFieldRow(fieldsPanel, gbc, row, "Address", true, txtAddress, lblAddressError);

        txtDateRegistered = createStyledTextField();
        txtDateRegistered.setEditable(false);
        txtDateRegistered.setBackground(new Color(240, 240, 240));
        row = addFieldRow(fieldsPanel, gbc, row, "Date Registered", false, txtDateRegistered, null);

        // Buttons nasa ilalim mismo ng mga field (hindi nakadikit sa pinakababa ng panel)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(15, LABEL_WIDTH + 10, 0, 0));

        btnSave = createStyledButton("Save Customer", COLOR_GREEN);
        JButton btnDelete = createStyledButton("Delete Customer", COLOR_GRAY);
        JButton btnClear = createStyledButton("Clear Form", COLOR_RED);

        btnSave.addActionListener(e -> saveCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnClear.addActionListener(e -> clearForm());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        JPanel formContent = new JPanel(new BorderLayout());
        formContent.setOpaque(false);
        formContent.add(fieldsPanel, BorderLayout.CENTER);
        formContent.add(buttonPanel, BorderLayout.SOUTH);

        container.add(formContent, BorderLayout.NORTH);

        return container;
    }


    private int addFieldRow(JPanel panel, GridBagConstraints gbc, int row, String labelText,
                             boolean required, JComponent field, JLabel errorLabel) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(6, 0, 0, 10);

        JLabel label = new JLabel(required
                ? "<html>" + labelText + " <span style='color:#DC3545;'>*</span></html>"
                : labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(COLOR_TEXT);
        label.setPreferredSize(new Dimension(LABEL_WIDTH, 34));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        field.setPreferredSize(new Dimension(100, 34));
        panel.add(field, gbc);
        row++;

        JLabel errLbl = (errorLabel != null) ? errorLabel : createErrorLabel();
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 10);
        panel.add(errLbl, gbc);
        row++;

        return row;
    }

    private JLabel createErrorLabel() {
        JLabel lbl = new JLabel(" ");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(COLOR_RED);
        return lbl;
    }


    private JPanel buildTablePanel() {
        JPanel container = new JPanel(new BorderLayout(0, 10));
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel toolbar = new JPanel();
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.Y_AXIS));
        toolbar.setOpaque(false);

        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setOpaque(false);

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSearch.setForeground(COLOR_TEXT);

        txtSearch = createStyledTextField();

        JButton btnSearch = createStyledButton("Search", COLOR_BLUE);
        JButton btnShowAll = createStyledButton("Show All", COLOR_GRAY);
        JButton btnHistory = createStyledButton("Purchase History", COLOR_GREEN);

        JPanel searchButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchButtons.setOpaque(false);
        searchButtons.add(btnSearch);
        searchButtons.add(btnShowAll);
        searchButtons.add(btnHistory);

        searchRow.add(lblSearch, BorderLayout.WEST);
        searchRow.add(txtSearch, BorderLayout.CENTER);
        searchRow.add(searchButtons, BorderLayout.EAST);

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        filterRow.setOpaque(false);

        JLabel lblFilter = new JLabel("Show:");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFilter.setForeground(COLOR_TEXT);

        cmbFilter = new JComboBox<>(new String[]{"All Customers", "Active Only", "Inactive Only"});
        cmbFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblDays = new JLabel("Inactive after (days):");
        lblDays.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDays.setForeground(COLOR_TEXT);

        spinnerDays = new JSpinner(new SpinnerNumberModel(DEFAULT_INACTIVE_DAYS, 1, 3650, 1));
        spinnerDays.setPreferredSize(new Dimension(70, 26));

        filterRow.add(lblFilter);
        filterRow.add(cmbFilter);
        filterRow.add(lblDays);
        filterRow.add(spinnerDays);

        toolbar.add(searchRow);
        toolbar.add(filterRow);

        container.add(toolbar, BorderLayout.NORTH);

        String[] columns = {
                "ID", "Full Name", "Contact Number", "Address", "Last Purchase", "Status"
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

        customerTable.getColumnModel().getColumn(0).setPreferredWidth(40);   // ID
        customerTable.getColumnModel().getColumn(1).setPreferredWidth(130);  // Full Name
        customerTable.getColumnModel().getColumn(2).setPreferredWidth(110);  // Contact Number
        customerTable.getColumnModel().getColumn(3).setPreferredWidth(150);  // Address
        customerTable.getColumnModel().getColumn(4).setPreferredWidth(100);  // Last Purchase
        customerTable.getColumnModel().getColumn(5).setPreferredWidth(80);   // Status

        customerTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                             boolean hasFocus, int rowIdx, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, rowIdx, col);
                setHorizontalAlignment(CENTER);
                setFont(getFont().deriveFont(Font.BOLD));
                if ("Inactive".equals(value)) {
                    setForeground(isSelected ? Color.WHITE : COLOR_INACTIVE);
                } else if ("Active".equals(value)) {
                    setForeground(isSelected ? Color.WHITE : COLOR_ACTIVE);
                } else {
                    setForeground(isSelected ? Color.WHITE : COLOR_TEXT);
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));
        container.add(scrollPane, BorderLayout.CENTER);

        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedCustomer();
            }
        });

        btnShowAll.addActionListener(e -> {
            txtSearch.setText("");
            loadCustomers();
        });
        btnSearch.addActionListener(e -> searchCustomers());
        txtSearch.addActionListener(e -> searchCustomers());
        btnHistory.addActionListener(e -> showPurchaseHistory());
        cmbFilter.addActionListener(e -> refreshTable());
        spinnerDays.addChangeListener(e -> refreshTable());

        return container;
    }


    private void loadCustomers() {
        try {
            displayedCustomers = CustomerDAO.getAllCustomers();
            refreshTable();
        } catch (Exception e) {
            showError("Unable to load customers.\n" + e.getMessage());
        }
    }

    private void searchCustomers() {
        String keyword = txtSearch.getText().trim();

        if (keyword.isEmpty()) {
            loadCustomers();
            return;
        }

        try {
            displayedCustomers = CustomerDAO.searchCustomers(keyword);
            refreshTable();
        } catch (Exception e) {
            showError("Search failed.\n" + e.getMessage());
        }
    }

    private void refreshTable() {
        customerModel.setRowCount(0);

        if (displayedCustomers == null) {
            return;
        }

        int thresholdDays = (Integer) spinnerDays.getValue();
        String filter = (String) cmbFilter.getSelectedItem();

        int shown = 0;
        for (Customer c : displayedCustomers) {
            boolean inactive = isInactive(c.getLastPurchaseDate(), thresholdDays);

            if ("Active Only".equals(filter) && inactive) continue;
            if ("Inactive Only".equals(filter) && !inactive) continue;

            customerModel.addRow(new Object[]{
                    c.getId(),
                    c.getName(),
                    c.getContact(),
                    (c.getAddress() == null || c.getAddress().isEmpty()) ? "-" : c.getAddress(),
                    formatLastPurchase(c.getLastPurchaseDate()),
                    inactive ? "Inactive" : "Active"
            });
            shown++;
        }

        if (shown == 0) {
            customerModel.addRow(new Object[]{"", "No customers found.", "", "", "", ""});
        }
    }

    private boolean isInactive(String lastPurchaseDate, int thresholdDays) {
        if (lastPurchaseDate == null || lastPurchaseDate.trim().isEmpty()
                || lastPurchaseDate.equalsIgnoreCase("N/A")) {
            return true;
        }
        try {
            LocalDate last = LocalDate.parse(lastPurchaseDate.trim());
            return last.isBefore(LocalDate.now().minusDays(thresholdDays));
        } catch (DateTimeParseException e) {
            return true;
        }
    }

    private void showPurchaseHistory() {
        int selectedRow = customerTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer first.",
                    "No Customer Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object idValue = customerModel.getValueAt(selectedRow, 0);
        if (!(idValue instanceof Integer)) {
            JOptionPane.showMessageDialog(this, "Please select a valid customer.",
                    "Invalid Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int customerId = (Integer) idValue;
        String customerName = customerModel.getValueAt(selectedRow, 1).toString();

        List<String[]> history = CustomerDAO.getPurchaseHistory(customerId);

        if (history.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No purchase history found for " + customerName + ".",
                    "Purchase History", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DefaultTableModel historyModel = new DefaultTableModel(
                new String[]{"Order No.", "Purchase Date", "Amount"}, 0) {
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
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setPreferredSize(new Dimension(500, 250));

        JOptionPane.showMessageDialog(this, scrollPane,
                "Purchase History - " + customerName, JOptionPane.INFORMATION_MESSAGE);
    }


    private boolean validateForm() {
        boolean valid = true;

        String name = txtFullName.getText().trim();
        if (name.isEmpty()) {
            setFieldError(txtFullName, lblFullNameError, "Full Name is required.");
            valid = false;
        } else if (!name.matches("[a-zA-Z .'-]+")) {
            setFieldError(txtFullName, lblFullNameError, "Letters only (no numbers or symbols).");
            valid = false;
        } else {
            clearFieldError(txtFullName, lblFullNameError);
        }

        String contact = txtContactNumber.getText().trim();
        if (contact.isEmpty()) {
            setFieldError(txtContactNumber, lblContactError, "Contact Number is required.");
            valid = false;
        } else if (!contact.matches("\\d{11}")) {
            setFieldError(txtContactNumber, lblContactError, "Must be exactly 11 digits.");
            valid = false;
        } else {
            clearFieldError(txtContactNumber, lblContactError);
        }

        String address = txtAddress.getText().trim();
        if (address.isEmpty()) {
            setFieldError(txtAddress, lblAddressError, "Address is required.");
            valid = false;
        } else {
            clearFieldError(txtAddress, lblAddressError);
        }

        return valid;
    }

    private void setFieldError(JTextField field, JLabel errorLabel, String message) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER_ERROR, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        errorLabel.setText(message);
    }

    private void clearFieldError(JTextField field, JLabel errorLabel) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        errorLabel.setText(" ");
    }


    private void saveCustomer() {
        if (!validateForm()) {
            return;
        }

        String name = txtFullName.getText().trim();
        String contact = txtContactNumber.getText().trim();
        String address = txtAddress.getText().trim();
        boolean isEdit = !txtCustomerId.getText().trim().isEmpty();

        try {
            if (isEdit) {
                int id = Integer.parseInt(txtCustomerId.getText().trim());
                Customer customer = new Customer(id, name, contact, address,
                        null /* last purchase untouched by DAO update */, txtDateRegistered.getText());
                CustomerDAO.updateCustomer(customer);
                JOptionPane.showMessageDialog(this, "Customer updated successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                String today = LocalDate.now().toString();
                Customer customer = new Customer(0, name, contact, address, "N/A", today);
                CustomerDAO.addCustomer(customer);
                JOptionPane.showMessageDialog(this, "Customer added successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }

            clearForm();
            loadCustomers();

        } catch (Exception e) {
            showError("Unable to save customer.\n" + e.getMessage());
        }
    }

    private void deleteCustomer() {
        int selectedRow = customerTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete.",
                    "No Customer Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object idValue = customerModel.getValueAt(selectedRow, 0);
        if (!(idValue instanceof Integer)) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this customer?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            CustomerDAO.deleteCustomer((Integer) idValue);
            JOptionPane.showMessageDialog(this, "Customer deleted successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadCustomers();
        } catch (Exception e) {
            showError("Unable to delete customer.\n" + e.getMessage());
        }
    }

    private void loadSelectedCustomer() {
        int row = customerTable.getSelectedRow();
        if (row == -1 || displayedCustomers == null) {
            return;
        }

        Object idValue = customerModel.getValueAt(row, 0);
        if (!(idValue instanceof Integer)) {
            return;
        }
        int id = (Integer) idValue;

        Customer match = null;
        for (Customer c : displayedCustomers) {
            if (c.getId() == id) {
                match = c;
                break;
            }
        }
        if (match == null) {
            return;
        }

        txtCustomerId.setText(String.valueOf(match.getId()));
        txtFullName.setText(match.getName());
        txtContactNumber.setText(match.getContact());
        txtAddress.setText(match.getAddress() == null ? "" : match.getAddress());
        txtDateRegistered.setText(
                (match.getDateRegistered() == null || match.getDateRegistered().isEmpty())
                        ? "Not available" : match.getDateRegistered());

        btnSave.setText("Update Customer");
        clearAllFieldErrors();
    }

    private void clearForm() {
        txtCustomerId.setText("");
        txtFullName.setText("");
        txtContactNumber.setText("");
        txtAddress.setText("");
        txtDateRegistered.setText("");
        btnSave.setText("Save Customer");
        clearAllFieldErrors();
        customerTable.clearSelection();
    }

    private void clearAllFieldErrors() {
        clearFieldError(txtFullName, lblFullNameError);
        clearFieldError(txtContactNumber, lblContactError);
        clearFieldError(txtAddress, lblAddressError);
    }

    private String formatLastPurchase(String date) {
        if (date == null || date.trim().isEmpty() || date.equalsIgnoreCase("N/A")) {
            return "No purchase yet";
        }
        return date;
    }


    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setForeground(new Color(70, 75, 80));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
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
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}