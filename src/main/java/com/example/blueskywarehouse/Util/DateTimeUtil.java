package com.example.blueskywarehouse.Util;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Hilfsklasse: Umwandlung zwischen String und Timestamp
 */
public class DateTimeUtil {

    // Gängige Formate
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Wandelt einen String in einen Timestamp um.
     * Unterstützte Formate:
     * - yyyy-MM-dd HH:mm:ss
     * - yyyy-MM-dd (automatisch 00:00:00 ergänzt)
     */
    public static Timestamp toTimestamp(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        dateTimeStr = dateTimeStr.trim();
        if (dateTimeStr.length() == 10) { // Format: yyyy-MM-dd
            LocalDate ld = LocalDate.parse(dateTimeStr, DATE_FORMATTER);
            return Timestamp.valueOf(ld.atStartOfDay());
        } else {
            LocalDateTime ldt = LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
            return Timestamp.valueOf(ldt);
        }
    }

    /**
     * Wandelt explizit einen String im Format yyyy-MM-dd in einen Timestamp um
     * (ergänzt automatisch 00:00:00)
     */
    public static Timestamp toTimestampFromDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        LocalDate ld = LocalDate.parse(dateStr, DATE_FORMATTER);
        return Timestamp.valueOf(ld.atStartOfDay());
    }

    /**
     * Wandelt einen ISO-8601 String (z. B. 2025-08-22T14:30:00) in einen Timestamp um
     */
    public static Timestamp toTimestampIso(String isoDateTime) {
        if (isoDateTime == null || isoDateTime.trim().isEmpty()) {
            return null;
        }
        LocalDateTime ldt = LocalDateTime.parse(isoDateTime);
        return Timestamp.valueOf(ldt);
    }

    /**
     * Formatiert einen Timestamp zurück in einen String (yyyy-MM-dd HH:mm:ss)
     */
    public static String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return DATE_TIME_FORMATTER.format(timestamp.toLocalDateTime());
    }
}
