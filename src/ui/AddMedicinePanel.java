package ui;

import db.MedicineDAO;
import db.SalesDAO;
import models.Medicine;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

public class AddMedicinePanel extends JPanel {

    private final Border grayBorder =
            BorderFactory.createLineBorder(new Color(200, 205, 210), 1);

    private JTextField txtName;
    private JComboBox<String> cmbCategory;
    private JTextField txtBuyPrice;
    private JTextField txtSellPrice;
    private JTextField txtQuantity;
    private JTextField txtCompany;
    
    private JDatePickerImpl datePickerExpire;
    private UtilDateModel dateModel;

    public AddMedicinePanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        );
        container.setOpaque(false);

        JPanel formCard = new JPanel(new BorderLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(
                BorderFactory.createLineBorder(
                        new Color(220, 225, 230)
                )
        );
        formCard.setMaximumSize(new Dimension(850, 680));

        JPanel headerPanel =
                new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));

        headerPanel.setBackground(new Color(248, 249, 250));
        headerPanel.setBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 1, 0,
                        new Color(220, 225, 230)
                )
        );

        JLabel lblHeader =
                new JLabel("Add New Medicine");

        lblHeader.setFont(
                new Font("Segoe UI", Font.BOLD, 14)
        );

        lblHeader.setForeground(
                new Color(80, 90, 100)
        );

        headerPanel.add(lblHeader);
        formCard.add(headerPanel, BorderLayout.NORTH);

        JPanel fieldsPanel =
                new JPanel(new GridBagLayout());

        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 25, 20, 25
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(8, 10, 8, 10);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.gridy = 0;

        txtName = createStyledTextField("");
        addFieldRow(
                fieldsPanel,
                gbc,
                "Medicine Name",
                true,
                txtName
        );

        String[] categories = {
                "-- Select Category --",
                "Tablet",
                "Capsule",
                "Syrup",
                "Injection",
                "Ointment"
        };

        cmbCategory =
                createStyledDropdown(categories);

        addDropdownRow(
                fieldsPanel,
                gbc,
                "Medicine Category",
                true,
                cmbCategory
        );

        txtBuyPrice = createStyledTextField("");

        addFieldRow(
                fieldsPanel,
                gbc,
                "Buy Price",
                true,
                txtBuyPrice
        );

        txtSellPrice = createStyledTextField("");

        addFieldRow(
                fieldsPanel,
                gbc,
                "Sell Price",
                true,
                txtSellPrice
        );

        txtQuantity = createStyledTextField("");

        addFieldRow(
                fieldsPanel,
                gbc,
                "Quantity",
                true,
                txtQuantity
        );

        txtCompany = createStyledTextField("");

        addFieldRow(
                fieldsPanel,
                gbc,
                "Company Name",
                true,
                txtCompany
        );
        
        dateModel = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        
        JDatePanelImpl datePanel = new JDatePanelImpl(dateModel, p);
        datePickerExpire = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        datePickerExpire.setPreferredSize(new Dimension(350, 30));
        datePickerExpire.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        datePickerExpire.setBorder(grayBorder); 

        JFormattedTextField dateTextField = datePickerExpire.getJFormattedTextField();
        dateTextField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateTextField.setBackground(Color.WHITE);
        dateTextField.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5)); 
        
        Component buttonComp = datePickerExpire.getComponent(1);
        
        if (buttonComp instanceof JButton) {
                applyDropdownStyleToButton((JButton) buttonComp);
        }

        addFieldRow(
                fieldsPanel,
                gbc,
                "Expire Date",
                true,
                datePickerExpire
        );

        gbc.gridx = 1;
        gbc.gridy++;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets =
                new Insets(15, 10, 10, 10);

        JPanel btnPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                10,
                                0
                        )
                );

        btnPanel.setOpaque(false);

        JButton btnSave =
                new JButton("Save Medicine");

        btnSave.setBackground(
                new Color(39, 174, 96)
        );

        btnSave.setForeground(Color.WHITE);

        btnSave.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        btnSave.setFocusPainted(false);

        btnSave.setBorder(
                BorderFactory.createEmptyBorder(
                        8, 20, 8, 20
                )
        );

        btnSave.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        btnPanel.add(btnSave);

        JButton btnClear =
                new JButton("Clear Form");

        btnClear.setBackground(
                new Color(231, 76, 60)
        );

        btnClear.setForeground(Color.WHITE);

        btnClear.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        btnClear.setFocusPainted(false);

        btnClear.setBorder(
                BorderFactory.createEmptyBorder(
                        8, 20, 8, 20
                )
        );

        btnClear.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        btnPanel.add(btnClear);

        fieldsPanel.add(btnPanel, gbc);

        formCard.add(
                fieldsPanel,
                BorderLayout.CENTER
        );

        container.add(formCard);

        JScrollPane mainScroll =
                new JScrollPane(container);

        mainScroll.setBorder(null);

        mainScroll.setBackground(
                new Color(240, 242, 245)
        );

        add(
                mainScroll,
                BorderLayout.CENTER
        );

        btnSave.addActionListener(e -> {

            try {

                String name =
                        txtName.getText().trim();

                String category =
                        cmbCategory.getSelectedItem() != null
                                ? cmbCategory
                                        .getSelectedItem()
                                        .toString()
                                        .trim()
                                : "";

                String buyPriceStr =
                        txtBuyPrice.getText().trim();

                String sellPriceStr =
                        txtSellPrice.getText().trim();

                String quantityStr =
                        txtQuantity.getText().trim();

                String company =
                        txtCompany.getText().trim();

                String expireDateStr = datePickerExpire.getJFormattedTextField().getText().trim();

                if (
                        name.isEmpty()
                                || category.equals(
                                        "-- Select Category --"
                                )
                                || buyPriceStr.isEmpty()
                                || sellPriceStr.isEmpty()
                                || quantityStr.isEmpty()
                                || company.isEmpty()
                                || expireDateStr.isEmpty()
                ) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Please fill up all the required fields.",
                            "Error",
                            JOptionPane.WARNING_MESSAGE
                    );

                    return;
                }

                double buyPrice =
                        Double.parseDouble(
                                buyPriceStr
                        );

                double sellPrice =
                        Double.parseDouble(
                                sellPriceStr
                        );

                int stock =
                        Integer.parseInt(
                                quantityStr
                        );

                if (buyPrice <= 0) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Buy Price must be greater than 0.",
                            "Invalid Buy Price",
                            JOptionPane.WARNING_MESSAGE
                    );

                    return;
                }

                if (sellPrice <= 0) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Sell Price must be greater than 0.",
                            "Invalid Sell Price",
                            JOptionPane.WARNING_MESSAGE
                    );

                    return;
                }

                if (stock <= 0) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Quantity must be greater than 0.",
                            "Invalid Quantity",
                            JOptionPane.WARNING_MESSAGE
                    );

                    return;
                }

                Medicine newMed =
                        new Medicine();

                newMed.setName(name);
                newMed.setMedicineCategory(category);
                newMed.setBuyPrice(buyPrice);
                newMed.setSellPrice(sellPrice);
                newMed.setStock(stock);
                newMed.setCompanyName(company);
                newMed.setExpiryDate(expireDateStr);

                MedicineDAO.addMedicine(newMed);

                double totalPurchaseCost =
                        buyPrice * stock;

                SalesDAO.addExpense(
                        "Purchase of "
                                + stock
                                + "x "
                                + name,
                        totalPurchaseCost,
                        java.time.LocalDate
                                .now()
                                .toString()
                );

                JOptionPane.showMessageDialog(
                        this,
                        "Successfully added new medicine!\n\n"
                                + "Quantity: "
                                + stock
                                + "\nBuy Price: ₱"
                                + String.format(
                                        "%.2f",
                                        buyPrice
                                )
                                + "\nTotal Purchase Cost: ₱"
                                + String.format(
                                        "%.2f",
                                        totalPurchaseCost
                                )
                                + "\n\n"
                                + "The purchase has been recorded in Expenses.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                clearForm();

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter valid numbers for Buy Price, Sell Price, and Quantity.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE
                );

            } catch (Exception ex) {

                ex.printStackTrace();

                JOptionPane.showMessageDialog(
                        this,
                        "Error in saving.\n\n"
                                + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        btnClear.addActionListener(
                e -> clearForm()
        );
    }

    private void clearForm() {

        txtName.setText("");

        if (cmbCategory.getItemCount() > 0) {
            cmbCategory.setSelectedIndex(0);
        }

        txtBuyPrice.setText("");
        txtSellPrice.setText("");
        txtQuantity.setText("");
        txtCompany.setText("");
        dateModel.setValue(null);
        datePickerExpire.getJFormattedTextField().setText("");
    }

    private void addFieldRow(
            JPanel panel,
            GridBagConstraints gbc,
            String labelText,
            boolean isRequired,
            JComponent field) {

        gbc.gridx = 0;
        gbc.weightx = 0.0;

        JPanel lblPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                0,
                                0
                        )
                );

        lblPanel.setOpaque(false);

        JLabel label =
                new JLabel(labelText + " ");

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        label.setForeground(
                new Color(70, 80, 90)
        );

        lblPanel.add(label);

        if (isRequired) {

            JLabel lblStar =
                    new JLabel("*");

            lblStar.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            13
                    )
            );

            lblStar.setForeground(Color.RED);

            lblPanel.add(lblStar);
        }

        panel.add(lblPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;

        panel.add(field, gbc);

        gbc.gridy++;
    }

    private void addDropdownRow(
            JPanel panel,
            GridBagConstraints gbc,
            String labelText,
            boolean isRequired,
            JComboBox<String> comboBox) {

        addFieldRow(
                panel,
                gbc,
                labelText,
                isRequired,
                comboBox
        );
    }

    private JTextField createStyledTextField(
            String defaultValue) {

        JTextField textField =
                new JTextField(defaultValue);

        textField.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        textField.setPreferredSize(
                new Dimension(350, 30)
        );

        textField.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        30
                )
        );

        textField.setBorder(
                BorderFactory.createCompoundBorder(
                        grayBorder,
                        BorderFactory.createEmptyBorder(
                                0, 5, 0, 5
                        )
                )
        );

        textField.setSelectionColor(
                new Color(220, 225, 230)
        );

        textField.setSelectedTextColor(
                Color.BLACK
        );

        return textField;
    }

    private JComboBox<String> createStyledDropdown(
            String[] items) {

        JComboBox<String> comboBox =
                new JComboBox<>(items);

        comboBox.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        comboBox.setPreferredSize(
                new Dimension(350, 30)
        );

        comboBox.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        30
                )
        );

        comboBox.setEditable(true);
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(grayBorder);

        Component editorComp =
                comboBox
                        .getEditor()
                        .getEditorComponent();

        if (editorComp instanceof JTextField) {

            JTextField editorField =
                    (JTextField) editorComp;

            editorField.setBorder(
                    BorderFactory.createEmptyBorder(
                            0, 5, 0, 5
                    )
            );

            editorField.setSelectionColor(
                    new Color(210, 215, 220)
            );

            editorField.setSelectedTextColor(
                    Color.BLACK
            );
        }

        comboBox.setUI(
                new javax.swing.plaf.basic.BasicComboBoxUI() {

                    @Override
                    protected javax.swing.plaf.basic.BasicComboPopup createPopup() {

                        javax.swing.plaf.basic.BasicComboPopup popup =
                                new javax.swing.plaf.basic.BasicComboPopup(
                                        comboBox
                                );

                        popup.setBorder(grayBorder);

                        return popup;
                    }

                    @Override
                    protected JButton createArrowButton() {

                        JButton btn =
                                new JButton() {

                                    @Override
                                    protected void paintComponent(
                                            Graphics g) {

                                        Graphics2D g2d =
                                                (Graphics2D) g.create();

                                        g2d.setRenderingHint(
                                                RenderingHints.KEY_ANTIALIASING,
                                                RenderingHints.VALUE_ANTIALIAS_ON
                                        );

                                        g2d.setColor(Color.WHITE);

                                        g2d.fillRect(
                                                0,
                                                0,
                                                getWidth(),
                                                getHeight()
                                        );

                                        g2d.setColor(
                                                new Color(
                                                        200,
                                                        205,
                                                        210
                                                )
                                        );

                                        g2d.drawLine(
                                                0,
                                                0,
                                                0,
                                                getHeight()
                                        );

                                        g2d.setColor(
                                                new Color(
                                                        80,
                                                        80,
                                                        80
                                                )
                                        );

                                        int[] xPoints = {
                                                getWidth() / 2 - 4,
                                                getWidth() / 2 + 4,
                                                getWidth() / 2
                                        };

                                        int[] yPoints = {
                                                getHeight() / 2 - 2,
                                                getHeight() / 2 - 2,
                                                getHeight() / 2 + 3
                                        };

                                        g2d.fillPolygon(
                                                xPoints,
                                                yPoints,
                                                3
                                        );

                                        g2d.dispose();
                                    }
                                };

                        btn.setBorder(
                                BorderFactory.createEmptyBorder()
                        );

                        btn.setCursor(
                                new Cursor(
                                        Cursor.HAND_CURSOR
                                )
                        );

                        btn.setFocusable(false);

                        return btn;
                    }
                }
        );

        comboBox.setRenderer(
                new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean isSelected,
                            boolean cellHasFocus) {

                        JLabel renderer =
                                (JLabel) super
                                        .getListCellRendererComponent(
                                                list,
                                                value,
                                                index,
                                                isSelected,
                                                cellHasFocus
                                        );

                        renderer.setBorder(
                                BorderFactory.createCompoundBorder(
                                        BorderFactory.createMatteBorder(
                                                0,
                                                0,
                                                1,
                                                0,
                                                new Color(
                                                        220,
                                                        225,
                                                        230
                                                )
                                        ),
                                        BorderFactory.createEmptyBorder(
                                                6,
                                                10,
                                                6,
                                                10
                                        )
                                )
                        );

                        return renderer;
                    }
                }
        );

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
                        g2d.setRenderingHint(
                                RenderingHints.KEY_ANTIALIASING, 
                                RenderingHints.VALUE_ANTIALIAS_ON
                        );
                                
                        g2d.setColor(Color.WHITE);
                        g2d.fillRect(0, 0, c.getWidth(), c.getHeight());
                        g2d.setColor(new Color(200, 205, 210));
                        g2d.drawLine(0, 0, 0, c.getHeight());
                        g2d.setColor(new Color(80, 80, 80));
                        int[] xPoints = {
                                c.getWidth() / 2 - 4, 
                                c.getWidth() / 2 + 4, 
                                c.getWidth() / 2
                        };
                        int[] yPoints = {                                        
                                c.getHeight() / 2 - 2, 
                                c.getHeight() / 2 - 2, 
                                c.getHeight() / 2 + 3
                                };

                        g2d.fillPolygon(xPoints, yPoints, 3);
                        g2d.dispose();

                        }
                });
        }

    public static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {

        private final String datePattern = "yyyy-MM-dd";
        private final SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

        @Override
        public Object stringToValue(String text) throws ParseException {
            return dateFormatter.parseObject(text);
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());
            }
            return "";
        }
    }
}