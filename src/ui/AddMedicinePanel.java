package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AddMedicinePanel extends JPanel {

    private JTextField txtName, txtBuyPrice, txtSellPrice, txtQuantity, txtCompany, txtExpire;
    private JComboBox<String> cmbCategory;

    public AddMedicinePanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Add New Medicine");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(60, 65, 70));
        add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 12, 15));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 215, 220)),
            new EmptyBorder(20, 20, 20, 20)
        ));

        txtName = createStyledTextField();
        
        cmbCategory = new JComboBox<>(new String[]{"-- Select Category --", "Tablet", "Syrup", "Capsule", "Drop", "Inhaler"});
        cmbCategory.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbCategory.setBackground(Color.WHITE);
        cmbCategory.setForeground(new Color(70, 75, 80));
        cmbCategory.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 205, 210)),
            BorderFactory.createEmptyBorder(2, 4, 2, 4)
        ));

        txtBuyPrice = createStyledTextField();
        txtSellPrice = createStyledTextField();
        txtQuantity = createStyledTextField();
        txtCompany = createStyledTextField();
        txtExpire = createStyledTextField();

        addFormRow(formPanel, "Medicine Name", txtName);
        addFormRow(formPanel, "Medicine Category", cmbCategory);
        addFormRow(formPanel, "Buy Price", txtBuyPrice);
        addFormRow(formPanel, "Sell Price", txtSellPrice);
        addFormRow(formPanel, "Quantity", txtQuantity);
        addFormRow(formPanel, "Company Name", txtCompany);
        addFormRow(formPanel, "Expire Date (YYYY-MM-DD)", txtExpire);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bottomPanel.setOpaque(false);
        
        JButton btnSave = new JButton("Save Medicine");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSave.setBackground(new Color(40, 167, 69));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton btnClear = new JButton("Clear Form");
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClear.setBackground(new Color(220, 53, 69));
        btnClear.setForeground(Color.WHITE);
        btnClear.setFocusPainted(false);
        btnClear.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bottomPanel.add(btnSave);
        bottomPanel.add(btnClear);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(formPanel, BorderLayout.CENTER);
        
        JPanel bottomWrapper = new JPanel(new BorderLayout());
        bottomWrapper.setOpaque(false);
        bottomWrapper.setBorder(new EmptyBorder(12, 0, 0, 0));
        bottomWrapper.add(bottomPanel, BorderLayout.WEST);
        
        wrapper.add(bottomWrapper, BorderLayout.SOUTH);

        add(wrapper, BorderLayout.CENTER);

        btnClear.addActionListener(e -> clearForm());

        btnSave.addActionListener(e -> {
            String name = txtName.getText().trim();
            String category = (String) cmbCategory.getSelectedItem();
            if (category == null || category.equals("-- Select Category --")) {
                category = "";
            }
            String buyPrice = "₱" + txtBuyPrice.getText().trim();
            String sellPrice = "₱" + txtSellPrice.getText().trim();
            String qty = txtQuantity.getText().trim();
            String company = txtCompany.getText().trim();
            String expire = txtExpire.getText().trim();

            if (!name.isEmpty() && !qty.isEmpty() && !category.isEmpty()) {
                String[] newMed = {name, category, buyPrice, sellPrice, qty, company, expire, ""};
                
                SharedData.addMedicine(newMed);
                JOptionPane.showMessageDialog(this, "Medicine successfully added!");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in Medicine Name, Category, and Quantity.");
            }
        });
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

    private void addFormRow(JPanel panel, String labelText, JComponent field) {
        JLabel lbl = new JLabel("<html><b>" + labelText + ":</b> <font color='red'>*</font></html>");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(70, 75, 80));
        panel.add(lbl);
        panel.add(field);
    }

    private void clearForm() {
        txtName.setText("");
        if (cmbCategory.getItemCount() > 0) cmbCategory.setSelectedIndex(0);
        txtBuyPrice.setText("");
        txtSellPrice.setText("");
        txtQuantity.setText("");
        txtCompany.setText("");
        txtExpire.setText("");
    }
}