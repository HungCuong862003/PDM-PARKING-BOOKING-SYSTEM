package main.java.com.parkeasy.util;

/**
 * Constants used throughout the application
 */
public class Constants {
    // Database connection constants
    protected static final String URL = "jdbc:mysql://127.0.0.1:3306/parking_system";
    protected static final String USERNAME = "root";
    protected static final String PASSWORD = "HCuong@862003";

    // Slot status constants
    public static final boolean SLOT_AVAILABLE = true; // Slot is available

    // Reservation status constants
    public static final String RESERVATION_IN_PROCESS = "Processing";
    public static final String RESERVATION_COMPLETE = "Completed";
    public static final String RESERVATION_PAID = "Paid";

    // Payment status constants
    public static final String PAYMENT_COMPLETED = "COMPLETED";
    public static final String PAYMENT_FAILED = "FAILED";
}