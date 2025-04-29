package com.parkeasy.view.common.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Sidebar navigation component for both user and admin interfaces.
 * Provides navigation options based on user role.
 */
public class SidebarView extends JPanel {

    private final JPanel menuItemsPanel;
    private final boolean isAdmin;
    private final List<JButton> menuButtons;

    /**
     * Creates a sidebar view with navigation options based on user role.
     *
     * @param isAdmin Whether the current user is an admin
     */
    public SidebarView(boolean isAdmin) {
        this.isAdmin = isAdmin;
        this.menuButtons = new ArrayList<>();

        // Set layout
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200, 600));

        // Set background color
        setBackground(isAdmin ?
                new Color(81, 45, 168) : // Purple for admin
                new Color(30, 41, 59));  // Dark blue for user

        // Create logo container
        JPanel logoContainer = createLogoContainer();

        // Create menu items container
        menuItemsPanel = new JPanel();
        menuItemsPanel.setLayout(new BoxLayout(menuItemsPanel, BoxLayout.Y_AXIS));
        menuItemsPanel.setBorder(new EmptyBorder(20, 10, 10, 10));
        menuItemsPanel.setOpaque(false);

        // Add appropriate menu items based on user role
        if (isAdmin) {
            createAdminMenu();
        } else {
            createUserMenu();
        }

        // Create sign out button at bottom
        JButton signOutButton = createSignOutButton();

        // Create main panel to hold all components
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.add(logoContainer, BorderLayout.NORTH);
        mainPanel.add(menuItemsPanel, BorderLayout.CENTER);
        mainPanel.add(signOutButton, BorderLayout.SOUTH);

        // Add main panel to this panel
        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Creates the logo container for the sidebar.
     *
     * @return JPanel containing the logo
     */
    private JPanel createLogoContainer() {
        JLabel logoLabel = new JLabel("ParkEasy");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel logoContainer = new JPanel(new BorderLayout());
        logoContainer.setOpaque(false);
        logoContainer.setBorder(new EmptyBorder(20, 0, 20, 0));
        logoContainer.add(logoLabel, BorderLayout.CENTER);

        return logoContainer;
    }

    /**
     * Creates menu items for admin users.
     */
    private void createAdminMenu() {
        // Create admin menu items
        JButton mainPageButton = createMenuButton("Main Page", true);
        JButton detailPageButton = createMenuButton("Parking Plot Management", false);
        JButton personalInfoButton = createMenuButton("Personal Page", false);

        // Add buttons to menu items panel
        menuItemsPanel.add(createMenuLabel("ADMIN"));
        menuItemsPanel.add(Box.createVerticalStrut(5));
        menuItemsPanel.add(mainPageButton);
        menuItemsPanel.add(Box.createVerticalStrut(5));
        menuItemsPanel.add(detailPageButton);
        menuItemsPanel.add(Box.createVerticalStrut(5));
        menuItemsPanel.add(personalInfoButton);

        // Add buttons to list for tracking
        menuButtons.add(mainPageButton);
        menuButtons.add(detailPageButton);
        menuButtons.add(personalInfoButton);
    }

    /**
     * Creates menu items for regular users.
     */
    private void createUserMenu() {
        // Create user menu items
        JButton mainPageButton = createMenuButton("Main Page", true);
        JButton searchPageButton = createMenuButton("Search Page", false);
        JButton infoPageButton = createMenuButton("Information Page", false);

        // Add buttons to menu items panel
        menuItemsPanel.add(createMenuLabel("MENU"));
        menuItemsPanel.add(Box.createVerticalStrut(5));
        menuItemsPanel.add(mainPageButton);
        menuItemsPanel.add(Box.createVerticalStrut(5));
        menuItemsPanel.add(searchPageButton);
        menuItemsPanel.add(Box.createVerticalStrut(5));
        menuItemsPanel.add(infoPageButton);

        // Add buttons to list for tracking
        menuButtons.add(mainPageButton);
        menuButtons.add(searchPageButton);
        menuButtons.add(infoPageButton);
    }

    /**
     * Creates a menu section label.
     *
     * @param text Text for the label
     * @return JLabel with appropriate styling
     */
    private JLabel createMenuLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(new Color(160, 174, 192));
        label.setBorder(new EmptyBorder(5, 10, 5, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    /**
     * Creates a menu button with the specified text.
     *
     * @param text Text for the button
     * @param isSelected Whether the button should appear selected
     * @return JButton with appropriate styling
     */
    private JButton createMenuButton(String text, boolean isSelected) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(180, 40));
        button.setPreferredSize(new Dimension(180, 40));
        button.setMinimumSize(new Dimension(180, 40));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Set colors based on state
        Color normalBg = new Color(0, 0, 0, 0); // Transparent
        Color selectedBg = isAdmin ?
                new Color(126, 87, 194) : // Purple for admin
                new Color(59, 130, 246);  // Blue for user
        Color hoverBg = isAdmin ?
                new Color(103, 58, 183) : // Purple hover for admin
                new Color(37, 99, 235);   // Blue hover for user

        // Set initial state
        button.setForeground(Color.WHITE);
        button.setBackground(isSelected ? selectedBg : normalBg);
        button.setOpaque(isSelected);

        // Add hover effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!button.isSelected()) {
                    button.setBackground(hoverBg);
                    button.setOpaque(true);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!button.isSelected()) {
                    button.setBackground(normalBg);
                    button.setOpaque(false);
                }
            }
        });

        return button;
    }

    /**
     * Creates the sign out button.
     *
     * @return Sign out button with appropriate styling
     */
    private JButton createSignOutButton() {
        JButton signOutButton = new JButton("Sign Out");
        signOutButton.setMaximumSize(new Dimension(180, 40));
        signOutButton.setPreferredSize(new Dimension(180, 40));
        signOutButton.setMinimumSize(new Dimension(180, 40));
        signOutButton.setHorizontalAlignment(SwingConstants.LEFT);
        signOutButton.setBorderPainted(false);
        signOutButton.setFocusPainted(false);
        signOutButton.setForeground(new Color(255, 82, 82)); // Red text
        signOutButton.setBackground(new Color(0, 0, 0, 0)); // Transparent
        signOutButton.setOpaque(false);

        // Add hover effects
        signOutButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                signOutButton.setBackground(new Color(255, 82, 82, 50)); // Semi-transparent red
                signOutButton.setOpaque(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                signOutButton.setBackground(new Color(0, 0, 0, 0)); // Transparent
                signOutButton.setOpaque(false);
            }
        });

        // Add padding
        signOutButton.setBorder(new EmptyBorder(10, 15, 20, 15));

        return signOutButton;
    }

    /**
     * Sets a menu item as selected.
     *
     * @param index Index of the menu item to select
     */
    public void setSelectedMenuItem(int index) {
        // Make sure index is within bounds
        if (index >= 0 && index < menuButtons.size()) {
            // Reset all buttons
            for (int i = 0; i < menuButtons.size(); i++) {
                JButton button = menuButtons.get(i);

                // Calculate if this button should be selected
                boolean isSelected = i == index;

                // Apply appropriate style
                Color normalBg = new Color(0, 0, 0, 0); // Transparent
                Color selectedBg = isAdmin ?
                        new Color(126, 87, 194) : // Purple for admin
                        new Color(59, 130, 246);  // Blue for user

                button.setBackground(isSelected ? selectedBg : normalBg);
                button.setOpaque(isSelected);
            }
        }
    }
}