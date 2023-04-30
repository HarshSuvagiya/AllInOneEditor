package com.scorpion.allinoneeditor.audioeditor.Activity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdView;
//import com.scorpion.allinoneeditor.FBInterstitial;
import com.scorpion.allinoneeditor.AdHelper;
import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.Utility;
import com.scorpion.allinoneeditor.videoeditor.activity.BaseActivity;
import com.scorpion.allinoneeditor.videoeditor.activity.VideoEditorMainActivity;
import com.scorpion.allinoneeditor.videoeditor.activity.VideoViewActivity;
//import com.scorpion.allinoneeditor.videoeditor.utils.AdHelper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.scorpion.allinoneeditor.videoeditor.utils.Utils.coverimg;
import static com.scorpion.allinoneeditor.videoeditor.utils.Utils.frommain;

public class outputAudioPlayer extends BaseActivity {

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
    ImageView audio_circle_image;
    Runnable seekrunnable = new Runnable() {
        public void run() {
            try{
                if (!pause && mp != null) {
                    if (mp.isPlaying()){
                        seekbar.setMax(mp.getDuration());
                        int currentPosition = mp.getCurrentPosition();
                        seekbar.setProgress(currentPosition);
                        TextView textView = total;
                        textView.setText("" + outputAudioPlayer.formatTimeUnit((long) mp.getDuration()));
                        try {
                            TextView textView2 = prog;
                            textView2.setText("" + outputAudioPlayer.formatTimeUnit((long) currentPosition));
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
        setContentView(R.layout.activity_outputaudio_player);
        title = (TextView) findViewById(R.id.title);
        total = (TextView) findViewById(R.id.total);
        audio_text = (TextView) findViewById(R.id.audio_text);
        prog = (TextView) findViewById(R.id.prog);
        play = (ImageView) findViewById(R.id.play);
        seekbar = (SeekBar) findViewById(R.id.pos);
        audio_circle_image=findViewById(R.id.audio_circle_image);

        handler = new Handler();
        path = getIntent().getStringExtra("path");
        name = getIntent().getStringExtra("name");
        audio_text.setText(name);
        Toolbar toolbar = findViewById(R.id.header);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);

        byte [] data = mmr.getEmbeddedPicture();
        //coverart is an Imageview object

        // convert the byte array to a bitmap
        if(data != null)
        {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            audio_circle_image.setImageBitmap(bitmap); //associated cover art in bitmap
        }
        else
        {
            audio_circle_image.setImageResource(R.drawable.music); //any default cover resourse folder
        }

        audio_circle_image.setAdjustViewBounds(true);
//        coverart.setLayoutParams(new LinearLayout.LayoutParams(500, 500));

//        if(coverimg!=null)
//        {
//            audio_circle_image.setImageBitmap(coverimg);
//        }

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

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (AdHelper.ratechk) {
            AdHelper.ratechk = false;
            final Dialog dialog = new Dialog(outputAudioPlayer.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            dialog.setContentView(R.layout.ratedialog);
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
            ImageView no = (ImageView) dialog.findViewById(R.id.btnno1);
            ImageView ok = (ImageView) dialog.findViewById(R.id.btnyes1);
            LinearLayout mainpop = (LinearLayout) dialog.findViewById(R.id.mainpop);

            TextView txt = (TextView) dialog.findViewById(R.id.text);

            LinearLayout.LayoutParams paramss = new LinearLayout.LayoutParams(
                    getResources().getDisplayMetrics().widthPixels * 296 / 1080,
                    getResources().getDisplayMetrics().heightPixels * 135 / 1920);
            paramss.gravity = Gravity.CENTER;
            ok.setLayoutParams(paramss);
            no.setLayoutParams(paramss);

            int w = AdHelper.getScreenWidth() - 100;
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    getResources().getDisplayMetrics().widthPixels * w / 1080,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            mainpop.setLayoutParams(params);

            ok.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
//                    try {
//                        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + getPackageName())));
//                    } catch (ActivityNotFoundException unused) {
//                        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
//                    }
                    dialog.dismiss();
                }
            });

            no.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    if(frommain) {
                        Intent intent = new Intent(outputAudioPlayer.this,
                                AudioEditorMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
//                        Intent intent = new Intent(outputAudioPlayer.this,
//                                My_Workaudio.class);
//                        startActivity(intent);
                        finish();
                    }
                    dialog.dismiss();
                }
            });

            dialog.show();

        } else {
//            FBInterstitial.getInstance().displayFBInterstitial(outputAudioPlayer.this, new FBInterstitial.FbCallback() {
//                public void callbackCall() {
                    if(frommain) {
                        Intent intent = new Intent(outputAudioPlayer.this,
                                AudioEditorMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
//                        Intent intent = new Intent(outputAudioPlayer.this,
//                                My_Workaudio.class);
//                        startActivity(intent);
                        finish();
                    }
            com.scorpion.allinoneeditor.AdHelper.ShowInter(outputAudioPlayer.this);
//                }
//            });
        }
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
