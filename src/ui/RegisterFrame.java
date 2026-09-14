package ui;

import db.UserDAO;
import models.User;
import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private JTextField txtEmail;
    private JPasswordField txtPassword;

    public RegisterFrame() {
        setTitle("Pharmacy Management System - Sign Up");
        setSize(400, 220);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtEmail = new JTextField();
        txtPassword = new JPasswordField();
        JButton btnRegister = new JButton("Register");

        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);
        panel.add(new JLabel("Password:"));
        panel.add(txtPassword);
        panel.add(new JLabel());
        panel.add(btnRegister);

        add(panel);

        btnRegister.addActionListener(e -> {
            String email = txtEmail.getText().trim();
            String password = new String(txtPassword.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Punan ang lahat ng field!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User newUser = new User(email, password);
            boolean success = UserDAO.registerUser(newUser);

            if (success) {
                JOptionPane.showMessageDialog(this, "Registration successful! Pwede ka nang mag-login.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "May error o baka naka-register na ang email na yan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}