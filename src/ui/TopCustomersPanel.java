package ui;

import facade.MostPurchasedFacade;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class TopCustomersPanel extends JPanel {

    private static final Color PAGE_BG       = new Color(240, 242, 245);
    private static final Color TEXT_DARK     = new Color(40, 45, 50);
    private static final Color TEXT_MUTED    = new Color(110, 120, 130);
    private static final Color HEADER_BG     = new Color(245, 247, 250);
    private static final Color HEADER_TEXT   = new Color(70, 80, 90);
    private static final Color GRID_COLOR    = new Color(214, 220, 226);
    private static final Color BORDER_COLOR  = new Color(220, 225, 230);
    private static final Color ROW_ALT       = new Color(250, 251, 253);
    private static final Color ROW_SELECTED  = new Color(224, 242, 241);
    private static final Color ACCENT        = new Color(0, 137, 123);

    private static final int COL_NAME     = 0;
    private static final int COL_MEDICINE = 1;
    private static final int COL_UNITS    = 2;
    private static final int COL_TOTAL    = 3;

    private static final double[] COL_RATIO = {0.30, 0.30, 0.16, 0.24};

    private static final int[] COL_ALIGN = {
            JLabel.LEFT, JLabel.LEFT, JLabel.CENTER, JLabel.RIGHT
    };

    private static final int CELL_PADDING = 16;

    private JTable table;
    private JScrollPane scrollPane;
    private DefaultTableModel tableModel;
    private MostPurchasedFacade facade;

    public TopCustomersPanel() {
        facade = new MostPurchasedFacade();

        setLayout(new BorderLayout(0, 15));
        setBackground(PAGE_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);

        refreshData();
    }

    private JPanel buildHeader() {
        JLabel lblTitle = new JLabel("Top Customers");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(TEXT_DARK);

        JLabel lblSubtitle = new JLabel("Overview of highest spending customers");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(TEXT_MUTED);

        JPanel titleContainer = new JPanel();
        titleContainer.setLayout(new BoxLayout(titleContainer, BoxLayout.Y_AXIS));
        titleContainer.setOpaque(false);
        titleContainer.add(lblTitle);
        titleContainer.add(Box.createVerticalStrut(3));
        titleContainer.add(lblSubtitle);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(titleContainer, BorderLayout.WEST);
        return headerPanel;
    }

    private JPanel buildTable() {
        String[] columnNames = {"Customer Name", "Top Medicine", "Units Bought", "Total Spent"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT_DARK);
        table.setRowHeight(38);
        table.setFillsViewportHeight(false);
        table.setSelectionBackground(ROW_SELECTED);
        table.setSelectionForeground(TEXT_DARK);

        table.setShowGrid(true);
        table.setGridColor(GRID_COLOR);
        table.setIntercellSpacing(new Dimension(1, 1));

        JTableHeader header = table.getTableHeader();
        header.setResizingAllowed(false);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 40));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        setupColumns();

        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        scrollPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                applyColumnWidths(scrollPane.getViewport().getWidth());
            }
        });

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel fullWidthWrapper = new JPanel(new BorderLayout());
        fullWidthWrapper.setOpaque(false);
        fullWidthWrapper.add(tablePanel, BorderLayout.NORTH);
        return fullWidthWrapper;
    }

    private void setupColumns() {
        int last = table.getColumnCount() - 1;

        for (int i = 0; i <= last; i++) {
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setCellRenderer(new RowRenderer(COL_ALIGN[i], i == COL_TOTAL));
            col.setHeaderRenderer(new HeaderRenderer(COL_ALIGN[i], i == last));
        }

        applyColumnWidths(900); 
    }

    private void applyColumnWidths(int totalWidth) {
        if (totalWidth <= 0) return;

        int used = 0;
        int last = table.getColumnCount() - 1;

        for (int i = 0; i <= last; i++) {
            
            int w = (i == last) ? totalWidth - used : (int) Math.round(totalWidth * COL_RATIO[i]);
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setPreferredWidth(w);
            col.setWidth(w);
            used += w;
        }
    }

    private static class HeaderRenderer extends DefaultTableCellRenderer {
        private final Border border;

        HeaderRenderer(int alignment, boolean lastColumn) {
            setHorizontalAlignment(alignment);

            Border divider = new MatteBorder(0, 0, 0, lastColumn ? 0 : 1, GRID_COLOR);
            Border accentLine = new MatteBorder(0, 0, 2, 0, ACCENT);
            Border padding = new EmptyBorder(0, CELL_PADDING, 0, CELL_PADDING);

            border = BorderFactory.createCompoundBorder(
                    divider,
                    BorderFactory.createCompoundBorder(accentLine, padding));
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(t, value, false, false, row, column);
            setBackground(HEADER_BG);
            setForeground(HEADER_TEXT);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setBorder(border);
            return this;
        }
    }

    private static class RowRenderer extends DefaultTableCellRenderer {
        private final boolean highlight;

        RowRenderer(int alignment, boolean highlight) {
            this.highlight = highlight;
            setHorizontalAlignment(alignment);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(t, value, isSelected, false, row, column);

            setBorder(new EmptyBorder(0, CELL_PADDING, 0, CELL_PADDING));

            if (isSelected) {
                setBackground(ROW_SELECTED);
            } else {
                setBackground(row % 2 == 0 ? Color.WHITE : ROW_ALT);
            }

            if (highlight) {
                setFont(t.getFont().deriveFont(Font.BOLD));
                setForeground(ACCENT);
            } else {
                setFont(t.getFont());
                setForeground(TEXT_DARK);
            }
            return this;
        }
    }

    public void refreshData() {
        tableModel.setRowCount(0);

        String symbol = (SharedData.currencySymbol != null && !SharedData.currencySymbol.trim().isEmpty())
                ? SharedData.currencySymbol
                : "₱";

        List<String[]> topCustomers = facade.getTopSpendingCustomers(10);

        for (String[] row : topCustomers) {
            String customerName = row[0];
            String topMedicine = row[1];
            String unitsBought = row[2];
            String totalSpent = symbol + row[3];

            tableModel.addRow(new Object[]{customerName, topMedicine, unitsBought, totalSpent});
        }
    }
}