package main.java.com.parkeasy.model;

public class Vehicle {
    // primary key
    private String vehicleID;
    // foreign key
    private int userID;

    public Vehicle(String vehicleID, int userID) {
        this.vehicleID = vehicleID;
        this.userID = userID;
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(String vehicleID) {
        this.vehicleID = vehicleID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
