package db;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SalesDAO {

    public static class SaleItemData {

        public int medicineId;
        public int quantity;
        public double unitPrice;

        public SaleItemData(
                int medicineId,
                int quantity,
                double unitPrice) {

            this.medicineId = medicineId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
    }

    public static void completeSale(
            int customerId,
            String orderNo,
            String purchaseDate,
            double amount,
            boolean isPwd,
            String pwdId,
            List<SaleItemData> items) throws SQLException {

        String saleSql =
                "INSERT INTO sales " +
                "(customer_id, order_no, purchase_date, amount, is_pwd, pwd_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        String itemSql =
                "INSERT INTO sale_items " +
                "(sale_id, medicine_id, quantity, unit_price, subtotal) " +
                "VALUES (?, ?, ?, ?, ?)";

        String stockSql =
                "SELECT stock FROM medicines WHERE id = ?";

        String decreaseStockSql =
                "UPDATE medicines SET stock = stock - ? WHERE id = ?";

        String historySql =
                "INSERT INTO purchase_history " +
                "(customer_id, order_no, purchase_date, amount) " +
                "VALUES (?, ?, ?, ?)";

        String customerSql =
                "UPDATE customers SET last_purchase_date = ? WHERE id = ?";

        Connection conn = Database.getConnection();

        if (conn == null) {
            throw new SQLException(
                    "Unable to connect to database."
            );
        }

        try {

            conn.setAutoCommit(false);

            long saleId;

            try (PreparedStatement pstmt =
                         conn.prepareStatement(
                                 saleSql,
                                 Statement.RETURN_GENERATED_KEYS)) {

                pstmt.setInt(1, customerId);
                pstmt.setString(2, orderNo);
                pstmt.setString(3, purchaseDate);
                pstmt.setDouble(4, amount);
                pstmt.setInt(5, isPwd ? 1 : 0);
                pstmt.setString(6, pwdId);

                pstmt.executeUpdate();

                try (ResultSet rs =
                             pstmt.getGeneratedKeys()) {

                    if (!rs.next()) {
                        throw new SQLException(
                                "Unable to create sale record."
                        );
                    }

                    saleId = rs.getLong(1);
                }
            }

            try (PreparedStatement stockCheck =
                         conn.prepareStatement(stockSql);
                 PreparedStatement itemStatement =
                         conn.prepareStatement(itemSql);
                 PreparedStatement decreaseStock =
                         conn.prepareStatement(decreaseStockSql)) {

                for (SaleItemData item : items) {

                    int stock = -1;

                    stockCheck.setInt(
                            1,
                            item.medicineId
                    );

                    try (ResultSet rs =
                                 stockCheck.executeQuery()) {

                        if (rs.next()) {
                            stock = rs.getInt("stock");
                        }
                    }

                    if (stock < 0) {
                        throw new SQLException(
                                "Medicine ID " +
                                item.medicineId +
                                " was not found."
                        );
                    }

                    if (stock < item.quantity) {
                        throw new SQLException(
                                "Insufficient stock for medicine ID " +
                                item.medicineId +
                                "."
                        );
                    }

                    double subtotal =
                            item.unitPrice *
                            item.quantity;

                    itemStatement.setLong(
                            1,
                            saleId
                    );

                    itemStatement.setInt(
                            2,
                            item.medicineId
                    );

                    itemStatement.setInt(
                            3,
                            item.quantity
                    );

                    itemStatement.setDouble(
                            4,
                            item.unitPrice
                    );

                    itemStatement.setDouble(
                            5,
                            subtotal
                    );

                    itemStatement.executeUpdate();

                    decreaseStock.setInt(
                            1,
                            item.quantity
                    );

                    decreaseStock.setInt(
                            2,
                            item.medicineId
                    );

                    decreaseStock.executeUpdate();
                }
            }

            try (PreparedStatement pstmt =
                         conn.prepareStatement(historySql)) {

                pstmt.setInt(
                        1,
                        customerId
                );

                pstmt.setString(
                        2,
                        orderNo
                );

                pstmt.setString(
                        3,
                        purchaseDate
                );

                pstmt.setDouble(
                        4,
                        amount
                );

                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt =
                         conn.prepareStatement(customerSql)) {

                pstmt.setString(
                        1,
                        purchaseDate
                );

                pstmt.setInt(
                        2,
                        customerId
                );

                pstmt.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {

            try {
                conn.rollback();
            } catch (SQLException ignored) {
            }

            throw e;

        } finally {

            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {
            }

            conn.close();
        }
    }

    public static void addSale(
            String description,
            double amount,
            String purchaseDate) throws SQLException {

        String orderNo = "ORD-" + System.currentTimeMillis();

        String sql =
                "INSERT INTO sales " +
                "(order_no, purchase_date, amount) " +
                "VALUES (?, ?, ?)";

        try (Connection conn =
                     Database.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(sql)) {

            pstmt.setString(1, orderNo + " (" + description + ")");
            pstmt.setString(2, purchaseDate);
            pstmt.setDouble(3, amount);

            pstmt.executeUpdate();
        }
    }

    public static void deleteSale(
            int id) throws SQLException {

        String sql =
                "DELETE FROM sales " +
                "WHERE id = ?";

        try (Connection conn =
                     Database.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            pstmt.executeUpdate();
        }
    }

    public static List<String[]> getAllSales() {

        List<String[]> sales =
                new ArrayList<>();

        String sql =
                "SELECT " +
                "id, " +
                "purchase_date, " +
                "COALESCE(order_no, 'Direct Sale') AS description, " +
                "amount " +
                "FROM sales " +
                "ORDER BY id DESC";

        try (Connection conn =
                     Database.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(sql);
             ResultSet rs =
                     pstmt.executeQuery()) {

            while (rs.next()) {

                sales.add(
                        new String[]{
                                String.valueOf(rs.getInt("id")),
                                rs.getString("purchase_date"),
                                rs.getString("description"),
                                String.format("%.2f", rs.getDouble("amount"))
                        }
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sales;
    }

    public static int getTodaySalesCount() {

        String today =
                LocalDate.now().toString();

        String sql =
                "SELECT COUNT(*) " +
                "FROM sales " +
                "WHERE substr(purchase_date,1,10) = ?";

        return getInt(
                sql,
                today
        );
    }

    public static double getTodaySalesTotal() {

        String today =
                LocalDate.now().toString();

        String sql =
                "SELECT COALESCE(SUM(amount),0) " +
                "FROM sales " +
                "WHERE substr(purchase_date,1,10) = ?";

        return getDouble(
                sql,
                today
        );
    }

    public static int getThisMonthSalesCount() {

        String month =
                LocalDate.now()
                        .withDayOfMonth(1)
                        .toString();

        String nextMonth =
                LocalDate.now()
                        .withDayOfMonth(1)
                        .plusMonths(1)
                        .toString();

        String sql =
                "SELECT COUNT(*) " +
                "FROM sales " +
                "WHERE substr(purchase_date,1,10) >= ? " +
                "AND substr(purchase_date,1,10) < ?";

        return getInt(
                sql,
                month,
                nextMonth
        );
    }

    public static double getThisMonthSalesTotal() {

        String month =
                LocalDate.now()
                        .withDayOfMonth(1)
                        .toString();

        String nextMonth =
                LocalDate.now()
                        .withDayOfMonth(1)
                        .plusMonths(1)
                        .toString();

        String sql =
                "SELECT COALESCE(SUM(amount),0) " +
                "FROM sales " +
                "WHERE substr(purchase_date,1,10) >= ? " +
                "AND substr(purchase_date,1,10) < ?";

        return getDouble(
                sql,
                month,
                nextMonth
        );
    }

    public static double getThisYearSalesTotal() {

        String yearStart =
                LocalDate.now()
                        .withDayOfYear(1)
                        .toString();

        String nextYearStart =
                LocalDate.now()
                        .withDayOfYear(1)
                        .plusYears(1)
                        .toString();

        String sql =
                "SELECT COALESCE(SUM(amount),0) " +
                "FROM sales " +
                "WHERE substr(purchase_date,1,10) >= ? " +
                "AND substr(purchase_date,1,10) < ?";

        return getDouble(
                sql,
                yearStart,
                nextYearStart
        );
    }

    public static double getThisMonthSalesProfit() {

        String month =
                LocalDate.now()
                        .withDayOfMonth(1)
                        .toString();

        String nextMonth =
                LocalDate.now()
                        .withDayOfMonth(1)
                        .plusMonths(1)
                        .toString();

        String sql =
                "SELECT COALESCE(" +
                "SUM((" +
                "si.unit_price - COALESCE(m.buy_price,0)" +
                ") * si.quantity),0) " +
                "FROM sale_items si " +
                "INNER JOIN sales s " +
                "ON s.id = si.sale_id " +
                "INNER JOIN medicines m " +
                "ON m.id = si.medicine_id " +
                "WHERE substr(s.purchase_date,1,10) >= ? " +
                "AND substr(s.purchase_date,1,10) < ?";

        return getDouble(
                sql,
                month,
                nextMonth
        );
    }

    public static double getThisYearSalesProfit() {

        String yearStart =
                LocalDate.now()
                        .withDayOfYear(1)
                        .toString();

        String nextYearStart =
                LocalDate.now()
                        .withDayOfYear(1)
                        .plusYears(1)
                        .toString();

        String sql =
                "SELECT COALESCE(" +
                "SUM((" +
                "si.unit_price - COALESCE(m.buy_price,0)" +
                ") * si.quantity),0) " +
                "FROM sale_items si " +
                "INNER JOIN sales s " +
                "ON s.id = si.sale_id " +
                "INNER JOIN medicines m " +
                "ON m.id = si.medicine_id " +
                "WHERE substr(s.purchase_date,1,10) >= ? " +
                "AND substr(s.purchase_date,1,10) < ?";

        return getDouble(
                sql,
                yearStart,
                nextYearStart
        );
    }

    public static void addExpense(
            String description,
            double amount,
            String expenseDate) throws SQLException {

        String sql =
                "INSERT INTO expenses " +
                "(description, amount, expense_date) " +
                "VALUES (?, ?, ?)";

        try (Connection conn =
                     Database.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(sql)) {

            pstmt.setString(
                    1,
                    description
            );

            pstmt.setDouble(
                    2,
                    amount
            );

            pstmt.setString(
                    3,
                    expenseDate
            );

            pstmt.executeUpdate();
        }
    }

    public static int getThisMonthExpenseCount() {

        String month =
                LocalDate.now()
                        .withDayOfMonth(1)
                        .toString();

        String nextMonth =
                LocalDate.now()
                        .withDayOfMonth(1)
                        .plusMonths(1)
                        .toString();

        String sql =
                "SELECT COUNT(*) " +
                "FROM expenses " +
                "WHERE substr(expense_date,1,10) >= ? " +
                "AND substr(expense_date,1,10) < ?";

        return getInt(
                sql,
                month,
                nextMonth
        );
    }

    public static double getThisMonthExpenseTotal() {

        String month =
                LocalDate.now()
                        .withDayOfMonth(1)
                        .toString();

        String nextMonth =
                LocalDate.now()
                        .withDayOfMonth(1)
                        .plusMonths(1)
                        .toString();

        String sql =
                "SELECT COALESCE(SUM(amount),0) " +
                "FROM expenses " +
                "WHERE substr(expense_date,1,10) >= ? " +
                "AND substr(expense_date,1,10) < ?";

        return getDouble(
                sql,
                month,
                nextMonth
        );
    }

    public static double getThisYearExpenseTotal() {

        String yearStart =
                LocalDate.now()
                        .withDayOfYear(1)
                        .toString();

        String nextYearStart =
                LocalDate.now()
                        .withDayOfYear(1)
                        .plusYears(1)
                        .toString();

        String sql =
                "SELECT COALESCE(SUM(amount),0) " +
                "FROM expenses " +
                "WHERE substr(expense_date,1,10) >= ? " +
                "AND substr(expense_date,1,10) < ?";

        return getDouble(
                sql,
                yearStart,
                nextYearStart
        );
    }

    public static double getTodayExpenseTotal() {

        String today =
                LocalDate.now().toString();

        String sql =
                "SELECT COALESCE(SUM(amount),0) " +
                "FROM expenses " +
                "WHERE substr(expense_date,1,10) = ?";

        return getDouble(
                sql,
                today
        );
    }

    public static List<String[]> getLatestSales(
            int limit) {

        List<String[]> sales =
                new ArrayList<>();

        String sql =
                "SELECT " +
                "s.order_no, " +
                "s.purchase_date, " +
                "s.amount, " +
                "c.name " +
                "FROM sales s " +
                "LEFT JOIN customers c " +
                "ON s.customer_id = c.id " +
                "ORDER BY s.id DESC " +
                "LIMIT ?";

        try (Connection conn =
                     Database.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(sql)) {

            pstmt.setInt(
                    1,
                    limit
            );

            try (ResultSet rs =
                         pstmt.executeQuery()) {

                while (rs.next()) {

                    sales.add(
                            new String[]{
                                    rs.getString(
                                            "order_no"
                                    ),
                                    rs.getString(
                                            "purchase_date"
                                    ),
                                    String.format(
                                            "%.2f",
                                            rs.getDouble(
                                                    "amount"
                                            )
                                    ),
                                    rs.getString(
                                            "name"
                                    )
                            }
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sales;
    }

    public static List<String[]> getAllExpenses() {

        List<String[]> expenses =
                new ArrayList<>();

        String sql =
                "SELECT " +
                "id, " +
                "expense_date, " +
                "description, " +
                "amount " +
                "FROM expenses " +
                "ORDER BY id DESC";

        try (Connection conn =
                     Database.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(sql);
             ResultSet rs =
                     pstmt.executeQuery()) {

            while (rs.next()) {

                expenses.add(
                        new String[]{
                                String.valueOf(
                                        rs.getInt("id")
                                ),
                                rs.getString(
                                        "expense_date"
                                ),
                                rs.getString(
                                        "description"
                                ),
                                String.format(
                                        "%.2f",
                                        rs.getDouble(
                                                "amount"
                                        )
                                )
                        }
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return expenses;
    }

    public static void deleteExpense(
            int id) throws SQLException {

        String sql =
                "DELETE FROM expenses " +
                "WHERE id = ?";

        try (Connection conn =
                     Database.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(sql)) {

            pstmt.setInt(
                    1,
                    id
            );

            pstmt.executeUpdate();
        }
    }

    public static List<String[]> getMostPurchasedOverall(int limit) {

        List<String[]> list = new ArrayList<>();

        String sql =
                "SELECT " +
                "m.name, " +
                "COALESCE(m.category, 'N/A') AS category, " +
                "COALESCE(m.company_name, 'N/A') AS company, " +
                "SUM(si.quantity) AS total_sold " +
                "FROM sale_items si " +
                "INNER JOIN medicines m ON si.medicine_id = m.id " +
                "GROUP BY m.id, m.name, m.category, m.company_name " +
                "ORDER BY total_sold DESC " +
                "LIMIT ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(
                            new String[]{
                                    rs.getString("name"),
                                    rs.getString("category"),
                                    rs.getString("company"),
                                    String.valueOf(rs.getInt("total_sold"))
                            }
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<String[]> getMostPurchasedByCategory() {

        List<String[]> list = new ArrayList<>();

        String sql =
                "SELECT category, medicine_name, max_sold FROM (" +
                "    SELECT COALESCE(m.category, 'Uncategorized') AS category, " +
                "           m.name AS medicine_name, " +
                "           SUM(si.quantity) AS max_sold, " +
                "           ROW_NUMBER() OVER (PARTITION BY COALESCE(m.category, 'Uncategorized') ORDER BY SUM(si.quantity) DESC) as rn " +
                "    FROM sale_items si " +
                "    INNER JOIN medicines m ON si.medicine_id = m.id " +
                "    GROUP BY COALESCE(m.category, 'Uncategorized'), m.id, m.name " +
                ") t WHERE rn = 1 ORDER BY max_sold DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(
                        new String[]{
                                rs.getString("category"),
                                rs.getString("medicine_name"),
                                String.valueOf(rs.getInt("max_sold"))
                        }
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<String[]> getMostPurchasedByCompany() {

        List<String[]> list = new ArrayList<>();

        String sql =
                "SELECT company_name, medicine_name, max_sold FROM (" +
                "    SELECT COALESCE(m.company_name, 'Unknown Company') AS company_name, " +
                "           m.name AS medicine_name, " +
                "           SUM(si.quantity) AS max_sold, " +
                "           ROW_NUMBER() OVER (PARTITION BY COALESCE(m.company_name, 'Unknown Company') ORDER BY SUM(si.quantity) DESC) as rn " +
                "    FROM sale_items si " +
                "    INNER JOIN medicines m ON si.medicine_id = m.id " +
                "    GROUP BY COALESCE(m.company_name, 'Unknown Company'), m.id, m.name " +
                ") t WHERE rn = 1 ORDER BY max_sold DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(
                        new String[]{
                                rs.getString("company_name"),
                                rs.getString("medicine_name"),
                                String.valueOf(rs.getInt("max_sold"))
                        }
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private static int getInt(
            String sql,
            String... params) {

        try (Connection conn =
                     Database.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(sql)) {

            for (int i = 0;
                 i < params.length;
                 i++) {

                pstmt.setString(
                        i + 1,
                        params[i]
                );
            }

            try (ResultSet rs =
                         pstmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static double getDouble(
            String sql,
            String... params) {

        try (Connection conn =
                     Database.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(sql)) {

            for (int i = 0;
                 i < params.length;
                 i++) {

                pstmt.setString(
                        i + 1,
                        params[i]
                );
            }

            try (ResultSet rs =
                         pstmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}