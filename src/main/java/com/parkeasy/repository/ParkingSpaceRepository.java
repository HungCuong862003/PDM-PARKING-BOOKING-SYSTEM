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

        String sql = "INSERT INTO " + TABLE_NAME + " (ParkingID, ParkingAddress, CostOfParking, NumberOfSlots, Description, AdminID) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) { // Use RETURN_GENERATED_KEYS to get the generated ID
            // Set the parameters for the prepared statement
            preparedStatement.setString(1, parkingSpace.getParkingID());
            preparedStatement.setString(2, parkingSpace.getParkingAddress());
            preparedStatement.setFloat(3, parkingSpace.getCostOfParking());
            preparedStatement.setInt(4, parkingSpace.getNumberOfSlots());
            preparedStatement.setString(5, parkingSpace.getDescription()); // Fixed index from 6 to 5
            preparedStatement.setInt(6, parkingSpace.getAdminID()); // Fixed index from 7 to 6

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
        String sql = "UPDATE " + TABLE_NAME + " SET ParkingAddress = ?, CostOfParking = ?, NumberOfSlots = ?, Description = ? WHERE ParkingID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, parkingSpace.getParkingAddress());
            preparedStatement.setFloat(2, parkingSpace.getCostOfParking());
            preparedStatement.setInt(3, parkingSpace.getNumberOfSlots());
            preparedStatement.setString(4, parkingSpace.getDescription()); // Fixed index from 5 to 4
            preparedStatement.setString(5, parkingSpace.getParkingID()); // Fixed index from 6 to 5

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
        // First check if parking space has active reservations
        if (hasActiveReservations(spaceId)) {
            LOGGER.log(Level.WARNING, "Cannot delete parking space with active reservations: " + spaceId);
            return false;
        }

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
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return extractParkingSpaceFromResultSet(resultSet);
                }
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
        return findAll(); // Use the existing findAll method
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
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<ParkingSpace> parkingSpaces = new ArrayList<>();
                while (resultSet.next()) {
                    parkingSpaces.add(extractParkingSpaceFromResultSet(resultSet));
                }
                return parkingSpaces;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting parking spaces by admin ID: " + adminID, e);
            return new ArrayList<>(); // Return empty list instead of null
        }
    }

    /**
     * Get the parking schedule for a specific day of the week
     * Note: This method assumes a PARKING_SCHEDULE table exists which is not in the provided schema
     * This method should be reviewed and updated based on actual requirements
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

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
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
            }
            return null; // No schedule found for this day
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting parking schedule for day: " + dayOfWeek, e);
            return null;
        }
    }

    /**
     * Search for parking spaces by address or description with MySQL-specific optimization techniques
     *
     * @param searchTerm The search term to look for
     * @param page The page number (0-based) for pagination
     * @param pageSize The number of results per page
     * @return List of parking spaces matching the search criteria
     */
    public List<ParkingSpace> searchParkingSpaces(String searchTerm, int page, int pageSize) {
        // 1. Determine if we should use full-text search or regular search
        // For short search terms or single words, full-text search might be less effective
        boolean useFullTextSearch = searchTerm.length() > 3 && searchTerm.contains(" ");

        String sql;
        if (useFullTextSearch) {
            // Use MySQL's FULLTEXT search for better performance with multi-word queries
            sql = "SELECT * FROM PARKING_SPACE WHERE " +
                    "MATCH(ParkingAddress, Description) AGAINST(? IN BOOLEAN MODE) " +
                    "ORDER BY MATCH(ParkingAddress, Description) AGAINST(? IN BOOLEAN MODE) DESC " +
                    "LIMIT ? OFFSET ?";
        } else {
            // Use regular LIKE for simple queries
            sql = "SELECT * FROM PARKING_SPACE WHERE " +
                    "ParkingAddress LIKE ? OR Description LIKE ? " +
                    "ORDER BY ParkingAddress " +
                    "LIMIT ? OFFSET ?";
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            if (useFullTextSearch) {
                // Format search term for boolean mode
                String formattedSearch = formatSearchTermForFullText(searchTerm);
                preparedStatement.setString(1, formattedSearch);
                preparedStatement.setString(2, formattedSearch);
                preparedStatement.setInt(3, pageSize);
                preparedStatement.setInt(4, page * pageSize);
            } else {
                // Use wildcards for LIKE search
                String searchPattern = "%" + searchTerm + "%";
                preparedStatement.setString(1, searchPattern);
                preparedStatement.setString(2, searchPattern);
                preparedStatement.setInt(3, pageSize);
                preparedStatement.setInt(4, page * pageSize);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<ParkingSpace> parkingSpaces = new ArrayList<>();

                while (resultSet.next()) {
                    parkingSpaces.add(extractParkingSpaceFromResultSet(resultSet));
                }

                return parkingSpaces;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching parking spaces: " + searchTerm, e);

            // If full-text search failed, try falling back to regular search
            if (useFullTextSearch) {
                LOGGER.info("Falling back to regular search after full-text search failure");
                return searchParkingSpacesWithLike(searchTerm, page, pageSize);
            }

            return new ArrayList<>(); // Return empty list if all attempts failed
        }
    }

    /**
     * Search for parking spaces (non-paginated version for backward compatibility)
     *
     * @param searchTerm The search term to look for
     * @return List of parking spaces matching the search criteria
     */
    public List<ParkingSpace> searchParkingSpaces(String searchTerm) {
        // Call the paginated version with a large page size for backward compatibility
        return searchParkingSpaces(searchTerm, 0, 100);
    }
    /**
     * Fallback method using LIKE for search when full-text search fails
     */
    private List<ParkingSpace> searchParkingSpacesWithLike(String searchTerm, int page, int pageSize) {
        String sql = "SELECT * FROM PARKING_SPACE WHERE " +
                "ParkingAddress LIKE ? OR Description LIKE ? " +
                "ORDER BY ParkingAddress " +
                "LIMIT ? OFFSET ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            preparedStatement.setString(1, searchPattern);
            preparedStatement.setString(2, searchPattern);
            preparedStatement.setInt(3, pageSize);
            preparedStatement.setInt(4, page * pageSize);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<ParkingSpace> parkingSpaces = new ArrayList<>();

                while (resultSet.next()) {
                    parkingSpaces.add(extractParkingSpaceFromResultSet(resultSet));
                }

                return parkingSpaces;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in fallback search: " + searchTerm, e);
            return new ArrayList<>();
        }
    }
    /**
     * Format search term for MySQL full-text search
     */
    private String formatSearchTermForFullText(String searchTerm) {
        // Split the search term into words
        String[] words = searchTerm.trim().split("\\s+");
        StringBuilder formatted = new StringBuilder();

        for (String word : words) {
            // Skip very short words as they might cause performance issues
            if (word.length() <= 2) {
                continue;
            }

            // Add + for required matches and * for prefix matching
            if (formatted.length() > 0) {
                formatted.append(" ");
            }
            formatted.append("+").append(word).append("*");
        }

        return formatted.toString();
    }

    /**
     * Create the necessary indexes for optimizing search operations
     * This should be run during application setup/initialization
     */
    public void createSearchIndexes() {
        // Check if fulltext index already exists to avoid errors
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS " +
                             "WHERE table_schema = DATABASE() " +
                             "AND table_name = 'PARKING_SPACE' " +
                             "AND index_name = 'ft_parking_search'")) {

            ResultSet rs = checkStatement.executeQuery();
            boolean fulltextExists = false;
            if (rs.next()) {
                fulltextExists = rs.getInt(1) > 0;
            }

            // Create MySQL fulltext index if it doesn't exist
            if (!fulltextExists) {
                try (PreparedStatement ftStatement = connection.prepareStatement(
                        "ALTER TABLE PARKING_SPACE " +
                                "ADD FULLTEXT INDEX ft_parking_search(ParkingAddress, Description)")) {
                    ftStatement.execute();
                    LOGGER.info("Created full-text search index on PARKING_SPACE table");
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Could not create fulltext index. Search will use regular indexes.", e);

                    // If creating fulltext index fails, create regular indexes
                    String[] regularIndexes = {
                            "CREATE INDEX IF NOT EXISTS idx_parking_address ON PARKING_SPACE (ParkingAddress(100))",
                            "CREATE INDEX IF NOT EXISTS idx_parking_description ON PARKING_SPACE (Description(100))"
                    };

                    for (String indexSql : regularIndexes) {
                        try (PreparedStatement indexStatement = connection.prepareStatement(indexSql)) {
                            indexStatement.execute();
                            LOGGER.info("Created index: " + indexSql);
                        } catch (SQLException indexEx) {
                            LOGGER.log(Level.SEVERE, "Error creating index: " + indexSql, indexEx);
                        }
                    }
                }
            } else {
                LOGGER.info("Full-text search index already exists on PARKING_SPACE table");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking or creating indexes", e);
        }
    }

    /**
     * Count total number of results for a search query (for pagination)
     * This method supports both full-text and LIKE search depending on the search term
     *
     * @param searchTerm The search term to look for
     * @return Total count of matching parking spaces
     */
    public int countSearchResults(String searchTerm) {
        // Determine if we should use full-text search
        boolean useFullTextSearch = searchTerm.length() > 3 && searchTerm.contains(" ");

        String sql;
        if (useFullTextSearch) {
            sql = "SELECT COUNT(*) FROM PARKING_SPACE WHERE " +
                    "MATCH(ParkingAddress, Description) AGAINST(? IN BOOLEAN MODE)";
        } else {
            sql = "SELECT COUNT(*) FROM PARKING_SPACE WHERE " +
                    "ParkingAddress LIKE ? OR Description LIKE ?";
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            if (useFullTextSearch) {
                preparedStatement.setString(1, formatSearchTermForFullText(searchTerm));
            } else {
                String searchPattern = "%" + searchTerm + "%";
                preparedStatement.setString(1, searchPattern);
                preparedStatement.setString(2, searchPattern);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting search results: " + searchTerm, e);

            // If full-text search failed, try falling back to regular count
            if (useFullTextSearch) {
                return countWithLike(searchTerm);
            }

            return 0;
        }
    }

    /**
     * Fallback method to count results using LIKE
     */
    private int countWithLike(String searchTerm) {
        String sql = "SELECT COUNT(*) FROM PARKING_SPACE WHERE " +
                "ParkingAddress LIKE ? OR Description LIKE ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            preparedStatement.setString(1, searchPattern);
            preparedStatement.setString(2, searchPattern);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in fallback count: " + searchTerm, e);
            return 0;
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
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
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
        String sql = "SELECT COUNT(*) FROM PARKING_SLOT WHERE SlotNumber = ? AND ParkingID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, slotNumber);
            preparedStatement.setString(2, parkingId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if slot is in parking space", e);
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
        List<String> parkingIds = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, adminId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    parkingIds.add(resultSet.getString("ParkingID"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting parking space IDs by admin ID: " + adminId, e);
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
        return getParkingSpacesByAdminId(adminId); // Use the existing method
    }

    /**
     * Get the admin ID for a specific parking space
     *
     * @param parkingId The ID of the parking space
     * @return The admin ID or -1 if not found
     */
    public int getAdminIdByParkingId(String parkingId) {
        String sql = "SELECT AdminID FROM " + TABLE_NAME + " WHERE ParkingID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, parkingId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("AdminID");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting admin ID by parking ID: " + parkingId, e);
        }

        return -1;
    }

    /**
     * Check if a parking space has any active reservations
     *
     * @param parkingId The ID of the parking space
     * @return true if there are active reservations, false otherwise
     */
    public boolean hasActiveReservations(String parkingId) {
        String sql = "SELECT COUNT(*) FROM PARKING_RESERVATION r " +
                "JOIN PARKING_SLOT s ON r.SlotNumber = s.SlotNumber " +
                "WHERE s.ParkingID = ? AND r.Status = 'ACTIVE'";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, parkingId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking active reservations for parking space: " + parkingId, e);
        }

        return false;
    }

    /**
     * Get available slots for a specific parking space
     *
     * @param parkingId The ID of the parking space
     * @return List of available slot numbers
     */
    public List<String> getAvailableSlots(String parkingId) {
        String sql = "SELECT SlotNumber FROM PARKING_SLOT WHERE ParkingID = ? AND Availability = true";
        List<String> availableSlots = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, parkingId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    availableSlots.add(resultSet.getString("SlotNumber"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting available slots for parking space: " + parkingId, e);
        }

        return availableSlots;
    }

    /**
     * Get the number of available slots for a specific parking space
     *
     * @param parkingId The ID of the parking space
     * @return Number of available slots
     */
    public int getAvailableSlotsCount(String parkingId) {
        String sql = "SELECT COUNT(*) FROM PARKING_SLOT WHERE ParkingID = ? AND Availability = true";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, parkingId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting available slots count for parking space: " + parkingId, e);
        }

        return 0;
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
                resultSet.getString("Description"),
                resultSet.getInt("AdminID")
        );
    }
}