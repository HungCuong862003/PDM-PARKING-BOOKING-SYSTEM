package main.java.com.parkeasy.view.auth;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton forgotPasswordButton;
    private JToggleButton renterToggle;
    private JToggleButton adminToggle;
    private ButtonGroup userTypeGroup;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Colors
    private final Color DARK_BG = new Color(40, 40, 40);
    private final Color MEDIUM_BG = new Color(60, 60, 60);
    private final Color BLUE_BUTTON = new Color(59, 130, 246);
    private final Color WHITE_TEXT = new Color(255, 255, 255);
    private final Color LIGHT_GRAY_TEXT = new Color(180, 180, 180);
    private final Color SELECTED_BG = new Color(59, 130, 246);
    private final Color UNSELECTED_BG = new Color(90, 90, 90);

    // Fonts
    private final Font HEADER_FONT = new Font("Arial", Font.BOLD, 24);
    private final Font REGULAR_FONT = new Font("Arial", Font.PLAIN, 14);
    private final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);
    private final Font FOOTER_FONT = new Font("Arial", Font.PLAIN, 12);

    public LoginView(JPanel mainPanel, CardLayout cardLayout) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;

        setLayout(new BorderLayout());
        setBackground(MEDIUM_BG);

        // Create left panel (welcome message)
        JPanel leftPanel = createLeftPanel();

        // Create right panel (login form)
        JPanel rightPanel = createRightPanel();

        // Add panels to main container
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(DARK_BG);
        leftPanel.setPreferredSize(new Dimension(300, 500));
        leftPanel.setBorder(new EmptyBorder(40, 30, 40, 30));

        // Logo and app name
        JLabel logoLabel = new JLabel("\uD83D\uDE97"); // Car emoji
        logoLabel.setFont(new Font("Dialog", Font.PLAIN, 36));
        logoLabel.setForeground(WHITE_TEXT);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel appNameLabel = new JLabel("ParkEasy");
        appNameLabel.setFont(HEADER_FONT);
        appNameLabel.setForeground(WHITE_TEXT);
        appNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome Back!");
        welcomeLabel.setFont(HEADER_FONT);
        welcomeLabel.setForeground(WHITE_TEXT);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea descriptionLabel = new JTextArea("Access your parking management dashboard to view and manage your parking spaces with ease.");
        descriptionLabel.setFont(REGULAR_FONT);
        descriptionLabel.setForeground(LIGHT_GRAY_TEXT);
        descriptionLabel.setBackground(DARK_BG);
        descriptionLabel.setWrapStyleWord(true);
        descriptionLabel.setLineWrap(true);
        descriptionLabel.setEditable(false);
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Feature description
        JPanel featuresPanel = createFeaturesPanel();

        // Add components to panel
        leftPanel.add(logoLabel);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(appNameLabel);
        leftPanel.add(Box.createVerticalStrut(30));
        leftPanel.add(welcomeLabel);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(descriptionLabel);
        leftPanel.add(Box.createVerticalStrut(40));
        leftPanel.add(featuresPanel);

        return leftPanel;
    }

    private JPanel createFeaturesPanel() {
        JPanel featuresPanel = new JPanel();
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));
        featuresPanel.setBackground(DARK_BG);
        featuresPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Feature 1
        JPanel feature1 = createFeatureItem("\uD83D\uDD11", "Easy Parking Management",
                "Manage all your parking spaces or find available spots quickly");

        // Feature 2
        JPanel feature2 = createFeatureItem("\uD83D\uDD52", "Real-time Availability",
                "Get instant updates on available parking spots");

        // Feature 3
        JPanel feature3 = createFeatureItem("\uD83D\uDCB3", "Secure Payments",
                "Pay for parking easily with multiple payment options");

        featuresPanel.add(feature1);
        featuresPanel.add(Box.createVerticalStrut(20));
        featuresPanel.add(feature2);
        featuresPanel.add(Box.createVerticalStrut(20));
        featuresPanel.add(feature3);

        return featuresPanel;
    }

    private JPanel createFeatureItem(String icon, String title, String description) {
        JPanel featurePanel = new JPanel();
        featurePanel.setLayout(new BoxLayout(featurePanel, BoxLayout.Y_AXIS));
        featurePanel.setBackground(DARK_BG);
        featurePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel iconAndTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        iconAndTitlePanel.setBackground(DARK_BG);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        iconLabel.setForeground(WHITE_TEXT);

        JLabel titleLabel = new JLabel(" " + title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(WHITE_TEXT);

        iconAndTitlePanel.add(iconLabel);
        iconAndTitlePanel.add(titleLabel);

        JTextArea descLabel = new JTextArea(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(LIGHT_GRAY_TEXT);
        descLabel.setBackground(DARK_BG);
        descLabel.setWrapStyleWord(true);
        descLabel.setLineWrap(true);
        descLabel.setEditable(false);
        descLabel.setBorder(null);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        featurePanel.add(iconAndTitlePanel);
        featurePanel.add(Box.createVerticalStrut(5));
        featurePanel.add(descLabel);

        return featurePanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(MEDIUM_BG);
        rightPanel.setBorder(new EmptyBorder(40, 60, 40, 60));

        // Login/Sign Up Tabs
        JPanel tabsPanel = new JPanel(new GridLayout(1, 2));
        tabsPanel.setBackground(MEDIUM_BG);
        tabsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JButton loginTabButton = new JButton("Login");
        loginTabButton.setFont(BUTTON_FONT);
        loginTabButton.setForeground(BLUE_BUTTON);
        loginTabButton.setBackground(MEDIUM_BG);
        loginTabButton.setBorderPainted(false);
        loginTabButton.setFocusPainted(false);
        loginTabButton.setContentAreaFilled(false);

        JButton signUpTabButton = new JButton("Sign Up");
        signUpTabButton.setFont(BUTTON_FONT);
        signUpTabButton.setForeground(LIGHT_GRAY_TEXT);
        signUpTabButton.setBackground(MEDIUM_BG);
        signUpTabButton.setBorderPainted(false);
        signUpTabButton.setFocusPainted(false);
        signUpTabButton.setContentAreaFilled(false);

        // Add active indicator for login tab
        JPanel loginIndicator = new JPanel();
        loginIndicator.setBackground(BLUE_BUTTON);
        loginIndicator.setPreferredSize(new Dimension(100, 2));
        loginIndicator.setMaximumSize(new Dimension(100, 2));

        JPanel signUpIndicator = new JPanel();
        signUpIndicator.setBackground(MEDIUM_BG);
        signUpIndicator.setPreferredSize(new Dimension(100, 2));
        signUpIndicator.setMaximumSize(new Dimension(100, 2));

        JPanel loginTabPanel = new JPanel(new BorderLayout());
        loginTabPanel.setBackground(MEDIUM_BG);
        loginTabPanel.add(loginTabButton, BorderLayout.CENTER);
        loginTabPanel.add(loginIndicator, BorderLayout.SOUTH);

        JPanel signUpTabPanel = new JPanel(new BorderLayout());
        signUpTabPanel.setBackground(MEDIUM_BG);
        signUpTabPanel.add(signUpTabButton, BorderLayout.CENTER);
        signUpTabPanel.add(signUpIndicator, BorderLayout.SOUTH);

        tabsPanel.add(loginTabPanel);
        tabsPanel.add(signUpTabPanel);

        // User type toggle buttons
        JPanel userTypePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        userTypePanel.setBackground(MEDIUM_BG);
        userTypePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        renterToggle = createToggleButton("Renter", true);
        adminToggle = createToggleButton("Owner/Admin", false);

        userTypeGroup = new ButtonGroup();
        userTypeGroup.add(renterToggle);
        userTypeGroup.add(adminToggle);

        userTypePanel.add(renterToggle);
        userTypePanel.add(adminToggle);

        // Form fields
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(REGULAR_FONT);
        emailLabel.setForeground(WHITE_TEXT);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField = new JTextField();
        emailField.setFont(REGULAR_FONT);
        emailField.setBackground(MEDIUM_BG);
        emailField.setForeground(WHITE_TEXT);
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, LIGHT_GRAY_TEXT),
                BorderFactory.createEmptyBorder(5, 0, 5, 0)));
        emailField.setCaretColor(WHITE_TEXT);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(REGULAR_FONT);
        passwordLabel.setForeground(WHITE_TEXT);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setFont(REGULAR_FONT);
        passwordField.setBackground(MEDIUM_BG);
        passwordField.setForeground(WHITE_TEXT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, LIGHT_GRAY_TEXT),
                BorderFactory.createEmptyBorder(5, 0, 5, 0)));
        passwordField.setCaretColor(WHITE_TEXT);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        // Login button
        loginButton = new JButton("Login");
        loginButton.setFont(BUTTON_FONT);
        loginButton.setForeground(WHITE_TEXT);
        loginButton.setBackground(BLUE_BUTTON);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // Forgot password link
        forgotPasswordButton = new JButton("Forgot Password?");
        forgotPasswordButton.setFont(FOOTER_FONT);
        forgotPasswordButton.setForeground(LIGHT_GRAY_TEXT);
        forgotPasswordButton.setBackground(MEDIUM_BG);
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setFocusPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to panel
        rightPanel.add(tabsPanel);
        rightPanel.add(Box.createVerticalStrut(30));
        rightPanel.add(userTypePanel);
        rightPanel.add(Box.createVerticalStrut(30));
        rightPanel.add(emailLabel);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(emailField);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(passwordLabel);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(passwordField);
        rightPanel.add(Box.createVerticalStrut(30));
        rightPanel.add(loginButton);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(forgotPasswordButton);

        // Add action listeners
        loginTabButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Stay on login panel
                loginTabButton.setForeground(BLUE_BUTTON);
                signUpTabButton.setForeground(LIGHT_GRAY_TEXT);
                loginIndicator.setBackground(BLUE_BUTTON);
                signUpIndicator.setBackground(MEDIUM_BG);
            }
        });

        signUpTabButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Switch to signup panel
                cardLayout.show(mainPanel, "signup");
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                String userType = renterToggle.isSelected() ? "Renter" : "Admin";

                // Basic validation
                if (email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginView.this,
                            "Please enter both email and password",
                            "Login Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // TODO: Implement authentication logic with database
                System.out.println("Login attempt - Email: " + email + ", Type: " + userType);

                // For demo purposes, simulate successful login
                JOptionPane.showMessageDialog(LoginView.this,
                        "Login successful as " + userType,
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                // TODO: Navigate to appropriate dashboard
            }
        });

        forgotPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show password recovery dialog
                showPasswordRecoveryDialog();
            }
        });

        return rightPanel;
    }

    private void showPasswordRecoveryDialog() {
        JDialog recoveryDialog = new JDialog();
        recoveryDialog.setTitle("Password Recovery");
        recoveryDialog.setSize(400, 200);
        recoveryDialog.setLocationRelativeTo(this);
        recoveryDialog.setModal(true);
        recoveryDialog.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(MEDIUM_BG);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Reset Password");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(WHITE_TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel emailLabel = new JLabel("Enter your email address:");
        emailLabel.setFont(REGULAR_FONT);
        emailLabel.setForeground(WHITE_TEXT);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField recoveryEmailField = new JTextField();
        recoveryEmailField.setFont(REGULAR_FONT);
        recoveryEmailField.setBackground(MEDIUM_BG);
        recoveryEmailField.setForeground(WHITE_TEXT);
        recoveryEmailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, LIGHT_GRAY_TEXT),
                BorderFactory.createEmptyBorder(5, 0, 5, 0)));
        recoveryEmailField.setCaretColor(WHITE_TEXT);
        recoveryEmailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JButton sendButton = new JButton("Send Recovery Email");
        sendButton.setFont(BUTTON_FONT);
        sendButton.setForeground(WHITE_TEXT);
        sendButton.setBackground(BLUE_BUTTON);
        sendButton.setBorderPainted(false);
        sendButton.setFocusPainted(false);
        sendButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = recoveryEmailField.getText();
                if (email.isEmpty()) {
                    JOptionPane.showMessageDialog(recoveryDialog,
                            "Please enter your email address",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // TODO: Implement password recovery logic

                JOptionPane.showMessageDialog(recoveryDialog,
                        "Password recovery instructions have been sent to " + email,
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                recoveryDialog.dispose();
            }
        });

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(emailLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(recoveryEmailField);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(sendButton);

        recoveryDialog.add(contentPanel, BorderLayout.CENTER);
        recoveryDialog.setVisible(true);
    }

    private JToggleButton createToggleButton(String text, boolean selected) {
        JToggleButton button = new JToggleButton(text, selected);
        button.setFont(BUTTON_FONT);
        button.setForeground(WHITE_TEXT);
        button.setBackground(selected ? SELECTED_BG : UNSELECTED_BG);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                renterToggle.setBackground(renterToggle.isSelected() ? SELECTED_BG : UNSELECTED_BG);
                adminToggle.setBackground(adminToggle.isSelected() ? SELECTED_BG : UNSELECTED_BG);
            }
        });

        return button;
    }

    // Method to clear all fields
    public void clearFields() {
        emailField.setText("");
        passwordField.setText("");
        renterToggle.setSelected(true);
        adminToggle.setSelected(false);
        renterToggle.setBackground(SELECTED_BG);
        adminToggle.setBackground(UNSELECTED_BG);
    }

    // For testing the panel independently
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("ParkEasy Login");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                CardLayout cardLayout = new CardLayout();
                JPanel mainPanel = new JPanel(cardLayout);

                LoginView loginPanel = new LoginView(mainPanel, cardLayout);

                mainPanel.add(loginPanel, "login");

                cardLayout.show(mainPanel, "login");

                frame.getContentPane().add(mainPanel);
                frame.setSize(900, 600);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
