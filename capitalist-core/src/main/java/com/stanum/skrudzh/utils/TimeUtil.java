package com.stanum.skrudzh.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TimeUtil {

    private TimeUtil() { }

    public static Timestamp now() {
        return Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Z")));
    }

    public static LocalDateTime parseParamTimestamp(String requestParamTimestamp) {
        return ZonedDateTime.parse(requestParamTimestamp).toLocalDateTime();
    }

    public static int amountOfDaysInCurrentMonth() {
        Calendar calendar = GregorianCalendar.getInstance();
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static int dayOfCurrentMonth() {
        Calendar calendar = GregorianCalendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static Timestamp beginningOfTheTime() {
        return new Timestamp(0);
    }

    public static LocalDateTime  beginningOfMinute() {
        Calendar calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone(ZoneId.of("Z")));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.of("Z"));
    }

    public static Timestamp beginningOfDay() {
        Calendar calendar = GregorianCalendar.getInstance();
        clear(calendar);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Timestamp beginningOfWeek() {
        Calendar calendar = GregorianCalendar.getInstance();
        clear(calendar);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Timestamp beginningOfMonth() {
        Calendar calendar = GregorianCalendar.getInstance();
        clear(calendar);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Timestamp beginningOfQuarter() {
        Calendar calendar = GregorianCalendar.getInstance();
        clear(calendar);
        calendar.set(Calendar.MONTH, getBeginOfQuarter(calendar));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Timestamp beginningOfYear() {
        Calendar calendar = GregorianCalendar.getInstance();
        clear(calendar);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Timestamp beginningOfPreviousMonth() {
        Calendar calendar = GregorianCalendar.getInstance();
        clear(calendar);
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Timestamp endOfDay() {
        Calendar calendar = GregorianCalendar.getInstance();
        roleDayToTheEnd(calendar);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Timestamp endOfWeek() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
        roleDayToTheEnd(calendar);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Timestamp endOfMonth() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        roleDayToTheEnd(calendar);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Timestamp endOfQuarter() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.MONTH, getEndOfQuarter(calendar));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        roleDayToTheEnd(calendar);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Timestamp endOfYear() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        roleDayToTheEnd(calendar);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static LocalDateTime roleDayToTheBeginning(LocalDateTime dateTime) {
        Calendar calendar = GregorianCalendar.from(ZonedDateTime.of(dateTime, ZoneId.of("Z")));
        clear(calendar);
        return LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.of("Z"));
    }

    private static void clear(Calendar calendar) {
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
    }

    private static void roleDayToTheEnd(Calendar calendar) {
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
    }

    private static int getBeginOfQuarter(Calendar calendar) {
        if (calendar.get(Calendar.MONTH) <= 2) {
            return Calendar.JANUARY;
        } else if (calendar.get(Calendar.MONTH) <= 5) {
            return Calendar.APRIL;
        } else if (calendar.get(Calendar.MONTH) <= 8) {
            return Calendar.JULY;
        } else {
            return Calendar.OCTOBER;
        }
    }

    private static int getEndOfQuarter(Calendar calendar) {
        if (calendar.get(Calendar.MONTH) <= 2) {
            return Calendar.MARCH;
        } else if (calendar.get(Calendar.MONTH) <= 5) {
            return Calendar.JUNE;
        } else if (calendar.get(Calendar.MONTH) <= 8) {
            return Calendar.SEPTEMBER;
        } else {
            return Calendar.DECEMBER;
        }
    }
}
