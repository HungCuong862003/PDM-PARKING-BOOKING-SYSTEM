package main.java.com.parkeasy.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Model class representing a parking reservation in the ParkEasy system.
 * Includes reservation details, time information, and cost calculation.
 */
public class Reservation {
    // primary key
    private int reservationID;
    // other attributes
    private Date startDate;
    private Date endDate;
    private Time startTime;
    private Time endTime;
    private Timestamp createdAt;
    private String status;
    // foreign keys
    private String vehicleID;
    private int slotID;
    private int userID; // Added userID as it's referenced in ReservationService

    /**
     * Default constructor
     */
    public Reservation() {
        // Default constructor
    }

    /**
     * Full constructor for Reservation
     */
    public Reservation(int reservationID, Date startDate, Date endDate, Time startTime, Time endTime,
                       Timestamp createdAt, String status, String vehicleID, int slotID, int userID) {
        this.reservationID = reservationID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdAt = createdAt;
        this.status = status;
        this.vehicleID = vehicleID;
        this.slotID = slotID;
        this.userID = userID;
    }

    /**
     * Gets the reservation ID
     * @return The reservation ID
     */
    public int getReservationID() {
        return reservationID;
    }

    /**
     * Sets the reservation ID
     * @param reservationID The reservation ID to set
     */
    public void setReservationID(int reservationID) {
        this.reservationID = reservationID;
    }

    /**
     * Gets the start date
     * @return The start date as a String in format YYYY-MM-DD
     */
    public String getStartDate() {
        return startDate != null ? startDate.toString() : null;
    }

    /**
     * Sets the start date
     * @param startDate The start date to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the end date
     * @return The end date as a String in format YYYY-MM-DD
     */
    public String getEndDate() {
        return endDate != null ? endDate.toString() : null;
    }

    /**
     * Sets the end date
     * @param endDate The end date to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Gets the start time
     * @return The start time as a String in format HH:MM
     */
    public String getStartTime() {
        return startTime != null ? startTime.toString().substring(0, 5) : null;
    }

    /**
     * Sets the start time
     * @param startTime The start time to set
     */
    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the end time
     * @return The end time as a String in format HH:MM
     */
    public String getEndTime() {
        return endTime != null ? endTime.toString().substring(0, 5) : null;
    }

    /**
     * Sets the end time
     * @param endTime The end time to set
     */
    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    /**
     * Gets the creation timestamp
     * @return The creation timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp
     * @param createdAt The creation timestamp to set
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the reservation status
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the reservation status
     * @param status The status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the vehicle ID
     * @return The vehicle ID
     */
    public String getVehicleID() {
        return vehicleID;
    }

    /**
     * Sets the vehicle ID
     * @param vehicleID The vehicle ID to set
     */
    public void setVehicleID(String vehicleID) {
        this.vehicleID = vehicleID;
    }

    /**
     * Gets the slot ID
     * @return The slot ID
     */
    public int getSlotID() {
        return slotID;
    }

    /**
     * Sets the slot ID
     * @param slotID The slot ID to set
     */
    public void setSlotID(int slotID) {
        this.slotID = slotID;
    }

    /**
     * Gets the user ID
     * @return The user ID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Sets the user ID
     * @param userID The user ID to set
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * Calculates the duration of the reservation in minutes
     * @return The duration in minutes
     */
    public long getDurationInMinutes() {
        if (startDate == null || endDate == null || startTime == null || endTime == null) {
            return 0;
        }

        // Calculate duration from start and end dates/times
        long startMillis = startTime.getTime() + startDate.getTime();
        long endMillis = endTime.getTime() + endDate.getTime();
        return (endMillis - startMillis) / (60 * 1000);
    }

    /**
     * Calculates the cost of the reservation based on an hourly rate
     * @param hourlyRate The hourly rate to use for calculation
     * @return The calculated cost
     */
    public BigDecimal calculateCost(BigDecimal hourlyRate) {
        if (hourlyRate == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal durationHours = new BigDecimal(getDurationInMinutes()).divide(
                new BigDecimal(60), 2, RoundingMode.HALF_UP);
        return hourlyRate.multiply(durationHours);
    }

    /**
     * Returns a string representation of the reservation
     * @return A string with reservation details
     */
    @Override
    public String toString() {
        return "Reservation{" +
                "reservationID=" + reservationID +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status='" + status + '\'' +
                ", vehicleID='" + vehicleID + '\'' +
                ", slotID=" + slotID +
                ", userID=" + userID +
                '}';
    }
}