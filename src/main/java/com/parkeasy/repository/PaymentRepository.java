package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.Payment;
import main.java.com.parkeasy.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repository class for handling payment-related database operations
 */
public class PaymentRepository {
    private static final Logger LOGGER = Logger.getLogger(PaymentRepository.class.getName());
    private static final String TABLE_NAME = "PAYMENT";

    /**
     * Inserts a new payment into the database
     *
     * @param payment The payment to insert
     * @return The ID of the inserted payment
     * @throws SQLException if a database error occurs
     */
    public int insertPayment(Payment payment) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME +
                " (PaymentMethod, Amount, PaymentDate, ReservationID, CardNumber, Status, TransactionID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet generatedKeys = null;

        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, payment.getPaymentMethod());
            preparedStatement.setDouble(2, payment.getAmount());

            // Use current timestamp if payment date is null
            if (payment.getPaymentDate() == null) {
                preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            } else {
                preparedStatement.setTimestamp(3, payment.getPaymentDate());
            }

            preparedStatement.setInt(4, payment.getReservationID());
            preparedStatement.setString(5, payment.getCardNumber());
            preparedStatement.setString(6, payment.getStatus());
            preparedStatement.setString(7, payment.getTransactionID());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting payment", e);
            throw e;
        } finally {
            DatabaseConnection.closeResultSet(generatedKeys);
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Gets a payment by its ID
     *
     * @param paymentID The ID of the payment to get
     * @return The payment if found, null otherwise
     * @throws SQLException if a database error occurs
     */
    public Payment getPaymentById(int paymentID) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE PaymentID = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, paymentID);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return extractPaymentFromResultSet(resultSet);
            }
            return null;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting payment by ID", e);
            throw e;
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Gets all payments by reservation ID
     *
     * @param reservationID The ID of the reservation
     * @return List of payments for the specified reservation
     * @throws SQLException if a database error occurs
     */
    public List<Payment> getPaymentsByReservationId(int reservationID) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE ReservationID = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, reservationID);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                payments.add(extractPaymentFromResultSet(resultSet));
            }
            return payments;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting payments by reservation ID", e);
            throw e;
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Gets all payments
     *
     * @return List of all payments
     * @throws SQLException if a database error occurs
     */
    public List<Payment> getAllPayments() throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                payments.add(extractPaymentFromResultSet(resultSet));
            }
            return payments;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all payments", e);
            throw e;
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(statement);
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Updates a payment in the database
     *
     * @param payment The payment with updated information
     * @return true if update was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean updatePayment(Payment payment) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME +
                " SET PaymentMethod = ?, Amount = ?, PaymentDate = ?, ReservationID = ?, " +
                "CardNumber = ?, Status = ?, TransactionID = ? WHERE PaymentID = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, payment.getPaymentMethod());
            preparedStatement.setDouble(2, payment.getAmount());
            preparedStatement.setTimestamp(3, payment.getPaymentDate());
            preparedStatement.setInt(4, payment.getReservationID());
            preparedStatement.setString(5, payment.getCardNumber());
            preparedStatement.setString(6, payment.getStatus());
            preparedStatement.setString(7, payment.getTransactionID());
            preparedStatement.setInt(8, payment.getPaymentID());
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating payment", e);
            throw e;
        } finally {
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Deletes a payment by its ID
     *
     * @param paymentID The ID of the payment to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean deletePaymentById(int paymentID) throws SQLException {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE PaymentID = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, paymentID);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting payment by ID", e);
            throw e;
        } finally {
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Deletes all payments by reservation ID
     *
     * @param reservationID The ID of the reservation
     * @return true if deletion was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean deletePaymentsByReservationId(int reservationID) throws SQLException {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE ReservationID = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, reservationID);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting payments by reservation ID", e);
            throw e;
        } finally {
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Gets the total amount spent by a user
     *
     * @param userId The ID of the user
     * @return The total amount spent by the user
     * @throws SQLException if a database error occurs
     */
    public double getTotalAmountByUserId(int userId) throws SQLException {
        String sql = "SELECT SUM(p.Amount) as TotalAmount FROM " + TABLE_NAME + " p " +
                "JOIN parking_reservation pr ON p.ReservationID = pr.ReservationID " +
                "JOIN vehicle v ON pr.VehicleID = v.VehicleID " +
                "WHERE v.UserID = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                double totalAmount = resultSet.getDouble("TotalAmount");
                // If the result is NULL (no payments found), SQL will return 0
                return resultSet.wasNull() ? 0.0 : totalAmount;
            }
            return 0.0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total amount by user ID", e);
            throw e;
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Helper method to extract a Payment object from a ResultSet
     *
     * @param resultSet The ResultSet to extract from
     * @return The extracted Payment object
     * @throws SQLException if a database error occurs
     */
    private Payment extractPaymentFromResultSet(ResultSet resultSet) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentID(resultSet.getInt("PaymentID"));
        payment.setPaymentMethod(resultSet.getString("PaymentMethod"));
        payment.setAmount(resultSet.getDouble("Amount"));
        payment.setPaymentDate(resultSet.getTimestamp("PaymentDate"));
        payment.setReservationID(resultSet.getInt("ReservationID"));
        payment.setCardNumber(resultSet.getString("CardNumber"));

        // These fields might be NULL in older records if they weren't part of the original schema
        try {
            payment.setStatus(resultSet.getString("Status"));
            payment.setTransactionID(resultSet.getString("TransactionID"));
        } catch (SQLException e) {
            // Ignore these fields if they don't exist in the result set
            LOGGER.log(Level.FINE, "Status or TransactionID fields not found in result set", e);
        }

        return payment;
    }

    /**
     * Gets a payment by reservation ID
     *
     * @param reservationId The ID of the reservation
     * @return The payment if found, null otherwise
     */
    public Payment getPaymentByReservationId(int reservationId) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE ReservationID = ? LIMIT 1";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, reservationId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return extractPaymentFromResultSet(resultSet);
            }
            return null;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting payment by reservation ID", e);
            return null;
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Gets all payments between two dates
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of payments between the dates
     */
    public List<Payment> getPaymentsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE PaymentDate BETWEEN ? AND ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(startDate));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(endDate));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                payments.add(extractPaymentFromResultSet(resultSet));
            }
            return payments;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting payments between dates", e);
            return payments;
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Gets payments for reservations in specific parking spaces within a date range
     *
     * @param parkingIds List of parking space IDs
     * @param startDate The start date
     * @param endDate The end date
     * @return List of payments matching the criteria
     */
    public List<Payment> getPaymentsByParkingIdsAndDateRange(List<String> parkingIds, LocalDateTime startDate, LocalDateTime endDate) {
        List<Payment> payments = new ArrayList<>();

        if (parkingIds == null || parkingIds.isEmpty()) {
            return payments;
        }

        // Building the SQL with placeholders for the IN clause
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < parkingIds.size(); i++) {
            placeholders.append("?");
            if (i < parkingIds.size() - 1) {
                placeholders.append(",");
            }
        }

        String sql = "SELECT p.* FROM " + TABLE_NAME + " p " +
                "JOIN parking_reservation pr ON p.ReservationID = pr.ReservationID " +
                "JOIN parkingslot ps ON pr.SlotNumber = ps.SlotNumber " +
                "WHERE ps.ParkingID IN (" + placeholders.toString() + ") " +
                "AND p.PaymentDate BETWEEN ? AND ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            // Set the parking IDs in the IN clause
            for (int i = 0; i < parkingIds.size(); i++) {
                preparedStatement.setString(i + 1, parkingIds.get(i));
            }

            // Set the date range parameters
            preparedStatement.setTimestamp(parkingIds.size() + 1, Timestamp.valueOf(startDate));
            preparedStatement.setTimestamp(parkingIds.size() + 2, Timestamp.valueOf(endDate));

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                payments.add(extractPaymentFromResultSet(resultSet));
            }
            return payments;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting payments by parking IDs and date range", e);
            return payments;
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Gets all payments for a specific parking space within a date range
     *
     * @param parkingId The ID of the parking space
     * @param startDate The start date
     * @param endDate The end date
     * @return List of payments matching the criteria
     */
    public List<Payment> getPaymentsByParkingIdAndDateRange(String parkingId, Date startDate, Date endDate) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.* FROM " + TABLE_NAME + " p " +
                "JOIN parking_reservation pr ON p.ReservationID = pr.ReservationID " +
                "JOIN parkingslot ps ON pr.SlotNumber = ps.SlotNumber " +
                "WHERE ps.ParkingID = ? " +
                "AND p.PaymentDate BETWEEN ? AND ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, parkingId);
            preparedStatement.setTimestamp(2, new Timestamp(startDate.getTime()));
            preparedStatement.setTimestamp(3, new Timestamp(endDate.getTime()));

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Payment payment = new Payment();
                payment.setPaymentID(resultSet.getInt("PaymentID"));
                payment.setPaymentMethod(resultSet.getString("PaymentMethod"));
                payment.setAmount(resultSet.getDouble("Amount"));
                payment.setPaymentDate(resultSet.getTimestamp("PaymentDate"));
                payment.setReservationID(resultSet.getInt("ReservationID"));
                payment.setCardNumber(resultSet.getString("CardNumber"));

                // These fields might be NULL in older records
                try {
                    payment.setStatus(resultSet.getString("Status"));
                    payment.setTransactionID(resultSet.getString("TransactionID"));
                } catch (SQLException e) {
                    // Ignore these fields if they don't exist
                    LOGGER.log(Level.FINE, "Status or TransactionID fields not found in result set", e);
                }

                payments.add(payment);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting payments by parking ID and date range: " + parkingId, e);
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }

        return payments;
    }

    /**
     * Gets payments by user ID
     *
     * @param userId User ID
     * @return List of payments for the user
     */
    public List<Payment> getPaymentsByUserId(int userId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.* FROM " + TABLE_NAME + " p " +
                "JOIN parking_reservation pr ON p.ReservationID = pr.ReservationID " +
                "JOIN vehicle v ON pr.VehicleID = v.VehicleID " +
                "WHERE v.UserID = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Payment payment = new Payment();
                payment.setPaymentID(resultSet.getInt("PaymentID"));
                payment.setPaymentMethod(resultSet.getString("PaymentMethod"));
                payment.setAmount(resultSet.getDouble("Amount"));
                payment.setPaymentDate(resultSet.getTimestamp("PaymentDate"));
                payment.setReservationID(resultSet.getInt("ReservationID"));
                payment.setCardNumber(resultSet.getString("CardNumber"));

                // Try to get status and transaction ID
                try {
                    payment.setStatus(resultSet.getString("Status"));
                    payment.setTransactionID(resultSet.getString("TransactionID"));
                } catch (SQLException e) {
                    // Ignore fields that don't exist
                    LOGGER.log(Level.FINE, "Status or TransactionID fields not found in result set", e);
                }

                payments.add(payment);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting payments by user ID: " + userId, e);
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(preparedStatement);
            DatabaseConnection.closeConnection(connection);
        }

        return payments;
    }
}