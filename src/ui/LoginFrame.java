package ui;

import db.UserDAO;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField txtEmail;
    private JPasswordField txtPassword;

    public LoginFrame() {
        setTitle("Pharmacy Management System - Login");
        setSize(400, 240);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtEmail = new JTextField();
        txtPassword = new JPasswordField();
        JButton btnLogin = new JButton("Login");
        JButton btnSignUp = new JButton("Sign Up");

        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);

        panel.add(new JLabel("Password:"));
        panel.add(txtPassword);

        panel.add(btnSignUp);
        panel.add(btnLogin);

        add(panel);

        btnSignUp.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
        });

        btnLogin.addActionListener(e -> {
            String email = txtEmail.getText().trim();
            String password = new String(txtPassword.getPassword());

            boolean isAdmin = email.equalsIgnoreCase("admin@pharmacy.com") && password.equals("admin123");
            boolean isDbUser = UserDAO.checkLogin(email, password);

            if (isAdmin || isDbUser) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                dispose();
                javax.swing.SwingUtilities.invokeLater(() -> App.openMainApplication());
            } else {
                JOptionPane.showMessageDialog(this, "Maling email o password!", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}