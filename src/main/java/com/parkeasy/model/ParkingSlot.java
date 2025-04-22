package main.java.com.parkeasy.model;

public class ParkingSlot {

    // primary key
    private int slotID;
    // other attributes
    private String slotNumber;
    private boolean availability;
    // foreign key
    private int parkingID;

    public ParkingSlot(int slotID, String slotNumber, boolean availability, int parkingID) {
        this.slotID = slotID;
        this.slotNumber = slotNumber;
        this.availability = availability;
        this.parkingID = parkingID;
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

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public int getParkingID() {
        return parkingID;
    }

    public void setParkingID(int parkingID) {
        this.parkingID = parkingID;
    }

}