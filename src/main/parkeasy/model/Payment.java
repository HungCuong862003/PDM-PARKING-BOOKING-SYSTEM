package main.java.com.parkeasy.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Payment {
    // primary key
    private int paymentID;
    // other attributes
    private String paymentMethod;
    private BigDecimal amount; // Changed from float to BigDecimal
    private Timestamp paymentDate;
    // foreign key
    private int reservationID;
    private String cardNumber;

    /**
     * Default constructor
     */
    public Payment() {
    }

    /**
     * Constructor with all fields
     */
    public Payment(int paymentID, String paymentMethod, float amount, Timestamp paymentDate, int reservationID,
                   String cardNumber) {
        this.paymentID = paymentID;
        this.paymentMethod = paymentMethod;
        this.amount = new BigDecimal(Float.toString(amount)); // Convert float to BigDecimal
        this.paymentDate = paymentDate;
        this.reservationID = reservationID;
        this.cardNumber = cardNumber;
    }

    /**
     * Alternative constructor with BigDecimal amount
     */
    public Payment(int paymentID, String paymentMethod, BigDecimal amount, Timestamp paymentDate, int reservationID,
                   String cardNumber) {
        this.paymentID = paymentID;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.reservationID = reservationID;
        this.cardNumber = cardNumber;
    }

    public int getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(int paymentID) {
        this.paymentID = paymentID;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Alternative setter for float amounts
     */
    public void setAmount(float amount) {
        this.amount = new BigDecimal(Float.toString(amount));
    }

    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }

    public int getReservationID() {
        return reservationID;
    }

    public void setReservationID(int reservationID) {
        this.reservationID = reservationID;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}