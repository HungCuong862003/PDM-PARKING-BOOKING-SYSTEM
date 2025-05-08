package main.java.com.parkeasy.util;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for date and time operations
 */
public class DateTimeUtil {
    private static final Logger LOGGER = Logger.getLogger(DateTimeUtil.class.getName());

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Convert java.sql.Date to java.time.LocalDate
     */
    public static LocalDate toLocalDate(Date date) {
        return date != null ? date.toLocalDate() : null;
    }

    /**
     * Convert java.sql.Time to java.time.LocalTime
     */
    public static LocalTime toLocalTime(Time time) {
        return time != null ? time.toLocalTime() : null;
    }

    /**
     * Convert java.sql.Timestamp to java.time.LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }

    /**
     * Convert java.time.LocalDate to java.sql.Date
     */
    public static Date toSqlDate(LocalDate localDate) {
        return localDate != null ? Date.valueOf(localDate) : null;
    }

    /**
     * Convert java.time.LocalTime to java.sql.Time
     */
    public static Time toSqlTime(LocalTime localTime) {
        return localTime != null ? Time.valueOf(localTime) : null;
    }

    /**
     * Convert java.time.LocalDateTime to java.sql.Timestamp
     */
    public static Timestamp toSqlTimestamp(LocalDateTime localDateTime) {
        return localDateTime != null ? Timestamp.valueOf(localDateTime) : null;
    }

    /**
     * Get current date as java.sql.Date
     */
    public static Date getCurrentSqlDate() {
        return new Date(System.currentTimeMillis());
    }

    /**
     * Get current time as java.sql.Time
     */
    public static Time getCurrentSqlTime() {
        return new Time(System.currentTimeMillis());
    }

    /**
     * Get current timestamp as java.sql.Timestamp
     */
    public static Timestamp getCurrentSqlTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * Format date as string
     */
    public static String formatDate(Date date) {
        return date != null ? DATE_FORMAT.format(date) : "";
    }

    /**
     * Format time as string
     */
    public static String formatTime(Time time) {
        return time != null ? TIME_FORMAT.format(time) : "";
    }

    /**
     * Format timestamp as string
     */
    public static String formatTimestamp(Timestamp timestamp) {
        return timestamp != null ? DATETIME_FORMAT.format(timestamp) : "";
    }

    /**
     * Parse string to date
     */
    public static Date parseDate(String dateStr) {
        try {
            if (dateStr == null || dateStr.isEmpty()) {
                return null;
            }
            java.util.Date parsed = DATE_FORMAT.parse(dateStr);
            return new Date(parsed.getTime());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error parsing date: " + dateStr, e);
            return null;
        }
    }

    /**
     * Parse string to time
     */
    public static Time parseTime(String timeStr) {
        try {
            if (timeStr == null || timeStr.isEmpty()) {
                return null;
            }
            java.util.Date parsed = TIME_FORMAT.parse(timeStr);
            return new Time(parsed.getTime());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error parsing time: " + timeStr, e);
            return null;
        }
    }

    /**
     * Parse string to timestamp
     */
    public static Timestamp parseTimestamp(String timestampStr) {
        try {
            if (timestampStr == null || timestampStr.isEmpty()) {
                return null;
            }
            java.util.Date parsed = DATETIME_FORMAT.parse(timestampStr);
            return new Timestamp(parsed.getTime());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error parsing timestamp: " + timestampStr, e);
            return null;
        }
    }

    /**
     * Calculate time difference in minutes between two timestamps
     */
    public static long getMinutesBetween(Timestamp start, Timestamp end) {
        if (start == null || end == null) {
            return 0;
        }
        long diffMillis = end.getTime() - start.getTime();
        return TimeUnit.MILLISECONDS.toMinutes(diffMillis);
    }

