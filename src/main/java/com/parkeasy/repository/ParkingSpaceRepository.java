package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ParkingSpaceRepository {

    private String sql;

    // method to add a new parking space and return the generated ID
    public int addParkingSpace(ParkingSpace parkingSpace) throws SQLException {
        // Check if AdminID exists in the admin table
        String checkAdminSql = "SELECT COUNT(*) FROM Admin WHERE AdminID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement checkAdminStatement = connection.prepareStatement(checkAdminSql)) {
            checkAdminStatement.setInt(1, parkingSpace.getAdminID());
            try (ResultSet resultSet = checkAdminStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) == 0) {
                    throw new SQLException(
                            "AdminID " + parkingSpace.getAdminID() + " does not exist in the admin table.");
                }
            }
        }

        sql = "INSERT INTO ParkingSpace (ParkingID, ParkingAddress, CostOfParking, NumberOfSlots, MaxDuration, Description, AdminID) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS)) { // Use RETURN_GENERATED_KEYS to get the generated ID
            // Set the parameters for the prepared statement
            preparedStatement.setString(1, parkingSpace.getParkingID());
            preparedStatement.setString(2, parkingSpace.getParkingAddress());
            preparedStatement.setFloat(3, parkingSpace.getCostOfParking());
            preparedStatement.setInt(4, parkingSpace.getNumberOfSlots());
            preparedStatement.setObject(5, parkingSpace.getMaxDuration(), java.sql.Types.INTEGER);
            preparedStatement.setString(6, parkingSpace.getDescription());
            preparedStatement.setInt(7, parkingSpace.getAdminID());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating parking space failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the generated ID
                } else {
                    throw new SQLException("Creating parking space failed, no ID obtained.");
                }
            }
        }
    }

    public boolean updateParkingSpace(ParkingSpace parkingSpace) {
        sql = "UPDATE ParkingSpace SET ParkingAddress = ?, CostOfParking = ?, NumberOfSlots = ?, MaxDuration = ?, Description = ? WHERE ParkingID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, parkingSpace.getParkingAddress());
            preparedStatement.setFloat(2, parkingSpace.getCostOfParking());
            preparedStatement.setInt(3, parkingSpace.getNumberOfSlots());
            preparedStatement.setInt(4, parkingSpace.getMaxDuration());
            preparedStatement.setString(5, parkingSpace.getDescription());
            preparedStatement.setString(6, parkingSpace.getParkingID());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // Return true if the update was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if there was an error
        }
    }

    // delete parking space by ID
    public void deleteParkingSpace(String spaceId) {
        sql = "DELETE FROM ParkingSpace WHERE ParkingID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, spaceId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // get all parking spaces
    public List<ParkingSpace> findAll() {
        sql = "SELECT * FROM ParkingSpace";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()) {
            List<ParkingSpace> parkingSpaces = new ArrayList<>();
            while (resultSet.next()) {
                ParkingSpace parkingSpace = new ParkingSpace(
                        resultSet.getString("ParkingID"),
                        resultSet.getString("ParkingAddress"),
                        resultSet.getFloat("CostOfParking"),
                        resultSet.getInt("NumberOfSlots"),
                        resultSet.getInt("MaxDuration"),
                        resultSet.getString("Description"),
                        resultSet.getInt("AdminID"));
                parkingSpaces.add(parkingSpace);
            }
            return parkingSpaces;
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // Return null if there was an error
        }
    }
}