package models;

public class Customer {
    private int id;
    private String lastname;
    private String firstname;
    private String contact;
    private String address;
    private String lastPurchaseDate;
    private String dateRegistered;

    public Customer() {
    }

    public Customer(int id, String lastname, String firstname, String contact, String address,
                     String lastPurchaseDate, String dateRegistered) {
        this.id = id;
        this.lastname = lastname;
        this.firstname = firstname;
        this.contact = contact;
        this.address = address;
        this.lastPurchaseDate = lastPurchaseDate;
        this.dateRegistered = dateRegistered;
    }

    /** Convenience constructor kept for backward compatibility (no address/dateRegistered). */
    public Customer(int id, String name, String firstname, String contact, String lastPurchaseDate) {
        this(id, lastname, firstname, contact, "", lastPurchaseDate, "");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastName(){
        return lastname;
    }

    public String getFirstName() {
        return firstname;
    }

    public void setLastName(String lastname) {
        this.lastname = lastname;
    }

    public void setFirstName(String firstname){
        this.firstname = firstname;
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