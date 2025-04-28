package main.java.com.parkeasy.view.common.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Font;

/**
 * Header view component for both user and admin interfaces.
 * Displays the application title, user/admin name, and any relevant actions.
 */
public class HeaderView extends JPanel {

    private final JLabel titleLabel;
    private final JLabel userLabel;

    /**
     * Creates a header view with the specified title and user name.
     *
     * @param title The title to display
     * @param userName The name of the user or admin
     * @param isAdmin Whether the current user is an admin
     */
    public HeaderView(String title, String userName, boolean isAdmin) {
        // Set layout
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 15, 10, 15));

        // Create title label
        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        // Create user label
        userLabel = new JLabel(isAdmin ? "Admin: " + userName : userName);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);

        // Create main panel with flow layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);

        // Add title to left
        mainPanel.add(titleLabel, BorderLayout.WEST);

        // Add user name to right
        mainPanel.add(userLabel, BorderLayout.EAST);

        // Add main panel to this panel
        add(mainPanel, BorderLayout.CENTER);

        // Set background color based on user type
        setBackground(isAdmin ?
                new Color(103, 80, 164) : // Purple for admin
                new Color(45, 55, 72));    // Dark blue for user
    }

    /**
     * Updates the title displayed in the header.
     *
     * @param title The new title to display
     */
    public void updateTitle(String title) {
        titleLabel.setText(title);
    }

    /**
     * Updates the user name displayed in the header.
     *
     * @param userName The new user name to display
     * @param isAdmin Whether the current user is an admin
     */
    public void updateUserName(String userName, boolean isAdmin) {
        userLabel.setText(isAdmin ? "Admin: " + userName : userName);
    }
}