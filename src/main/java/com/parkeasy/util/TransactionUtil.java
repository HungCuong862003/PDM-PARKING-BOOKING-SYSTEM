package main.java.com.parkeasy.util;

import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.model.Reservation;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for transaction-related operations
 */
public class TransactionUtil {
    private static final Logger LOGGER = Logger.getLogger(TransactionUtil.class.getName());

    /**
     * Calculate the fee for a reservation based on rate and duration
     *
     * @param reservation The reservation
     * @param parkingSpace The parking space
     * @return The calculated fee
     */
    public static float calculateReservationFee(Reservation reservation, ParkingSpace parkingSpace) {
        try {
            if (reservation == null || parkingSpace == null) {
                return 0.0f;
            }

            // Get hourly rate
            float hourlyRate = parkingSpace.getCostOfParking();

            // Calculate duration in hours
            LocalDateTime startDateTime = LocalDateTime.of(
                    reservation.getStartDate().toLocalDate(),
                    reservation.getStartTime().toLocalTime()
            );

            LocalDateTime endDateTime = LocalDateTime.of(
                    reservation.getEndDate().toLocalDate(),
                    reservation.getEndTime().toLocalTime()
            );

            long durationMinutes = Duration.between(startDateTime, endDateTime).toMinutes();
            float durationHours = durationMinutes / 60.0f;

            // Round up to nearest hour
            durationHours = (float) Math.ceil(durationHours);

            // Calculate fee
            return hourlyRate * durationHours;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating reservation fee", e);
            return 0.0f;
        }
    }

    /**
     * Format a monetary amount for display
     *
     * @param amount The amount to format
     * @return Formatted amount (e.g., "$25.50")
     */
    public static String formatCurrency(float amount) {
        return String.format("$%.2f", amount);
    }

    /**
     * Check if a user has sufficient balance for a transaction
     *
     * @param userBalance The user's balance
     * @param amount The amount to check
     * @return true if sufficient, false otherwise
     */
    public static boolean hasSufficientBalance(float userBalance, float amount) {
        return userBalance >= amount;
    }
}