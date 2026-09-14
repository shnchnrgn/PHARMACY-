package ui;

import javax.swing.*;
import java.awt.*;
import db.DatabaseHelper;

public class App {
    public static void main(String[] args) {
        DatabaseHelper.connect();
        DatabaseHelper.createTables();

        javax.swing.SwingUtilities.invokeLater(() -> {
            openMainApplication(); // Diretso na bubukas ang main dashboard
        });
    }

    public static void openMainApplication() {
        JFrame frame = new JFrame("Pharmacy Management System - Bug Busters");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLayout(new BorderLayout());

        CardLayout cardLayout = new CardLayout();
        JPanel mainContentPanel = new JPanel(cardLayout);

        InventoryPanel inventoryPanel = new InventoryPanel();
        CustomerPanel customerPanel = new CustomerPanel();
        
        JPanel inventoryAndCustomerSplit = new JPanel(new GridLayout(2, 1));
        inventoryAndCustomerSplit.add(inventoryPanel);
        inventoryAndCustomerSplit.add(customerPanel);

        JPanel posPlaceholder = new JPanel();
        posPlaceholder.add(new JLabel("POS & Sales Module (Under Development)"));

        mainContentPanel.add(inventoryAndCustomerSplit, "INVENTORY");
        mainContentPanel.add(posPlaceholder, "POS");

        JPanel navPanel = new JPanel();
        JButton btnInventory = new JButton("Inventory & Customers");
        JButton btnPOS = new JButton("POS & Sales");

        btnInventory.addActionListener(e -> cardLayout.show(mainContentPanel, "INVENTORY"));
        btnPOS.addActionListener(e -> cardLayout.show(mainContentPanel, "POS"));

        navPanel.add(btnInventory);
        navPanel.add(btnPOS);

        frame.add(navPanel, BorderLayout.NORTH);
        frame.add(mainContentPanel, BorderLayout.CENTER);
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}



