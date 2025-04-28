package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.repository.ReservationRepository;
import main.java.com.parkeasy.repository.ParkingSlotRepository;
import main.java.com.parkeasy.repository.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service class for handling reservation operations in the ParkEasy system.
 * Provides functionality for creating, retrieving, updating, and canceling
 * parking reservations,
 * as well as checking availability and managing reservations for specific
 * parking spaces.
 */
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final PaymentRepository paymentRepository;

    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_CANCELLED = "CANCELLED";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Constructor for ReservationService.
     */
    public ReservationService() {
        this.reservationRepository = new ReservationRepository();
        this.parkingSlotRepository = new ParkingSlotRepository();
        this.paymentRepository = new PaymentRepository();
    }

    /**
     * Inserts a new reservation into the database.
     *
     * @param reservation The reservation to insert
     * @return true if insertion successful, false otherwise
     */
    public boolean insertReservation(Reservation reservation) {
        if (reservation == null) {
            System.err.println("Cannot insert null reservation");
            return false;
        }

        if (!validateReservation(reservation)) {
            System.err.println("Invalid reservation data");
            return false;
        }

        try {
            // Check if slot is available for the requested time
            if (!isSlotAvailable(reservation.getSlotID(),
                    reservation.getStartDate(),
                    reservation.getStartTime(),
                    reservation.getEndDate(),
                    reservation.getEndTime())) {
                System.err.println("Slot not available for the requested time period");
                return false;
            }

            boolean success = reservationRepository.insertReservation(reservation);

            if (success) {
                // Update slot availability to false
                parkingSlotRepository.updateSlotAvailability(reservation.getSlotID(), false);
            }

            return success;
        } catch (Exception e) {
            System.err.println("Error inserting reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Validates reservation data.
     *
     * @param reservation The reservation to validate
     * @return true if reservation data is valid, false otherwise
     */
    private boolean validateReservation(Reservation reservation) {
        if (reservation.getSlotID() <= 0) {
            System.err.println("Invalid slot ID");
            return false;
        }

        if (reservation.getUserID() <= 0) {
            System.err.println("Invalid user ID");
            return false;
        }

        if (reservation.getVehicleID() == null || reservation.getVehicleID().trim().isEmpty()) {
            System.err.println("Invalid vehicle ID");
            return false;
        }

        if (!isValidDateFormat(reservation.getStartDate()) || !isValidTimeFormat(reservation.getStartTime())) {
            System.err.println("Invalid start date/time format");
            return false;
        }

        if (!isValidDateFormat(reservation.getEndDate()) || !isValidTimeFormat(reservation.getEndTime())) {
            System.err.println("Invalid end date/time format");
            return false;
        }

        // Check that end date/time is after start date/time
        LocalDateTime startDateTime = parseDateTime(reservation.getStartDate(), reservation.getStartTime());
        LocalDateTime endDateTime = parseDateTime(reservation.getEndDate(), reservation.getEndTime());

        if (startDateTime == null || endDateTime == null || !endDateTime.isAfter(startDateTime)) {
            System.err.println("End date/time must be after start date/time");
            return false;
        }

        // Check that start date/time is not in the past
        if (startDateTime.isBefore(LocalDateTime.now())) {
            System.err.println("Start date/time cannot be in the past");
            return false;
        }

        return true;
    }

    /**
     * Checks if a date string has valid format.
     *
     * @param date The date string to check
     * @return true if format is valid, false otherwise
     */
    private boolean isValidDateFormat(String date) {
        if (date == null || date.trim().isEmpty()) {
            return false;
        }

        try {
            LocalDate.parse(date, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Checks if a time string has valid format.
     *
     * @param time The time string to check
     * @return true if format is valid, false otherwise
     */
    private boolean isValidTimeFormat(String time) {
        if (time == null || time.trim().isEmpty()) {
            return false;
        }

        try {
            LocalDateTime.parse("2020-01-01T" + time, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Parses date and time strings to LocalDateTime.
     *
     * @param date The date string
     * @param time The time string
     * @return LocalDateTime object, or null if parsing fails
     */
    private LocalDateTime parseDateTime(String date, String time) {
        try {
            LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);
            int hour = Integer.parseInt(time.split(":")[0]);
            int minute = Integer.parseInt(time.split(":")[1]);
            return localDate.atTime(hour, minute);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets a reservation by its ID.
     *
     * @param reservationID The ID of the reservation to retrieve
     * @return The reservation if found, null otherwise
     */
    public Reservation getReservationById(int reservationID) {
        if (reservationID <= 0) {
            System.err.println("Invalid reservation ID");
            return null;
        }

        try {
            return reservationRepository.getReservationById(reservationID);
        } catch (Exception e) {
            System.err.println("Error retrieving reservation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets all reservations for a specific vehicle.
     *
     * @param vehicleID The ID of the vehicle
     * @return List of reservations for the vehicle
     */
    public List<Reservation> getReservationsByVehicleId(String vehicleID) {
        if (vehicleID == null || vehicleID.trim().isEmpty()) {
            System.err.println("Invalid vehicle ID");
            return Collections.emptyList();
        }

        try {
            return reservationRepository.getReservationsByVehicleId(vehicleID);
        } catch (Exception e) {
            System.err.println("Error retrieving reservations by vehicle: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Gets all reservations for a specific parking slot.
     *
     * @param slotID The ID of the parking slot
     * @return List of reservations for the slot
     */
    public List<Reservation> getReservationsBySlotId(int slotID) {
        if (slotID <= 0) {
            System.err.println("Invalid slot ID");
            return Collections.emptyList();
        }

        try {
            return reservationRepository.getReservationsByParkingSlotId(slotID);
        } catch (Exception e) {
            System.err.println("Error retrieving reservations by slot: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Gets all reservations for a specific user.
     *
     * @param userID The ID of the user
     * @return List of reservations for the user
     */
    public List<Reservation> getReservationsByUser(int userID) {
        if (userID <= 0) {
            System.err.println("Invalid user ID");
            return Collections.emptyList();
        }

        try {
            return reservationRepository.getReservationsByUserId(userID);
        } catch (Exception e) {
            System.err.println("Error retrieving reservations by user: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Gets all reservations for a specific parking space.
     *
     * @param parkingID The ID of the parking space
     * @return List of reservations for the parking space
     */
    public List<Reservation> getReservationsByParkingSpace(String parkingID) {
        if (parkingID == null || parkingID.trim().isEmpty()) {
            System.err.println("Invalid parking ID");
            return Collections.emptyList();
        }

        try {
            List<Integer> slotIDs = parkingSlotRepository.getListOfSlotIdsByParkingId(parkingID);
            List<Reservation> reservations = new ArrayList<>();

            for (Integer slotID : slotIDs) {
                List<Reservation> slotReservations = getReservationsBySlotId(slotID);
                reservations.addAll(slotReservations);
            }

            return reservations;
        } catch (Exception e) {
            System.err.println("Error retrieving reservations by parking space: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Gets reservations within a specific date range for a list of parking spaces.
     *
     * @param parkingSpaces List of parking spaces
     * @param startDate     Start date for the range (YYYY-MM-DD)
     * @param endDate       End date for the range (YYYY-MM-DD)
     * @return List of reservations within the date range
     */
    public List<Reservation> getReservationsByDateRange(List<ParkingSpace> parkingSpaces, String startDate,
            String endDate) {
        if (parkingSpaces == null || parkingSpaces.isEmpty() ||
                !isValidDateFormat(startDate) || !isValidDateFormat(endDate)) {
            System.err.println("Invalid parameters for date range search");
            return Collections.emptyList();
        }

        try {
            List<Reservation> allReservations = new ArrayList<>();

            for (ParkingSpace space : parkingSpaces) {
                List<Integer> slotIDs = parkingSlotRepository.getListOfSlotIdsByParkingId(space.getParkingID());
                for (Integer slotID : slotIDs) {
                    List<Reservation> slotReservations = reservationRepository.getReservationsByDateRange(
                            slotID, startDate, endDate);
                    allReservations.addAll(slotReservations);
                }
            }

            return allReservations;
        } catch (Exception e) {
            System.err.println("Error retrieving reservations by date range: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Deletes a reservation by its ID.
     *
     * @param reservationID The ID of the reservation to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteReservationById(int reservationID) {
        if (reservationID <= 0) {
            System.err.println("Invalid reservation ID");
            return false;
        }

        try {
            Reservation reservation = getReservationById(reservationID);
            if (reservation == null) {
                System.err.println("Reservation not found");
                return false;
            }

            boolean success = reservationRepository.deleteReservationById(reservationID);

            if (success && STATUS_ACTIVE.equals(reservation.getStatus())) {
                // Free up the parking slot if reservation was active
                parkingSlotRepository.updateSlotAvailability(reservation.getSlotID(), true);
            }

            return success;
        } catch (Exception e) {
            System.err.println("Error deleting reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates a reservation by its ID.
     *
     * @param reservationID The ID of the reservation to update
     * @param reservation   The updated reservation data
     * @return true if update successful, false otherwise
     */
    public boolean updateReservationById(int reservationID, Reservation reservation) {
        if (reservationID <= 0 || reservation == null) {
            System.err.println("Invalid parameters for reservation update");
            return false;
        }

        if (reservation.getReservationID() != reservationID) {
            System.err.println("Reservation ID mismatch");
            return false;
        }

        if (!validateReservation(reservation)) {
            System.err.println("Invalid reservation data for update");
            return false;
        }

        try {
            // Get current reservation
            Reservation currentReservation = getReservationById(reservationID);
            if (currentReservation == null) {
                System.err.println("Reservation not found");
                return false;
            }

            // If we're changing the slot or time, check availability
            if (reservation.getSlotID() != currentReservation.getSlotID() ||
                    !reservation.getStartDate().equals(currentReservation.getStartDate()) ||
                    !reservation.getStartTime().equals(currentReservation.getStartTime()) ||
                    !reservation.getEndDate().equals(currentReservation.getEndDate()) ||
                    !reservation.getEndTime().equals(currentReservation.getEndTime())) {

                // Check if new slot is available
                if (!isSlotAvailable(reservation.getSlotID(),
                        reservation.getStartDate(),
                        reservation.getStartTime(),
                        reservation.getEndDate(),
                        reservation.getEndTime())) {
                    System.err.println("New slot/time not available");
                    return false;
                }
            }

            boolean success = reservationRepository.updateReservationById(reservationID, reservation);

            if (success) {
                // If status changed from active, update slot availability
                if (STATUS_ACTIVE.equals(currentReservation.getStatus()) &&
                        !STATUS_ACTIVE.equals(reservation.getStatus())) {
                    parkingSlotRepository.updateSlotAvailability(currentReservation.getSlotID(), true);
                }

                // If slot changed, update old and new slot availability
                if (reservation.getSlotID() != currentReservation.getSlotID()) {
                    if (STATUS_ACTIVE.equals(currentReservation.getStatus())) {
                        parkingSlotRepository.updateSlotAvailability(currentReservation.getSlotID(), true);
                    }
                    if (STATUS_ACTIVE.equals(reservation.getStatus())) {
                        parkingSlotRepository.updateSlotAvailability(reservation.getSlotID(), false);
                    }
                }
            }

            return success;
        } catch (Exception e) {
            System.err.println("Error updating reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cancels a reservation.
     *
     * @param reservationID The ID of the reservation to cancel
     * @return true if cancellation successful, false otherwise
     */
    public boolean cancelReservation(int reservationID) {
        if (reservationID <= 0) {
            System.err.println("Invalid reservation ID");
            return false;
        }

        try {
            Reservation reservation = getReservationById(reservationID);
            if (reservation == null) {
                System.err.println("Reservation not found");
                return false;
            }

            if (!STATUS_ACTIVE.equals(reservation.getStatus())) {
                System.err.println("Cannot cancel non-active reservation");
                return false;
            }

            reservation.setStatus(STATUS_CANCELLED);
            boolean updated = updateReservationById(reservationID, reservation);

            if (updated) {
                // Update slot availability
                return parkingSlotRepository.updateSlotAvailability(reservation.getSlotID(), true);
            }

            return false;
        } catch (Exception e) {
            System.err.println("Error cancelling reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets payment amount for a reservation.
     *
     * @param reservationID The ID of the reservation
     * @return The payment amount, or BigDecimal.ZERO if no payment found
     */
    public BigDecimal getPaymentAmount(int reservationID) {
        if (reservationID <= 0) {
            System.err.println("Invalid reservation ID");
            return BigDecimal.ZERO;
        }

        try {
            BigDecimal amount = paymentRepository.getPaymentAmountByReservation(reservationID);
            return amount != null ? amount : BigDecimal.ZERO;
        } catch (Exception e) {
            System.err.println("Error retrieving payment amount: " + e.getMessage());
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    /**
     * Gets the parking space ID from a slot ID.
     *
     * @param slotID The ID of the parking slot
     * @return The parking space ID if found, null otherwise
     */
    public String getParkingIDFromSlot(int slotID) {
        if (slotID <= 0) {
            System.err.println("Invalid slot ID");
            return null;
        }

        try {
            return parkingSlotRepository.getParkingIdBySlotId(slotID);
        } catch (Exception e) {
            System.err.println("Error retrieving parking ID from slot: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Checks if a parking slot is available for a given time period.
     *
     * @param slotID    The ID of the parking slot
     * @param startDate Start date (YYYY-MM-DD)
     * @param startTime Start time (HH:MM)
     * @param endDate   End date (YYYY-MM-DD)
     * @param endTime   End time (HH:MM)
     * @return true if slot is available, false otherwise
     */
    public boolean isSlotAvailable(int slotID, String startDate, String startTime, String endDate, String endTime) {
        if (slotID <= 0 ||
                !isValidDateFormat(startDate) || !isValidTimeFormat(startTime) ||
                !isValidDateFormat(endDate) || !isValidTimeFormat(endTime)) {
            System.err.println("Invalid parameters for slot availability check");
            return false;
        }

        try {
            return reservationRepository.isSlotAvailability(slotID, startDate, startTime, endDate, endTime);
        } catch (Exception e) {
            System.err.println("Error checking slot availability: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Finds available slots in a parking space for a given time period.
     *
     * @param parkingID The ID of the parking space
     * @param startDate Start date (YYYY-MM-DD)
     * @param startTime Start time (HH:MM)
     * @param endDate   End date (YYYY-MM-DD)
     * @param endTime   End time (HH:MM)
     * @return List of available slot IDs
     */
    public List<Integer> findAvailableSlots(String parkingID, String startDate, String startTime, String endDate,
            String endTime) {
        if (parkingID == null || parkingID.trim().isEmpty() ||
                !isValidDateFormat(startDate) || !isValidTimeFormat(startTime) ||
                !isValidDateFormat(endDate) || !isValidTimeFormat(endTime)) {
            System.err.println("Invalid parameters for available slots search");
            return Collections.emptyList();
        }

        try {
            List<Integer> allSlots = parkingSlotRepository.getListOfSlotIdsByParkingId(parkingID);
            List<Integer> availableSlots = new ArrayList<>();

            for (Integer slotID : allSlots) {
                if (isSlotAvailable(slotID, startDate, startTime, endDate, endTime)) {
                    availableSlots.add(slotID);
                }
            }

            return availableSlots;
        } catch (Exception e) {
            System.err.println("Error finding available slots: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Gets all active reservations in the system.
     *
     * @return List of active reservations
     */
    public List<Reservation> getActiveReservations() {
        try {
            return reservationRepository.getListOfReservationsByStatus(STATUS_ACTIVE);
        } catch (Exception e) {
            System.err.println("Error retrieving active reservations: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Gets all reservations with a specific status.
     *
     * @param status The status to filter by
     * @return List of reservations with the specified status
     */
    public List<Reservation> getReservationsByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            System.err.println("Invalid status");
            return Collections.emptyList();
        }

        try {
            return reservationRepository.getListOfReservationsByStatus(status);
        } catch (Exception e) {
            System.err.println("Error retrieving reservations by status: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Gets upcoming reservations for a user.
     *
     * @param userID The ID of the user
     * @return List of upcoming reservations
     */
    public List<Reservation> getUpcomingReservations(int userID) {
        if (userID <= 0) {
            System.err.println("Invalid user ID");
            return Collections.emptyList();
        }

        try {
            List<Reservation> userReservations = getReservationsByUser(userID);
            List<Reservation> upcomingReservations = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();

            for (Reservation reservation : userReservations) {
                if (STATUS_ACTIVE.equals(reservation.getStatus())) {
                    LocalDateTime startDateTime = parseDateTime(reservation.getStartDate(), reservation.getStartTime());
                    if (startDateTime != null && startDateTime.isAfter(now)) {
                        upcomingReservations.add(reservation);
                    }
                }
            }

            return upcomingReservations;
        } catch (Exception e) {
            System.err.println("Error retrieving upcoming reservations: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Completes a reservation.
     *
     * @param reservationID The ID of the reservation to complete
     * @return true if completion successful, false otherwise
     */
    public boolean completeReservation(int reservationID) {
        if (reservationID <= 0) {
            System.err.println("Invalid reservation ID");
            return false;
        }

        try {
            Reservation reservation = getReservationById(reservationID);
            if (reservation == null) {
                System.err.println("Reservation not found");
                return false;
            }

            if (!STATUS_ACTIVE.equals(reservation.getStatus())) {
                System.err.println("Cannot complete non-active reservation");
                return false;
            }

            reservation.setStatus(STATUS_COMPLETED);
            boolean updated = updateReservationById(reservationID, reservation);

            if (updated) {
                // Update slot availability
                return parkingSlotRepository.updateSlotAvailability(reservation.getSlotID(), true);
            }

            return false;
        } catch (Exception e) {
            System.err.println("Error completing reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}