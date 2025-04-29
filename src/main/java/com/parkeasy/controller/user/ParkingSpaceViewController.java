package main.java.com.parkeasy.controller.user;

import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.model.ParkingSlot;
import main.java.com.parkeasy.model.ParkingReview;
import main.java.com.parkeasy.service.ParkingSpaceService;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParkingSpaceViewController {
    private static final Logger LOGGER = Logger.getLogger(ParkingSpaceViewController.class.getName());
    private final ParkingSpaceService parkingSpaceService;

    public ParkingSpaceViewController() {
        this.parkingSpaceService = new ParkingSpaceService();
    }

    /**
     * Get all available parking spaces
     *
     * @return List of all parking spaces
     */
    public List<ParkingSpace> getAllParkingPlots() {
        try {
            return parkingSpaceService.getAllParkingSpaces();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving parking plots", e);
            return null;
        }
    }

    /**
     * Get a parking space by its ID
     *
     * @param parkingId The ID of the parking space
     * @return The parking space or null if not found
     */
    public ParkingSpace getParkingPlotById(String parkingId) {
        try {
            return parkingSpaceService.getParkingSpaceById(parkingId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving parking plot", e);
            return null;
        }
    }

    /**
     * Search for parking spaces by location
     *
     * @param location Location search term
     * @return List of matching parking spaces
     */
    public List<ParkingSpace> searchParkingPlots(String location) {
        try {
            return parkingSpaceService.searchParkingSpaces(location);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error searching parking plots", e);
            return null;
        }
    }

    /**
     * Sort parking spaces by price
     *
     * @param ascending If true, sort in ascending order; otherwise, descending
     * @return Sorted list of parking spaces
     */
    public List<ParkingSpace> sortParkingPlotsByPrice(boolean ascending) {
        try {
            List<ParkingSpace> allPlots = parkingSpaceService.getAllParkingSpaces();
            if (allPlots == null) {
                return null;
            }

            if (ascending) {
                return allPlots.stream()
                        .sorted(Comparator.comparing(ParkingSpace::getCostOfParking))
                        .toList();
            } else {
                return allPlots.stream()
                        .sorted(Comparator.comparing(ParkingSpace::getCostOfParking).reversed())
                        .toList();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error sorting parking plots by price", e);
            return null;
        }
    }

    /**
     * Sort parking spaces by their average rating
     *
     * @return Sorted list of parking spaces by rating (highest first)
     */
    public List<ParkingSpace> sortParkingPlotsByRating() {
        try {
            // We can use the service's method to get highest rated parking spaces
            return parkingSpaceService.getHighestRatedParkingSpaces(Integer.MAX_VALUE);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error sorting parking plots by rating", e);
            return null;
        }
    }

    /**
     * Get available parking slots for a specific time period
     *
     * @param parkingId The ID of the parking space
     * @param startDateTime Start date and time
     * @param endDateTime End date and time
     * @return List of available parking slots
     */
    public List<ParkingSlot> getAvailableSlots(String parkingId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        try {
            return parkingSpaceService.getAvailableSlotsForTimeRange(parkingId, startDateTime, endDateTime);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting available slots", e);
            return null;
        }
    }

    /**
     * Get all reviews for a parking space
     *
     * @param parkingId The ID of the parking space
     * @return List of reviews
     */
    public List<ParkingReview> getParkingReviews(String parkingId) {
        try {
            return parkingSpaceService.getReviewsForParkingSpace(parkingId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving parking reviews", e);
            return null;
        }
    }

    /**
     * Get the average rating for a parking space
     *
     * @param parkingId The ID of the parking space
     * @return Average rating (0-5) or 0 if no ratings
     */
    public double getAverageRating(String parkingId) {
        try {
            return parkingSpaceService.getAverageRatingForParkingSpace(parkingId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting average rating", e);
            return 0.0;
        }
    }

    /**
     * Check if a parking space is open at a specific time
     *
     * @param parkingId The ID of the parking space
     * @param dateTime The date and time to check
     * @return true if open, false if closed or error
     */
    public boolean isParkingPlotOpenAt(String parkingId, LocalDateTime dateTime) {
        try {
            return parkingSpaceService.isParkingSpaceOpenAt(parkingId, dateTime);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking if parking plot is open", e);
            return false;
        }
    }
}