package models;

public class Customer {
    private int id;
    private String name;
    private String contact;
    private String address;
    private String lastPurchaseDate;
    private String dateRegistered;

    public Customer() {
    }

    public Customer(int id, String name, String contact, String address,
                     String lastPurchaseDate, String dateRegistered) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.address = address;
        this.lastPurchaseDate = lastPurchaseDate;
        this.dateRegistered = dateRegistered;
    }

    /** Convenience constructor kept for backward compatibility (no address/dateRegistered). */
    public Customer(int id, String name, String contact, String lastPurchaseDate) {
        this(id, name, contact, "", lastPurchaseDate, "");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLastPurchaseDate() {
        return lastPurchaseDate;
    }

    public void setLastPurchaseDate(String lastPurchaseDate) {
        this.lastPurchaseDate = lastPurchaseDate;
    }

    public String getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(String dateRegistered) {
        this.dateRegistered = dateRegistered;
    }
}