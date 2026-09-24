package ui;

import db.CustomerDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class POSFrame extends JPanel {

    private JTable medicineTable;
    private JTable cartTable;
    private DefaultTableModel medicineModel;
    private DefaultTableModel cartModel;
    private JTextField searchField;
    private JLabel lblSubtotalVal;

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

        loadMedicinesToPOS();

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

        JPanel subtotalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        subtotalPanel.setOpaque(false);

        JLabel lblSubtotalText = new JLabel("Subtotal: ");
        lblSubtotalText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSubtotalText.setForeground(new Color(90, 95, 100));

        lblSubtotalVal = new JLabel("₱ 0.00");
        lblSubtotalVal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSubtotalVal.setForeground(new Color(40, 167, 69));

        subtotalPanel.add(lblSubtotalText);
        subtotalPanel.add(lblSubtotalVal);
        bottomCartPanel.add(subtotalPanel, BorderLayout.NORTH);

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
                loadMedicinesToPOS();
            }
        });

        btnAddToCart.addActionListener(e -> {
            int selectedRow = medicineTable.getSelectedRow();

            if (selectedRow != -1) {
                String name = (String) medicineModel.getValueAt(selectedRow, 0);
                String priceStr = (String) medicineModel.getValueAt(selectedRow, 2);

                double price = Double.parseDouble(
                    priceStr.replace("₱", "").trim()
                );

                boolean found = false;

                for (int i = 0; i < cartModel.getRowCount(); i++) {
                    if (cartModel.getValueAt(i, 0).equals(name)) {

                        int qty = Integer.parseInt(
                            (String) cartModel.getValueAt(i, 2)
                        ) + 1;

                        cartModel.setValueAt(
                            String.valueOf(qty), i, 2
                        );

                        cartModel.setValueAt(
                            String.format("₱%.2f", price * qty),
                            i, 3
                        );

                        found = true;
                        break;
                    }
                }

                if (!found) {
                    cartModel.addRow(new Object[]{
                        name,
                        String.format("₱%.2f", price),
                        "1",
                        String.format("₱%.2f", price)
                    });
                }

                updateSubtotal();

            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Please select a medicine from the list first."
                );
            }
        });

        btnRemove.addActionListener(e -> {
            int selectedCartRow = cartTable.getSelectedRow();

            if (selectedCartRow != -1) {

                String priceStr = (String) cartModel.getValueAt(
                    selectedCartRow, 1
                );

                int currentQty = Integer.parseInt(
                    (String) cartModel.getValueAt(selectedCartRow, 2)
                );

                if (currentQty > 1) {

                    double unitPrice = Double.parseDouble(
                        priceStr.replace("₱", "").trim()
                    );

                    int newQty = currentQty - 1;

                    cartModel.setValueAt(
                        String.valueOf(newQty),
                        selectedCartRow,
                        2
                    );

                    cartModel.setValueAt(
                        String.format("₱%.2f", unitPrice * newQty),
                        selectedCartRow,
                        3
                    );

                } else {
                    cartModel.removeRow(selectedCartRow);
                }

                updateSubtotal();

            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Please select an item from the cart to remove."
                );
            }
        });

        btnCheckout.addActionListener(e -> {
            try {

                if (cartModel.getRowCount() > 0) {

                    String customerName = JOptionPane.showInputDialog(
                        this,
                        "Enter Customer Name:",
                        "Customer Details",
                        JOptionPane.QUESTION_MESSAGE
                    );

                    if (customerName != null &&
                        !customerName.trim().isEmpty()) {

                        double totalAmount = calculateTotal();

                        String orderNo =
                            "ORD-" + (System.currentTimeMillis() % 10000);

                        String currentDate =
                            LocalDate.now().toString();

                        int customerId = CustomerDAO.getCustomerIdByName(customerName.trim());

                        if (customerId == -1) {
    JOptionPane.showMessageDialog(
        this,
        "Customer not found.\nPlease make sure the customer is already registered.",
        "Customer Not Found",
        JOptionPane.WARNING_MESSAGE
    );
    return;
}

                         CustomerDAO.recordPurchase(
                         customerId,
                         orderNo,
                         currentDate,
                         totalAmount
                       );

                         CustomerDAO.updateLastPurchaseDate(
                         customerId,
                         currentDate
                      );

                        SharedData.totalSalesToday += totalAmount;

                        SharedData.addSale(
                        orderNo,
                        currentDate,
                        String.format("%.2f", totalAmount),
                        customerName.trim()
                        );

                        DashboardPanel.refreshDashboardData();

                        JOptionPane.showMessageDialog(
                            this,
                            "Checkout successful! Transferred to Dashboard."
                        );

                        cartModel.setRowCount(0);
                        updateSubtotal();

                    } else {

                        JOptionPane.showMessageDialog(
                            this,
                            "Checkout cancelled. Customer name is required."
                        );
                    }

                } else {

                    JOptionPane.showMessageDialog(
                        this,
                        "The cart is empty."
                    );
                }

            } catch (Exception ex) {

                ex.printStackTrace();

                JOptionPane.showMessageDialog(
                    this,
                    "Checkout Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        btnSearch.addActionListener(e -> {

            String searchInput = searchField.getText().trim();

            if (searchInput.isEmpty()) {

                JOptionPane.showMessageDialog(
                    this,
                    "Please enter a medicine name to search.",
                    "Incorrect Input",
                    JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (searchInput.matches("\\d+")) {

                JOptionPane.showMessageDialog(
                    this,
                    "Incorrect Input (Please search using Name)",
                    "Incorrect Input",
                    JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            boolean itemFound = false;

            for (int i = 0; i < medicineModel.getRowCount(); i++) {

                String name =
                    (String) medicineModel.getValueAt(i, 0);

                if (name.equalsIgnoreCase(searchInput) ||
                    name.toLowerCase().equalsIgnoreCase(
                        searchInput.toLowerCase()
                    )) {

                    itemFound = true;

                    medicineTable.setRowSelectionInterval(i, i);

                    medicineTable.scrollRectToVisible(
                        medicineTable.getCellRect(i, 0, true)
                    );

                    Object stockObj =
                        medicineModel.getValueAt(i, 3);

                    int stock =
                        Integer.parseInt(stockObj.toString().trim());

                    if (stock <= 0) {

                        JOptionPane.showMessageDialog(
                            this,
                            "Medicine '" + name + "' is Out of Stock!",
                            "Out of Stock",
                            JOptionPane.ERROR_MESSAGE
                        );

                    } else {

                        JOptionPane.showMessageDialog(
                            this,
                            "Found: " + name +
                            "\nPrice: " + medicineModel.getValueAt(i, 2) +
                            "\nStock: " + stock,
                            "Item Found",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    }

                    break;
                }
            }

            if (!itemFound) {

                JOptionPane.showMessageDialog(
                    this,
                    "Medicine '" + searchInput + "' Not Found.",
                    "Not Found",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }

    private void loadMedicinesToPOS() {

        medicineModel.setRowCount(0);

        if (!SharedData.medicineList.isEmpty()) {

            for (String[] med : SharedData.medicineList) {

                medicineModel.addRow(new Object[]{
                    med[0],
                    med[1],
                    med[3],
                    med[4]
                });
            }

        } else {

            medicineModel.addRow(new Object[]{
                "Biogesic 500mg",
                "Tablet",
                "₱6.00",
                "150"
            });

            medicineModel.addRow(new Object[]{
                "Neozep Forte",
                "Tablet",
                "₱7.75",
                "120"
            });
        }
    }

    private double calculateTotal() {

        double total = 0;

        for (int i = 0; i < cartModel.getRowCount(); i++) {

            String totalStr =
                (String) cartModel.getValueAt(i, 3);

            total += Double.parseDouble(
                totalStr.replace("₱", "").trim()
            );
        }

        return total;
    }

    private void updateSubtotal() {

        lblSubtotalVal.setText(
            String.format("₱ %.2f", calculateTotal())
        );
    }
}
