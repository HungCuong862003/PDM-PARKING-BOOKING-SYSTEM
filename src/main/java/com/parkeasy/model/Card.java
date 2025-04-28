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

    /**
     * Default constructor
     */
    public Card() {
    }

    /**
     * Constructor with all fields
     *
     * @param cardNumber The card number (primary key)
     * @param validTo The expiration date (can be String or Date)
     * @param cardHolderName The name of the card holder
     * @param userID The user ID that owns this card
     */
    public Card(String cardNumber, String validTo, String cardHolderName, int userID) {
        this.cardNumber = cardNumber;
        this.validTo = Date.valueOf(validTo);
        this.cardHolderName = cardHolderName;
        this.userID = userID;
    }

    /**
     * Alternative constructor with Date for validTo
     *
     * @param cardNumber The card number (primary key)
     * @param validTo The expiration date as a Date object
     * @param cardHolderName The name of the card holder
     * @param userID The user ID that owns this card
     */
    public Card(String cardNumber, Date validTo, String cardHolderName, int userID) {
        this.cardNumber = cardNumber;
        this.validTo = validTo;
        this.cardHolderName = cardHolderName;
        this.userID = userID;
    }

    /**
     * Gets the card number
     *
     * @return The card number
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Sets the card number
     *
     * @param cardNumber The card number to set
     */
    public void setCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Card number cannot be null or empty");
        }
        // Basic validation for card number format
        if (!cardNumber.matches("^\\d{13,19}$")) {
            throw new IllegalArgumentException("Invalid card number format");
        }
        this.cardNumber = cardNumber;
    }

    /**
     * Gets the expiration date as a string (yyyy-MM-dd)
     *
     * @return The expiration date string
     */
    public String getValidTo() {
        return validTo.toString();
    }

    /**
     * Gets the expiration date as a Date object
     *
     * @return The expiration date
     */
    public Date getValidToAsDate() {
        return validTo;
    }

    /**
     * Sets the expiration date using a string (yyyy-MM-dd)
     *
     * @param validTo The expiration date string
     */
    public void setValidTo(String validTo) {
        this.validTo = Date.valueOf(validTo);
    }

    /**
     * Sets the expiration date using a Date object
     *
     * @param validTo The expiration date
     */
    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    /**
     * Gets the card holder name
     *
     * @return The card holder name
     */
    public String getCardHolder() {
        return cardHolderName;
    }

    /**
     * Sets the card holder name
     *
     * @param cardHolderName The card holder name to set
     */
    public void setCardHolder(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    /**
     * Gets the user ID
     *
     * @return The user ID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Sets the user ID
     *
     * @param userID The user ID to set
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * Returns a formatted string representation of the card details
     * with the card number partially masked for security
     *
     * @return Formatted card string
     */
    @Override
    public String toString() {
        String maskedNumber = maskCardNumber(cardNumber);
        return "Card {" +
                "number='" + maskedNumber + '\'' +
                ", expires='" + validTo + '\'' +
                ", holder='" + cardHolderName + '\'' +
                '}';
    }

    /**
     * Masks a card number for display (e.g., **** **** **** 1234)
     *
     * @param number The full card number
     * @return The masked card number
     */
    private String maskCardNumber(String number) {
        if (number == null || number.length() < 4) {
            return number;
        }
        String lastFour = number.substring(number.length() - 4);
        return "**** **** **** " + lastFour;
    }
}