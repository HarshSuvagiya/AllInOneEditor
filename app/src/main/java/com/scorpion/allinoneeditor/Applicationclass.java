package com.scorpion.allinoneeditor;

import android.app.Application;

//import com.facebook.ads.AdSettings;
//import com.facebook.ads.AudienceNetworkAds;

public class Applicationclass extends Application {

	private static Applicationclass instance;

	@Override
	public void onCreate() {

		super.onCreate();
		instance = this;
//		AudienceNetworkAds.initialize(this);
//		AdSettings.addTestDevice("ce491a29-c7c9-4b2b-82d9-10460a9c62d3");
//		FBInterstitial.getInstance().loadFBInterstitial(this);

	}

}
