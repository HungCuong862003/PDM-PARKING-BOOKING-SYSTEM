package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.repository.AdminRepository;
import main.java.com.parkeasy.repository.ParkingSpaceRepository;
import main.java.com.parkeasy.repository.ParkingSlotRepository;
import main.java.com.parkeasy.repository.PaymentRepository;
import main.java.com.parkeasy.repository.ReservationRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Timestamp;

/**
 * Service class for admin-related operations
 */
public class AdminService {
    private static final Logger LOGGER = Logger.getLogger(AdminService.class.getName());

    private final AdminRepository adminRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    /**
     * Constructor with dependency injection
     */
    public AdminService(AdminRepository adminRepository, ParkingSpaceRepository parkingSpaceRepository,
                        ParkingSlotRepository parkingSlotRepository, PaymentRepository paymentRepository,
                        ReservationRepository reservationRepository) {
        this.adminRepository = adminRepository;
        this.parkingSpaceRepository = parkingSpaceRepository;
        this.parkingSlotRepository = parkingSlotRepository;
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Default constructor
     */
    public AdminService() {
        this.adminRepository = new AdminRepository();
        this.parkingSpaceRepository = new ParkingSpaceRepository();
        this.parkingSlotRepository = new ParkingSlotRepository();
        this.paymentRepository = new PaymentRepository();
        this.reservationRepository = new ReservationRepository();
    }

    /**
     * Create a new admin
     *
     * @param admin Admin object with data to save
     * @return true if successful, false otherwise
     */
    public boolean createAdmin(Admin admin) {
        try {
            LOGGER.log(Level.INFO, "Creating new admin: {0}", admin.getEmail());
            return adminRepository.createAdmin(admin);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating admin", e);
            return false;
        }
    }

    /**
     * Update an existing admin
     *
     * @param admin Admin object with updated data
     * @return true if successful, false otherwise
     */
    public boolean updateAdmin(Admin admin) {
        try {
            LOGGER.log(Level.INFO, "Updating admin: {0}", admin.getAdminID());

            // Validate admin object
            if (admin == null || admin.getAdminID() <= 0) {
                LOGGER.log(Level.WARNING, "Invalid admin data for update");
                return false;
            }

            // Check if admin exists
            Admin existingAdmin = getAdminById(admin.getAdminID());
            if (existingAdmin == null) {
                LOGGER.log(Level.WARNING, "Admin not found for update: {0}", admin.getAdminID());
                return false;
            }

            // Update admin in database
            boolean success = adminRepository.updateAdmin(admin);

            if (success) {
                LOGGER.log(Level.INFO, "Admin updated successfully: {0}", admin.getAdminID());
            } else {
                LOGGER.log(Level.WARNING, "Failed to update admin: {0}", admin.getAdminID());
            }

            return success;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating admin", e);
            return false;
        }
    }


    /**
     * Get admin by ID
     *
     * @param adminId ID of the admin to get
     * @return Admin object if found, null otherwise
     */
    public Admin getAdminById(int adminId) {
        try {
            LOGGER.log(Level.FINE, "Getting admin by ID: {0}", adminId);
            return adminRepository.getAdminById(adminId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting admin by ID", e);
            return null;
        }
    }

    /**
     * Check if an email already exists in the admin database
     *
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    public boolean isEmailExists(String email) {
        try {
            return adminRepository.isEmailExists(email);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if email exists", e);
            return false;
        }
    }

    /**
     * Check if a phone number already exists in the admin database
     *
     * @param phone Phone number to check
     * @return true if exists, false otherwise
     */
    public boolean isPhoneExists(String phone) {
        try {
            return adminRepository.isPhoneExists(phone);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if phone exists", e);
            return false;
        }
    }

    /**
     * Create a new parking space
     *
     * @param parkingSpace Parking space object with data to save
     * @return true if successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean createParkingSpace(ParkingSpace parkingSpace) throws SQLException {
        try {
            LOGGER.log(Level.INFO, "Creating new parking space: {0}", parkingSpace.getParkingID());
            int id = parkingSpaceRepository.addParkingSpace(parkingSpace);
            return id > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating parking space", e);
            throw e;
        }
    }

    /**
     * Update an existing parking space
     *
     * @param parkingSpace Parking space object with updated data
     * @return true if successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean updateParkingSpace(ParkingSpace parkingSpace) throws SQLException {
        LOGGER.log(Level.INFO, "Updating parking space: {0}", parkingSpace.getParkingID());
        return parkingSpaceRepository.updateParkingSpace(parkingSpace);
    }

    /**
     * Delete a parking space
     *
     * @param parkingId ID of the parking space to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteParkingSpace(String parkingId) {
        try {
            LOGGER.log(Level.INFO, "Deleting parking space: {0}", parkingId);

            // First delete all slots associated with this parking space
            parkingSlotRepository.deleteParkingSlotsByParkingId(parkingId);

            // Then delete the parking space itself
            return parkingSpaceRepository.deleteParkingSpace(parkingId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting parking space", e);
            return false;
        }
    }

    /**
     * Update a parking slot's availability status by slot number
     *
     * @param slotNumber Slot number to update
     * @param isAvailable New availability status
     * @return true if successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean updateSlotStatus(String slotNumber, boolean isAvailable) throws SQLException {
        LOGGER.log(Level.INFO, "Updating slot status: {0} to {1}", new Object[]{slotNumber, isAvailable});
        return parkingSlotRepository.updateSlotAvailability(slotNumber, isAvailable);
    }


    /**
     * Get dashboard summary for an admin
     *
     * @param adminId Admin ID
     * @return Map containing dashboard summary data
     */
    public Map<String, Object> getDashboardSummary(int adminId) {
        Map<String, Object> summary = new HashMap<>();
        try {
            // Get admin information
            Admin admin = getAdminById(adminId);
            if (admin == null) {
                LOGGER.log(Level.WARNING, "Admin not found: {0}", adminId);
                return Map.of(
                        "success", false,
                        "message", "Admin not found"
                );
            }

            summary.put("admin", admin);

            // Get parking spaces managed by this admin
            List<ParkingSpace> parkingSpaces = parkingSpaceRepository.getParkingSpacesByAdminId(adminId);
            summary.put("parkingSpaceCount", parkingSpaces.size());
            summary.put("parkingSpaces", parkingSpaces);

            // Calculate total slots
            int totalSlots = getTotalSlotsByAdminId(adminId);
            summary.put("totalSlots", totalSlots);

            // Calculate occupied slots
            int occupiedSlots = getOccupiedSlotCountByAdminId(adminId);
            summary.put("occupiedSlots", occupiedSlots);

            // Calculate occupancy rate
            double occupancyRate = totalSlots > 0 ? ((double) occupiedSlots / totalSlots) * 100 : 0;
            summary.put("occupancyRate", occupancyRate);

            // Get revenue statistics for today
            LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = startOfDay.plusDays(1);
            Map<String, Object> todayRevenue = getRevenueStatistics(adminId, startOfDay, endOfDay);
            summary.put("todayRevenue", todayRevenue.getOrDefault("totalRevenue", 0.0));

            // Get revenue statistics for this month
            LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
            Map<String, Object> monthlyRevenue = getRevenueStatistics(adminId, startOfMonth, endOfMonth);
            summary.put("monthlyRevenue", monthlyRevenue.getOrDefault("totalRevenue", 0.0));

            // Get most popular parking space
            String mostPopular = getMostPopularParkingSpace(adminId, startOfMonth, endOfMonth);
            summary.put("mostPopularParkingSpace", mostPopular);

            summary.put("success", true);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating dashboard summary for admin: " + adminId, e);
            summary.put("success", false);
            summary.put("message", "Error generating dashboard summary: " + e.getMessage());
        }

        return summary;
    }

    /**
     * Get the total number of parking slots managed by an admin
     *
     * @param adminId Admin ID
     * @return Total number of slots
     */
    public int getTotalSlotsByAdminId(int adminId) {
        try {
            // Get all parking spaces managed by this admin
            List<ParkingSpace> parkingSpaces = parkingSpaceRepository.getParkingSpacesByAdminId(adminId);

            int totalSlots = 0;
            for (ParkingSpace space : parkingSpaces) {
                totalSlots += space.getNumberOfSlots();
            }

            return totalSlots;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating total slots for admin: " + adminId, e);
            return 0;
        }
    }

    /**
     * Get the number of occupied slots managed by an admin
     *
     * @param adminId Admin ID
     * @return Number of occupied slots
     */
    public int getOccupiedSlotCountByAdminId(int adminId) {
        try {
            return parkingSlotRepository.getOccupiedSlotCountByAdminId(adminId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating occupied slots for admin: " + adminId, e);
            return 0;
        }
    }

    /**
     * Get revenue statistics for a specific time period
     *
     * @param adminId Admin ID
     * @param startDate Start date
     * @param endDate End date
     * @return Map containing revenue statistics
     */
    public Map<String, Object> getRevenueStatistics(int adminId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> statistics = new HashMap<>();
        try {
            // Get all parking spaces managed by this admin
            List<String> parkingIds = parkingSpaceRepository.getAllParkingSpaceIdsByAdminId(adminId);

            if (parkingIds.isEmpty()) {
                statistics.put("totalRevenue", 0.0);
                statistics.put("reservationCount", 0);
                statistics.put("paymentCount", 0);
                return statistics;
            }

            // Get payments for these parking spaces within the date range
            List<Map<String, Object>> payments = new ArrayList<>();
            double totalRevenue = 0.0;

            // Convert to java.sql.Timestamp for the query
            Timestamp startTimestamp = Timestamp.valueOf(startDate);
            Timestamp endTimestamp = Timestamp.valueOf(endDate);

            // Prepare SQL query for revenue calculation
            String sql = "SELECT p.* FROM payment p " +
                    "JOIN parking_reservation pr ON p.ReservationID = pr.ReservationID " +
                    "JOIN parkingslot ps ON pr.SlotNumber = ps.SlotNumber " +
                    "WHERE ps.ParkingID IN (";

            for (int i = 0; i < parkingIds.size(); i++) {
                sql += "?";
                if (i < parkingIds.size() - 1) {
                    sql += ",";
                }
            }

            sql += ") AND p.PaymentDate BETWEEN ? AND ?";

            LOGGER.log(Level.INFO, "Executing SQL: {0}", sql);

            // Get payments from the repository
            List<main.java.com.parkeasy.model.Payment> paymentList =
                    paymentRepository.getPaymentsByParkingIdsAndDateRange(parkingIds, startDate, endDate);

            // Process payments
            for (main.java.com.parkeasy.model.Payment payment : paymentList) {
                totalRevenue += payment.getAmount();

                Map<String, Object> paymentData = new HashMap<>();
                paymentData.put("paymentId", payment.getPaymentID());
                paymentData.put("amount", payment.getAmount());
                paymentData.put("date", payment.getPaymentDate());
                paymentData.put("method", payment.getPaymentMethod());
                paymentData.put("reservationId", payment.getReservationID());

                payments.add(paymentData);
            }

            // Get reservation count for the period
            int reservationCount = 0;
            for (String parkingId : parkingIds) {
                // Convert LocalDateTime to java.util.Date for getReservationsByParkingIdAndDateRange
                java.util.Date startUtilDate = java.util.Date.from(startDate.atZone(java.time.ZoneId.systemDefault()).toInstant());
                java.util.Date endUtilDate = java.util.Date.from(endDate.atZone(java.time.ZoneId.systemDefault()).toInstant());

                List<main.java.com.parkeasy.model.Reservation> reservations =
                        reservationRepository.getReservationsByParkingIdAndDateRange(parkingId, startUtilDate, endUtilDate);

                reservationCount += reservations.size();
            }

            // Populate statistics
            statistics.put("totalRevenue", totalRevenue);
            statistics.put("reservationCount", reservationCount);
            statistics.put("paymentCount", payments.size());
            statistics.put("payments", payments);

            return statistics;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating revenue statistics for admin: " + adminId, e);
            statistics.put("error", "Error calculating revenue statistics: " + e.getMessage());
            statistics.put("totalRevenue", 0.0);
            return statistics;
        }
    }

    /**
     * Get the most popular parking space for a time period
     *
     * @param adminId Admin ID
     * @param startDate Start date
     * @param endDate End date
     * @return Name of the most popular parking space
     */
    public String getMostPopularParkingSpace(int adminId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return reservationRepository.getMostPopularParkingSpaceByAdminId(adminId, startDate, endDate);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding most popular parking space", e);
            return "Unknown";
        }
    }

    /**
     * Get recent activity for an admin
     *
     * @param adminId Admin ID
     * @param limit Maximum number of activities to return
     * @return List of recent activities
     */
    public List<Map<String, Object>> getRecentActivityByAdminId(int adminId, int limit) {
        List<Map<String, Object>> activities = new ArrayList<>();
        try {
            // Get all parking spaces managed by this admin
            List<String> parkingIds = parkingSpaceRepository.getAllParkingSpaceIdsByAdminId(adminId);

            if (parkingIds.isEmpty()) {
                return activities;
            }

            // Get recent reservations for these parking spaces
            List<main.java.com.parkeasy.model.Reservation> reservations =
                    reservationRepository.getRecentReservationsByParkingIds(parkingIds, limit);

            // Process reservations
            for (main.java.com.parkeasy.model.Reservation reservation : reservations) {
                Map<String, Object> activity = new HashMap<>();
                activity.put("type", "Reservation");
                activity.put("timestamp", reservation.getCreatedAt());
                activity.put("reservationId", reservation.getReservationID());
                activity.put("vehicleId", reservation.getVehicleID());
                activity.put("status", reservation.getStatus());

                // Get slot information
                String slotNumber = reservation.getSlotNumber();
                String parkingId = parkingSlotRepository.getParkingIdBySlotNumber(slotNumber);
                String parkingAddress = "Unknown";

                if (parkingId != null) {
                    ParkingSpace space = parkingSpaceRepository.getParkingSpaceById(parkingId);
                    if (space != null) {
                        parkingAddress = space.getParkingAddress();
                    }
                }

                activity.put("parkingId", parkingId);
                activity.put("parkingAddress", parkingAddress);
                activity.put("slotNumber", slotNumber);

                activities.add(activity);
            }

            return activities;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving recent activity for admin: " + adminId, e);
            return activities;
        }
    }
}