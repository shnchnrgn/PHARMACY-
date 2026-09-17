package db;
import models.Medicine;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicineDAO {

    public static final int LOW_STOCK_THRESHOLD = 10;

    public static void insertMedicine(Medicine medicine) {
        String sql = "INSERT INTO medicines(name, price, stock) VALUES(?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, medicine.getName());
            pstmt.setDouble(2, medicine.getPrice());
            pstmt.setInt(3, medicine.getStock());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error inserting medicine: " + e.getMessage());
        }
    }

    public static boolean updateMedicine(Medicine medicine) {
        String sql = "UPDATE medicines SET name = ?, price = ?, stock = ? WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, medicine.getName());
            pstmt.setDouble(2, medicine.getPrice());
            pstmt.setInt(3, medicine.getStock());
            pstmt.setInt(4, medicine.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating medicine: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteMedicine(int id) {
        String sql = "DELETE FROM medicines WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting medicine: " + e.getMessage());
            return false;
        }
    }

    public static List<Medicine> getAllMedicines() {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines";
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching medicines: " + e.getMessage());
        }
        return list;
    }

    
    public static List<Medicine> searchMedicines(String keyword) {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE name LIKE ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error searching medicines: " + e.getMessage());
        }
        return list;
    }

    
    public static List<Medicine> getLowStockMedicines(int threshold) {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE stock <= ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, threshold);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching low stock medicines: " + e.getMessage());
        }
        return list;
    }

    private static Medicine mapRow(ResultSet rs) throws SQLException {
        Medicine m = new Medicine();
        m.setId(rs.getInt("id"));
        m.setName(rs.getString("name"));
        m.setPrice(rs.getDouble("price"));
        m.setStock(rs.getInt("stock"));
        return m;
    }
}