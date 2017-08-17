package com.morgane.painauchocolatv2;

import android.app.Application;

import io.realm.Realm;

/**
 * Application class.
 */
public class PainAuChocolatv2Application extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
