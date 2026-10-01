package ui;

import db.Database;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class App extends JFrame {

    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private JPanel sidebar;
    private JPanel medicineSubMenu;

    private boolean isMedicineMenuOpen = true;
    private JLabel lblArrow;

    private JButton selectedButton;
    private final List<JButton> navigationButtons = new ArrayList<>();

    private static final Color SIDEBAR_COLOR =
            new Color(7, 25, 29);

    private static final Color SELECTED_COLOR =
            new Color(20, 57, 61);

    private static final Color HOVER_COLOR =
            new Color(24, 64, 68);

    private static final Color SUBMENU_COLOR =
            new Color(10, 42, 45);

    private static final Color TEXT_COLOR =
            new Color(238, 244, 244);

    private static final Color SUBTEXT_COLOR =
            new Color(151, 211, 208);

    private static final Color ACCENT_COLOR =
            new Color(75, 218, 210);

    public App() {

        Database.createTables();

        setTitle("Pharmacy Management System");
        setSize(1250, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        cardLayout = new CardLayout();

        mainContentPanel =
                new JPanel(cardLayout);

        mainContentPanel.add(
                new DashboardPanel(),
                "DASHBOARD"
        );

        mainContentPanel.add(
                new POSFrame(),
                "POS"
        );

        mainContentPanel.add(
                new SalesPanel(),
                "SALES"
        );

        mainContentPanel.add(
                new MedicinePanel(),
                "MEDICINE_LIST"
        );

        mainContentPanel.add(
                new AddMedicinePanel(),
                "ADD_MEDICINE"
        );

        mainContentPanel.add(
                new MedicineCategoryPanel(),
                "MEDICINE_CATEGORY"
        );

        mainContentPanel.add(
                new CustomerPanel(),
                "CUSTOMERS"
        );

        mainContentPanel.add(
                new ExpensePanel(),
                "EXPENSES"
        );

        mainContentPanel.add(
                new SystemSettingsPanel(),
                "SYSTEM_SETTINGS"
        );

        sidebar = createSidebar();

        add(
                sidebar,
                BorderLayout.WEST
        );

        add(
                mainContentPanel,
                BorderLayout.CENTER
        );
    }

    private JPanel createSidebar() {

        JPanel sb = new JPanel();

        sb.setPreferredSize(
                new Dimension(
                        240,
                        getHeight()
                )
        );

        sb.setBackground(
                SIDEBAR_COLOR
        );

        sb.setLayout(
                new BoxLayout(
                        sb,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel lblLogo;

        try {

            ImageIcon originalIcon =
                    new ImageIcon(
                            getClass().getResource(
                                    "/ui/VANGUARD LOGO HEADER.png"
                            )
                    );

            Image image =
                    originalIcon.getImage();

            int originalWidth =
                    image.getWidth(null);

            int originalHeight =
                    image.getHeight(null);

            int targetWidth = 205;

            int targetHeight =
                    (int) (
                            (double) originalHeight
                                    / originalWidth
                                    * targetWidth
                    );

            Image scaledImage =
                    image.getScaledInstance(
                            targetWidth,
                            targetHeight,
                            Image.SCALE_SMOOTH
                    );

            lblLogo =
                    new JLabel(
                            new ImageIcon(
                                    scaledImage
                            )
                    );

            lblLogo.setMaximumSize(
                    new Dimension(
                            240,
                            targetHeight + 12
                    )
            );

            lblLogo.setAlignmentX(
                    Component.LEFT_ALIGNMENT
            );

            lblLogo.setBorder(
                    BorderFactory.createEmptyBorder(
                            8,
                            7,
                            8,
                            7
                    )
            );

        } catch (Exception e) {

            lblLogo =
                    new JLabel(
                            "VANGUARD PHARMA"
                    );

            lblLogo.setForeground(
                    ACCENT_COLOR
            );

            lblLogo.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            16
                    )
            );

            lblLogo.setMaximumSize(
                    new Dimension(
                            240,
                            45
                    )
            );

            lblLogo.setAlignmentX(
                    Component.LEFT_ALIGNMENT
            );

            lblLogo.setBorder(
                    BorderFactory.createEmptyBorder(
                            8,
                            12,
                            8,
                            12
                    )
            );
        }

        sb.add(lblLogo);

        sb.add(
                Box.createVerticalStrut(4)
        );

        JButton dashboardButton =
                createStyledNavButton(
                        "Dashboard",
                        "/ui/dashboard.png"
                );

        dashboardButton.addActionListener(
                e -> {

                    selectButton(
                            (JButton) e.getSource()
                    );

                    DashboardPanel.refreshDashboardData();

                    cardLayout.show(
                            mainContentPanel,
                            "DASHBOARD"
                    );

                    mainContentPanel.revalidate();
                    mainContentPanel.repaint();
                }
        );

        sb.add(dashboardButton);

        JButton posButton =
                createStyledNavButton(
                        "Point Of Sales",
                        "/ui/pos.png"
                );

        posButton.addActionListener(
                e -> {

                    selectButton(
                            (JButton) e.getSource()
                    );

                    cardLayout.show(
                            mainContentPanel,
                            "POS"
                    );

                    mainContentPanel.revalidate();
                    mainContentPanel.repaint();
                }
        );

        sb.add(posButton);

        JButton salesButton =
                createStyledNavButton(
                        "Sales",
                        "/ui/pos.png"
                );

        salesButton.addActionListener(
                e -> {

                    selectButton(
                            (JButton) e.getSource()
                    );

                    cardLayout.show(
                            mainContentPanel,
                            "SALES"
                    );

                    mainContentPanel.revalidate();
                    mainContentPanel.repaint();
                }
        );

        sb.add(salesButton);

        JButton medicineButton =
                createMedicineDropdownButton();

        sb.add(medicineButton);

        medicineSubMenu =
                new JPanel();

        medicineSubMenu.setLayout(
                new BoxLayout(
                        medicineSubMenu,
                        BoxLayout.Y_AXIS
                )
        );

        medicineSubMenu.setBackground(
                SUBMENU_COLOR
        );

        medicineSubMenu.setMaximumSize(
                new Dimension(
                        240,
                        138
                )
        );

        medicineSubMenu.setBorder(
                BorderFactory.createEmptyBorder(
                        2,
                        7,
                        4,
                        7
                )
        );

        medicineSubMenu.setVisible(true);

        JButton addMedicineButton =
                createSubNavButton(
                        "Add Medicine",
                        "/ui/add_medicine.png"
                );

        addMedicineButton.addActionListener(
                e -> {

                    selectButton(
                            (JButton) e.getSource()
                    );

                    cardLayout.show(
                            mainContentPanel,
                            "ADD_MEDICINE"
                    );

                    mainContentPanel.revalidate();
                    mainContentPanel.repaint();
                }
        );

        medicineSubMenu.add(
                addMedicineButton
        );

        JButton medicineListButton =
                createSubNavButton(
                        "Medicine List",
                        "/ui/medicine_list.png"
                );

        medicineListButton.addActionListener(
                e -> {

                    selectButton(
                            (JButton) e.getSource()
                    );

                    cardLayout.show(
                            mainContentPanel,
                            "MEDICINE_LIST"
                    );

                    mainContentPanel.revalidate();
                    mainContentPanel.repaint();
                }
        );

        medicineSubMenu.add(
                medicineListButton
        );

        JButton medicineCategoryButton =
                createSubNavButton(
                        "Medicine Category",
                        "/ui/medicine_category.png"
                );

        medicineCategoryButton.addActionListener(
                e -> {

                    selectButton(
                            (JButton) e.getSource()
                    );

                    cardLayout.show(
                            mainContentPanel,
                            "MEDICINE_CATEGORY"
                    );

                    mainContentPanel.revalidate();
                    mainContentPanel.repaint();
                }
        );

        medicineSubMenu.add(
                medicineCategoryButton
        );

        sb.add(
                medicineSubMenu
        );

        JButton customerButton =
                createStyledNavButton(
                        "Customers",
                        "/ui/customers.png"
                );

        customerButton.addActionListener(
                e -> {

                    selectButton(
                            (JButton) e.getSource()
                    );

                    cardLayout.show(
                            mainContentPanel,
                            "CUSTOMERS"
                    );

                    mainContentPanel.revalidate();
                    mainContentPanel.repaint();
                }
        );

        sb.add(customerButton);

        JButton expenseButton =
                createStyledNavButton(
                        "Expenses",
                        "/ui/Expense.png"
                );

        expenseButton.addActionListener(
                e -> {

                    selectButton(
                            (JButton) e.getSource()
                    );

                    cardLayout.show(
                            mainContentPanel,
                            "EXPENSES"
                    );

                    mainContentPanel.revalidate();
                    mainContentPanel.repaint();
                }
        );

        sb.add(expenseButton);

        JButton settingsButton =
                createStyledNavButton(
                        "System Settings",
                        "/ui/system_settings.png"
                );

        settingsButton.addActionListener(
                e -> {

                    selectButton(
                            (JButton) e.getSource()
                    );

                    cardLayout.show(
                            mainContentPanel,
                            "SYSTEM_SETTINGS"
                    );

                    mainContentPanel.revalidate();
                    mainContentPanel.repaint();
                }
        );

        sb.add(settingsButton);

        sb.add(
                Box.createVerticalGlue()
        );

        SwingUtilities.invokeLater(
                () -> {

                    selectButton(
                            dashboardButton
                    );

                    DashboardPanel.refreshDashboardData();

                    cardLayout.show(
                            mainContentPanel,
                            "DASHBOARD"
                    );
                }
        );

        return sb;
    }

    private void selectButton(
            JButton button
    ) {

        selectedButton = button;

        for (
                JButton navButton :
                navigationButtons
        ) {

            navButton.repaint();
        }

        if (button != null) {
            button.repaint();
        }
    }

    private JButton createStyledNavButton(
            String text,
            String iconPath
    ) {

        JButton button =
                new JButton(text) {

            @Override
            protected void paintComponent(
                    Graphics g
            ) {

                Graphics2D g2 =
                        (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                boolean selected =
                        this == selectedButton;

                boolean hover =
                        getModel().isRollover();

                if (selected) {

                    g2.setColor(
                            SELECTED_COLOR
                    );

                    g2.fillRoundRect(
                            7,
                            3,
                            getWidth() - 14,
                            getHeight() - 6,
                            5,
                            5
                    );

                    g2.setColor(
                            ACCENT_COLOR
                    );

                    g2.fillRoundRect(
                            7,
                            8,
                            3,
                            getHeight() - 16,
                            3,
                            3
                    );

                } else if (hover) {

                    g2.setColor(
                            HOVER_COLOR
                    );

                    g2.fillRoundRect(
                            7,
                            3,
                            getWidth() - 14,
                            getHeight() - 6,
                            5,
                            5
                    );
                }

                g2.dispose();

                super.paintComponent(g);
            }
        };

        navigationButtons.add(button);

        button.setMaximumSize(
                new Dimension(
                        240,
                        42
                )
        );

        button.setPreferredSize(
                new Dimension(
                        240,
                        42
                )
        );

        button.setMinimumSize(
                new Dimension(
                        240,
                        42
                )
        );

        button.setForeground(
                TEXT_COLOR
        );

        button.setOpaque(false);

        button.setContentAreaFilled(false);

        button.setFocusPainted(false);

        button.setBorderPainted(false);

        button.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        button.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        14,
                        0,
                        0
                )
        );

        if (
                iconPath != null &&
                !iconPath.isEmpty()
        ) {

            try {

                ImageIcon originalIcon =
                        new ImageIcon(
                                getClass().getResource(
                                        iconPath
                                )
                        );

                Image scaledImg =
                        originalIcon
                                .getImage()
                                .getScaledInstance(
                                        17,
                                        17,
                                        Image.SCALE_SMOOTH
                                );

                button.setIcon(
                        new ImageIcon(
                                scaledImg
                        )
                );

                button.setIconTextGap(11);

            } catch (Exception e) {
            }
        }

        return button;
    }

    private JButton createMedicineDropdownButton() {

        JButton button =
                new JButton();

        navigationButtons.add(button);

        button.setLayout(
                new GridBagLayout()
        );

        button.setMaximumSize(
                new Dimension(
                        240,
                        42
                )
        );

        button.setPreferredSize(
                new Dimension(
                        240,
                        42
                )
        );

        button.setMinimumSize(
                new Dimension(
                        240,
                        42
                )
        );

        button.setOpaque(false);

        button.setContentAreaFilled(false);

        button.setFocusPainted(false);

        button.setBorderPainted(false);

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        14,
                        0,
                        12
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.gridy = 0;

        gbc.anchor =
                GridBagConstraints.CENTER;

        try {

            ImageIcon originalIcon =
                    new ImageIcon(
                            getClass().getResource(
                                    "/ui/medicine.png"
                            )
                    );

            Image scaledImg =
                    originalIcon
                            .getImage()
                            .getScaledInstance(
                                    17,
                                    17,
                                    Image.SCALE_SMOOTH
                            );

            JLabel lblIcon =
                    new JLabel(
                            new ImageIcon(
                                    scaledImg
                            )
                    );

            gbc.gridx = 0;

            gbc.insets =
                    new Insets(
                            0,
                            0,
                            0,
                            11
                    );

            button.add(
                    lblIcon,
                    gbc
            );

        } catch (Exception e) {
        }

        JLabel lblText =
                new JLabel("Medicine");

        lblText.setForeground(
                TEXT_COLOR
        );

        lblText.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        gbc.gridx = 1;

        gbc.weightx = 1.0;

        gbc.anchor =
                GridBagConstraints.WEST;

        button.add(
                lblText,
                gbc
        );

        lblArrow =
                new JLabel("^");

        lblArrow.setForeground(
                ACCENT_COLOR
        );

        lblArrow.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );

        gbc.gridx = 2;

        gbc.weightx = 0.0;

        gbc.anchor =
                GridBagConstraints.EAST;

        button.add(
                lblArrow,
                gbc
        );

        button.setUI(
                new javax.swing.plaf.basic.BasicButtonUI() {

            @Override
            public void paint(
                    Graphics g,
                    JComponent c
            ) {

                Graphics2D g2 =
                        (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                JButton b =
                        (JButton) c;

                boolean selected =
                        b == selectedButton;

                boolean hover =
                        b.getModel().isRollover();

                if (selected) {

                    g2.setColor(
                            SELECTED_COLOR
                    );

                    g2.fillRoundRect(
                            7,
                            3,
                            b.getWidth() - 14,
                            b.getHeight() - 6,
                            5,
                            5
                    );

                    g2.setColor(
                            ACCENT_COLOR
                    );

                    g2.fillRoundRect(
                            7,
                            8,
                            3,
                            b.getHeight() - 16,
                            3,
                            3
                    );

                } else if (hover) {

                    g2.setColor(
                            HOVER_COLOR
                    );

                    g2.fillRoundRect(
                            7,
                            3,
                            b.getWidth() - 14,
                            b.getHeight() - 6,
                            5,
                            5
                    );
                }

                g2.dispose();

                super.paint(
                        g,
                        c
                );
            }
        });

        button.addActionListener(
                e -> {

                    selectButton(
                            (JButton) e.getSource()
                    );

                    isMedicineMenuOpen =
                            !isMedicineMenuOpen;

                    medicineSubMenu.setVisible(
                            isMedicineMenuOpen
                    );

                    if (isMedicineMenuOpen) {

                        lblArrow.setText("^");

                    } else {

                        lblArrow.setText("v");
                    }

                    sidebar.revalidate();
                    sidebar.repaint();
                }
        );

        return button;
    }

    private JButton createSubNavButton(
            String text,
            String iconPath
    ) {

        JButton button =
                new JButton(text) {

            @Override
            protected void paintComponent(
                    Graphics g
            ) {

                Graphics2D g2 =
                        (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                boolean selected =
                        this == selectedButton;

                boolean hover =
                        getModel().isRollover();

                if (selected) {

                    g2.setColor(
                            HOVER_COLOR
                    );

                    g2.fillRoundRect(
                            2,
                            2,
                            getWidth() - 4,
                            getHeight() - 4,
                            4,
                            4
                    );

                    g2.setColor(
                            ACCENT_COLOR
                    );

                    g2.fillRoundRect(
                            2,
                            7,
                            2,
                            getHeight() - 14,
                            2,
                            2
                    );

                } else if (hover) {

                    g2.setColor(
                            HOVER_COLOR
                    );

                    g2.fillRoundRect(
                            2,
                            2,
                            getWidth() - 4,
                            getHeight() - 4,
                            4,
                            4
                    );
                }

                g2.dispose();

                super.paintComponent(g);
            }
        };

        navigationButtons.add(button);

        button.setMaximumSize(
                new Dimension(
                        240,
                        42
                )
        );

        button.setPreferredSize(
                new Dimension(
                        240,
                        42
                )
        );

        button.setMinimumSize(
                new Dimension(
                        240,
                        42
                )
        );

        button.setOpaque(false);

        button.setContentAreaFilled(false);

        button.setFocusPainted(false);

        button.setBorderPainted(false);

        button.setForeground(
                SUBTEXT_COLOR
        );

        button.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        button.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        12,
                        0,
                        0
                )
        );

        if (
                iconPath != null &&
                !iconPath.isEmpty()
        ) {

            try {

                ImageIcon originalIcon =
                        new ImageIcon(
                                getClass().getResource(
                                        iconPath
                                )
                        );

                Image scaledImg =
                        originalIcon
                                .getImage()
                                .getScaledInstance(
                                        15,
                                        15,
                                        Image.SCALE_SMOOTH
                                );

                button.setIcon(
                        new ImageIcon(
                                scaledImg
                        )
                );

                button.setIconTextGap(10);

            } catch (Exception e) {
            }
        }

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getCrossPlatformLookAndFeelClassName()
                );
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