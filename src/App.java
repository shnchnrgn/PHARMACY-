import db.DatabaseHelper;
import java.awt.*;
import javax.swing.*;

public class App {
    public static void main(String[] args) {
        DatabaseHelper.connect();
        DatabaseHelper.createTables();

        JFrame frame = new JFrame("Pharmacy Management System - Bug Busters");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLayout(new BorderLayout());

        JPanel navPanel = new JPanel();
        JButton btnInventory = new JButton("Inventory & Customers");
        JButton btnPOS = new JButton("POS & Sales");
        navPanel.add(btnInventory);
        navPanel.add(btnPOS);

        frame.add(navPanel, BorderLayout.NORTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}