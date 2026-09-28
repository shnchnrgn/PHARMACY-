package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class SalesDAO {

    public static class SaleItemData {
        private final int medicineId;
        private final int quantity;
        private final double unitPrice;

        public SaleItemData(int medicineId, int quantity, double unitPrice) {
            this.medicineId = medicineId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public int getMedicineId() {
            return medicineId;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getUnitPrice() {
            return unitPrice;
        }
    }

    public static boolean completeSale(int customerId, String orderNo, String purchaseDate,
                                       double amount, boolean isPwd, String pwdId,
                                       List<SaleItemData> items) throws SQLException {
        if (items == null || items.isEmpty()) {
            throw new SQLException("The sale has no items.");
        }

        try (Connection conn = Database.getConnection()) {
            if (conn == null) {
                throw new SQLException("Unable to connect to the database.");
            }

            conn.setAutoCommit(false);

            try {
                int saleId;

                String saleSql = "INSERT INTO sales " +
                        "(customer_id, order_no, purchase_date, amount, is_pwd, pwd_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

                try (PreparedStatement saleStmt = conn.prepareStatement(saleSql, Statement.RETURN_GENERATED_KEYS)) {
                    saleStmt.setInt(1, customerId);
                    saleStmt.setString(2, orderNo);
                    saleStmt.setString(3, purchaseDate);
                    saleStmt.setDouble(4, amount);
                    saleStmt.setInt(5, isPwd ? 1 : 0);
                    saleStmt.setString(6, pwdId == null ? "" : pwdId);
                    saleStmt.executeUpdate();

                    try (ResultSet keys = saleStmt.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Unable to create the sale record.");
                        }
                        saleId = keys.getInt(1);
                    }
                }

                String stockSql = "SELECT stock FROM medicines WHERE id = ?";
                String itemSql = "INSERT INTO sale_items " +
                        "(sale_id, medicine_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";
                String updateStockSql = "UPDATE medicines SET stock = stock - ? WHERE id = ?";

                try (PreparedStatement stockStmt = conn.prepareStatement(stockSql);
                     PreparedStatement itemStmt = conn.prepareStatement(itemSql);
                     PreparedStatement updateStockStmt = conn.prepareStatement(updateStockSql)) {

                    for (SaleItemData item : items) {
                        if (item.getQuantity() <= 0) {
                            throw new SQLException("Invalid quantity for a medicine.");
                        }

                        stockStmt.setInt(1, item.getMedicineId());
                        int stock;

                        try (ResultSet rs = stockStmt.executeQuery()) {
                            if (!rs.next()) {
                                throw new SQLException("Medicine ID " + item.getMedicineId() + " was not found.");
                            }
                            stock = rs.getInt("stock");
                        }

                        if (stock < item.getQuantity()) {
                            throw new SQLException("Insufficient stock for medicine ID " + item.getMedicineId() + ". Available: " + stock + ".");
                        }

                        double subtotal = item.getUnitPrice() * item.getQuantity();

                        itemStmt.setInt(1, saleId);
                        itemStmt.setInt(2, item.getMedicineId());
                        itemStmt.setInt(3, item.getQuantity());
                        itemStmt.setDouble(4, item.getUnitPrice());
                        itemStmt.setDouble(5, subtotal);
                        itemStmt.executeUpdate();

                        updateStockStmt.setInt(1, item.getQuantity());
                        updateStockStmt.setInt(2, item.getMedicineId());
                        updateStockStmt.executeUpdate();
                    }
                }

                String historySql = "INSERT INTO purchase_history " +
                        "(customer_id, order_no, purchase_date, amount) VALUES (?, ?, ?, ?)";

                try (PreparedStatement historyStmt = conn.prepareStatement(historySql)) {
                    historyStmt.setInt(1, customerId);
                    historyStmt.setString(2, orderNo);
                    historyStmt.setString(3, purchaseDate);
                    historyStmt.setDouble(4, amount);
                    historyStmt.executeUpdate();
                }

                String customerSql = "UPDATE customers SET last_purchase_date = ? WHERE id = ?";
                try (PreparedStatement customerStmt = conn.prepareStatement(customerSql)) {
                    customerStmt.setString(1, purchaseDate);
                    customerStmt.setInt(2, customerId);
                    customerStmt.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }
                throw e;
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public static double getTodaySalesTotal() {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM sales WHERE date(purchase_date) = date('now', 'localtime')";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        } catch (SQLException e) {
            return 0.0;
        }
    }

    public static int getTodaySalesCount() {
        String sql = "SELECT COUNT(*) FROM sales WHERE date(purchase_date) = date('now', 'localtime')";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    public static java.util.List<String[]> getLatestSales(int limit) {
        java.util.List<String[]> sales = new java.util.ArrayList<>();
        String sql = "SELECT s.order_no, s.purchase_date, s.amount, c.name " +
                "FROM sales s LEFT JOIN customers c ON c.id = s.customer_id " +
                "ORDER BY s.id DESC LIMIT ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sales.add(new String[]{
                            rs.getString("order_no"),
                            rs.getString("purchase_date"),
                            String.format("%.2f", rs.getDouble("amount")),
                            rs.getString("name") == null ? "Walk-in" : rs.getString("name")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sales;
    }
}
