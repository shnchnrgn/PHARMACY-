package ui;

import java.util.ArrayList;
import java.util.List;

public class SharedData {
    public static double totalSalesToday = 0.0;
    
    public static class SaleItem {
        public String orderNo;
        public String date;
        public String amount;
        public String customerName;

        public SaleItem(String orderNo, String date, String amount, String customerName) {
            this.orderNo = orderNo;
            this.date = date;
            this.amount = amount;
            this.customerName = customerName;
        }
    }

    private static List<SaleItem> salesList = new ArrayList<>();

    public static void addSale(String orderNo, String date, String amount, String customerName) {
        salesList.add(new SaleItem(orderNo, date, amount, customerName));
    }

    public static List<SaleItem> getSalesList() {
        return salesList;
    }
}