package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private static JLabel lblSalesVal;
    private static JLabel lblSalesCountVal;
    private static DefaultTableModel tableModel;

    public DashboardPanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setPreferredSize(new Dimension(0, 100));

        lblSalesVal = new JLabel("₱ " + String.format("%.2f", SharedData.totalSalesToday));
        cardsPanel.add(createCard("Sales", lblSalesVal, "Total Sales Today", new Color(41, 128, 185)));
        cardsPanel.add(createCard("Expenses", "₱ 0.00", "Total Expenses Today", new Color(39, 174, 96)));
        cardsPanel.add(createCard("Medicines", "24", "Total Medicine In Store", new Color(230, 126, 34)));
        cardsPanel.add(createCard("Expired", "3", "Medicines Expired", new Color(231, 76, 60)));

        add(cardsPanel, BorderLayout.NORTH);

        JPanel mainCenterPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        mainCenterPanel.setOpaque(false);

        JPanel topCenterRow = new JPanel(new GridLayout(1, 2, 15, 0));
        topCenterRow.setOpaque(false);

        JPanel statsCard = createSectionCard("Statistics This Month");
        lblSalesCountVal = new JLabel("  Number Of Sales: " + SharedData.latestSalesList.size());
        lblSalesCountVal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statsCard.add(lblSalesCountVal, BorderLayout.CENTER);

        JPanel chartPlaceholder = createSectionCard("Sales & Profit Overview");
        chartPlaceholder.add(new JLabel(" [ Graph Placeholder ]", SwingConstants.CENTER), BorderLayout.CENTER);

        topCenterRow.add(statsCard);
        topCenterRow.add(chartPlaceholder);
        mainCenterPanel.add(topCenterRow);

        JPanel salesCard = createSectionCard("Latest Sales & Customer Details");
        String[] columns = {"Order No", "Date", "Amount", "Customer Name"};
        
        tableModel = new DefaultTableModel(columns, 0);
        SharedData.dashboardSalesModel = tableModel;
        
        for (String[] sale : SharedData.latestSalesList) {
            tableModel.addRow(sale);
        }

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        salesCard.add(scrollPane, BorderLayout.CENTER);

        mainCenterPanel.add(salesCard);

        add(mainCenterPanel, BorderLayout.CENTER);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        refreshDashboardData();
    }

    public static void refreshDashboardData() {
        if (lblSalesVal != null) {
            lblSalesVal.setText("₱ " + String.format("%.2f", SharedData.totalSalesToday));
        }
        if (lblSalesCountVal != null) {
            lblSalesCountVal.setText("  Number Of Sales: " + SharedData.latestSalesList.size());
        }
        if (tableModel != null) {
            tableModel.setRowCount(0);
            for (String[] sale : SharedData.latestSalesList) {
                tableModel.addRow(sale);
            }
        }
    }

    private JPanel createCard(String title, Object valueObj, String subtext, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230)),
            new EmptyBorder(10, 12, 10, 12)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(Color.DARK_GRAY);

        JComponent valueComponent;
        if (valueObj instanceof JLabel) {
            valueComponent = (JLabel) valueObj;
        } else {
            JLabel lblVal = new JLabel((String) valueObj);
            lblVal.setFont(new Font("Segoe UI", Font.BOLD, 20));
            lblVal.setForeground(new Color(40, 40, 40));
            valueComponent = lblVal;
        }

        if (valueComponent instanceof JLabel) {
            ((JLabel) valueComponent).setFont(new Font("Segoe UI", Font.BOLD, 20));
            ((JLabel) valueComponent).setForeground(new Color(40, 40, 40));
        }

        JLabel lblSub = new JLabel(subtext);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(Color.GRAY);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueComponent, BorderLayout.CENTER);
        card.add(lblSub, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createSectionCard(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230)),
            new EmptyBorder(8, 8, 8, 8)
        ));

        JLabel lblHeader = new JLabel(" " + title);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblHeader.setBorder(new EmptyBorder(0, 0, 8, 0));
        panel.add(lblHeader, BorderLayout.NORTH);

        return panel;
    }
}