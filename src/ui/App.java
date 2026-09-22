package ui;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame {
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private JPanel sidebar;
    private JPanel medicineSubMenu;
    private boolean isMedicineMenuOpen = true;
    private JLabel lblArrow;

    public App() {
        setTitle("Pharmacy Management System");
        setSize(1250, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        sidebar = createSidebar();
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
        JPanel sb = new JPanel();
        sb.setPreferredSize(new Dimension(240, getHeight()));
        sb.setBackground(new Color(24, 34, 45));
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));

        JLabel lblLogo = new JLabel(" Vanguard Pharmacy MS");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setMaximumSize(new Dimension(240, 60));
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sb.add(lblLogo);

        sb.add(createStyledNavButton("Dashboard", e -> cardLayout.show(mainContentPanel, "DASHBOARD")));
        sb.add(createStyledNavButton("Point Of Sales", e -> cardLayout.show(mainContentPanel, "POS")));

        sb.add(createMedicineDropdownButton());

        medicineSubMenu = new JPanel();
        medicineSubMenu.setLayout(new BoxLayout(medicineSubMenu, BoxLayout.Y_AXIS));
        medicineSubMenu.setBackground(new Color(24, 34, 45));
        medicineSubMenu.setMaximumSize(new Dimension(240, 135));
        medicineSubMenu.setVisible(true);

        medicineSubMenu.add(createSubNavButton("   + Add Medicine", e -> cardLayout.show(mainContentPanel, "ADD_MEDICINE")));
        medicineSubMenu.add(createSubNavButton("   -  Medicine List", e -> cardLayout.show(mainContentPanel, "MEDICINE_LIST")));
        medicineSubMenu.add(createSubNavButton("   +  Medicine Category", e -> cardLayout.show(mainContentPanel, "MEDICINE_CATEGORY")));

        sb.add(medicineSubMenu);

        sb.add(createStyledNavButton("Customers", e -> cardLayout.show(mainContentPanel, "CUSTOMERS")));

        return sb;
    }

    private JButton createStyledNavButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color colorTop = new Color(52, 73, 94);
                Color colorBottom = new Color(41, 57, 75);
                GradientPaint gp = new GradientPaint(0, 0, colorTop, 0, getHeight(), colorBottom);
                g2d.setPaint(gp);
                g2d.fillRoundRect(5, 3, getWidth() - 10, getHeight() - 6, 6, 6);
                
                g2d.setColor(new Color(70, 90, 110));
                g2d.drawRoundRect(5, 3, getWidth() - 10, getHeight() - 6, 6, 6);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setMaximumSize(new Dimension(240, 42));
        button.setPreferredSize(new Dimension(240, 42));
        button.setMinimumSize(new Dimension(240, 42));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        button.addActionListener(action);
        return button;
    }

    private JButton createMedicineDropdownButton() {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color colorTop = new Color(52, 73, 94);
                Color colorBottom = new Color(41, 57, 75);
                GradientPaint gp = new GradientPaint(0, 0, colorTop, 0, getHeight(), colorBottom);
                g2d.setPaint(gp);
                g2d.fillRoundRect(5, 3, getWidth() - 10, getHeight() - 6, 6, 6);
                
                g2d.setColor(new Color(70, 90, 110));
                g2d.drawRoundRect(5, 3, getWidth() - 10, getHeight() - 6, 6, 6);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setLayout(new BorderLayout());
        button.setMaximumSize(new Dimension(240, 42));
        button.setPreferredSize(new Dimension(240, 42));
        button.setMinimumSize(new Dimension(240, 42));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        JLabel lblText = new JLabel("Medicine");
        lblText.setForeground(Color.WHITE);
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        lblArrow = new JLabel("^");
        lblArrow.setForeground(Color.WHITE);
        lblArrow.setFont(new Font("Segoe UI", Font.BOLD, 12));

        button.add(lblText, BorderLayout.WEST);
        button.add(lblArrow, BorderLayout.EAST);

        button.addActionListener(e -> {
            isMedicineMenuOpen = !isMedicineMenuOpen;
            medicineSubMenu.setVisible(isMedicineMenuOpen);
            if (isMedicineMenuOpen) {
                lblArrow.setText("^");
            } else {
                lblArrow.setText("v");
            }
            sidebar.revalidate();
            sidebar.repaint();
        });

        return button;
    }

    private JButton createSubNavButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color colorTop = new Color(41, 57, 75);
                Color colorBottom = new Color(33, 47, 61);
                GradientPaint gp = new GradientPaint(0, 0, colorTop, 0, getHeight(), colorBottom);
                g2d.setPaint(gp);
                g2d.fillRoundRect(10, 2, getWidth() - 15, getHeight() - 4, 5, 5);
                
                g2d.setColor(new Color(60, 80, 100));
                g2d.drawRoundRect(10, 2, getWidth() - 15, getHeight() - 4, 5, 5);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setMaximumSize(new Dimension(240, 38));
        button.setPreferredSize(new Dimension(240, 38));
        button.setForeground(new Color(210, 215, 220));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
        button.addActionListener(action);
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
            }

            new App().setVisible(true);
        });
    }
}