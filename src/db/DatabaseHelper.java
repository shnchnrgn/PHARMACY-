package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {
    private static final String URL = "jdbc:sqlite:pharmacy.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void connect() {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Connected to SQLite database successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTables() {
        String createMedicinesTable = "CREATE TABLE IF NOT EXISTS medicines (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "stock INTEGER NOT NULL)";

        String createCustomersTable = "CREATE TABLE IF NOT EXISTS customers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "contact TEXT NOT NULL, " +
                "last_purchase_date TEXT)";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createMedicinesTable);
            stmt.execute(createCustomersTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}