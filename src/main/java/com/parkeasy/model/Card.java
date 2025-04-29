package main.java.com.parkeasy.model;

import java.util.Date;

/**
 * Model class representing a payment card in the system
 */
public class Card {
    private String cardNumber;
    private Date validTo;
    private String cardHolder;
    private int userID;

    /**
     * Default constructor
     */
    public Card() {
    }

    /**
     * Constructor with all fields
     *
     * @param cardNumber The card number (primary key)
     * @param validTo    The expiration date of the card
     * @param cardHolder The name of the card holder
     * @param userID     The ID of the user who owns this card
     */
    public Card(String cardNumber, Date validTo, String cardHolder, int userID) {
        this.cardNumber = cardNumber;
        this.validTo = validTo;
        this.cardHolder = cardHolder;
        this.userID = userID;
    }

    /**
     * Get the card number
     *
     * @return The card number
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Set the card number
     *
     * @param cardNumber The card number to set
     */
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * Get the expiration date
     *
     * @return The expiration date
     */
    public Date getValidTo() {
        return validTo;
    }

    /**
     * Set the expiration date
     *
     * @param validTo The expiration date to set
     */
    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    /**
     * Get the card holder name
     *
     * @return The card holder name
     */
    public String getCardHolder() {
        return cardHolder;
    }

    /**
     * Set the card holder name
     *
     * @param cardHolder The card holder name to set
     */
    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    /**
     * Get the user ID
     *
     * @return The user ID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Set the user ID
     *
     * @param userID The user ID to set
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return "Card{" +
                "cardNumber='" + cardNumber + '\'' +
                ", validTo=" + validTo +
                ", cardHolder='" + cardHolder + '\'' +
                ", userID=" + userID +
                '}';
    }
}