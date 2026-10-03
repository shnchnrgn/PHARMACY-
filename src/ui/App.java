package ui;

import db.Database;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class App extends JFrame {

    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private JPanel sidebar;
    private JPanel salesSubMenu;
    private JPanel medicineSubMenu;

    private MostPurchasedPanel mostPurchasedPanel;

    private boolean isSalesMenuOpen = true;
    private boolean isMedicineMenuOpen = true;
    private JLabel lblSalesArrow;
    private JLabel lblMedicineArrow;
    private JLabel lblWelcome;

    private JButton selectedButton;
    private final List<JButton> navigationButtons = new ArrayList<>();

    private static final Color SIDEBAR_COLOR = new Color(7, 25, 29);
    private static final Color SELECTED_COLOR = new Color(20, 57, 61);
    private static final Color HOVER_COLOR = new Color(24, 64, 68);
    private static final Color SUBMENU_COLOR = new Color(10, 42, 45);
    private static final Color TEXT_COLOR = new Color(238, 244, 244);
    private static final Color SUBTEXT_COLOR = new Color(151, 211, 208);
    private static final Color ACCENT_COLOR = new Color(75, 218, 210);
    private static final Color THEME_TEAL = new Color(13, 148, 136);

    public App() {
        Database.createTables();

        setTitle("Pharmacy Management System");
        setSize(1250, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 242, 245));

        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        mostPurchasedPanel = new MostPurchasedPanel();

        mainContentPanel.add(new DashboardPanel(), "DASHBOARD");
        mainContentPanel.add(new POSFrame(), "POS");
        mainContentPanel.add(new SalesPanel(), "SALES");
        mainContentPanel.add(mostPurchasedPanel, "MOST_PURCHASED");
        mainContentPanel.add(new MedicinePanel(), "MEDICINE_LIST");
        mainContentPanel.add(new AddMedicinePanel(), "ADD_MEDICINE");
        mainContentPanel.add(new MedicineCategoryPanel(), "MEDICINE_CATEGORY");
        mainContentPanel.add(new CustomerPanel(), "CUSTOMERS");
        mainContentPanel.add(new ExpensePanel(), "EXPENSES");
        mainContentPanel.add(new SystemSettingsPanel(), "SYSTEM_SETTINGS");

        sidebar = createSidebar();
        JPanel headerPanel = createHeaderPanel();

        JPanel contentContainer = new JPanel(new BorderLayout());
        contentContainer.add(headerPanel, BorderLayout.NORTH);
        contentContainer.add(mainContentPanel, BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
        add(contentContainer, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 230)),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        lblWelcome = new JLabel("Welcome to Pharmacy Management System");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblWelcome.setForeground(new Color(80, 90, 100));
        header.add(lblWelcome, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        int expiredCount = 0;

        JButton btnNotification = new JButton("Notifications (" + expiredCount + ")");
        if (expiredCount > 0) {
            btnNotification.setForeground(new Color(192, 57, 43));
        } else {
            btnNotification.setForeground(new Color(90, 100, 110));
        }
        btnNotification.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNotification.setFocusPainted(false);
        btnNotification.setBackground(new Color(245, 247, 250));
        btnNotification.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btnNotification.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        try {
            ImageIcon bellIcon = new ImageIcon(getClass().getResource("/ui/notification.png"));
            Image scaledBell = bellIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            btnNotification.setIcon(new ImageIcon(scaledBell));
            btnNotification.setIconTextGap(8);
        } catch (Exception e) {
            btnNotification.setText("🔔 Notifications (" + expiredCount + ")");
        }
        btnNotification.addActionListener(e -> {
            showCustomMessage(
                    this,
                    "<b>Expired Medicines Count:</b> " + expiredCount + "<br><br>Please check the Medicine List or Dashboard for full details.",
                    "System Notifications"
            );
        });
        
        JButton btnLogout = new JButton("Log out");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setForeground(new Color(192, 57, 43));
        btnLogout.setFocusPainted(false);
        btnLogout.setBackground(new Color(245, 247, 250));
        btnLogout.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        try {
            ImageIcon logoutIcon = new ImageIcon(getClass().getResource("/ui/logout.png"));
            Image scaledLogout = logoutIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            btnLogout.setIcon(new ImageIcon(scaledLogout));
            btnLogout.setIconTextGap(8);
        } catch (Exception e) {
            btnLogout.setText("🚪 Log out");
        }
        
        btnLogout.addActionListener(e -> {
            boolean confirmed = showCustomConfirm(
                this,
                "Are you sure you want to log out?",
                "Confirm Logout"
            );
            
            if (confirmed) {
                dispose();
                SwingUtilities.invokeLater(() -> {
                    LoginDialog loginDialog = new LoginDialog(null);
                    loginDialog.setVisible(true);
                    
                    if (loginDialog.isLoggedIn()) {
                        App app = new App();
                        app.setVisible(true);
                    } else {
                        System.exit(0);
                    }
                });
            }
        });
        
        rightPanel.add(btnNotification);
        rightPanel.add(btnLogout);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private void showCustomMessage(Component parent, String htmlMessage, String title) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        centerPanel.setBackground(Color.WHITE);

        JLabel lblMsg = new JLabel("<html>" + htmlMessage + "</html>");
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMsg.setForeground(new Color(70, 75, 80));
        centerPanel.add(lblMsg);

        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(new Color(248, 249, 250));
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 230)));

        JButton btnOk = new JButton("OK");
        btnOk.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnOk.setBackground(THEME_TEAL);
        btnOk.setForeground(Color.WHITE);
        btnOk.setFocusPainted(false);
        btnOk.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
        btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOk.addActionListener(ev -> dialog.dispose());

        bottomPanel.add(btnOk);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setSize(Math.max(dialog.getWidth() + 80, 480), Math.min(Math.max(dialog.getHeight() + 40, 160), 350));
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private final boolean[] logoutResult = {false};

    private boolean showCustomConfirm(Component parent, String message, String title) {
        logoutResult[0] = false;
        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        centerPanel.setBackground(Color.WHITE);

        JLabel lblMsg = new JLabel("<html>" + message + "</html>");
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMsg.setForeground(new Color(70, 75, 80));
        centerPanel.add(lblMsg);

        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(new Color(248, 249, 250));
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 230)));

        JButton btnYes = new JButton("Yes");
        btnYes.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnYes.setBackground(THEME_TEAL);
        btnYes.setForeground(Color.WHITE);
        btnYes.setFocusPainted(false);
        btnYes.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
        btnYes.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnNo = new JButton("No");
        btnNo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNo.setBackground(new Color(108, 117, 125));
        btnNo.setForeground(Color.WHITE);
        btnNo.setFocusPainted(false);
        btnNo.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
        btnNo.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnYes.addActionListener(ev -> {
            logoutResult[0] = true;
            dialog.dispose();
        });

        btnNo.addActionListener(ev -> {
            logoutResult[0] = false;
            dialog.dispose();
        });

        bottomPanel.add(btnYes);
        bottomPanel.add(btnNo);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setSize(Math.max(dialog.getWidth() + 80, 420), Math.min(Math.max(dialog.getHeight() + 40, 160), 300));
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

        return logoutResult[0];
    }

    private JPanel createSidebar() {
        JPanel sb = new JPanel();
        sb.setPreferredSize(new Dimension(240, getHeight()));
        sb.setBackground(SIDEBAR_COLOR);
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));

        JLabel lblLogo;
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/ui/VANGUARD LOGO HEADER.png"));
            Image image = originalIcon.getImage();
            int targetWidth = 205;
            int targetHeight = (int) ((double) image.getHeight(null) / image.getWidth(null) * targetWidth);
            Image scaledImage = image.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            lblLogo = new JLabel(new ImageIcon(scaledImage));
            lblLogo.setMaximumSize(new Dimension(240, targetHeight + 12));
            lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
            lblLogo.setBorder(BorderFactory.createEmptyBorder(8, 7, 8, 7));
        } catch (Exception e) {
            lblLogo = new JLabel("VANGUARD PHARMA");
            lblLogo.setForeground(ACCENT_COLOR);
            lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblLogo.setMaximumSize(new Dimension(240, 45));
            lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
            lblLogo.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        }

        sb.add(lblLogo);
        sb.add(Box.createVerticalStrut(4));

        JButton dashboardButton = createStyledNavButton("Dashboard", "/ui/dashboard.png");
        dashboardButton.addActionListener(e -> {
            selectButton((JButton) e.getSource());
            DashboardPanel.refreshDashboardData();
            cardLayout.show(mainContentPanel, "DASHBOARD");
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        });
        sb.add(dashboardButton);

        JButton posButton = createStyledNavButton("Point Of Sales", "/ui/pos.png");
        posButton.addActionListener(e -> {
            selectButton((JButton) e.getSource());
            cardLayout.show(mainContentPanel, "POS");
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        });
        sb.add(posButton);

        // Sales Dropdown Menu
        JButton salesButton = createSalesDropdownButton();
        sb.add(salesButton);

        salesSubMenu = new JPanel();
        salesSubMenu.setLayout(new BoxLayout(salesSubMenu, BoxLayout.Y_AXIS));
        salesSubMenu.setBackground(SUBMENU_COLOR);
        salesSubMenu.setMaximumSize(new Dimension(240, 92));
        salesSubMenu.setBorder(BorderFactory.createEmptyBorder(2, 7, 4, 7));
        salesSubMenu.setVisible(true);

        JButton salesListButton = createSubNavButton("Sales List", "/ui/pos.png");
        salesListButton.addActionListener(e -> {
            selectButton((JButton) e.getSource());
            cardLayout.show(mainContentPanel, "SALES");
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        });
        salesSubMenu.add(salesListButton);

        JButton mostPurchasedButton = createSubNavButton("Most Purchased", "/ui/medicine_list.png");
        mostPurchasedButton.addActionListener(e -> {
            selectButton((JButton) e.getSource());
            if (mostPurchasedPanel != null) {
                mostPurchasedPanel.refreshData();
            }
            cardLayout.show(mainContentPanel, "MOST_PURCHASED");
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        });
        salesSubMenu.add(mostPurchasedButton);
        sb.add(salesSubMenu);

        // Medicine Dropdown Menu
        JButton medicineButton = createMedicineDropdownButton();
        sb.add(medicineButton);

        medicineSubMenu = new JPanel();
        medicineSubMenu.setLayout(new BoxLayout(medicineSubMenu, BoxLayout.Y_AXIS));
        medicineSubMenu.setBackground(SUBMENU_COLOR);
        medicineSubMenu.setMaximumSize(new Dimension(240, 138));
        medicineSubMenu.setBorder(BorderFactory.createEmptyBorder(2, 7, 4, 7));
        medicineSubMenu.setVisible(true);

        JButton addMedicineButton = createSubNavButton("Add Medicine", "/ui/add_medicine.png");
        addMedicineButton.addActionListener(e -> {
            selectButton((JButton) e.getSource());
            cardLayout.show(mainContentPanel, "ADD_MEDICINE");
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        });
        medicineSubMenu.add(addMedicineButton);

        JButton medicineListButton = createSubNavButton("Medicine List", "/ui/medicine_list.png");
        medicineListButton.addActionListener(e -> {
            selectButton((JButton) e.getSource());
            cardLayout.show(mainContentPanel, "MEDICINE_LIST");
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        });
        medicineSubMenu.add(medicineListButton);

        JButton medicineCategoryButton = createSubNavButton("Medicine Category", "/ui/medicine_category.png");
        medicineCategoryButton.addActionListener(e -> {
            selectButton((JButton) e.getSource());
            cardLayout.show(mainContentPanel, "MEDICINE_CATEGORY");
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        });
        medicineSubMenu.add(medicineCategoryButton);
        sb.add(medicineSubMenu);

        JButton customerButton = createStyledNavButton("Customers", "/ui/customers.png");
        customerButton.addActionListener(e -> {
            selectButton((JButton) e.getSource());
            cardLayout.show(mainContentPanel, "CUSTOMERS");
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        });
        sb.add(customerButton);

        JButton expenseButton = createStyledNavButton("Expenses", "/ui/Expense.png");
        expenseButton.addActionListener(e -> {
            selectButton((JButton) e.getSource());
            cardLayout.show(mainContentPanel, "EXPENSES");
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        });
        sb.add(expenseButton);

        JButton settingsButton = createStyledNavButton("System Settings", "/ui/system_settings.png");
        settingsButton.addActionListener(e -> {
            selectButton((JButton) e.getSource());
            cardLayout.show(mainContentPanel, "SYSTEM_SETTINGS");
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        });
        sb.add(settingsButton);

        sb.add(Box.createVerticalGlue());

        SwingUtilities.invokeLater(() -> {
            selectButton(dashboardButton);
            DashboardPanel.refreshDashboardData();
            cardLayout.show(mainContentPanel, "DASHBOARD");
        });

        return sb;
    }

    private void selectButton(JButton button) {
        selectedButton = button;
        for (JButton navButton : navigationButtons) {
            navButton.repaint();
        }
        if (button != null) {
            button.repaint();
        }
    }

    private JButton createStyledNavButton(String text, String iconPath) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean selected = this == selectedButton;
                boolean hover = getModel().isRollover();

                if (selected) {
                    g2.setColor(SELECTED_COLOR);
                    g2.fillRoundRect(7, 3, getWidth() - 14, getHeight() - 6, 5, 5);
                    g2.setColor(ACCENT_COLOR);
                    g2.fillRoundRect(7, 8, 3, getHeight() - 16, 3, 3);
                } else if (hover) {
                    g2.setColor(HOVER_COLOR);
                    g2.fillRoundRect(7, 3, getWidth() - 14, getHeight() - 6, 5, 5);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        navigationButtons.add(button);
        button.setMaximumSize(new Dimension(240, 42));
        button.setPreferredSize(new Dimension(240, 42));
        button.setMinimumSize(new Dimension(240, 42));
        button.setForeground(TEXT_COLOR);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 0));

        if (iconPath != null && !iconPath.isEmpty()) {
            try {
                ImageIcon originalIcon = new ImageIcon(getClass().getResource(iconPath));
                Image scaledImg = originalIcon.getImage().getScaledInstance(17, 17, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaledImg));
                button.setIconTextGap(11);
            } catch (Exception ignored) {}
        }

        return button;
    }

    private JButton createSalesDropdownButton() {
        JButton button = new JButton();
        navigationButtons.add(button);
        button.setLayout(new GridBagLayout());
        button.setMaximumSize(new Dimension(240, 42));
        button.setPreferredSize(new Dimension(240, 42));
        button.setMinimumSize(new Dimension(240, 42));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/ui/pos.png"));
            Image scaledImg = originalIcon.getImage().getScaledInstance(17, 17, Image.SCALE_SMOOTH);
            JLabel lblIcon = new JLabel(new ImageIcon(scaledImg));
            gbc.gridx = 0;
            gbc.insets = new Insets(0, 0, 0, 11);
            button.add(lblIcon, gbc);
        } catch (Exception ignored) {}

        JLabel lblText = new JLabel("Sales");
        lblText.setForeground(TEXT_COLOR);
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        button.add(lblText, gbc);

        lblSalesArrow = new JLabel("^");
        lblSalesArrow.setForeground(ACCENT_COLOR);
        lblSalesArrow.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        button.add(lblSalesArrow, gbc);

        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                JButton b = (JButton) c;
                boolean selected = b == selectedButton;
                boolean hover = b.getModel().isRollover();

                if (selected) {
                    g2.setColor(SELECTED_COLOR);
                    g2.fillRoundRect(7, 3, b.getWidth() - 14, b.getHeight() - 6, 5, 5);
                    g2.setColor(ACCENT_COLOR);
                    g2.fillRoundRect(7, 8, 3, b.getHeight() - 16, 3, 3);
                } else if (hover) {
                    g2.setColor(HOVER_COLOR);
                    g2.fillRoundRect(7, 3, b.getWidth() - 14, b.getHeight() - 6, 5, 5);
                }
                g2.dispose();
                super.paint(g, c);
            }
        });

        button.addActionListener(e -> {
            selectButton((JButton) e.getSource());
            isSalesMenuOpen = !isSalesMenuOpen;
            salesSubMenu.setVisible(isSalesMenuOpen);
            lblSalesArrow.setText(isSalesMenuOpen ? "^" : "v");
            sidebar.revalidate();
            sidebar.repaint();
        });

        return button;
    }

    private JButton createMedicineDropdownButton() {
        JButton button = new JButton();
        navigationButtons.add(button);
        button.setLayout(new GridBagLayout());
        button.setMaximumSize(new Dimension(240, 42));
        button.setPreferredSize(new Dimension(240, 42));
        button.setMinimumSize(new Dimension(240, 42));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/ui/medicine.png"));
            Image scaledImg = originalIcon.getImage().getScaledInstance(17, 17, Image.SCALE_SMOOTH);
            JLabel lblIcon = new JLabel(new ImageIcon(scaledImg));
            gbc.gridx = 0;
            gbc.insets = new Insets(0, 0, 0, 11);
            button.add(lblIcon, gbc);
        } catch (Exception ignored) {}

        JLabel lblText = new JLabel("Medicine");
        lblText.setForeground(TEXT_COLOR);
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        button.add(lblText, gbc);

        lblMedicineArrow = new JLabel("^");
        lblMedicineArrow.setForeground(ACCENT_COLOR);
        lblMedicineArrow.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        button.add(lblMedicineArrow, gbc);

        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                JButton b = (JButton) c;
                boolean selected = b == selectedButton;
                boolean hover = b.getModel().isRollover();

                if (selected) {
                    g2.setColor(SELECTED_COLOR);
                    g2.fillRoundRect(7, 3, b.getWidth() - 14, b.getHeight() - 6, 5, 5);
                    g2.setColor(ACCENT_COLOR);
                    g2.fillRoundRect(7, 8, 3, b.getHeight() - 16, 3, 3);
                } else if (hover) {
                    g2.setColor(HOVER_COLOR);
                    g2.fillRoundRect(7, 3, b.getWidth() - 14, b.getHeight() - 6, 5, 5);
                }
                g2.dispose();
                super.paint(g, c);
            }
        });

        button.addActionListener(e -> {
            selectButton((JButton) e.getSource());
            isMedicineMenuOpen = !isMedicineMenuOpen;
            medicineSubMenu.setVisible(isMedicineMenuOpen);
            lblMedicineArrow.setText(isMedicineMenuOpen ? "^" : "v");
            sidebar.revalidate();
            sidebar.repaint();
        });

        return button;
    }

    private JButton createSubNavButton(String text, String iconPath) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean selected = this == selectedButton;
                boolean hover = getModel().isRollover();

                if (selected) {
                    g2.setColor(HOVER_COLOR);
                    g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 4, 4);
                    g2.setColor(ACCENT_COLOR);
                    g2.fillRoundRect(2, 7, 2, getHeight() - 14, 2, 2);
                } else if (hover) {
                    g2.setColor(HOVER_COLOR);
                    g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 4, 4);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        navigationButtons.add(button);
        button.setMaximumSize(new Dimension(240, 42));
        button.setPreferredSize(new Dimension(240, 42));
        button.setMinimumSize(new Dimension(240, 42));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setForeground(SUBTEXT_COLOR);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));

        if (iconPath != null && !iconPath.isEmpty()) {
            try {
                ImageIcon originalIcon = new ImageIcon(getClass().getResource(iconPath));
                Image scaledImg = originalIcon.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaledImg));
                button.setIconTextGap(10);
            } catch (Exception ignored) {}
        }

        return button;
    }

    public void updateWelcomeTitle(String newTitle) {
        if (lblWelcome != null) {
            lblWelcome.setText("Welcome to " + newTitle);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            LoginDialog loginDialog = new LoginDialog(null);
            loginDialog.setVisible(true);

            if (loginDialog.isLoggedIn()) {
                App app = new App();
                app.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}