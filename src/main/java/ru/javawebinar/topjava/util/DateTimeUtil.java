package ru.javawebinar.topjava.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class DateTimeUtil {
    private static Map<String, DateTimeFormatter> dateTimeFormatterByPattern = new HashMap<>();

    public static boolean isBetweenHalfOpen(LocalTime lt, LocalTime startTime, LocalTime endTime) {
        return lt.compareTo(startTime) >= 0 && lt.compareTo(endTime) < 0;
    }

    public static String formatLocalDateTime(LocalDateTime localDateTime, String pattern) {
        dateTimeFormatterByPattern.putIfAbsent(pattern, DateTimeFormatter.ofPattern(pattern));
        return localDateTime.format(dateTimeFormatterByPattern.get(pattern));
    }
}
