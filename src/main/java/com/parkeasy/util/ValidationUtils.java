package main.java.com.parkeasy.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Utility class for validating user input
 */
public class ValidationUtils {

    // Regular expression for validating email format
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    // Regular expression for validating phone number format
    // This pattern allows various formats like:
    // 1234567890, 123-456-7890, (123) 456-7890, 123.456.7890
    private static final String PHONE_PATTERN =
            "^\\(?(\\d{3})\\)?[-.\\s]?(\\d{3})[-.\\s]?(\\d{4})$";

    private static final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
    private static final Pattern phonePattern = Pattern.compile(PHONE_PATTERN);

    /**
     * Private constructor to prevent instantiation
     */
    private ValidationUtils() {
        // Utility class should not be instantiated
    }

    /**
     * Validates an email address format
     *
     * @param email The email address to validate
     * @return true if the email format is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }

        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Validates a phone number format
     *
     * @param phone The phone number to validate
     * @return true if the phone format is valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }

        Matcher matcher = phonePattern.matcher(phone);
        return matcher.matches();
    }

    /**
     * Validates if a string is not null or empty
     *
     * @param text The string to check
     * @return true if the string is not null or empty, false otherwise
     */
    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    /**
     * Validates a password strength
     * Checks if the password:
     * 1. Is at least 8 characters long
     * 2. Contains at least one digit
     * 3. Contains at least one lowercase letter
     * 4. Contains at least one uppercase letter
     *
     * @param password The password to validate
     * @return true if the password meets the requirements, false otherwise
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasDigit = false;
        boolean hasLower = false;
        boolean hasUpper = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isUpperCase(c)) {
                hasUpper = true;
            }
        }

        return hasDigit && hasLower && hasUpper;
    }

    /**
     * Validates that a value is within a specified range
     *
     * @param value The value to check
     * @param min The minimum allowed value
     * @param max The maximum allowed value
     * @return true if the value is within the range, false otherwise
     */
    public static boolean isInRange(float value, float min, float max) {
        return value >= min && value <= max;
    }

    /**
     * Validates that a number is positive
     *
     * @param value The number to check
     * @return true if the number is positive, false otherwise
     */
    public static boolean isPositive(float value) {
        return value > 0;
    }

    /**
     * Validates that a number is not negative
     *
     * @param value The number to check
     * @return true if the number is not negative, false otherwise
     */
    public static boolean isNotNegative(float value) {
        return value >= 0;
    }
    /**
     * Validates a vehicle ID (license plate) format
     * Assumes a standard license plate format with:
     * - 1-7 alphanumeric characters
     * - No special characters except hyphens or spaces
     *
     * @param vehicleId The vehicle ID (license plate) to validate
     * @return true if the vehicle ID format is valid, false otherwise
     */
    public static boolean isValidVehicleId(String vehicleId) {
        if (vehicleId == null) {
            return false;
        }

        // Trim the input to remove any leading/trailing spaces
        String trimmedId = vehicleId.trim();

        // Check if the vehicle ID is empty after trimming
        if (trimmedId.isEmpty()) {
            return false;
        }

        // Check if the vehicle ID is too long or too short
        // Most license plates are between 1-7 characters
        if (trimmedId.length() < 1 || trimmedId.length() > 10) {
            return false;
        }

        // Regular expression for license plate validation
        // Allows letters, numbers, hyphens, and spaces
        // The pattern should be adjusted based on specific requirements for different regions
        String regex = "^[A-Za-z0-9\\- ]+$";

        return trimmedId.matches(regex);
    }
}