package main.java.com.parkeasy.model;

/**
 * Vehicle model class
 * Represents a vehicle in the system with the fields defined in the database schema
 */
public class Vehicle {
    private String vehicleID;
    private int userID;

    /**
     * Default constructor
     */
    public Vehicle() {
    }

    /**
     * Parameterized constructor
     *
     * @param vehicleID The unique identifier for the vehicle
     * @param userID The ID of the user who owns this vehicle
     */
    public Vehicle(String vehicleID, int userID) {
        this.vehicleID = vehicleID;
        this.userID = userID;
    }

    /**
     * Get the vehicle ID
     *
     * @return The vehicle ID
     */
    public String getVehicleID() {
        return vehicleID;
    }

    /**
     * Set the vehicle ID
     *
     * @param vehicleID The vehicle ID to set
     */
    public void setVehicleID(String vehicleID) {
        this.vehicleID = vehicleID;
    }

    /**
     * Get the user ID
     *
     * @return The user ID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Set the user ID
     *
     * @param userID The user ID to set
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleID='" + vehicleID + '\'' +
                ", userID=" + userID +
                '}';
    }
}