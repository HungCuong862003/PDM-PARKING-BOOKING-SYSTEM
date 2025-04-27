package main.java.com.parkeasy.controller.auth;

import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.service.AuthService;
import main.java.com.parkeasy.view.auth.LoginView;
import main.java.com.parkeasy.view.auth.RegistrationView;
import main.java.com.parkeasy.view.admin.AdminDashboardView;
import main.java.com.parkeasy.view.user.UserDashboardView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * Controller class for handling authentication operations.
 * Manages user and admin login, registration, and session management.
 */
public class AuthController {

    private final AuthService authService;
    private LoginView loginView;
    private RegistrationView registrationView;

    /**
     * Constructor for AuthController.
     */
    public AuthController() throws SQLException {
        this.authService = new AuthService();
    }

    /**
     * Sets the login view that this controller will manage.
     *
     * @param loginView The login view
     */
    public void setLoginView(LoginView loginView) {
        this.loginView = loginView;
        setupLoginViewListeners();
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
     * Sets up event listeners for the login view.
     */
    private void setupLoginViewListeners() {
        if (loginView == null) return;

        // Login button listener
        loginView.getLoginButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // Register link listener
        loginView.getRegisterLink().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRegistrationView();
            }
        });

        // Admin login toggle listener
        loginView.getAdminLoginToggle().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isAdminLogin = loginView.getAdminLoginToggle().isSelected();
                loginView.updateUIForAdminLogin(isAdminLogin);
            }
        });
    }

    /**
     * Sets up event listeners for the registration view.
     */
    private void setupRegistrationViewListeners() {
        if (registrationView == null) return;

        // Register button listener
        registrationView.getRegisterButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegistration();
            }
        });

        // Back to login link listener
        registrationView.getLoginLink().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLoginView();
            }
        });
    }

    /**
     * Handles the login process when the login button is clicked.
     */
    private void handleLogin() {
        String email = loginView.getEmailField().getText();
        String password = new String(loginView.getPasswordField().getPassword());
        boolean isAdminLogin = loginView.getAdminLoginToggle().isSelected();

        // Validate inputs
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(loginView,
                    "Please enter both email and password",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean loginSuccess;

        if (isAdminLogin) {
            // Admin login
            loginSuccess = authService.loginAdmin(email, password);

            if (loginSuccess) {
                Admin currentAdmin = authService.getCurrentAdmin();
                showAdminDashboard(currentAdmin);
            } else {
                JOptionPane.showMessageDialog(loginView,
                        "Invalid admin credentials. Please try again.",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // User login
            loginSuccess = authService.loginUser(email, password);

            if (loginSuccess) {
                User currentUser = authService.getCurrentUser();
                showUserDashboard(currentUser);
            } else {
                JOptionPane.showMessageDialog(loginView,
                        "Invalid user credentials. Please try again.",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Handles the registration process when the register button is clicked.
     */
    private void handleRegistration() {
        String name = registrationView.getNameField().getText();
        String email = registrationView.getEmailField().getText();
        String phone = registrationView.getPhoneField().getText();
        String password = new String(registrationView.getPasswordField().getPassword());
        String confirmPassword = new String(registrationView.getConfirmPasswordField().getPassword());

        // Validate inputs
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(registrationView,
                    "Please fill in all fields",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(registrationView,
                    "Passwords do not match",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if email already exists
        if (authService.isEmailExists(email)) {
            JOptionPane.showMessageDialog(registrationView,
                    "Email already registered. Please use a different email.",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Register the new user
        boolean registrationSuccess = authService.registerUser(name, email, phone, password);

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
     * Shows the registration view and hides the login view.
     */
    private void showRegistrationView() {
        if (loginView != null) {
            loginView.setVisible(false);
        }

        if (registrationView != null) {
            registrationView.clearFields();
            registrationView.setVisible(true);
        } else {
            // Create registration view if it doesn't exist
            registrationView = new RegistrationView();
            setRegistrationView(registrationView);
            registrationView.setVisible(true);
        }
    }

    /**
     * Shows the login view and hides the registration view.
     */
    private void showLoginView() {
        if (registrationView != null) {
            registrationView.setVisible(false);
        }

        if (loginView != null) {
            loginView.clearFields();
            loginView.setVisible(true);
        } else {
            // Create login view if it doesn't exist
            loginView = new LoginView();
            setLoginView(loginView);
            loginView.setVisible(true);
        }
    }

    /**
     * Shows the admin dashboard with the current admin user.
     *
     * @param admin The logged-in admin
     */
    private void showAdminDashboard(Admin admin) {
        if (loginView != null) {
            loginView.setVisible(false);
        }

        // Create and show admin dashboard
        AdminDashboardView adminDashboard = new AdminDashboardView(admin);
        adminDashboard.setVisible(true);
    }

    /**
     * Shows the user dashboard with the current user.
     *
     * @param user The logged-in user
     */
    private void showUserDashboard(User user) {
        if (loginView != null) {
            loginView.setVisible(false);
        }

        // Create and show user dashboard
        UserDashboardView userDashboard = new UserDashboardView(user);
        userDashboard.setVisible(true);
    }

    /**
     * Logs out the current user or admin.
     *
     * @return true if logout successful, false otherwise
     */
    public boolean logout() {
        authService.logout();
        return true;
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return true if a user is logged in, false otherwise
     */
    public boolean isUserLoggedIn() {
        return authService.isUserLoggedIn();
    }

    /**
     * Checks if an admin is currently logged in.
     *
     * @return true if an admin is logged in, false otherwise
     */
    public boolean isAdminLoggedIn() {
        return authService.isAdminLoggedIn();
    }

    /**
     * Gets the currently logged in user.
     *
     * @return the current User object, or null if no user is logged in
     */
    public User getCurrentUser() {
        return authService.getCurrentUser();
    }

    /**
     * Gets the currently logged in admin.
     *
     * @return the current Admin object, or null if no admin is logged in
     */
    public Admin getCurrentAdmin() {
        return authService.getCurrentAdmin();
    }
}