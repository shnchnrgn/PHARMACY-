package ui;

import db.MedicineDAO;
import db.SalesDAO;
import models.Medicine;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.time.LocalDate;

public class AddMedicinePanel extends JPanel {

    private static final Color PAGE_BG = new Color(240, 242, 245);
    private static final Color SURFACE = Color.WHITE;
    private static final Color BORDER_LIGHT = new Color(235, 238, 242);
    private static final Color BORDER = BORDER_LIGHT;
    private static final Color TEXT = new Color(60, 65, 70);
    private static final Color MUTED = new Color(108, 117, 125);
    private static final Color PRIMARY = new Color(13, 148, 136);
    private static final Color PRIMARY_HOVER = new Color(15, 118, 110);
    private static final Color DANGER = new Color(192, 57, 43);
    private static final Font TITLE = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font SUBTITLE = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font BODY = new Font("Segoe UI", Font.PLAIN, 12);

    private static final Border grayBorder =

            BorderFactory.createLineBorder(new Color(200, 205, 210), 1);

    private JTextField txtName;
    private JComboBox<String> cmbCategory;
    private JTextField txtBuyPrice;
    private JTextField txtSellPrice;
    private JTextField txtQuantity;
    private JTextField txtCompany;
    private JTextField txtExpireDate;

