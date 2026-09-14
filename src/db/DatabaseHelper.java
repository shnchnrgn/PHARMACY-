package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseHelper {
    private static final String URL = "jdbc:sqlite:pharmacy.db";
    private static Connection connection;

    public static Connection connect() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static Connection getConnection() {
        return connect();
    }

    public static void createTables() {
        String sqlMedicines = "CREATE TABLE IF NOT EXISTS medicines (" +
                              "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                              "name TEXT, " +
                              "price REAL, " +
                              "stock INTEGER)";

        String sqlCustomers = "CREATE TABLE IF NOT EXISTS customers (" +
                              "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                              "name TEXT, " +
                              "contact TEXT, " +
                              "last_purchase_date TEXT)";

        String sqlUsers = "CREATE TABLE IF NOT EXISTS users (" +
                          "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                          "email TEXT UNIQUE, " +
                          "password TEXT)";

        try (Statement stmt = connect().createStatement()) {
            stmt.execute(sqlMedicines);
            stmt.execute(sqlCustomers);
            stmt.execute(sqlUsers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}