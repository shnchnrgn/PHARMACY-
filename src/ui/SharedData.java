package ui;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class SharedData {
    public static double totalSalesToday = 0.0;
    public static List<String[]> latestSalesList = new ArrayList<>();
    public static DefaultTableModel dashboardSalesModel;

    public static DefaultTableModel medicineTableModel;
    public static List<String[]> medicineList = new ArrayList<>();

    public static void addSale(String orderNo, String date, String amount, String customerName) {
        totalSalesToday += Double.parseDouble(amount);
        String[] saleEntry = {orderNo, date, "₱ " + amount, customerName};
        latestSalesList.add(0, saleEntry);

        if (dashboardSalesModel != null) {
            dashboardSalesModel.setRowCount(0);
            for (String[] sale : latestSalesList) {
                dashboardSalesModel.addRow(sale);
            }
        }
    }

    public static void addMedicine(String[] medData) {
        medicineList.add(medData);
        if (medicineTableModel != null) {
            medicineTableModel.addRow(medData);
        }
    }
}