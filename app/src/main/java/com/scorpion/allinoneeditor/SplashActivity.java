package com.scorpion.allinoneeditor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends Activity {

    Context context;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    int failedToLoadCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        db.child("AudioVideoLab").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Utility.loadAd = Boolean.parseBoolean(snapshot.child("LoadAd").getValue().toString());
//                Utility.BannerID = snapshot.child("BannerId").getValue().toString();
//                Utility.InterID = snapshot.child("InterId").getValue().toString();
//                Utility.NativeID= snapshot.child("NativeId").getValue().toString();
//                Utility.AppOpenID= snapshot.child("AppOpenId").getValue().toString();
//                Utility.tmpInter= Integer.parseInt(snapshot.child("TimesInterAd").getValue().toString());

                Log.d("banner123",Utility.BannerID);

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        context = this;
    }

}
