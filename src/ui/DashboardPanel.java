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

    private final Border grayBorder =
            BorderFactory.createLineBorder(new Color(200, 205, 210), 1);

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

    public DashboardPanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(new Color (255,255,255));
        setOpaque(true);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        25,
                        20,
                        25
                )
        );
        container.setOpaque(false);

        JPanel topCardsPanel =
                new JPanel(new GridLayout(1, 4, 15, 0));

        topCardsPanel.setOpaque(false);

        topCardsPanel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        110
                )
        );

        topCardsPanel.add(
                createMetricCard(
                        "Sales",
                        "₱ 0.00",
                        "Total Sales Today",
                        new Color(41, 128, 185)
                )
        );

        topCardsPanel.add(
                createMetricCard(
                        "Expenses",
                        "₱ 0.00",
                        "Total Expenses Today",
                        new Color(22, 160, 133)
                )
        );

        topCardsPanel.add(
                createMetricCard(
                        "Medicines",
                        String.valueOf(
                                MedicineDAO.getAllMedicines().size()
                        ),
                        "Total Medicine In Store",
                        new Color(243, 156, 18)
                )
        );

        topCardsPanel.add(
                createMetricCard(
                        "Expired",
                        String.valueOf(
                                MedicineDAO.getExpiredCount()
                        ),
                        "Medicines Expired In Store",
                        new Color(231, 76, 60)
                )
        );

        container.add(topCardsPanel);
        container.add(Box.createVerticalStrut(20));

        JPanel middlePanel =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                15,
                                0
                        )
                );

        middlePanel.setOpaque(false);

        middlePanel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        260
                )
        );

        middlePanel.add(createStatisticsCard());
        middlePanel.add(createGraphCard());

        container.add(middlePanel);
        container.add(Box.createVerticalStrut(20));

        container.add(createTableCard());

        JScrollPane mainScroll =
                new JScrollPane(container);

        mainScroll.setBorder(null);
        mainScroll.setBackground(
                new Color(240, 242, 245)
        );

        styleScrollBar(
                mainScroll.getVerticalScrollBar()
        );

        add(
                mainScroll,
                BorderLayout.CENTER
        );

        refreshDashboardData();
    }

    public static void refreshDashboardData() {

        double todaySales =
                SalesDAO.getTodaySalesTotal();

        double todayExpenses =
                SalesDAO.getTodayExpenseTotal();

        int monthSalesCount =
                SalesDAO.getThisMonthSalesCount();

        double monthSalesTotal =
                SalesDAO.getThisMonthSalesTotal();

        double monthSalesProfit =
                SalesDAO.getThisMonthSalesProfit();

        int monthExpenseCount =
                SalesDAO.getThisMonthExpenseCount();

        double monthExpenseTotal =
                SalesDAO.getThisMonthExpenseTotal();

        SharedData.totalSalesToday =
                todaySales;

        if (lblSalesVal != null) {

            lblSalesVal.setText(
                    String.format(
                            "₱ %.2f",
                            todaySales
                    )
            );
        }

        if (lblExpensesVal != null) {

            lblExpensesVal.setText(
                    String.format(
                            "₱ %.2f",
                            todayExpenses
                    )
            );
        }

        if (lblNumberOfSales != null) {

            lblNumberOfSales.setText(
                    String.valueOf(
                            monthSalesCount
                    )
            );
        }

        if (lblTotalSalesAmount != null) {

            lblTotalSalesAmount.setText(
                    String.format(
                            "₱ %.2f",
                            monthSalesTotal
                    )
            );
        }

        if (lblSalesProfit != null) {

            lblSalesProfit.setText(
                    String.format(
                            "₱ %.2f",
                            monthSalesProfit
                    )
            );
        }

        if (lblNumberOfExpenses != null) {

            lblNumberOfExpenses.setText(
                    String.valueOf(
                            monthExpenseCount
                    )
            );
        }

        if (lblTotalExpensesAmount != null) {

            lblTotalExpensesAmount.setText(
                    String.format(
                            "₱ %.2f",
                            monthExpenseTotal
                    )
            );
        }

        if (lblMedicineVal != null) {

            lblMedicineVal.setText(
                    String.valueOf(
                            MedicineDAO
                                    .getAllMedicines()
                                    .size()
                    )
            );
        }

        if (lblExpiredVal != null) {

            lblExpiredVal.setText(
                    String.valueOf(
                            MedicineDAO.getExpiredCount()
                    )
            );
        }

        if (tableModel != null) {

            tableModel.setRowCount(0);

            List<String[]> sales =
                    SalesDAO.getLatestSales(10);

            for (String[] sale : sales) {

                tableModel.addRow(
                        new Object[]{
                                sale[0],
                                sale[1],
                                "₱ " + sale[2],
                                sale[3]
                        }
                );
            }
        }
    }

    private JPanel createMetricCard(
            String title,
            String value,
            String subtitle,
            Color accentColor) {

        JPanel card =
                new JPanel(new BorderLayout());

        card.setBackground(Color.WHITE);
        card.setBorder(grayBorder);

        JPanel header =
                new JPanel(new BorderLayout());

        header.setBackground(
                new Color(248, 249, 250)
        );

        header.setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        1,
                        0,
                        new Color(220, 225, 230)
                )
        );

        JLabel lblTitle =
                new JLabel("  " + title);

        lblTitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        lblTitle.setForeground(
                new Color(90, 100, 110)
        );

        header.add(
                lblTitle,
                BorderLayout.WEST
        );

        JPanel accentBar =
                new JPanel();

        accentBar.setBackground(
                accentColor
        );

        accentBar.setPreferredSize(
                new Dimension(
                        5,
                        0
                )
        );

        card.add(
                accentBar,
                BorderLayout.WEST
        );

        JPanel content =
                new JPanel();

        content.setLayout(
                new BoxLayout(
                        content,
                        BoxLayout.Y_AXIS
                )
        );

        content.setBackground(Color.WHITE);

        content.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        15,
                        10,
                        15
                )
        );

        JLabel lblValue =
                new JLabel(value);

        if (title.equals("Sales")) {
            lblSalesVal = lblValue;
        }

        if (title.equals("Expenses")) {
            lblExpensesVal = lblValue;
        }

        if (title.equals("Medicines")) {
            lblMedicineVal = lblValue;
        }

        if (title.equals("Expired")) {
            lblExpiredVal = lblValue;
        }

        lblValue.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        22
                )
        );

        lblValue.setForeground(
                new Color(40, 50, 60)
        );

        JLabel lblSub =
                new JLabel(subtitle);

        lblSub.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );

        lblSub.setForeground(
                new Color(130, 140, 150)
        );

        content.add(lblValue);
        content.add(
                Box.createVerticalStrut(3)
        );
        content.add(lblSub);

        JPanel innerWrapper =
                new JPanel(new BorderLayout());

        innerWrapper.add(
                header,
                BorderLayout.NORTH
        );

        innerWrapper.add(
                content,
                BorderLayout.CENTER
        );

        innerWrapper.setBackground(
                Color.WHITE
        );

        card.add(
                innerWrapper,
                BorderLayout.CENTER
        );

        return card;
    }

    private JPanel createStatisticsCard() {

        JPanel card =
                new JPanel(new BorderLayout());

        card.setBackground(Color.WHITE);
        card.setBorder(grayBorder);

        JPanel header =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                15,
                                10
                        )
                );

        header.setBackground(
                new Color(248, 249, 250)
        );

        header.setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        1,
                        0,
                        new Color(220, 225, 230)
                )
        );

        JLabel lblTitle =
                new JLabel(
                        "Statistics This Month"
                );

        lblTitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        lblTitle.setForeground(
                new Color(80, 90, 100)
        );

        header.add(lblTitle);

        card.add(
                header,
                BorderLayout.NORTH
        );

        JPanel content =
                new JPanel();

        content.setLayout(
                new BoxLayout(
                        content,
                        BoxLayout.Y_AXIS
                )
        );

        content.setBackground(Color.WHITE);

        content.setBorder(
                BorderFactory.createEmptyBorder(
                        15,
                        20,
                        15,
                        20
                )
        );

        content.add(
                createStatRow(
                        "Number Of Sales",
                        "0",
                        "NUMBER_OF_SALES"
                )
        );

        content.add(
                createStatRow(
                        "Total Sales Amount",
                        "₱ 0.00",
                        "TOTAL_SALES_AMOUNT"
                )
        );

        content.add(
                createStatRow(
                        "Sales Profit",
                        "₱ 0.00",
                        "SALES_PROFIT"
                )
        );

        content.add(
                createStatRow(
                        "Number Of Expenses",
                        "0",
                        "NUMBER_OF_EXPENSES"
                )
        );

        content.add(
                createStatRow(
                        "Total Expenses Amount",
                        "₱ 0.00",
                        "TOTAL_EXPENSES_AMOUNT"
                )
        );

        card.add(
                content,
                BorderLayout.CENTER
        );

        return card;
    }

    private JPanel createStatRow(
            String label,
            String value,
            String type) {

        JPanel row =
                new JPanel(new BorderLayout());

        row.setBackground(Color.WHITE);

        row.setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        1,
                        0,
                        new Color(235, 240, 245)
                )
        );

        row.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        35
                )
        );

        JLabel lblName =
                new JLabel(label);

        lblName.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        lblName.setForeground(
                new Color(90, 100, 110)
        );

        JLabel lblVal =
                new JLabel(value);

        lblVal.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        lblVal.setForeground(
                new Color(40, 50, 60)
        );

        if (type.equals("NUMBER_OF_SALES")) {
            lblNumberOfSales = lblVal;
        }

        if (type.equals("TOTAL_SALES_AMOUNT")) {
            lblTotalSalesAmount = lblVal;
        }

        if (type.equals("SALES_PROFIT")) {
            lblSalesProfit = lblVal;
        }

        if (type.equals("NUMBER_OF_EXPENSES")) {
            lblNumberOfExpenses = lblVal;
        }

        if (type.equals("TOTAL_EXPENSES_AMOUNT")) {
            lblTotalExpensesAmount = lblVal;
        }

        row.add(
                lblName,
                BorderLayout.WEST
        );

        row.add(
                lblVal,
                BorderLayout.EAST
        );

        return row;
    }

    private JPanel createGraphCard() {

        JPanel card =
                new JPanel(new BorderLayout());

        card.setBackground(Color.WHITE);
        card.setBorder(grayBorder);

        JPanel header =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                15,
                                10
                        )
                );

        header.setBackground(
                new Color(248, 249, 250)
        );

        header.setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        1,
                        0,
                        new Color(220, 225, 230)
                )
        );

        JLabel lblTitle =
                new JLabel(
                        "Sales & Profit Overview"
                );

        lblTitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        lblTitle.setForeground(
                new Color(80, 90, 100)
        );

        header.add(lblTitle);

        card.add(
                header,
                BorderLayout.NORTH
        );

        JPanel graphPlaceholder =
                new JPanel() {

                    @Override
                    protected void paintComponent(
                            Graphics g) {

                        super.paintComponent(g);

                        Graphics2D g2d =
                                (Graphics2D) g;

                        g2d.setRenderingHint(
                                RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON
                        );

                        g2d.setColor(
                                new Color(
                                        150,
                                        160,
                                        170
                                )
                        );

                        g2d.setFont(
                                new Font(
                                        "Segoe UI",
                                        Font.ITALIC,
                                        12
                                )
                        );

                        String text =
                                "[ Unique Bar Graph Overview Placeholder ]";

                        FontMetrics fm =
                                g2d.getFontMetrics();

                        int x =
                                (
                                        getWidth()
                                        - fm.stringWidth(text)
                                ) / 2;

                        int y =
                                getHeight() / 2;

                        g2d.drawString(
                                text,
                                x,
                                y
                        );
                    }
                };

        graphPlaceholder.setBackground(
                Color.WHITE
        );

        card.add(
                graphPlaceholder,
                BorderLayout.CENTER
        );

        return card;
    }

    private JPanel createTableCard() {

        JPanel card =
                new JPanel(new BorderLayout());

        card.setBackground(Color.WHITE);
        card.setBorder(grayBorder);

        card.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        250
                )
        );

        JPanel header =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                15,
                                10
                        )
                );

        header.setBackground(
                new Color(248, 249, 250)
        );

        header.setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        1,
                        0,
                        new Color(220, 225, 230)
                )
        );

        JLabel lblTitle =
                new JLabel(
                        "Latest Sales & Customer Details"
                );

        lblTitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        lblTitle.setForeground(
                new Color(80, 90, 100)
        );

        header.add(lblTitle);

        card.add(
                header,
                BorderLayout.NORTH
        );

        String[] columns = {
                "Order No",
                "Date",
                "Amount",
                "Customer Name"
        };

        tableModel =
                new DefaultTableModel(
                        columns,
                        0
                );

        JTable table =
                new JTable(tableModel);

        table.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        table.setRowHeight(25);
        table.setShowHorizontalLines(true);

        table.setGridColor(
                new Color(
                        230,
                        235,
                        240
                )
        );

        table.setShowVerticalLines(false);

        table.setBorder(
                BorderFactory.createEmptyBorder()
        );

        DefaultTableCellRenderer headerRenderer =
                new DefaultTableCellRenderer();

        headerRenderer.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        headerRenderer.setBackground(
                new Color(
                        245,
                        247,
                        250
                )
        );

        headerRenderer.setForeground(
                new Color(
                        80,
                        90,
                        100
                )
        );

        headerRenderer.setHorizontalAlignment(
                JLabel.CENTER
        );

        headerRenderer.setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        1,
                        1,
                        new Color(
                                220,
                                225,
                                230
                        )
                )
        );

        table.getTableHeader()
                .setDefaultRenderer(
                        headerRenderer
                );

        JScrollPane scrollPane =
                new JScrollPane(table);

        scrollPane.setBorder(
                BorderFactory.createEmptyBorder()
        );

        scrollPane.setBackground(
                Color.WHITE
        );

        scrollPane.getViewport()
                .setBackground(
                        Color.WHITE
                );

        styleScrollBar(
                scrollPane.getVerticalScrollBar()
        );

        styleScrollBar(
                scrollPane.getHorizontalScrollBar()
        );

        card.add(
                scrollPane,
                BorderLayout.CENTER
        );

        return card;
    }

    private void styleScrollBar(
            JScrollBar scrollBar) {

        scrollBar.setPreferredSize(
                new Dimension(
                        8,
                        8
                )
        );

        scrollBar.setBackground(
                new Color(
                        248,
                        249,
                        250
                )
        );

        scrollBar.setUI(
                new BasicScrollBarUI() {

                    @Override
                    protected void configureScrollBarColors() {

                        this.thumbColor =
                                new Color(
                                        200,
                                        205,
                                        210
                                );

                        this.trackColor =
                                new Color(
                                        248,
                                        249,
                                        250
                                );

                        this.thumbHighlightColor =
                                new Color(
                                        200,
                                        205,
                                        210
                                );

                        this.thumbDarkShadowColor =
                                new Color(
                                        200,
                                        205,
                                        210
                                );

                        this.thumbLightShadowColor =
                                new Color(
                                        200,
                                        205,
                                        210
                                );
                    }

                    @Override
                    protected JButton createDecreaseButton(
                            int orientation) {

                        return createZeroButton();
                    }

                    @Override
                    protected JButton createIncreaseButton(
                            int orientation) {

                        return createZeroButton();
                    }

                    private JButton createZeroButton() {

                        JButton button =
                                new JButton();

                        button.setPreferredSize(
                                new Dimension(
                                        0,
                                        0
                                )
                        );

                        button.setMinimumSize(
                                new Dimension(
                                        0,
                                        0
                                )
                        );

                        button.setMaximumSize(
                                new Dimension(
                                        0,
                                        0
                                )
                        );

                        button.setBorder(
                                BorderFactory.createEmptyBorder()
                        );

                        return button;
                    }
                }
        );
    }
}