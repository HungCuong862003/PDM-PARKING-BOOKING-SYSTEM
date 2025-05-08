package main.java.com.parkeasy.view.auth;

import main.java.com.parkeasy.controller.auth.AuthController;
import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.service.AdminService;
import main.java.com.parkeasy.service.AuthService;
import main.java.com.parkeasy.service.UserService;
import main.java.com.parkeasy.util.DatabaseConnection;
import main.java.com.parkeasy.view.admin.AdminDashboardView;
import main.java.com.parkeasy.view.user.UserDashboardView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Login view for the ParkEasy application
 * Allows both users and admins to log in
 */
public class LoginView extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JRadioButton userRadioButton;
    private JRadioButton adminRadioButton;
    private JButton loginButton;
    private JButton registerButton;
    private JButton forgotPasswordButton;

    private AuthController authController;

    public LoginView() {
        // Initialize services and controller
        AuthService authService = new AuthService();
        UserService userService = new UserService();
        AdminService adminService = new AdminService();
        authController = new AuthController(authService, userService, adminService);

        // Set up the frame
        setTitle("ParkEasy - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create components
        initComponents();

        // Layout the components
        layoutComponents();

        // Make the frame visible
        setVisible(true);
    }

    private void initComponents() {
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);

        userRadioButton = new JRadioButton("User");
        userRadioButton.setSelected(true); // Default selection

        adminRadioButton = new JRadioButton("Admin");

        // Group the radio buttons
        ButtonGroup group = new ButtonGroup();
        group.add(userRadioButton);
        group.add(adminRadioButton);

        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        forgotPasswordButton = new JButton("Forgot Password");

        // Add action listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegistrationView();
            }
        });

        forgotPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openPasswordResetView();
            }
        });
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));

        // Create the form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(emailField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passwordField, gbc);

        // Radio buttons
        JPanel radioPanel = new JPanel();
        radioPanel.add(userRadioButton);
        radioPanel.add(adminRadioButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(radioPanel, gbc);

        // Add form panel to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Create the button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(forgotPasswordButton);

        // Add button panel to main panel
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);
    }

    private void login() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        boolean isAdmin = adminRadioButton.isSelected();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both email and password.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (isAdmin) {
                Admin admin = authController.adminLogin(email, password);
                if (admin != null) {
                    JOptionPane.showMessageDialog(this,
                            "Admin login successful. Welcome, " + admin.getAdminName() + "!",
                            "Login Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                    new AdminDashboardView(admin);
                    dispose(); // Close login window
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Invalid admin email or password.",
                            "Login Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                User user = authController.login(email, password);
                if (user != null) {
                    JOptionPane.showMessageDialog(this,
                            "User login successful. Welcome, " + user.getUserName() + "!",
                            "Login Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                    new UserDashboardView(user);
                    dispose(); // Close login window
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Invalid user email or password.",
                            "Login Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "An error occurred during login: " + ex.getMessage(),
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openRegistrationView() {
        new RegistrationView();
        dispose(); // Close login window
    }

    private void openPasswordResetView() {
        new PasswordResetView();
        dispose(); // Close login window
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginView();
        });
    }
}