    /**
     * Calculate time difference in minutes between date/time combinations
     */
    public static long getMinutesBetween(Date startDate, Time startTime, Date endDate, Time endTime) {
        if (startDate == null || startTime == null || endDate == null || endTime == null) {
            return 0;
        }

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        Calendar timeCalStart = Calendar.getInstance();
        timeCalStart.setTime(startTime);
        startCal.set(Calendar.HOUR_OF_DAY, timeCalStart.get(Calendar.HOUR_OF_DAY));
        startCal.set(Calendar.MINUTE, timeCalStart.get(Calendar.MINUTE));
        startCal.set(Calendar.SECOND, timeCalStart.get(Calendar.SECOND));

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        Calendar timeCalEnd = Calendar.getInstance();
        timeCalEnd.setTime(endTime);
        endCal.set(Calendar.HOUR_OF_DAY, timeCalEnd.get(Calendar.HOUR_OF_DAY));
        endCal.set(Calendar.MINUTE, timeCalEnd.get(Calendar.MINUTE));
        endCal.set(Calendar.SECOND, timeCalEnd.get(Calendar.SECOND));

        long diffMillis = endCal.getTimeInMillis() - startCal.getTimeInMillis();
        return TimeUnit.MILLISECONDS.toMinutes(diffMillis);
    }

    /**
     * Format a duration in minutes to a readable string
     */
    public static String formatDuration(long minutes) {
        if (minutes < 0) {
            return "Invalid duration";
        }

        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;

        if (hours > 0) {
            return String.format("%d hour%s %d minute%s",
                    hours, hours != 1 ? "s" : "",
                    remainingMinutes, remainingMinutes != 1 ? "s" : "");
        } else {
            return String.format("%d minute%s", remainingMinutes, remainingMinutes != 1 ? "s" : "");
        }
    }

    /**
     * Check if a date/time is within a specified number of minutes from now
     */
    public static boolean isWithinMinutes(Date date, Time time, int minutes) {
        try {
            if (date == null || time == null || minutes <= 0) {
                return false;
            }

            // Get current date and time
            Timestamp now = new Timestamp(System.currentTimeMillis());

            // Create a timestamp from the target date and time
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            Calendar timeCal = Calendar.getInstance();
            timeCal.setTime(time);
            cal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
            cal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
            cal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));

            Timestamp target = new Timestamp(cal.getTimeInMillis());

            // Calculate time difference in minutes
            long diffMinutes = getMinutesBetween(now, target);

