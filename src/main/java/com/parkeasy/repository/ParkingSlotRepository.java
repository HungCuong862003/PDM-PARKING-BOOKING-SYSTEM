package main.java.com.parkeasy.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import main.java.com.parkeasy.model.ParkingSlot;
import main.java.com.parkeasy.util.DatabaseConnection;

/**
 * Repository class for managing ParkingSlot entities in the database.
 * Provides CRUD operations for parking slots.
 */
public class ParkingSlotRepository {

    /**
     * Inserts a new parking slot into the database.
     *
     * @param parkingSlot The parking slot to insert
     * @return true if insertion was successful, false otherwise
     */
    public boolean insertParkingSlot(ParkingSlot parkingSlot) {
        String sql = "INSERT INTO parkingSlot (SlotID, SlotNumber, Availability, ParkingID) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, parkingSlot.getSlotID());
            preparedStatement.setString(2, parkingSlot.getSlotNumber());
            preparedStatement.setBoolean(3, parkingSlot.isAvailability());
            preparedStatement.setString(4, parkingSlot.getParkingID());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting parking slot: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves a parking slot by its ID.
     *
     * @param slotID The ID of the parking slot to retrieve
     * @return The found ParkingSlot or null if not found
     */
    public ParkingSlot getParkingSlotById(int slotID) {
        String sql = "SELECT * FROM parkingSlot WHERE SlotID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, slotID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return extractParkingSlotFromResultSet(resultSet);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving parking slot by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Retrieves all parking slots for a specific parking space.
     *
     * @param parkingID The ID of the parking space
     * @return A list of parking slots belonging to the specified parking space
     */
    public List<ParkingSlot> getParkingSlotsByParkingSpaceId(String parkingID) {
        List<ParkingSlot> parkingSlots = new ArrayList<>();
        String sql = "SELECT * FROM parkingSlot WHERE ParkingID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, parkingID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    parkingSlots.add(extractParkingSlotFromResultSet(resultSet));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving parking slots by parking space ID: " + e.getMessage());
            e.printStackTrace();
        }

        return parkingSlots;
    }

    /**
     * Retrieves all available parking slots for a specific parking space.
     *
     * @param parkingID The ID of the parking space
     * @return A list of available parking slots belonging to the specified parking space
     */
    public List<ParkingSlot> getAvailableParkingSlotsByParkingSpaceId(String parkingID) {
        List<ParkingSlot> parkingSlots = new ArrayList<>();
        String sql = "SELECT * FROM parkingSlot WHERE ParkingID = ? AND Availability = true";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, parkingID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    parkingSlots.add(extractParkingSlotFromResultSet(resultSet));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving available parking slots: " + e.getMessage());
            e.printStackTrace();
        }

        return parkingSlots;
    }

    /**
     * Deletes a parking slot by its ID.
     *
     * @param slotID The ID of the parking slot to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteParkingSlotById(int slotID) {
        String sql = "DELETE FROM parkingSlot WHERE SlotID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, slotID);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting parking slot: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates a parking slot's information by its ID.
     *
     * @param slotID The ID of the parking slot to update
     * @param parkingSlot The updated parking slot information
     * @return true if update was successful, false otherwise
     */
    public boolean updateParkingSlotById(int slotID, ParkingSlot parkingSlot) {
        String sql = "UPDATE parkingSlot SET SlotNumber = ?, Availability = ?, ParkingID = ? WHERE SlotID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, parkingSlot.getSlotNumber());
            preparedStatement.setBoolean(2, parkingSlot.isAvailability());
            preparedStatement.setString(3, parkingSlot.getParkingID());
            preparedStatement.setInt(4, slotID);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating parking slot: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates the availability status of a parking slot.
     *
     * This method is used by ReservationService to update slot availability.
     *
     * @param slotID The ID of the parking slot
     * @param availability The new availability status
     * @return true if update was successful, false otherwise
     */
    public boolean updateParkingSlotAvailability(int slotID, boolean availability) {
        String sql = "UPDATE parkingSlot SET Availability = ? WHERE SlotID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setBoolean(1, availability);
            preparedStatement.setInt(2, slotID);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating parking slot availability: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates the availability status of a parking slot.
     * This is an alias for updateParkingSlotAvailability to maintain compatibility with ReservationService.
     *
     * @param slotID The ID of the parking slot
     * @param availability The new availability status
     * @return true if update was successful, false otherwise
     */
    public boolean updateSlotAvailability(int slotID, boolean availability) {
        return updateParkingSlotAvailability(slotID, availability);
    }

    /**
     * Retrieves slot IDs for a specific parking space.
     * This method is used by ReservationService to get all slot IDs for a parking space.
     *
     * @param parkingID The ID of the parking space
     * @return A list of slot IDs belonging to the specified parking space
     */
    public List<Integer> getSlotIdsByParkingId(String parkingID) {
        List<Integer> slotIDs = new ArrayList<>();
        String sql = "SELECT SlotID FROM parkingSlot WHERE ParkingID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, parkingID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    slotIDs.add(resultSet.getInt("SlotID"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving slot IDs by parking ID: " + e.getMessage());
            e.printStackTrace();
        }

        return slotIDs;
    }

    /**
     * Retrieves the parking ID for a specific slot.
     * This method is used by ReservationService to get the parking space ID for a slot.
     *
     * @param slotID The ID of the parking slot
     * @return The parking space ID, or null if not found
     */
    public String getParkingIdBySlotId(int slotID) {
        String sql = "SELECT ParkingID FROM parkingSlot WHERE SlotID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, slotID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("ParkingID");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving parking ID by slot ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Counts the total number of parking slots for a specific parking space.
     *
     * @param parkingID The ID of the parking space
     * @return The count of parking slots
     */
    public int countParkingSlotsByParkingSpaceId(String parkingID) {
        String sql = "SELECT COUNT(*) FROM parkingSlot WHERE ParkingID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, parkingID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error counting parking slots: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Counts the number of available parking slots for a specific parking space.
     *
     * @param parkingID The ID of the parking space
     * @return The count of available parking slots
     */
    public int countAvailableParkingSlotsByParkingSpaceId(String parkingID) {
        String sql = "SELECT COUNT(*) FROM parkingSlot WHERE ParkingID = ? AND Availability = true";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, parkingID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error counting available parking slots: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Retrieves all parking slots from the database.
     *
     * @return A list of all parking slots
     */
    public List<ParkingSlot> getAllParkingSlots() {
        List<ParkingSlot> parkingSlots = new ArrayList<>();
        String sql = "SELECT * FROM parkingSlot";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                parkingSlots.add(extractParkingSlotFromResultSet(resultSet));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all parking slots: " + e.getMessage());
            e.printStackTrace();
        }

        return parkingSlots;
    }

    /**
     * Helper method to extract a ParkingSlot object from a ResultSet.
     *
     * @param resultSet The ResultSet containing parking slot data
     * @return A ParkingSlot object
     * @throws SQLException if there's an error accessing the ResultSet
     */
    private ParkingSlot extractParkingSlotFromResultSet(ResultSet resultSet) throws SQLException {
        ParkingSlot parkingSlot = new ParkingSlot();
        parkingSlot.setSlotID(resultSet.getInt("SlotID"));
        parkingSlot.setSlotNumber(resultSet.getString("SlotNumber"));
        parkingSlot.setAvailability(resultSet.getBoolean("Availability"));
        parkingSlot.setParkingID(resultSet.getString("ParkingID"));
        return parkingSlot;
    }
}