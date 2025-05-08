package main.java.com.parkeasy.view.user;

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
 * Design synced with AdminProfileView
 */
public class UserProfileView extends JFrame {
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

    private UserService userService;
    private VehicleService vehicleService;
    private User currentUser;

    public UserProfileView(User user) {
        this.currentUser = user;

        // Initialize services
        userService = new UserService();
        vehicleService = new VehicleService();

        // Set up the frame
        setTitle("ParkEasy - User Profile");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create components
        initComponents();

        // Layout the components
        layoutComponents();

        // Load user data
        loadUserData();

        // Make the frame visible
        setVisible(true);
    }

    private void initComponents() {
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);
        currentPasswordField = new JPasswordField(20);
        newPasswordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        amountField = new JTextField(20);
        balanceLabel = new JLabel();

        updateProfileButton = new JButton("Update Profile");
        changePasswordButton = new JButton("Change Password");
        addFundsButton = new JButton("Add Funds");
        backButton = new JButton("Back to Dashboard");

        // Add action listeners
        updateProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProfile();
            }
        });

        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePassword();
            }
        });

        addFundsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addFunds();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the profile window
            }
        });
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        // Create the main panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        // Profile panel
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // User ID (non-editable)
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("User ID:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField idField = new JTextField(String.valueOf(currentUser.getUserID()), 20);
        idField.setEditable(false);
        formPanel.add(idField, gbc);

        // Name field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(nameField, gbc);

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(emailField, gbc);

        // Phone field
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Phone:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(phoneField, gbc);

        // Update button
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(updateProfileButton, gbc);

        profilePanel.add(formPanel, BorderLayout.NORTH);

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
        gbc.gridy = 0;
        passwordPanel.add(currentPasswordField, gbc);

        // New password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        passwordPanel.add(new JLabel("New Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        passwordPanel.add(newPasswordField, gbc);

        // Confirm password field
        gbc.gridx = 0;
        gbc.gridy = 2;
        passwordPanel.add(new JLabel("Confirm Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
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

        // Current balance display
        gbc.gridx = 0;
        gbc.gridy = 0;
        balancePanel.add(new JLabel("Current Balance:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        balancePanel.add(balanceLabel, gbc);

        // Amount field for adding funds
        gbc.gridx = 0;
        gbc.gridy = 1;
        balancePanel.add(new JLabel("Add Amount:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        balancePanel.add(amountField, gbc);

        // Add funds button
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        balancePanel.add(addFundsButton, gbc);

        // Add the panels to the tabbed pane
        tabbedPane.addTab("Profile Information", profilePanel);
        tabbedPane.addTab("Change Password", passwordPanel);
        tabbedPane.addTab("Account Balance", balancePanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Add back button at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadUserData() {
        try {
            // Refresh user data from the database
            User refreshedUser = userService.getUserById(currentUser.getUserID());
            if (refreshedUser != null) {
                currentUser = refreshedUser;
            }

            // Set field values
            nameField.setText(currentUser.getUserName());
            emailField.setText(currentUser.getEmail());
            phoneField.setText(currentUser.getPhone());
            balanceLabel.setText(String.format("%,.0f VND", currentUser.getBalance()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading user data: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProfile() {
        try {
            String name = nameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();

            // Validate input
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill in all fields.",
                        "Update Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update user data
            currentUser.setUserName(name);
            currentUser.setEmail(email);
            currentUser.setPhone(phone);

            // Save to database
            boolean success = userService.updateUser(currentUser);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Profile updated successfully.",
                        "Update Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update profile. Email or phone may already be in use.",
                        "Update Error",
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
        try {
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            // Validate input
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill in all password fields.",
                        "Password Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this,
                        "New password and confirmation do not match.",
                        "Password Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verify current password
            if (!currentPassword.equals(currentUser.getPassword())) {
                JOptionPane.showMessageDialog(this,
                        "Current password is incorrect.",
                        "Password Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update password
            currentUser.setPassword(newPassword);

            // Save to database
            boolean success = userService.updateUser(currentUser);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Password changed successfully.",
                        "Password Change Successful",
                        JOptionPane.INFORMATION_MESSAGE);

                // Clear password fields
                currentPasswordField.setText("");
                newPasswordField.setText("");
                confirmPasswordField.setText("");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to change password.",
                        "Password Error",
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
        try {
            String amountText = amountField.getText();

            // Validate input
            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter an amount to add.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            float amount;
            try {
                amount = (float) Float.parseFloat(amountText);
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

            // Add funds to the user's account
            float newBalance = currentUser.getBalance() + amount;
            currentUser.setBalance(newBalance);

            // Save to database
            boolean success = userService.updateUser(currentUser);

            if (success) {
                // Update the balance label
                balanceLabel.setText(String.format("%,.0f VND", newBalance));

                JOptionPane.showMessageDialog(this,
                        "Funds added successfully. New balance: " + String.format("%,.0f VND", newBalance),
                        "Funds Added",
                        JOptionPane.INFORMATION_MESSAGE);

                // Clear amount field
                amountField.setText("");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to add funds.",
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
        mockUser.setBalance(100.0F);

        SwingUtilities.invokeLater(() -> {
            new UserProfileView(mockUser);
        });
    }
}