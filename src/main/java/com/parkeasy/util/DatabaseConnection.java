package main.java.com.parkeasy.util;

import java.sql.*;

/**
 * Utility class for database connections.
 * Designed to be used with try-with-resources for automatic resource management.
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/parking_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password";

    static {
        try {
            // Load the JDBC driver once when the class is loaded
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load MySQL JDBC driver", e);
        }
    }

    /**
     * Creates a new database connection.
     * This method should be used in a try-with-resources block to ensure
     * the connection is properly closed after use.
     *
     * @return A new Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}