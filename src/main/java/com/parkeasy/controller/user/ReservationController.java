package main.java.com.parkeasy.controller.user;

import main.java.com.parkeasy.model.ParkingSlot;
import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.model.Vehicle;
import main.java.com.parkeasy.service.ParkingSpaceService;
import main.java.com.parkeasy.service.ReservationService;
import main.java.com.parkeasy.service.VehicleService;
import main.java.com.parkeasy.util.Constants;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for handling reservation operations
 */
public class ReservationController {
    private static final Logger LOGGER = Logger.getLogger(ReservationController.class.getName());

    private final ReservationService reservationService;
    private final ParkingSpaceService parkingSpaceService;
    private final VehicleService vehicleService;

    /**
     * Constructor with dependency injection
     */
    public ReservationController(ReservationService reservationService,
                                 ParkingSpaceService parkingSpaceService,
                                 VehicleService vehicleService) {
        this.reservationService = reservationService;
        this.parkingSpaceService = parkingSpaceService;
        this.vehicleService = vehicleService;
    }

    /**
     * Create a new reservation
     *
     * @param userId The ID of the user making the reservation
     * @param vehicleId The ID of the vehicle to park
     * @param parkingId The ID of the parking space
     * @param slotNumber The number of the specific parking slot
     * @param startDate The start date (yyyy-MM-dd)
     * @param startTime The start time (HH:mm)
     * @param endDate The end date (yyyy-MM-dd)
     * @param endTime The end time (HH:mm)
     * @return Map containing result of the reservation process
     */
    public Map<String, Object> createReservation(int userId, String vehicleId, String parkingId, String slotNumber,
                                                 LocalDate startDate, LocalTime startTime,
                                                 LocalDate endDate, LocalTime endTime) {
        try {
            // Validate vehicle ownership
            if (!vehicleService.isVehicleOwnedByUser(vehicleId, userId)) {
                LOGGER.log(Level.WARNING, "Vehicle {0} not owned by user {1}", new Object[]{vehicleId, userId});
                return Map.of(
                        "success", false,
                        "message", "You can only make reservations for your own vehicles"
                );
            }

            // Validate slot belongs to the specified parking space
            ParkingSlot slot = parkingSpaceService.getParkingSlotByNumber(slotNumber);
            if (slot == null || !slot.getParkingID().equals(parkingId)) {
                LOGGER.log(Level.WARNING, "Slot {0} not found or doesn't belong to parking space {1}",
                        new Object[]{slotNumber, parkingId});
                return Map.of(
                        "success", false,
                        "message", "Invalid parking slot selected"
                );
            }

            // Check if slot is available
            if (!slot.getAvailability()) {
                return Map.of(
                        "success", false,
                        "message", "The selected parking slot is not available"
                );
            }

            // Check if dates and times are valid
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime reservationStart = LocalDateTime.of(startDate, startTime);
            LocalDateTime reservationEnd = LocalDateTime.of(endDate, endTime);

            if (reservationStart.isBefore(now)) {
                return Map.of(
                        "success", false,
                        "message", "Reservation start time cannot be in the past"
                );
            }

            if (reservationEnd.isBefore(reservationStart)) {
                return Map.of(
                        "success", false,
                        "message", "Reservation end time must be after start time"
                );
            }

            // Check if the slot is available for the specified time period
            if (!reservationService.isSlotAvailableForPeriod(slotNumber, reservationStart, reservationEnd)) {
                return Map.of(
                        "success", false,
                        "message", "The slot is not available for the selected time period"
                );
            }

            // Create the reservation
            Reservation reservation = new Reservation();
            reservation.setVehicleID(vehicleId);
            reservation.setSlotNumber(slotNumber);
            reservation.setStartDate(Date.valueOf(startDate));
            reservation.setStartTime(Time.valueOf(startTime));
            reservation.setEndDate(Date.valueOf(endDate));
            reservation.setEndTime(Time.valueOf(endTime));
            reservation.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            reservation.setStatus(Constants.RESERVATION_PENDING);

            // Generate a unique reservation ID
            int reservationId = reservationService.generateReservationId();
            reservation.setReservationID(reservationId);

            boolean success = reservationService.createReservation(reservation);

            if (success) {
                // Update slot availability
                slot.setAvailability(false);
                parkingSpaceService.updateParkingSlotByNumber(slotNumber, slot);

                return Map.of(
                        "success", true,
                        "message", "Reservation created successfully",
                        "reservationId", reservationId
                );
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
                    "message", "Error creating reservation: " + e.getMessage()
            );
        }
    }

