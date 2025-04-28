package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.repository.UserRepository;
import main.java.com.parkeasy.repository.AdminRepository;
import main.java.com.parkeasy.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/**
 * Service class for handling authentication in the ParkEasy system.
 * Provides functionality for user and admin login, logout operations,
 * registration, password reset, and session management.
 */
public class AuthService {

    private UserRepository userRepository;
    private AdminRepository adminRepository;
    private User currentUser;
    private Admin currentAdmin;
    private boolean isAdminLoggedIn;
    private String sessionToken;

    // User-related table names and columns
    private static final String USER_TABLE = "USER";
    private static final String USER_ID_COLUMN = "UserID";
    private static final String USER_NAME_COLUMN = "UserName";

    // Admin-related table names and columns
    private static final String ADMIN_TABLE = "ADMIN";
    private static final String ADMIN_ID_COLUMN = "AdminID";
    private static final String ADMIN_NAME_COLUMN = "AdminName";

    // Common columns
    private static final String EMAIL_COLUMN = "Email";
    private static final String PHONE_COLUMN = "Phone";
    private static final String PASSWORD_COLUMN = "Password";
    private static final String BALANCE_COLUMN = "Balance";

    /**
     * Constructor for AuthService.
     */
    public AuthService() throws SQLException {
        this.userRepository = new UserRepository();
        this.adminRepository = new AdminRepository();
        this.currentUser = null;
        this.currentAdmin = null;
        this.isAdminLoggedIn = false;
        this.sessionToken = null;
    }

    /**
     * Updates user profile information.
     *
     * @param userId   User ID
     * @param userName New user name (or null to keep current)
     * @param phone    New phone (or null to keep current)
     * @return true if update was successful, false otherwise
     */
    public boolean updateUserProfile(int userId, String userName, String phone) {
        return updateProfile(USER_TABLE, USER_ID_COLUMN, USER_NAME_COLUMN, userId, userName, phone, false);
    }

    /**
     * Updates admin profile information.
     *
     * @param adminId   Admin ID
     * @param adminName New admin name (or null to keep current)
     * @param phone     New phone (or null to keep current)
     * @return true if update was successful, false otherwise
     */
    public boolean updateAdminProfile(int adminId, String adminName, String phone) {
        return updateProfile(ADMIN_TABLE, ADMIN_ID_COLUMN, ADMIN_NAME_COLUMN, adminId, adminName, phone, true);
    }

    /**
     * Generic method to verify a password.
     *
     * @param tableName    Table name
     * @param idColumnName ID column name
     * @param id           User or Admin ID
     * @param password     Password to check
     * @return true if password matches, false otherwise
     */
    private boolean verifyPassword(String tableName, String idColumnName, int id, String password) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + idColumnName + " = ? AND "
                    + PASSWORD_COLUMN + " = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, id);
                statement.setString(2, password);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error verifying password: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Checks if the given password matches the stored password for a user.
     *
     * @param userId   User ID
     * @param password Password to check
     * @return true if password matches, false otherwise
     */
    public boolean verifyUserPassword(int userId, String password) {
        return verifyPassword(USER_TABLE, USER_ID_COLUMN, userId, password);
    }

    /**
     * Checks if the given password matches the stored password for an admin.
     *
     * @param adminId  Admin ID
     * @param password Password to check
     * @return true if password matches, false otherwise
     */
    public boolean verifyAdminPassword(int adminId, String password) {
        return verifyPassword(ADMIN_TABLE, ADMIN_ID_COLUMN, adminId, password);
    }

    // ========== Private helper methods ==========

    /**
     * Generates a unique session token.
     *
     * @return a unique session token
     */
    private String generateSessionToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generates a unique reset token.
     *
     * @return a unique reset token
     */
    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Records a login activity.
     *
     * @param id      User or Admin ID
     * @param isAdmin true if admin, false if user
     */
    private void recordLoginActivity(int id, boolean isAdmin) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Check if the login_activity table exists and create it if it doesn't
            try {
                String createTableQuery = "CREATE TABLE IF NOT EXISTS login_activity (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "userId INT NOT NULL, " +
                        "isAdmin BOOLEAN NOT NULL, " +
                        "activityType VARCHAR(10) NOT NULL, " +
                        "activityTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ")";

