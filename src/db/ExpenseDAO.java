package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExpenseDAO {

    public static boolean addExpense(String description, double amount, String expenseDate) {
        String sql = "INSERT INTO expenses (description, amount, expense_date) VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, description);
            stmt.setDouble(2, amount);
            stmt.setString(3, expenseDate);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getThisMonthExpenseCount() {
        String sql =
                "SELECT COUNT(*) FROM expenses " +
                "WHERE date(expense_date) >= date('now', 'localtime', 'start of month') " +
                "AND date(expense_date) < date('now', 'localtime', 'start of month', '+1 month')";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static double getThisMonthExpenseTotal() {
        String sql =
                "SELECT COALESCE(SUM(amount), 0) FROM expenses " +
                "WHERE date(expense_date) >= date('now', 'localtime', 'start of month') " +
                "AND date(expense_date) < date('now', 'localtime', 'start of month', '+1 month')";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            return rs.next() ? rs.getDouble(1) : 0.0;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public static double getTodayExpenseTotal() {
        String sql =
                "SELECT COALESCE(SUM(amount), 0) FROM expenses " +
                "WHERE date(expense_date) = date('now', 'localtime')";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            return rs.next() ? rs.getDouble(1) : 0.0;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}