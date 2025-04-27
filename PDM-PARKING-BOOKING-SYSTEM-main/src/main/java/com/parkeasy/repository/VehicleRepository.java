package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.Vehicle;
import main.java.com.parkeasy.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing Vehicle entities in the database.
 * Provides CRUD operations for vehicles.
 */
public class VehicleRepository {

    /**
     * Inserts a new vehicle into the database.
     *
     * @param vehicle The vehicle to insert
     * @return true if insertion was successful, false otherwise
     */
    public boolean insertVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO VEHICLE (VehicleID, UserID) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, vehicle.getVehicleID());
            preparedStatement.setInt(2, vehicle.getUserID());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting vehicle: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves a vehicle by its ID.
     *
     * @param vehicleID The ID of the vehicle to retrieve
     * @return The found Vehicle or null if not found
     */
    public Vehicle getVehicleById(String vehicleID) {
        String sql = "SELECT * FROM VEHICLE WHERE VehicleID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, vehicleID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Vehicle(
                            resultSet.getString("VehicleID"),
                            resultSet.getInt("UserID")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving vehicle by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Retrieves all vehicles from the database.
     *
     * @return A list of all vehicles
     */
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM VEHICLE";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                vehicles.add(new Vehicle(
                        resultSet.getString("VehicleID"),
                        resultSet.getInt("UserID")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all vehicles: " + e.getMessage());
            e.printStackTrace();
        }

        return vehicles;
    }

    /**
     * Deletes a vehicle by its ID.
     *
     * @param vehicleID The ID of the vehicle to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteVehicle(String vehicleID) {
        String sql = "DELETE FROM VEHICLE WHERE VehicleID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, vehicleID);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting vehicle: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all vehicles belonging to a specific user.
     *
     * @param userID The ID of the user
     * @return A list of vehicles belonging to the specified user
     */
    public List<Vehicle> getVehiclesByUserId(int userID) {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM VEHICLE WHERE UserID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    vehicles.add(new Vehicle(
                            resultSet.getString("VehicleID"),
                            resultSet.getInt("UserID")
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving vehicles by user ID: " + e.getMessage());
            e.printStackTrace();
        }

        return vehicles;
    }

    /**
     * Updates vehicle information.
     *
     * @param vehicleID The ID of the vehicle to update
     * @param vehicle The updated vehicle information
     * @return true if update was successful, false otherwise
     */
    public boolean updateVehicle(String vehicleID, Vehicle vehicle) {
        String sql = "UPDATE VEHICLE SET UserID = ? WHERE VehicleID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, vehicle.getUserID());
            preparedStatement.setString(2, vehicleID);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating vehicle: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets the number of vehicles owned by a user.
     *
     * @param userID The ID of the user
     * @return The count of vehicles owned by the user
     */
    public int getVehicleCountByUserId(int userID) {
        String sql = "SELECT COUNT(*) FROM VEHICLE WHERE UserID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error counting vehicles by user ID: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Checks if a vehicle exists in the database.
     *
     * @param vehicleID The ID of the vehicle to check
     * @return true if the vehicle exists, false otherwise
     */
    public boolean vehicleExists(String vehicleID) {
        String sql = "SELECT COUNT(*) FROM VEHICLE WHERE VehicleID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, vehicleID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking if vehicle exists: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Checks if a vehicle belongs to a specific user.
     *
     * @param vehicleID The ID of the vehicle
     * @param userID The ID of the user
     * @return true if the vehicle belongs to the user, false otherwise
     */
    public boolean isVehicleOwnedByUser(String vehicleID, int userID) {
        String sql = "SELECT COUNT(*) FROM VEHICLE WHERE VehicleID = ? AND UserID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, vehicleID);
            preparedStatement.setInt(2, userID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking if vehicle is owned by user: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}