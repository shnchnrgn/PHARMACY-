package ui;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class SharedData {
    public static double totalSalesToday = 0.0;
    public static List<String[]> latestSalesList = new ArrayList<>();
    public static DefaultTableModel dashboardSalesModel;

    public static List<String[]> medicineList = new ArrayList<>();
    public static DefaultTableModel medicineTableModel;

    public static void addMedicine(String[] newMed) {
        medicineList.add(newMed);

        if (medicineTableModel != null) {
            medicineTableModel.addRow(newMed);
        }
    }

    public static void addSale(String orderNo, String date, String amount, String customerName) {
        String[] saleEntry = {orderNo, date, "₱ " + amount, customerName};
        latestSalesList.add(0, saleEntry);

        if (dashboardSalesModel != null) {
            dashboardSalesModel.setRowCount(0);
            for (String[] sale : latestSalesList) {
                dashboardSalesModel.addRow(sale);
            }
        }
        
        recomputeTotalSales();
    }

    public static void recomputeTotalSales() {
        double total = 0.0;
        for (String[] sale : latestSalesList) {
            try {
                String cleanAmount = sale[2].replace("₱", "").replace(",", "").trim();
                total += Double.parseDouble(cleanAmount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        totalSalesToday = total;
    }
}