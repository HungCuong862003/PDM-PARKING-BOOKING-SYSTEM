package main.java.com.parkeasy.model;

import java.sql.Time;

public class ParkingSchedule {
    // primary key
    private int scheduleID;
    // other attributes
    private int dayOfWeek;
    private Time openingTime;
    private Time closingTime;
    // foreign key
    private String parkingID;

    public ParkingSchedule(int scheduleID, int dayOfWeek, Time openingTime, Time closingTime, String parkingID) {
        this.scheduleID = scheduleID;
        this.dayOfWeek = dayOfWeek;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.parkingID = parkingID;
    }

    public int getScheduleID() {
        return scheduleID;
    }

    public void setScheduleID(int scheduleID) {
        this.scheduleID = scheduleID;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Time getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(Time openingTime) {
        this.openingTime = openingTime;
    }

    public Time getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(Time closingTime) {
        this.closingTime = closingTime;
    }

    public String getParkingID() {
        return parkingID;
    }

    public void setParkingID(String parkingID) {
        this.parkingID = parkingID;
    }
}