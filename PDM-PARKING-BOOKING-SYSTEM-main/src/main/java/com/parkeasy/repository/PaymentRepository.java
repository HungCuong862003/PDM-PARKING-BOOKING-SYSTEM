package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.Payment;
import main.java.com.parkeasy.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing Payment entities in the database.
 * Provides CRUD operations for payments.
 */
public class PaymentRepository {

    /**
     * Inserts a new payment into the database.
     *
     * @param payment The payment to insert
     * @return true if insertion was successful, false otherwise
     */
    public boolean insertPayment(Payment payment) {
        String sql = "INSERT INTO PAYMENT (PaymentMethod, Amount, PaymentDate, ReservationID, CardNumber) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, payment.getPaymentMethod());
            preparedStatement.setBigDecimal(2, payment.getAmount());
            preparedStatement.setTimestamp(3, payment.getPaymentDate());
            preparedStatement.setInt(4, payment.getReservationID());
            preparedStatement.setString(5, payment.getCardNumber());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        payment.setPaymentID(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error inserting payment: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Retrieves a payment by its ID.
     *
     * @param paymentID The ID of the payment to retrieve
     * @return The found Payment or null if not found
     */
    public Payment getPaymentById(int paymentID) {
        String sql = "SELECT * FROM PAYMENT WHERE PaymentID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, paymentID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Payment payment = new Payment();
                    payment.setPaymentID(resultSet.getInt("PaymentID"));
                    payment.setPaymentMethod(resultSet.getString("PaymentMethod"));
                    payment.setAmount(resultSet.getBigDecimal("Amount"));
                    payment.setPaymentDate(resultSet.getTimestamp("PaymentDate"));
                    payment.setReservationID(resultSet.getInt("ReservationID"));
                    payment.setCardNumber(resultSet.getString("CardNumber"));
                    return payment;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving payment by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Retrieves all payments for a specific reservation.
     *
     * @param reservationID The ID of the reservation
     * @return A list of payments for the specified reservation
     */
    public List<Payment> getPaymentsByReservationId(int reservationID) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM PAYMENT WHERE ReservationID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, reservationID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Payment payment = new Payment();
                    payment.setPaymentID(resultSet.getInt("PaymentID"));
                    payment.setPaymentMethod(resultSet.getString("PaymentMethod"));
                    payment.setAmount(resultSet.getBigDecimal("Amount"));
                    payment.setPaymentDate(resultSet.getTimestamp("PaymentDate"));
                    payment.setReservationID(resultSet.getInt("ReservationID"));
                    payment.setCardNumber(resultSet.getString("CardNumber"));
                    payments.add(payment);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving payments by reservation ID: " + e.getMessage());
            e.printStackTrace();
        }

        return payments;
    }

    /**
     * Retrieves all payments from the database.
     *
     * @return A list of all payments
     */
    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM PAYMENT";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Payment payment = new Payment();
                payment.setPaymentID(resultSet.getInt("PaymentID"));
                payment.setPaymentMethod(resultSet.getString("PaymentMethod"));
                payment.setAmount(resultSet.getBigDecimal("Amount"));
                payment.setPaymentDate(resultSet.getTimestamp("PaymentDate"));
                payment.setReservationID(resultSet.getInt("ReservationID"));
                payment.setCardNumber(resultSet.getString("CardNumber"));
                payments.add(payment);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all payments: " + e.getMessage());
            e.printStackTrace();
        }

        return payments;
    }

    /**
     * Updates an existing payment in the database.
     *
     * @param payment The updated payment information
     * @return true if update was successful, false otherwise
     */
    public boolean updatePayment(Payment payment) {
        String sql = "UPDATE PAYMENT SET PaymentMethod = ?, Amount = ?, PaymentDate = ?, ReservationID = ?, CardNumber = ? WHERE PaymentID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, payment.getPaymentMethod());
            preparedStatement.setBigDecimal(2, payment.getAmount());
            preparedStatement.setTimestamp(3, payment.getPaymentDate());
            preparedStatement.setInt(4, payment.getReservationID());
            preparedStatement.setString(5, payment.getCardNumber());
            preparedStatement.setInt(6, payment.getPaymentID());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating payment: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Deletes a payment by its ID.
     *
     * @param paymentID The ID of the payment to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deletePaymentById(int paymentID) {
        String sql = "DELETE FROM PAYMENT WHERE PaymentID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, paymentID);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting payment: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Deletes all payments for a specific reservation.
     *
     * @param reservationID The ID of the reservation
     * @return true if deletion was successful, false otherwise
     */
    public boolean deletePaymentsByReservationId(int reservationID) {
        String sql = "DELETE FROM PAYMENT WHERE ReservationID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, reservationID);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting payments by reservation ID: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Retrieves all payments made by a specific user.
     *
     * @param userID The ID of the user
     * @return A list of payments made by the specified user
     */
    public List<Payment> getPaymentsByUserId(int userID) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.* FROM PAYMENT p " +
                "JOIN PARKING_RESERVATION r ON p.ReservationID = r.ReservationID " +
                "JOIN VEHICLE v ON r.VehicleID = v.VehicleID " +
                "WHERE v.UserID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Payment payment = new Payment();
                    payment.setPaymentID(resultSet.getInt("PaymentID"));
                    payment.setPaymentMethod(resultSet.getString("PaymentMethod"));
                    payment.setAmount(resultSet.getBigDecimal("Amount"));
                    payment.setPaymentDate(resultSet.getTimestamp("PaymentDate"));
                    payment.setReservationID(resultSet.getInt("ReservationID"));
                    payment.setCardNumber(resultSet.getString("CardNumber"));
                    payments.add(payment);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving payments by user ID: " + e.getMessage());
            e.printStackTrace();
        }

        return payments;
    }

    /**
     * Calculates the total payment amount for a specific reservation.
     *
     * @param reservationID The ID of the reservation
     * @return The total payment amount
     */
    public BigDecimal getPaymentAmountByReservation(int reservationID) {
        String sql = "SELECT SUM(Amount) AS TotalAmount FROM PAYMENT WHERE ReservationID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, reservationID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal("TotalAmount");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error calculating payment amount by reservation: " + e.getMessage());
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }
}