package ui;

import db.CustomerDAO;
import db.MedicineDAO;
import db.SalesDAO;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import models.Customer;
import models.Medicine;

public class POSFrame extends JPanel {

    // same palette / spacing as MedicinePanel
    private static final Color BORDER = new Color(220, 225, 230);
    private static final Color TEXT = new Color(60, 65, 70);
    private static final Color MUTED = new Color(108, 117, 125);
    private static final Color PRIMARY_SOFT = new Color(224, 243, 241);
    private static final Color DANGER = new Color(192, 57, 43);
    private static final int CELL_PAD = 10;

    // model column indexes of the medicine table
    private static final int M_NAME = 0, M_CATEGORY = 1, M_PRICE = 2, M_STOCK = 3, M_STATUS = 4, M_ID = 5;

    // order of the columns as shown on screen (model indexes)
    private static final int[] MED_VIEW_ORDER = {M_CATEGORY, M_NAME, M_PRICE, M_STOCK, M_STATUS, M_ID};
    // group label above each column (by view position). null = no group, header spans both rows
    private static final String[] MED_HEADER_GROUPS = {null, null, "Price", "Stock", "Stock", null};

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
                updateSubtotal();
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
                    // Pattern.quote so characters like ( or [ do not break the search
                    medicineRowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
                }
            }
        });

        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        leftPanel.add(searchPanel, BorderLayout.NORTH);

        // same header names as the Medicine List page
        String[] medColumns = {
                "Medicine Name",
                "Medicine Category",
                "Sell Price",
                "Quantity",
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

        medicineTable.setSelectionBackground(PRIMARY_SOFT);
        medicineTable.setSelectionForeground(TEXT);

        medicineRowSorter = new TableRowSorter<>(medicineModel);
        medicineTable.setRowSorter(medicineRowSorter);

        medicineTable.setRowHeight(33);
        medicineTable.setShowVerticalLines(true);
        medicineTable.setShowHorizontalLines(true);
        medicineTable.setGridColor(new Color(235, 238, 242));
        medicineTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        medicineTable.setFillsViewportHeight(true);
        medicineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // two-level header (Price / Stock groups) like the Medicine List page
        JTableHeader medHeader = new GroupHeader(medicineTable.getColumnModel(), MED_HEADER_GROUPS, 52);
        medHeader.setFont(new Font("Segoe UI", Font.BOLD, 11));
        medicineTable.setTableHeader(medHeader);

        int[] widths = {140, 120, 85, 75, 95, 0};
        for (int i = 0; i < widths.length; i++) {
            medicineTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, false, row, column);

                int mc = table.convertColumnIndexToModel(column);
                int modelRow = table.convertRowIndexToModel(row);

                setBorder(new EmptyBorder(0, CELL_PAD, 0, CELL_PAD));
                setHorizontalAlignment(mc == M_STOCK || mc == M_STATUS ? JLabel.CENTER : JLabel.LEFT);

                boolean problem = false;
                try {
                    String status = table.getModel().getValueAt(modelRow, M_STATUS).toString();
                    problem = "Out of Stock".equalsIgnoreCase(status) || "Expired".equalsIgnoreCase(status);
                } catch (Exception ignored) {}

                setFont(new Font("Segoe UI", problem ? Font.BOLD : Font.PLAIN, 12));
                setForeground(problem ? DANGER : TEXT);

                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                } else {
                    setBackground(Color.WHITE);
                }
                return this;
            }
        };

        for (int i = 0; i < medicineTable.getColumnModel().getColumnCount(); i++) {
            medicineTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        // arrange columns visually: Category | Name | Price | Quantity | Status  (ID stays hidden)
        for (int pos = 0; pos < MED_VIEW_ORDER.length; pos++) {
            int from = medicineTable.convertColumnIndexToView(MED_VIEW_ORDER[pos]);
            if (from != pos) medicineTable.moveColumn(from, pos);
        }
        medicineTable.getTableHeader().setReorderingAllowed(false);
        medicineTable.getTableHeader().setResizingAllowed(false); // columns can't be dragged

        // hidden ID column
        medicineTable.getColumnModel().getColumn(medicineTable.convertColumnIndexToView(M_ID)).setMinWidth(0);
        medicineTable.getColumnModel().getColumn(medicineTable.convertColumnIndexToView(M_ID)).setMaxWidth(0);
        medicineTable.getColumnModel().getColumn(medicineTable.convertColumnIndexToView(M_ID)).setPreferredWidth(0);

        leftPanel.add(scroll(medicineTable, true), BorderLayout.CENTER);

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

        cartTable.setSelectionBackground(PRIMARY_SOFT);
        cartTable.setSelectionForeground(TEXT);

        cartTable.setRowHeight(33);
        cartTable.setShowVerticalLines(true);
        cartTable.setShowHorizontalLines(true);
        cartTable.setGridColor(new Color(235, 238, 242));
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cartTable.setFillsViewportHeight(true);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // same header look as the medicine table (single row, no groups)
        JTableHeader cartHeader = new GroupHeader(cartTable.getColumnModel(), new String[cartColumns.length], 36);
        cartHeader.setFont(new Font("Segoe UI", Font.BOLD, 11));
        cartTable.setTableHeader(cartHeader);
        cartTable.getTableHeader().setReorderingAllowed(false);
        cartTable.getTableHeader().setResizingAllowed(false);

        DefaultTableCellRenderer cartRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
                int mc = table.convertColumnIndexToModel(column);
                setBorder(new EmptyBorder(0, CELL_PAD, 0, CELL_PAD));
                setHorizontalAlignment(mc == 2 ? JLabel.CENTER : JLabel.LEFT);
                setFont(new Font("Segoe UI", Font.PLAIN, 12));
                setForeground(TEXT);
                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                } else {
                    setBackground(Color.WHITE);
                }
                return this;
            }
        };
        for (int i = 0; i < cartColumns.length; i++) {
            cartTable.getColumnModel().getColumn(i).setCellRenderer(cartRenderer);
        }

        cartTable.getColumnModel().getColumn(4).setMinWidth(0);
        cartTable.getColumnModel().getColumn(4).setMaxWidth(0);
        cartTable.getColumnModel().getColumn(4).setPreferredWidth(0);

        rightPanel.add(scroll(cartTable, false), BorderLayout.CENTER);

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

        String sym = SharedData.currencySymbol + " ";

        lblSubtotalVal = createSummaryValue(
                sym + "0.00",
                textColor
        );

        lblDiscountTitle = createSummaryLabel("Discount (0%):");
        lblDiscountVal = createSummaryValue(
                "- " + sym + "0.00",
                textColor
        );

        lblTotalVal = createSummaryValue(
                sym + "0.00",
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

    // ------------------------------------------------------------------ table helpers

    /** Scroll pane with the same thin scrollbars as the Medicine List page. */
    private JScrollPane scroll(JTable t, boolean responsive) {
        JScrollPane sp = new JScrollPane(t);
        sp.getViewport().setBackground(Color.WHITE);
        sp.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));
        for (JScrollBar bar : new JScrollBar[]{sp.getVerticalScrollBar(), sp.getHorizontalScrollBar()}) {
            bar.setUI(new SlimScrollBarUI());
            bar.setOpaque(true);
            bar.setBackground(Color.WHITE);
            bar.setUnitIncrement(16);
        }
        JPanel corner = new JPanel();
        corner.setBackground(Color.WHITE);
        sp.setCorner(JScrollPane.LOWER_RIGHT_CORNER, corner);
        if (responsive) {
            sp.getViewport().addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override
                public void componentResized(java.awt.event.ComponentEvent e) {
                    updateResizeMode(t);
                }
            });
        }
        return sp;
    }

    /** Size every column to its longest value so nothing (like medicine names) is cut off. */
    private void fitColumns(JTable t) {
        DefaultTableModel model = (DefaultTableModel) t.getModel();
        FontMetrics fm = t.getFontMetrics(new Font("Segoe UI", Font.BOLD, 12));
        TableColumnModel cm = t.getColumnModel();
        for (int v = 0; v < cm.getColumnCount(); v++) {
            TableColumn col = cm.getColumn(v);
            int mc = col.getModelIndex();
            if (mc == M_ID) continue; // hidden column

            int w = fm.stringWidth(String.valueOf(col.getHeaderValue()));
            for (int r = 0; r < model.getRowCount(); r++) {
                Object val = model.getValueAt(r, mc);
                if (val != null) w = Math.max(w, fm.stringWidth(val.toString()));
            }
            w += CELL_PAD * 2 + 6;
            col.setMinWidth(w);
            col.setPreferredWidth(w);
        }
    }

    /** Fill the width when there is room, scroll sideways when the columns do not fit. */
    private void updateResizeMode(JTable t) {
        int total = 0;
        TableColumnModel cm = t.getColumnModel();
        for (int i = 0; i < cm.getColumnCount(); i++) total += cm.getColumn(i).getPreferredWidth();
        Container vp = SwingUtilities.getAncestorOfClass(JViewport.class, t);
        int avail = vp == null ? 0 : vp.getWidth();
        t.setAutoResizeMode(avail > 0 && total > avail ? JTable.AUTO_RESIZE_OFF : JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    // ------------------------------------------------------------------ cart / checkout logic

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
                    .getValueAt(modelRow, M_NAME)
                    .toString();

            String priceText = medicineModel
                    .getValueAt(modelRow, M_PRICE)
                    .toString();

            int availableStock = Integer.parseInt(
                    medicineModel
                            .getValueAt(modelRow, M_STOCK)
                            .toString()
            );

            String status = medicineModel
                    .getValueAt(modelRow, M_STATUS)
                    .toString();

            int medicineId = Integer.parseInt(
                    medicineModel
                            .getValueAt(modelRow, M_ID)
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
            String sym = SharedData.currencySymbol + " ";

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
                        String.format("%s%.2f", sym, price * newQty),
                        existingRow,
                        3
                );

            } else {

                cartModel.addRow(new Object[]{
                        name,
                        String.format("%s%.2f", sym, price),
                        "1",
                        String.format("%s%.2f", sym, price),
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
                    if (medicineModel.getValueAt(m, M_NAME).toString().equals(itemName)) {
                        itemCategory = medicineModel.getValueAt(m, M_CATEGORY).toString();
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
                            "<b>Total:</b> " + SharedData.currencySymbol + String.format("%.2f", totalAmount) + "<br><br>" +
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
            String sym = SharedData.currencySymbol + " ";

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
                    status = "Available"; // same wording as the Medicine List page
                }

                medicineModel.addRow(
                        new Object[]{
                                name,
                                category,
                                String.format("%s%.2f", sym, med.getSellPrice()),
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

        fitColumns(medicineTable);
        updateResizeMode(medicineTable);
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
        String sym = SharedData.currencySymbol + " ";

        lblSubtotalVal.setText(
                String.format(
                        "%s%.2f",
                        sym,
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
                        "- %s%.2f",
                        sym,
                        discount
                )
        );

        lblTotalVal.setText(
                String.format(
                        "%s%.2f",
                        sym,
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
                        .replaceAll("[^0-9.]", "")
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

    // ------------------------------------------------------------ grouped header

    /** Table header: optional group label on top, column names below. Same look as the Medicine List page. */
    private static class GroupHeader extends JTableHeader {
        private final String[] groups;

        GroupHeader(TableColumnModel cm, String[] groups, int height) {
            super(cm);
            this.groups = groups;
            setPreferredSize(new Dimension(0, height));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(new Color(248, 250, 252));
            g.fillRect(0, 0, getWidth(), getHeight());

            int n = columnModel.getColumnCount();
            int h = getHeight();
            int half = h / 2;
            int i = 0;
            while (i < n) {
                String grp = i < groups.length ? groups[i] : null;
                if (grp == null) {
                    Rectangle r = getHeaderRect(i);
                    if (r.width > 0) cell(g, r.x, 0, r.width, h, title(i), isCentered(i));
                    i++;
                } else {
                    int j = i;
                    while (j + 1 < n && j + 1 < groups.length && grp.equals(groups[j + 1])) j++;
                    Rectangle a = getHeaderRect(i), b = getHeaderRect(j);
                    cell(g, a.x, 0, b.x + b.width - a.x, half, grp, true);
                    for (int k = i; k <= j; k++) {
                        Rectangle r = getHeaderRect(k);
                        if (r.width > 0) cell(g, r.x, half, r.width, h - half, title(k), isCentered(k));
                    }
                    i = j + 1;
                }
            }
        }

        private String title(int viewCol) {
            return String.valueOf(columnModel.getColumn(viewCol).getHeaderValue());
        }

        private boolean isCentered(int viewCol) {
            int mc = columnModel.getColumn(viewCol).getModelIndex();
            // medicine table: Quantity / Status. The cart table uses different headers, so match by title too.
            String t = title(viewCol);
            return (t.equals("Quantity") || t.equals("Status") || t.equals("Qty"))
                    || (mc == M_STOCK && t.equals("Quantity"));
        }

        private void cell(Graphics2D g, int x, int y, int w, int h, String text, boolean centered) {
            g.setColor(new Color(248, 250, 252));
            g.fillRect(x, y, w, h);
            g.setColor(BORDER);
            g.drawRect(x, y, w - 1, h - 1);
            g.setColor(MUTED);
            g.setFont(getFont());
            FontMetrics fm = g.getFontMetrics();
            int tx = centered ? x + (w - fm.stringWidth(text)) / 2 : x + CELL_PAD;
            int ty = y + (h + fm.getAscent() - fm.getDescent()) / 2;
            g.drawString(text, tx, ty);
        }
    }

    // ------------------------------------------------------------ slim scrollbar

    /** Thin rounded scrollbar: no arrow buttons, no track, just a soft gray thumb. */
    private static class SlimScrollBarUI extends BasicScrollBarUI {
        private static final int SIZE = 12;
        private static final Color THUMB = new Color(176, 184, 192);
        private static final Color THUMB_HOVER = new Color(150, 159, 168);

        @Override
        protected void configureScrollBarColors() {
            super.configureScrollBarColors();
            thumbColor = THUMB;
            trackColor = Color.WHITE;
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            return new Dimension(SIZE, SIZE);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return noButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return noButton();
        }

        private JButton noButton() {
            JButton b = new JButton();
            Dimension zero = new Dimension(0, 0);
            b.setPreferredSize(zero);
            b.setMinimumSize(zero);
            b.setMaximumSize(zero);
            return b;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            g.setColor(Color.WHITE);
            g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty() || !scrollbar.isEnabled()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isThumbRollover() || isDragging ? THUMB_HOVER : THUMB);
            int pad = 2;
            int arc = Math.min(r.width, r.height) - pad * 2;
            g2.fillRoundRect(r.x + pad, r.y + pad, r.width - pad * 2, r.height - pad * 2, arc, arc);
            g2.dispose();
        }
    }

    // ------------------------------------------------------------ dialogs

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