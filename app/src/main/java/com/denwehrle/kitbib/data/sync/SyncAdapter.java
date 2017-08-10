package com.denwehrle.kitbib.data.sync;

import android.accounts.Account;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.WorkerThread;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.denwehrle.kitbib.R;
import com.denwehrle.kitbib.data.model.BibFee;
import com.denwehrle.kitbib.data.model.BibSummaryItem;
import com.denwehrle.kitbib.data.model.LearningPlace;
import com.denwehrle.kitbib.data.model.LearningPlaceOccupation;
import com.denwehrle.kitbib.data.provider.Contract.BibFeeEntry;
import com.denwehrle.kitbib.data.provider.Contract.BibSummaryEntry;
import com.denwehrle.kitbib.data.provider.Contract.LearningPlaceEntry;
import com.denwehrle.kitbib.data.provider.Contract.LearningPlaceOccupationEntry;
import com.denwehrle.kitbib.data.remote.NetworkTasks;
import com.denwehrle.kitbib.data.remote.interfaces.AsyncBookList;
import com.denwehrle.kitbib.data.remote.interfaces.AsyncFeeList;
import com.denwehrle.kitbib.data.remote.interfaces.AsyncLearningPlaceList;
import com.denwehrle.kitbib.data.remote.interfaces.AsyncLearningPlaceOccupationList;
import com.denwehrle.kitbib.features.main.MainActivity;
import com.denwehrle.kitbib.utils.DateUtils;
import com.denwehrle.kitbib.utils.PreferenceHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Dennis Wehrle
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = SyncAdapter.class.getSimpleName();

    private final ContentResolver contentResolver;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        contentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "executing sync");

        int position = 0;

        if (extras != null) {
            position = extras.getInt("POSITION", 0);
        }

        if ((position != 0)) {
            if (position == 1) {
                new NetworkTasks.DownloadBooks(new AsyncBookList() {
                    @Override
                    public void resultList(List<BibSummaryItem> resultList) {
                        persistDownloadBooksData(resultList);
                    }
                }, getContext(), account).execute();
                Log.d(TAG, "sync only books");
                return;
            }
            if (position == 2) {
                new NetworkTasks.DownloadFees(new AsyncFeeList() {
                    @Override
                    public void resultList(List<BibFee> resultList) {
                        persistDownloadFeesData(resultList);
                    }
                }, getContext(), account).execute();
                Log.d(TAG, "sync only fees");
                return;
            }
            if (position == 3) {
                new NetworkTasks.DownloadLearningPlaces(new AsyncLearningPlaceList() {
                    @Override
                    public void resultList(List<LearningPlace> resultList) {
                        persistDownloadLearningPlacesData(resultList);
                    }
                }).execute();

                new NetworkTasks.DownloadLearningPlaceOccupations(new AsyncLearningPlaceOccupationList() {
                    @Override
                    public void resultList(List<LearningPlaceOccupation> resultList) {
                        persistDownloadLearningPlaceOccupationsData(resultList);
                    }
                }).execute();
                Log.d(TAG, "sync only learning");
                return;
            }
        }

        new NetworkTasks.DownloadBooks(new AsyncBookList() {
            @Override
            public void resultList(List<BibSummaryItem> resultList) {
                persistDownloadBooksData(resultList);
            }
        }, getContext(), account).execute();

        new NetworkTasks.DownloadFees(new AsyncFeeList() {
            @Override
            public void resultList(List<BibFee> resultList) {
                persistDownloadFeesData(resultList);
            }
        }, getContext(), account).execute();

        new NetworkTasks.DownloadLearningPlaces(new AsyncLearningPlaceList() {
            @Override
            public void resultList(List<LearningPlace> resultList) {
                persistDownloadLearningPlacesData(resultList);
            }
        }).execute();

        new NetworkTasks.DownloadLearningPlaceOccupations(new AsyncLearningPlaceOccupationList() {
            @Override
            public void resultList(List<LearningPlaceOccupation> resultList) {
                persistDownloadLearningPlaceOccupationsData(resultList);
            }
        }).execute();

        checkIfNotificationRaiseIsValid();
        Log.d(TAG, "sync done");
    }

    @WorkerThread
    private void persistDownloadBooksData(List<BibSummaryItem> resultList) {
        ArrayList<ContentValues> valuesList = new ArrayList<>();

        for (int i = 0; i < resultList.size(); i++) {
            ContentValues values = new ContentValues();

            values.put(BibSummaryEntry.COLUMN_INDEX, resultList.get(i).index);
            values.put(BibSummaryEntry.COLUMN_TITLE, resultList.get(i).title);
            values.put(BibSummaryEntry.COLUMN_STATE, resultList.get(i).state.getCode());
            values.put(BibSummaryEntry.COLUMN_ORIGIN_BIB, resultList.get(i).originBib);
            values.put(BibSummaryEntry.COLUMN_SIGNATURE, resultList.get(i).signature);
            values.put(BibSummaryEntry.COLUMN_ORDER_INFO, resultList.get(i).orderInfo);
            values.put(BibSummaryEntry.COLUMN_DUE_DATE, resultList.get(i).dueDate.getTime());
            values.put(BibSummaryEntry.COLUMN_FLAGGED, resultList.get(i).flagged);
            values.put(BibSummaryEntry.COLUMN_BOOK_COVER, resultList.get(i).bookCover);
            valuesList.add(values);
        }

        ContentValues[] valuesArray = new ContentValues[valuesList.size()];
        valuesArray = valuesList.toArray(valuesArray);

        contentResolver.delete(BibSummaryEntry.CONTENT_URI, null, null);
        contentResolver.bulkInsert(BibSummaryEntry.CONTENT_URI, valuesArray);
    }

    @WorkerThread
    private void persistDownloadFeesData(List<BibFee> resultList) {
        ArrayList<ContentValues> valuesList = new ArrayList<>();

        for (int i = 0; i < resultList.size(); i++) {
            ContentValues values = new ContentValues();

            values.put(BibFeeEntry.COLUMN_FEE, resultList.get(i).fee);
            values.put(BibFeeEntry.COLUMN_FEE_TITLE, resultList.get(i).feeTitle);
            values.put(BibFeeEntry.COLUMN_ARTICLE, resultList.get(i).article);
            values.put(BibFeeEntry.COLUMN_SIGNATURE, resultList.get(i).signature);
            values.put(BibFeeEntry.COLUMN_DUE_DATE, DateUtils.simpleDateFormat(resultList.get(i).dueDate));
            valuesList.add(values);
        }

        ContentValues[] valuesArray = new ContentValues[valuesList.size()];
        valuesArray = valuesList.toArray(valuesArray);

        contentResolver.delete(BibFeeEntry.CONTENT_URI, null, null);
        contentResolver.bulkInsert(BibFeeEntry.CONTENT_URI, valuesArray);
    }

    @WorkerThread
    private void persistDownloadLearningPlaceOccupationsData(List<LearningPlaceOccupation> resultList) {
        ArrayList<ContentValues> valuesList = new ArrayList<>();

        for (int i = 0; i < resultList.size(); i++) {
            ContentValues values = new ContentValues();

            values.put(LearningPlaceOccupationEntry.COLUMN_LOCATION, resultList.get(i).location);
            values.put(LearningPlaceOccupationEntry.COLUMN_OCCUPIED, resultList.get(i).occupiedSeats);
            values.put(LearningPlaceOccupationEntry.COLUMN_FREE, resultList.get(i).freeSeats);
            values.put(LearningPlaceOccupationEntry.COLUMN_UPDATEDAT, resultList.get(i).updatedAt.getTime());
            valuesList.add(values);
        }

        ContentValues[] valuesArray = new ContentValues[valuesList.size()];
        valuesArray = valuesList.toArray(valuesArray);

        contentResolver.bulkInsert(LearningPlaceOccupationEntry.CONTENT_URI, valuesArray);
    }

    @WorkerThread
    private void persistDownloadLearningPlacesData(List<LearningPlace> resultList) {
        ArrayList<ContentValues> valuesList = new ArrayList<>();

        for (int i = 0; i < resultList.size(); i++) {
            ContentValues values = new ContentValues();

            values.put(LearningPlaceEntry.COLUMN_LOCATION, resultList.get(i).location);
            values.put(LearningPlaceEntry.COLUMN_LONG_NAME, resultList.get(i).longName);
            values.put(LearningPlaceEntry.COLUMN_NAME, resultList.get(i).name);
            values.put(LearningPlaceEntry.COLUMN_SEATS, resultList.get(i).availableSeats);
            values.put(LearningPlaceEntry.COLUMN_INDEX, resultList.get(i).index);
            values.put(LearningPlaceEntry.COLUMN_URL, resultList.get(i).url);
            values.put(LearningPlaceEntry.COLUMN_COORDINATES, resultList.get(i).geoCoordinates);

            valuesList.add(values);
        }

        ContentValues[] valuesArray = new ContentValues[valuesList.size()];
        valuesArray = valuesList.toArray(valuesArray);

        contentResolver.bulkInsert(LearningPlaceEntry.CONTENT_URI, valuesArray);
    }

    @WorkerThread
    private void checkIfNotificationRaiseIsValid() {
        Date date = new Date();
        long timeDelta = date.getTime() - PreferenceHelper.getLastNotificationDate(getContext()).getTime();
        if (timeDelta > 24 * 60 * 60 * 1000) { // 24 hours
            checkIfNotificationIsNeeded();
            PreferenceHelper.setLastNotificationDate(getContext());
        } else
            Log.d(TAG, "Don't raise notification!");
    }

    @WorkerThread
    private void checkIfNotificationIsNeeded() {
        Cursor cursor = getContext().getContentResolver().query(
                BibSummaryEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int columnText = cursor.getColumnIndex(BibSummaryEntry.COLUMN_DUE_DATE);
                String stringDate = cursor.getString(columnText);
                Date date = DateUtils.dateFromTimestamp(stringDate);
                int diff = DateUtils.calculateTimeDifferenceInDays(date);

                if (diff > 0 && diff < 6) {
                    raiseNotification();
                }
            }
            cursor.close();
        }
    }

    private void raiseNotification() {

        // Open NotificationView Class on Notification Click
        Intent intent = new Intent(getContext(), MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        int ledColor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ledColor = getContext().getColor(R.color.colorPrimary);
        } else {
            ledColor = getContext().getResources().getColor(R.color.colorPrimary);
        }

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Create Notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext())
                .setSmallIcon(R.drawable.app_icon_plane)
                .setColor(getContext().getResources().getColor(R.color.colorPrimary))
                .setContentTitle(getContext().getString(R.string.notification_title))
                .setContentText(getContext().getString(R.string.notification_text))
                .setContentIntent(pIntent)
                .addAction(R.drawable.ic_schedule, getContext().getString(R.string.notification_action), pIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(getContext().getString(R.string.notification_text)))
                .setVibrate(new long[]{0, 500})
                .setLights(ledColor, 1000, 2000)
                .setSound(notificationSound)
                .setAutoCancel(true);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());
    }
}