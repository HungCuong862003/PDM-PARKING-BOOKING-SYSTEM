package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.model.ParkingSlot;
import main.java.com.parkeasy.model.Vehicle;
import main.java.com.parkeasy.repository.ReservationRepository;
import main.java.com.parkeasy.repository.ParkingSlotRepository;
import main.java.com.parkeasy.repository.ParkingSpaceRepository;
import main.java.com.parkeasy.repository.VehicleRepository;
import main.java.com.parkeasy.util.Constants;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Service class for reservation operations
 */
public class ReservationService {
    private static final Logger LOGGER = Logger.getLogger(ReservationService.class.getName());

    private final ReservationRepository reservationRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final VehicleRepository vehicleRepository;

    /**
     * Constructor with dependency injection
     */
    public ReservationService(ReservationRepository reservationRepository,
                              ParkingSlotRepository parkingSlotRepository,
                              ParkingSpaceRepository parkingSpaceRepository,
                              VehicleRepository vehicleRepository) {
        this.reservationRepository = reservationRepository;
        this.parkingSlotRepository = parkingSlotRepository;
        this.parkingSpaceRepository = parkingSpaceRepository;
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Default constructor
     */
    public ReservationService() {
        this.reservationRepository = new ReservationRepository();
        this.parkingSlotRepository = new ParkingSlotRepository();
        this.parkingSpaceRepository = new ParkingSpaceRepository();
        this.vehicleRepository = new VehicleRepository();
    }

    /**
     * Create a new reservation
     *
     * @param reservation The reservation to create
     * @return true if successful, false otherwise
     */
    public boolean createReservation(Reservation reservation) {
        try {
            LOGGER.log(Level.INFO, "Creating new reservation for vehicle: {0}", reservation.getVehicleID());

            // Insert the reservation
            int reservationId = reservationRepository.insertReservation(reservation);

            return reservationId > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating reservation", e);
            return false;
        }
    }

    /**
     * Generate a unique reservation ID
     *
     * @return A unique reservation ID
     */
    public int generateReservationId() {
        // A simple implementation to generate a unique ID
        // In a real system, you might want to use a more sophisticated approach
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    /**
     * Get a reservation by ID
     *
     * @param reservationId The ID of the reservation
     * @return The reservation or null if not found
     */
    public Reservation getReservationById(int reservationId) {
        try {
            LOGGER.log(Level.INFO, "Getting reservation by ID: {0}", reservationId);
            return reservationRepository.getReservationById(reservationId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting reservation by ID", e);
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
            LOGGER.log(Level.INFO, "Getting reservations by vehicle ID: {0}", vehicleId);
            return reservationRepository.getReservationsByVehicleId(vehicleId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting reservations by vehicle ID", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get reservations by parking slot number
     *
     * @param slotNumber The number of the parking slot
     * @return List of reservations for the slot
     */
    public List<Reservation> getReservationsByParkingSlotNumber(String slotNumber) {
        try {
            LOGGER.log(Level.INFO, "Getting reservations by parking slot number: {0}", slotNumber);
            return reservationRepository.getReservationsByParkingSlotNumber(slotNumber);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting reservations by parking slot number", e);
            return new ArrayList<>();
        }
    }


    /**
     * Get reservations by user ID
     *
     * @param userId The ID of the user
     * @return List of reservations for the user
     */
    public List<Reservation> getReservationsByUserId(int userId) {
        try {
            LOGGER.log(Level.INFO, "Getting reservations by user ID: {0}", userId);
            return reservationRepository.getReservationsByUserId(userId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting reservations by user ID", e);
            return new ArrayList<>();
        }
    }

    /**
     * Update a reservation
     *
     * @param reservation The reservation with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateReservation(Reservation reservation) {
        try {
            LOGGER.log(Level.INFO, "Updating reservation: {0}", reservation.getReservationID());
            return reservationRepository.updateReservationById(reservation.getReservationID(), reservation);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating reservation", e);
            return false;
        }
    }

    /**
     * Cancel a reservation
     *
     * @param reservationId The ID of the reservation to cancel
     * @return true if successful, false otherwise
     */
    public boolean cancelReservation(int reservationId) {
        try {
            LOGGER.log(Level.INFO, "Cancelling reservation: {0}", reservationId);

            // Get the reservation
            Reservation reservation = getReservationById(reservationId);
            if (reservation == null) {
                LOGGER.log(Level.WARNING, "Reservation not found for cancellation: {0}", reservationId);
                return false;
            }

            // Update status to cancelled
            reservation.setStatus(Constants.RESERVATION_CANCELLED);
            boolean updated = updateReservation(reservation);

            if (updated) {
                // Make the slot available again
                ParkingSlot slot = parkingSlotRepository.findParkingSlotByNumber(reservation.getSlotNumber());
                if (slot != null) {
                    slot.setAvailability(true);
                    parkingSlotRepository.updateParkingSlot(slot);
                }
            }

            return updated;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error cancelling reservation", e);
            return false;
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
            if (slot == null || !slot.getAvailability()) {
                // Slot doesn't exist or is not available
                return false;
            }

            // Get active reservations for this slot
            List<Reservation> reservations = reservationRepository.getReservationsByParkingSlotNumber(slotNumber);

            // Filter for active reservations
            List<Reservation> activeReservations = reservations.stream()
                    .filter(res -> res.getStatus().equals(Constants.RESERVATION_PENDING) ||
                            res.getStatus().equals(Constants.RESERVATION_PAID) ||
                            res.getStatus().equals(Constants.RESERVATION_ACTIVE))
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
     * Get active reservations for a vehicle
     *
     * @param vehicleId The ID of the vehicle
     * @return List of active reservations
     */
    public List<Reservation> getActiveReservationsByVehicleId(String vehicleId) {
        try {
            LOGGER.log(Level.INFO, "Getting active reservations for vehicle: {0}", vehicleId);

            List<Reservation> allReservations = getReservationsByVehicleId(vehicleId);

            // Filter for active reservations
            return allReservations.stream()
                    .filter(res -> res.getStatus().equals(Constants.RESERVATION_PENDING) ||
                            res.getStatus().equals(Constants.RESERVATION_PAID) ||
                            res.getStatus().equals(Constants.RESERVATION_ACTIVE))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting active reservations for vehicle", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get the current active reservation for a vehicle
     *
     * @param vehicleId The ID of the vehicle
     * @return The current active reservation or null if none
     */
    public Reservation getCurrentActiveReservationForVehicle(String vehicleId) {
        try {
            LOGGER.log(Level.INFO, "Getting current active reservation for vehicle: {0}", vehicleId);

            List<Reservation> activeReservations = getActiveReservationsByVehicleId(vehicleId);
            LocalDateTime now = LocalDateTime.now();

            // Find the reservation that is currently active (start time <= now < end time)
            for (Reservation res : activeReservations) {
                LocalDateTime resStart = LocalDateTime.of(
                        res.getStartDate().toLocalDate(),
                        res.getStartTime().toLocalTime()
                );

                LocalDateTime resEnd = LocalDateTime.of(
                        res.getEndDate().toLocalDate(),
                        res.getEndTime().toLocalTime()
                );

                if (!now.isBefore(resStart) && now.isBefore(resEnd)) {
                    return res;
                }
            }

            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting current active reservation for vehicle", e);
            return null;
        }
    }

    /**
     * Check if a vehicle is currently parked
     *
     * @param vehicleId The ID of the vehicle
     * @return true if currently parked, false otherwise
     */
    public boolean isVehicleCurrentlyParked(String vehicleId) {
        return getCurrentActiveReservationForVehicle(vehicleId) != null;
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
            return reservationRepository.getActiveReservationCountByUserId(userId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error counting active reservations for user", e);
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

            List<Reservation> reservations = getReservationsByUserId(userId);
            return reservations.size();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error counting total reservations for user", e);
            return 0;
        }
    }

    /**
     * Count pending payment reservations for a user
     *
     * @param userId The ID of the user
     * @return Count of pending payment reservations
     */
    public int countPendingPaymentReservationsForUser(int userId) {
        try {
            LOGGER.log(Level.INFO, "Counting pending payment reservations for user: {0}", userId);

            List<Reservation> reservations = getReservationsByUserId(userId);

            return (int) reservations.stream()
                    .filter(res -> res.getStatus().equals(Constants.RESERVATION_PENDING))
                    .count();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error counting pending payment reservations for user", e);
            return 0;
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
            LOGGER.log(Level.INFO, "Getting recent reservations for user: {0}, limit: {1}", new Object[]{userId, limit});

            List<Reservation> reservations = getReservationsByUserId(userId);

            // Sort by creation time (most recent first) and limit
            return reservations.stream()
                    .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                    .limit(limit)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting recent reservations for user", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get IDs of recently used parking spaces by a user
     *
     * @param userId The ID of the user
     * @param limit Maximum number of parking space IDs to return
     * @return List of parking space IDs
     */
    public List<String> getRecentParkingSpaceIdsForUser(int userId, int limit) {
        try {
            LOGGER.log(Level.INFO, "Getting recent parking space IDs for user: {0}, limit: {1}", new Object[]{userId, limit});

            List<Reservation> reservations = getRecentReservationsForUser(userId, limit * 2); // Get more to account for duplicates

            // Extract parking IDs from reservations
            List<String> parkingIds = new ArrayList<>();
            for (Reservation res : reservations) {
                String slotNumber = res.getSlotNumber();
                ParkingSlot slot = parkingSlotRepository.findParkingSlotByNumber(slotNumber);

                if (slot != null) {
                    String parkingId = slot.getParkingID();

                    // Add if not already in the list
                    if (!parkingIds.contains(parkingId)) {
                        parkingIds.add(parkingId);

                        // Break if we've reached the limit
                        if (parkingIds.size() >= limit) {
                            break;
                        }
                    }
                }
            }

            return parkingIds;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting recent parking space IDs for user", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get IDs of frequently used parking spaces by a user
     *
     * @param userId The ID of the user
     * @param limit Maximum number of parking space IDs to return
     * @return List of parking space IDs
     */
    public List<String> getFrequentParkingSpaceIdsForUser(int userId, int limit) {
        try {
            LOGGER.log(Level.INFO, "Getting frequent parking space IDs for user: {0}, limit: {1}", new Object[]{userId, limit});

            List<Reservation> reservations = getReservationsByUserId(userId);

            // Count occurrences of each parking ID
            Map<String, Integer> parkingIdCounts = new HashMap<>();

            for (Reservation res : reservations) {
                String slotNumber = res.getSlotNumber();
                ParkingSlot slot = parkingSlotRepository.findParkingSlotByNumber(slotNumber);

                if (slot != null) {
                    String parkingId = slot.getParkingID();
                    parkingIdCounts.put(parkingId, parkingIdCounts.getOrDefault(parkingId, 0) + 1);
                }
            }

            // Sort by count (most frequent first) and get IDs
            return parkingIdCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(limit)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting frequent parking space IDs for user", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get the parking ID by slot number
     *
     * @param slotNumber The number of the slot
     * @return The parking ID or null if not found
     */
    public String getParkingIdBySlotNumber(String slotNumber) {
        try {
            LOGGER.log(Level.FINE, "Getting parking ID by slot number: {0}", slotNumber);
            return parkingSlotRepository.getParkingIdBySlotNumber(slotNumber);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting parking ID by slot number", e);
            return null;
        }
    }

    /**
     * Get the parking address by parking ID
     *
     * @param parkingId The ID of the parking space
     * @return The address or "Unknown" if not found
     */
    public String getParkingAddressByParkingId(String parkingId) {
        try {
            LOGGER.log(Level.FINE, "Getting parking address by parking ID: {0}", parkingId);

            ParkingSpace space = parkingSpaceRepository.getParkingSpaceById(parkingId);
            return space != null ? space.getParkingAddress() : "Unknown";
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting parking address by parking ID", e);
            return "Unknown";
        }
    }

    /**
     * Get the hourly rate for a parking space
     *
     * @param parkingId The ID of the parking space
     * @return The hourly rate or 0 if not found
     */
    public double getParkingHourlyRate(String parkingId) {
        try {
            LOGGER.log(Level.FINE, "Getting hourly rate for parking ID: {0}", parkingId);

            ParkingSpace space = parkingSpaceRepository.getParkingSpaceById(parkingId);
            return space != null ? space.getCostOfParking() : 0.0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting hourly rate for parking", e);
            return 0.0;
        }
    }
    /**
     * Calculate revenue for a specific parking space within a time period
     *
     * @param parkingId Parking space ID
     * @param startTime Start of period
     * @param endTime End of period
     * @return Total revenue
     * @throws SQLException If database error occurs
     */
    public double calculateRevenueForParkingSpace(String parkingId, LocalDateTime startTime, LocalDateTime endTime) throws SQLException {
        // Example implementation - in a real application, this would query the database
        // Sum all reservation payments for the specified parking space and time period
        try {
            // Query database for all completed reservations in this time period for this parking space
            // This is a mockup - implement actual database query in production
            String query = "SELECT SUM(payment_amount) FROM reservations WHERE parking_id = ? " +
                    "AND reservation_time BETWEEN ? AND ? AND status = 'COMPLETED'";

            // For this example, we'll return a default value
            // In a real implementation, execute the query and return the actual sum
            return 0.0; // Return 0 for now - replace with actual implementation
        } catch (Exception e) {
            throw new SQLException("Error calculating revenue: " + e.getMessage(), e);
        }
    }
}