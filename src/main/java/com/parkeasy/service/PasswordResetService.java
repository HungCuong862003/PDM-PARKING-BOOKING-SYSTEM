package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.repository.AdminRepository;
import main.java.com.parkeasy.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for password reset operations
 */
public class PasswordResetService {
    private static final Logger LOGGER = Logger.getLogger(PasswordResetService.class.getName());

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    // Default password to reset to
    private static final String DEFAULT_PASSWORD = "ParkEasy123";

    /**
     * Constructor with dependency injection
     */
    public PasswordResetService(UserRepository userRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    /**
     * Default constructor
     */
    public PasswordResetService() {
        this.userRepository = new UserRepository();
        this.adminRepository = new AdminRepository();
    }

    /**
     * Reset password to a default value
     *
     * @param email Email of the user or admin
     * @return true if successful, false otherwise
     */
    public boolean resetPassword(String email) {
        try {
            LOGGER.log(Level.INFO, "Resetting password for: {0}", email);

            // Validate email
            if (email == null || email.trim().isEmpty()) {
                LOGGER.log(Level.WARNING, "Empty email provided for password reset");
                return false;
            }

            // Check if email exists as user
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                // Update user with default password
                User user = userOptional.get();
                user.setPassword(DEFAULT_PASSWORD);

                userRepository.save(user);
                LOGGER.log(Level.INFO, "Password reset successful for user: {0}", email);
                return true;
            }

            // Check if email exists as admin
            Optional<Admin> adminOptional = adminRepository.findByEmail(email);
            if (adminOptional.isPresent()) {
                // Update admin with default password
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
     * Reset password to a specified value
     *
     * @param email Email of the user or admin
     * @param newPassword New password to set
     * @return true if successful, false otherwise
     */
    public boolean resetPasswordToValue(String email, String newPassword) {
        try {
            LOGGER.log(Level.INFO, "Resetting password for: {0}", email);

            // Validate inputs
            if (email == null || email.trim().isEmpty() || newPassword == null || newPassword.isEmpty()) {
                LOGGER.log(Level.WARNING, "Invalid inputs for password reset");
                return false;
            }

            // Check if email exists as user
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                // Update user with new password
                User user = userOptional.get();
                user.setPassword(newPassword);

                userRepository.save(user);
                LOGGER.log(Level.INFO, "Password reset successful for user: {0}", email);
                return true;
            }

            // Check if email exists as admin
            Optional<Admin> adminOptional = adminRepository.findByEmail(email);
            if (adminOptional.isPresent()) {
                // Update admin with new password
                Admin admin = adminOptional.get();
                admin.setPassword(newPassword);

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
     * Reset password using token-based approach (simulated)
     * In a real application, this would verify a token sent to the user's email
     *
     * @param email Email of the user or admin
     * @return A reset token (for demonstration purposes)
     */
    public String generatePasswordResetToken(String email) {
        try {
            LOGGER.log(Level.INFO, "Generating password reset token for: {0}", email);

            // Validate email
            if (email == null || email.trim().isEmpty()) {
                LOGGER.log(Level.WARNING, "Empty email provided for password reset token");
                return null;
            }

            // Check if email exists as user or admin
            boolean userExists = false;
            try {
                userExists = userRepository.isEmailExists(email) || adminRepository.isEmailExists(email);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error checking if email exists", e);
                return null;
            }

            if (!userExists) {
                LOGGER.log(Level.WARNING, "Email not found for password reset token: {0}", email);
                return null;
            }

            // Generate a token (in a real application, this would be stored with an expiration)
            String token = UUID.randomUUID().toString();

            // In a real application, you would:
            // 1. Store the token in a database with an expiration time
            // 2. Send an email to the user with a link containing the token

            LOGGER.log(Level.INFO, "Password reset token generated for: {0}", email);
            return token;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating password reset token for: " + email, e);
            return null;
        }
    }

    /**
     * Verify a password reset token (simulated)
     * In a real application, this would verify against a stored token
     *
     * @param token The token to verify
     * @return true if valid (always returns true for demonstration)
     */
    public boolean verifyPasswordResetToken(String token) {
        // In a real application, you would:
        // 1. Look up the token in the database
        // 2. Check if it's expired
        // 3. Return true only if the token is valid and not expired

        // For demonstration, we'll always return true
        return token != null && !token.isEmpty();
    }

    /**
     * Reset password using a token (simulated)
     * In a real application, this would verify the token and reset the password
     *
     * @param token The reset token
     * @param email Email of the user or admin
     * @param newPassword New password to set
     * @return true if successful, false otherwise
     */
    public boolean resetPasswordWithToken(String token, String email, String newPassword) {
        try {
            LOGGER.log(Level.INFO, "Resetting password with token for: {0}", email);

            // Validate inputs
            if (token == null || token.isEmpty() || email == null || email.isEmpty() || newPassword == null || newPassword.isEmpty()) {
                LOGGER.log(Level.WARNING, "Invalid inputs for password reset with token");
                return false;
            }

            // Verify token (in a real application, this would check against a stored token)
            if (!verifyPasswordResetToken(token)) {
                LOGGER.log(Level.WARNING, "Invalid or expired token for password reset");
                return false;
            }

            // Reset the password
            return resetPasswordToValue(email, newPassword);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error resetting password with token for: " + email, e);
            return false;
        }
    }

    /**
     * Get the default password that will be set by resetPassword()
     *
     * @return The default password
     */
    public String getDefaultPassword() {
        return DEFAULT_PASSWORD;
    }
}