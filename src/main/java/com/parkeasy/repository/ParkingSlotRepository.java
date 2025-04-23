package main.java.com.parkeasy.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import main.java.com.parkeasy.model.ParkingSlot;
import main.java.com.parkeasy.util.DatabaseConnection;

public class ParkingSlotRepository {
    // insert a new parking slot into the database
    public void insertParkingSpace(ParkingSlot parkingSlot) {
        String sql = "INSERT INTO parkingSlot (SlotID, SlotNumber, Availability, ParkingID) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, parkingSlot.getSlotID());
            preparedStatement.setString(2, parkingSlot.getSlotNumber());
            preparedStatement.setBoolean(3, parkingSlot.getAvailability());
            preparedStatement.setString(4, parkingSlot.getParkingID());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // get a parking slot by its ID
    public ParkingSlot getParkingSlotById(int slotID) {
        String sql = "SELECT * FROM parkingSlot WHERE SlotID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, slotID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new ParkingSlot(resultSet.getInt("SlotID"), resultSet.getString("SlotNumber"),
                        resultSet.getBoolean("Availability"), resultSet.getString("ParkingID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // get all parking slots by parking space ID
    public List<ParkingSlot> getParkingSlotsByParkingSpaceId(String parkingID) {
        List<ParkingSlot> parkingSlots = new ArrayList<>();
        String sql = "SELECT * FROM parkingSlot WHERE ParkingID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, parkingID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                parkingSlots.add(new ParkingSlot(resultSet.getInt("SlotID"), resultSet.getString("SlotNumber"),
                        resultSet.getBoolean("Availability"), resultSet.getString("ParkingID")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return parkingSlots;
    }

    // delete a parking slot by its ID
    public void deleteParkingSlotById(int slotID) {
        String sql = "DELETE FROM parkingSlot WHERE SlotID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, slotID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // update a parking slot by its ID
    public void updateParkingSlotById(int slotID, ParkingSlot parkingSlot) {
        String sql = "UPDATE parkingSlot SET SlotNumber = ?, Availability = ?, ParkingID = ? WHERE SlotID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, parkingSlot.getSlotNumber());
            preparedStatement.setBoolean(2, parkingSlot.getAvailability());
            preparedStatement.setString(3, parkingSlot.getParkingID());
            preparedStatement.setInt(4, slotID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}