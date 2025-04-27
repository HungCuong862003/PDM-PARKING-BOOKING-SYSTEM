package main.java.com.parkeasy.model;

import java.util.Objects;

/**
 * Represents a parking slot within a parking space.
 * Each parking slot has a unique ID, number, availability status,
 * and belongs to a specific parking space.
 */
public class ParkingSlot {

    private ParkingSpace parkingSpace; // Reference to the actual object


    // Primary key
    private int slotID;

    // Other attributes
    private String slotNumber;
    private boolean availability;

    // Foreign key
    private String parkingID;

    /**
     * Parameterized constructor to create a new ParkingSlot with all required fields.
     *
     * @param slotID      Unique identifier for the slot
     * @param slotNumber  The slot number or identifier
     * @param availability Whether the slot is available (true) or occupied (false)
     * @param parkingID   The ID of the parking space this slot belongs to
     */
    public ParkingSlot(int slotID, String slotNumber, boolean availability, String parkingID) {
        this.slotID = slotID;
        this.slotNumber = slotNumber;
        this.availability = availability;
        this.parkingID = parkingID;
    }

    public void setParkingSpace(ParkingSpace parkingSpace) {
        this.parkingSpace = parkingSpace;
        this.parkingID = parkingSpace.getParkingID();
    }
    /**
     * Default constructor for ORM frameworks.
     */
    public ParkingSlot() {
        // Default constructor
    }

    /**
     * Gets the slot ID.
     *
     * @return The slot's unique identifier
     */
    public int getSlotID() {
        return slotID;
    }

    /**
     * Sets the slot ID.
     *
     * @param slotID The slot's unique identifier
     */
    public void setSlotID(int slotID) {
        this.slotID = slotID;
    }

    /**
     * Gets the slot number or identifier.
     *
     * @return The slot number
     */
    public String getSlotNumber() {
        return slotNumber;
    }

    /**
     * Sets the slot number or identifier.
     *
     * @param slotNumber The slot number
     */
    public void setSlotNumber(String slotNumber) {
        this.slotNumber = slotNumber;
    }

    /**
     * Checks if the slot is available.
     *
     * @return true if the slot is available, false if occupied
     */
    public boolean isAvailability() {
        return availability;
    }

    /**
     * Sets the availability status of the slot.
     *
     * @param availability true if the slot is available, false if occupied
     */
    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    /**
     * Gets the ID of the parking space this slot belongs to.
     *
     * @return The parking space ID
     */
    public String getParkingID() {
        return parkingID;
    }

    /**
     * Sets the ID of the parking space this slot belongs to.
     *
     * @param parkingID The parking space ID
     */
    public void setParkingID(String parkingID) {
        this.parkingID = parkingID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParkingSlot that = (ParkingSlot) o;
        return slotID == that.slotID &&
                availability == that.availability &&
                Objects.equals(slotNumber, that.slotNumber) &&
                Objects.equals(parkingID, that.parkingID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slotID, slotNumber, availability, parkingID);
    }

    @Override
    public String toString() {
        return "ParkingSlot{" +
                "slotID=" + slotID +
                ", slotNumber='" + slotNumber + '\'' +
                ", availability=" + availability +
                ", parkingID='" + parkingID + '\'' +
                '}';
    }
}