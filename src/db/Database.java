package db;

import java.sql.Connection;
import java.sql.DriverManager;
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
                "name TEXT NOT NULL, " +
                "contact TEXT, " +
                "last_purchase_date TEXT" +
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

        addColumnIfMissing("customers", "address", "TEXT");
        addColumnIfMissing("customers", "date_registered", "TEXT");

        addColumnIfMissing("medicines", "expirydate", "TEXT");
        addColumnIfMissing("medicines", "category", "TEXT");
        addColumnIfMissing("medicines", "buy_price", "REAL");
        addColumnIfMissing("medicines", "sell_price", "REAL");
        addColumnIfMissing("medicines", "company_name", "TEXT");
        addColumnIfMissing("medicines", "medicine_category", "TEXT");
    }

    private static void addColumnIfMissing(
            String table,
            String column,
            String type) {

        String sql =
                "ALTER TABLE " +
                table +
                " ADD COLUMN " +
                column +
                " " +
                type;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

        } catch (SQLException e) {

            if (e.getMessage() == null ||
                !e.getMessage()
                        .toLowerCase()
                        .contains("duplicate")) {

                System.out.println(
                        "Migration note (" +
                        column +
                        "): " +
                        e.getMessage()
                );
            }
        }
    }
}