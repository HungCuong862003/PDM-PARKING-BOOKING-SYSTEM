package main.java.com.parkeasy.model;

public class Admin {
    // primary key
    private int adminID;

    // other attributes
    private String adminName;
    private String phone;
    private String email;
    private String password;

    public Admin(int adminID, String adminName, String phone, String email, String password) {
        this.adminID = adminID;
        this.adminName = adminName;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    public Admin() {

    }

    public int getAdminID() {
        return adminID;
    }

    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}