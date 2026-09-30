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
        String sql = "INSERT INTO medicines (name, buy_price, sell_price, stock, expirydate, company_name, medicine_category) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
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
        String sql = "UPDATE medicines SET name = ?, buy_price = ?, sell_price = ?, stock = ?, " +
                     "expirydate = ?, company_name = ?, medicine_category = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
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
        String sql = "DELETE FROM medicines WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Medicine> getAllMedicines() {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT * FROM medicines";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Medicine m = mapResultSetToMedicine(rs);
                medicines.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medicines;
    }

    public static int getLowStockCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM medicines WHERE stock < 5";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
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
        // Gumamit ng 'localtime' para tumugma sa lokal na petsa ng computer[cite: 12]
        String sql = "SELECT COUNT(*) FROM medicines WHERE expirydate <= DATE('now', 'localtime')";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    private static Medicine mapResultSetToMedicine(ResultSet rs) throws SQLException {
        Medicine m = new Medicine();
        m.setId(rs.getInt("id"));
        m.setName(rs.getString("name"));
        m.setBuyPrice(rs.getDouble("buy_price"));
        m.setSellPrice(rs.getDouble("sell_price"));
        m.setStock(rs.getInt("stock"));
        m.setExpiryDate(rs.getString("expirydate"));
        m.setCompanyName(rs.getString("company_name"));
        m.setMedicineCategory(rs.getString("medicine_category"));
        return m;
    }

    public static void decreaseStock(String medicineName, int qtySold) {
        String sql = "UPDATE medicines SET stock = stock - ? WHERE name = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, qtySold);
            pstmt.setString(2, medicineName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}