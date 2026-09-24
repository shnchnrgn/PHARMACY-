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
                "price REAL NOT NULL, " +
                "stock INTEGER NOT NULL, " +
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

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(medicinesSql);
            stmt.execute(customersSql);
            stmt.execute(purchaseHistorySql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}