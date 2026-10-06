package ui;

import db.MedicineDAO;
import models.Medicine;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

public class MedicineCategoryPanel extends JPanel {

    // same palette / spacing as MedicinePanel
    private static final Color BORDER = new Color(220, 225, 230);
    private static final Color TEXT = new Color(60, 65, 70);
    private static final Color MUTED = new Color(108, 117, 125);
    private static final Color PRIMARY_SOFT = new Color(224, 243, 241);
    private static final int CELL_PAD = 10;

    // model column indexes
    private static final int COL_NAME = 0, COL_STATUS = 1, COL_ACTION = 2;
    private static final int ACTION_WIDTH = 150;
    private static final int BADGE_WIDTH = 75;

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
                fitColumns();
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
                    // Pattern.quote so characters like ( or [ do not break the search
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
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
                return column == COL_ACTION;
            }
        };

        loadCategoriesWithDynamicStatus();

        categoryTable = new JTable(categoryModel);

        // same look as the Medicine List table
        categoryTable.setSelectionBackground(PRIMARY_SOFT);
        categoryTable.setSelectionForeground(TEXT);

        rowSorter = new TableRowSorter<>(categoryModel);
        categoryTable.setRowSorter(rowSorter);
        rowSorter.setSortable(COL_ACTION, false);

        categoryTable.setRowHeight(33);
        categoryTable.setShowVerticalLines(true);
        categoryTable.setShowHorizontalLines(true);
        categoryTable.setGridColor(new Color(235, 238, 242));
        categoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        categoryTable.setFillsViewportHeight(true);

        JTableHeader header = new GroupHeader(categoryTable.getColumnModel(), 36);
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        categoryTable.setTableHeader(header);

        // fixed columns: can't be dragged to resize or reorder
        categoryTable.getTableHeader().setResizingAllowed(false);
        categoryTable.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer textRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
                setBorder(new EmptyBorder(0, CELL_PAD, 0, CELL_PAD));
                setHorizontalAlignment(JLabel.LEFT);
                setFont(new Font("Segoe UI", Font.PLAIN, 12));
                setForeground(TEXT);
                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                } else {
                    setBackground(Color.WHITE);
                }
                return this;
            }
        };
        categoryTable.getColumnModel().getColumn(COL_NAME).setCellRenderer(textRenderer);

        categoryTable.getColumnModel().getColumn(COL_STATUS).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, CELL_PAD, 4));
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
                badge.setPreferredSize(new Dimension(BADGE_WIDTH, 24));

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

        categoryTable.getColumnModel().getColumn(COL_ACTION).setCellRenderer(new ActionButtonRenderer());
        categoryTable.getColumnModel().getColumn(COL_ACTION).setCellEditor(new ActionButtonEditor(new JCheckBox(), categoryTable));

        categoryTable.getColumnModel().getColumn(COL_NAME).setPreferredWidth(200);
        categoryTable.getColumnModel().getColumn(COL_STATUS).setPreferredWidth(110);
        categoryTable.getColumnModel().getColumn(COL_ACTION).setPreferredWidth(ACTION_WIDTH);

        JScrollPane scrollPane = new JScrollPane(categoryTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));
        for (JScrollBar bar : new JScrollBar[]{scrollPane.getVerticalScrollBar(), scrollPane.getHorizontalScrollBar()}) {
            bar.setUI(new SlimScrollBarUI());
            bar.setOpaque(true);
            bar.setBackground(Color.WHITE);
            bar.setUnitIncrement(16);
        }
        JPanel corner = new JPanel();
        corner.setBackground(Color.WHITE);
        scrollPane.setCorner(JScrollPane.LOWER_RIGHT_CORNER, corner);
        scrollPane.getViewport().addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                updateResizeMode();
            }
        });

        bottomTableContainer.add(scrollPane, BorderLayout.CENTER);
        mainContainer.add(bottomTableContainer, BorderLayout.CENTER);

        add(mainContainer, BorderLayout.CENTER);

        fitColumns();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadCategoriesWithDynamicStatus();
            }
        });
    }

    // ------------------------------------------------------------------ table helpers

    /** Size the Name / Status columns to their longest value; Action stays a fixed width. */
    private void fitColumns() {
        if (categoryTable == null) return;
        FontMetrics fm = categoryTable.getFontMetrics(new Font("Segoe UI", Font.BOLD, 12));
        TableColumnModel cm = categoryTable.getColumnModel();
        for (int v = 0; v < cm.getColumnCount(); v++) {
            TableColumn col = cm.getColumn(v);
            int mc = col.getModelIndex();

            int w;
            if (mc == COL_ACTION) {
                w = ACTION_WIDTH;
            } else {
                w = fm.stringWidth(String.valueOf(col.getHeaderValue()));
                for (int r = 0; r < categoryModel.getRowCount(); r++) {
                    Object val = categoryModel.getValueAt(r, mc);
                    if (val != null) w = Math.max(w, fm.stringWidth(val.toString()));
                }
                w += CELL_PAD * 2 + 6;
                if (mc == COL_STATUS) w = Math.max(w, BADGE_WIDTH + CELL_PAD * 2);
            }
            col.setMinWidth(w);
            col.setPreferredWidth(w);
        }
        updateResizeMode();
    }

    /** Fill the width when there is room, scroll sideways when the columns do not fit. */
    private void updateResizeMode() {
        if (categoryTable == null) return;
        int total = 0;
        TableColumnModel cm = categoryTable.getColumnModel();
        for (int i = 0; i < cm.getColumnCount(); i++) total += cm.getColumn(i).getPreferredWidth();
        Container vp = SwingUtilities.getAncestorOfClass(JViewport.class, categoryTable);
        int avail = vp == null ? 0 : vp.getWidth();
        categoryTable.setAutoResizeMode(avail > 0 && total > avail ? JTable.AUTO_RESIZE_OFF : JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    private void loadCategoriesWithDynamicStatus() {
        categoryModel.setRowCount(0);

        List<Medicine> allMeds = MedicineDAO.getAllMedicines();

        java.util.Set<String> uniqueCategories = new java.util.LinkedHashSet<>();
        uniqueCategories.add("Tablet");
        uniqueCategories.add("Syrup");
        uniqueCategories.add("Capsule");
        uniqueCategories.add("Injection");
        uniqueCategories.add("Ointment");
        uniqueCategories.add("Drops");
        uniqueCategories.add("Supplements / Vitamins");

        if (allMeds != null) {
            for (Medicine med : allMeds) {
                if (med.getMedicineCategory() != null && !med.getMedicineCategory().trim().isEmpty()) {
                    uniqueCategories.add(med.getMedicineCategory().trim());
                }
            }
        }

        for (String catName : uniqueCategories) {
            boolean hasMedicine = false;
            if (allMeds != null) {
                for (Medicine med : allMeds) {
                    if (med.getMedicineCategory() != null && med.getMedicineCategory().equalsIgnoreCase(catName)) {
                        hasMedicine = true;
                        break;
                    }
                }
            }

            String status = hasMedicine ? "Active" : "Inactive";
            categoryModel.addRow(new Object[]{catName, status, "Action"});
        }

        fitColumns();
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

    // ------------------------------------------------------------ header

    /** Table header with the same look as the Medicine List page (single row here, no groups). */
    private static class GroupHeader extends JTableHeader {

        GroupHeader(TableColumnModel cm, int height) {
            super(cm);
            setPreferredSize(new Dimension(0, height));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(new Color(248, 250, 252));
            g.fillRect(0, 0, getWidth(), getHeight());

            int h = getHeight();
            for (int i = 0; i < columnModel.getColumnCount(); i++) {
                Rectangle r = getHeaderRect(i);
                if (r.width <= 0) continue;
                String text = String.valueOf(columnModel.getColumn(i).getHeaderValue());

                g.setColor(new Color(248, 250, 252));
                g.fillRect(r.x, 0, r.width, h);
                g.setColor(BORDER);
                g.drawRect(r.x, 0, r.width - 1, h - 1);
                g.setColor(MUTED);
                g.setFont(getFont());
                FontMetrics fm = g.getFontMetrics();
                int ty = (h + fm.getAscent() - fm.getDescent()) / 2;
                g.drawString(text, r.x + CELL_PAD, ty);
            }
        }
    }

    // ------------------------------------------------------------ slim scrollbar

    /** Thin rounded scrollbar: no arrow buttons, no track, just a soft gray thumb. */
    private static class SlimScrollBarUI extends BasicScrollBarUI {
        private static final int SIZE = 12;
        private static final Color THUMB = new Color(176, 184, 192);
        private static final Color THUMB_HOVER = new Color(150, 159, 168);

        @Override
        protected void configureScrollBarColors() {
            super.configureScrollBarColors();
            thumbColor = THUMB;
            trackColor = Color.WHITE;
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            return new Dimension(SIZE, SIZE);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return noButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return noButton();
        }

        private JButton noButton() {
            JButton b = new JButton();
            Dimension zero = new Dimension(0, 0);
            b.setPreferredSize(zero);
            b.setMinimumSize(zero);
            b.setMaximumSize(zero);
            return b;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            g.setColor(Color.WHITE);
            g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty() || !scrollbar.isEnabled()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isThumbRollover() || isDragging ? THUMB_HOVER : THUMB);
            int pad = 2;
            int arc = Math.min(r.width, r.height) - pad * 2;
            g2.fillRoundRect(r.x + pad, r.y + pad, r.width - pad * 2, r.height - pad * 2, arc, arc);
            g2.dispose();
        }
    }

    // ------------------------------------------------------------ action buttons

    static class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton btnEdit = new JButton("Edit");
        private final JButton btnDelete = new JButton("Delete");

        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.LEFT, 5, 4));
            setBorder(new EmptyBorder(0, CELL_PAD - 5, 0, 0)); // line up with the header text
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
            panel.setBorder(new EmptyBorder(0, CELL_PAD - 5, 0, 0));
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
                    fitColumns();
                }
            });

            btnDelete.addActionListener(e -> {
                fireEditingStopped();
                int modelRow = table.convertRowIndexToModel(currentRow);
                String categoryName = (String) table.getModel().getValueAt(modelRow, 0);
                boolean confirm = showCustomConfirmDialog("Are you sure you want to delete " + categoryName + "?", "Warning");
                if (confirm) {
                    ((DefaultTableModel) table.getModel()).removeRow(modelRow);
                    fitColumns();
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