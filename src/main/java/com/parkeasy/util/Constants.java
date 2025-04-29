package main.java.com.parkeasy.util;

/**
 * Constants used throughout the application
 */
public class Constants {

    // Database connection constants
    protected static final String URL = "jdbc:mysql://127.0.0.1:3306/parking_system"; // TODO: change to your database name
    protected static final String USERNAME = "root"; // TODO: change to your database username
    protected static final String PASSWORD = "HCuong@862003"; // TODO: change to your database password

    // Slot status constants
    public static final boolean SLOT_AVAILABLE = true; // Slot is available

    // Reservation status constants
    public static final String RESERVATION_PENDING = "PENDING";
    public static final String RESERVATION_PAID = "PAID";
    public static final String RESERVATION_ACTIVE = "ACTIVE";
    public static final String RESERVATION_COMPLETED = "COMPLETED";
    public static final String RESERVATION_CANCELLED = "CANCELLED";

    // Payment status constants
    public static final String PAYMENT_PENDING = "PENDING";
    public static final String PAYMENT_COMPLETED = "COMPLETED";
    public static final String PAYMENT_FAILED = "FAILED";
    public static final String PAYMENT_REFUNDED = "REFUNDED";
}