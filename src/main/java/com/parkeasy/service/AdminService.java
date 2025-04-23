package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.*;
import main.java.com.parkeasy.repository.*;
import main.java.com.parkeasy.util.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminService {

    private static final Logger LOGGER = Logger.getLogger(AdminService.class.getName());

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    // private final NotificationService notificationService;

    /**
     * Constructor for AdminService
     */
    public AdminService() {
        this.adminRepository = new AdminRepository();
        this.userRepository = new UserRepository();
        this.parkingSpaceRepository = new ParkingSpaceRepository();
        this.parkingSlotRepository = new ParkingSlotRepository();
        // this.notificationService = new NotificationService();
    }

    /**
     * Creates a new parking plot
     *
     * @param parkingSpace The parking plot to create
     * @return true if creation was successful, false otherwise
     */
    public boolean createParkingSpace(ParkingSpace parkingSpace) {
        Connection connection = null;
        boolean success = false;

        try {
            connection = DatabaseConnection.getConnection();

            if (connection == null) {
                LOGGER.log(Level.SEVERE, "Database connection is null");
                return false;
            }

            // Start transaction
            connection.setAutoCommit(false);

            // Create the parking space
            int spaceId = parkingSpaceRepository.addParkingSpace(parkingSpace);

            if (spaceId > 0) {
                // Create the associated parking slots
                for (int i = 1; i <= parkingSpace.getNumberOfSlots(); i++) {
                    ParkingSlot slot = new ParkingSlot();
                    slot.setSlotID(spaceId); // Use the generated spaceId
                    slot.setSlotNumber("S" + String.format("%03d", i));
                    slot.setAvailability(Constants.SLOT_AVAILABLE);

                    parkingSlotRepository.addParkingSlot(slot); // Adjusted to match the updated method signature
                }

                // Commit the transaction
                connection.commit();
                success = true;

                LOGGER.log(Level.INFO, "Parking plot created successfully: " + spaceId);
            } else {
                LOGGER.log(Level.WARNING, "Failed to create parking space");
            }

        } catch (SQLException e) {
            // Rollback the transaction in case of error
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error during transaction rollback", ex);
                }
            }

            LOGGER.log(Level.SEVERE, "Error creating parking plot", e);
        } finally {
            // Reset auto-commit mode and close the connection
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error resetting auto-commit or closing connection", e);
                }
            }
        }

        return success;
    }

    /**
     * Updates an existing parking plot
     *
     * @param parkingSpace The parking plot with updated information
     * @return true if update was successful, false otherwise
     */
    public boolean updateParkingSpace(ParkingSpace parkingSpace) throws SQLException {
        return parkingSpaceRepository.updateParkingSpace(parkingSpace);
    }

    /**
     * Deletes a parking plot and its associated slots
     *
     * @param parkingId The ID of the parking plot to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteParkingSpace(String parkingId) {
        Connection connection = null;
        boolean success = false;

        try {
            connection = DatabaseConnection.getConnection();

            // Start transaction
            connection.setAutoCommit(false);

            // Delete all slots associated with the parking plot
            parkingSlotRepository.deleteParkingSlot(parkingId);

            // Delete the parking plot from the database
            parkingSpaceRepository.deleteParkingSpace(parkingId);

            // Commit the transaction
            connection.commit();
            success = true;

            LOGGER.log(Level.INFO, "Parking plot deleted successfully: " + parkingId);

        } catch (SQLException e) {
            // Rollback the transaction in case of error
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error during transaction rollback", ex);
                }
            }

            LOGGER.log(Level.SEVERE, "Error deleting parking plot: " + parkingId, e);
        } finally {
            // Reset auto-commit mode
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error resetting auto-commit", e);
                }
            }
        }

        return success;
    }

    /**
     * Gets all parking plots
     *
     * @return List of all parking plots
     */
    public List<ParkingSpace> getAllParkingSpaces() throws SQLException {
        return parkingSpaceRepository.findAll();
    }

    /**
     * Gets all users
     *
     * @return List of all users
     */
    public List<User> getAllUsers() throws SQLException {
        return userRepository.findAll();
    }

    /**
     * Gets revenue statistics for a given time period
     *
     * @param startDate Start date for the report
     * @param endDate   End date for the report
     * @return Map containing revenue statistics
     */
    public Map<String, Object> getRevenueStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> statistics = new HashMap<>();

        try {
            // Implement revenue calculation logic here
            // This would typically involve querying the payment repository

            // For demonstration, we'll return some sample data
            statistics.put("totalRevenue", 15000.00);
            statistics.put("averageDailyRevenue", 500.00);
            statistics.put("totalReservations", 300);
            statistics.put("mostPopularParkingSpace", "Central Parking");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating revenue statistics", e);
        }

        return statistics;
    }

    /**
     * Sends a notification to all users
     *
     * @param subject The notification subject
     * @param message The notification message
     * @return Number of users notified successfully
     */
    // public int notifyAllUsers(String subject, String message) {
    // int successCount = 0;

    // try {
    // List<User> users = userRepository.findAll();

    // for (User user : users) {
    // boolean sent = notificationService.sendEmail(user.getEmail(), subject,
    // message);
    // if (sent) {
    // successCount++;
    // }
    // }

    // LOGGER.log(Level.INFO, "Sent notification to {0} users", successCount);

    // } catch (SQLException e) {
    // LOGGER.log(Level.SEVERE, "Error notifying users", e);
    // }

    // return successCount;
    // }

    /**
     * Updates the status of a parking slot
     *
     * @param slotId      The ID of the slot to update
     * @param isAvailable The new availability status
     * @return true if update was successful, false otherwise
     */
    public boolean updateSlotStatus(int slotId, boolean isAvailable) throws SQLException {
        ParkingSlot slot = parkingSlotRepository.findParkingSlotById(slotId);
        if (slot != null) {
            slot.setAvailability(isAvailable);
            return parkingSlotRepository.updateParkingSlot(slot);
        }
        return false;
    }
}