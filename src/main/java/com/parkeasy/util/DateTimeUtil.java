package main.java.com.parkeasy.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import main.java.com.parkeasy.util.Constants;

/**
 * Utility class for handling date and time operations in the ParkEasy application.
 * Provides methods for formatting, parsing, and calculating durations between dates and times.
 */
public final class DateTimeUtil {

    // Private constructor to prevent instantiation
    private DateTimeUtil() {
        throw new AssertionError("DateTimeUtil class should not be instantiated");
    }

    // Common date and time format patterns
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String DATE_TIME_SECONDS_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DISPLAY_DATE_FORMAT = "MMM dd, yyyy";
    public static final String DISPLAY_TIME_FORMAT = "h:mm a";
    public static final String DISPLAY_DATE_TIME_FORMAT = "MMM dd, yyyy h:mm a";

    // Formatters
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    private static final DateTimeFormatter DATE_TIME_SECONDS_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_SECONDS_FORMAT);
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern(DISPLAY_DATE_FORMAT);
    private static final DateTimeFormatter DISPLAY_TIME_FORMATTER = DateTimeFormatter.ofPattern(DISPLAY_TIME_FORMAT);
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DISPLAY_DATE_TIME_FORMAT);

    /**
     * Gets the current date.
     *
     * @return Current date as LocalDate
     */
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    /**
     * Gets the current time.
     *
     * @return Current time as LocalTime
     */
    public static LocalTime getCurrentTime() {
        return LocalTime.now();
    }

    /**
     * Gets the current date and time.
     *
     * @return Current date and time as LocalDateTime
     */
    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }

    /**
     * Formats a LocalDate to string using the default date format.
     *
     * @param date The LocalDate to format
     * @return Formatted date string
     */
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    /**
     * Formats a LocalTime to string using the default time format.
     *
     * @param time The LocalTime to format
     * @return Formatted time string
     */
    public static String formatTime(LocalTime time) {
        return time != null ? time.format(TIME_FORMATTER) : "";
    }

    /**
     * Formats a LocalDateTime to string using the default date-time format.
     *
     * @param dateTime The LocalDateTime to format
     * @return Formatted date-time string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : "";
    }

    /**
     * Formats a LocalDate to display format for UI.
     *
     * @param date The LocalDate to format
     * @return Formatted date string for display
     */
    public static String formatDateForDisplay(LocalDate date) {
        return date != null ? date.format(DISPLAY_DATE_FORMATTER) : "";
    }

    /**
     * Formats a LocalTime to display format for UI.
     *
     * @param time The LocalTime to format
     * @return Formatted time string for display
     */
    public static String formatTimeForDisplay(LocalTime time) {
        return time != null ? time.format(DISPLAY_TIME_FORMATTER) : "";
    }

    /**
     * Formats a LocalDateTime to display format for UI.
     *
     * @param dateTime The LocalDateTime to format
     * @return Formatted date-time string for display
     */
    public static String formatDateTimeForDisplay(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DISPLAY_DATE_TIME_FORMATTER) : "";
    }

    /**
     * Parses a date string to LocalDate.
     *
     * @param dateStr The date string to parse
     * @return Parsed LocalDate or null if invalid format
     */
    public static LocalDate parseDate(String dateStr) {
        try {
            return dateStr != null && !dateStr.isEmpty() ? LocalDate.parse(dateStr, DATE_FORMATTER) : null;
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parses a time string to LocalTime.
     *
     * @param timeStr The time string to parse
     * @return Parsed LocalTime or null if invalid format
     */
    public static LocalTime parseTime(String timeStr) {
        try {
            return timeStr != null && !timeStr.isEmpty() ? LocalTime.parse(timeStr, TIME_FORMATTER) : null;
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parses a date-time string to LocalDateTime.
     *
     * @param dateTimeStr The date-time string to parse
     * @return Parsed LocalDateTime or null if invalid format
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return dateTimeStr != null && !dateTimeStr.isEmpty() ?
                    LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER) : null;
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Calculates the duration between two dates in days.
     *
     * @param startDate Start date
     * @param endDate End date
     * @return Duration in days
     */
    public static long calculateDurationInDays(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * Calculates the duration between two times in minutes.
     *
     * @param startTime Start time
     * @param endTime End time
     * @return Duration in minutes
     */
    public static long calculateDurationInMinutes(LocalTime startTime, LocalTime endTime) {
        return ChronoUnit.MINUTES.between(startTime, endTime);
    }

    /**
     * Calculates the duration between start and end date-times in hours.
     *
     * @param startDateTime Start date-time
     * @param endDateTime End date-time
     * @return Duration in hours
     */
    public static long calculateDurationInHours(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return ChronoUnit.HOURS.between(startDateTime, endDateTime);
    }

    /**
     * Calculates the duration between start and end date-times in minutes.
     *
     * @param startDateTime Start date-time
     * @param endDateTime End date-time
     * @return Duration in minutes
     */
    public static long calculateDurationInMinutes(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return ChronoUnit.MINUTES.between(startDateTime, endDateTime);
    }

    /**
     * Calculates the duration between start and end dates and times in minutes.
     *
     * @param startDate Start date as string (yyyy-MM-dd)
     * @param startTime Start time as string (HH:mm)
     * @param endDate End date as string (yyyy-MM-dd)
     * @param endTime End time as string (HH:mm)
     * @return Duration in minutes
     */
    public static long calculateDurationInMinutes(String startDate, String startTime, String endDate, String endTime) {
        LocalDate parsedStartDate = parseDate(startDate);
        LocalTime parsedStartTime = parseTime(startTime);
        LocalDate parsedEndDate = parseDate(endDate);
        LocalTime parsedEndTime = parseTime(endTime);

        if (parsedStartDate == null || parsedStartTime == null || parsedEndDate == null || parsedEndTime == null) {
            throw new IllegalArgumentException("Invalid date or time format");
        }

        LocalDateTime startDateTime = LocalDateTime.of(parsedStartDate, parsedStartTime);
        LocalDateTime endDateTime = LocalDateTime.of(parsedEndDate, parsedEndTime);

        return calculateDurationInMinutes(startDateTime, endDateTime);
    }

    /**
     * Formats a duration in minutes to a human-readable string (e.g., "2 hours 30 minutes").
     *
     * @param minutes Duration in minutes
     * @return Human-readable duration string
     */
    public static String formatDuration(long minutes) {
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;

        if (hours > 0) {
            return hours + (hours == 1 ? " hour " : " hours ") +
                    (remainingMinutes > 0 ? remainingMinutes + (remainingMinutes == 1 ? " minute" : " minutes") : "");
        } else {
            return remainingMinutes + (remainingMinutes == 1 ? " minute" : " minutes");
        }
    }

    /**
     * Checks if the given time is within the specified operating hours.
     *
     * @param time The time to check
     * @param openingTime Opening time of the parking plot
     * @param closingTime Closing time of the parking plot
     * @return true if the time is within operating hours, false otherwise
     */
    public static boolean isWithinOperatingHours(LocalTime time, LocalTime openingTime, LocalTime closingTime) {
        // Handle the case where operating hours span across midnight
        if (closingTime.isBefore(openingTime)) {
            return !time.isAfter(closingTime) || !time.isBefore(openingTime);
        } else {
            return !time.isBefore(openingTime) && !time.isAfter(closingTime);
        }
    }

    /**
     * Checks if two time ranges overlap.
     *
     * @param start1 Start of first time range
     * @param end1 End of first time range
     * @param start2 Start of second time range
     * @param end2 End of second time range
     * @return true if the time ranges overlap, false otherwise
     */
    public static boolean isTimeRangeOverlap(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    /**
     * Checks if two date-time ranges overlap.
     *
     * @param start1 Start of first date-time range
     * @param end1 End of first date-time range
     * @param start2 Start of second date-time range
     * @param end2 End of second date-time range
     * @return true if the date-time ranges overlap, false otherwise
     */
    public static boolean isDateTimeRangeOverlap(LocalDateTime start1, LocalDateTime end1,
                                                 LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    /**
     * Adds days to a given date.
     *
     * @param date The original date
     * @param days Number of days to add
     * @return New date with days added
     */
    public static LocalDate addDays(LocalDate date, long days) {
        return date.plusDays(days);
    }

    /**
     * Adds hours to a given time.
     *
     * @param time The original time
     * @param hours Number of hours to add
     * @return New time with hours added
     */
    public static LocalTime addHours(LocalTime time, long hours) {
        return time.plusHours(hours);
    }

    /**
     * Adds minutes to a given time.
     *
     * @param time The original time
     * @param minutes Number of minutes to add
     * @return New time with minutes added
     */
    public static LocalTime addMinutes(LocalTime time, long minutes) {
        return time.plusMinutes(minutes);
    }

    /**
     * Converts the day of week value (1-7) from the database to the corresponding
     * day name (Sunday, Monday, etc.).
     *
     * @param dayOfWeek Day of week value (1 for Sunday, 7 for Saturday)
     * @return Day name as a string
     */
    public static String getDayNameFromValue(int dayOfWeek) {
        switch (dayOfWeek) {
            case Constants.SUNDAY: return "Sunday";
            case Constants.MONDAY: return "Monday";
            case Constants.TUESDAY: return "Tuesday";
            case Constants.WEDNESDAY: return "Wednesday";
            case Constants.THURSDAY: return "Thursday";
            case Constants.FRIDAY: return "Friday";
            case Constants.SATURDAY: return "Saturday";
            default: return "Unknown";
        }
    }

    /**
     * Gets the day of week value (1-7) for a given date.
     *
     * @param date The date to get day of week for
     * @return Day of week value (1 for Sunday, 7 for Saturday)
     */
    public static int getDayOfWeekValue(LocalDate date) {
        int javaDayOfWeek = date.getDayOfWeek().getValue(); // 1 (Monday) to 7 (Sunday)
        // Convert Java's day of week (1=Monday, 7=Sunday) to our system (1=Sunday, 7=Saturday)
        return javaDayOfWeek == 7 ? 1 : javaDayOfWeek + 1;
    }

    /**
     * Checks if a date is in the past.
     *
     * @param date The date to check
     * @return true if the date is in the past, false otherwise
     */
    public static boolean isDateInPast(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }

    /**
     * Checks if a time is in the past on the current date.
     *
     * @param time The time to check
     * @return true if the time is in the past today, false otherwise
     */
    public static boolean isTimeInPastToday(LocalTime time) {
        return time.isBefore(LocalTime.now());
    }

    /**
     * Checks if a date-time is in the past.
     *
     * @param dateTime The date-time to check
     * @return true if the date-time is in the past, false otherwise
     */
    public static boolean isDateTimeInPast(LocalDateTime dateTime) {
        return dateTime.isBefore(LocalDateTime.now());
    }

    /**
     * Calculates the total cost for parking based on duration and hourly rate.
     *
     * @param startDateTime Start date-time of parking
     * @param endDateTime End date-time of parking
     * @param hourlyRate Hourly rate for parking
     * @return Total cost for the parking duration
     */
    public static double calculateParkingCost(LocalDateTime startDateTime, LocalDateTime endDateTime, double hourlyRate) {
        long minutes = calculateDurationInMinutes(startDateTime, endDateTime);
        double hours = minutes / 60.0;
        // Round up to the nearest hour
        hours = Math.ceil(hours);
        return hours * hourlyRate;
    }
}