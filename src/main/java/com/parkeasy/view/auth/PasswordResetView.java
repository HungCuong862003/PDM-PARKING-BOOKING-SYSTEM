package main.java.com.parkeasy.view.auth;

import main.java.com.parkeasy.controller.auth.PasswordResetController;
import main.java.com.parkeasy.repository.AdminRepository;
import main.java.com.parkeasy.repository.UserRepository;
import main.java.com.parkeasy.util.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Password Reset view for the ParkEasy application
 * Allows users and admins to reset their passwords
 */
public class PasswordResetView extends JFrame {
    private JTextField emailField;
    private JButton resetButton;
    private JButton backToLoginButton;

    private PasswordResetController passwordResetController;

    public PasswordResetView() {
        // Initialize repositories and controller
        UserRepository userRepository = new UserRepository();
        AdminRepository adminRepository = new AdminRepository();
        passwordResetController = new PasswordResetController(userRepository, adminRepository);

        // Set up the frame
        setTitle("ParkEasy - Password Reset");
        setSize(400, 200);
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
        resetButton = new JButton("Reset Password");
        backToLoginButton = new JButton("Back to Login");

        // Add action listeners
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetPassword();
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

        // Add instructions
        JLabel instructionsLabel = new JLabel("Enter your email to reset your password:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(instructionsLabel, gbc);

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(emailField, gbc);

        // Add form panel to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Create the button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(resetButton);
        buttonPanel.add(backToLoginButton);

        // Add button panel to main panel
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);
    }

    private void resetPassword() {
        String email = emailField.getText();

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter your email address.",
                    "Reset Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean success = passwordResetController.resetPassword(email);

            if (success) {
                String defaultPassword = passwordResetController.getDefaultPassword();
                JOptionPane.showMessageDialog(this,
                        "Your password has been reset. Your temporary password is: " + defaultPassword +
                                "\nPlease change it after logging in.",
                        "Password Reset Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                openLoginView();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Password reset failed. Email not found.",
                        "Reset Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "An error occurred during password reset: " + ex.getMessage(),
                    "Reset Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openLoginView() {
        new LoginView();
        dispose(); // Close password reset window
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PasswordResetView();
        });
    }
}