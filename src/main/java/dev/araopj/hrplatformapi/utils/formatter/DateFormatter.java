package dev.araopj.hrplatformapi.utils.formatter;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for formatting and parsing dates and date-times.
 * Supports predefined formats: iso (yyyy-MM-dd), short (MM/dd/yyyy), long (MMMM dd, yyyy),
 * date_time (yyyy-MM-dd HH:mm:ss), full (MMMM d, yyyy), short_month (MMM d, yyyy), no_year (MMMM d).
 * Allows adding custom formats.
 *
 * <p>Example usage:
 * <pre>
 * {@code
 * @Autowired
 * private DateFormatter dateFormatter;
 *
 * // Formatting
 * LocalDate date = LocalDate.of(2000, 1, 2);
 * String longFormat = dateFormatter.format(date, "long"); // January 02, 2000
 * String shortMonth = dateFormatter.format(date, "short_month"); // Jan 2, 2000
 * String noYear = dateFormatter.format(date, "no_year"); // January 2
 *
 * LocalDateTime dateTime = LocalDateTime.of(2000, 1, 2, 15, 30);
 * String dateTimeStr = dateFormatter.format(dateTime, "date_time"); // 2000-01-02 15:30:00
 *
 * // Parsing
 * LocalDate parsedDate = dateFormatter.parseDate("January 2, 2000", "full"); // 2000-01-02
 * LocalDateTime parsedDateTime = dateFormatter.parseDateTime("2000-01-02 15:30:00", "date_time"); // 2000-01-02T15:30
 *
 * // Custom format
 * dateFormatter.addCustomFormat("custom", "dd-MMM-yyyy");
 * String customFormat = dateFormatter.format(date, "custom"); // 02-Jan-2000
 * }
 * </pre>
 *
 * @see DateTimeFormatter
 */
@UtilityClass
public class DateFormatter {
    private final Map<String, DateTimeFormatter> FORMATTERS = new HashMap<>(
            Map.of(
                    "iso", DateTimeFormatter.ISO_LOCAL_DATE,
                    "short", DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                    "long", DateTimeFormatter.ofPattern("MMMM dd, yyyy"),
                    "date_time", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                    "full", DateTimeFormatter.ofPattern("MMMM d, yyyy"),
                    "short_month", DateTimeFormatter.ofPattern("MMM d, yyyy"),
                    "no_year", DateTimeFormatter.ofPattern("MMMM d")
            )
    );

    /**
     * Adds a custom date format to the formatter map.
     *
     * @param formatKey The key to identify the custom format.
     * @param pattern   The date pattern string.
     */
    public void addCustomFormat(String formatKey, String pattern) {
        FORMATTERS.put(formatKey, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Formats a LocalDate using the specified format key.
     *
     * @param date      The LocalDate to format.
     * @param formatKey The key identifying the desired format.
     * @return The formatted date string.
     */
    public String format(LocalDate date, String formatKey) {
        DateTimeFormatter formatter = FORMATTERS.getOrDefault(formatKey, DateTimeFormatter.ISO_LOCAL_DATE);
        return date.format(formatter);
    }

    /**
     * Formats a LocalDateTime using the specified format key.
     *
     * @param dateTime  The LocalDateTime to format.
     * @param formatKey The key identifying the desired format.
     * @return The formatted date-time string.
     */
    public String format(LocalDateTime dateTime, String formatKey) {
        DateTimeFormatter formatter = FORMATTERS.getOrDefault(formatKey, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return dateTime.format(formatter);
    }

    /**
     * Parses a date string into a LocalDate using the specified format key.
     *
     * @param dateStr   The date string to parse.
     * @param formatKey The key identifying the format of the input string.
     * @return The parsed LocalDate.
     */
    public LocalDate parseDate(String dateStr, String formatKey) {
        DateTimeFormatter formatter = FORMATTERS.getOrDefault(formatKey, DateTimeFormatter.ISO_LOCAL_DATE);
        return LocalDate.parse(dateStr, formatter);
    }

    /**
     * Parses a date-time string into a LocalDateTime using the specified format key.
     *
     * @param dateTimeStr The date-time string to parse.
     * @param formatKey   The key identifying the format of the input string.
     * @return The parsed LocalDateTime.
     */
    public LocalDateTime parseDateTime(String dateTimeStr, String formatKey) {
        DateTimeFormatter formatter = FORMATTERS.getOrDefault(formatKey, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return LocalDateTime.parse(dateTimeStr, formatter);
    }
}