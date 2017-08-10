package com.denwehrle.kitbib.data.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;

import com.denwehrle.kitbib.features.login.LoginActivity;

/**
 * @author Dennis Wehrle
 */
public class Authenticator extends AbstractAccountAuthenticator {

    private Context mContext;

    public Authenticator(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        final Bundle result;
        final Intent intent;

        intent = new Intent(this.mContext, LoginActivity.class);
        intent.putExtra(SyncStateContract.Constants.ACCOUNT_TYPE, authTokenType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        result = new Bundle();
        result.putParcelable(AccountManager.KEY_INTENT, intent);

        return result;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }
}