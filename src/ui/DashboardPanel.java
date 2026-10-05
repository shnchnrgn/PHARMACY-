package ui;

import db.MedicineDAO;
import db.SalesDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class DashboardPanel extends JPanel {

    private static final Color PAGE_BG = new Color(240, 242, 245);
    private static final Color SURFACE = Color.WHITE;
    private static final Color BORDER = new Color(220, 225, 230);
    private static final Color BORDER_LIGHT = new Color(235, 238, 242);
    private static final Color TEXT = new Color(60, 65, 70);
    private static final Color MUTED = new Color(108, 117, 125);
    private static final Color SUBTLE = new Color(151, 211, 208);
    private static final Color PRIMARY = new Color(13, 148, 136);
    private static final Color PRIMARY_SOFT = new Color(224, 243, 241);
    private static final Color DANGER = new Color(192, 57, 43);
    private static final Color WARNING = new Color(243, 156, 18);
    private static final Color INFO = new Color(41, 128, 185);
    private static final Font SECTION = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    private static final int CELL_PAD = 10; // same padding as the Medicine List page

    private static DefaultTableModel tableModel;
    private static JTable latestSalesTable;
    private static JLabel lblSalesVal;
    private static JLabel lblExpensesVal;
    private static JLabel lblMedicineVal;
    private static JLabel lblExpiredVal;
    private static JLabel lblNumberOfSales;
    private static JLabel lblTotalSalesAmount;
    private static JLabel lblSalesProfit;
    private static JLabel lblNumberOfExpenses;
    private static JLabel lblTotalExpensesAmount;
    private static JPanel graphPanel;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(PAGE_BG);
        setBorder(new EmptyBorder(16, 18, 16, 18));
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(new EmptyBorder(2, 2, 10, 2));
        container.setOpaque(false);

        JPanel pageHeader = new JPanel(new BorderLayout());
        pageHeader.setOpaque(false);
        JLabel pageTitle = new JLabel("Dashboard");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 21));
        pageTitle.setForeground(TEXT);
        JLabel pageSubtitle = new JLabel("Overview of today's pharmacy activity");
        pageSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        pageSubtitle.setForeground(MUTED);
        JPanel pageText = new JPanel();
        pageText.setOpaque(false);
        pageText.setLayout(new BoxLayout(pageText, BoxLayout.Y_AXIS));
        pageText.add(pageTitle);
        pageText.add(Box.createVerticalStrut(2));
        pageText.add(pageSubtitle);
        pageHeader.add(pageText, BorderLayout.WEST);
        pageHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        container.add(pageHeader);
        container.add(Box.createVerticalStrut(12));

        String sym = SharedData.currencySymbol + " ";

        JPanel topCardsPanel = new JPanel(new GridLayout(1, 4, 14, 0));
        topCardsPanel.setOpaque(false);
        topCardsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        topCardsPanel.add(createMetricCard("Sales", sym + "0.00", "Sales today", INFO));
        topCardsPanel.add(createMetricCard("Expenses", sym + "0.00", "Expenses today", WARNING));
        topCardsPanel.add(createMetricCard("Medicines", String.valueOf(MedicineDAO.getAllMedicines().size()), "Total medicines", INFO));
        topCardsPanel.add(createMetricCard("Expired", String.valueOf(MedicineDAO.getExpiredCount()), "Expired medicines", DANGER));
        container.add(topCardsPanel);
        container.add(Box.createVerticalStrut(14));
        JPanel middlePanel = new JPanel(new GridLayout(1, 2, 14, 0));
        middlePanel.setOpaque(false);
        middlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 230));
        middlePanel.add(createStatisticsCard());
        middlePanel.add(createGraphCard());
        container.add(middlePanel);
        container.add(Box.createVerticalStrut(14));
        container.add(createTableCard());
        JScrollPane mainScroll = new JScrollPane(container);
        mainScroll.setBorder(null);
        mainScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mainScroll.setBackground(PAGE_BG);
        mainScroll.getViewport().setBackground(PAGE_BG);
        styleScrollBar(mainScroll.getVerticalScrollBar());
        add(mainScroll, BorderLayout.CENTER);
        refreshDashboardData();
    }

    public static void refreshDashboardData() {
        double todaySales = SalesDAO.getTodaySalesTotal();
        double todayExpenses = SalesDAO.getTodayExpenseTotal();
        int monthSalesCount = SalesDAO.getThisMonthSalesCount();
        double monthSalesTotal = SalesDAO.getThisMonthSalesTotal();
        double monthSalesProfit = SalesDAO.getThisMonthSalesProfit();
        int monthExpenseCount = SalesDAO.getThisMonthExpenseCount();
        double monthExpenseTotal = SalesDAO.getThisMonthExpenseTotal();

        SharedData.totalSalesToday = todaySales;

        String sym = SharedData.currencySymbol + " ";

        if (lblSalesVal != null) {
            lblSalesVal.setText(String.format("%s%.2f", sym, todaySales));
        }
        if (lblExpensesVal != null) {
            lblExpensesVal.setText(String.format("%s%.2f", sym, todayExpenses));
        }
        if (lblNumberOfSales != null) {
            lblNumberOfSales.setText(String.valueOf(monthSalesCount));
        }
        if (lblTotalSalesAmount != null) {
            lblTotalSalesAmount.setText(String.format("%s%.2f", sym, monthSalesTotal));
        }
        if (lblSalesProfit != null) {
            lblSalesProfit.setText(String.format("%s%.2f", sym, monthSalesProfit));
        }
        if (lblNumberOfExpenses != null) {
            lblNumberOfExpenses.setText(String.valueOf(monthExpenseCount));
        }
        if (lblTotalExpensesAmount != null) {
            lblTotalExpensesAmount.setText(String.format("%s%.2f", sym, monthExpenseTotal));
        }
        if (lblMedicineVal != null) {
            lblMedicineVal.setText(String.valueOf(MedicineDAO.getAllMedicines().size()));
        }
        if (lblExpiredVal != null) {
            lblExpiredVal.setText(String.valueOf(MedicineDAO.getExpiredCount()));
        }
        if (graphPanel != null) {
            graphPanel.repaint();
        }
        if (tableModel != null) {
            tableModel.setRowCount(0);
            List<String[]> sales = SalesDAO.getLatestSales(10);
            for (String[] sale : sales) {
                tableModel.addRow(
                        new Object[]{
                                sale[0],
                                sale[1],
                                sym + sale[2],
                                sale[3]
                        }
                );
            }
            if (latestSalesTable != null) {
                fitColumns(latestSalesTable);
                updateResizeMode(latestSalesTable);
            }
        }
    }

    private JPanel createMetricCard(String title, String value, String subtitle, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(SURFACE);
        card.setBorder(cardBorder());
        JPanel accent = new JPanel();
        accent.setBackground(accentColor);
        accent.setPreferredSize(new Dimension(4, 0));
        card.add(accent, BorderLayout.WEST);
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(13, 15, 12, 15));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SMALL);
        titleLabel.setForeground(MUTED);
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(TEXT);
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subtitleLabel.setForeground(SUBTLE);
        if (title.equals("Sales")) lblSalesVal = valueLabel;
        if (title.equals("Expenses")) lblExpensesVal = valueLabel;
        if (title.equals("Medicines")) lblMedicineVal = valueLabel;
        if (title.equals("Expired")) lblExpiredVal = valueLabel;
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(valueLabel);
        content.add(Box.createVerticalStrut(2));
        content.add(subtitleLabel);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createStatisticsCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(SURFACE);
        card.setBorder(cardBorder());
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT),
                new EmptyBorder(10, 14, 10, 14)));
        JLabel title = new JLabel("Monthly Summary");
        title.setFont(SECTION);
        title.setForeground(TEXT);
        header.add(title, BorderLayout.WEST);
        card.add(header, BorderLayout.NORTH);
        JPanel content = new JPanel();
        content.setBackground(SURFACE);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(6, 14, 8, 14));
        
        String sym = SharedData.currencySymbol + " ";
        
        content.add(createStatRow("Number of sales", "0", "NUMBER_OF_SALES"));
        content.add(createStatRow("Sales amount", sym + "0.00", "TOTAL_SALES_AMOUNT"));
        content.add(createStatRow("Sales profit", sym + "0.00", "SALES_PROFIT"));
        content.add(createStatRow("Number of expenses", "0", "NUMBER_OF_EXPENSES"));
        content.add(createStatRow("Expenses amount", sym + "0.00", "TOTAL_EXPENSES_AMOUNT"));
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createStatRow(String label, String value, String type) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(SURFACE);
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        JLabel name = new JLabel(label);
        name.setFont(SMALL);
        name.setForeground(MUTED);
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 12));
        val.setForeground(TEXT);
        if (type.equals("NUMBER_OF_SALES")) lblNumberOfSales = val;
        if (type.equals("TOTAL_SALES_AMOUNT")) lblTotalSalesAmount = val;
        if (type.equals("SALES_PROFIT")) lblSalesProfit = val;
        if (type.equals("NUMBER_OF_EXPENSES")) lblNumberOfExpenses = val;
        if (type.equals("TOTAL_EXPENSES_AMOUNT")) lblTotalExpensesAmount = val;
        row.add(name, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        return row;
    }

    private JPanel createGraphCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(SURFACE);
        card.setBorder(cardBorder());
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT),
                new EmptyBorder(10, 14, 10, 14)));
        JLabel title = new JLabel("Monthly Overview");
        title.setFont(SECTION);
        title.setForeground(TEXT);
        header.add(title, BorderLayout.WEST);
        card.add(header, BorderLayout.NORTH);
        graphPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int left = 34;
                int top = 18;
                int right = 16;
                int bottom = 30;
                int width = getWidth() - left - right;
                int height = getHeight() - top - bottom;
                if (width <= 0 || height <= 0) {
                    g2.dispose();
                    return;
                }
                for (int i = 0; i < 4; i++) {
                    int y = top + (height * i / 3);
                    g2.setColor(BORDER_LIGHT);
                    g2.drawLine(left, y, getWidth() - right, y);
                }
                double[] values = {
                        SalesDAO.getThisMonthSalesTotal(),
                        SalesDAO.getThisMonthSalesProfit(),
                        SalesDAO.getThisMonthExpenseTotal()
                };
                String[] labels = {"Sales", "Profit", "Expenses"};
                Color[] colors = {PRIMARY, INFO, WARNING};
                double max = Math.max(1.0, Math.max(values[0], Math.max(values[1], values[2])));
                int gap = 18;
                int barWidth = Math.max(34, (width - gap * 4) / 3);
                for (int i = 0; i < values.length; i++) {
                    int x = left + gap + i * (barWidth + gap);
                    int barHeight = (int) Math.round((values[i] / max) * Math.max(4, height - 4));
                    int y = top + height - barHeight;
                    g2.setColor(colors[i]);
                    g2.fillRoundRect(x, y, barWidth, Math.max(4, barHeight), 8, 8);
                    
                    String amount = String.format("%s %.0f", SharedData.currencySymbol, values[i]);
                    
                    g2.setColor(MUTED);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(amount, x + (barWidth - fm.stringWidth(amount)) / 2, Math.max(12, y - 4));
                    g2.setColor(TEXT);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    fm = g2.getFontMetrics();
                    g2.drawString(labels[i], x + (barWidth - fm.stringWidth(labels[i])) / 2, getHeight() - 10);
                }
                g2.dispose();
            }
        };
        graphPanel.setBackground(SURFACE);
        card.add(graphPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(SURFACE);
        card.setBorder(cardBorder());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 245));
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT),
                new EmptyBorder(10, 14, 10, 14)));
        JLabel title = new JLabel("Latest Sales");
        title.setFont(SECTION);
        title.setForeground(TEXT);
        header.add(title, BorderLayout.WEST);
        card.add(header, BorderLayout.NORTH);
        String[] columns = {"Order No", "Date", "Amount", "Customer Name"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        latestSalesTable = table;
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(SURFACE);

        // same thin scrollbars as the Medicine List page
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
                updateResizeMode(table);
            }
        });

        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private Border cardBorder() {
        return BorderFactory.createLineBorder(BORDER, 1);
    }

    /** Table with the same look as the Medicine List page: fixed columns, padded cells, boxed header. */
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setForeground(TEXT);
        table.setBackground(SURFACE);
        table.setRowHeight(33);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setGridColor(BORDER_LIGHT);
        table.setSelectionBackground(PRIMARY_SOFT);
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);

        JTableHeader header = new StyledHeader(table.getColumnModel(), 36);
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        table.setTableHeader(header);

        // fixed columns: can't be dragged to resize or reorder
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(t, value, isSelected, false, row, column);
                int mc = t.convertColumnIndexToModel(column);
                setBorder(new EmptyBorder(0, CELL_PAD, 0, CELL_PAD));
                setHorizontalAlignment(isCenteredColumn(mc) ? JLabel.CENTER : JLabel.LEFT);
                setFont(new Font("Segoe UI", Font.PLAIN, 12));
                setForeground(TEXT);
                if (isSelected) {
                    setBackground(t.getSelectionBackground());
                    setForeground(t.getSelectionForeground());
                } else {
                    setBackground(Color.WHITE);
                }
                return this;
            }
        };
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        int[] widths = {90, 110, 100, 180};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    /** Order No, Date and Amount are centered; Customer Name is left aligned. */
    private static boolean isCenteredColumn(int modelCol) {
        return modelCol <= 2;
    }

    /** Size every column to its longest value so nothing is cut off. */
    private static void fitColumns(JTable t) {
        DefaultTableModel model = (DefaultTableModel) t.getModel();
        FontMetrics fm = t.getFontMetrics(new Font("Segoe UI", Font.BOLD, 12));
        TableColumnModel cm = t.getColumnModel();
        for (int v = 0; v < cm.getColumnCount(); v++) {
            TableColumn col = cm.getColumn(v);
            int mc = col.getModelIndex();

            int w = fm.stringWidth(String.valueOf(col.getHeaderValue()));
            for (int r = 0; r < model.getRowCount(); r++) {
                Object val = model.getValueAt(r, mc);
                if (val != null) w = Math.max(w, fm.stringWidth(val.toString()));
            }
            w += CELL_PAD * 2 + 6;
            col.setMinWidth(w);
            col.setPreferredWidth(w);
        }
    }

    /** Fill the width when there is room, scroll sideways when the columns do not fit. */
    private static void updateResizeMode(JTable t) {
        int total = 0;
        TableColumnModel cm = t.getColumnModel();
        for (int i = 0; i < cm.getColumnCount(); i++) total += cm.getColumn(i).getPreferredWidth();
        Container vp = SwingUtilities.getAncestorOfClass(JViewport.class, t);
        int avail = vp == null ? 0 : vp.getWidth();
        t.setAutoResizeMode(avail > 0 && total > avail ? JTable.AUTO_RESIZE_OFF : JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    private void styleScrollBar(JScrollBar scrollBar) {
        scrollBar.setPreferredSize(new Dimension(8, 8));
        scrollBar.setBackground(new Color(248, 249, 250));
        scrollBar.setUI(
                new BasicScrollBarUI() {
                    @Override
                    protected void configureScrollBarColors() {
                        this.thumbColor = new Color(200, 205, 210);
                        this.trackColor = new Color(248, 249, 250);
                        this.thumbHighlightColor = new Color(200, 205, 210);
                        this.thumbDarkShadowColor = new Color(200, 205, 210);
                        this.thumbLightShadowColor = new Color(200, 205, 210);
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
                        JButton button = new JButton();
                        button.setPreferredSize(new Dimension(0, 0));
                        button.setMinimumSize(new Dimension(0, 0));
                        button.setMaximumSize(new Dimension(0, 0));
                        button.setBorder(BorderFactory.createEmptyBorder());
                        return button;
                    }
                }
        );
    }

    // ------------------------------------------------------------ header

    /** Table header with the same look as the Medicine List page (single row, no groups). */
    private static class StyledHeader extends JTableHeader {

        StyledHeader(TableColumnModel cm, int height) {
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
                boolean centered = isCenteredColumn(columnModel.getColumn(i).getModelIndex());

                g.setColor(new Color(248, 250, 252));
                g.fillRect(r.x, 0, r.width, h);
                g.setColor(BORDER);
                g.drawRect(r.x, 0, r.width - 1, h - 1);
                g.setColor(MUTED);
                g.setFont(getFont());
                FontMetrics fm = g.getFontMetrics();
                int tx = centered ? r.x + (r.width - fm.stringWidth(text)) / 2 : r.x + CELL_PAD;
                int ty = (h + fm.getAscent() - fm.getDescent()) / 2;
                g.drawString(text, tx, ty);
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
}