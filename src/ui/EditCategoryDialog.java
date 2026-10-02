package ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class EditCategoryDialog extends JDialog {

    private final Border grayBorder = BorderFactory.createLineBorder(new Color(200, 205, 210), 1);
    private JTextField txtCategoryName;
    private JComboBox<String> cmbStatus;
    private boolean updated = false;

    public EditCategoryDialog(Frame parent, String categoryName, String status) {
        super(parent, "Edit Medicine Category", true);
        setLayout(new BorderLayout());
        setSize(450, 300);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        JLabel lblHeader = new JLabel("Edit Category Details");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHeader.setForeground(new Color(80, 90, 100));
        headerPanel.add(lblHeader);
        container.add(headerPanel, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblCatName = new JLabel("<html>Category Name <font color='red'>*</font></html>");
        lblCatName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtCategoryName = new JTextField(categoryName, 15);
        txtCategoryName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtCategoryName.setPreferredSize(new Dimension(250, 30));
        txtCategoryName.setBorder(BorderFactory.createCompoundBorder(grayBorder, BorderFactory.createEmptyBorder(0, 8, 0, 8)));

        JLabel lblStatus = new JLabel("<html>Status <font color='red'>*</font></html>");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        cmbStatus = createStyledDropdown(new String[]{"Active", "Inactive"}, 250);
        cmbStatus.setSelectedItem(status);

        gbc.gridx = 0; gbc.gridy = 0;
        fieldsPanel.add(lblCatName, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        fieldsPanel.add(txtCategoryName, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        fieldsPanel.add(lblStatus, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        fieldsPanel.add(cmbStatus, gbc);

        container.add(fieldsPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBackground(new Color(192, 57, 43));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancel.setFocusPainted(false);
        btnCancel.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> dispose());

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBackground(new Color(26, 143, 136));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnUpdate.setFocusPainted(false);
        btnUpdate.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        btnUpdate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUpdate.addActionListener(e -> {
            if (txtCategoryName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Category Name cannot be empty.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            updated = true;
            dispose();
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnUpdate);
        container.add(btnPanel, BorderLayout.SOUTH);

        add(container, BorderLayout.CENTER);
    }

    private JComboBox<String> createStyledDropdown(String[] items, int width) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboBox.setPreferredSize(new Dimension(width, 30));
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        comboBox.setEditable(false);
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(grayBorder);
        comboBox.setFocusable(true);

        comboBox.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected javax.swing.plaf.basic.BasicComboPopup createPopup() {
                javax.swing.plaf.basic.BasicComboPopup popup = new javax.swing.plaf.basic.BasicComboPopup(comboBox);
                popup.setBorder(grayBorder);
                return popup;
            }

            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d.setColor(Color.WHITE);
                        g2d.fillRect(0, 0, getWidth(), getHeight());
                        g2d.setColor(new Color(200, 205, 210));
                        g2d.drawLine(0, 0, 0, getHeight());
                        g2d.setColor(new Color(80, 80, 80));
                        int[] xPoints = {getWidth() / 2 - 4, getWidth() / 2 + 4, getWidth() / 2};
                        int[] yPoints = {getHeight() / 2 - 2, getHeight() / 2 - 2, getHeight() / 2 + 3};
                        g2d.fillPolygon(xPoints, yPoints, 3);
                        g2d.dispose();
                    }
                };
                btn.setBorder(BorderFactory.createEmptyBorder());
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.setFocusable(false);
                return btn;
            }
        });

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel renderer = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                renderer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                if (isSelected) {
                    renderer.setBackground(new Color(210, 215, 220));
                    renderer.setForeground(Color.BLACK);
                } else {
                    renderer.setBackground(Color.WHITE);
                    renderer.setForeground(Color.BLACK);
                }
                return renderer;
            }
        });

        return comboBox;
    }

    public boolean isUpdated() {
        return updated;
    }

    public String getCategoryName() {
        return txtCategoryName.getText().trim();
    }

    public String getStatus() {
        return cmbStatus.getSelectedItem().toString();
    }
}