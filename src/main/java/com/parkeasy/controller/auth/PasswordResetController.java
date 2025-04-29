package main.java.com.parkeasy.controller.auth;

import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.repository.AdminRepository;
import main.java.com.parkeasy.repository.UserRepository;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Basic password reset controller
 * Resets password to a plain value when email is provided
 */
public class PasswordResetController {
    private static final Logger LOGGER = Logger.getLogger(PasswordResetController.class.getName());

    // Default password to reset to
    private static final String DEFAULT_PASSWORD = "Parking123";

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    /**
     * Constructor with dependency injection
     */
    public PasswordResetController(UserRepository userRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    /**
     * Reset password by email only
     * Sets password to default value
     *
     * @param email User or admin email
     * @return true if successful, false otherwise
     */
    public boolean resetPassword(String email) {
        try {
            // Validate email
            if (email == null || email.trim().isEmpty()) {
                LOGGER.log(Level.WARNING, "Empty email provided for password reset");
                return false;
            }

            // Check if email exists as user
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                // Update user with plain password (no hashing)
                User user = userOptional.get();
                user.setPassword(DEFAULT_PASSWORD);

                userRepository.save(user);
                LOGGER.log(Level.INFO, "Password reset successful for user: {0}", email);
                return true;
            }

            // Check if email exists as admin
            Optional<Admin> adminOptional = adminRepository.findByEmail(email);
            if (adminOptional.isPresent()) {
                // Update admin with plain password (no hashing)
                Admin admin = adminOptional.get();
                admin.setPassword(DEFAULT_PASSWORD);

                adminRepository.save(admin);
                LOGGER.log(Level.INFO, "Password reset successful for admin: {0}", email);
                return true;
            }

            // Email not found
            LOGGER.log(Level.WARNING, "Email not found for password reset: {0}", email);
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error resetting password for: " + email, e);
            return false;
        }
    }

    /**
     * Get the default password that will be set
     *
     * @return The default password
     */
    public String getDefaultPassword() {
        return DEFAULT_PASSWORD;
    }
}