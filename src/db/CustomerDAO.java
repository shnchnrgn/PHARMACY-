package db;

import models.Customer;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public static void addCustomer(Customer customer) {

        String sql =
                "INSERT INTO customers " +
                "(name, contact, address, last_purchase_date, date_registered) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getContact());
            pstmt.setString(3, customer.getAddress());
            pstmt.setString(4, customer.getLastPurchaseDate());
            pstmt.setString(5, customer.getDateRegistered());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int addCustomerAndGetId(Customer customer) throws SQLException {

        String sql =
                "INSERT INTO customers " +
                "(name, contact, address, last_purchase_date, date_registered) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            if (conn == null) {
                throw new SQLException("Unable to connect to database.");
            }

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getContact());
            pstmt.setString(3, customer.getAddress());
            pstmt.setString(4, customer.getLastPurchaseDate());
            pstmt.setString(5, customer.getDateRegistered());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Unable to create customer record.");
    }

    public static int createCustomerIfNotExists(String name)
            throws SQLException {

        String cleanName = name == null
                ? ""
                : name.trim();

        if (cleanName.isEmpty()) {
            throw new SQLException("Customer name is required.");
        }

        int existingId = getCustomerIdByNameIgnoreCase(cleanName);

        if (existingId != -1) {
            return existingId;
        }

        String sql =
                "INSERT INTO customers " +
                "(name, contact, address, last_purchase_date, date_registered) " +
                "VALUES (?, ?, ?, ?, ?)";

        String today = LocalDate.now().toString();

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            if (conn == null) {
                throw new SQLException("Unable to connect to database.");
            }

            pstmt.setString(1, cleanName);
            pstmt.setString(2, "");
            pstmt.setString(3, "");
            pstmt.setString(4, "N/A");
            pstmt.setString(5, today);

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Unable to create customer record.");
    }

    public static void updateCustomer(Customer customer) {

        String sql =
                "UPDATE customers SET " +
                "name = ?, " +
                "contact = ?, " +
                "address = ? " +
                "WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getContact());
            pstmt.setString(3, customer.getAddress());
            pstmt.setInt(4, customer.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteCustomer(int id) {

        String sql =
                "DELETE FROM customers WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Customer> getAllCustomers() {

        List<Customer> customers = new ArrayList<>();

        String sql =
                "SELECT * FROM customers ORDER BY id DESC";

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

        String sql =
                "SELECT * FROM customers " +
                "WHERE LOWER(name) LIKE LOWER(?) " +
                "OR LOWER(contact) LIKE LOWER(?) " +
                "ORDER BY id DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(sql)) {

            String searchPattern =
                    "%" + (keyword == null ? "" : keyword.trim()) + "%";

            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

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

        LocalDate thresholdDate =
                LocalDate.now().minusDays(days);

        String sql =
                "SELECT * FROM customers " +
                "WHERE last_purchase_date IS NULL " +
                "OR last_purchase_date = 'N/A' " +
                "OR last_purchase_date = '' " +
                "OR substr(last_purchase_date,1,10) <= ? " +
                "ORDER BY id DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(sql)) {

            pstmt.setString(
                    1,
                    thresholdDate.toString()
            );

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

        String sql =
                "SELECT order_no, purchase_date, amount " +
                "FROM purchase_history " +
                "WHERE customer_id = ? " +
                "ORDER BY purchase_date DESC, id DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    history.add(
                            new String[]{
                                    rs.getString("order_no"),
                                    rs.getString("purchase_date"),
                                    String.format(
                                            "₱ %.2f",
                                            rs.getDouble("amount")
                                    )
                            }
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return history;
    }

    public static int getCustomerIdByName(
            String firstName,
            String lastName) {

        String fullName =
                ((firstName == null ? "" : firstName.trim()) +
                " " +
                (lastName == null ? "" : lastName.trim()))
                .trim();

        return getCustomerIdByNameIgnoreCase(fullName);
    }

    public static int getCustomerIdByNameIgnoreCase(
            String firstName,
            String lastName) {

        String fullName =
                ((firstName == null ? "" : firstName.trim()) +
                " " +
                (lastName == null ? "" : lastName.trim()))
                .trim();

        return getCustomerIdByNameIgnoreCase(fullName);
    }

    public static int getCustomerIdByNameIgnoreCase(
            String fullName) {

        if (fullName == null ||
                fullName.trim().isEmpty()) {

            return -1;
        }

        String sql =
                "SELECT id FROM customers " +
                "WHERE LOWER(TRIM(name)) = LOWER(TRIM(?)) " +
                "LIMIT 1";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(sql)) {

            pstmt.setString(
                    1,
                    fullName.trim()
            );

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

    public static String getCustomerNameById(int customerId) {

        String sql =
                "SELECT name FROM customers WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getString("name");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "Unknown Customer";
    }

    public static void recordPurchase(
            int customerId,
            String orderNo,
            String purchaseDate,
            double amount) {

        String sql =
                "INSERT INTO purchase_history " +
                "(customer_id, order_no, purchase_date, amount) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            pstmt.setString(2, orderNo);
            pstmt.setString(3, purchaseDate);
            pstmt.setDouble(4, amount);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateLastPurchaseDate(
            int customerId,
            String purchaseDate) {

        String sql =
                "UPDATE customers " +
                "SET last_purchase_date = ? " +
                "WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(sql)) {

            pstmt.setString(1, purchaseDate);
            pstmt.setInt(2, customerId);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Customer mapRow(
            ResultSet rs) throws SQLException {

        Customer customer = new Customer();

        customer.setId(
                rs.getInt("id")
        );

        customer.setName(
                safeGetString(rs, "name")
        );

        customer.setContact(
                safeGetString(rs, "contact")
        );

        customer.setLastPurchaseDate(
                safeGetString(
                        rs,
                        "last_purchase_date"
                )
        );

        customer.setAddress(
                safeGetString(
                        rs,
                        "address"
                )
        );

        customer.setDateRegistered(
                safeGetString(
                        rs,
                        "date_registered"
                )
        );

        return customer;
    }

    private static String safeGetString(
            ResultSet rs,
            String column) {

        try {

            String value =
                    rs.getString(column);

            return value == null
                    ? ""
                    : value;

        } catch (SQLException e) {

            return "";
        }
    }
}