package main.java.com.parkeasy.controller.auth;

import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.repository.UserRepository;
import main.java.com.parkeasy.repository.AdminRepository;

public class PasswordResetController {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    public PasswordResetController() {
        this.userRepository = new UserRepository();
        this.adminRepository = new AdminRepository();
    }

    /**
     * Initiates the password reset process for a user
     * @param email The email address of the user
     * @return true if the reset process was initiated successfully, false otherwise
     */
    public boolean initiatePasswordReset(String email) {
        // Check if the email exists in either user or admin database
        if (userRepository.findByEmail(email).isPresent() || adminRepository.getAdminByEmail(email) != null) {
            // In a real application, you would:
            // 1. Generate a reset token
            // 2. Store the token in the database with an expiration time
            // 3. Send an email with the reset link
            System.out.println("Password reset instructions have been sent to " + email);
            return true;
        }
        System.out.println("No account found with the provided email address");
        return false;
    }

    /**
     * Resets the password for a user
     * @param email The email address of the user
     * @param newPassword The new password to set
     * @return true if the password was reset successfully, false otherwise
     */
    public boolean resetPassword(String email, String newPassword) {
        // Validate password (you might want to add more validation)
        if (newPassword == null || newPassword.length() < 6) {
            System.out.println("Password must be at least 6 characters long");
            return false;
        }

        // Check if the email exists in either user or admin database
        if (userRepository.findByEmail(email).isPresent()) {
            // Update user password
            User user = userRepository.findByEmail(email).get();
            user.setPassword(newPassword);
            // In a real application, you would update the password in the database
            System.out.println("Password has been reset successfully");
            return true;
        } else if (adminRepository.getAdminByEmail(email) != null) {
            // Update admin password
            // In a real application, you would update the password in the database
            System.out.println("Admin password has been reset successfully");
            return true;
        }

        System.out.println("No account found with the provided email address");
        return false;
    }
}
