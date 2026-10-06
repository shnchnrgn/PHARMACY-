package ui;

import db.UserDAO;
import models.User;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Random;

public class LoginDialog extends JDialog {

    private final UserDAO userDAO = new UserDAO();

    private boolean isLoggedIn = false;
    private User loggedInUser = null;

    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;

    private JTextField regLastNameField;
    private JTextField regFirstNameField;
    private JTextField regUsernameField;
    private JTextField regContactField;
    private JPasswordField regPasswordField;
    private JPasswordField regConfirmPasswordField;
    private JComboBox<String> regRoleComboBox;
    
    private JComboBox<String> regSecurityQuestion1Combo;
    private JTextField regSecurityAnswer1Field;
    private JComboBox<String> regSecurityQuestion2Combo;
    private JTextField regSecurityAnswer2Field;

    private boolean isOtpVerified = false;
    private String generatedOTP = "";

    private final Border cardBorder =
        BorderFactory.createLineBorder(new Color(210, 215, 220), 1);

    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color THEME_TEAL = new Color(13, 148, 136);
    private static final Color WINDOW_BG = new Color(240, 242, 245);
    private static final Color DIALOG_LINE = new Color(225, 228, 232);
    private static final Color SELECTION_BG = new Color(224, 243, 241);

    private JPanel rightCardContainer;
    private CardLayout cardLayout;
    
    private JLabel leftTitleLabel;
    private JLabel leftDescLabel;
    private JButton leftSwitchBtn;
    private boolean isLoginView = true;

    private static final String[] SECURITY_QUESTIONS_1 = {
        "What is your mother's maiden name?",
        "What was the name of your first pet?",
        "What was the name of your elementary school?",
        "In what city were you born?"
    };

    private static final String[] SECURITY_QUESTIONS_2 = {
        "What is your favorite book?",
        "What was the make of your first car?",
        "What is your childhood nickname?",
        "What is the name of your favorite teacher?"
    };

