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

public class MostPurchasedPanel extends JPanel {

    private static final Color PAGE_BG = new Color(240, 242, 245);
    private static final Color SURFACE = Color.WHITE;
    private static final Color BORDER = new Color(220, 225, 230);
    private static final Color BORDER_LIGHT = new Color(235, 238, 242);
    private static final Color TEXT = new Color(60, 65, 70);
    private static final Color MUTED = new Color(108, 117, 125);
    private static final Color BAR_COLOR = new Color(13, 148, 136);
    private static final Font SECTION = new Font("Segoe UI", Font.BOLD, 13);

    private DefaultTableModel overallModel;
    private DefaultTableModel companyModel;
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
        overallModel = new DefaultTableModel(overallCols, 0);
        JTable overallTable = createStyledTable(overallModel);
        overallTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        alignColumnCenter(overallTable, 2);
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
        companyModel = new DefaultTableModel(compCols, 0);
        JTable compTable = createStyledTable(companyModel);
        alignColumnCenter(compTable, 2);
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
    }

    private JPanel createTableCard(String cardTitle, JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(SURFACE);
        scrollPane.setPreferredSize(new Dimension(0, 220));
        styleScrollBar(scrollPane.getVerticalScrollBar());

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

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setForeground(TEXT);
        table.setBackground(SURFACE);
        table.setRowHeight(32);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(BORDER_LIGHT);
        table.setSelectionBackground(new Color(224, 243, 241));
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.getTableHeader().setForeground(MUTED);
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        return table;
    }

    private void alignColumnCenter(JTable table, int columnIndex) {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(columnIndex).setCellRenderer(centerRenderer);
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