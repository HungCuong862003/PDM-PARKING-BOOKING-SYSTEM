package main.java.com.parkeasy.model;

import java.sql.Date;

public class Card {
    // primary key
    private String cardNumber;
    // other attributes
    private Date validTo;
    private String cardHolderName;
    // foreign key
    private int userID;

    public Card(String cardNumber, Date validTo, String cardHolderName, int userID) {
        this.cardNumber = cardNumber;
        this.validTo = validTo;
        this.cardHolderName = cardHolderName;
        this.userID = userID;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}