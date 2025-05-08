package main.java.com.parkeasy.controller.admin;

import main.java.com.parkeasy.model.ParkingSlot;
import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.service.AdminService;
import main.java.com.parkeasy.service.ParkingSlotService;
import main.java.com.parkeasy.service.ParkingSpaceService;
import main.java.com.parkeasy.service.ReservationService;
import main.java.com.parkeasy.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static main.java.com.parkeasy.repository.ParkingSlotRepository.getActiveReservationsForSlot;

public class ParkingManagementController {
    private static final Logger LOGGER = Logger.getLogger(ParkingManagementController.class.getName());
    private final AdminService adminService;
    private final ParkingSpaceService parkingSpaceService;
    private final ParkingSlotService parkingSlotService;
    private final ReservationService reservationService;

    public ParkingManagementController() {
        this.adminService = new AdminService();
        this.parkingSpaceService = new ParkingSpaceService();
        this.parkingSlotService = new ParkingSlotService();
        this.reservationService = new ReservationService();
    }

    public List<ParkingSpace> getAllParkingSpaces() {
        try {
            return parkingSpaceService.getAllParkingSpaces();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all parking spaces", e);
            return null;
        }
    }

    public ParkingSpace getParkingSpaceById(String parkingId) {
        try {
            return parkingSpaceService.getParkingSpaceById(parkingId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving parking space by ID", e);
            return null;
        }
    }

    public boolean createParkingSpace(ParkingSpace parkingSpace) {
        try {
            return adminService.createParkingSpace(parkingSpace);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating parking space", e);
            return false;
        }
    }

    public boolean updateParkingSpace(ParkingSpace parkingSpace) {
        try {
            return adminService.updateParkingSpace(parkingSpace);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating parking space", e);
            return false;
        }
    }

    public boolean deleteParkingSpace(String parkingId) {
        try {
            return adminService.deleteParkingSpace(parkingId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting parking space", e);
            return false;
        }
    }

    public List<ParkingSlot> getParkingSlotsByParkingId(String parkingId) {
        try {
            return parkingSlotService.getParkingSlotsByParkingSpaceId(parkingId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving parking slots by parking ID", e);
            return null;
        }
    }

    /**
     * Update a parking slot's availability status
     *
     * @param slotNumber The slot number to update
     * @param isAvailable New availability status
     * @return true if successful, false otherwise
     */
    public boolean updateSlotStatus(String slotNumber, boolean isAvailable) {
        try {
            return adminService.updateSlotStatus(slotNumber, isAvailable);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating slot status", e);
            return false;
        }
    }

    /**
     * Add a new parking slot
     *
     * @param parkingSlot The parking slot to add
     * @return true if successful, false otherwise
     */
    public boolean addParkingSlot(ParkingSlot parkingSlot) {
        try {
            parkingSlotService.insertParkingSlot(parkingSlot);
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding parking slot", e);
            return false;
        }
    }

    /**
     * Remove a parking slot by its slot number
     *
     * @param slotNumber The slot number to remove
     * @return true if successful, false otherwise
     */
    public boolean removeParkingSlot(String slotNumber) {
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = DatabaseConnection.getConnection();

            // Debug info
            System.out.println("Executing simple slot removal for: " + slotNumber);

            // First check if the slot exists
            PreparedStatement checkStmt = connection.prepareStatement(
                    "SELECT COUNT(*) FROM PARKING_SLOT WHERE SlotNumber = ?"
            );
            checkStmt.setString(1, slotNumber);
            ResultSet rs = checkStmt.executeQuery();

            boolean slotExists = false;
            if (rs.next()) {
                slotExists = rs.getInt(1) > 0;
            }

            rs.close();
            checkStmt.close();

            if (!slotExists) {
                System.out.println("Slot not found in database: " + slotNumber);
                return false;
            }

            // Check for reservations separately as a safeguard
            PreparedStatement reservationStmt = connection.prepareStatement(
                    "SELECT COUNT(*) FROM PARKING_RESERVATION WHERE SlotNumber = ? AND Status IN ('Active', 'Confirmed') AND EndDate >= CURRENT_DATE"
            );
            reservationStmt.setString(1, slotNumber);
            rs = reservationStmt.executeQuery();

            boolean hasReservations = false;
            if (rs.next()) {
                hasReservations = rs.getInt(1) > 0;
            }

            rs.close();
            reservationStmt.close();

            if (hasReservations) {
                System.out.println("Direct DB check found reservations for slot: " + slotNumber);
                return false;
            }

            // Delete the slot
            String query = "DELETE FROM PARKING_SLOT WHERE SlotNumber = ?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, slotNumber);

            int result = pstmt.executeUpdate();
            System.out.println("Delete operation affected " + result + " rows");

            return result > 0;

        } catch (Exception e) {
            System.out.println("Error in removeParkingSlot: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, Object> getActiveReservationInfoForSlot(String slotNumber) {
        Map<String, Object> reservationInfo = new HashMap<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Log the query attempt
            System.out.println("Fetching reservation info for slot: " + slotNumber);

            // Create SQL to get reservation info with user details and fee
            String sql = "SELECT r.reservationID, r.startDate, r.endDate, r.startTime, r.endTime, "
                    + "r.status, r.fee, v.vehicleID, u.userName "
                    + "FROM PARKING_RESERVATION r "
                    + "JOIN VEHICLE v ON r.vehicleID = v.vehicleID "
                    + "JOIN USER u ON v.userID = u.userID "
                    + "WHERE r.slotNumber = ? AND r.status = 'Processing' "
                    + "ORDER BY r.startDate DESC, r.startTime DESC LIMIT 1";

            // Explicitly manage resources rather than using try-with-resources
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Error: Database connection is null");
                return reservationInfo;
            }

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, slotNumber);
            System.out.println("Executing SQL: " + stmt.toString());

            rs = stmt.executeQuery();

            if (rs.next()) {
                try {
                    // Get all reservation details with explicit type checking
                    int reservationID = rs.getInt("reservationID");
                    if (!rs.wasNull()) {
                        reservationInfo.put("reservationID", reservationID);
                    }

                    java.sql.Date startDate = rs.getDate("startDate");
                    if (startDate != null) {
                        reservationInfo.put("startDate", startDate);
                    }

                    java.sql.Date endDate = rs.getDate("endDate");
                    if (endDate != null) {
                        reservationInfo.put("endDate", endDate);
                    }

                    java.sql.Time startTime = rs.getTime("startTime");
                    if (startTime != null) {
                        reservationInfo.put("startTime", startTime);
                    }

                    java.sql.Time endTime = rs.getTime("endTime");
                    if (endTime != null) {
                        reservationInfo.put("endTime", endTime);
                    }

                    String status = rs.getString("status");
                    if (status != null) {
                        reservationInfo.put("status", status);
                    }

                    // Get fee information
                    double fee = rs.getDouble("fee");
                    if (!rs.wasNull()) {
                        reservationInfo.put("fee", fee);
                    }

                    String vehicleID = rs.getString("vehicleID");
                    if (vehicleID != null) {
                        reservationInfo.put("vehicleID", vehicleID);
                    }

                    String userName = rs.getString("userName");
                    if (userName != null) {
                        reservationInfo.put("userName", userName);
                    }

                    System.out.println("Successfully retrieved reservation info with " +
                            reservationInfo.size() + " fields");
                } catch (SQLException dataEx) {
                    System.err.println("Error retrieving column data: " + dataEx.getMessage());
                    dataEx.printStackTrace();
                }
            } else {
                System.out.println("No active reservation found for slot: " + slotNumber);
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching reservation info: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in getActiveReservationInfoForSlot: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Properly close all resources in finally block
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }

            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing PreparedStatement: " + e.getMessage());
            }

            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing Connection: " + e.getMessage());
            }
        }

