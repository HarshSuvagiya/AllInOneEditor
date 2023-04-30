package com.scorpion.allinoneeditor.videoeditor.activity;

import android.media.MediaPlayer;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdView;
import com.scorpion.allinoneeditor.AdHelper;
import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.Utility;
import com.scorpion.allinoneeditor.audioeditor.Activity.AmplifierAudio;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AudioPlayer extends BaseActivity {

    TextView audio_text;
    Handler handler;
    Handler mHandler;
    MediaPlayer mp;
    String path;
    String name;
    boolean pause = false;
    ImageView play;
    TextView prog;
    SeekBar seekbar;
    TextView title;
    TextView total;

    Runnable seekrunnable = new Runnable() {
        public void run() {
            try{
                if (!pause && mp != null) {
                    if (mp.isPlaying()){
                        seekbar.setMax(mp.getDuration());
                        int currentPosition = mp.getCurrentPosition();
                        seekbar.setProgress(currentPosition);
                        TextView textView = total;
                        textView.setText("" + AudioPlayer.formatTimeUnit((long) mp.getDuration()));
                        try {
                            TextView textView2 = prog;
                            textView2.setText("" + AudioPlayer.formatTimeUnit((long) currentPosition));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        mHandler.postDelayed(seekrunnable, 100);
                    }
                }
        }catch (Exception e){}}

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
        title = (TextView) findViewById(R.id.title);
        total = (TextView) findViewById(R.id.total);
        audio_text = (TextView) findViewById(R.id.audio_text);
        prog = (TextView) findViewById(R.id.prog);
        play = (ImageView) findViewById(R.id.play);
        seekbar = (SeekBar) findViewById(R.id.pos);
        handler = new Handler();
        path = getIntent().getStringExtra("path");
        name = getIntent().getStringExtra("name");
        audio_text.setText(name);
        Toolbar toolbar = findViewById(R.id.header);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FrameLayout adContainerView = findViewById(R.id.adView1);
        adView = new AdView(this);
        adView.setAdUnitId(Utility.BannerID);
        adContainerView.addView(adView);
        loadBanner();
        AdHelper.AdLoadHelper(this, adView);

        mp = new MediaPlayer();

        mHandler = new Handler();
        try {
            mp.setAudioStreamType(3);
            mp.setDataSource(getApplicationContext(), Uri.parse(path));
            mp.prepare();
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    mHandler.postDelayed(seekrunnable, 100);
                }
            });
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mediaPlayer) {
                    seekbar.setProgress(0);
                    mediaPlayer.seekTo(0);
                    play.setBackgroundResource(R.drawable.play);
                    prog.setText("00:00");
                }
            });
            seekbar.setMax(mp.getDuration());
            seekbar.setProgress(0);
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                    if (z) {
                        mp.seekTo(i);
                    }
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                    mHandler.removeCallbacks(seekrunnable);
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (!mp.isPlaying()) {
                        mp.start();
                    }
                    mHandler.removeCallbacks(seekrunnable);
                    mp.seekTo(seekBar.getProgress());
                    mHandler.postDelayed(seekrunnable, 100);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    if (mp != null)
                        if (mp.isPlaying()) {
                            play.setBackgroundResource(R.drawable.play);
                            mp.pause();
                            mHandler.removeCallbacks(seekrunnable);
                            return;
                        }
                    play.setBackgroundResource(R.drawable.pause);
                    mp.start();
                    mHandler.postDelayed(seekrunnable, 100);
                }
                catch (Exception e){}
            }
        });

    }

    public static String formatTimeUnit(long j) throws ParseException {
        return String.format("%02d:%02d", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(j)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(j) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(j)))});
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mp!=null){
            handler.removeCallbacks(seekrunnable);
            handler.removeMessages(0);
            mp.release();
        }
    }
}
