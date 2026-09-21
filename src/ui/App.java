package ui;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame {
    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    public App() {
        setTitle("Pharmacy Management System");
        setSize(1250, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        mainContentPanel.add(new DashboardPanel(), "DASHBOARD");
        mainContentPanel.add(new POSFrame(), "POS");
        mainContentPanel.add(new MedicinePanel(), "MEDICINE");
        mainContentPanel.add(new CustomerPanel(), "CUSTOMERS");

        add(mainContentPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setBackground(new Color(33, 47, 61));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel lblLogo = new JLabel("  Pharmacy MS");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setMaximumSize(new Dimension(220, 60));
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblLogo);

        sidebar.add(createNavButton("Dashboard", e -> cardLayout.show(mainContentPanel, "DASHBOARD")));
        sidebar.add(createNavButton("Point Of Sales", e -> cardLayout.show(mainContentPanel, "POS")));
        sidebar.add(createNavButton("Medicine", e -> cardLayout.show(mainContentPanel, "MEDICINE")));
        sidebar.add(createNavButton("Customers", e -> cardLayout.show(mainContentPanel, "CUSTOMERS")));

        return sidebar;
    }

    private JButton createNavButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(220, 45));
        button.setPreferredSize(new Dimension(220, 45));
        button.setMinimumSize(new Dimension(220, 45));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(33, 47, 61));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        button.addActionListener(action);
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new App().setVisible(true);
        });
    }
}