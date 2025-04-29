package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.model.ParkingSlot;
import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.repository.AdminRepository;
import main.java.com.parkeasy.repository.ParkingSpaceRepository;
import main.java.com.parkeasy.repository.ParkingSlotRepository;
import main.java.com.parkeasy.repository.UserRepository;
import main.java.com.parkeasy.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for handling admin-specific operations in the ParkEasy system.
 * Provides functionality for managing parking spaces, slots, pricing, and user
 * management.
 */
public class AdminService {

    private final AdminRepository adminRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final UserRepository userRepository;

    // SQL table and column names as constants
    private static final String ADMIN_TABLE = "ADMIN";
    private static final String ADMIN_ID_COLUMN = "AdminID";
    private static final String ADMIN_NAME_COLUMN = "AdminName";
    private static final String EMAIL_COLUMN = "Email";
    private static final String PHONE_COLUMN = "Phone";
    private static final String PASSWORD_COLUMN = "Password";

    private static final String PARKING_SPACE_TABLE = "PARKING_SPACE";
    private static final String PARKING_ID_COLUMN = "ParkingID";
    private static final String PARKING_ADDRESS_COLUMN = "ParkingAddress";
    private static final String COST_OF_PARKING_COLUMN = "CostOfParking";
    private static final String NUMBER_OF_SLOTS_COLUMN = "NumberOfSlots";
    private static final String MAX_DURATION_COLUMN = "MaxDuration";
    private static final String DESCRIPTION_COLUMN = "Description";

    private static final String PARKING_SLOT_TABLE = "PARKING_SLOT";
    private static final String SLOT_ID_COLUMN = "SlotID";
    private static final String SLOT_NUMBER_COLUMN = "SlotNumber";
    private static final String AVAILABILITY_COLUMN = "Availability";

    private static final String USER_TABLE = "USER";
    private static final String USER_ID_COLUMN = "UserID";
    private static final String USER_NAME_COLUMN = "UserName";
    private static final String BALANCE_COLUMN = "Balance";

    private static final String PAYMENT_TABLE = "PAYMENT";
    private static final String AMOUNT_COLUMN = "Amount";
    private static final String PAYMENT_DATE_COLUMN = "PaymentDate";
    private static final String RESERVATION_ID_COLUMN = "ReservationID";

    private static final String PARKING_RESERVATION_TABLE = "PARKING_RESERVATION";
    private static final String STATUS_COLUMN = "Status";

    /**
     * Constructor for AdminService.
     */
    public AdminService() {
        this.adminRepository = new AdminRepository();
        this.parkingSpaceRepository = new ParkingSpaceRepository();
        this.parkingSlotRepository = new ParkingSlotRepository();
        this.userRepository = new UserRepository();
    }

