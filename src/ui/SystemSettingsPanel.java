package ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class SystemSettingsPanel extends JPanel {

    private final Border grayBorder = BorderFactory.createLineBorder(new Color(200, 205, 210), 1);

    public SystemSettingsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        container.setOpaque(false);

        JPanel formCard = new JPanel(new BorderLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 230)));
        formCard.setMaximumSize(new Dimension(850, 620));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        headerPanel.setBackground(new Color(248, 249, 250));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 230)));
        
        JLabel lblHeader = new JLabel("+ Store Information");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHeader.setForeground(new Color(80, 90, 100));
        headerPanel.add(lblHeader);
        formCard.add(headerPanel, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 4, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        addLabel(fieldsPanel, gbc, "Store Title", true);
        addTextFieldWithHelper(fieldsPanel, gbc, "Pharmacy Management System", "eg. XYZ Management System");

        addLabel(fieldsPanel, gbc, "Store Name", true);
        addTextFieldWithHelper(fieldsPanel, gbc, "Vanguard Pharmacy", "eg. Your store name (ABC or XYZ)");

        addLabel(fieldsPanel, gbc, "Store Email", false);
        addStandardField(fieldsPanel, gbc, "Vanguard.p.m.s@gmail.com");

        addLabel(fieldsPanel, gbc, "Store Phone", false);
        addStandardField(fieldsPanel, gbc, "null");

        gbc.gridx = 0; gbc.gridy++;
        gbc.weightx = 0.0;
        JLabel lblAddress = new JLabel("Store Address");
        lblAddress.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblAddress.setForeground(new Color(70, 80, 90));
        fieldsPanel.add(lblAddress, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextArea txtAddress = new JTextArea("null", 3, 20);
        txtAddress.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtAddress.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        txtAddress.setSelectionColor(new Color(220, 225, 230));
        txtAddress.setSelectedTextColor(Color.BLACK);
        JScrollPane scrollAddress = new JScrollPane(txtAddress);
        scrollAddress.setBorder(grayBorder);
        fieldsPanel.add(scrollAddress, gbc);

        String[] currencies = {"PHP (₱)", "USD ($)", "EUR (€)", "GBP (£)", "JPY (¥)", "SGD ($)"};
        addEditableDropdownField(fieldsPanel, gbc, "Store Currency", true, currencies);

        String[] discountTypes = {"Flat", "Percentage (%)", "Senior Citizen (20%)", "PWD (20%)"};
        addEditableDropdownField(fieldsPanel, gbc, "Store Discount Type", true, discountTypes);

        gbc.gridx = 1; gbc.gridy++;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(15, 10, 10, 10);
        
        JButton btnSave = new JButton("✔ Save");
        btnSave.setBackground(new Color(26, 188, 156));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setFocusPainted(false);
        btnSave.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        fieldsPanel.add(btnSave, gbc);

        formCard.add(fieldsPanel, BorderLayout.CENTER);
        container.add(formCard);

        JScrollPane mainScroll = new JScrollPane(container);
        mainScroll.setBorder(null);
        mainScroll.setBackground(new Color(240, 242, 245));
        add(mainScroll, BorderLayout.CENTER);
    }

    private void addLabel(JPanel panel, GridBagConstraints gbc, String text, boolean isRequired) {
        gbc.gridx = 0; gbc.gridy++;
        gbc.weightx = 0.0;
        JPanel lblPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        lblPanel.setOpaque(false);
        
        JLabel label = new JLabel(text + " ");
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
    }

    private void addTextFieldWithHelper(JPanel panel, GridBagConstraints gbc, String defaultValue, String helperText) {
        gbc.gridx = 1; 
        gbc.weightx = 1.0;
        
        JPanel fieldWrapper = new JPanel();
        fieldWrapper.setLayout(new BoxLayout(fieldWrapper, BoxLayout.Y_AXIS));
        fieldWrapper.setOpaque(false);

        JTextField textField = createStyledTextField(defaultValue);
        fieldWrapper.add(textField);

        if (helperText != null && !helperText.isEmpty()) {
            fieldWrapper.add(Box.createVerticalStrut(2));
            JLabel lblHelper = new JLabel(helperText);
            lblHelper.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            lblHelper.setForeground(new Color(130, 140, 150));
            fieldWrapper.add(lblHelper);
        }

        panel.add(fieldWrapper, gbc);
    }

    private void addStandardField(JPanel panel, GridBagConstraints gbc, String defaultValue) {
        gbc.gridx = 1; 
        gbc.weightx = 1.0;
        JTextField textField = createStyledTextField(defaultValue);
        panel.add(textField, gbc);
    }

    private JTextField createStyledTextField(String defaultValue) {
        JTextField textField = new JTextField(defaultValue);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textField.setPreferredSize(new Dimension(350, 30));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        textField.setBorder(BorderFactory.createCompoundBorder(grayBorder, BorderFactory.createEmptyBorder(0, 5, 0, 5)));
        textField.setSelectionColor(new Color(220, 225, 230));
        textField.setSelectedTextColor(Color.BLACK);
        return textField;
    }

    private void addEditableDropdownField(JPanel panel, GridBagConstraints gbc, String labelText, boolean isRequired, String[] items) {
        addLabel(panel, gbc, labelText, isRequired);
        
        gbc.gridx = 1; 
        gbc.weightx = 1.0;
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
            protected javax.swing.plaf.basic.ComboPopup createPopup() {
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
                
                renderer.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 230)),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)
                ));
                
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
        
        panel.add(comboBox, gbc);
    }
}