package main.java.com.parkeasy.model;

public class ParkingSlot {

    // primary key is now slotNumber
    private String slotNumber;
    private boolean availability;
    // foreign key
    private String parkingID;

    /**
     * Constructor with all fields
     *
     * @param slotNumber The slot number (primary key)
     * @param availability Whether the slot is available
     * @param parkingID The ID of the parking space this slot belongs to
     */
    public ParkingSlot(String slotNumber, boolean availability, String parkingID) {
        this.slotNumber = slotNumber;
        this.availability = availability;
        this.parkingID = parkingID;
    }

    public ParkingSlot() {
        // Default constructor
    }

    public String getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(String slotNumber) {
        this.slotNumber = slotNumber;
    }

    public boolean getAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public String getParkingID() {
        return parkingID;
    }

    public void setParkingID(String parkingID) {
        this.parkingID = parkingID;
    }

    @Override
    public String toString() {
        return "ParkingSlot{" +
                "slotNumber='" + slotNumber + '\'' +
                ", availability=" + availability +
                ", parkingID='" + parkingID + '\'' +
                '}';
    }
}