package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CustomerPanel extends JPanel {

    private JTable customerTable;
    private JTextField txtCustomerId, txtFullName, txtContactNumber, txtAddress, txtDateRegistered, txtSearch;
    private DefaultTableModel customerModel;

    public CustomerPanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Customer Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(60, 65, 70));
        add(lblTitle, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        contentPanel.setOpaque(false);

        JPanel leftFormContainer = new JPanel(new BorderLayout());
        leftFormContainer.setBackground(Color.WHITE);
        leftFormContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 215, 220)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel formFieldsPanel = new JPanel(new GridLayout(5, 2, 10, 12));
        formFieldsPanel.setOpaque(false);

        txtCustomerId = createStyledTextField();
        txtFullName = createStyledTextField();
        txtContactNumber = createStyledTextField();
        txtAddress = createStyledTextField();
        txtDateRegistered = createStyledTextField();

        addFormRow(formFieldsPanel, "Customer ID", txtCustomerId);
        addFormRow(formFieldsPanel, "Full Name", txtFullName);
        addFormRow(formFieldsPanel, "Contact Number", txtContactNumber);
        addFormRow(formFieldsPanel, "Address", txtAddress);
        addFormRow(formFieldsPanel, "Date Registered", txtDateRegistered);

        leftFormContainer.add(formFieldsPanel, BorderLayout.CENTER);

        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionButtonPanel.setOpaque(false);
        actionButtonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton btnAdd = createStyledButton("Add", new Color(40, 167, 69));
        JButton btnEdit = createStyledButton("Edit", new Color(51, 122, 183));
        JButton btnDelete = createStyledButton("Delete", new Color(220, 53, 69));

        actionButtonPanel.add(btnAdd);
        actionButtonPanel.add(btnEdit);
        actionButtonPanel.add(btnDelete);

        leftFormContainer.add(actionButtonPanel, BorderLayout.SOUTH);

        JPanel rightTableContainer = new JPanel(new BorderLayout(0, 10));
        rightTableContainer.setBackground(Color.WHITE);
        rightTableContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 215, 220)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel searchBarPanel = new JPanel(new BorderLayout(8, 0));
        searchBarPanel.setOpaque(false);

        JLabel lblSearch = new JLabel("Search Customer:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSearch.setForeground(new Color(70, 75, 80));

        txtSearch = createStyledTextField();
        
        JButton btnSearch = new JButton("Search");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSearch.setBackground(new Color(240, 242, 245));
        btnSearch.setForeground(new Color(70, 75, 80));
        btnSearch.setFocusPainted(false);
        btnSearch.setBorder(BorderFactory.createLineBorder(new Color(190, 195, 200)));
        btnSearch.setPreferredSize(new Dimension(75, 30));
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));

        searchBarPanel.add(lblSearch, BorderLayout.WEST);
        searchBarPanel.add(txtSearch, BorderLayout.CENTER);
        searchBarPanel.add(btnSearch, BorderLayout.EAST);

        rightTableContainer.add(searchBarPanel, BorderLayout.NORTH);

        String[] columns = {"Customer ID", "Full Name", "Contact Number", "Address", "Date Registered"};
        customerModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customerModel.addRow(new Object[]{"C001", "Juan Dela Cruz", "09123456789", "Sampaloc, Manila", "2026-09-01"});
        customerModel.addRow(new Object[]{"C002", "Maria Santos", "09987654321", "Sta. Mesa, Manila", "2026-09-05"});

        customerTable = new JTable(customerModel);
        customerTable.setRowHeight(32);
        customerTable.setShowVerticalLines(false);
        customerTable.setShowHorizontalLines(true);
        customerTable.setGridColor(new Color(235, 238, 242));
        customerTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        customerTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        customerTable.getTableHeader().setBackground(new Color(248, 249, 250));
        customerTable.getTableHeader().setForeground(new Color(80, 85, 90));

        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));

        rightTableContainer.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(leftFormContainer);
        contentPanel.add(rightTableContainer);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setForeground(new Color(70, 75, 80));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 205, 210)),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        return tf;
    }

    private JButton createStyledButton(String text, Color bgCol) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bgCol);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void addFormRow(JPanel panel, String labelText, JComponent field) {
        JLabel lbl = new JLabel("<html><b>" + labelText + ":</b></html>");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(70, 75, 80));
        panel.add(lbl);
        panel.add(field);
    }
}