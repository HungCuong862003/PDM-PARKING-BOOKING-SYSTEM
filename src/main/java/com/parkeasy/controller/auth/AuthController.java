package main.java.com.parkeasy.controller.auth;

import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.service.AdminService;
import main.java.com.parkeasy.service.AuthService;
import main.java.com.parkeasy.service.UserService;


import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for authentication operations
 */
public class AuthController {
    private static final Logger LOGGER = Logger.getLogger(AuthController.class.getName());

    private final AuthService authService;
    private final UserService userService;
    private final AdminService adminService;

    /**
     * Constructor with dependency injection
     */
    public AuthController(AuthService authService, UserService userService,
                          AdminService adminService) {
        this.authService = authService;
        this.userService = userService;
        this.adminService = adminService;
    }

    /**
     * Authenticate user login
     *
     * @param email User email
     * @param password User password
     * @return User object if authentication successful, null otherwise
     */
    public User login(String email, String password) {
        try {
            // Validate input
            if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
                LOGGER.log(Level.WARNING, "Invalid login credentials provided");
                return null;
            }

            User user = authService.authenticateUser(email, password);
            if (user != null) {
                // Create session
                LOGGER.log(Level.INFO, "User logged in successfully: {0}", email);
            } else {
                LOGGER.log(Level.WARNING, "Login failed for user: {0}", email);
            }

            return user;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during login process for user: " + email, e);
            return null;
        }
    }

    /**
     * Authenticate admin login
     *
     * @param email Admin email
     * @param password Admin password
     * @return Admin object if authentication successful, null otherwise
     */
    public Admin adminLogin(String email, String password) {
        try {
            // Validate input
            if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
                LOGGER.log(Level.WARNING, "Invalid admin login credentials provided");
                return null;
            }

            Admin admin = authService.authenticateAdmin(email, password);
            if (admin != null) {
                // Create session
                LOGGER.log(Level.INFO, "Admin logged in successfully: {0}", email);
            } else {
                LOGGER.log(Level.WARNING, "Login failed for admin: {0}", email);
            }

            return admin;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during login process for admin: " + email, e);
            return null;
        }
    }

    /**
     * Check if an email is for an admin
     *
     * @param email Email to check
     * @return true if admin, false otherwise
     */
    public boolean isAdmin(String email) {
        try {
            if (email == null || email.isEmpty()) {
                return false;
            }

            return adminService.isEmailExists(email);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking if email belongs to admin: " + email, e);
            return false;
        }
    }

}