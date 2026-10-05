package ui;

import db.MedicineDAO;
import models.Medicine;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MedicinePanel extends JPanel {

    private static final Color PAGE_BG = new Color(240, 242, 245);
    private static final Color SURFACE = Color.WHITE;
    private static final Color BORDER = new Color(220, 225, 230);
    private static final Color TEXT = new Color(60, 65, 70);
    private static final Color MUTED = new Color(108, 117, 125);
    private static final Color PRIMARY = new Color(13, 148, 136);
    private static final Color PRIMARY_SOFT = new Color(224, 243, 241);
    private static final Color DANGER = new Color(192, 57, 43);
    private JTable activeTable, outOfStockTable, expiredTable;
    private DefaultTableModel activeModel, outOfStockModel, expiredModel;
    private JTextField txtSearch;
    private TableRowSorter<DefaultTableModel> activeRowSorter, outOfStockRowSorter, expiredRowSorter;
    private JTabbedPane tabbedPane;

    public MedicinePanel() {

        UIManager.put("TabbedPane.highlight", new Color(200, 205, 210));

        UIManager.put("TabbedPane.lightHighlight", new Color(220, 224, 230));

        UIManager.put("TabbedPane.selected", new Color(230, 235, 240));

        UIManager.put("TabbedPane.selectHighlight", new Color(200, 205, 210));

        UIManager.put("TabbedPane.focus", new Color(200, 205, 210));

        UIManager.put("TabbedPane.borderHighlightColor", new Color(200, 205, 210));

        UIManager.put("TabbedPane.darkShadow", new Color(180, 185, 190));

        UIManager.put("TabbedPane.shadow", new Color(180, 185, 190));

        setLayout(new BorderLayout(0, 15));

        setBackground(PAGE_BG);

        setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel lblTitle = new JLabel("Medicine List");

        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));

        lblTitle.setForeground(TEXT);

        add(lblTitle, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));

        contentPanel.setBackground(Color.WHITE);

        contentPanel.setBorder(BorderFactory.createCompoundBorder(

            BorderFactory.createLineBorder(BORDER, 1),

            new EmptyBorder(15, 15, 15, 15)

        ));

        JPanel topBar = new JPanel(new BorderLayout());

        topBar.setOpaque(false);

        JPanel rightTopPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

        rightTopPanel.setOpaque(false);

        JButton btnCopy = new JButton("Copy");

        JButton btnCsv = new JButton("CSV");

        JButton btnExcel = new JButton("Excel");

        JButton btnPdf = new JButton("PDF");

        JButton btnPrint = new JButton("Print");

        for (JButton b : new JButton[]{btnCopy, btnCsv, btnExcel, btnPdf, btnPrint}) {

            b.setFont(new Font("Segoe UI", Font.PLAIN, 11));

            b.setFocusPainted(false);

            b.setBackground(SURFACE);

            b.setForeground(TEXT);

            b.setBorder(BorderFactory.createLineBorder(BORDER));

            b.setPreferredSize(new Dimension(62, 30));

            b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        }

        btnCopy.addActionListener(e -> {

            JTable currentTable = getActiveTableByTab();

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < currentTable.getRowCount(); i++) {

                for (int j = 0; j < currentTable.getColumnCount() - 1; j++) {

                    sb.append(currentTable.getValueAt(i, j)).append("\t");

                }

                sb.append("\n");

            }

            StringSelection selection = new StringSelection(sb.toString());

            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);

            CustomDialog.showMessage(this, "Table data copied to clipboard successfully!", "Information", false);

        });

        btnCsv.addActionListener(e -> {

            try {

                JTable currentTable = getActiveTableByTab();

                FileDialog fileDialog = new FileDialog((Frame) SwingUtilities.getWindowAncestor(this), "Save CSV", FileDialog.SAVE);

                fileDialog.setFile("medicines_export.csv");

                fileDialog.setVisible(true);

                String fileName = fileDialog.getFile();

                String directory = fileDialog.getDirectory();

                if (fileName != null) {

                    File file = new File(directory, fileName);

                    PrintWriter pw = new PrintWriter(new FileWriter(file));

                    for (int i = 0; i < currentTable.getColumnCount() - 1; i++) {

                        pw.print(currentTable.getColumnName(i) + (i == currentTable.getColumnCount() - 2 ? "" : ","));

                    }

                    pw.println();

                    for (int i = 0; i < currentTable.getRowCount(); i++) {

                        for (int j = 0; j < currentTable.getColumnCount() - 1; j++) {

                            pw.print(currentTable.getValueAt(i, j) + (j == currentTable.getColumnCount() - 2 ? "" : ","));

                        }

                        pw.println();

                    }

                    pw.close();

                    CustomDialog.showMessage(this, "CSV file exported successfully!", "CSV Export", false);

                }

            } catch (Exception ex) {

                CustomDialog.showMessage(this, "Error exporting to CSV: " + ex.getMessage(), "Error", true);

            }

        });

        btnExcel.addActionListener(e -> {

            try {

                JTable currentTable = getActiveTableByTab();

                FileDialog fileDialog = new FileDialog((Frame) SwingUtilities.getWindowAncestor(this), "Save Excel", FileDialog.SAVE);

                fileDialog.setFile("medicines_export.xls");

                fileDialog.setVisible(true);

                String fileName = fileDialog.getFile();

                String directory = fileDialog.getDirectory();

                if (fileName != null) {

                    File file = new File(directory, fileName);

                    PrintWriter pw = new PrintWriter(new FileWriter(file));

                    pw.println("<html xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:x=\"urn:schemas-microsoft-com:office:excel\" xmlns=\"http://www.w3.org/TR/REC-html40\">");

                    pw.println("<head><meta charset='UTF-8'></head><body>");

                    pw.println("<table border='1'><tr>");

                    for (int i = 0; i < currentTable.getColumnCount() - 1; i++) {

                        pw.println("<th>" + currentTable.getColumnName(i) + "</th>");

                    }

                    pw.println("</tr>");

                    for (int i = 0; i < currentTable.getRowCount(); i++) {

                        pw.println("<tr>");

                        for (int j = 0; j < currentTable.getColumnCount() - 1; j++) {

                            pw.println("<td>" + currentTable.getValueAt(i, j) + "</td>");

                        }

                        pw.println("</tr>");

                    }

                    pw.println("</table></body></html>");

                    pw.close();

                    CustomDialog.showMessage(this, "Excel file exported successfully!", "Excel Export", false);

                }

            } catch (Exception ex) {

                CustomDialog.showMessage(this, "Error exporting to Excel: " + ex.getMessage(), "Error", true);

            }

        });

        btnPdf.addActionListener(e -> {

            try {

                JTable currentTable = getActiveTableByTab();

                FileDialog fileDialog = new FileDialog((Frame) SwingUtilities.getWindowAncestor(this), "Save PDF", FileDialog.SAVE);

                fileDialog.setFile("medicine_list.pdf");

                fileDialog.setVisible(true);

                String fileName = fileDialog.getFile();

                String directory = fileDialog.getDirectory();

                if (fileName != null) {

                    boolean complete = currentTable.print(JTable.PrintMode.FIT_WIDTH, null, null);

                    if (complete) {

                        CustomDialog.showMessage(this, "Document saved to PDF successfully!", "PDF Export", false);

                    }

                }

            } catch (Exception ex) {

                CustomDialog.showMessage(this, "Error generating PDF: " + ex.getMessage(), "PDF Error", true);

            }

        });

        btnPrint.addActionListener(e -> {

            try {

                JTable currentTable = getActiveTableByTab();

                FileDialog fileDialog = new FileDialog((Frame) SwingUtilities.getWindowAncestor(this), "Print Document", FileDialog.SAVE);

                fileDialog.setFile("medicine_print.pdf");

                fileDialog.setVisible(true);

                String fileName = fileDialog.getFile();

                String directory = fileDialog.getDirectory();

                if (fileName != null) {

                    boolean complete = currentTable.print();

                    if (complete) {

                        CustomDialog.showMessage(this, "Printing completed successfully.", "Print Success", false);

                    }

                }

            } catch (Exception ex) {

                CustomDialog.showMessage(this, "Printing failed: " + ex.getMessage(), "Print Error", true);

            }

        });

        txtSearch = new JTextField(18);

        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSearch.setBackground(SURFACE);
        txtSearch.setForeground(TEXT);
        txtSearch.setPreferredSize(new Dimension(220, 32));

        txtSearch.setBorder(BorderFactory.createCompoundBorder(

            BorderFactory.createLineBorder(BORDER),

            BorderFactory.createEmptyBorder(4, 6, 4, 6)

        ));

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {

            @Override

            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }

            @Override

            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }

            @Override

            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }

            private void filterTable() {

                String text = txtSearch.getText().trim();

                RowFilter<DefaultTableModel, Object> rf = null;

                if (!text.isEmpty()) {

                    rf = RowFilter.regexFilter("(?i)" + text);

                }

                if (activeRowSorter != null) activeRowSorter.setRowFilter(rf);

                if (outOfStockRowSorter != null) outOfStockRowSorter.setRowFilter(rf);

                if (expiredRowSorter != null) expiredRowSorter.setRowFilter(rf);

            }

        });

        JLabel lblSearch = new JLabel("Search:");

        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        lblSearch.setForeground(MUTED);

        rightTopPanel.add(btnCopy);

        rightTopPanel.add(btnCsv);

        rightTopPanel.add(btnExcel);

        rightTopPanel.add(btnPdf);

        rightTopPanel.add(btnPrint);

        rightTopPanel.add(Box.createHorizontalStrut(8));

        rightTopPanel.add(lblSearch);

        rightTopPanel.add(txtSearch);

        topBar.add(rightTopPanel, BorderLayout.EAST);

        contentPanel.add(topBar, BorderLayout.NORTH);

        String[] columns = {

            "ID", "Medicine Name", "Medicine Category", "Buy Price",

            "Sell Price", "Quantity", "Company Name", "Expire Date", "Status", "Action"

        };

        activeModel = new DefaultTableModel(columns, 0) {

            @Override public boolean isCellEditable(int row, int column) { return column == 9; }

        };

        outOfStockModel = new DefaultTableModel(columns, 0) {

            @Override public boolean isCellEditable(int row, int column) { return column == 9; }

        };

        expiredModel = new DefaultTableModel(columns, 0) {

            @Override public boolean isCellEditable(int row, int column) { return column == 9; }

        };

        activeTable = new JTable(activeModel);

        outOfStockTable = new JTable(outOfStockModel);

        expiredTable = new JTable(expiredModel);

        for (JTable t : new JTable[]{activeTable, outOfStockTable, expiredTable}) {

            t.setSelectionBackground(PRIMARY_SOFT);

            t.setSelectionForeground(TEXT);

            t.setRowHeight(33);

            t.setShowVerticalLines(false);

            t.setShowHorizontalLines(true);

            t.setGridColor(new Color(235, 238, 242));

            t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            t.setFillsViewportHeight(true);

            t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
            t.getTableHeader().setPreferredSize(new Dimension(0, 36));

            t.getTableHeader().setBackground(new Color(248, 250, 252));

            t.getTableHeader().setForeground(MUTED);

            t.getColumnModel().getColumn(0).setPreferredWidth(40);

            t.getColumnModel().getColumn(1).setPreferredWidth(140);

            t.getColumnModel().getColumn(2).setPreferredWidth(100);

            t.getColumnModel().getColumn(3).setPreferredWidth(75);

            t.getColumnModel().getColumn(4).setPreferredWidth(75);

            t.getColumnModel().getColumn(5).setPreferredWidth(65);

            t.getColumnModel().getColumn(6).setPreferredWidth(110);

            t.getColumnModel().getColumn(7).setPreferredWidth(85);

            t.getColumnModel().getColumn(8).setPreferredWidth(95);

            t.getColumnModel().getColumn(9).setPreferredWidth(160);

            ActionButtonPanel actionPanel = new ActionButtonPanel();

            t.getColumnModel().getColumn(9).setCellRenderer(actionPanel);

            t.getColumnModel().getColumn(9).setCellEditor(actionPanel);

        }

        DefaultTableCellRenderer activeTableRenderer = new DefaultTableCellRenderer() {

            @Override

            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setHorizontalAlignment(column == 5 || column == 8 ? JLabel.CENTER : JLabel.LEFT);

                int modelRow = table.convertRowIndexToModel(row);

                try {

                    String qtyStr = table.getModel().getValueAt(modelRow, 5).toString().replaceAll("[^0-9\\\\-]", "");

                    int qty = Integer.parseInt(qtyStr);

                    if (qty <= 0) {

                        c.setForeground(DANGER);

                        setFont(new Font("Segoe UI", Font.BOLD, 12));

                    } else {

                        if (!isSelected) {

                            c.setForeground(TEXT);

                        }

                        setFont(new Font("Segoe UI", Font.PLAIN, 12));

                    }

                } catch (Exception ignored) {}

                if (isSelected) {

                    c.setBackground(table.getSelectionBackground());

                    c.setForeground(table.getSelectionForeground());

                } else {

                    c.setBackground(Color.WHITE);

                }

                return c;

            }

        };

        for (int i = 0; i < 9; i++) {

            activeTable.getColumnModel().getColumn(i).setCellRenderer(activeTableRenderer);

            outOfStockTable.getColumnModel().getColumn(i).setCellRenderer(activeTableRenderer);

            expiredTable.getColumnModel().getColumn(i).setCellRenderer(activeTableRenderer);

        }

        activeRowSorter = new TableRowSorter<>(activeModel);

        activeTable.setRowSorter(activeRowSorter);

        outOfStockRowSorter = new TableRowSorter<>(outOfStockModel);

        outOfStockTable.setRowSorter(outOfStockRowSorter);

        expiredRowSorter = new TableRowSorter<>(expiredModel);

        expiredTable.setRowSorter(expiredRowSorter);

        loadTableData();

        tabbedPane = new JTabbedPane();

        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabbedPane.setBackground(new Color(245, 247, 250));

        tabbedPane.setFocusable(false);

        tabbedPane.setBorder(BorderFactory.createLineBorder(new Color(225, 229, 234), 1));

        tabbedPane.setUI(new BasicTabbedPaneUI() {

            @Override

            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {

                if (isSelected) {

                    g.setColor(PRIMARY_SOFT);

                } else {

                    g.setColor(Color.WHITE);

                }

                g.fillRect(x, y, w, h);

            }

            @Override

            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {

                g.setColor(BORDER);

                g.drawRect(x, y, w, h);

            }

            @Override

            protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {

            }

            @Override

            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {

                int width = tabPane.getWidth();

                int height = tabPane.getHeight();

                Insets insets = tabPane.getInsets();

                int x = insets.left;

                int y = insets.top;

                int w = width - insets.right - insets.left;

                int h = height - insets.top - insets.bottom;

                g.setColor(BORDER);

                g.drawRect(x, y + calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight), w - 1, h - calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight) - 1);

            }

        });

        JScrollPane activeScroll = new JScrollPane(activeTable);

        activeScroll.getViewport().setBackground(Color.WHITE);

        activeScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));

        JScrollPane outOfStockScroll = new JScrollPane(outOfStockTable);

        outOfStockScroll.getViewport().setBackground(Color.WHITE);

        outOfStockScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));

        JScrollPane expiredScroll = new JScrollPane(expiredTable);

        expiredScroll.getViewport().setBackground(Color.WHITE);

        expiredScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));

        tabbedPane.addTab("Active Medicines", activeScroll);

        tabbedPane.addTab("Out of Stock", outOfStockScroll);

        tabbedPane.addTab("Expired Medicines", expiredScroll);

        contentPanel.add(tabbedPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        this.addComponentListener(new java.awt.event.ComponentAdapter() {

            @Override

            public void componentShown(java.awt.event.ComponentEvent e) {

                loadTableData();

            }

        });

    }

    private JTable getActiveTableByTab() {

        int index = tabbedPane.getSelectedIndex();

        if (index == 0) return activeTable;

        if (index == 1) return outOfStockTable;

        return expiredTable;

    }

    public void loadTableData() {

        activeModel.setRowCount(0);

        outOfStockModel.setRowCount(0);

        expiredModel.setRowCount(0);

        List<Medicine> list = MedicineDAO.getAllMedicines();

        LocalDate today = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Medicine m : list) {

            boolean isExpired = false;

            try {

                if (m.getExpiryDate() != null && !m.getExpiryDate().isEmpty()) {

                    LocalDate expiryDate = LocalDate.parse(m.getExpiryDate().trim(), formatter);

                    if (expiryDate.isBefore(today) || expiryDate.isEqual(today)) {

                        isExpired = true;

                    }

                }

            } catch (Exception ignored) {}

            boolean isOutOfStock = m.getStock() <= 0;

            String status = isExpired ? "Expired" : (isOutOfStock ? "Out of Stock" : "Available");

            Object[] rowData = new Object[]{

                m.getId(),

                m.getName(),

                m.getMedicineCategory(),

                "₱" + m.getBuyPrice(),

                "₱" + m.getSellPrice(),

                m.getStock(),

                m.getCompanyName(),

                m.getExpiryDate(),

                status,

                ""

            };

            if (isExpired) {

                expiredModel.addRow(rowData);

            } else if (isOutOfStock) {

                outOfStockModel.addRow(rowData);

                activeModel.addRow(rowData);

            } else {

                activeModel.addRow(rowData);

            }

        }

    }

    @Override

    public void addNotify() {

        super.addNotify();

        loadTableData();

    }

    class ActionButtonPanel extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

        private JPanel panel;
        private JButton btnEdit, btnDelete;

        public ActionButtonPanel() {

            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));

            panel.setOpaque(true);

            btnEdit = new JButton("Edit");

            btnEdit.setBackground(PRIMARY);

            btnEdit.setForeground(Color.WHITE);

            btnEdit.setFocusPainted(false);

            btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 11));

            btnEdit.setPreferredSize(new Dimension(70, 26));

            btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnDelete = new JButton("Delete");

            btnDelete.setBackground(DANGER);

            btnDelete.setForeground(Color.WHITE);

            btnDelete.setFocusPainted(false);

            btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 11));

            btnDelete.setPreferredSize(new Dimension(70, 26));

            btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));

            panel.add(btnEdit);

            panel.add(btnDelete);

            btnEdit.addActionListener(new ActionListener() {

                @Override

                public void actionPerformed(ActionEvent e) {

                    fireEditingStopped();

                    JTable currentTable = getActiveTableByTab();

                    int row = currentTable.getSelectedRow();

                    if (row != -1) {

                        int modelRow = currentTable.convertRowIndexToModel(row);

                        DefaultTableModel model = (DefaultTableModel) currentTable.getModel();

                        try {

                            int id = Integer.parseInt(model.getValueAt(modelRow, 0).toString());

                            String name = model.getValueAt(modelRow, 1).toString();

                            String category = model.getValueAt(modelRow, 2).toString();

                            double buyPrice = Double.parseDouble(model.getValueAt(modelRow, 3).toString().replace("₱", "").replace(",", "").trim());

                            double sellPrice = Double.parseDouble(model.getValueAt(modelRow, 4).toString().replace("₱", "").replace(",", "").trim());

                            String qtyRaw = model.getValueAt(modelRow, 5).toString().replaceAll("[^0-9\\\\-]", "");

                            int stock = Integer.parseInt(qtyRaw);

                            String company = model.getValueAt(modelRow, 6).toString();

                            String expiry = model.getValueAt(modelRow, 7).toString();

                            Medicine medToEdit = new Medicine();

                            medToEdit.setId(id);

                            medToEdit.setName(name);

                            medToEdit.setMedicineCategory(category);

                            medToEdit.setBuyPrice(buyPrice);

                            medToEdit.setSellPrice(sellPrice);

                            medToEdit.setStock(stock);

                            medToEdit.setCompanyName(company);

                            medToEdit.setExpiryDate(expiry);

                            EditMedicineDialog editDialog = new EditMedicineDialog((Frame) SwingUtilities.getWindowAncestor(currentTable), medToEdit);

                            editDialog.setVisible(true);

                            if (editDialog.isUpdated()) {

                                loadTableData();

                            }

                        } catch (Exception ex) {

                            CustomDialog.showMessage(currentTable, "Error opening edit form: " + ex.getMessage(), "Error", true);

                        }

                    }

                }

            });

            btnDelete.addActionListener(new ActionListener() {

                @Override

                public void actionPerformed(ActionEvent e) {

                    fireEditingStopped();

                    JTable currentTable = getActiveTableByTab();

                    int row = currentTable.getSelectedRow();

                    if (row != -1) {

                        int modelRow = currentTable.convertRowIndexToModel(row);

                        DefaultTableModel model = (DefaultTableModel) currentTable.getModel();

                        int medicineId = Integer.parseInt(model.getValueAt(modelRow, 0).toString());

                        boolean confirmed = CustomDialog.showConfirm(currentTable, "Are you sure you want to delete this medicine? Click \"Yes\" to delete.", "Warning");

                        if (confirmed) {

                            MedicineDAO.deleteMedicine(medicineId);

                            loadTableData();

                        }

                    }

                }

            });

        }

        @Override

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);

            return panel;

        }

        @Override

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

            panel.setBackground(table.getSelectionBackground());

            return panel;

        }

        @Override

        public Object getCellEditorValue() {

            return null;

        }

    }

    private static class CustomDialog {

        private static boolean confirmResult = false;

        public static void showMessage(Component parent, String message, String title, boolean isWarning) {

            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), title, true);

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

            dialog.setLocationRelativeTo(parent);

            dialog.setVisible(true);

        }

        public static boolean showConfirm(Component parent, String message, String title) {

            confirmResult = false;

            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), title, true);

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

            JButton btnNo = new JButton("No");

            btnNo.setFont(new Font("Segoe UI", Font.BOLD, 12));

            btnNo.setBackground(new Color(192, 57, 43));

            btnNo.setForeground(Color.WHITE);

            btnNo.setFocusPainted(false);

            btnNo.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));

            btnNo.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnYes.addActionListener(e -> {

                confirmResult = true;

                dialog.dispose();

            });

            btnNo.addActionListener(e -> {

                confirmResult = false;

                dialog.dispose();

            });

            bottomPanel.add(btnYes);

            bottomPanel.add(btnNo);

            dialog.add(bottomPanel, BorderLayout.SOUTH);

            dialog.pack();

            dialog.setSize(Math.max(dialog.getWidth() + 80, 520), Math.max(dialog.getHeight() + 40, 150));

            dialog.setLocationRelativeTo(parent);

            dialog.setVisible(true);

            return confirmResult;

        }

    }

}
