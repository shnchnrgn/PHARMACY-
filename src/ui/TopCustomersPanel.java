package ui;

import facade.MostPurchasedFacade;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TopCustomersPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private MostPurchasedFacade facade;

    public TopCustomersPanel() {
        facade = new MostPurchasedFacade();

        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(240, 242, 245));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Top Customers");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(40, 45, 50));

        JLabel lblSubtitle = new JLabel("Overview of highest spending customers");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(110, 120, 130));

        JPanel titleContainer = new JPanel();
        titleContainer.setLayout(new BoxLayout(titleContainer, BoxLayout.Y_AXIS));
        titleContainer.setOpaque(false);
        titleContainer.add(lblTitle);
        titleContainer.add(Box.createVerticalStrut(3));
        titleContainer.add(lblSubtitle);

        headerPanel.add(titleContainer, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = {"Customer Name", "Top Medicine", "Units Bought", "Total Spent"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.setFillsViewportHeight(false); // Gitangtang ang pagpuno sa tibuok vertical space
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 235, 240));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(245, 247, 250));
        table.getTableHeader().setForeground(new Color(70, 80, 90));
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));

        table.getTableHeader().setResizingAllowed(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 230)));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Wrapper aron mapuno ang tibuok lapad apan dili mo-stretch paubos ang gitas-on sa puting box
        JPanel fullWidthWrapper = new JPanel(new BorderLayout());
        fullWidthWrapper.setOpaque(false);
        fullWidthWrapper.add(tablePanel, BorderLayout.NORTH);

        add(fullWidthWrapper, BorderLayout.CENTER);

        refreshData();
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