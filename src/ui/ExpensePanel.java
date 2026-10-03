package ui;

import db.SalesDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ExpensePanel extends JPanel {

    private JTable expenseTable;
    private DefaultTableModel tableModel;

    private JLabel lblTotal;
    private JLabel lblCount;

    public ExpensePanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));
        setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel title =
                new JLabel("Expenses");

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        22
                )
        );

        title.setForeground(
                new Color(60, 65, 70)
        );

        add(title, BorderLayout.NORTH);

        JPanel mainPanel =
                new JPanel(new BorderLayout(0, 15));

        mainPanel.setOpaque(false);
        mainPanel.setBorder(
                new EmptyBorder(15, 0, 0, 0)
        );

        JPanel summaryPanel =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                15,
                                0
                        )
                );

        summaryPanel.setOpaque(false);

        String sym = SharedData.currencySymbol + " ";

        lblTotal =
                new JLabel(sym + "0.00");

        lblCount =
                new JLabel("0");

        summaryPanel.add(
                createSummaryCard(
                        "Total Expenses This Month",
                        lblTotal
                )
        );

        summaryPanel.add(
                createSummaryCard(
                        "Number Of Expenses",
                        lblCount
                )
        );

        mainPanel.add(
                summaryPanel,
                BorderLayout.NORTH
        );

        JPanel tablePanel =
                new JPanel(new BorderLayout());

        tablePanel.setBackground(Color.WHITE);

        tablePanel.setBorder(
                BorderFactory.createLineBorder(
                        new Color(205, 210, 215)
                )
        );

        JPanel toolbar =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                12,
                                10
                        )
                );

        toolbar.setBackground(Color.WHITE);

        JButton btnAdd =
                new JButton("+ Add Expense");

        styleButton(
                btnAdd,
                new Color(13, 148, 136)
        );

        JButton btnDelete =
                new JButton("Delete Selected");

        styleButton(
                btnDelete,
                new Color(220, 53, 69)
        );

        JButton btnRefresh =
                new JButton("Refresh");

        styleButton(
                btnRefresh,
                new Color(80, 90, 100)
        );

        toolbar.add(btnAdd);
        toolbar.add(btnDelete);
        toolbar.add(btnRefresh);

        tablePanel.add(
                toolbar,
                BorderLayout.NORTH
        );

        String[] columns = {
                "ID",
                "Date",
                "Description",
                "Amount"
        };

        tableModel =
                new DefaultTableModel(
                        columns,
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column) {

                        return false;
                    }
                };

        expenseTable =
                new JTable(tableModel);

        expenseTable.setRowHeight(32);

        expenseTable.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        expenseTable.getTableHeader().setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        expenseTable.getTableHeader()
                .setBackground(
                        new Color(248, 249, 250)
                );

        expenseTable.getTableHeader()
                .setForeground(
                        new Color(80, 85, 90)
                );

        expenseTable.setShowVerticalLines(false);

        expenseTable.setGridColor(
                new Color(235, 238, 242)
        );

        expenseTable.setSelectionBackground(new Color(210, 215, 220));
        expenseTable.setSelectionForeground(Color.BLACK);

        expenseTable.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(60);

        expenseTable.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(120);

        expenseTable.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(400);

        expenseTable.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(150);

        JScrollPane scrollPane =
                new JScrollPane(
                        expenseTable
                );

        scrollPane.setBorder(
                BorderFactory.createEmptyBorder()
        );

        scrollPane.getViewport()
                .setBackground(Color.WHITE);

        tablePanel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        mainPanel.add(
                tablePanel,
                BorderLayout.CENTER
        );

        add(
                mainPanel,
                BorderLayout.CENTER
        );

        btnAdd.addActionListener(
                e -> showAddExpenseDialog()
        );

        btnDelete.addActionListener(
                e -> deleteSelectedExpense()
        );

        btnRefresh.addActionListener(
                e -> refreshExpenses()
        );

        addComponentListener(
                new java.awt.event.ComponentAdapter() {

                    @Override
                    public void componentShown(
                            java.awt.event.ComponentEvent e) {

                        refreshExpenses();
                    }
                }
        );

        refreshExpenses();
    }

    private JPanel createSummaryCard(
            String title,
            JLabel value) {

        JPanel panel =
                new JPanel(new BorderLayout());

        panel.setBackground(Color.WHITE);

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        205,
                                        210,
                                        215
                                )
                        ),
                        new EmptyBorder(
                                15,
                                18,
                                15,
                                18
                        )
                )
        );

        JLabel lblTitle =
                new JLabel(title);

        lblTitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        lblTitle.setForeground(
                new Color(
                        100,
                        105,
                        110
                )
        );

        value.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        24
                )
        );

        value.setForeground(
                new Color(
                        35,
                        50,
                        55
                )
        );

        panel.add(
                lblTitle,
                BorderLayout.NORTH
        );

        panel.add(
                value,
                BorderLayout.CENTER
        );

        return panel;
    }

    private void styleButton(
            JButton button,
            Color color) {

        button.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        9,
                        15,
                        9,
                        15
                )
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );
    }

    private boolean showAddExpenseCustomDialog(JPanel panel, String title) {
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

    private void showAddExpenseDialog() {

        JTextField descriptionField =
                new JTextField();

        JTextField amountField =
                new JTextField();

        descriptionField.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        amountField.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        JPanel panel =
                new JPanel();

        panel.setLayout(
                new GridLayout(
                        0,
                        1,
                        5,
                        5
                )
        );

        JLabel lblDesc = new JLabel("Expense Description:");
        lblDesc.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDesc.setForeground(new Color(70, 75, 80));
        panel.add(lblDesc);

        descriptionField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panel.add(
                descriptionField
        );

        JLabel lblAmt = new JLabel("Amount:");
        lblAmt.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAmt.setForeground(new Color(70, 75, 80));
        panel.add(lblAmt);

        amountField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panel.add(
                amountField
        );

        boolean okClicked =
                showAddExpenseCustomDialog(
                        panel,
                        "Add Expense"
                );

        if (!okClicked) {
            return;
        }

        String description =
                descriptionField
                        .getText()
                        .trim();

        String amountText =
                amountField
                        .getText()
                        .trim()
                        .replaceAll("[^0-9.]", "");

        if (description.isEmpty()) {

            showCustomDialog(
                    "Expense description is required.",
                    "Validation Error",
                    true
            );

            return;
        }

        double amount;

        try {

            amount =
                    Double.parseDouble(
                            amountText
                    );

        } catch (NumberFormatException e) {

            showCustomDialog(
                    "Please enter a valid amount.",
                    "Validation Error",
                    true
            );

            return;
        }

        if (amount <= 0) {

            showCustomDialog(
                    "Amount must be greater than zero.",
                    "Validation Error",
                    true
            );

            return;
        }

        try {

            SalesDAO.addExpense(
                    description,
                    amount,
                    LocalDate.now().toString()
            );

            refreshExpenses();

            DashboardPanel.refreshDashboardData();

            showCustomDialog(
                    "Expense saved successfully.",
                    "Success",
                    false
            );

        } catch (Exception e) {

            e.printStackTrace();

            showCustomDialog(
                    "Unable to save expense:\n" +
                    e.getMessage(),
                    "Database Error",
                    true
            );
        }
    }

    private void deleteSelectedExpense() {

        int row =
                expenseTable.getSelectedRow();

        if (row == -1) {

            showCustomDialog(
                    "Please select an expense first.",
                    "Selection Error",
                    true
            );

            return;
        }

        int id =
                Integer.parseInt(
                        tableModel
                                .getValueAt(
                                        row,
                                        0
                                )
                                .toString()
                );

        boolean confirm =
                showCustomConfirmDialog(
                        "Delete the selected expense?",
                        "Confirm Delete"
                );

        if (!confirm) {
            return;
        }

        try {

            SalesDAO.deleteExpense(id);

            refreshExpenses();

            DashboardPanel.refreshDashboardData();

            showCustomDialog(
                    "Expense deleted successfully.",
                    "Success",
                    false
            );

        } catch (Exception e) {

            e.printStackTrace();

            showCustomDialog(
                    "Unable to delete expense:\n" +
                    e.getMessage(),
                    "Database Error",
                    true
            );
        }
    }

    public void refreshExpenses() {

        tableModel.setRowCount(0);

        List<String[]> expenses =
                SalesDAO.getAllExpenses();

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
                String.format(
                        "%s%.2f",
                        sym,
                        SalesDAO
                                .getThisMonthExpenseTotal()
                )
        );

        lblCount.setText(
                String.valueOf(
                        SalesDAO
                                .getThisMonthExpenseCount()
                )
        );
    }
}