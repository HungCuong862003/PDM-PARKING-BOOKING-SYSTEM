package main.java.com.parkeasy.view.admin;

import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.service.AdminService;
import main.java.com.parkeasy.util.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Profile view for admin users
 * Allows viewing and updating admin information
 */
public class AdminProfileView extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton updateProfileButton;
    private JButton changePasswordButton;
    private JButton backButton;
    
    private AdminService adminService;
    private Admin currentAdmin;
    
    public AdminProfileView(Admin admin) {
        this.currentAdmin = admin;
        
        // Initialize services
        adminService = new AdminService();
        
        // Set up the frame
        setTitle("ParkEasy - Admin Profile");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create components
        initComponents();
        
        // Layout the components
        layoutComponents();
        
        // Load admin data
        loadAdminData();
        
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
        
        updateProfileButton = new JButton("Update Profile");
        changePasswordButton = new JButton("Change Password");
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
        
        // Admin ID (non-editable)
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Admin ID:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField idField = new JTextField(String.valueOf(currentAdmin.getAdminID()), 20);
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
        
        // Add the panels to the tabbed pane
        tabbedPane.addTab("Profile Information", profilePanel);
        tabbedPane.addTab("Change Password", passwordPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Add back button at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadAdminData() {
        try {
            // Refresh admin data from the database
            Admin refreshedAdmin = adminService.getAdminById(currentAdmin.getAdminID());
            if (refreshedAdmin != null) {
                currentAdmin = refreshedAdmin;
            }
            
            // Set field values
            nameField.setText(currentAdmin.getAdminName());
            emailField.setText(currentAdmin.getEmail());
            phoneField.setText(currentAdmin.getPhone());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading admin data: " + ex.getMessage(), 
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
            
            // Update admin data
            currentAdmin.setAdminName(name);
            currentAdmin.setEmail(email);
            currentAdmin.setPhone(phone);
            
            // Save to database
            boolean success = adminService.updateAdmin(currentAdmin);
            
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
            if (!currentPassword.equals(currentAdmin.getPassword())) {
                JOptionPane.showMessageDialog(this, 
                    "Current password is incorrect.", 
                    "Password Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update password
            currentAdmin.setPassword(newPassword);
            
            // Save to database
            boolean success = adminService.updateAdmin(currentAdmin);
            
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
    
    // Main method for testing
    public static void main(String[] args) {
        // Create a mock admin for testing
        Admin mockAdmin = new Admin();
        mockAdmin.setAdminID(1);
        mockAdmin.setAdminName("Test Admin");
        mockAdmin.setEmail("admin@example.com");
        mockAdmin.setPhone("123-456-7890");
        mockAdmin.setPassword("password");
        
        SwingUtilities.invokeLater(() -> {
            new AdminProfileView(mockAdmin);
        });
    }
}