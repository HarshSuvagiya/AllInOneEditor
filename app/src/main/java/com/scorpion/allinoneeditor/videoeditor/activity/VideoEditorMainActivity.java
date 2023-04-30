package com.scorpion.allinoneeditor.videoeditor.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdView;
import com.scorpion.allinoneeditor.AdHelper;
import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.ShowCreationDialog;
import com.scorpion.allinoneeditor.Utility;
import com.scorpion.allinoneeditor.audioeditor.Activity.AmplifierAudio;
import com.scorpion.allinoneeditor.videoeditor.utils.Utils;

public class VideoEditorMainActivity extends BaseActivity {

    private FrameLayout adContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video_editor_main);

        Toolbar toolbar = findViewById(R.id.header);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FrameLayout adContainerView = findViewById(R.id.adView1);
        adView = new AdView(this);
        adView.setAdUnitId(Utility.BannerID);
        adContainerView.addView(adView);
        loadBanner();
        AdHelper.AdLoadHelper(this, adView);

        findViewById(R.id.videoAudioMix).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.position = 0;
                callActivity();
            }
        });
        findViewById(R.id.videoConverter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.position = 1;
                callActivity();
            }
        });
        findViewById(R.id.slowVideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.position = 2;
                callActivity();
            }
        });
        findViewById(R.id.fastVideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.position = 3;
                callActivity();
            }
        });
        findViewById(R.id.videoCompress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.position = 4;
                callActivity();
            }
        });
        findViewById(R.id.rotateVideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.position = 5;
                callActivity();
            }
        });
        findViewById(R.id.muteVideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.position = 6;
                callActivity();
            }
        });
        findViewById(R.id.mirrorVideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.position = 7;
                callActivity();
            }
        });
        findViewById(R.id.videoToMp3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.position = 8;
                callActivity();
            }
        });
        findViewById(R.id.videoReverse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.position = 9;
                callActivity();
            }
        });
        findViewById(R.id.videoTrim).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.position = 10;
                callActivity();
            }
        });
        findViewById(R.id.videoFade).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.position = 11;
                callActivity();
            }
        });
        findViewById(R.id.videoToGif).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.position = 12;
                callActivity();
            }
        });
        findViewById(R.id.videoFlip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.position = 13;
                callActivity();
            }
        });
        findViewById(R.id.videosplit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.position = 14;
                callActivity();
            }
        });
        findViewById(R.id.videoToimage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.position = 15;
                callActivity();
            }
        });
        findViewById(R.id.videomerge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.position = 16;
                callActivity();
            }
        });
        findViewById(R.id.myCreation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),My_Work.class));
            }
        });
    }

    public void callActivity(){
        Utils.frommain=true;
        startActivity(new Intent(getApplicationContext(),SelectVideoActivity.class));
    }

}