    /**
     * Gets an Admin by their ID.
     *
     * @param adminID The admin's unique identifier
     * @return The Admin object if found, null otherwise
     */
    public Admin getAdminById(int adminID) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM " + ADMIN_TABLE + " WHERE " + ADMIN_ID_COLUMN + " = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, adminID);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return createAdminFromResultSet(resultSet);
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error retrieving admin", e);
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error while creating admin object: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Creates an Admin object from a ResultSet.
     *
     * @param resultSet ResultSet containing admin data
     * @return Admin object
     * @throws SQLException if a database error occurs
     */
    private Admin createAdminFromResultSet(ResultSet resultSet) throws SQLException {
        Admin admin = new Admin();
        admin.setAdminID(resultSet.getInt(ADMIN_ID_COLUMN));
        admin.setAdminName(resultSet.getString(ADMIN_NAME_COLUMN));
        admin.setEmail(resultSet.getString(EMAIL_COLUMN));
        admin.setPhone(resultSet.getString(PHONE_COLUMN));
        admin.setPassword(resultSet.getString(PASSWORD_COLUMN));
        return admin;
    }

    /**
     * Updates an admin's information in the database.
     *
     * @param admin The admin object with updated information
     * @return true if update successful, false otherwise
     */
    public boolean updateAdmin(Admin admin) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE " + ADMIN_TABLE + " SET " +
                    ADMIN_NAME_COLUMN + " = ?, " +
                    EMAIL_COLUMN + " = ?, " +
                    PHONE_COLUMN + " = ?, " +
                    PASSWORD_COLUMN + " = ? WHERE " +
                    ADMIN_ID_COLUMN + " = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, admin.getAdminName());
                statement.setString(2, admin.getEmail());
                statement.setString(3, admin.getPhone());
                statement.setString(4, admin.getPassword());
                statement.setInt(5, admin.getAdminID());

                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            handleSQLException("Error updating admin", e);
        }

        return false;
    }

    /**
     * Creates a new parking space.
     *
     * @param space The parking space to create
     * @return true if creation successful, false otherwise
     */
    public boolean createParkingSpace(ParkingSpace space) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO " + PARKING_SPACE_TABLE +
                    " (" + PARKING_ID_COLUMN + ", " +
                    PARKING_ADDRESS_COLUMN + ", " +
                    COST_OF_PARKING_COLUMN + ", " +
                    NUMBER_OF_SLOTS_COLUMN + ", " +
                    MAX_DURATION_COLUMN + ", " +
                    DESCRIPTION_COLUMN + ", " +
                    ADMIN_ID_COLUMN + ") VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, space.getParkingID());
                statement.setString(2, space.getParkingAddress());
                statement.setBigDecimal(3, space.getCostOfParking());
                statement.setInt(4, space.getNumberOfSlots());
                statement.setInt(5, space.getMaxDuration());
                statement.setString(6, space.getDescription());
                statement.setInt(7, space.getAdminID());

                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            handleSQLException("Error creating parking space", e);
        }

        return false;
    }

    /**
     * Updates an existing parking space.
     *
     * @param space The parking space with updated information
     * @return true if update successful, false otherwise
     */
    public boolean updateParkingSpace(ParkingSpace space) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE " + PARKING_SPACE_TABLE + " SET " +
                    PARKING_ADDRESS_COLUMN + " = ?, " +
                    COST_OF_PARKING_COLUMN + " = ?, " +
                    NUMBER_OF_SLOTS_COLUMN + " = ?, " +
                    MAX_DURATION_COLUMN + " = ?, " +
                    DESCRIPTION_COLUMN + " = ? WHERE " +
                    PARKING_ID_COLUMN + " = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, space.getParkingAddress());
                statement.setBigDecimal(2, space.getCostOfParking());
                statement.setInt(3, space.getNumberOfSlots());
                statement.setInt(4, space.getMaxDuration());
                statement.setString(5, space.getDescription());
                statement.setString(6, space.getParkingID());

                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            handleSQLException("Error updating parking space", e);
        }

        return false;
    }

    /**
     * Deletes a parking space.
     *
     * @param parkingID The ID of the parking space to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteParkingSpace(String parkingID) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // First check if there are any active reservations
            if (hasActiveReservations(connection, parkingID)) {
                return false;
            }

            // Delete associated slots first
            deleteAssociatedSlots(connection, parkingID);

            // Delete parking schedules
            deleteAssociatedSchedules(connection, parkingID);

            // Finally delete the parking space
            return deleteActualParkingSpace(connection, parkingID);
        } catch (SQLException e) {
            handleSQLException("Error deleting parking space", e);
        }

        return false;
    }

    /**
     * Checks if a parking space has active reservations.
     *
     * @param connection Database connection
     * @param parkingID  Parking space ID
     * @return true if has active reservations, false otherwise
     * @throws SQLException if a database error occurs
     */
    private boolean hasActiveReservations(Connection connection, String parkingID) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM " + PARKING_RESERVATION_TABLE + " r " +
                "JOIN " + PARKING_SLOT_TABLE + " s ON r." + SLOT_ID_COLUMN + " = s." + SLOT_ID_COLUMN + " " +
                "WHERE s." + PARKING_ID_COLUMN + " = ? AND r." + STATUS_COLUMN + " = 'ACTIVE'";

        try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
            checkStatement.setString(1, parkingID);

            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    // Has active reservations, cannot delete
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Deletes all slots associated with a parking space.
     *
     * @param connection Database connection
     * @param parkingID  Parking space ID
     * @throws SQLException if a database error occurs
     */
    private void deleteAssociatedSlots(Connection connection, String parkingID) throws SQLException {
        String deleteSlots = "DELETE FROM " + PARKING_SLOT_TABLE + " WHERE " + PARKING_ID_COLUMN + " = ?";
        try (PreparedStatement slotStatement = connection.prepareStatement(deleteSlots)) {
            slotStatement.setString(1, parkingID);
            slotStatement.executeUpdate();
        }
    }

    /**
     * Deletes all schedules associated with a parking space.
     *
     * @param connection Database connection
     * @param parkingID  Parking space ID
     * @throws SQLException if a database error occurs
     */
    private void deleteAssociatedSchedules(Connection connection, String parkingID) throws SQLException {
        String deleteSchedules = "DELETE FROM PARKING_SCHEDULE WHERE " + PARKING_ID_COLUMN + " = ?";
        try (PreparedStatement scheduleStatement = connection.prepareStatement(deleteSchedules)) {
            scheduleStatement.setString(1, parkingID);
            scheduleStatement.executeUpdate();
        }
    }

    /**
     * Deletes the actual parking space record.
     *
     * @param connection Database connection
     * @param parkingID  Parking space ID
     * @return true if deletion successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    private boolean deleteActualParkingSpace(Connection connection, String parkingID) throws SQLException {
        String deleteSpace = "DELETE FROM " + PARKING_SPACE_TABLE + " WHERE " + PARKING_ID_COLUMN + " = ?";
        try (PreparedStatement spaceStatement = connection.prepareStatement(deleteSpace)) {
            spaceStatement.setString(1, parkingID);
            int rowsAffected = spaceStatement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Gets all parking spaces.
     *
     * @return List of all parking spaces
     */
    public List<ParkingSpace> getAllParkingSpaces() {
        List<ParkingSpace> spaces = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM " + PARKING_SPACE_TABLE;

            try (PreparedStatement statement = connection.prepareStatement(query);
                    ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    spaces.add(createParkingSpaceFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error retrieving parking spaces", e);
        }

        return spaces;
    }

    /**
     * Creates a ParkingSpace object from a ResultSet.
     *
     * @param resultSet ResultSet containing parking space data
     * @return ParkingSpace object
     * @throws SQLException if a database error occurs
     */
    private ParkingSpace createParkingSpaceFromResultSet(ResultSet resultSet) throws SQLException {
        return new ParkingSpace(
                resultSet.getString(PARKING_ID_COLUMN),
                resultSet.getString(PARKING_ADDRESS_COLUMN),
                resultSet.getFloat(COST_OF_PARKING_COLUMN),
                resultSet.getInt(NUMBER_OF_SLOTS_COLUMN),
                resultSet.getInt(MAX_DURATION_COLUMN),
                resultSet.getString(DESCRIPTION_COLUMN),
                resultSet.getInt(ADMIN_ID_COLUMN));
    }

    /**
     * Gets all users in the system.
     *
     * @return List of all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM " + USER_TABLE;

            try (PreparedStatement statement = connection.prepareStatement(query);
                    ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    users.add(createUserFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error retrieving users", e);
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error while creating user object: " + e.getMessage());
            e.printStackTrace();
        }

        return users;
    }

    /**
     * Creates a User object from a ResultSet.
     *
     * @param resultSet ResultSet containing user data
     * @return User object
     * @throws SQLException if a database error occurs
     */
    private User createUserFromResultSet(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setUserID(resultSet.getInt(USER_ID_COLUMN));
        user.setUserName(resultSet.getString(USER_NAME_COLUMN));
        user.setEmail(resultSet.getString(EMAIL_COLUMN));
        user.setPhone(resultSet.getString(PHONE_COLUMN));
        user.setBalance(new BigDecimal(resultSet.getString(BALANCE_COLUMN)));
        return user;
    }

    /**
     * Gets occupancy statistics for all parking spaces.
     *
     * @return Map with parking space IDs as keys and occupancy rates as values
     */
    public Map<String, Double> getOccupancyRates() {
        Map<String, Double> occupancyRates = new HashMap<>();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT p." + PARKING_ID_COLUMN + ", " +
                    "COUNT(s." + SLOT_ID_COLUMN + ") AS TotalSlots, " +
                    "SUM(CASE WHEN s." + AVAILABILITY_COLUMN + " = FALSE THEN 1 ELSE 0 END) AS OccupiedSlots " +
                    "FROM " + PARKING_SPACE_TABLE + " p " +
                    "LEFT JOIN " + PARKING_SLOT_TABLE + " s ON p." + PARKING_ID_COLUMN + " = s." + PARKING_ID_COLUMN
                    + " " +
                    "GROUP BY p." + PARKING_ID_COLUMN;

            try (PreparedStatement statement = connection.prepareStatement(query);
                    ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String parkingID = resultSet.getString(PARKING_ID_COLUMN);
                    int totalSlots = resultSet.getInt("TotalSlots");
                    int occupiedSlots = resultSet.getInt("OccupiedSlots");

                    // Calculate occupancy rate (avoid division by zero)
                    double occupancyRate = totalSlots > 0 ? (double) occupiedSlots / totalSlots * 100 : 0.0;

                    occupancyRates.put(parkingID, occupancyRate);
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error calculating occupancy rates", e);
        }

        return occupancyRates;
    }

    /**
     * Gets revenue for a specific parking space within a specified time period.
     *
     * @param parkingID   The ID of the parking space
     * @param whereClause SQL WHERE clause for time period filtering
     * @param parameters  Query parameters to be set
     * @return The total revenue for the specified period
     */
    private BigDecimal getRevenue(String parkingID, String whereClause, Object... parameters) {
        BigDecimal revenue = BigDecimal.ZERO;

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT SUM(p." + AMOUNT_COLUMN + ") AS Revenue " +
                    "FROM " + PAYMENT_TABLE + " p " +
                    "JOIN " + PARKING_RESERVATION_TABLE + " r ON p." + RESERVATION_ID_COLUMN + " = r."
                    + RESERVATION_ID_COLUMN + " " +
                    "JOIN " + PARKING_SLOT_TABLE + " s ON r." + SLOT_ID_COLUMN + " = s." + SLOT_ID_COLUMN + " " +
                    "WHERE s." + PARKING_ID_COLUMN + " = ? " + whereClause;

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, parkingID);

                // Set additional parameters if provided
                for (int i = 0; i < parameters.length; i++) {
                    statement.setObject(i + 2, parameters[i]);
                }

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next() && resultSet.getBigDecimal("Revenue") != null) {
                        revenue = resultSet.getBigDecimal("Revenue");
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error calculating revenue", e);
        }

        return revenue;
    }

    /**
     * Gets daily revenue for a specific parking space.
     *
     * @param parkingID The ID of the parking space
     * @param date      The date for which to calculate revenue (in format
     *                  YYYY-MM-DD)
     * @return The total revenue for the specified date
     */
    public BigDecimal getDailyRevenue(String parkingID, String date) {
        return getRevenue(parkingID, "AND DATE(p." + PAYMENT_DATE_COLUMN + ") = ?", date);
    }

    /**
     * Gets monthly revenue for a specific parking space.
     *
     * @param parkingID The ID of the parking space
     * @param year      The year for which to calculate revenue
     * @param month     The month for which to calculate revenue (1-12)
     * @return The total revenue for the specified month
     */
    public BigDecimal getMonthlyRevenueForSpace(String parkingID, int year, int month) {
        return getRevenue(parkingID,
                "AND YEAR(p." + PAYMENT_DATE_COLUMN + ") = ? AND MONTH(p." + PAYMENT_DATE_COLUMN + ") = ?",
                year, month);
    }

    /**
     * Gets total revenue for a specific parking space.
     *
     * @param parkingID The ID of the parking space
     * @return The total revenue for all time
     */
    public BigDecimal getTotalRevenueForSpace(String parkingID) {
        return getRevenue(parkingID, "");
    }

    /**
     * Updates the pricing for a parking space.
     *
     * @param parkingID The ID of the parking space
     * @param newPrice  The new price per hour/day
     * @return true if update successful, false otherwise
     */
    public boolean updateParkingPrice(String parkingID, BigDecimal newPrice) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE " + PARKING_SPACE_TABLE + " SET " + COST_OF_PARKING_COLUMN + " = ? WHERE "
                    + PARKING_ID_COLUMN + " = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setBigDecimal(1, newPrice);
                statement.setString(2, parkingID);

                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            handleSQLException("Error updating parking price", e);
        }

        return false;
    }

    /**
     * Creates new parking slots for a parking space.
     *
     * @param parkingID     The ID of the parking space
     * @param numberOfSlots The number of slots to create
     * @return true if creation successful, false otherwise
     */
    public boolean createParkingSlots(String parkingID, int numberOfSlots) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // First, determine the current highest slot number
            int startingSlotNumber = getNextSlotNumber(connection, parkingID);

            // Now insert the new slots
            boolean success = insertNewSlots(connection, parkingID, startingSlotNumber, numberOfSlots);

            // If successful, update the total number of slots in the parking space
            if (success) {
                updateTotalSlotCount(connection, parkingID, numberOfSlots);
            }

            return success;
        } catch (SQLException e) {
            handleSQLException("Error creating parking slots", e);
        }

        return false;
    }

    /**
     * Gets the next available slot number for a parking space.
     *
     * @param connection Database connection
     * @param parkingID  Parking space ID
     * @return Next available slot number
     * @throws SQLException if a database error occurs
     */
    private int getNextSlotNumber(Connection connection, String parkingID) throws SQLException {
        String maxQuery = "SELECT MAX(CAST(" + SLOT_NUMBER_COLUMN + " AS UNSIGNED)) AS MaxSlotNumber " +
                "FROM " + PARKING_SLOT_TABLE + " WHERE " + PARKING_ID_COLUMN + " = ?";

        int startingSlotNumber = 1;
        try (PreparedStatement maxStatement = connection.prepareStatement(maxQuery)) {
            maxStatement.setString(1, parkingID);

            try (ResultSet resultSet = maxStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getObject("MaxSlotNumber") != null) {
                    startingSlotNumber = resultSet.getInt("MaxSlotNumber") + 1;
                }
            }
        }
        return startingSlotNumber;
    }

    /**
     * Inserts new parking slots.
     *
     * @param connection         Database connection
     * @param parkingID          Parking space ID
     * @param startingSlotNumber First slot number to use
     * @param numberOfSlots      Number of slots to create
     * @return true if all inserts were successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    private boolean insertNewSlots(Connection connection, String parkingID, int startingSlotNumber, int numberOfSlots)
            throws SQLException {
        String insertQuery = "INSERT INTO " + PARKING_SLOT_TABLE + " (" + SLOT_NUMBER_COLUMN + ", " +
                AVAILABILITY_COLUMN + ", " + PARKING_ID_COLUMN + ") VALUES (?, TRUE, ?)";

        boolean allSuccessful = true;
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            connection.setAutoCommit(false);

            for (int i = 0; i < numberOfSlots; i++) {
                int slotNumber = startingSlotNumber + i;
                insertStatement.setString(1, String.valueOf(slotNumber));
                insertStatement.setString(2, parkingID);
                insertStatement.addBatch();
            }

            int[] results = insertStatement.executeBatch();

            // Check if all inserts were successful
            for (int result : results) {
                if (result <= 0) {
                    allSuccessful = false;
                    break;
                }
            }

            if (allSuccessful) {
                connection.commit();
            } else {
                connection.rollback();
            }

            connection.setAutoCommit(true);
        }
        return allSuccessful;
    }

    /**
     * Updates the total slot count for a parking space.
     *
     * @param connection      Database connection
     * @param parkingID       Parking space ID
     * @param additionalSlots Number of additional slots
     * @throws SQLException if a database error occurs
     */
    private void updateTotalSlotCount(Connection connection, String parkingID, int additionalSlots)
            throws SQLException {
        String updateSpaceQuery = "UPDATE " + PARKING_SPACE_TABLE + " SET " +
                NUMBER_OF_SLOTS_COLUMN + " = " + NUMBER_OF_SLOTS_COLUMN + " + ? WHERE " +
                PARKING_ID_COLUMN + " = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(updateSpaceQuery)) {
            updateStatement.setInt(1, additionalSlots);
            updateStatement.setString(2, parkingID);
            updateStatement.executeUpdate();
        }
    }

    /**
     * Gets a specific parking space by its ID.
     *
     * @param parkingID The ID of the parking space
     * @return The parking space if found, null otherwise
     */
    public ParkingSpace getParkingSpaceById(String parkingID) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM " + PARKING_SPACE_TABLE + " WHERE " + PARKING_ID_COLUMN + " = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, parkingID);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return createParkingSpaceFromResultSet(resultSet);
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error retrieving parking space", e);
        }

        return null;
    }

    /**
     * Gets all parking spaces managed by a specific admin.
     *
     * @param adminID The ID of the admin
     * @return List of parking spaces managed by the specified admin
     */
    public List<ParkingSpace> getParkingSpacesByAdmin(int adminID) {
        List<ParkingSpace> spaces = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM " + PARKING_SPACE_TABLE + " WHERE " + ADMIN_ID_COLUMN + " = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, adminID);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        spaces.add(createParkingSpaceFromResultSet(resultSet));
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error retrieving parking spaces by admin", e);
        }

        return spaces;
    }

    /**
     * Gets monthly revenue statistics for all parking spaces.
     *
     * @param year  The year for which to calculate revenue
     * @param month The month for which to calculate revenue (1-12)
     * @return Map with parking space IDs as keys and monthly revenue as values
     */
    public Map<String, BigDecimal> getMonthlyRevenue(int year, int month) {
        Map<String, BigDecimal> monthlyRevenue = new HashMap<>();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT s." + PARKING_ID_COLUMN + ", SUM(p." + AMOUNT_COLUMN + ") AS MonthlyRevenue " +
                    "FROM " + PAYMENT_TABLE + " p " +
                    "JOIN " + PARKING_RESERVATION_TABLE + " r ON p." + RESERVATION_ID_COLUMN + " = r."
                    + RESERVATION_ID_COLUMN + " " +
                    "JOIN " + PARKING_SLOT_TABLE + " s ON r." + SLOT_ID_COLUMN + " = s." + SLOT_ID_COLUMN + " " +
                    "WHERE YEAR(p." + PAYMENT_DATE_COLUMN + ") = ? AND MONTH(p." + PAYMENT_DATE_COLUMN + ") = ? " +
                    "GROUP BY s." + PARKING_ID_COLUMN;

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, year);
                statement.setInt(2, month);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String parkingID = resultSet.getString(PARKING_ID_COLUMN);
                        BigDecimal revenue = resultSet.getBigDecimal("MonthlyRevenue");

                        monthlyRevenue.put(parkingID, revenue != null ? revenue : BigDecimal.ZERO);
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error calculating monthly revenue", e);
        }

        return monthlyRevenue;
    }

    /**
     * Gets detailed information about a specific parking slot.
     *
     * @param slotID The ID of the parking slot
     * @return The parking slot if found, null otherwise
     */
    public ParkingSlot getParkingSlotById(int slotID) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM " + PARKING_SLOT_TABLE + " WHERE " + SLOT_ID_COLUMN + " = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, slotID);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return createParkingSlotFromResultSet(resultSet);
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error retrieving parking slot", e);
        }

        return null;
    }

    /**
     * Creates a ParkingSlot object from a ResultSet.
     *
     * @param resultSet ResultSet containing parking slot data
     * @return ParkingSlot object
     * @throws SQLException if a database error occurs
     */
    private ParkingSlot createParkingSlotFromResultSet(ResultSet resultSet) throws SQLException {
        return new ParkingSlot(
                resultSet.getInt(SLOT_ID_COLUMN),
                resultSet.getString(SLOT_NUMBER_COLUMN),
                resultSet.getBoolean(AVAILABILITY_COLUMN),
                resultSet.getString(PARKING_ID_COLUMN));
    }

    /**
     * Gets all parking slots for a specific parking space.
     *
     * @param parkingID The ID of the parking space
     * @return List of parking slots in the specified parking space
     */
    public List<ParkingSlot> getParkingSlotsByParkingID(String parkingID) {
        List<ParkingSlot> slots = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM " + PARKING_SLOT_TABLE + " WHERE " + PARKING_ID_COLUMN + " = ? ORDER BY CAST("
                    + SLOT_NUMBER_COLUMN + " AS UNSIGNED)";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, parkingID);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        slots.add(createParkingSlotFromResultSet(resultSet));
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error retrieving parking slots", e);
        }

        return slots;
    }

    /**
     * Updates the availability of a parking slot.
     *
     * @param slotID       The ID of the parking slot
     * @param availability The new availability status
     * @return true if update successful, false otherwise
     */
    public boolean updateSlotAvailability(int slotID, boolean availability) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE " + PARKING_SLOT_TABLE + " SET " + AVAILABILITY_COLUMN + " = ? WHERE "
                    + SLOT_ID_COLUMN + " = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setBoolean(1, availability);
                statement.setInt(2, slotID);

                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            handleSQLException("Error updating slot availability", e);
        }

        return false;
    }

    /**
     * Gets total revenue statistics for all time.
     *
     * @return Map with parking space IDs as keys and total revenue as values
     */
    public Map<String, BigDecimal> getTotalRevenue() {
        Map<String, BigDecimal> totalRevenue = new HashMap<>();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT s." + PARKING_ID_COLUMN + ", SUM(p." + AMOUNT_COLUMN + ") AS TotalRevenue " +
                    "FROM " + PAYMENT_TABLE + " p " +
                    "JOIN " + PARKING_RESERVATION_TABLE + " r ON p." + RESERVATION_ID_COLUMN + " = r."
                    + RESERVATION_ID_COLUMN + " " +
                    "JOIN " + PARKING_SLOT_TABLE + " s ON r." + SLOT_ID_COLUMN + " = s." + SLOT_ID_COLUMN + " " +
                    "GROUP BY s." + PARKING_ID_COLUMN;

            try (PreparedStatement statement = connection.prepareStatement(query);
                    ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String parkingID = resultSet.getString(PARKING_ID_COLUMN);
                    BigDecimal revenue = resultSet.getBigDecimal("TotalRevenue");

                    totalRevenue.put(parkingID, revenue != null ? revenue : BigDecimal.ZERO);
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error calculating total revenue", e);
        }

        return totalRevenue;
    }

    /**
     * Handles a SQLException by logging it.
     *
     * @param message Error message prefix
     * @param e       SQLException that was thrown
     */
    private void handleSQLException(String message, SQLException e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
    }
}