package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AddMedicinePanel extends JPanel {

    private JTextField txtName, txtBuyPrice, txtSellPrice, txtQuantity, txtRackNo, txtCompany, txtExpire;
    private JComboBox<String> cmbCategory;

    public AddMedicinePanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Add New Medicine");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(40, 40, 40));
        add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 15));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230)),
            new EmptyBorder(20, 20, 20, 20)
        ));

        txtName = new JTextField();
        cmbCategory = new JComboBox<>(new String[]{"Tablet", "Syrup", "Capsule", "Drop", "Inhaler"});
        txtBuyPrice = new JTextField();
        txtSellPrice = new JTextField();
        txtQuantity = new JTextField();
        txtRackNo = new JTextField();
        txtCompany = new JTextField();
        txtExpire = new JTextField();

        formPanel.add(new JLabel("Medicine Name:"));
        formPanel.add(txtName);

        formPanel.add(new JLabel("Medicine Category:"));
        formPanel.add(cmbCategory);

        formPanel.add(new JLabel("Buy Price:"));
        formPanel.add(txtBuyPrice);

        formPanel.add(new JLabel("Sell Price:"));
        formPanel.add(txtSellPrice);

        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(txtQuantity);

        formPanel.add(new JLabel("Rack No:"));
        formPanel.add(txtRackNo);

        formPanel.add(new JLabel("Company Name:"));
        formPanel.add(txtCompany);

        formPanel.add(new JLabel("Expire Date (YYYY-MM-DD):"));
        formPanel.add(txtExpire);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setOpaque(false);
        JButton btnSave = new JButton("Save Medicine");
        btnSave.setBackground(new Color(39, 174, 96));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        bottomPanel.add(btnSave);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(formPanel, BorderLayout.CENTER);
        wrapper.add(bottomPanel, BorderLayout.SOUTH);

        add(wrapper, BorderLayout.CENTER);

        btnSave.addActionListener(e -> {
            String name = txtName.getText().trim();
            String category = (String) cmbCategory.getSelectedItem();
            String buyPrice = "₱" + txtBuyPrice.getText().trim();
            String sellPrice = "₱" + txtSellPrice.getText().trim();
            String qty = txtQuantity.getText().trim();
            String rack = txtRackNo.getText().trim();
            String company = txtCompany.getText().trim();
            String expire = txtExpire.getText().trim();

            if (!name.isEmpty() && !qty.isEmpty()) {
                String[] newMed = {name, category, buyPrice, sellPrice, qty, rack, company, expire, "Edit | Delete"};
                SharedData.addMedicine(newMed);

                JOptionPane.showMessageDialog(this, "Medicine successfully added!");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in Medicine Name and Quantity.");
            }
        });
    }

    private void clearForm() {
        txtName.setText("");
        txtBuyPrice.setText("");
        txtSellPrice.setText("");
        txtQuantity.setText("");
        txtRackNo.setText("");
        txtCompany.setText("");
        txtExpire.setText("");
    }
}