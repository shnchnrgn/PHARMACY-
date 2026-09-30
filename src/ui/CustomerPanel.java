package ui;

import db.CustomerDAO;
import models.Customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class CustomerPanel extends JPanel {

    private static final Color COLOR_GREEN = new Color(40, 167, 69);
    private static final Color COLOR_RED = new Color(220, 53, 69);
    private static final Color COLOR_BLUE = new Color(41, 128, 185);
    private static final Color COLOR_GRAY = new Color(108, 117, 125);
    private static final Color COLOR_BORDER = new Color(200, 205, 210);
    private static final Color COLOR_BORDER_ERROR = new Color(220, 53, 69);
    private static final Color COLOR_TEXT = new Color(60, 65, 70);
    private static final Color COLOR_ACTIVE = new Color(30, 130, 76);
    private static final Color COLOR_INACTIVE = new Color(193, 47, 47);

    private static final Color COLOR_TEAL = new Color(13, 148, 136);
    private static final Color COLOR_TEAL_HOVER = new Color(15, 118, 110);
    private static final Color COLOR_DIALOG_BG = new Color(248, 249, 251);
    private static final Color COLOR_DIALOG_LINE = new Color(225, 228, 232);

    private static final int DEFAULT_INACTIVE_DAYS = 90;
    private static final int LABEL_WIDTH = 130;

    private JTable customerTable;
    private DefaultTableModel customerModel;

    private JTextField txtCustomerId;
    private JTextField txtLastName;
    private JTextField txtFirstName;
    private JTextField txtMiddleName;
    private JTextField txtSuffix;
    private JTextField txtContactNumber;
    private JTextField txtAddress;
    private JTextField txtDateRegistered;
    private JTextField txtSearch;
    private JTextField txtPwdId;
    private JTextField txtSeniorCitizenId;
    private JComboBox<String> cmbDiscountType;
    private JTextField txtDiscountPercent;

    private JLabel lblLastNameError;
    private JLabel lblFirstNameError;
    private JLabel lblContactError;
    private JLabel lblAddressError;

    private JButton btnSave;

    private JComboBox<String> cmbFilter;
    private JSpinner spinnerDays;

    private List<Customer> displayedCustomers;

    public CustomerPanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("Customer Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(COLOR_TEXT);
        add(lblTitle, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);
        splitPane.setDividerSize(10);
        
        JPanel formPanel = buildFormPanel();
        JPanel tablePanel = buildTablePanel();

        splitPane.setLeftComponent(formPanel);
        splitPane.setRightComponent(tablePanel);
        
        SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(400));

        add(splitPane, BorderLayout.CENTER);

        loadCustomers();
    }

    // =====================================================================
    // FORM PANEL
    // =====================================================================

    private JPanel buildFormPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220)),
                new EmptyBorder(15, 15, 15, 15)));

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

        txtLastName = createStyledTextField();
        lblLastNameError = createErrorLabel();
        row = addFieldRow(fieldsPanel, gbc, row, "Last Name", true, txtLastName, lblLastNameError);

        txtFirstName = createStyledTextField();
        lblFirstNameError = createErrorLabel();
        row = addFieldRow(fieldsPanel, gbc, row, "First Name", true, txtFirstName, lblFirstNameError);

        txtMiddleName = createStyledTextField();
        row = addFieldRow(fieldsPanel, gbc, row, "Middle Name", false, txtMiddleName, null);

        txtSuffix = createStyledTextField();
        row = addFieldRow(fieldsPanel, gbc, row, "Suffix (Jr/Sr/III)", false, txtSuffix, null);

        txtContactNumber = createStyledTextField();
        lblContactError = createErrorLabel();
        row = addFieldRow(fieldsPanel, gbc, row, "Contact Number", true, txtContactNumber, lblContactError);

        txtAddress = createStyledTextField();
        lblAddressError = createErrorLabel();
        row = addFieldRow(fieldsPanel, gbc, row, "Address", true, txtAddress, lblAddressError);

        txtPwdId = createStyledTextField();
        row = addFieldRow(fieldsPanel, gbc, row, "PWD ID Number", false, txtPwdId, null);

        txtSeniorCitizenId = createStyledTextField();
        row = addFieldRow(fieldsPanel, gbc, row, "Senior Citizen ID", false, txtSeniorCitizenId, null);

        cmbDiscountType = new JComboBox<>(new String[]{
                "No Discount",
                "PWD",
                "Senior Citizen"
        });
        styleComboBox(cmbDiscountType);
        row = addFieldRow(fieldsPanel, gbc, row, "Discount Type", false, cmbDiscountType, null);

        txtDiscountPercent = createStyledTextField();
        txtDiscountPercent.setText("0");
        row = addFieldRow(fieldsPanel, gbc, row, "Discount (%)", false, txtDiscountPercent, null);

        txtDateRegistered = createStyledTextField();
        txtDateRegistered.setEditable(false);
        txtDateRegistered.setBackground(new Color(240, 240, 240));
        row = addFieldRow(fieldsPanel, gbc, row, "Date Registered", false, txtDateRegistered, null);

        cmbDiscountType.addActionListener(e -> {
            String type = String.valueOf(cmbDiscountType.getSelectedItem());

            if ("No Discount".equals(type)) {
                txtDiscountPercent.setText("0");
                txtDiscountPercent.setEnabled(false);
                txtPwdId.setEnabled(false);
                txtSeniorCitizenId.setEnabled(false);

            } else if ("PWD".equals(type)) {
                txtDiscountPercent.setText("20");
                txtDiscountPercent.setEnabled(true);
                txtPwdId.setEnabled(true);
                txtSeniorCitizenId.setEnabled(false);
                txtSeniorCitizenId.setText("");

            } else {
                txtDiscountPercent.setText("20");
                txtDiscountPercent.setEnabled(true);
                txtPwdId.setEnabled(false);
                txtPwdId.setText("");
                txtSeniorCitizenId.setEnabled(true);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 0, 0, 0);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonPanel.setOpaque(false);

        btnSave = createStyledButton("Save Customer", COLOR_TEAL);
        JButton btnDelete = createStyledButton("Delete Customer", COLOR_GRAY);
        JButton btnClear = createStyledButton(
        "Clear Form",
        new Color(180, 55, 55)
        );

        btnSave.addActionListener(e -> saveCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnClear.addActionListener(e -> clearForm());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        fieldsPanel.add(buttonPanel, gbc);

        JPanel formContent = new JPanel(new BorderLayout());
        formContent.setOpaque(false);
        formContent.add(fieldsPanel, BorderLayout.NORTH);

        JScrollPane formScroll = new JScrollPane(formContent);

        formScroll.setBorder(null);
        formScroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        formScroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );
        formScroll.setWheelScrollingEnabled(true);
        formScroll.getViewport().setBackground(Color.WHITE);

        JScrollBar verticalBar = formScroll.getVerticalScrollBar();
        verticalBar.setPreferredSize(new Dimension(7, 0));
        verticalBar.setUnitIncrement(16);
        verticalBar.setBlockIncrement(80);
        verticalBar.setUI(createCleanScrollBarUI());

        JScrollBar horizontalBar = formScroll.getHorizontalScrollBar();
        horizontalBar.setPreferredSize(new Dimension(0, 7));
        horizontalBar.setUnitIncrement(16);
        horizontalBar.setBlockIncrement(80);
        horizontalBar.setUI(createCleanScrollBarUI());

        container.add(formScroll, BorderLayout.CENTER);

        cmbDiscountType.setSelectedItem("No Discount");
        txtDiscountPercent.setEnabled(false);
        txtPwdId.setEnabled(false);
        txtSeniorCitizenId.setEnabled(false);

        return container;
    }

    private BasicScrollBarUI createCleanScrollBarUI() {
        return new BasicScrollBarUI() {
            @Override
            protected JButton createDecreaseButton(int orientation) { return createInvisibleButton(); }
            @Override
            protected JButton createIncreaseButton(int orientation) { return createInvisibleButton(); }
            private JButton createInvisibleButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setOpaque(false);
                button.setContentAreaFilled(false);
                button.setBorderPainted(false);
                return button;
            }
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(248, 249, 250));
                g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
                g2.dispose();
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty()) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(185, 190, 195));
                g2.fillRoundRect(thumbBounds.x + 1, thumbBounds.y + 1, Math.max(1, thumbBounds.width - 2), Math.max(1, thumbBounds.height - 2), 6, 6);
                g2.dispose();
            }
        };
    }

    private int addFieldRow(JPanel panel, GridBagConstraints gbc, int row,
                            String labelText, boolean required,
                            JComponent field, JLabel errorLabel) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(4, 0, 0, 8);

        JLabel label = new JLabel(required
                ? "<html>" + labelText + " <span style='color:#DC3545;'>*</span></html>"
                : labelText);

        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(COLOR_TEXT);
        label.setPreferredSize(new Dimension(LABEL_WIDTH, 30));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        field.setPreferredSize(new Dimension(100, 30));
        panel.add(field, gbc);

        row++;

        JLabel errLbl = errorLabel != null ? errorLabel : createErrorLabel();

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 8);
        panel.add(errLbl, gbc);

        return row + 1;
    }

    private JLabel createErrorLabel() {
        JLabel lbl = new JLabel(" ");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lbl.setForeground(COLOR_RED);
        return lbl;
    }

    private JPanel buildTablePanel() {
        JPanel container = new JPanel(new BorderLayout(0, 10));
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220)),
                new EmptyBorder(15, 15, 15, 15)));

        JPanel toolbar = new JPanel();
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.Y_AXIS));
        toolbar.setOpaque(false);

        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setOpaque(false);

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSearch.setForeground(COLOR_TEXT);

        txtSearch = createStyledTextField();

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchCustomers();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                searchCustomers();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                searchCustomers();
            }
        });

        JButton btnShowAll = createStyledButton("Show All", COLOR_GRAY);
        JButton btnHistory = createStyledButton("Purchase History", COLOR_TEAL);

        JPanel searchButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchButtons.setOpaque(false);
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

        cmbFilter = new JComboBox<>(new String[]{
                "-- Select Filter --",
                "All Customers",
                "Active Only",
                "Inactive Only"
        });

        styleComboBox(cmbFilter);

        JLabel lblDays = new JLabel("Inactive after (days):");
        lblDays.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDays.setForeground(COLOR_TEXT);

        spinnerDays = new JSpinner(
                new SpinnerNumberModel(DEFAULT_INACTIVE_DAYS, 1, 3650, 1)
        );

        spinnerDays.setPreferredSize(new Dimension(70, 26));

        filterRow.add(lblFilter);
        filterRow.add(cmbFilter);
        filterRow.add(lblDays);
        filterRow.add(spinnerDays);

        toolbar.add(searchRow);
        toolbar.add(filterRow);
        container.add(toolbar, BorderLayout.NORTH);

        String[] columns = {
                "ID",
                "Last Name",
                "First Name",
                "Contact Number",
                "ID Number",
                "Discount Type",
                "Last Purchase",
                "Status"
        };

        customerModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customerTable = new JTable(customerModel);

        customerTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        customerTable.setRowHeight(32);
        customerTable.setShowVerticalLines(false);
        customerTable.setShowHorizontalLines(true);
        customerTable.setGridColor(new Color(235, 238, 242));
        customerTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        // --- CUSTOM GRAY SELECTION BACKGROUND PARA SA CUSTOMER TABLE ---
        customerTable.setSelectionBackground(new Color(210, 215, 220));
        customerTable.setSelectionForeground(Color.BLACK);

        customerTable.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 11)
        );

        customerTable.getTableHeader().setBackground(
                new Color(248, 249, 250)
        );

        customerTable.getTableHeader().setForeground(
                new Color(80, 85, 90)
        );

        int[] widths = {
                40,   // ID
                110,  // Last Name
                110,  // First Name
                125,  // Contact Number
                115,  // ID Number
                115,  // Discount Type
                105,  // Last Purchase
                85    // Status
        };

        for (int i = 0; i < widths.length; i++) {
            customerTable.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(widths[i]);
        }

        customerTable.getColumnModel().getColumn(7)
                .setCellRenderer(new DefaultTableCellRenderer() {

                    private Color badgeColor;
                    private Color rowColor = Color.WHITE;

                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table,
                            Object value,
                            boolean isSelected,
                            boolean hasFocus,
                            int rowIdx,
                            int col) {

                        super.getTableCellRendererComponent(
                                table,
                                value,
                                isSelected,
                                hasFocus,
                                rowIdx,
                                col
                        );

                        setOpaque(false);
                        setBorder(BorderFactory.createEmptyBorder());
                        setHorizontalAlignment(CENTER);
                        setFont(new Font("Segoe UI", Font.BOLD, 11));

                        rowColor = isSelected
                                ? table.getSelectionBackground()
                                : Color.WHITE;

                        if ("Active".equals(value)) {
                            badgeColor = COLOR_TEAL;
                            setForeground(Color.WHITE);

                        } else if ("Inactive".equals(value)) {
                            badgeColor = COLOR_INACTIVE;
                            setForeground(Color.WHITE);

                        } else {
                            badgeColor = null;
                            setForeground(
                                    isSelected
                                            ? table.getSelectionForeground()
                                            : COLOR_TEXT
                            );
                        }

                        return this;
                    }

                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();

                        g2.setRenderingHint(
                                RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON
                        );

                        g2.setColor(rowColor);
                        g2.fillRect(0, 0, getWidth(), getHeight());

                        if (badgeColor != null) {
                            int w = Math.min(getWidth() - 12, 60);
                            int h = 22;
                            int x = (getWidth() - w) / 2;
                            int y = (getHeight() - h) / 2;

                            g2.setColor(badgeColor);
                            g2.fillRoundRect(x, y, w, h, 8, 8);
                        }

                        g2.dispose();
                        super.paintComponent(g);
                    }
                });

        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        new Color(220, 224, 230)
                )
        );

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JScrollBar tableScrollBar = scrollPane.getVerticalScrollBar();
        tableScrollBar.setPreferredSize(new Dimension(7, 0));
        tableScrollBar.setUnitIncrement(16);
        tableScrollBar.setBlockIncrement(80);
        tableScrollBar.setUI(createCleanScrollBarUI());

        scrollPane.setWheelScrollingEnabled(true);

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

            String discountType = c.getDiscountType();
            String idNumber = "-";
            if (discountType != null) {
                if (discountType.equalsIgnoreCase("PWD")) {
                    idNumber = (c.getPwdId() != null && !c.getPwdId().trim().isEmpty()) ? c.getPwdId() : "-";
                } else if (discountType.equalsIgnoreCase("Senior Citizen")) {
                    idNumber = (c.getSeniorCitizenId() != null && !c.getSeniorCitizenId().trim().isEmpty()) ? c.getSeniorCitizenId() : "-";
                }
            }

            String formattedDiscountType = discountType;
            if (formattedDiscountType == null || formattedDiscountType.trim().isEmpty() || formattedDiscountType.equalsIgnoreCase("No Discount")) {
                formattedDiscountType = "None";
            } else {
                formattedDiscountType = formattedDiscountType + " " + formatDiscountPercent(c.getDiscountPercent());
            }

            customerModel.addRow(new Object[]{
                    c.getId(),
                    c.getLastName(),
                    c.getFirstName(),
                    c.getContact(),
                    idNumber,
                    formattedDiscountType,
                    formatLastPurchase(c.getLastPurchaseDate()),
                    inactive ? "Inactive" : "Active"
            });

            shown++;
        }

        if (shown == 0) {
            customerModel.addRow(new Object[]{"", "No customers found.", "", "", "", "", "", ""});
        }
    }

    private String formatDiscountPercent(double percent) {
        if (percent <= 0) return "";
        if (percent == Math.rint(percent)) {
            return String.format("(%d%%)", (int) percent);
        }
        return String.format("(%.1f%%)", percent);
    }

    private boolean isInactive(String lastPurchaseDate, int thresholdDays) {
        if (lastPurchaseDate == null || lastPurchaseDate.trim().isEmpty() || lastPurchaseDate.equalsIgnoreCase("N/A")) {
            return true;
        }
        try {
            LocalDate last = LocalDate.parse(lastPurchaseDate.trim());
            return last.isBefore(LocalDate.now().minusDays(thresholdDays));
        } catch (DateTimeParseException e) {
            return true;
        }
    }

    private String formatLastPurchase(String date) {
        if (date == null || date.trim().isEmpty() || date.equalsIgnoreCase("N/A")) {
            return "No purchase yet";
        }
        return date;
    }

    private void showPurchaseHistory() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            showMessage("Purchase History", "Purchase History", "Please select a customer first.");
            return;
        }

        Object idValue = customerModel.getValueAt(selectedRow, 0);
        if (!(idValue instanceof Integer)) {
            showMessage("Purchase History", "Purchase History", "Please select a valid customer.");
            return;
        }

        int customerId = (Integer) idValue;
        String customerName = customerModel.getValueAt(selectedRow, 1).toString() + ", " + customerModel.getValueAt(selectedRow, 2).toString();

        List<String[]> history = CustomerDAO.getPurchaseHistory(customerId);
        if (history.isEmpty()) {
            showMessage("Purchase History", customerName + " - Orders", "No purchase history found for " + customerName + ".");
            return;
        }

        DefaultTableModel historyModel = new DefaultTableModel(new String[]{"Order No.", "Purchase Date", "Amount"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        for (String[] purchase : history) {
            historyModel.addRow(purchase);
        }

        JTable historyTable = new JTable(historyModel);
        historyTable.setRowHeight(28);
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        historyTable.setShowVerticalLines(false);
        historyTable.setGridColor(new Color(235, 238, 242));
        
        // --- GRAY SELECTION PARA SA PURCHASE HISTORY TABLE ---
        historyTable.setSelectionBackground(new Color(210, 215, 220));
        historyTable.setSelectionForeground(Color.BLACK);

        historyTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        historyTable.getTableHeader().setBackground(COLOR_DIALOG_BG);
        historyTable.getTableHeader().setForeground(COLOR_TEXT);

        JScrollPane scroll = new JScrollPane(historyTable);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_DIALOG_LINE));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setPreferredSize(new Dimension(500, Math.min(history.size() * 28 + 30, 220)));
        
        // --- CLEAN SCROLLBAR PARA SA PURCHASE HISTORY DIALOG ---
        JScrollBar verticalBar = scroll.getVerticalScrollBar();
        verticalBar.setPreferredSize(new Dimension(7, 0));
        verticalBar.setUnitIncrement(16);
        verticalBar.setBlockIncrement(80);
        verticalBar.setUI(createCleanScrollBarUI());

        scroll.setWheelScrollingEnabled(true);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(16, 16, 16, 16));
        body.add(scroll, BorderLayout.CENTER);

        JDialog dialog = createDialog("Purchase History");
        JButton btnOk = createTealButton("OK");
        btnOk.addActionListener(e -> dialog.dispose());

        showDialog(dialog, buildDialogHeader(customerName + " - Orders"), body, buildDialogFooter(btnOk), btnOk);
    }

    private void showMessage(String title, String header, String message) {
        openMessageDialog(title, header, message, false);
    }

    private void showSuccess(String message) {
        openMessageDialog("Success", "Customer Management", message, true);
    }

    private void openMessageDialog(String title, String header, String message, boolean success) {
        JDialog dialog = createDialog(title);
        JButton btnOk = createTealButton("OK");
        btnOk.addActionListener(e -> dialog.dispose());
        showDialog(dialog, buildDialogHeader(header), buildMessageBody(message, success), buildDialogFooter(btnOk), btnOk);
    }

    private boolean showConfirm(String title, String header, String message, String confirmText) {
        JDialog dialog = createDialog(title);
        boolean[] result = {false};

        JButton btnConfirm = createTealButton(confirmText);
        btnConfirm.addActionListener(e -> {
            result[0] = true;
            dialog.dispose();
        });

        JButton btnCancel = createGrayOutlineButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());

        showDialog(dialog, buildDialogHeader(header), buildMessageBody(message, false), buildDialogFooter(btnCancel, btnConfirm), btnConfirm);
        return result[0];
    }

    private JDialog createDialog(String title) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        return new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
    }

    private void showDialog(JDialog dialog, JPanel header, JPanel body, JPanel footer, JButton defaultButton) {
        dialog.setLayout(new BorderLayout());
        dialog.add(header, BorderLayout.NORTH);
        dialog.add(body, BorderLayout.CENTER);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.getRootPane().setDefaultButton(defaultButton);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(420, 0));
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JPanel buildDialogHeader(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(COLOR_TEXT);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_DIALOG_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_DIALOG_LINE),
                new EmptyBorder(12, 16, 12, 16)));
        header.add(lbl, BorderLayout.WEST);
        return header;
    }

    private JPanel buildMessageBody(String message, boolean success) {
        String html = "<html><div style='width:280px;'>" + message.replace("\n", "<br>") + "</div></html>";
        JLabel lblMessage = new JLabel(html);
        lblMessage.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMessage.setForeground(COLOR_TEXT);

        JComponent icon = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_TEAL);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(Color.WHITE);
                if (success) {
                    g2.setStroke(new BasicStroke(2.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawPolyline(new int[]{9, 14, 23}, new int[]{17, 22, 11}, 3);
                } else {
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth("!")) / 2;
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString("!", x, y);
                }
                g2.dispose();
            }
        };
        icon.setPreferredSize(new Dimension(32, 32));

        JPanel iconWrap = new JPanel(new BorderLayout());
        iconWrap.setOpaque(false);
        iconWrap.add(icon, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(14, 0));
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(24, 20, 24, 20));
        body.add(iconWrap, BorderLayout.WEST);
        body.add(lblMessage, BorderLayout.CENTER);
        return body;
    }

    private JPanel buildDialogFooter(JButton... buttons) {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        footer.setBackground(COLOR_DIALOG_BG);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_DIALOG_LINE),
                new EmptyBorder(10, 16, 10, 8)));
        for (JButton b : buttons) {
            footer.add(b);
        }
        return footer;
    }

    private JButton createTealButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(COLOR_TEAL);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 26, 8, 26));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(COLOR_TEAL_HOVER); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(COLOR_TEAL); }
        });
        return btn;
    }

    private JButton createGrayOutlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(Color.WHITE);
        btn.setForeground(COLOR_TEXT);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                new EmptyBorder(7, 25, 7, 25)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(240, 242, 245)); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(Color.WHITE); }
        });
        return btn;
    }

    private void showError(String message) {
        showMessage("Error", "Error", message);
    }

    private boolean validateForm() {
        boolean valid = true;

        String lastName = txtLastName.getText().trim();
        if (lastName.isEmpty()) {
            setFieldError(txtLastName, lblLastNameError, "Last Name is required.");
            valid = false;
        } else if (!lastName.matches("[a-zA-Z .'-]+")) {
            setFieldError(txtLastName, lblLastNameError, "Letters only.");
            valid = false;
        } else {
            clearFieldError(txtLastName, lblLastNameError);
        }

        String firstName = txtFirstName.getText().trim();
        if (firstName.isEmpty()) {
            setFieldError(txtFirstName, lblFirstNameError, "First Name is required.");
            valid = false;
        } else if (!firstName.matches("[a-zA-Z .'-]+")) {
            setFieldError(txtFirstName, lblFirstNameError, "Letters only.");
            valid = false;
        } else {
            clearFieldError(txtFirstName, lblFirstNameError);
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

        String discountType = String.valueOf(cmbDiscountType.getSelectedItem());
        String discountText = txtDiscountPercent.getText().trim();

        if ("PWD".equals(discountType) && txtPwdId.getText().trim().isEmpty()) {
            showMessage("Discount Details", "PWD Discount", "Please enter the PWD ID Number.");
            valid = false;
        }

        if ("Senior Citizen".equals(discountType) && txtSeniorCitizenId.getText().trim().isEmpty()) {
            showMessage("Discount Details", "Senior Citizen Discount", "Please enter the Senior Citizen ID.");
            valid = false;
        }

        try {
            double discount = discountText.isEmpty() ? 0 : Double.parseDouble(discountText);
            if (discount < 0 || discount > 100) {
                showMessage("Discount Details", "Invalid Discount", "Discount must be between 0 and 100.");
                valid = false;
            }
        } catch (NumberFormatException e) {
            showMessage("Discount Details", "Invalid Discount", "Please enter a valid discount percentage.");
            valid = false;
        }

        return valid;
    }

    private void setFieldError(JTextField field, JLabel errorLabel, String message) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER_ERROR, 1),
                BorderFactory.createEmptyBorder(5, 7, 5, 7)));
        errorLabel.setText(message);
    }

    private void clearFieldError(JTextField field, JLabel errorLabel) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(5, 7, 5, 7)));
        errorLabel.setText(" ");
    }

    private void clearAllFieldErrors() {
        clearFieldError(txtLastName, lblLastNameError);
        clearFieldError(txtFirstName, lblFirstNameError);
        clearFieldError(txtContactNumber, lblContactError);
        clearFieldError(txtAddress, lblAddressError);
    }

    private void saveCustomer() {
        if (!validateForm()) return;

        String lastName = txtLastName.getText().trim();
        String firstName = txtFirstName.getText().trim();
        String middleName = txtMiddleName.getText().trim();
        String suffix = txtSuffix.getText().trim();

        String contact = txtContactNumber.getText().trim();
        String address = txtAddress.getText().trim();
        String pwdId = txtPwdId.getText().trim();
        String seniorCitizenId = txtSeniorCitizenId.getText().trim();
        String discountType = String.valueOf(cmbDiscountType.getSelectedItem());
        double discountPercent = Double.parseDouble(txtDiscountPercent.getText().trim().isEmpty() ? "0" : txtDiscountPercent.getText().trim());

        boolean isEdit = !txtCustomerId.getText().trim().isEmpty();

        try {
            if (isEdit) {
                int id = Integer.parseInt(txtCustomerId.getText().trim());
                String dateRegistered = txtDateRegistered.getText().trim();
                if (dateRegistered.isEmpty() || dateRegistered.equalsIgnoreCase("Not available")) {
                    dateRegistered = LocalDate.now().toString();
                }

                Customer customer = new Customer(id, lastName, firstName, contact, address, null, dateRegistered, pwdId, seniorCitizenId, discountType, discountPercent);
                CustomerDAO.updateCustomer(customer);
                showSuccess("Customer updated successfully.");
            } else {
                String today = LocalDate.now().toString();
                Customer customer = new Customer(0, lastName, firstName, contact, address, "N/A", today, pwdId, seniorCitizenId, discountType, discountPercent);
                CustomerDAO.addCustomer(customer);
                showSuccess("Customer added successfully.");
            }

            clearForm();
            loadCustomers();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to save customer.\n" + e.getMessage());
        }
    }

    private void deleteCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            showMessage("No Customer Selected", "Customer Management", "Please select a customer to delete.");
            return;
        }

        Object idValue = customerModel.getValueAt(selectedRow, 0);
        if (!(idValue instanceof Integer)) return;

        boolean confirmed = showConfirm("Confirm Delete", "Delete Customer", "Are you sure you want to delete this customer?", "Delete");
        if (!confirmed) return;

        try {
            CustomerDAO.deleteCustomer((Integer) idValue);
            showSuccess("Customer deleted successfully.");
            clearForm();
            loadCustomers();
        } catch (Exception e) {
            showError("Unable to delete customer.\n" + e.getMessage());
        }
    }

    private void loadSelectedCustomer() {
        int row = customerTable.getSelectedRow();
        if (row == -1 || displayedCustomers == null) return;

        Object idValue = customerModel.getValueAt(row, 0);
        if (!(idValue instanceof Integer)) return;

        int id = (Integer) idValue;
        Customer match = null;
        for (Customer c : displayedCustomers) {
            if (c.getId() == id) {
                match = c;
                break;
            }
        }

        if (match == null) return;

        txtCustomerId.setText(String.valueOf(match.getId()));
        txtLastName.setText(match.getLastName() == null ? "" : match.getLastName());
        txtFirstName.setText(match.getFirstName() == null ? "" : match.getFirstName());
        txtMiddleName.setText("");
        txtSuffix.setText("");
        txtContactNumber.setText(match.getContact());
        txtAddress.setText(match.getAddress() == null ? "" : match.getAddress());
        txtPwdId.setText(match.getPwdId() == null ? "" : match.getPwdId());
        txtSeniorCitizenId.setText(match.getSeniorCitizenId() == null ? "" : match.getSeniorCitizenId());
        cmbDiscountType.setSelectedItem(match.getDiscountType() == null || match.getDiscountType().trim().isEmpty() ? "No Discount" : match.getDiscountType());
        txtDiscountPercent.setText(String.valueOf(match.getDiscountPercent()));

        String registered = match.getDateRegistered();
        txtDateRegistered.setText(registered == null || registered.isEmpty() ? "Not available" : registered);

        btnSave.setText("Update Customer");
        clearAllFieldErrors();
    }

    private void clearForm() {
        txtCustomerId.setText("");
        txtLastName.setText("");
        txtFirstName.setText("");
        txtMiddleName.setText("");
        txtSuffix.setText("");
        txtContactNumber.setText("");
        txtAddress.setText("");
        txtPwdId.setText("");
        txtSeniorCitizenId.setText("");
        cmbDiscountType.setSelectedItem("No Discount");
        txtDiscountPercent.setText("0");
        txtDateRegistered.setText("");

        btnSave.setText("Save Customer");
        clearAllFieldErrors();
        customerTable.clearSelection();
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setForeground(new Color(70, 75, 80));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(5, 7, 5, 7)));
        return tf;
    }

    private JButton createStyledButton(String text, Color bgCol) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBackground(bgCol);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleComboBox(JComboBox<String> combo) {
        final Color textColor = new Color(51, 51, 51);
        final Color lineColor = new Color(220, 220, 220);
        final Color selColor = new Color(207, 212, 216);

        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBackground(Color.WHITE);
        combo.setForeground(textColor);
        combo.setFocusable(false);
        combo.setPreferredSize(new Dimension(200, 30));
        combo.setMaximumRowCount(10);
        combo.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));

        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton b = new JButton() {
                    @Override
                    public void paint(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(Color.WHITE);
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        g2.setColor(lineColor);
                        g2.drawLine(0, 0, 0, getHeight());
                        int cx = getWidth() / 2 + 1;
                        int cy = getHeight() / 2;
                        g2.setColor(textColor);
                        g2.fillPolygon(new int[]{cx - 4, cx + 4, cx}, new int[]{cy - 2, cy - 2, cy + 3}, 3);
                        g2.dispose();
                    }
                };
                b.setPreferredSize(new Dimension(26, 0));
                b.setBorder(BorderFactory.createEmptyBorder());
                b.setFocusable(false);
                b.setContentAreaFilled(false);
                return b;
            }

            @Override
            protected ComboPopup createPopup() {
                return new BasicComboPopup(comboBox) {
                    @Override
                    protected void configurePopup() {
                        super.configurePopup();
                        setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
                    }
                };
            }
        });

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                lbl.setForeground(textColor);
                lbl.setOpaque(true);

                if (index < 0) {
                    lbl.setBackground(Color.WHITE);
                    lbl.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                } else {
                    lbl.setBackground(isSelected ? selColor : Color.WHITE);
                    lbl.setBorder(BorderFactory.createCompoundBorder(
                            index > 0 ? BorderFactory.createMatteBorder(1, 0, 0, 0, lineColor) : BorderFactory.createEmptyBorder(),
                            BorderFactory.createEmptyBorder(0, 6, 0, 6)
                    ));
                    lbl.setPreferredSize(new Dimension(lbl.getPreferredSize().width, 29));
                }
                return lbl;
            }
        });
    }
}