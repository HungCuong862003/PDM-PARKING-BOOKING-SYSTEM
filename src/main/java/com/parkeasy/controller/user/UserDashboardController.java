package main.java.com.parkeasy.controller.user;

import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.model.Vehicle;
import main.java.com.parkeasy.service.PaymentService;
import main.java.com.parkeasy.service.ParkingSpaceService;
import main.java.com.parkeasy.service.ReservationService;
import main.java.com.parkeasy.service.UserService;
import main.java.com.parkeasy.service.VehicleService;
import main.java.com.parkeasy.util.Constants;

import java.sql.Timestamp;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for handling user dashboard operations
 */
public class UserDashboardController {
    private static final Logger LOGGER = Logger.getLogger(UserDashboardController.class.getName());

    private final UserService userService;
    private final VehicleService vehicleService;
    private final ReservationService reservationService;
    private final ParkingSpaceService parkingSpaceService;
    private final PaymentService paymentService;

    /**
     * Constructor with dependency injection
     */
    public UserDashboardController(UserService userService, VehicleService vehicleService,
                                   ReservationService reservationService,
                                   ParkingSpaceService parkingSpaceService,
                                   PaymentService paymentService) {
        this.userService = userService;
        this.vehicleService = vehicleService;
        this.reservationService = reservationService;
        this.parkingSpaceService = parkingSpaceService;
        this.paymentService = paymentService;
    }

    /**
     * Get dashboard summary for a user
     *
     * @param userId The ID of the user
     * @return Map containing dashboard summary data
     */
    public Map<String, Object> getDashboardSummary(int userId) {
        Map<String, Object> summary = new HashMap<>();

        try {
            // Get user information
            User user = userService.getUserById(userId);
            if (user == null) {
                LOGGER.log(Level.WARNING, "User not found: {0}", userId);
                return Map.of(
                        "success", false,
                        "message", "User not found"
                );
            }

            summary.put("user", user);

            // Get user's vehicles
            List<Vehicle> vehicles = vehicleService.getVehiclesByUserId(userId);
            summary.put("vehicleCount", vehicles.size());
            summary.put("vehicles", vehicles);

            // Get active reservations
            List<Map<String, Object>> activeReservations = new ArrayList<>();
            for (Vehicle vehicle : vehicles) {
                List<Reservation> vehicleReservations = reservationService.getReservationsByVehicleId(vehicle.getVehicleID());

                for (Reservation reservation : vehicleReservations) {
                    if ((reservation.getStatus().equals(Constants.RESERVATION_PENDING) ||
                            reservation.getStatus().equals(Constants.RESERVATION_PAID)) &&
                            isReservationActive(reservation)) {

                        Map<String, Object> resDetails = getReservationWithDetails(reservation, vehicle);
                        if (resDetails != null) {
                            activeReservations.add(resDetails);
                        }
                    }
                }
            }

            // Sort active reservations by start time (soonest first)
            List<Map<String, Object>> sortedActiveReservations = activeReservations.stream()
                    .sorted(Comparator.comparing(map -> (LocalDateTime)map.get("startDateTime")))
                    .collect(Collectors.toList());

            summary.put("activeReservations", sortedActiveReservations);
            summary.put("activeReservationCount", sortedActiveReservations.size());

            // Get upcoming reservations (not started yet)
            List<Map<String, Object>> upcomingReservations = sortedActiveReservations.stream()
                    .filter(map -> ((LocalDateTime)map.get("startDateTime")).isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList());

            summary.put("upcomingReservations", upcomingReservations);
            summary.put("upcomingReservationCount", upcomingReservations.size());

            // Get current reservations (already started but not ended)
            List<Map<String, Object>> currentReservations = sortedActiveReservations.stream()
                    .filter(map ->
                            ((LocalDateTime)map.get("startDateTime")).isBefore(LocalDateTime.now()) &&
                                    ((LocalDateTime)map.get("endDateTime")).isAfter(LocalDateTime.now())
                    )
                    .collect(Collectors.toList());

            summary.put("currentReservations", currentReservations);
            summary.put("currentReservationCount", currentReservations.size());

            // Get total spent on parking
            double totalSpent = paymentService.getTotalAmountByUserId(userId);
            summary.put("totalSpent", totalSpent);

            // Get recently used parking spaces
            List<String> recentParkingIds = reservationService.getRecentParkingSpaceIdsForUser(userId, 5);
            List<ParkingSpace> recentParkingSpaces = new ArrayList<>();

            for (String parkingId : recentParkingIds) {
                ParkingSpace space = parkingSpaceService.getParkingSpaceById(parkingId);
                if (space != null) {
                    recentParkingSpaces.add(space);
                }
            }

            summary.put("recentParkingSpaces", recentParkingSpaces);

            // Get pending payments
            int pendingPayments = reservationService.countPendingPaymentReservationsForUser(userId);
            summary.put("pendingPayments", pendingPayments);

            summary.put("success", true);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating dashboard summary for user: " + userId, e);
            summary.put("success", false);
            summary.put("message", "Error generating dashboard summary: " + e.getMessage());
        }

        return summary;
    }

