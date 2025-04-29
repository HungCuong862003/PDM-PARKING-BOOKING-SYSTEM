package main.java.com.parkeasy.model;

import java.sql.Timestamp;

public class Payment {
    // primary key
    private int paymentID;
    // other attributes
    private String paymentMethod;
    private double amount; // Changed from float to double to match service implementation
    private Timestamp paymentDate;
    // foreign keys
    private int reservationID;
    private String cardNumber;
    // additional attributes used in the service
    private String status;
    private String transactionID;

    /**
     * Default no-argument constructor
     */
    public Payment() {
        // Default constructor with no arguments
    }

    /**
     * Constructor with all fields except status and transactionID
     */
    public Payment(int paymentID, String paymentMethod, float amount, Timestamp paymentDate, int reservationID,
                   String cardNumber) {
        this.paymentID = paymentID;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.reservationID = reservationID;
        this.cardNumber = cardNumber;
    }

    /**
     * Constructor with all fields including status and transactionID
     */
    public Payment(int paymentID, String paymentMethod, double amount, Timestamp paymentDate, int reservationID,
                   String cardNumber, String status, String transactionID) {
        this.paymentID = paymentID;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.reservationID = reservationID;
        this.cardNumber = cardNumber;
        this.status = status;
        this.transactionID = transactionID;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentID=" + paymentID +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                ", reservationID=" + reservationID +
                ", cardNumber='" + cardNumber + '\'' +
                ", status='" + status + '\'' +
                ", transactionID='" + transactionID + '\'' +
                '}';
    }
}