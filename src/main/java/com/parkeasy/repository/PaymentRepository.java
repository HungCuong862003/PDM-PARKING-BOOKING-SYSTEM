package main.java.com.parkeasy.repository;

import main.java.com.parkeasy.model.Payment;
import main.java.com.parkeasy.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepository {
    // insert a new payment into the database
    public void insertPayment(Payment payment) {
        String sql = "INSERT INTO payment (PaymentID, Amount, PaymentDate, ReservationID) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, payment.getPaymentID());
            preparedStatement.setDouble(2, payment.getAmount());
            preparedStatement.setTimestamp(3, payment.getPaymentDate());
            preparedStatement.setInt(4, payment.getReservationID());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // get a payment by its ID
    public Payment getPaymentById(int paymentID) {
        String sql = "SELECT * FROM payment WHERE PaymentID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, paymentID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Payment(resultSet.getInt("PaymentID"), resultSet.getString("PaymentMethod"),
                        resultSet.getFloat("Amount"), resultSet.getTimestamp("PaymentDate"),
                        resultSet.getInt("ReservationID"), resultSet.getString("CardNumber"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // get all payments by reservation ID
    public List<Payment> getPaymentsByReservationId(int reservationID) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payment WHERE ReservationID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, reservationID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                payments.add(new Payment(resultSet.getInt("PaymentID"), resultSet.getString("PaymentMethod"),
                        resultSet.getFloat("Amount"), resultSet.getTimestamp("PaymentDate"),
                        resultSet.getInt("ReservationID"), resultSet.getString("CardNumber")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    // get all payments
    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payment";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                payments.add(new Payment(resultSet.getInt("PaymentID"), resultSet.getString("PaymentMethod"),
                        resultSet.getFloat("Amount"), resultSet.getTimestamp("PaymentDate"),
                        resultSet.getInt("ReservationID"), resultSet.getString("CardNumber")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    // update a payment
    public void updatePayment(Payment payment) {
        String sql = "UPDATE payment SET PaymentMethod = ?, Amount = ?, PaymentDate = ?, ReservationID = ? WHERE PaymentID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, payment.getPaymentMethod());
            preparedStatement.setDouble(2, payment.getAmount());
            preparedStatement.setTimestamp(3, payment.getPaymentDate());
            preparedStatement.setInt(4, payment.getReservationID());
            preparedStatement.setInt(5, payment.getPaymentID());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // delete a payment by its ID
    public void deletePaymentById(int paymentID) {
        String sql = "DELETE FROM payment WHERE PaymentID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, paymentID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // delete all payments by reservation ID
    public void deletePaymentsByReservationId(int reservationID) {
        String sql = "DELETE FROM payment WHERE ReservationID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, reservationID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}