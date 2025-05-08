package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.model.Transaction;
import main.java.com.parkeasy.repository.ParkingSpaceRepository;
import main.java.com.parkeasy.repository.ParkingSlotRepository;
import main.java.com.parkeasy.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for revenue-related reporting and analysis
 */
public class RevenueService {
    private static final Logger LOGGER = Logger.getLogger(RevenueService.class.getName());

    private final TransactionService transactionService;
    private final ReservationRepository reservationRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final ParkingSlotRepository parkingSlotRepository;

    /**
     * Constructor with dependency injection
     */
    public RevenueService(TransactionService transactionService,
                          ReservationRepository reservationRepository,
                          ParkingSpaceRepository parkingSpaceRepository,
                          ParkingSlotRepository parkingSlotRepository) {
        this.transactionService = transactionService;
        this.reservationRepository = reservationRepository;
        this.parkingSpaceRepository = parkingSpaceRepository;
        this.parkingSlotRepository = parkingSlotRepository;
    }

    /**
     * Default constructor
     */
    public RevenueService() {
        this.transactionService = new TransactionService();
        this.reservationRepository = new ReservationRepository();
        this.parkingSpaceRepository = new ParkingSpaceRepository();
        this.parkingSlotRepository = new ParkingSlotRepository();
    }

    /**
     * Get revenue statistics for an admin
     *
     * @param adminId Admin ID
     * @param startDate Start date
     * @param endDate End date
     * @return Map containing revenue statistics
     */
    public Map<String, Object> getRevenueStatistics(int adminId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> statistics = new HashMap<>();
        try {
            LOGGER.log(Level.INFO, "Getting revenue statistics for admin: {0}", adminId);

            // Calculate total revenue using TransactionService
            float totalRevenue = transactionService.calculateRevenueForAdminByDateRange(adminId, startDate, endDate);

            // Get all transactions for this admin in the date range
            List<Transaction> transactions = transactionService.getTransactionsByAdminIdAndDateRange(adminId, startDate, endDate);

            // Calculate average transaction amount
            float avgTransactionAmount = transactions.isEmpty() ? 0.0f : totalRevenue / transactions.size();

            // Get all parking spaces managed by this admin
            List<String> parkingIds = parkingSpaceRepository.getAllParkingSpaceIdsByAdminId(adminId);

            // Count reservations in the date range
            int reservationCount = 0;
            for (String parkingId : parkingIds) {
                // Convert to java.util.Date for getReservationsByParkingIdAndDateRange
                java.util.Date startUtilDate = java.util.Date.from(startDate.atZone(java.time.ZoneId.systemDefault()).toInstant());
                java.util.Date endUtilDate = java.util.Date.from(endDate.atZone(java.time.ZoneId.systemDefault()).toInstant());

                List<Reservation> reservations = reservationRepository.getReservationsByParkingIdAndDateRange(parkingId, startUtilDate, endUtilDate);
                reservationCount += reservations.size();
            }

            // Populate statistics
            statistics.put("totalRevenue", totalRevenue);
            statistics.put("reservationCount", reservationCount);
            statistics.put("transactionCount", transactions.size());
            statistics.put("averageTransactionAmount", avgTransactionAmount);
            statistics.put("startDate", startDate);
            statistics.put("endDate", endDate);
            statistics.put("adminId", adminId);

            return statistics;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting revenue statistics", e);
            statistics.put("error", "Error getting revenue statistics: " + e.getMessage());
            statistics.put("totalRevenue", 0.0);
            return statistics;
        }
    }

