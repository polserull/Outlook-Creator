package xyz.polserull.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class ZuluTime {
    public final static String[] zTime = {
            "00Z",
            "01Z",
            "02Z",
            "03Z",
            "04Z",
            "05Z",
            "06Z",
            "07Z",
            "08Z",
            "09Z",
            "10Z",
            "11Z",
            "12Z",
            "13Z",
            "14Z",
            "15Z",
            "16Z",
            "17Z",
            "18Z",
            "19Z",
            "20Z",
            "21Z",
            "22Z",
            "23Z",
    };

    public static int getCurrentZuluHour() {
        ZonedDateTime gmtTime = ZonedDateTime.now(ZoneId.of("GMT"));
        return gmtTime.getHour();
    }

    public static String[] nextThreeDays() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        String[] nextThreeDates = new String[4];
        nextThreeDates[0] = currentDate.format(formatter);
        for (int i = 1; i <= 3; i++) {
            LocalDate nextDate = currentDate.plusDays(i);
            nextThreeDates[i] = nextDate.format(formatter);
        }
        return nextThreeDates;
    }
}
