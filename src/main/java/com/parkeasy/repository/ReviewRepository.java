package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.ParkingReview;
import main.java.com.parkeasy.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repository class for performing database operations related to parking reviews
 */
public class ReviewRepository {
    private static final Logger LOGGER = Logger.getLogger(ReviewRepository.class.getName());

    /**
     * Default constructor
     */
    public ReviewRepository() {
    }

    /**
     * Save a new review to the database
     *
     * @param review The review to save
     * @return The saved review with generated ID
     */
    public ParkingReview save(ParkingReview review) throws SQLException {
        String sql = "INSERT INTO PARKING_REVIEW (Rating, Comment, ReviewDate, UserID, ParkingID, ReservationID) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, review.getRating());

            if (review.getComment() != null) {
                pstmt.setString(2, review.getComment());
            } else {
                pstmt.setNull(2, java.sql.Types.VARCHAR);
            }

            pstmt.setTimestamp(3, Timestamp.valueOf(review.getReviewDate()));
            pstmt.setInt(4, review.getUserId());
            pstmt.setString(5, review.getParkingId());
            pstmt.setInt(6, review.getReservationId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating review failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    review.setReviewId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating review failed, no ID obtained.");
                }
            }

            return review;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving review", e);
            throw e;
        }
    }

    /**
     * Find a review by its ID
     *
     * @param reviewId The ID of the review
     * @return The review object or null if not found
     */
    public ParkingReview findById(Integer reviewId) throws SQLException {
        String sql = "SELECT * FROM PARKING_REVIEW WHERE ReviewID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reviewId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReview(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding review by ID: " + reviewId, e);
            throw e;
        }
    }

    /**
     * Find reviews by parking space ID
     *
     * @param parkingId The ID of the parking space
     * @return List of reviews for the parking space
     */
    public List<ParkingReview> findByParkingId(String parkingId) throws SQLException {
        String sql = "SELECT * FROM PARKING_REVIEW WHERE ParkingID = ? ORDER BY ReviewDate DESC";
        List<ParkingReview> reviews = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, parkingId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapResultSetToReview(rs));
                }
                return reviews;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding reviews by parking ID: " + parkingId, e);
            throw e;
        }
    }

    /**
     * Find reviews by user ID
     *
     * @param userId The ID of the user
     * @return List of reviews by the user
     */
    public List<ParkingReview> findByUserId(Integer userId) throws SQLException {
        String sql = "SELECT * FROM PARKING_REVIEW WHERE UserID = ? ORDER BY ReviewDate DESC";
        List<ParkingReview> reviews = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapResultSetToReview(rs));
                }
                return reviews;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding reviews by user ID: " + userId, e);
            throw e;
        }
    }

    /**
     * Find review by reservation ID
     *
     * @param reservationId The ID of the reservation
     * @return The review or null if not found
     */
    public ParkingReview findByReservationId(Integer reservationId) throws SQLException {
        String sql = "SELECT * FROM PARKING_REVIEW WHERE ReservationID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reservationId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReview(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding review by reservation ID: " + reservationId, e);
            throw e;
        }
    }

    /**
     * Update an existing review
     *
     * @param review The review to update
     * @return The updated review
     */
    public ParkingReview update(ParkingReview review) throws SQLException {
        String sql = "UPDATE PARKING_REVIEW SET Rating = ?, Comment = ? WHERE ReviewID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, review.getRating());

            if (review.getComment() != null) {
                pstmt.setString(2, review.getComment());
            } else {
                pstmt.setNull(2, java.sql.Types.VARCHAR);
            }

            pstmt.setInt(3, review.getReviewId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating review failed, no rows affected.");
            }

            return review;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating review: " + review.getReviewId(), e);
            throw e;
        }
    }

    /**
     * Delete a review
     *
     * @param reviewId The ID of the review to delete
     * @return True if successfully deleted
     */
    public boolean delete(Integer reviewId) throws SQLException {
        String sql = "DELETE FROM PARKING_REVIEW WHERE ReviewID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reviewId);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting review: " + reviewId, e);
            throw e;
        }
    }

    /**
     * Get the average rating for a parking space
     *
     * @param parkingId The ID of the parking space
     * @return The average rating or 0 if no reviews
     */
    public double getAverageRatingForParkingSpace(String parkingId) throws SQLException {
        String sql = "SELECT AVG(Rating) FROM PARKING_REVIEW WHERE ParkingID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, parkingId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double avgRating = rs.getDouble(1);
                    return rs.wasNull() ? 0.0 : avgRating;
                } else {
                    return 0.0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error calculating average rating for parking space: " + parkingId, e);
            throw e;
        }
    }

    /**
     * Count the number of reviews for a parking space
     *
     * @param parkingId The ID of the parking space
     * @return The count of reviews
     */
    public int countReviewsByParkingId(String parkingId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM PARKING_REVIEW WHERE ParkingID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, parkingId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    return 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting reviews for parking space: " + parkingId, e);
            throw e;
        }
    }

    /**
     * Maps a database result set row to a ParkingReview object
     *
     * @param rs The result set positioned at the current row
     * @return A populated ParkingReview object
     * @throws SQLException if there's an error reading from the result set
     */
    private ParkingReview mapResultSetToReview(ResultSet rs) throws SQLException {
        ParkingReview review = new ParkingReview();

        review.setReviewId(rs.getInt("ReviewID"));
        review.setRating(rs.getInt("Rating"));
        review.setComment(rs.getString("Comment"));

        Timestamp reviewTimestamp = rs.getTimestamp("ReviewDate");
        if (reviewTimestamp != null) {
            review.setReviewDate(reviewTimestamp.toLocalDateTime());
        }

        review.setUserId(rs.getInt("UserID"));
        review.setParkingId(rs.getString("ParkingID"));
        review.setReservationId(rs.getInt("ReservationID"));

        return review;
    }
}