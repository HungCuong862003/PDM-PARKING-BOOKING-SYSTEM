package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.Vehicle;
import main.java.com.parkeasy.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleRepository {
    // Insert a new vehicle into the database
    public void insertVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles (VehicleID, UserID) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, vehicle.getVehicleID());
            preparedStatement.setInt(2, vehicle.getUserID());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // get a vehicle by its ID
    public Vehicle getVehicleById(String vehicleID) {
        String sql = "SELECT * FROM vehicles WHERE VehicleID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, vehicleID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Vehicle(resultSet.getString("VehicleID"), resultSet.getInt("UserID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // get all vehicles
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery(sql)) {
            while (resultSet.next()) {
                vehicles.add(new Vehicle(resultSet.getString("VehicleID"), resultSet.getInt("UserID")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    // delete a vehicle by its ID
    public void deleteVehicleById(String vehicleID) {
        String sql = "DELETE FROM vehicles WHERE VehicleID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, vehicleID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}