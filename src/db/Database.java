package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:sqlite:pharmacy.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(URL);
        } catch (Exception e) {
            System.out.println("Connection Error: " + e.getMessage());
        }
        return conn;
    }

    // Idagdag ito para gumana ang getConnection() na tinatawag ng UserDAO
    public static Connection getConnection() {
        return connect();
    }

    public static void createTables() {
        String medicineSql = "CREATE TABLE IF NOT EXISTS medicines (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "category TEXT, " +
                "buy_price REAL, " +
                "sell_price REAL, " +
                "stock INTEGER, " +
                "company TEXT, " +
                "expiry_date TEXT)";

        String customerSql = "CREATE TABLE IF NOT EXISTS customers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "last_name TEXT NOT NULL, " +
                "first_name TEXT NOT NULL, " +
                "middle_initial TEXT, " +
                "contact_number TEXT, " +
                "address TEXT, " +
                "discount_type TEXT, " +
                "pwd_id TEXT, " +
                "senior_citizen_id TEXT, " +
                "discount_percent REAL)";

        String salesSql = "CREATE TABLE IF NOT EXISTS sales (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customer_id INTEGER, " +
                "order_no TEXT, " +
                "date TEXT, " +
                "total_amount REAL, " +
                "is_pwd INTEGER, " +
                "pwd_id TEXT, " +
                "FOREIGN KEY(customer_id) REFERENCES customers(id))";

        String saleItemsSql = "CREATE TABLE IF NOT EXISTS sale_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sale_id INTEGER, " +
                "medicine_id INTEGER, " +
                "quantity INTEGER, " +
                "unit_price REAL, " +
                "FOREIGN KEY(sale_id) REFERENCES sales(id), " +
                "FOREIGN KEY(medicine_id) REFERENCES medicines(id))";

        String expenseSql = "CREATE TABLE IF NOT EXISTS expenses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "expense_name TEXT NOT NULL, " +
                "amount REAL NOT NULL, " +
                "date TEXT NOT NULL)";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(medicineSql);
            stmt.execute(customerSql);
            stmt.execute(salesSql);
            stmt.execute(saleItemsSql);
            stmt.execute(expenseSql);
        } catch (Exception e) {
            System.out.println("Table Creation Error: " + e.getMessage());
        }
    }
}