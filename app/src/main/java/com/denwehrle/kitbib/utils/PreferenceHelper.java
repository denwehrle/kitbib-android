package com.denwehrle.kitbib.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Date;

/**
 * @author Dennis Wehrle
 */
public class PreferenceHelper {

    private static final String PREF_LAST_NOTIFICATION_DATE = "lastNotificationDate";

    public static void setLastNotificationDate(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(PREF_LAST_NOTIFICATION_DATE, new Date(System.currentTimeMillis()).getTime()).apply();
    }

    public static Date getLastNotificationDate(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return new Date(sp.getLong(PREF_LAST_NOTIFICATION_DATE, 0));
    }
}