                try (Statement createStatement = connection.createStatement()) {
                    createStatement.execute(createTableQuery);
                }
            } catch (SQLException e) {
                System.err.println("Error creating login_activity table: " + e.getMessage());
                return;
            }

            // Insert the login activity
            String query = "INSERT INTO login_activity (userId, isAdmin, activityType, activityTime) VALUES (?, ?, ?, NOW())";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, id);
                statement.setBoolean(2, isAdmin);
                statement.setString(3, "LOGIN");

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            // Just log the error, don't disrupt the main flow
            System.err.println("Error recording login activity: " + e.getMessage());
        }
    }

    /**
     * Records a logout activity.
     *
     * @param id      User or Admin ID
     * @param isAdmin true if admin, false if user
     */
    private void recordLogoutActivity(int id, boolean isAdmin) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Assuming the login_activity table exists
            String query = "INSERT INTO login_activity (userId, isAdmin, activityType, activityTime) VALUES (?, ?, ?, NOW())";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, id);
                statement.setBoolean(2, isAdmin);
                statement.setString(3, "LOGOUT");

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            // Just log the error, don't disrupt the main flow
            System.err.println("Error recording logout activity: " + e.getMessage());
        }
    }

    /**
     * Stores a reset token.
     *
     * @param id      User or Admin ID
     * @param token   Reset token
     * @param isAdmin true if admin, false if user
     * @throws SQLException if an error occurs during database operation
     */
    private void storeResetToken(int id, String token, boolean isAdmin) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Create the password_resets table if it doesn't exist
            try {
                String createTableQuery = "CREATE TABLE IF NOT EXISTS password_resets (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "userId INT, " +
                        "adminId INT, " +
                        "token VARCHAR(255) NOT NULL, " +
                        "expiryDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP + INTERVAL 1 DAY, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ")";

                try (Statement createStatement = connection.createStatement()) {
                    createStatement.execute(createTableQuery);
                }
            } catch (SQLException e) {
                System.err.println("Error creating password_resets table: " + e.getMessage());
                throw e;
            }

            // Store the token
            String idField = isAdmin ? "adminId" : "userId";
            String query = "INSERT INTO password_resets (" + idField + ", token) VALUES (?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, id);
                statement.setString(2, token);

                statement.executeUpdate();
            }
        }
    }

    /**
     * Attempts to log in a user with the provided email and password.
     *
     * @param email    User's email address
     * @param password User's password
     * @return true if login successful, false otherwise
     */
    public boolean loginUser(String email, String password) {
        User user = authenticateUser(USER_TABLE, email, password);
        if (user != null) {
            // Set current user and session state
            this.currentUser = user;
            this.isAdminLoggedIn = false;

            // Generate a session token
            this.sessionToken = generateSessionToken();

            // Record login activity
            recordLoginActivity(user.getUserID(), false);

            return true;
        }
        return false;
    }

    /**
     * Attempts to log in an admin with the provided email and password.
     *
     * @param email    Admin's email address
     * @param password Admin's password
     * @return true if login successful, false otherwise
     */
    public boolean loginAdmin(String email, String password) {
        Admin admin = authenticateAdmin(ADMIN_TABLE, email, password);
        if (admin != null) {
            // Set current admin and session state
            this.currentAdmin = admin;
            this.isAdminLoggedIn = true;

            // Generate a session token
            this.sessionToken = generateSessionToken();

            // Record login activity
            recordLoginActivity(admin.getAdminID(), true);

            return true;
        }
        return false;
    }

    /**
     * Helper method to authenticate a user.
     */
    private User authenticateUser(String tableName, String email, String password) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM " + tableName + " WHERE " + EMAIL_COLUMN + " = ? AND " + PASSWORD_COLUMN
                    + " = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, email);
                statement.setString(2, password); // No hashing as per requirements

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        // User login successful, create User object
                        User user = new User();
                        user.setUserID(resultSet.getInt(USER_ID_COLUMN));
                        user.setUserName(resultSet.getString(USER_NAME_COLUMN));
                        user.setEmail(resultSet.getString(EMAIL_COLUMN));
                        user.setPhone(resultSet.getString(PHONE_COLUMN));
                        user.setBalance(BigDecimal.valueOf(resultSet.getDouble(BALANCE_COLUMN)));
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during user authentication: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Helper method to authenticate an admin.
     */
    private Admin authenticateAdmin(String tableName, String email, String password) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM " + tableName + " WHERE " + EMAIL_COLUMN + " = ? AND " + PASSWORD_COLUMN
                    + " = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, email);
                statement.setString(2, password); // No hashing as per requirements

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        // Admin login successful, create Admin object
                        Admin admin = new Admin();
                        admin.setAdminID(resultSet.getInt(ADMIN_ID_COLUMN));
                        admin.setAdminName(resultSet.getString(ADMIN_NAME_COLUMN));
                        admin.setEmail(resultSet.getString(EMAIL_COLUMN));
                        admin.setPhone(resultSet.getString(PHONE_COLUMN));
                        return admin;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during admin authentication: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Logs out the current user or admin.
     */
    public void logout() {
        // Record logout activity if a user/admin is logged in
        if (isUserLoggedIn()) {
            recordLogoutActivity(currentUser.getUserID(), false);
        } else if (isAdminLoggedIn()) {
            recordLogoutActivity(currentAdmin.getAdminID(), true);
        }

        this.currentUser = null;
        this.currentAdmin = null;
        this.isAdminLoggedIn = false;
        this.sessionToken = null;
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return true if a user is logged in, false otherwise
     */
    public boolean isUserLoggedIn() {
        return currentUser != null && !isAdminLoggedIn;
    }

    /**
     * Checks if an admin is currently logged in.
     *
     * @return true if an admin is logged in, false otherwise
     */
    public boolean isAdminLoggedIn() {
        return currentAdmin != null && isAdminLoggedIn;
    }

    /**
     * Gets the currently logged in user.
     *
     * @return the current User object, or null if no user is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Gets the currently logged in admin.
     *
     * @return the current Admin object, or null if no admin is logged in
     */
    public Admin getCurrentAdmin() {
        return currentAdmin;
    }

    /**
     * Gets the current session token.
     *
     * @return the current session token, or null if no session exists
     */
    public String getSessionToken() {
        return sessionToken;
    }

    /**
     * Checks if an email already exists in a database table.
     *
     * @param tableName Table to check
     * @param email     Email to check
     * @return true if the email exists, false otherwise
     */
    private boolean isEmailExists(String tableName, String email) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + EMAIL_COLUMN + " = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, email);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Checks if a phone number already exists in a database table.
     *
     * @param tableName Table to check
     * @param phone     Phone number to check
     * @return true if the phone number exists, false otherwise
     */
    private boolean isPhoneExists(String tableName, String phone) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + PHONE_COLUMN + " = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, phone);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking phone existence: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Checks if an email already exists in the user database.
     *
     * @param email Email to check
     * @return true if the email exists, false otherwise
     */
    public boolean isUserEmailExists(String email) {
        return isEmailExists(USER_TABLE, email);
    }

    /**
     * Checks if a phone number already exists in the user database.
     *
     * @param phone Phone number to check
     * @return true if the phone number exists, false otherwise
     */
    public boolean isUserPhoneExists(String phone) {
        return isPhoneExists(USER_TABLE, phone);
    }

    /**
     * Checks if an email already exists in the admin database.
     *
     * @param email Email to check
     * @return true if the email exists, false otherwise
     */
    public boolean isAdminEmailExists(String email) {
        return isEmailExists(ADMIN_TABLE, email);
    }

    /**
     * Checks if a phone number already exists in the admin database.
     *
     * @param phone Phone number to check
     * @return true if the phone number exists, false otherwise
     */
    public boolean isAdminPhoneExists(String phone) {
        return isPhoneExists(ADMIN_TABLE, phone);
    }

    /**
     * Registers a new user with the provided details.
     *
     * @param userName User's name
     * @param email    User's email address
     * @param phone    User's phone number
     * @param password User's password
     * @return true if registration successful, false otherwise
     */
    public boolean registerUser(String userName, String email, String phone, String password) {
        // Check if email or phone already exists
        if (isUserEmailExists(email)) {
            System.err.println("Registration failed: Email already exists.");
            return false;
        }

        if (isUserPhoneExists(phone)) {
            System.err.println("Registration failed: Phone number already exists.");
            return false;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO " + USER_TABLE + " (" + USER_NAME_COLUMN + ", " +
                    EMAIL_COLUMN + ", " + PHONE_COLUMN + ", " + PASSWORD_COLUMN + ", " +
                    BALANCE_COLUMN + ") VALUES (?, ?, ?, ?, 0.0)";

            try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, userName);
                statement.setString(2, email);
                statement.setString(3, phone);
                statement.setString(4, password); // No hashing as per requirements

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    // Get the newly generated user ID
                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int userId = generatedKeys.getInt(1);
                            System.out.println("User registered successfully with ID: " + userId);
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during user registration: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Registers a new admin with the provided details.
     *
     * @param adminName Admin's name
     * @param email     Admin's email address
     * @param phone     Admin's phone number
     * @param password  Admin's password
     * @return true if registration successful, false otherwise
     */
    public boolean registerAdmin(String adminName, String email, String phone, String password) {
        // Check if email or phone already exists
        if (isAdminEmailExists(email)) {
            System.err.println("Registration failed: Email already exists.");
            return false;
        }

        if (isAdminPhoneExists(phone)) {
            System.err.println("Registration failed: Phone number already exists.");
            return false;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO " + ADMIN_TABLE + " (" + ADMIN_NAME_COLUMN + ", " +
                    EMAIL_COLUMN + ", " + PHONE_COLUMN + ", " + PASSWORD_COLUMN + ") VALUES (?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, adminName);
                statement.setString(2, email);
                statement.setString(3, phone);
                statement.setString(4, password); // No hashing as per requirements

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    // Get the newly generated admin ID
                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int adminId = generatedKeys.getInt(1);
                            System.out.println("Admin registered successfully with ID: " + adminId);
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during admin registration: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Updates a password in the specified table.
     *
     * @param tableName    Table name (USER or ADMIN)
     * @param idColumnName Column name for ID
     * @param id           User or Admin ID
     * @param oldPassword  Current password
     * @param newPassword  New password
     * @return true if password was updated successfully, false otherwise
     */
    private boolean updatePassword(String tableName, String idColumnName, int id, String oldPassword,
            String newPassword) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // First verify the old password
            String verifyQuery = "SELECT COUNT(*) FROM " + tableName + " WHERE " + idColumnName + " = ? AND "
                    + PASSWORD_COLUMN + " = ?";

            try (PreparedStatement verifyStatement = connection.prepareStatement(verifyQuery)) {
                verifyStatement.setInt(1, id);
                verifyStatement.setString(2, oldPassword);

                try (ResultSet resultSet = verifyStatement.executeQuery()) {
                    if (resultSet.next() && resultSet.getInt(1) > 0) {
                        // Old password verified, update to new password
                        String updateQuery = "UPDATE " + tableName + " SET " + PASSWORD_COLUMN + " = ? WHERE "
                                + idColumnName + " = ?";

                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setString(1, newPassword);
                            updateStatement.setInt(2, id);

                            int rowsAffected = updateStatement.executeUpdate();
                            return rowsAffected > 0;
                        }
                    } else {
                        System.err.println("Password update failed: Old password does not match.");
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Updates a user's password.
     *
     * @param userId      User ID
     * @param oldPassword Current password
     * @param newPassword New password
     * @return true if password was updated successfully, false otherwise
     */
    public boolean updateUserPassword(int userId, String oldPassword, String newPassword) {
        return updatePassword(USER_TABLE, USER_ID_COLUMN, userId, oldPassword, newPassword);
    }

    /**
     * Updates an admin's password.
     *
     * @param adminId     Admin ID
     * @param oldPassword Current password
     * @param newPassword New password
     * @return true if password was updated successfully, false otherwise
     */
    public boolean updateAdminPassword(int adminId, String oldPassword, String newPassword) {
        return updatePassword(ADMIN_TABLE, ADMIN_ID_COLUMN, adminId, oldPassword, newPassword);
    }

    /**
     * Generic method to request a password reset.
     *
     * @param tableName    Table name (USER or ADMIN)
     * @param idColumnName Column name for ID
     * @param email        Email address
     * @param isAdmin      true if admin, false if user
     * @return reset token if successful, empty string otherwise
     */
    private String requestPasswordReset(String tableName, String idColumnName, String email, boolean isAdmin) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Check if the entity exists
            String query = "SELECT " + idColumnName + " FROM " + tableName + " WHERE " + EMAIL_COLUMN + " = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, email);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int id = resultSet.getInt(idColumnName);

                        // Generate a reset token
                        String resetToken = generateResetToken();

                        // Store the reset token in the database
                        try {
                            storeResetToken(id, resetToken, isAdmin);

                            // In a real application, send an email with the reset link
                            System.out.println("Password reset initiated for: " + email);
                            System.out.println("Reset token: " + resetToken);

                            return resetToken;
                        } catch (SQLException e) {
                            System.err.println("Error storing reset token: " + e.getMessage());
                        }
                    } else {
                        System.err.println("Password reset failed: Not found with email: " + email);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error requesting password reset: " + e.getMessage());
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Initiates a password reset for a user by email.
     * In a real application, this would typically send an email with a reset link.
     * This implementation returns a reset token.
     *
     * @param email User's email address
     * @return reset token if successful, empty string otherwise
     */
    public String requestUserPasswordReset(String email) {
        return requestPasswordReset(USER_TABLE, USER_ID_COLUMN, email, false);
    }

    /**
     * Initiates a password reset for an admin by email.
     * In a real application, this would typically send an email with a reset link.
     * This implementation returns a reset token.
     *
     * @param email Admin's email address
     * @return reset token if successful, empty string otherwise
     */
    public String requestAdminPasswordReset(String email) {
        return requestPasswordReset(ADMIN_TABLE, ADMIN_ID_COLUMN, email, true);
    }

    /**
     * Generic method to reset a password using a token.
     *
     * @param token       Reset token
     * @param newPassword New password
     * @param isAdmin     true for admin, false for user
     * @return true if password was reset successfully, false otherwise
     */
    private boolean resetPassword(String token, String newPassword, boolean isAdmin) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Determine which field to query
            String idField = isAdmin ? "adminId" : "userId";
            String tableName = isAdmin ? ADMIN_TABLE : USER_TABLE;
            String idColumnName = isAdmin ? ADMIN_ID_COLUMN : USER_ID_COLUMN;

            // Verify the token and get the ID
            String verifyQuery = "SELECT " + idField + " FROM password_resets WHERE token = ? AND expiryDate > NOW()";

            try (PreparedStatement verifyStatement = connection.prepareStatement(verifyQuery)) {
                verifyStatement.setString(1, token);

                try (ResultSet resultSet = verifyStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int id = resultSet.getInt(idField);

                        // Update the password
                        String updateQuery = "UPDATE " + tableName + " SET " + PASSWORD_COLUMN + " = ? WHERE "
                                + idColumnName + " = ?";

                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setString(1, newPassword);
                            updateStatement.setInt(2, id);

                            int rowsAffected = updateStatement.executeUpdate();

                            if (rowsAffected > 0) {
                                // Delete the used token
                                String deleteQuery = "DELETE FROM password_resets WHERE token = ?";

                                try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                                    deleteStatement.setString(1, token);
                                    deleteStatement.executeUpdate();
                                }

                                return true;
                            }
                        }
                    } else {
                        System.err.println("Password reset failed: Invalid or expired token.");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error resetting password: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Resets a user's password using a reset token.
     *
     * @param token       Reset token
     * @param newPassword New password
     * @return true if password was reset successfully, false otherwise
     */
    public boolean resetUserPassword(String token, String newPassword) {
        return resetPassword(token, newPassword, false);
    }

    /**
     * Resets an admin's password using a reset token.
     *
     * @param token       Reset token
     * @param newPassword New password
     * @return true if password was reset successfully, false otherwise
     */
    public boolean resetAdminPassword(String token, String newPassword) {
        return resetPassword(token, newPassword, true);
    }

    /**
     * Generic method to validate credentials.
     *
     * @param tableName Table name (USER or ADMIN)
     * @param email     Email
     * @param password  Password
     * @return true if credentials are valid, false otherwise
     */
    private boolean validateCredentials(String tableName, String email, String password) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + EMAIL_COLUMN + " = ? AND "
                    + PASSWORD_COLUMN + " = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, email);
                statement.setString(2, password);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error validating credentials: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Validates a user's credentials.
     *
     * @param email    User's email
     * @param password User's password
     * @return true if credentials are valid, false otherwise
     */
    public boolean validateUserCredentials(String email, String password) {
        return validateCredentials(USER_TABLE, email, password);
    }

    /**
     * Validates an admin's credentials.
     *
     * @param email    Admin's email
     * @param password Admin's password
     * @return true if credentials are valid, false otherwise
     */
    public boolean validateAdminCredentials(String email, String password) {
        return validateCredentials(ADMIN_TABLE, email, password);
    }

    /**
     * Gets user information by email.
     *
     * @param email User's email
     * @return User object if found, null otherwise
     */
    public User getUserByEmail(String email) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM " + USER_TABLE + " WHERE " + EMAIL_COLUMN + " = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, email);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        User user = new User();
                        user.setUserID(resultSet.getInt(USER_ID_COLUMN));
                        user.setUserName(resultSet.getString(USER_NAME_COLUMN));
                        user.setEmail(resultSet.getString(EMAIL_COLUMN));
                        user.setPhone(resultSet.getString(PHONE_COLUMN));
                        user.setBalance(BigDecimal.valueOf(resultSet.getDouble(BALANCE_COLUMN)));

                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by email: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets admin information by email.
     *
     * @param email Admin's email
     * @return Admin object if found, null otherwise
     */
    public Admin getAdminByEmail(String email) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM " + ADMIN_TABLE + " WHERE " + EMAIL_COLUMN + " = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, email);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        Admin admin = new Admin();
                        admin.setAdminID(resultSet.getInt(ADMIN_ID_COLUMN));
                        admin.setAdminName(resultSet.getString(ADMIN_NAME_COLUMN));
                        admin.setEmail(resultSet.getString(EMAIL_COLUMN));
                        admin.setPhone(resultSet.getString(PHONE_COLUMN));

                        return admin;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting admin by email: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets user information by ID.
     *
     * @param userId User's ID
     * @return User object if found, null otherwise
     */
    public User getUserById(int userId) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM " + USER_TABLE + " WHERE " + USER_ID_COLUMN + " = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        User user = new User();
                        user.setUserID(resultSet.getInt(USER_ID_COLUMN));
                        user.setUserName(resultSet.getString(USER_NAME_COLUMN));
                        user.setEmail(resultSet.getString(EMAIL_COLUMN));
                        user.setPhone(resultSet.getString(PHONE_COLUMN));
                        user.setBalance(BigDecimal.valueOf(resultSet.getDouble(BALANCE_COLUMN)));

                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets admin information by ID.
     *
     * @param adminId Admin's ID
     * @return Admin object if found, null otherwise
     */
    public Admin getAdminById(int adminId) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM " + ADMIN_TABLE + " WHERE " + ADMIN_ID_COLUMN + " = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, adminId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        Admin admin = new Admin();
                        admin.setAdminID(resultSet.getInt(ADMIN_ID_COLUMN));
                        admin.setAdminName(resultSet.getString(ADMIN_NAME_COLUMN));
                        admin.setEmail(resultSet.getString(EMAIL_COLUMN));
                        admin.setPhone(resultSet.getString(PHONE_COLUMN));

                        return admin;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting admin by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Generic method to update a profile.
     *
     * @param tableName      Table name (USER or ADMIN)
     * @param idColumnName   ID column name
     * @param nameColumnName Name column name
     * @param id             Entity ID
     * @param name           New name (or null to keep current)
     * @param phone          New phone (or null to keep current)
     * @param isPhoneExists  Function to check if phone exists
     * @return true if update was successful, false otherwise
     */
    /**
     * Updates a profile with the provided details.
     *
     * @param tableName      Table name to update
     * @param idColumnName   ID column name
     * @param nameColumnName Name column name
     * @param id             Entity ID
     * @param name           New name (or null to keep current)
     * @param phone          New phone (or null to keep current)
     * @param isAdmin        Whether this is an admin profile
     * @return true if update was successful, false otherwise
     */
    private boolean updateProfile(String tableName, String idColumnName, String nameColumnName, int id,
            String name, String phone, boolean isAdmin) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Build the update query based on which fields are being updated
            StringBuilder queryBuilder = new StringBuilder("UPDATE " + tableName + " SET ");
            boolean needsComma = false;

            if (name != null) {
                queryBuilder.append(nameColumnName + " = ?");
                needsComma = true;
            }

            if (phone != null) {
                if (needsComma) {
                    queryBuilder.append(", ");
                }
                queryBuilder.append(PHONE_COLUMN + " = ?");
            }

            queryBuilder.append(" WHERE " + idColumnName + " = ?");
            String query = queryBuilder.toString();

            // Check if phone exists (if updating phone)
            if (phone != null) {
                // Get current phone
                String currentPhone = "";
                if (isAdmin) {
                    Admin currentAdmin = getAdminById(id);
                    if (currentAdmin != null) {
                        currentPhone = currentAdmin.getPhone();
                    }
                } else {
                    User currentUser = getUserById(id);
                    if (currentUser != null) {
                        currentPhone = currentUser.getPhone();
                    }
                }

                // If phone is changing and already exists for another user/admin
                if (!phone.equals(currentPhone)) {
                    boolean phoneExists = isAdmin ? isAdminPhoneExists(phone) : isUserPhoneExists(phone);
                    if (phoneExists) {
                        System.err.println("Profile update failed: Phone number already in use.");
                        return false;
                    }
                }
            }

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                int paramIndex = 1;

                if (name != null) {
                    statement.setString(paramIndex++, name);
                }

                if (phone != null) {
                    statement.setString(paramIndex++, phone);
                }

                statement.setInt(paramIndex, id);

                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating profile: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}