    /**
     * Cancel a reservation
     *
     * @param reservationId The ID of the reservation to cancel
     * @param userId The ID of the user requesting the cancellation
     * @return Map containing result of the cancellation process
     */
    public Map<String, Object> cancelReservation(int reservationId, int userId) {
        try {
            Reservation reservation = reservationService.getReservationById(reservationId);
            if (reservation == null) {
                return Map.of(
                        "success", false,
                        "message", "Reservation not found"
                );
            }

            // Verify the reservation belongs to the user
            Vehicle vehicle = vehicleService.getVehicleById(reservation.getVehicleID());
            if (vehicle == null || vehicle.getUserID() != userId) {
                return Map.of(
                        "success", false,
                        "message", "You can only cancel your own reservations"
                );
            }

            // Check if reservation can be cancelled (not already completed or cancelled)
            if (reservation.getStatus().equals(Constants.RESERVATION_COMPLETED) ||
                    reservation.getStatus().equals(Constants.RESERVATION_CANCELLED)) {
                return Map.of(
                        "success", false,
                        "message", "This reservation cannot be cancelled"
                );
            }

            // Check cancellation policy (e.g., can't cancel if less than X hours before start time)
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime reservationStart = LocalDateTime.of(
                    reservation.getStartDate().toLocalDate(),
                    reservation.getStartTime().toLocalTime()
            );

            // For example, require 2 hours notice for cancellation
            if (reservationStart.minusHours(2).isBefore(now)) {
                return Map.of(
                        "success", false,
                        "message", "Reservations must be cancelled at least 2 hours in advance"
                );
            }

            // Update reservation status
            reservation.setStatus(Constants.RESERVATION_CANCELLED);
            boolean updated = reservationService.updateReservation(reservation);

            if (updated) {
                // Make the slot available again
                ParkingSlot slot = parkingSpaceService.getParkingSlotByNumber(reservation.getSlotNumber());
                if (slot != null) {
                    slot.setAvailability(true);
                    parkingSpaceService.updateParkingSlotByNumber(reservation.getSlotNumber(), slot);
                }

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
                    "message", "Error cancelling reservation: " + e.getMessage()
            );
        }
    }

    /**
     * Get all active reservations for a user
     *
     * @param userId The ID of the user
     * @return List of active reservations
     */
    public List<Map<String, Object>> getActiveReservations(int userId) {
        try {
            // Get all vehicles owned by the user
            List<Vehicle> userVehicles = vehicleService.getVehiclesByUserId(userId);

            if (userVehicles.isEmpty()) {
                return List.of();
            }

            List<Map<String, Object>> activeReservations = new ArrayList<>();

            for (Vehicle vehicle : userVehicles) {
                List<Reservation> vehicleReservations = reservationService.getReservationsByVehicleId(vehicle.getVehicleID());

                for (Reservation reservation : vehicleReservations) {
                    // Check if reservation is active (not cancelled or completed)
                    if (reservation.getStatus().equals(Constants.RESERVATION_PENDING) ||
                            reservation.getStatus().equals(Constants.RESERVATION_PAID)) {

                        // Check if end time is in the future
                        LocalDateTime endDateTime = LocalDateTime.of(
                                reservation.getEndDate().toLocalDate(),
                                reservation.getEndTime().toLocalTime()
                        );

                        if (endDateTime.isAfter(LocalDateTime.now())) {
                            // Get parking details
                            String slotNumber = reservation.getSlotNumber();
                            ParkingSlot slot = parkingSpaceService.getParkingSlotByNumber(slotNumber);
                            String parkingAddress = "Unknown";
                            if (slot != null) {
                                parkingAddress = parkingSpaceService.getParkingSpaceById(slot.getParkingID()).getParkingAddress();
                            }

                            activeReservations.add(Map.of(
                                    "reservation", reservation,
                                    "vehicle", vehicle,
                                    "parkingAddress", parkingAddress,
                                    "slotNumber", slotNumber
                            ));
                        }
                    }
                }
            }

            // Sort by start time (soonest first)
            return activeReservations.stream()
                    .sorted(Comparator.comparing(map -> {
                        Reservation res = (Reservation) map.get("reservation");
                        return LocalDateTime.of(
                                res.getStartDate().toLocalDate(),
                                res.getStartTime().toLocalTime()
                        );
                    }))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving active reservations for user: " + userId, e);
            return List.of(); // Return empty list on error
        }
    }

