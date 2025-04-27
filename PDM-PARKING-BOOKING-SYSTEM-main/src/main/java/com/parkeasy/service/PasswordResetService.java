package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service class that handles password reset functionality for users and admins
 * in the ParkEasy system. Password can be reset by providing the correct
 * email and phone number combination.
 */
public class PasswordResetService {

    private final AuthService authService;

    // SQL table and column names as constants
    private static final String PASSWORD_RESETS_TABLE = "password_resets";
    private static final String ID_COLUMN = "id";
    private static final String USER_ID_COLUMN = "userId";
    private static final String ADMIN_ID_COLUMN = "adminId";
    private static final String TOKEN_COLUMN = "token";
    private static final String EXPIRY_DATE_COLUMN = "expiryDate";
    private static final String CREATED_AT_COLUMN = "created_at";

    private static final String USER_TABLE = "USER";
    private static final String USER_ID = "UserID";
    private static final String USER_NAME = "UserName";
    private static final String EMAIL = "Email";
    private static final String PHONE = "Phone";
    private static final String PASSWORD = "Password";

    private static final String ADMIN_TABLE = "ADMIN";
    private static final String ADMIN_ID = "AdminID";
    private static final String ADMIN_NAME = "AdminName";

    // Token types
    private static final String USER_TYPE = "USER";
    private static final String ADMIN_TYPE = "ADMIN";

    /**
     * Constructor for PasswordResetService.
     *
     * @throws SQLException if database connection fails
     */
    public PasswordResetService() throws SQLException {
        this.authService = new AuthService();
    }

    /**
     * Initiates the password reset process for a user by verifying email and phone.
     *
     * @param email User's email address
     * @param phone User's phone number
     * @return reset token if verification successful, empty string otherwise
     */
    public String initiateUserPasswordReset(String email, String phone) {
        if (email == null || email.trim().isEmpty() || phone == null || phone.trim().isEmpty()) {
            System.err.println("Invalid email or phone provided");
            return "";
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Check if the user exists with the provided email and phone
            String query = "SELECT " + USER_ID + " FROM " + USER_TABLE +
                    " WHERE " + EMAIL + " = ? AND " + PHONE + " = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, email);
                statement.setString(2, phone);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int userId = resultSet.getInt(USER_ID);
                        return createResetToken(userId, true);
                    } else {
                        System.err.println("Password reset failed: Email or phone does not match any user.");
                        return "";
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error initiating user password reset", e);
            return "";
        }
    }

    /**
     * Initiates the password reset process for an admin by verifying email and phone.
     *
     * @param email Admin's email address
     * @param phone Admin's phone number
     * @return reset token if verification successful, empty string otherwise
     */
    public String initiateAdminPasswordReset(String email, String phone) {
        if (email == null || email.trim().isEmpty() || phone == null || phone.trim().isEmpty()) {
            System.err.println("Invalid email or phone provided");
            return "";
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Check if the admin exists with the provided email and phone
            String query = "SELECT " + ADMIN_ID + " FROM " + ADMIN_TABLE +
                    " WHERE " + EMAIL + " = ? AND " + PHONE + " = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, email);
                statement.setString(2, phone);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int adminId = resultSet.getInt(ADMIN_ID);
                        return createResetToken(adminId, false);
                    } else {
                        System.err.println("Password reset failed: Email or phone does not match any admin.");
                        return "";
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error initiating admin password reset", e);
            return "";
        }
    }

    /**
     * Creates a reset token for a user or admin.
     *
     * @param id The user or admin ID
     * @param isUser True if this is for a user, false if for an admin
     * @return The generated token or empty string if failed
     */
    private String createResetToken(int id, boolean isUser) {
        try {
            // Generate a reset token
            String resetToken = generateResetToken();

            // Store the reset token in the database
            if (isUser) {
                storeUserResetToken(id, resetToken);
                // Log the reset request
                System.out.println("Password reset initiated for user ID: " + id);
            } else {
                storeAdminResetToken(id, resetToken);
                // Log the reset request
                System.out.println("Password reset initiated for admin ID: " + id);
            }
            System.out.println("Reset token: " + resetToken);

            return resetToken;
        } catch (SQLException e) {
            handleSQLException("Error creating reset token", e);
            return "";
        }
    }

