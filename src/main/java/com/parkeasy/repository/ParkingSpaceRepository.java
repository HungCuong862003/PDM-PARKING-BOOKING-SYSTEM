package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repository class for managing parking spaces in the database
 */
public class ParkingSpaceRepository {
    private static final Logger LOGGER = Logger.getLogger(ParkingSpaceRepository.class.getName());
    private static final String TABLE_NAME = "PARKING_SPACE";

    /**
     * Add a new parking space and return the generated ID
     *
     * @param parkingSpace The parking space to add
     * @return The ID of the inserted parking space
     * @throws SQLException if a database error occurs
     */
    public int addParkingSpace(ParkingSpace parkingSpace) throws SQLException {
        // Check if AdminID exists in the admin table
        String checkAdminSql = "SELECT COUNT(*) FROM Admin WHERE AdminID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkAdminStatement = connection.prepareStatement(checkAdminSql)) {
            checkAdminStatement.setInt(1, parkingSpace.getAdminID());
            try (ResultSet resultSet = checkAdminStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) == 0) {
                    throw new SQLException(
                            "AdminID " + parkingSpace.getAdminID() + " does not exist in the admin table.");
                }
            }
        }

        String sql = "INSERT INTO " + TABLE_NAME + " (ParkingID, ParkingAddress, CostOfParking, NumberOfSlots, MaxDuration, Description, AdminID) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) { // Use RETURN_GENERATED_KEYS to get the generated ID
            // Set the parameters for the prepared statement
            preparedStatement.setString(1, parkingSpace.getParkingID());
            preparedStatement.setString(2, parkingSpace.getParkingAddress());
            preparedStatement.setFloat(3, parkingSpace.getCostOfParking());
            preparedStatement.setInt(4, parkingSpace.getNumberOfSlots());
            preparedStatement.setObject(5, parkingSpace.getMaxDuration(), java.sql.Types.INTEGER);
            preparedStatement.setString(6, parkingSpace.getDescription());
            preparedStatement.setInt(7, parkingSpace.getAdminID());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating parking space failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the generated ID
                } else {
                    throw new SQLException("Creating parking space failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Update an existing parking space
     *
     * @param parkingSpace The parking space with updated information
     * @return true if update was successful, false otherwise
     */
    public boolean updateParkingSpace(ParkingSpace parkingSpace) {
        String sql = "UPDATE " + TABLE_NAME + " SET ParkingAddress = ?, CostOfParking = ?, NumberOfSlots = ?, MaxDuration = ?, Description = ? WHERE ParkingID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, parkingSpace.getParkingAddress());
            preparedStatement.setFloat(2, parkingSpace.getCostOfParking());
            preparedStatement.setInt(3, parkingSpace.getNumberOfSlots());
            preparedStatement.setInt(4, parkingSpace.getMaxDuration());
            preparedStatement.setString(5, parkingSpace.getDescription());
            preparedStatement.setString(6, parkingSpace.getParkingID());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // Return true if the update was successful
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating parking space: " + parkingSpace.getParkingID(), e);
            return false; // Return false if there was an error
        }
    }

    /**
     * Delete parking space by ID
     *
     * @param spaceId The ID of the parking space to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteParkingSpace(String spaceId) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE ParkingID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, spaceId);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting parking space: " + spaceId, e);
            return false;
        }
    }

    /**
     * Get all parking spaces
     *
     * @return List of all parking spaces
     */
    public List<ParkingSpace> findAll() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<ParkingSpace> parkingSpaces = new ArrayList<>();
            while (resultSet.next()) {
                parkingSpaces.add(extractParkingSpaceFromResultSet(resultSet));
            }
            return parkingSpaces;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all parking spaces", e);
            return new ArrayList<>(); // Return empty list instead of null
        }
    }

    /**
     * Get a parking space by its ID
     *
     * @param parkingID The ID of the parking space
     * @return The parking space or null if not found
     */
    public ParkingSpace getParkingSpaceById(String parkingID) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE ParkingID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, parkingID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return extractParkingSpaceFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting parking space by ID: " + parkingID, e);
        }
        return null; // Return null if no parking space is found
    }

    /**
     * Get all parking spaces
     *
     * @return List of all parking spaces
     */
    public List<ParkingSpace> getAllParkingSpaces() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<ParkingSpace> parkingSpaces = new ArrayList<>();
            while (resultSet.next()) {
                parkingSpaces.add(extractParkingSpaceFromResultSet(resultSet));
            }
            return parkingSpaces;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all parking spaces", e);
            return new ArrayList<>(); // Return empty list instead of null
        }
    }

    /**
     * Get all parking spaces managed by a specific admin
     *
     * @param adminID The ID of the admin
     * @return List of parking spaces managed by the admin
     */
    public List<ParkingSpace> getParkingSpacesByAdminId(int adminID) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE AdminID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, adminID);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<ParkingSpace> parkingSpaces = new ArrayList<>();
            while (resultSet.next()) {
                parkingSpaces.add(extractParkingSpaceFromResultSet(resultSet));
            }
            return parkingSpaces;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting parking spaces by admin ID: " + adminID, e);
            return new ArrayList<>(); // Return empty list instead of null
        }
    }

    /**
     * Get the parking schedule for a specific day of the week
     *
     * @param parkingId The ID of the parking space
     * @param dayOfWeek The day of the week (1-7, where 1 is Sunday)
     * @return A map containing opening and closing times, or null if no schedule exists
     */
    public Map<String, Object> getParkingScheduleForDay(String parkingId, int dayOfWeek) {
        String sql = "SELECT OpeningTime, ClosingTime FROM ParkingSchedule WHERE ParkingID = ? AND DayOfWeek = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, parkingId);
            preparedStatement.setInt(2, dayOfWeek);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Map<String, Object> schedule = new java.util.HashMap<>();

                // Convert SQL Time to LocalTime
                java.sql.Time openingSqlTime = resultSet.getTime("OpeningTime");
                java.sql.Time closingSqlTime = resultSet.getTime("ClosingTime");

                java.time.LocalTime openingTime = openingSqlTime.toLocalTime();
                java.time.LocalTime closingTime = closingSqlTime.toLocalTime();

                schedule.put("openingTime", openingTime);
                schedule.put("closingTime", closingTime);

                return schedule;
            }
            return null; // No schedule found for this day
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting parking schedule for day: " + dayOfWeek, e);
            return null;
        }
    }

    /**
     * Search for parking spaces by address or description
     *
     * @param searchTerm The search term to look for
     * @return List of parking spaces matching the search criteria
     */
    public List<ParkingSpace> searchParkingSpaces(String searchTerm) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " +
                "ParkingAddress LIKE ? OR " +
                "Description LIKE ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            preparedStatement.setString(1, searchPattern);
            preparedStatement.setString(2, searchPattern);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<ParkingSpace> parkingSpaces = new ArrayList<>();

            while (resultSet.next()) {
                parkingSpaces.add(extractParkingSpaceFromResultSet(resultSet));
            }

            return parkingSpaces;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching parking spaces: " + searchTerm, e);
            return new ArrayList<>(); // Return empty list instead of null
        }
    }

    /**
     * Check if a parking space exists by its ID
     *
     * @param parkingId The ID of the parking space
     * @return true if it exists, false otherwise
     */
    public boolean parkingSpaceExists(String parkingId) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE ParkingID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, parkingId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if parking space exists: " + parkingId, e);
        }
        return false;
    }

    /**
     * Check if a parking slot belongs to a specific parking space
     *
     * @param slotNumber The ID of the parking slot
     * @param parkingId The ID of the parking space
     * @return true if the slot belongs to the parking space, false otherwise
     */
    public boolean isSlotInParkingSpace(String slotNumber, String parkingId) {
        String sql = "SELECT COUNT(*) FROM parkingslot WHERE SlotNumber = ? AND ParkingID = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, slotNumber);
            preparedStatement.setString(2, parkingId);

            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if slot is in parking space", e);
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }

        return false;
    }

    /**
     * Get all parking space IDs managed by a specific admin
     *
     * @param adminId The ID of the admin
     * @return List of parking space IDs managed by the admin
     */
    public List<String> getAllParkingSpaceIdsByAdminId(int adminId) {
        String sql = "SELECT ParkingID FROM " + TABLE_NAME + " WHERE AdminID = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<String> parkingIds = new ArrayList<>();

        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, adminId);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                parkingIds.add(resultSet.getString("ParkingID"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting parking space IDs by admin ID: " + adminId, e);
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }

        return parkingIds;
    }

    /**
     * Get all parking spaces managed by a specific admin
     *
     * @param adminId The ID of the admin
     * @return List of parking spaces managed by the admin
     */
    public List<ParkingSpace> getAllParkingSpacesByAdminId(int adminId) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE AdminID = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<ParkingSpace> parkingSpaces = new ArrayList<>();

        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, adminId);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                parkingSpaces.add(extractParkingSpaceFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting parking spaces by admin ID: " + adminId, e);
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }

        return parkingSpaces;
    }

    /**
     * Get the admin ID for a specific parking space
     *
     * @param parkingId The ID of the parking space
     * @return The admin ID or -1 if not found
     */
    public int getAdminIdByParkingId(String parkingId) {
        String sql = "SELECT AdminID FROM " + TABLE_NAME + " WHERE ParkingID = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, parkingId);

            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("AdminID");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting admin ID by parking ID: " + parkingId, e);
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }

        return -1;
    }

    /**
     * Helper method to extract a ParkingSpace object from a ResultSet
     *
     * @param resultSet The ResultSet to extract from
     * @return The extracted ParkingSpace object
     * @throws SQLException if a database error occurs
     */
    private ParkingSpace extractParkingSpaceFromResultSet(ResultSet resultSet) throws SQLException {
        return new ParkingSpace(
                resultSet.getString("ParkingID"),
                resultSet.getString("ParkingAddress"),
                resultSet.getFloat("CostOfParking"),
                resultSet.getInt("NumberOfSlots"),
                resultSet.getInt("MaxDuration"),
                resultSet.getString("Description"),
                resultSet.getInt("AdminID")
        );
    }
}