package main.java.com.parkeasy.model;

public class ParkingSpace {
    // primary key
    private String parkingID;
    // other attributes
    private String parkingAddress;
    private float costOfParking;
    private int numberOfSlots;
    private String description;
    // foreign key
    private int adminID;

    /**
     * Default constructor
     */
    public ParkingSpace() {
        // Initialize with default values if needed
    }

    /**
     * Constructor with all fields
     *
     * @param parkingID Unique identifier for the parking space
     * @param parkingAddress Address or location of the parking space
     * @param costOfParking Cost associated with this parking
     * @param numberOfSlots Total number of parking slots available
     * @param description Additional information about the parking space
     * @param adminID Identifier for the admin who manages this parking space
     */
    public ParkingSpace(String parkingID, String parkingAddress, float costOfParking, int numberOfSlots
            , String description, int adminID) {
        this.parkingID = parkingID;
        this.parkingAddress = parkingAddress;
        this.costOfParking = costOfParking;
        this.numberOfSlots = numberOfSlots;
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

    public float getCostOfParking() {
        return costOfParking;
    }

    public void setCostOfParking(float costOfParking) {
        this.costOfParking = costOfParking;
    }

    public int getNumberOfSlots() {
        return numberOfSlots;
    }

    public void setNumberOfSlots(int numberOfSlots) {
        this.numberOfSlots = numberOfSlots;
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

    @Override
    public String toString() {
        return "ParkingSpace{" +
                "parkingID='" + parkingID + '\'' +
                ", parkingAddress='" + parkingAddress + '\'' +
                ", costOfParking=" + costOfParking +
                ", numberOfSlots=" + numberOfSlots +
                ", description='" + description + '\'' +
                ", adminID=" + adminID +
                '}';
    }
}