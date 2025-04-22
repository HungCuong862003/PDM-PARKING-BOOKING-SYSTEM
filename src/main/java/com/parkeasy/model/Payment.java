package main.java.com.parkeasy.model;

import java.sql.Date;

public class Payment {
    // primary key
    private int paymentID;
    // other attributes
    private String paymentMethod;
    private float amount;
    private Date paymentDate;
    // foreign key
    private int reservationID;
    private String cardNumber;

    public Payment(int paymentID, String paymentMethod, float amount, Date paymentDate, int reservationID,
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

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
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