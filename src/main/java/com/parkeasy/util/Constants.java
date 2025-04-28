package main.java.com.parkeasy.util;

/**
 * A utility class that contains all the constant values used throughout the ParkEasy application.
 * This class cannot be instantiated and all fields are static and final.
 */
public final class Constants {

    // Private constructor to prevent instantiation
    private Constants() {
        throw new AssertionError("Constants class should not be instantiated");
    }

    // User Constants
    public static final int MAX_VEHICLES_PER_USER = 3;

    // Application Constants
    public static final String APPLICATION_NAME = "ParkEasy";
    public static final String APPLICATION_VERSION = "1.0.0";

    // Database Constants
    public static final String DB_NAME = "parkeasy_db";
    public static final String DB_HOST = "localhost";
    public static final int DB_PORT = 3306;
    public static final String DB_USERNAME = "parkeasy_user";

    // Time Constants
    public static final int DEFAULT_MAX_PARKING_DURATION_HOURS = 24;
    public static final int MIN_RESERVATION_DURATION_MINUTES = 30;
    public static final int MAX_ADVANCE_BOOKING_DAYS = 30;
    public static final int DEFAULT_SESSION_TIMEOUT_MINUTES = 30;

    // Reservation Status
    public static final String RESERVATION_STATUS_PENDING = "PENDING";
    public static final String RESERVATION_STATUS_CONFIRMED = "CONFIRMED";
    public static final String RESERVATION_STATUS_ACTIVE = "ACTIVE";
    public static final String RESERVATION_STATUS_COMPLETED = "COMPLETED";
    public static final String RESERVATION_STATUS_CANCELLED = "CANCELLED";

    // Payment Methods
    public static final String PAYMENT_METHOD_CREDIT_CARD = "CREDIT_CARD";
    public static final String PAYMENT_METHOD_DEBIT_CARD = "DEBIT_CARD";
    public static final String PAYMENT_METHOD_WALLET = "WALLET";
    public static final String PAYMENT_METHOD_NET_BANKING = "NET_BANKING";

    // File Paths
    public static final String LOGO_PATH = "/images/logo.png";
    public static final String ICON_PATH = "/images/icons/";
    public static final String MARKER_PATH = "/images/markers/";

    // View Constants
    public static final double DEFAULT_SPACING = 10.0;
    public static final double DEFAULT_PADDING = 15.0;
    public static final String PRIMARY_STYLE_CLASS = "primary";
    public static final String SECONDARY_STYLE_CLASS = "secondary";
    public static final String SUCCESS_STYLE_CLASS = "success";
    public static final String WARNING_STYLE_CLASS = "warning";
    public static final String DANGER_STYLE_CLASS = "danger";
    public static final String INFO_STYLE_CLASS = "info";

    // Notification Types
    public static final String NOTIFICATION_TYPE_INFO = "INFO";
    public static final String NOTIFICATION_TYPE_SUCCESS = "SUCCESS";
    public static final String NOTIFICATION_TYPE_WARNING = "WARNING";
    public static final String NOTIFICATION_TYPE_ERROR = "ERROR";

    // Email Constants
    public static final String EMAIL_SUPPORT = "support@parkeasy.com";
    public static final String EMAIL_NO_REPLY = "no-reply@parkeasy.com";

    // Day of Week Constants (matching with database schema)
    public static final int SUNDAY = 1;
    public static final int MONDAY = 2;
    public static final int TUESDAY = 3;
    public static final int WEDNESDAY = 4;
    public static final int THURSDAY = 5;
    public static final int FRIDAY = 6;
    public static final int SATURDAY = 7;

    // Regex Patterns
    public static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    public static final String PHONE_REGEX = "^\\+?[0-9]{10,15}$";
    public static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
    public static final String VEHICLE_NUMBER_REGEX = "^[A-Z0-9]{2,10}$";
    public static final String CARD_NUMBER_REGEX = "^[0-9]{16}$";

    // Default values
    public static final double DEFAULT_USER_BALANCE = 0.0;
    public static final int DEFAULT_RATING = 0;

    // Error Messages
    public static final String ERROR_INVALID_LOGIN = "Invalid username or password.";
    public static final String ERROR_USER_EXISTS = "User already exists with this email/phone.";
    public static final String ERROR_RESERVATION_OVERLAP = "This slot is already reserved for the selected time.";
    public static final String ERROR_INSUFFICIENT_BALANCE = "Insufficient balance for this reservation.";
    public static final String ERROR_OUTSIDE_OPERATING_HOURS = "The selected time is outside operating hours.";
    public static final String ERROR_DATABASE_CONNECTION = "Could not connect to database. Please try again later.";

    // Success Messages
    public static final String SUCCESS_RESERVATION = "Parking slot reserved successfully!";
    public static final String SUCCESS_PAYMENT = "Payment completed successfully!";
    public static final String SUCCESS_REGISTRATION = "Registration completed successfully!";
    public static final String SUCCESS_PASSWORD_RESET = "Password has been reset successfully!";
}