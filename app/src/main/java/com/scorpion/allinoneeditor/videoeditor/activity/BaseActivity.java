package com.scorpion.allinoneeditor.videoeditor.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

//import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.scorpion.allinoneeditor.AdHelper;
import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.videoeditor.utils.Helper;

import java.io.File;

import static com.scorpion.allinoneeditor.AdHelper.LoadInter;
import static com.scorpion.allinoneeditor.AdHelper.mInterstitialAd;

public class BaseActivity extends AppCompatActivity {

    private FrameLayout adContainerView;
    public AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File file = new File(externalStorageDirectory, getString(R.string.app_name));
        if (!file.exists()) {
            file.mkdir();
        }


        if (Helper.check_permission(BaseActivity.this)) {
            Helper.request_permission(BaseActivity.this, 24);
            return;
        }

    }

    public void loadBannerFirst(Activity act, AdView mAdView2){

//        //banner ad
//        com.facebook.ads.AdView adViewfb = new com.facebook.ads.AdView(act, getString(R.string.fb_banner_id), AdSize.BANNER_HEIGHT_50);
//
//        // Add the ad view to your activity layout
//        laybanner.addView(adViewfb);
//        // Request an ad
//        adViewfb.loadAd();

        AdHelper.AdLoadHelper(act, mAdView2);
    }


    public void loadBanner() {
        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);
    }

    public AdSize getAdSize() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }


}
