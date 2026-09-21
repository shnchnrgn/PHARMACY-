package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CustomerPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch, txtCustId, txtName, txtContact, txtAddress, txtDate;

    public CustomerPanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Customer Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(40, 40, 40));
        add(lblTitle, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(15, 0));
        mainContent.setOpaque(false);

        JPanel leftContainer = new JPanel(new BorderLayout(0, 10));
        leftContainer.setBackground(Color.WHITE);
        leftContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        leftContainer.setPreferredSize(new Dimension(340, 0));

        JPanel leftForm = new JPanel(new GridLayout(5, 2, 5, 12));
        leftForm.setOpaque(false);

        txtCustId = new JTextField();
        txtName = new JTextField();
        txtContact = new JTextField();
        txtAddress = new JTextField();
        txtDate = new JTextField();

        leftForm.add(new JLabel("Customer ID:"));
        leftForm.add(txtCustId);
        leftForm.add(new JLabel("Full Name:"));
        leftForm.add(txtName);
        leftForm.add(new JLabel("Contact Number:"));
        leftForm.add(txtContact);
        leftForm.add(new JLabel("Address:"));
        leftForm.add(txtAddress);
        leftForm.add(new JLabel("Date Registered:"));
        leftForm.add(txtDate);

        leftContainer.add(leftForm, BorderLayout.CENTER);

        JPanel btnFormPanel = new JPanel(new GridLayout(1, 3, 8, 0));
        btnFormPanel.setOpaque(false);
        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");

        btnAdd.setBackground(new Color(39, 174, 96));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnEdit.setBackground(new Color(41, 128, 185));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFocusPainted(false);
        btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnFormPanel.add(btnAdd);
        btnFormPanel.add(btnEdit);
        btnFormPanel.add(btnDelete);

        leftContainer.add(btnFormPanel, BorderLayout.SOUTH);

        mainContent.add(leftContainer, BorderLayout.WEST);

        JPanel rightTablePanel = new JPanel(new BorderLayout(0, 10));
        rightTablePanel.setBackground(Color.WHITE);
        rightTablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel searchBarPanel = new JPanel(new BorderLayout(10, 0));
        searchBarPanel.setOpaque(false);
        txtSearch = new JTextField();
        JButton btnSearch = new JButton("Search");
        searchBarPanel.add(new JLabel("Search Customer:"), BorderLayout.WEST);
        searchBarPanel.add(txtSearch, BorderLayout.CENTER);
        searchBarPanel.add(btnSearch, BorderLayout.EAST);
        rightTablePanel.add(searchBarPanel, BorderLayout.NORTH);

        String[] columns = {"Customer ID", "Full Name", "Contact Number", "Address", "Date Registered"};
        Object[][] data = {
            {"C001", "Juan Dela Cruz", "09123456789", "Sampaloc, Manila", "2026-09-01"},
            {"C002", "Maria Santos", "09987654321", "Sta. Mesa, Manila", "2026-09-05"}
        };

        tableModel = new DefaultTableModel(data, columns);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        rightTablePanel.add(scrollPane, BorderLayout.CENTER);

        mainContent.add(rightTablePanel, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> {
            if (!txtCustId.getText().isEmpty() && !txtName.getText().isEmpty()) {
                tableModel.addRow(new Object[]{txtCustId.getText(), txtName.getText(), txtContact.getText(), txtAddress.getText(), txtDate.getText()});
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in Customer ID and Name.");
            }
        });

        btnEdit.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.setValueAt(txtCustId.getText(), selectedRow, 0);
                tableModel.setValueAt(txtName.getText(), selectedRow, 1);
                tableModel.setValueAt(txtContact.getText(), selectedRow, 2);
                tableModel.setValueAt(txtAddress.getText(), selectedRow, 3);
                tableModel.setValueAt(txtDate.getText(), selectedRow, 4);
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to edit.");
            }
        });

        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            }
        });

        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText().toLowerCase();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String name = ((String) tableModel.getValueAt(i, 1)).toLowerCase();
                if (name.contains(keyword)) {
                    table.setRowSelectionInterval(i, i);
                    break;
                }
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                txtCustId.setText((String) tableModel.getValueAt(selectedRow, 0));
                txtName.setText((String) tableModel.getValueAt(selectedRow, 1));
                txtContact.setText((String) tableModel.getValueAt(selectedRow, 2));
                txtAddress.setText((String) tableModel.getValueAt(selectedRow, 3));
                txtDate.setText((String) tableModel.getValueAt(selectedRow, 4));
            }
        });
    }

    private void clearForm() {
        txtCustId.setText("");
        txtName.setText("");
        txtContact.setText("");
        txtAddress.setText("");
        txtDate.setText("");
    }
}