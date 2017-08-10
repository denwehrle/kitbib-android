package com.denwehrle.kitbib.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Dennis Wehrle
 */
public class DateUtils {

    public static String simpleDateFormat(Date date) {
        return new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(date);
    }

    public static Date stringToDateFormat(String stringDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
        Date date = null;
        try {
            date = formatter.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date dateFromTimestamp(String timestamp) {
        return new Date(Long.parseLong(timestamp));
    }

    public static String SimpleDateFromTimestamp(String timestamp) {
        return new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(new Date(Long.parseLong(timestamp)));
    }

    public static int calculateTimeDifferenceInDays(Date date) {
        Date currentDate = new Date();

        long diff = date.getTime() - currentDate.getTime();

        int days = (int) Math.ceil(diff / 1000.0 / 60.0 / 60.0 / 24.0);
        if (days < 0) {
            days = 0;
        }
        return days;
    }

    public static int calculateTimeDifferenceInMinutes(Date date) {
        Date currentDate = new Date();

        long diff = currentDate.getTime() - date.getTime();

        int minutes = (int) (diff / 1000.0 / 60.0);
        if (minutes < 0) {
            minutes = 0;
        }
        return minutes;
    }
}