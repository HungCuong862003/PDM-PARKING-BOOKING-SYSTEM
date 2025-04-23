package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ReservationRepository {
    private Connection databaseConnection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private String sql;

    public void save(Reservation reservation) {
        databaseConnection = DatabaseConnection.getConnection();
        sql = "INSERT INTO reservation (reservationID, startDate, endDate, startTime, endTime, createdAt, status, vehicleID, slotID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            preparedStatement = databaseConnection.prepareStatement(sql);
            preparedStatement.setInt(1, reservation.getReservationID());
            preparedStatement.setDate(2, reservation.getStartDate());
            preparedStatement.setDate(3, reservation.getEndDate());
            preparedStatement.setTime(4, reservation.getStartTime());
            preparedStatement.setTime(5, reservation.getEndTime());
            preparedStatement.setTimestamp(6, reservation.getCreatedAt());
            preparedStatement.setString(7, reservation.getStatus());
            preparedStatement.setString(8, reservation.getVehicleID());
            preparedStatement.setInt(9, reservation.getSlotID());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving reservation: " + e.getMessage());
        } finally {
            DatabaseConnection.closePreparedStatement(preparedStatement);
            DatabaseConnection.closeConnection(databaseConnection);
        }
    }

    public void deleteById(int reservationID) {
        databaseConnection = DatabaseConnection.getConnection();
        sql = "DELETE FROM reservation WHERE reservationID = ?";
        try {
            preparedStatement = databaseConnection.prepareStatement(sql);
            preparedStatement.setInt(1, reservationID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting reservation: " + e.getMessage());
        } finally {
            DatabaseConnection.closePreparedStatement(preparedStatement);
            DatabaseConnection.closeConnection(databaseConnection);
        }
    }

    public Optional<Reservation> getById(int reservationID) {
        databaseConnection = DatabaseConnection.getConnection();
        sql = "SELECT * FROM reservation WHERE reservationID = ?";
        Reservation reservation = null;
        try {
            preparedStatement = databaseConnection.prepareStatement(sql);
            preparedStatement.setInt(1, reservationID);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                reservation = new Reservation(
                        resultSet.getInt("reservationID"),
                        resultSet.getDate("startDate"),
                        resultSet.getDate("endDate"),
                        resultSet.getTime("startTime"),
                        resultSet.getTime("endTime"),
                        resultSet.getTimestamp("createdAt"),
                        resultSet.getString("status"),
                        resultSet.getString("vehicleID"),
                        resultSet.getInt("slotID"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching reservation: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closePreparedStatement(preparedStatement);
            DatabaseConnection.closeConnection(databaseConnection);
        }
        return Optional.ofNullable(reservation);
    }
}