package main.java.com.parkeasy.model;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

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
    private String slotNumber;

    /**
     * Default no-argument constructor
     */
    public Reservation() {
        // Default constructor
    }

    /**
     *
     * @param reservationID The ID of the reservation
     * @param startDate The start date
     * @param endDate The end date
     * @param startTime The start time
     * @param endTime The end time
     * @param createdAt The creation timestamp
     * @param status The status of the reservation
     * @param vehicleID The ID of the vehicle
     */
    @Deprecated
    public Reservation(int reservationID, Date startDate, Date endDate, Time startTime, Time endTime,
                       Timestamp createdAt, String status, String vehicleID, String slotNumber) {
        this.reservationID = reservationID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdAt = createdAt;
        this.status = status;
        this.vehicleID = vehicleID;
        this.slotNumber = slotNumber;
    }

    public int getReservationID() {
        return reservationID;
    }

    public void setReservationID(int reservationID) {
        this.reservationID = reservationID;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(String vehicleID) {
        this.vehicleID = vehicleID;
    }

    /**
     * Get the parking slot number
     *
     * @return The slot number
     */
    public String getSlotNumber() {
        return slotNumber;
    }

    /**
     * Set the parking slot number
     *
     * @param slotNumber The slot number to set
     */
    public void setSlotNumber(String slotNumber) {
        this.slotNumber = slotNumber;
    }



    @Override
    public String toString() {
        return "Reservation{" +
                "reservationID=" + reservationID +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", createdAt=" + createdAt +
                ", status='" + status + '\'' +
                ", vehicleID='" + vehicleID + '\'' +
                ", slotNumber='" + slotNumber + '\'' +
                '}';
    }
}