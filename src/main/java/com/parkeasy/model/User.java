package main.java.com.parkeasy.model;

public class User {
    private int userID;
    private String userName;
    private String phone;
    private String email;
    private String password;
    private double balance;

    /**
     * Fully parameterized constructor
     */
    public User(int userID, String userName, String phone, String email, String password, double balance) {
        this.userID = userID;
        this.userName = userName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.balance = balance;
    }

    /**
     * Default constructor
     */
    public User() {
        this.balance = 0.0; // Default balance to 0
    }

    // ID getters and setters
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    // User info getters and setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    // Balance getters and setters
    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}