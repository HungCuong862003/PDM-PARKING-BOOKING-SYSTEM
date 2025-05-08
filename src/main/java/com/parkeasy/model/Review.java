package main.java.com.parkeasy.model;

import java.sql.Timestamp;

/**
 * Model class representing a parking review
 * Users can rate their parking experience after completing a reservation
 */
public class Review {
    private int reviewID;
    private int userID;
    private int reservationID;
    private int rating;
    // Removed comment field as it doesn't exist in the database schema
    private Timestamp createdAt;

    /**
     * Default constructor
     */
    public Review() {
    }

    /**
     * Parameterized constructor
     *
     * @param reviewID The unique identifier for the review
     * @param userID The ID of the user who created the review
     * @param reservationID The ID of the reservation being reviewed
     * @param rating The rating (1-5) given by the user
     * @param createdAt Timestamp when the review was created
     */
    public Review(int reviewID, int userID, int reservationID, int rating, Timestamp createdAt) {
        this.reviewID = reviewID;
        this.userID = userID;
        this.reservationID = reservationID;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    /**
     * Get the review ID
     *
     * @return The review ID
     */
    public int getReviewID() {
        return reviewID;
    }

    /**
     * Set the review ID
     *
     * @param reviewID The review ID to set
     */
    public void setReviewID(int reviewID) {
        this.reviewID = reviewID;
    }

    /**
     * Get the user ID
     *
     * @return The user ID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Set the user ID
     *
     * @param userID The user ID to set
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * Get the reservation ID
     *
     * @return The reservation ID
     */
    public int getReservationID() {
        return reservationID;
    }

    /**
     * Set the reservation ID
     *
     * @param reservationID The reservation ID to set
     */
    public void setReservationID(int reservationID) {
        this.reservationID = reservationID;
    }

    /**
     * Get the rating
     *
     * @return The rating (1-5)
     */
    public int getRating() {
        return rating;
    }

    /**
     * Set the rating
     *
     * @param rating The rating to set (should be 1-5)
     */
    public void setRating(int rating) {
        // Validate rating range (1-5)
        if (rating < 1) {
            this.rating = 1;
        } else if (rating > 5) {
            this.rating = 5;
        } else {
            this.rating = rating;
        }
    }

    /**
     * Get the creation timestamp
     *
     * @return The timestamp when the review was created
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Set the creation timestamp
     *
     * @param createdAt The timestamp to set
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewID=" + reviewID +
                ", userID=" + userID +
                ", reservationID=" + reservationID +
                ", rating=" + rating +
                ", createdAt=" + createdAt +
                '}';
    }
}