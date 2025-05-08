package main.java.com.parkeasy.controller.admin;

import main.java.com.parkeasy.service.AdminService;
import main.java.com.parkeasy.service.RevenueService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for managing revenue-related operations
 */
public class RevenueController {
    private static final Logger LOGGER = Logger.getLogger(RevenueController.class.getName());

    private final AdminService adminService;
    private final RevenueService revenueService;

    /**
     * Constructor with dependency injection
     */
    public RevenueController(AdminService adminService, RevenueService revenueService) {
        this.adminService = adminService;
        this.revenueService = revenueService;
    }

    /**
     * Get daily revenue statistics
     *
     * @param adminId Admin ID
     * @return Map containing daily revenue statistics
     */
    public Map<String, Object> getDailyRevenue(int adminId) {
        try {
            LocalDateTime startOfDay = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
            LocalDateTime endOfDay = startOfDay.plusDays(1);

            Map<String, Object> revenueStats = revenueService.getRevenueStatistics(adminId, startOfDay, endOfDay);
            addMetadata(revenueStats, "Daily Revenue", adminId, startOfDay, endOfDay);

            return revenueStats;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving daily revenue for admin: " + adminId, e);
            return Collections.emptyMap();
        }
    }

    /**
     * Get weekly revenue statistics
     *
     * @param adminId Admin ID
     * @return Map containing weekly revenue statistics
     */
    public Map<String, Object> getWeeklyRevenue(int adminId) {
        try {
            // Calculate start of week (Monday of current week)
            LocalDateTime now = LocalDateTime.now();
            int dayOfWeek = now.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday
            LocalDateTime startOfWeek = now.minusDays(dayOfWeek - 1).truncatedTo(ChronoUnit.DAYS);
            LocalDateTime endOfWeek = startOfWeek.plusDays(7);

            Map<String, Object> revenueStats = revenueService.getRevenueStatistics(adminId, startOfWeek, endOfWeek);
            addMetadata(revenueStats, "Weekly Revenue", adminId, startOfWeek, endOfWeek);

            // Add daily breakdown
            List<Map<String, Object>> dailyBreakdown = revenueService.getDailyRevenueBreakdown(adminId, startOfWeek, endOfWeek);
            revenueStats.put("dailyBreakdown", dailyBreakdown);

            return revenueStats;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving weekly revenue for admin: " + adminId, e);
            return Collections.emptyMap();
        }
    }

