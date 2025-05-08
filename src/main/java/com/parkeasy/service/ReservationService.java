package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.*;
import main.java.com.parkeasy.repository.*;
import main.java.com.parkeasy.util.Constants;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for reservation operations with balance transfer functionality
 */
public class ReservationService {
    private static final Logger LOGGER = Logger.getLogger(ReservationService.class.getName());

    private final ReservationRepository reservationRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    /**
     * Constructor with dependency injection
     */
    public ReservationService(ReservationRepository reservationRepository,
                              ParkingSlotRepository parkingSlotRepository,
                              ParkingSpaceRepository parkingSpaceRepository,
                              UserRepository userRepository,
                              AdminRepository adminRepository) {
        this.reservationRepository = reservationRepository;
        this.parkingSlotRepository = parkingSlotRepository;
        this.parkingSpaceRepository = parkingSpaceRepository;
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    /**
     * Default constructor
     */
    public ReservationService() {
        this.reservationRepository = new ReservationRepository();
        this.parkingSlotRepository = new ParkingSlotRepository();
        this.parkingSpaceRepository = new ParkingSpaceRepository();
        this.userRepository = new UserRepository();
        this.adminRepository = new AdminRepository();
    }

    /**
     * Create a new reservation with balance transfer
     *
     * @param reservation The reservation to create
     * @param userId The ID of the user making the reservation
     * @param parkingId The ID of the parking space
     * @return true if successful, false otherwise
     */
    public boolean createReservation(Reservation reservation, int userId, String parkingId) {
        try {
            LOGGER.log(Level.INFO, "Creating new reservation for vehicle: {0}", reservation.getVehicleID());

            // Convert reservation times to LocalDateTime for validation
            LocalDateTime startDateTime = LocalDateTime.of(
                    reservation.getStartDate().toLocalDate(),
                    reservation.getStartTime().toLocalTime()
            );

            LocalDateTime endDateTime = LocalDateTime.of(
                    reservation.getEndDate().toLocalDate(),
                    reservation.getEndTime().toLocalTime()
            );

            // Check for overlapping reservations
            if (hasOverlappingReservations(reservation.getVehicleID(), startDateTime, endDateTime)) {
                LOGGER.log(Level.WARNING, "Vehicle already has an overlapping reservation: {0}",
                        reservation.getVehicleID());
                return false;
            }

            // Calculate fee based on duration and hourly rate
            float fee = calculateReservationFee(reservation, parkingId);
            reservation.setFee(fee);


            // Get user and admin
            User user = userRepository.getUserById(userId);
            ParkingSpace parkingSpace = parkingSpaceRepository.getParkingSpaceById(parkingId);
            Admin admin = adminRepository.getAdminById(parkingSpace.getAdminID());

            // Check if user has enough balance
            if (user.getBalance() < fee) {
                LOGGER.log(Level.WARNING, "Insufficient balance for user: {0}", userId);
                return false;
            }

            // Set status to IN_PROCESS
            reservation.setStatus(Constants.RESERVATION_IN_PROCESS);

            // Insert the reservation
            int reservationId = reservationRepository.insertReservation(reservation);

            if (reservationId > 0) {
                // Deduct fee from user's balance
                user.setBalance(user.getBalance() - fee);
                userRepository.updateUser(user);

                // Credit fee to admin's balance
                admin.setBalance(admin.getBalance() + fee);
                adminRepository.updateAdmin(admin);

                return true;
            }

            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating reservation", e);
            return false;
        }
    }

    /**
     * Calculate reservation fee based on duration and hourly rate
     *
     * @param reservation The reservation object
     * @param parkingId The ID of the parking space
     * @return The calculated fee
     */
    private float calculateReservationFee(Reservation reservation, String parkingId) {
        try {
            // Get the parking space details
            ParkingSpace parkingSpace = parkingSpaceRepository.getParkingSpaceById(parkingId);

            // Calculate the duration in hours
            LocalDateTime startDateTime = LocalDateTime.of(
                    reservation.getStartDate().toLocalDate(),
                    reservation.getStartTime().toLocalTime()
            );

            LocalDateTime endDateTime = LocalDateTime.of(
                    reservation.getEndDate().toLocalDate(),
                    reservation.getEndTime().toLocalTime()
            );

            // Calculate the duration in hours (rounded up)
            long durationHours = Duration.between(startDateTime, endDateTime).toHours();
            if (Duration.between(startDateTime, endDateTime).toMinutesPart() > 0) {
                durationHours += 1; // Round up any partial hour
            }

            // Calculate the fee (explicitly using float)
            return parkingSpace.getCostOfParking() * durationHours;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating reservation fee", e);
            return 0.0f; // Note: Using 0.0f for float return type
        }
    }

    /**
     * Check if a parking slot is available for a specific time period
     *
     * @param slotNumber The number of the slot
     * @param startDateTime Start date and time
     * @param endDateTime End date and time
     * @return true if available, false otherwise
     */
    public boolean isSlotAvailableForPeriod(String slotNumber, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        try {
            LOGGER.log(Level.INFO, "Checking slot availability for period: {0}", slotNumber);

            // Get the slot
            ParkingSlot slot = parkingSlotRepository.findParkingSlotByNumber(slotNumber);
            if (slot == null) {
                return false;
            }

            // Get active reservations for this slot
            List<Reservation> reservations = reservationRepository.getReservationsByParkingSlotNumber(slotNumber);

            // Filter for IN_PROCESS reservations only
            List<Reservation> activeReservations = reservations.stream()
                    .filter(res -> res.getStatus().equals(Constants.RESERVATION_IN_PROCESS))
                    .collect(Collectors.toList());

            // Check for overlapping reservations
            for (Reservation res : activeReservations) {
                // Convert SQL dates to LocalDateTime
                LocalDateTime resStart = LocalDateTime.of(
                        res.getStartDate().toLocalDate(),
                        res.getStartTime().toLocalTime()
                );

                LocalDateTime resEnd = LocalDateTime.of(
                        res.getEndDate().toLocalDate(),
                        res.getEndTime().toLocalTime()
                );

                // Check if there is an overlap
                if (!(endDateTime.isBefore(resStart) || startDateTime.isAfter(resEnd))) {
                    // There is an overlap
                    return false;
                }
            }

            // No overlapping reservations found
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking slot availability for period", e);
            return false;
        }
    }
    /**
     * Generate a unique reservation ID
     *
     * @return A unique reservation ID
     */
    public int generateReservationId() {
        try {
            // A simple implementation to generate a unique ID
            return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating reservation ID", e);
            return (int) (Math.random() * Integer.MAX_VALUE);
        }
    }
    /**
     * Get a reservation by its ID
     *
     * @param reservationId The ID of the reservation to retrieve
     * @return The reservation object or null if not found
     */
    public Reservation getReservationById(int reservationId) {
        try {
            LOGGER.log(Level.INFO, "Retrieving reservation with ID: {0}", reservationId);

            // Call the repository method to fetch the reservation
            Reservation reservation = reservationRepository.getReservationById(reservationId);

            if (reservation == null) {
                LOGGER.log(Level.WARNING, "No reservation found with ID: {0}", reservationId);
            }

            return reservation;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error retrieving reservation with ID: " + reservationId, e);
            return null;
        }
    }
    /**
     * Update an existing reservation
     *
     * @param reservation The reservation to update
     * @return true if successful, false otherwise
     */
    /**
     * Update an existing reservation
     *
     * @param reservation The reservation to update
     * @return true if successful, false otherwise
     */
    public boolean updateReservation(Reservation reservation) {
        try {
            LOGGER.log(Level.INFO, "Updating reservation with ID: {0}", reservation.getReservationID());

            // Convert reservation times to LocalDateTime for validation
            LocalDateTime startDateTime = LocalDateTime.of(
                    reservation.getStartDate().toLocalDate(),
                    reservation.getStartTime().toLocalTime()
            );

            LocalDateTime endDateTime = LocalDateTime.of(
                    reservation.getEndDate().toLocalDate(),
                    reservation.getEndTime().toLocalTime()
            );

            // Check for overlapping reservations, excluding this reservation's ID
            if (hasOverlappingReservations(reservation.getVehicleID(), startDateTime, endDateTime,
                    reservation.getReservationID())) {
                LOGGER.log(Level.WARNING, "Vehicle has an overlapping reservation: {0}",
                        reservation.getVehicleID());
                return false;
            }

            // Call the repository method to update the reservation
            return reservationRepository.updateReservationById(reservation.getReservationID(), reservation);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating reservation", e);
            return false;
        }
    }
    /**
     * Get parking ID by slot number
     *
     * @param slotNumber The parking slot number
     * @return The parking ID or null if not found
     */
    public String getParkingIdBySlotNumber(String slotNumber) {
        try {
            LOGGER.log(Level.INFO, "Retrieving parking ID for slot: {0}", slotNumber);
            ParkingSlot slot = parkingSlotRepository.findParkingSlotByNumber(slotNumber);
            if (slot != null) {
                return slot.getParkingID();
            }
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving parking ID for slot: " + slotNumber, e);
            return null;
        }
    }

    /**
     * Get parking hourly rate by parking ID
     *
     * @param parkingId The ID of the parking space
     * @return The hourly rate or 0.0 if not found
     */
    public float getParkingHourlyRate(String parkingId) {
        try {
            LOGGER.log(Level.INFO, "Retrieving hourly rate for parking: {0}", parkingId);
            ParkingSpace parkingSpace = parkingSpaceRepository.getParkingSpaceById(parkingId);
            if (parkingSpace != null) {
                return parkingSpace.getCostOfParking();
            }
            return 0.0F;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving hourly rate for parking: " + parkingId, e);
            return 0.0F;
        }
    }

    /**
     * Get parking address by parking ID
     *
     * @param parkingId The ID of the parking space
     * @return The address or null if not found
     */
    public String getParkingAddressByParkingId(String parkingId) {
        try {
            LOGGER.log(Level.INFO, "Retrieving address for parking: {0}", parkingId);
            ParkingSpace parkingSpace = parkingSpaceRepository.getParkingSpaceById(parkingId);
            if (parkingSpace != null) {
                return parkingSpace.getParkingAddress();  // Using getParkingAddress() instead of getAddress()
            }
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving address for parking: " + parkingId, e);
            return null;
        }
    }
    /**
     * Get reservations by vehicle ID
     *
     * @param vehicleId The ID of the vehicle
     * @return List of reservations for the vehicle
     */
    public List<Reservation> getReservationsByVehicleId(String vehicleId) {
        try {
            LOGGER.log(Level.INFO, "Retrieving reservations for vehicle: {0}", vehicleId);
            return reservationRepository.getReservationsByVehicleId(vehicleId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving reservations for vehicle: " + vehicleId, e);
            return new ArrayList<>(); // Return empty list on error
        }
    }

    /**
     * Get recent reservations for a user
     *
     * @param userId The ID of the user
     * @param limit Maximum number of reservations to return
     * @return List of recent reservations
     */
    public List<Reservation> getRecentReservationsForUser(int userId, int limit) {
        try {
            LOGGER.log(Level.INFO, "Retrieving recent reservations for user: {0}", userId);

            // Get all reservations for the user
            List<Reservation> allReservations = reservationRepository.getReservationsByUserId(userId);

            // Sort by creation date (newest first)
            List<Reservation> sortedReservations = allReservations.stream()
                    .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                    .limit(limit)
                    .collect(Collectors.toList());

            return sortedReservations;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving recent reservations for user: " + userId, e);
            return new ArrayList<>(); // Return empty list on error
        }
    }

    /**
     * Get recent parking space IDs used by a user
     *
     * @param userId The ID of the user
     * @param limit Maximum number of parking space IDs to return
     * @return List of recent parking space IDs
     */
    public List<String> getRecentParkingSpaceIdsForUser(int userId, int limit) {
        try {
            LOGGER.log(Level.INFO, "Retrieving recent parking space IDs for user: {0}", userId);

            // Get recent reservations for the user
            List<Reservation> recentReservations = getRecentReservationsForUser(userId, limit * 2); // Get more to account for duplicates

            // Extract unique parking space IDs
            List<String> parkingIds = new ArrayList<>();
            for (Reservation reservation : recentReservations) {
                String slotNumber = reservation.getSlotNumber();
                String parkingId = getParkingIdBySlotNumber(slotNumber);

                if (parkingId != null && !parkingIds.contains(parkingId)) {
                    parkingIds.add(parkingId);

                    // Break if we reached the limit
                    if (parkingIds.size() >= limit) {
                        break;
                    }
                }
            }

            return parkingIds;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving recent parking space IDs for user: " + userId, e);
            return new ArrayList<>(); // Return empty list on error
        }
    }

    /**
     * Count active reservations for a user
     *
     * @param userId The ID of the user
     * @return Count of active reservations
     */
    public int countActiveReservationsForUser(int userId) {
        try {
            LOGGER.log(Level.INFO, "Counting active reservations for user: {0}", userId);

            // Use repository method if available
            if (reservationRepository != null) {
                return reservationRepository.getActiveReservationCountByUserId(userId);
            }

            // Fallback: get all reservations and count active ones
            List<Reservation> allReservations = reservationRepository.getReservationsByUserId(userId);

            return (int) allReservations.stream()
                    .filter(r -> r.getStatus().equals(Constants.RESERVATION_IN_PROCESS) ||
                            r.getStatus().equals(Constants.RESERVATION_PAID))
                    .count();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error counting active reservations for user: " + userId, e);
            return 0;
        }
    }

    /**
     * Count total reservations for a user
     *
     * @param userId The ID of the user
     * @return Count of total reservations
     */
    public int countTotalReservationsForUser(int userId) {
        try {
            LOGGER.log(Level.INFO, "Counting total reservations for user: {0}", userId);

            // Get all reservations for the user
            List<Reservation> allReservations = reservationRepository.getReservationsByUserId(userId);

            return allReservations.size();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error counting total reservations for user: " + userId, e);
            return 0;
        }
    }

    /**
     * Get frequent parking space IDs used by a user
     *
     * @param userId The ID of the user
     * @param limit Maximum number of parking space IDs to return
     * @return List of frequent parking space IDs
     */
    public List<String> getFrequentParkingSpaceIdsForUser(int userId, int limit) {
        try {
            LOGGER.log(Level.INFO, "Retrieving frequent parking space IDs for user: {0}", userId);

            // Get all reservations for the user
            List<Reservation> allReservations = reservationRepository.getReservationsByUserId(userId);

            // Count occurrences of each parking space
            Map<String, Integer> parkingSpaceCounts = new HashMap<>();

            for (Reservation reservation : allReservations) {
                String slotNumber = reservation.getSlotNumber();
                String parkingId = getParkingIdBySlotNumber(slotNumber);

                if (parkingId != null) {
                    parkingSpaceCounts.put(parkingId, parkingSpaceCounts.getOrDefault(parkingId, 0) + 1);
                }
            }

            // Sort by count (most frequent first) and get the top 'limit' parking spaces
            return parkingSpaceCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(limit)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving frequent parking space IDs for user: " + userId, e);
            return new ArrayList<>(); // Return empty list on error
        }
    }

    /**
     * Calculate revenue for a parking space within a specified time period
     *
     * @param parkingId The ID of the parking space
     * @param startDateTime Start date and time of the period
     * @param endDateTime End date and time of the period
     * @return Total revenue generated in the specified period
     */
    public float calculateRevenueForParkingSpace(String parkingId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        try {
            LOGGER.log(Level.INFO, "Calculating revenue for parking space: {0} between {1} and {2}",
                    new Object[]{parkingId, startDateTime, endDateTime});

            // Convert LocalDateTime to java.util.Date for the repository method
            java.util.Date startDate = java.sql.Timestamp.valueOf(startDateTime);
            java.util.Date endDate = java.sql.Timestamp.valueOf(endDateTime);

            // Get reservations for the parking space in the given time period
            List<Reservation> reservations = reservationRepository.getReservationsByParkingIdAndDateRange(
                    parkingId, startDate, endDate);

            // Calculate total revenue
            float totalRevenue = 0.0F;
            for (Reservation reservation : reservations) {
                // Only count completed or paid reservations
                if (reservation.getStatus().equals(Constants.RESERVATION_COMPLETE) ||
                        reservation.getStatus().equals(Constants.RESERVATION_PAID)) {
                    totalRevenue += reservation.getFee();
                }
            }

            LOGGER.log(Level.INFO, "Total revenue for parking space {0}: {1}",
                    new Object[]{parkingId, totalRevenue});

            return totalRevenue;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating revenue for parking space: " + parkingId, e);
            return 0.0F;
        }
    }
    /**
     * Get all reservations for a specific user
     *
     * @param userId The ID of the user
     * @return List of reservations for the user
     */
    public List<Reservation> getReservationsByUserId(int userId) {
        try {
            LOGGER.log(Level.INFO, "Retrieving reservations for user: {0}", userId);

            // Call the repository method to get all reservations for the user
            List<Reservation> reservations = reservationRepository.getReservationsByUserId(userId);

            if (reservations.isEmpty()) {
                LOGGER.log(Level.INFO, "No reservations found for user: {0}", userId);
            } else {
                LOGGER.log(Level.INFO, "Found {0} reservations for user: {1}",
                        new Object[]{reservations.size(), userId});
            }

            return reservations;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving reservations for user: " + userId, e);
            return new ArrayList<>(); // Return empty list on error
        }
    }
    /**
     * Check if a vehicle is currently parked
     *
     * @param vehicleId The ID of the vehicle
     * @return true if currently parked, false otherwise
     */
    public boolean isVehicleCurrentlyParked(String vehicleId) {
        try {
            LOGGER.log(Level.INFO, "Checking if vehicle is currently parked: {0}", vehicleId);

            // Get the current active reservation for this vehicle
            Reservation activeReservation = getCurrentActiveReservationForVehicle(vehicleId);

            // If there is an active reservation, the vehicle is currently parked
            return activeReservation != null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking if vehicle is currently parked: " + vehicleId, e);
            return false;
        }
    }

    /**
     * Get the current active reservation for a vehicle
     *
     * @param vehicleId The ID of the vehicle
     * @return The active reservation or null if none exists
     */
    public Reservation getCurrentActiveReservationForVehicle(String vehicleId) {
        try {
            LOGGER.log(Level.INFO, "Retrieving current active reservation for vehicle: {0}", vehicleId);

            // Get all reservations for this vehicle
            List<Reservation> reservations = getReservationsByVehicleId(vehicleId);

            // Current time
            LocalDateTime now = LocalDateTime.now();

            // Find the active reservation (status is IN_PROCESS or PAID, and current time is between start and end)
            for (Reservation reservation : reservations) {
                // Only consider active reservations
                if (reservation.getStatus().equals(Constants.RESERVATION_IN_PROCESS) ||
                        reservation.getStatus().equals(Constants.RESERVATION_PAID)) {

                    // Convert to LocalDateTime for comparison
                    LocalDateTime startDateTime = LocalDateTime.of(
                            reservation.getStartDate().toLocalDate(),
                            reservation.getStartTime().toLocalTime()
                    );

                    LocalDateTime endDateTime = LocalDateTime.of(
                            reservation.getEndDate().toLocalDate(),
                            reservation.getEndTime().toLocalTime()
                    );

                    // Check if current time is within the reservation period
                    if (now.isAfter(startDateTime) && now.isBefore(endDateTime)) {
                        LOGGER.log(Level.INFO, "Found active reservation for vehicle: {0}", vehicleId);
                        return reservation;
                    }
                }
            }

            LOGGER.log(Level.INFO, "No active reservation found for vehicle: {0}", vehicleId);
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving current active reservation for vehicle: " + vehicleId, e);
            return null;
        }
    }
    /**
     * Check if a vehicle has any overlapping reservations for a specific time period
     * This ensures a vehicle cannot be parked in multiple slots simultaneously
     *
     * @param vehicleId The ID of the vehicle to check
     * @param startDateTime Start date and time for the new reservation
     * @param endDateTime End date and time for the new reservation
     * @param excludeReservationId Optional reservation ID to exclude from check (for updates)
     * @return true if there are overlapping reservations, false otherwise
     */
    public boolean hasOverlappingReservations(String vehicleId, LocalDateTime startDateTime,
                                              LocalDateTime endDateTime, Integer excludeReservationId) {
        try {
            LOGGER.log(Level.INFO, "Checking for overlapping reservations for vehicle: {0}", vehicleId);

            if (vehicleId == null || startDateTime == null || endDateTime == null) {
                LOGGER.log(Level.WARNING, "Invalid parameters for overlapping reservation check");
                return false;
            }

            // Get all active or future reservations for this vehicle
            List<Reservation> reservations = getReservationsByVehicleId(vehicleId);

            // Filter for active or upcoming reservations only
            List<Reservation> activeReservations = reservations.stream()
                    .filter(res ->
                            res.getStatus().equals(Constants.RESERVATION_IN_PROCESS) ||
                                    res.getStatus().equals(Constants.RESERVATION_PAID))
                    .collect(Collectors.toList());

            // Check for overlapping time periods
            for (Reservation res : activeReservations) {
                // Skip the current reservation if updating
                if (excludeReservationId != null && res.getReservationID() == excludeReservationId) {
                    continue;
                }

                // Convert to LocalDateTime for comparison
                LocalDateTime resStart = LocalDateTime.of(
                        res.getStartDate().toLocalDate(),
                        res.getStartTime().toLocalTime()
                );

                LocalDateTime resEnd = LocalDateTime.of(
                        res.getEndDate().toLocalDate(),
                        res.getEndTime().toLocalTime()
                );

                // Check if there is an overlap
                // Two reservations overlap if one starts before the other ends and ends after the other starts
                if (!(endDateTime.isBefore(resStart) || startDateTime.isAfter(resEnd))) {
                    LOGGER.log(Level.INFO, "Found overlapping reservation: {0}", res.getReservationID());
                    return true;
                }
            }

            LOGGER.log(Level.INFO, "No overlapping reservations found for vehicle: {0}", vehicleId);
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking for overlapping reservations", e);
            return true; // Return true on error to be safe (prevent potentially invalid reservations)
        }
    }
    /**
     * Update the availability status of a parking slot
     *
     * @param slotNumber The number of the slot to update
     * @param isAvailable The new availability status (true for available, false for occupied)
     * @return true if the update was successful, false otherwise
     */
    public boolean updateSlotAvailability(String slotNumber, boolean isAvailable) {
        try {
            LOGGER.log(Level.INFO, "Updating availability for slot {0} to {1}",
                    new Object[]{slotNumber, isAvailable ? "available" : "unavailable"});

            // Get the current slot
            ParkingSlot slot = parkingSlotRepository.findParkingSlotByNumber(slotNumber);

            if (slot == null) {
                LOGGER.log(Level.WARNING, "Parking slot not found: {0}", slotNumber);
                return false;
            }

            // Check if there are any active reservations for this slot before making it available
            if (isAvailable) {
                LocalDateTime now = LocalDateTime.now();
                List<Reservation> activeReservations = reservationRepository.getReservationsByParkingSlotNumber(slotNumber);

                boolean hasActiveReservation = activeReservations.stream()
                        .anyMatch(res -> {
                            // Only consider active reservations (not completed or cancelled)
                            if (res.getStatus().equals(Constants.RESERVATION_IN_PROCESS) ||
                                    res.getStatus().equals(Constants.RESERVATION_PAID)) {

                                // Convert to LocalDateTime for comparison
                                LocalDateTime startDateTime = LocalDateTime.of(
                                        res.getStartDate().toLocalDate(),
                                        res.getStartTime().toLocalTime()
                                );

                                LocalDateTime endDateTime = LocalDateTime.of(
                                        res.getEndDate().toLocalDate(),
                                        res.getEndTime().toLocalTime()
                                );

                                // Check if current time is within the reservation period
                                return now.isAfter(startDateTime) && now.isBefore(endDateTime);
                            }
                            return false;
                        });

                // If there's still an active reservation, don't make the slot available
                if (hasActiveReservation) {
                    LOGGER.log(Level.WARNING,
                            "Cannot make slot {0} available because it has an active reservation",
                            slotNumber);
                    return false;
                }
            }

            // Update the slot availability
            slot.setAvailability(isAvailable);
            boolean updated = parkingSlotRepository.updateParkingSlot(slot);

            if (updated) {
                LOGGER.log(Level.INFO, "Successfully updated slot {0} availability to {1}",
                        new Object[]{slotNumber, isAvailable ? "available" : "unavailable"});
                return true;
            } else {
                LOGGER.log(Level.WARNING, "Failed to update slot {0} availability", slotNumber);
                return false;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating slot availability: " + slotNumber, e);
            return false;
        }
    }

    /**
     * Overloaded method without excludeReservationId for new reservations
     */
    public boolean hasOverlappingReservations(String vehicleId, LocalDateTime startDateTime,
                                              LocalDateTime endDateTime) {
        return hasOverlappingReservations(vehicleId, startDateTime, endDateTime, null);
    }
    /**
     * Get active reservations for a vehicle
     *
     * @param vehicleId The ID of the vehicle
     * @return List of active reservations
     */
    public List<Reservation> getActiveReservationsByVehicleId(String vehicleId) {
        try {
            LOGGER.log(Level.INFO, "Retrieving active reservations for vehicle: {0}", vehicleId);

            // Get all reservations for this vehicle
            List<Reservation> allReservations = getReservationsByVehicleId(vehicleId);

            // Current time
            LocalDateTime now = LocalDateTime.now();

            // Filter for active or future reservations
            List<Reservation> activeReservations = allReservations.stream()
                    .filter(reservation -> {
                        // Only consider active or paid reservations
                        if (reservation.getStatus().equals(Constants.RESERVATION_IN_PROCESS) ||
                                reservation.getStatus().equals(Constants.RESERVATION_PAID)) {

                            // Convert to LocalDateTime for comparison
                            LocalDateTime endDateTime = LocalDateTime.of(
                                    reservation.getEndDate().toLocalDate(),
                                    reservation.getEndTime().toLocalTime()
                            );

                            // Check if reservation has not yet ended
                            return now.isBefore(endDateTime);
                        }
                        return false;
                    })
                    .collect(Collectors.toList());

            return activeReservations;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving active reservations for vehicle: " + vehicleId, e);
            return new ArrayList<>(); // Return empty list on error
        }
    }
}