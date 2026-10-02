package models;

public class Medicine {
    private int id;
    private String name;
    private double buyPrice;
    private double sellPrice;
    private int stock;
    private String expiryDate;
    private String companyName;
    private String medicineCategory;

    public Medicine() {}

    public Medicine(String name, double buyPrice, double sellPrice, int stock, String expiryDate, String companyName, String medicineCategory) {
        this.name = name;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.stock = stock;
        this.expiryDate = expiryDate;
        this.companyName = companyName;
        this.medicineCategory = medicineCategory;
    }

    public Medicine(int id, String name, double buyPrice, double sellPrice, int stock, String expiryDate, String companyName, String medicineCategory) {
        this.id = id;
        this.name = name;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.stock = stock;
        this.expiryDate = expiryDate;
        this.companyName = companyName;
        this.medicineCategory = medicineCategory;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getBuyPrice() { return buyPrice; }
    public void setBuyPrice(double buyPrice) { this.buyPrice = buyPrice; }

    public double getSellPrice() { return sellPrice; }
    public void setSellPrice(double sellPrice) { this.sellPrice = sellPrice; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getMedicineCategory() { return medicineCategory; }
    public void setMedicineCategory(String medicineCategory) { this.medicineCategory = medicineCategory; }
}