package ui;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame {
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private JPanel medicineSubMenu;
    private boolean isMedicineMenuOpen = false;

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
        mainContentPanel.add(new MedicinePanel(), "MEDICINE_LIST");
        mainContentPanel.add(new AddMedicinePanel(), "ADD_MEDICINE");
        mainContentPanel.add(new MedicineCategoryPanel(), "MEDICINE_CATEGORY");
        mainContentPanel.add(new CustomerPanel(), "CUSTOMERS");

        add(mainContentPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setBackground(new Color(33, 47, 61));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel lblLogo = new JLabel("  Pharmacy MS");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setMaximumSize(new Dimension(240, 60));
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblLogo);

        sidebar.add(createNavButton("Dashboard", e -> cardLayout.show(mainContentPanel, "DASHBOARD")));
        sidebar.add(createNavButton("Point Of Sales", e -> cardLayout.show(mainContentPanel, "POS")));

        sidebar.add(createMedicineDropdownButton());

        medicineSubMenu = new JPanel();
        medicineSubMenu.setLayout(new BoxLayout(medicineSubMenu, BoxLayout.Y_AXIS));
        medicineSubMenu.setBackground(new Color(25, 35, 45));
        medicineSubMenu.setMaximumSize(new Dimension(240, 135));
        medicineSubMenu.setVisible(false);

        medicineSubMenu.add(createSubNavButton("   + Add Medicine", e -> cardLayout.show(mainContentPanel, "ADD_MEDICINE")));
        medicineSubMenu.add(createSubNavButton("   -  Medicine List", e -> cardLayout.show(mainContentPanel, "MEDICINE_LIST")));
        medicineSubMenu.add(createSubNavButton("   +  Medicine Category", e -> cardLayout.show(mainContentPanel, "MEDICINE_CATEGORY")));

        sidebar.add(medicineSubMenu);

        sidebar.add(createNavButton("Customers", e -> cardLayout.show(mainContentPanel, "CUSTOMERS")));

        return sidebar;
    }

    private JButton createNavButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(240, 45));
        button.setPreferredSize(new Dimension(240, 45));
        button.setMinimumSize(new Dimension(240, 45));
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

    private JButton createMedicineDropdownButton() {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setMaximumSize(new Dimension(240, 45));
        button.setPreferredSize(new Dimension(240, 45));
        button.setMinimumSize(new Dimension(240, 45));
        button.setBackground(new Color(33, 47, 61));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblText = new JLabel("   Medicine");
        lblText.setForeground(Color.WHITE);
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel lblArrow = new JLabel("v  ");
        lblArrow.setForeground(Color.WHITE);
        lblArrow.setFont(new Font("Segoe UI", Font.BOLD, 12));

        button.add(lblText, BorderLayout.WEST);
        button.add(lblArrow, BorderLayout.EAST);

        button.addActionListener(e -> {
            isMedicineMenuOpen = !isMedicineMenuOpen;
            medicineSubMenu.setVisible(isMedicineMenuOpen);
            revalidate();
            repaint();
        });

        return button;
    }

    private JButton createSubNavButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(240, 38));
        button.setPreferredSize(new Dimension(240, 38));
        button.setForeground(new Color(180, 190, 200));
        button.setBackground(new Color(25, 35, 45));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
            }
            
            Font segoeFont = new Font("Segoe UI", Font.PLAIN, 12);
            java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = UIManager.get(key);
                if (value instanceof javax.swing.plaf.FontUIResource) {
                    UIManager.put(key, new javax.swing.plaf.FontUIResource(segoeFont));
                }
            }

            new App().setVisible(true);
        });
    }
}