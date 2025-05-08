package main.java.com.parkeasy.controller.admin;

import main.java.com.parkeasy.model.Admin;
import main.java.com.parkeasy.model.ParkingSpace;
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
    public float getDailyRevenue(int adminId) {
        try {
            LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime tomorrow = today.plusDays(1);
            Map<String, Object> stats = adminService.getRevenueStatistics(adminId, today, tomorrow);
            return (float) stats.getOrDefault("totalRevenue", 0.0);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating daily revenue", e);
            return 0.0F;
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
    public float getOccupancyRate(int adminId) {
        try {
            int totalSlots = getTotalParkingSlots(adminId);
            int occupiedSlots = getTotalOccupiedSlots(adminId);

            if (totalSlots > 0) {
                return ((float) occupiedSlots / totalSlots) * 100;
            } else {
                return 0.0F;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating occupancy rate", e);
            return 0.0F;
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

// Include these methods in the AdminDashboardController class

    /**
     * Get the number of occupied slots for a specific parking space
     *
     * @param parkingId Parking space ID
     * @return Count of occupied slots
     */
    public int getOccupiedSlots(String parkingId) {
        try {
            return parkingSpaceService.getOccupiedSlotCountByParkingId(parkingId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating occupied slots for parking space: " + parkingId, e);
            return 0;
        }
    }
    /**
     * Calculate weekly revenue for all parking spaces managed by an admin
     *
     * @param adminId Admin ID
     * @return Weekly revenue
     */
    public float getWeeklyRevenue(int adminId) {
        try {
            // Calculate dates for the current week (starting from Monday)
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1)
                    .withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfWeek = startOfWeek.plusDays(7);

            // Get revenue statistics for the week
            Map<String, Object> stats = adminService.getRevenueStatistics(adminId, startOfWeek, endOfWeek);
            return (float) stats.getOrDefault("totalRevenue", 0.0);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating weekly revenue for admin: " + adminId, e);
            return 0.0F;
        }
    }

    /**
     * Calculate monthly revenue for all parking spaces managed by an admin
     *
     * @param adminId Admin ID
     * @return Monthly revenue
     */
    public float getMonthlyRevenue(int adminId) {
        try {
            // Calculate dates for the current month
            LocalDateTime startOfMonth = LocalDateTime.now()
                    .withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1);

            // Get revenue statistics for the month
            Map<String, Object> stats = adminService.getRevenueStatistics(adminId, startOfMonth, endOfMonth);
            return (float) stats.getOrDefault("totalRevenue", 0.0);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating monthly revenue for admin: " + adminId, e);
            return 0.0F;
        }
    }

    /**
     * Calculate weekly revenue for a specific parking space
     *
     * @param parkingId Parking space ID
     * @return Weekly revenue
     */
    public float getParkingSpaceWeeklyRevenue(String parkingId) {
        try {
            // Calculate dates for the current week (starting from Monday)
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1)
                    .withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfWeek = startOfWeek.plusDays(7);

            // Calculate revenue for the parking space for this week
            return (float) reservationService.calculateRevenueForParkingSpace(parkingId, startOfWeek, endOfWeek);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating weekly revenue for parking space: " + parkingId, e);
            return 0.0F;
        }
    }

    /**
     * Calculate monthly revenue for a specific parking space
     *
     * @param parkingId Parking space ID
     * @return Monthly revenue
     */
    public float getParkingSpaceMonthlyRevenue(String parkingId) {
        try {
            // Calculate dates for the current month
            LocalDateTime startOfMonth = LocalDateTime.now()
                    .withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1);

            // Calculate revenue for the parking space for this month
            return (float) reservationService.calculateRevenueForParkingSpace(parkingId, startOfMonth, endOfMonth);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating monthly revenue for parking space: " + parkingId, e);
            return 0.0F;
        }
    }
    /**
     * Calculate daily revenue for a specific parking space
     *
     * @param parkingId Parking space ID
     * @return Daily revenue
     */
    public float getParkingSpaceDailyRevenue(String parkingId) {
        try {
            LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime tomorrow = today.plusDays(1);
            return (float) reservationService.calculateRevenueForParkingSpace(parkingId, today, tomorrow);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating daily revenue for parking space: " + parkingId, e);
            return 0.0F;
        }
    }
}