package ui;

import facade.MostPurchasedFacade;

import java.awt.*;
import java.util.ArrayList;
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

public class MostPurchasedPanel extends JPanel {

    private static final Color PAGE_BG = new Color(240, 242, 245);
    private static final Color SURFACE = Color.WHITE;
    private static final Color BORDER = new Color(220, 225, 230);
    private static final Color BORDER_LIGHT = new Color(235, 238, 242);
    private static final Color TEXT = new Color(60, 65, 70);
    private static final Color MUTED = new Color(108, 117, 125);
    private static final Color BAR_COLOR = new Color(13, 148, 136);
    private static final Color PRIMARY_SOFT = new Color(224, 243, 241);
    private static final Font SECTION = new Font("Segoe UI", Font.BOLD, 13);

    private static final int CELL_PAD = 10; // same padding as the Medicine List page
    private static final int NUMBER_COL = 2; // quantity / units column (centered) in both tables

    private DefaultTableModel overallModel;
    private DefaultTableModel companyModel;
    private JTable overallTable;
    private JTable compTable;
    private BarChartPanel barChartPanel;

    private final MostPurchasedFacade salesFacade = new MostPurchasedFacade();

    public MostPurchasedPanel() {
        setLayout(new BorderLayout());
        setBackground(PAGE_BG);
        setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(new EmptyBorder(2, 2, 10, 2));
        container.setOpaque(false);

        // Header section
        JPanel pageHeader = new JPanel(new BorderLayout());
        pageHeader.setOpaque(false);
        JLabel pageTitle = new JLabel("Most Purchased Medicine");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 21));
        pageTitle.setForeground(TEXT);
        JLabel pageSubtitle = new JLabel("Sales analytics by popularity, overall trend, and manufacturer");
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
        container.add(Box.createVerticalStrut(14));

        // 1. Overall Table Card
        String[] overallCols = {"Medicine Name", "Company", "Total Quantity Sold"};
        overallModel = new DefaultTableModel(overallCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        overallTable = createStyledTable(overallModel);
        container.add(createTableCard("Top Purchased Medicines (Overall)", overallTable));
        container.add(Box.createVerticalStrut(14));

        // 2. Middle Panel (Bar Graph & Company Split View)
        JPanel middlePanel = new JPanel(new GridLayout(1, 2, 14, 0));
        middlePanel.setOpaque(false);

        // Bar Graph Panel
        barChartPanel = new BarChartPanel();
        middlePanel.add(createCustomCard("Top Purchased Medicines Chart", barChartPanel));

        // Company Table
        String[] compCols = {"Company", "Top Medicine", "Units Sold"};
        companyModel = new DefaultTableModel(compCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        compTable = createStyledTable(companyModel);
        middlePanel.add(createTableCard("Top Medicine by Company Name", compTable));

        container.add(middlePanel);

        JScrollPane mainScroll = new JScrollPane(container);
        mainScroll.setBorder(null);
        mainScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mainScroll.setBackground(PAGE_BG);
        mainScroll.getViewport().setBackground(PAGE_BG);
        styleScrollBar(mainScroll.getVerticalScrollBar());

        add(mainScroll, BorderLayout.CENTER);
        refreshData();
    }

    public void refreshData() {
        overallModel.setRowCount(0);
        companyModel.setRowCount(0);

        // Fetch overall data through Facade
        List<String[]> overallData = salesFacade.getTopPurchasedMedicines(10);
        List<BarChartData> chartDataList = new ArrayList<>();

        for (String[] row : overallData) {
            String name = row[0];
            String company = row[1];
            String qtyStr = row[2];

            overallModel.addRow(new Object[]{name, company, qtyStr});

            try {
                int qty = Integer.parseInt(qtyStr);
                chartDataList.add(new BarChartData(name, qty));
            } catch (NumberFormatException ignored) {}
        }
        barChartPanel.setData(chartDataList);

        // Fetch company table data through Facade
        List<String[]> companyData = salesFacade.getTopMedicinesByCompany();
        for (String[] row : companyData) {
            companyModel.addRow(row);
        }

        fitColumns(overallTable);
        fitColumns(compTable);
        updateResizeMode(overallTable);
        updateResizeMode(compTable);
    }

    private JPanel createTableCard(String cardTitle, JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(SURFACE);
        scrollPane.setPreferredSize(new Dimension(0, 220));

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

        return createCustomCard(cardTitle, scrollPane);
    }

    private JPanel createCustomCard(String cardTitle, JComponent content) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(SURFACE);
        card.setBorder(cardBorder());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT),
                new EmptyBorder(10, 14, 10, 14)));
        JLabel title = new JLabel(cardTitle);
        title.setFont(SECTION);
        title.setForeground(TEXT);
        header.add(title, BorderLayout.WEST);
        card.add(header, BorderLayout.NORTH);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    /** Table with the same look as the Medicine List page: fixed columns, padded cells, boxed header. */
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
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
                setHorizontalAlignment(mc == NUMBER_COL ? JLabel.CENTER : JLabel.LEFT);
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
        return table;
    }

    /** Size every column to its longest value so nothing is cut off. */
    private void fitColumns(JTable t) {
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
    private void updateResizeMode(JTable t) {
        int total = 0;
        TableColumnModel cm = t.getColumnModel();
        for (int i = 0; i < cm.getColumnCount(); i++) total += cm.getColumn(i).getPreferredWidth();
        Container vp = SwingUtilities.getAncestorOfClass(JViewport.class, t);
        int avail = vp == null ? 0 : vp.getWidth();
        t.setAutoResizeMode(avail > 0 && total > avail ? JTable.AUTO_RESIZE_OFF : JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    private Border cardBorder() {
        return BorderFactory.createLineBorder(BORDER, 1);
    }

    private void styleScrollBar(JScrollBar scrollBar) {
        scrollBar.setPreferredSize(new Dimension(8, 8));
        scrollBar.setBackground(new Color(248, 249, 250));
        scrollBar.setUI(new BasicScrollBarUI() {
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
        });
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
                boolean centered = columnModel.getColumn(i).getModelIndex() == NUMBER_COL;

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

    // ------------------------------------------------------------ bar chart

    private static class BarChartData {
        String label;
        int value;

        BarChartData(String label, int value) {
            this.label = label;
            this.value = value;
        }
    }

    private static class BarChartPanel extends JPanel {
        private List<BarChartData> data = new ArrayList<>();

        public BarChartPanel() {
            setBackground(SURFACE);
            setPreferredSize(new Dimension(0, 220));
        }

        public void setData(List<BarChartData> data) {
            this.data = data != null ? data : new ArrayList<>();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.setColor(MUTED);
                FontMetrics fm = g2.getFontMetrics();
                String msg = "No sales data available";
                g2.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int paddingLeft = 40;
            int paddingRight = 20;
            int paddingTop = 25;
            int paddingBottom = 40;

            int chartWidth = width - paddingLeft - paddingRight;
            int chartHeight = height - paddingTop - paddingBottom;

            int maxValue = 0;
            for (BarChartData item : data) {
                if (item.value > maxValue) {
                    maxValue = item.value;
                }
            }
            if (maxValue == 0) maxValue = 1;

            g2.setColor(BORDER_LIGHT);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            int gridCount = 4;
            for (int i = 0; i <= gridCount; i++) {
                int y = paddingTop + (chartHeight * i / gridCount);
                g2.drawLine(paddingLeft, y, width - paddingRight, y);

                int val = maxValue - (maxValue * i / gridCount);
                g2.setColor(MUTED);
                String valStr = String.valueOf(val);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(valStr, paddingLeft - fm.stringWidth(valStr) - 6, y + 4);
                g2.setColor(BORDER_LIGHT);
            }

            int itemCount = data.size();
            int barWidth = Math.max(12, Math.min(36, (chartWidth / itemCount) - 12));
            int gap = (chartWidth - (barWidth * itemCount)) / (itemCount + 1);

            for (int i = 0; i < itemCount; i++) {
                BarChartData item = data.get(i);
                int barHeight = (int) (((double) item.value / maxValue) * chartHeight);
                int x = paddingLeft + gap + i * (barWidth + gap);
                int y = paddingTop + (chartHeight - barHeight);

                g2.setColor(BAR_COLOR);
                g2.fillRoundRect(x, y, barWidth, barHeight, 4, 4);

                g2.setColor(TEXT);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                FontMetrics fm = g2.getFontMetrics();
                String valStr = String.valueOf(item.value);
                g2.drawString(valStr, x + (barWidth - fm.stringWidth(valStr)) / 2, y - 4);

                g2.setColor(MUTED);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                fm = g2.getFontMetrics();
                String label = item.label;
                if (fm.stringWidth(label) > barWidth + gap) {
                    while (label.length() > 3 && fm.stringWidth(label + "..") > barWidth + gap) {
                        label = label.substring(0, label.length() - 1);
                    }
                    label += "..";
                }
                g2.drawString(label, x + (barWidth - fm.stringWidth(label)) / 2, height - paddingBottom + 16);
            }

            g2.dispose();
        }
    }
}