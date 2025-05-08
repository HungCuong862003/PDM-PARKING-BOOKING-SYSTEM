package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.ParkingSlot;
import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParkingSlotRepository {
    private static final Logger LOGGER = Logger.getLogger(ParkingSlotRepository.class.getName());
    private static String sql;
    private static Connection connection;

    public boolean addParkingSlot(ParkingSlot slot) {
        connection = DatabaseConnection.getConnection();
        sql = "INSERT INTO PARKING_SLOT (SlotNumber, Availability, ParkingID) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, slot.getSlotNumber());
            preparedStatement.setBoolean(2, slot.getAvailability());
            preparedStatement.setString(3, slot.getParkingID());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding parking slot", e);
            return false;
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    // Update by SlotNumber
    public boolean updateParkingSlotByNumber(String slotNumber, ParkingSlot parkingSlot) {
        connection = DatabaseConnection.getConnection();
        sql = "UPDATE PARKING_SLOT SET Availability = ?, ParkingID = ? WHERE SlotNumber = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setBoolean(1, parkingSlot.getAvailability());
            preparedStatement.setString(2, parkingSlot.getParkingID());
            preparedStatement.setString(3, slotNumber);
            int rowsUpdated = preparedStatement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating parking slot by number", e);
            return false;
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    // Update using the parking slot object
    public boolean updateParkingSlot(ParkingSlot parkingSlot) {
        connection = DatabaseConnection.getConnection();
        sql = "UPDATE PARKING_SLOT SET Availability = ?, ParkingID = ? WHERE SlotNumber = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setBoolean(1, parkingSlot.getAvailability());
            preparedStatement.setString(2, parkingSlot.getParkingID());
            preparedStatement.setString(3, parkingSlot.getSlotNumber());
            int rowsUpdated = preparedStatement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating parking slot", e);
            return false;
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    // Get all parking slots - maintain backward compatibility
    public void getAllParkingSlots() {
        List<ParkingSlot> slots = getAllParkingSlotsAsList();
        for (ParkingSlot slot : slots) {
            System.out.println("Slot Number: " + slot.getSlotNumber());
            System.out.println("Availability: " + slot.getAvailability());
            System.out.println("Parking ID: " + slot.getParkingID());
        }
    }

    // Get all parking slots as list
    public List<ParkingSlot> getAllParkingSlotsAsList() {
        List<ParkingSlot> parkingSlots = new ArrayList<>();
        connection = DatabaseConnection.getConnection();
        sql = "SELECT * FROM PARKING_SLOT";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                ParkingSlot slot = new ParkingSlot(
                        resultSet.getString("SlotNumber"),
                        resultSet.getBoolean("Availability"),
                        resultSet.getString("ParkingID")
                );
                parkingSlots.add(slot);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all parking slots", e);
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return parkingSlots;
    }

    // Find by SlotNumber (main method to use now)
    public ParkingSlot findParkingSlotByNumber(String slotNumber) {
        connection = DatabaseConnection.getConnection();
        sql = "SELECT * FROM PARKING_SLOT WHERE SlotNumber = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, slotNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new ParkingSlot(
                        resultSet.getString("SlotNumber"),
                        resultSet.getBoolean("Availability"),
                        resultSet.getString("ParkingID"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding parking slot by number", e);
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return null;
    }



    // Maintain backward compatibility for getParkingSlotById
    @Deprecated
    public ParkingSlot getParkingSlotById(String slotNumber) {
        // Use the new method with the slotNumber directly
        return findParkingSlotByNumber(slotNumber);
    }

    // Get parking slots by parking space ID
    public List<ParkingSlot> getParkingSlotsByParkingId(String parkingID) {
        List<ParkingSlot> parkingSlots = new ArrayList<>();
        connection = DatabaseConnection.getConnection();
        sql = "SELECT * FROM PARKING_SLOT WHERE ParkingID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, parkingID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ParkingSlot slot = new ParkingSlot(
                        resultSet.getString("SlotNumber"),
                        resultSet.getBoolean("Availability"),
                        resultSet.getString("ParkingID"));
                parkingSlots.add(slot);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting parking slots by parking ID", e);
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return parkingSlots;
    }

    // Delete parking slot by SlotNumber
    public boolean deleteParkingSlot(String slotNumber) {
        connection = DatabaseConnection.getConnection();
        sql = "DELETE FROM PARKING_SLOT WHERE SlotNumber = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, slotNumber);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting parking slot", e);
            return false;
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Deletes all parking slots for a specific parking space
     *
     * @param parkingId The ID of the parking space
     * @return true if at least one slot was deleted, false otherwise
     */
    public boolean deleteParkingSlotsByParkingId(String parkingId) {
        connection = DatabaseConnection.getConnection();
        sql = "DELETE FROM PARKING_SLOT WHERE ParkingID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, parkingId);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting parking slots by parking ID", e);
            return false;
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Count occupied parking slots for a specific parking space
     *
     * @param parkingId The ID of the parking space
     * @return Number of occupied slots
     */
    public int getOccupiedSlotCountByParkingId(String parkingId) {
        connection = DatabaseConnection.getConnection();
        sql = "SELECT COUNT(*) FROM PARKING_SLOT WHERE ParkingID = ? AND Availability = false";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, parkingId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting occupied slots by parking ID", e);
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return 0;
    }

    /**
     * Count occupied parking slots for all parking spaces managed by an admin
     *
     * @param adminId The ID of the admin
     * @return Number of occupied slots
     */
    public int getOccupiedSlotCountByAdminId(int adminId) {
        connection = DatabaseConnection.getConnection();
        sql = "SELECT COUNT(*) FROM PARKING_SLOT ps " +
                "JOIN PARKING_SPACE p ON ps.ParkingID = p.ParkingID " +
                "WHERE p.AdminID = ? AND ps.Availability = false";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, adminId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting occupied slots by admin ID", e);
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return 0;
    }

    /**
     * Get the parking ID for a specific slot
     * @param slotNumber The slot number
     * @return Parking ID or null if not found
     */
    public String getParkingIdBySlotNumber(String slotNumber) {
        connection = DatabaseConnection.getConnection();
        sql = "SELECT ParkingID FROM PARKING_SLOT WHERE SlotNumber = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, slotNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("ParkingID");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting parking ID by slot number", e);
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return null;
    }

    /**
     * Get the parking space for a specific slot by slot number
     *
     * @param slotNumber The number of the slot
     * @return ParkingSpace object or null if not found
     */
    public ParkingSpace getParkingSpaceBySlotNumber(String slotNumber) {
        connection = DatabaseConnection.getConnection();
        sql = "SELECT ps.* FROM PARKING_SPACE ps " +
                "JOIN PARKING_SLOT sl ON ps.ParkingID = sl.ParkingID " +
                "WHERE sl.SlotNumber = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, slotNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new ParkingSpace(
                        resultSet.getString("ParkingID"),
                        resultSet.getString("ParkingAddress"),
                        resultSet.getFloat("CostOfParking"),
                        resultSet.getInt("NumberOfSlots"),
                        resultSet.getString("Description"),
                        resultSet.getInt("AdminID")
                );
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting parking space by slot number", e);
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return null;
    }
    /**
     * Update availability of a parking slot
     *
     * @param slotNumber The slot number
     * @param available Whether the slot is available or not
     * @return true if update successful, false otherwise
     */
    public boolean updateSlotAvailability(String slotNumber, boolean available) {
        connection = DatabaseConnection.getConnection();
        sql = "UPDATE PARKING_SLOT SET Availability = ? WHERE SlotNumber = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setBoolean(1, available);
            preparedStatement.setString(2, slotNumber);
            int rowsUpdated = preparedStatement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating slot availability", e);
            return false;
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }
    /**
     * Get total count of parking slots
     *
     * @return Total number of parking slots
     */
    public int getTotalSlotCount() {
        connection = DatabaseConnection.getConnection();
        sql = "SELECT COUNT(*) FROM PARKING_SLOT";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total slot count", e);
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return 0;
    }

    /**
     * Get count of available slots for a parking space
     *
     * @param parkingId The ID of the parking space
     * @return Number of available slots
     */
    public int getAvailableSlotCountByParkingId(String parkingId) {
        connection = DatabaseConnection.getConnection();
        sql = "SELECT COUNT(*) FROM PARKING_SLOT WHERE ParkingID = ? AND Availability = true";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, parkingId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting available slots", e);
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return 0;
    }

    /**
     * Get only available slots for a parking space
     *
     * @param parkingId The ID of the parking space
     * @return List of available parking slots
     */
    public List<ParkingSlot> getAvailableSlotsByParkingId(String parkingId) {
        List<ParkingSlot> parkingSlots = new ArrayList<>();
        connection = DatabaseConnection.getConnection();
        sql = "SELECT * FROM PARKING_SLOT WHERE ParkingID = ? AND Availability = true";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, parkingId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ParkingSlot slot = new ParkingSlot(
                        resultSet.getString("SlotNumber"),
                        resultSet.getBoolean("Availability"),
                        resultSet.getString("ParkingID"));
                parkingSlots.add(slot);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting available slots", e);
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return parkingSlots;
    }

    /**
     * Get parking slots by parking space ID (alias for getParkingSlotsByParkingId)
     *
     * @param parkingSpaceId The ID of the parking space
     * @return List of parking slots for the specified parking space
     */
    public List<ParkingSlot> getParkingSlotsByParkingSpaceId(String parkingSpaceId) {
        return getParkingSlotsByParkingId(parkingSpaceId);
    }
    /**
     * Get active reservations for a specific slot
     *
     * @param slotNumber The slot number
     * @return List of active reservations as maps
     */
    public static List<Map<String, Object>> getActiveReservationsForSlot(String slotNumber) {
        List<Map<String, Object>> reservations = new ArrayList<>();
        connection = DatabaseConnection.getConnection();
        sql = "SELECT r.* FROM PARKING_RESERVATION r " +
                "WHERE r.SlotNumber = ? AND r.Status IN ('Processing', 'In Use') " +
                "AND ((r.EndDate > CURRENT_DATE) OR (r.EndDate = CURRENT_DATE AND r.EndTime > CURRENT_TIME))";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, slotNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Map<String, Object> reservation = new HashMap<>();

                // Convert SQL dates and times to LocalDateTime
                LocalDateTime startDateTime = LocalDateTime.of(
                        resultSet.getDate("StartDate").toLocalDate(),
                        resultSet.getTime("StartTime").toLocalTime()
                );

                LocalDateTime endDateTime = LocalDateTime.of(
                        resultSet.getDate("EndDate").toLocalDate(),
                        resultSet.getTime("EndTime").toLocalTime()
                );

                reservation.put("reservationId", resultSet.getInt("ReservationID"));
                reservation.put("startDateTime", startDateTime);
                reservation.put("endDateTime", endDateTime);
                reservation.put("status", resultSet.getString("Status"));
                reservation.put("vehicleId", resultSet.getString("VehicleID"));

                reservations.add(reservation);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting active reservations for slot", e);
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return reservations;
    }
}