package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repository class for User entity
 * Handles database operations related to users
 */
public class UserRepository {
    private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());

    /**
     * Creates a new user in the database
     *
     * @param user User object with data to save
     * @return true if successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean createUser(User user) throws SQLException {
        String sql = "INSERT INTO USER (UserName, Phone, Email, Password, Balance) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPhone());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());
            pstmt.setDouble(5, user.getBalance());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Updates an existing user in the database
     *
     * @param user User object with updated data
     * @return true if successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE USER SET UserName = ?, Phone = ?, Email = ?, Password = ?, Balance = ? WHERE UserID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPhone());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());
            pstmt.setDouble(5, user.getBalance());
            pstmt.setInt(6, user.getUserID());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Updates user's balance
     *
     * @param userId User ID
     * @param amount Amount to add to balance (use negative for deduction)
     * @return true if successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean updateBalance(int userId, double amount) throws SQLException {
        String sql = "UPDATE USER SET Balance = Balance + ? WHERE UserID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, amount);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Deletes a user from the database
     *
     * @param userId ID of the user to delete
     * @return true if successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM USER WHERE UserID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Gets a user by ID
     *
     * @param userId ID of the user to get
     * @return User object if found, null otherwise
     * @throws SQLException if a database error occurs
     */
    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM USER WHERE UserID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }

        return null;
    }

    /**
     * Gets a user by email
     *
     * @param email Email of the user to get
     * @return User object if found, null otherwise
     */
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM USER WHERE Email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting user by email", e);
        }

        return null;
    }

    /**
     * Gets a user by phone number
     *
     * @param phone Phone number of the user to get
     * @return User object if found, null otherwise
     * @throws SQLException if a database error occurs
     */
    public User getUserByPhone(String phone) throws SQLException {
        String sql = "SELECT * FROM USER WHERE Phone = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, phone);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }

        return null;
    }

    /**
     * Checks if an email already exists in the database
     *
     * @param email Email to check
     * @return true if exists, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean isEmailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM USER WHERE Email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    /**
     * Checks if a phone number already exists in the database
     *
     * @param phone Phone number to check
     * @return true if exists, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean isPhoneExists(String phone) throws SQLException {
        String sql = "SELECT COUNT(*) FROM USER WHERE Phone = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, phone);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    /**
     * Searches users by name or email
     *
     * @param searchTerm Term to search for
     * @return List of matching users
     * @throws SQLException if a database error occurs
     */
    public List<User> searchUsers(String searchTerm) throws SQLException {
        String sql = "SELECT * FROM USER WHERE UserName LIKE ? OR Email LIKE ?";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }

        return users;
    }

    /**
     * Gets all users
     *
     * @return List of all users
     * @throws SQLException if a database error occurs
     */
    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM USER";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }

        return users;
    }

    /**
     * Find a user by email
     *
     * @param email User email
     * @return Optional containing User if found, empty otherwise
     */
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM USER WHERE Email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by email", e);
        }

        return Optional.empty();
    }

    /**
     * Save a user (creates if new, updates if exists)
     *
     * @param user User to save
     * @return true if successful, false otherwise
     */
    public boolean save(User user) {
        try {
            if (user.getUserID() > 0) {
                // Update existing user
                return updateUser(user);
            } else {
                // Create new user
                return createUser(user);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving user", e);
            return false;
        }
    }

    /**
     * Find a user who owns a specific vehicle
     *
     * @param vehicleId Vehicle ID
     * @return User who owns the vehicle, or null if not found
     */
    public User findUserByVehicleId(String vehicleId) {
        String sql = "SELECT u.* FROM USER u " +
                "JOIN vehicle v ON u.UserID = v.UserID " +
                "WHERE v.VehicleID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, vehicleId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by vehicle ID", e);
        }

        return null;
    }

    /**
     * Maps a ResultSet to a User object
     *
     * @param rs ResultSet to map
     * @return User object
     * @throws SQLException if a database error occurs
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserID(rs.getInt("UserID"));
        user.setUserName(rs.getString("UserName"));
        user.setPhone(rs.getString("Phone"));
        user.setEmail(rs.getString("Email"));
        user.setPassword(rs.getString("Password"));
        user.setBalance(rs.getDouble("Balance"));
        return user;
    }
}