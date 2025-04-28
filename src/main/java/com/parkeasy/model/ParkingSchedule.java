package main.java.com.parkeasy.model;

import java.sql.Time;
import java.util.Objects;

/**
 * Represents a schedule for a parking space, defining when the parking space
 * is open and closed on a specific day of the week.
 * Each schedule has a unique ID, day of the week, opening and closing times,
 * and is associated with a specific parking space.
 */
public class ParkingSchedule {
    // primary key
    private int scheduleID;
    // other attributes
    private int dayOfWeek;
    private Time openingTime;
    private Time closingTime;
    // foreign key
    private String parkingID;

    /**
     * Default constructor for ORM frameworks.
     */
    public ParkingSchedule() {
        // Default constructor
    }

    /**
     * Parameterized constructor to create a new ParkingSchedule with all required fields.
     *
     * @param scheduleID Unique identifier for the schedule
     * @param dayOfWeek The day of the week (1-7, where 1 is Sunday)
     * @param openingTime The opening time for the parking space on this day
     * @param closingTime The closing time for the parking space on this day
     * @param parkingID The ID of the parking space this schedule belongs to
     */
    public ParkingSchedule(int scheduleID, int dayOfWeek, Time openingTime, Time closingTime, String parkingID) {
        this.scheduleID = scheduleID;
        setDayOfWeek(dayOfWeek);
        setOpeningTime(openingTime);
        setClosingTime(closingTime);
        setParkingID(parkingID);
    }

    /**
     * Gets the schedule ID.
     *
     * @return The schedule's unique identifier
     */
    public int getScheduleID() {
        return scheduleID;
    }

    /**
     * Sets the schedule ID.
     *
     * @param scheduleID The schedule's unique identifier
     */
    public void setScheduleID(int scheduleID) {
        this.scheduleID = scheduleID;
    }

    /**
     * Gets the day of the week.
     *
     * @return The day of the week (1-7, where 1 is Sunday)
     */
    public int getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * Sets the day of the week, with validation.
     *
     * @param dayOfWeek The day of the week (1-7, where 1 is Sunday)
     * @throws IllegalArgumentException if the day of week is not between 1 and 7
     */
    public void setDayOfWeek(int dayOfWeek) {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new IllegalArgumentException("Day of week must be between 1 and 7 (1: Sunday, 7: Saturday)");
        }
        this.dayOfWeek = dayOfWeek;
    }

    /**
     * Gets the opening time.
     *
     * @return The opening time for the parking space on this day
     */
    public Time getOpeningTime() {
        return openingTime;
    }

    /**
     * Sets the opening time, with validation.
     *
     * @param openingTime The opening time for the parking space on this day
     * @throws IllegalArgumentException if openingTime is null
     */
    public void setOpeningTime(Time openingTime) {
        if (openingTime == null) {
            throw new IllegalArgumentException("Opening time cannot be null");
        }
        this.openingTime = openingTime;
    }

    /**
     * Gets the closing time.
     *
     * @return The closing time for the parking space on this day
     */
    public Time getClosingTime() {
        return closingTime;
    }

    /**
     * Sets the closing time, with validation.
     *
     * @param closingTime The closing time for the parking space on this day
     * @throws IllegalArgumentException if closingTime is null or before openingTime
     */
    public void setClosingTime(Time closingTime) {
        if (closingTime == null) {
            throw new IllegalArgumentException("Closing time cannot be null");
        }
        if (this.openingTime != null && closingTime.before(this.openingTime)) {
            throw new IllegalArgumentException("Closing time cannot be before opening time");
        }
        this.closingTime = closingTime;
    }

    /**
     * Gets the ID of the parking space this schedule belongs to.
     *
     * @return The parking space ID
     */
    public String getParkingID() {
        return parkingID;
    }

    /**
     * Sets the ID of the parking space this schedule belongs to.
     *
     * @param parkingID The parking space ID
     * @throws IllegalArgumentException if parkingID is null or empty
     */
    public void setParkingID(String parkingID) {
        if (parkingID == null || parkingID.trim().isEmpty()) {
            throw new IllegalArgumentException("Parking ID cannot be null or empty");
        }
        this.parkingID = parkingID;
    }

    /**
     * Checks if the parking space is open at the specified time on this day.
     *
     * @param time The time to check
     * @return true if the parking space is open, false otherwise
     */
    public boolean isOpenAt(Time time) {
        return !time.before(openingTime) && !time.after(closingTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParkingSchedule that = (ParkingSchedule) o;
        return scheduleID == that.scheduleID &&
                dayOfWeek == that.dayOfWeek &&
                Objects.equals(parkingID, that.parkingID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheduleID, dayOfWeek, parkingID);
    }

    @Override
    public String toString() {
        return "ParkingSchedule{" +
                "scheduleID=" + scheduleID +
                ", dayOfWeek=" + dayOfWeek +
                ", openingTime=" + openingTime +
                ", closingTime=" + closingTime +
                ", parkingID='" + parkingID + '\'' +
                '}';
    }
}