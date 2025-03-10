package ru.practicum.server.helpers;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTimeHelper {
    private static final String pattern = "yyyy-MM-dd HH:mm:ss";

    public static Instant toInstant(String input) {
        if (input == null) {
            return null;
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, Locale.US);
        LocalDateTime localDateTime = LocalDateTime.parse(input, dateTimeFormatter);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return zonedDateTime.toInstant();
    }

    public static Instant toInstant(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toInstant();
    }

    public static String toString(Instant instant) {
        if (instant == null) {
            return null;
        }
        return DateTimeFormatter
                .ofPattern(pattern)
                .withZone(ZoneId.systemDefault())
                .format(instant);
    }
}
