package ui;

import db.MedicineDAO;
import models.Medicine;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class EditMedicineDialog extends JDialog {

    private final Border grayBorder = BorderFactory.createLineBorder(new Color(200, 205, 210), 1);

    private JTextField txtName;
    private JComboBox<String> cmbCategory;
    private JTextField txtBuyPrice;
    private JTextField txtSellPrice;
    private JTextField txtQuantity;
    private JTextField txtCompany;
    private JTextField txtExpireDate;

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

        String medName = (med != null) ? med.getName() : "";
        txtName = createStyledTextField(medName);
        addFieldRow(fieldsPanel, gbc, "Medicine Name", true, txtName);

        String[] categories = {"-- Select Category --", "Tablet", "Capsule", "Syrup", "Injection", "Ointment", "Drops", "Supplements / Vitamins"};
        cmbCategory = createStyledDropdown(categories, 350);
        if (med != null && med.getMedicineCategory() != null) {
            cmbCategory.setSelectedItem(med.getMedicineCategory());
        }
        addDropdownRow(fieldsPanel, gbc, "Medicine Category", true, cmbCategory);

        String buyPriceStr = (med != null) ? String.valueOf(med.getBuyPrice()) : "";
        txtBuyPrice = createStyledTextField(buyPriceStr);
        addFieldRow(fieldsPanel, gbc, "Buy Price", true, txtBuyPrice);

        String sellPriceStr = (med != null) ? String.valueOf(med.getSellPrice()) : "";
        txtSellPrice = createStyledTextField(sellPriceStr);
        addFieldRow(fieldsPanel, gbc, "Sell Price", true, txtSellPrice);

        String stockStr = (med != null) ? String.valueOf(med.getStock()) : "";
        txtQuantity = createStyledTextField(stockStr);
        addFieldRow(fieldsPanel, gbc, "Quantity", true, txtQuantity);

        String companyStr = (med != null && med.getCompanyName() != null) ? med.getCompanyName() : "";
        txtCompany = createStyledTextField(companyStr);
        addFieldRow(fieldsPanel, gbc, "Company Name", true, txtCompany);

        // --- EXPIRATION DATE SELECTOR (THEME-MATCHED) ---
        JPanel datePanel = new JPanel(new BorderLayout(5, 0));
        datePanel.setOpaque(false);
        datePanel.setPreferredSize(new Dimension(350, 30));
        datePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        String expiryStr = (med != null && med.getExpiryDate() != null) ? med.getExpiryDate() : "";
        txtExpireDate = createStyledTextField(expiryStr);
        txtExpireDate.setEditable(false);
        txtExpireDate.setBackground(Color.WHITE);

        JButton btnCalendar = new JButton("...");
        btnCalendar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCalendar.setBackground(new Color(240, 242, 245));
        btnCalendar.setForeground(new Color(70, 75, 80));
        btnCalendar.setFocusPainted(false);
        btnCalendar.setBorder(grayBorder);
        btnCalendar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCalendar.setPreferredSize(new Dimension(40, 30));

        btnCalendar.addActionListener(e -> {
            // Ipinasa ang 'this' dialog bilang parent para lumitaw nang sigurado ang dialog
            CustomDropdownDateDialog dateDialog = new CustomDropdownDateDialog(this, txtExpireDate.getText().trim());
            dateDialog.setVisible(true);
            if (dateDialog.getSelectedDate() != null) {
                txtExpireDate.setText(dateDialog.getSelectedDate().toString());
            }
        });

        datePanel.add(txtExpireDate, BorderLayout.CENTER);
        datePanel.add(btnCalendar, BorderLayout.EAST);

        addFieldRow(fieldsPanel, gbc, "Expire Date", true, datePanel);

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
                String expireDateStr = txtExpireDate.getText().trim();

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

    private <T> JComboBox<T> createStyledDropdown(T[] items, int width) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboBox.setPreferredSize(new Dimension(width, 30));
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(new Color(70, 75, 80));
        comboBox.setBorder(grayBorder);

        comboBox.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected javax.swing.plaf.basic.BasicComboPopup createPopup() {
                javax.swing.plaf.basic.BasicComboPopup popup = new javax.swing.plaf.basic.BasicComboPopup(comboBox);
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
                renderer.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                return renderer;
            }
        });

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

        public CustomDropdownDateDialog(Dialog owner, String initialDateStr) {
            super(owner, "Select Expiry Date", Dialog.ModalityType.APPLICATION_MODAL);
            setLayout(new BorderLayout());
            setSize(390, 190);
            setResizable(false);
            setLocationRelativeTo(owner);
            getContentPane().setBackground(Color.WHITE);

            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
            headerPanel.setBackground(new Color(248, 249, 250));
            headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 230)));
            JLabel lblTitle = new JLabel("Select Expiry Date");
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblTitle.setForeground(new Color(70, 75, 80));
            headerPanel.add(lblTitle);
            add(headerPanel, BorderLayout.NORTH);

            JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 20));
            centerPanel.setBackground(Color.WHITE);

            String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
            };
            
            EditMedicineDialog dummyPanel = new EditMedicineDialog(null, null);
            cmbMonth = dummyPanel.createStyledDropdown(months, 110);
            
            Integer[] days = new Integer[31];
            for (int i = 1; i <= 31; i++) days[i - 1] = i;
            cmbDay = dummyPanel.createStyledDropdown(days, 70);

            int currentYear = LocalDate.now().getYear();
            Integer[] years = new Integer[20];
            for (int i = 0; i < 20; i++) years[i] = currentYear + i;
            cmbYear = dummyPanel.createStyledDropdown(years, 90);

            // Parse initial date kung mayroon man, kung wala gagamitin ang ngayon
            LocalDate targetDate = LocalDate.now();
            try {
                if (initialDateStr != null && !initialDateStr.isEmpty()) {
                    targetDate = LocalDate.parse(initialDateStr.trim());
                }
            } catch (DateTimeParseException ignored) {}

            cmbMonth.setSelectedIndex(targetDate.getMonthValue() - 1);
            cmbDay.setSelectedItem(targetDate.getDayOfMonth());
            cmbYear.setSelectedItem(targetDate.getYear());

            centerPanel.add(cmbMonth);
            centerPanel.add(cmbDay);
            centerPanel.add(cmbYear);
            add(centerPanel, BorderLayout.CENTER);

            JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
            footerPanel.setBackground(new Color(248, 249, 250));
            footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 230)));

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
}