package ui;

import db.CustomerDAO;
import db.MedicineDAO;
import db.SalesDAO;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.Border;
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

    private static final Color PAGE_BG = new Color(240, 242, 245);
    private static final Color BORDER = new Color(220, 225, 230);
    private static final Color GRID = new Color(228, 232, 237);
    private static final Color TEXT = new Color(60, 65, 70);
    private static final Color TEXT_DARK = new Color(40, 45, 50);
    private static final Color MUTED = new Color(108, 117, 125);
    private static final Color HEADER_BG = new Color(245, 247, 250);
    private static final Color HEADER_TEXT = new Color(70, 80, 90);
    private static final Color ROW_ALT = new Color(250, 251, 253);
    private static final Color PRIMARY = new Color(26, 143, 136);
    private static final Color PRIMARY_SOFT = new Color(224, 243, 241);
    private static final Color DANGER = new Color(192, 57, 43);
    private static final int CELL_PAD = 8;
    private static final int M_NAME = 0, M_CATEGORY = 1, M_PRICE = 2, M_STOCK = 3, M_STATUS = 4, M_ID = 5;
    private static final int C_TOTAL = 3;
    private static final int[] MED_VIEW_ORDER = {M_CATEGORY, M_NAME, M_PRICE, M_STOCK, M_STATUS, M_ID};
    private static final String[] MED_HEADER_GROUPS = {null, null, "Price", "Stock", "Stock", null};
    private static final int[] MED_ALIGN = {
            JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER
    };
    private static final int[] CART_ALIGN = {
            JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER
    };
    private static final double[] MED_RATIOS = {0.23, 0.23, 0.18, 0.16, 0.20, 0};
    private static final double[] CART_RATIOS = {0.31, 0.24, 0.15, 0.30, 0};
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
        setBackground(PAGE_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildHeader(), BorderLayout.NORTH);

        JPanel leftPanel = buildMedicinePanel();
        JPanel rightPanel = buildCartPanel();

    
        leftPanel.setPreferredSize(new Dimension(58, 100));
        rightPanel.setPreferredSize(new Dimension(42, 100));

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1;

        gc.gridx = 0;
        gc.weightx = 58;
        gc.insets = new Insets(0, 0, 0, 8);
        contentPanel.add(leftPanel, gc);

        gc.gridx = 1;
        gc.weightx = 42;
        gc.insets = new Insets(0, 8, 0, 0);
        contentPanel.add(rightPanel, gc);

        add(contentPanel, BorderLayout.CENTER);

        loadMedicinesToPOS();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadMedicinesToPOS();
                updateSubtotal();
            }
        });
    }

    private JPanel buildHeader() {
        JLabel lblTitle = new JLabel("Point of Sales (POS)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(TEXT_DARK);

        JLabel lblSubtitle = new JLabel("Pick a medicine, add it to the cart, then check out");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(MUTED);

        JPanel titleContainer = new JPanel();
        titleContainer.setLayout(new BoxLayout(titleContainer, BoxLayout.Y_AXIS));
        titleContainer.setOpaque(false);
        titleContainer.add(lblTitle);
        titleContainer.add(Box.createVerticalStrut(3));
        titleContainer.add(lblSubtitle);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(titleContainer, BorderLayout.WEST);
        return headerPanel;
    }

    private Border cardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(14, 14, 14, 14)
        );
    }


    private JPanel buildMedicinePanel() {

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(cardBorder());

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel lblSearch = new JLabel("Search Medicine:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSearch.setForeground(TEXT);

        searchField = new SearchField("Type to search medicines...");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });

        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        leftPanel.add(searchPanel, BorderLayout.NORTH);

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

        medicineTable = new JTable(medicineModel) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawEmptyMessage(g, this, "No medicines found");
            }
        };
        styleTable(medicineTable);

        medicineRowSorter = new TableRowSorter<>(medicineModel);
        medicineTable.setRowSorter(medicineRowSorter);

        JTableHeader medHeader = new GroupHeader(medicineTable.getColumnModel(), MED_HEADER_GROUPS, MED_ALIGN, 56);
        medHeader.setFont(new Font("Segoe UI", Font.BOLD, 11));
        medicineTable.setTableHeader(medHeader);

        for (int v = 0; v < medicineTable.getColumnCount(); v++) {
            TableColumn col = medicineTable.getColumnModel().getColumn(v);
            int mc = col.getModelIndex();

            col.setCellRenderer(new BodyRenderer(MED_ALIGN[mc]) {
                @Override
                protected void style(JTable t, Object value, int row, int column) {
                    boolean problem = false;
                    try {
                        int modelRow = t.convertRowIndexToModel(row);
                        String status = t.getModel().getValueAt(modelRow, M_STATUS).toString();
                        problem = "Out of Stock".equalsIgnoreCase(status) || "Expired".equalsIgnoreCase(status);
                    } catch (Exception ignored) {}

                    if (problem) {
                        setFont(t.getFont().deriveFont(Font.BOLD));
                        setForeground(DANGER);
                    } else if (mc == M_STATUS) {
                        setFont(t.getFont().deriveFont(Font.BOLD));
                        setForeground(PRIMARY);
                    }
                }
            });
        }

        for (int pos = 0; pos < MED_VIEW_ORDER.length; pos++) {
            int from = medicineTable.convertColumnIndexToView(MED_VIEW_ORDER[pos]);
            if (from != pos) medicineTable.moveColumn(from, pos);
        }
        medicineTable.getTableHeader().setReorderingAllowed(false);
        medicineTable.getTableHeader().setResizingAllowed(false); 

        TableColumn idCol = medicineTable.getColumnModel().getColumn(medicineTable.convertColumnIndexToView(M_ID));
        idCol.setMinWidth(0);
        idCol.setMaxWidth(0);
        idCol.setPreferredWidth(0);

        applyColumnWidths(medicineTable, MED_RATIOS, 520); 

        leftPanel.add(scroll(medicineTable, MED_RATIOS), BorderLayout.CENTER);

        JButton btnAddToCart = createButton("Add to Cart", PRIMARY, Color.WHITE);
        btnAddToCart.setBorder(new EmptyBorder(11, 0, 11, 0));

        JPanel btnAddWrapper = new JPanel(new BorderLayout());
        btnAddWrapper.setOpaque(false);
        btnAddWrapper.setBorder(new EmptyBorder(12, 0, 0, 0));
        btnAddWrapper.add(btnAddToCart, BorderLayout.CENTER);

        leftPanel.add(btnAddWrapper, BorderLayout.SOUTH);

        medicineTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    addSelectedMedicineToCart();
                }
            }
        });

        btnAddToCart.addActionListener(e -> addSelectedMedicineToCart());

        return leftPanel;
    }

    private void filterTable() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            medicineRowSorter.setRowFilter(null);
        } else {
            medicineRowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
        }
    }


    private JPanel buildCartPanel() {

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(cardBorder());

        JLabel lblCartTitle = new JLabel("Current Cart & Checkout");
        lblCartTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCartTitle.setForeground(TEXT_DARK);
        lblCartTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        rightPanel.add(lblCartTitle, BorderLayout.NORTH);

        String[] cartColumns = {
                "Item Name",
                "Price",
                "Qty",
                "Total",
                "Medicine ID"
        };

        cartModel = new DefaultTableModel(new Object[][]{}, cartColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        cartTable = new JTable(cartModel) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawEmptyMessage(g, this, "Your cart is empty");
            }
        };
        styleTable(cartTable);

        JTableHeader cartHeader = new GroupHeader(cartTable.getColumnModel(), new String[cartColumns.length], CART_ALIGN, 56);
        cartHeader.setFont(new Font("Segoe UI", Font.BOLD, 11));
        cartTable.setTableHeader(cartHeader);
        cartTable.getTableHeader().setReorderingAllowed(false);
        cartTable.getTableHeader().setResizingAllowed(false);

        for (int v = 0; v < cartTable.getColumnCount(); v++) {
            TableColumn col = cartTable.getColumnModel().getColumn(v);
            int mc = col.getModelIndex();

            col.setCellRenderer(new BodyRenderer(CART_ALIGN[mc]) {
                @Override
                protected void style(JTable t, Object value, int row, int column) {
                    if (mc == C_TOTAL) setFont(t.getFont().deriveFont(Font.BOLD));
                }
            });
        }

        cartTable.getColumnModel().getColumn(4).setMinWidth(0);
        cartTable.getColumnModel().getColumn(4).setMaxWidth(0);
        cartTable.getColumnModel().getColumn(4).setPreferredWidth(0);

        applyColumnWidths(cartTable, CART_RATIOS, 380);

        rightPanel.add(scroll(cartTable, CART_RATIOS), BorderLayout.CENTER);

        JPanel bottomCartPanel = new JPanel(new BorderLayout());
        bottomCartPanel.setOpaque(false);
        bottomCartPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        bottomCartPanel.add(buildSummaryPanel(), BorderLayout.NORTH);

        JPanel actionButtons = new JPanel(new GridLayout(1, 2, 10, 0));
        actionButtons.setOpaque(false);

        JButton btnRemove = createButton("Remove Item", DANGER, Color.WHITE);
        JButton btnCheckout = createButton("Checkout", PRIMARY, Color.WHITE);

        btnRemove.setBorder(new EmptyBorder(11, 0, 11, 0));
        btnCheckout.setBorder(new EmptyBorder(11, 0, 11, 0));

        actionButtons.add(btnRemove);
        actionButtons.add(btnCheckout);

        bottomCartPanel.add(actionButtons, BorderLayout.SOUTH);

        rightPanel.add(bottomCartPanel, BorderLayout.SOUTH);

        btnRemove.addActionListener(e -> removeSelectedCartItem());
        btnCheckout.addActionListener(e -> processCheckout());

        cartTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
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

        lblSubtotalVal = createSummaryValue(sym + "0.00", textColor);

        lblDiscountTitle = createSummaryLabel("Discount (0%):");
        lblDiscountVal = createSummaryValue("- " + sym + "0.00", textColor);

        lblTotalVal = createSummaryValue(sym + "0.00", PRIMARY);
        lblTotalVal.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JPanel summaryPanel = new JPanel(new GridLayout(0, 2, 5, 6));
        summaryPanel.setOpaque(false);

        summaryPanel.add(createSummaryLabel("Subtotal:"));
        summaryPanel.add(lblSubtotalVal);

        summaryPanel.add(lblDiscountTitle);
        summaryPanel.add(lblDiscountVal);

        JLabel lblTotalText = createSummaryLabel("TOTAL:");
        lblTotalText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTotalText.setForeground(textColor);

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, lineColor),
                new EmptyBorder(8, 0, 0, 0)
        ));

        totalRow.add(lblTotalText, BorderLayout.WEST);
        totalRow.add(lblTotalVal, BorderLayout.EAST);

        JPanel summaryBox = new JPanel(new BorderLayout(0, 8));
        summaryBox.setOpaque(false);
        summaryBox.setBorder(new EmptyBorder(5, 0, 10, 0));

        summaryBox.add(summaryPanel, BorderLayout.CENTER);
        summaryBox.add(totalRow, BorderLayout.SOUTH);

        JPanel summaryWrap = new JPanel(new BorderLayout());
        summaryWrap.setOpaque(false);
        summaryWrap.add(summaryBox, BorderLayout.CENTER);

        return summaryWrap;
    }


    private void styleTable(JTable t) {
        t.setRowHeight(36);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setShowGrid(true);
        t.setGridColor(GRID);
        t.setIntercellSpacing(new Dimension(1, 1));
        t.setFillsViewportHeight(true);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setSelectionBackground(PRIMARY_SOFT);
        t.setSelectionForeground(TEXT);
        t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    private JScrollPane scroll(JTable t, double[] ratios) {
        JScrollPane sp = new JScrollPane(t);
        sp.getViewport().setBackground(Color.WHITE);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));

        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JScrollBar bar = sp.getVerticalScrollBar();
        bar.setUI(new SlimScrollBarUI());
        bar.setOpaque(true);
        bar.setBackground(Color.WHITE);
        bar.setUnitIncrement(16);

        sp.getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                applyColumnWidths(t, ratios, sp.getViewport().getWidth());
            }
        });
        return sp;
    }

    private void applyColumnWidths(JTable t, double[] ratios, int totalWidth) {
        if (totalWidth <= 0) return;

        TableColumnModel cm = t.getColumnModel();

        int lastVisible = -1;
        for (int i = 0; i < cm.getColumnCount(); i++) {
            if (ratios[i] > 0) lastVisible = i;
        }

        int used = 0;
        for (int i = 0; i < cm.getColumnCount(); i++) {
            if (ratios[i] <= 0) continue;

            int w = (i == lastVisible) ? totalWidth - used : (int) Math.round(totalWidth * ratios[i]);
            cm.getColumn(i).setPreferredWidth(w);
            cm.getColumn(i).setWidth(w);
            used += w;
        }
    }

    private static void drawEmptyMessage(Graphics g, JTable t, String message) {
        if (t.getRowCount() > 0) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(new Color(150, 158, 166));
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(message, (t.getWidth() - fm.stringWidth(message)) / 2, t.getHeight() / 2);
        g2.dispose();
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

            String name = medicineModel.getValueAt(modelRow, M_NAME).toString();
            String priceText = medicineModel.getValueAt(modelRow, M_PRICE).toString();
            int availableStock = Integer.parseInt(medicineModel.getValueAt(modelRow, M_STOCK).toString());
            String status = medicineModel.getValueAt(modelRow, M_STATUS).toString();
            int medicineId = Integer.parseInt(medicineModel.getValueAt(modelRow, M_ID).toString());

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

                int currentQty = Integer.parseInt(cartModel.getValueAt(existingRow, 2).toString());

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

                cartModel.setValueAt(String.valueOf(newQty), existingRow, 2);
                cartModel.setValueAt(String.format("%s%.2f", sym, price * newQty), existingRow, 3);

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

            int existingId = Integer.parseInt(cartModel.getValueAt(i, 4).toString());

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

            int customerId = CustomerDAO.getCustomerIdByNameIgnoreCase(firstName, lastName);

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

            String orderNo = "ORD-" + System.currentTimeMillis();

            String currentDate = LocalDate.now().toString();

            List<SalesDAO.SaleItemData> items = new ArrayList<>();

            StringBuilder itemsDetails = new StringBuilder();

            for (int i = 0; i < cartModel.getRowCount(); i++) {

                int medicineId = Integer.parseInt(cartModel.getValueAt(i, 4).toString());

                int qtySold = Integer.parseInt(cartModel.getValueAt(i, 2).toString());

                double unitPrice = parseMoney(cartModel.getValueAt(i, 1).toString());

                String itemName = cartModel.getValueAt(i, 0).toString();

                String itemCategory = "N/A";
                for (int m = 0; m < medicineModel.getRowCount(); m++) {
                    if (medicineModel.getValueAt(m, M_NAME).toString().equals(itemName)) {
                        itemCategory = medicineModel.getValueAt(m, M_CATEGORY).toString();
                        break;
                    }
                }

                items.add(new SalesDAO.SaleItemData(medicineId, qtySold, unitPrice));

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

            SharedData.totalSalesToday = SalesDAO.getTodaySalesTotal();

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

            List<Medicine> medicines = MedicineDAO.getAllMedicines();

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
                    status = "Available"; 
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
                    "Unable to load medicines.\n" + e.getMessage(),
                    "Database Error",
                    true
            );
        }
    }

    private double calculateSubtotal() {

        double total = 0;

        for (int i = 0; i < cartModel.getRowCount(); i++) {
            total += parseMoney(cartModel.getValueAt(i, 3).toString());
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

        return Math.max(0, subtotal - discount);
    }

    private void updateSubtotal() {

        double subtotal = calculateSubtotal();
        double discount = calculateDiscountAmount(subtotal);
        double total = Math.max(0, subtotal - discount);
        String sym = SharedData.currencySymbol + " ";

        lblSubtotalVal.setText(String.format("%s%.2f", sym, subtotal));

        if (activeCheckoutCustomer != null && activeCheckoutCustomer.getDiscountType() != null &&
           !activeCheckoutCustomer.getDiscountType().trim().equalsIgnoreCase("No Discount") &&
           !activeCheckoutCustomer.getDiscountType().trim().isEmpty()) {

            String dtype = activeCheckoutCustomer.getDiscountType().trim();
            double dpct = activeCheckoutCustomer.getDiscountPercent();
            if (dpct <= 0) dpct = 20.0;

            lblDiscountTitle.setText(dtype + " (" + (int) dpct + "%):");
        } else {
            lblDiscountTitle.setText("Discount (0%):");
        }

        lblDiscountVal.setText(String.format("- %s%.2f", sym, discount));

        lblTotalVal.setText(String.format("%s%.2f", sym, total));
    }

    private double parseMoney(String value) {

        if (value == null || value.trim().isEmpty()) {
            return 0;
        }

        return Double.parseDouble(value.replaceAll("[^0-9.]", "").trim());
    }


    private JButton createButton(String text, Color background, Color foreground) {

        JButton button = new JButton(text);

        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8, 14, 8, 14));

        Color hover = darken(background, 0.88);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { button.setBackground(hover); }
            @Override
            public void mouseExited(MouseEvent e) { button.setBackground(background); }
        });

        return button;
    }

    private static Color darken(Color c, double factor) {
        return new Color(
                (int) (c.getRed() * factor),
                (int) (c.getGreen() * factor),
                (int) (c.getBlue() * factor)
        );
    }

    private JLabel createSummaryLabel(String text) {

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(90, 95, 100));

        return label;
    }

    private JLabel createSummaryValue(String text, Color color) {

        JLabel label = new JLabel(text, SwingConstants.RIGHT);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(color);

        return label;
    }


    private static class SearchField extends JTextField {
        private final String hint;
        private boolean focused = false;

        SearchField(String hint) {
            this.hint = hint;
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setForeground(TEXT);
            setCaretColor(PRIMARY);
            updateBorder();

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    focused = true;
                    updateBorder();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    focused = false;
                    updateBorder();
                }
            });
        }

        private void updateBorder() {
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(focused ? PRIMARY : new Color(200, 205, 210)),
                    new EmptyBorder(7, 10, 7, 10)
            ));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (!getText().isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(new Color(160, 167, 174));
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(hint, getInsets().left, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();
        }
    }


    private static class BodyRenderer extends DefaultTableCellRenderer {

        BodyRenderer(int alignment) {
            setHorizontalAlignment(alignment);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(t, value, isSelected, false, row, column);

            setBorder(new EmptyBorder(0, CELL_PAD, 0, CELL_PAD));
            setFont(t.getFont());
            setForeground(TEXT);
            setBackground(isSelected ? t.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : ROW_ALT));

            setToolTipText(value == null ? null : value.toString());

            style(t, value, row, column);
            return this;
        }

        protected void style(JTable t, Object value, int row, int column) {
        }
    }


    private static class GroupHeader extends JTableHeader {
        private final String[] groups;
        private final int[] alignByModel;

        GroupHeader(TableColumnModel cm, String[] groups, int[] alignByModel, int height) {
            super(cm);
            this.groups = groups;
            this.alignByModel = alignByModel;
            setPreferredSize(new Dimension(0, height));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(HEADER_BG);
            g.fillRect(0, 0, getWidth(), getHeight());

            int n = columnModel.getColumnCount();
            int h = getHeight();
            int half = h / 2;

            int lastVisible = -1;
            for (int k = 0; k < n; k++) {
                if (columnModel.getColumn(k).getWidth() > 0) lastVisible = k;
            }

            int i = 0;
            while (i < n) {
                String grp = i < groups.length ? groups[i] : null;

                if (grp == null) {
                    Rectangle r = getHeaderRect(i);
                    if (r.width > 0) {
                        label(g, title(i), r.x, 0, r.width, h, align(i));
                        if (i != lastVisible) divider(g, r.x + r.width - 1, 0, h);
                    }
                    i++;
                } else {
                    int j = i;
                    while (j + 1 < n && j + 1 < groups.length && grp.equals(groups[j + 1])) j++;

                    Rectangle a = getHeaderRect(i);
                    Rectangle b = getHeaderRect(j);
                    int spanW = b.x + b.width - a.x;

                    label(g, grp, a.x, 0, spanW, half, JLabel.CENTER);
                    g.setColor(GRID);
                    g.drawLine(a.x, half - 1, a.x + spanW - 1, half - 1);
                    if (j != lastVisible) divider(g, b.x + b.width - 1, 0, half);

                    for (int k = i; k <= j; k++) {
                        Rectangle r = getHeaderRect(k);
                        if (r.width > 0) {
                            label(g, title(k), r.x, half, r.width, h - half, align(k));
                            if (k != lastVisible) divider(g, r.x + r.width - 1, half, h);
                        }
                    }
                    i = j + 1;
                }
            }

            g.setColor(PRIMARY);
            g.fillRect(0, h - 2, getWidth(), 2);
            g.dispose();
        }

        private String title(int viewCol) {
            return String.valueOf(columnModel.getColumn(viewCol).getHeaderValue());
        }

        private int align(int viewCol) {
            return alignByModel[columnModel.getColumn(viewCol).getModelIndex()];
        }

        private void divider(Graphics2D g, int x, int y1, int y2) {
            g.setColor(GRID);
            g.drawLine(x, y1, x, y2);
        }

        private void label(Graphics2D g, String text, int x, int y, int w, int h, int alignment) {
            g.setColor(HEADER_TEXT);
            g.setFont(getFont());
            FontMetrics fm = g.getFontMetrics();

            int max = w - CELL_PAD * 2;
            String shown = text;
            if (fm.stringWidth(shown) > max) {
                while (shown.length() > 1 && fm.stringWidth(shown + "...") > max) {
                    shown = shown.substring(0, shown.length() - 1);
                }
                shown = shown.trim() + "...";
            }

            int textWidth = fm.stringWidth(shown);
            int tx;
            if (alignment == JLabel.CENTER) {
                tx = x + (w - textWidth) / 2;
            } else if (alignment == JLabel.RIGHT) {
                tx = x + w - CELL_PAD - textWidth;
            } else {
                tx = x + CELL_PAD;
            }
            int ty = y + (h + fm.getAscent() - fm.getDescent()) / 2;
            g.drawString(shown, tx, ty);
        }
    }


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

            Window owner = SwingUtilities.getWindowAncestor(parent);

            JDialog dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setLayout(new BorderLayout());
            dialog.setResizable(false);

            JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
            centerPanel.setBackground(Color.WHITE);

            JLabel lblMsg = new JLabel("<html>" + htmlMessage + "</html>");
            lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblMsg.setForeground(new Color(70, 75, 80));

            centerPanel.add(lblMsg);

            JScrollPane scrollPane = new JScrollPane(centerPanel);
            scrollPane.setBorder(null);
            scrollPane.getViewport().setBackground(Color.WHITE);

            dialog.add(scrollPane, BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
            bottomPanel.setBackground(new Color(248, 249, 250));
            bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));

            JButton btnOk = new JButton("OK");
            btnOk.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnOk.setBackground(PRIMARY);
            btnOk.setForeground(Color.WHITE);
            btnOk.setFocusPainted(false);
            btnOk.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
            btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnOk.addActionListener(e -> dialog.dispose());

            bottomPanel.add(btnOk);

            dialog.add(bottomPanel, BorderLayout.SOUTH);

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
            bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));

            JButton btnOk = new JButton("OK");
            btnOk.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnOk.setBackground(PRIMARY);
            btnOk.setForeground(Color.WHITE);
            btnOk.setFocusPainted(false);
            btnOk.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));

            JButton btnCancel = new JButton("Cancel");
            btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnCancel.setBackground(DANGER);
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