package main.java.com.parkeasy.controller.auth;

import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.service.AdminService;
import main.java.com.parkeasy.service.UserService;
import main.java.com.parkeasy.util.ValidationUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for user and admin registration
 * Simple implementation focusing on entity relationships
 */
public class RegistrationController {
    private static final Logger LOGGER = Logger.getLogger(RegistrationController.class.getName());

    private final UserService userService;
    private final AdminService adminService;

    /**
     * Constructor with dependency injection
     */
    public RegistrationController(UserService userService, AdminService adminService) {
        this.userService = userService;
        this.adminService = adminService;
    }

    /**
     * Register a new user
     *
     * @param username User's name
     * @param email User's email
     * @param phone User's phone number
     * @param password User's password
     * @return true if registration successful, false otherwise
     */
    public boolean registerUser(String username, String email, String phone, String password) {
        try {
            // Validate input
            if (!validateUserInput(username, email, phone, password)) {
                return false;
            }

            // Check if email is already in use
            if (!isEmailAvailable(email)) {
                LOGGER.log(Level.WARNING, "Email already in use: {0}", email);
                return false;
            }

            // Check if phone is already in use
            if (!isPhoneAvailable(phone)) {
                LOGGER.log(Level.WARNING, "Phone number already in use: {0}", phone);
                return false;
            }

            // Create user object
            User newUser = new User();
            newUser.setUserName(username);
            newUser.setEmail(email);
            newUser.setPhone(phone);
            newUser.setPassword(password); // Store password as plain text
            newUser.setBalance(0.0); // Initialize balance to 0

            // Save the user
            boolean success = userService.createUser(newUser);
            if (success) {
                LOGGER.log(Level.INFO, "User registered successfully: {0}", email);
                return true;
            } else {
                LOGGER.log(Level.WARNING, "Failed to register user: {0}", email);
                return false;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during user registration: " + email, e);
            return false;
        }
    }

    /**
     * Register a new admin
     *
     * @param adminName Admin's name
     * @param email Admin's email
     * @param phone Admin's phone number
     * @param password Admin's password
     * @return true if registration successful, false otherwise
     */
    public boolean registerAdmin(String adminName, String email, String phone, String password) {
        try {
            // Validate input
            if (!validateUserInput(adminName, email, phone, password)) {
                return false;
            }

            // Check if email is already in use
            if (!isEmailAvailable(email)) {
                LOGGER.log(Level.WARNING, "Email already in use: {0}", email);
                return false;
            }

            // Check if phone is already in use
            if (!isPhoneAvailable(phone)) {
                LOGGER.log(Level.WARNING, "Phone number already in use: {0}", phone);
                return false;
            }

            // Create admin object
            Admin newAdmin = new Admin();
            newAdmin.setAdminName(adminName);
            newAdmin.setEmail(email);
            newAdmin.setPhone(phone);
            newAdmin.setPassword(password); // Store password as plain text

            // Save the admin
            boolean success = adminService.createAdmin(newAdmin);
            if (success) {
                LOGGER.log(Level.INFO, "Admin registered successfully: {0}", email);
                return true;
            } else {
                LOGGER.log(Level.WARNING, "Failed to register admin: {0}", email);
                return false;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during admin registration: " + email, e);
            return false;
        }
    }

    /**
     * Check if an email is available (not already in use)
     *
     * @param email Email to check
     * @return true if email is available, false otherwise
     */
    public boolean isEmailAvailable(String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                return false;
            }

            boolean userExists = userService.isEmailExists(email);
            boolean adminExists = adminService.isEmailExists(email);

            return !userExists && !adminExists;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking email availability: " + email, e);
            return false;
        }
    }

    /**
     * Check if a phone number is available (not already in use)
     *
     * @param phone Phone number to check
     * @return true if phone is available, false otherwise
     */
    public boolean isPhoneAvailable(String phone) {
        try {
            if (phone == null || phone.trim().isEmpty()) {
                return false;
            }

            boolean userExists = userService.isPhoneExists(phone);
            boolean adminExists = adminService.isPhoneExists(phone);

            return !userExists && !adminExists;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking phone availability: " + phone, e);
            return false;
        }
    }

    /**
     * Validate registration input data
     *
     * @param name User or admin name
     * @param email Email address
     * @param phone Phone number
     * @param password Password
     * @return true if input is valid, false otherwise
     */
    private boolean validateUserInput(String name, String email, String phone, String password) {
        // Check for null or empty fields
        if (name == null || email == null || phone == null || password == null ||
                name.trim().isEmpty() || email.trim().isEmpty() ||
                phone.trim().isEmpty() || password.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Required registration fields missing");
            return false;
        }

        // Validate email format
        if (!ValidationUtils.isValidEmail(email)) {
            LOGGER.log(Level.WARNING, "Invalid email format: {0}", email);
            return false;
        }

        // Validate phone format
        if (!ValidationUtils.isValidPhone(phone)) {
            LOGGER.log(Level.WARNING, "Invalid phone format: {0}", phone);
            return false;
        }

        // Check password length
        if (password.length() < 8) {
            LOGGER.log(Level.WARNING, "Password is too short (minimum 8 characters)");
            return false;
        }

        return true;
    }
}