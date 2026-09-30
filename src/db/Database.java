package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String URL = "jdbc:sqlite:pharmacy.db";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Database Connection Error: " + e.getMessage());
            return null;
        }
    }

    public static void createTables() {

        String medicinesSql =
                "CREATE TABLE IF NOT EXISTS medicines (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "price REAL NOT NULL DEFAULT 0, " +
                "stock INTEGER NOT NULL DEFAULT 0, " +
                "expiry_date TEXT" +
                ");";

        String customersSql =
                "CREATE TABLE IF NOT EXISTS customers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "last_name TEXT, " +
                "first_name TEXT, " +
                "contact TEXT, " +
                "last_purchase_date TEXT, " +
                "address TEXT, " +
                "date_registered TEXT, " +
                "pwd_id TEXT, " +
                "senior_citizen_id TEXT, " +
                "discount_type TEXT DEFAULT 'No Discount', " +
                "discount_percent REAL DEFAULT 0" +
                ");";

        String purchaseHistorySql =
                "CREATE TABLE IF NOT EXISTS purchase_history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customer_id INTEGER, " +
                "order_no TEXT NOT NULL, " +
                "purchase_date TEXT NOT NULL, " +
                "amount REAL NOT NULL, " +
                "FOREIGN KEY (customer_id) REFERENCES customers(id)" +
                ");";

        String salesSql =
                "CREATE TABLE IF NOT EXISTS sales (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customer_id INTEGER NOT NULL, " +
                "order_no TEXT NOT NULL UNIQUE, " +
                "purchase_date TEXT NOT NULL, " +
                "amount REAL NOT NULL, " +
                "is_pwd INTEGER NOT NULL DEFAULT 0, " +
                "pwd_id TEXT, " +
                "FOREIGN KEY (customer_id) REFERENCES customers(id)" +
                ");";

        String saleItemsSql =
                "CREATE TABLE IF NOT EXISTS sale_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sale_id INTEGER NOT NULL, " +
                "medicine_id INTEGER NOT NULL, " +
                "quantity INTEGER NOT NULL, " +
                "unit_price REAL NOT NULL, " +
                "subtotal REAL NOT NULL, " +
                "FOREIGN KEY (sale_id) REFERENCES sales(id), " +
                "FOREIGN KEY (medicine_id) REFERENCES medicines(id)" +
                ");";

        String expensesSql =
                "CREATE TABLE IF NOT EXISTS expenses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "description TEXT NOT NULL, " +
                "amount REAL NOT NULL, " +
                "expense_date TEXT NOT NULL" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(medicinesSql);
            stmt.execute(customersSql);
            stmt.execute(purchaseHistorySql);
            stmt.execute(salesSql);
            stmt.execute(saleItemsSql);
            stmt.execute(expensesSql);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        addColumnIfMissing("customers", "last_name", "TEXT");
        addColumnIfMissing("customers", "first_name", "TEXT");
        addColumnIfMissing("customers", "address", "TEXT");
        addColumnIfMissing("customers", "date_registered", "TEXT");
        addColumnIfMissing("customers", "pwd_id", "TEXT");
        addColumnIfMissing("customers", "senior_citizen_id", "TEXT");
        addColumnIfMissing("customers", "discount_type", "TEXT DEFAULT 'No Discount'");
        addColumnIfMissing("customers", "discount_percent", "REAL DEFAULT 0");

        migrateCustomerNames();

        addColumnIfMissing("medicines", "expirydate", "TEXT");
        addColumnIfMissing("medicines", "category", "TEXT");
        addColumnIfMissing("medicines", "buy_price", "REAL");
        addColumnIfMissing("medicines", "sell_price", "REAL");
        addColumnIfMissing("medicines", "company_name", "TEXT");
        addColumnIfMissing("medicines", "medicine_category", "TEXT");
    }

    private static void migrateCustomerNames() {

        String checkSql =
                "SELECT id, name, first_name, last_name " +
                "FROM customers";

        String updateSql =
                "UPDATE customers " +
                "SET first_name = ?, last_name = ? " +
                "WHERE id = ?";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {

            java.util.List<CustomerNameMigration> migrations =
                    new java.util.ArrayList<>();

            while (rs.next()) {

                String oldName = rs.getString("name");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");

                if ((firstName == null || firstName.trim().isEmpty()) &&
                    (lastName == null || lastName.trim().isEmpty()) &&
                    oldName != null &&
                    !oldName.trim().isEmpty()) {

                    String[] parts = oldName.trim().split("\\s+", 2);
                    String migratedFirstName = parts[0];
                    String migratedLastName = parts.length > 1 ? parts[1] : "";

                    migrations.add(new CustomerNameMigration(
                            rs.getInt("id"),
                            migratedFirstName,
                            migratedLastName));
                }
            }

            try (java.sql.PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                for (CustomerNameMigration migration : migrations) {
                    pstmt.setString(1, migration.firstName);
                    pstmt.setString(2, migration.lastName);
                    pstmt.setInt(3, migration.id);
                    pstmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Customer name migration note: " + e.getMessage());
        }
    }

    private static void addColumnIfMissing(
            String table,
            String column,
            String type) {

        String sql =
                "ALTER TABLE " + table +
                " ADD COLUMN " + column + " " + type;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

        } catch (SQLException e) {
            if (e.getMessage() == null ||
                !e.getMessage().toLowerCase().contains("duplicate")) {

                System.out.println(
                        "Migration note (" + column + "): " + e.getMessage());
            }
        }
    }

    private static class CustomerNameMigration {
        int id;
        String firstName;
        String lastName;

        CustomerNameMigration(int id, String firstName, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }
}
