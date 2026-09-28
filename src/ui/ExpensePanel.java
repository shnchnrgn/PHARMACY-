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

        lblTotal =
                new JLabel("₱ 0.00");

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

        panel.add(
                new JLabel("Expense Description:")
        );

        panel.add(
                descriptionField
        );

        panel.add(
                new JLabel("Amount:")
        );

        panel.add(
                amountField
        );

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Add Expense",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (result != JOptionPane.OK_OPTION) {
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
                        .replace("₱", "")
                        .replace(",", "");

        if (description.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Expense description is required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
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

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid amount.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (amount <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Amount must be greater than zero.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
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

            JOptionPane.showMessageDialog(
                    this,
                    "Expense saved successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to save expense:\n" +
                    e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void deleteSelectedExpense() {

        int row =
                expenseTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an expense first.",
                    "Selection Error",
                    JOptionPane.WARNING_MESSAGE
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

        int confirm =
                JOptionPane.showConfirmDialog(
                        this,
                        "Delete the selected expense?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            SalesDAO.deleteExpense(id);

            refreshExpenses();

            DashboardPanel.refreshDashboardData();

            JOptionPane.showMessageDialog(
                    this,
                    "Expense deleted successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to delete expense:\n" +
                    e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void refreshExpenses() {

        tableModel.setRowCount(0);

        List<String[]> expenses =
                SalesDAO.getAllExpenses();

        for (String[] expense : expenses) {

            tableModel.addRow(
                    new Object[]{
                            expense[0],
                            expense[1],
                            expense[2],
                            "₱ " + expense[3]
                    }
            );
        }

        lblTotal.setText(
                String.format(
                        "₱ %.2f",
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