    /**
     * Get monthly revenue statistics
     *
     * @param adminId Admin ID
     * @return Map containing monthly revenue statistics
     */
    public Map<String, Object> getMonthlyRevenue(int adminId) {
        try {
            LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1);

            Map<String, Object> revenueStats = revenueService.getRevenueStatistics(adminId, startOfMonth, endOfMonth);
            addMetadata(revenueStats, "Monthly Revenue", adminId, startOfMonth, endOfMonth);

            // Add weekly breakdown
            List<Map<String, Object>> weeklyBreakdown = revenueService.getWeeklyRevenueBreakdown(adminId, startOfMonth, endOfMonth);
            revenueStats.put("weeklyBreakdown", weeklyBreakdown);


            return revenueStats;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving monthly revenue for admin: " + adminId, e);
            return Collections.emptyMap();
        }
    }

    /**
     * Get revenue statistics for a custom date range
     *
     * @param adminId Admin ID
     * @param startDate Start date
     * @param endDate End date
     * @return Map containing revenue statistics
     */
    public Map<String, Object> getCustomPeriodRevenue(int adminId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            // Validate date range
            if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
                LOGGER.log(Level.WARNING, "Invalid date range for custom period revenue");
                return Collections.emptyMap();
            }

            Map<String, Object> revenueStats = revenueService.getRevenueStatistics(adminId, startDate, endDate);
            addMetadata(revenueStats, "Custom Period Revenue", adminId, startDate, endDate);

            // Determine appropriate breakdown (daily, weekly, or monthly) based on range
            long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

            if (daysBetween <= 31) {
                // For short periods, show daily breakdown
                List<Map<String, Object>> dailyBreakdown = revenueService.getDailyRevenueBreakdown(adminId, startDate, endDate);
                revenueStats.put("dailyBreakdown", dailyBreakdown);
            } else if (daysBetween <= 90) {
                // For medium periods, show weekly breakdown
                List<Map<String, Object>> weeklyBreakdown = revenueService.getWeeklyRevenueBreakdown(adminId, startDate, endDate);
                revenueStats.put("weeklyBreakdown", weeklyBreakdown);
            } else {
                // For long periods, show monthly breakdown
                List<Map<String, Object>> monthlyBreakdown = revenueService.getMonthlyRevenueBreakdown(adminId, startDate, endDate);
                revenueStats.put("monthlyBreakdown", monthlyBreakdown);
            }

            return revenueStats;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving custom period revenue for admin: " + adminId, e);
            return Collections.emptyMap();
        }
    }

    /**
     * Get revenue statistics for a specific parking space
     *
     * @param parkingId Parking space ID
     * @param startDate Start date
     * @param endDate End date
     * @return Map containing revenue statistics
     */
    public Map<String, Object> getRevenueByParkingSpace(String parkingId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            // Validate input
            if (parkingId == null || parkingId.trim().isEmpty() ||
                    startDate == null || endDate == null || startDate.isAfter(endDate)) {
                LOGGER.log(Level.WARNING, "Invalid parameters for parking space revenue query");
                return Collections.emptyMap();
            }

            Map<String, Object> revenueData = revenueService.getParkingSpaceRevenue(parkingId, startDate, endDate);

            // Add metadata
            revenueData.put("reportType", "Parking Space Revenue");
            revenueData.put("parkingId", parkingId);
            revenueData.put("startDate", startDate);
            revenueData.put("endDate", endDate);
            revenueData.put("generatedAt", LocalDateTime.now());

            // Add utilization rate
            double utilizationRate = revenueService.getParkingSpaceUtilization(parkingId, startDate, endDate);
            revenueData.put("utilizationRate", utilizationRate);

            // Add daily revenue trend
            List<Map<String, Object>> dailyTrend = revenueService.getDailyRevenueTrend(parkingId, startDate, endDate);
            revenueData.put("dailyTrend", dailyTrend);

            return revenueData;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving revenue for parking space: " + parkingId, e);
            return Collections.emptyMap();
        }
    }

    /**
     * Get comparative revenue for all parking spaces
     *
     * @param adminId Admin ID
     * @param startDate Start date
     * @param endDate End date
     * @return Map containing revenue comparison data
     */
    public Map<String, Object> getRevenueComparison(int adminId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            // Validate date range
            if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
                LOGGER.log(Level.WARNING, "Invalid date range for revenue comparison");
                return Collections.emptyMap();
            }

            Map<String, Object> comparisonData = new HashMap<>();
            comparisonData.put("adminId", adminId);
            comparisonData.put("startDate", startDate);
            comparisonData.put("endDate", endDate);
            comparisonData.put("generatedAt", LocalDateTime.now());

            // Get parking space revenue breakdown
            List<Map<String, Object>> parkingSpaceRevenue = revenueService.getAllParkingSpaceRevenue(adminId, startDate, endDate);
            comparisonData.put("parkingSpaceRevenue", parkingSpaceRevenue);

            // Calculate total and average revenue
            double totalRevenue = parkingSpaceRevenue.stream()
                    .mapToDouble(m -> (double) m.getOrDefault("revenue", 0.0))
                    .sum();

            double avgRevenue = parkingSpaceRevenue.size() > 0 ?
                    totalRevenue / parkingSpaceRevenue.size() : 0.0;

            comparisonData.put("totalRevenue", totalRevenue);
            comparisonData.put("averageRevenue", avgRevenue);

            return comparisonData;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving revenue comparison for admin: " + adminId, e);
            return Collections.emptyMap();
        }
    }

    /**
     * Helper method to add metadata to revenue statistics
     */
    private void addMetadata(Map<String, Object> revenueStats, String reportType, int adminId,
                             LocalDateTime startDate, LocalDateTime endDate) {
        revenueStats.put("reportType", reportType);
        revenueStats.put("adminId", adminId);
        revenueStats.put("startDate", startDate);
        revenueStats.put("endDate", endDate);
        revenueStats.put("generatedAt", LocalDateTime.now());
    }
}