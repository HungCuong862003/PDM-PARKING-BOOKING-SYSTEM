package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminRepository {

    // Assuming we have a database connection established
    // private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    // private Statement statement;
    private String sql;

    private Connection databaseConnection;

    public void save(Admin admin) {
        databaseConnection = DatabaseConnection.getConnection();
        sql = "INSERT INTO admin (adminName, phone, email, password) VALUES (?, ?, ?, ?)";
        try {
            preparedStatement = databaseConnection.prepareStatement(sql);
            preparedStatement.setString(1, admin.getAdminName());
            preparedStatement.setString(2, admin.getPhone());
            preparedStatement.setString(3, admin.getEmail());
            preparedStatement.setString(4, admin.getPassword());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closePreparedStatement(preparedStatement);
            DatabaseConnection.closeConnection(databaseConnection);
        }
    }

    // Method to delete an admin by ID
    public void deleteById(int adminID) {
        databaseConnection = DatabaseConnection.getConnection();
        sql = "DELETE FROM admin WHERE adminID = ?";
        try {
            preparedStatement = databaseConnection.prepareStatement(sql);
            preparedStatement.setInt(1, adminID);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closePreparedStatement(preparedStatement);
            DatabaseConnection.closeConnection(databaseConnection);
        }
    }

    // Method to get an admin by ID
    public void update(Admin admin) {
        databaseConnection = DatabaseConnection.getConnection();
        sql = "UPDATE admin SET adminName = ?, phone = ?, email = ?, password = ? WHERE adminID = ?";
        try {
            preparedStatement = databaseConnection.prepareStatement(sql);
            preparedStatement.setString(1, admin.getAdminName());
            preparedStatement.setString(2, admin.getPhone());
            preparedStatement.setString(3, admin.getEmail());
            preparedStatement.setString(4, admin.getPassword());
            preparedStatement.setInt(5, admin.getAdminID());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closePreparedStatement(preparedStatement);
            DatabaseConnection.closeConnection(databaseConnection);
        }
    }

    public Admin getAdminByEmail(String email) {
        databaseConnection = DatabaseConnection.getConnection();
        sql = "SELECT * FROM admin WHERE email = ?";
        try {
            preparedStatement = databaseConnection.prepareStatement(sql);
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Admin admin = new Admin();
                admin.setAdminID(resultSet.getInt("adminID"));
                admin.setAdminName(resultSet.getString("adminName"));
                admin.setPhone(resultSet.getString("phone"));
                admin.setEmail(resultSet.getString("email"));
                admin.setPassword(resultSet.getString("password"));
                return admin;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closePreparedStatement(preparedStatement);
            DatabaseConnection.closeConnection(databaseConnection);
        }
        return null;
    }

    public Object getAdminByPhone(String phone) {
        databaseConnection = DatabaseConnection.getConnection();
        sql = "SELECT * FROM admin WHERE phone = ?";
        try {
            preparedStatement = databaseConnection.prepareStatement(sql);
            preparedStatement.setString(1, phone);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Admin admin = new Admin();
                admin.setAdminID(resultSet.getInt("adminID"));
                admin.setAdminName(resultSet.getString("adminName"));
                admin.setPhone(resultSet.getString("phone"));
                admin.setEmail(resultSet.getString("email"));
                admin.setPassword(resultSet.getString("password"));
                return admin;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closePreparedStatement(preparedStatement);
            DatabaseConnection.closeConnection(databaseConnection);
        }
        return null;
    }
}