package main.java.com.parkeasy.view.auth;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * The RegistrationView class represents the registration screen for the ParkEasy application.
 * It provides a simple interface for users to create a new account.
 * Implemented with Java Swing for IntelliJ.
 */
public class RegistrationView extends JPanel {

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton renterButton;
    private JButton ownerAdminButton;
    private JButton loginButton;

    /**
     * Constructor for the RegistrationView class.
     * Sets up the layout and components of the registration screen.
     */
    public RegistrationView() {
        setLayout(new BorderLayout());

        // Create left and right panels
        JPanel leftPanel = createLeftPanel();
        JPanel rightPanel = createRightPanel();

        // Add panels to main layout
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    /**
     * Creates the left panel of the registration screen with app logo and information.
     *
     * @return the configured left panel
     */
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(45, 45, 45));
        leftPanel.setPreferredSize(new Dimension(500, 700));
        leftPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // App logo and name
        JLabel logoLabel = new JLabel("🚗", JLabel.CENTER);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 48));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appNameLabel = new JLabel("ParkEasy", JLabel.CENTER);
        appNameLabel.setFont(new Font("Arial", Font.BOLD, 32));
        appNameLabel.setForeground(Color.WHITE);
        appNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Separator
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(500, 2));
        separator.setForeground(new Color(80, 80, 80));

        // Welcome message
        JLabel welcomeLabel = new JLabel("Join ParkEasy Today!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Description
        JTextArea descriptionArea = new JTextArea(
                "Create an account to access all features of our parking management system."
        );
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setOpaque(false);
        descriptionArea.setForeground(Color.WHITE);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 16));
        descriptionArea.setEditable(false);
        descriptionArea.setMaximumSize(new Dimension(400, 100));
        descriptionArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Feature panels
        JPanel featurePanel = createFeaturePanel();

        // Add spacing between components
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(logoLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(appNameLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        leftPanel.add(separator);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        leftPanel.add(welcomeLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(descriptionArea);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        leftPanel.add(featurePanel);
        leftPanel.add(Box.createVerticalGlue());

        return leftPanel;
    }

    /**
     * Creates a panel containing feature information.
     *
     * @return the configured feature panel
     */
    private JPanel createFeaturePanel() {
        JPanel featurePanel = new JPanel();
        featurePanel.setLayout(new BoxLayout(featurePanel, BoxLayout.Y_AXIS));
        featurePanel.setOpaque(false);

        // Feature 1
        JPanel feature1 = new JPanel(new BorderLayout());
        feature1.setOpaque(false);
        feature1.setMaximumSize(new Dimension(420, 100));

        JLabel icon1 = new JLabel("🅿️");
        icon1.setFont(new Font("Arial", Font.PLAIN, 24));
        icon1.setForeground(Color.WHITE);
        icon1.setBorder(new EmptyBorder(0, 0, 0, 15));

        JPanel textPanel1 = new JPanel();
        textPanel1.setLayout(new BoxLayout(textPanel1, BoxLayout.Y_AXIS));
        textPanel1.setOpaque(false);

        JLabel title1 = new JLabel("Easy Parking Management");
        title1.setFont(new Font("Arial", Font.BOLD, 18));
        title1.setForeground(Color.WHITE);

        JLabel desc1 = new JLabel("Manage all your parking spaces or find available spots quickly");
        desc1.setFont(new Font("Arial", Font.PLAIN, 14));
        desc1.setForeground(Color.LIGHT_GRAY);

        textPanel1.add(title1);
        textPanel1.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel1.add(desc1);

        feature1.add(icon1, BorderLayout.WEST);
        feature1.add(textPanel1, BorderLayout.CENTER);

        // Feature 2
        JPanel feature2 = new JPanel(new BorderLayout());
        feature2.setOpaque(false);
        feature2.setMaximumSize(new Dimension(420, 100));

        JLabel icon2 = new JLabel("🔄");
        icon2.setFont(new Font("Arial", Font.PLAIN, 24));
        icon2.setForeground(Color.WHITE);
        icon2.setBorder(new EmptyBorder(0, 0, 0, 15));

        JPanel textPanel2 = new JPanel();
        textPanel2.setLayout(new BoxLayout(textPanel2, BoxLayout.Y_AXIS));
        textPanel2.setOpaque(false);

        JLabel title2 = new JLabel("Real-time Availability");
        title2.setFont(new Font("Arial", Font.BOLD, 18));
        title2.setForeground(Color.WHITE);

        JLabel desc2 = new JLabel("Get instant updates on available parking spots");
        desc2.setFont(new Font("Arial", Font.PLAIN, 14));
        desc2.setForeground(Color.LIGHT_GRAY);

        textPanel2.add(title2);
        textPanel2.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel2.add(desc2);

        feature2.add(icon2, BorderLayout.WEST);
        feature2.add(textPanel2, BorderLayout.CENTER);

        // Feature 3
        JPanel feature3 = new JPanel(new BorderLayout());
        feature3.setOpaque(false);
        feature3.setMaximumSize(new Dimension(420, 100));

        JLabel icon3 = new JLabel("💳");
        icon3.setFont(new Font("Arial", Font.PLAIN, 24));
        icon3.setForeground(Color.WHITE);
        icon3.setBorder(new EmptyBorder(0, 0, 0, 15));

        JPanel textPanel3 = new JPanel();
        textPanel3.setLayout(new BoxLayout(textPanel3, BoxLayout.Y_AXIS));
        textPanel3.setOpaque(false);

        JLabel title3 = new JLabel("Secure Payments");
        title3.setFont(new Font("Arial", Font.BOLD, 18));
        title3.setForeground(Color.WHITE);

        JLabel desc3 = new JLabel("Pay for parking easily with multiple payment options");
        desc3.setFont(new Font("Arial", Font.PLAIN, 14));
        desc3.setForeground(Color.LIGHT_GRAY);

        textPanel3.add(title3);
        textPanel3.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel3.add(desc3);

        feature3.add(icon3, BorderLayout.WEST);
        feature3.add(textPanel3, BorderLayout.CENTER);

        // Add features to panel with spacing
        featurePanel.add(feature1);
        featurePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        featurePanel.add(feature2);
        featurePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        featurePanel.add(feature3);

        return featurePanel;
    }

    /**
     * Creates the right panel of the registration screen with registration form.
     *
     * @return the configured right panel
     */
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(92, 92, 92));
        rightPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Tab buttons for Login and Sign Up
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        tabPanel.setOpaque(false);

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 18));
        loginButton.setForeground(Color.WHITE);
        loginButton.setContentAreaFilled(false);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel signUpLabel = new JLabel("Sign Up");
        signUpLabel.setFont(new Font("Arial", Font.BOLD, 18));
        signUpLabel.setForeground(new Color(98, 161, 232)); // Blue color

        tabPanel.add(loginButton);
        tabPanel.add(signUpLabel);

        // Tab indicator (blue line under "Sign Up")
        JPanel indicatorPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        indicatorPanel.setOpaque(false);

        JPanel blueIndicator = new JPanel();
        blueIndicator.setBackground(new Color(98, 161, 232));
        blueIndicator.setPreferredSize(new Dimension(80, 3));

        indicatorPanel.add(blueIndicator);

        // User type selection
        JPanel userTypePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        userTypePanel.setOpaque(false);

        renterButton = new JButton();
        renterButton.setLayout(new BorderLayout());
        renterButton.setBackground(new Color(98, 161, 232));
        renterButton.setForeground(Color.WHITE);
        renterButton.setPreferredSize(new Dimension(200, 100));
        renterButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel renterContent = new JPanel();
        renterContent.setLayout(new BoxLayout(renterContent, BoxLayout.Y_AXIS));
        renterContent.setOpaque(false);

        JLabel carIcon = new JLabel("🚗", JLabel.CENTER);
        carIcon.setFont(new Font("Arial", Font.PLAIN, 24));
        carIcon.setForeground(Color.WHITE);
        carIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel renterLabel = new JLabel("Renter", JLabel.CENTER);
        renterLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        renterLabel.setForeground(Color.WHITE);
        renterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        renterContent.add(Box.createVerticalGlue());
        renterContent.add(carIcon);
        renterContent.add(Box.createRigidArea(new Dimension(0, 10)));
        renterContent.add(renterLabel);
        renterContent.add(Box.createVerticalGlue());

        renterButton.add(renterContent, BorderLayout.CENTER);

        ownerAdminButton = new JButton();
        ownerAdminButton.setLayout(new BorderLayout());
        ownerAdminButton.setBackground(new Color(92, 92, 92));
        ownerAdminButton.setForeground(Color.WHITE);
        ownerAdminButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        ownerAdminButton.setPreferredSize(new Dimension(200, 100));
        ownerAdminButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel ownerContent = new JPanel();
        ownerContent.setLayout(new BoxLayout(ownerContent, BoxLayout.Y_AXIS));
        ownerContent.setOpaque(false);

        JLabel adminIcon = new JLabel("≡", JLabel.CENTER);
        adminIcon.setFont(new Font("Arial", Font.PLAIN, 24));
        adminIcon.setForeground(Color.WHITE);
        adminIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel ownerLabel = new JLabel("Owner/Admin", JLabel.CENTER);
        ownerLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        ownerLabel.setForeground(Color.WHITE);
        ownerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ownerContent.add(Box.createVerticalGlue());
        ownerContent.add(adminIcon);
        ownerContent.add(Box.createRigidArea(new Dimension(0, 10)));
        ownerContent.add(ownerLabel);
        ownerContent.add(Box.createVerticalGlue());

        ownerAdminButton.add(ownerContent, BorderLayout.CENTER);

        userTypePanel.add(renterButton);
        userTypePanel.add(ownerAdminButton);

        // Name field
        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        nameField = new JTextField();
        nameField.setBackground(new Color(92, 92, 92));
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(Color.WHITE);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        nameField.setPreferredSize(new Dimension(400, 40));
        nameField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));

        // Email field
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField = new JTextField();
        emailField.setBackground(new Color(92, 92, 92));
        emailField.setForeground(Color.WHITE);
        emailField.setCaretColor(Color.WHITE);
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        emailField.setPreferredSize(new Dimension(400, 40));
        emailField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));

        // Phone field
        JLabel phoneLabel = new JLabel("Phone Number");
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        phoneLabel.setForeground(Color.WHITE);
        phoneLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        phoneField = new JTextField();
        phoneField.setBackground(new Color(92, 92, 92));
        phoneField.setForeground(Color.WHITE);
        phoneField.setCaretColor(Color.WHITE);
        phoneField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        phoneField.setPreferredSize(new Dimension(400, 40));
        phoneField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));

        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setBackground(new Color(92, 92, 92));
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        passwordField.setPreferredSize(new Dimension(400, 40));
        passwordField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));

        // Confirm Password field
        JLabel confirmPasswordLabel = new JLabel("Confirm Password");
        confirmPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        confirmPasswordLabel.setForeground(Color.WHITE);
        confirmPasswordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBackground(new Color(92, 92, 92));
        confirmPasswordField.setForeground(Color.WHITE);
        confirmPasswordField.setCaretColor(Color.WHITE);
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        confirmPasswordField.setPreferredSize(new Dimension(400, 40));
        confirmPasswordField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));

        // Register button
        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBackground(new Color(98, 161, 232));
        registerButton.setPreferredSize(new Dimension(400, 40));
        registerButton.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add components to panel with spacing
        rightPanel.add(tabPanel);
        rightPanel.add(indicatorPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        rightPanel.add(userTypePanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(nameLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        rightPanel.add(nameField);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        rightPanel.add(emailLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        rightPanel.add(emailField);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        rightPanel.add(phoneLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        rightPanel.add(phoneField);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        rightPanel.add(passwordLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        rightPanel.add(passwordField);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        rightPanel.add(confirmPasswordLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        rightPanel.add(confirmPasswordField);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        rightPanel.add(registerButton);

        return rightPanel;
    }

    /**
     * Shows the registration view in a frame.
     */
    public void display() {
        JFrame frame = new JFrame("ParkEasy - Registration");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Event listeners

    /**
     * Sets the action listener for the register button.
     *
     * @param listener the ActionListener for the register button
     */
    public void setRegisterButtonListener(ActionListener listener) {
        registerButton.addActionListener(listener);
    }

    /**
     * Sets the action listener for the login button.
     *
     * @param listener the ActionListener for the login button
     */
    public void setLoginButtonListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    /**
     * Sets the action listener for the renter button.
     *
     * @param listener the ActionListener for the renter button
     */
    public void setRenterButtonListener(ActionListener listener) {
        renterButton.addActionListener(listener);
    }

    /**
     * Sets the action listener for the owner/admin button.
     *
     * @param listener the ActionListener for the owner/admin button
     */
    public void setOwnerAdminButtonListener(ActionListener listener) {
        ownerAdminButton.addActionListener(listener);
    }

    // Getters for input fields

    /**
     * Gets the name input.
     *
     * @return the name text
     */
    public String getName() {
        return nameField.getText();
    }

    /**
     * Gets the email input.
     *
     * @return the email text
     */
    public String getEmail() {
        return emailField.getText();
    }

    /**
     * Gets the phone input.
     *
     * @return the phone text
     */
    public String getPhone() {
        return phoneField.getText();
    }

    /**
     * Gets the password input.
     *
     * @return the password text
     */
    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    /**
     * Gets the confirm password input.
     *
     * @return the confirm password text
     */
    public String getConfirmPassword() {
        return new String(confirmPasswordField.getPassword());
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new RegistrationView().display();
        });
    }
}