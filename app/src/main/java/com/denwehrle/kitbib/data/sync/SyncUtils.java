package com.denwehrle.kitbib.data.sync;

import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;

import com.denwehrle.kitbib.data.provider.Contract;

/**
 * @author Dennis Wehrle
 */
public class SyncUtils {

    private static final long SYNC_FREQUENCY = 60 * 60 * 6;  // 6 hours (in seconds)
    private static final String CONTENT_AUTHORITY = Contract.CONTENT_AUTHORITY;

    public static void createSyncAccount(Account account) {

        // Inform the system that this account supports sync
        ContentResolver.setIsSyncable(account, CONTENT_AUTHORITY, 1);

        // Inform the system that this account is eligible for auto sync when the network is up
        ContentResolver.setSyncAutomatically(account, CONTENT_AUTHORITY, true);

        // Recommend a schedule for automatic synchronization. The system may modify this based
        // on other scheduled syncs and network utilization.
        ContentResolver.addPeriodicSync(account, CONTENT_AUTHORITY, new Bundle(), SYNC_FREQUENCY);
    }

    public static void triggerRefresh(Account account, int position) {
        Bundle bundle = new Bundle();

        // Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putInt("POSITION", position);
        ContentResolver.requestSync(account, CONTENT_AUTHORITY, bundle);
    }
}