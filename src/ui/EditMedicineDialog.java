package ui;

import db.MedicineDAO;
import models.Medicine;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class EditMedicineDialog extends JDialog {

    private final Border grayBorder = BorderFactory.createLineBorder(new Color(200, 205, 210), 1);

    private JTextField txtName;
    private JComboBox<String> cmbCategory;
    private JTextField txtBuyPrice;
    private JTextField txtSellPrice;
    private JTextField txtQuantity;
    private JTextField txtCompany;
    private JDatePickerImpl datePickerExpire;
    private UtilDateModel dateModel;

    private boolean updated = false;
    private Medicine medicine;

    public EditMedicineDialog(Frame parent, Medicine med) {
        super(parent, "Edit Medicine", true);
        this.medicine = med;

        setLayout(new BorderLayout());
        setSize(850, 680);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        container.setBackground(new Color(240, 242, 245));

        JPanel formCard = new JPanel(new BorderLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 230)));
        formCard.setMaximumSize(new Dimension(850, 680));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        headerPanel.setBackground(new Color(248, 249, 250));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 230)));

        JLabel lblHeader = new JLabel("Edit Medicine Details");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHeader.setForeground(new Color(80, 90, 100));
        headerPanel.add(lblHeader);
        formCard.add(headerPanel, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;

        txtName = createStyledTextField(med.getName());
        addFieldRow(fieldsPanel, gbc, "Medicine Name", true, txtName);

        String[] categories = {"-- Select Category --", "Tablet", "Capsule", "Syrup", "Injection", "Ointment"};
        cmbCategory = createStyledDropdown(categories);
        cmbCategory.setSelectedItem(med.getMedicineCategory());
        addDropdownRow(fieldsPanel, gbc, "Medicine Category", true, cmbCategory);

        txtBuyPrice = createStyledTextField(String.valueOf(med.getBuyPrice()));
        addFieldRow(fieldsPanel, gbc, "Buy Price", true, txtBuyPrice);

        txtSellPrice = createStyledTextField(String.valueOf(med.getSellPrice()));
        addFieldRow(fieldsPanel, gbc, "Sell Price", true, txtSellPrice);

        txtQuantity = createStyledTextField(String.valueOf(med.getStock()));
        addFieldRow(fieldsPanel, gbc, "Quantity", true, txtQuantity);

        txtCompany = createStyledTextField(med.getCompanyName() != null ? med.getCompanyName() : "");
        addFieldRow(fieldsPanel, gbc, "Company Name", true, txtCompany);

        dateModel = new UtilDateModel();
        try {
            if (med.getExpiryDate() != null && !med.getExpiryDate().isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(med.getExpiryDate());
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                dateModel.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                dateModel.setSelected(true);
            }
        } catch (Exception ignored) {}

        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        
        JDatePanelImpl datePanel = new JDatePanelImpl(dateModel, p);
        datePanel.setBackground(Color.WHITE);
        for (Component comp : datePanel.getComponents()) {
            comp.setBackground(Color.WHITE);
        }
        
        datePickerExpire = new JDatePickerImpl(datePanel, new AddMedicinePanel.DateLabelFormatter());
        datePickerExpire.setPreferredSize(new Dimension(350, 30));
        datePickerExpire.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        datePickerExpire.setBorder(grayBorder);

        JFormattedTextField dateTextField = datePickerExpire.getJFormattedTextField();
        dateTextField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateTextField.setBackground(Color.WHITE);
        dateTextField.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        if (datePickerExpire.getComponentCount() > 1) {
            Component buttonComp = datePickerExpire.getComponent(1);
            if (buttonComp instanceof JButton) {
                applyDropdownStyleToButton((JButton) buttonComp);
            }
        }

        addFieldRow(fieldsPanel, gbc, "Expire Date", true, datePickerExpire);

        gbc.gridx = 1;
        gbc.gridy++;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(15, 10, 10, 10);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBackground(new Color(192, 57, 43));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancel.setFocusPainted(false);
        btnCancel.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBackground(new Color(26, 143, 136));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnUpdate.setFocusPainted(false);
        btnUpdate.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnUpdate.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnPanel.add(btnCancel);
        btnPanel.add(btnUpdate);
        fieldsPanel.add(btnPanel, gbc);

        formCard.add(fieldsPanel, BorderLayout.CENTER);
        container.add(formCard);
        
        JScrollPane mainScroll = new JScrollPane(container);
        mainScroll.setBorder(null);
        mainScroll.setBackground(new Color(240, 242, 245));
        
        add(mainScroll, BorderLayout.CENTER);

        btnUpdate.addActionListener(e -> {
            try {
                String name = txtName.getText().trim();
                String category = cmbCategory.getSelectedItem() != null ? cmbCategory.getSelectedItem().toString().trim() : "";
                double buyPrice = Double.parseDouble(txtBuyPrice.getText().trim());
                double sellPrice = Double.parseDouble(txtSellPrice.getText().trim());
                int stock = Integer.parseInt(txtQuantity.getText().trim());
                String company = txtCompany.getText().trim();
                String expireDateStr = datePickerExpire.getJFormattedTextField().getText().trim();

                if (name.isEmpty() || category.equals("-- Select Category --") || company.isEmpty() || expireDateStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill up all required fields.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                medicine.setName(name);
                medicine.setMedicineCategory(category);
                medicine.setBuyPrice(buyPrice);
                medicine.setSellPrice(sellPrice);
                medicine.setStock(stock);
                medicine.setCompanyName(company);
                medicine.setExpiryDate(expireDateStr);

                MedicineDAO.updateMedicine(medicine);
                updated = true;
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numeric values for prices and quantity.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dispose());
    }

    public boolean isUpdated() {
        return updated;
    }

    private void addFieldRow(JPanel panel, GridBagConstraints gbc, String labelText, boolean isRequired, JComponent field) {
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        JPanel lblPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        lblPanel.setOpaque(false);
        JLabel label = new JLabel(labelText + " ");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(70, 80, 90));
        lblPanel.add(label);
        if (isRequired) {
            JLabel lblStar = new JLabel("*");
            lblStar.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblStar.setForeground(Color.RED);
            lblPanel.add(lblStar);
        }
        panel.add(lblPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
        gbc.gridy++;
    }

    private void addDropdownRow(JPanel panel, GridBagConstraints gbc, String labelText, boolean isRequired, JComboBox<String> box) {
        addFieldRow(panel, gbc, labelText, isRequired, box);
    }

    private JTextField createStyledTextField(String val) {
        JTextField tf = new JTextField(val);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setPreferredSize(new Dimension(350, 30));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        tf.setBorder(BorderFactory.createCompoundBorder(grayBorder, BorderFactory.createEmptyBorder(0, 5, 0, 5)));
        return tf;
    }

    // Eksaktong istilo galing sa AddMedicinePanel para mawala ang blue highlight/line at maging malinis
    private JComboBox<String> createStyledDropdown(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setPreferredSize(new Dimension(350, 30));
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        comboBox.setEditable(true);
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(grayBorder);

        Component editorComp = comboBox.getEditor().getEditorComponent();
        if (editorComp instanceof JTextField) {
            JTextField editorField = (JTextField) editorComp;
            editorField.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            editorField.setSelectionColor(new Color(210, 215, 220));
            editorField.setSelectedTextColor(Color.BLACK);
        }

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
                if (isSelected) {
                    renderer.setBackground(new Color(210, 215, 220));
                    renderer.setForeground(Color.BLACK);
                } else {
                    renderer.setBackground(Color.WHITE);
                    renderer.setForeground(Color.BLACK);
                }
                renderer.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 230)),
                        BorderFactory.createEmptyBorder(6, 10, 6, 10)
                ));
                return renderer;
            }
        });

        return comboBox;
    }

    private void applyDropdownStyleToButton(JButton btn) {
        btn.setText("");
        btn.setPreferredSize(new Dimension(30, 30));
        btn.setFocusable(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, c.getWidth(), c.getHeight());
                g2d.setColor(new Color(200, 205, 210));
                g2d.drawLine(0, 0, 0, c.getHeight());
                g2d.setColor(new Color(80, 80, 80));
                int[] xPoints = {c.getWidth() / 2 - 4, c.getWidth() / 2 + 4, c.getWidth() / 2};
                int[] yPoints = {c.getHeight() / 2 - 2, c.getHeight() / 2 - 2, c.getHeight() / 2 + 3};
                g2d.fillPolygon(xPoints, yPoints, 3);
                g2d.dispose();
            }
        });
    }
}