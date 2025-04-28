package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing Reservation entities in the database.
 * Provides CRUD operations for reservations.
 */
public class ReservationRepository {

    /**
     * Inserts a new reservation into the database.
     *
     * @param reservation The reservation to insert
     * @return true if insertion was successful, false otherwise
     */
    public boolean insertReservation(Reservation reservation) {
        String query = "INSERT INTO PARKING_RESERVATION (VehicleID, SlotID, StartDate, StartTime, " +
                "EndDate, EndTime, Status, CreatedAt, UserID) VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, reservation.getVehicleID());
            pstmt.setInt(2, reservation.getSlotID());
            pstmt.setString(3, reservation.getStartDate());
            pstmt.setString(4, reservation.getStartTime());
            pstmt.setString(5, reservation.getEndDate());
            pstmt.setString(6, reservation.getEndTime());
            pstmt.setString(7, reservation.getStatus());
            pstmt.setInt(8, reservation.getUserID());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        reservation.setReservationID(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting reservation: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Retrieves a reservation by its ID.
     *
     * @param reservationID The ID of the reservation to retrieve
     * @return The found Reservation or null if not found
     */
    public Reservation getReservationById(int reservationID) {
        String query = "SELECT r.*, v.UserID FROM PARKING_RESERVATION r " +
                "JOIN VEHICLE v ON r.VehicleID = v.VehicleID " +
                "WHERE r.ReservationID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, reservationID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReservation(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving reservation by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Retrieves all reservations for a specific vehicle.
     *
     * @param vehicleID The ID of the vehicle
     * @return A list of reservations for the specified vehicle
     */
    public List<Reservation> getReservationsByVehicleId(String vehicleID) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, v.UserID FROM PARKING_RESERVATION r " +
                "JOIN VEHICLE v ON r.VehicleID = v.VehicleID " +
                "WHERE r.VehicleID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, vehicleID);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving reservations by vehicle ID: " + e.getMessage());
            e.printStackTrace();
        }

        return reservations;
    }