    /**
     * Get quick stats for user
     *
     * @param userId The ID of the user
     * @return Map containing quick statistics
     */
    public Map<String, Object> getQuickStats(int userId) {
        try {
            Map<String, Object> stats = new HashMap<>();

            // Get user balance
            User user = userService.getUserById(userId);
            if (user != null) {
                stats.put("balance", user.getBalance());
            }

            // Count vehicles
            int vehicleCount = vehicleService.countUserVehicles(userId);
            stats.put("vehicleCount", vehicleCount);

            // Count active reservations
            int activeReservations = reservationService.countActiveReservationsForUser(userId);
            stats.put("activeReservations", activeReservations);

            // Count total reservations
            int totalReservations = reservationService.countTotalReservationsForUser(userId);
            stats.put("totalReservations", totalReservations);

            // Get total spent
            double totalSpent = paymentService.getTotalAmountByUserId(userId);
            stats.put("totalSpent", totalSpent);

            // Get this month's spend
            LocalDateTime firstDayOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            double monthlySpend = paymentService.getTotalAmountByUserIdAndDateRange(userId, firstDayOfMonth, LocalDateTime.now());
            stats.put("monthlySpend", monthlySpend);

            return stats;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving quick stats for user: " + userId, e);
            return Map.of(
                    "error", true,
                    "message", "Error retrieving statistics"
            );
        }
    }

    /**
     * Get reservation recommendations based on user history
     *
     * @param userId The ID of the user
     * @return List of recommended parking spaces
     */
    public List<ParkingSpace> getRecommendedParkingSpaces(int userId) {
        try {
            // Get frequently used parking spaces
            List<String> frequentParkingIds = reservationService.getFrequentParkingSpaceIdsForUser(userId, 3);

            // Get highest rated parking spaces
            List<String> highestRatedParkingIds = parkingSpaceService.getHighestRatedParkingSpaceIds(5);

            // Combine and remove duplicates
            List<String> recommendedIds = new ArrayList<>(frequentParkingIds);
            for (String id : highestRatedParkingIds) {
                if (!recommendedIds.contains(id)) {
                    recommendedIds.add(id);
                }
            }

            // Limit to 5 recommendations
            if (recommendedIds.size() > 5) {
                recommendedIds = recommendedIds.subList(0, 5);
            }

            // Get parking space details
            List<ParkingSpace> recommendations = new ArrayList<>();
            for (String id : recommendedIds) {
                ParkingSpace space = parkingSpaceService.getParkingSpaceById(id);
                if (space != null) {
                    recommendations.add(space);
                }
            }

            return recommendations;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting parking recommendations for user: " + userId, e);
            return List.of(); // Return empty list on error
        }
    }

    /**
     * Get recent activity for a user
     *
     * @param userId The ID of the user
     * @param limit Maximum number of activities to return
     * @return List of recent activities
     */
    public List<Map<String, Object>> getRecentActivity(int userId, int limit) {
        try {
            List<Map<String, Object>> activities = new ArrayList<>();

            // Get recent reservations
            List<Reservation> recentReservations = reservationService.getRecentReservationsForUser(userId, limit);

            for (Reservation reservation : recentReservations) {
                Vehicle vehicle = vehicleService.getVehicleById(reservation.getVehicleID());

                if (vehicle != null) {
                    Map<String, Object> resDetails = getReservationWithDetails(reservation, vehicle);

                    if (resDetails != null) {
                        // Add activity type and timestamp
                        Map<String, Object> activity = new HashMap<>();
                        activity.put("type", "RESERVATION");
                        activity.put("timestamp", reservation.getCreatedAt());
                        activity.put("details", resDetails);

                        activities.add(activity);
                    }
                }
            }


            // Sort by timestamp (most recent first)
            return activities.stream()
                    .sorted((a, b) -> {
                        Timestamp timestampA = (Timestamp) a.get("timestamp");
                        Timestamp timestampB = (Timestamp) b.get("timestamp");
                        return timestampB.compareTo(timestampA); // Reversed order for newest first
                    })
                    .limit(limit)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving recent activity for user: " + userId, e);
            return List.of(); // Return empty list on error
        }
    }

    /**
     * Helper method to check if a reservation is active
     */
    private boolean isReservationActive(Reservation reservation) {
        LocalDateTime endDateTime = LocalDateTime.of(
                reservation.getEndDate().toLocalDate(),
                reservation.getEndTime().toLocalTime()
        );

        return endDateTime.isAfter(LocalDateTime.now());
    }

    /**
     * Helper method to get reservation with additional details
     */
    private Map<String, Object> getReservationWithDetails(Reservation reservation, Vehicle vehicle) {
        try {
            // Get slot and parking space details
            String slotNumber = reservation.getSlotNumber();
            String parkingAddress = "Unknown";
            String parkingId = null;

            try {
                String parkingSpaceId = reservationService.getParkingIdBySlotNumber(slotNumber);
                parkingId = parkingSpaceId;

                if (parkingSpaceId != null) {
                    ParkingSpace space = parkingSpaceService.getParkingSpaceById(parkingSpaceId);
                    if (space != null) {
                        parkingAddress = space.getParkingAddress();
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error retrieving parking details for reservation: " + reservation.getReservationID(), e);
            }

            LocalDateTime startDateTime = LocalDateTime.of(
                    reservation.getStartDate().toLocalDate(),
                    reservation.getStartTime().toLocalTime()
            );

            LocalDateTime endDateTime = LocalDateTime.of(
                    reservation.getEndDate().toLocalDate(),
                    reservation.getEndTime().toLocalTime()
            );

            Map<String, Object> result = new HashMap<>();
            result.put("reservation", reservation);
            result.put("vehicle", vehicle);
            result.put("slotNumber", slotNumber);
            result.put("parkingAddress", parkingAddress);
            result.put("parkingId", parkingId != null ? parkingId : "Unknown");
            result.put("startDateTime", startDateTime);
            result.put("endDateTime", endDateTime);
            result.put("status", reservation.getStatus());

            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting reservation details", e);
            return null;
        }
    }
}