    /**
     * Get daily revenue breakdown
     *
     * @param adminId Admin ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of daily revenue data
     */
    public List<Map<String, Object>> getDailyRevenueBreakdown(int adminId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Map<String, Object>> dailyBreakdown = new ArrayList<>();
        try {
            LOGGER.log(Level.INFO, "Getting daily revenue breakdown for admin: {0}", adminId);

            // Get all parking spaces managed by this admin
            List<String> parkingIds = parkingSpaceRepository.getAllParkingSpaceIdsByAdminId(adminId);

            if (parkingIds.isEmpty()) {
                return dailyBreakdown;
            }

            // Calculate number of days in the period
            long daysBetween = ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());

            // Generate daily breakdown
            for (long i = 0; i <= daysBetween; i++) {
                LocalDateTime dayStart = startDate.plusDays(i).toLocalDate().atStartOfDay();
                LocalDateTime dayEnd = dayStart.plusDays(1);

                // Skip if day is after end date
                if (dayStart.isAfter(endDate)) {
                    break;
                }

                float dailyRevenue = 0.0F;
                int reservationCount = 0;
                int transactionCount = 0;

                // Get transactions for this day (for all parking spaces)
                for (String parkingId : parkingIds) {
                    List<Transaction> transactions = transactionService.getTransactionsByParkingIdAndDateRange(parkingId, dayStart, dayEnd);

                    for (Transaction transaction : transactions) {
                        dailyRevenue += transaction.getAmount();
                        transactionCount++;

                        // Count each transaction as one reservation
                        reservationCount++;
                    }
                }

                Map<String, Object> dayData = new HashMap<>();
                dayData.put("date", dayStart.toLocalDate());
                dayData.put("revenue", dailyRevenue);
                dayData.put("reservationCount", reservationCount);
                dayData.put("transactionCount", transactionCount);

                dailyBreakdown.add(dayData);
            }

            return dailyBreakdown;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting daily revenue breakdown", e);
            return dailyBreakdown;
        }
    }

    /**
     * Get weekly revenue breakdown
     *
     * @param adminId Admin ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of weekly revenue data
     */
    public List<Map<String, Object>> getWeeklyRevenueBreakdown(int adminId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Map<String, Object>> weeklyBreakdown = new ArrayList<>();
        try {
            LOGGER.log(Level.INFO, "Getting weekly revenue breakdown for admin: {0}", adminId);

            // Calculate number of weeks in the period
            LocalDateTime currentWeekStart = startDate;

            // Process week by week
            while (!currentWeekStart.isAfter(endDate)) {
                // Calculate week end (7 days later or endDate, whichever comes first)
                LocalDateTime weekEnd = currentWeekStart.plusDays(7);
                if (weekEnd.isAfter(endDate)) {
                    weekEnd = endDate;
                }

                float weeklyRevenue = 0.0F;
                int reservationCount = 0;
                int transactionCount = 0;

                // Get transactions for all parking spaces in this week
                List<Transaction> weeklyTransactions = transactionService.getTransactionsByAdminIdAndDateRange(adminId, currentWeekStart, weekEnd);

                for (Transaction transaction : weeklyTransactions) {
                    weeklyRevenue += transaction.getAmount();
                    transactionCount++;

                    // Count each transaction as one reservation
                    reservationCount++;
                }

                Map<String, Object> weekData = new HashMap<>();
                weekData.put("weekStart", currentWeekStart.toLocalDate());
                weekData.put("weekEnd", weekEnd.toLocalDate());
                weekData.put("revenue", weeklyRevenue);
                weekData.put("reservationCount", reservationCount);
                weekData.put("transactionCount", transactionCount);

                weeklyBreakdown.add(weekData);

                // Move to next week
                currentWeekStart = weekEnd.plusDays(1);
            }

            return weeklyBreakdown;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting weekly revenue breakdown", e);
            return weeklyBreakdown;
        }
    }

    /**
     * Get monthly revenue breakdown
     *
     * @param adminId Admin ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of monthly revenue data
     */
    public List<Map<String, Object>> getMonthlyRevenueBreakdown(int adminId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Map<String, Object>> monthlyBreakdown = new ArrayList<>();
        try {
            LOGGER.log(Level.INFO, "Getting monthly revenue breakdown for admin: {0}", adminId);

            // Start from the first day of the month of startDate
            LocalDateTime currentMonthStart = startDate.withDayOfMonth(1);

            // Process month by month
            while (!currentMonthStart.isAfter(endDate)) {
                // Calculate month end (last day of the month or endDate, whichever comes first)
                LocalDateTime monthEnd = currentMonthStart.plusMonths(1).withDayOfMonth(1).minusDays(1);
                if (monthEnd.isAfter(endDate)) {
                    monthEnd = endDate;
                }

                float monthlyRevenue = 0.0F;
                int reservationCount = 0;
                int transactionCount = 0;

                // Get transactions for all parking spaces in this month
                List<Transaction> monthlyTransactions = transactionService.getTransactionsByAdminIdAndDateRange(adminId, currentMonthStart, monthEnd);

                for (Transaction transaction : monthlyTransactions) {
                    monthlyRevenue += transaction.getAmount();
                    transactionCount++;

                    // Count each transaction as one reservation
                    reservationCount++;
                }

                Map<String, Object> monthData = new HashMap<>();
                monthData.put("month", currentMonthStart.getMonth());
                monthData.put("year", currentMonthStart.getYear());
                monthData.put("revenue", monthlyRevenue);
                monthData.put("reservationCount", reservationCount);
                monthData.put("transactionCount", transactionCount);

                monthlyBreakdown.add(monthData);

                // Move to next month
                currentMonthStart = currentMonthStart.plusMonths(1);
            }

            return monthlyBreakdown;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting monthly revenue breakdown", e);
            return monthlyBreakdown;
        }
    }

    /**
     * Get parking space utilization rate
     *
     * @param parkingId Parking space ID
     * @param startDate Start date
     * @param endDate End date
     * @return Utilization rate (0-100%)
     */
    public float getParkingSpaceUtilization(String parkingId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            LOGGER.log(Level.INFO, "Getting utilization rate for parking space: {0}", parkingId);

            // Get parking space details
            ParkingSpace parkingSpace = parkingSpaceRepository.getParkingSpaceById(parkingId);
            if (parkingSpace == null) {
                return 0.0F;
            }

            // Get total number of slots
            int totalSlots = parkingSpace.getNumberOfSlots();
            if (totalSlots == 0) {
                return 0.0F;
            }

            // Convert to java.util.Date for getReservationsByParkingIdAndDateRange
            java.util.Date startUtilDate = java.util.Date.from(startDate.atZone(java.time.ZoneId.systemDefault()).toInstant());
            java.util.Date endUtilDate = java.util.Date.from(endDate.atZone(java.time.ZoneId.systemDefault()).toInstant());

            // Get reservations for this parking space
            List<Reservation> reservations = reservationRepository.getReservationsByParkingIdAndDateRange(parkingId, startUtilDate, endUtilDate);

            // Calculate total hours in the date range
            long totalHoursInRange = ChronoUnit.HOURS.between(startDate, endDate);
            if (totalHoursInRange <= 0) {
                return 0.0F;
            }

            // Calculate total slot-hours available
            long totalSlotHours = totalSlots * totalHoursInRange;

            // Calculate reserved slot-hours
            long reservedSlotHours = 0;
            for (Reservation reservation : reservations) {
                // Convert SQL dates to LocalDateTime
                LocalDateTime resStart = LocalDateTime.of(
                        reservation.getStartDate().toLocalDate(),
                        reservation.getStartTime().toLocalTime()
                );

                LocalDateTime resEnd = LocalDateTime.of(
                        reservation.getEndDate().toLocalDate(),
                        reservation.getEndTime().toLocalTime()
                );

                // Adjust to be within the range
                if (resStart.isBefore(startDate)) {
                    resStart = startDate;
                }

                if (resEnd.isAfter(endDate)) {
                    resEnd = endDate;
                }

                // Calculate hours for this reservation
                long resHours = ChronoUnit.HOURS.between(resStart, resEnd);
                if (resHours > 0) {
                    reservedSlotHours += resHours;
                }
            }

            // Calculate utilization rate
            return (float) reservedSlotHours / totalSlotHours * 100;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating utilization rate for parking space: " + parkingId, e);
            return 0.0F;
        }
    }

    /**
     * Get revenue for a specific parking space
     *
     * @param parkingId Parking space ID
     * @param startDate Start date
     * @param endDate End date
     * @return Map containing revenue data
     */
    public Map<String, Object> getParkingSpaceRevenue(String parkingId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> revenueData = new HashMap<>();
        try {
            LOGGER.log(Level.INFO, "Getting revenue for parking space: {0}", parkingId);

            // Get parking space details
            ParkingSpace parkingSpace = parkingSpaceRepository.getParkingSpaceById(parkingId);
            if (parkingSpace == null) {
                revenueData.put("error", "Parking space not found");
                return revenueData;
            }

            // Get transactions for this parking space in the date range
            List<Transaction> transactions = transactionService.getTransactionsByParkingIdAndDateRange(parkingId, startDate, endDate);

            // Calculate total revenue
            float totalRevenue = 0.0F;
            for (Transaction transaction : transactions) {
                totalRevenue += transaction.getAmount();
            }

            // Get reservations for this parking space
            java.util.Date startUtilDate = java.util.Date.from(startDate.atZone(java.time.ZoneId.systemDefault()).toInstant());
            java.util.Date endUtilDate = java.util.Date.from(endDate.atZone(java.time.ZoneId.systemDefault()).toInstant());
            List<Reservation> reservations = reservationRepository.getReservationsByParkingIdAndDateRange(parkingId, startUtilDate, endUtilDate);

            // Calculate average transaction amount
            float avgTransactionAmount = (float) (transactions.isEmpty() ? 0.0 : totalRevenue / transactions.size());

            // Calculate utilization rate
            float utilizationRate = getParkingSpaceUtilization(parkingId, startDate, endDate);

            // Populate data
            revenueData.put("parkingId", parkingId);
            revenueData.put("parkingAddress", parkingSpace.getParkingAddress());
            revenueData.put("totalRevenue", totalRevenue);
            revenueData.put("reservationCount", reservations.size());
            revenueData.put("transactionCount", transactions.size());
            revenueData.put("averageTransactionAmount", avgTransactionAmount);
            revenueData.put("utilizationRate", utilizationRate);
            revenueData.put("startDate", startDate);
            revenueData.put("endDate", endDate);

            return revenueData;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting revenue for parking space: " + parkingId, e);
            revenueData.put("error", "Error getting revenue data: " + e.getMessage());
            return revenueData;
        }
    }

    /**
     * Get daily revenue trend for a parking space
     *
     * @param parkingId Parking space ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of daily revenue data
     */
    public List<Map<String, Object>> getDailyRevenueTrend(String parkingId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Map<String, Object>> dailyTrend = new ArrayList<>();
        try {
            LOGGER.log(Level.INFO, "Getting daily revenue trend for parking space: {0}", parkingId);

            // Calculate number of days in the period
            long daysBetween = ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());

            // Process day by day
            for (long i = 0; i <= daysBetween; i++) {
                LocalDateTime dayStart = startDate.plusDays(i).toLocalDate().atStartOfDay();
                LocalDateTime dayEnd = dayStart.plusDays(1);

                // Skip if day is after end date
                if (dayStart.isAfter(endDate)) {
                    break;
                }

                // Get transactions for this day
                List<Transaction> transactions = transactionService.getTransactionsByParkingIdAndDateRange(parkingId, dayStart, dayEnd);

                // Calculate daily revenue
                float dailyRevenue = 0.0F;
                for (Transaction transaction : transactions) {
                    dailyRevenue += transaction.getAmount();
                }

                // Get reservations for this day (for calculating reservation count)
                java.util.Date dayStartUtilDate = java.util.Date.from(dayStart.atZone(java.time.ZoneId.systemDefault()).toInstant());
                java.util.Date dayEndUtilDate = java.util.Date.from(dayEnd.atZone(java.time.ZoneId.systemDefault()).toInstant());
                List<Reservation> reservations = reservationRepository.getReservationsByParkingIdAndDateRange(parkingId, dayStartUtilDate, dayEndUtilDate);

                Map<String, Object> dayData = new HashMap<>();
                dayData.put("date", dayStart.toLocalDate());
                dayData.put("revenue", dailyRevenue);
                dayData.put("reservationCount", reservations.size());
                dayData.put("transactionCount", transactions.size());

                dailyTrend.add(dayData);
            }

            return dailyTrend;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting daily revenue trend for parking space: " + parkingId, e);
            return dailyTrend;
        }
    }

    /**
     * Get revenue comparison for all parking spaces managed by an admin
     *
     * @param adminId Admin ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of parking space revenue data
     */
    public List<Map<String, Object>> getAllParkingSpaceRevenue(int adminId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Map<String, Object>> parkingSpaceRevenue = new ArrayList<>();
        try {
            LOGGER.log(Level.INFO, "Getting revenue comparison for admin: {0}", adminId);

            // Get all parking spaces managed by this admin
            List<ParkingSpace> parkingSpaces = parkingSpaceRepository.getAllParkingSpacesByAdminId(adminId);

            for (ParkingSpace parkingSpace : parkingSpaces) {
                String parkingId = parkingSpace.getParkingID();

                // Get revenue data for this parking space
                Map<String, Object> revenueData = getParkingSpaceRevenue(parkingId, startDate, endDate);

                // Add parking space details
                revenueData.put("parkingId", parkingId);
                revenueData.put("parkingAddress", parkingSpace.getParkingAddress());
                revenueData.put("numberOfSlots", parkingSpace.getNumberOfSlots());

                parkingSpaceRevenue.add(revenueData);
            }

            return parkingSpaceRevenue;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting revenue comparison for admin: " + adminId, e);
            return parkingSpaceRevenue;
        }
    }

    /**
     * Get top performing parking spaces for an admin
     *
     * @param adminId Admin ID
     * @param startDate Start date
     * @param endDate End date
     * @param limit Maximum number of results to return
     * @return List of top performing parking spaces
     */
    public List<Map<String, Object>> getTopPerformingParkingSpaces(int adminId, LocalDateTime startDate, LocalDateTime endDate, int limit) {
        try {
            LOGGER.log(Level.INFO, "Getting top performing parking spaces for admin: {0}", adminId);

            // Get all parking spaces revenue data
            List<Map<String, Object>> allSpacesRevenue = getAllParkingSpaceRevenue(adminId, startDate, endDate);

            // Sort by revenue (highest first)
            allSpacesRevenue.sort((a, b) -> {
                float revenueA = (float) a.getOrDefault("totalRevenue", 0.0f);
                float revenueB = (float) b.getOrDefault("totalRevenue", 0.0f);
                return Float.compare(revenueB, revenueA); // Descending order
            });

            // Limit the results
            int resultLimit = Math.min(limit, allSpacesRevenue.size());
            return allSpacesRevenue.subList(0, resultLimit);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting top performing parking spaces", e);
            return List.of();
        }
    }

    /**
     * Get underperforming parking spaces for an admin
     *
     * @param adminId Admin ID
     * @param startDate Start date
     * @param endDate End date
     * @param utilizationThreshold Threshold below which a parking space is considered underperforming
     * @return List of underperforming parking spaces
     */
    public List<Map<String, Object>> getUnderperformingParkingSpaces(int adminId, LocalDateTime startDate, LocalDateTime endDate, float utilizationThreshold) {
        try {
            LOGGER.log(Level.INFO, "Getting underperforming parking spaces for admin: {0}", adminId);

            // Get all parking spaces revenue data
            List<Map<String, Object>> allSpacesRevenue = getAllParkingSpaceRevenue(adminId, startDate, endDate);
            List<Map<String, Object>> underperformingSpaces = new ArrayList<>();

            // Filter by utilization rate
            for (Map<String, Object> space : allSpacesRevenue) {
                float utilizationRate = (float) space.getOrDefault("utilizationRate", 0.0f);
                if (utilizationRate < utilizationThreshold) {
                    underperformingSpaces.add(space);
                }
            }

            return underperformingSpaces;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting underperforming parking spaces", e);
            return List.of();
        }
    }
}