package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.ParkingSchedule;
import main.java.com.parkeasy.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ParkingScheduleRepository {
    private Connection databaseConnection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private String sql;

    public void save(ParkingSchedule parkingSchedule) {
        databaseConnection = DatabaseConnection.getConnection();
        sql = "INSERT INTO PARKING_SCHEDULE (scheduleID, dayOfWeek, openingTime, closingTime, parkingID) VALUES (?, ?, ?, ?, ?)";
        try {
            preparedStatement = databaseConnection.prepareStatement(sql);
            preparedStatement.setInt(1, parkingSchedule.getScheduleID());
            preparedStatement.setInt(2, parkingSchedule.getDayOfWeek());
            preparedStatement.setTime(3, parkingSchedule.getOpeningTime());
            preparedStatement.setTime(4, parkingSchedule.getClosingTime());
            preparedStatement.setString(5, parkingSchedule.getParkingID());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closePreparedStatement(preparedStatement);
            DatabaseConnection.closeConnection(databaseConnection);
        }
    }

    // Method to delete a parking schedule by ID
    public void deleteById(int scheduleID) {
        databaseConnection = DatabaseConnection.getConnection();
        sql = "DELETE FROM PARKING_SCHEDULE WHERE scheduleID = ?";
        try {
            preparedStatement = databaseConnection.prepareStatement(sql);
            preparedStatement.setInt(1, scheduleID);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closePreparedStatement(preparedStatement);
            DatabaseConnection.closeConnection(databaseConnection);
        }
    }

    // Method to get a parking schedule by ID
    public ParkingSchedule getById(int scheduleID) {
        databaseConnection = DatabaseConnection.getConnection();
        sql = "SELECT * FROM PARKING_SCHEDULE WHERE scheduleID = ?";
        ParkingSchedule parkingSchedule = null;
        try {
            preparedStatement = databaseConnection.prepareStatement(sql);
            preparedStatement.setInt(1, scheduleID);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                parkingSchedule = new ParkingSchedule(
                        resultSet.getInt("scheduleID"),
                        resultSet.getInt("dayOfWeek"),
                        resultSet.getTime("openingTime"),
                        resultSet.getTime("closingTime"),
                        resultSet.getString("parkingID"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closePreparedStatement(preparedStatement);
            DatabaseConnection.closeConnection(databaseConnection);
        }
        return parkingSchedule;
    }
}