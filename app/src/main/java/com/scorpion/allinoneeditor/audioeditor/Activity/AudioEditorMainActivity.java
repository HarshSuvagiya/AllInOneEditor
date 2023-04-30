package com.scorpion.allinoneeditor.audioeditor.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdView;
import com.scorpion.allinoneeditor.AdHelper;
import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.Utility;
import com.scorpion.allinoneeditor.videoeditor.activity.BaseActivity;
import com.scorpion.allinoneeditor.videoeditor.utils.Utils;

import static com.scorpion.allinoneeditor.videoeditor.utils.Utils.frommain;

public class AudioEditorMainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_audio_editor_main);
//        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
//        loadBannerFirst(AudioEditorMainActivity.this, adContainer);
        FrameLayout adContainerView = findViewById(R.id.adView1);
        adView = new AdView(this);
        adView.setAdUnitId(Utility.BannerID);
        adContainerView.addView(adView);
        loadBanner();
        AdHelper.AdLoadHelper(this, adView);
        Toolbar toolbar = findViewById(R.id.header);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
//        loadBannerFirst(AudioEditorMainActivity.this,adContainer);

        findViewById(R.id.audioConverter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.posaudio = 1;
                callActivity();
            }
        });
        findViewById(R.id.audiocompr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.posaudio = 2;
                callActivity();
            }
        });
        findViewById(R.id.fastaudio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.posaudio = 3;
                callActivity();
            }
        });
        findViewById(R.id.slowaudio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.posaudio = 4;
                callActivity();
            }
        });
        findViewById(R.id.trimaudio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.posaudio = 5;
                callActivity();
            }
        });
        findViewById(R.id.reverseaudio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.posaudio = 6;
                callActivity();
            }
        });
        findViewById(R.id.amplifieraudio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.posaudio = 7;
                callActivity();
            }
        });
        findViewById(R.id.mixaudio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.posaudio = 8;
                callActivity();
            }
        });
        findViewById(R.id.mergeaudio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.posaudio = 9;
                callActivity();
            }
        });
        findViewById(R.id.coverphoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.posaudio = 10;
                callActivity();
            }
        });
        findViewById(R.id.mutepart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.posaudio = 11;
                callActivity();
            }
        });


        findViewById(R.id.myCreation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), My_Workaudio.class));
            }
        });
    }

    public void callActivity(){
        frommain=true;
        startActivity(new Intent(getApplicationContext(), SelectAudio.class));

    }

}
