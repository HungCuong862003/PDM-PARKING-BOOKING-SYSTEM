package main.java.com.parkeasy.model;

public class Admin {
    // primary key
    private int adminID;

    // other attributes
    private String adminName;
    private String phone;
    private String email;
    private String password;
    private float balance; // Added balance field

    /**
     * Constructor with all fields
     *
     * @param adminID The admin ID
     * @param adminName The admin name
     * @param phone The admin phone number
     * @param email The admin email
     * @param password The admin password
     * @param balance The admin balance
     */
    public Admin(int adminID, String adminName, String phone, String email, String password, float balance) {
        this.adminID = adminID;
        this.adminName = adminName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.balance = balance;
    }

    /**
     * Constructor without balance (for backward compatibility)
     */
    public Admin(int adminID, String adminName, String phone, String email, String password) {
        this(adminID, adminName, phone, email, password, 0.0F);
    }

    /**
     * Default no-argument constructor
     */
    public Admin() {
        this.balance = 0.0F; // Initialize balance to 0
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

    /**
     * Get the admin's balance
     *
     * @return The balance
     */
    public float getBalance() {
        return balance;
    }

    /**
     * Set the admin's balance
     *
     * @param balance The balance to set
     */
    public void setBalance(float balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminID=" + adminID +
                ", adminName='" + adminName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", balance=" + balance +
                '}';
    }
}