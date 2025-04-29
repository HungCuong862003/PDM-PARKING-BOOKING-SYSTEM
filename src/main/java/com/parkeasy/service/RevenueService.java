package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.Payment;
import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.repository.PaymentRepository;
import main.java.com.parkeasy.repository.ReservationRepository;
import main.java.com.parkeasy.repository.ParkingSpaceRepository;
import main.java.com.parkeasy.repository.ParkingSlotRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for revenue-related operations
 */
public class RevenueService {
    private static final Logger LOGGER = Logger.getLogger(RevenueService.class.getName());

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final ParkingSlotRepository parkingSlotRepository;

    /**
     * Constructor with dependency injection
     */
    public RevenueService(PaymentRepository paymentRepository,
                          ReservationRepository reservationRepository,
                          ParkingSpaceRepository parkingSpaceRepository,
                          ParkingSlotRepository parkingSlotRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.parkingSpaceRepository = parkingSpaceRepository;
        this.parkingSlotRepository = parkingSlotRepository;
    }

    /**
     * Default constructor
     */
    public RevenueService() {
        this.paymentRepository = new PaymentRepository();
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

            // Get all parking spaces managed by this admin
            List<String> parkingIds = parkingSpaceRepository.getAllParkingSpaceIdsByAdminId(adminId);

            if (parkingIds.isEmpty()) {
                statistics.put("totalRevenue", 0.0);
                statistics.put("reservationCount", 0);
                statistics.put("paymentCount", 0);
                return statistics;
            }

            // Get payments for these parking spaces
            List<Payment> payments = new ArrayList<>();
            double totalRevenue = 0.0;

            for (String parkingId : parkingIds) {
                // Convert to java.util.Date for getPaymentsByParkingIdAndDateRange
                java.util.Date startUtilDate = java.util.Date.from(startDate.atZone(java.time.ZoneId.systemDefault()).toInstant());
                java.util.Date endUtilDate = java.util.Date.from(endDate.atZone(java.time.ZoneId.systemDefault()).toInstant());

                List<Payment> parkingPayments = paymentRepository.getPaymentsByParkingIdAndDateRange(parkingId, startUtilDate, endUtilDate);
                payments.addAll(parkingPayments);

                // Sum revenue
                for (Payment payment : parkingPayments) {
                    totalRevenue += payment.getAmount();
                }
            }

            // Get reservation count
            int reservationCount = 0;
            for (String parkingId : parkingIds) {
                // Convert to java.util.Date for getReservationsByParkingIdAndDateRange
                java.util.Date startUtilDate = java.util.Date.from(startDate.atZone(java.time.ZoneId.systemDefault()).toInstant());
                java.util.Date endUtilDate = java.util.Date.from(endDate.atZone(java.time.ZoneId.systemDefault()).toInstant());

                List<Reservation> reservations = reservationRepository.getReservationsByParkingIdAndDateRange(parkingId, startUtilDate, endUtilDate);
                reservationCount += reservations.size();
            }

            // Calculate average payment amount
            double avgPaymentAmount = payments.isEmpty() ? 0.0 : totalRevenue / payments.size();

            // Calculate statistics
            statistics.put("totalRevenue", totalRevenue);
            statistics.put("reservationCount", reservationCount);
            statistics.put("paymentCount", payments.size());
            statistics.put("averagePaymentAmount", avgPaymentAmount);

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

                double dailyRevenue = 0.0;
                int reservationCount = 0;

                // Get payments for this day
                for (String parkingId : parkingIds) {
                    // Convert to java.util.Date for getPaymentsByParkingIdAndDateRange
                    java.util.Date dayStartUtilDate = java.util.Date.from(dayStart.atZone(java.time.ZoneId.systemDefault()).toInstant());
                    java.util.Date dayEndUtilDate = java.util.Date.from(dayEnd.atZone(java.time.ZoneId.systemDefault()).toInstant());

                    List<Payment> payments = paymentRepository.getPaymentsByParkingIdAndDateRange(parkingId, dayStartUtilDate, dayEndUtilDate);

                    for (Payment payment : payments) {
                        dailyRevenue += payment.getAmount();
                    }

                    // Get reservations for this day
                    List<Reservation> reservations = reservationRepository.getReservationsByParkingIdAndDateRange(parkingId, dayStartUtilDate, dayEndUtilDate);
                    reservationCount += reservations.size();
                }

                Map<String, Object> dayData = new HashMap<>();
                dayData.put("date", dayStart.toLocalDate());
                dayData.put("revenue", dailyRevenue);
                dayData.put("reservationCount", reservationCount);

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

            // Get all parking spaces managed by this admin
            List<String> parkingIds = parkingSpaceRepository.getAllParkingSpaceIdsByAdminId(adminId);

            if (parkingIds.isEmpty()) {
                return weeklyBreakdown;
            }

            // Calculate number of weeks in the period
            LocalDateTime currentWeekStart = startDate;

            // Process week by week
            while (!currentWeekStart.isAfter(endDate)) {
                // Calculate week end (7 days later or endDate, whichever comes first)
                LocalDateTime weekEnd = currentWeekStart.plusDays(7);
                if (weekEnd.isAfter(endDate)) {
                    weekEnd = endDate;
                }

                double weeklyRevenue = 0.0;
                int reservationCount = 0;

                // Get payments for this week
                for (String parkingId : parkingIds) {
                    // Convert to java.util.Date for getPaymentsByParkingIdAndDateRange
                    java.util.Date weekStartUtilDate = java.util.Date.from(currentWeekStart.atZone(java.time.ZoneId.systemDefault()).toInstant());
                    java.util.Date weekEndUtilDate = java.util.Date.from(weekEnd.atZone(java.time.ZoneId.systemDefault()).toInstant());

                    List<Payment> payments = paymentRepository.getPaymentsByParkingIdAndDateRange(parkingId, weekStartUtilDate, weekEndUtilDate);

                    for (Payment payment : payments) {
                        weeklyRevenue += payment.getAmount();
                    }

                    // Get reservations for this week
                    List<Reservation> reservations = reservationRepository.getReservationsByParkingIdAndDateRange(parkingId, weekStartUtilDate, weekEndUtilDate);
                    reservationCount += reservations.size();
                }

                Map<String, Object> weekData = new HashMap<>();
                weekData.put("weekStart", currentWeekStart.toLocalDate());
                weekData.put("weekEnd", weekEnd.toLocalDate());
                weekData.put("revenue", weeklyRevenue);
                weekData.put("reservationCount", reservationCount);

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

            // Get all parking spaces managed by this admin
            List<String> parkingIds = parkingSpaceRepository.getAllParkingSpaceIdsByAdminId(adminId);

            if (parkingIds.isEmpty()) {
                return monthlyBreakdown;
            }

            // Start from the first day of the month of startDate
            LocalDateTime currentMonthStart = startDate.withDayOfMonth(1);

            // Process month by month
            while (!currentMonthStart.isAfter(endDate)) {
                // Calculate month end (last day of the month or endDate, whichever comes first)
                LocalDateTime monthEnd = currentMonthStart.plusMonths(1).withDayOfMonth(1).minusDays(1);
                if (monthEnd.isAfter(endDate)) {
                    monthEnd = endDate;
                }

                double monthlyRevenue = 0.0;
                int reservationCount = 0;

                // Get payments for this month
                for (String parkingId : parkingIds) {
                    // Convert to java.util.Date for getPaymentsByParkingIdAndDateRange
                    java.util.Date monthStartUtilDate = java.util.Date.from(currentMonthStart.atZone(java.time.ZoneId.systemDefault()).toInstant());
                    java.util.Date monthEndUtilDate = java.util.Date.from(monthEnd.atZone(java.time.ZoneId.systemDefault()).toInstant());

                    List<Payment> payments = paymentRepository.getPaymentsByParkingIdAndDateRange(parkingId, monthStartUtilDate, monthEndUtilDate);

                    for (Payment payment : payments) {
                        monthlyRevenue += payment.getAmount();
                    }

                    // Get reservations for this month
                    List<Reservation> reservations = reservationRepository.getReservationsByParkingIdAndDateRange(parkingId, monthStartUtilDate, monthEndUtilDate);
                    reservationCount += reservations.size();
                }

                Map<String, Object> monthData = new HashMap<>();
                monthData.put("month", currentMonthStart.getMonth());
                monthData.put("year", currentMonthStart.getYear());
                monthData.put("revenue", monthlyRevenue);
                monthData.put("reservationCount", reservationCount);

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
     * Get payment method breakdown
     *
     * @param adminId Admin ID
     * @param startDate Start date
     * @param endDate End date
     * @return Map of payment methods to amounts
     */
    public Map<String, Double> getPaymentMethodBreakdown(int adminId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Double> methodBreakdown = new HashMap<>();
        try {
            LOGGER.log(Level.INFO, "Getting payment method breakdown for admin: {0}", adminId);

            // Get all parking spaces managed by this admin
            List<String> parkingIds = parkingSpaceRepository.getAllParkingSpaceIdsByAdminId(adminId);

            if (parkingIds.isEmpty()) {
                return methodBreakdown;
            }

            // Initialize common payment methods
            methodBreakdown.put("CARD", 0.0);
            methodBreakdown.put("BALANCE", 0.0);

            // Process payments for each parking space
            for (String parkingId : parkingIds) {
                // Convert to java.util.Date for getPaymentsByParkingIdAndDateRange
                java.util.Date startUtilDate = java.util.Date.from(startDate.atZone(java.time.ZoneId.systemDefault()).toInstant());
                java.util.Date endUtilDate = java.util.Date.from(endDate.atZone(java.time.ZoneId.systemDefault()).toInstant());

                List<Payment> payments = paymentRepository.getPaymentsByParkingIdAndDateRange(parkingId, startUtilDate, endUtilDate);

                // Count amounts by payment method
                for (Payment payment : payments) {
                    String method = payment.getPaymentMethod();
                    double amount = payment.getAmount();

                    // Add to existing count or initialize
                    methodBreakdown.put(method, methodBreakdown.getOrDefault(method, 0.0) + amount);
                }
            }

            return methodBreakdown;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting payment method breakdown", e);
            return methodBreakdown;
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

            // Convert to java.util.Date for getPaymentsByParkingIdAndDateRange
            java.util.Date startUtilDate = java.util.Date.from(startDate.atZone(java.time.ZoneId.systemDefault()).toInstant());
            java.util.Date endUtilDate = java.util.Date.from(endDate.atZone(java.time.ZoneId.systemDefault()).toInstant());

            // Get payments for this parking space
            List<Payment> payments = paymentRepository.getPaymentsByParkingIdAndDateRange(parkingId, startUtilDate, endUtilDate);

            // Calculate total revenue
            double totalRevenue = 0.0;
            for (Payment payment : payments) {
                totalRevenue += payment.getAmount();
            }

            // Get reservations for this parking space
            List<Reservation> reservations = reservationRepository.getReservationsByParkingIdAndDateRange(parkingId, startUtilDate, endUtilDate);

            // Calculate average payment amount
            double avgPaymentAmount = payments.isEmpty() ? 0.0 : totalRevenue / payments.size();

            // Populate data
            revenueData.put("parkingId", parkingId);
            revenueData.put("parkingAddress", parkingSpace.getParkingAddress());
            revenueData.put("totalRevenue", totalRevenue);
            revenueData.put("reservationCount", reservations.size());
            revenueData.put("paymentCount", payments.size());
            revenueData.put("averagePaymentAmount", avgPaymentAmount);

            return revenueData;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting revenue for parking space: " + parkingId, e);
            revenueData.put("error", "Error getting revenue data: " + e.getMessage());
            return revenueData;
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
    public double getParkingSpaceUtilization(String parkingId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            LOGGER.log(Level.INFO, "Getting utilization rate for parking space: {0}", parkingId);

            // Get parking space details
            ParkingSpace parkingSpace = parkingSpaceRepository.getParkingSpaceById(parkingId);
            if (parkingSpace == null) {
                return 0.0;
            }

            // Get total number of slots
            int totalSlots = parkingSpace.getNumberOfSlots();
            if (totalSlots == 0) {
                return 0.0;
            }

            // Convert to java.util.Date for getReservationsByParkingIdAndDateRange
            java.util.Date startUtilDate = java.util.Date.from(startDate.atZone(java.time.ZoneId.systemDefault()).toInstant());
            java.util.Date endUtilDate = java.util.Date.from(endDate.atZone(java.time.ZoneId.systemDefault()).toInstant());

            // Get reservations for this parking space
            List<Reservation> reservations = reservationRepository.getReservationsByParkingIdAndDateRange(parkingId, startUtilDate, endUtilDate);

            // Calculate total hours in the date range
            long totalHoursInRange = ChronoUnit.HOURS.between(startDate, endDate);
            if (totalHoursInRange <= 0) {
                return 0.0;
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
            return (double) reservedSlotHours / totalSlotHours * 100;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating utilization rate for parking space: " + parkingId, e);
            return 0.0;
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

                // Convert to java.util.Date for getPaymentsByParkingIdAndDateRange
                java.util.Date dayStartUtilDate = java.util.Date.from(dayStart.atZone(java.time.ZoneId.systemDefault()).toInstant());
                java.util.Date dayEndUtilDate = java.util.Date.from(dayEnd.atZone(java.time.ZoneId.systemDefault()).toInstant());

                // Get payments for this day
                List<Payment> payments = paymentRepository.getPaymentsByParkingIdAndDateRange(parkingId, dayStartUtilDate, dayEndUtilDate);

                // Calculate daily revenue
                double dailyRevenue = 0.0;
                for (Payment payment : payments) {
                    dailyRevenue += payment.getAmount();
                }

                // Get reservations for this day
                List<Reservation> reservations = reservationRepository.getReservationsByParkingIdAndDateRange(parkingId, dayStartUtilDate, dayEndUtilDate);

                Map<String, Object> dayData = new HashMap<>();
                dayData.put("date", dayStart.toLocalDate());
                dayData.put("revenue", dailyRevenue);
                dayData.put("reservationCount", reservations.size());

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

                // Calculate utilization rate
                double utilizationRate = getParkingSpaceUtilization(parkingId, startDate, endDate);
                revenueData.put("utilizationRate", utilizationRate);

                parkingSpaceRevenue.add(revenueData);
            }

            return parkingSpaceRevenue;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting revenue comparison for admin: " + adminId, e);
            return parkingSpaceRevenue;
        }
    }
}