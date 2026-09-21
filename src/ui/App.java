package ui;

import javax.swing.*;
import java.awt.*;
import db.DatabaseHelper;

public class App {
    private static InventoryPanel inventoryPanel;
    private static CustomerPanel customerPanel;

    public static void main(String[] args) {
        DatabaseHelper.connect();
        DatabaseHelper.createTables();

        SwingUtilities.invokeLater(() -> {
            openMainApplication();
        });
    }

    public static void openMainApplication() {
        JFrame frame = new JFrame("Pharmacy Management System - Bug Busters");
        frame.setSize(950, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel navPanel = new JPanel();
        JButton inventoryButton = new JButton("Inventory & Customers");
        JButton posButton = new JButton("POS & Sales");

        navPanel.add(inventoryButton);
        navPanel.add(posButton);
        mainPanel.add(navPanel, BorderLayout.NORTH);

        JPanel containerPanel = new JPanel(new CardLayout());

        inventoryPanel = new InventoryPanel();
        customerPanel = new CustomerPanel();

        JSplitPane mainDashboard = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            inventoryPanel,
            customerPanel
        );
        mainDashboard.setDividerLocation(320);

        containerPanel.add(mainDashboard, "MAIN_VIEW");
        mainPanel.add(containerPanel, BorderLayout.CENTER);

        inventoryButton.addActionListener(e -> {
            if (inventoryPanel != null) {
                inventoryPanel.loadTableData();
            }
            if (customerPanel != null) {
                customerPanel.loadAllCustomers();
            }
            CardLayout cl = (CardLayout)(containerPanel.getLayout());
            cl.show(containerPanel, "MAIN_VIEW");
            frame.toFront();
            frame.requestFocus();
        });

        posButton.addActionListener(e -> {
            POSFrame posFrame = new POSFrame();
            posFrame.setVisible(true);
        });

        frame.add(mainPanel);
        frame.setVisible(true);
    }
}