package ui;

import db.MedicineDAO;
import models.Medicine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class POSFrame extends JPanel {

    private JTable medicineTable;
    private JTable cartTable;
    private DefaultTableModel medicineModel;
    private DefaultTableModel cartModel;
    private JTextField searchField;

    private JLabel lblSubtotalVal;
    private JCheckBox chkPwd;
    private JLabel lblVatExemptVal;
    private JLabel lblDiscountVal;
    private JLabel lblTotalVal;

    private static final double VAT_RATE = 0.12;
    private static final double PWD_DISCOUNT_RATE = 0.20;
    private static final boolean PWD_VAT_EXEMPT = true; // false kung 20% discount lang

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

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 215, 220)),
            new EmptyBorder(12, 12, 12, 12)
        ));

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel lblSearch = new JLabel("Search Medicine: ");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSearch.setForeground(new Color(70, 75, 80));

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 205, 210)),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        JButton btnSearch = new JButton("Search");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSearch.setBackground(new Color(240, 242, 245));
        btnSearch.setForeground(new Color(70, 75, 80));
        btnSearch.setFocusPainted(false);
        btnSearch.setBorder(BorderFactory.createLineBorder(new Color(190, 195, 200)));
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));

        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);
        leftPanel.add(searchPanel, BorderLayout.NORTH);

        String[] medColumns = {"Medicine Name", "Category", "Price", "Stock"};

        medicineModel = new DefaultTableModel(medColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        loadMedicinesToPOS("");

        medicineTable = new JTable(medicineModel);
        medicineTable.setRowHeight(32);
        medicineTable.setShowVerticalLines(false);
        medicineTable.setShowHorizontalLines(true);
        medicineTable.setGridColor(new Color(235, 238, 242));
        medicineTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        medicineTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        medicineTable.getTableHeader().setBackground(new Color(248, 249, 250));
        medicineTable.getTableHeader().setForeground(new Color(80, 85, 90));

        JScrollPane medScroll = new JScrollPane(medicineTable);
        medScroll.getViewport().setBackground(Color.WHITE);
        medScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));
        leftPanel.add(medScroll, BorderLayout.CENTER);

        JButton btnAddToCart = new JButton("Add to Cart");
        btnAddToCart.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAddToCart.setBackground(new Color(51, 122, 183));
        btnAddToCart.setForeground(Color.WHITE);
        btnAddToCart.setFocusPainted(false);
        btnAddToCart.setBorder(new EmptyBorder(10, 0, 10, 0));
        btnAddToCart.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel btnAddWrapper = new JPanel(new BorderLayout());
        btnAddWrapper.setOpaque(false);
        btnAddWrapper.setBorder(new EmptyBorder(10, 0, 0, 0));
        btnAddWrapper.add(btnAddToCart, BorderLayout.CENTER);
        leftPanel.add(btnAddWrapper, BorderLayout.SOUTH);

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

        String[] cartColumns = {"Item Name", "Price", "Qty", "Total"};
        cartModel = new DefaultTableModel(new Object[][]{}, cartColumns);
        cartTable = new JTable(cartModel);
        cartTable.setRowHeight(32);
        cartTable.setShowVerticalLines(false);
        cartTable.setShowHorizontalLines(true);
        cartTable.setGridColor(new Color(235, 238, 242));
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cartTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        cartTable.getTableHeader().setBackground(new Color(248, 249, 250));
        cartTable.getTableHeader().setForeground(new Color(80, 85, 90));

        JScrollPane cartScroll = new JScrollPane(cartTable);
        cartScroll.getViewport().setBackground(Color.WHITE);
        cartScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));
        rightPanel.add(cartScroll, BorderLayout.CENTER);

        JPanel bottomCartPanel = new JPanel(new BorderLayout());
        bottomCartPanel.setOpaque(false);
        bottomCartPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        // ===== Summary panel (PWD checkbox + computation) =====
        JPanel summaryPanel = new JPanel(new GridLayout(0, 2, 5, 4));
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        chkPwd = new JCheckBox("PWD Customer");
        chkPwd.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chkPwd.setOpaque(false);
        chkPwd.setForeground(new Color(70, 75, 80));
        chkPwd.setFocusPainted(false);
        chkPwd.addActionListener(e -> updateSubtotal());

        lblSubtotalVal = createSummaryValue("₱ 0.00", new Color(90, 95, 100));
        lblVatExemptVal = createSummaryValue("- ₱ 0.00", new Color(90, 95, 100));
        lblDiscountVal = createSummaryValue("- ₱ 0.00", new Color(220, 53, 69));
        lblTotalVal = createSummaryValue("₱ 0.00", new Color(40, 167, 69));
        lblTotalVal.setFont(new Font("Segoe UI", Font.BOLD, 16));

        summaryPanel.add(chkPwd);
        summaryPanel.add(new JLabel());
        summaryPanel.add(createSummaryLabel("Subtotal:"));
        summaryPanel.add(lblSubtotalVal);
        summaryPanel.add(createSummaryLabel("VAT Exemption:"));
        summaryPanel.add(lblVatExemptVal);
        summaryPanel.add(createSummaryLabel("PWD Discount (20%):"));
        summaryPanel.add(lblDiscountVal);
        summaryPanel.add(createSummaryLabel("TOTAL:"));
        summaryPanel.add(lblTotalVal);

        bottomCartPanel.add(summaryPanel, BorderLayout.NORTH);

        JPanel actionButtons = new JPanel(new GridLayout(1, 2, 10, 0));
        actionButtons.setOpaque(false);

        JButton btnRemove = new JButton("Remove Item");
        btnRemove.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRemove.setBackground(new Color(220, 53, 69));
        btnRemove.setForeground(Color.WHITE);
        btnRemove.setFocusPainted(false);
        btnRemove.setBorder(new EmptyBorder(10, 0, 10, 0));
        btnRemove.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnCheckout = new JButton("Checkout");
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCheckout.setBackground(new Color(40, 167, 69));
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setFocusPainted(false);
        btnCheckout.setBorder(new EmptyBorder(10, 0, 10, 0));
        btnCheckout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        actionButtons.add(btnRemove);
        actionButtons.add(btnCheckout);
        bottomCartPanel.add(actionButtons, BorderLayout.SOUTH);

        rightPanel.add(bottomCartPanel, BorderLayout.SOUTH);

        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);
        add(contentPanel, BorderLayout.CENTER);

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadMedicinesToPOS("");
            }
        });

        btnSearch.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            loadMedicinesToPOS(keyword);
        });

        btnAddToCart.addActionListener(e -> {
            int selectedRow = medicineTable.getSelectedRow();
            if (selectedRow != -1) {
                String name = (String) medicineModel.getValueAt(selectedRow, 0);
                String priceStr = (String) medicineModel.getValueAt(selectedRow, 2);
                int availableStock = Integer.parseInt(medicineModel.getValueAt(selectedRow, 3).toString());

                double price = Double.parseDouble(priceStr.replace("₱", "").trim());

                int currentQtyInCart = 0;
                for (int i = 0; i < cartModel.getRowCount(); i++) {
                    if (cartModel.getValueAt(i, 0).equals(name)) {
                        currentQtyInCart = Integer.parseInt((String) cartModel.getValueAt(i, 2));
                        break;
                    }
                }

                if (availableStock <= 0 || currentQtyInCart + 1 > availableStock) {
                    CustomDialog.showMessage(this, "Insufficient stock available for this medicine!", "Stock Error", true);
                    return;
                }

                boolean found = false;
                for (int i = 0; i < cartModel.getRowCount(); i++) {
                    if (cartModel.getValueAt(i, 0).equals(name)) {
                        int qty = currentQtyInCart + 1;
                        cartModel.setValueAt(String.valueOf(qty), i, 2);
                        cartModel.setValueAt(String.format("₱%.2f", price * qty), i, 3);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    cartModel.addRow(new Object[]{name, String.format("₱%.2f", price), "1", String.format("₱%.2f", price)});
                }
                updateSubtotal();
            } else {
                CustomDialog.showMessage(this, "Please select a medicine from the list first.", "Selection Error", true);
            }
        });

        btnRemove.addActionListener(e -> {
            int selectedCartRow = cartTable.getSelectedRow();
            if (selectedCartRow != -1) {
                cartModel.removeRow(selectedCartRow);
                updateSubtotal();
            } else {
                CustomDialog.showMessage(this, "Please select an item from the cart to remove.", "Selection Error", true);
            }
        });

        btnCheckout.addActionListener(e -> {
            try {
                if (cartModel.getRowCount() > 0) {
                    String customerName = CustomDialog.showInput(this, "Enter Customer Name:", "Customer Details");

                    if (customerName != null && !customerName.trim().isEmpty()) {

                        boolean isPwd = chkPwd.isSelected();
                        String pwdId = "";

                        if (isPwd) {
                            String input = CustomDialog.showInput(this, "Enter PWD ID Number:", "PWD Verification");
                            if (input == null) return; 
                            if (input.trim().isEmpty()) {
                                CustomDialog.showMessage(this, "PWD ID number is required for PWD discount.", "Validation Error", true);
                                return;
                            }
                            pwdId = input.trim();
                        }

                        double totalAmount = calculateTotal();
                        String orderNo = "ORD-" + (System.currentTimeMillis() % 10000);
                        String currentDate = LocalDate.now().toString();

                        for (int i = 0; i < cartModel.getRowCount(); i++) {
                            String itemName = (String) cartModel.getValueAt(i, 0);
                            int qtySold = Integer.parseInt((String) cartModel.getValueAt(i, 2));
                            MedicineDAO.decreaseStock(itemName, qtySold);
                        }

                        SharedData.totalSalesToday += totalAmount;
                        SharedData.addSale(orderNo, currentDate, String.format("%.2f", totalAmount),
                                           customerName.trim(), isPwd, pwdId);
                        DashboardPanel.refreshDashboardData();

                        CustomDialog.showMessage(this, "Checkout successful! Stock updated and transferred to Dashboard.", "Success", false);
                        cartModel.setRowCount(0);
                        chkPwd.setSelected(false);
                        updateSubtotal();
                        loadMedicinesToPOS("");
                    } else if (customerName != null) {
                        CustomDialog.showMessage(this, "Customer name is required.", "Validation Error", true);
                    }
                } else {
                    CustomDialog.showMessage(this, "The cart is empty.", "Cart Error", true);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                CustomDialog.showMessage(this, "Checkout Error: " + ex.getMessage(), "Error", true);
            }
        });
    }

    private void loadMedicinesToPOS(String filterKeyword) {
        medicineModel.setRowCount(0);
        List<Medicine> medicines = MedicineDAO.getAllMedicines();

        for (Medicine med : medicines) {
            if (filterKeyword.isEmpty() ||
                med.getName().toLowerCase().contains(filterKeyword.toLowerCase()) ||
                med.getMedicineCategory().toLowerCase().contains(filterKeyword.toLowerCase())) {

                medicineModel.addRow(new Object[]{
                    med.getName(),
                    med.getMedicineCategory(),
                    String.format("₱%.2f", med.getSellPrice()),
                    med.getStock()
                });
            }
        }
    }


    private double calculateSubtotal() {
        double total = 0;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            String totalStr = (String) cartModel.getValueAt(i, 3);
            total += Double.parseDouble(totalStr.replace("₱", "").trim());
        }
        return total;
    }

    private double calculateVatExemption(double subtotal) {
        if (!chkPwd.isSelected() || !PWD_VAT_EXEMPT) return 0;
        return subtotal - (subtotal / (1 + VAT_RATE));
    }

    private double calculatePwdDiscount(double subtotal) {
        if (!chkPwd.isSelected()) return 0;
        double base = subtotal - calculateVatExemption(subtotal);
        return base * PWD_DISCOUNT_RATE;
    }

    private double calculateTotal() {
        double subtotal = calculateSubtotal();
        return subtotal - calculateVatExemption(subtotal) - calculatePwdDiscount(subtotal);
    }

    private void updateSubtotal() {
        double subtotal = calculateSubtotal();
        lblSubtotalVal.setText(String.format("₱ %.2f", subtotal));
        lblVatExemptVal.setText(String.format("- ₱ %.2f", calculateVatExemption(subtotal)));
        lblDiscountVal.setText(String.format("- ₱ %.2f", calculatePwdDiscount(subtotal)));
        lblTotalVal.setText(String.format("₱ %.2f", calculateTotal()));
    }

    private JLabel createSummaryLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(90, 95, 100));
        return lbl;
    }

    private JLabel createSummaryValue(String text, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.RIGHT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(color);
        return lbl;
    }

    private static class CustomDialog {
        private static String inputResult = null;

        public static void showMessage(Component parent, String message, String title, boolean isWarning) {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), title, true);
            dialog.setSize(450, 180);
            dialog.setLocationRelativeTo(parent);
            dialog.setLayout(new BorderLayout());

            JPanel topHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
            topHeader.setBackground(new Color(248, 249, 250));
            topHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 230)));
            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblTitle.setForeground(isWarning ? new Color(217, 119, 6) : new Color(50, 60, 70));
            topHeader.add(lblTitle);
            dialog.add(topHeader, BorderLayout.NORTH);

            JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
            centerPanel.setBackground(Color.WHITE);
            JLabel lblMsg = new JLabel(message);
            lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblMsg.setForeground(new Color(70, 75, 80));
            centerPanel.add(lblMsg);
            dialog.add(centerPanel, BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
            bottomPanel.setBackground(new Color(248, 249, 250));
            bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 230)));

            JButton btnOk = new JButton("OK");
            btnOk.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnOk.setBackground(new Color(13, 148, 136));
            btnOk.setForeground(Color.WHITE);
            btnOk.setFocusPainted(false);
            btnOk.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
            btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnOk.addActionListener(e -> dialog.dispose());

            bottomPanel.add(btnOk);
            dialog.add(bottomPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);
        }

        public static String showInput(Component parent, String message, String title) {
            inputResult = null;
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), title, true);
            dialog.setSize(450, 200);
            dialog.setLocationRelativeTo(parent);
            dialog.setLayout(new BorderLayout());

            JPanel topHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
            topHeader.setBackground(new Color(248, 249, 250));
            topHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 230)));
            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblTitle.setForeground(new Color(50, 60, 70));
            topHeader.add(lblTitle);
            dialog.add(topHeader, BorderLayout.NORTH);

            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.setBackground(Color.WHITE);
            centerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

            JLabel lblMsg = new JLabel(message);
            lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblMsg.setForeground(new Color(70, 75, 80));
            lblMsg.setAlignmentX(Component.LEFT_ALIGNMENT);
            centerPanel.add(lblMsg);

            centerPanel.add(Box.createVerticalStrut(10));

            JTextField textField = new JTextField();
            textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
            textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210)),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
            ));
            textField.setAlignmentX(Component.LEFT_ALIGNMENT);
            centerPanel.add(textField);

            dialog.add(centerPanel, BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            bottomPanel.setBackground(new Color(248, 249, 250));
            bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 230)));

            JButton btnOk = new JButton("OK");
            btnOk.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnOk.setBackground(new Color(13, 148, 136));
            btnOk.setForeground(Color.WHITE);
            btnOk.setFocusPainted(false);
            btnOk.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
            btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JButton btnCancel = new JButton("Cancel");
            btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnCancel.setBackground(new Color(220, 53, 69));
            btnCancel.setForeground(Color.WHITE);
            btnCancel.setFocusPainted(false);
            btnCancel.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
            btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnOk.addActionListener(e -> {
                inputResult = textField.getText().trim();
                dialog.dispose();
            });

            btnCancel.addActionListener(e -> {
                inputResult = null;
                dialog.dispose();
            });

            bottomPanel.add(btnOk);
            bottomPanel.add(btnCancel);
            dialog.add(bottomPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);

            return inputResult;
        }
    }
}