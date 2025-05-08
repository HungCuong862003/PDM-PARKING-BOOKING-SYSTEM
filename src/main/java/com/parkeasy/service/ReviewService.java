package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.Review;
import main.java.com.parkeasy.repository.ReviewRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for managing parking reviews
 * Handles database operations related to reviews
 */
public class ReviewService {
    private static final Logger LOGGER = Logger.getLogger(ReviewService.class.getName());
    private final ReviewRepository reviewRepository;

    /**
     * Default constructor
     */
    public ReviewService() {
        this.reviewRepository = new ReviewRepository();
    }

    /**
     * Constructor with repository injection for testing
     *
     * @param reviewRepository The repository to use
     */
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    /**
     * Generate a new unique review ID
     *
     * @return A new unique review ID
     */
    public int generateReviewId() {
        try {
            return reviewRepository.generateReviewId();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating review ID", e);
            // Fallback to a timestamp-based ID in case of repository failure
            return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        }
    }

    /**
     * Create a new review in the database
     *
     * @param review The review to create
     * @param parkingId The ID of the parking space being reviewed
     * @return true if successful, false otherwise
     */
    public boolean createReview(Review review, String parkingId) {
        try {
            // Validate review fields
            if (review.getUserID() <= 0 || review.getReservationID() <= 0 ||
                    review.getRating() < 1 || review.getRating() > 5 || parkingId == null) {
                LOGGER.log(Level.WARNING, "Invalid review data: {0} or parking ID: {1}",
                        new Object[]{review, parkingId});
                return false;
            }

            // Set creation timestamp if not already set
            if (review.getCreatedAt() == null) {
                review.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            }

            return reviewRepository.createReview(review, parkingId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating review", e);
            return false;
        }
    }

    /**
     * Create a new review in the database
     * This method retrieves the parkingId from the reservation
     *
     * @param review The review to create
     * @return true if successful, false otherwise
     */
    public boolean createReview(Review review) {
        try {
            // Validate review fields
            if (review.getUserID() <= 0 || review.getReservationID() <= 0 ||
                    review.getRating() < 1 || review.getRating() > 5) {
                LOGGER.log(Level.WARNING, "Invalid review data: {0}", review);
                return false;
            }

            // Set creation timestamp if not already set
            if (review.getCreatedAt() == null) {
                review.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            }

            // Get parkingId for this reservation
            String parkingId = reviewRepository.getParkingIdByReservationId(review.getReservationID());
            if (parkingId == null) {
                LOGGER.log(Level.SEVERE, "Could not find parkingId for reservation: {0}",
                        review.getReservationID());
                return false;
            }

            return reviewRepository.createReview(review, parkingId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating review", e);
            return false;
        }
    }

    /**
     * Get a review by its ID
     *
     * @param reviewId The ID of the review to get
     * @return The review, or null if not found
     */
    public Review getReviewById(int reviewId) {
        try {
            return reviewRepository.getReviewById(reviewId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting review by ID: " + reviewId, e);
            return null;
        }
    }

    /**
     * Get a review by reservation ID
     *
     * @param reservationId The ID of the reservation
     * @return The review, or null if not found
     */
    public Review getReviewByReservationId(int reservationId) {
        try {
            return reviewRepository.getReviewByReservationId(reservationId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting review by reservation ID: " + reservationId, e);
            return null;
        }
    }

    /**
     * Get all reviews by a user
     *
     * @param userId The ID of the user
     * @return List of reviews by the user
     */
    public List<Review> getReviewsByUserId(int userId) {
        try {
            return reviewRepository.getReviewsByUserId(userId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting reviews by user ID: " + userId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Get all reviews for a parking space
     *
     * @param parkingId The ID of the parking space
     * @return List of reviews for the parking space
     */
    public List<Review> getReviewsByParkingId(String parkingId) {
        try {
            return reviewRepository.getReviewsByParkingId(parkingId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting reviews by parking ID: " + parkingId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Update an existing review
     *
     * @param review The review to update
     * @return true if successful, false otherwise
     */
    public boolean updateReview(Review review) {
        try {
            // Validate review fields
            if (review.getReviewID() <= 0 || review.getUserID() <= 0 ||
                    review.getReservationID() <= 0 || review.getRating() < 1 ||
                    review.getRating() > 5) {
                LOGGER.log(Level.WARNING, "Invalid review data for update: {0}", review);
                return false;
            }

            return reviewRepository.updateReview(review);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating review", e);
            return false;
        }
    }

    /**
     * Delete a review
     *
     * @param reviewId The ID of the review to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteReview(int reviewId) {
        try {
            return reviewRepository.deleteReview(reviewId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting review: " + reviewId, e);
            return false;
        }
    }

    /**
     * Calculate the average rating for a parking space
     *
     * @param parkingId The ID of the parking space
     * @return The average rating, or 0 if no reviews
     */
    public double calculateAverageRatingForParking(String parkingId) {
        try {
            List<Review> reviews = getReviewsByParkingId(parkingId);

            if (reviews.isEmpty()) {
                return 0.0;
            }

            int sum = 0;
            for (Review review : reviews) {
                sum += review.getRating();
            }

            return (double) sum / reviews.size();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating average rating", e);
            return 0.0;
        }
    }

    /**
     * Update the average rating for a parking space
     *
     * @param parkingId The ID of the parking space
     * @param newRating The new average rating
     * @return true if successful, false otherwise
     */
    public boolean updateParkingSpaceRating(String parkingId, double newRating) {
        try {
            return reviewRepository.updateParkingSpaceRating(parkingId, newRating);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating parking space rating", e);
            return false;
        }
    }

    /**
     * Get the parking ID for a reservation
     *
     * @param reservationId The ID of the reservation
     * @return The parking ID, or null if not found
     */
    public String getParkingIdByReservationId(int reservationId) {
        try {
            return reviewRepository.getParkingIdByReservationId(reservationId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting parking ID for reservation: " + reservationId, e);
            return null;
        }
    }
}