    public LoginDialog(Frame parent) {
        super(parent, "Vanguard Login & Registration", true);
        setSize(860, 640);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(WINDOW_BG);

        // Terminate application if dialog is closed without logging in
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!isLoggedIn) {
                    System.exit(0);
                }
            }
        });

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(WINDOW_BG);

        // 1. LEFT SIDEBAR (Teal Panel with Logo)
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(THEME_TEAL);
        leftPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        GridBagConstraints lgb = new GridBagConstraints();
        lgb.gridx = 0; lgb.gridy = 0; lgb.anchor = GridBagConstraints.CENTER;
        lgb.fill = GridBagConstraints.HORIZONTAL;

        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/pngs/VANGUARD LOGO HEADER.png"));
            Image scaledImage = logoIcon.getImage().getScaledInstance(180, 60, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledImage), SwingConstants.CENTER);
            lgb.insets = new Insets(0, 0, 20, 0);
            leftPanel.add(logoLabel, lgb);
        } catch (Exception e) {
            // Fallback kung sakaling walang logo file
        }

        lgb.gridy = 1;
        lgb.insets = new Insets(0, 0, 10, 0);
        leftTitleLabel = new JLabel("Welcome Back!", SwingConstants.CENTER);
        leftTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        leftTitleLabel.setForeground(Color.WHITE);
        leftPanel.add(leftTitleLabel, lgb);

        lgb.gridy = 2;
        lgb.insets = new Insets(0, 0, 25, 0);
        leftDescLabel = new JLabel("<html><div style='text-align: center; width: 220px;'>To keep connected with us please login with your personal info</div></html>", SwingConstants.CENTER);
        leftDescLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        leftDescLabel.setForeground(new Color(240, 248, 248));
        leftPanel.add(leftDescLabel, lgb);

        lgb.gridy = 3;
        lgb.insets = new Insets(0, 40, 0, 40);
        leftSwitchBtn = new JButton("SIGN UP");
        styleOutlinedButton(leftSwitchBtn);
        leftPanel.add(leftSwitchBtn, lgb);

        leftSwitchBtn.addActionListener(e -> toggleView());

        // 2. RIGHT CONTENT PANEL (CardLayout para sa Sign In at Create Account)
        cardLayout = new CardLayout();
        rightCardContainer = new JPanel(cardLayout);
        rightCardContainer.setBackground(Color.WHITE);

        rightCardContainer.add(createLoginContentPanel(), "LOGIN");
        rightCardContainer.add(createRegisterContentPanel(), "REGISTER");

        mainPanel.add(leftPanel);
        mainPanel.add(rightCardContainer);

        add(mainPanel);
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    private void toggleView() {
        isLoginView = !isLoginView;
        if (isLoginView) {
            cardLayout.show(rightCardContainer, "LOGIN");
            leftTitleLabel.setText("Welcome Back!");
            leftDescLabel.setText("<html><div style='text-align: center; width: 220px;'>To keep connected with us please login with your personal info</div></html>");
            leftSwitchBtn.setText("SIGN UP");
        } else {
            cardLayout.show(rightCardContainer, "REGISTER");
            leftTitleLabel.setText("Hello, Friend!");
            leftDescLabel.setText("<html><div style='text-align: center; width: 220px;'>Enter your personal details and start journey with us</div></html>");
            leftSwitchBtn.setText("SIGN IN");
        }
    }

    private void styleOutlinedButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(THEME_TEAL);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(8, 24, 8, 24)
        ));
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBackground(new Color(248, 249, 250));
        field.setForeground(new Color(55, 60, 65));
        field.setCaretColor(THEME_TEAL);
        field.setOpaque(true);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(190, 198, 205), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        field.setPreferredSize(new Dimension(360, 30));
    }

    private JPanel createLoginContentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = createGBC();

        JLabel headerTitle = new JLabel("Sign In", SwingConstants.CENTER);
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerTitle.setForeground(THEME_TEAL);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 25, 0);
        panel.add(headerTitle, gbc);

        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 1; gbc.gridx = 0;
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(usernameLabel, gbc);

        loginUsernameField = new JTextField();
        styleTextField(loginUsernameField);
        gbc.gridy = 2; gbc.insets = new Insets(4, 0, 15, 0);
        panel.add(loginUsernameField, gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(passwordLabel, gbc);

        loginPasswordField = new JPasswordField();
        styleTextField(loginPasswordField);
        gbc.gridy = 4; gbc.insets = new Insets(4, 0, 6, 0);
        panel.add(loginPasswordField, gbc);

        JLabel forgotPasswordLabel = new JLabel("Forgot password?");
        forgotPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        forgotPasswordLabel.setForeground(THEME_TEAL);
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 25, 0);
        panel.add(forgotPasswordLabel, gbc);

        JButton loginBtn = new JButton("SIGN IN");
        loginBtn.setBackground(THEME_TEAL);
        loginBtn.setForeground(TEXT_COLOR);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 6; gbc.anchor = GridBagConstraints.CENTER; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel.add(loginBtn, gbc);

        loginBtn.addActionListener(e -> handleLogin());

        forgotPasswordLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleForgotPassword();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                forgotPasswordLabel.setText("<html><u>Forgot password?</u></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                forgotPasswordLabel.setText("Forgot password?");
            }
        });

        return panel;
    }

    private JPanel createRegisterContentPanel() {
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(Color.WHITE);
        outerPanel.setBorder(new EmptyBorder(8, 22, 8, 22));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setPreferredSize(new Dimension(378, 468));
        contentPanel.setMinimumSize(new Dimension(378, 468));
        contentPanel.setMaximumSize(new Dimension(378, 468));

        regLastNameField = new JTextField();
        contentPanel.add(createFloatingField("Last Name", regLastNameField));
        contentPanel.add(Box.createVerticalStrut(4));

        regFirstNameField = new JTextField();
        contentPanel.add(createFloatingField("First Name", regFirstNameField));
        contentPanel.add(Box.createVerticalStrut(4));

        regUsernameField = new JTextField();
        contentPanel.add(createFloatingField("Username", regUsernameField));
        contentPanel.add(Box.createVerticalStrut(4));

        regContactField = new JTextField();
        styleRegisterTextField(regContactField);

        JButton sendOtpBtn = new CenteredButton("Send OTP");
        sendOtpBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        sendOtpBtn.setBackground(THEME_TEAL);
        sendOtpBtn.setForeground(Color.WHITE);
        sendOtpBtn.setFocusPainted(false);
        sendOtpBtn.setBorder(BorderFactory.createEmptyBorder());
        sendOtpBtn.setMargin(new Insets(0, 0, 0, 0));
        sendOtpBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendOtpBtn.setPreferredSize(new Dimension(92, 20));
        sendOtpBtn.setMinimumSize(new Dimension(92, 20));
        sendOtpBtn.setMaximumSize(new Dimension(92, 20));
        sendOtpBtn.addActionListener(e -> handleSendOTP());

        JPanel otpButtonHolder = new JPanel();
        otpButtonHolder.setLayout(new BoxLayout(otpButtonHolder, BoxLayout.Y_AXIS));
        otpButtonHolder.setBackground(Color.WHITE);
        otpButtonHolder.setOpaque(true);
        otpButtonHolder.setPreferredSize(new Dimension(92, 24));
        otpButtonHolder.setMinimumSize(new Dimension(92, 24));
        otpButtonHolder.setMaximumSize(new Dimension(92, Integer.MAX_VALUE));
        otpButtonHolder.add(Box.createVerticalGlue());
        sendOtpBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        otpButtonHolder.add(sendOtpBtn);
        otpButtonHolder.add(Box.createVerticalGlue());

        JPanel contactRow = new JPanel(new BorderLayout(4, 0));
        contactRow.setBackground(Color.WHITE);
        contactRow.setOpaque(true);
        contactRow.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 4));
        contactRow.add(regContactField, BorderLayout.CENTER);
        contactRow.add(otpButtonHolder, BorderLayout.EAST);

        contentPanel.add(createFloatingComponentField("Contact No.", contactRow));
        contentPanel.add(Box.createVerticalStrut(4));

        regPasswordField = new JPasswordField();
        contentPanel.add(createFloatingField("Password", regPasswordField));
        contentPanel.add(Box.createVerticalStrut(4));

        regConfirmPasswordField = new JPasswordField();
        contentPanel.add(createFloatingField("Confirm Password", regConfirmPasswordField));
        contentPanel.add(Box.createVerticalStrut(4));

        String[] initialRoles = {"Pharmacist", "Cashier", "Doctor", "Nurse", "Admin"};
        regRoleComboBox = new JComboBox<>(initialRoles);
        applyCustomDesign(regRoleComboBox);
        contentPanel.add(createFloatingComponentField("Role", regRoleComboBox));
        contentPanel.add(Box.createVerticalStrut(4));

        regSecurityQuestion1Combo = new JComboBox<>(SECURITY_QUESTIONS_1);
        applyCustomDesign(regSecurityQuestion1Combo);
        contentPanel.add(createFloatingComponentField("Security Q1", regSecurityQuestion1Combo));
        contentPanel.add(Box.createVerticalStrut(4));

        regSecurityAnswer1Field = new JTextField();
        contentPanel.add(createFloatingField("Answer 1", regSecurityAnswer1Field));
        contentPanel.add(Box.createVerticalStrut(4));

        regSecurityQuestion2Combo = new JComboBox<>(SECURITY_QUESTIONS_2);
        applyCustomDesign(regSecurityQuestion2Combo);
        contentPanel.add(createFloatingComponentField("Security Q2", regSecurityQuestion2Combo));
        contentPanel.add(Box.createVerticalStrut(4));

        regSecurityAnswer2Field = new JTextField();
        contentPanel.add(createFloatingField("Answer 2", regSecurityAnswer2Field));
        contentPanel.add(Box.createVerticalStrut(8));

        JButton registerBtn = new CenteredButton("SIGN UP");
        registerBtn.setBackground(THEME_TEAL);
        registerBtn.setForeground(TEXT_COLOR);
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        registerBtn.setFocusPainted(false);
        registerBtn.setBorder(BorderFactory.createEmptyBorder());
        registerBtn.setMargin(new Insets(0, 0, 0, 0));
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerBtn.setPreferredSize(new Dimension(378, 30));
        registerBtn.setMinimumSize(new Dimension(378, 30));
        registerBtn.setMaximumSize(new Dimension(378, 30));
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.addActionListener(e -> handleRegistration());

        JPanel registerButtonHolder = new JPanel(new GridBagLayout());
        registerButtonHolder.setBackground(Color.WHITE);
        registerButtonHolder.setOpaque(true);
        registerButtonHolder.setPreferredSize(new Dimension(378, 30));
        registerButtonHolder.setMinimumSize(new Dimension(378, 30));
        registerButtonHolder.setMaximumSize(new Dimension(378, 30));
        registerButtonHolder.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints registerGbc = new GridBagConstraints();
        registerGbc.gridx = 0;
        registerGbc.gridy = 0;
        registerGbc.anchor = GridBagConstraints.CENTER;
        registerGbc.fill = GridBagConstraints.NONE;
        registerButtonHolder.add(registerBtn, registerGbc);
        contentPanel.add(registerButtonHolder);

        outerPanel.add(contentPanel);
        return outerPanel;
    }

    private JPanel createFloatingField(String labelText, JTextField field) {
        styleRegisterTextField(field);
        return createFloatingComponentField(labelText, field);
    }

    private void styleRegisterTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        field.setBackground(Color.WHITE);
        field.setForeground(new Color(55, 60, 65));
        field.setCaretColor(THEME_TEAL);
        field.setOpaque(true);
        field.setBorder(BorderFactory.createEmptyBorder(0, 8, 2, 8));
        field.setPreferredSize(new Dimension(360, 22));
        field.setMinimumSize(new Dimension(360, 22));
        field.setMaximumSize(new Dimension(360, 22));
    }

    private JPanel createFloatingComponentField(String labelText, JComponent component) {
        JPanel fieldPanel = new JPanel(new BorderLayout(0, 0));
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setOpaque(true);
        fieldPanel.setPreferredSize(new Dimension(378, 36));
        fieldPanel.setMinimumSize(new Dimension(378, 36));
        fieldPanel.setMaximumSize(new Dimension(378, 36));
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(185, 195, 203), 1),
            BorderFactory.createEmptyBorder(2, 1, 1, 1)
        ));

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelPanel.setBackground(Color.WHITE);
        labelPanel.setOpaque(true);
        labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 7, 0, 0));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        label.setForeground(new Color(150, 158, 166));
        label.setOpaque(false);

        JLabel star = new JLabel("*");
        star.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        star.setForeground(new Color(211, 47, 47));
        star.setOpaque(false);

        labelPanel.add(label);
        labelPanel.add(Box.createHorizontalStrut(2));
        labelPanel.add(star);
        fieldPanel.add(labelPanel, BorderLayout.NORTH);

        if (component instanceof JComboBox<?>) {
            JComboBox<?> combo = (JComboBox<?>) component;
            combo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            combo.setBackground(Color.WHITE);
            combo.setForeground(new Color(55, 60, 65));
            combo.setBorder(BorderFactory.createEmptyBorder());
            combo.setOpaque(true);
            combo.setFocusable(false);
            combo.setPreferredSize(new Dimension(370, 21));
            combo.setMinimumSize(new Dimension(370, 21));
            combo.setMaximumSize(new Dimension(370, 21));
        } else if (component instanceof JPanel) {
            component.setBackground(Color.WHITE);
            component.setOpaque(true);
            component.setBorder(BorderFactory.createEmptyBorder());
        } else {
            component.setBackground(Color.WHITE);
            component.setBorder(BorderFactory.createEmptyBorder());
        }

        fieldPanel.add(component, BorderLayout.CENTER);
        return fieldPanel;
    }

    private static class CenteredButton extends JButton {
        CenteredButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setForeground(Color.WHITE);
            setBackground(THEME_TEAL);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder());
            setMargin(new Insets(0, 0, 0, 0));
            setContentAreaFilled(false);
            setOpaque(false);
            setHorizontalAlignment(SwingConstants.CENTER);
            setVerticalAlignment(SwingConstants.CENTER);
        }

        @Override

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
            
            FontMetrics fm = g2.getFontMetrics(getFont());
            String value = getText();
            
            int x = (getWidth() - fm.stringWidth(value)) / 2;
            
            int textHeight = fm.getAscent() - fm.getDescent();
            int y = (getHeight() + textHeight) / 2;
            
            g2.setFont(getFont());
            g2.setColor(getForeground());
            g2.drawString(value, x, y);
            g2.dispose();
        }
    }

    private void handleSendOTP() {
        String contact = regContactField.getText().trim();
        if (contact.isEmpty() || !contact.matches("09\\d{9}")) {
            showCustomMessage("Error", "Invalid Contact Number", "Please enter a valid 11-digit Philippine mobile number starting with 09 (e.g., 09123456789).", true);
            return;
        }

        generatedOTP = String.format("%06d", new Random().nextInt(999999));
        
        // Custom Theme OTP Input Dialog
        JDialog otpDialog = new JDialog(this, "OTP Verification", true);
        otpDialog.setLayout(new BorderLayout());
        otpDialog.setResizable(false);
        otpDialog.getContentPane().setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(248, 249, 250));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, DIALOG_LINE),
                new EmptyBorder(10, 15, 10, 15)
        ));
        JLabel lblTitle = new JLabel("Mobile Number Verification");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(new Color(60, 65, 70));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBackground(Color.WHITE);
        bodyPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = createGBC();

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblInfo = new JLabel("<html>Verification code sent to <b>" + contact + "</b>.<br><font color='#009688'>[Simulation Code: " + generatedOTP + "]</font></html>");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bodyPanel.add(lblInfo, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 5, 0);
        JTextField otpField = new JTextField();
        styleTextField(otpField);
        bodyPanel.add(otpField, gbc);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        footerPanel.setBackground(new Color(248, 249, 250));
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, DIALOG_LINE),
                new EmptyBorder(6, 12, 6, 12)
        ));

        JButton btnVerify = new JButton("Verify");
        btnVerify.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnVerify.setBackground(THEME_TEAL);
        btnVerify.setForeground(Color.WHITE);
        btnVerify.setFocusPainted(false);
        btnVerify.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnVerify.addActionListener(e -> {
            String entered = otpField.getText().trim();
            if (entered.equals(generatedOTP)) {
                isOtpVerified = true;
                otpDialog.dispose();
                showCustomMessage("Success", "OTP Verified", "Mobile number successfully verified via OTP!", false);
            } else {
                showCustomMessage("Error", "Verification Failed", "Incorrect OTP code entered. Please try again.", true);
            }
        });

        footerPanel.add(btnVerify);

        otpDialog.add(headerPanel, BorderLayout.NORTH);
        otpDialog.add(bodyPanel, BorderLayout.CENTER);
        otpDialog.add(footerPanel, BorderLayout.SOUTH);
        otpDialog.getRootPane().setDefaultButton(btnVerify);

        otpDialog.pack();
        otpDialog.setSize(Math.max(otpDialog.getWidth() + 40, 400), otpDialog.getHeight() + 10);
        otpDialog.setLocationRelativeTo(this);
        otpDialog.setVisible(true);
    }

    private JLabel createRequiredLabel(String text) {
        JLabel lbl = new JLabel("<html>" + text + " <font color='red'>*</font></html>");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        return lbl;
    }

    private void showCustomMessage(String title, String header, String message, boolean isError) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(248, 249, 250));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, DIALOG_LINE),
                new EmptyBorder(12, 16, 12, 16)
        ));
        JLabel lblTitle = new JLabel(header);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(new Color(60, 65, 70));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel bodyPanel = new JPanel(new BorderLayout(14, 0));
        bodyPanel.setBackground(Color.WHITE);
        bodyPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblMsg = new JLabel("<html><div style='width:260px;'>" + message.replace("\n", "<br>") + "</div></html>");
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMsg.setForeground(new Color(70, 75, 80));
        bodyPanel.add(lblMsg, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        footerPanel.setBackground(new Color(248, 249, 250));
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, DIALOG_LINE),
                new EmptyBorder(8, 16, 8, 12)
        ));

        JButton btnOk = new JButton("OK");
        btnOk.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnOk.setBackground(THEME_TEAL);
        btnOk.setForeground(Color.WHITE);
        btnOk.setFocusPainted(false);
        btnOk.setBorder(new EmptyBorder(6, 22, 6, 22));
        btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOk.addActionListener(e -> dialog.dispose());
        footerPanel.add(btnOk);

        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(bodyPanel, BorderLayout.CENTER);
        dialog.add(footerPanel, BorderLayout.SOUTH);

        dialog.getRootPane().setDefaultButton(btnOk);
        dialog.pack();
        dialog.setSize(Math.max(dialog.getWidth() + 60, 400), Math.max(dialog.getHeight() + 20, 160));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void handleLogin() {
        String username = loginUsernameField.getText().trim();
        char[] passChars = loginPasswordField.getPassword();
        String password = new String(passChars);

        if (username.isEmpty() || password.isEmpty()) {
            showCustomMessage("Login Warning", "Login Warning", "Please enter both username and password.", true);
            return;
        }

        try {
            boolean userExists = userDAO.doesUsernameExist(username);

            if (!userExists) {
                showCustomMessage("Login Failed", "Login Failed", "User not found. Please check your username or register a new account.", true);
            } else {
                User user = userDAO.authenticateUser(username, password);
                if (user != null) {
                    this.loggedInUser = user;
                    this.isLoggedIn = true;
                    showCustomMessage("Login Successful", "Login Successful", "Welcome back, " + user.getFirstName() + " (" + user.getRole() + ")!", false);
                    dispose(); 
                } else {
                    showCustomMessage("Login Failed", "Login Failed", "Incorrect password. Please try again.", true);
                }
            }
        } catch (SQLException ex) {
            showCustomMessage("Error", "Database Error", ex.getMessage(), true);
            ex.printStackTrace();
        } finally {
            Arrays.fill(passChars, '0');
        }
    }

    private void handleRegistration() {
        String lastName = regLastNameField.getText().trim();
        String firstName = regFirstNameField.getText().trim();
        String username = regUsernameField.getText().trim();
        String contact = regContactField.getText().trim();
        char[] pass = regPasswordField.getPassword();
        char[] confirmPass = regConfirmPasswordField.getPassword();

        Object roleObj = regRoleComboBox.getSelectedItem();
        String role = (roleObj != null) ? roleObj.toString().trim() : "";

        String q1 = (String) regSecurityQuestion1Combo.getSelectedItem();
        String a1 = regSecurityAnswer1Field.getText().trim();
        String q2 = (String) regSecurityQuestion2Combo.getSelectedItem();
        String a2 = regSecurityAnswer2Field.getText().trim();

        if (lastName.isEmpty() || firstName.isEmpty() || username.isEmpty() || contact.isEmpty()
                || pass.length == 0 || confirmPass.length == 0 || role.isEmpty()
                || a1.isEmpty() || a2.isEmpty()) {
            showCustomMessage("Registration Error", "Validation Error", "All fields are required! Please do not leave any field blank.", true);
            return;
        }

        if (!isOtpVerified) {
            showCustomMessage("Registration Error", "OTP Required", "Please verify your contact number first by clicking 'Send OTP'.", true);
            return;
        }

        if (!Arrays.equals(pass, confirmPass)) {
            showCustomMessage("Password Mismatch", "Validation Error", "Passwords do not match. Please try again.", true);
            return;
        }

        try {
            boolean success = userDAO.registerUser(lastName, firstName, username, new String(pass), role, q1, a1, q2, a2);
            if (success) {
                showCustomMessage("Success", "Account Registration", "Account registered successfully as " + role + "!", false);
                toggleView(); 
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            if (ex.getErrorCode() == 19 || (ex.getMessage() != null && ex.getMessage().contains("UNIQUE"))) {
                showCustomMessage("Registration Error", "Database Error", "Username is already taken. Please choose another.", true);
            } else {
                showCustomMessage("Error", "Database Error", ex.getMessage(), true);
            }
        } finally {
            Arrays.fill(pass, '0');
            Arrays.fill(confirmPass, '0');
        }
    }

    private void handleForgotPassword() {
        JDialog inputDialog = new JDialog(this, "Password Recovery - Step 1", true);
        inputDialog.setLayout(new BorderLayout());
        inputDialog.setResizable(false);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(248, 249, 250));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, DIALOG_LINE),
                new EmptyBorder(12, 16, 12, 16)
        ));
        JLabel lblTitle = new JLabel("Password Recovery - Step 1");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(new Color(60, 65, 70));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBackground(Color.WHITE);
        bodyPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbcInput = createGBC();

        gbcInput.gridx = 0; gbcInput.gridy = 0;
        JLabel lblPrompt = new JLabel("Enter your username to recover your password:");
        lblPrompt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPrompt.setForeground(new Color(70, 75, 80));
        bodyPanel.add(lblPrompt, gbcInput);

        gbcInput.gridy = 1;
        gbcInput.insets = new Insets(10, 0, 0, 0);
        JTextField usernameField = new JTextField(20);
        styleTextField(usernameField);
        bodyPanel.add(usernameField, gbcInput);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        footerPanel.setBackground(new Color(248, 249, 250));
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, DIALOG_LINE),
                new EmptyBorder(8, 16, 8, 12)
        ));

        JButton btnOk = new JButton("OK");
        btnOk.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnOk.setBackground(THEME_TEAL);
        btnOk.setForeground(Color.WHITE);
        btnOk.setFocusPainted(false);
        btnOk.setBorder(new EmptyBorder(6, 20, 6, 20));
        btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancel.setBackground(new Color(192, 57, 43));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setBorder(new EmptyBorder(6, 20, 6, 20));
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        final String[] enteredUsername = {null};

        btnOk.addActionListener(e -> {
            enteredUsername[0] = usernameField.getText().trim();
            inputDialog.dispose();
        });

        btnCancel.addActionListener(e -> inputDialog.dispose());

        footerPanel.add(btnOk);
        footerPanel.add(btnCancel);

        inputDialog.add(headerPanel, BorderLayout.NORTH);
        inputDialog.add(bodyPanel, BorderLayout.CENTER);
        inputDialog.add(footerPanel, BorderLayout.SOUTH);

        inputDialog.getRootPane().setDefaultButton(btnOk);
        
        inputDialog.pack();
        inputDialog.setSize(Math.max(inputDialog.getWidth() + 40, 440), inputDialog.getHeight() + 10);
        inputDialog.setLocationRelativeTo(this);
        inputDialog.setVisible(true);

        String username = enteredUsername[0];
        if (username == null || username.isEmpty()) {
            return;
        }

        try {
            if (!userDAO.doesUsernameExist(username)) {
                showCustomMessage("Error", "Password Recovery", "Username not found.", true);
                return;
            }

            String[] questions = userDAO.getSecurityQuestions(username);
            if (questions == null || questions[0] == null || questions[1] == null) {
                showCustomMessage("Error", "Password Recovery", "No security questions found for this account.", true);
                return;
            }

            JDialog recoveryDialog = new JDialog(this, "Password Recovery - Step 2", true);
            recoveryDialog.setLayout(new BorderLayout());
            recoveryDialog.setResizable(false);

            JPanel step2HeaderPanel = new JPanel(new BorderLayout());
            step2HeaderPanel.setBackground(new Color(248, 249, 250));
            step2HeaderPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, DIALOG_LINE),
                    new EmptyBorder(12, 16, 12, 16)
            ));
            JLabel step2LblTitle = new JLabel("Password Recovery - Answer Security Questions");
            step2LblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
            step2LblTitle.setForeground(new Color(60, 65, 70));
            step2HeaderPanel.add(step2LblTitle, BorderLayout.WEST);

            JPanel dialogPanel = new JPanel(new GridBagLayout());
            dialogPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
            dialogPanel.setBackground(Color.WHITE);
            GridBagConstraints gbcStep2 = createGBC();

            gbcStep2.gridx = 0; gbcStep2.gridy = 0; gbcStep2.gridwidth = 2;
            JLabel q1Label = new JLabel("<html><b>Q1:</b> " + questions[0] + "</html>");
            q1Label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            dialogPanel.add(q1Label, gbcStep2);

            gbcStep2.gridy = 1;
            gbcStep2.insets = new Insets(4, 0, 10, 0);
            JTextField a1Field = new JTextField(20);
            styleTextField(a1Field);
            dialogPanel.add(a1Field, gbcStep2);

            gbcStep2.gridy = 2;
            gbcStep2.insets = new Insets(4, 0, 4, 0);
            JLabel q2Label = new JLabel("<html><b>Q2:</b> " + questions[1] + "</html>");
            q2Label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            dialogPanel.add(q2Label, gbcStep2);

            gbcStep2.gridy = 3;
            gbcStep2.insets = new Insets(4, 0, 4, 0);
            JTextField a2Field = new JTextField(20);
            styleTextField(a2Field);
            dialogPanel.add(a2Field, gbcStep2);

            JPanel step2FooterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
            step2FooterPanel.setBackground(new Color(248, 249, 250));
            step2FooterPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 0, 0, DIALOG_LINE),
                    new EmptyBorder(8, 16, 8, 12)
            ));

            JButton submitBtn = new JButton("Verify Answers");
            submitBtn.setBackground(THEME_TEAL);
            submitBtn.setForeground(Color.WHITE);
            submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            submitBtn.setFocusPainted(false);
            submitBtn.setBorder(new EmptyBorder(6, 20, 6, 20));
            submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            step2FooterPanel.add(submitBtn);

            String targetUsername = username;
            submitBtn.addActionListener(e -> {
                String ans1 = a1Field.getText().trim();
                String ans2 = a2Field.getText().trim();

                if (ans1.isEmpty() || ans2.isEmpty()) {
                    showCustomMessage("Warning", "Validation Warning", "Please answer both security questions.", true);
                    return;
                }

                try {
                    boolean isCorrect = userDAO.verifySecurityAnswers(targetUsername, ans1, ans2);
                    if (isCorrect) {
                        String password = userDAO.getPasswordByUsername(targetUsername);
                        recoveryDialog.dispose();
                        showCustomMessage("Password Recovered", "Success", "Verification Successful!\nYour Password is: " + password, false);
                    } else {
                        showCustomMessage("Verification Failed", "Failed", "Incorrect answers. Please try again.", true);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showCustomMessage("Error", "Database Error", ex.getMessage(), true);
                }
            });

            recoveryDialog.add(step2HeaderPanel, BorderLayout.NORTH);
            recoveryDialog.add(dialogPanel, BorderLayout.CENTER);
            recoveryDialog.add(step2FooterPanel, BorderLayout.SOUTH);

            recoveryDialog.pack();
            recoveryDialog.setSize(Math.max(recoveryDialog.getWidth() + 40, 480), recoveryDialog.getHeight() + 10);
            recoveryDialog.setLocationRelativeTo(this);
            recoveryDialog.setVisible(true);

        } catch (SQLException ex) {
            ex.printStackTrace();
            showCustomMessage("Error", "Database Error", ex.getMessage(), true);
        }
    }

    private GridBagConstraints createGBC() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 4, 3, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        return gbc;
    }

    private GridBagConstraints createCompactGBC() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        return gbc;
    }

    private void applyCustomDesign(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(new Color(55, 60, 65));
        comboBox.setFocusable(false);
        comboBox.setOpaque(true);
        comboBox.setBorder(BorderFactory.createEmptyBorder());

        comboBox.setUI(new BasicComboBoxUI() {
            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                g.setColor(Color.WHITE);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }

            @Override
            public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
                ListCellRenderer<? super Object> renderer = comboBox.getRenderer();
                Component c = renderer.getListCellRendererComponent(
                    listBox, comboBox.getSelectedItem(), -1, false, false);
                c.setBackground(Color.WHITE);
                c.setForeground(new Color(55, 60, 65));
                c.setFont(new Font("Segoe UI", Font.PLAIN, 11));

                SwingUtilities.paintComponent(g, c, listBox,
                    bounds.x, bounds.y, bounds.width, bounds.height);
            }

            @Override
            protected BasicComboPopup createPopup() {
                BasicComboPopup popup = new BasicComboPopup(comboBox);
                popup.setBackground(Color.WHITE);
                popup.setBorder(BorderFactory.createLineBorder(new Color(185, 195, 203), 1));
                return popup;
            }

            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(Color.WHITE);
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        g2.setColor(new Color(215, 215, 215));
                        g2.drawLine(0, 0, 0, getHeight());
                        g2.setColor(new Color(85, 85, 85));
                        int cx = getWidth() / 2;
                        int cy = getHeight() / 2;
                        int[] xPoints = {cx - 4, cx + 4, cx};
                        int[] yPoints = {cy - 2, cy - 2, cy + 3};
                        g2.fillPolygon(xPoints, yPoints, 3);
                        g2.dispose();
                    }
                };
                btn.setPreferredSize(new Dimension(24, 0));
                btn.setBorder(BorderFactory.createEmptyBorder());
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.setFocusable(false);
                return btn;
            }
        });

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel renderer = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                renderer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                renderer.setBackground(Color.WHITE);
                renderer.setForeground(new Color(55, 60, 65));
                renderer.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
                return renderer;
            }
        });
    }

}