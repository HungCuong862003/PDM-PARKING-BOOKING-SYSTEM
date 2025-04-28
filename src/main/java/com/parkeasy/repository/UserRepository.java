package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository class for accessing User data in the database.
 * Handles CRUD operations for User entities.
 */
public class UserRepository {

    /**
     * Save a new user to the database.
     *
     * @param user User to save
     * @return Saved user with generated ID
     * @throws SQLException if a database error occurs
     */
    public User save(User user) throws SQLException {
        String sql = "INSERT INTO USER (UserName, Phone, Email, Password, Balance) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPhone());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setBigDecimal(5, user.getBalance());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }

        return user;
    }

    /**
     * Update an existing user in the database.
     *
     * @param user User to update
     * @return true if update was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE USER SET UserName = ?, Phone = ?, Email = ?, Password = ?, Balance = ? WHERE UserID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPhone());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setBigDecimal(5, user.getBalance());
            stmt.setInt(6, user.getUserID());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Find a user by their ID.
     *
     * @param userID ID of the user to find
     * @return Optional containing the found user, or empty if not found
     * @throws SQLException if a database error occurs
     */
    public Optional<User> findById(int userID) throws SQLException {
        String sql = "SELECT * FROM USER WHERE UserID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, userID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    /**
     * Find a user by their email.
     *
     * @param email Email of the user to find
     * @return Optional containing the found user, or empty if not found
     * @throws SQLException if a database error occurs
     */
    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM USER WHERE Email = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    /**
     * Find a user by their phone number.
     *
     * @param phone Phone number of the user to find
     * @return Optional containing the found user, or empty if not found
     * @throws SQLException if a database error occurs
     */
    public Optional<User> findByPhone(String phone) throws SQLException {
        String sql = "SELECT * FROM USER WHERE Phone = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, phone);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    /**
     * Find a user by email and password (for authentication).
     *
     * @param email Email of the user
     * @param password Password of the user
     * @return Optional containing the found user, or empty if not found
     * @throws SQLException if a database error occurs
     */
    public Optional<User> findByEmailAndPassword(String email, String password) throws SQLException {
        String sql = "SELECT * FROM USER WHERE Email = ? AND Password = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    /**
     * Delete a user from the database.
     *
     * @param userID ID of the user to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean delete(int userID) throws SQLException {
        String sql = "DELETE FROM USER WHERE UserID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, userID);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Get all users from the database.
     *
     * @return List of all users
     * @throws SQLException if a database error occurs
     */
    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM USER";
        List<User> users = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }

        return users;
    }

    /**
     * Find users with a balance greater than the specified amount.
     *
     * @param amount Minimum balance amount
     * @return List of users with balance greater than the specified amount
     * @throws SQLException if a database error occurs
     */
    public List<User> findByBalanceGreaterThan(BigDecimal amount) throws SQLException {
        String sql = "SELECT * FROM USER WHERE Balance > ?";
        List<User> users = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setBigDecimal(1, amount);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }

        return users;
    }

    /**
     * Update a user's balance.
     *
     * @param userID ID of the user
     * @param newBalance New balance to set
     * @return true if update was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean updateBalance(int userID, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE USER SET Balance = ? WHERE UserID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setBigDecimal(1, newBalance);
            stmt.setInt(2, userID);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Count total number of users in the database.
     *
     * @return Total number of users
     * @throws SQLException if a database error occurs
     */
    public int countUsers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM USER";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        }
    }

    /**
     * Check if an email is already registered.
     *
     * @param email Email to check
     * @return true if email exists, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM USER WHERE Email = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                } else {
                    return false;
                }
            }
        }
    }

    /**
     * Check if a phone number is already registered.
     *
     * @param phone Phone number to check
     * @return true if phone exists, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean existsByPhone(String phone) throws SQLException {
        String sql = "SELECT COUNT(*) FROM USER WHERE Phone = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, phone);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                } else {
                    return false;
                }
            }
        }
    }

    /**
     * Search for users by name (partial match).
     *
     * @param namePattern Pattern to search for in user names
     * @return List of matching users
     * @throws SQLException if a database error occurs
     */
    public List<User> searchByName(String namePattern) throws SQLException {
        String sql = "SELECT * FROM USER WHERE UserName LIKE ?";
        List<User> users = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, "%" + namePattern + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }

        return users;
    }

    /**
     * Helper method to map a ResultSet row to a User object.
     *
     * @param rs ResultSet containing user data
     * @return User object with data from the ResultSet
     * @throws SQLException if a database error occurs
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserID(rs.getInt("UserID"));
        user.setUserName(rs.getString("UserName"));
        user.setPhone(rs.getString("Phone"));
        user.setEmail(rs.getString("Email"));
        user.setPassword(rs.getString("Password"));
        user.setBalance(rs.getBigDecimal("Balance"));
        return user;
    }
}