        return reservationInfo;
    }

    /**
     * Removes a parking slot and renumbers all slots with higher numbers.
     * For example, if slot 5P66 is removed, then 6P66 becomes 5P66, 7P66 becomes 6P66, etc.
     *
     * @param slotNumber The slot number to remove (e.g. "5P66")
     * @param removedValue The numeric value of the removed slot (e.g. 5)
     * @return true if the operation was successful, false otherwise
     */
    public boolean twoPhaseRemoveSlot(String slotNumber, int removedValue) {
        Connection connection = null;

        try {
            // Get database connection
            connection = DatabaseConnection.getConnection();
            if (connection == null) {
                LOGGER.log(Level.SEVERE, "Could not establish database connection");
                return false;
            }

            // Disable auto-commit for transaction
            connection.setAutoCommit(false);

            LOGGER.info("Starting slot removal with renumbering for slot: " + slotNumber);

            // 1. Verify the slot exists
            boolean slotExists = false;
            try (PreparedStatement checkStmt = connection.prepareStatement(
                    "SELECT COUNT(*) FROM PARKING_SLOT WHERE SlotNumber = ?")) {
                checkStmt.setString(1, slotNumber);

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        slotExists = rs.getInt(1) > 0;
                    }
                }
            }

            if (!slotExists) {
                LOGGER.warning("Slot not found in database: " + slotNumber);
                return false;
            }

            // 2. Check for active reservations as a safety measure
            List<Map<String, Object>> activeReservations = getActiveReservationsForSlot(slotNumber);
            if (activeReservations != null && !activeReservations.isEmpty()) {
                LOGGER.warning("Cannot remove slot with active reservations: " + slotNumber);
                return false;
            }

            // 3. Extract the prefix part (e.g., "P66" from "5P66")
            String prefix = "";
            int pIndex = slotNumber.indexOf('P');
            if (pIndex >= 0) {
                prefix = slotNumber.substring(pIndex);
            } else {
                LOGGER.severe("Slot number format invalid: " + slotNumber);
                return false;
            }

            // 4. Get all slots that need to be renumbered (with higher numbers)
            Map<String, String> slotsToUpdate = new HashMap<>(); // Map of oldNumber -> newNumber
            List<String> allSlotsInOrder = new ArrayList<>();

            try (PreparedStatement selectStmt = connection.prepareStatement(
                    "SELECT SlotNumber FROM PARKING_SLOT WHERE SlotNumber LIKE ? ORDER BY LENGTH(SlotNumber), SlotNumber")) {
                selectStmt.setString(1, "%" + prefix);

                try (ResultSet rs = selectStmt.executeQuery()) {
                    while (rs.next()) {
                        String currentSlot = rs.getString("SlotNumber");
                        allSlotsInOrder.add(currentSlot);
                    }
                }
            }

            // 5. Process slots for updating
            for (String currentSlot : allSlotsInOrder) {
                try {
                    int pPos = currentSlot.indexOf('P');
                    if (pPos <= 0) continue; // Skip if format is invalid

                    String numStr = currentSlot.substring(0, pPos);
                    int numValue = Integer.parseInt(numStr);

                    // Only update slots with higher numbers than the one removed
                    if (numValue > removedValue) {
                        String newSlotNum = (numValue - 1) + prefix;
                        slotsToUpdate.put(currentSlot, newSlotNum);
                    }
                } catch (NumberFormatException e) {
                    LOGGER.warning("Skipping malformed slot number: " + currentSlot);
                }
            }

            LOGGER.info("Found " + slotsToUpdate.size() + " slots to renumber");

            // 6. Update reservations FIRST (maintain referential integrity)
            try {
                // Check if reservation table exists
                PreparedStatement checkTableStmt = connection.prepareStatement(
                        "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES " +
                                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'PARKING_RESERVATION'");

                boolean tableExists = false;
                try (ResultSet rs = checkTableStmt.executeQuery()) {
                    if (rs.next()) {
                        tableExists = rs.getInt(1) > 0;
                    }
                }

                if (tableExists) {
                    LOGGER.info("PARKING_RESERVATION table exists, updating reservations...");

                    // Sort slot numbers in descending order (for reservations)
                    List<String> sortedSlots = new ArrayList<>(slotsToUpdate.keySet());
                    sortedSlots.sort((s1, s2) -> {
                        try {
                            int p1Index = s1.indexOf('P');
                            int p2Index = s2.indexOf('P');

                            if (p1Index > 0 && p2Index > 0) {
                                int num1 = Integer.parseInt(s1.substring(0, p1Index));
                                int num2 = Integer.parseInt(s2.substring(0, p2Index));
                                return Integer.compare(num2, num1); // Descending order
                            }
                        } catch (NumberFormatException e) {
                            // Fall back to string comparison
                        }
                        return s2.compareTo(s1);
                    });

                    // Update reservations in reverse order
                    for (String oldSlotNum : sortedSlots) {
                        String newSlotNum = slotsToUpdate.get(oldSlotNum);

                        PreparedStatement updateResStmt = connection.prepareStatement(
                                "UPDATE PARKING_RESERVATION SET SlotNumber = ? WHERE SlotNumber = ?");
                        updateResStmt.setString(1, newSlotNum);
                        updateResStmt.setString(2, oldSlotNum);
                        int resUpdated = updateResStmt.executeUpdate();

                        LOGGER.info("Updated " + resUpdated + " reservations from " + oldSlotNum + " to " + newSlotNum);
                    }
                } else {
                    LOGGER.info("PARKING_RESERVATION table doesn't exist, skipping reservation updates");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error updating reservations: " + e.getMessage());
                // Continue even if reservation update fails as this is not critical
            }

            // 7. Use direct update for renumbering (more efficient approach)
            // Sort slot numbers in descending order to avoid conflicts (update highest numbers first)
            List<String> sortedSlots = new ArrayList<>(slotsToUpdate.keySet());
            sortedSlots.sort((s1, s2) -> {
                try {
                    int p1Index = s1.indexOf('P');
                    int p2Index = s2.indexOf('P');

                    if (p1Index > 0 && p2Index > 0) {
                        int num1 = Integer.parseInt(s1.substring(0, p1Index));
                        int num2 = Integer.parseInt(s2.substring(0, p2Index));
                        return Integer.compare(num2, num1); // Descending order
                    }
                } catch (NumberFormatException e) {
                    // Fall back to string comparison
                }
                return s2.compareTo(s1);
            });

            // Update slots in descending order (highest numbers first)
            for (String oldSlotNum : sortedSlots) {
                String newSlotNum = slotsToUpdate.get(oldSlotNum);

                try (PreparedStatement updateStmt = connection.prepareStatement(
                        "UPDATE PARKING_SLOT SET SlotNumber = ? WHERE SlotNumber = ?")) {
                    updateStmt.setString(1, newSlotNum);
                    updateStmt.setString(2, oldSlotNum);
                    int updated = updateStmt.executeUpdate();

                    if (updated == 0) {
                        LOGGER.warning("Failed to update slot " + oldSlotNum + " to " + newSlotNum);
                        connection.rollback();
                        return false;
                    }

                    LOGGER.info("Updated slot " + oldSlotNum + " to " + newSlotNum);
                }
            }

            // 8. Finally delete the original slot
            try (PreparedStatement deleteStmt = connection.prepareStatement(
                    "DELETE FROM PARKING_SLOT WHERE SlotNumber = ?")) {
                deleteStmt.setString(1, slotNumber);
                int deleteResult = deleteStmt.executeUpdate();

                if (deleteResult == 0) {
                    LOGGER.warning("Failed to delete slot " + slotNumber);
                    connection.rollback();
                    return false;
                }

                LOGGER.info("Slot " + slotNumber + " deleted successfully");
            }

            // 9. Update the parking space slot count if there's at least one slot in the system
            if (!allSlotsInOrder.isEmpty()) {
                try (PreparedStatement spaceStmt = connection.prepareStatement(
                        "UPDATE PARKING_SPACE SET NumberOfSlots = NumberOfSlots - 1 " +
                                "WHERE ParkingID = (SELECT ParkingID FROM PARKING_SLOT WHERE SlotNumber = ? LIMIT 1)")) {
                    spaceStmt.setString(1, allSlotsInOrder.get(0)); // Use any slot with the same ParkingID
                    spaceStmt.executeUpdate();
                }
            }

            // 10. Commit the transaction
            connection.commit();
            LOGGER.info("Slot removal and renumbering completed successfully");
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL Error during slot removal and renumbering: " + e.getMessage(), e);
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.rollback();
                    LOGGER.info("Transaction rolled back due to SQL error");
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error during rollback: " + ex.getMessage(), ex);
            }
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "General error during slot removal and renumbering: " + e.getMessage(), e);
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.rollback();
                    LOGGER.info("Transaction rolled back due to general error");
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error during rollback: " + ex.getMessage(), ex);
            }
            return false;
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing connection: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Force removes a parking slot directly from the database, bypassing business logic.
     * This is a last resort method when normal removal methods fail.
     *
     * @param slotNumber The slot number to remove
     * @return true if successful, false otherwise
     */
    public boolean forceRemoveSlot(String slotNumber) {
        Connection connection = null;

        try {
            connection = DatabaseConnection.getConnection();

            // Print detailed database information for debugging
            try (PreparedStatement infoStmt = connection.prepareStatement("SELECT @@version")) {
                try (ResultSet rs = infoStmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Database Info: " + rs.getString(1));
                    }
                }
            } catch (Exception e) {
                System.out.println("Could not get DB version: " + e.getMessage());
            }

            // Start transaction
            connection.setAutoCommit(false);

            System.out.println("Attempting FORCE REMOVAL for slot: " + slotNumber);

            // Try with a direct delete (bypassing all business logic)
            try (PreparedStatement directDeleteStmt = connection.prepareStatement(
                    "DELETE FROM PARKING_SLOT WHERE SlotNumber = ?")) {

                directDeleteStmt.setString(1, slotNumber);
                int result = directDeleteStmt.executeUpdate();

                System.out.println("Force DELETE affected " + result + " rows");

                if (result > 0) {
                    connection.commit();
                    return true;
                } else {
                    // If direct delete returned 0, try a select to see if the slot exists
                    try (PreparedStatement checkStmt = connection.prepareStatement(
                            "SELECT * FROM PARKING_SLOT WHERE SlotNumber = ?")) {

                        checkStmt.setString(1, slotNumber);
                        try (ResultSet rs = checkStmt.executeQuery()) {
                            if (!rs.next()) {
                                System.out.println("ERROR: Slot " + slotNumber + " does not exist in database");
                            } else {
                                System.out.println("WARNING: Slot exists but could not be deleted");
                                // Print all column values for debugging
                                int columnCount = rs.getMetaData().getColumnCount();
                                for (int i = 1; i <= columnCount; i++) {
                                    String columnName = rs.getMetaData().getColumnName(i);
                                    String value = rs.getString(i);
                                    System.out.println("  " + columnName + ": " + value);
                                }
                            }
                        }
                    }

                    connection.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL ERROR during force remove: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();

            try {
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Map<String, Object>> getAllReservationsForSlot(String slotNumber) {
        List<Map<String, Object>> reservations = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT r.reservationID, r.startDate, r.endDate, r.startTime, r.endTime, "
                    + "r.status, r.fee, r.vehicleID "
                    + "FROM PARKING_RESERVATION r "
                    + "WHERE r.slotNumber = ? "
                    + "ORDER BY r.startDate DESC, r.startTime DESC";

            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Error: Database connection is null");
                return reservations;
            }

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, slotNumber);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> reservation = new HashMap<>();

                reservation.put("reservationID", rs.getInt("reservationID"));
                reservation.put("startDate", rs.getDate("startDate"));
                reservation.put("endDate", rs.getDate("endDate"));
                reservation.put("startTime", rs.getTime("startTime"));
                reservation.put("endTime", rs.getTime("endTime"));
                reservation.put("status", rs.getString("status"));
                reservation.put("vehicleID", rs.getString("vehicleID"));

                // Add fee information
                double fee = rs.getDouble("fee");
                if (!rs.wasNull()) {
                    reservation.put("fee", fee);
                }

                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching reservation history: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close resources
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignore */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* ignore */ }
        }

        return reservations;
    }

    /**
     * Retrieves all active reservations for a specific parking slot
     *
     * @param slotNumber The slot number to check
     * @return A list of maps containing active reservation details
     */
    public List<Map<String, Object>> getActiveReservationsForSlot(String slotNumber) {
        List<Map<String, Object>> activeReservations = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT r.reservationID, r.startDate, r.endDate, r.startTime, r.endTime, "
                    + "r.status, r.vehicleID "
                    + "FROM PARKING_RESERVATION r "
                    + "WHERE r.slotNumber = ? AND r.status = 'Processing' "
                    + "ORDER BY r.startDate DESC, r.startTime DESC";

            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("Error: Database connection is null");
                return activeReservations;
            }

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, slotNumber);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> reservation = new HashMap<>();

                reservation.put("reservationID", rs.getInt("reservationID"));
                reservation.put("startDate", rs.getDate("startDate"));
                reservation.put("endDate", rs.getDate("endDate"));
                reservation.put("startTime", rs.getTime("startTime"));
                reservation.put("endTime", rs.getTime("endTime"));
                reservation.put("status", rs.getString("status"));
                reservation.put("vehicleID", rs.getString("vehicleID"));

                activeReservations.add(reservation);
            }

            System.out.println("Found " + activeReservations.size() + " active reservations for slot " + slotNumber);
        } catch (SQLException e) {
            System.err.println("Database error fetching active reservations: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close resources
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignore */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* ignore */ }
        }

        return activeReservations;
    }
}