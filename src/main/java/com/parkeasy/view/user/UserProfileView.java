package main.java.com.parkeasy.view.user;

import main.java.com.parkeasy.controller.user.UserProfileController;
import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.service.UserService;
import main.java.com.parkeasy.service.VehicleService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * Profile view for regular users
 * Allows users to update their account information
 * With centered layout and scrolling capability
 */
public class UserProfileView extends JFrame {
    private User currentUser;
    private UserProfileController userProfileController;

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JTextField amountField;
    private JLabel balanceLabel;

    private JButton updateProfileButton;
    private JButton changePasswordButton;
    private JButton addFundsButton;
    private JButton backButton;

    private JTabbedPane tabbedPane;

    public UserProfileView(User user) {
        this.currentUser = user;

        // Initialize services and controller
        UserService userService = new UserService();
        VehicleService vehicleService = new VehicleService();
        userProfileController = new UserProfileController(userService, vehicleService);

        // Set up the frame
        setTitle("ParkEasy - User Profile");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create components
        initComponents();

        // Layout the components
        layoutComponents();

        // Make the frame visible
        setVisible(true);
    }

    private void initComponents() {
        nameField = new JTextField(currentUser.getUserName(), 20);
        emailField = new JTextField(currentUser.getEmail(), 20);
        phoneField = new JTextField(currentUser.getPhone(), 20);

        currentPasswordField = new JPasswordField(20);
        newPasswordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);

        amountField = new JTextField(20);
        addFundsButton = new JButton("Add Funds");

        updateProfileButton = new JButton("Update Profile");
        changePasswordButton = new JButton("Change Password");
        backButton = new JButton("Back to Dashboard");

        // Add action listeners
        updateProfileButton.addActionListener(e -> updateProfile());
        changePasswordButton.addActionListener(e -> changePassword());
        addFundsButton.addActionListener(e -> addFunds());
        backButton.addActionListener(e -> dispose());

        tabbedPane = new JTabbedPane();
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        // Profile panel
        JPanel profilePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        profilePanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        profilePanel.add(nameField, gbc);

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 1;
        profilePanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        profilePanel.add(emailField, gbc);

        // Phone field
        gbc.gridx = 0;
        gbc.gridy = 2;
        profilePanel.add(new JLabel("Phone:"), gbc);

        gbc.gridx = 1;
        profilePanel.add(phoneField, gbc);

        // Update button
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        profilePanel.add(updateProfileButton, gbc);

        // Password panel
        JPanel passwordPanel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Current password field
        gbc.gridx = 0;
        gbc.gridy = 0;
        passwordPanel.add(new JLabel("Current Password:"), gbc);

        gbc.gridx = 1;
        passwordPanel.add(currentPasswordField, gbc);

        // New password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        passwordPanel.add(new JLabel("New Password:"), gbc);

        gbc.gridx = 1;
        passwordPanel.add(newPasswordField, gbc);

        // Confirm password field
        gbc.gridx = 0;
        gbc.gridy = 2;
        passwordPanel.add(new JLabel("Confirm Password:"), gbc);

        gbc.gridx = 1;
        passwordPanel.add(confirmPasswordField, gbc);

        // Change password button
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        passwordPanel.add(changePasswordButton, gbc);

        // Balance panel
        JPanel balancePanel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Amount field
        gbc.gridx = 0;
        gbc.gridy = 0;
        balancePanel.add(new JLabel("Amount:"), gbc);

        gbc.gridx = 1;
        balancePanel.add(amountField, gbc);

        // Add funds button
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        balancePanel.add(addFundsButton, gbc);

        // Add panels to tabbed pane
        tabbedPane.addTab("Profile Information", profilePanel);
        tabbedPane.addTab("Change Password", passwordPanel);
        tabbedPane.addTab("Add Funds", balancePanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Back button at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateProfile() {
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields.",
                    "Update Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Map<String, Object> result = userProfileController.updateUserProfile(
                    currentUser.getUserID(), name, phone, email);

            boolean success = (boolean) result.get("success");
            if (success) {
                // Update the current user object
                currentUser.setUserName(name);
                currentUser.setEmail(email);
                currentUser.setPhone(phone);

                JOptionPane.showMessageDialog(this,
                        "Profile updated successfully.",
                        "Update Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        result.get("message").toString(),
                        "Update Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error updating profile: " + ex.getMessage(),
                    "Update Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changePassword() {
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all password fields.",
                    "Password Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "New passwords do not match.",
                    "Password Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Map<String, Object> result = userProfileController.changePassword(
                    currentUser.getUserID(), currentPassword, newPassword);

            boolean success = (boolean) result.get("success");
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Password changed successfully.",
                        "Password Changed",
                        JOptionPane.INFORMATION_MESSAGE);

                // Clear password fields
                currentPasswordField.setText("");
                newPasswordField.setText("");
                confirmPasswordField.setText("");
            } else {
                JOptionPane.showMessageDialog(this,
                        result.get("message").toString(),
                        "Password Change Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error changing password: " + ex.getMessage(),
                    "Password Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addFunds() {
        String amountText = amountField.getText();

        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter an amount to add.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid number.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (amount <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Amount must be greater than zero.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Update the user's balance
            currentUser.setBalance(currentUser.getBalance() + amount);

            // Reflect the updated balance in the database
            boolean success = userProfileController.updateUserBalance(currentUser.getUserID(),
                    currentUser.getBalance());

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Funds added successfully. New balance: $" + String.format("%.2f", currentUser.getBalance()),
                        "Funds Added",
                        JOptionPane.INFORMATION_MESSAGE);

                // Clear amount field
                amountField.setText("");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update balance in the database.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error adding funds: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        // Create a mock user for testing
        User mockUser = new User();
        mockUser.setUserID(1);
        mockUser.setUserName("Test User");
        mockUser.setEmail("test@example.com");
        mockUser.setPhone("123-456-7890");
        mockUser.setBalance(100.0);

        SwingUtilities.invokeLater(() -> {
            new UserProfileView(mockUser);
        });
    }
}