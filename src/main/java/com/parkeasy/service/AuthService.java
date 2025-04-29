package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.repository.AdminRepository;
import main.java.com.parkeasy.repository.UserRepository;

import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for authentication operations
 */
public class AuthService {
    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    /**
     * Constructor with dependency injection
     */
    public AuthService(UserRepository userRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    /**
     * Default constructor
     */
    public AuthService() {
        this.userRepository = new UserRepository();
        this.adminRepository = new AdminRepository();
    }

    /**
     * Authenticate a user login
     *
     * @param email User email
     * @param password User password
     * @return User object if authentication successful, null otherwise
     */
    public User authenticateUser(String email, String password) {
        try {
            User user = userRepository.getUserByEmail(email);

            if (user != null) {
                // In a real application, use a password hashing library:
                // boolean passwordMatch = PasswordHashing.verify(password, user.getPassword());

                // For demonstration purposes, direct comparison:
                boolean passwordMatch = user.getPassword().equals(password);

                if (passwordMatch) {
                    LOGGER.log(Level.INFO, "User authenticated successfully: {0}", email);
                    return user;
                }
            }

            LOGGER.log(Level.INFO, "Authentication failed for user: {0}", email);
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during user authentication", e);
            return null;
        }
    }

    /**
     * Authenticate an admin login
     *
     * @param email Admin email
     * @param password Admin password
     * @return Admin object if authentication successful, null otherwise
     */
    public Admin authenticateAdmin(String email, String password) {
        try {
            Admin admin = adminRepository.getAdminByEmail(email);

            if (admin != null) {
                // Trim both passwords to remove any leading/trailing whitespace
                String trimmedInputPassword = password.trim();
                String trimmedStoredPassword = admin.getPassword().trim();

                // Compare the trimmed passwords
                boolean passwordMatch = trimmedStoredPassword.equals(trimmedInputPassword);

                if (passwordMatch) {
                    LOGGER.log(Level.INFO, "Admin authenticated successfully: {0}", email);
                    return admin;
                }
            }

            LOGGER.log(Level.INFO, "Authentication failed for admin: {0}", email);
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during admin authentication", e);
            return null;
        }
    }

    /**
     * Check if email exists for user or admin
     *
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    public boolean isEmailExists(String email) {
        try {
            return userRepository.isEmailExists(email) || adminRepository.isEmailExists(email);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if email exists", e);
            return false;
        }
    }
}