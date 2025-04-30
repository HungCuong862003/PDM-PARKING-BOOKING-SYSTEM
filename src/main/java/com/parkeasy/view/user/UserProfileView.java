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

    public UserProfileView(User user) {
        this.currentUser = user;

        // Initialize services and controller
        UserService userService = new UserService();
        VehicleService vehicleService = new VehicleService();
        userProfileController = new UserProfileController(userService, vehicleService);

        // Set up the frame
        setTitle("ParkEasy - User Profile");
        setSize(550, 600);
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
        balanceLabel = new JLabel("$" + String.format("%.2f", currentUser.getBalance()));

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
                dispose();
            }
        });
    }

    private void layoutComponents() {
        // Create a main panel to hold everything
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add profile panel
        mainPanel.add(createProfilePanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Add password panel
        mainPanel.add(createPasswordPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Add balance panel
        mainPanel.add(createBalancePanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Add back button in a centered panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel);

        // Create a scroll pane with the content
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);

        // Add scroll pane to frame
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Profile Information"));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        // Create a form panel with right-aligned labels
        JPanel formPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 5);

        // Name field - right aligned label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel nameLabel = new JLabel("Name:");
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(nameField, gbc);

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        JLabel emailLabel = new JLabel("Email:");
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(emailField, gbc);

        // Phone field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        JLabel phoneLabel = new JLabel("Phone:");
        formPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(phoneField, gbc);

        // Add formPanel to the center of the main panel
        panel.add(formPanel, BorderLayout.CENTER);

        // Create button panel at the bottom
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(updateProfileButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Change Password"));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // Create a form panel with right-aligned labels
        JPanel formPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 5);

        // Current password field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel currentPasswordLabel = new JLabel("Current Password:");
        formPanel.add(currentPasswordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(currentPasswordField, gbc);

        // New password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        JLabel newPasswordLabel = new JLabel("New Password:");
        formPanel.add(newPasswordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(newPasswordField, gbc);

        // Confirm password field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        formPanel.add(confirmPasswordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(confirmPasswordField, gbc);

        // Add formPanel to the center of the main panel
        panel.add(formPanel, BorderLayout.CENTER);

        // Create button panel at the bottom
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(changePasswordButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBalancePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Account Balance"));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        // Create a form panel with right-aligned labels
        JPanel formPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 5);

        // Current balance field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel currentBalanceLabel = new JLabel("Current Balance:");
        formPanel.add(currentBalanceLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(balanceLabel, gbc);

        // Add amount field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel addAmountLabel = new JLabel("Add Amount:");
        formPanel.add(addAmountLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(amountField, gbc);

        // Add formPanel to the center of the main panel
        panel.add(formPanel, BorderLayout.CENTER);

        // Create button panel at the bottom
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addFundsButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
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
            Map<String, Object> result = userProfileController.addFunds(currentUser.getUserID(), amount);

            boolean success = (boolean) result.get("success");
            if (success) {
                // Update the user's balance
                double newBalance = (double) result.get("newBalance");
                currentUser.setBalance(newBalance);

                // Update the balance label
                balanceLabel.setText("$" + String.format("%.2f", newBalance));

                JOptionPane.showMessageDialog(this,
                        "Funds added successfully. New balance: $" + String.format("%.2f", newBalance),
                        "Funds Added",
                        JOptionPane.INFORMATION_MESSAGE);

                // Clear amount field
                amountField.setText("");
            } else {
                JOptionPane.showMessageDialog(this,
                        result.get("message").toString(),
                        "Failed to Add Funds",
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