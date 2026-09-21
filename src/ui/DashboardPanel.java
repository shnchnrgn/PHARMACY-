package ui;

import db.MedicineDAO;
import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private final JLabel lblTotalProducts;
    private final JLabel lblLowStockCount;

    public DashboardPanel() {
        setLayout(new GridLayout(2, 1, 15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panel1 = new JPanel(new BorderLayout());
        panel1.setBorder(BorderFactory.createTitledBorder("Total Medicines in Inventory"));
        lblTotalProducts = new JLabel("0", JLabel.CENTER);
        lblTotalProducts.setFont(new Font("Arial", Font.BOLD, 32));
        panel1.add(lblTotalProducts, BorderLayout.CENTER);

        JPanel panel2 = new JPanel(new BorderLayout());
        panel2.setBorder(BorderFactory.createTitledBorder("Low Stock Items"));
        lblLowStockCount = new JLabel("0", JLabel.CENTER);
        lblLowStockCount.setFont(new Font("Arial", Font.BOLD, 32));
        lblLowStockCount.setForeground(new Color(200, 0, 0));
        panel2.add(lblLowStockCount, BorderLayout.CENTER);

        add(panel1);
        add(panel2);

        refreshDashboardData();
    }

    public void refreshDashboardData() {
        int total = MedicineDAO.getTotalMedicines();
        int lowStock = MedicineDAO.getLowStockCount();

        lblTotalProducts.setText(String.valueOf(total));
        lblLowStockCount.setText(String.valueOf(lowStock));
    }
}