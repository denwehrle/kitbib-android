package com.denwehrle.kitbib.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import com.denwehrle.kitbib.R;

/**
 * @author Dennis Wehrle
 */
public class RecentTasksStyler {

    private static Bitmap sIcon = null;

    private RecentTasksStyler() {
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void styleRecentTasksEntry(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        Resources resources = activity.getResources();
        String label = resources.getString(activity.getApplicationInfo().labelRes);
        int colorPrimary = ContextCompat.getColor(activity, R.color.colorPrimary);

        if (sIcon == null) {
            // Cache to avoid decoding the same bitmap on every Activity change
            sIcon = BitmapFactory.decodeResource(resources, R.drawable.app_icon_plane);
        }

        activity.setTaskDescription(new ActivityManager.TaskDescription(label, sIcon, colorPrimary));
    }
}