    /**
     * Retrieves all reservations for a specific parking slot.
     *
     * @param slotID The ID of the parking slot
     * @return A list of reservations for the specified parking slot
     */
    public List<Reservation> getReservationsByParkingSlotId(int slotID) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, v.UserID FROM PARKING_RESERVATION r " +
                "JOIN VEHICLE v ON r.VehicleID = v.VehicleID " +
                "WHERE r.SlotID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, slotID);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving reservations by parking slot ID: " + e.getMessage());
            e.printStackTrace();
        }

        return reservations;
    }

    /**
     * Retrieves all reservations made by a specific user.
     *
     * @param userID The ID of the user
     * @return A list of reservations made by the specified user
     */
    public List<Reservation> getReservationsByUserId(int userID) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, v.UserID FROM PARKING_RESERVATION r " +
                "JOIN VEHICLE v ON r.VehicleID = v.VehicleID " +
                "WHERE v.UserID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, userID);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving reservations by user ID: " + e.getMessage());
            e.printStackTrace();
        }

        return reservations;
    }

    /**
     * Retrieves reservations within a specific date range for a parking slot.
     *
     * @param slotID    The ID of the parking slot
     * @param startDate The start date of the range
     * @param endDate   The end date of the range
     * @return A list of reservations in the specified date range for the slot
     */
    public List<Reservation> getReservationsByDateRange(int slotID, String startDate, String endDate) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, v.UserID FROM PARKING_RESERVATION r " +
                "JOIN VEHICLE v ON r.VehicleID = v.VehicleID " +
                "WHERE r.SlotID = ? AND " +
                "((r.StartDate BETWEEN ? AND ?) OR (r.EndDate BETWEEN ? AND ?)) AND " +
                "r.Status = 'ACTIVE'";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, slotID);
            pstmt.setString(2, startDate);
            pstmt.setString(3, endDate);
            pstmt.setString(4, startDate);
            pstmt.setString(5, endDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving reservations by date range: " + e.getMessage());
            e.printStackTrace();
        }

        return reservations;
    }

    /**
     * Deletes a reservation by its ID.
     *
     * @param reservationID The ID of the reservation to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteReservationById(int reservationID) {
        String query = "DELETE FROM PARKING_RESERVATION WHERE ReservationID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, reservationID);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting reservation: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Updates an existing reservation.
     *
     * @param reservationID The ID of the reservation to update
     * @param reservation   The updated reservation information
     * @return true if update was successful, false otherwise
     */
    public boolean updateReservationById(int reservationID, Reservation reservation) {
        String query = "UPDATE PARKING_RESERVATION SET VehicleID = ?, SlotID = ?, " +
                "StartDate = ?, StartTime = ?, EndDate = ?, EndTime = ?, Status = ?, UserID = ? " +
                "WHERE ReservationID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, reservation.getVehicleID());
            pstmt.setInt(2, reservation.getSlotID());
            pstmt.setString(3, reservation.getStartDate());
            pstmt.setString(4, reservation.getStartTime());
            pstmt.setString(5, reservation.getEndDate());
            pstmt.setString(6, reservation.getEndTime());
            pstmt.setString(7, reservation.getStatus());
            pstmt.setInt(8, reservation.getUserID());
            pstmt.setInt(9, reservationID);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating reservation: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Checks if a parking slot is available for a specific time period.
     *
     * @param slotID    The ID of the parking slot
     * @param startDate The start date of the period
     * @param startTime The start time of the period
     * @param endDate   The end date of the period
     * @param endTime   The end time of the period
     * @return true if the slot is available, false otherwise
     */
    public boolean isSlotAvailability(int slotID, String startDate, String startTime, String endDate, String endTime) {
        String query = "SELECT COUNT(*) FROM PARKING_RESERVATION WHERE SlotID = ? AND Status = 'ACTIVE' AND " +
                "((? BETWEEN StartDate AND EndDate) OR " +
                "(? BETWEEN StartDate AND EndDate) OR " +
                "(StartDate BETWEEN ? AND ?) OR " +
                "(EndDate BETWEEN ? AND ?))";

        if (startDate.equals(endDate)) {
            // If same day, also check time overlaps
            query += " AND ((? = StartDate AND ? = EndDate AND " +
                    "((? BETWEEN StartTime AND EndTime) OR " +
                    "(? BETWEEN StartTime AND EndTime) OR " +
                    "(StartTime BETWEEN ? AND ?) OR " +
                    "(EndTime BETWEEN ? AND ?))))";
        }

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, slotID);
            pstmt.setString(2, startDate);
            pstmt.setString(3, endDate);
            pstmt.setString(4, startDate);
            pstmt.setString(5, endDate);
            pstmt.setString(6, startDate);
            pstmt.setString(7, endDate);

            if (startDate.equals(endDate)) {
                pstmt.setString(8, startDate);
                pstmt.setString(9, endDate);
                pstmt.setString(10, startTime);
                pstmt.setString(11, endTime);
                pstmt.setString(12, startTime);
                pstmt.setString(13, endTime);
                pstmt.setString(14, startTime);
                pstmt.setString(15, endTime);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count == 0; // Available if no overlapping reservations
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking slot availability: " + e.getMessage());
            e.printStackTrace();
        }

        return false; // Default to unavailable if there's an error
    }

    /**
     * Retrieves all reservations with a specific status.
     *
     * @param status The status to filter by
     * @return A list of reservations with the specified status
     */
    public List<Reservation> getListOfReservationsByStatus(String status) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, v.UserID FROM PARKING_RESERVATION r " +
                "JOIN VEHICLE v ON r.VehicleID = v.VehicleID " +
                "WHERE r.Status = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving reservations by status: " + e.getMessage());
            e.printStackTrace();
        }

        return reservations;
    }

    /**
     * Retrieves all reservations from the database.
     *
     * @return A list of all reservations
     */
    public List<Reservation> getListOfAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, v.UserID FROM PARKING_RESERVATION r " +
                "JOIN VEHICLE v ON r.VehicleID = v.VehicleID";

        try (Connection connection = DatabaseConnection.getConnection();
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all reservations: " + e.getMessage());
            e.printStackTrace();
        }

        return reservations;
    }

    /**
     * Updates the status of a reservation.
     *
     * @param reservationID The ID of the reservation
     * @param status        The new status
     * @return true if update was successful, false otherwise
     */
    public boolean updateReservationStatus(int reservationID, String status) {
        String query = "UPDATE PARKING_RESERVATION SET Status = ? WHERE ReservationID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, reservationID);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating reservation status: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Helper method to map ResultSet to Reservation object.
     *
     * @param rs The ResultSet containing reservation data
     * @return A Reservation object
     * @throws SQLException if there's an error accessing the ResultSet
     */
    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        int reservationID = rs.getInt("ReservationID");
        java.sql.Date startDate = rs.getDate("StartDate");
        java.sql.Date endDate = rs.getDate("EndDate");
        java.sql.Time startTime = rs.getTime("StartTime");
        java.sql.Time endTime = rs.getTime("EndTime");
        java.sql.Timestamp createdAt = rs.getTimestamp("CreatedAt");
        String status = rs.getString("Status");
        String vehicleID = rs.getString("VehicleID");
        int slotID = rs.getInt("SlotID");
        int userID = rs.getInt("UserID");

        Reservation reservation = new Reservation(
                reservationID,
                startDate,
                endDate,
                startTime,
                endTime,
                createdAt,
                status,
                vehicleID,
                slotID,
                userID);

        return reservation;
    }
}