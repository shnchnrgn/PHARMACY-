package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import models.User;

public class UserDAO {

    private Connection getConnection() throws SQLException {
        return Database.getConnection();
    }

    public boolean doesUsernameExist(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM login WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public User authenticateUser(String username, String password) throws SQLException {
        String query = "SELECT * FROM login WHERE LOWER(username) = LOWER(?) AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public boolean registerUser(String lastName, String firstName, String username, 
                                String password, String role, 
                                String q1, String a1, String q2, String a2) throws SQLException {
        String query = "INSERT INTO login (last_name, first_name, username, password, role, " +
                       "security_question_1, security_answer_1, security_question_2, security_answer_2) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, lastName);
            stmt.setString(2, firstName);
            stmt.setString(3, username);
            stmt.setString(4, password);
            stmt.setString(5, role);
            stmt.setString(6, q1);
            stmt.setString(7, a1.toLowerCase().trim()); 
            stmt.setString(8, q2);
            stmt.setString(9, a2.toLowerCase().trim());

            return stmt.executeUpdate() > 0;
        }
    }

    public String[] getSecurityQuestions(String username) throws SQLException {
        String query = "SELECT security_question_1, security_question_2 FROM login WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                        rs.getString("security_question_1"),
                        rs.getString("security_question_2")
                    };
                }
            }
        }
        return null;
    }

    public boolean verifySecurityAnswers(String username, String ans1, String ans2) throws SQLException {
        String query = "SELECT security_answer_1, security_answer_2 FROM login WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String dbAns1 = rs.getString("security_answer_1");
                    String dbAns2 = rs.getString("security_answer_2");

                    return dbAns1 != null && dbAns2 != null 
                        && dbAns1.equalsIgnoreCase(ans1.trim()) 
                        && dbAns2.equalsIgnoreCase(ans2.trim());
                }
            }
        }
        return false;
    }

    public String getPasswordByUsername(String username) throws SQLException {
        String query = "SELECT password FROM login WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password");
                }
            }
        }
        return null;
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setLastName(rs.getString("last_name"));
        user.setFirstName(rs.getString("first_name"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setSecurityQuestion1(rs.getString("security_question_1"));
        user.setSecurityAnswer1(rs.getString("security_answer_1"));
        user.setSecurityQuestion2(rs.getString("security_question_2"));
        user.setSecurityAnswer2(rs.getString("security_answer_2"));
        return user;
    }
}