package ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class MedicineCategoryPanel extends JPanel {

    private final Border grayBorder = BorderFactory.createLineBorder(new Color(200, 205, 210), 1);

    private JTable categoryTable;
    private JTextField txtCategoryName, txtSearch;
    private JComboBox<String> cmbStatus;
    private DefaultTableModel categoryModel;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public MedicineCategoryPanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Medicine Category Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(60, 65, 70));
        add(lblTitle, BorderLayout.NORTH);

        JPanel mainContainer = new JPanel(new BorderLayout(0, 15));
        mainContainer.setOpaque(false);

        // --- TOP FORM PANEL ---
        JPanel topFormPanel = new JPanel(new BorderLayout());
        topFormPanel.setBackground(Color.WHITE);
        topFormPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 215, 220)),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JPanel formTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        formTitlePanel.setOpaque(false);
        formTitlePanel.setBorder(new EmptyBorder(0, 0, 12, 0));
        JLabel lblFormTitle = new JLabel("Add New Medicine Category");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFormTitle.setForeground(new Color(70, 75, 80));
        formTitlePanel.add(lblFormTitle);
        topFormPanel.add(formTitlePanel, BorderLayout.NORTH);

        JPanel formFieldsGrid = new JPanel(new GridBagLayout());
        formFieldsGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;

        JLabel lblCatName = new JLabel("<html>Category Name <font color='red'>*</font></html>");
        lblCatName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCatName.setForeground(new Color(70, 75, 80));

        txtCategoryName = createStyledTextField();
        txtCategoryName.setPreferredSize(new Dimension(220, 30));

        JLabel lblStatus = new JLabel("<html>Status <font color='red'>*</font></html>");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(70, 75, 80));

        cmbStatus = createStyledDropdown(new String[]{"Active", "Inactive"}, 130);

        JButton btnSubmit = new JButton("Submit");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSubmit.setBackground(new Color(26, 143, 136));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSubmit.addActionListener(e -> {
            String catName = txtCategoryName.getText().trim();
            String status = cmbStatus.getSelectedItem() != null ? cmbStatus.getSelectedItem().toString() : "Active";
            
            if (catName.isEmpty()) {
                showCustomDialog("Please enter a category name.", "Error");
            } else {
                categoryModel.addRow(new Object[]{catName, status, "Action"});
                txtCategoryName.setText("");
                cmbStatus.setSelectedIndex(0);
                showCustomDialog("Category added successfully!", "Information");
            }
        });

        gbc.gridx = 0; gbc.gridy = 0;
        formFieldsGrid.add(lblCatName, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        formFieldsGrid.add(txtCategoryName, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        formFieldsGrid.add(lblStatus, gbc);

        gbc.gridx = 3; gbc.gridy = 0;
        formFieldsGrid.add(cmbStatus, gbc);

        gbc.gridx = 4; gbc.gridy = 0;
        gbc.insets = new Insets(5, 15, 5, 5);
        formFieldsGrid.add(btnSubmit, gbc);

        topFormPanel.add(formFieldsGrid, BorderLayout.CENTER);
        mainContainer.add(topFormPanel, BorderLayout.NORTH);

        // --- BOTTOM TABLE LIST PANEL ---
        JPanel bottomTableContainer = new JPanel(new BorderLayout(0, 10));
        bottomTableContainer.setBackground(Color.WHITE);
        bottomTableContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 215, 220)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        tableHeaderPanel.setOpaque(false);

        JLabel lblListTitle = new JLabel("Medicine Category List");
        lblListTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblListTitle.setForeground(new Color(70, 75, 80));
        tableHeaderPanel.add(lblListTitle, BorderLayout.WEST);

        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchBarPanel.setOpaque(false);

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSearch.setForeground(new Color(70, 75, 80));

        txtSearch = createStyledTextField();
        txtSearch.setPreferredSize(new Dimension(180, 28));

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            
            private void filterTable() {
                String text = txtSearch.getText().trim();
                if (text.isEmpty()) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        searchBarPanel.add(lblSearch);
        searchBarPanel.add(txtSearch);
        tableHeaderPanel.add(searchBarPanel, BorderLayout.EAST);

        bottomTableContainer.add(tableHeaderPanel, BorderLayout.NORTH);

        String[] columns = {"Category Name", "Status", "Action"};
        categoryModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };

        categoryModel.addRow(new Object[]{"Tablet", "Active", "Action"});
        categoryModel.addRow(new Object[]{"Syrup", "Active", "Action"});
        categoryModel.addRow(new Object[]{"Capsule", "Active", "Action"});
        categoryModel.addRow(new Object[]{"Injection", "Active", "Action"});

        categoryTable = new JTable(categoryModel);
        
        categoryTable.setSelectionBackground(new Color(210, 215, 220));
        categoryTable.setSelectionForeground(Color.BLACK);
        
        rowSorter = new TableRowSorter<>(categoryModel);
        categoryTable.setRowSorter(rowSorter);

        categoryTable.setRowHeight(38);
        categoryTable.setShowVerticalLines(false);
        categoryTable.setShowHorizontalLines(true);
        categoryTable.setGridColor(new Color(235, 238, 242));
        categoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        categoryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        categoryTable.getTableHeader().setBackground(new Color(248, 249, 250));
        categoryTable.getTableHeader().setForeground(new Color(80, 85, 90));

        categoryTable.getColumnModel().getColumn(1).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
                panel.setOpaque(true);
                panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);

                String status = value != null ? value.toString() : "";
                
                JLabel badge = new JLabel(status, JLabel.CENTER) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(getBackground());
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                        super.paintComponent(g);
                        g2.dispose();
                    }
                };
                
                badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
                badge.setForeground(Color.WHITE);
                badge.setPreferredSize(new Dimension(75, 24));

                if ("Active".equalsIgnoreCase(status)) {
                    badge.setBackground(new Color(26, 143, 136));
                } else {
                    badge.setBackground(new Color(108, 117, 125));
                }
                badge.setOpaque(false);

                panel.add(badge);
                return panel;
            }
        });

        categoryTable.getColumnModel().getColumn(2).setCellRenderer(new ActionButtonRenderer());
        categoryTable.getColumnModel().getColumn(2).setCellEditor(new ActionButtonEditor(new JCheckBox(), categoryTable));

        JScrollPane scrollPane = new JScrollPane(categoryTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));

        bottomTableContainer.add(scrollPane, BorderLayout.CENTER);
        mainContainer.add(bottomTableContainer, BorderLayout.CENTER);

        add(mainContainer, BorderLayout.CENTER);
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setForeground(new Color(70, 75, 80));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 205, 210)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return tf;
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

    private void showCustomDialog(String message, String title) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 25));
        centerPanel.setBackground(Color.WHITE);
        
        JLabel lblMsg = new JLabel("<html><font color='#C0392B'><b>" + title + ":</b></font> " + message + "</html>");
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMsg.setForeground(new Color(70, 75, 80));
        centerPanel.add(lblMsg);
        
        dialog.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(new Color(248, 249, 250));
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 230)));

        JButton btnOk = new JButton("OK");
        btnOk.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnOk.setBackground(new Color(26, 143, 136));
        btnOk.setForeground(Color.WHITE);
        btnOk.setFocusPainted(false);
        btnOk.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
        btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOk.addActionListener(e -> dialog.dispose());

        bottomPanel.add(btnOk);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setSize(Math.max(dialog.getWidth() + 80, 520), Math.max(dialog.getHeight() + 40, 150));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private boolean showCustomConfirmDialog(String message, String title) {
        final boolean[] result = {false};
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 25));
        centerPanel.setBackground(Color.WHITE);
        
        JLabel lblMsg = new JLabel("<html><font color='#C0392B'><b>" + title + ":</b></font> " + message + "</html>");
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMsg.setForeground(new Color(70, 75, 80));
        centerPanel.add(lblMsg);
        
        dialog.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(new Color(248, 249, 250));
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 230)));

        JButton btnYes = new JButton("Yes");
        btnYes.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnYes.setBackground(new Color(26, 143, 136));
        btnYes.setForeground(Color.WHITE);
        btnYes.setFocusPainted(false);
        btnYes.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
        btnYes.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnYes.addActionListener(e -> {
            result[0] = true;
            dialog.dispose();
        });

        JButton btnNo = new JButton("No");
        btnNo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNo.setBackground(new Color(192, 57, 43));
        btnNo.setForeground(Color.WHITE);
        btnNo.setFocusPainted(false);
        btnNo.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
        btnNo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNo.addActionListener(e -> {
            result[0] = false;
            dialog.dispose();
        });

        bottomPanel.add(btnYes);
        bottomPanel.add(btnNo);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setSize(Math.max(dialog.getWidth() + 80, 520), Math.max(dialog.getHeight() + 40, 150));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        return result[0];
    }

    static class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton btnEdit = new JButton("Edit");
        private final JButton btnDelete = new JButton("Delete");

        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.LEFT, 5, 4));
            setOpaque(true);
            
            btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnEdit.setBackground(new Color(41, 128, 185));
            btnEdit.setForeground(Color.WHITE);
            btnEdit.setFocusPainted(false);
            btnEdit.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

            btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnDelete.setBackground(new Color(192, 57, 43));
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setFocusPainted(false);
            btnDelete.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

            add(btnEdit);
            add(btnDelete);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }
    }

    class ActionButtonEditor extends DefaultCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 4));
        private final JButton btnEdit = new JButton("Edit");
        private final JButton btnDelete = new JButton("Delete");
        private JTable table;
        private int currentRow;

        public ActionButtonEditor(JCheckBox checkBox, JTable table) {
            super(checkBox);
            this.table = table;
            panel.setOpaque(true);

            btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnEdit.setBackground(new Color(41, 128, 185));
            btnEdit.setForeground(Color.WHITE);
            btnEdit.setFocusPainted(false);
            btnEdit.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnDelete.setBackground(new Color(192, 57, 43));
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setFocusPainted(false);
            btnDelete.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnEdit.addActionListener(e -> {
                fireEditingStopped();
                int modelRow = table.convertRowIndexToModel(currentRow);
                String currentCategory = (String) table.getModel().getValueAt(modelRow, 0);
                String currentStatus = (String) table.getModel().getValueAt(modelRow, 1);
                
                EditCategoryDialog editDialog = new EditCategoryDialog((Frame) SwingUtilities.getWindowAncestor(table), currentCategory, currentStatus);
                editDialog.setVisible(true);
                
                if (editDialog.isUpdated()) {
                    table.getModel().setValueAt(editDialog.getCategoryName(), modelRow, 0);
                    table.getModel().setValueAt(editDialog.getStatus(), modelRow, 1);
                }
            });

            btnDelete.addActionListener(e -> {
                fireEditingStopped();
                int modelRow = table.convertRowIndexToModel(currentRow);
                String categoryName = (String) table.getModel().getValueAt(modelRow, 0);
                boolean confirm = showCustomConfirmDialog("Are you sure you want to delete " + categoryName + "?", "Warning");
                if (confirm) {
                    ((DefaultTableModel) table.getModel()).removeRow(modelRow);
                }
            });

            panel.add(btnEdit);
            panel.add(btnDelete);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.currentRow = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Action";
        }
    }
}