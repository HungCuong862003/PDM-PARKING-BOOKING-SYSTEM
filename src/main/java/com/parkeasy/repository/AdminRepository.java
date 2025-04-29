package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for Admin entity.
 * Handles database operations related to administrators.
 */
public class AdminRepository {

    /**
     * Saves a new admin to the database.
     *
     * @param admin The admin object to save
     */
    public void save(Admin admin) {
        String sql = "INSERT INTO admin (adminName, phone, email, password) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, admin.getAdminName());
            preparedStatement.setString(2, admin.getPhone());
            preparedStatement.setString(3, admin.getEmail());
            preparedStatement.setString(4, admin.getPassword());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error saving admin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Deletes an admin by ID.
     *
     * @param adminID The ID of the admin to delete
     */
    public void deleteById(int adminID) {
        String sql = "DELETE FROM admin WHERE adminID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, adminID);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting admin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Updates an existing admin.
     *
     * @param admin The admin object with updated information
     */
    public void update(Admin admin) {
        String sql = "UPDATE admin SET adminName = ?, phone = ?, email = ?, password = ? WHERE adminID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, admin.getAdminName());
            preparedStatement.setString(2, admin.getPhone());
            preparedStatement.setString(3, admin.getEmail());
            preparedStatement.setString(4, admin.getPassword());
            preparedStatement.setInt(5, admin.getAdminID());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating admin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets an admin by email.
     *
     * @param email The email of the admin to retrieve
     * @return The admin if found, null otherwise
     */
    public Admin getAdminByEmail(String email) {
        String sql = "SELECT * FROM admin WHERE email = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Admin admin = new Admin();
                    admin.setAdminID(resultSet.getInt("adminID"));
                    admin.setAdminName(resultSet.getString("adminName"));
                    admin.setPhone(resultSet.getString("phone"));
                    admin.setEmail(resultSet.getString("email"));
                    admin.setPassword(resultSet.getString("password"));
                    return admin;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving admin by email: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets an admin by phone number.
     *
     * @param phone The phone number of the admin to retrieve
     * @return The admin if found, null otherwise
     */
    public Admin getAdminByPhone(String phone) {
        String sql = "SELECT * FROM admin WHERE phone = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, phone);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Admin admin = new Admin();
                    admin.setAdminID(resultSet.getInt("adminID"));
                    admin.setAdminName(resultSet.getString("adminName"));
                    admin.setPhone(resultSet.getString("phone"));
                    admin.setEmail(resultSet.getString("email"));
                    admin.setPassword(resultSet.getString("password"));
                    return admin;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving admin by phone: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets an admin by ID.
     *
     * @param adminID The ID of the admin to retrieve
     * @return The admin if found, null otherwise
     */
    public Admin getAdminById(int adminID) {
        String sql = "SELECT * FROM admin WHERE adminID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, adminID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Admin admin = new Admin();
                    admin.setAdminID(resultSet.getInt("adminID"));
                    admin.setAdminName(resultSet.getString("adminName"));
                    admin.setPhone(resultSet.getString("phone"));
                    admin.setEmail(resultSet.getString("email"));
                    admin.setPassword(resultSet.getString("password"));
                    return admin;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving admin by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets all admins from the database.
     *
     * @return A list of all admins
     */
    public List<Admin> getAllAdmins() {
        String sql = "SELECT * FROM admin";
        List<Admin> admins = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Admin admin = new Admin();
                admin.setAdminID(resultSet.getInt("adminID"));
                admin.setAdminName(resultSet.getString("adminName"));
                admin.setPhone(resultSet.getString("phone"));
                admin.setEmail(resultSet.getString("email"));
                admin.setPassword(resultSet.getString("password"));
                admins.add(admin);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all admins: " + e.getMessage());
            e.printStackTrace();
        }

        return admins;
    }
}