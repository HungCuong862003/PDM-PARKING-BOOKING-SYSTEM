package main.java.com.parkeasy.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import main.java.com.parkeasy.model.ParkingSchedule;
import main.java.com.parkeasy.util.DatabaseConnection;

/**
 * Repository class for managing ParkingSchedule entities in the database.
 * Provides CRUD operations for parking schedules.
 */
public class ParkingScheduleRepository {

    /**
     * Inserts a new parking schedule into the database.
     *
     * @param parkingSchedule The parking schedule to insert
     * @return true if insertion was successful, false otherwise
     */
    public boolean insertParkingSchedule(ParkingSchedule parkingSchedule) {
        String sql = "INSERT INTO parkingSchedule (ScheduleID, DayOfWeek, OpeningTime, ClosingTime, ParkingID) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, parkingSchedule.getScheduleID());
            preparedStatement.setInt(2, parkingSchedule.getDayOfWeek());
            preparedStatement.setTime(3, parkingSchedule.getOpeningTime());
            preparedStatement.setTime(4, parkingSchedule.getClosingTime());
            preparedStatement.setString(5, parkingSchedule.getParkingID());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting parking schedule: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves a parking schedule by its ID.
     *
     * @param scheduleID The ID of the parking schedule to retrieve
     * @return The found ParkingSchedule or null if not found
     */
    public ParkingSchedule getParkingScheduleById(int scheduleID) {
        String sql = "SELECT * FROM parkingSchedule WHERE ScheduleID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, scheduleID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return extractParkingScheduleFromResultSet(resultSet);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving parking schedule by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Retrieves all parking schedules for a specific parking space.
     *
     * @param parkingID The ID of the parking space
     * @return A list of parking schedules for the specified parking space
     */
    public List<ParkingSchedule> getParkingSchedulesByParkingSpaceId(String parkingID) {
        List<ParkingSchedule> parkingSchedules = new ArrayList<>();
        String sql = "SELECT * FROM parkingSchedule WHERE ParkingID = ? ORDER BY DayOfWeek, OpeningTime";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, parkingID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    parkingSchedules.add(extractParkingScheduleFromResultSet(resultSet));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving parking schedules by parking space ID: " + e.getMessage());
            e.printStackTrace();
        }

        return parkingSchedules;
    }

    /**
     * Retrieves the parking schedule for a specific day of the week in a parking space.
     *
     * @param parkingID The ID of the parking space
     * @param dayOfWeek The day of the week (1-7, where 1 is Sunday)
     * @return A list of parking schedules for the specified day and parking space
     */
    public List<ParkingSchedule> getParkingSchedulesByDayOfWeek(String parkingID, int dayOfWeek) {
        List<ParkingSchedule> parkingSchedules = new ArrayList<>();
        String sql = "SELECT * FROM parkingSchedule WHERE ParkingID = ? AND DayOfWeek = ? ORDER BY OpeningTime";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, parkingID);
            preparedStatement.setInt(2, dayOfWeek);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    parkingSchedules.add(extractParkingScheduleFromResultSet(resultSet));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving parking schedules by day of week: " + e.getMessage());
            e.printStackTrace();
        }

