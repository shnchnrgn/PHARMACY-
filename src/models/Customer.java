package models;

public class Customer {
    private int id;
    private String name;
    private String contact;
    private String lastPurchaseDate;

    public Customer() {
    }

    public Customer(int id, String name, String contact, String lastPurchaseDate) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.lastPurchaseDate = lastPurchaseDate;
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

    public String getLastPurchaseDate() {
        return lastPurchaseDate;
    }

    public void setLastPurchaseDate(String lastPurchaseDate) {
        this.lastPurchaseDate = lastPurchaseDate;
    }
}