package main.java.com.parkeasy.controller.user;

import main.java.com.parkeasy.model.ParkingReview;
import main.java.com.parkeasy.model.ParkingSlot;
import main.java.com.parkeasy.model.ParkingSpace;
import main.java.com.parkeasy.service.ParkingSpaceService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for parking space view operations
 */
public class ParkingSpaceViewController {
    private final ParkingSpaceService parkingSpaceService;

    // Current search state for pagination
    private String currentSearchTerm = "";
    private int currentPage = 0;
    private final int defaultPageSize = 10;
    private int totalResultsCount = 0;

    /**
     * Constructor
     */
    public ParkingSpaceViewController() {
        this.parkingSpaceService = new ParkingSpaceService();

        // Initialize search indexes during application startup
        initializeSearch();
    }

    /**
     * Initialize search functionality
     */
    private void initializeSearch() {
        try {
            // Create necessary indexes for optimized search
            parkingSpaceService.initializeSearch();
        } catch (Exception e) {
            System.err.println("Failed to initialize search: " + e.getMessage());
        }
    }

    /**
     * Get all parking plots
     *
     * @return List of all parking spaces
     */
    public List<ParkingSpace> getAllParkingPlots() {
        try {
            // Reset pagination state
            currentSearchTerm = "";
            currentPage = 0;

            // Get all parking spaces
            return parkingSpaceService.getAllParkingSpaces();
        } catch (Exception e) {
            System.err.println("Error getting all parking plots: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Search parking plots by term with optimized search
     *
     * @param searchTerm The search term
     * @return List of matching parking spaces
     */
    public List<ParkingSpace> searchParkingPlots(String searchTerm) {
        try {
            // Reset pagination for new search
            this.currentSearchTerm = searchTerm;
            this.currentPage = 0;

            // Get count for pagination
            this.totalResultsCount = parkingSpaceService.countSearchResults(searchTerm);

            // Get first page of results
            return parkingSpaceService.searchParkingSpaces(searchTerm, currentPage, defaultPageSize);
        } catch (Exception e) {
            System.err.println("Error searching parking plots: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Load the next page of search results
     *
     * @return List of parking spaces for the next page
     */
    public List<ParkingSpace> loadNextPage() {
        try {
            // Check if there are more results
            int totalPages = (int) Math.ceil((double) totalResultsCount / defaultPageSize);
            if (currentPage >= totalPages - 1) {
                return new ArrayList<>(); // No more pages
            }

            // Increment page and load results
            currentPage++;
            return parkingSpaceService.searchParkingSpaces(currentSearchTerm, currentPage, defaultPageSize);
        } catch (Exception e) {
            System.err.println("Error loading next page: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Load the previous page of search results
     *
     * @return List of parking spaces for the previous page
     */
    public List<ParkingSpace> loadPreviousPage() {
        try {
            // Check if we're on the first page
            if (currentPage <= 0) {
                return new ArrayList<>(); // No previous page
            }

            // Decrement page and load results
            currentPage--;
            return parkingSpaceService.searchParkingSpaces(currentSearchTerm, currentPage, defaultPageSize);
        } catch (Exception e) {
            System.err.println("Error loading previous page: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get the current page number (0-based)
     *
     * @return Current page number
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Get the total number of pages for the current search
     *
     * @return Total number of pages
     */
    public int getTotalPages() {
        return (int) Math.ceil((double) totalResultsCount / defaultPageSize);
    }

    /**
     * Sort parking plots by price
     *
     * @param ascending Whether to sort in ascending order
     * @return Sorted list of parking spaces
     */
    public List<ParkingSpace> sortParkingPlotsByPrice(boolean ascending) {
        try {
            List<ParkingSpace> parkingSpaces;

            // Use current search results if available, otherwise get all
            if (!currentSearchTerm.isEmpty()) {
                parkingSpaces = parkingSpaceService.searchParkingSpaces(currentSearchTerm, 0, totalResultsCount);
            } else {
                parkingSpaces = parkingSpaceService.getAllParkingSpaces();
            }

            // Sort the spaces by price
            if (ascending) {
                return parkingSpaces.stream()
                        .sorted(Comparator.comparing(ParkingSpace::getCostOfParking))
                        .collect(Collectors.toList());
            } else {
                return parkingSpaces.stream()
                        .sorted(Comparator.comparing(ParkingSpace::getCostOfParking).reversed())
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            System.err.println("Error sorting parking plots by price: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Sort parking plots by rating
     *
     * @return Sorted list of parking spaces by rating (highest first)
     */
    public List<ParkingSpace> sortParkingPlotsByRating() {
        try {
            // Get all parking IDs sorted by rating
            List<String> topRatedIDs = parkingSpaceService.getHighestRatedParkingSpaceIds(100);

            if (topRatedIDs.isEmpty()) {
                return new ArrayList<>();
            }

            // Get all parking spaces based on current search
            List<ParkingSpace> parkingSpaces;
            if (!currentSearchTerm.isEmpty()) {
                parkingSpaces = parkingSpaceService.searchParkingSpaces(currentSearchTerm, 0, totalResultsCount);
            } else {
                parkingSpaces = parkingSpaceService.getAllParkingSpaces();
            }

            // Create a map for ordering
            java.util.Map<String, Integer> ratingOrder = new java.util.HashMap<>();
            for (int i = 0; i < topRatedIDs.size(); i++) {
                ratingOrder.put(topRatedIDs.get(i), i);
            }

            // Sort the spaces by their position in the rating order
            return parkingSpaces.stream()
                    .sorted(Comparator.comparing(space -> {
                        // Default to a high value if not in the top rated list
                        return ratingOrder.getOrDefault(space.getParkingID(), Integer.MAX_VALUE);
                    }))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error sorting parking plots by rating: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get the average rating for a parking space
     *
     * @param parkingId The ID of the parking space
     * @return The average rating
     */
    public float getAverageRating(String parkingId) {
        try {
            return parkingSpaceService.getAverageRatingForParkingSpace(parkingId);
        } catch (Exception e) {
            System.err.println("Error getting average rating: " + e.getMessage());
            return 0.0f;
        }
    }
    /**
     * Get available slots for a specific parking space and time range
     *
     * @param parkingId The ID of the parking space
     * @param startDateTime Start date and time for availability check
     * @param endDateTime End date and time for availability check
     * @return List of available parking slots for the specified time range
     */
    public List<ParkingSlot> getAvailableSlots(String parkingId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        try {
            // Call the parkingSpaceService to get available slots
            return parkingSpaceService.getAvailableSlotsForTimeRange(parkingId, startDateTime, endDateTime);
        } catch (Exception e) {
            System.err.println("Error getting available slots: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Get reviews for a specific parking space
     *
     * @param parkingId The ID of the parking space
     * @return List of reviews for the parking space
     */
    public List<ParkingReview> getParkingReviews(String parkingId) {
        try {
            // Call the parkingSpaceService to get reviews
            return parkingSpaceService.getReviewsForParkingSpace(parkingId);
        } catch (Exception e) {
            System.err.println("Error getting parking reviews: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Get a specific parking space by ID
     *
     * @param parkingId The ID of the parking space
     * @return The parking space or null if not found
     */
    public ParkingSpace getParkingSpaceById(String parkingId) {
        try {
            return parkingSpaceService.getParkingSpaceById(parkingId);
        } catch (Exception e) {
            System.err.println("Error getting parking space by ID: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get count of available slots for a specific parking space
     *
     * @param parkingId The ID of the parking space
     * @return Count of available slots
     */
    public int getAvailableSlotsCount(String parkingId) {
        try {
            ParkingSpace space = parkingSpaceService.getParkingSpaceById(parkingId);
            if (space == null) {
                return 0;
            }

            // Get current time slot availability
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneHourLater = now.plusHours(1);

            List<ParkingSlot> availableSlots = getAvailableSlots(parkingId, now, oneHourLater);
            return availableSlots.size();
        } catch (Exception e) {
            System.err.println("Error getting available slots count: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get count of total slots for a specific parking space
     *
     * @param parkingId The ID of the parking space
     * @return Total number of slots
     */
    public int getTotalSlotsCount(String parkingId) {
        try {
            ParkingSpace space = parkingSpaceService.getParkingSpaceById(parkingId);
            if (space == null) {
                return 0;
            }

            return space.getNumberOfSlots();
        } catch (Exception e) {
            System.err.println("Error getting total slots count: " + e.getMessage());
            return 0;
        }
    }
}