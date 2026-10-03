package ui;

import db.UserDAO;
import models.User;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicScrollBarUI;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Arrays;

public class LoginDialog extends JDialog {

    private final UserDAO userDAO = new UserDAO();

    // Authentication State
    private boolean isLoggedIn = false;
    private User loggedInUser = null;

    // Login Components
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;
    private JLabel loginStatusLabel;

    // Registration Components
    private JTextField regLastNameField;
    private JTextField regFirstNameField;
    private JTextField regUsernameField;
    private JPasswordField regPasswordField;
    private JPasswordField regConfirmPasswordField;
    private JComboBox<String> regRoleComboBox;
    
    // Security Question Components
    private JComboBox<String> regSecurityQuestion1Combo;
    private JTextField regSecurityAnswer1Field;
    private JComboBox<String> regSecurityQuestion2Combo;
    private JTextField regSecurityAnswer2Field;

    private JLabel regStatusLabel;

    private final Border grayBorder =
        BorderFactory.createLineBorder(new Color(200, 205, 210), 1);

    private static final Color TEXT_COLOR = new Color(238, 244, 244);
    private static final Color SUBTEXT_COLOR = new Color(120, 120, 120);

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
        super(parent, "Vanguard Login & Registration", true); // true sets modal mode
        setSize(480, 620);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        setResizable(false);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Login", createLoginPanel());
        tabbedPane.addTab("Register", createRegisterPanel());

        add(tabbedPane);
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = createGBC();
        panel.setBackground(new Color(250, 249, 246));
        panel.setBorder(grayBorder);

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/pngs/VANGUARD LOGO HEADER.png"));
        Image scaledImage = logoIcon.getImage().getScaledInstance(240, 80, Image.SCALE_SMOOTH);
        ImageIcon resizedLogo = new ImageIcon(scaledImage);

        JLabel headerLogo = new JLabel(resizedLogo, SwingConstants.CENTER);

        gbc.gridx = 0; 
        gbc.gridy = 0; 
        gbc.gridwidth = 2;
        panel.add(headerLogo, gbc);

