package main.java.com.parkeasy.model;

import java.util.Objects;
import java.math.BigDecimal;
import java.util.regex.Pattern;
import main.java.com.parkeasy.util.Constants;

/**
 * User model class representing a user in the ParkEasy system.
 * Contains all user information including credentials and account balance.
 */
public class User {
    private int userID;
    private String userName;
    private String phone;
    private String email;
    private String password;
    private BigDecimal balance;

    /**
     * Parameterized constructor to create a new User with all required fields.
     *
     * @param userID   Unique identifier for the user
     * @param userName User's full name
     * @param phone    User's phone number
     * @param email    User's email address
     * @param password User's password
     * @param balance  User's account balance
     */
    public User(int userID, String userName, String phone, String email, String password, BigDecimal balance) {
        this.userID = userID;
        setUserName(userName);
        setPhone(phone);
        setEmail(email);
        setPassword(password);
        this.balance = (balance != null) ? balance : BigDecimal.ZERO;
    }

    /**
     * Constructor with default balance (0.0).
     *
     * @param userID   Unique identifier for the user
     * @param userName User's full name
     * @param phone    User's phone number
     * @param email    User's email address
     * @param password User's password
     */
    public User(int userID, String userName, String phone, String email, String password) {
        this(userID, userName, phone, email, password, BigDecimal.ZERO);
    }

    /**
     * Default constructor for ORM frameworks.
     */
    public User() {
        this.balance = BigDecimal.ZERO;
    }

    /**
     * Get the user ID.
     *
     * @return The user's unique identifier
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Set the user ID.
     *
     * @param userID The user's unique identifier
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * Get the user's name.
     *
     * @return The user's full name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Set the user's name, with validation.
     *
     * @param userName The user's full name
     * @throws IllegalArgumentException if the name is null or empty
     */
    public void setUserName(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty");
        }
        this.userName = userName;
    }

    /**
     * Get the user's phone number.
     *
     * @return The user's phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Set the user's phone number, with validation.
     *
     * @param phone The user's phone number
     * @throws IllegalArgumentException if the phone number is invalid
     */
    public void setPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        // Add regex validation for phone format
        if (!Pattern.matches("^\\d{10,15}$", phone)) {
            throw new IllegalArgumentException("Invalid phone format");
        }
        this.phone = phone;
    }

    /**
     * Get the user's email address.
     *
     * @return The user's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the user's email address, with validation.
     *
     * @param email The user's email address
     * @throws IllegalArgumentException if the email is invalid
     */
    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        // Simple validation to ensure it contains @ character
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format: must contain @ character");
        }
        this.email = email;
    }

    /**
     * Get the user's password.
     *
     * @return The user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the user's password, with validation.
     *
     * @param password The user's password
     * @throws IllegalArgumentException if the password doesn't meet requirements
     */
    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        // Add password strength requirements
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        this.password = password;
    }

    /**
     * Get the user's account balance.
     *
     * @return The user's current balance
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Set the user's account balance.
     *
     * @param balance The user's new balance
     */
    public void setBalance(BigDecimal balance) {
        this.balance = (balance != null) ? balance : BigDecimal.ZERO;
    }

    /**
     * Add amount to the user's balance.
     *
     * @param amount Amount to add
     * @return The new balance
     * @throws IllegalArgumentException if amount is negative
     */
    public BigDecimal addToBalance(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cannot add negative amount to balance");
        }
        this.balance = this.balance.add(amount);
        return this.balance;
    }

    /**
     * Deduct amount from the user's balance.
     *
     * @param amount Amount to deduct
     * @return The new balance
     * @throws IllegalArgumentException if amount is negative or if insufficient balance
     */
    public BigDecimal deductFromBalance(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cannot deduct negative amount from balance");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        this.balance = this.balance.subtract(amount);
        return this.balance;
    }

    /**
     * Check if user has sufficient balance for an amount.
     *
     * @param amount Amount to check against
     * @return true if user has sufficient balance, false otherwise
     */
    public boolean hasSufficientBalance(BigDecimal amount) {
        return this.balance.compareTo(amount) >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userID == user.userID &&
                Objects.equals(email, user.email) &&
                Objects.equals(phone, user.phone);
    }

    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", userName='" + userName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", balance=" + balance +
                '}';
    }
}