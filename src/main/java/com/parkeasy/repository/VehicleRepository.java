package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.Vehicle;
import main.java.com.parkeasy.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class VehicleRepository {

    public Vehicle getVehicleById(String vehicleID) {
        String query = "SELECT * FROM vehicles WHERE vehicleID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, vehicleID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new Vehicle(
                        resultSet.getString("vehicleID"),
                        resultSet.getInt("userID"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addVehicle(Vehicle vehicle) {
        String query = "INSERT INTO vehicles (vehicleID, userID) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, vehicle.getVehicleID());
            preparedStatement.setInt(2, vehicle.getUserID());
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteVehicleById(String vehicleID) {
        String query = "DELETE FROM vehicles WHERE vehicleID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, vehicleID);
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}