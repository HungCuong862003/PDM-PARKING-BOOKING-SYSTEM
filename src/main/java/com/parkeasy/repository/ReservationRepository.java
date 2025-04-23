package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationRepository {
    // insert a new reservation into the database
    public void insertReservation(Reservation reservation) {
        String sql = "INSERT INTO reservation (ReservationID, StartDate, EndDate, StartTime, EndTime, CreatedAt, Status, VehicleID, SlotID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
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
            e.printStackTrace();
        }
    }

    // get a reservation by its ID
    public Reservation getReservationById(int reservationID) {
        String sql = "SELECT * FROM reservation WHERE ReservationID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, reservationID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Reservation(resultSet.getInt("ReservationID"), resultSet.getDate("StartDate"),
                        resultSet.getDate("EndDate"), resultSet.getTime("StartTime"), resultSet.getTime("EndTime"),
                        resultSet.getTimestamp("CreatedAt"), resultSet.getString("Status"),
                        resultSet.getString("VehicleID"), resultSet.getInt("SlotID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // get all reservations by vehicle ID
    public List<Reservation> getReservationsByVehicleId(String vehicleID) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservation WHERE VehicleID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, vehicleID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                reservations.add(new Reservation(resultSet.getInt("ReservationID"), resultSet.getDate("StartDate"),
                        resultSet.getDate("EndDate"), resultSet.getTime("StartTime"), resultSet.getTime("EndTime"),
                        resultSet.getTimestamp("CreatedAt"), resultSet.getString("Status"),
                        resultSet.getString("VehicleID"), resultSet.getInt("SlotID")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    // get all reservations by parking slot ID
    public List<Reservation> getReservationsByParkingSlotId(int slotID) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservation WHERE SlotID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, slotID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                reservations.add(new Reservation(resultSet.getInt("ReservationID"), resultSet.getDate("StartDate"),
                        resultSet.getDate("EndDate"), resultSet.getTime("StartTime"), resultSet.getTime("EndTime"),
                        resultSet.getTimestamp("CreatedAt"), resultSet.getString("Status"),
                        resultSet.getString("VehicleID"), resultSet.getInt("SlotID")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    // delete a reservation by its ID
    public void deleteReservationById(int reservationID) {
        String sql = "DELETE FROM reservation WHERE ReservationID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, reservationID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // update a reservation by its ID
    public void updateReservationById(int reservationID, Reservation reservation) {
        String sql = "UPDATE reservation SET StartDate = ?, EndDate = ?, StartTime = ?, EndTime = ?, CreatedAt = ?, Status = ?, VehicleID = ?, SlotID = ? WHERE ReservationID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDate(1, reservation.getStartDate());
            preparedStatement.setDate(2, reservation.getEndDate());
            preparedStatement.setTime(3, reservation.getStartTime());
            preparedStatement.setTime(4, reservation.getEndTime());
            preparedStatement.setTimestamp(5, reservation.getCreatedAt());
            preparedStatement.setString(6, reservation.getStatus());
            preparedStatement.setString(7, reservation.getVehicleID());
            preparedStatement.setInt(8, reservation.getSlotID());
            preparedStatement.setInt(9, reservationID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}