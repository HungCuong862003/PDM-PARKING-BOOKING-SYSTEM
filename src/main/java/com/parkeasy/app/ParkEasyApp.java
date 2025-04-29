package main.java.com.parkeasy.app;

import main.java.com.parkeasy.view.auth.LoginView;

import javax.swing.*;
import java.awt.*;

/**
 * Main application class for the ParkEasy system
 * Serves as the entry point for the application
 */
public class ParkEasyApp {
    /**
     * Main method to launch the application
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error setting look and feel: " + e.getMessage());
        }

        // Use SwingUtilities to ensure UI is created on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    /**
     * Creates and displays the main application window
     */
    private static void createAndShowGUI() {
        // Create and configure the initial login view
        LoginView loginView = new LoginView();
        loginView.setVisible(true);
    }
}