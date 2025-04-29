package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.Admin;
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
 * Repository class for Admin entity
 * Handles database operations related to admins
 */
public class AdminRepository {
    private static final Logger LOGGER = Logger.getLogger(AdminRepository.class.getName());

    /**
     * Creates a new admin in the database
     *
     * @param admin Admin object with data to save
     * @return true if successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean createAdmin(Admin admin) throws SQLException {
        String sql = "INSERT INTO ADMIN (AdminName, Phone, Email, Password) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, admin.getAdminName());
            pstmt.setString(2, admin.getPhone());
            pstmt.setString(3, admin.getEmail());
            pstmt.setString(4, admin.getPassword());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        admin.setAdminID(generatedKeys.getInt(1));
                    }
                }
                return true;
            }

            return false;
        }
    }

    /**
     * Updates an existing admin in the database
     *
     * @param admin Admin object with updated data
     * @return true if successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean updateAdmin(Admin admin) throws SQLException {
        String sql = "UPDATE ADMIN SET AdminName = ?, Phone = ?, Email = ?, Password = ? WHERE AdminID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, admin.getAdminName());
            pstmt.setString(2, admin.getPhone());
            pstmt.setString(3, admin.getEmail());
            pstmt.setString(4, admin.getPassword());
            pstmt.setInt(5, admin.getAdminID());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Gets an admin by ID
     *
     * @param adminId ID of the admin to get
     * @return Admin object if found, null otherwise
     * @throws SQLException if a database error occurs
     */
    public Admin getAdminById(int adminId) throws SQLException {
        String sql = "SELECT * FROM ADMIN WHERE AdminID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, adminId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdmin(rs);
                }
            }
        }

        return null;
    }

    /**
     * Checks if an email already exists in the admin database
     *
     * @param email Email to check
     * @return true if exists, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean isEmailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ADMIN WHERE Email = ?";

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
     * Checks if a phone number already exists in the admin database
     *
     * @param phone Phone number to check
     * @return true if exists, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean isPhoneExists(String phone) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ADMIN WHERE Phone = ?";

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
     * Find an admin by email
     *
     * @param email Admin email
     * @return Optional containing Admin if found, empty otherwise
     */
    public Optional<Admin> findByEmail(String email) {
        String sql = "SELECT * FROM ADMIN WHERE Email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAdmin(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding admin by email", e);
        }

        return Optional.empty();
    }

    /**
     * Save an admin (creates if new, updates if exists)
     *
     * @param admin Admin to save
     * @return true if successful, false otherwise
     */
    public boolean save(Admin admin) {
        try {
            if (admin.getAdminID() > 0) {
                // Update existing admin
                return updateAdmin(admin);
            } else {
                // Create new admin
                return createAdmin(admin);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving admin", e);
            return false;
        }
    }

    /**
     * Gets all admins
     *
     * @return List of all admins
     * @throws SQLException if a database error occurs
     */
    public List<Admin> getAllAdmins() throws SQLException {
        String sql = "SELECT * FROM ADMIN";
        List<Admin> admins = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                admins.add(mapResultSetToAdmin(rs));
            }
        }

        return admins;
    }
    /**
     * Gets an admin by email
     *
     * @param email Email of the admin to get
     * @return Admin object if found, null otherwise
     * @throws SQLException if a database error occurs
     */
    /**
     * Gets an admin by email
     *
     * @param email Email of the admin to get
     * @return Admin object if found, null otherwise
     */
    public Admin getAdminByEmail(String email) {
        String sql = "SELECT * FROM ADMIN WHERE Email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdmin(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting admin by email", e);
        }

        return null;
    }

    /**
     * Maps a ResultSet to an Admin object
     *
     * @param rs ResultSet to map
     * @return Admin object
     * @throws SQLException if a database error occurs
     */
    private Admin mapResultSetToAdmin(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setAdminID(rs.getInt("AdminID"));
        admin.setAdminName(rs.getString("AdminName"));
        admin.setPhone(rs.getString("Phone"));
        admin.setEmail(rs.getString("Email"));
        admin.setPassword(rs.getString("Password"));
        return admin;
    }
}