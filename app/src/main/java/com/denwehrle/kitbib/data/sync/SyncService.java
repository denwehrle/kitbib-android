package com.denwehrle.kitbib.data.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * @author Dennis Wehrle
 */
public class SyncService extends Service {

    private static final String TAG = SyncService.class.getSimpleName();

    private static SyncAdapter syncAdapter = null;
    private static final Object syncAdapterLock = new Object();

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        synchronized (syncAdapterLock) {
            if (syncAdapter == null) {
                syncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return syncAdapter.getSyncAdapterBinder();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}