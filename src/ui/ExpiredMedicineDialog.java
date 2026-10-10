package ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import models.Medicine;

public class ExpiredMedicineDialog extends JDialog {

    private static final Color TEAL = new Color(0, 150, 136);
    private static final Color DARK_TEXT = new Color(45, 45, 45);
    private static final Color SECONDARY_TEXT = new Color(95, 95, 95);
    private static final Color BORDER = new Color(225, 225, 225);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color BACKGROUND = new Color(248, 249, 250);

    public ExpiredMedicineDialog(
            Frame parent,
            List<Medicine> expiredMedicines
    ) {
        super(parent, "System Notifications", true);

        setSize(560, 650);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND);

        mainPanel.add(createHeader(), BorderLayout.NORTH);
        mainPanel.add(createContent(expiredMedicines), BorderLayout.CENTER);
        mainPanel.add(createFooter(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0, 0, 1, 0,
                                BORDER
                        ),
                        new EmptyBorder(
                                18,
                                22,
                                18,
                                18
                        )
                )
        );

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(
                new BoxLayout(
                        titlePanel,
                        BoxLayout.Y_AXIS
                )
        );
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("System Notifications");
        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );
        title.setForeground(DARK_TEXT);

        JLabel subtitle = new JLabel(
                "Medicine expiration alerts"
        );
        subtitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );
        subtitle.setForeground(SECONDARY_TEXT);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(3));
        titlePanel.add(subtitle);

        JButton closeButton = new JButton("×");
        closeButton.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        24
                )
        );
        closeButton.setForeground(
                new Color(110, 110, 110)
        );
        closeButton.setBackground(Color.WHITE);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        closeButton.addActionListener(e -> dispose());

        header.add(titlePanel, BorderLayout.WEST);
        header.add(closeButton, BorderLayout.EAST);

        return header;
    }

    private JPanel createContent(
            List<Medicine> expiredMedicines
    ) {

        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(BACKGROUND);
        outerPanel.setBorder(
                new EmptyBorder(
                        18,
                        22,
                        10,
                        22
                )
        );

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(
                new BoxLayout(
                        contentPanel,
                        BoxLayout.Y_AXIS
                )
        );
        contentPanel.setBackground(BACKGROUND);

        JLabel sectionTitle = new JLabel(
                "Expired Medicines (" +
                        expiredMedicines.size() +
                        ")"
        );

        sectionTitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        15
                )
        );

        sectionTitle.setForeground(
                new Color(190, 45, 45)
        );

        sectionTitle.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        contentPanel.add(sectionTitle);
        contentPanel.add(
                Box.createVerticalStrut(14)
        );

        if (expiredMedicines.isEmpty()) {

            JPanel emptyPanel = new JPanel(
                    new BorderLayout()
            );

            emptyPanel.setBackground(Color.WHITE);
            emptyPanel.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                    BORDER
                            ),
                            new EmptyBorder(
                                    25,
                                    20,
                                    25,
                                    20
                            )
                    )
            );

            JLabel emptyLabel = new JLabel(
                    "No expired medicines found.",
                    SwingConstants.CENTER
            );

            emptyLabel.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            13
                    )
            );

            emptyLabel.setForeground(
                    SECONDARY_TEXT
            );

            emptyPanel.add(
                    emptyLabel,
                    BorderLayout.CENTER
            );

            contentPanel.add(emptyPanel);

        } else {

            for (Medicine medicine : expiredMedicines) {

                JPanel medicineCard =
                        createMedicineCard(medicine);

                medicineCard.setAlignmentX(
                        Component.LEFT_ALIGNMENT
                );

                contentPanel.add(medicineCard);
                contentPanel.add(
                        Box.createVerticalStrut(12)
                );
            }
        }

        JScrollPane scrollPane =
                new JScrollPane(contentPanel);

        scrollPane.setBorder(null);
        scrollPane.setBackground(BACKGROUND);
        scrollPane.getViewport().setBackground(
                BACKGROUND
        );

        scrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants
                        .HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollPane.setVerticalScrollBarPolicy(
                ScrollPaneConstants
                        .VERTICAL_SCROLLBAR_AS_NEEDED
        );

        installCustomScrollBar(
                scrollPane.getVerticalScrollBar()
        );

        returnPanel(
                outerPanel,
                scrollPane
        );

        return outerPanel;
    }

    private void returnPanel(
            JPanel parent,
            JScrollPane scrollPane
    ) {
        parent.add(
                scrollPane,
                BorderLayout.CENTER
        );
    }

    private JPanel createMedicineCard(
            Medicine medicine
    ) {

        JPanel card = new JPanel(
                new BorderLayout()
        );

        card.setBackground(
                CARD_BACKGROUND
        );

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        new EmptyBorder(
                                14,
                                16,
                                14,
                                16
                        )
                )
        );

        JPanel detailsPanel = new JPanel();

        detailsPanel.setLayout(
                new BoxLayout(
                        detailsPanel,
                        BoxLayout.Y_AXIS
                )
        );

        detailsPanel.setOpaque(false);

        JLabel medicineName =
                new JLabel(
                        safeText(
                                medicine.getName()
                        )
                );

        medicineName.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        medicineName.setForeground(
                DARK_TEXT
        );

        JLabel category =
                createDetailLabel(
                        "Category",
                        safeText(
                                medicine.getMedicineCategory()
                        )
                );

        JLabel quantity =
                createDetailLabel(
                        "Quantity",
                        String.valueOf(
                                medicine.getStock()
                        )
                );

        JLabel expiry =
                createDetailLabel(
                        "Expiry Date",
                        safeText(
                                medicine.getExpiryDate()
                        )
                );

        JLabel company =
                createDetailLabel(
                        "Company",
                        safeText(
                                medicine.getCompanyName()
                        )
                );

        detailsPanel.add(medicineName);
        detailsPanel.add(
                Box.createVerticalStrut(8)
        );

        detailsPanel.add(category);
        detailsPanel.add(
                Box.createVerticalStrut(3)
        );

        detailsPanel.add(quantity);
        detailsPanel.add(
                Box.createVerticalStrut(3)
        );

        detailsPanel.add(expiry);
        detailsPanel.add(
                Box.createVerticalStrut(3)
        );

        detailsPanel.add(company);

        card.add(
                detailsPanel,
                BorderLayout.CENTER
        );

        return card;
    }

    private JLabel createDetailLabel(
            String title,
            String value
    ) {

        JLabel label = new JLabel(
                "<html>" +
                        "<span style='color:#5F5F5F;'>" +
                        title +
                        ":</span> " +
                        "<span style='color:#2D2D2D;'>" +
                        value +
                        "</span>" +
                        "</html>"
        );

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        return label;
    }

    private JPanel createFooter() {

        JPanel footer = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        18,
                        14
                )
        );

        footer.setBackground(Color.WHITE);

        footer.setBorder(
                BorderFactory.createMatteBorder(
                        1,
                        0,
                        0,
                        0,
                        BORDER
                )
        );

        JButton okButton =
                new JButton("OK");

        okButton.setPreferredSize(
                new Dimension(
                        90,
                        38
                )
        );

        okButton.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        okButton.setForeground(Color.WHITE);
        okButton.setBackground(TEAL);

        okButton.setBorderPainted(false);
        okButton.setFocusPainted(false);
        okButton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        okButton.addActionListener(
                e -> dispose()
        );

        footer.add(okButton);

        return footer;
    }

    private void installCustomScrollBar(
            JScrollBar scrollBar
    ) {

        scrollBar.setPreferredSize(
                new Dimension(
                        10,
                        Integer.MAX_VALUE
                )
        );

        scrollBar.setUnitIncrement(16);

        scrollBar.setBlockIncrement(60);

        scrollBar.setOpaque(false);

        scrollBar.setUI(
                new BasicScrollBarUI() {

                    @Override
                    protected void configureScrollBarColors() {
                        thumbColor =
                                new Color(
                                        190,
                                        190,
                                        190
                                );

                        trackColor =
                                new Color(
                                        242,
                                        242,
                                        242
                                );
                    }

                    @Override
                    protected JButton createDecreaseButton(
                            int orientation
                    ) {
                        return createZeroButton();
                    }

                    @Override
                    protected JButton createIncreaseButton(
                            int orientation
                    ) {
                        return createZeroButton();
                    }

                    private JButton createZeroButton() {

                        JButton button =
                                new JButton();

                        button.setPreferredSize(
                                new Dimension(
                                        0,
                                        0
                                )
                        );

                        button.setMinimumSize(
                                new Dimension(
                                        0,
                                        0
                                )
                        );

                        button.setMaximumSize(
                                new Dimension(
                                        0,
                                        0
                                )
                        );

                        button.setBorder(null);
                        button.setBorderPainted(false);
                        button.setContentAreaFilled(false);
                        button.setFocusPainted(false);

                        return button;
                    }

                    @Override
                    protected void paintTrack(
                            Graphics g,
                            JComponent c,
                            Rectangle trackBounds
                    ) {

                        Graphics2D g2 =
                                (Graphics2D) g.create();

                        g2.setColor(
                                new Color(
                                        242,
                                        242,
                                        242
                                )
                        );

                        g2.fillRect(
                                trackBounds.x,
                                trackBounds.y,
                                trackBounds.width,
                                trackBounds.height
                        );

                        g2.dispose();
                    }

                    @Override
                    protected void paintThumb(
                            Graphics g,
                            JComponent c,
                            Rectangle thumbBounds
                    ) {

                        if (thumbBounds.isEmpty()
                                || !scrollBar.isEnabled()) {
                            return;
                        }

                        Graphics2D g2 =
                                (Graphics2D) g.create();

                        g2.setRenderingHint(
                                RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON
                        );

                        g2.setColor(
                                new Color(
                                        180,
                                        180,
                                        180
                                )
                        );

                        g2.fillRoundRect(
                                thumbBounds.x + 2,
                                thumbBounds.y,
                                thumbBounds.width - 4,
                                thumbBounds.height,
                                8,
                                8
                        );

                        g2.dispose();
                    }
                }
        );
    }

    private String safeText(
            Object value
    ) {

        if (value == null) {
            return "-";
        }

        String text =
                String.valueOf(value).trim();

        if (text.isEmpty()) {
            return "-";
        }

        return text;
    }
}