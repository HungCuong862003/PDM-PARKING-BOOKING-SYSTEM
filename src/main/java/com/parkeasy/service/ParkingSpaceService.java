package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.ParkingSlot;
import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.model.ParkingReview;
import main.java.com.parkeasy.model.ParkingSchedule;
import main.java.com.parkeasy.repository.ParkingSlotRepository;
import main.java.com.parkeasy.repository.ParkingSpaceRepository;
import main.java.com.parkeasy.repository.ParkingReviewRepository;
import main.java.com.parkeasy.repository.ParkingScheduleRepository;
import main.java.com.parkeasy.repository.ReservationRepository;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Service class for parking space operations
 */
public class ParkingSpaceService {
    private static final Logger LOGGER = Logger.getLogger(ParkingSpaceService.class.getName());

    private final ParkingSpaceRepository parkingSpaceRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingReviewRepository parkingReviewRepository;
    private final ParkingScheduleRepository parkingScheduleRepository;
    private final ReservationRepository reservationRepository;

    /**
     * Constructor with dependency injection
     */
    public ParkingSpaceService(ParkingSpaceRepository parkingSpaceRepository,
                               ParkingSlotRepository parkingSlotRepository,
                               ParkingReviewRepository parkingReviewRepository,
                               ParkingScheduleRepository parkingScheduleRepository,
                               ReservationRepository reservationRepository) {
        this.parkingSpaceRepository = parkingSpaceRepository;
        this.parkingSlotRepository = parkingSlotRepository;
        this.parkingReviewRepository = parkingReviewRepository;
        this.parkingScheduleRepository = parkingScheduleRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Default constructor
     */
    public ParkingSpaceService() {
        this.parkingSpaceRepository = new ParkingSpaceRepository();
        this.parkingSlotRepository = new ParkingSlotRepository();
        this.parkingReviewRepository = new ParkingReviewRepository();
        this.parkingScheduleRepository = new ParkingScheduleRepository();
        this.reservationRepository = new ReservationRepository();
    }

    /**
     * Get all parking spaces
     *
     * @return List of all parking spaces
     */
    public List<ParkingSpace> getAllParkingSpaces() {
        try {
            LOGGER.log(Level.INFO, "Getting all parking spaces");
            return parkingSpaceRepository.getAllParkingSpaces();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting all parking spaces", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get a parking space by its ID
     *
     * @param parkingId The ID of the parking space
     * @return The parking space or null if not found
     */
    public ParkingSpace getParkingSpaceById(String parkingId) {
        try {
            LOGGER.log(Level.INFO, "Getting parking space by ID: {0}", parkingId);
            return parkingSpaceRepository.getParkingSpaceById(parkingId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting parking space by ID", e);
            return null;
        }
    }

    /**
     * Search for parking spaces by address or description
     *
     * @param searchTerm The search term
     * @return List of matching parking spaces
     */
    public List<ParkingSpace> searchParkingSpaces(String searchTerm) {
        try {
            LOGGER.log(Level.INFO, "Searching parking spaces with term: {0}", searchTerm);
            return parkingSpaceRepository.searchParkingSpaces(searchTerm);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error searching parking spaces", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get all parking spaces managed by an admin
     *
     * @param adminId The ID of the admin
     * @return List of parking spaces managed by the admin
     */
    public List<ParkingSpace> getAllParkingSpacesByAdminId(int adminId) {
        try {
            LOGGER.log(Level.INFO, "Getting parking spaces by admin ID: {0}", adminId);
            return parkingSpaceRepository.getAllParkingSpacesByAdminId(adminId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting parking spaces by admin ID", e);
            return new ArrayList<>();
        }
    }



    /**
     * Get all parking slots for a parking space
     *
     * @param parkingId The ID of the parking space
     * @return List of parking slots
     */
    public List<ParkingSlot> getParkingSlotsByParkingSpaceId(String parkingId) {
        try {
            LOGGER.log(Level.INFO, "Getting parking slots by parking ID: {0}", parkingId);
            return parkingSlotRepository.getParkingSlotsByParkingSpaceId(parkingId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting parking slots by parking ID", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get available parking slots for a parking space
     *
     * @param parkingId The ID of the parking space
     * @return List of available parking slots
     */
    public List<ParkingSlot> getAvailableSlotsByParkingId(String parkingId) {
        try {
            LOGGER.log(Level.INFO, "Getting available slots by parking ID: {0}", parkingId);
            return parkingSlotRepository.getAvailableSlotsByParkingId(parkingId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting available slots by parking ID", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get available parking slots for a specific time range
     *
     * @param parkingId The ID of the parking space
     * @param startDateTime Start date and time
     * @param endDateTime End date and time
     * @return List of available parking slots
     */
    public List<ParkingSlot> getAvailableSlotsForTimeRange(String parkingId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        try {
            LOGGER.log(Level.INFO, "Getting available slots for time range at parking ID: {0}", parkingId);

            // Get all slots for the parking space
            List<ParkingSlot> allSlots = parkingSlotRepository.getParkingSlotsByParkingSpaceId(parkingId);
            List<ParkingSlot> availableSlots = new ArrayList<>();

            // Check if parking space is open during the requested time
            if (!isParkingSpaceOpenAt(parkingId, startDateTime) || !isParkingSpaceOpenAt(parkingId, endDateTime)) {
                LOGGER.log(Level.INFO, "Parking space is not open during the requested time range");
                return availableSlots;
            }

            // Check each slot for availability during the time range
            for (ParkingSlot slot : allSlots) {
                if (slot.getAvailability()) {
                    // Check if there are any overlapping reservations
                    if (isSlotAvailableForPeriod(slot.getSlotNumber(), startDateTime, endDateTime)) {
                        availableSlots.add(slot);
                    }
                }
            }

            return availableSlots;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting available slots for time range", e);
            return new ArrayList<>();
        }
    }

    /**
     * Check if a slot is available for a specific time period
     *
     * @param slotNumber Slot number
     * @param startDateTime Start date and time
     * @param endDateTime End date and time
     * @return true if available, false otherwise
     */
    public boolean isSlotAvailableForPeriod(String slotNumber, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        try {
            // Get the parking slot to check current availability
            ParkingSlot slot = parkingSlotRepository.findParkingSlotByNumber(slotNumber);
            if (slot == null || !slot.getAvailability()) {
                return false;
            }

            // Get active reservations for this slot
            List<Map<String, Object>> reservations = parkingSlotRepository.getActiveReservationsForSlot(slotNumber);

            // Check for overlapping reservations
            for (Map<String, Object> reservation : reservations) {
                LocalDateTime resStart = (LocalDateTime) reservation.get("startDateTime");
                LocalDateTime resEnd = (LocalDateTime) reservation.get("endDateTime");

                // Check for overlap
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
     * Update a parking slot
     *
     * @param parkingSlot The parking slot to update
     * @return true if successful, false otherwise
     */
    public boolean updateParkingSlot(ParkingSlot parkingSlot) {
        try {
            LOGGER.log(Level.INFO, "Updating parking slot: {0}", parkingSlot.getSlotNumber());
            return parkingSlotRepository.updateParkingSlot(parkingSlot);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating parking slot", e);
            return false;
        }
    }

    /**
     * Get the total count of slots managed by an admin
     *
     * @param adminId The ID of the admin
     * @return Total number of slots
     */
    public int getTotalSlotsByAdminId(int adminId) {
        try {
            LOGGER.log(Level.INFO, "Calculating total slots for admin: {0}", adminId);

            // Get all parking spaces managed by this admin
            List<ParkingSpace> parkingSpaces = parkingSpaceRepository.getParkingSpacesByAdminId(adminId);

            int totalSlots = 0;
            for (ParkingSpace space : parkingSpaces) {
                totalSlots += space.getNumberOfSlots();
            }

            return totalSlots;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating total slots for admin", e);
            return 0;
        }
    }

    /**
     * Get the count of occupied slots managed by an admin
     *
     * @param adminId The ID of the admin
     * @return Number of occupied slots
     */
    public int getOccupiedSlotCountByAdminId(int adminId) {
        try {
            LOGGER.log(Level.INFO, "Calculating occupied slots for admin: {0}", adminId);
            return parkingSlotRepository.getOccupiedSlotCountByAdminId(adminId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating occupied slots for admin", e);
            return 0;
        }
    }

    /**
     * Check if a parking space is open at a specific time
     *
     * @param parkingId The ID of the parking space
     * @param dateTime The date and time to check
     * @return true if open, false otherwise
     */
    public boolean isParkingSpaceOpenAt(String parkingId, LocalDateTime dateTime) {
        try {
            LOGGER.log(Level.INFO, "Checking if parking space {0} is open at {1}", new Object[]{parkingId, dateTime});

            // Get the day of week (1-7, with 1 being Sunday)
            int dayOfWeek = dateTime.getDayOfWeek().getValue();
            // Adjust to match our database convention (1 = Sunday, 7 = Saturday)
            if (dayOfWeek == 7) {
                dayOfWeek = 1; // Sunday
            } else {
                dayOfWeek += 1; // Other days
            }

            // Get schedule for this day
            Map<String, Object> schedule = parkingSpaceRepository.getParkingScheduleForDay(parkingId, dayOfWeek);

            if (schedule == null) {
                // No schedule found for this day, assume closed
                return false;
            }

            LocalTime openingTime = (LocalTime) schedule.get("openingTime");
            LocalTime closingTime = (LocalTime) schedule.get("closingTime");
            LocalTime currentTime = dateTime.toLocalTime();

            // Check if current time is within opening hours
            return !currentTime.isBefore(openingTime) && !currentTime.isAfter(closingTime);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking if parking space is open", e);
            return false;
        }
    }

    /**
     * Get reviews for a parking space
     *
     * @param parkingId The ID of the parking space
     * @return List of reviews
     */
    public List<ParkingReview> getReviewsForParkingSpace(String parkingId) {
        try {
            LOGGER.log(Level.INFO, "Getting reviews for parking space: {0}", parkingId);
            return parkingReviewRepository.getReviewsByParkingId(parkingId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting reviews for parking space", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get the average rating for a parking space
     *
     * @param parkingId The ID of the parking space
     * @return Average rating (0-5)
     */
    public double getAverageRatingForParkingSpace(String parkingId) {
        try {
            LOGGER.log(Level.INFO, "Calculating average rating for parking space: {0}", parkingId);
            return parkingReviewRepository.getAverageRatingForParkingSpace(parkingId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating average rating for parking space", e);
            return 0.0;
        }
    }

    /**
     * Get highest rated parking spaces
     *
     * @param limit Maximum number of spaces to return
     * @return List of highest rated parking spaces
     */
    public List<ParkingSpace> getHighestRatedParkingSpaces(int limit) {
        try {
            LOGGER.log(Level.INFO, "Getting highest rated parking spaces, limit: {0}", limit);

            // Get all parking spaces
            List<ParkingSpace> allSpaces = parkingSpaceRepository.getAllParkingSpaces();

            // Calculate average rating for each space
            List<Map.Entry<ParkingSpace, Double>> spacesWithRatings = new ArrayList<>();

            for (ParkingSpace space : allSpaces) {
                double avgRating = getAverageRatingForParkingSpace(space.getParkingID());
                spacesWithRatings.add(Map.entry(space, avgRating));
            }

            // Sort by rating (highest first) and get top spaces
            return spacesWithRatings.stream()
                    .sorted(Map.Entry.<ParkingSpace, Double>comparingByValue().reversed())
                    .limit(limit)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting highest rated parking spaces", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get IDs of highest rated parking spaces
     *
     * @param limit Maximum number of IDs to return
     * @return List of parking space IDs
     */
    public List<String> getHighestRatedParkingSpaceIds(int limit) {
        try {
            List<ParkingSpace> highestRated = getHighestRatedParkingSpaces(limit);
            return highestRated.stream()
                    .map(ParkingSpace::getParkingID)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting highest rated parking space IDs", e);
            return new ArrayList<>();
        }
    }

    /**
     * Insert a new parking slot
     *
     * @param parkingSlot The parking slot to insert
     * @return true if successful, false otherwise
     */
    public boolean insertParkingSlot(ParkingSlot parkingSlot) {
        try {
            LOGGER.log(Level.INFO, "Inserting new parking slot for parking ID: {0}", parkingSlot.getParkingID());
            return parkingSlotRepository.addParkingSlot(parkingSlot);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inserting parking slot", e);
            return false;
        }
    }
    /**
     * Update a parking slot by its slot number, taking a ParkingSlot object
     *
     * @param slotNumber The number of the parking slot
     * @param parkingSlot The updated parking slot object
     * @return true if successful, false otherwise
     */
    public boolean updateParkingSlotByNumber(String slotNumber, ParkingSlot parkingSlot) {
        try {
            LOGGER.log(Level.INFO, "Updating parking slot by number: {0}", slotNumber);

            // Validate input
            if (parkingSlot == null) {
                LOGGER.log(Level.WARNING, "Cannot update slot with null parking slot object");
                return false;
            }

            // Ensure the slot number matches
            if (!slotNumber.equals(parkingSlot.getSlotNumber())) {
                LOGGER.log(Level.WARNING, "Slot number mismatch. Requested: {0}, Object contains: {1}",
                        new Object[]{slotNumber, parkingSlot.getSlotNumber()});

                // Set the correct slot number
                parkingSlot.setSlotNumber(slotNumber);
            }

            // Update the slot using the repository
            boolean updateSuccess = parkingSlotRepository.updateParkingSlot(parkingSlot);

            if (updateSuccess) {
                LOGGER.log(Level.INFO, "Successfully updated parking slot: {0}", slotNumber);
            } else {
                LOGGER.log(Level.WARNING, "Failed to update parking slot: {0}", slotNumber);
            }

            return updateSuccess;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating parking slot by number", e);
            return false;
        }
    }
    /**
     * Get a parking slot by its slot number
     *
     * @param slotNumber The number of the parking slot
     * @return The parking slot or null if not found
     */
    public ParkingSlot getParkingSlotByNumber(String slotNumber) {
        try {
            LOGGER.log(Level.INFO, "Getting parking slot by number: {0}", slotNumber);
            return parkingSlotRepository.findParkingSlotByNumber(slotNumber);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting parking slot by number", e);
            return null;
        }
    }
    /**
     * Delete a parking slot by SlotNumber
     *
     * @param slotNumber The number of the slot to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteParkingSlotById(String slotNumber) {
        try {
            LOGGER.log(Level.INFO, "Deleting parking slot: {0}", slotNumber);
            return parkingSlotRepository.deleteParkingSlot(slotNumber);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting parking slot", e);
            return false;
        }
    }
    /**
     * Get the count of occupied slots for a specific parking space
     *
     * @param parkingId Parking space ID
     * @return Count of occupied slots
     * @throws SQLException If database error occurs
     */
    public int getOccupiedSlotCountByParkingId(String parkingId) throws SQLException {
        // Example implementation - in a real application, this would query the database
        // Count slots that are currently occupied (status = 'OCCUPIED')
        try {
            // Query database for occupied slots in this parking space
            // This is a mockup - implement actual database query in production
            String query = "SELECT COUNT(*) FROM parking_slots WHERE parking_id = ? AND status = 'OCCUPIED'";

            // For this example, we'll return a default value
            // In a real implementation, execute the query and return the actual count
            return 0; // Return 0 for now - replace with actual implementation
        } catch (Exception e) {
            throw new SQLException("Error getting occupied slot count: " + e.getMessage(), e);
        }
    }
}