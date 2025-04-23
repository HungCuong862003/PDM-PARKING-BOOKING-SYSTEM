package main.java.com.parkeasy.model;

public class ParkingSlot {

    // primary key
    private int slotID;
    // other attributes
    private String slotNumber;
    private boolean availability;
    // foreign key
    private String parkingID;

    public ParkingSlot(int slotID, String slotNumber, boolean availability, String parkingID) {
        this.slotID = slotID;
        this.slotNumber = slotNumber;
        this.availability = availability;
        this.parkingID = parkingID;
    }

    public ParkingSlot() {
        // Default constructor
    }

    public int getSlotID() {
        return slotID;
    }

    public void setSlotID(int slotID) {
        this.slotID = slotID;
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

}