package models;

public class Customer {

    private int id;
    private String lastname;
    private String firstname;
    private String contact;
    private String address;
    private String lastPurchaseDate;
    private String dateRegistered;
    private String pwdId;
    private String seniorCitizenId;
    private String discountType;
    private double discountPercent;

    public Customer() {}

    public Customer(int id, String lastname, String firstname, String contact,
                    String address, String lastPurchaseDate, String dateRegistered) {
        this.id = id;
        this.lastname = lastname;
        this.firstname = firstname;
        this.contact = contact;
        this.address = address;
        this.lastPurchaseDate = lastPurchaseDate;
        this.dateRegistered = dateRegistered;
        this.pwdId = "";
        this.seniorCitizenId = "";
        this.discountType = "No Discount";
        this.discountPercent = 0;
    }

    public Customer(int id, String lastname, String firstname, String contact,
                    String lastPurchaseDate) {
        this(id, lastname, firstname, contact, "", lastPurchaseDate, "");
    }

    public Customer(int id, String lastname, String firstname, String contact,
                    String address, String lastPurchaseDate, String dateRegistered,
                    String pwdId, String seniorCitizenId,
                    String discountType, double discountPercent) {
        this.id = id;
        this.lastname = lastname;
        this.firstname = firstname;
        this.contact = contact;
        this.address = address;
        this.lastPurchaseDate = lastPurchaseDate;
        this.dateRegistered = dateRegistered;
        this.pwdId = pwdId;
        this.seniorCitizenId = seniorCitizenId;
        this.discountType = discountType;
        this.discountPercent = discountPercent;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getLastName() { return lastname; }
    public void setLastName(String lastname) { this.lastname = lastname; }
    public String getFirstName() { return firstname; }
    public void setFirstName(String firstname) { this.firstname = firstname; }
    public String getName() { return (firstname + " " + lastname).trim(); }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            this.firstname = "";
            this.lastname = "";
            return;
        }
        String[] parts = name.trim().split("\\s+", 2);
        if (parts.length == 1) {
            this.firstname = parts[0];
            this.lastname = "";
        } else {
            this.firstname = parts[0];
            this.lastname = parts[1];
        }
    }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getLastPurchaseDate() { return lastPurchaseDate; }
    public void setLastPurchaseDate(String lastPurchaseDate) { this.lastPurchaseDate = lastPurchaseDate; }
    public String getDateRegistered() { return dateRegistered; }
    public void setDateRegistered(String dateRegistered) { this.dateRegistered = dateRegistered; }
    public String getPwdId() { return pwdId; }
    public void setPwdId(String pwdId) { this.pwdId = pwdId; }
    public String getSeniorCitizenId() { return seniorCitizenId; }
    public void setSeniorCitizenId(String seniorCitizenId) { this.seniorCitizenId = seniorCitizenId; }
    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }
    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }
}
