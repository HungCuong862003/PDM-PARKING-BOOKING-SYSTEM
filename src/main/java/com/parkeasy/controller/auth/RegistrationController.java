package main.java.com.parkeasy.controller.auth;

import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.service.AuthService;
import main.java.com.parkeasy.view.auth.RegistrationView;
import main.java.com.parkeasy.view.auth.LoginView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * Controller class for handling registration operations.
 * Manages user and admin registration, validation, and navigation.
 */
public class RegistrationController {

    private final AuthService authService;
    private RegistrationView registrationView;
    private boolean isAdminRegistration;

    /**
     * Constructor for RegistrationController.
     *
     * @throws SQLException if database connection fails
     */
    public RegistrationController() throws SQLException {
        this.authService = new AuthService();
        this.isAdminRegistration = false;
    }

    /**
     * Sets the registration view that this controller will manage.
     *
     * @param registrationView The registration view
     */
    public void setRegistrationView(RegistrationView registrationView) {
        this.registrationView = registrationView;
        setupRegistrationViewListeners();
    }

    /**
     * Sets up event listeners for the registration view.
     */
    private void setupRegistrationViewListeners() {
        if (registrationView == null) return;

        // Register button listener
        registrationView.setRegisterButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegistration();
            }
        });

        // Login button listener
        registrationView.setLoginButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLoginView();
            }
        });

        // Renter button listener
        registrationView.setRenterButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isAdminRegistration = false;
                updateUIForUserType();
            }
        });

        // Owner/Admin button listener
        registrationView.setOwnerAdminButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isAdminRegistration = true;
                updateUIForUserType();
            }
        });
    }

    /**
     * Updates the UI based on the selected user type (renter or owner/admin).
     */
    private void updateUIForUserType() {
        if (registrationView == null) return;

        // Update button colors and styles based on selection
        if (isAdminRegistration) {
            registrationView.getRenterButton().setBackground(new Color(92, 92, 92));
            registrationView.getOwnerAdminButton().setBackground(new Color(98, 161, 232));
        } else {
            registrationView.getRenterButton().setBackground(new Color(98, 161, 232));
            registrationView.getOwnerAdminButton().setBackground(new Color(92, 92, 92));
        }
    }

    /**
     * Handles the registration process when the register button is clicked.
     */
    private void handleRegistration() {
        if (registrationView == null) return;

        String name = registrationView.getName();
        String email = registrationView.getEmail();
        String phone = registrationView.getPhone();
        String password = registrationView.getPassword();
        String confirmPassword = registrationView.getConfirmPassword();

        // Validate inputs
        if (!validateInputs(name, email, phone, password, confirmPassword)) {
            return;
        }

        boolean registrationSuccess;
        if (isAdminRegistration) {
            registrationSuccess = authService.registerAdmin(name, email, phone, password);
        } else {
            registrationSuccess = authService.registerUser(name, email, phone, password);
        }

        if (registrationSuccess) {
            JOptionPane.showMessageDialog(registrationView,
                    "Registration successful! You can now login with your credentials.",
                    "Registration Success",
                    JOptionPane.INFORMATION_MESSAGE);

            // Clear fields and return to login
            registrationView.clearFields();
            showLoginView();
        } else {
            JOptionPane.showMessageDialog(registrationView,
                    "Registration failed. Please try again.",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Validates the registration inputs.
     *
     * @param name Name input
     * @param email Email input
     * @param phone Phone input
     * @param password Password input
     * @param confirmPassword Confirm password input
     * @return true if all inputs are valid, false otherwise
     */
    private boolean validateInputs(String name, String email, String phone, String password, String confirmPassword) {
        // Check for empty fields
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(registrationView,
                    "Please fill in all fields",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(registrationView,
                    "Please enter a valid email address",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate phone format (10-15 digits)
        if (!phone.matches("^\\d{10,15}$")) {
            JOptionPane.showMessageDialog(registrationView,
                    "Please enter a valid phone number (10-15 digits)",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate password length
        if (password.length() < 8) {
            JOptionPane.showMessageDialog(registrationView,
                    "Password must be at least 8 characters long",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(registrationView,
                    "Passwords do not match",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    /**
     * Shows the login view and hides the registration view.
     */
    private void showLoginView() {
        if (registrationView != null) {
            registrationView.setVisible(false);
        }

        try {
            // Create and show login view
            LoginView loginView = new LoginView();
            AuthController authController = new AuthController();
            authController.setLoginView(loginView);
            loginView.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(registrationView,
                    "Error initializing login view: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Shows the registration view.
     */
    public void showRegistrationView() {
        if (registrationView != null) {
            registrationView.clearFields();
            registrationView.setVisible(true);
        } else {
            registrationView = new RegistrationView();
            setRegistrationView(registrationView);
            registrationView.setVisible(true);
        }
    }
}