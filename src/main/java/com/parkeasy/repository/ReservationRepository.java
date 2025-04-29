package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservationRepository {
    private static final Logger LOGGER = Logger.getLogger(ReservationRepository.class.getName());
    private static final String TABLE_NAME = "PARKING_RESERVATION";

    /**
     * Insert a new reservation into the database
     *
     * @param reservation The reservation to insert
     * @return ID of the inserted reservation or 0 if failed
     */
    public int insertReservation(Reservation reservation) {
        String sql = "INSERT INTO " + TABLE_NAME + " (ReservationID, StartDate, EndDate, StartTime, EndTime, CreatedAt, Status, VehicleID, SlotNumber) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, reservation.getReservationID());
            preparedStatement.setDate(2, reservation.getStartDate());
            preparedStatement.setDate(3, reservation.getEndDate());
            preparedStatement.setTime(4, reservation.getStartTime());
            preparedStatement.setTime(5, reservation.getEndTime());
            preparedStatement.setTimestamp(6, reservation.getCreatedAt());
            preparedStatement.setString(7, reservation.getStatus());
            preparedStatement.setString(8, reservation.getVehicleID());
            preparedStatement.setString(9, reservation.getSlotNumber());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting reservation", e);
        }
        return 0;
    }


    /**
     * Get a reservation by its ID
     *
     * @param reservationID The ID of the reservation
     * @return The reservation or null if not found
     */
    public Reservation getReservationById(int reservationID) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE ReservationID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, reservationID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return extractReservationFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting reservation by ID", e);
        }
        return null;
    }

    /**
     * Get all reservations by vehicle ID
     *
     * @param vehicleID The ID of the vehicle
     * @return List of reservations for the vehicle
     */
    public List<Reservation> getReservationsByVehicleId(String vehicleID) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE VehicleID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, vehicleID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                reservations.add(extractReservationFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting reservations by vehicle ID", e);
        }
        return reservations;
    }

    /**
     * Get all reservations by parking slot number
     *
     * @param slotNumber The number of the parking slot
     * @return List of reservations for the slot
     */
    public List<Reservation> getReservationsByParkingSlotNumber(String slotNumber) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE SlotNumber = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, slotNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                reservations.add(extractReservationFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting reservations by slot number", e);
        }
        return reservations;
    }

    /**
     * Delete a reservation by its ID
     *
     * @param reservationID The ID of the reservation to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteReservationById(int reservationID) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE ReservationID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, reservationID);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting reservation by ID", e);
        }
        return false;
    }

    /**
     * Update a reservation by its ID
     *
     * @param reservationID The ID of the reservation to update
     * @param reservation The updated reservation data
     * @return true if successful, false otherwise
     */
    public boolean updateReservationById(int reservationID, Reservation reservation) {
        String sql = "UPDATE " + TABLE_NAME + " SET StartDate = ?, EndDate = ?, StartTime = ?, EndTime = ?, CreatedAt = ?, Status = ?, VehicleID = ?, SlotNumber = ? WHERE ReservationID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDate(1, reservation.getStartDate());
            preparedStatement.setDate(2, reservation.getEndDate());
            preparedStatement.setTime(3, reservation.getStartTime());
            preparedStatement.setTime(4, reservation.getEndTime());
            preparedStatement.setTimestamp(5, reservation.getCreatedAt());
            preparedStatement.setString(6, reservation.getStatus());
            preparedStatement.setString(7, reservation.getVehicleID());
            preparedStatement.setString(8, reservation.getSlotNumber());
            preparedStatement.setInt(9, reservationID);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating reservation by ID", e);
        }
        return false;
    }

    /**
     * Gets the most popular parking space (most reservations) within a time period
     *
     * @param startDate Start date of the period
     * @param endDate End date of the period
     * @return Name of the most popular parking space
     */
    public String getMostPopularParkingSpace(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT ps.ParkingAddress, COUNT(r.ReservationID) as ReservationCount " +
                "FROM " + TABLE_NAME + " r " +
                "JOIN parkingslot s ON r.SlotNumber = s.SlotNumber " +
                "JOIN parkingspace ps ON s.ParkingID = ps.ParkingID " +
                "WHERE r.CreatedAt BETWEEN ? AND ? " +
                "GROUP BY ps.ParkingID " +
                "ORDER BY ReservationCount DESC " +
                "LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setTimestamp(1, Timestamp.valueOf(startDate));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(endDate));

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("ParkingAddress");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting most popular parking space", e);
        }

        return "N/A";
    }

    /**
     * Gets the most popular parking space managed by a specific admin
     *
     * @param adminId The ID of the admin
     * @param startDate Start date of the period
     * @param endDate End date of the period
     * @return Name of the most popular parking space
     */
    public String getMostPopularParkingSpaceByAdminId(int adminId, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT ps.ParkingAddress, COUNT(r.ReservationID) as ReservationCount " +
                "FROM " + TABLE_NAME + " r " +
                "JOIN parkingslot s ON r.SlotNumber = s.SlotNumber " +
                "JOIN parkingspace ps ON s.ParkingID = ps.ParkingID " +
                "WHERE r.CreatedAt BETWEEN ? AND ? " +
                "AND ps.AdminID = ? " +
                "GROUP BY ps.ParkingID " +
                "ORDER BY ReservationCount DESC " +
                "LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setTimestamp(1, Timestamp.valueOf(startDate));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(endDate));
            preparedStatement.setInt(3, adminId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("ParkingAddress");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting most popular parking space by admin ID", e);
        }

        return "N/A";
    }

    /**
     * Gets recent reservations for specific parking spaces
     *
     * @param parkingIds List of parking space IDs
     * @param limit Maximum number of reservations to return
     * @return List of recent reservations
     */
    public List<Reservation> getRecentReservationsByParkingIds(List<String> parkingIds, int limit) {
        List<Reservation> reservations = new ArrayList<>();

        if (parkingIds == null || parkingIds.isEmpty()) {
            return reservations;
        }

        // Building the SQL with placeholders for the IN clause
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < parkingIds.size(); i++) {
            placeholders.append("?");
            if (i < parkingIds.size() - 1) {
                placeholders.append(",");
            }
        }

        String sql = "SELECT r.* FROM " + TABLE_NAME + " r " +
                "JOIN parkingslot s ON r.SlotNumber = s.SlotNumber " +
                "WHERE s.ParkingID IN (" + placeholders.toString() + ") " +
                "ORDER BY r.CreatedAt DESC " +
                "LIMIT ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Set the parking IDs in the IN clause
            for (int i = 0; i < parkingIds.size(); i++) {
                preparedStatement.setString(i + 1, parkingIds.get(i));
            }

            // Set the limit parameter
            preparedStatement.setInt(parkingIds.size() + 1, limit);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                reservations.add(extractReservationFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting recent reservations by parking IDs", e);
        }

        return reservations;
    }

    /**
     * Checks if a vehicle has any active reservations
     *
     * @param vehicleID The ID of the vehicle to check
     * @return true if the vehicle has active reservations, false otherwise
     */
    public static boolean hasActiveReservations(String vehicleID) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE VehicleID = ? AND Status IN ('CONFIRMED', 'ACTIVE', 'PENDING')";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, vehicleID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // If count is greater than 0, vehicle has active reservations
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking for active reservations for vehicle: " + vehicleID, e);
        }

        // In case of error, assume there are active reservations to be safe
        return true;
    }

    /**
     * Gets all reservations for a specific parking space within a date range
     *
     * @param parkingId The ID of the parking space
     * @param startDate The start date
     * @param endDate The end date
     * @return List of reservations matching the criteria
     */
    public List<Reservation> getReservationsByParkingIdAndDateRange(String parkingId, Date startDate, Date endDate) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.* FROM " + TABLE_NAME + " r " +
                "JOIN parkingslot ps ON r.SlotNumber = ps.SlotNumber " +
                "WHERE ps.ParkingID = ? " +
                "AND ((r.StartDate BETWEEN ? AND ?) OR (r.EndDate BETWEEN ? AND ?))";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, parkingId);

            // Convert Java util Date to SQL Date
            java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
            java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());

            preparedStatement.setDate(2, sqlStartDate);
            preparedStatement.setDate(3, sqlEndDate);
            preparedStatement.setDate(4, sqlStartDate);
            preparedStatement.setDate(5, sqlEndDate);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                reservations.add(extractReservationFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting reservations by parking ID and date range: " + parkingId, e);
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }

        return reservations;
    }

    /**
     * Gets all reservations for a specific user
     *
     * @param userId The ID of the user
     * @return List of reservations for the user
     */
    public List<Reservation> getReservationsByUserId(int userId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.* FROM " + TABLE_NAME + " r " +
                "JOIN vehicle v ON r.VehicleID = v.VehicleID " +
                "WHERE v.UserID = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                reservations.add(extractReservationFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting reservations by user ID: " + userId, e);
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }

        return reservations;
    }

    /**
     * Gets count of active reservations for a specific user
     *
     * @param userId The ID of the user
     * @return Count of active reservations
     */
    public int getActiveReservationCountByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " r " +
                "JOIN vehicle v ON r.VehicleID = v.VehicleID " +
                "WHERE v.UserID = ? AND r.Status IN ('CONFIRMED', 'ACTIVE', 'PENDING')";

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
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting active reservation count by user ID: " + userId, e);
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }

        return 0;
    }


    /**
     * Helper method to extract a Reservation object from a ResultSet
     *
     * @param resultSet The ResultSet to extract from
     * @return The extracted Reservation object
     * @throws SQLException if a database error occurs
     */
    private Reservation extractReservationFromResultSet(ResultSet resultSet) throws SQLException {
        return new Reservation(
                resultSet.getInt("ReservationID"),
                resultSet.getDate("StartDate"),
                resultSet.getDate("EndDate"),
                resultSet.getTime("StartTime"),
                resultSet.getTime("EndTime"),
                resultSet.getTimestamp("CreatedAt"),
                resultSet.getString("Status"),
                resultSet.getString("VehicleID"),
                resultSet.getString("SlotNumber")
        );
    }
}