package main.java.com.parkeasy.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.util.DatabaseConnection;

public class ParkingSpaceRepository {
    // insert a new parking space into the database
    public void insertParkingSpace(ParkingSpace parkingSpace) {
        String sql = "INSERT INTO parkingSpace (ParkingID, ParkingAddress, CostOfParking, NumberOfSlots, MaxDuration, Description, AdminID) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, parkingSpace.getParkingID());
            preparedStatement.setString(2, parkingSpace.getParkingAddress());
            preparedStatement.setFloat(3, parkingSpace.getCostOfParking());
            preparedStatement.setInt(4, parkingSpace.getNumberOfSlots());
            preparedStatement.setInt(5, parkingSpace.getMaxDuration());
            preparedStatement.setString(6, parkingSpace.getDescription());
            preparedStatement.setInt(7, parkingSpace.getAdminID());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // get a parking space by its ID
    public ParkingSpace getParkingSpaceById(String parkingID) {
        String sql = "SELECT * FROM parkingSpace WHERE ParkingID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, parkingID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new ParkingSpace(resultSet.getString("ParkingID"), resultSet.getString("ParkingAddress"),
                        resultSet.getFloat("CostOfParking"), resultSet.getInt("NumberOfSlots"),
                        resultSet.getInt("MaxDuration"), resultSet.getString("Description"),
                        resultSet.getInt("AdminID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // get all parking spaces
    public List<ParkingSpace> getAllParkingSpaces() {
        List<ParkingSpace> parkingSpaces = new ArrayList<>();
        String sql = "SELECT * FROM parkingSpace";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery(sql)) {
            while (resultSet.next()) {
                parkingSpaces
                        .add(new ParkingSpace(resultSet.getString("ParkingID"), resultSet.getString("ParkingAddress"),
                                resultSet.getFloat("CostOfParking"), resultSet.getInt("NumberOfSlots"),
                                resultSet.getInt("MaxDuration"), resultSet.getString("Description"),
                                resultSet.getInt("AdminID")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return parkingSpaces;
    }

    // get all parking spaces by admin ID
    public List<ParkingSpace> getParkingSpacesByAdminId(int adminID) {
        List<ParkingSpace> parkingSpaces = new ArrayList<>();
        String sql = "SELECT * FROM parkingSpace WHERE AdminID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, adminID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                parkingSpaces
                        .add(new ParkingSpace(resultSet.getString("ParkingID"), resultSet.getString("ParkingAddress"),
                                resultSet.getFloat("CostOfParking"), resultSet.getInt("NumberOfSlots"),
                                resultSet.getInt("MaxDuration"), resultSet.getString("Description"),
                                resultSet.getInt("AdminID")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return parkingSpaces;
    }

    // delete a parking space by its ID
    public void deleteParkingSpaceById(String parkingID) {
        String sql = "DELETE FROM parkingSpace WHERE ParkingID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, parkingID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // update a parking space by its ID
    public void updateParkingSpaceById(String parkingID, ParkingSpace parkingSpace) {
        String sql = "UPDATE parkingSpace SET ParkingAddress = ?, CostOfParking = ?, NumberOfSlots = ?, MaxDuration = ?, Description = ?, AdminID = ? WHERE ParkingID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, parkingSpace.getParkingAddress());
            preparedStatement.setFloat(2, parkingSpace.getCostOfParking());
            preparedStatement.setInt(3, parkingSpace.getNumberOfSlots());
            preparedStatement.setInt(4, parkingSpace.getMaxDuration());
            preparedStatement.setString(5, parkingSpace.getDescription());
            preparedStatement.setInt(6, parkingSpace.getAdminID());
            preparedStatement.setString(7, parkingID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}