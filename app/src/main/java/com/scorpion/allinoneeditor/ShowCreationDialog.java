package com.scorpion.allinoneeditor;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.util.ArrayList;
import java.util.List;
//import com.facebook.ads.Ad;
//import com.facebook.ads.AdError;
//import com.facebook.ads.AdIconView;
//import com.facebook.ads.AdOptionsView;
//import com.facebook.ads.NativeAd;
//import com.facebook.ads.NativeAdLayout;
//import com.facebook.ads.NativeAdListener;


public class ShowCreationDialog {

    Dialog pd = null;
    FrameLayout frameLayout;
    Context mContext;
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
    private UnifiedNativeAdView nativeAdView;
    private AdLoader adLoader;

    public UnifiedNativeAd nativeAdmob;
    public ShowCreationDialog(Context context) {
        mContext = context;
        pd = new Dialog(context, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        pd.setContentView(R.layout.progress_dialog);
        LottieAnimationView lottieAnimationView;
        lottieAnimationView = pd.findViewById(R.id.gif_loading);
        lottieAnimationView.setAnimation("videoeditloader.json");
        lottieAnimationView.playAnimation();
        lottieAnimationView.loop(true);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        initNativeAdvanceAds(pd);
//        nativeAdLayout = (NativeAdLayout) pd.findViewById(R.id.native_ad_container);

        //nativead
//        loadNativeAd();

        if (Utility.idLoad) {
//            if (Utils.AdmobFacebook == 1) {
//                nat();
//            } else {
//                loadNativeAd();
//            }
        }
    }

    public void showDialog() {
        if (pd != null)
            pd.show();
    }

    public void dismissDialog() {
        if (pd != null)
            pd.dismiss();
    }

//    public void nat() {
//        AdLoader.Builder builder = new AdLoader.Builder(mContext, mContext.getResources().getString(R.string.native_ad_unit_id));
//        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
//            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
//                frameLayout.setVisibility(View.VISIBLE);
//                if (nativeAdmob != null) {
//                    nativeAdmob.destroy();
//                }
//                nativeAdmob = unifiedNativeAd;
//                UnifiedNativeAdView unifiedNativeAdView = (UnifiedNativeAdView) getLayoutInflater().inflate(R.layout.native_ad, null);
//                populateUnifiedNativeAdView(unifiedNativeAd, unifiedNativeAdView);
//                frameLayout.removeAllViews();
//                frameLayout.addView(unifiedNativeAdView);
//            }
//        });
//        builder.withAdListener(new AdListener() {
//            public void onAdFailedToLoad(int i) {
//            }
//        }).build().loadAd(new AdRequest.Builder().build());
//    }
//
//    public void populateUnifiedNativeAdView(UnifiedNativeAd unifiedNativeAd, UnifiedNativeAdView unifiedNativeAdView) {
//        unifiedNativeAdView.setMediaView((MediaView) unifiedNativeAdView.findViewById(R.id.ad_media));
//        unifiedNativeAdView.setHeadlineView(unifiedNativeAdView.findViewById(R.id.ad_headline));
//        unifiedNativeAdView.setBodyView(unifiedNativeAdView.findViewById(R.id.ad_body));
//        unifiedNativeAdView.setCallToActionView(unifiedNativeAdView.findViewById(R.id.ad_call_to_action));
//        unifiedNativeAdView.setIconView(unifiedNativeAdView.findViewById(R.id.ad_app_icon));
//        unifiedNativeAdView.setStarRatingView(unifiedNativeAdView.findViewById(R.id.ad_stars));
//        unifiedNativeAdView.setAdvertiserView(unifiedNativeAdView.findViewById(R.id.ad_advertiser));
//        ((TextView) unifiedNativeAdView.getHeadlineView()).setText(unifiedNativeAd.getHeadline());
//        unifiedNativeAdView.setNativeAd(unifiedNativeAd);
//    }


//    NativeAdLayout nativeAdLayout;
//
//    private void loadNativeAd() {
//        final com.facebook.ads.NativeAd nativeAd = new NativeAd(mContext, mContext.getString(R.string.fb_native_id));
//        nativeAd.setAdListener(new NativeAdListener() {
//            public void onAdClicked(Ad ad) {
//            }
//
//            public void onError(Ad ad, AdError adError) {
//            }
//
//            public void onLoggingImpression(Ad ad) {
//            }
//
//            public void onMediaDownloaded(Ad ad) {
//            }
//
//            public void onAdLoaded(Ad ad) {
//                inflateAd(nativeAd);
//            }
//        });
//        nativeAd.loadAd();
//    }
//
//    public void inflateAd(com.facebook.ads.NativeAd nativeAd2) {
//        nativeAd2.unregisterView();
//        int i = 0;
//        LinearLayout adView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.fbnative_ad, nativeAdLayout, false);
//        nativeAdLayout.addView(adView);
//        LinearLayout linearLayout = (LinearLayout) adView.findViewById(R.id.ad_choices_container);
//        AdOptionsView adOptionsView = new AdOptionsView(mContext, nativeAd2, nativeAdLayout);
//        linearLayout.removeAllViews();
//        linearLayout.addView(adOptionsView, 0);
//        AdIconView adIconView = (AdIconView) adView.findViewById(R.id.native_ad_icon);
//        TextView textView = (TextView) adView.findViewById(R.id.native_ad_title);
//        com.facebook.ads.MediaView mediaView = (com.facebook.ads.MediaView) adView.findViewById(R.id.native_ad_media);
//        TextView textView2 = (TextView) adView.findViewById(R.id.native_ad_sponsored_label);
//        Button button = (Button) adView.findViewById(R.id.native_ad_call_to_action);
//        textView.setText(nativeAd2.getAdvertiserName());
//        ((TextView) adView.findViewById(R.id.native_ad_body)).setText(nativeAd2.getAdBodyText());
//        ((TextView) adView.findViewById(R.id.native_ad_social_context)).setText(nativeAd2.getAdSocialContext());
//        if (!nativeAd2.hasCallToAction()) {
//            i = 4;
//        }
//        button.setVisibility(i);
//        button.setText(nativeAd2.getAdCallToAction());
//        textView2.setText(nativeAd2.getSponsoredTranslation());
//        ArrayList arrayList = new ArrayList();
//        arrayList.add(textView);
//        arrayList.add(button);
//        nativeAd2.registerViewForInteraction((View) adView, mediaView, (com.facebook.ads.MediaView) adIconView, (List<View>) arrayList);
//    }

    private void initNativeAdvanceAds(Dialog view){

        flNativeAds=(FrameLayout)view.findViewById(R.id.flNativeAds);
        flNativeAds.setVisibility(View.GONE);
//        MobileAds.initialize(this,
//                getString(R.string.admob_app_id));


        nativeAdView = (UnifiedNativeAdView) view.findViewById(R.id.ad_view);

        // The MediaView will display a video asset if one is present in the ad, and the
        // first image asset otherwise.
        nativeAdView.setMediaView((com.google.android.gms.ads.formats.MediaView) nativeAdView.findViewById(R.id.ad_media));

        // Register the view used for each individual asset.
        nativeAdView.setHeadlineView(nativeAdView.findViewById(R.id.ad_headline));
        nativeAdView.setBodyView(nativeAdView.findViewById(R.id.ad_body));
        nativeAdView.setCallToActionView(nativeAdView.findViewById(R.id.ad_call_to_action));
        nativeAdView.setIconView(nativeAdView.findViewById(R.id.ad_icon));
//        nativeAdView.setPriceView(nativeAdView.view.findViewById(R.id.ad_price));
//        nativeAdView.setStarRatingView(nativeAdView.view.findViewById(R.id.ad_stars));
//        nativeAdView.setStoreView(nativeAdView.view.findViewById(R.id.ad_store));
//        nativeAdView.setAdvertiserView(nativeAdView.view.findViewById(R.id.ad_advertiser));
        loadNativeAds();
    }
    private void populateNativeAdView(UnifiedNativeAd nativeAd,
                                      UnifiedNativeAdView adView) {

        VideoController vc = nativeAd.getVideoController();


        vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            public void onVideoEnd() {

                super.onVideoEnd();
            }
        });

        // Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        com.google.android.gms.ads.formats.NativeAd.Image icon = nativeAd.getIcon();

        if (icon == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }


        adView.setNativeAd(nativeAd);
    }
    private FrameLayout flNativeAds;
    private void loadNativeAds() {
        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(false)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        AdLoader.Builder builder = new AdLoader.Builder(mContext, Utility.NativeID);
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // A native ad loaded successfully, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
//						mNativeAds.add(unifiedNativeAd);
                        if (!adLoader.isLoading()) {
                            flNativeAds.setVisibility(View.VISIBLE);
                            populateNativeAdView(unifiedNativeAd,nativeAdView);
                        }
                    }
                }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                + " load another.");
                        if (!adLoader.isLoading()) {
                        }
                    }
                }).withNativeAdOptions(adOptions).build();

        // Load the Native ads.
        adLoader.loadAd( new AdRequest.Builder().build());
    }

}
