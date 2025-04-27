package main.java.com.parkeasy.view.auth;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * The PasswordResetView class represents the password reset screen for the ParkEasy application.
 * It provides a simple interface for users to reset their password using email and phone number.
 * Implemented with Java Swing for IntelliJ.
 */
public class PasswordResetView extends JPanel {

    private JTextField emailField;
    private JTextField phoneField;
    private JButton resetPasswordButton;
    private JButton backToLoginButton;

    /**
     * Constructor for the PasswordResetView class.
     * Sets up the layout and components of the password reset screen.
     */
    public PasswordResetView() {
        setLayout(new BorderLayout());

        // Create left and right panels
        JPanel leftPanel = createLeftPanel();
        JPanel rightPanel = createRightPanel();

        // Add panels to main layout
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    /**
     * Creates the left panel of the password reset screen with app logo and information.
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

        // Description
        JTextArea descriptionArea = new JTextArea(
                "Access your parking management dashboard to view and manage your parking spaces with ease."
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
     * Creates the right panel of the password reset screen with reset form.
     *
     * @return the configured right panel
     */
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(92, 92, 92));
        rightPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Title
        JLabel titleLabel = new JLabel("Reset Password");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Instructions
        JTextArea instructionsArea = new JTextArea(
                "Enter your email and phone number to reset your password. Both must match our records for security verification."
        );
        instructionsArea.setWrapStyleWord(true);
        instructionsArea.setLineWrap(true);
        instructionsArea.setOpaque(false);
        instructionsArea.setForeground(Color.LIGHT_GRAY);
        instructionsArea.setFont(new Font("Arial", Font.PLAIN, 14));
        instructionsArea.setEditable(false);
        instructionsArea.setMaximumSize(new Dimension(400, 80));
        instructionsArea.setAlignmentX(Component.LEFT_ALIGNMENT);

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

        // Reset Password button
        resetPasswordButton = new JButton("Reset Password");
        resetPasswordButton.setFont(new Font("Arial", Font.BOLD, 16));
        resetPasswordButton.setForeground(Color.WHITE);
        resetPasswordButton.setBackground(new Color(98, 161, 232));
        resetPasswordButton.setPreferredSize(new Dimension(400, 40));
        resetPasswordButton.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        resetPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Back to login button
        backToLoginButton = new JButton("Back to Login");
        backToLoginButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backToLoginButton.setForeground(new Color(98, 161, 232));
        backToLoginButton.setContentAreaFilled(false);
        backToLoginButton.setBorderPainted(false);
        backToLoginButton.setFocusPainted(false);
        backToLoginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backToLoginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to panel with spacing
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(titleLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(instructionsArea);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        rightPanel.add(emailLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        rightPanel.add(emailField);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(phoneLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        rightPanel.add(phoneField);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        rightPanel.add(resetPasswordButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(backToLoginButton);
        rightPanel.add(Box.createVerticalGlue());

        return rightPanel;
    }

    /**
     * Shows the password reset view in a frame.
     */
    public void display() {
        JFrame frame = new JFrame("ParkEasy - Reset Password");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Event listeners

    /**
     * Sets the action listener for the reset password button.
     *
     * @param listener the ActionListener for the reset password button
     */
    public void setResetPasswordButtonListener(ActionListener listener) {
        resetPasswordButton.addActionListener(listener);
    }

    /**
     * Sets the action listener for the back to login button.
     *
     * @param listener the ActionListener for the back to login button
     */
    public void setBackToLoginButtonListener(ActionListener listener) {
        backToLoginButton.addActionListener(listener);
    }

    // Getters for input fields

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
     * Clears all input fields.
     */
    public void clearFields() {
        emailField.setText("");
        phoneField.setText("");
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new PasswordResetView().display();
        });
    }
}