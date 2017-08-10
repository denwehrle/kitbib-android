package com.denwehrle.kitbib.data.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author Dennis Wehrle
 */
public class AuthenticatorService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return new Authenticator(this).getIBinder();
    }
}