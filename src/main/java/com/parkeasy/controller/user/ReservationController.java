package main.java.com.parkeasy.controller.user;

import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.model.Vehicle;
import main.java.com.parkeasy.service.ParkingSpaceService;
import main.java.com.parkeasy.service.ReservationService;
import main.java.com.parkeasy.service.UserService;
import main.java.com.parkeasy.service.VehicleService;
import main.java.com.parkeasy.util.Constants;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservationController {
    private static final Logger LOGGER = Logger.getLogger(ReservationController.class.getName());

    private final ReservationService reservationService;
    private final ParkingSpaceService parkingSpaceService;
    private final VehicleService vehicleService;
    private final UserService userService;

    /**
     * Constructor with dependency injection
     */
    public ReservationController(ReservationService reservationService,
                                 ParkingSpaceService parkingSpaceService,
                                 VehicleService vehicleService,
                                 UserService userService) {
        this.reservationService = reservationService;
        this.parkingSpaceService = parkingSpaceService;
        this.vehicleService = vehicleService;
        this.userService = userService;
    }

    /**
     * Alternative constructor for when UserService is not needed
     */
    public ReservationController(ReservationService reservationService,
                                 ParkingSpaceService parkingSpaceService,
                                 VehicleService vehicleService) {
        this.reservationService = reservationService;
        this.parkingSpaceService = parkingSpaceService;
        this.vehicleService = vehicleService;
        this.userService = new UserService(); // Create default UserService
    }

    /**
     * Get active reservations for a user
     *
     * @param userId The ID of the user
     * @return List of active reservations with additional details
     */
    public List<Map<String, Object>> getActiveReservations(int userId) {
        try {
            LOGGER.log(Level.INFO, "Getting active reservations for user: {0}", userId);

            List<Map<String, Object>> result = new ArrayList<>();
            List<Reservation> userReservations = reservationService.getReservationsByUserId(userId);

            // Current time
            LocalDateTime now = LocalDateTime.now();

            for (Reservation reservation : userReservations) {
                // Only include active or paid reservations that haven't ended yet
                if ((reservation.getStatus().equals(Constants.RESERVATION_IN_PROCESS) ||
                        reservation.getStatus().equals(Constants.RESERVATION_PAID)) &&
                        isReservationActive(reservation)) {

                    Map<String, Object> reservationDetails = createReservationDetailMap(reservation);
                    if (reservationDetails != null) {
                        result.add(reservationDetails);
                    }
                }
            }

            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting active reservations for user: " + userId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Get reservation history for a user
     *
     * @param userId The ID of the user
     * @return List of past reservations with additional details
     */
    public List<Map<String, Object>> getReservationHistory(int userId) {
        try {
            LOGGER.log(Level.INFO, "Getting reservation history for user: {0}", userId);

            List<Map<String, Object>> result = new ArrayList<>();
            List<Reservation> userReservations = reservationService.getReservationsByUserId(userId);

            for (Reservation reservation : userReservations) {
                // Include only completed, cancelled, or expired reservations
                if (reservation.getStatus().equals(Constants.RESERVATION_COMPLETE) ||
                        !isReservationActive(reservation)) {

                    Map<String, Object> reservationDetails = createReservationDetailMap(reservation);
                    if (reservationDetails != null) {
                        result.add(reservationDetails);
                    }
                }
            }

            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting reservation history for user: " + userId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Get detailed information about a specific reservation
     *
     * @param reservationId The ID of the reservation
     * @param userId The ID of the user (for verification)
     * @return Map containing reservation details
     */
    public Map<String, Object> getReservationDetails(int reservationId, int userId) {
        try {
            LOGGER.log(Level.INFO, "Getting details for reservation: {0}", reservationId);

            // Get the reservation
            Reservation reservation = reservationService.getReservationById(reservationId);

            if (reservation == null) {
                return Map.of(
                        "success", false,
                        "message", "Reservation not found"
                );
            }

            // Verify reservation belongs to user by checking vehicle ownership
            Vehicle vehicle = vehicleService.getVehicleById(reservation.getVehicleID());
            if (vehicle == null || vehicle.getUserID() != userId) {
                return Map.of(
                        "success", false,
                        "message", "Access denied: Reservation does not belong to user"
                );
            }

            // Get detailed information
            Map<String, Object> details = createReservationDetailMap(reservation);
            if (details == null) {
                return Map.of(
                        "success", false,
                        "message", "Error retrieving reservation details"
                );
            }

            // Get time values for calculations
            LocalDateTime startDateTime = (LocalDateTime) details.get("startDateTime");
            LocalDateTime endDateTime = (LocalDateTime) details.get("endDateTime");

            // Get parking ID and hourly rate
            String parkingId = reservationService.getParkingIdBySlotNumber(reservation.getSlotNumber());
            float hourlyRate = (float) reservationService.getParkingHourlyRate(parkingId);

            // Calculate duration in hours
            long durationHours = Duration.between(startDateTime, endDateTime).toHours();
            if (Duration.between(startDateTime, endDateTime).toMinutesPart() > 0) {
                durationHours += 1; // Round up any partial hour
            }

            // Add additional details
            details.put("success", true);
            details.put("durationHours", durationHours);
            details.put("hourlyRate", hourlyRate);
            details.put("totalCost", reservation.getFee()); // Use the stored fee value

            return details;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting details for reservation: " + reservationId, e);
            return Map.of(
                    "success", false,
                    "message", "Error: " + e.getMessage()
            );
        }
    }

    /**
     * Cancel a reservation
     *
     * @param reservationId The ID of the reservation to cancel
     * @param userId The ID of the user (for verification)
     * @return Map containing success status and message
     */
    public Map<String, Object> cancelReservation(int reservationId, int userId) {
        try {
            LOGGER.log(Level.INFO, "Cancelling reservation: {0} for user: {1}", new Object[]{reservationId, userId});

            // Get the reservation
            Reservation reservation = reservationService.getReservationById(reservationId);

            if (reservation == null) {
                return Map.of(
                        "success", false,
                        "message", "Reservation not found"
                );
            }

            // Verify reservation belongs to user by checking vehicle ownership
            Vehicle vehicle = vehicleService.getVehicleById(reservation.getVehicleID());
            if (vehicle == null || vehicle.getUserID() != userId) {
                return Map.of(
                        "success", false,
                        "message", "Access denied: Reservation does not belong to user"
                );
            }

            // Check if reservation can be cancelled
            if (reservation.getStatus().equals(Constants.RESERVATION_COMPLETE)) {
                return Map.of(
                        "success", false,
                        "message", "Cannot cancel a completed reservation"
                );
            }

            // Process cancellation - get admin and user for refund
            String parkingId = reservationService.getParkingIdBySlotNumber(reservation.getSlotNumber());

            // Update reservation status
            reservation.setStatus("CANCELLED");
            boolean updated = reservationService.updateReservation(reservation);

            if (updated) {
                return Map.of(
                        "success", true,
                        "message", "Reservation cancelled successfully"
                );
            } else {
                return Map.of(
                        "success", false,
                        "message", "Failed to cancel reservation"
                );
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error cancelling reservation: " + reservationId, e);
            return Map.of(
                    "success", false,
                    "message", "Error: " + e.getMessage()
            );
        }
    }

    /**
     * Check if reservation can be extended
     *
     * @param reservationId The ID of the reservation
     * @param userId The ID of the user (for verification)
     * @param newEndDateTime New end date and time
     * @return Map containing result of the check
     */
    public Map<String, Object> checkReservationExtension(int reservationId, int userId, LocalDateTime newEndDateTime) {
        try {
            LOGGER.log(Level.INFO, "Checking extension for reservation: {0}", reservationId);

            // Get the reservation
            Reservation reservation = reservationService.getReservationById(reservationId);

            if (reservation == null) {
                return Map.of(
                        "success", false,
                        "message", "Reservation not found"
                );
            }

            // Verify reservation belongs to user by checking vehicle ownership
            Vehicle vehicle = vehicleService.getVehicleById(reservation.getVehicleID());
            if (vehicle == null || vehicle.getUserID() != userId) {
                return Map.of(
                        "success", false,
                        "message", "Access denied: Reservation does not belong to user"
                );
            }

            // Check if reservation can be extended
            if (!reservation.getStatus().equals(Constants.RESERVATION_IN_PROCESS) &&
                    !reservation.getStatus().equals(Constants.RESERVATION_PAID)) {
                return Map.of(
                        "success", false,
                        "message", "Only active reservations can be extended"
                );
            }

            // Get current end time
            LocalDateTime currentEndDateTime = LocalDateTime.of(
                    reservation.getEndDate().toLocalDate(),
                    reservation.getEndTime().toLocalTime()
            );

            // Ensure new end time is after current end time
            if (!newEndDateTime.isAfter(currentEndDateTime)) {
                return Map.of(
                        "success", false,
                        "message", "New end time must be after current end time"
                );
            }

            // Check if slot is available for the extended period
            boolean slotAvailable = reservationService.isSlotAvailableForPeriod(
                    reservation.getSlotNumber(), currentEndDateTime, newEndDateTime);

            if (!slotAvailable) {
                return Map.of(
                        "success", false,
                        "message", "Slot is not available for the requested extension period"
                );
            }

            return Map.of(
                    "success", true,
                    "message", "Reservation can be extended"
            );
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking extension for reservation: " + reservationId, e);
            return Map.of(
                    "success", false,
                    "message", "Error: " + e.getMessage()
            );
        }
    }


    /**
     * Helper method to create a detailed map of reservation information
     *
     * @param reservation The reservation object
     * @return Map containing detailed reservation information
     */
    private Map<String, Object> createReservationDetailMap(Reservation reservation) {
        try {
            Vehicle vehicle = vehicleService.getVehicleById(reservation.getVehicleID());
            if (vehicle == null) {
                return null;
            }

            String slotNumber = reservation.getSlotNumber();
            String parkingId = reservationService.getParkingIdBySlotNumber(slotNumber);
            String parkingAddress = parkingId != null ?
                    reservationService.getParkingAddressByParkingId(parkingId) : "Unknown";

            // Convert SQL dates to LocalDateTime
            LocalDateTime startDateTime = LocalDateTime.of(
                    reservation.getStartDate().toLocalDate(),
                    reservation.getStartTime().toLocalTime()
            );

            LocalDateTime endDateTime = LocalDateTime.of(
                    reservation.getEndDate().toLocalDate(),
                    reservation.getEndTime().toLocalTime()
            );

            Map<String, Object> details = new HashMap<>();
            details.put("reservation", reservation);
            details.put("vehicle", vehicle);
            details.put("vehiclePlate", vehicle.getVehicleID());
            details.put("slotNumber", slotNumber);
            details.put("parkingId", parkingId);
            details.put("parkingAddress", parkingAddress);
            details.put("startDateTime", startDateTime);
            details.put("endDateTime", endDateTime);
            details.put("status", reservation.getStatus());

            return details;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating reservation detail map", e);
            return null;
        }
    }

    /**
     * Helper method to check if a reservation is currently active
     *
     * @param reservation The reservation to check
     * @return true if the reservation is active, false otherwise
     */
    private boolean isReservationActive(Reservation reservation) {
        try {
            LocalDateTime endDateTime = LocalDateTime.of(
                    reservation.getEndDate().toLocalDate(),
                    reservation.getEndTime().toLocalTime()
            );

            // Reservation is active if end time is in the future
            return endDateTime.isAfter(LocalDateTime.now());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking if reservation is active", e);
            return false;
        }
    }
    /**
     * Create a new reservation
     *
     * @param userId The ID of the user making the reservation
     * @param vehicleId The ID of the vehicle being parked
     * @param parkingId The ID of the parking space
     * @param slotNumber The parking slot number
     * @param startDate The start date of the reservation
     * @param startTime The start time of the reservation
     * @param endDate The end date of the reservation
     * @param endTime The end time of the reservation
     * @return Map containing result of the reservation creation
     */
    public Map<String, Object> createReservation(
            int userId,
            String vehicleId,
            String parkingId,
            String slotNumber,
            java.time.LocalDate startDate,
            java.time.LocalTime startTime,
            java.time.LocalDate endDate,
            java.time.LocalTime endTime) {

        try {
            LOGGER.log(Level.INFO, "Creating reservation for user: {0}, vehicle: {1}, parking: {2}, slot: {3}",
                    new Object[]{userId, vehicleId, parkingId, slotNumber});

            // Validate inputs
            if (userId <= 0 || vehicleId == null || parkingId == null || slotNumber == null ||
                    startDate == null || startTime == null || endDate == null || endTime == null) {
                return Map.of(
                        "success", false,
                        "message", "Invalid input parameters"
                );
            }

            // Verify user exists
            User user = userService.getUserById(userId);
            if (user == null) {
                return Map.of(
                        "success", false,
                        "message", "User not found"
                );
            }

            // Verify vehicle exists and belongs to user
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            if (vehicle == null) {
                return Map.of(
                        "success", false,
                        "message", "Vehicle not found"
                );
            }
            if (vehicle.getUserID() != userId) {
                return Map.of(
                        "success", false,
                        "message", "Vehicle does not belong to this user"
                );
            }

// Modified section of createReservation method
// Create LocalDateTime objects for validation and fee calculation
            LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
            LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);
            LocalDateTime now = LocalDateTime.now();

// Validate times
            if (startDateTime.isBefore(now)) {
                return Map.of(
                        "success", false,
                        "message", "Start time cannot be in the past"
                );
            }
            if (endDateTime.isBefore(startDateTime)) {
                return Map.of(
                        "success", false,
                        "message", "End time must be after start time"
                );
            }

// Check if slot is available for the specified period
            boolean slotAvailable = reservationService.isSlotAvailableForPeriod(slotNumber, startDateTime, endDateTime);
            if (!slotAvailable) {
                return Map.of(
                        "success", false,
                        "message", "Slot is not available for the requested period"
                );
            }

// Calculate fee
            float fee = calculateReservationFee(parkingId, startDateTime, endDateTime);

// Create the reservation object
            Reservation reservation = new Reservation();
            reservation.setReservationID(reservationService.generateReservationId());
            reservation.setVehicleID(vehicleId);
            reservation.setSlotNumber(slotNumber);
            reservation.setStartDate(java.sql.Date.valueOf(startDate));
            reservation.setEndDate(java.sql.Date.valueOf(endDate));
            reservation.setStartTime(java.sql.Time.valueOf(startTime));
            reservation.setEndTime(java.sql.Time.valueOf(endTime));
            reservation.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
            reservation.setFee(fee); // Set the calculated fee

            // Process the reservation
            boolean created = reservationService.createReservation(reservation, userId, parkingId);

            if (created) {

                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "Reservation created successfully");
                result.put("reservationId", reservation.getReservationID());
                result.put("fee", fee);
                result.put("startDateTime", startDateTime);
                result.put("endDateTime", endDateTime);

                return result;
            } else {
                return Map.of(
                        "success", false,
                        "message", "Failed to create reservation"
                );
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating reservation", e);
            return Map.of(
                    "success", false,
                    "message", "Error: " + e.getMessage()
            );
        }
    }
    /**
     * Complete a reservation
     *
     * @param reservationId The ID of the reservation to complete
     * @param userId The ID of the user (for verification)
     * @return Map containing success status and message
     */
    public Map<String, Object> completeReservation(int reservationId, int userId) {
        try {
            LOGGER.log(Level.INFO, "Completing reservation: {0} for user: {1}", new Object[]{reservationId, userId});

            // Get the reservation
            Reservation reservation = reservationService.getReservationById(reservationId);

            if (reservation == null) {
                return Map.of(
                        "success", false,
                        "message", "Reservation not found"
                );
            }

            // Verify reservation belongs to user by checking vehicle ownership
            Vehicle vehicle = vehicleService.getVehicleById(reservation.getVehicleID());
            if (vehicle == null || vehicle.getUserID() != userId) {
                return Map.of(
                        "success", false,
                        "message", "Access denied: Reservation does not belong to user"
                );
            }

            // Check if reservation is already completed
            if (reservation.getStatus().equals(Constants.RESERVATION_COMPLETE)) {
                return Map.of(
                        "success", false,
                        "message", "Reservation is already completed"
                );
            }

            // Simply mark the reservation as complete, keeping the original fee and end time
            // NO VALIDATION on whether reservation has started - allow completing in any situation
            reservation.setStatus(Constants.RESERVATION_COMPLETE);

            // Update the reservation
            boolean updated = reservationService.updateReservation(reservation);

            if (updated) {
                // Update parking slot availability to make it available again
                reservationService.updateSlotAvailability(reservation.getSlotNumber(), true);

                return Map.of(
                        "success", true,
                        "message", "Reservation completed successfully",
                        "reservationId", reservation.getReservationID()
                );
            } else {
                return Map.of(
                        "success", false,
                        "message", "Failed to complete reservation"
                );
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error completing reservation: " + reservationId, e);
            return Map.of(
                    "success", false,
                    "message", "Error: " + e.getMessage()
            );
        }
    }
    /**
     * Calculate reservation fee based on parking space hourly rate and duration
     *
     * @param parkingId The ID of the parking space
     * @param startDateTime The start date and time of the reservation
     * @param endDateTime The end date and time of the reservation
     * @return The calculated fee
     */
    private float calculateReservationFee(String parkingId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        try {
            // Get hourly rate for the parking space
            float hourlyRate = (float) reservationService.getParkingHourlyRate(parkingId);

            // Calculate duration in hours, rounding up partial hours
            long durationHours = Duration.between(startDateTime, endDateTime).toHours();
            if (Duration.between(startDateTime, endDateTime).toMinutesPart() > 0) {
                durationHours += 1; // Round up any partial hour
            }

            // Calculate total fee
            return (float) (hourlyRate * durationHours);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating reservation fee", e);
            return 0.0f;
        }
    }
}