    public AddMedicinePanel() {

        setLayout(new BorderLayout());

        setBackground(PAGE_BG);

        JPanel container = new JPanel();

        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        container.setBorder(

                BorderFactory.createEmptyBorder(22, 32, 22, 32)

        );

        container.setOpaque(false);

        JPanel formCard = new JPanel(new BorderLayout());

        formCard.setBackground(SURFACE);

        formCard.setBorder(

                BorderFactory.createLineBorder(

                        BORDER

                )

        );

        formCard.setMaximumSize(new Dimension(900, 680));

        JPanel headerPanel =

                new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));

        headerPanel.setBackground(Color.WHITE);

        headerPanel.setBorder(

                BorderFactory.createMatteBorder(

                        0, 0, 1, 0,

                        BORDER

                )

        );

        JLabel lblHeader =

                new JLabel("Add New Medicine");

        lblHeader.setFont(

                TITLE

        );

        lblHeader.setForeground(

                TEXT

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

                new Insets(7, 10, 7, 10);

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

                "Ointment",

                "Drops",

                "Supplements / Vitamins"

        };

        cmbCategory =

                createStyledDropdown(categories, 350);

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

        // --- EXPIRATION DATE SELECTOR (THEME-MATCHED) ---

        JPanel datePanel = new JPanel(new BorderLayout(5, 0));

        datePanel.setOpaque(false);

        datePanel.setPreferredSize(new Dimension(420, 36));

        datePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        txtExpireDate = createStyledTextField("");

        txtExpireDate.setEditable(false);

        txtExpireDate.setBackground(Color.WHITE);

        JButton btnCalendar = new JButton("...");

        btnCalendar.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnCalendar.setBackground(Color.WHITE);

        btnCalendar.setForeground(TEXT);

        btnCalendar.setFocusPainted(false);

        btnCalendar.setBorder(grayBorder);

        btnCalendar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnCalendar.setPreferredSize(new Dimension(42, 36));

        btnCalendar.addActionListener(e -> {

            CustomDropdownDateDialog dateDialog = new CustomDropdownDateDialog(SwingUtilities.getWindowAncestor(this));

            dateDialog.setVisible(true);

            if (dateDialog.getSelectedDate() != null) {

                txtExpireDate.setText(dateDialog.getSelectedDate().toString());

            }

        });

        datePanel.add(txtExpireDate, BorderLayout.CENTER);

        datePanel.add(btnCalendar, BorderLayout.EAST);

        addFieldRow(

                fieldsPanel,

                gbc,

                "Expire Date",

                true,

                datePanel

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

                new Color(26, 143, 136)

        );

        btnSave.setForeground(Color.WHITE);

        btnSave.setFont(

                new Font(

                        "Segoe UI",

                        Font.BOLD,

                        12

                )

        );

        btnSave.setFocusPainted(false);

        btnSave.setBorder(

                BorderFactory.createEmptyBorder(

                        8, 18, 8, 18

                )

        );

        btnSave.setCursor(

                new Cursor(Cursor.HAND_CURSOR)

        );

        btnPanel.add(btnSave);

        JButton btnClear =

                new JButton("Clear Form");

        btnClear.setBackground(

                new Color(192, 57, 43)

        );

        btnClear.setForeground(TEXT);

        btnClear.setFont(

                new Font(

                        "Segoe UI",

                        Font.BOLD,

                        12

                )

        );

        btnClear.setFocusPainted(false);

        btnClear.setBorder(

                BorderFactory.createEmptyBorder(

                        8, 18, 8, 18

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

                String expireDateStr = txtExpireDate.getText().trim();

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

                    CustomDialog.showCustomMessage(

                            this,

                            "Please fill up all the required fields.",

                            "Warning",

                            true

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

                    CustomDialog.showCustomMessage(

                            this,

                            "Buy Price must be greater than 0.",

                            "Invalid Buy Price",

                            true

                    );

                    return;

                }

                if (sellPrice <= 0) {

                    CustomDialog.showCustomMessage(

                            this,

                            "Sell Price must be greater than 0.",

                            "Invalid Sell Price",

                            true

                    );

                    return;

                }

                if (stock <= 0) {

                    CustomDialog.showCustomMessage(

                            this,

                            "Quantity must be greater than 0.",

                            "Invalid Quantity",

                            true

                    );

                    return;

                }

                double totalPurchaseCost =

                        buyPrice * stock;

                boolean confirmed = CustomDialog.showCustomMessage(

                        this,

                        "<b>Please review the medicine details:</b><br><br>"

                                + "<b>Medicine Name:</b> " + name + "<br>"

                                + "<b>Category:</b> " + category + "<br>"

                                + "<b>Buy Price:</b> ₱" + String.format("%.2f", buyPrice) + "<br>"

                                + "<b>Sell Price:</b> ₱" + String.format("%.2f", sellPrice) + "<br>"

                                + "<b>Quantity:</b> " + stock + "<br>"

                                + "<b>Company Name:</b> " + company + "<br>"

                                + "<b>Expire Date:</b> " + expireDateStr + "<br>"

                                + "<b>Total Purchase Cost:</b> ₱" + String.format("%.2f", totalPurchaseCost) + "<br><br>"

                                + "Do you want to save this medicine?",

                        "Confirm Save",

                        false

                );

                if (confirmed) {

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

                    CustomDialog.showCustomMessage(

                            this,

                            "Successfully added new medicine!<br><br>The purchase has been recorded in Expenses.",

                            "Success",

                            false

                    );

                    clearForm();

                }

            } catch (NumberFormatException ex) {

                CustomDialog.showCustomMessage(

                        this,

                        "Please enter valid numbers for Buy Price, Sell Price, and Quantity.",

                        "Invalid Input",

                        true

                );

            } catch (Exception ex) {

                ex.printStackTrace();

                CustomDialog.showCustomMessage(

                        this,

                        "Error in saving.<br><br>" + ex.getMessage(),

                        "Database Error",

                        true

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

        txtExpireDate.setText("");

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

                MUTED

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

            JComboBox<?> comboBox) {

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

                new Dimension(420, 36)

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

                BORDER_LIGHT

        );

        textField.setSelectedTextColor(

                Color.BLACK

        );

        return textField;

    }

    private <T> JComboBox<T> createStyledDropdown(

            T[] items, int width) {

        JComboBox<T> comboBox =

                new JComboBox<>(items);

        comboBox.setFont(

                new Font(

                        "Segoe UI",

                        Font.PLAIN,

                        12

                )

        );

        comboBox.setPreferredSize(

                new Dimension(width, 30)

        );

        comboBox.setMaximumSize(

                new Dimension(

                        Integer.MAX_VALUE,

                        30

                )

        );

        comboBox.setBackground(Color.WHITE);

        comboBox.setForeground(new Color(70, 75, 80));

        comboBox.setBorder(grayBorder);

        comboBox.setUI(

                new javax.swing.plaf.basic.BasicComboBoxUI() {

                    @Override

                    protected javax.swing.plaf.basic.BasicComboPopup createPopup() {

                        javax.swing.plaf.basic.BasicComboPopup popup =

                                new javax.swing.plaf.basic.BasicComboPopup(

                                        comboBox

                                );

                        popup.setBorder(grayBorder);

                        popup.setBackground(Color.WHITE);

                        for (Component c : popup.getComponents()) {

                            if (c instanceof JScrollPane) {

                                JScrollPane scrollPane = (JScrollPane) c;

                                scrollPane.setBackground(Color.WHITE);

                                scrollPane.getViewport().setBackground(Color.WHITE);

                                scrollPane.setBorder(null);

                                JScrollBar verticalBar = scrollPane.getVerticalScrollBar();

                                verticalBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {

                                    @Override

                                    protected void configureScrollBarColors() {

                                        this.thumbColor = new Color(200, 205, 210);

                                        this.trackColor = Color.WHITE;

                                    }

                                    @Override

                                    protected JButton createDecreaseButton(int orientation) {

                                        return createZeroButton();

                                    }

                                    @Override

                                    protected JButton createIncreaseButton(int orientation) {

                                        return createZeroButton();

                                    }

                                    private JButton createZeroButton() {

                                        JButton btn = new JButton();

                                        btn.setPreferredSize(new Dimension(0, 0));

                                        btn.setMinimumSize(new Dimension(0, 0));

                                        btn.setMaximumSize(new Dimension(0, 0));

                                        return btn;

                                    }

                                });

                            }

                        }

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

                        if (isSelected) {

                            renderer.setBackground(new Color(210, 215, 220));

                            renderer.setForeground(Color.BLACK);

                        } else {

                            renderer.setBackground(Color.WHITE);

                            renderer.setForeground(Color.BLACK);

                        }

                        renderer.setBorder(

                                BorderFactory.createEmptyBorder(

                                        6,

                                        10,

                                        6,

                                        10

                                )

                        );

                        return renderer;

                    }

                }

        );

        return comboBox;

    }

    // =====================================================================

    // CUSTOM DROPDOWN DATE DIALOG (THEME-MATCHED)

    // =====================================================================

    private static class CustomDropdownDateDialog extends JDialog {

        private LocalDate selectedDate = null;
        private JComboBox<String> cmbMonth;
        private JComboBox<Integer> cmbDay;
        private JComboBox<Integer> cmbYear;

        public CustomDropdownDateDialog(Window owner) {

            super(owner, "Select Expiry Date", Dialog.ModalityType.APPLICATION_MODAL);

            setLayout(new BorderLayout());

            setSize(390, 190);

            setResizable(false);

            setLocationRelativeTo(owner);

            getContentPane().setBackground(Color.WHITE);

            // Header Title

            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));

            headerPanel.setBackground(Color.WHITE);

            headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT));

            JLabel lblTitle = new JLabel("Select Expiry Date");

            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));

            lblTitle.setForeground(new Color(70, 75, 80));

            headerPanel.add(lblTitle);

            add(headerPanel, BorderLayout.NORTH);

            // Center Dropdowns Panel

            JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 20));

            centerPanel.setBackground(Color.WHITE);

            String[] months = {

                "January", "February", "March", "April", "May", "June",

                "July", "August", "September", "October", "November", "December"

            };

            AddMedicinePanel dummyPanel = new AddMedicinePanel();

            cmbMonth = dummyPanel.createStyledDropdown(months, 110);

            Integer[] days = new Integer[31];

            for (int i = 1; i <= 31; i++) days[i - 1] = i;

            cmbDay = dummyPanel.createStyledDropdown(days, 70);

            int currentYear = LocalDate.now().getYear();

            Integer[] years = new Integer[20];

            for (int i = 0; i < 20; i++) years[i] = currentYear + i;

            cmbYear = dummyPanel.createStyledDropdown(years, 90);

            // Set default to today

            LocalDate now = LocalDate.now();

            cmbMonth.setSelectedIndex(now.getMonthValue() - 1);

            cmbDay.setSelectedItem(now.getDayOfMonth());

            cmbYear.setSelectedItem(now.getYear());

            centerPanel.add(cmbMonth);

            centerPanel.add(cmbDay);

            centerPanel.add(cmbYear);

            add(centerPanel, BorderLayout.CENTER);

            // Footer Buttons

            JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));

            footerPanel.setBackground(Color.WHITE);

            footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_LIGHT));

            JButton btnSelect = new JButton("Select");

            btnSelect.setFont(new Font("Segoe UI", Font.BOLD, 12));

            btnSelect.setBackground(new Color(26, 143, 136));

            btnSelect.setForeground(Color.WHITE);

            btnSelect.setFocusPainted(false);

            btnSelect.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));

            btnSelect.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnSelect.addActionListener(e -> {

                int month = cmbMonth.getSelectedIndex() + 1;

                int day = (Integer) cmbDay.getSelectedItem();

                int year = (Integer) cmbYear.getSelectedItem();

                try {

                    selectedDate = LocalDate.of(year, month, day);

                    dispose();

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(this, "Invalid date combination (e.g. Feb 30). Please check.", "Error", JOptionPane.ERROR_MESSAGE);

                }

            });

            JButton btnCancel = new JButton("Cancel");

            btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 12));

            btnCancel.setBackground(new Color(192, 57, 43));

            btnCancel.setForeground(Color.WHITE);

            btnCancel.setFocusPainted(false);

            btnCancel.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));

            btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnCancel.addActionListener(e -> dispose());

            footerPanel.add(btnSelect);

            footerPanel.add(btnCancel);

            add(footerPanel, BorderLayout.SOUTH);

        }

        public LocalDate getSelectedDate() {

            return selectedDate;

        }

    }

    private static class CustomDialog {

        private static boolean userConfirmed = false;

        public static boolean showCustomMessage(Component parent, String message, String title, boolean isWarning) {

            userConfirmed = false;

            Window owner = SwingUtilities.getWindowAncestor(parent);

            JDialog dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);

            dialog.setLayout(new BorderLayout());

            dialog.setResizable(false);

            JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));

            centerPanel.setBackground(Color.WHITE);

            String colorHex = isWarning ? "#C0392B" : "#261436";

            JLabel lblMsg = new JLabel("<html><font color='" + colorHex + "'><b>" + title + ":</b></font><br><br>" + message + "</html>");

            lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            lblMsg.setForeground(new Color(70, 75, 80));

            centerPanel.add(lblMsg);

            JScrollPane scrollPane = new JScrollPane(centerPanel);

            scrollPane.setBorder(null);

            scrollPane.getViewport().setBackground(Color.WHITE);

            dialog.add(scrollPane, BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));

            bottomPanel.setBackground(Color.WHITE);

            bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_LIGHT));

            JButton btnOk = new JButton("OK");

            btnOk.setFont(new Font("Segoe UI", Font.BOLD, 12));

            btnOk.setBackground(new Color(26, 143, 136));

            btnOk.setForeground(Color.WHITE);

            btnOk.setFocusPainted(false);

            btnOk.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));

            btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnOk.addActionListener(e -> {

                userConfirmed = true;

                dialog.dispose();

            });

            JButton btnCancel = new JButton("Cancel");

            btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 12));

            btnCancel.setBackground(new Color(192, 57, 43));

            btnCancel.setForeground(Color.WHITE);

            btnCancel.setFocusPainted(false);

            btnCancel.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));

            btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnCancel.addActionListener(e -> {

                userConfirmed = false;

                dialog.dispose();

            });

            bottomPanel.add(btnOk);

            bottomPanel.add(btnCancel);

            dialog.add(bottomPanel, BorderLayout.SOUTH);

            dialog.pack();

            dialog.setSize(Math.max(dialog.getWidth() + 100, 520), Math.min(Math.max(dialog.getHeight() + 60, 220), 450));

            dialog.setLocationRelativeTo(parent);

            dialog.setVisible(true);

            return userConfirmed;

        }

    }

}