            // Return true if the difference is within the specified minutes
            return Math.abs(diffMinutes) <= minutes;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking if within minutes", e);
            return false;
        }
    }

    /**
     * Get time remaining until a specific date/time as a formatted string
     */
    public static String getTimeRemainingUntil(Date date, Time time) {
        try {
            if (date == null || time == null) {
                return "Unknown";
            }

            // Get current date and time
            Timestamp now = new Timestamp(System.currentTimeMillis());

            // Create a timestamp from the target date and time
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            Calendar timeCal = Calendar.getInstance();
            timeCal.setTime(time);
            cal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
            cal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
            cal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));

            Timestamp target = new Timestamp(cal.getTimeInMillis());

            // If the target time is in the past, return "Expired"
            if (target.before(now)) {
                return "Expired";
            }

            // Calculate time difference
            long diffMillis = target.getTime() - now.getTime();
            long days = TimeUnit.MILLISECONDS.toDays(diffMillis);
            diffMillis -= TimeUnit.DAYS.toMillis(days);
            long hours = TimeUnit.MILLISECONDS.toHours(diffMillis);
            diffMillis -= TimeUnit.HOURS.toMillis(hours);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis);

            // Format the time remaining
            StringBuilder sb = new StringBuilder();
            if (days > 0) {
                sb.append(days).append(" day").append(days != 1 ? "s" : "").append(" ");
            }
            if (hours > 0 || days > 0) {
                sb.append(hours).append(" hour").append(hours != 1 ? "s" : "").append(" ");
            }
            sb.append(minutes).append(" minute").append(minutes != 1 ? "s" : "");

            return sb.toString();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating time remaining", e);
            return "Unknown";
        }
    }

    /**
     * Get elapsed time since a specific date/time as a formatted string
     */
    public static String getElapsedTimeSince(Date date, Time time) {
        try {
            if (date == null || time == null) {
                return "Unknown";
            }

            // Get current date and time
            Timestamp now = new Timestamp(System.currentTimeMillis());

            // Create a timestamp from the start date and time
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            Calendar timeCal = Calendar.getInstance();
            timeCal.setTime(time);
            cal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
            cal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
            cal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));

            Timestamp start = new Timestamp(cal.getTimeInMillis());

            // If the start time is in the future, return "Not started yet"
            if (start.after(now)) {
                return "Not started yet";
            }

            // Calculate time difference
            long diffMillis = now.getTime() - start.getTime();
            long days = TimeUnit.MILLISECONDS.toDays(diffMillis);
            diffMillis -= TimeUnit.DAYS.toMillis(days);
            long hours = TimeUnit.MILLISECONDS.toHours(diffMillis);
            diffMillis -= TimeUnit.HOURS.toMillis(hours);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis);

            // Format the elapsed time
            StringBuilder sb = new StringBuilder();
            if (days > 0) {
                sb.append(days).append(" day").append(days != 1 ? "s" : "").append(" ");
            }
            if (hours > 0 || days > 0) {
                sb.append(hours).append(" hour").append(hours != 1 ? "s" : "").append(" ");
            }
            sb.append(minutes).append(" minute").append(minutes != 1 ? "s" : "");

            return sb.toString();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating elapsed time", e);
            return "Unknown";
        }
    }

    /**
     * Add minutes to a date and time
     */
    public static Timestamp addMinutes(Date date, Time time, int minutes) {
        try {
            if (date == null || time == null) {
                return null;
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            Calendar timeCal = Calendar.getInstance();
            timeCal.setTime(time);
            cal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
            cal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
            cal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));

            cal.add(Calendar.MINUTE, minutes);
            return new Timestamp(cal.getTimeInMillis());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding minutes to date/time", e);
            return null;
        }
    }

    /**
     * Check if a time period overlaps with another time period
     */
    public static boolean isOverlapping(Date start1, Time time1, Date end1, Time time1End,
                                        Date start2, Time time2, Date end2, Time time2End) {
        try {
            Calendar startCal1 = Calendar.getInstance();
            startCal1.setTime(start1);
            Calendar timeCal1 = Calendar.getInstance();
            timeCal1.setTime(time1);
            startCal1.set(Calendar.HOUR_OF_DAY, timeCal1.get(Calendar.HOUR_OF_DAY));
            startCal1.set(Calendar.MINUTE, timeCal1.get(Calendar.MINUTE));
            startCal1.set(Calendar.SECOND, timeCal1.get(Calendar.SECOND));

            Calendar endCal1 = Calendar.getInstance();
            endCal1.setTime(end1);
            Calendar timeCal1End = Calendar.getInstance();
            timeCal1End.setTime(time1End);
            endCal1.set(Calendar.HOUR_OF_DAY, timeCal1End.get(Calendar.HOUR_OF_DAY));
            endCal1.set(Calendar.MINUTE, timeCal1End.get(Calendar.MINUTE));
            endCal1.set(Calendar.SECOND, timeCal1End.get(Calendar.SECOND));

            Calendar startCal2 = Calendar.getInstance();
            startCal2.setTime(start2);
            Calendar timeCal2 = Calendar.getInstance();
            timeCal2.setTime(time2);
            startCal2.set(Calendar.HOUR_OF_DAY, timeCal2.get(Calendar.HOUR_OF_DAY));
            startCal2.set(Calendar.MINUTE, timeCal2.get(Calendar.MINUTE));
            startCal2.set(Calendar.SECOND, timeCal2.get(Calendar.SECOND));

            Calendar endCal2 = Calendar.getInstance();
            endCal2.setTime(end2);
            Calendar timeCal2End = Calendar.getInstance();
            timeCal2End.setTime(time2End);
            endCal2.set(Calendar.HOUR_OF_DAY, timeCal2End.get(Calendar.HOUR_OF_DAY));
            endCal2.set(Calendar.MINUTE, timeCal2End.get(Calendar.MINUTE));
            endCal2.set(Calendar.SECOND, timeCal2End.get(Calendar.SECOND));

            // Check for overlap
            return !(endCal1.before(startCal2) || startCal1.after(endCal2));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking for time period overlap", e);
            return false;
        }
    }
}