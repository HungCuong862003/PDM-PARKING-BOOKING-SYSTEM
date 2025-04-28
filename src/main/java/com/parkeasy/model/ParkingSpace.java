package main.java.com.parkeasy.model;

import java.math.BigDecimal;

public class ParkingSpace {
    // primary key
    private String parkingID;
    // other attributes
    private String parkingAddress;
    private BigDecimal costOfParking;
    private int numberOfSlots;
    private int maxDuration;
    private String description;
    // foreign key
    private int adminID;

    public ParkingSpace(String parkingID, String parkingAddress, float costOfParking, int numberOfSlots,
            int maxDuration,
            String description, int adminID) {
        this.parkingID = parkingID;
        this.parkingAddress = parkingAddress;
        this.costOfParking = BigDecimal.valueOf(costOfParking);
        this.numberOfSlots = numberOfSlots;
        this.maxDuration = maxDuration;
        this.description = description;
        this.adminID = adminID;
    }

    public String getParkingID() {
        return parkingID;
    }


    public void setParkingID(String parkingID) {
        this.parkingID = parkingID;
    }

    public String getParkingAddress() {
        return parkingAddress;
    }

    public void setParkingAddress(String parkingAddress) {
        this.parkingAddress = parkingAddress;
    }

    public BigDecimal getCostOfParking() {
        return costOfParking;
    }

    public void setCostOfParking(BigDecimal costOfParking) {
        this.costOfParking = costOfParking;
    }

    public int getNumberOfSlots() {
        return numberOfSlots;
    }

    public void setNumberOfSlots(int numberOfSlots) {
        this.numberOfSlots = numberOfSlots;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAdminID() {
        return adminID;
    }

    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }
}