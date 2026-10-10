package ui;

import db.SalesDAO;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class SalesPanel extends JPanel {

    private static final Color BORDER = new Color(220, 225, 230);
    private static final Color COLOR_BORDER = new Color(200, 205, 210);
    private static final Color TEXT = new Color(60, 65, 70);
    private static final Color MUTED = new Color(108, 117, 125);
    private static final Color COLOR_RED = new Color(220, 53, 69);
    private static final Color COLOR_TEAL = new Color(13, 148, 136);
    private static final Color COLOR_TEAL_HOVER = new Color(15, 118, 110);
    private static final Color COLOR_TEAL_LIGHT = new Color(204, 235, 231);
    private static final Color COLOR_TEAL_BG = new Color(240, 250, 249);
    private static final Color COLOR_ZEBRA = new Color(247, 250, 250);
    private static final Color COLOR_DIALOG_BG = new Color(248, 249, 251);
    private static final Color COLOR_DIALOG_LINE = new Color(225, 228, 232);

    private static final int CELL_PAD = 10;

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

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Sales & Profit Overview");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Track gross sales, expenses, and net profit");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitle.setForeground(MUTED);

        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(subtitle);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        filterPanel.setOpaque(true);
        filterPanel.setBackground(COLOR_TEAL_BG);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_TEAL_LIGHT),
                new EmptyBorder(0, 6, 0, 6)));

        cmbTimeframe = new JComboBox<>(new String[]{"This Month", "This Year"});
        styleComboBox(cmbTimeframe);

        cmbMonth = new JComboBox<>();
        for (int m = 1; m <= 12; m++) {
            cmbMonth.addItem(Month.of(m).getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        }
        cmbMonth.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        styleComboBox(cmbMonth);

        cmbYear = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int y = currentYear; y >= currentYear - 5; y--) {
            cmbYear.addItem(y);
        }
        styleComboBox(cmbYear);

        cmbTimeframe.addActionListener(e -> {
            cmbMonth.setEnabled(!cmbTimeframe.getSelectedItem().toString().equalsIgnoreCase("This Year"));
            refreshSalesData();
        });
        cmbMonth.addActionListener(e -> refreshSalesData());
        cmbYear.addActionListener(e -> refreshSalesData());

        filterPanel.add(createFilterLabel("View:"));
        filterPanel.add(cmbTimeframe);
        filterPanel.add(createFilterLabel("Month:"));
        filterPanel.add(cmbMonth);
        filterPanel.add(createFilterLabel("Year:"));
        filterPanel.add(cmbYear);

        headerPanel.add(titleBox, BorderLayout.WEST);
        headerPanel.add(filterPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        summaryPanel.setOpaque(false);

        String sym = SharedData.currencySymbol + " ";

        lblGrossSales = new JLabel(sym + "0.00");
        lblExpenses = new JLabel(sym + "0.00");
        lblNetProfit = new JLabel(sym + "0.00");

        summaryPanel.add(createSummaryCard("Gross Sales", lblGrossSales, COLOR_TEAL));
        summaryPanel.add(createSummaryCard("Total Expenses", lblExpenses, COLOR_RED));
        summaryPanel.add(createSummaryCard("Net Profit", lblNetProfit, COLOR_TEAL_HOVER));

        mainPanel.add(summaryPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 220)));

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(new EmptyBorder(12, 15, 10, 15));

        toolbar.add(createSectionTitle("Sales Records"), BorderLayout.WEST);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);

        JButton btnAdd = new RoundedButton("+ Add Sale", COLOR_TEAL);
        JButton btnDelete = new RoundedButton("Delete Selected", COLOR_RED);
        JButton btnRefresh = new RoundedButton("Refresh", Color.WHITE);

        buttons.add(btnAdd);
        buttons.add(btnDelete);
        buttons.add(btnRefresh);
        toolbar.add(buttons, BorderLayout.EAST);

        tablePanel.add(toolbar, BorderLayout.NORTH);

        String[] columns = {"ID", "Date", "Description", "Gross Amount"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        salesTable = new JTable(tableModel);

        salesTable.setRowHeight(33);
        salesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        salesTable.setShowVerticalLines(false);
        salesTable.setShowHorizontalLines(true);
        salesTable.setGridColor(new Color(235, 238, 242));
        salesTable.setFillsViewportHeight(true);
        salesTable.setSelectionBackground(COLOR_TEAL_LIGHT);
        salesTable.setSelectionForeground(TEXT);

        JTableHeader header = new StyledHeader(salesTable.getColumnModel(), 36);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        salesTable.setTableHeader(header);

        salesTable.getTableHeader().setResizingAllowed(false);
        salesTable.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
                setBorder(new EmptyBorder(0, CELL_PAD, 0, CELL_PAD));
                setHorizontalAlignment(JLabel.LEFT);
                setFont(new Font("Segoe UI", Font.PLAIN, 12));
                setForeground(TEXT);
                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : COLOR_ZEBRA);
                }
                return this;
            }
        };
        for (int i = 0; i < salesTable.getColumnModel().getColumnCount(); i++) {
            salesTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        salesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {60, 120, 400, 150};
        for (int i = 0; i < widths.length; i++) {
            TableColumn col = salesTable.getColumnModel().getColumn(i);
            col.setMinWidth(widths[i]);
            col.setMaxWidth(widths[i]);
            col.setPreferredWidth(widths[i]);
            col.setResizable(false);
        }

        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_DIALOG_LINE));
        scrollPane.getViewport().setBackground(Color.WHITE);
        applyScrollBar(scrollPane);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

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



    private JLabel createFilterLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXT);
        return lbl;
    }

    private JPanel createSectionTitle(String text) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);

        JPanel bar = new JPanel();
        bar.setBackground(COLOR_TEAL);
        bar.setPreferredSize(new Dimension(4, 18));

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(TEXT);

        p.add(bar, BorderLayout.WEST);
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    private JPanel createSummaryCard(String title, JLabel value, Color highlightColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 220)));

        JPanel accent = new JPanel();
        accent.setBackground(highlightColor);
        accent.setPreferredSize(new Dimension(5, 1));

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(15, 18, 15, 18));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(new Color(100, 105, 110));

        value.setFont(new Font("Segoe UI", Font.BOLD, 22));
        value.setForeground(highlightColor);

        body.add(lblTitle, BorderLayout.NORTH);
        body.add(value, BorderLayout.CENTER);

        card.add(accent, BorderLayout.WEST);
        card.add(body, BorderLayout.CENTER);

        return card;
    }

    private static Border fieldBorder(Color line) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(line, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8));
    }

    private JTextField createField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setForeground(new Color(70, 75, 80));
        tf.setBorder(fieldBorder(COLOR_BORDER));
        tf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                tf.setBorder(fieldBorder(COLOR_TEAL));
            }

            @Override
            public void focusLost(FocusEvent e) {
                tf.setBorder(fieldBorder(COLOR_BORDER));
            }
        });
        return tf;
    }

    private void styleComboBox(JComboBox<?> combo) {
        final Color lineColor = new Color(225, 238, 236);

        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBackground(Color.WHITE);
        combo.setForeground(TEXT);
        combo.setFocusable(false);
        combo.setPreferredSize(new Dimension(140, 32));
        combo.setMaximumRowCount(8);
        combo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        combo.setBorder(BorderFactory.createLineBorder(COLOR_TEAL));

        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton b = new JButton() {
                    @Override
                    public void paint(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        g2.setColor(comboBox.isEnabled() ? COLOR_TEAL : new Color(190, 198, 206));
                        g2.fillRect(0, 0, getWidth(), getHeight());

                        int cx = getWidth() / 2;
                        int cy = getHeight() / 2;
                        g2.setColor(Color.WHITE);
                        g2.fillPolygon(
                                new int[]{cx - 4, cx + 4, cx},
                                new int[]{cy - 2, cy - 2, cy + 3},
                                3);
                        g2.dispose();
                    }
                };
                b.setPreferredSize(new Dimension(28, 0));
                b.setBorder(BorderFactory.createEmptyBorder());
                b.setFocusable(false);
                b.setContentAreaFilled(false);
                return b;
            }

            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup popup = new BasicComboPopup(comboBox) {
                    @Override
                    protected JScrollPane createScroller() {
                        JScrollPane scroller = super.createScroller();
                        JScrollBar bar = scroller.getVerticalScrollBar();
                        bar.setUI(new TealScrollBarUI());
                        bar.setPreferredSize(new Dimension(12, 0));
                        bar.setOpaque(false);
                        return scroller;
                    }
                };
                popup.setBorder(BorderFactory.createLineBorder(COLOR_TEAL));
                return popup;
            }
        });

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                lbl.setOpaque(true);

                if (index < 0) {
                    lbl.setForeground(combo.isEnabled() ? TEXT : MUTED);
                    lbl.setBackground(Color.WHITE);
                    lbl.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 6));
                } else {
                    lbl.setForeground(TEXT);
                    lbl.setBackground(isSelected ? COLOR_TEAL_LIGHT : Color.WHITE);
                    lbl.setBorder(BorderFactory.createCompoundBorder(
                            index > 0
                                    ? BorderFactory.createMatteBorder(1, 0, 0, 0, lineColor)
                                    : BorderFactory.createEmptyBorder(),
                            BorderFactory.createEmptyBorder(6, 8, 6, 8)));
                }
                return lbl;
            }
        });
    }



    private void showAddSaleDialog() {
        JTextField descriptionField = createField();
        JTextField amountField = createField();

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));

        JLabel lblDesc = new JLabel("Sale Description / Item:");
        lblDesc.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDesc.setForeground(new Color(70, 75, 80));
        panel.add(lblDesc);
        panel.add(descriptionField);

        JLabel lblAmt = new JLabel("Gross Amount:");
        lblAmt.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAmt.setForeground(new Color(70, 75, 80));
        panel.add(lblAmt);
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

        List<String[]> sales = SalesDAO.getAllSales();
        for (String[] sale : sales) {
            String saleDate = sale[1];
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

        double grossSales = filteredGross;
        double expenses = isYearly ? SalesDAO.getThisYearExpenseTotal() : SalesDAO.getThisMonthExpenseTotal();
        double netProfit = grossSales - expenses;

        lblGrossSales.setText(String.format("%s%.2f", sym, grossSales));
        lblExpenses.setText(String.format("%s%.2f", sym, expenses));
        lblNetProfit.setText(String.format("%s%.2f", sym, netProfit));

        lblNetProfit.setForeground(netProfit < 0 ? COLOR_RED : COLOR_TEAL_HOVER);
    }

    private JButton dialogButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private boolean showCustomFormDialog(JPanel panel, String title) {
        final boolean[] result = {false};
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);

        JPanel topHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        topHeader.setBackground(COLOR_DIALOG_BG);
        topHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_DIALOG_LINE));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(COLOR_TEAL_HOVER);
        topHeader.add(lblTitle);
        dialog.add(topHeader, BorderLayout.NORTH);

        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        dialog.add(panel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(COLOR_DIALOG_BG);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_DIALOG_LINE));

        JButton btnOk = dialogButton("OK", COLOR_TEAL);
        JButton btnCancel = dialogButton("Cancel", new Color(192, 57, 43));

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

        String colorHex = isError ? "#C0392B" : "#0D9488";
        JLabel lblMsg = new JLabel("<html><font color='" + colorHex + "'><b>" + title + ":</b></font> " + message + "</html>");
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMsg.setForeground(new Color(70, 75, 80));
        centerPanel.add(lblMsg);

        dialog.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(COLOR_DIALOG_BG);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_DIALOG_LINE));

        JButton btnOk = dialogButton("OK", COLOR_TEAL);
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
        bottomPanel.setBackground(COLOR_DIALOG_BG);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_DIALOG_LINE));

        JButton btnYes = dialogButton("Yes", COLOR_TEAL);
        btnYes.addActionListener(e -> {
            result[0] = true;
            dialog.dispose();
        });

        JButton btnNo = dialogButton("No", new Color(192, 57, 43));
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



    private static void applyScrollBar(JScrollPane sp) {
        JScrollBar v = sp.getVerticalScrollBar();
        v.setUI(new TealScrollBarUI());
        v.setPreferredSize(new Dimension(14, 0));
        v.setUnitIncrement(16);
        v.setOpaque(false);

        JScrollBar h = sp.getHorizontalScrollBar();
        h.setUI(new TealScrollBarUI());
        h.setPreferredSize(new Dimension(0, 14));
        h.setUnitIncrement(16);
        h.setOpaque(false);

        JPanel corner = new JPanel();
        corner.setBackground(new Color(243, 245, 247));
        sp.setCorner(JScrollPane.LOWER_RIGHT_CORNER, corner);
    }

    private static class TealScrollBarUI extends BasicScrollBarUI {

        private static final Color TRACK = new Color(243, 245, 247);
        private static final Color THUMB = new Color(190, 198, 206);
        private static final Color THUMB_DRAG = new Color(10, 118, 108);

        @Override
        protected void configureScrollBarColors() {
            thumbColor = THUMB;
            trackColor = TRACK;
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
            b.setBorder(null);
            b.setContentAreaFilled(false);
            b.setFocusable(false);
            return b;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(TRACK);
            int pad = 2;
            if (scrollbar.getOrientation() == Adjustable.VERTICAL) {
                g2.fillRoundRect(r.x + pad, r.y, r.width - pad * 2, r.height, r.width, r.width);
            } else {
                g2.fillRoundRect(r.x, r.y + pad, r.width, r.height - pad * 2, r.height, r.height);
            }
            g2.dispose();
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty() || !scrollbar.isEnabled()) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isDragging ? THUMB_DRAG : (isThumbRollover() ? COLOR_TEAL : THUMB));

            int pad = 2;
            if (scrollbar.getOrientation() == Adjustable.VERTICAL) {
                int w = r.width - pad * 2;
                g2.fillRoundRect(r.x + pad, r.y, w, r.height, w, w);
            } else {
                int hh = r.height - pad * 2;
                g2.fillRoundRect(r.x, r.y + pad, r.width, hh, hh, hh);
            }
            g2.dispose();
        }

        @Override
        protected Dimension getMinimumThumbSize() {
            return new Dimension(10, 36);
        }
    }



    private static class StyledHeader extends JTableHeader {
        StyledHeader(javax.swing.table.TableColumnModel cm, int height) {
            super(cm);
            setPreferredSize(new Dimension(0, height));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int h = getHeight();

            g.setColor(COLOR_TEAL_BG);
            g.fillRect(0, 0, getWidth(), h);

            for (int i = 0; i < columnModel.getColumnCount(); i++) {
                Rectangle r = getHeaderRect(i);
                if (r.width <= 0) continue;
                String text = String.valueOf(columnModel.getColumn(i).getHeaderValue());

                g.setColor(COLOR_TEAL_LIGHT);
                g.drawLine(r.x + r.width - 1, 6, r.x + r.width - 1, h - 8);

                g.setColor(COLOR_TEAL_HOVER);
                g.setFont(getFont());
                FontMetrics fm = g.getFontMetrics();
                int ty = (h - 2 + fm.getAscent() - fm.getDescent()) / 2;
                g.drawString(text, r.x + CELL_PAD, ty);
            }

            g.setColor(COLOR_TEAL);
            g.fillRect(0, h - 2, getWidth(), 2);
            g.dispose();
        }
    }



    private static class RoundedButton extends JButton {

        private final Color base;
        private final boolean outline;
        private boolean hovered;

        RoundedButton(String text, Color base) {
            super(text);
            this.base = base;
            this.outline = Color.WHITE.equals(base);

            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(outline ? TEXT : Color.WHITE);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setBorder(new EmptyBorder(8, 16, 8, 16));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            if (outline) {
                g2.setColor(hovered ? COLOR_TEAL_BG : Color.WHITE);
                g2.fillRoundRect(0, 0, w - 1, h - 1, 10, 10);
                g2.setColor(hovered ? COLOR_TEAL : COLOR_BORDER);
                g2.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);
            } else {
                g2.setColor(hovered ? base.darker() : base);
                g2.fillRoundRect(0, 0, w, h, 10, 10);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}