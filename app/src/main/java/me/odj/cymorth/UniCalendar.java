package me.odj.cymorth;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by owain on 9/18/13.
 */
public class UniCalendar {
    public static final String[] SLOTS = {
            "09:00",
            "10:00",
            "11:10",
            "12:10",
            "13:10",
            "14:10",
            "15:10",
            "16:10",
            "17:10"
    };
    public static final int SLOT_LENGTH_MIN = 50;

    public static java.util.Calendar getInstance() {
        java.util.Calendar calendar = java.util.Calendar.getInstance(Locale.UK);
        calendar.setFirstDayOfWeek(java.util.Calendar.MONDAY);
        return calendar;
    }

    public static Calendar getStartOfWeek(Date date) {
        Calendar calendar = getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return calendar;
    }

    public static Calendar[] getWeekRange(Date date) {
        Calendar[] range = new Calendar[2];
        java.util.Calendar calendar = getInstance();
        java.util.Calendar calendar2 = getInstance();
        calendar.setTime(date);
        calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 9);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        range[0] = calendar;
        calendar2.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.FRIDAY);
        calendar2.set(java.util.Calendar.HOUR_OF_DAY, 18);
        range[1] = calendar2;
        return range;
    }

    public static Calendar getStartOfUniDay(Date date) {
        Calendar calendar = getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static Calendar getEndOfUniDay(Date date) {
        Calendar calendar = getStartOfUniDay(date);
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static Calendar getStartOfNextWeek(Date date) {
        Calendar calendar = getStartOfUniDay(date);
        while(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return calendar;
    }

    public static Calendar getEndOfLastWeek(Date date) {
        Calendar calendar = getEndOfUniDay(date);
        while(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
        return calendar;
    }
}
