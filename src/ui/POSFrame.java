package ui;

import db.CustomerDAO;
import db.MedicineDAO;
import db.SalesDAO;
import models.Medicine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class POSFrame extends JPanel {

    private JTable medicineTable;
    private JTable cartTable;
    private DefaultTableModel medicineModel;
    private DefaultTableModel cartModel;
    private JTextField searchField;

    private JLabel lblSubtotalVal;
    private JCheckBox chkPwd;
    private JLabel lblDiscountVal;
    private JLabel lblTotalVal;

    private static final double PWD_DISCOUNT_RATE = 0.20;

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

        loadMedicinesToPOS("");

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadMedicinesToPOS(searchField.getText().trim());
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
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        JButton btnSearch = createButton(
                "Search",
                new Color(51, 122, 183),
                Color.WHITE
        );

        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);

        leftPanel.add(searchPanel, BorderLayout.NORTH);

        String[] medColumns = {
                "Medicine Name",
                "Category",
                "Price",
                "Stock",
                "ID"
        };

        medicineModel = new DefaultTableModel(medColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        medicineTable = new JTable(medicineModel);
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

        medicineTable.getColumnModel().getColumn(4).setMinWidth(0);
        medicineTable.getColumnModel().getColumn(4).setMaxWidth(0);
        medicineTable.getColumnModel().getColumn(4).setPreferredWidth(0);

        JScrollPane medScroll = new JScrollPane(medicineTable);
        medScroll.getViewport().setBackground(Color.WHITE);
        medScroll.setBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230))
        );

        leftPanel.add(medScroll, BorderLayout.CENTER);

        JButton btnAddToCart = createButton(
                "Add to Cart",
                new Color(51, 122, 183),
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

        btnSearch.addActionListener(e ->
                loadMedicinesToPOS(searchField.getText().trim())
        );

        searchField.addActionListener(e ->
                loadMedicinesToPOS(searchField.getText().trim())
        );

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
                new Color(220, 53, 69),
                Color.WHITE
        );

        JButton btnCheckout = createButton(
                "Checkout",
                new Color(40, 167, 69),
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

        chkPwd = new JCheckBox("PWD Customer");
        chkPwd.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkPwd.setForeground(textColor);
        chkPwd.setOpaque(false);
        chkPwd.setFocusPainted(false);
        chkPwd.setIcon(new FlatCheckIcon(false));
        chkPwd.setSelectedIcon(new FlatCheckIcon(true));
        chkPwd.setIconTextGap(8);
        chkPwd.setCursor(new Cursor(Cursor.HAND_CURSOR));

        chkPwd.addActionListener(e ->
                updateSubtotal()
        );

        JLabel lblPwdNote = new JLabel("20% discount");
        lblPwdNote.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPwdNote.setForeground(new Color(120, 125, 130));

        JPanel pwdRow = new JPanel(new BorderLayout(10, 0));
        pwdRow.setBackground(new Color(248, 249, 250));
        pwdRow.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(200, 205, 210)
                ),
                new EmptyBorder(7, 10, 7, 10)
        ));

        pwdRow.add(chkPwd, BorderLayout.WEST);
        pwdRow.add(lblPwdNote, BorderLayout.EAST);

        lblSubtotalVal = createSummaryValue(
                "₱ 0.00",
                textColor
        );

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

        summaryPanel.add(createSummaryLabel(
                "PWD Discount (20%):"
        ));
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
                new EmptyBorder(10, 0, 10, 0)
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
                pwdRow,
                BorderLayout.NORTH
        );

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

        try {

            String name = medicineModel
                    .getValueAt(selectedRow, 0)
                    .toString();

            String priceText = medicineModel
                    .getValueAt(selectedRow, 2)
                    .toString();

            int availableStock = Integer.parseInt(
                    medicineModel
                            .getValueAt(selectedRow, 3)
                            .toString()
            );

            int medicineId = Integer.parseInt(
                    medicineModel
                            .getValueAt(selectedRow, 4)
                            .toString()
            );

            double price = parseMoney(priceText);

            if (availableStock <= 0) {
                CustomDialog.showMessage(
                        this,
                        "This medicine is out of stock.",
                        "Stock Error",
                        true
                );
                return;
            }

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

            String customerName = CustomDialog.showInput(
                    this,
                    "Enter Customer Full Name:",
                    "Customer Details"
            );

            if (customerName == null) {
                return;
            }

            customerName = customerName.trim();

            if (customerName.isEmpty()) {
                CustomDialog.showMessage(
                        this,
                        "Customer name is required.",
                        "Validation Error",
                        true
                );
                return;
            }

            String[] nameParts = splitCustomerName(customerName);

            String firstName = nameParts[0];
            String lastName = nameParts[1];

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

            boolean isPwd = chkPwd.isSelected();

            String pwdId = "";

            if (isPwd) {

                String input = CustomDialog.showInput(
                        this,
                        "Enter PWD ID Number:",
                        "PWD Verification"
                );

                if (input == null) {
                    return;
                }

                pwdId = input.trim();

                if (pwdId.isEmpty()) {

                    CustomDialog.showMessage(
                            this,
                            "PWD ID number is required for PWD discount.",
                            "Validation Error",
                            true
                    );

                    return;
                }
            }

            double totalAmount = calculateTotal();

            String orderNo =
                    "ORD-" + System.currentTimeMillis();

            String currentDate =
                    LocalDate.now().toString();

            List<SalesDAO.SaleItemData> items =
                    new ArrayList<>();

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

                items.add(
                        new SalesDAO.SaleItemData(
                                medicineId,
                                qtySold,
                                unitPrice
                        )
                );
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
                    customerName,
                    isPwd,
                    pwdId
            );

            DashboardPanel.refreshDashboardData();

            CustomDialog.showMessage(
                    this,
                    "Checkout successful!\n\n" +
                            "Order No.: " + orderNo + "\n" +
                            "Customer: " + customerName + "\n" +
                            "Total: ₱" +
                            String.format("%.2f", totalAmount) +
                            "\n\n" +
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

    private String[] splitCustomerName(String fullName) {

        String cleaned = fullName.trim();

        String firstName;
        String lastName;

        int firstSpace = cleaned.indexOf(' ');

        if (firstSpace == -1) {
            firstName = cleaned;
            lastName = "";
        } else {
            firstName = cleaned.substring(
                    0,
                    firstSpace
            ).trim();

            lastName = cleaned.substring(
                    firstSpace + 1
            ).trim();
        }

        return new String[]{
                firstName,
                lastName
        };
    }

    private void clearCartAfterCheckout() {

        cartModel.setRowCount(0);
        chkPwd.setSelected(false);

        updateSubtotal();

        loadMedicinesToPOS(
                searchField.getText().trim()
        );
    }

    private void loadMedicinesToPOS(String filterKeyword) {

        medicineModel.setRowCount(0);

        try {

            List<Medicine> medicines =
                    MedicineDAO.getAllMedicines();

            String keyword =
                    filterKeyword == null
                            ? ""
                            : filterKeyword.trim()
                                    .toLowerCase();

            for (Medicine med : medicines) {

                String name =
                        med.getName() == null
                                ? ""
                                : med.getName();

                String category =
                        med.getMedicineCategory() == null
                                ? ""
                                : med.getMedicineCategory();

                if (keyword.isEmpty()
                        || name.toLowerCase()
                                .contains(keyword)
                        || category.toLowerCase()
                                .contains(keyword)) {

                    medicineModel.addRow(
                            new Object[]{
                                    name,
                                    category,
                                    String.format(
                                            "₱%.2f",
                                            med.getSellPrice()
                                    ),
                                    med.getStock(),
                                    med.getId()
                            }
                    );
                }
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

    private double calculatePwdDiscount(
            double subtotal) {

        if (!chkPwd.isSelected()) {
            return 0;
        }

        return subtotal * PWD_DISCOUNT_RATE;
    }

    private double calculateTotal() {

        double subtotal =
                calculateSubtotal();

        double discount =
                calculatePwdDiscount(subtotal);

        return Math.max(
                0,
                subtotal - discount
        );
    }

    private void updateSubtotal() {

        double subtotal =
                calculateSubtotal();

        double discount =
                calculatePwdDiscount(subtotal);

        double total =
                Math.max(
                        0,
                        subtotal - discount
                );

        lblSubtotalVal.setText(
                String.format(
                        "₱ %.2f",
                        subtotal
                )
        );

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

    private static class FlatCheckIcon
            implements Icon {

        private final boolean checked;

        FlatCheckIcon(boolean checked) {
            this.checked = checked;
        }

        @Override
        public int getIconWidth() {
            return 16;
        }

        @Override
        public int getIconHeight() {
            return 16;
        }

        @Override
        public void paintIcon(
                Component c,
                Graphics g,
                int x,
                int y) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            Color blue =
                    new Color(51, 122, 183);

            g2.setColor(
                    checked
                            ? blue
                            : Color.WHITE
            );

            g2.fillRect(
                    x,
                    y,
                    15,
                    15
            );

            g2.setColor(
                    checked
                            ? blue
                            : new Color(
                                    160,
                                    165,
                                    170
                            )
            );

            g2.drawRect(
                    x,
                    y,
                    15,
                    15
            );

            if (checked) {

                g2.setColor(Color.WHITE);

                g2.setStroke(
                        new BasicStroke(
                                2f,
                                BasicStroke.CAP_ROUND,
                                BasicStroke.JOIN_ROUND
                        )
                );

                g2.drawLine(
                        x + 4,
                        y + 8,
                        x + 7,
                        y + 11
                );

                g2.drawLine(
                        x + 7,
                        y + 11,
                        x + 12,
                        y + 4
                );
            }

            g2.dispose();
        }
    }

    private static class CustomDialog {

        private static String inputResult;

        public static void showMessage(
                Component parent,
                String message,
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

            dialog.setSize(450, 190);
            dialog.setLocationRelativeTo(parent);
            dialog.setLayout(
                    new BorderLayout()
            );

            JPanel topHeader =
                    new JPanel(
                            new FlowLayout(
                                    FlowLayout.LEFT,
                                    15,
                                    10
                            )
                    );

            topHeader.setBackground(
                    new Color(248, 249, 250)
            );

            topHeader.setBorder(
                    BorderFactory.createMatteBorder(
                            0,
                            0,
                            1,
                            0,
                            new Color(
                                    220,
                                    225,
                                    230
                            )
                    )
            );

            JLabel lblTitle =
                    new JLabel(title);

            lblTitle.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            13
                    )
            );

            lblTitle.setForeground(
                    isWarning
                            ? new Color(
                                    217,
                                    119,
                                    6
                            )
                            : new Color(
                                    50,
                                    60,
                                    70
                            )
            );

            topHeader.add(lblTitle);

            dialog.add(
                    topHeader,
                    BorderLayout.NORTH
            );

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
                            "<html>" +
                                    message.replace(
                                            "\n",
                                            "<br>"
                                    ) +
                                    "</html>"
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

            dialog.add(
                    centerPanel,
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
                            13,
                            148,
                            136
                    )
            );

            btnOk.setForeground(Color.WHITE);
            btnOk.setFocusPainted(false);
            btnOk.setBorder(
                    BorderFactory.createEmptyBorder(
                            6,
                            18,
                            6,
                            18
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

            dialog.getRootPane()
                    .setDefaultButton(btnOk);

            dialog.setVisible(true);
        }

        public static String showInput(
                Component parent,
                String message,
                String title) {

            inputResult = null;

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

            dialog.setSize(450, 220);
            dialog.setLocationRelativeTo(parent);
            dialog.setLayout(
                    new BorderLayout()
            );

            JPanel topHeader =
                    new JPanel(
                            new FlowLayout(
                                    FlowLayout.LEFT,
                                    15,
                                    10
                            )
                    );

            topHeader.setBackground(
                    new Color(
                            248,
                            249,
                            250
                    )
            );

            topHeader.setBorder(
                    BorderFactory.createMatteBorder(
                            0,
                            0,
                            1,
                            0,
                            new Color(
                                    220,
                                    225,
                                    230
                            )
                    )
            );

            JLabel lblTitle =
                    new JLabel(title);

            lblTitle.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            13
                    )
            );

            lblTitle.setForeground(
                    new Color(
                            50,
                            60,
                            70
                    )
            );

            topHeader.add(lblTitle);

            dialog.add(
                    topHeader,
                    BorderLayout.NORTH
            );

            JPanel centerPanel =
                    new JPanel();

            centerPanel.setLayout(
                    new BoxLayout(
                            centerPanel,
                            BoxLayout.Y_AXIS
                    )
            );

            centerPanel.setBackground(
                    Color.WHITE
            );

            centerPanel.setBorder(
                    new EmptyBorder(
                            15,
                            20,
                            15,
                            20
                    )
            );

            JLabel lblMsg =
                    new JLabel(message);

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

            lblMsg.setAlignmentX(
                    Component.LEFT_ALIGNMENT
            );

            centerPanel.add(lblMsg);

            centerPanel.add(
                    Box.createVerticalStrut(10)
            );

            JTextField textField =
                    new JTextField();

            textField.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            13
                    )
            );

            textField.setMaximumSize(
                    new Dimension(
                            Integer.MAX_VALUE,
                            32
                    )
            );

            textField.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                    new Color(
                                            200,
                                            205,
                                            210
                                    )
                            ),
                            BorderFactory.createEmptyBorder(
                                    4,
                                    6,
                                    4,
                                    6
                            )
                    )
            );

            textField.setAlignmentX(
                    Component.LEFT_ALIGNMENT
            );

            centerPanel.add(textField);

            dialog.add(
                    centerPanel,
                    BorderLayout.CENTER
            );

            JPanel bottomPanel =
                    new JPanel(
                            new FlowLayout(
                                    FlowLayout.RIGHT,
                                    10,
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
                            13,
                            148,
                            136
                    )
            );

            btnOk.setForeground(Color.WHITE);
            btnOk.setFocusPainted(false);
            btnOk.setBorder(
                    BorderFactory.createEmptyBorder(
                            6,
                            18,
                            6,
                            18
                    )
            );

            btnOk.setCursor(
                    new Cursor(
                            Cursor.HAND_CURSOR
                    )
            );

            JButton btnCancel =
                    new JButton("Cancel");

            btnCancel.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            12
                    )
            );

            btnCancel.setBackground(
                    new Color(
                            220,
                            53,
                            69
                    )
            );

            btnCancel.setForeground(Color.WHITE);
            btnCancel.setFocusPainted(false);
            btnCancel.setBorder(
                    BorderFactory.createEmptyBorder(
                            6,
                            18,
                            6,
                            18
                    )
            );

            btnCancel.setCursor(
                    new Cursor(
                            Cursor.HAND_CURSOR
                    )
            );

            btnOk.addActionListener(e -> {
                inputResult =
                        textField
                                .getText()
                                .trim();

                dialog.dispose();
            });

            btnCancel.addActionListener(e -> {
                inputResult = null;
                dialog.dispose();
            });

            textField.addActionListener(e -> {
                inputResult =
                        textField
                                .getText()
                                .trim();

                dialog.dispose();
            });

            bottomPanel.add(btnOk);
            bottomPanel.add(btnCancel);

            dialog.add(
                    bottomPanel,
                    BorderLayout.SOUTH
            );

            dialog.getRootPane()
                    .setDefaultButton(btnOk);

            dialog.setVisible(true);

            return inputResult;
        }
    }
}