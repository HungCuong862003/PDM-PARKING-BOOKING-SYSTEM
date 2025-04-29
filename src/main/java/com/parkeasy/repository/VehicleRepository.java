package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.Vehicle;
import main.java.com.parkeasy.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repository class for handling vehicle-related database operations
 */
public class VehicleRepository {
    private static final Logger LOGGER = Logger.getLogger(VehicleRepository.class.getName());
    private static final String TABLE_NAME = "VEHICLE"; // Using the table name from schema

    /**
     * Inserts a new vehicle into the database
     *
     * @param vehicle The vehicle to insert
     * @return true if insertion was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean insertVehicle(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (VehicleID, UserID) VALUES (?, ?)";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, vehicle.getVehicleID());
            preparedStatement.setInt(2, vehicle.getUserID());
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting vehicle", e);
            throw e;
        } finally {
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Gets a vehicle by its ID
     *
     * @param vehicleID The ID of the vehicle to get
     * @return The vehicle if found, null otherwise
     * @throws SQLException if a database error occurs
     */
    public Vehicle getVehicleById(String vehicleID) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE VehicleID = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, vehicleID);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Vehicle(resultSet.getString("VehicleID"), resultSet.getInt("UserID"));
            }
            return null;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting vehicle by ID", e);
            throw e;
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Gets all vehicles
     *
     * @return List of all vehicles
     * @throws SQLException if a database error occurs
     */
    public List<Vehicle> getAllVehicles() throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                vehicles.add(new Vehicle(resultSet.getString("VehicleID"), resultSet.getInt("UserID")));
            }
            return vehicles;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all vehicles", e);
            throw e;
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(statement);
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Deletes a vehicle by its ID
     *
     * @param vehicleID The ID of the vehicle to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean deleteVehicleById(String vehicleID) throws SQLException {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE VehicleID = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, vehicleID);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting vehicle by ID", e);
            throw e;
        } finally {
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Gets all vehicles for a specific user
     *
     * @param userId The ID of the user
     * @return List of vehicles belonging to the user
     * @throws SQLException if a database error occurs
     */
    public List<Vehicle> getVehiclesByUserId(int userId) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE UserID = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                vehicles.add(new Vehicle(resultSet.getString("VehicleID"), resultSet.getInt("UserID")));
            }
            return vehicles;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting vehicles by user ID", e);
            throw e;
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Updates a vehicle in the database
     *
     * @param vehicle The vehicle with updated information
     * @return true if update was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean updateVehicle(Vehicle vehicle) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET UserID = ? WHERE VehicleID = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, vehicle.getUserID());
            preparedStatement.setString(2, vehicle.getVehicleID());
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating vehicle", e);
            throw e;
        } finally {
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Checks if a vehicle exists
     *
     * @param vehicleID The vehicle ID to check
     * @return true if the vehicle exists, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean isVehicleExists(String vehicleID) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE VehicleID = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, vehicleID);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if vehicle exists", e);
            throw e;
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Gets all vehicle IDs for a specific user
     *
     * @param userId The ID of the user
     * @return List of vehicle IDs belonging to the user
     * @throws SQLException if a database error occurs
     */
    public List<String> getVehicleIdsByUserId(int userId) throws SQLException {
        List<String> vehicleIds = new ArrayList<>();
        String sql = "SELECT VehicleID FROM " + TABLE_NAME + " WHERE UserID = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                vehicleIds.add(resultSet.getString("VehicleID"));
            }
            return vehicleIds;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting vehicle IDs by user ID", e);
            throw e;
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Counts the number of vehicles owned by a user
     *
     * @param userId The ID of the user
     * @return The count of vehicles
     * @throws SQLException if a database error occurs
     */
    public int countUserVehicles(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE UserID = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting user vehicles", e);
            return 0;
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Checks if a vehicle belongs to a specific user
     *
     * @param vehicleId The ID of the vehicle
     * @param userId The ID of the user
     * @return true if the vehicle belongs to the user, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean isVehicleOwnedByUser(String vehicleId, int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE VehicleID = ? AND UserID = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, vehicleId);
            preparedStatement.setInt(2, userId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if vehicle is owned by user", e);
            return false;
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Gets the most frequently used vehicle for a user
     *
     * @param userId The ID of the user
     * @return The vehicle ID or null if none found
     * @throws SQLException if a database error occurs
     */
    public String getMostUsedVehicleForUser(int userId) throws SQLException {
        String sql = "SELECT v.VehicleID, COUNT(r.ReservationID) as ReservationCount " +
                "FROM " + TABLE_NAME + " v " +
                "JOIN parkingreservation r ON v.VehicleID = r.VehicleID " +
                "WHERE v.UserID = ? " +
                "GROUP BY v.VehicleID " +
                "ORDER BY ReservationCount DESC " +
                "LIMIT 1";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("VehicleID");
            }
            return null;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting most used vehicle for user", e);
            return null;
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Gets all vehicles that have active reservations
     *
     * @return List of vehicles with active reservations
     * @throws SQLException if a database error occurs
     */
    public List<Vehicle> getVehiclesWithActiveReservations() throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT DISTINCT v.* FROM " + TABLE_NAME + " v " +
                "JOIN parkingreservation r ON v.VehicleID = r.VehicleID " +
                "WHERE r.Status IN ('ACTIVE', 'PENDING', 'PAID') " +
                "AND ((r.EndDate > CURRENT_DATE) OR (r.EndDate = CURRENT_DATE AND r.EndTime > CURRENT_TIME))";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                vehicles.add(new Vehicle(resultSet.getString("VehicleID"), resultSet.getInt("UserID")));
            }
            return vehicles;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting vehicles with active reservations", e);
            throw e;
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(statement);
            DatabaseConnection.closeConnection(connection);
        }
    }
}