package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.Payment;
import main.java.com.parkeasy.model.Card;
import main.java.com.parkeasy.repository.PaymentRepository;
import main.java.com.parkeasy.repository.CardRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for handling payment operations in the ParkEasy system.
 * Provides functionality for creating, retrieving, updating, and deleting payments,
 * as well as handling card operations and calculating costs.
 */
public class PaymentService {
    private static final Logger LOGGER = Logger.getLogger(PaymentService.class.getName());

    private final PaymentRepository paymentRepository;
    private final CardRepository cardRepository;

    // Constants for payment methods
    public static final String PAYMENT_METHOD_CARD = "CARD";
    public static final String PAYMENT_METHOD_WALLET = "WALLET";
    public static final String PAYMENT_METHOD_REFUND = "REFUND";

    /**
     * Constructor for PaymentService
     * @param paymentRepository The payment repository for database operations
     */
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
        this.cardRepository = new CardRepository();
    }

    /**
     * Inserts a new payment into the database
     * @param payment The payment to insert
     * @return boolean indicating success or failure
     */
    public boolean insertPayment(Payment payment) {
        if (payment == null || !isValidPayment(payment)) {
            System.err.println("Invalid payment data provided");
            return false;
        }

        try {
            return paymentRepository.insertPayment(payment);
        } catch (Exception e) {
            handleException("Error inserting payment", e);
            return false;
        }
    }

    /**
     * Validates payment data
     * @param payment The payment to validate
     * @return boolean indicating if the payment is valid
     */
    private boolean isValidPayment(Payment payment) {
        if (payment.getReservationID() <= 0) {
            System.err.println("Invalid reservation ID");
            return false;
        }

        if (payment.getPaymentMethod() == null || payment.getPaymentMethod().trim().isEmpty()) {
            System.err.println("Payment method is required");
            return false;
        }

        if (payment.getAmount() == null) {
            System.err.println("Payment amount is required");
            return false;
        }

        if (payment.getPaymentDate() == null) {
            System.err.println("Payment date is required");
            return false;
        }

        return true;
    }

    /**
     * Gets a payment by its ID
     * @param paymentID The ID of the payment
     * @return Payment object if found, null otherwise
     */
    public Payment getPaymentById(int paymentID) {
        if (paymentID <= 0) {
            System.err.println("Invalid payment ID provided");
            return null;
        }

        try {
            return paymentRepository.getPaymentById(paymentID);
        } catch (Exception e) {
            handleException("Error retrieving payment", e);
            return null;
        }
    }

    /**
     * Gets all payments by reservation ID
     * @param reservationID The ID of the reservation
     * @return List of payments for the reservation
     */
    public List<Payment> getPaymentsByReservationId(int reservationID) {
        if (reservationID <= 0) {
            System.err.println("Invalid reservation ID provided");
            return new ArrayList<>();
        }

        try {
            return paymentRepository.getPaymentsByReservationId(reservationID);
        } catch (Exception e) {
            handleException("Error retrieving payments by reservation", e);
            return new ArrayList<>();
        }
    }

    /**
     * Gets all payments in the system
     * @return List of all payments
     */
    public List<Payment> getAllPayments() {
        try {
            return paymentRepository.getAllPayments();
        } catch (Exception e) {
            handleException("Error retrieving all payments", e);
            return new ArrayList<>();
        }
    }

    /**
     * Updates a payment's information
     * @param payment The updated payment
     * @return boolean indicating success or failure
     */
    public boolean updatePayment(Payment payment) {
        if (payment == null || payment.getPaymentID() <= 0 || !isValidPayment(payment)) {
            System.err.println("Invalid payment data provided for update");
            return false;
        }

        try {
            // Check if payment exists
            Payment existingPayment = paymentRepository.getPaymentById(payment.getPaymentID());
            if (existingPayment == null) {
                System.err.println("Payment not found with ID: " + payment.getPaymentID());
                return false;
            }

            return paymentRepository.updatePayment(payment);
        } catch (Exception e) {
            handleException("Error updating payment", e);
            return false;
        }
    }

    /**
     * Deletes a payment by its ID
     * @param paymentID The ID of the payment to delete
     * @return boolean indicating success or failure
     */
    public boolean deletePayment(int paymentID) {
        if (paymentID <= 0) {
            System.err.println("Invalid payment ID provided");
            return false;
        }

        try {
            // Check if payment exists
            Payment existingPayment = paymentRepository.getPaymentById(paymentID);
            if (existingPayment == null) {
                System.err.println("Payment not found with ID: " + paymentID);
                return false;
            }

            return paymentRepository.deletePaymentById(paymentID);
        } catch (Exception e) {
            handleException("Error deleting payment", e);
            return false;
        }
    }

    /**
     * Deletes all payments associated with a reservation
     * @param reservationID The ID of the reservation
     * @return boolean indicating success or failure
     */
    public boolean deletePaymentsByReservationId(int reservationID) {
        if (reservationID <= 0) {
            System.err.println("Invalid reservation ID provided");
            return false;
        }

        try {
            return paymentRepository.deletePaymentsByReservationId(reservationID);
        } catch (Exception e) {
            handleException("Error deleting payments by reservation", e);
            return false;
        }
    }

    /**
     * Creates a new payment for a reservation
     * @param reservationID The ID of the reservation
     * @param paymentMethod The payment method used
     * @param amount The payment amount
     * @param cardNumber The card number used for payment (can be null for non-card payments)
     * @return The ID of the created payment, or -1 if failed
     */
    public int createPayment(int reservationID, String paymentMethod, BigDecimal amount, String cardNumber) {
        // Validate inputs
        if (reservationID <= 0) {
            System.err.println("Invalid reservation ID provided");
            return -1;
        }

        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            System.err.println("Payment method is required");
            return -1;
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            System.err.println("Valid payment amount is required");
            return -1;
        }

        // If payment method is card, validate card number
        if (PAYMENT_METHOD_CARD.equals(paymentMethod) && (cardNumber == null || cardNumber.trim().isEmpty())) {
            System.err.println("Card number is required for card payments");
            return -1;
        }

        // If it's a card payment, verify the card exists
        if (PAYMENT_METHOD_CARD.equals(paymentMethod) && !isCardRegistered(cardNumber)) {
            System.err.println("Card is not registered in the system");
            return -1;
        }

        try {
            Payment payment = new Payment();
            payment.setReservationID(reservationID);
            payment.setPaymentMethod(paymentMethod);
            payment.setAmount(amount);
            payment.setPaymentDate(new Timestamp(System.currentTimeMillis()));
            payment.setCardNumber(cardNumber);

            boolean success = paymentRepository.insertPayment(payment);
            if (success) {
                return payment.getPaymentID();
            }
            return -1;
        } catch (Exception e) {
            handleException("Error creating payment", e);
            return -1;
        }
    }

    /**
     * Checks if a card is registered in the system
     * @param cardNumber The card number to check
     * @return boolean indicating if the card is registered
     */
    private boolean isCardRegistered(String cardNumber) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            return false;
        }

        try {
            Card card = cardRepository.getCardByNumber(cardNumber);
            return card != null;
        } catch (Exception e) {
            handleException("Error checking if card is registered", e);
            return false;
        }
    }

    /**
     * Handles exceptions by logging them
     * @param message The error message
     * @param e The exception that occurred
     */
    private void handleException(String message, Exception e) {
        LOGGER.log(Level.SEVERE, message, e);
        System.err.println(message + ": " + e.getMessage());

        // For SQL exceptions, log more detailed information
        if (e instanceof SQLException) {
            SQLException sqlEx = (SQLException) e;
            LOGGER.log(Level.SEVERE, "SQL State: " + sqlEx.getSQLState());
            LOGGER.log(Level.SEVERE, "Error Code: " + sqlEx.getErrorCode());
        }
    }
}