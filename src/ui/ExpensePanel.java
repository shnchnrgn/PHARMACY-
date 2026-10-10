package ui;

import db.SalesDAO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class ExpensePanel extends JPanel {

    private static final Color TEAL = new Color(13, 148, 136);
    private static final Color TEAL_DARK = new Color(15, 118, 110);
    private static final Color TEAL_LIGHT = new Color(204, 240, 236);
    private static final Color TEAL_TINT = new Color(240, 250, 249);
    private static final Color DIALOG_TEAL = new Color(26, 143, 136);
    private static final Color DANGER = new Color(220, 53, 69);
    private static final Color TEXT_DARK = new Color(45, 55, 60);
    private static final Color TEXT_MUTED = new Color(110, 118, 125);
    private static final Color BORDER_COLOR = new Color(215, 220, 225);
    private static final Color BG = new Color(240, 242, 245);

    private JTable expenseTable;
    private DefaultTableModel tableModel;

    private JLabel lblTotal;
    private JLabel lblCount;

    public ExpensePanel() {

        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Expenses");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEAL_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Track and manage your business expenses");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(2));
        headerPanel.add(subtitle);

        add(headerPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(18, 0, 0, 0));

        JPanel summaryPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        summaryPanel.setOpaque(false);

        String sym = SharedData.currencySymbol + " ";

        lblTotal = new JLabel(sym + "0.00");
        lblCount = new JLabel("0");

        summaryPanel.add(createSummaryCard("TOTAL EXPENSES THIS MONTH", lblTotal));
        summaryPanel.add(createSummaryCard("NUMBER OF EXPENSES", lblCount));

        mainPanel.add(summaryPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        JButton btnAdd = new JButton("+ Add Expense");
        styleButton(btnAdd, TEAL);

        JButton btnDelete = new JButton("Delete Selected");
        styleButton(btnDelete, DANGER);

        JButton btnRefresh = new JButton("Refresh");
        styleButton(btnRefresh, new Color(80, 90, 100));

        toolbar.add(btnAdd);
        toolbar.add(btnDelete);
        toolbar.add(btnRefresh);

        tablePanel.add(toolbar, BorderLayout.NORTH);

        String[] columns = {"ID", "Date", "Description", "Amount"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        expenseTable = new JTable(tableModel);
        expenseTable.setRowHeight(34);
        expenseTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        final Color gridColor = new Color(225, 229, 233);
        expenseTable.setShowGrid(true);
        expenseTable.setShowVerticalLines(true);
        expenseTable.setShowHorizontalLines(true);
        expenseTable.setGridColor(gridColor);
        expenseTable.setIntercellSpacing(new Dimension(1, 1));
        expenseTable.setFillsViewportHeight(true);
        expenseTable.setSelectionBackground(TEAL_LIGHT);
        expenseTable.setSelectionForeground(TEAL_DARK);

        JTableHeader header = expenseTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 38));
        header.setResizingAllowed(false);
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel lbl = new JLabel(String.valueOf(value));
                lbl.setOpaque(true);
                lbl.setBackground(TEAL_TINT);
                lbl.setForeground(TEAL_DARK);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setHorizontalAlignment(JLabel.LEFT);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 1, column == table.getColumnCount() - 1 ? TEAL : TEAL),
                        new EmptyBorder(0, 10, 0, 10)
                ));
                return lbl;
            }
        });

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setHorizontalAlignment(JLabel.LEFT);
                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 252, 252));
                    setForeground(TEXT_DARK);
                }
                setFont(new Font("Segoe UI", column == 3 ? Font.BOLD : Font.PLAIN, 12));
                return this;
            }
        };
        for (int i = 0; i < expenseTable.getColumnModel().getColumnCount(); i++) {
            expenseTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        expenseTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        int[] fixed = {80, 140};
        for (int i = 0; i < fixed.length; i++) {
            TableColumn col = expenseTable.getColumnModel().getColumn(i);
            col.setMinWidth(fixed[i]);
            col.setMaxWidth(fixed[i]);
            col.setPreferredWidth(fixed[i]);
        }
        expenseTable.getColumnModel().getColumn(2).setPreferredWidth(600);
        expenseTable.getColumnModel().getColumn(3).setPreferredWidth(200);

        JScrollPane scrollPane = new JScrollPane(expenseTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> showAddExpenseDialog());
        btnDelete.addActionListener(e -> deleteSelectedExpense());
        btnRefresh.addActionListener(e -> refreshExpenses());

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                refreshExpenses();
            }
        });

        refreshExpenses();
    }

    private JPanel createSummaryCard(String title, JLabel value) {

        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(Color.WHITE);

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createMatteBorder(0, 5, 0, 0, TEAL),
                                new EmptyBorder(16, 18, 16, 18)
                        )
                )
        );

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(TEXT_MUTED);

        value.setFont(new Font("Segoe UI", Font.BOLD, 28));
        value.setForeground(TEAL);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(value, BorderLayout.CENTER);

        return panel;
    }

    private void styleButton(JButton button, Color color) {

        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        final Color hover = color.darker();
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
    }

    private JButton createDialogButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 22, 7, 22));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private boolean showAddExpenseCustomDialog(JPanel panel, String title) {
        final boolean[] result = {false};
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);

        JPanel topHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        topHeader.setBackground(TEAL_TINT);
        topHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, TEAL));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(TEAL_DARK);
        topHeader.add(lblTitle);
        dialog.add(topHeader, BorderLayout.NORTH);

        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        dialog.add(panel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(new Color(248, 249, 250));
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 230)));

        JButton btnOk = createDialogButton("OK", DIALOG_TEAL);
        JButton btnCancel = createDialogButton("Cancel", new Color(192, 57, 43));

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
        bottomPanel.setBackground(new Color(248, 249, 250));
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 230)));

        JButton btnOk = createDialogButton("OK", DIALOG_TEAL);
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

        JButton btnYes = createDialogButton("Yes", DIALOG_TEAL);
        btnYes.addActionListener(e -> {
            result[0] = true;
            dialog.dispose();
        });

        JButton btnNo = createDialogButton("No", new Color(192, 57, 43));
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

    private void showAddExpenseDialog() {

        JTextField descriptionField = new JTextField();
        JTextField amountField = new JTextField();

        descriptionField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1, 5, 5));

        JLabel lblDesc = new JLabel("Expense Description:");
        lblDesc.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDesc.setForeground(new Color(70, 75, 80));
        panel.add(lblDesc);

        descriptionField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panel.add(descriptionField);

        JLabel lblAmt = new JLabel("Amount:");
        lblAmt.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAmt.setForeground(new Color(70, 75, 80));
        panel.add(lblAmt);

        amountField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panel.add(amountField);

        boolean okClicked = showAddExpenseCustomDialog(panel, "Add Expense");

        if (!okClicked) {
            return;
        }

        String description = descriptionField.getText().trim();

        String amountText = amountField.getText().trim().replaceAll("[^0-9.]", "");

        if (description.isEmpty()) {
            showCustomDialog("Expense description is required.", "Validation Error", true);
            return;
        }

        double amount;

        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showCustomDialog("Please enter a valid amount.", "Validation Error", true);
            return;
        }

        if (amount <= 0) {
            showCustomDialog("Amount must be greater than zero.", "Validation Error", true);
            return;
        }

        try {

            SalesDAO.addExpense(description, amount, LocalDate.now().toString());

            refreshExpenses();

            DashboardPanel.refreshDashboardData();

            showCustomDialog("Expense saved successfully.", "Success", false);

        } catch (Exception e) {

            e.printStackTrace();

            showCustomDialog("Unable to save expense:\n" + e.getMessage(), "Database Error", true);
        }
    }

    private void deleteSelectedExpense() {

        int row = expenseTable.getSelectedRow();

        if (row == -1) {
            showCustomDialog("Please select an expense first.", "Selection Error", true);
            return;
        }

        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());

        boolean confirm = showCustomConfirmDialog("Delete the selected expense?", "Confirm Delete");

        if (!confirm) {
            return;
        }

        try {

            SalesDAO.deleteExpense(id);

            refreshExpenses();

            DashboardPanel.refreshDashboardData();

            showCustomDialog("Expense deleted successfully.", "Success", false);

        } catch (Exception e) {

            e.printStackTrace();

            showCustomDialog("Unable to delete expense:\n" + e.getMessage(), "Database Error", true);
        }
    }

    public void refreshExpenses() {

        tableModel.setRowCount(0);

        List<String[]> expenses = SalesDAO.getAllExpenses();

        String sym = SharedData.currencySymbol + " ";

        for (String[] expense : expenses) {

            tableModel.addRow(
                    new Object[]{
                            expense[0],
                            expense[1],
                            expense[2],
                            sym + expense[3]
                    }
            );
        }

        lblTotal.setText(
                String.format("%s%.2f", sym, SalesDAO.getThisMonthExpenseTotal())
        );

        lblCount.setText(
                String.valueOf(SalesDAO.getThisMonthExpenseCount())
        );
    }
}