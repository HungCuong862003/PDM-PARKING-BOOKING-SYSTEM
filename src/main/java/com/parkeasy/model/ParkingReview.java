package main.java.com.parkeasy.model;

import java.time.LocalDateTime;

/**
 * Model class representing a review for a parking space
 */
public class ParkingReview {
    // primary key
    private Integer reviewId;
    // attributes
    private Integer rating;
    private String comment;
    private LocalDateTime reviewDate;
    // foreign keys
    private Integer userId;
    private String parkingId;
    private Integer reservationId;

    /**
     * Default constructor
     */
    public ParkingReview() {
    }

    /**
     * Parameterized constructor
     */
    public ParkingReview(Integer reviewId, Integer rating, String comment, LocalDateTime reviewDate,
                         Integer userId, String parkingId, Integer reservationId) {
        this.reviewId = reviewId;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
        this.userId = userId;
        this.parkingId = parkingId;
        this.reservationId = reservationId;
    }

    /**
     * @return the reviewId
     */
    public Integer getReviewId() {
        return reviewId;
    }

    /**
     * @param reviewId the reviewId to set
     */
    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    /**
     * @return the rating
     */
    public Integer getRating() {
        return rating;
    }

    /**
     * @param rating the rating to set (must be between 1-5)
     */
    public void setRating(Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the reviewDate
     */
    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    /**
     * @param reviewDate the reviewDate to set
     */
    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    /**
     * @return the userId
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * @return the parkingId
     */
    public String getParkingId() {
        return parkingId;
    }

    /**
     * @param parkingId the parkingId to set
     */
    public void setParkingId(String parkingId) {
        this.parkingId = parkingId;
    }

    /**
     * @return the reservationId
     */
    public Integer getReservationId() {
        return reservationId;
    }

    /**
     * @param reservationId the reservationId to set
     */
    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    @Override
    public String toString() {
        return "ParkingReview{" +
                "reviewId=" + reviewId +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", reviewDate=" + reviewDate +
                ", userId=" + userId +
                ", parkingId='" + parkingId + '\'' +
                ", reservationId=" + reservationId +
                '}';
    }
}