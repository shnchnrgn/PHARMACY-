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
        public boolean isPwd;
        public String pwdId;

        public SaleItem(String orderNo, String date, String amount, String customerName,
                        boolean isPwd, String pwdId) {
            this.orderNo = orderNo;
            this.date = date;
            this.amount = amount;
            this.customerName = customerName;
            this.isPwd = isPwd;
            this.pwdId = pwdId;
        }

        public SaleItem(String orderNo, String date, String amount, String customerName) {
            this(orderNo, date, amount, customerName, false, "");
        }
    }

    private static List<SaleItem> salesList = new ArrayList<>();

    public static void addSale(String orderNo, String date, String amount, String customerName,
                               boolean isPwd, String pwdId) {
        salesList.add(new SaleItem(orderNo, date, amount, customerName, isPwd, pwdId));
    }

    public static void addSale(String orderNo, String date, String amount, String customerName) {
        addSale(orderNo, date, amount, customerName, false, "");
    }

    public static List<SaleItem> getSalesList() {
        return salesList;
    }
}