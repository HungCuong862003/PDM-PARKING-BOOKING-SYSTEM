package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.ParkingSlot;
import main.java.com.parkeasy.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ParkingSlotRepository {

    private String sql;
    private Connection connection;

    public int addParkingSlot(ParkingSlot slot) {
        connection = DatabaseConnection.getConnection();
        sql = "INSERT INTO parkingslot (SlotID, SlotNumber, Available, ParkingID) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, slot.getSlotID());
            preparedStatement.setString(2, slot.getSlotNumber());
            preparedStatement.setBoolean(3, slot.getAvailability());
            preparedStatement.setString(4, slot.getParkingID());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating parking slot failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the generated ID
                } else {
                    throw new SQLException("Creating parking slot failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Indicate failure
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    public boolean updateParkingSlot(ParkingSlot slot) {
        connection = DatabaseConnection.getConnection();
        sql = "UPDATE parkingslot SET SlotNumber = ?, Available = ?, ParkingID = ? WHERE SlotID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, slot.getSlotNumber());
            preparedStatement.setBoolean(2, slot.getAvailability());
            preparedStatement.setString(3, slot.getParkingID()); // Set the parking ID from the slot object
            preparedStatement.setInt(4, slot.getSlotID()); // Set the SlotID
            int rowsUpdated = preparedStatement.executeUpdate();
            return rowsUpdated > 0; // Return true if at least one row was updated
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false in case of an exception
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    public void deleteParkingSlot(String slotId) {
        connection = DatabaseConnection.getConnection();
        sql = "DELETE FROM parkingslot WHERE SlotID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, slotId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    // test method to get all parking slots
    public void getAllParkingSlots() {
        connection = DatabaseConnection.getConnection();
        sql = "SELECT * FROM parkingslot";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                System.out.println("Slot ID: " + resultSet.getInt("slotID"));
                System.out.println("Slot Number: " + resultSet.getString("slotNumber"));
                System.out.println("Availability: " + resultSet.getBoolean("availability"));
                System.out.println("Parking ID: " + resultSet.getString("parkingID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    // find parking slot by ID
    public ParkingSlot findParkingSlotById(int slotId) {
        connection = DatabaseConnection.getConnection();
        sql = "SELECT * FROM parkingslot WHERE SlotID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, slotId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new ParkingSlot(
                        resultSet.getInt("SlotID"),
                        resultSet.getString("SlotNumber"),
                        resultSet.getBoolean("Available"),
                        resultSet.getString("ParkingID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return null; // Return null if not found
    }
}