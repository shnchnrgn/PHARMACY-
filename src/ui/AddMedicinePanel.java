package ui;
import db.MedicineDAO;
import db.SalesDAO;
import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.border.Border;
import models.Medicine;

public class AddMedicinePanel extends JPanel {
    private static final Color PAGE_BG = new Color(245, 247, 248);
    private static final Color SURFACE = Color.WHITE;
    private static final Color TEAL = new Color(26, 143, 136);
    private static final Color TEAL_DARK = new Color(20, 117, 111);
    private static final Color TEAL_LIGHT = new Color(232, 247, 245);
    private static final Color BORDER = new Color(218, 224, 227);
    private static final Color TEXT = new Color(45, 52, 55);
    private static final Color MUTED = new Color(105, 115, 120);
    private static final Color DANGER = new Color(192, 57, 43);
    private static final Font TITLE = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font LABEL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FIELD = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Border FIELD_BORDER = BorderFactory.createLineBorder(BORDER, 1);
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
        container.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        container.setOpaque(false);
        JPanel formCard = new JPanel(new BorderLayout());
        formCard.setBackground(SURFACE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(0, 0, 5, 0)
        ));
        formCard.setMaximumSize(new Dimension(950, 720));
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(SURFACE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(16, 22, 16, 22)
        ));
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        JLabel accent = new JLabel();
        accent.setOpaque(true);
        accent.setBackground(TEAL);
        accent.setPreferredSize(new Dimension(5, 25));
        JLabel lblHeader = new JLabel("Add New Medicine");
        lblHeader.setFont(TITLE);
        lblHeader.setForeground(TEXT);
        lblHeader.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        titlePanel.add(accent);
        titlePanel.add(lblHeader);
        headerPanel.add(titlePanel, BorderLayout.WEST);
        JLabel lblSubtitle = new JLabel("Enter the medicine information below");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(MUTED);
        headerPanel.add(lblSubtitle, BorderLayout.EAST);
        formCard.add(headerPanel, BorderLayout.NORTH);
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(SURFACE);
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 8, 7, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;
        txtName = createStyledTextField();
        addFieldRow(fieldsPanel, gbc, "Medicine Name", true, txtName);
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
        cmbCategory = createStyledDropdown(categories, 420);
        addFieldRow(fieldsPanel, gbc, "Medicine Category", true, cmbCategory);
        txtBuyPrice = createStyledTextField();
        addFieldRow(fieldsPanel, gbc, "Buy Price", true, txtBuyPrice);
        txtSellPrice = createStyledTextField();
        addFieldRow(fieldsPanel, gbc, "Sell Price", true, txtSellPrice);
        txtQuantity = createStyledTextField();
        addFieldRow(fieldsPanel, gbc, "Quantity", true, txtQuantity);
        txtCompany = createStyledTextField();
        addFieldRow(fieldsPanel, gbc, "Company Name", true, txtCompany);
        JPanel datePanel = new JPanel(new BorderLayout(6, 0));
        datePanel.setOpaque(false);
        txtExpireDate = createStyledTextField();
        txtExpireDate.setEditable(false);
        txtExpireDate.setBackground(Color.WHITE);
        JButton btnCalendar = createSmallButton("...");
        btnCalendar.addActionListener(e -> openDatePicker());
        datePanel.add(txtExpireDate, BorderLayout.CENTER);
        datePanel.add(btnCalendar, BorderLayout.EAST);
        addFieldRow(fieldsPanel, gbc, "Expire Date", true, datePanel);
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(18, 8, 5, 8);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);
        JButton btnSave = createPrimaryButton("Save Medicine");
        JButton btnClear = createClearButton("Clear Form");
        buttonPanel.add(btnSave);
        buttonPanel.add(btnClear);
        fieldsPanel.add(buttonPanel, gbc);
        formCard.add(fieldsPanel, BorderLayout.CENTER);
        container.add(formCard);
        JScrollPane mainScroll = new JScrollPane(container);
        mainScroll.setBorder(null);
        mainScroll.setBackground(PAGE_BG);
        mainScroll.getViewport().setBackground(PAGE_BG);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);
        btnSave.addActionListener(e -> saveMedicine());
        btnClear.addActionListener(e -> clearForm());
    }

    private void saveMedicine() {
        try {
            String name = txtName.getText().trim();
            String category = cmbCategory.getSelectedItem() != null
                    ? cmbCategory.getSelectedItem().toString().trim()
                    : "";
            String buyPriceStr = txtBuyPrice.getText().trim();
            String sellPriceStr = txtSellPrice.getText().trim();
            String quantityStr = txtQuantity.getText().trim();
            String company = txtCompany.getText().trim();
            String expireDateStr = txtExpireDate.getText().trim();
            if (name.isEmpty()
                    || category.isEmpty()
                    || category.equals("-- Select Category --")
                    || buyPriceStr.isEmpty()
                    || sellPriceStr.isEmpty()
                    || quantityStr.isEmpty()
                    || company.isEmpty()
                    || expireDateStr.isEmpty()) {
                CustomDialog.showMessage(
                        this,
                        "Please fill up all the required fields.",
                        "Incomplete Information",
                        true
                );
                return;
            }
            double buyPrice = Double.parseDouble(buyPriceStr);
            double sellPrice = Double.parseDouble(sellPriceStr);
            int stock = Integer.parseInt(quantityStr);
            if (buyPrice <= 0) {
                CustomDialog.showMessage(
                        this,
                        "Buy Price must be greater than 0.",
                        "Invalid Buy Price",
                        true
                );
                txtBuyPrice.requestFocus();
                return;
            }
            if (sellPrice <= 0) {
                CustomDialog.showMessage(
                        this,
                        "Sell Price must be greater than 0.",
                        "Invalid Sell Price",
                        true
                );
                txtSellPrice.requestFocus();
                return;
            }
            if (stock <= 0) {
                CustomDialog.showMessage(
                        this,
                        "Quantity must be greater than 0.",
                        "Invalid Quantity",
                        true
                );
                txtQuantity.requestFocus();
                return;
            }
            double totalPurchaseCost = buyPrice * stock;
            String message = "<b>Please review the medicine details:</b><br><br>"
                    + "<b>Medicine Name:</b> " + name + "<br>"
                    + "<b>Category:</b> " + category + "<br>"
                    + "<b>Buy Price:</b> ₱" + String.format("%.2f", buyPrice) + "<br>"
                    + "<b>Sell Price:</b> ₱" + String.format("%.2f", sellPrice) + "<br>"
                    + "<b>Quantity:</b> " + stock + "<br>"
                    + "<b>Company Name:</b> " + company + "<br>"
                    + "<b>Expire Date:</b> " + expireDateStr + "<br>"
                    + "<b>Total Purchase Cost:</b> ₱" + String.format("%.2f", totalPurchaseCost)
                    + "<br><br>Do you want to save this medicine?";
            boolean confirmed = CustomDialog.showConfirmation(
                    this,
                    message,
                    "Confirm Save"
            );
            if (!confirmed) {
                return;
            }
            Medicine newMed = new Medicine();
            newMed.setName(name);
            newMed.setMedicineCategory(category);
            newMed.setBuyPrice(buyPrice);
            newMed.setSellPrice(sellPrice);
            newMed.setStock(stock);
            newMed.setCompanyName(company);
            newMed.setExpiryDate(expireDateStr);
            MedicineDAO.addMedicine(newMed);
            SalesDAO.addExpense(
                    "Purchase of " + stock + "x " + name,
                    totalPurchaseCost,
                    LocalDate.now().toString()
            );
            CustomDialog.showMessage(
                    this,
                    "Successfully added new medicine!<br><br>"
                            + "The purchase has been recorded in Expenses.",
                    "Medicine Added",
                    false
            );
            clearForm();
        } catch (NumberFormatException ex) {
            CustomDialog.showMessage(
                    this,
                    "Please enter valid numbers for Buy Price, Sell Price, and Quantity.",
                    "Invalid Input",
                    true
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            CustomDialog.showMessage(
                    this,
                    "Error in saving.<br><br>" + ex.getMessage(),
                    "Database Error",
                    true
            );
        }
    }

    private void openDatePicker() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        CustomDropdownDateDialog dialog = new CustomDropdownDateDialog(owner);
        dialog.setVisible(true);
        if (dialog.getSelectedDate() != null) {
            txtExpireDate.setText(dialog.getSelectedDate().toString());
        }
    }

    private void clearForm() {
        txtName.setText("");
        cmbCategory.setSelectedIndex(0);
        txtBuyPrice.setText("");
        txtSellPrice.setText("");
        txtQuantity.setText("");
        txtCompany.setText("");
        txtExpireDate.setText("");
        txtName.requestFocus();
    }

    private void addFieldRow(
            JPanel panel,
            GridBagConstraints gbc,
            String labelText,
            boolean required,
            JComponent field) {
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelPanel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL);
        label.setForeground(TEXT);
        labelPanel.add(label);
        if (required) {
            JLabel star = new JLabel(" *");
            star.setFont(new Font("Segoe UI", Font.BOLD, 13));
            star.setForeground(DANGER);
            labelPanel.add(star);
        }
        panel.add(labelPanel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
        gbc.gridy++;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(FIELD);
        field.setForeground(TEXT);
        field.setBackground(Color.WHITE);
        field.setPreferredSize(new Dimension(420, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                FIELD_BORDER,
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        field.setCaretColor(TEAL);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(TEAL, 1),
                        BorderFactory.createEmptyBorder(0, 10, 0, 10)
                ));
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        FIELD_BORDER,
                        BorderFactory.createEmptyBorder(0, 10, 0, 10)
                ));
            }
        });
        return field;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(TEAL);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(TEAL_DARK);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(TEAL);
            }
        });
        return button;
    }

    private JButton createClearButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(Color.WHITE);
        button.setForeground(TEXT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(9, 19, 9, 19)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(245, 247, 248));
                button.setForeground(TEAL_DARK);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(Color.WHITE);
                button.setForeground(TEXT);
            }
        });
        return button;
    }

    private JButton createSmallButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(TEAL);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        button.setPreferredSize(new Dimension(48, 38));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(TEAL_DARK);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(TEAL);
            }
        });
        return button;
    }

    private <T> JComboBox<T> createStyledDropdown(T[] items, int width) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setPreferredSize(new Dimension(width, 38));
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(TEXT);
        comboBox.setBorder(FIELD_BORDER);
        comboBox.setFocusable(false);
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean selected,
                    boolean cellHasFocus) {
                JLabel renderer = (JLabel) super.getListCellRendererComponent(
                        list,
                        value,
                        index,
                        selected,
                        cellHasFocus
                );
                renderer.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                renderer.setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 10));
                if (selected) {
                    renderer.setBackground(TEAL_LIGHT);
                    renderer.setForeground(TEAL_DARK);
                } else {
                    renderer.setBackground(Color.WHITE);
                    renderer.setForeground(TEXT);
                }
                return renderer;
            }
        });
        return comboBox;
    }

    private static class CustomDropdownDateDialog extends JDialog {
        private LocalDate selectedDate;
        private JComboBox<String> cmbMonth;
        private JComboBox<Integer> cmbDay;
        private JComboBox<Integer> cmbYear;

        public CustomDropdownDateDialog(Window owner) {
            super(owner, "Select Expiry Date", Dialog.ModalityType.APPLICATION_MODAL);
            setLayout(new BorderLayout());
            setSize(430, 230);
            setResizable(false);
            setLocationRelativeTo(owner);
            getContentPane().setBackground(Color.WHITE);
            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(Color.WHITE);
            header.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)
            ));
            JLabel title = new JLabel("Select Expiry Date");
            title.setFont(new Font("Segoe UI", Font.BOLD, 15));
            title.setForeground(TEXT);
            header.add(title, BorderLayout.WEST);
            add(header, BorderLayout.NORTH);
            JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 25));
            center.setBackground(Color.WHITE);
            String[] months = {
                    "January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"
            };
            cmbMonth = createDateDropdown(months, 115);
            Integer[] days = new Integer[31];
            for (int i = 1; i <= 31; i++) {
                days[i - 1] = i;
            }
            cmbDay = createDateDropdown(days, 70);
            int currentYear = LocalDate.now().getYear();
            Integer[] years = new Integer[20];
            for (int i = 0; i < 20; i++) {
                years[i] = currentYear + i;
            }
            cmbYear = createDateDropdown(years, 85);
            LocalDate today = LocalDate.now();
            cmbMonth.setSelectedIndex(today.getMonthValue() - 1);
            cmbDay.setSelectedItem(today.getDayOfMonth());
            cmbYear.setSelectedItem(today.getYear());
            center.add(cmbMonth);
            center.add(cmbDay);
            center.add(cmbYear);
            add(center, BorderLayout.CENTER);
            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            footer.setBackground(Color.WHITE);
            footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
            JButton select = new JButton("Select");
            styleDialogButton(select, TEAL, Color.WHITE);
            select.addActionListener(e -> selectDate());
            JButton cancel = new JButton("Cancel");
            styleDialogButton(cancel, Color.WHITE, TEXT);
            cancel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER, 1),
                    BorderFactory.createEmptyBorder(7, 17, 7, 17)
            ));
            cancel.addActionListener(e -> dispose());
            footer.add(select);
            footer.add(cancel);
            add(footer, BorderLayout.SOUTH);
        }

        private void selectDate() {
            int month = cmbMonth.getSelectedIndex() + 1;
            int day = (Integer) cmbDay.getSelectedItem();
            int year = (Integer) cmbYear.getSelectedItem();
            try {
                selectedDate = LocalDate.of(year, month, day);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid date combination. Please select a valid date.",
                        "Invalid Date",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        }

        private <T> JComboBox<T> createDateDropdown(T[] items, int width) {
            JComboBox<T> combo = new JComboBox<>(items);
            combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            combo.setPreferredSize(new Dimension(width, 36));
            combo.setBackground(Color.WHITE);
            combo.setForeground(TEXT);
            combo.setBorder(FIELD_BORDER);
            return combo;
        }

        private void styleDialogButton(
                JButton button,
                Color background,
                Color foreground) {
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setBackground(background);
            button.setForeground(foreground);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        public LocalDate getSelectedDate() {
            return selectedDate;
        }
    }

    private static class CustomDialog {
        private static boolean confirmed;

        public static void showMessage(
                Component parent,
                String message,
                String title,
                boolean warning) {
            showDialog(parent, message, title, warning, false);
        }

        public static boolean showConfirmation(
                Component parent,
                String message,
                String title) {
            confirmed = false;
            showDialog(parent, message, title, false, true);
            return confirmed;
        }

        private static void showDialog(
                Component parent,
                String message,
                String title,
                boolean warning,
                boolean confirmation) {
            Window owner = SwingUtilities.getWindowAncestor(parent);
            JDialog dialog = new JDialog(
                    owner,
                    title,
                    Dialog.ModalityType.APPLICATION_MODAL
            );
            dialog.setLayout(new BorderLayout());
            dialog.setResizable(false);
            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(SURFACE);
            header.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                    BorderFactory.createEmptyBorder(14, 20, 14, 20)
            ));
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
            titleLabel.setForeground(warning ? DANGER : TEAL_DARK);
            header.add(titleLabel, BorderLayout.WEST);
            dialog.add(header, BorderLayout.NORTH);
            JPanel center = new JPanel(new BorderLayout());
            center.setBackground(SURFACE);
            center.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
            JLabel messageLabel = new JLabel("<html>" + message + "</html>");
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            messageLabel.setForeground(TEXT);
            center.add(messageLabel, BorderLayout.CENTER);
            dialog.add(center, BorderLayout.CENTER);
            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            footer.setBackground(SURFACE);
            footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
            JButton ok = new JButton(confirmation ? "Save" : "OK");
            styleButton(ok, TEAL, Color.WHITE);
            ok.addActionListener(e -> {
                confirmed = true;
                dialog.dispose();
            });
            footer.add(ok);
            if (confirmation) {
                JButton cancel = new JButton("Cancel");
                styleButton(cancel, Color.WHITE, TEXT);
                cancel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER, 1),
                        BorderFactory.createEmptyBorder(7, 19, 7, 19)
                ));
                cancel.addActionListener(e -> {
                    confirmed = false;
                    dialog.dispose();
                });
                footer.add(cancel);
            }
            dialog.add(footer, BorderLayout.SOUTH);
            dialog.pack();
            dialog.setMinimumSize(new Dimension(500, 220));
            dialog.setLocationRelativeTo(parent);
            dialog.setVisible(true);
        }

        private static void styleButton(
                JButton button,
                Color background,
                Color foreground) {
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setBackground(background);
            button.setForeground(foreground);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
    }
}
