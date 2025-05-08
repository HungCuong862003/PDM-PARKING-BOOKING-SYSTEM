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
public class ParkingReviewRepository {
    private static final Logger LOGGER = Logger.getLogger(ParkingReviewRepository.class.getName());
    private String sql;
    private Connection connection;

    /**
     * Add a new review to the database
     *
     * @param review The review to add
     * @return The ID of the newly added review, or -1 if the operation failed
     */
    public int addReview(ParkingReview review) {
        connection = DatabaseConnection.getConnection();
        sql = "INSERT INTO PARKING_REVIEW (Rating, Comment, ReviewDate, UserID, ParkingID, ReservationID) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, review.getRating());

            if (review.getComment() != null) {
                preparedStatement.setString(2, review.getComment());
            } else {
                preparedStatement.setNull(2, java.sql.Types.VARCHAR);
            }

            preparedStatement.setTimestamp(3, Timestamp.valueOf(review.getReviewDate()));
            preparedStatement.setInt(4, review.getUserId());
            preparedStatement.setString(5, review.getParkingId());
            preparedStatement.setInt(6, review.getReservationId());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating review failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating review failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding review", e);
            return -1;
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Update an existing review
     *
     * @param review The review with updated information
     * @return true if the update was successful, false otherwise
     */
    public boolean updateReview(ParkingReview review) {
        connection = DatabaseConnection.getConnection();
        sql = "UPDATE PARKING_REVIEW SET Rating = ?, Comment = ? WHERE ReviewID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, review.getRating());

            if (review.getComment() != null) {
                preparedStatement.setString(2, review.getComment());
            } else {
                preparedStatement.setNull(2, java.sql.Types.VARCHAR);
            }

            preparedStatement.setInt(3, review.getReviewId());

            int rowsUpdated = preparedStatement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating review: " + review.getReviewId(), e);
            return false;
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Delete a review from the database
     *
     * @param reviewId The ID of the review to delete
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteReview(int reviewId) {
        connection = DatabaseConnection.getConnection();
        sql = "DELETE FROM PARKING_REVIEW WHERE ReviewID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, reviewId);

            int rowsDeleted = preparedStatement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting review: " + reviewId, e);
            return false;
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Get a review by its ID
     *
     * @param reviewId The ID of the review
     * @return The review object or null if not found
     */
    public ParkingReview getReviewById(int reviewId) {
        connection = DatabaseConnection.getConnection();
        sql = "SELECT * FROM PARKING_REVIEW WHERE ReviewID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, reviewId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToReview(resultSet);
            }
            return null;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting review by ID: " + reviewId, e);
            return null;
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Get all reviews for a parking space
     *
     * @param parkingId The ID of the parking space
     * @return List of reviews for the parking space
     */
    public List<ParkingReview> getReviewsByParkingId(String parkingId) {
        List<ParkingReview> reviews = new ArrayList<>();
        connection = DatabaseConnection.getConnection();
        sql = "SELECT * FROM PARKING_REVIEW WHERE ParkingID = ? ORDER BY ReviewDate DESC";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, parkingId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                reviews.add(mapResultSetToReview(resultSet));
            }
            return reviews;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting reviews by parking ID: " + parkingId, e);
            return reviews; // Return empty list on error
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Get all reviews by a user
     *
     * @param userId The ID of the user
     * @return List of reviews by the user
     */
    public List<ParkingReview> getReviewsByUserId(int userId) {
        List<ParkingReview> reviews = new ArrayList<>();
        connection = DatabaseConnection.getConnection();
        sql = "SELECT * FROM PARKING_REVIEW WHERE UserID = ? ORDER BY ReviewDate DESC";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                reviews.add(mapResultSetToReview(resultSet));
            }
            return reviews;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting reviews by user ID: " + userId, e);
            return reviews; // Return empty list on error
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Get a review by reservation ID
     *
     * @param reservationId The ID of the reservation
     * @return The review object or null if not found
     */
    public ParkingReview getReviewByReservationId(int reservationId) {
        connection = DatabaseConnection.getConnection();
        sql = "SELECT * FROM PARKING_REVIEW WHERE ReservationID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, reservationId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToReview(resultSet);
            }
            return null;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting review by reservation ID: " + reservationId, e);
            return null;
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Calculate the average rating for a parking space
     *
     * @param parkingId The ID of the parking space
     * @return The average rating or 0 if no reviews
     */
    public float getAverageRatingForParkingSpace(String parkingId) {
        connection = DatabaseConnection.getConnection();
        sql = "SELECT AVG(Rating) AS average_rating FROM PARKING_REVIEW WHERE ParkingID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, parkingId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                float averageRating = resultSet.getFloat("average_rating");
                return resultSet.wasNull() ? (float) 0.0 : averageRating;
            }
            return 0.0F;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error calculating average rating for parking space: " + parkingId, e);
            return 0.0F;
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Count the number of reviews for a parking space
     *
     * @param parkingId The ID of the parking space
     * @return The number of reviews
     */
    public int getReviewCountForParkingSpace(String parkingId) {
        connection = DatabaseConnection.getConnection();
        sql = "SELECT COUNT(*) AS review_count FROM PARKING_REVIEW WHERE ParkingID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, parkingId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("review_count");
            }
            return 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting reviews for parking space: " + parkingId, e);
            return 0;
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Get the latest reviews across all parking spaces
     *
     * @param limit The maximum number of reviews to return
     * @return List of the latest reviews
     */
    public List<ParkingReview> getLatestReviews(int limit) {
        List<ParkingReview> reviews = new ArrayList<>();
        connection = DatabaseConnection.getConnection();
        sql = "SELECT * FROM PARKING_REVIEW ORDER BY ReviewDate DESC LIMIT ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, limit);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                reviews.add(mapResultSetToReview(resultSet));
            }
            return reviews;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting latest reviews, limit: " + limit, e);
            return reviews; // Return empty list on error
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Maps a ResultSet to a ParkingReview object
     *
     * @param rs The ResultSet containing the review data
     * @return A ParkingReview object
     * @throws SQLException If there's an error processing the ResultSet
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