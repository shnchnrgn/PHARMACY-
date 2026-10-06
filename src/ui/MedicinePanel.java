package ui;

import db.MedicineDAO;
import models.Medicine;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.geom.Path2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MedicinePanel extends JPanel {

    private static final Color PAGE_BG = new Color(240, 242, 245);
    private static final Color SURFACE = Color.WHITE;
    private static final Color BORDER = new Color(220, 225, 230);
    private static final Color LINE = new Color(226, 231, 236); // table lines (header + body use the same color)
    private static final Color TEXT = new Color(60, 65, 70);
    private static final Color MUTED = new Color(108, 117, 125);
    private static final Color PRIMARY = new Color(13, 148, 136);
    private static final Color PRIMARY_SOFT = new Color(224, 243, 241);
    private static final Color DANGER = new Color(192, 57, 43);
    private static final Color GROUP_BG = new Color(246, 249, 250);
    private static final Color GROUP_HOVER = new Color(233, 244, 242);
    private static final Color TOTAL_BG = new Color(238, 245, 244);
    private static final Color TOTAL_LINE = new Color(200, 208, 214);
    private static final Color HEADER_BG = new Color(248, 250, 252);

    private static final int CELL_PAD = 10; // same left/right padding for header text and cell text
    private static final int ACTION_WIDTH = 160; // Action column keeps a fixed width

    // Model column indexes (unchanged order + new Total Value column)
    private static final int COL_ID = 0, COL_NAME = 1, COL_CATEGORY = 2, COL_BUY = 3, COL_SELL = 4,
            COL_QTY = 5, COL_COMPANY = 6, COL_EXPIRE = 7, COL_STATUS = 8, COL_TOTAL = 9, COL_ACTION = 10;

    // Everything is centered. The header uses this same list, so a header always sits
    // exactly over its cells. Change an entry to JLabel.LEFT / RIGHT to align one column differently.
    // (indexed by MODEL column)
    private static final int[] ALIGN = {
        JLabel.CENTER, // ID
        JLabel.CENTER, // Medicine Name
        JLabel.CENTER, // Medicine Category
        JLabel.CENTER, // Buy Price
        JLabel.CENTER, // Sell Price
        JLabel.CENTER, // Quantity
        JLabel.CENTER, // Company Name
        JLabel.CENTER, // Expire Date
        JLabel.CENTER, // Status
        JLabel.CENTER, // Total Value
        JLabel.CENTER  // Action
    };

    // Order of columns as shown on screen (model indexes)
    private static final int[] VIEW_ORDER = {
        COL_CATEGORY, COL_NAME, COL_ID, COL_COMPANY, COL_BUY, COL_SELL,
        COL_QTY, COL_STATUS, COL_EXPIRE, COL_TOTAL, COL_ACTION
    };
    // Group label above each column (by view position). null = no group, header spans both rows
    private static final String[] HEADER_GROUPS = {
        null, null, null, null, "Price", "Price", "Stock", "Stock", null, null, null
    };

    private JTable activeTable, outOfStockTable, expiredTable;
    private DefaultTableModel activeModel, outOfStockModel, expiredModel;
    private JTextField txtSearch;
    private TableRowSorter<DefaultTableModel> activeRowSorter, outOfStockRowSorter, expiredRowSorter;
    private JTabbedPane tabbedPane;

    private final Map<JTable, List<Medicine>> tableData = new HashMap<>();
    private final Map<JTable, Set<String>> collapsed = new HashMap<>();
    private final Map<JTable, int[]> naturalWidths = new HashMap<>();

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
            JTable t = getActiveTableByTab();
            StringBuilder sb = new StringBuilder();
            for (String[] row : exportRows(t)) {
                sb.append(String.join("\t", row)).append("\n");
            }
            StringSelection selection = new StringSelection(sb.toString());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
            CustomDialog.showMessage(this, "Table data copied to clipboard successfully!", "Information", false);
        });

        btnCsv.addActionListener(e -> {
            try {
                JTable t = getActiveTableByTab();
                FileDialog fd = new FileDialog((Frame) SwingUtilities.getWindowAncestor(this), "Save CSV", FileDialog.SAVE);
                fd.setFile("medicines_export.csv");
                fd.setVisible(true);
                if (fd.getFile() != null) {
                    try (PrintWriter pw = new PrintWriter(new FileWriter(new File(fd.getDirectory(), fd.getFile())))) {
                        pw.println(String.join(",", exportHeaders(t)));
                        for (String[] row : exportRows(t)) {
                            pw.println(String.join(",", row));
                        }
                    }
                    CustomDialog.showMessage(this, "CSV file exported successfully!", "CSV Export", false);
                }
            } catch (Exception ex) {
                CustomDialog.showMessage(this, "Error exporting to CSV: " + ex.getMessage(), "Error", true);
            }
        });

        btnExcel.addActionListener(e -> {
            try {
                JTable t = getActiveTableByTab();
                FileDialog fd = new FileDialog((Frame) SwingUtilities.getWindowAncestor(this), "Save Excel", FileDialog.SAVE);
                fd.setFile("medicines_export.xls");
                fd.setVisible(true);
                if (fd.getFile() != null) {
                    try (PrintWriter pw = new PrintWriter(new FileWriter(new File(fd.getDirectory(), fd.getFile())))) {
                        pw.println("<html xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:x=\"urn:schemas-microsoft-com:office:excel\" xmlns=\"http://www.w3.org/TR/REC-html40\">");
                        pw.println("<head><meta charset='UTF-8'></head><body>");
                        pw.println("<table border='1'><tr>");
                        for (String h : exportHeaders(t)) pw.println("<th>" + h + "</th>");
                        pw.println("</tr>");
                        for (String[] row : exportRows(t)) {
                            pw.println("<tr>");
                            for (String c : row) pw.println("<td>" + c + "</td>");
                            pw.println("</tr>");
                        }
                        pw.println("</table></body></html>");
                    }
                    CustomDialog.showMessage(this, "Excel file exported successfully!", "Excel Export", false);
                }
            } catch (Exception ex) {
                CustomDialog.showMessage(this, "Error exporting to Excel: " + ex.getMessage(), "Error", true);
            }
        });

        btnPdf.addActionListener(e -> {
            try {
                JTable t = getActiveTableByTab();
                FileDialog fd = new FileDialog((Frame) SwingUtilities.getWindowAncestor(this), "Save PDF", FileDialog.SAVE);
                fd.setFile("medicine_list.pdf");
                fd.setVisible(true);
                if (fd.getFile() != null) {
                    boolean complete = t.print(JTable.PrintMode.FIT_WIDTH, null, null);
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
                JTable t = getActiveTableByTab();
                FileDialog fd = new FileDialog((Frame) SwingUtilities.getWindowAncestor(this), "Print Document", FileDialog.SAVE);
                fd.setFile("medicine_print.pdf");
                fd.setVisible(true);
                if (fd.getFile() != null) {
                    boolean complete = t.print();
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
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
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
            "Sell Price", "Quantity", "Company Name", "Expire Date", "Status", "Total Value", "Action"
        };

        activeModel = newModel(columns);
        outOfStockModel = newModel(columns);
        expiredModel = newModel(columns);

        activeTable = new JTable(activeModel);
        outOfStockTable = new JTable(outOfStockModel);
        expiredTable = new JTable(expiredModel);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
                int mc = table.convertColumnIndexToModel(column);
                int mr = table.convertRowIndexToModel(row);
                DefaultTableModel mdl = (DefaultTableModel) table.getModel();
                boolean group = isGroup(mdl, mr);
                boolean total = mr == mdl.getRowCount() - 1;
                boolean lastColumn = column == table.getColumnCount() - 1;
                boolean nextIsGroup = row == table.getRowCount() - 1
                        || isGroup(mdl, table.convertRowIndexToModel(row + 1));

                // Inside a category the (blank) category cell has no line between medicine rows,
                // so the whole category reads as one block. It closes at the last medicine row.
                boolean bottomLine = group || mc != COL_CATEGORY || nextIsGroup;

                setIcon(null);
                setBorder(lineBorder(total, bottomLine, !lastColumn, true));
                setHorizontalAlignment(ALIGN[mc]);
                setFont(new Font("Segoe UI", group ? Font.BOLD : Font.PLAIN, 12));
                setForeground(TEXT);

                if (group && mc == COL_CATEGORY && value instanceof CategoryLabel) {
                    CategoryLabel c = (CategoryLabel) value;
                    boolean open = !collapsed.get(table).contains(c.name);
                    setIcon(new ChevronIcon(open));
                    setIconTextGap(8);
                    setText("<html>" + escapeHtml(c.name)
                        + " <span style='color:#6c757d;font-weight:normal'>(" + c.count + ")</span></html>");
                }

                if (!group) {
                    // category name is shown only on the group row (like the sample table)
                    if (mc == COL_CATEGORY) setText("");
                    try {
                        int qty = Integer.parseInt(mdl.getValueAt(mr, COL_QTY).toString().replaceAll("[^0-9\\-]", ""));
                        if (qty <= 0) {
                            setForeground(DANGER);
                            setFont(new Font("Segoe UI", Font.BOLD, 12));
                        }
                    } catch (Exception ignored) {}
                }

                setBackground(rowBackground(table, row, isSelected));
                if (isSelected) setForeground(table.getSelectionForeground());
                return this;
            }
        };

        for (JTable t : new JTable[]{activeTable, outOfStockTable, expiredTable}) {
            tableData.put(t, new ArrayList<>());
            collapsed.put(t, new HashSet<>());

            t.setSelectionBackground(PRIMARY_SOFT);
            t.setSelectionForeground(TEXT);
            t.setRowHeight(33);

            // The lines are drawn by the cell borders (see lineBorder) instead of the JTable grid,
            // so header and body share the exact same line color and position.
            t.setShowGrid(false);
            t.setIntercellSpacing(new Dimension(0, 0));

            t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            t.setFillsViewportHeight(true);

            // two-level header
            JTableHeader header = new GroupHeader(t.getColumnModel(), HEADER_GROUPS);
            header.setFont(new Font("Segoe UI", Font.BOLD, 11));
            t.setTableHeader(header);

            int[] widths = {40, 140, 120, 75, 75, 65, 110, 85, 95, 100, ACTION_WIDTH};
            for (int i = 0; i < widths.length; i++) {
                t.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            }

            for (int i = 0; i < COL_ACTION; i++) {
                t.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
            }
            ActionButtonPanel actionPanel = new ActionButtonPanel();
            t.getColumnModel().getColumn(COL_ACTION).setCellRenderer(actionPanel);
            t.getColumnModel().getColumn(COL_ACTION).setCellEditor(actionPanel);

            // arrange columns visually: Category | Name | ID | Company | Price | Stock | Expire | Total | Action
            for (int pos = 0; pos < VIEW_ORDER.length; pos++) {
                int from = t.convertColumnIndexToView(VIEW_ORDER[pos]);
                if (from != pos) t.moveColumn(from, pos);
            }
            t.getTableHeader().setReorderingAllowed(false);
            t.getTableHeader().setResizingAllowed(false); // columns can't be dragged

            // hand cursor + highlight while the mouse is over a category row
            MouseAdapter hover = new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int r = t.rowAtPoint(e.getPoint());
                    boolean onCategory = false;
                    if (r >= 0) {
                        DefaultTableModel mdl = (DefaultTableModel) t.getModel();
                        onCategory = mdl.getValueAt(t.convertRowIndexToModel(r), COL_CATEGORY) instanceof CategoryLabel;
                    }
                    Integer now = onCategory ? Integer.valueOf(r) : null;
                    t.setCursor(onCategory ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
                    if (!java.util.Objects.equals(t.getClientProperty("hoverRow"), now)) {
                        t.putClientProperty("hoverRow", now);
                        t.repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    t.setCursor(Cursor.getDefaultCursor());
                    if (t.getClientProperty("hoverRow") != null) {
                        t.putClientProperty("hoverRow", null);
                        t.repaint();
                    }
                }
            };
            t.addMouseMotionListener(hover);
            t.addMouseListener(hover);

            // click a category row to expand / collapse
            t.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int r = t.rowAtPoint(e.getPoint());
                    if (r < 0) return;
                    int mr = t.convertRowIndexToModel(r);
                    DefaultTableModel mdl = (DefaultTableModel) t.getModel();
                    if (!isGroup(mdl, mr)) return;
                    Object v = mdl.getValueAt(mr, COL_CATEGORY);
                    if (v instanceof CategoryLabel) {
                        String cat = ((CategoryLabel) v).name;
                        Set<String> set = collapsed.get(t);
                        if (!set.remove(cat)) set.add(cat);
                        rebuild(t);
                    }
                }
            });
        }

        activeRowSorter = new TableRowSorter<>(activeModel);
        activeTable.setRowSorter(activeRowSorter);
        outOfStockRowSorter = new TableRowSorter<>(outOfStockModel);
        outOfStockTable.setRowSorter(outOfStockRowSorter);
        expiredRowSorter = new TableRowSorter<>(expiredModel);
        expiredTable.setRowSorter(expiredRowSorter);

        // sorting is disabled because rows are grouped by category
        for (TableRowSorter<DefaultTableModel> s : List.of(activeRowSorter, outOfStockRowSorter, expiredRowSorter)) {
            for (int i = 0; i < columns.length; i++) s.setSortable(i, false);
        }

        loadTableData();

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(new Color(245, 247, 250));
        tabbedPane.setFocusable(false);
        tabbedPane.setBorder(BorderFactory.createLineBorder(new Color(225, 229, 234), 1));
        tabbedPane.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                g.setColor(isSelected ? PRIMARY_SOFT : Color.WHITE);
                g.fillRect(x, y, w, h);
            }
            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                g.setColor(BORDER);
                g.drawRect(x, y, w, h);
            }
            @Override
            protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) { }
            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                int width = tabPane.getWidth();
                int height = tabPane.getHeight();
                Insets insets = tabPane.getInsets();
                int x = insets.left;
                int y = insets.top;
                int w = width - insets.right - insets.left;
                int h = height - insets.top - insets.bottom;
                int tabH = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                g.setColor(BORDER);
                g.drawRect(x, y + tabH, w - 1, h - tabH - 1);
            }
        });

        tabbedPane.addTab("Active Medicines", scroll(activeTable));
        tabbedPane.addTab("Out of Stock", scroll(outOfStockTable));
        tabbedPane.addTab("Expired Medicines", scroll(expiredTable));

        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadTableData();
            }
        });
    }

    // ------------------------------------------------------------------ helpers

    private JScrollPane scroll(JTable t) {
        JScrollPane sp = new JScrollPane(t);
        sp.getViewport().setBackground(Color.WHITE);
        sp.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));
        for (JScrollBar bar : new JScrollBar[]{sp.getVerticalScrollBar(), sp.getHorizontalScrollBar()}) {
            bar.setUI(new SlimScrollBarUI());
            bar.setOpaque(true);
            bar.setBackground(Color.WHITE);
            bar.setUnitIncrement(16);
        }
        JPanel corner = new JPanel();
        corner.setBackground(Color.WHITE);
        sp.setCorner(JScrollPane.LOWER_RIGHT_CORNER, corner);
        sp.getViewport().addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                layoutColumns(t);
            }
        });
        return sp;
    }

    private DefaultTableModel newModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == COL_ACTION && !isGroup(this, row);
            }
        };
    }

    /** Category rows and the Total row have an empty ID cell. */
    private static boolean isGroup(DefaultTableModel m, int modelRow) {
        Object v = m.getValueAt(modelRow, COL_ID);
        return v == null || v.toString().isEmpty();
    }

    private static String money(double v) {
        return "\u20B1" + String.format("%.2f", v);
    }

    /**
     * Border that draws a cell's bottom / right line (plus the padding when asked).
     * The Total row also gets a darker line on top.
     */
    private static Border lineBorder(boolean totalRow, boolean bottom, boolean right, boolean padded) {
        Border line = BorderFactory.createMatteBorder(0, 0, bottom ? 1 : 0, right ? 1 : 0, LINE);
        Border inner = padded
                ? BorderFactory.createCompoundBorder(line, new EmptyBorder(0, CELL_PAD, 0, CELL_PAD))
                : line;
        if (!totalRow) return inner;
        return BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, TOTAL_LINE), inner);
    }

    private void applyFilter() {
        final String text = txtSearch.getText().trim().toLowerCase();
        RowFilter<DefaultTableModel, Object> rf = null;
        if (!text.isEmpty()) {
            rf = new RowFilter<DefaultTableModel, Object>() {
                @Override
                public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                    Object id = entry.getValue(COL_ID);
                    if (id == null || id.toString().isEmpty()) return true; // keep category / total rows
                    for (int i = 0; i < entry.getValueCount(); i++) {
                        if (entry.getStringValue(i).toLowerCase().contains(text)) return true;
                    }
                    return false;
                }
            };
        }
        if (activeRowSorter != null) activeRowSorter.setRowFilter(rf);
        if (outOfStockRowSorter != null) outOfStockRowSorter.setRowFilter(rf);
        if (expiredRowSorter != null) expiredRowSorter.setRowFilter(rf);
    }

    private List<String> exportHeaders(JTable t) {
        List<String> h = new ArrayList<>();
        for (int i = 0; i < t.getColumnCount() - 1; i++) h.add(t.getColumnName(i));
        return h;
    }

    /** Only real medicine rows (no category / total rows, no Action column). */
    private List<String[]> exportRows(JTable t) {
        DefaultTableModel mdl = (DefaultTableModel) t.getModel();
        List<String[]> rows = new ArrayList<>();
        for (int i = 0; i < t.getRowCount(); i++) {
            if (isGroup(mdl, t.convertRowIndexToModel(i))) continue;
            String[] r = new String[t.getColumnCount() - 1];
            for (int j = 0; j < r.length; j++) {
                int mc = t.convertColumnIndexToModel(j);
                // category cell is blank on screen, but export it
                r[j] = String.valueOf(mdl.getValueAt(t.convertRowIndexToModel(i), mc));
            }
            rows.add(r);
        }
        return rows;
    }

    private JTable getActiveTableByTab() {
        int index = tabbedPane.getSelectedIndex();
        if (index == 0) return activeTable;
        if (index == 1) return outOfStockTable;
        return expiredTable;
    }

    private static boolean isExpired(Medicine m, LocalDate today, DateTimeFormatter f) {
        try {
            if (m.getExpiryDate() != null && !m.getExpiryDate().isEmpty()) {
                LocalDate d = LocalDate.parse(m.getExpiryDate().trim(), f);
                return !d.isAfter(today);
            }
        } catch (Exception ignored) {}
        return false;
    }

    public void loadTableData() {
        List<Medicine> all = MedicineDAO.getAllMedicines();
        LocalDate today = LocalDate.now();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<Medicine> active = new ArrayList<>(), out = new ArrayList<>(), expired = new ArrayList<>();
        for (Medicine m : all) {
            if (isExpired(m, today, f)) {
                expired.add(m);
            } else if (m.getStock() <= 0) {
                out.add(m);
                active.add(m);
            } else {
                active.add(m);
            }
        }
        tableData.put(activeTable, active);
        tableData.put(outOfStockTable, out);
        tableData.put(expiredTable, expired);

        rebuild(activeTable);
        rebuild(outOfStockTable);
        rebuild(expiredTable);
    }

    /**
     * Rebuild one table: category rows (collapsible) + medicine rows + grand total row.
     * The category row only shows its subtotal (Quantity / Total Value) while collapsed,
     * so the numbers are not repeated when the medicine rows are visible.
     */
    private void rebuild(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        Set<String> closed = collapsed.get(table);
        LocalDate today = LocalDate.now();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Map<String, List<Medicine>> byCategory = new TreeMap<>();
        for (Medicine m : tableData.get(table)) {
            String cat = (m.getMedicineCategory() == null || m.getMedicineCategory().trim().isEmpty())
                    ? "Uncategorized" : m.getMedicineCategory().trim();
            byCategory.computeIfAbsent(cat, k -> new ArrayList<>()).add(m);
        }

        model.setRowCount(0);
        int grandQty = 0;
        double grandValue = 0;

        for (Map.Entry<String, List<Medicine>> entry : byCategory.entrySet()) {
            String cat = entry.getKey();
            int catQty = 0;
            double catValue = 0;
            for (Medicine m : entry.getValue()) {
                catQty += m.getStock();
                catValue += m.getStock() * m.getSellPrice();
            }
            grandQty += catQty;
            grandValue += catValue;

            boolean open = !closed.contains(cat);
            model.addRow(new Object[]{
                "", "", new CategoryLabel(cat, entry.getValue().size()), "", "",
                open ? "" : (Object) catQty,          // Quantity subtotal only when collapsed
                "", "", "",
                open ? "" : money(catValue),          // Total Value subtotal only when collapsed
                ""
            });

            if (!open) continue;
            for (Medicine m : entry.getValue()) {
                String status = isExpired(m, today, f) ? "Expired" : (m.getStock() <= 0 ? "Out of Stock" : "Available");
                model.addRow(new Object[]{
                    m.getId(),
                    m.getName(),
                    m.getMedicineCategory(),
                    money(m.getBuyPrice()),    // always 2 decimals so the amounts line up
                    money(m.getSellPrice()),
                    m.getStock(),
                    m.getCompanyName(),
                    m.getExpiryDate(),
                    status,
                    money(m.getStock() * m.getSellPrice()),
                    ""
                });
            }
        }

        model.addRow(new Object[]{"", "", "Total", "", "", grandQty, "", "", "", money(grandValue), ""});

        measureColumns(table);
        layoutColumns(table);
    }

    /** Measure the natural width of every column (its longest value) so nothing gets cut off. */
    private void measureColumns(JTable t) {
        DefaultTableModel model = (DefaultTableModel) t.getModel();
        FontMetrics fm = t.getFontMetrics(new Font("Segoe UI", Font.BOLD, 12));
        TableColumnModel cm = t.getColumnModel();
        int[] natural = new int[cm.getColumnCount()];

        for (int v = 0; v < cm.getColumnCount(); v++) {
            TableColumn col = cm.getColumn(v);
            int mc = col.getModelIndex();
            if (mc == COL_ACTION) {
                natural[v] = ACTION_WIDTH;
                continue;
            }

            int w = fm.stringWidth(String.valueOf(col.getHeaderValue()));
            for (int r = 0; r < model.getRowCount(); r++) {
                // the category cell is blank on medicine rows, so only measure the category rows
                if (mc == COL_CATEGORY && !isGroup(model, r)) continue;
                Object val = model.getValueAt(r, mc);
                if (val != null) w = Math.max(w, fm.stringWidth(val.toString()));
            }
            if (mc == COL_CATEGORY) w += 60; // room for chevron + item count
            natural[v] = w + CELL_PAD * 2 + 6;
        }
        naturalWidths.put(t, natural);
    }

    /**
     * Lay the columns out. When there is spare room, it is shared between the columns in
     * proportion to their natural width (so the spacing stays even). When the window is too
     * narrow, columns keep their natural width and the table scrolls sideways instead.
     * The Action column always stays at a fixed width.
     */
    private void layoutColumns(JTable t) {
        if (naturalWidths.get(t) == null) measureColumns(t);
        int[] natural = naturalWidths.get(t);
        TableColumnModel cm = t.getColumnModel();
        int n = cm.getColumnCount();
        if (natural.length != n) return;

        int flexTotal = 0;
        int fixedTotal = 0;
        int lastFlex = -1;
        for (int v = 0; v < n; v++) {
            if (cm.getColumn(v).getModelIndex() == COL_ACTION) {
                fixedTotal += natural[v];
            } else {
                flexTotal += natural[v];
                lastFlex = v;
            }
        }
        if (lastFlex == -1 || flexTotal == 0) return;

        Container vp = SwingUtilities.getAncestorOfClass(JViewport.class, t);
        int avail = vp == null ? 0 : vp.getWidth();
        int target = Math.max(avail, flexTotal + fixedTotal);
        double scale = (double) (target - fixedTotal) / flexTotal;

        t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int used = 0;
        for (int v = 0; v < n; v++) {
            TableColumn col = cm.getColumn(v);
            int w;
            if (col.getModelIndex() == COL_ACTION) {
                w = natural[v];
            } else if (v == lastFlex) {
                w = target - fixedTotal - used; // whatever is left, so rounding never leaves a gap
            } else {
                w = (int) Math.round(natural[v] * scale);
                used += w;
            }
            col.setMinWidth(15);
            col.setPreferredWidth(w);
            col.setWidth(w);
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        loadTableData();
    }

    // ------------------------------------------------------------ category row look

    /** Background for a row: white for medicines, soft tint for category / total rows, hover + selection aware. */
    private Color rowBackground(JTable table, int viewRow, boolean selected) {
        if (selected) return table.getSelectionBackground();
        DefaultTableModel m = (DefaultTableModel) table.getModel();
        int mr = table.convertRowIndexToModel(viewRow);
        if (!isGroup(m, mr)) return Color.WHITE;
        if (mr == m.getRowCount() - 1) return TOTAL_BG;
        Object hover = table.getClientProperty("hoverRow");
        return (hover instanceof Integer && (Integer) hover == viewRow) ? GROUP_HOVER : GROUP_BG;
    }

    private static String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /** Value stored in the category cell of a group row (name + how many medicines it holds). */
    private static class CategoryLabel {
        final String name;
        final int count;

        CategoryLabel(String name, int count) {
            this.name = name;
            this.count = count;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /** Small chevron drawn with vector lines: points down when open, right when collapsed. */
    private static class ChevronIcon implements Icon {
        private static final int SIZE = 14;
        private final boolean open;

        ChevronIcon(boolean open) {
            this.open = open;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.setColor(PRIMARY);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.translate(x, y);
            Path2D.Float p = new Path2D.Float();
            if (open) {            // v
                p.moveTo(3, 5);
                p.lineTo(7, 9);
                p.lineTo(11, 5);
            } else {               // >
                p.moveTo(5, 3);
                p.lineTo(9, 7);
                p.lineTo(5, 11);
            }
            g2.draw(p);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return SIZE;
        }

        @Override
        public int getIconHeight() {
            return SIZE;
        }
    }

    // ------------------------------------------------------------ grouped header

    /** Two-row table header: group label on top, column names below. */
    private static class GroupHeader extends JTableHeader {
        private final String[] groups;

        GroupHeader(TableColumnModel cm, String[] groups) {
            super(cm);
            this.groups = groups;
            setPreferredSize(new Dimension(0, 52));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(HEADER_BG);
            g.fillRect(0, 0, getWidth(), getHeight());

            int n = columnModel.getColumnCount();
            int h = getHeight();
            int half = h / 2;

            // the last column has no divider on its right (the scroll pane border is there)
            int last = n - 1;

            int i = 0;
            while (i < n) {
                String grp = i < groups.length ? groups[i] : null;
                if (grp == null) {
                    Rectangle r = getHeaderRect(i);
                    label(g, title(i), r.x, 0, r.width, h, align(i));
                    if (i != last) divider(g, r.x + r.width - 1, 0, h);
                    i++;
                } else {
                    int j = i;
                    while (j + 1 < n && j + 1 < groups.length && grp.equals(groups[j + 1])) j++;
                    Rectangle a = getHeaderRect(i), b = getHeaderRect(j);
                    int spanW = b.x + b.width - a.x;

                    // group label (top row) with a line under it
                    label(g, grp, a.x, 0, spanW, half, JLabel.CENTER);
                    g.setColor(LINE);
                    g.drawLine(a.x, half - 1, a.x + spanW - 1, half - 1);
                    if (j != last) divider(g, b.x + b.width - 1, 0, half);

                    // column names (bottom row)
                    for (int k = i; k <= j; k++) {
                        Rectangle r = getHeaderRect(k);
                        label(g, title(k), r.x, half, r.width, h - half, align(k));
                        if (k != last) divider(g, r.x + r.width - 1, half, h);
                    }
                    i = j + 1;
                }
            }

            // bottom line of the header
            g.setColor(LINE);
            g.drawLine(0, h - 1, getWidth(), h - 1);
            g.dispose();
        }

        private String title(int viewCol) {
            return String.valueOf(columnModel.getColumn(viewCol).getHeaderValue());
        }

        private int align(int viewCol) {
            return ALIGN[columnModel.getColumn(viewCol).getModelIndex()];
        }

        // drawn on the same pixel column as the vertical line of the cells below
        private void divider(Graphics2D g, int x, int y1, int y2) {
            g.setColor(LINE);
            g.drawLine(x, y1, x, y2);
        }

        private void label(Graphics2D g, String text, int x, int y, int w, int h, int alignment) {
            g.setColor(MUTED);
            g.setFont(getFont());
            FontMetrics fm = g.getFontMetrics();

            // same padding as the cells (the 1px line is on the right edge)
            int max = w - 1 - CELL_PAD * 2;
            String shown = text;
            if (fm.stringWidth(shown) > max) {
                while (shown.length() > 1 && fm.stringWidth(shown + "...") > max) {
                    shown = shown.substring(0, shown.length() - 1);
                }
                shown = shown.trim() + "...";
            }

            int textWidth = fm.stringWidth(shown);
            int tx;
            if (alignment == JLabel.CENTER) {
                tx = x + (w - 1 - textWidth) / 2;
            } else if (alignment == JLabel.RIGHT) {
                tx = x + w - 1 - CELL_PAD - textWidth;
            } else {
                tx = x + CELL_PAD;
            }
            int ty = y + (h + fm.getAscent() - fm.getDescent()) / 2;
            g.drawString(shown, tx, ty);
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

    class ActionButtonPanel extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

        private JPanel panel;
        private JPanel blank;
        private JButton btnEdit, btnDelete;

        public ActionButtonPanel() {
            // row is 33px tall, minus the 1px bottom line = 32 -> 3 + 26 + 3 keeps the buttons centered
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 3));
            panel.setOpaque(true);
            blank = new JPanel();
            blank.setOpaque(true);

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
                        if (isGroup(model, modelRow)) return;
                        try {
                            int id = Integer.parseInt(model.getValueAt(modelRow, COL_ID).toString());
                            String name = model.getValueAt(modelRow, COL_NAME).toString();
                            String category = model.getValueAt(modelRow, COL_CATEGORY).toString();
                            double buyPrice = Double.parseDouble(model.getValueAt(modelRow, COL_BUY).toString().replace("\u20B1", "").replace(",", "").trim());
                            double sellPrice = Double.parseDouble(model.getValueAt(modelRow, COL_SELL).toString().replace("\u20B1", "").replace(",", "").trim());
                            int stock = Integer.parseInt(model.getValueAt(modelRow, COL_QTY).toString().replaceAll("[^0-9\\-]", ""));
                            String company = model.getValueAt(modelRow, COL_COMPANY).toString();
                            String expiry = model.getValueAt(modelRow, COL_EXPIRE).toString();

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
                        if (isGroup(model, modelRow)) return;
                        int medicineId = Integer.parseInt(model.getValueAt(modelRow, COL_ID).toString());
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
            int mr = table.convertRowIndexToModel(row);
            DefaultTableModel tm = (DefaultTableModel) table.getModel();
            boolean lastColumn = column == table.getColumnCount() - 1;
            boolean total = mr == tm.getRowCount() - 1;

            if (isGroup(tm, mr)) {
                blank.setBackground(rowBackground(table, row, isSelected));
                blank.setBorder(lineBorder(total, true, !lastColumn, false));
                return blank;
            }
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            panel.setBorder(lineBorder(false, true, !lastColumn, false));
            return panel;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            panel.setBackground(table.getSelectionBackground());
            panel.setBorder(lineBorder(false, true, column != table.getColumnCount() - 1, false));
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

    // ------------------------------------------------------------ dialogs

    private static class CustomDialog {

        private static boolean confirmResult = false;

        private static JDialog buildDialog(Component parent, String message, String title, JButton... buttons) {
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
            for (JButton b : buttons) bottomPanel.add(b);
            dialog.add(bottomPanel, BorderLayout.SOUTH);

            dialog.pack();
            dialog.setSize(Math.max(dialog.getWidth() + 80, 520), Math.max(dialog.getHeight() + 40, 150));
            dialog.setLocationRelativeTo(parent);
            return dialog;
        }

        private static JButton button(String text, Color bg) {
            JButton b = new JButton(text);
            b.setFont(new Font("Segoe UI", Font.BOLD, 12));
            b.setBackground(bg);
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            b.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return b;
        }

        public static void showMessage(Component parent, String message, String title, boolean isWarning) {
            JButton btnOk = button("OK", new Color(26, 143, 136));
            JDialog dialog = buildDialog(parent, message, title, btnOk);
            btnOk.addActionListener(e -> dialog.dispose());
            dialog.setVisible(true);
        }

        public static boolean showConfirm(Component parent, String message, String title) {
            confirmResult = false;
            JButton btnYes = button("Yes", new Color(26, 143, 136));
            JButton btnNo = button("No", new Color(192, 57, 43));
            JDialog dialog = buildDialog(parent, message, title, btnYes, btnNo);
            btnYes.addActionListener(e -> { confirmResult = true; dialog.dispose(); });
            btnNo.addActionListener(e -> { confirmResult = false; dialog.dispose(); });
            dialog.setVisible(true);
            return confirmResult;
        }
    }
}