        return parkingSchedules;
    }

    /**
     * Deletes a parking schedule by its ID.
     *
     * @param scheduleID The ID of the parking schedule to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteParkingScheduleById(int scheduleID) {
        String sql = "DELETE FROM parkingSchedule WHERE ScheduleID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, scheduleID);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting parking schedule: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates a parking schedule's information.
     *
     * @param scheduleID The ID of the parking schedule to update
     * @param parkingSchedule The updated parking schedule information
     * @return true if update was successful, false otherwise
     */
    public boolean updateParkingSchedule(int scheduleID, ParkingSchedule parkingSchedule) {
        String sql = "UPDATE parkingSchedule SET DayOfWeek = ?, OpeningTime = ?, ClosingTime = ?, ParkingID = ? WHERE ScheduleID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, parkingSchedule.getDayOfWeek());
            preparedStatement.setTime(2, parkingSchedule.getOpeningTime());
            preparedStatement.setTime(3, parkingSchedule.getClosingTime());
            preparedStatement.setString(4, parkingSchedule.getParkingID());
            preparedStatement.setInt(5, scheduleID);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating parking schedule: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates opening and closing times for a specific schedule.
     *
     * @param scheduleID The ID of the parking schedule
     * @param openingTime The new opening time
     * @param closingTime The new closing time
     * @return true if update was successful, false otherwise
     */
    public boolean updateParkingScheduleTimes(int scheduleID, Time openingTime, Time closingTime) {
        String sql = "UPDATE parkingSchedule SET OpeningTime = ?, ClosingTime = ? WHERE ScheduleID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setTime(1, openingTime);
            preparedStatement.setTime(2, closingTime);
            preparedStatement.setInt(3, scheduleID);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating parking schedule times: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes all schedules for a specific parking space.
     *
     * @param parkingID The ID of the parking space
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteAllSchedulesForParkingSpace(String parkingID) {
        String sql = "DELETE FROM parkingSchedule WHERE ParkingID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, parkingID);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting all schedules for parking space: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a specific day's schedule for a parking space.
     *
     * @param parkingID The ID of the parking space
     * @param dayOfWeek The day of the week to delete (1-7, where 1 is Sunday)
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteScheduleForDay(String parkingID, int dayOfWeek) {
        String sql = "DELETE FROM parkingSchedule WHERE ParkingID = ? AND DayOfWeek = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, parkingID);
            preparedStatement.setInt(2, dayOfWeek);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting schedule for day: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if a parking space has any schedules.
     *
     * @param parkingID The ID of the parking space
     * @return true if schedules exist, false otherwise
     */
    public boolean hasSchedules(String parkingID) {
        String sql = "SELECT COUNT(*) FROM parkingSchedule WHERE ParkingID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, parkingID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking if parking space has schedules: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Retrieves all parking schedules from the database.
     *
     * @return A list of all parking schedules
     */
    public List<ParkingSchedule> getAllParkingSchedules() {
        List<ParkingSchedule> parkingSchedules = new ArrayList<>();
        String sql = "SELECT * FROM parkingSchedule ORDER BY ParkingID, DayOfWeek, OpeningTime";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                parkingSchedules.add(extractParkingScheduleFromResultSet(resultSet));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all parking schedules: " + e.getMessage());
            e.printStackTrace();
        }

        return parkingSchedules;
    }

    /**
     * Creates a default weekly schedule for a parking space (7 days a week, 24 hours).
     *
     * @param parkingID The ID of the parking space
     * @return true if creation was successful, false otherwise
     */
    public boolean createDefaultWeeklySchedule(String parkingID) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Start a transaction
            connection.setAutoCommit(false);
            boolean success = true;
            String sql = "INSERT INTO parkingSchedule (DayOfWeek, OpeningTime, ClosingTime, ParkingID) VALUES (?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                // Create a schedule for each day of the week (1-7, Sunday through Saturday)
                for (int day = 1; day <= 7; day++) {
                    preparedStatement.setInt(1, day);
                    preparedStatement.setTime(2, Time.valueOf("00:00:00")); // 12 AM opening
                    preparedStatement.setTime(3, Time.valueOf("23:59:59")); // 11:59 PM closing
                    preparedStatement.setString(4, parkingID);

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected <= 0) {
                        success = false;
                        break;
                    }
                }

                if (success) {
                    connection.commit();
                    return true;
                } else {
                    connection.rollback();
                    return false;
                }
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Error creating default weekly schedule: " + e.getMessage());
                e.printStackTrace();
                return false;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Helper method to extract a ParkingSchedule object from a ResultSet.
     *
     * @param resultSet The ResultSet containing parking schedule data
     * @return A ParkingSchedule object
     * @throws SQLException if there's an error accessing the ResultSet
     */
    private ParkingSchedule extractParkingScheduleFromResultSet(ResultSet resultSet) throws SQLException {
        return new ParkingSchedule(
                resultSet.getInt("ScheduleID"),
                resultSet.getInt("DayOfWeek"),
                resultSet.getTime("OpeningTime"),
                resultSet.getTime("ClosingTime"),
                resultSet.getString("ParkingID")
        );
    }
}