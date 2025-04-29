package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.ParkingReview;
import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.repository.ParkingReviewRepository;
import main.java.com.parkeasy.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for managing parking reviews
 */
public class ReviewService {
    private static final Logger LOGGER = Logger.getLogger(ReviewService.class.getName());

    private final ParkingReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;

    /**
     * Constructor with dependency injection
     */
    public ReviewService(ParkingReviewRepository reviewRepository, ReservationRepository reservationRepository) {
        this.reviewRepository = reviewRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Default constructor
     */
    public ReviewService() {
        this.reviewRepository = new ParkingReviewRepository();
        this.reservationRepository = new ReservationRepository();
    }

    /**
     * Add a new review
     *
     * @param review The review to add
     * @return The ID of the new review, or -1 if failed
     */
    public int addReview(ParkingReview review) {
        try {
            LOGGER.log(Level.INFO, "Adding review for parking space: {0}", review.getParkingId());

            // Validate review data
            if (!isValidReview(review)) {
                LOGGER.log(Level.WARNING, "Invalid review data");
                return -1;
            }

            // Check if reservation exists
            Reservation reservation = reservationRepository.getReservationById(review.getReservationId());
            if (reservation == null) {
                LOGGER.log(Level.WARNING, "Reservation not found: {0}", review.getReservationId());
                return -1;
            }

            // Check if user has already reviewed this reservation
            ParkingReview existingReview = reviewRepository.getReviewByReservationId(review.getReservationId());
            if (existingReview != null) {
                LOGGER.log(Level.WARNING, "Review already exists for this reservation");
                return -1;
            }

            // Set review date if not provided
            if (review.getReviewDate() == null) {
                review.setReviewDate(LocalDateTime.now());
            }

            return reviewRepository.addReview(review);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding review", e);
            return -1;
        }
    }

    /**
     * Update an existing review
     *
     * @param review The review with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateReview(ParkingReview review) {
        try {
            LOGGER.log(Level.INFO, "Updating review: {0}", review.getReviewId());

            // Validate review data
            if (!isValidReview(review) || review.getReviewId() <= 0) {
                LOGGER.log(Level.WARNING, "Invalid review data");
                return false;
            }

            // Check if review exists
            ParkingReview existingReview = reviewRepository.getReviewById(review.getReviewId());
            if (existingReview == null) {
                LOGGER.log(Level.WARNING, "Review not found: {0}", review.getReviewId());
                return false;
            }

            // Check if user owns the review
            if (!existingReview.getUserId().equals(review.getUserId())) {
                LOGGER.log(Level.WARNING, "User does not own this review");
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
     * @param userId The ID of the user requesting deletion (for ownership verification)
     * @return true if successful, false otherwise
     */
    public boolean deleteReview(int reviewId, int userId) {
        try {
            LOGGER.log(Level.INFO, "Deleting review: {0}", reviewId);

            // Check if review exists
            ParkingReview existingReview = reviewRepository.getReviewById(reviewId);
            if (existingReview == null) {
                LOGGER.log(Level.WARNING, "Review not found: {0}", reviewId);
                return false;
            }

            // Check if user owns the review
            if (!existingReview.getUserId().equals(userId)) {
                LOGGER.log(Level.WARNING, "User does not own this review");
                return false;
            }

            return reviewRepository.deleteReview(reviewId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting review", e);
            return false;
        }
    }

    /**
     * Get a review by ID
     *
     * @param reviewId The ID of the review
     * @return The review or null if not found
     */
    public ParkingReview getReviewById(int reviewId) {
        try {
            LOGGER.log(Level.INFO, "Getting review by ID: {0}", reviewId);
            return reviewRepository.getReviewById(reviewId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting review by ID", e);
            return null;
        }
    }

    /**
     * Get reviews for a parking space
     *
     * @param parkingId The ID of the parking space
     * @return List of reviews
     */
    public List<ParkingReview> getReviewsByParkingId(String parkingId) {
        try {
            LOGGER.log(Level.INFO, "Getting reviews for parking space: {0}", parkingId);
            return reviewRepository.getReviewsByParkingId(parkingId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting reviews by parking ID", e);
            return List.of(); // Return empty list on error
        }
    }

    /**
     * Get reviews by a user
     *
     * @param userId The ID of the user
     * @return List of reviews by the user
     */
    public List<ParkingReview> getReviewsByUserId(int userId) {
        try {
            LOGGER.log(Level.INFO, "Getting reviews by user: {0}", userId);
            return reviewRepository.getReviewsByUserId(userId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting reviews by user ID", e);
            return List.of(); // Return empty list on error
        }
    }

    /**
     * Get review for a reservation
     *
     * @param reservationId The ID of the reservation
     * @return The review or null if not found
     */
    public ParkingReview getReviewByReservationId(int reservationId) {
        try {
            LOGGER.log(Level.INFO, "Getting review by reservation ID: {0}", reservationId);
            return reviewRepository.getReviewByReservationId(reservationId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting review by reservation ID", e);
            return null;
        }
    }

    /**
     * Get the average rating for a parking space
     *
     * @param parkingId The ID of the parking space
     * @return Average rating or 0 if no reviews
     */
    public double getAverageRatingForParkingSpace(String parkingId) {
        try {
            LOGGER.log(Level.INFO, "Getting average rating for parking space: {0}", parkingId);
            return reviewRepository.getAverageRatingForParkingSpace(parkingId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting average rating for parking space", e);
            return 0.0;
        }
    }

    /**
     * Get the number of reviews for a parking space
     *
     * @param parkingId The ID of the parking space
     * @return Number of reviews
     */
    public int getReviewCountForParkingSpace(String parkingId) {
        try {
            LOGGER.log(Level.INFO, "Getting review count for parking space: {0}", parkingId);
            return reviewRepository.getReviewCountForParkingSpace(parkingId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting review count for parking space", e);
            return 0;
        }
    }

    /**
     * Get latest reviews
     *
     * @param limit Maximum number of reviews to return
     * @return List of latest reviews
     */
    public List<ParkingReview> getLatestReviews(int limit) {
        try {
            LOGGER.log(Level.INFO, "Getting latest reviews, limit: {0}", limit);
            return reviewRepository.getLatestReviews(limit);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting latest reviews", e);
            return List.of(); // Return empty list on error
        }
    }

    /**
     * Check if a user has already reviewed a parking space
     *
     * @param userId The ID of the user
     * @param parkingId The ID of the parking space
     * @return true if already reviewed, false otherwise
     */
    public boolean hasUserReviewedParkingSpace(int userId, String parkingId) {
        try {
            LOGGER.log(Level.INFO, "Checking if user {0} has reviewed parking space {1}",
                    new Object[]{userId, parkingId});

            List<ParkingReview> userReviews = getReviewsByUserId(userId);

            for (ParkingReview review : userReviews) {
                if (review.getParkingId().equals(parkingId)) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking if user has reviewed parking space", e);
            return false;
        }
    }

    /**
     * Validate review data
     *
     * @param review The review to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidReview(ParkingReview review) {
        // Check for null values in required fields
        if (review == null || review.getRating() == null || review.getUserId() == null ||
                review.getParkingId() == null || review.getReservationId() == null) {
            return false;
        }

        // Validate rating (1-5)
        if (review.getRating() < 1 || review.getRating() > 5) {
            return false;
        }

        return true;
    }
}