package db;

import models.Medicine;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicineDAO {

    public static void addMedicine(Medicine medicine) {
        insertMedicine(medicine);
    }

    public static void insertMedicine(Medicine medicine) {
        String sql =
                "INSERT INTO medicines " +
                "(name, buy_price, sell_price, stock, expirydate, company_name, medicine_category) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, medicine.getName());
            pstmt.setDouble(2, medicine.getBuyPrice());
            pstmt.setDouble(3, medicine.getSellPrice());
            pstmt.setInt(4, medicine.getStock());
            pstmt.setString(5, medicine.getExpiryDate());
            pstmt.setString(6, medicine.getCompanyName());
            pstmt.setString(7, medicine.getMedicineCategory());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateMedicine(Medicine medicine) {
        String sql =
                "UPDATE medicines SET " +
                "name = ?, " +
                "buy_price = ?, " +
                "sell_price = ?, " +
                "stock = ?, " +
                "expirydate = ?, " +
                "company_name = ?, " +
                "medicine_category = ? " +
                "WHERE id = ?";

        try (
                Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, medicine.getName());
            pstmt.setDouble(2, medicine.getBuyPrice());
            pstmt.setDouble(3, medicine.getSellPrice());
            pstmt.setInt(4, medicine.getStock());
            pstmt.setString(5, medicine.getExpiryDate());
            pstmt.setString(6, medicine.getCompanyName());
            pstmt.setString(7, medicine.getMedicineCategory());
            pstmt.setInt(8, medicine.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMedicine(int id) {
        String sql =
                "DELETE FROM medicines WHERE id = ?";

        try (
                Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Medicine> getAllMedicines() {
        List<Medicine> medicines = new ArrayList<>();

        String sql =
                "SELECT * FROM medicines";

        try (
                Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            while (rs.next()) {
                medicines.add(
                        mapResultSetToMedicine(rs)
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return medicines;
    }

    public static int getLowStockCount() {
        int count = 0;

        String sql =
                "SELECT COUNT(*) " +
                "FROM medicines " +
                "WHERE stock < 5";

        try (
                Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public static int getExpiredCount() {
        int count = 0;

        String sql =
                "SELECT COUNT(*) " +
                "FROM medicines " +
                "WHERE expirydate <= DATE('now', 'localtime')";

        try (
                Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public static List<Medicine> getExpiredMedicines() {
        List<Medicine> medicines = new ArrayList<>();

        String sql =
                "SELECT * FROM medicines " +
                "WHERE expirydate <= DATE('now', 'localtime') " +
                "ORDER BY expirydate ASC";

        try (
                Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            while (rs.next()) {
                medicines.add(
                        mapResultSetToMedicine(rs)
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return medicines;
    }

    private static Medicine mapResultSetToMedicine(
            ResultSet rs
    ) throws SQLException {

        Medicine medicine = new Medicine();

        medicine.setId(
                rs.getInt("id")
        );

        medicine.setName(
                rs.getString("name")
        );

        medicine.setBuyPrice(
                rs.getDouble("buy_price")
        );

        medicine.setSellPrice(
                rs.getDouble("sell_price")
        );

        medicine.setStock(
                rs.getInt("stock")
        );

        medicine.setExpiryDate(
                rs.getString("expirydate")
        );

        medicine.setCompanyName(
                rs.getString("company_name")
        );

        medicine.setMedicineCategory(
                rs.getString("medicine_category")
        );

        return medicine;
    }

    public static void decreaseStock(
            String medicineName,
            int qtySold
    ) {

        String sql =
                "UPDATE medicines " +
                "SET stock = stock - ? " +
                "WHERE name = ?";

        try (
                Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, qtySold);
            pstmt.setString(2, medicineName);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}