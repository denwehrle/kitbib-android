package com.denwehrle.kitbib.data.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.denwehrle.kitbib.data.provider.Contract.BibFeeEntry;
import com.denwehrle.kitbib.data.provider.Contract.BibSummaryEntry;

/**
 * @author Dennis Wehrle
 */
public class AuthenticatorUtils {

    public static Account getAccount(Context context) {
        Account account = null;

        AccountManager accountManager = AccountManager.get(context);

        Account[] acc = accountManager.getAccountsByType("com.denwehrle.kitbib");
        if (acc.length > 0) {
            account = acc[0];
        }

        return account;
    }

    public static void removeAccount(Context context) {
        AccountManager accountManager = AccountManager.get(context);

        accountManager.removeAccount(getAccount(context), null, null);

        // clear db
        context.getContentResolver().delete(BibSummaryEntry.CONTENT_URI, null, null);
        context.getContentResolver().delete(BibFeeEntry.CONTENT_URI, null, null);
    }
}