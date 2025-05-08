package main.java.com.parkeasy.view.auth;

import main.java.com.parkeasy.controller.auth.RegistrationController;
import main.java.com.parkeasy.service.AdminService;
import main.java.com.parkeasy.service.UserService;
import main.java.com.parkeasy.util.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Registration view for the ParkEasy application
 * Allows users and admins to register new accounts
 */
public class RegistrationView extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JRadioButton userRadioButton;
    private JRadioButton adminRadioButton;
    private JButton registerButton;
    private JButton backToLoginButton;

    private RegistrationController registrationController;

    public RegistrationView() {
        // Initialize services and controller
        UserService userService = new UserService();
        AdminService adminService = new AdminService();
        registrationController = new RegistrationController(userService, adminService);

        // Set up the frame
        setTitle("ParkEasy - Registration");
        setSize(450, 350);
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
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);

        userRadioButton = new JRadioButton("User");
        userRadioButton.setSelected(true); // Default selection

        adminRadioButton = new JRadioButton("Admin");

        // Group the radio buttons
        ButtonGroup group = new ButtonGroup();
        group.add(userRadioButton);
        group.add(adminRadioButton);

        registerButton = new JButton("Register");
        backToLoginButton = new JButton("Back to Login");

        // Add action listeners
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

        backToLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openLoginView();
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

        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(nameField, gbc);

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(emailField, gbc);

        // Phone field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Phone:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(phoneField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passwordField, gbc);

        // Confirm Password field
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Confirm Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(confirmPasswordField, gbc);

        // Radio buttons
        JPanel radioPanel = new JPanel();
        radioPanel.add(userRadioButton);
        radioPanel.add(adminRadioButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(radioPanel, gbc);

        // Add form panel to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Create the button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(registerButton);
        buttonPanel.add(backToLoginButton);

        // Add button panel to main panel
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);
    }

    private void register() {
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        boolean isAdmin = adminRadioButton.isSelected();

        // Validate input
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields.",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match.",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean success;

            if (isAdmin) {
                success = registrationController.registerAdmin(name, email, phone, password);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Admin registration successful!",
                            "Registration Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                    openLoginView();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Admin registration failed. Email or phone may already be in use.",
                            "Registration Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                success = registrationController.registerUser(name, email, phone, password);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "User registration successful!",
                            "Registration Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                    openLoginView();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "User registration failed. Email or phone may already be in use.",
                            "Registration Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "An error occurred during registration: " + ex.getMessage(),
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openLoginView() {
        new LoginView();
        dispose(); // Close registration window
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RegistrationView();
        });
    }
}