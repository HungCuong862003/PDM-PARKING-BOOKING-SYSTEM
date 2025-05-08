package main.java.com.parkeasy.model;

/**
 * Model class representing a financial transaction in the system
 */
public class Transaction {
    // Primary key
    private int transactionID;
    
    // Other attributes
    private float amount;
    
    // Foreign key
    private int reservationID;
    
    /**
     * Default constructor
     */
    public Transaction() {
    }
    
    /**
     * Constructor with all fields
     *
     * @param transactionID The transaction ID
     * @param amount The transaction amount
     * @param reservationID The associated reservation ID
     */
    public Transaction(int transactionID, float amount, int reservationID) {
        this.transactionID = transactionID;
        this.amount = amount;
        this.reservationID = reservationID;
    }
    
    /**
     * Get the transaction ID
     *
     * @return The transaction ID
     */
    public int getTransactionID() {
        return transactionID;
    }
    
    /**
     * Set the transaction ID
     *
     * @param transactionID The transaction ID to set
     */
    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }
    
    /**
     * Get the transaction amount
     *
     * @return The amount
     */
    public float getAmount() {
        return amount;
    }
    
    /**
     * Set the transaction amount
     *
     * @param amount The amount to set
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }
    
    /**
     * Get the reservation ID
     *
     * @return The reservation ID
     */
    public int getReservationID() {
        return reservationID;
    }
    
    /**
     * Set the reservation ID
     *
     * @param reservationID The reservation ID to set
     */
    public void setReservationID(int reservationID) {
        this.reservationID = reservationID;
    }
    
    /**
     * String representation of the Transaction object
     */
    @Override
    public String toString() {
        return "Transaction{" +
                "transactionID=" + transactionID +
                ", amount=" + amount +
                ", reservationID=" + reservationID +
                '}';
    }
}