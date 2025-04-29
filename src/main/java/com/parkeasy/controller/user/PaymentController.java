package main.java.com.parkeasy.controller.user;

import main.java.com.parkeasy.model.Card;
import main.java.com.parkeasy.model.Payment;
import main.java.com.parkeasy.model.Reservation;
import main.java.com.parkeasy.model.User;
import main.java.com.parkeasy.service.CardService;
import main.java.com.parkeasy.service.PaymentService;
import main.java.com.parkeasy.service.ReservationService;
import main.java.com.parkeasy.service.UserService;
import main.java.com.parkeasy.util.Constants;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for handling payment operations
 */
public class PaymentController {
    private static final Logger LOGGER = Logger.getLogger(PaymentController.class.getName());

    private final PaymentService paymentService;
    private final CardService cardService;
    private final ReservationService reservationService;
    private final UserService userService;

    /**
     * Constructor with dependency injection
     */
    public PaymentController(PaymentService paymentService, CardService cardService,
                             ReservationService reservationService, UserService userService) {
        this.paymentService = paymentService;
        this.cardService = cardService;
        this.reservationService = reservationService;
        this.userService = userService;
    }

    /**
     * Process payment for a reservation
     *
     * @param reservationId The ID of the reservation to pay for
     * @param cardNumber The card number to use for payment
     * @param paymentMethod The payment method (CARD, BALANCE)
     * @return Result of the payment process containing success status and message
     */
    public Map<String, Object> processPayment(int reservationId, String cardNumber, String paymentMethod) {
        try {
            // Get the reservation
            Reservation reservation = reservationService.getReservationById(reservationId);
            if (reservation == null) {
                LOGGER.log(Level.WARNING, "Reservation not found: {0}", reservationId);
                return Map.of(
                        "success", false,
                        "message", "Reservation not found"
                );
            }

            // Calculate payment amount based on reservation duration
            double amount = calculatePaymentAmount(reservation);

            if (paymentMethod.equals("BALANCE")) {
                // Process payment using user balance
                User user = userService.getUserByVehicleId(reservation.getVehicleID());
                if (user == null) {
                    return Map.of(
                            "success", false,
                            "message", "User not found"
                    );
                }

                if (user.getBalance() < amount) {
                    return Map.of(
                            "success", false,
                            "message", "Insufficient balance"
                    );
                }

                // Deduct from user balance
                user.setBalance(user.getBalance() - amount);
                userService.updateUser(user);

                // Create payment record
                Payment payment = new Payment();
                payment.setPaymentMethod("BALANCE");
                payment.setAmount((float) amount);
                payment.setPaymentDate(Timestamp.valueOf(LocalDateTime.now()));
                payment.setReservationID(reservationId);
                payment.setCardNumber(null);  // No card used

                boolean success = paymentService.processPayment(payment);
                if (success) {
                    // Update reservation status
                    reservation.setStatus(Constants.RESERVATION_PAID);
                    reservationService.updateReservation(reservation);

                    return Map.of(
                            "success", true,
                            "message", "Payment processed successfully",
                            "amount", amount,
                            "paymentId", payment.getPaymentID()
                    );
                } else {
                    // Rollback balance deduction
                    user.setBalance(user.getBalance() + amount);
                    userService.updateUser(user);

                    return Map.of(
                            "success", false,
                            "message", "Payment processing failed"
                    );
                }
            } else if (paymentMethod.equals("CARD")) {
                // Process payment using card
                Card card = cardService.getCardByNumber(cardNumber);
                if (card == null) {
                    return Map.of(
                            "success", false,
                            "message", "Card not found"
                    );
                }

                // Check if card is valid
                if (card.getValidTo().before(java.sql.Date.valueOf(LocalDate.now()))) {
                    return Map.of(
                            "success", false,
                            "message", "Card has expired"
                    );
                }

                // Create payment record
                Payment payment = new Payment();
                payment.setPaymentMethod("CARD");
                payment.setAmount((float) amount);
                payment.setPaymentDate(Timestamp.valueOf(LocalDateTime.now()));
                payment.setReservationID(reservationId);
                payment.setCardNumber(cardNumber);

                boolean success = paymentService.processPayment(payment);
                if (success) {
                    // Update reservation status
                    reservation.setStatus(Constants.RESERVATION_PAID);
                    reservationService.updateReservation(reservation);

                    return Map.of(
                            "success", true,
                            "message", "Payment processed successfully",
                            "amount", amount,
                            "paymentId", payment.getPaymentID()
                    );
                } else {
                    return Map.of(
                            "success", false,
                            "message", "Payment processing failed"
                    );
                }
            } else {
                return Map.of(
                        "success", false,
                        "message", "Invalid payment method"
                );
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing payment for reservation: " + reservationId, e);
            return Map.of(
                    "success", false,
                    "message", "Payment processing error: " + e.getMessage()
            );
        }
    }

    /**
     * Calculate payment amount based on reservation details
     *
     * @param reservation The reservation to calculate payment for
     * @return The calculated payment amount
     */
    private double calculatePaymentAmount(Reservation reservation) {
        try {
            // Get parking space details to determine cost
            String slotNumber = reservation.getSlotNumber();
            String parkingId = reservationService.getParkingIdBySlotNumber(slotNumber);
            double hourlyRate = reservationService.getParkingHourlyRate(parkingId);

            // Calculate duration in hours
            LocalDateTime startDateTime = LocalDateTime.of(
                    reservation.getStartDate().toLocalDate(),
                    reservation.getStartTime().toLocalTime()
            );

            LocalDateTime endDateTime = LocalDateTime.of(
                    reservation.getEndDate().toLocalDate(),
                    reservation.getEndTime().toLocalTime()
            );

            long durationHours = Duration.between(startDateTime, endDateTime).toHours();
            // Ensure minimum 1 hour charge
            if (durationHours < 1) durationHours = 1;

            return hourlyRate * durationHours;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating payment amount", e);
            return 0.0;
        }
    }

    /**
     * Get payment history for a user
     *
     * @param userId The ID of the user
     * @return List of payments made by the user
     */
    public List<Payment> getUserPaymentHistory(int userId) {
        try {
            return paymentService.getPaymentsByUserId(userId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving payment history for user: " + userId, e);
            return List.of(); // Return empty list on error
        }
    }

    /**
     * Get payment details
     *
     * @param paymentId The ID of the payment
     * @return Payment details or null if not found
     */
    public Payment getPaymentDetails(int paymentId) {
        try {
            return paymentService.getPaymentById(paymentId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving payment details: " + paymentId, e);
            return null;
        }
    }

    /**
     * Get payment receipt
     *
     * @param paymentId The ID of the payment
     * @return Map containing receipt details
     */
    public Map<String, Object> getPaymentReceipt(int paymentId) {
        try {
            Payment payment = paymentService.getPaymentById(paymentId);
            if (payment == null) {
                return Map.of(
                        "success", false,
                        "message", "Payment not found"
                );
            }

            Reservation reservation = reservationService.getReservationById(payment.getReservationID());
            if (reservation == null) {
                return Map.of(
                        "success", false,
                        "message", "Reservation not found"
                );
            }

            String slotNumber = reservation.getSlotNumber();
            String parkingId = reservationService.getParkingIdBySlotNumber(slotNumber);
            String parkingAddress = reservationService.getParkingAddressByParkingId(parkingId);

            return Map.of(
                    "success", true,
                    "receiptId", "REC-" + paymentId,
                    "paymentDate", payment.getPaymentDate(),
                    "amount", payment.getAmount(),
                    "paymentMethod", payment.getPaymentMethod(),
                    "reservationId", payment.getReservationID(),
                    "parkingAddress", parkingAddress,
                    "slotNumber", slotNumber,
                    "startDateTime", LocalDateTime.of(
                            reservation.getStartDate().toLocalDate(),
                            reservation.getStartTime().toLocalTime()
                    ),
                    "endDateTime", LocalDateTime.of(
                            reservation.getEndDate().toLocalDate(),
                            reservation.getEndTime().toLocalTime()
                    )
            );
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating receipt for payment: " + paymentId, e);
            return Map.of(
                    "success", false,
                    "message", "Error generating receipt: " + e.getMessage()
            );
        }
    }

    /**
     * Get user's saved cards
     *
     * @param userId The ID of the user
     * @return List of cards saved by the user
     */
    public List<Card> getUserCards(int userId) {
        try {
            return cardService.getCardsByUserId(userId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving cards for user: " + userId, e);
            return List.of(); // Return empty list on error
        }
    }

    /**
     * Add a new card for a user
     *
     * @param card The card to add
     * @return true if the card was added successfully, false otherwise
     */
    public boolean addCard(Card card) {
        try {
            return cardService.addCard(card);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding card for user: " + card.getUserID(), e);
            return false;
        }
    }

    /**
     * Remove a card
     *
     * @param cardNumber The card number to remove
     * @param userId The ID of the user who owns the card
     * @return true if the card was removed successfully, false otherwise
     */
    public boolean removeCard(String cardNumber, int userId) {
        try {
            // Verify card belongs to user
            Card card = cardService.getCardByNumber(cardNumber);
            if (card == null || card.getUserID() != userId) {
                return false;
            }
            return cardService.removeCard(cardNumber);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error removing card: " + cardNumber, e);
            return false;
        }
    }
}