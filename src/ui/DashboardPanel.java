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

public class DashboardPanel extends JPanel {

    private static final Color PAGE_BG = new Color(240, 242, 245);
    private static final Color SURFACE = Color.WHITE;
    private static final Color BORDER = new Color(220, 225, 230);
    private static final Color BORDER_LIGHT = new Color(235, 238, 242);
    private static final Color TEXT = new Color(60, 65, 70);
    private static final Color MUTED = new Color(108, 117, 125);
    private static final Color SUBTLE = new Color(151, 211, 208);
    private static final Color PRIMARY = new Color(13, 148, 136);
    private static final Color DANGER = new Color(192, 57, 43);
    private static final Color WARNING = new Color(243, 156, 18);
    private static final Color INFO = new Color(41, 128, 185);
    private static final Font SECTION = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    private static DefaultTableModel tableModel;
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
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        styleTable(table);
        table.setRowHeight(31);
        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(110);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(180);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(SURFACE);
        styleScrollBar(scrollPane.getVerticalScrollBar());
        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }
    private Border cardBorder() {
        return BorderFactory.createLineBorder(BORDER, 1);
    }

    private void styleTable(JTable table) {
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
}