    /**
     * Get reservation history for a user
     *
     * @param userId The ID of the user
     * @return List of past reservations
     */
    public List<Map<String, Object>> getReservationHistory(int userId) {
        try {
            // Get all vehicles owned by the user
            List<Vehicle> userVehicles = vehicleService.getVehiclesByUserId(userId);

            if (userVehicles.isEmpty()) {
                return List.of();
            }

            List<Map<String, Object>> pastReservations = new ArrayList<>();

            for (Vehicle vehicle : userVehicles) {
                List<Reservation> vehicleReservations = reservationService.getReservationsByVehicleId(vehicle.getVehicleID());

                for (Reservation reservation : vehicleReservations) {
                    // Check if reservation is completed or cancelled, or has passed
                    boolean isPast = reservation.getStatus().equals(Constants.RESERVATION_COMPLETED) ||
                            reservation.getStatus().equals(Constants.RESERVATION_CANCELLED);

                    if (!isPast) {
                        // Check if end time is in the past
                        LocalDateTime endDateTime = LocalDateTime.of(
                                reservation.getEndDate().toLocalDate(),
                                reservation.getEndTime().toLocalTime()
                        );

                        isPast = endDateTime.isBefore(LocalDateTime.now());
                    }

                    if (isPast) {
                        // Get parking details
                        String slotNumber = reservation.getSlotNumber();
                        ParkingSlot slot = parkingSpaceService.getParkingSlotByNumber(slotNumber);
                        String parkingAddress = "Unknown";
                        if (slot != null) {
                            parkingAddress = parkingSpaceService.getParkingSpaceById(slot.getParkingID()).getParkingAddress();
                        }

                        pastReservations.add(Map.of(
                                "reservation", reservation,
                                "vehicle", vehicle,
                                "parkingAddress", parkingAddress,
                                "slotNumber", slotNumber
                        ));
                    }
                }
            }

            // Sort by start time (most recent first)
            return pastReservations.stream()
                    .sorted(Comparator.<Map<String, Object>, LocalDateTime>comparing(map -> {
                        Reservation res = (Reservation) map.get("reservation");
                        return LocalDateTime.of(
                                res.getStartDate().toLocalDate(),
                                res.getStartTime().toLocalTime()
                        );
                    }).reversed())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving reservation history for user: " + userId, e);
            return List.of(); // Return empty list on error
        }
    }

    /**
     * Get details for a specific reservation
     *
     * @param reservationId The ID of the reservation
     * @param userId The ID of the user requesting the details
     * @return Map containing reservation details or error message
     */
    public Map<String, Object> getReservationDetails(int reservationId, int userId) {
        try {
            Reservation reservation = reservationService.getReservationById(reservationId);
            if (reservation == null) {
                return Map.of(
                        "success", false,
                        "message", "Reservation not found"
                );
            }

            // Verify the reservation belongs to the user
            Vehicle vehicle = vehicleService.getVehicleById(reservation.getVehicleID());
            if (vehicle == null || vehicle.getUserID() != userId) {
                return Map.of(
                        "success", false,
                        "message", "You can only view your own reservations"
                );
            }

            // Get parking details
            String slotNumber = reservation.getSlotNumber();
            ParkingSlot slot = parkingSpaceService.getParkingSlotByNumber(slotNumber);
            if (slot == null) {
                return Map.of(
                        "success", false,
                        "message", "Parking slot information not found"
                );
            }

            String parkingId = slot.getParkingID();
            String parkingAddress = parkingSpaceService.getParkingSpaceById(parkingId).getParkingAddress();
            double costOfParking = parkingSpaceService.getParkingSpaceById(parkingId).getCostOfParking();

            // Calculate duration and cost
            LocalDateTime startDateTime = LocalDateTime.of(
                    reservation.getStartDate().toLocalDate(),
                    reservation.getStartTime().toLocalTime()
            );

            LocalDateTime endDateTime = LocalDateTime.of(
                    reservation.getEndDate().toLocalDate(),
                    reservation.getEndTime().toLocalTime()
            );

            long durationHours = java.time.Duration.between(startDateTime, endDateTime).toHours();
            if (durationHours < 1) durationHours = 1;

            double totalCost = costOfParking * durationHours;

            return Map.of(
                    "success", true,
                    "reservation", reservation,
                    "vehicle", vehicle,
                    "parkingAddress", parkingAddress,
                    "slotNumber", slotNumber,
                    "durationHours", durationHours,
                    "hourlyRate", costOfParking,
                    "totalCost", totalCost,
                    "startDateTime", startDateTime,
                    "endDateTime", endDateTime
            );

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving reservation details: " + reservationId, e);
            return Map.of(
                    "success", false,
                    "message", "Error retrieving reservation details: " + e.getMessage()
            );
        }
    }

