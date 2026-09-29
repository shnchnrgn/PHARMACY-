package db;

import models.Customer;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public static void addCustomer(Customer customer) {
        String sql = "INSERT INTO customers (last_name, first_name, contact, address, last_purchase_date, date_registered) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customer.getLastName());
            pstmt.setString(2, customer.getFirstName());
            pstmt.setString(3, customer.getContact());
            pstmt.setString(4, customer.getAddress());
            pstmt.setString(5, customer.getLastPurchaseDate());
            pstmt.setString(6, customer.getDateRegistered());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET last_name = ?, first_name = ?, contact = ?, address = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customer.getLastName());
            pstmt.setString(2, customer.getFirstName());
            pstmt.setString(3, customer.getContact());
            pstmt.setString(4, customer.getAddress());
            pstmt.setInt(5, customer.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteCustomer(int id) {
        String sql = "DELETE FROM customers WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                customers.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public static List<Customer> searchCustomers(String keyword) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE first_name LIKE ? OR last_name LIKE ? OR CONCAT(first_name, ' ', last_name) LIKE ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public static List<Customer> getInactiveCustomers(int days) {
        List<Customer> customers = new ArrayList<>();
        LocalDate thresholdDate = LocalDate.now().minusDays(days);
        String sql = "SELECT * FROM customers WHERE last_purchase_date IS NULL OR last_purchase_date = 'N/A' OR last_purchase_date <= ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, thresholdDate.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public static List<String[]> getPurchaseHistory(int customerId) {
        List<String[]> history = new ArrayList<>();

        String sql = "SELECT order_no, purchase_date, amount " +
                     "FROM purchase_history " +
                     "WHERE customer_id = ? " +
                     "ORDER BY purchase_date DESC, id DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    history.add(new String[]{
                        rs.getString("order_no"),
                        rs.getString("purchase_date"),
                        String.format("\u20b1 %.2f", rs.getDouble("amount"))
                    });
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return history;
    }

    public static int getCustomerIdByName(String firstName, String lastName) {
        String sql = "SELECT id FROM customers WHERE first_name = ? AND last_name = ? LIMIT 1";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static int getCustomerIdByNameIgnoreCase(String firstName, String lastName) {
        String sql = "SELECT id FROM customers WHERE LOWER(TRIM(first_name)) = LOWER(TRIM(?)) AND LOWER(TRIM(last_name)) = LOWER(TRIM(?)) LIMIT 1";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static void recordPurchase(int customerId, String orderNo, String purchaseDate, double amount) {
        String sql = "INSERT INTO purchase_history " +
                     "(customer_id, order_no, purchase_date, amount) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            pstmt.setString(2, orderNo);
            pstmt.setString(3, purchaseDate);
            pstmt.setDouble(4, amount);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateLastPurchaseDate(int customerId, String purchaseDate) {
        String sql = "UPDATE customers SET last_purchase_date = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, purchaseDate);
            pstmt.setInt(2, customerId);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Customer mapRow(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setId(rs.getInt("id"));
        c.setLastName(rs.getString("last_name"));
        c.setFirstName(rs.getString("first_name"));
        c.setContact(rs.getString("contact"));
        c.setLastPurchaseDate(rs.getString("last_purchase_date"));
        c.setAddress(safeGetString(rs, "address"));
        c.setDateRegistered(safeGetString(rs, "date_registered"));
        return c;
    }

    private static String safeGetString(ResultSet rs, String column) {
        try {
            String value = rs.getString(column);
            return value == null ? "" : value;
        } catch (SQLException e) {
            return "";
        }
    }
}