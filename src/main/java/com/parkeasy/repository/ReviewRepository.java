package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.Review;
import main.java.com.parkeasy.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repository class for parking reviews
 * Handles direct database access for reviews
 */
public class ReviewRepository {
    private static final Logger LOGGER = Logger.getLogger(ReviewRepository.class.getName());

    /**
     * Generate a new unique review ID
     *
     * @return A new unique review ID
     * @throws SQLException If a database error occurs
     */
    public int generateReviewId() throws SQLException {
        int newId = 1;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(ReviewID) FROM PARKING_REVIEW")) {

            if (rs.next() && rs.getObject(1) != null) {
                newId = rs.getInt(1) + 1;
            }
        }
        return newId;
    }

    /**
     * Create a new review in the database
     *
     * @param review The review to create
     * @param parkingId The ID of the parking space being reviewed
     * @return true if successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean createReview(Review review, String parkingId) throws SQLException {
        // Updated SQL query to include ParkingID
        String query = "INSERT INTO PARKING_REVIEW (ReviewID, UserID, ReservationID, Rating, ParkingID) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, review.getReviewID());
            pstmt.setInt(2, review.getUserID());
            pstmt.setInt(3, review.getReservationID());
            pstmt.setInt(4, review.getRating());
            pstmt.setString(5, parkingId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Get a review by its ID
     *
     * @param reviewId The ID of the review to get
     * @return The review, or null if not found
     * @throws SQLException If a database error occurs
     */
    public Review getReviewById(int reviewId) throws SQLException {
        String query = "SELECT * FROM PARKING_REVIEW WHERE ReviewID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, reviewId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReview(rs);
                }
            }
        }

        return null;
    }

    /**
     * Get a review by reservation ID
     *
     * @param reservationId The ID of the reservation
     * @return The review, or null if not found
     * @throws SQLException If a database error occurs
     */
    public Review getReviewByReservationId(int reservationId) throws SQLException {
        String query = "SELECT * FROM PARKING_REVIEW WHERE ReservationID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, reservationId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReview(rs);
                }
            }
        }

        return null;
    }

    /**
     * Get all reviews by a user
     *
     * @param userId The ID of the user
     * @return List of reviews by the user
     * @throws SQLException If a database error occurs
     */
    public List<Review> getReviewsByUserId(int userId) throws SQLException {
        String query = "SELECT * FROM PARKING_REVIEW WHERE UserID = ?";
        List<Review> reviews = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapResultSetToReview(rs));
                }
            }
        }

        return reviews;
    }

    /**
     * Get all reviews for a parking space
     *
     * @param parkingId The ID of the parking space
     * @return List of reviews for the parking space
     * @throws SQLException If a database error occurs
     */
    public List<Review> getReviewsByParkingId(String parkingId) throws SQLException {
        // This query is simplified since we now have ParkingID directly in the PARKING_REVIEW table
        String query = "SELECT * FROM PARKING_REVIEW WHERE ParkingID = ?";

        List<Review> reviews = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, parkingId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapResultSetToReview(rs));
                }
            }
        }

        return reviews;
    }

    /**
     * Update an existing review
     *
     * @param review The review to update
     * @return true if successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updateReview(Review review) throws SQLException {
        // Updated SQL query to only update the Rating field
        String query = "UPDATE PARKING_REVIEW SET Rating = ? WHERE ReviewID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, review.getRating());
            pstmt.setInt(2, review.getReviewID());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Delete a review
     *
     * @param reviewId The ID of the review to delete
     * @return true if successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean deleteReview(int reviewId) throws SQLException {
        String query = "DELETE FROM PARKING_REVIEW WHERE ReviewID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, reviewId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Update the average rating for a parking space
     *
     * @param parkingId The ID of the parking space
     * @param newRating The new average rating
     * @return true if successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updateParkingSpaceRating(String parkingId, double newRating) throws SQLException {
        String query = "UPDATE PARKING_SPACE SET AverageRating = ? WHERE ParkingID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDouble(1, newRating);
            pstmt.setString(2, parkingId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Get the parking ID for a reservation
     *
     * @param reservationId The ID of the reservation
     * @return The parking ID, or null if not found
     * @throws SQLException If a database error occurs
     */
    public String getParkingIdByReservationId(int reservationId) throws SQLException {
        String query = "SELECT ps.ParkingID FROM PARKING_SPACE ps " +
                "JOIN PARKING_SLOT psl ON ps.ParkingID = psl.ParkingID " +
                "JOIN PARKING_RESERVATION pr ON psl.SlotNumber = pr.SlotNumber " +
                "WHERE pr.ReservationID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, reservationId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("ParkingID");
                }
            }
        }

        return null;
    }

    /**
     * Map a ResultSet row to a Review object
     *
     * @param rs The ResultSet containing review data
     * @return The Review object
     * @throws SQLException If a database error occurs
     */
    private Review mapResultSetToReview(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setReviewID(rs.getInt("ReviewID"));
        review.setUserID(rs.getInt("UserID"));
        review.setReservationID(rs.getInt("ReservationID"));
        review.setRating(rs.getInt("Rating"));
        // ParkingID is stored in the database but not in our Review model
        // Use current time for createdAt if needed
        review.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return review;
    }
}