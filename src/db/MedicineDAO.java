package db;
import models.Medicine;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicineDAO {
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

    public static List<Medicine> getAllMedicines() {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines";
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Medicine m = new Medicine();
                m.setId(rs.getInt("id"));
                m.setName(rs.getString("name"));
                m.setPrice(rs.getDouble("price"));
                m.setStock(rs.getInt("stock"));
                list.add(m);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching medicines: " + e.getMessage());
        }
        return list;
    }
}