        JLabel subtextLabel = new JLabel("Pharmacy Management System", SwingConstants.CENTER);
        subtextLabel.setFont(new Font("SEGOE UI", Font.ITALIC, 12));
        subtextLabel.setForeground(SUBTEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 1; 
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(subtextLabel, gbc);

        gbc.gridwidth = 1; gbc.gridy = 2; gbc.gridx = 0;
        JLabel usernameLabel = new JLabel("Username: ");
        usernameLabel.setFont(new Font("SEGOE UI", Font.PLAIN, 14));
        panel.add(usernameLabel, gbc);

        loginUsernameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(loginUsernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel passwordLabel = new JLabel("Password: ");
        passwordLabel.setFont(new Font("SEGOE UI", Font.PLAIN, 14));
        panel.add(passwordLabel, gbc);

        loginPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(loginPasswordField, gbc);

        JLabel forgotPasswordLabel = new JLabel("Forgot Password?");
        forgotPasswordLabel.setFont(new Font("SEGOE UI", Font.PLAIN, 12));
        forgotPasswordLabel.setForeground(new Color(26, 143, 136));
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(forgotPasswordLabel, gbc);

        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.CENTER;

        JButton loginBtn = new JButton("Login");
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);
        loginBtn.setBackground(new Color(26, 143, 136));
        loginBtn.setForeground(TEXT_COLOR);
        loginBtn.setOpaque(true);
        loginBtn.setFont(new Font("SEGOE UI", Font.BOLD, 14));

        loginStatusLabel = new JLabel("", SwingConstants.CENTER);
        gbc.gridy = 6;
        panel.add(loginStatusLabel, gbc);

        loginBtn.addActionListener(e -> handleLogin());

        forgotPasswordLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleForgotPassword();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                forgotPasswordLabel.setText("<html><u>Forgot Password?</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                forgotPasswordLabel.setText("Forgot Password?");
            }
        });

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.setBackground(new Color(250, 249, 246));
        GridBagConstraints gbc = createGBC();

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/pngs/VANGUARD LOGO HEADER.png"));
        Image scaledImage = logoIcon.getImage().getScaledInstance(200, 65, Image.SCALE_SMOOTH);
        ImageIcon resizedLogo = new ImageIcon(scaledImage);

        JLabel headerLogo = new JLabel(resizedLogo, SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(headerLogo, gbc);

        JLabel subtextLabel = new JLabel("Pharmacy Management System", SwingConstants.CENTER);
        subtextLabel.setFont(new Font("SEGOE UI", Font.ITALIC, 11));
        subtextLabel.setForeground(SUBTEXT_COLOR); 
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(subtextLabel, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setFont(new Font("SEGOE UI", Font.PLAIN, 13));
        panel.add(lastNameLabel, gbc);
        regLastNameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(regLastNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setFont(new Font("SEGOE UI", Font.PLAIN, 13));
        panel.add(firstNameLabel, gbc);
        regFirstNameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(regFirstNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("SEGOE UI", Font.PLAIN, 13));
        panel.add(usernameLabel, gbc);
        regUsernameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(regUsernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("SEGOE UI", Font.PLAIN, 13));
        panel.add(passwordLabel, gbc);
        regPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(regPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setFont(new Font("SEGOE UI", Font.PLAIN, 13));
        panel.add(confirmLabel, gbc);
        regConfirmPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(regConfirmPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("SEGOE UI", Font.PLAIN, 13));
        panel.add(roleLabel, gbc);
        String[] initialRoles = {"Pharmacist", "Cashier", "Doctor", "Nurse", "Admin"};
        regRoleComboBox = new JComboBox<>(initialRoles);
        applyCustomDesign(regRoleComboBox);
        gbc.gridx = 1;
        panel.add(regRoleComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 8;
        JLabel q1Label = new JLabel("Security Question 1:    ");
        q1Label.setFont(new Font("SEGOE UI", Font.PLAIN, 13));
        panel.add(q1Label, gbc);
        regSecurityQuestion1Combo = new JComboBox<>(SECURITY_QUESTIONS_1);
        applyCustomDesign(regSecurityQuestion1Combo);
        gbc.gridx = 1;
        panel.add(regSecurityQuestion1Combo, gbc);

        gbc.gridx = 0; gbc.gridy = 9;
        JLabel a1Label = new JLabel("Answer 1:");
        a1Label.setFont(new Font("SEGOE UI", Font.PLAIN, 13));
        panel.add(a1Label, gbc);
        regSecurityAnswer1Field = new JTextField(15);
        gbc.gridx = 1;
        panel.add(regSecurityAnswer1Field, gbc);

        gbc.gridx = 0; gbc.gridy = 10;
        JLabel q2Label = new JLabel("Security Question 2:    ");
        q2Label.setFont(new Font("SEGOE UI", Font.PLAIN, 13));
        panel.add(q2Label, gbc);
        regSecurityQuestion2Combo = new JComboBox<>(SECURITY_QUESTIONS_2);
        applyCustomDesign(regSecurityQuestion2Combo);
        gbc.gridx = 1;
        panel.add(regSecurityQuestion2Combo, gbc);

        gbc.gridx = 0; gbc.gridy = 11;
        JLabel a2Label = new JLabel("Answer 2:");
        a2Label.setFont(new Font("SEGOE UI", Font.PLAIN, 13));
        panel.add(a2Label, gbc);
        regSecurityAnswer2Field = new JTextField(15);
        gbc.gridx = 1;
        panel.add(regSecurityAnswer2Field, gbc);

        JButton registerBtn = new JButton("Register Account");
        gbc.gridx = 0; gbc.gridy = 12; gbc.gridwidth = 2;
        panel.add(registerBtn, gbc);
        registerBtn.setBackground(new Color(26, 143, 136));
        registerBtn.setForeground(TEXT_COLOR);
        registerBtn.setFocusPainted(false);
        registerBtn.setOpaque(true);
        registerBtn.setFont(new Font("SEGOE UI", Font.BOLD, 14));

        regStatusLabel = new JLabel("", SwingConstants.CENTER);
        gbc.gridy = 13;
        panel.add(regStatusLabel, gbc);

        registerBtn.addActionListener(e -> handleRegistration());

        return panel;
    }

    private void handleLogin() {
        String username = loginUsernameField.getText().trim();
        char[] passChars = loginPasswordField.getPassword();
        String password = new String(passChars);

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Please enter both username and password.", 
                    "Login Warning", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean userExists = userDAO.doesUsernameExist(username);

            if (!userExists) {
                JOptionPane.showMessageDialog(this, 
                        "User not found. Please check your username or register a new account.", 
                        "Login Failed", 
                        JOptionPane.ERROR_MESSAGE);
            } else {
                User user = userDAO.authenticateUser(username, password);
                if (user != null) {
                    this.loggedInUser = user;
                    this.isLoggedIn = true;
                    JOptionPane.showMessageDialog(this, 
                            "Welcome back, " + user.getFirstName() + " (" + user.getRole() + ")!", 
                            "Login Successful", 
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // Close modal dialog and yield execution back to main flow
                } else {
                    JOptionPane.showMessageDialog(this, 
                            "Incorrect password. Please try again.", 
                            "Login Failed", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                    "Database Error: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            Arrays.fill(passChars, '0');
        }
    }

    private void handleForgotPassword() {
        String username = JOptionPane.showInputDialog(
                this, 
                "Enter your username to recover your password:", 
                "Password Recovery - Step 1", 
                JOptionPane.QUESTION_MESSAGE
        );

        if (username == null || username.trim().isEmpty()) {
            return;
        }

        username = username.trim();

        try {
            if (!userDAO.doesUsernameExist(username)) {
                JOptionPane.showMessageDialog(
                        this, 
                        "Username not found.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            String[] questions = userDAO.getSecurityQuestions(username);
            if (questions == null || questions[0] == null || questions[1] == null) {
                JOptionPane.showMessageDialog(
                        this, 
                        "No security questions found for this account.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            JDialog recoveryDialog = new JDialog(this, "Password Recovery - Step 2", true);
            recoveryDialog.setSize(420, 260);
            recoveryDialog.setLocationRelativeTo(this);
            recoveryDialog.setResizable(false);

            JPanel dialogPanel = new JPanel(new GridBagLayout());
            dialogPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            dialogPanel.setBackground(new Color(250, 249, 246));
            GridBagConstraints gbc = createGBC();

            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            JLabel q1Label = new JLabel("Q1: " + questions[0]);
            q1Label.setFont(new Font("SEGOE UI", Font.BOLD, 12));
            dialogPanel.add(q1Label, gbc);

            gbc.gridy = 1;
            JTextField a1Field = new JTextField(20);
            dialogPanel.add(a1Field, gbc);

            gbc.gridy = 2;
            JLabel q2Label = new JLabel("Q2: " + questions[1]);
            q2Label.setFont(new Font("SEGOE UI", Font.BOLD, 12));
            dialogPanel.add(q2Label, gbc);

            gbc.gridy = 3;
            JTextField a2Field = new JTextField(20);
            dialogPanel.add(a2Field, gbc);

            JButton submitBtn = new JButton("Verify Answers");
            submitBtn.setBackground(new Color(26, 143, 136));
            submitBtn.setForeground(TEXT_COLOR);
            submitBtn.setFont(new Font("SEGOE UI", Font.BOLD, 13));
            submitBtn.setFocusPainted(false);
            gbc.gridy = 4; gbc.insets = new Insets(10, 0, 0, 0);
            dialogPanel.add(submitBtn, gbc);

            String targetUsername = username;
            submitBtn.addActionListener(e -> {
                String ans1 = a1Field.getText().trim();
                String ans2 = a2Field.getText().trim();

                if (ans1.isEmpty() || ans2.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            recoveryDialog, 
                            "Please answer both security questions.", 
                            "Warning", 
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                try {
                    boolean isCorrect = userDAO.verifySecurityAnswers(targetUsername, ans1, ans2);
                    if (isCorrect) {
                        String password = userDAO.getPasswordByUsername(targetUsername);
                        recoveryDialog.dispose();
                        JOptionPane.showMessageDialog(
                                this, 
                                "Verification Successful!\nYour Password is: " + password, 
                                "Password Recovered", 
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    } else {
                        JOptionPane.showMessageDialog(
                                recoveryDialog, 
                                "Incorrect answers. Please try again.", 
                                "Verification Failed", 
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            recoveryDialog, 
                            "Database Error: " + ex.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            });

            recoveryDialog.add(dialogPanel);
            recoveryDialog.setVisible(true);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this, 
                    "Database Error: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleRegistration() {
        String lastName = regLastNameField.getText().trim();
        String firstName = regFirstNameField.getText().trim();
        String username = regUsernameField.getText().trim();
        char[] pass = regPasswordField.getPassword();
        char[] confirmPass = regConfirmPasswordField.getPassword();

        Object roleObj = regRoleComboBox.getSelectedItem();
        String role = (roleObj != null) ? roleObj.toString().trim() : "";

        String q1 = (String) regSecurityQuestion1Combo.getSelectedItem();
        String a1 = regSecurityAnswer1Field.getText().trim();
        String q2 = (String) regSecurityQuestion2Combo.getSelectedItem();
        String a2 = regSecurityAnswer2Field.getText().trim();

        if (lastName.isEmpty() || firstName.isEmpty() || username.isEmpty() 
                || pass.length == 0 || confirmPass.length == 0 || role.isEmpty()
                || a1.isEmpty() || a2.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "All fields are required! Please do not leave any field blank.", 
                    "Registration Error", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!lastName.matches("[a-zA-Z\\s-]+") || !firstName.matches("[a-zA-Z\\s-]+")) {
            JOptionPane.showMessageDialog(this, 
                    "Names cannot contain numbers or special characters. Please enter a valid name.", 
                    "Invalid Name Input", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Arrays.equals(pass, confirmPass)) {
            JOptionPane.showMessageDialog(this, 
                    "Passwords do not match. Please try again.", 
                    "Password Mismatch", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean success = userDAO.registerUser(lastName, firstName, username, new String(pass), role, q1, a1, q2, a2);
            if (success) {
                JOptionPane.showMessageDialog(this, 
                        "Account registered successfully as " + role + "!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                clearRegisterForm();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            if (ex.getErrorCode() == 19 || (ex.getMessage() != null && ex.getMessage().contains("UNIQUE"))) {
                JOptionPane.showMessageDialog(this, 
                        "Username is already taken. Please choose another.", 
                        "Registration Error", 
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Database Error: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        } finally {
            Arrays.fill(pass, '0');
            Arrays.fill(confirmPass, '0');
        }
    }

    private void clearRegisterForm() {
        regLastNameField.setText("");
        regFirstNameField.setText("");
        regUsernameField.setText("");
        regPasswordField.setText("");
        regConfirmPasswordField.setText("");
        regRoleComboBox.setSelectedIndex(0);
        regSecurityQuestion1Combo.setSelectedIndex(0);
        regSecurityAnswer1Field.setText("");
        regSecurityQuestion2Combo.setSelectedIndex(0);
        regSecurityAnswer2Field.setText("");
    }

    private GridBagConstraints createGBC() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private void applyCustomDesign(JComboBox<?> comboBox) {
        comboBox.setUI(new BasicComboBoxUI() {
            @Override
            protected BasicComboPopup createPopup() {
                BasicComboPopup popup = new BasicComboPopup(comboBox);
                popup.setBorder(grayBorder);
                popup.setBackground(Color.WHITE);

                for (Component c : popup.getComponents()) {
                    if (c instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) c;
                        scrollPane.setBackground(Color.WHITE);
                        scrollPane.getViewport().setBackground(Color.WHITE);
                        scrollPane.setBorder(null);

                        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
                        verticalBar.setUI(new BasicScrollBarUI() {
                            @Override
                            protected void configureScrollBarColors() {
                                this.thumbColor = new Color(200, 205, 210);
                                this.trackColor = Color.WHITE;
                            }

                            @Override
                            protected JButton createDecreaseButton(int orientation) {
                                return createZeroButton();
                            }

                            @Override
                            protected JButton createIncreaseButton(int orientation) {
                                return createZeroButton();
                            }

                            private JButton createZeroButton() {
                                JButton btn = new JButton();
                                btn.setPreferredSize(new Dimension(0, 0));
                                btn.setMinimumSize(new Dimension(0, 0));
                                btn.setMaximumSize(new Dimension(0, 0));
                                return btn;
                            }
                        });
                    }
                }
                return popup;
            }

            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        g2d.setColor(Color.WHITE);
                        g2d.fillRect(0, 0, getWidth(), getHeight());
                        
                        g2d.setColor(new Color(200, 205, 210));
                        g2d.drawLine(0, 0, 0, getHeight());
                        
                        g2d.setColor(new Color(80, 80, 80));
                        int[] xPoints = { getWidth() / 2 - 4, getWidth() / 2 + 4, getWidth() / 2 };
                        int[] yPoints = { getHeight() / 2 - 2, getHeight() / 2 - 2, getHeight() / 2 + 3 };
                        g2d.fillPolygon(xPoints, yPoints, 3);
                        
                        g2d.dispose();
                    }
                };

                btn.setBorder(BorderFactory.createEmptyBorder());
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.setFocusable(false);
                return btn;
            }
        });

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel renderer = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (isSelected) {
                    renderer.setBackground(new Color(210, 215, 220));
                    renderer.setForeground(Color.BLACK);
                } else {
                    renderer.setBackground(Color.WHITE);
                    renderer.setForeground(Color.BLACK);
                }

                renderer.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return renderer;
            }
        });
    }
}