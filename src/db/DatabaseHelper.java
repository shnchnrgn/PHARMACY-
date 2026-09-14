package db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseHelper {
    private static final String URL = "jdbc:sqlite:pharmacy.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(URL);
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
        return conn;
    }

    public static void createTables() {
        String sqlMedicines = "CREATE TABLE IF NOT EXISTS medicines (" +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "name TEXT, " +
                                "price REAL, " +
                                "stock INTEGER);";
        
        String sqlCustomers = "CREATE TABLE IF NOT EXISTS customers (" +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "name TEXT, " +
                                "contact TEXT, " +
                                "last_purchase_date TEXT);";

        try (Statement stmt = connect().createStatement()) {
            stmt.execute(sqlMedicines);
            stmt.execute(sqlCustomers);
            System.out.println("Tables created successfully.");
        } catch (Exception e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }
}