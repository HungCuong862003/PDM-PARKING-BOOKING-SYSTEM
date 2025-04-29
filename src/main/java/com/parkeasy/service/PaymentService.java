package main.java.com.parkeasy.service;

import main.java.com.parkeasy.model.Payment;
import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.repository.PaymentRepository;
import main.java.com.parkeasy.repository.ReservationRepository;
import main.java.com.parkeasy.util.Constants;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for payment-related operations
 */
public class PaymentService {
    private static final Logger LOGGER = Logger.getLogger(PaymentService.class.getName());

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    /**
     * Constructor with dependency injection
     */
    public PaymentService(PaymentRepository paymentRepository, ReservationRepository reservationRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Default constructor
     */
    public PaymentService() {
        this.paymentRepository = new PaymentRepository();
        this.reservationRepository = new ReservationRepository();
    }

    /**
     * Process a payment
     *
     * @param payment The payment to process
     * @return true if successful, false otherwise
     */
    public boolean processPayment(Payment payment) {
        try {
            LOGGER.log(Level.INFO, "Processing payment for reservation: {0}", payment.getReservationID());

            // Generate a transaction ID if not provided
            if (payment.getTransactionID() == null || payment.getTransactionID().isEmpty()) {
                payment.setTransactionID(generateTransactionId());
            }

            // Set status if not provided
            if (payment.getStatus() == null || payment.getStatus().isEmpty()) {
                payment.setStatus(Constants.PAYMENT_PENDING);
            }

            // Set payment date if not provided
            if (payment.getPaymentDate() == null) {
                payment.setPaymentDate(new Timestamp(System.currentTimeMillis()));
            }

            // Insert the payment
            int paymentId = paymentRepository.insertPayment(payment);

            if (paymentId > 0) {
                // Update reservation status to PAID
                Reservation reservation = reservationRepository.getReservationById(payment.getReservationID());
                if (reservation != null) {
                    reservation.setStatus(Constants.RESERVATION_PAID);
                    reservationRepository.updateReservationById(reservation.getReservationID(), reservation);
                }

                // Update payment with generated ID and status
                payment.setPaymentID(paymentId);
                payment.setStatus(Constants.PAYMENT_COMPLETED);
                paymentRepository.updatePayment(payment);

                return true;
            }

            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error processing payment", e);
            return false;
        }
    }

    /**
     * Generate a unique transaction ID
     *
     * @return A unique transaction ID
     */
    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Get a payment by ID
     *
     * @param paymentId The ID of the payment
     * @return The payment or null if not found
     */
    public Payment getPaymentById(int paymentId) {
        try {
            LOGGER.log(Level.INFO, "Getting payment by ID: {0}", paymentId);
            return paymentRepository.getPaymentById(paymentId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting payment by ID", e);
            return null;
        }
    }

    /**
     * Get payments by reservation ID
     *
     * @param reservationId The ID of the reservation
     * @return List of payments for the reservation
     */
    public List<Payment> getPaymentsByReservationId(int reservationId) {
        try {
            LOGGER.log(Level.INFO, "Getting payments by reservation ID: {0}", reservationId);
            return paymentRepository.getPaymentsByReservationId(reservationId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting payments by reservation ID", e);
            return List.of(); // Return empty list on error
        }
    }

    /**
     * Get payments by user ID
     *
     * @param userId The ID of the user
     * @return List of payments made by the user
     */
    public List<Payment> getPaymentsByUserId(int userId) {
        try {
            LOGGER.log(Level.INFO, "Getting payments by user ID: {0}", userId);
            return paymentRepository.getPaymentsByUserId(userId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting payments by user ID", e);
            return List.of(); // Return empty list on error
        }
    }

    /**
     * Get total amount spent by a user
     *
     * @param userId The ID of the user
     * @return Total amount spent
     */
    public double getTotalAmountByUserId(int userId) {
        try {
            LOGGER.log(Level.INFO, "Getting total amount by user ID: {0}", userId);
            return paymentRepository.getTotalAmountByUserId(userId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total amount by user ID", e);
            return 0.0;
        }
    }

    /**
     * Get total amount spent by a user within a date range
     *
     * @param userId The ID of the user
     * @param startDate Start date
     * @param endDate End date
     * @return Total amount spent within the date range
     */
    public double getTotalAmountByUserIdAndDateRange(int userId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            LOGGER.log(Level.INFO, "Getting total amount by user ID and date range: {0}", userId);

            // Get all payments by user
            List<Payment> payments = getPaymentsByUserId(userId);

            // Filter by date range and sum amounts
            return payments.stream()
                    .filter(payment -> {
                        LocalDateTime paymentDate = payment.getPaymentDate().toLocalDateTime();
                        return !paymentDate.isBefore(startDate) && !paymentDate.isAfter(endDate);
                    })
                    .mapToDouble(Payment::getAmount)
                    .sum();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting total amount by user ID and date range", e);
            return 0.0;
        }
    }

    /**
     * Refund a payment
     *
     * @param paymentId The ID of the payment to refund
     * @return true if successful, false otherwise
     */
    public boolean refundPayment(int paymentId) {
        try {
            LOGGER.log(Level.INFO, "Refunding payment: {0}", paymentId);

            // Get the payment
            Payment payment = getPaymentById(paymentId);
            if (payment == null) {
                LOGGER.log(Level.WARNING, "Payment not found for refund: {0}", paymentId);
                return false;
            }

            // Check if payment is already refunded
            if (Constants.PAYMENT_REFUNDED.equals(payment.getStatus())) {
                LOGGER.log(Level.WARNING, "Payment already refunded: {0}", paymentId);
                return false;
            }

            // Update payment status to refunded
            payment.setStatus(Constants.PAYMENT_REFUNDED);
            boolean updated = paymentRepository.updatePayment(payment);

            if (updated) {
                // Update reservation status to cancelled
                Reservation reservation = reservationRepository.getReservationById(payment.getReservationID());
                if (reservation != null) {
                    reservation.setStatus(Constants.RESERVATION_CANCELLED);
                    reservationRepository.updateReservationById(reservation.getReservationID(), reservation);
                }

                return true;
            }

            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error refunding payment", e);
            return false;
        }
    }

    /**
     * Check if a reservation has been paid
     *
     * @param reservationId The ID of the reservation
     * @return true if paid, false otherwise
     */
    public boolean isReservationPaid(int reservationId) {
        try {
            LOGGER.log(Level.INFO, "Checking if reservation is paid: {0}", reservationId);

            // Get reservation
            Reservation reservation = reservationRepository.getReservationById(reservationId);
            if (reservation == null) {
                return false;
            }

            // Check status
            return Constants.RESERVATION_PAID.equals(reservation.getStatus());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking if reservation is paid", e);
            return false;
        }
    }

    /**
     * Get payment by reservation ID
     *
     * @param reservationId The ID of the reservation
     * @return The payment or null if not found
     */
    public Payment getPaymentByReservationId(int reservationId) {
        try {
            LOGGER.log(Level.INFO, "Getting payment by reservation ID: {0}", reservationId);
            return paymentRepository.getPaymentByReservationId(reservationId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting payment by reservation ID", e);
            return null;
        }
    }
}