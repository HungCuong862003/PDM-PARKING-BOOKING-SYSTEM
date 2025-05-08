package main.java.com.parkeasy.controller.user;

import main.java.com.parkeasy.model.Review;
import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.service.ReviewService;
import main.java.com.parkeasy.service.ReservationService;
import main.java.com.parkeasy.util.Constants;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller class for handling user review operations
 */
public class ReviewController {
    private static final Logger LOGGER = Logger.getLogger(ReviewController.class.getName());

    private final ReviewService reviewService;
    private ReservationService reservationService;

    /**
     * Constructor with dependency injection
     *
     * @param reviewService The review service
     */
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
        this.reservationService = new ReservationService(); // Default initialization
    }

    /**
     * Constructor with both services injected
     *
     * @param reviewService The review service
     * @param reservationService The reservation service
     */
    public ReviewController(ReviewService reviewService, ReservationService reservationService) {
        this.reviewService = reviewService;
        this.reservationService = reservationService;
    }

    /**
     * Submit a review for a completed reservation
     *
     * @param userId The ID of the user submitting the review
     * @param reservationId The ID of the reservation being reviewed
     * @param rating The rating (1-5) given by the user
     * @param comment Optional comment provided with the review (will be ignored as there's no Comment field in DB)
     * @return Map containing the result of the submission
     */
    public Map<String, Object> submitReview(int userId, int reservationId, int rating, String comment) {
        try {
            LOGGER.log(Level.INFO, "Submitting review for reservation: {0} by user: {1}",
                    new Object[]{reservationId, userId});

            // Validate input parameters
            if (userId <= 0 || reservationId <= 0) {
                return Map.of(
                        "success", false,
                        "message", "Invalid user ID or reservation ID"
                );
            }

            if (rating < 1 || rating > 5) {
                return Map.of(
                        "success", false,
                        "message", "Rating must be between 1 and 5"
                );
            }

            // Verify that the reservation exists and is completed
            Reservation reservation = reservationService.getReservationById(reservationId);
            if (reservation == null) {
                return Map.of(
                        "success", false,
                        "message", "Reservation not found"
                );
            }

            // Check if reservation is completed
            if (!reservation.getStatus().equals(Constants.RESERVATION_COMPLETE)) {
                return Map.of(
                        "success", false,
                        "message", "Cannot review a reservation that is not completed"
                );
            }

            // Check if review already exists for this reservation
            Review existingReview = reviewService.getReviewByReservationId(reservationId);
            if (existingReview != null) {
                return Map.of(
                        "success", false,
                        "message", "A review has already been submitted for this reservation"
                );
            }

            // Get the parking ID for this reservation
            String parkingId = reservationService.getParkingIdBySlotNumber(reservation.getSlotNumber());
            if (parkingId == null) {
                return Map.of(
                        "success", false,
                        "message", "Could not determine parking space for this reservation"
                );
            }

            // Create and save the review
            Review review = new Review();
            review.setReviewID(reviewService.generateReviewId());
            review.setUserID(userId);
            review.setReservationID(reservationId);
            review.setRating(rating);
            review.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            boolean created = reviewService.createReview(review, parkingId);

            if (created) {
                // Update parking space average rating
                updateParkingSpaceRating(parkingId);

                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "Review submitted successfully");
                result.put("reviewId", review.getReviewID());
                return result;
            } else {
                return Map.of(
                        "success", false,
                        "message", "Failed to submit review"
                );
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error submitting review", e);
            return Map.of(
                    "success", false,
                    "message", "Error: " + e.getMessage()
            );
        }
    }

    /**
     * Get user's review for a specific reservation
     *
     * @param userId The ID of the user
     * @param reservationId The ID of the reservation
     * @return Map containing the review details
     */
    public Map<String, Object> getUserReviewForReservation(int userId, int reservationId) {
        try {
            LOGGER.log(Level.INFO, "Getting review for reservation: {0} by user: {1}",
                    new Object[]{reservationId, userId});

            // Get the review
            Review review = reviewService.getReviewByReservationId(reservationId);

            if (review == null) {
                return Map.of(
                        "success", false,
                        "message", "No review found for this reservation"
                );
            }

            // Verify the review belongs to the user
            if (review.getUserID() != userId) {
                return Map.of(
                        "success", false,
                        "message", "Access denied: Review does not belong to user"
                );
            }

            // Return review details
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("reviewId", review.getReviewID());
            result.put("rating", review.getRating());
            result.put("createdAt", review.getCreatedAt());

            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting review", e);
            return Map.of(
                    "success", false,
                    "message", "Error: " + e.getMessage()
            );
        }
    }

    /**
     * Update a user's existing review
     *
     * @param userId The ID of the user
     * @param reviewId The ID of the review to update
     * @param rating The new rating
     * @param comment The new comment (will be ignored as there's no Comment field in DB)
     * @return Map containing the result of the update operation
     */
    public Map<String, Object> updateReview(int userId, int reviewId, int rating, String comment) {
        try {
            LOGGER.log(Level.INFO, "Updating review: {0} by user: {1}",
                    new Object[]{reviewId, userId});

            // Validate input parameters
            if (userId <= 0 || reviewId <= 0) {
                return Map.of(
                        "success", false,
                        "message", "Invalid user ID or review ID"
                );
            }

            if (rating < 1 || rating > 5) {
                return Map.of(
                        "success", false,
                        "message", "Rating must be between 1 and 5"
                );
            }

            // Get the existing review
            Review review = reviewService.getReviewById(reviewId);

            if (review == null) {
                return Map.of(
                        "success", false,
                        "message", "Review not found"
                );
            }

            // Verify the review belongs to the user
            if (review.getUserID() != userId) {
                return Map.of(
                        "success", false,
                        "message", "Access denied: Review does not belong to user"
                );
            }

            // Update the review
            review.setRating(rating);

            boolean updated = reviewService.updateReview(review);

            if (updated) {
                // Get the parking ID for this reservation
                Reservation reservation = reservationService.getReservationById(review.getReservationID());
                if (reservation != null) {
                    String parkingId = reservationService.getParkingIdBySlotNumber(reservation.getSlotNumber());
                    if (parkingId != null) {
                        updateParkingSpaceRating(parkingId);
                    }
                }

                return Map.of(
                        "success", true,
                        "message", "Review updated successfully"
                );
            } else {
                return Map.of(
                        "success", false,
                        "message", "Failed to update review"
                );
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating review", e);
            return Map.of(
                    "success", false,
                    "message", "Error: " + e.getMessage()
            );
        }
    }

    /**
     * Helper method to update parking space rating
     *
     * @param parkingId The ID of the parking space
     */
    private void updateParkingSpaceRating(String parkingId) {
        try {
            // Calculate new average rating
            double averageRating = reviewService.calculateAverageRatingForParking(parkingId);

            // Update parking space rating
            reviewService.updateParkingSpaceRating(parkingId, averageRating);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error updating parking space rating", e);
            // We don't want to fail the review submission if this fails
        }
    }
}