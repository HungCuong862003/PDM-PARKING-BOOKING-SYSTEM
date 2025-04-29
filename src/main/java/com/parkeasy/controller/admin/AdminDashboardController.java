package main.java.com.parkeasy.controller.admin;

import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.model.ParkingSlot;
import main.java.com.parkeasy.service.AdminService;
import main.java.com.parkeasy.service.ParkingSpaceService;
import main.java.com.parkeasy.service.ReservationService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for handling admin dashboard operations
 */
public class AdminDashboardController {
    private static final Logger LOGGER = Logger.getLogger(AdminDashboardController.class.getName());

    private final AdminService adminService;
    private final ParkingSpaceService parkingSpaceService;
    private final ReservationService reservationService;

    /**
     * Constructor with dependency injection
     */
    public AdminDashboardController(AdminService adminService,
                                    ParkingSpaceService parkingSpaceService,
                                    ReservationService reservationService) {
        this.adminService = adminService;
        this.parkingSpaceService = parkingSpaceService;
        this.reservationService = reservationService;
    }

    /**
     * Get admin profile information
     *
     * @param adminId Admin ID
     * @return Admin profile or null if not found
     */
    public Admin getAdminProfile(int adminId) {
        try {
            return adminService.getAdminById(adminId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving admin profile", e);
            return null;
        }
    }

    /**
     * Get all parking spaces managed by an admin
     *
     * @param adminId Admin ID
     * @return List of parking spaces
     */
    public List<ParkingSpace> getAdminParkingSpaces(int adminId) {
        try {
            List<ParkingSpace> spaces = parkingSpaceService.getAllParkingSpacesByAdminId(adminId);
            return spaces != null ? spaces : Collections.emptyList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving admin parking spaces", e);
            return Collections.emptyList();
        }
    }

    /**
     * Count total parking spaces managed by an admin
     *
     * @param adminId Admin ID
     * @return Count of parking spaces
     */
    public int getTotalParkingSpaces(int adminId) {
        try {
            List<ParkingSpace> spaces = parkingSpaceService.getAllParkingSpacesByAdminId(adminId);
            return spaces != null ? spaces.size() : 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating total parking spaces", e);
            return 0;
        }
    }

    /**
     * Count total parking slots across all spaces managed by an admin
     *
     * @param adminId Admin ID
     * @return Total number of parking slots
     */
    public int getTotalParkingSlots(int adminId) {
        try {
            return parkingSpaceService.getTotalSlotsByAdminId(adminId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating total parking slots", e);
            return 0;
        }
    }

    /**
     * Calculate daily revenue for all parking spaces managed by an admin
     *
     * @param adminId Admin ID
     * @return Daily revenue
     */
    public double getDailyRevenue(int adminId) {
        try {
            LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime tomorrow = today.plusDays(1);
            Map<String, Object> stats = adminService.getRevenueStatistics(adminId, today, tomorrow);
            return (double) stats.getOrDefault("totalRevenue", 0.0);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating daily revenue", e);
            return 0.0;
        }
    }

    /**
     * Count total occupied parking slots for an admin
     *
     * @param adminId Admin ID
     * @return Count of occupied slots
     */
    public int getTotalOccupiedSlots(int adminId) {
        try {
            return parkingSpaceService.getOccupiedSlotCountByAdminId(adminId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating occupied slots", e);
            return 0;
        }
    }

    /**
     * Calculate occupancy rate for all parking spaces managed by an admin
     *
     * @param adminId Admin ID
     * @return Occupancy rate as percentage (0-100)
     */
    public double getOccupancyRate(int adminId) {
        try {
            int totalSlots = getTotalParkingSlots(adminId);
            int occupiedSlots = getTotalOccupiedSlots(adminId);

            if (totalSlots > 0) {
                return ((double) occupiedSlots / totalSlots) * 100;
            } else {
                return 0.0;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating occupancy rate", e);
            return 0.0;
        }
    }

    /**
     * Get dashboard summary data for admin
     *
     * @param adminId Admin ID
     * @return Map containing dashboard summary data
     */
    public Map<String, Object> getDashboardSummary(int adminId) {
        try {
            // Comprehensive data for dashboard
            return adminService.getDashboardSummary(adminId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving dashboard summary", e);
            return Collections.emptyMap();
        }
    }

    /**
     * Get recent activity for admin dashboard
     *
     * @param adminId Admin ID
     * @param limit Maximum number of activities to return
     * @return List of recent activities
     */
    public List<Map<String, Object>> getRecentActivity(int adminId, int limit) {
        try {
            return adminService.getRecentActivityByAdminId(adminId, limit);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving recent activity", e);
            return Collections.emptyList();
        }
    }
}