    /**
     * Validates a reset token to ensure it's valid and not expired.
     *
     * @param token Reset token to validate
     * @return true if token is valid, false otherwise
     */
    public boolean isValidResetToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT " + ID_COLUMN + " FROM " + PASSWORD_RESETS_TABLE +
                    " WHERE " + TOKEN_COLUMN + " = ? AND " + EXPIRY_DATE_COLUMN + " > NOW()";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, token);

                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next(); // If we got a row, token is valid and not expired
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error validating reset token", e);
            return false;
        }
    }

    /**
     * Completes the password reset process for a user by providing the reset token
     * and new password.
     *
     * @param token Reset token
     * @param newPassword New password
     * @return true if password was reset successfully, false otherwise
     */
    public boolean completeUserPasswordReset(String token, String newPassword) {
        if (token == null || token.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
            System.err.println("Invalid token or password provided");
            return false;
        }

        // First check if the token is valid
        if (!isValidResetToken(token)) {
            System.err.println("Password reset failed: Invalid or expired token.");
            return false;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Get the user ID associated with this token
            int userId = getEntityIdFromToken(connection, token, true);
            if (userId <= 0) {
                System.err.println("Password reset failed: Token not associated with any user.");
                return false;
            }

            // Update the user's password
            if (updatePassword(connection, userId, newPassword, true)) {
                // Password updated successfully, now delete the used token
                deleteResetToken(token);

                // Log the successful reset
                System.out.println("Password reset completed successfully for user ID: " + userId);
                return true;
            } else {
                System.err.println("Password reset failed: Could not update password for user ID: " + userId);
                return false;
            }
        } catch (SQLException e) {
            handleSQLException("Error completing user password reset", e);
            return false;
        }
    }

    /**
     * Completes the password reset process for an admin by providing the reset token
     * and new password.
     *
     * @param token Reset token
     * @param newPassword New password
     * @return true if password was reset successfully, false otherwise
     */
    public boolean completeAdminPasswordReset(String token, String newPassword) {
        if (token == null || token.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
            System.err.println("Invalid token or password provided");
            return false;
        }

        // First check if the token is valid
        if (!isValidResetToken(token)) {
            System.err.println("Password reset failed: Invalid or expired token.");
            return false;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Get the admin ID associated with this token
            int adminId = getEntityIdFromToken(connection, token, false);
            if (adminId <= 0) {
                System.err.println("Password reset failed: Token not associated with any admin.");
                return false;
            }

            // Update the admin's password
            if (updatePassword(connection, adminId, newPassword, false)) {
                // Password updated successfully, now delete the used token
                deleteResetToken(token);

                // Log the successful reset
                System.out.println("Password reset completed successfully for admin ID: " + adminId);
                return true;
            } else {
                System.err.println("Password reset failed: Could not update password for admin ID: " + adminId);
                return false;
            }
        } catch (SQLException e) {
            handleSQLException("Error completing admin password reset", e);
            return false;
        }
    }

    /**
     * Gets an entity ID (user or admin) from a reset token.
     *
     * @param connection Database connection
     * @param token Reset token
     * @param isUser True if looking for a user ID, false if for an admin ID
     * @return The entity ID or 0 if not found
     * @throws SQLException if a database error occurs
     */
    private int getEntityIdFromToken(Connection connection, String token, boolean isUser) throws SQLException {
        String idColumn = isUser ? USER_ID_COLUMN : ADMIN_ID_COLUMN;
        String query = "SELECT " + idColumn + " FROM " + PASSWORD_RESETS_TABLE +
                " WHERE " + TOKEN_COLUMN + " = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, token);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(idColumn);
                }
            }
        }
        return 0;
    }

    /**
     * Updates a password for a user or admin.
     *
     * @param connection Database connection
     * @param id Entity ID (user or admin)
     * @param newPassword New password
     * @param isUser True if updating a user, false if updating an admin
     * @return True if password was updated successfully
     * @throws SQLException if a database error occurs
     */
    private boolean updatePassword(Connection connection, int id, String newPassword, boolean isUser) throws SQLException {
        String table = isUser ? USER_TABLE : ADMIN_TABLE;
        String idColumn = isUser ? USER_ID : ADMIN_ID;

        String updateQuery = "UPDATE " + table + " SET " + PASSWORD + " = ? WHERE " + idColumn + " = ?";

        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, newPassword);
            updateStatement.setInt(2, id);

            int rowsAffected = updateStatement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Determines if the reset token is for a user or admin.
     *
     * @param token Reset token
     * @return "USER" if token is for a user, "ADMIN" if token is for an admin, empty string if invalid
     */
    public String getTokenType(String token) {
        if (token == null || token.trim().isEmpty()) {
            return "";
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT " + USER_ID_COLUMN + ", " + ADMIN_ID_COLUMN +
                    " FROM " + PASSWORD_RESETS_TABLE +
                    " WHERE " + TOKEN_COLUMN + " = ? AND " + EXPIRY_DATE_COLUMN + " > NOW()";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, token);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int userId = resultSet.getInt(USER_ID_COLUMN);
                        int adminId = resultSet.getInt(ADMIN_ID_COLUMN);

                        if (userId > 0) {
                            return USER_TYPE;
                        } else if (adminId > 0) {
                            return ADMIN_TYPE;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error determining token type", e);
        }

        return "";
    }

    /**
     * Gets user information associated with a reset token.
     *
     * @param token Reset token
     * @return User object if token is valid and for a user, null otherwise
     */
    public User getUserFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT u.* FROM " + USER_TABLE + " u " +
                    "JOIN " + PASSWORD_RESETS_TABLE + " pr ON u." + USER_ID + " = pr." + USER_ID_COLUMN + " " +
                    "WHERE pr." + TOKEN_COLUMN + " = ? AND pr." + EXPIRY_DATE_COLUMN + " > NOW()";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, token);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return createUserFromResultSet(resultSet);
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error getting user from token", e);
        }

        return null;
    }

    /**
     * Creates a User object from a ResultSet.
     *
     * @param resultSet ResultSet containing user data
     * @return User object
     * @throws SQLException if a database error occurs
     */
    private User createUserFromResultSet(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setUserID(resultSet.getInt(USER_ID));
        user.setUserName(resultSet.getString(USER_NAME));
        user.setEmail(resultSet.getString(EMAIL));
        user.setPhone(resultSet.getString(PHONE));
        // Don't include password for security reasons
        return user;
    }

    /**
     * Gets admin information associated with a reset token.
     *
     * @param token Reset token
     * @return Admin object if token is valid and for an admin, null otherwise
     */
    public Admin getAdminFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT a.* FROM " + ADMIN_TABLE + " a " +
                    "JOIN " + PASSWORD_RESETS_TABLE + " pr ON a." + ADMIN_ID + " = pr." + ADMIN_ID_COLUMN + " " +
                    "WHERE pr." + TOKEN_COLUMN + " = ? AND pr." + EXPIRY_DATE_COLUMN + " > NOW()";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, token);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return createAdminFromResultSet(resultSet);
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error getting admin from token", e);
        }

        return null;
    }

    /**
     * Creates an Admin object from a ResultSet.
     *
     * @param resultSet ResultSet containing admin data
     * @return Admin object
     * @throws SQLException if a database error occurs
     */
    private Admin createAdminFromResultSet(ResultSet resultSet) throws SQLException {
        Admin admin = new Admin();
        admin.setAdminID(resultSet.getInt(ADMIN_ID));
        admin.setAdminName(resultSet.getString(ADMIN_NAME));
        admin.setEmail(resultSet.getString(EMAIL));
        admin.setPhone(resultSet.getString(PHONE));
        // Don't include password for security reasons
        return admin;
    }

    // ========== Private helper methods ==========

    /**
     * Generates a unique reset token.
     *
     * @return a unique reset token
     */
    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Stores a reset token for a user.
     *
     * @param userId User ID
     * @param token Reset token
     * @throws SQLException if an error occurs during database operation
     */
    private void storeUserResetToken(int userId, String token) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Create the password_resets table if it doesn't exist
            ensureResetTableExists(connection);

            // Store the token
            String query = "INSERT INTO " + PASSWORD_RESETS_TABLE + " (" +
                    USER_ID_COLUMN + ", " + ADMIN_ID_COLUMN + ", " +
                    TOKEN_COLUMN + ", " + EXPIRY_DATE_COLUMN + ", " +
                    CREATED_AT_COLUMN + ") " +
                    "VALUES (?, NULL, ?, DATE_ADD(NOW(), INTERVAL 1 DAY), NOW())";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                statement.setString(2, token);

                statement.executeUpdate();
            }
        }
    }

    /**
     * Stores a reset token for an admin.
     *
     * @param adminId Admin ID
     * @param token Reset token
     * @throws SQLException if an error occurs during database operation
     */
    private void storeAdminResetToken(int adminId, String token) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Create the password_resets table if it doesn't exist
            ensureResetTableExists(connection);

            // Store the token
            String query = "INSERT INTO " + PASSWORD_RESETS_TABLE + " (" +
                    USER_ID_COLUMN + ", " + ADMIN_ID_COLUMN + ", " +
                    TOKEN_COLUMN + ", " + EXPIRY_DATE_COLUMN + ", " +
                    CREATED_AT_COLUMN + ") " +
                    "VALUES (NULL, ?, ?, DATE_ADD(NOW(), INTERVAL 1 DAY), NOW())";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, adminId);
                statement.setString(2, token);

                statement.executeUpdate();
            }
        }
    }

    /**
     * Ensures that the password_resets table exists in the database.
     *
     * @param connection Database connection
     * @throws SQLException if an error occurs during database operation
     */
    private void ensureResetTableExists(Connection connection) throws SQLException {
        String createTableQuery =
                "CREATE TABLE IF NOT EXISTS " + PASSWORD_RESETS_TABLE + " (" +
                        ID_COLUMN + " INT AUTO_INCREMENT PRIMARY KEY, " +
                        USER_ID_COLUMN + " INT, " +
                        ADMIN_ID_COLUMN + " INT, " +
                        TOKEN_COLUMN + " VARCHAR(255) NOT NULL, " +
                        EXPIRY_DATE_COLUMN + " TIMESTAMP NOT NULL, " +
                        CREATED_AT_COLUMN + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "INDEX(" + TOKEN_COLUMN + "), " +
                        "INDEX(" + USER_ID_COLUMN + "), " +
                        "INDEX(" + ADMIN_ID_COLUMN + ")" +
                        ")";

        try (Statement createStatement = connection.createStatement()) {
            createStatement.execute(createTableQuery);
        }
    }

    /**
     * Deletes a reset token from the database after it has been used.
     *
     * @param token Reset token to delete
     */
    private void deleteResetToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM " + PASSWORD_RESETS_TABLE + " WHERE " + TOKEN_COLUMN + " = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, token);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            handleSQLException("Error deleting reset token", e);
        }
    }

    /**
     * Cleans up expired tokens from the database.
     * This method can be called periodically to remove expired tokens.
     *
     * @return the number of expired tokens removed
     */
    public int cleanupExpiredTokens() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM " + PASSWORD_RESETS_TABLE + " WHERE " + EXPIRY_DATE_COLUMN + " <= NOW()";

            try (Statement statement = connection.createStatement()) {
                return statement.executeUpdate(query);
            }
        } catch (SQLException e) {
            handleSQLException("Error cleaning up expired tokens", e);
            return 0;
        }
    }

    /**
     * Handles SQL exceptions in a consistent way.
     *
     * @param message Error message prefix
     * @param e SQLException that was thrown
     */
    private void handleSQLException(String message, SQLException e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
    }
}