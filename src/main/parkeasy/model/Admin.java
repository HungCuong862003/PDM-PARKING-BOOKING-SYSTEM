package main.java.com.parkeasy.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Admin model class representing an administrator in the ParkEasy system.
 * Contains all admin information including credentials.
 */
public class Admin {
    // primary key
    private int adminID;

    // other attributes
    private String adminName;
    private String phone;
    private String email;
    private String password;

    /**
     * Parameterized constructor to create a new Admin with all required fields.
     *
     * @param adminID   Unique identifier for the admin
     * @param adminName Admin's full name
     * @param phone     Admin's phone number
     * @param email     Admin's email address
     * @param password  Admin's password
     */
    public Admin(int adminID, String adminName, String phone, String email, String password) {
        this.adminID = adminID;
        setAdminName(adminName);
        setPhone(phone);
        setEmail(email);
        setPassword(password);
    }

    /**
     * Default constructor for ORM frameworks.
     */
    public Admin() {
    }
    /**
     * Get the admin ID.
     *
     * @return The admin's unique identifier
     */
    public int getAdminID() {
        return adminID;
    }

    /**
     * Set the admin ID.
     *
     * @param adminID The admin's unique identifier
     */
    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }

    /**
     * Get the admin's name.
     *
     * @return The admin's full name
     */
    public String getAdminName() {
        return adminName;
    }

    /**
     * Set the admin's name, with validation.
     *
     * @param adminName The admin's full name
     * @throws IllegalArgumentException if the name is null or empty
     */
    public void setAdminName(String adminName) {
        if (adminName == null || adminName.trim().isEmpty()) {
            throw new IllegalArgumentException("Admin name cannot be null or empty");
        }
        this.adminName = adminName;
    }

    /**
     * Get the admin's phone number.
     *
     * @return The admin's phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Set the admin's phone number, with validation.
     *
     * @param phone The admin's phone number
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
     * Get the admin's email address.
     *
     * @return The admin's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the admin's email address, with validation.
     *
     * @param email The admin's email address
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
     * Get the admin's password.
     *
     * @return The admin's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the admin's password, with validation.
     *
     * @param password The admin's password
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Admin admin = (Admin) o;
        return adminID == admin.adminID &&
                Objects.equals(email, admin.email) &&
                Objects.equals(phone, admin.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(adminID, email, phone);
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminID=" + adminID +
                ", adminName='" + adminName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}