package ui;

import db.CustomerDAO;
import db.MedicineDAO;
import db.SalesDAO;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import models.Customer;
import models.Medicine;

public class POSFrame extends JPanel {

    private JTable medicineTable;
    private JTable cartTable;
    private DefaultTableModel medicineModel;
    private DefaultTableModel cartModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> medicineRowSorter;

    private JLabel lblSubtotalVal;
    private JLabel lblDiscountTitle;
    private JLabel lblDiscountVal;
    private JLabel lblTotalVal;

    private Customer activeCheckoutCustomer = null;

    public POSFrame() {

        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Point of Sales (POS)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(60, 65, 70));

        add(lblTitle, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JPanel leftPanel = buildMedicinePanel();
        JPanel rightPanel = buildCartPanel();

        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);

        add(contentPanel, BorderLayout.CENTER);

        loadMedicinesToPOS();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadMedicinesToPOS();
            }
        });
    }

    private JPanel buildMedicinePanel() {

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220)),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel lblSearch = new JLabel("Search Medicine:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSearch.setForeground(new Color(70, 75, 80));

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210)),
                new EmptyBorder(6, 8, 6, 8)
        ));

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }

            private void filterTable() {
                String text = searchField.getText().trim();
                if (text.isEmpty()) {
                    medicineRowSorter.setRowFilter(null);
                } else {
                    medicineRowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        leftPanel.add(searchPanel, BorderLayout.NORTH);

        String[] medColumns = {
                "Medicine Name",
                "Category",
                "Price",
                "Stock",
                "Status",
                "ID"
        };

        medicineModel = new DefaultTableModel(medColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        medicineTable = new JTable(medicineModel);
        
        medicineTable.setSelectionBackground(new Color(210, 215, 220));
        medicineTable.setSelectionForeground(Color.BLACK);

        medicineRowSorter = new TableRowSorter<>(medicineModel);
        medicineTable.setRowSorter(medicineRowSorter);

        medicineTable.setRowHeight(32);
        medicineTable.setShowVerticalLines(false);
        medicineTable.setShowHorizontalLines(true);
        medicineTable.setGridColor(new Color(235, 238, 242));
        medicineTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        medicineTable.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 12)
        );
        medicineTable.getTableHeader().setBackground(
                new Color(248, 249, 250)
        );
        medicineTable.getTableHeader().setForeground(
                new Color(80, 85, 90)
        );
        medicineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(column == 3 || column == 4 ? JLabel.CENTER : JLabel.LEFT);
                
                int modelRow = table.convertRowIndexToModel(row);
                try {
                    String status = table.getModel().getValueAt(modelRow, 4).toString();
                    
                    if ("Out of Stock".equalsIgnoreCase(status) || "Expired".equalsIgnoreCase(status)) {
                        c.setForeground(new Color(192, 57, 43));
                        setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } else {
                        c.setForeground(new Color(70, 75, 80));
                        setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    }
                } catch (Exception ignored) {}

                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        };

        for (int i = 0; i < 5; i++) {
            medicineTable.getColumnModel().getColumn(i).setCellRenderer(statusRenderer);
        }

        medicineTable.getColumnModel().getColumn(5).setMinWidth(0);
        medicineTable.getColumnModel().getColumn(5).setMaxWidth(0);
        medicineTable.getColumnModel().getColumn(5).setPreferredWidth(0);

        JScrollPane medScroll = new JScrollPane(medicineTable);
        medScroll.getViewport().setBackground(Color.WHITE);
        medScroll.setBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230))
        );

        leftPanel.add(medScroll, BorderLayout.CENTER);

        JButton btnAddToCart = createButton(
                "Add to Cart",
                new Color(41, 128, 185),
                Color.WHITE
        );

        btnAddToCart.setBorder(
                new EmptyBorder(10, 0, 10, 0)
        );

        JPanel btnAddWrapper = new JPanel(new BorderLayout());
        btnAddWrapper.setOpaque(false);
        btnAddWrapper.setBorder(new EmptyBorder(10, 0, 0, 0));
        btnAddWrapper.add(btnAddToCart, BorderLayout.CENTER);

        leftPanel.add(btnAddWrapper, BorderLayout.SOUTH);

        medicineTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    addSelectedMedicineToCart();
                }
            }
        });

        btnAddToCart.addActionListener(e ->
                addSelectedMedicineToCart()
        );

        return leftPanel;
    }

    private JPanel buildCartPanel() {

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220)),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel lblCartTitle = new JLabel("Current Cart & Checkout");
        lblCartTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCartTitle.setForeground(new Color(70, 75, 80));
        lblCartTitle.setBorder(new EmptyBorder(0, 0, 10, 0));

        rightPanel.add(lblCartTitle, BorderLayout.NORTH);

        String[] cartColumns = {
                "Item Name",
                "Price",
                "Qty",
                "Total",
                "Medicine ID"
        };

        cartModel = new DefaultTableModel(
                new Object[][]{},
                cartColumns
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        cartTable = new JTable(cartModel);
        
        cartTable.setSelectionBackground(new Color(210, 215, 220));
        cartTable.setSelectionForeground(Color.BLACK);

        cartTable.setRowHeight(32);
        cartTable.setShowVerticalLines(false);
        cartTable.setShowHorizontalLines(true);
        cartTable.setGridColor(new Color(235, 238, 242));
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cartTable.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 12)
        );
        cartTable.getTableHeader().setBackground(
                new Color(248, 249, 250)
        );
        cartTable.getTableHeader().setForeground(
                new Color(80, 85, 90)
        );
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        cartTable.getColumnModel().getColumn(4).setMinWidth(0);
        cartTable.getColumnModel().getColumn(4).setMaxWidth(0);
        cartTable.getColumnModel().getColumn(4).setPreferredWidth(0);

        JScrollPane cartScroll = new JScrollPane(cartTable);
        cartScroll.getViewport().setBackground(Color.WHITE);
        cartScroll.setBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230))
        );

        rightPanel.add(cartScroll, BorderLayout.CENTER);

        JPanel bottomCartPanel = new JPanel(new BorderLayout());
        bottomCartPanel.setOpaque(false);
        bottomCartPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel summaryWrap = buildSummaryPanel();

        bottomCartPanel.add(summaryWrap, BorderLayout.NORTH);

        JPanel actionButtons = new JPanel(new GridLayout(1, 2, 10, 0));
        actionButtons.setOpaque(false);

        JButton btnRemove = createButton(
                "Remove Item",
                new Color(192, 57, 43),
                Color.WHITE
        );

        JButton btnCheckout = createButton(
                "Checkout",
                new Color(26, 143, 136),
                Color.WHITE
        );

        btnRemove.setBorder(new EmptyBorder(10, 0, 10, 0));
        btnCheckout.setBorder(new EmptyBorder(10, 0, 10, 0));

        actionButtons.add(btnRemove);
        actionButtons.add(btnCheckout);

        bottomCartPanel.add(actionButtons, BorderLayout.SOUTH);

        rightPanel.add(bottomCartPanel, BorderLayout.SOUTH);

        btnRemove.addActionListener(e ->
                removeSelectedCartItem()
        );

        btnCheckout.addActionListener(e ->
                processCheckout()
        );

        cartTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    removeSelectedCartItem();
                }
            }
        });

        return rightPanel;
    }

    private JPanel buildSummaryPanel() {

        final Color textColor = new Color(51, 51, 51);
        final Color lineColor = new Color(220, 220, 220);

        lblSubtotalVal = createSummaryValue(
                "₱ 0.00",
                textColor
        );

        lblDiscountTitle = createSummaryLabel("Discount (0%):");
        lblDiscountVal = createSummaryValue(
                "- ₱ 0.00",
                textColor
        );

        lblTotalVal = createSummaryValue(
                "₱ 0.00",
                textColor
        );

        lblTotalVal.setFont(
                new Font("Segoe UI", Font.BOLD, 16)
        );

        JPanel summaryPanel = new JPanel(
                new GridLayout(0, 2, 5, 6)
        );
        summaryPanel.setOpaque(false);

        summaryPanel.add(createSummaryLabel("Subtotal:"));
        summaryPanel.add(lblSubtotalVal);

        summaryPanel.add(lblDiscountTitle);
        summaryPanel.add(lblDiscountVal);

        JLabel lblTotalText = createSummaryLabel("TOTAL:");
        lblTotalText.setFont(
                new Font("Segoe UI", Font.BOLD, 15)
        );
        lblTotalText.setForeground(textColor);

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        1, 0, 0, 0, lineColor
                ),
                new EmptyBorder(8, 0, 0, 0)
        ));

        totalRow.add(lblTotalText, BorderLayout.WEST);
        totalRow.add(lblTotalVal, BorderLayout.EAST);

        JPanel summaryBox = new JPanel(
                new BorderLayout(0, 8)
        );
        summaryBox.setOpaque(false);
        summaryBox.setBorder(
                new EmptyBorder(5, 0, 10, 0)
        );

        summaryBox.add(
                summaryPanel,
                BorderLayout.CENTER
        );

        summaryBox.add(
                totalRow,
                BorderLayout.SOUTH
        );

        JPanel summaryWrap = new JPanel(
                new BorderLayout()
        );
        summaryWrap.setOpaque(false);

        summaryWrap.add(
                summaryBox,
                BorderLayout.CENTER
        );

        return summaryWrap;
    }

    private void addSelectedMedicineToCart() {

        int selectedRow = medicineTable.getSelectedRow();

        if (selectedRow == -1) {
            CustomDialog.showMessage(
                    this,
                    "Please select a medicine from the list first.",
                    "Selection Error",
                    true
            );
            return;
        }

        int modelRow = medicineTable.convertRowIndexToModel(selectedRow);

        try {

            String name = medicineModel
                    .getValueAt(modelRow, 0)
                    .toString();

            String priceText = medicineModel
                    .getValueAt(modelRow, 2)
                    .toString();

            int availableStock = Integer.parseInt(
                    medicineModel
                            .getValueAt(modelRow, 3)
                            .toString()
            );

            String status = medicineModel
                    .getValueAt(modelRow, 4)
                    .toString();

            int medicineId = Integer.parseInt(
                    medicineModel
                            .getValueAt(modelRow, 5)
                            .toString()
            );

            if ("Out of Stock".equalsIgnoreCase(status)) {
                CustomDialog.showMessage(
                        this,
                        "This medicine is out of stock and cannot be added to cart.",
                        "Stock Error",
                        true
                );
                return;
            }

            if ("Expired".equalsIgnoreCase(status)) {
                CustomDialog.showMessage(
                        this,
                        "This medicine is expired and cannot be sold.",
                        "Expired Error",
                        true
                );
                return;
            }

            double price = parseMoney(priceText);

            int existingRow = findCartMedicineRow(medicineId);

            if (existingRow != -1) {

                int currentQty = Integer.parseInt(
                        cartModel
                                .getValueAt(existingRow, 2)
                                .toString()
                );

                if (currentQty + 1 > availableStock) {
                    CustomDialog.showMessage(
                            this,
                            "Insufficient stock available for this medicine.",
                            "Stock Error",
                            true
                    );
                    return;
                }

                int newQty = currentQty + 1;

                cartModel.setValueAt(
                        String.valueOf(newQty),
                        existingRow,
                        2
                );

                cartModel.setValueAt(
                        String.format("₱%.2f", price * newQty),
                        existingRow,
                        3
                );

            } else {

                cartModel.addRow(new Object[]{
                        name,
                        String.format("₱%.2f", price),
                        "1",
                        String.format("₱%.2f", price),
                        medicineId
                });
            }

            updateSubtotal();

        } catch (Exception e) {

            e.printStackTrace();

            CustomDialog.showMessage(
                    this,
                    "Unable to add medicine to cart.\n" + e.getMessage(),
                    "Error",
                    true
            );
        }
    }

    private int findCartMedicineRow(int medicineId) {

        for (int i = 0; i < cartModel.getRowCount(); i++) {

            int existingId = Integer.parseInt(
                    cartModel.getValueAt(i, 4).toString()
            );

            if (existingId == medicineId) {
                return i;
            }
        }

        return -1;
    }

    private void removeSelectedCartItem() {

        int selectedRow = cartTable.getSelectedRow();

        if (selectedRow == -1) {
            CustomDialog.showMessage(
                    this,
                    "Please select an item from the cart to remove.",
                    "Selection Error",
                    true
            );
            return;
        }

        cartModel.removeRow(selectedRow);
        updateSubtotal();
    }

    private void processCheckout() {

        if (cartModel.getRowCount() == 0) {
            CustomDialog.showMessage(
                    this,
                    "The cart is empty.",
                    "Cart Error",
                    true
            );
            return;
        }

        try {
            String[] customerInputs = CustomDialog.showCustomerNameInput(this);

            if (customerInputs == null) {
                return;
            }

            String firstName = customerInputs[0].trim();
            String lastName = customerInputs[1].trim();

            if (firstName.isEmpty() || lastName.isEmpty()) {
                CustomDialog.showMessage(
                        this,
                        "Both First Name and Last Name are required.",
                        "Validation Error",
                        true
                );
                return;
            }

            int customerId =
                    CustomerDAO.getCustomerIdByNameIgnoreCase(
                            firstName,
                            lastName
                    );

            if (customerId == -1) {
                CustomDialog.showMessage(
                        this,
                        "Customer not found.\n\n" +
                                "Please register the customer first " +
                                "in Customer Management.",
                        "Customer Not Found",
                        true
                );
                return;
            }

            List<Customer> allCustomers = CustomerDAO.getAllCustomers();
            Customer foundCustomer = null;
            for (Customer c : allCustomers) {
                if (c.getId() == customerId) {
                    foundCustomer = c;
                    break;
                }
            }

            activeCheckoutCustomer = foundCustomer;
            updateSubtotal();

            boolean isPwd = false;
            String pwdId = "";
            String discountType = foundCustomer != null ? foundCustomer.getDiscountType() : "No Discount";
            String idNumberInfo = "";

            if (discountType != null) {
                if (discountType.equalsIgnoreCase("PWD")) {
                    isPwd = true;
                    pwdId = foundCustomer.getPwdId() != null ? foundCustomer.getPwdId() : "";
                    if (!pwdId.isEmpty()) {
                        idNumberInfo = "<b>PWD ID No.:</b> " + pwdId + "<br>";
                    }
                } else if (discountType.equalsIgnoreCase("Senior Citizen")) {
                    isPwd = true;
                    pwdId = foundCustomer.getSeniorCitizenId() != null ? foundCustomer.getSeniorCitizenId() : "";
                    if (!pwdId.isEmpty()) {
                        idNumberInfo = "<b>Senior Citizen ID No.:</b> " + pwdId + "<br>";
                    }
                }
            }

            double totalAmount = calculateTotal();

            String orderNo =
                    "ORD-" + System.currentTimeMillis();

            String currentDate =
                    LocalDate.now().toString();

            List<SalesDAO.SaleItemData> items =
                    new ArrayList<>();

            StringBuilder itemsDetails = new StringBuilder();

            for (int i = 0;
                 i < cartModel.getRowCount();
                 i++) {

                int medicineId = Integer.parseInt(
                        cartModel
                                .getValueAt(i, 4)
                                .toString()
                );

                int qtySold = Integer.parseInt(
                        cartModel
                                .getValueAt(i, 2)
                                .toString()
                );

                double unitPrice = parseMoney(
                        cartModel
                                .getValueAt(i, 1)
                                .toString()
                );

                String itemName = cartModel.getValueAt(i, 0).toString();
                
                String itemCategory = "N/A";
                for (int m = 0; m < medicineModel.getRowCount(); m++) {
                    if (medicineModel.getValueAt(m, 0).toString().equals(itemName)) {
                        itemCategory = medicineModel.getValueAt(m, 1).toString();
                        break;
                    }
                }

                items.add(
                        new SalesDAO.SaleItemData(
                                medicineId,
                                qtySold,
                                unitPrice
                        )
                );

                itemsDetails.append("- ").append(itemName)
                            .append(" (Category: ").append(itemCategory)
                            .append(", Qty: ").append(qtySold).append(")<br>");
            }

            SalesDAO.completeSale(
                    customerId,
                    orderNo,
                    currentDate,
                    totalAmount,
                    isPwd,
                    pwdId,
                    items
            );

            SharedData.totalSalesToday =
                    SalesDAO.getTodaySalesTotal();

            SharedData.addSale(
                    orderNo,
                    currentDate,
                    String.format("%.2f", totalAmount),
                    firstName + " " + lastName,
                    isPwd,
                    pwdId
            );

            DashboardPanel.refreshDashboardData();

            CustomDialog.showHtmlMessage(
                    this,
                    "<b>Checkout successful!</b><br><br>" +
                            "<b>Order No.:</b> " + orderNo + "<br>" +
                            "<b>Customer:</b> " + firstName + " " + lastName + "<br>" +
                            "<b>Discount:</b> " + discountType + "<br>" +
                            idNumberInfo +
                            "<b>Items Ordered:</b><br>" + itemsDetails.toString() + "<br>" +
                            "<b>Total:</b> ₱" + String.format("%.2f", totalAmount) + "<br><br>" +
                            "Sale saved successfully.",
                    "Checkout Successful",
                    false
            );

            clearCartAfterCheckout();

        } catch (Exception e) {

            e.printStackTrace();

            CustomDialog.showMessage(
                    this,
                    "Checkout Error:\n" + e.getMessage(),
                    "Error",
                    true
            );
        }
    }

    private void clearCartAfterCheckout() {

        cartModel.setRowCount(0);
        activeCheckoutCustomer = null;
        updateSubtotal();
        loadMedicinesToPOS();
    }

    public void loadMedicinesToPOS() {

        medicineModel.setRowCount(0);

        try {

            List<Medicine> medicines =
                    MedicineDAO.getAllMedicines();

            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (Medicine med : medicines) {

                String name = med.getName() == null ? "" : med.getName();
                String category = med.getMedicineCategory() == null ? "" : med.getMedicineCategory();
                
                boolean isExpired = false;
                try {
                    if (med.getExpiryDate() != null && !med.getExpiryDate().isEmpty()) {
                        LocalDate expiryDate = LocalDate.parse(med.getExpiryDate().trim(), formatter);
                        if (expiryDate.isBefore(today) || expiryDate.isEqual(today)) {
                            isExpired = true;
                        }
                    }
                } catch (Exception ignored) {}

                boolean isOutOfStock = med.getStock() <= 0;

                String status;
                if (isExpired) {
                    status = "Expired";
                } else if (isOutOfStock) {
                    status = "Out of Stock";
                } else {
                    status = "Active";
                }

                medicineModel.addRow(
                        new Object[]{
                                name,
                                category,
                                String.format("₱%.2f", med.getSellPrice()),
                                med.getStock(),
                                status,
                                med.getId()
                        }
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            CustomDialog.showMessage(
                    this,
                    "Unable to load medicines.\n" +
                            e.getMessage(),
                    "Database Error",
                    true
            );
        }
    }

    private double calculateSubtotal() {

        double total = 0;

        for (int i = 0;
             i < cartModel.getRowCount();
             i++) {

            total += parseMoney(
                    cartModel
                            .getValueAt(i, 3)
                            .toString()
            );
        }

        return total;
    }

    private double calculateDiscountAmount(double subtotal) {
        if (activeCheckoutCustomer == null || activeCheckoutCustomer.getDiscountType() == null) {
            return 0;
        }

        String type = activeCheckoutCustomer.getDiscountType().trim();
        if (type.equalsIgnoreCase("PWD") || type.equalsIgnoreCase("Senior Citizen")) {
            double pct = activeCheckoutCustomer.getDiscountPercent();
            if (pct <= 0) pct = 20.0;
            return subtotal * (pct / 100.0);
        }

        return 0;
    }

    private double calculateTotal() {

        double subtotal = calculateSubtotal();
        double discount = calculateDiscountAmount(subtotal);

        return Math.max(
                0,
                subtotal - discount
        );
    }

    private void updateSubtotal() {

        double subtotal = calculateSubtotal();
        double discount = calculateDiscountAmount(subtotal);
        double total = Math.max(0, subtotal - discount);

        lblSubtotalVal.setText(
                String.format(
                        "₱ %.2f",
                        subtotal
                )
        );

        if (activeCheckoutCustomer != null && activeCheckoutCustomer.getDiscountType() != null && 
           !activeCheckoutCustomer.getDiscountType().trim().equalsIgnoreCase("No Discount") &&
           !activeCheckoutCustomer.getDiscountType().trim().isEmpty()) {
            
            String dtype = activeCheckoutCustomer.getDiscountType().trim();
            double dpct = activeCheckoutCustomer.getDiscountPercent();
            if (dpct <= 0) dpct = 20.0;
            
            lblDiscountTitle.setText(dtype + " (" + (int)dpct + "%):");
        } else {
            lblDiscountTitle.setText("Discount (0%):");
        }

        lblDiscountVal.setText(
                String.format(
                        "- ₱ %.2f",
                        discount
                )
        );

        lblTotalVal.setText(
                String.format(
                        "₱ %.2f",
                        total
                )
        );
    }

    private double parseMoney(String value) {

        if (value == null || value.trim().isEmpty()) {
            return 0;
        }

        return Double.parseDouble(
                value
                        .replace("₱", "")
                        .replace(",", "")
                        .trim()
        );
    }

    private JButton createButton(
            String text,
            Color background,
            Color foreground) {

        JButton button = new JButton(text);

        button.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        button.setBorder(
                new EmptyBorder(
                        8,
                        14,
                        8,
                        14
                )
        );

        return button;
    }

    private JLabel createSummaryLabel(
            String text) {

        JLabel label = new JLabel(text);

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        label.setForeground(
                new Color(90, 95, 100)
        );

        return label;
    }

    private JLabel createSummaryValue(
            String text,
            Color color) {

        JLabel label =
                new JLabel(
                        text,
                        SwingConstants.RIGHT
                );

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        label.setForeground(color);

        return label;
    }

    private static class CustomDialog {

        private static String[] customerResult;

        public static void showMessage(
                Component parent,
                String message,
                String title,
                boolean isWarning) {
            showHtmlMessage(parent, message, title, isWarning);
        }

        public static void showHtmlMessage(
                Component parent,
                String htmlMessage,
                String title,
                boolean isWarning) {

            Window owner =
                    SwingUtilities.getWindowAncestor(
                            parent
                    );

            JDialog dialog =
                    new JDialog(
                            owner,
                            title,
                            Dialog.ModalityType.APPLICATION_MODAL
                    );

            dialog.setLayout(new BorderLayout());
            dialog.setResizable(false);

            JPanel centerPanel =
                    new JPanel(
                            new FlowLayout(
                                    FlowLayout.LEFT,
                                    20,
                                    20
                            )
                    );

            centerPanel.setBackground(
                    Color.WHITE
            );

            JLabel lblMsg =
                    new JLabel(
                            "<html>" + htmlMessage + "</html>"
                    );

            lblMsg.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            13
                    )
            );

            lblMsg.setForeground(
                    new Color(
                            70,
                            75,
                            80
                    )
            );

            centerPanel.add(lblMsg);

            JScrollPane scrollPane = new JScrollPane(centerPanel);
            scrollPane.setBorder(null);
            scrollPane.getViewport().setBackground(Color.WHITE);

            dialog.add(
                    scrollPane,
                    BorderLayout.CENTER
            );

            JPanel bottomPanel =
                    new JPanel(
                            new FlowLayout(
                                    FlowLayout.RIGHT,
                                    15,
                                    10
                            )
                    );

            bottomPanel.setBackground(
                    new Color(
                            248,
                            249,
                            250
                    )
            );

            bottomPanel.setBorder(
                    BorderFactory.createMatteBorder(
                            1,
                            0,
                            0,
                            0,
                            new Color(
                                    220,
                                    225,
                                    230
                            )
                    )
            );

            JButton btnOk =
                    new JButton("OK");

            btnOk.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            12
                    )
            );

            btnOk.setBackground(
                    new Color(
                            41,
                            128,
                            185
                    )
            );

            btnOk.setForeground(Color.WHITE);
            btnOk.setFocusPainted(false);
            btnOk.setBorder(
                    BorderFactory.createEmptyBorder(
                            6,
                            20,
                            6,
                            20
                    )
            );

            btnOk.setCursor(
                    new Cursor(
                            Cursor.HAND_CURSOR
                    )
            );

            btnOk.addActionListener(
                    e -> dialog.dispose()
            );

            bottomPanel.add(btnOk);

            dialog.add(
                    bottomPanel,
                    BorderLayout.SOUTH
            );

            dialog.pack();
            dialog.setSize(Math.max(dialog.getWidth() + 80, 480), Math.min(Math.max(dialog.getHeight() + 40, 180), 400));
            dialog.setLocationRelativeTo(parent);
            dialog.setVisible(true);
        }

        public static String[] showCustomerNameInput(Component parent) {
            customerResult = null;

            Window owner = SwingUtilities.getWindowAncestor(parent);
            JDialog dialog = new JDialog(owner, "Customer Details", Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setLayout(new BorderLayout());
            dialog.setResizable(false);

            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.setBackground(Color.WHITE);
            centerPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

            JLabel lblLastName = new JLabel("Last Name:");
            lblLastName.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblLastName.setForeground(new Color(60, 65, 70));
            lblLastName.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextField txtLastName = new JTextField();
            txtLastName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            txtLastName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            txtLastName.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 205, 210), 1),
                    BorderFactory.createEmptyBorder(5, 8, 5, 8)
            ));
            txtLastName.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel lblFirstName = new JLabel("First Name:");
            lblFirstName.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblFirstName.setForeground(new Color(60, 65, 70));
            lblFirstName.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextField txtFirstName = new JTextField();
            txtFirstName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            txtFirstName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            txtFirstName.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 205, 210), 1),
                    BorderFactory.createEmptyBorder(5, 8, 5, 8)
            ));
            txtFirstName.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel lblStatus = new JLabel(" ");
            lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);

            DocumentListener checkListener = new DocumentListener() {
                private void checkRecord() {
                    String fName = txtFirstName.getText().trim();
                    String lName = txtLastName.getText().trim();

                    if (fName.isEmpty() && lName.isEmpty()) {
                        lblStatus.setText(" ");
                        return;
                    }

                    int custId = CustomerDAO.getCustomerIdByNameIgnoreCase(fName, lName);
                    if (custId != -1) {
                        try {
                            List<Customer> all = CustomerDAO.getAllCustomers();
                            for (Customer c : all) {
                                if (c.getId() == custId) {
                                    String dtype = c.getDiscountType() != null ? c.getDiscountType() : "No Discount";
                                    lblStatus.setForeground(new Color(39, 174, 96));
                                    lblStatus.setText("Record Found! (Discount: " + dtype + ")");
                                    return;
                                }
                            }
                        } catch (Exception ignored) {}
                        lblStatus.setForeground(new Color(39, 174, 96));
                        lblStatus.setText("Record Found for this customer.");
                    } else {
                        lblStatus.setForeground(new Color(192, 57, 43));
                        lblStatus.setText("No records found for this customer.");
                    }
                }

                @Override public void insertUpdate(DocumentEvent e) { checkRecord(); }
                @Override public void removeUpdate(DocumentEvent e) { checkRecord(); }
                @Override public void changedUpdate(DocumentEvent e) { checkRecord(); }
            };

            txtLastName.getDocument().addDocumentListener(checkListener);
            txtFirstName.getDocument().addDocumentListener(checkListener);

            centerPanel.add(lblLastName);
            centerPanel.add(Box.createVerticalStrut(6));
            centerPanel.add(txtLastName);
            centerPanel.add(Box.createVerticalStrut(14));
            centerPanel.add(lblFirstName);
            centerPanel.add(Box.createVerticalStrut(6));
            centerPanel.add(txtFirstName);
            centerPanel.add(Box.createVerticalStrut(14));
            centerPanel.add(lblStatus);

            dialog.add(centerPanel, BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
            bottomPanel.setBackground(new Color(248, 249, 250));
            bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 230)));

            JButton btnOk = new JButton("OK");
            btnOk.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnOk.setBackground(new Color(41, 128, 185));
            btnOk.setForeground(Color.WHITE);
            btnOk.setFocusPainted(false);
            btnOk.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));

            JButton btnCancel = new JButton("Cancel");
            btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnCancel.setBackground(new Color(192, 57, 43));
            btnCancel.setForeground(Color.WHITE);
            btnCancel.setFocusPainted(false);
            btnCancel.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));

            btnOk.addActionListener(e -> {
                customerResult = new String[]{
                        txtFirstName.getText().trim(),
                        txtLastName.getText().trim()
                };
                dialog.dispose();
            });

            btnCancel.addActionListener(e -> {
                customerResult = null;
                dialog.dispose();
            });

            bottomPanel.add(btnOk);
            bottomPanel.add(btnCancel);

            dialog.add(bottomPanel, BorderLayout.SOUTH);
            dialog.pack();
            dialog.setSize(500, 310);
            dialog.setLocationRelativeTo(parent);
            dialog.setVisible(true);

            return customerResult;
        }
    }
}