    /**
     * Check if extending a reservation is possible
     *
     * @param reservationId The ID of the reservation to extend
     * @param userId The ID of the user requesting the extension
     * @param newEndDateTime The new requested end date and time
     * @return Map containing result of the check
     */
    public Map<String, Object> checkReservationExtension(int reservationId, int userId, LocalDateTime newEndDateTime) {
        try {
            Reservation reservation = reservationService.getReservationById(reservationId);
            if (reservation == null) {
                return Map.of(
                        "success", false,
                        "message", "Reservation not found"
                );
            }

            // Verify the reservation belongs to the user
            Vehicle vehicle = vehicleService.getVehicleById(reservation.getVehicleID());
            if (vehicle == null || vehicle.getUserID() != userId) {
                return Map.of(
                        "success", false,
                        "message", "You can only extend your own reservations"
                );
            }

            // Check if reservation is active
            if (!reservation.getStatus().equals(Constants.RESERVATION_PENDING) &&
                    !reservation.getStatus().equals(Constants.RESERVATION_PAID)) {
                return Map.of(
                        "success", false,
                        "message", "This reservation cannot be extended"
                );
            }

            // Verify new end time is after current end time
            LocalDateTime currentEndDateTime = LocalDateTime.of(
                    reservation.getEndDate().toLocalDate(),
                    reservation.getEndTime().toLocalTime()
            );

            if (!newEndDateTime.isAfter(currentEndDateTime)) {
                return Map.of(
                        "success", false,
                        "message", "New end time must be after current end time"
                );
            }

            // Check if the slot is available for the extended period
            String slotNumber = reservation.getSlotNumber();
            boolean slotAvailable = reservationService.isSlotAvailableForPeriod(
                    slotNumber, currentEndDateTime, newEndDateTime);

            if (!slotAvailable) {
                return Map.of(
                        "success", false,
                        "message", "The slot is not available for the requested extension period"
                );
            }

            // Calculate additional cost
            String parkingId = reservationService.getParkingIdBySlotNumber(slotNumber);
            double hourlyRate = reservationService.getParkingHourlyRate(parkingId);

            long additionalHours = java.time.Duration.between(currentEndDateTime, newEndDateTime).toHours();
            if (additionalHours < 1) additionalHours = 1;

            double additionalCost = hourlyRate * additionalHours;

            return Map.of(
                    "success", true,
                    "message", "Reservation can be extended",
                    "additionalHours", additionalHours,
                    "additionalCost", additionalCost
            );

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking reservation extension: " + reservationId, e);
            return Map.of(
                    "success", false,
                    "message", "Error checking reservation extension: " + e.getMessage()
            );
        }
    }

    /**
     * Extend a reservation
     *
     * @param reservationId The ID of the reservation to extend
     * @param userId The ID of the user requesting the extension
     * @param newEndDateTime The new end date and time
     * @return Map containing result of the extension process
     */
    public Map<String, Object> extendReservation(int reservationId, int userId, LocalDateTime newEndDateTime) {
        try {
            // First check if extension is possible
            Map<String, Object> checkResult = checkReservationExtension(reservationId, userId, newEndDateTime);

            if (!(boolean)checkResult.get("success")) {
                return checkResult;
            }

            // Get the reservation
            Reservation reservation = reservationService.getReservationById(reservationId);

            // Update the reservation end time
            reservation.setEndDate(Date.valueOf(newEndDateTime.toLocalDate()));
            reservation.setEndTime(Time.valueOf(newEndDateTime.toLocalTime()));

            boolean updated = reservationService.updateReservation(reservation);

            if (updated) {
                // Calculate additional payment required
                long additionalHours = (long)checkResult.get("additionalHours");
                double additionalCost = (double)checkResult.get("additionalCost");

                return Map.of(
                        "success", true,
                        "message", "Reservation extended successfully",
                        "newEndDateTime", newEndDateTime,
                        "additionalHours", additionalHours,
                        "additionalCost", additionalCost,
                        "requiresPayment", additionalCost > 0
                );
            } else {
                return Map.of(
                        "success", false,
                        "message", "Failed to extend reservation"
                );
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error extending reservation: " + reservationId, e);
            return Map.of(
                    "success", false,
                    "message", "Error extending reservation: " + e.getMessage()
            );
        }
    }
}