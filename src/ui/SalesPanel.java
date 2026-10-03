package ui;

import db.SalesDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class SalesPanel extends JPanel {

    private JTable salesTable;
    private DefaultTableModel tableModel;

    private JLabel lblGrossSales;
    private JLabel lblExpenses;
    private JLabel lblNetProfit;
    private JComboBox<String> cmbTimeframe;
    private JComboBox<String> cmbMonth;
    private JComboBox<Integer> cmbYear;

    public SalesPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));
        setBorder(new EmptyBorder(20, 25, 20, 25));

        // Header Panel with Title & Timeframe Selectors
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Sales & Profit Overview");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(60, 65, 70));

        // Filter Controls Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterPanel.setOpaque(false);

        cmbTimeframe = new JComboBox<>(new String[]{"This Month", "This Year"});
        cmbTimeframe.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cmbTimeframe.setBackground(Color.WHITE);

        // Month Selector
        cmbMonth = new JComboBox<>();
        cmbMonth.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbMonth.setBackground(Color.WHITE);
        for (int m = 1; m <= 12; m++) {
            cmbMonth.addItem(Month.of(m).getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        }
        cmbMonth.setSelectedIndex(LocalDate.now().getMonthValue() - 1);

        // Year Selector (Current year and past 5 years)
        cmbYear = new JComboBox<>();
        cmbYear.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbYear.setBackground(Color.WHITE);
        int currentYear = LocalDate.now().getYear();
        for (int y = currentYear; y >= currentYear - 5; y--) {
            cmbYear.addItem(y);
        }

        // Add Listeners
        cmbTimeframe.addActionListener(e -> {
            cmbMonth.setEnabled(!cmbTimeframe.getSelectedItem().toString().equalsIgnoreCase("This Year"));
            refreshSalesData();
        });
        cmbMonth.addActionListener(e -> refreshSalesData());
        cmbYear.addActionListener(e -> refreshSalesData());

        filterPanel.add(new JLabel("View:"));
        filterPanel.add(cmbTimeframe);
        filterPanel.add(new JLabel("Month:"));
        filterPanel.add(cmbMonth);
        filterPanel.add(new JLabel("Year:"));
        filterPanel.add(cmbYear);

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(filterPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Summary Cards Section (3 Metrics: Gross, Expense, Net Profit)
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        summaryPanel.setOpaque(false);

        String sym = SharedData.currencySymbol + " ";

        lblGrossSales = new JLabel(sym + "0.00");
        lblExpenses = new JLabel(sym + "0.00");
        lblNetProfit = new JLabel(sym + "0.00");

        summaryPanel.add(createSummaryCard("Gross Sales", lblGrossSales, new Color(40, 167, 69)));
        summaryPanel.add(createSummaryCard("Total Expenses", lblExpenses, new Color(220, 53, 69)));
        summaryPanel.add(createSummaryCard("Net Profit", lblNetProfit, new Color(13, 148, 136)));

        mainPanel.add(summaryPanel, BorderLayout.NORTH);

        // Table & Toolbar Section
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createLineBorder(new Color(205, 210, 215)));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        toolbar.setBackground(Color.WHITE);

        JButton btnAdd = new JButton("+ Add Sale");
        styleButton(btnAdd, new Color(13, 148, 136));

        JButton btnDelete = new JButton("Delete Selected");
        styleButton(btnDelete, new Color(220, 53, 69));

        JButton btnRefresh = new JButton("Refresh");
        styleButton(btnRefresh, new Color(80, 90, 100));

        toolbar.add(btnAdd);
        toolbar.add(btnDelete);
        toolbar.add(btnRefresh);

        tablePanel.add(toolbar, BorderLayout.NORTH);

        String[] columns = {"ID", "Date", "Description", "Gross Amount"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        salesTable = new JTable(tableModel);
        salesTable.setRowHeight(32);
        salesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        salesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        salesTable.getTableHeader().setBackground(new Color(248, 249, 250));
        salesTable.getTableHeader().setForeground(new Color(80, 85, 90));
        salesTable.setShowVerticalLines(false);
        salesTable.setGridColor(new Color(235, 238, 242));

        salesTable.setSelectionBackground(new Color(210, 215, 220));
        salesTable.setSelectionForeground(Color.BLACK);

        salesTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        salesTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        salesTable.getColumnModel().getColumn(2).setPreferredWidth(400);
        salesTable.getColumnModel().getColumn(3).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Event Listeners
        btnAdd.addActionListener(e -> showAddSaleDialog());
        btnDelete.addActionListener(e -> deleteSelectedSale());
        btnRefresh.addActionListener(e -> refreshSalesData());

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                refreshSalesData();
            }
        });

        refreshSalesData();
    }

    private JPanel createSummaryCard(String title, JLabel value, Color highlightColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 210, 215)),
                new EmptyBorder(15, 18, 15, 18)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(new Color(100, 105, 110));

        value.setFont(new Font("Segoe UI", Font.BOLD, 22));
        value.setForeground(highlightColor);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(value, BorderLayout.CENTER);

        return panel;
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(9, 15, 9, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void showAddSaleDialog() {
        JTextField descriptionField = new JTextField();
        JTextField amountField = new JTextField();

        descriptionField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));

        JLabel lblDesc = new JLabel("Sale Description / Item:");
        lblDesc.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDesc.setForeground(new Color(70, 75, 80));
        panel.add(lblDesc);

        descriptionField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panel.add(descriptionField);

        JLabel lblAmt = new JLabel("Gross Amount:");
        lblAmt.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAmt.setForeground(new Color(70, 75, 80));
        panel.add(lblAmt);

        amountField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panel.add(amountField);

        boolean okClicked = showCustomFormDialog(panel, "Add Sale");
        if (!okClicked) return;

        String description = descriptionField.getText().trim();
        String amountText = amountField.getText().trim().replaceAll("[^0-9.]", "");

        if (description.isEmpty()) {
            showCustomDialog("Sale description is required.", "Validation Error", true);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showCustomDialog("Please enter a valid gross amount.", "Validation Error", true);
            return;
        }

        if (amount <= 0) {
            showCustomDialog("Amount must be greater than zero.", "Validation Error", true);
            return;
        }

        try {
            SalesDAO.addSale(description, amount, LocalDate.now().toString());
            refreshSalesData();
            DashboardPanel.refreshDashboardData();
            showCustomDialog("Sale saved successfully.", "Success", false);
        } catch (Exception e) {
            e.printStackTrace();
            showCustomDialog("Unable to save sale:\n" + e.getMessage(), "Database Error", true);
        }
    }

    private void deleteSelectedSale() {
        int row = salesTable.getSelectedRow();
        if (row == -1) {
            showCustomDialog("Please select a sale entry first.", "Selection Error", true);
            return;
        }

        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());

        boolean confirm = showCustomConfirmDialog("Delete the selected sale record?", "Confirm Delete");
        if (!confirm) return;

        try {
            SalesDAO.deleteSale(id);
            refreshSalesData();
            DashboardPanel.refreshDashboardData();
            showCustomDialog("Sale deleted successfully.", "Success", false);
        } catch (Exception e) {
            e.printStackTrace();
            showCustomDialog("Unable to delete sale:\n" + e.getMessage(), "Database Error", true);
        }
    }

    public void refreshSalesData() {
        tableModel.setRowCount(0);

        boolean isYearly = cmbTimeframe.getSelectedItem() != null &&
                cmbTimeframe.getSelectedItem().toString().equalsIgnoreCase("This Year");

        int selectedMonth = cmbMonth.getSelectedIndex() + 1;
        Integer selectedYear = (Integer) cmbYear.getSelectedItem();

        if (selectedYear == null) selectedYear = LocalDate.now().getYear();

        String selectedMonthPrefix = String.format("%04d-%02d", selectedYear, selectedMonth);
        String selectedYearPrefix = String.format("%04d", selectedYear);

        double filteredGross = 0;
        String sym = SharedData.currencySymbol + " ";

        // Filter and Populate Table Records
        List<String[]> sales = SalesDAO.getAllSales();
        for (String[] sale : sales) {
            String saleDate = sale[1]; // Format: YYYY-MM-DD
            boolean matches = isYearly ? saleDate.startsWith(selectedYearPrefix)
                    : saleDate.startsWith(selectedMonthPrefix);

            if (matches) {
                tableModel.addRow(new Object[]{
                        sale[0],
                        sale[1],
                        sale[2],
                        sym + sale[3]
                });

                try {
                    filteredGross += Double.parseDouble(sale[3].replace(",", ""));
                } catch (Exception ignored) {}
            }
        }

        // Calculate Totals based on Filter
        double grossSales = filteredGross;
        double expenses = isYearly ? SalesDAO.getThisYearExpenseTotal() : SalesDAO.getThisMonthExpenseTotal();
        double netProfit = grossSales - expenses;

        lblGrossSales.setText(String.format("%s%.2f", sym, grossSales));
        lblExpenses.setText(String.format("%s%.2f", sym, expenses));
        lblNetProfit.setText(String.format("%s%.2f", sym, netProfit));

        // Adjust text color based on profit standing (red for loss, green for profit)
        if (netProfit < 0) {
            lblNetProfit.setForeground(new Color(220, 53, 69));
        } else {
            lblNetProfit.setForeground(new Color(13, 148, 136));
        }
    }

    private boolean showCustomFormDialog(JPanel panel, String title) {
        final boolean[] result = {false};
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);

        JPanel topHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        topHeader.setBackground(new Color(248, 249, 250));
        topHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 230)));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(new Color(41, 128, 185));
        topHeader.add(lblTitle);
        dialog.add(topHeader, BorderLayout.NORTH);

        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        dialog.add(panel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(new Color(248, 249, 250));
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 230)));

        JButton btnOk = new JButton("OK");
        btnOk.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnOk.setBackground(new Color(26, 143, 136));
        btnOk.setForeground(Color.WHITE);
        btnOk.setFocusPainted(false);
        btnOk.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
        btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancel.setBackground(new Color(192, 57, 43));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnOk.addActionListener(e -> {
            result[0] = true;
            dialog.dispose();
        });

        btnCancel.addActionListener(e -> {
            result[0] = false;
            dialog.dispose();
        });

        bottomPanel.add(btnOk);
        bottomPanel.add(btnCancel);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setSize(Math.max(dialog.getWidth() + 100, 450), dialog.getHeight() + 40);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        return result[0];
    }

    private void showCustomDialog(String message, String title, boolean isError) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 25));
        centerPanel.setBackground(Color.WHITE);

        String colorHex = isError ? "#C0392B" : "#261436";
        JLabel lblMsg = new JLabel("<html><font color='" + colorHex + "'><b>" + title + ":</b></font> " + message + "</html>");
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMsg.setForeground(new Color(70, 75, 80));
        centerPanel.add(lblMsg);

        dialog.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(new Color(248, 249, 250));
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 230)));

        JButton btnOk = new JButton("OK");
        btnOk.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnOk.setBackground(new Color(26, 143, 136));
        btnOk.setForeground(Color.WHITE);
        btnOk.setFocusPainted(false);
        btnOk.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
        btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOk.addActionListener(e -> dialog.dispose());

        bottomPanel.add(btnOk);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setSize(Math.max(dialog.getWidth() + 80, 520), Math.max(dialog.getHeight() + 40, 150));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private boolean showCustomConfirmDialog(String message, String title) {
        final boolean[] result = {false};
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 25));
        centerPanel.setBackground(Color.WHITE);

        JLabel lblMsg = new JLabel("<html><font color='#C0392B'><b>" + title + ":</b></font> " + message + "</html>");
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMsg.setForeground(new Color(70, 75, 80));
        centerPanel.add(lblMsg);

        dialog.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(new Color(248, 249, 250));
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 230)));

        JButton btnYes = new JButton("Yes");
        btnYes.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnYes.setBackground(new Color(26, 143, 136));
        btnYes.setForeground(Color.WHITE);
        btnYes.setFocusPainted(false);
        btnYes.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
        btnYes.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnYes.addActionListener(e -> {
            result[0] = true;
            dialog.dispose();
        });

        JButton btnNo = new JButton("No");
        btnNo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNo.setBackground(new Color(192, 57, 43));
        btnNo.setForeground(Color.WHITE);
        btnNo.setFocusPainted(false);
        btnNo.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
        btnNo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNo.addActionListener(e -> {
            result[0] = false;
            dialog.dispose();
        });

        bottomPanel.add(btnYes);
        bottomPanel.add(btnNo);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setSize(Math.max(dialog.getWidth() + 80, 520), Math.max(dialog.getHeight() + 40, 150));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        return result[0];
    }
}