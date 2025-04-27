package main.java.com.parkeasy.controller.auth;

import main.java.com.parkeasy.service.PasswordResetService;
import main.java.com.parkeasy.view.auth.PasswordResetView;
import main.java.com.parkeasy.view.auth.LoginView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * Controller class for handling password reset operations.
 * Manages the password reset process and navigation between views.
 */
public class PasswordResetController {

    private final PasswordResetService passwordResetService;
    private PasswordResetView passwordResetView;
    private boolean isAdminReset;

    /**
     * Constructor for PasswordResetController.
     *
     * @throws SQLException if database connection fails
     */
    public PasswordResetController() throws SQLException {
        this.passwordResetService = new PasswordResetService();
        this.isAdminReset = false;
    }

    /**
     * Sets the password reset view that this controller will manage.
     *
     * @param passwordResetView The password reset view
     */
    public void setPasswordResetView(PasswordResetView passwordResetView) {
        this.passwordResetView = passwordResetView;
        setupPasswordResetViewListeners();
    }

    /**
     * Sets up event listeners for the password reset view.
     */
    private void setupPasswordResetViewListeners() {
        if (passwordResetView == null) return;

        // Reset password button listener
        passwordResetView.setResetPasswordButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handlePasswordReset();
            }
        });

        // Back to login button listener
        passwordResetView.setBackToLoginButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLoginView();
            }
        });
    }

    /**
     * Handles the password reset process when the reset button is clicked.
     */
    private void handlePasswordReset() {
        if (passwordResetView == null) return;

        String email = passwordResetView.getEmail();
        String phone = passwordResetView.getPhone();

        // Validate inputs
        if (!validateInputs(email, phone)) {
            return;
        }

        String resetToken;
        if (isAdminReset) {
            resetToken = passwordResetService.initiateAdminPasswordReset(email, phone);
        } else {
            resetToken = passwordResetService.initiateUserPasswordReset(email, phone);
        }

        if (resetToken != null && !resetToken.isEmpty()) {
            // Show success message with instructions
            JOptionPane.showMessageDialog(passwordResetView,
                    "Password reset instructions have been sent to your email.\n" +
                            "Please check your email for further instructions.",
                    "Reset Initiated",
                    JOptionPane.INFORMATION_MESSAGE);

            // Clear fields and return to login
            passwordResetView.clearFields();
            showLoginView();
        } else {
            JOptionPane.showMessageDialog(passwordResetView,
                    "Password reset failed. Please verify your email and phone number.",
                    "Reset Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Validates the password reset inputs.
     *
     * @param email Email input
     * @param phone Phone input
     * @return true if all inputs are valid, false otherwise
     */
    private boolean validateInputs(String email, String phone) {
        // Check for empty fields
        if (email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(passwordResetView,
                    "Please fill in all fields",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(passwordResetView,
                    "Please enter a valid email address",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate phone format (10-15 digits)
        if (!phone.matches("^\\d{10,15}$")) {
            JOptionPane.showMessageDialog(passwordResetView,
                    "Please enter a valid phone number (10-15 digits)",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    /**
     * Shows the login view and hides the password reset view.
     */
    private void showLoginView() {
        if (passwordResetView != null) {
            passwordResetView.setVisible(false);
        }

        try {
            // Create and show login view
            LoginView loginView = new LoginView();
            AuthController authController = new AuthController();
            authController.setLoginView(loginView);
            loginView.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(passwordResetView,
                    "Error initializing login view: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Shows the password reset view.
     */
    public void showPasswordResetView() {
        if (passwordResetView != null) {
            passwordResetView.clearFields();
            passwordResetView.setVisible(true);
        } else {
            passwordResetView = new PasswordResetView();
            setPasswordResetView(passwordResetView);
            passwordResetView.setVisible(true);
        }
    }

    /**
     * Sets whether this is an admin password reset.
     *
     * @param isAdminReset true if this is an admin reset, false otherwise
     */
    public void setAdminReset(boolean isAdminReset) {
        this.isAdminReset = isAdminReset;
    }
}
