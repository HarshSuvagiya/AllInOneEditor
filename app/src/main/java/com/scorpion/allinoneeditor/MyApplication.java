package com.scorpion.allinoneeditor;

import android.app.Application;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.lang.Thread.UncaughtExceptionHandler;

public class MyApplication extends Application {

    private static MyApplication _instance;
    private SharedPreferences _sharedPRefrences;
    private static AppOpenManager appOpenManager;

    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(
                this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {}
                });
        appOpenManager = new AppOpenManager(this);

        Thread.setDefaultUncaughtExceptionHandler (new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                e.printStackTrace ();
            }
        });

        _instance = this;
    }

    public static synchronized MyApplication getInstance() {
        MyApplication SCRMyApplication;
        synchronized (MyApplication.class) {
            SCRMyApplication = _instance;
        }
        return SCRMyApplication;
    }

    public SharedPreferences getPrefrences() {
        return _sharedPRefrences;
    }

    public boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isAvailable() && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
