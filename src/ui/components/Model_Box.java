package ui.components;

import java.time.LocalDate;
import java.time.LocalTime;

//redo later, retrieve data from Car, Resevation, Slot, Parking Space etc.

public class Model_Box {
    private int vehicleID;
    private String reservationStatus;
    private LocalDate reservationStartDate;
    private LocalTime reservationStartTime;
    private LocalDate reservationEndDate;
    private LocalTime reservationEndTime;
    private int parkingLotID;
    private float pricePerHour;
    private String parkingLotLocation;
    private int slotID;

    public int getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(int vehicleID) {
        this.vehicleID = vehicleID;
    }

    public String getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(String reservationDtatus) {
        this.reservationStatus = reservationDtatus;
    }

    public LocalDate getReservationStartDate() {
        return reservationStartDate;
    }

    public void setReservationStartDate(LocalDate reservationStartDate) {
        this.reservationStartDate = reservationStartDate;
    }

    public LocalTime getReservationStartTime() {
        return reservationStartTime;
    }

    public void setReservationStartTime(LocalTime reservationDtartTime) {
        this.reservationStartTime = reservationDtartTime;
    }

    public LocalDate getReservationEndDate() {
        return reservationEndDate;
    }

    public void setReservationEndDate(LocalDate reservationEndDate) {
        this.reservationEndDate = reservationEndDate;
    }

    public LocalTime getReservationEndTime() {
        return reservationEndTime;
    }

    public void setReservationEndTime(LocalTime reservationEndTime) {
        this.reservationEndTime = reservationEndTime;
    }

    public int getParkingLotID() {
        return parkingLotID;
    }

    public void setParkingLotID(int parkingLotID) {
        this.parkingLotID = parkingLotID;
    }

    public float getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(float pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public String getParkingLotLocation() {
        return parkingLotLocation;
    }

    public void setParkingLotLocation(String parkingLotLocation) {
        this.parkingLotLocation = parkingLotLocation;
    }

    public int getSlotID() {
        return slotID;
    }

    public void setSlotID(int slotID) {
        this.slotID = slotID;
    }

    public Model_Box(int vehicleID, String reservationStatus, LocalDate reservationStartDate, LocalTime reservationStartTime, LocalDate reservationEndDate, LocalTime reservationEndTime, int parkingLotID, float pricePerHour, String parkingLotLocation, int slotID) {
        this.vehicleID = vehicleID;
        this.reservationStatus = reservationStatus;
        this.reservationStartDate = reservationStartDate;
        this.reservationStartTime = reservationStartTime;
        this.reservationEndDate = reservationEndDate;
        this.reservationEndTime = reservationEndTime;
        this.parkingLotID = parkingLotID;
        this.pricePerHour = pricePerHour;
        this.parkingLotLocation = parkingLotLocation;
        this.slotID = slotID;
    }
    
    
    
    
}
