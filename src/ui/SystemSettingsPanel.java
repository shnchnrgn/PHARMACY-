package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.Border;

public class SystemSettingsPanel extends JPanel {

    private static final Color TEAL = new Color(13, 148, 136);
    private static final Color TEAL_DARK = new Color(15, 118, 110);
    private static final Color TEAL_LIGHT = new Color(204, 240, 236);
    private static final Color TEAL_TINT = new Color(240, 250, 249);
    private static final Color DIALOG_TEAL = new Color(26, 143, 136);
    private static final Color TEXT_DARK = new Color(45, 55, 60);
    private static final Color TEXT_MUTED = new Color(110, 118, 125);
    private static final Color BORDER_COLOR = new Color(215, 220, 225);

    private final Border grayBorder = BorderFactory.createLineBorder(new Color(200, 205, 210), 1);
    private final Border tealBorder = BorderFactory.createLineBorder(TEAL, 1);

    private JTextField txtStoreTitle, txtStoreName, txtStoreEmail, txtStorePhone;
    private JTextArea txtAddress;
    private JComboBox<String> cmbCurrency, cmbDiscountType;

    public SystemSettingsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createEmptyBorder(25, 40, 30, 40));
        container.setOpaque(false);

        JLabel pageTitle = new JLabel("System Settings");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        pageTitle.setForeground(TEAL_DARK);
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pageSubtitle = new JLabel("Manage your store details, currency, and discount settings");
        pageSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pageSubtitle.setForeground(TEXT_MUTED);
        pageSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        container.add(pageTitle);
        container.add(Box.createVerticalStrut(2));
        container.add(pageSubtitle);
        container.add(Box.createVerticalStrut(18));

        JPanel formCard = new JPanel(new BorderLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        formCard.setMaximumSize(new Dimension(850, 620));
        formCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        headerPanel.setBackground(TEAL_TINT);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, TEAL),
                BorderFactory.createMatteBorder(0, 5, 0, 0, TEAL)
        ));

        JLabel lblHeader = new JLabel("+ Store Information");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHeader.setForeground(TEAL_DARK);
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
        txtStoreTitle = addTextFieldWithHelper(fieldsPanel, gbc, "Pharmacy Management System", "eg. XYZ Management System");

        addLabel(fieldsPanel, gbc, "Store Name", true);
        txtStoreName = addTextFieldWithHelper(fieldsPanel, gbc, "Vanguard Pharmacy", "eg. Your store name (ABC or XYZ)");

        addLabel(fieldsPanel, gbc, "Store Email", false);
        txtStoreEmail = addStandardField(fieldsPanel, gbc, "Vanguard.p.m.s@gmail.com");

        addLabel(fieldsPanel, gbc, "Store Phone", false);
        txtStorePhone = addStandardField(fieldsPanel, gbc, "");

        gbc.gridx = 0; gbc.gridy++;
        gbc.weightx = 0.0;
        JLabel lblAddress = new JLabel("Store Address");
        lblAddress.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblAddress.setForeground(TEXT_DARK);
        fieldsPanel.add(lblAddress, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtAddress = new JTextArea("", 3, 20);
        txtAddress.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtAddress.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        txtAddress.setSelectionColor(TEAL_LIGHT);
        txtAddress.setSelectedTextColor(TEAL_DARK);
        txtAddress.setCaretColor(TEAL);
        JScrollPane scrollAddress = new JScrollPane(txtAddress);
        scrollAddress.setBorder(grayBorder);
        addFocusHighlight(txtAddress, scrollAddress, grayBorder, tealBorder);
        fieldsPanel.add(scrollAddress, gbc);

        String[] currencies = {"PHP (₱)", "USD ($)", "EUR (€)", "GBP (£)", "JPY (¥)", "SGD ($)"};
        cmbCurrency = addEditableDropdownField(fieldsPanel, gbc, "Store Currency", true, currencies);

        String[] discountTypes = {"Flat", "Percentage (%)", "Senior Citizen (20%)", "PWD (20%)"};
        cmbDiscountType = addEditableDropdownField(fieldsPanel, gbc, "Store Discount Type", true, discountTypes);

        gbc.gridx = 1; gbc.gridy++;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(15, 10, 10, 10);

        JButton btnSave = new JButton("Save");
        btnSave.setBackground(TEAL);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setFocusPainted(false);
        btnSave.setBorder(BorderFactory.createEmptyBorder(9, 28, 9, 28));
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSave.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnSave.setBackground(TEAL_DARK);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnSave.setBackground(TEAL);
            }
        });

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String storeTitle = txtStoreTitle.getText().trim();
                String storeName = txtStoreName.getText().trim();

                if (storeTitle.isEmpty() || storeName.isEmpty()) {
                    showCustomDialog("Store Title and Store Name are required.", "Error");
                    return;
                }

                SharedData.storeTitle = storeTitle;
                SharedData.storeName = storeName;
                SharedData.storeEmail = txtStoreEmail.getText().trim();
                SharedData.storePhone = txtStorePhone.getText().trim();
                SharedData.storeAddress = txtAddress.getText().trim();

                String selectedCurrency = (String) cmbCurrency.getSelectedItem();
                SharedData.currencySymbol = SharedData.extractCurrencySymbol(selectedCurrency);

                if (cmbDiscountType.getSelectedItem() != null) {
                    SharedData.discountType = cmbDiscountType.getSelectedItem().toString();
                }

                Window parentWindow = SwingUtilities.getWindowAncestor(SystemSettingsPanel.this);
                if (parentWindow instanceof App) {
                    ((App) parentWindow).updateWelcomeTitle(storeTitle);
                }

                DashboardPanel.refreshDashboardData();

                showCustomDialog("Store information saved successfully!", "Information");
            }
        });

        fieldsPanel.add(btnSave, gbc);

        formCard.add(fieldsPanel, BorderLayout.CENTER);

        formCard.setPreferredSize(new Dimension(850, formCard.getPreferredSize().height));

        container.add(formCard);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        GridBagConstraints wrapGbc = new GridBagConstraints();
        wrapGbc.gridx = 0;
        wrapGbc.gridy = 0;
        wrapGbc.weightx = 1.0;
        wrapGbc.weighty = 1.0;
        wrapGbc.anchor = GridBagConstraints.NORTH;   
        wrapGbc.fill = GridBagConstraints.NONE;
        centerWrapper.add(container, wrapGbc);

        JScrollPane mainScroll = new JScrollPane(centerWrapper);
        mainScroll.setBorder(null);
        mainScroll.setBackground(new Color(240, 242, 245));
        mainScroll.getViewport().setBackground(new Color(240, 242, 245));
        add(mainScroll, BorderLayout.CENTER);
    }

    private void addFocusHighlight(JComponent focusSource, JComponent borderTarget, Border normal, Border focused) {
        focusSource.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                borderTarget.setBorder(focused);
            }

            @Override
            public void focusLost(FocusEvent e) {
                borderTarget.setBorder(normal);
            }
        });
    }

    private void showCustomDialog(String message, String title) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 25));
        centerPanel.setBackground(Color.WHITE);

        JLabel lblMsg = new JLabel("<html><font color='" + (title.equals("Error") ? "#C0392B" : "#0D9488") + "'><b>" + title + ":</b></font> " + message + "</html>");
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMsg.setForeground(new Color(70, 75, 80));
        centerPanel.add(lblMsg);

        dialog.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(new Color(248, 249, 250));
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 230)));

        JButton btnOk = new JButton("OK");
        btnOk.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnOk.setBackground(DIALOG_TEAL);
        btnOk.setForeground(Color.WHITE);
        btnOk.setFocusPainted(false);
        btnOk.setBorder(BorderFactory.createEmptyBorder(7, 22, 7, 22));
        btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOk.addActionListener(e -> dialog.dispose());

        bottomPanel.add(btnOk);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setSize(Math.max(dialog.getWidth() + 80, 520), Math.max(dialog.getHeight() + 40, 150));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addLabel(JPanel panel, GridBagConstraints gbc, String text, boolean isRequired) {
        gbc.gridx = 0; gbc.gridy++;
        gbc.weightx = 0.0;
        JPanel lblPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        lblPanel.setOpaque(false);

        JLabel label = new JLabel(text + " ");
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_DARK);
        lblPanel.add(label);

        if (isRequired) {
            JLabel lblStar = new JLabel("*");
            lblStar.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblStar.setForeground(Color.RED);
            lblPanel.add(lblStar);
        }
        panel.add(lblPanel, gbc);
    }

    private JTextField addTextFieldWithHelper(JPanel panel, GridBagConstraints gbc, String defaultValue, String helperText) {
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
        return textField;
    }

    private JTextField addStandardField(JPanel panel, GridBagConstraints gbc, String defaultValue) {
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField textField = createStyledTextField(defaultValue);
        panel.add(textField, gbc);
        return textField;
    }

    private JTextField createStyledTextField(String defaultValue) {
        JTextField textField = new JTextField(defaultValue);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textField.setPreferredSize(new Dimension(350, 32));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        final Border normal = BorderFactory.createCompoundBorder(grayBorder, BorderFactory.createEmptyBorder(0, 8, 0, 8));
        final Border focused = BorderFactory.createCompoundBorder(tealBorder, BorderFactory.createEmptyBorder(0, 8, 0, 8));
        textField.setBorder(normal);
        textField.setSelectionColor(TEAL_LIGHT);
        textField.setSelectedTextColor(TEAL_DARK);
        textField.setCaretColor(TEAL);
        addFocusHighlight(textField, textField, normal, focused);
        return textField;
    }

    private JComboBox<String> addEditableDropdownField(JPanel panel, GridBagConstraints gbc, String labelText, boolean isRequired, String[] items) {
        addLabel(panel, gbc, labelText, isRequired);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setPreferredSize(new Dimension(350, 32));
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        comboBox.setEditable(false);
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(grayBorder);
        comboBox.setFocusable(true);
        addFocusHighlight(comboBox, comboBox, grayBorder, tealBorder);

        comboBox.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected javax.swing.plaf.basic.BasicComboPopup createPopup() {
                javax.swing.plaf.basic.BasicComboPopup popup = new javax.swing.plaf.basic.BasicComboPopup(comboBox);
                popup.setBorder(tealBorder);
                return popup;
            }

            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        g2d.setColor(TEAL_TINT);
                        g2d.fillRect(0, 0, getWidth(), getHeight());

                        g2d.setColor(new Color(200, 205, 210));
                        g2d.drawLine(0, 0, 0, getHeight());

                        g2d.setColor(TEAL);
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
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(228, 232, 236)),
                        BorderFactory.createEmptyBorder(7, 10, 7, 10)
                ));

                if (isSelected) {
                    renderer.setBackground(TEAL_LIGHT);
                    renderer.setForeground(TEAL_DARK);
                } else {
                    renderer.setBackground(Color.WHITE);
                    renderer.setForeground(TEXT_DARK);
                }
                return renderer;
            }
        });

        panel.add(comboBox, gbc);
        return comboBox;
    }
}