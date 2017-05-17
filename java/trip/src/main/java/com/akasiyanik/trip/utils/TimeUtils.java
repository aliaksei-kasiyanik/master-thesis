package com.akasiyanik.trip.utils;

import java.time.LocalTime;

/**
 * @author akasiyanik
 *         5/5/17
 */
public final class TimeUtils {

    private static final int MAX_MINUTES = 24 * 60;

    public static int timeToMinutes(LocalTime time) {
        return time.getHour() * 60 + time.getMinute();
    }

    public static LocalTime minutesToTime(int minutes) {
        if (minutes > MAX_MINUTES) {
            throw new RuntimeException("Parameter 'minutes' is not valid: " + minutes);
        }
        int hours = minutes / 60;
        int mins = minutes % 60;
        return LocalTime.of(hours, mins);
    }
}
