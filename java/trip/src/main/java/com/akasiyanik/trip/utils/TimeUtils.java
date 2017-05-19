package com.akasiyanik.trip.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.ChronoUnit.MINUTES;

/**
 * @author akasiyanik
 *         5/5/17
 */
public final class TimeUtils {

    private static final LocalTime START_OF_DAY = LocalTime.of(4, 0); // 4 am

    private static final LocalTime MIDNIGHT = LocalTime.of(0, 0);

    private static final int MAX_MINUTES = 24 * 60;

    private static final int MINUTES_FROM_START_OF_DAY_TO_MIDNIGHT = (int) MINUTES.between(START_OF_DAY, LocalTime.of(23, 59));

    private static ThreadLocal<DateTimeFormatter> formatter = new ThreadLocal<DateTimeFormatter>() {
        @Override
        protected DateTimeFormatter initialValue() {
            return DateTimeFormatter.ofPattern("H:mm");
        }
    };

    public static int timeToMinutes(String time) {
       return timeToMinutes(LocalTime.parse(time, formatter.get()));
    }

    public static int timeToMinutes(LocalTime time) {
        int minutes = (int) MINUTES.between(START_OF_DAY, time);
        if (minutes < 0) {
            minutes = (int) (MINUTES_FROM_START_OF_DAY_TO_MIDNIGHT + 1 + MINUTES.between(MIDNIGHT, time));
        }
        return minutes;
//        int realMinutes = time.getHour() * 60 + time.getMinute();
//        if (time.getHour() < 4) {
//            realMinutes += MINUTES_FROM_START_OF_DAY_TO_MIDNIGHT;
//        }
//        return realMinutes - START_OF_DAY;
    }

    public static LocalTime minutesToTime(int minutes) {
        if (minutes > MAX_MINUTES) {
            throw new RuntimeException("Parameter 'minutes' is not valid: " + minutes);
        }

        if (minutes > MINUTES_FROM_START_OF_DAY_TO_MIDNIGHT) {
            minutes = minutes - MINUTES_FROM_START_OF_DAY_TO_MIDNIGHT - 1;
            int hours = minutes / 60;
            int mins = minutes % 60;
            return LocalTime.of(hours, mins);
        } else {
            return START_OF_DAY.plusMinutes(minutes);
        }

    }
}
