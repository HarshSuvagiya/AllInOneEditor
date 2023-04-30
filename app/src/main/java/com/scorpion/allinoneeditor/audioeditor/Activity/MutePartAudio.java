package com.scorpion.allinoneeditor.audioeditor.Activity;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.scorpion.allinoneeditor.AdHelper;
import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.ShowCreationDialog;
import com.scorpion.allinoneeditor.Utility;
import com.scorpion.allinoneeditor.audioeditor.model.MediaItem;
import com.scorpion.allinoneeditor.videoeditor.activity.AudioPlayer;
import com.scorpion.allinoneeditor.videoeditor.activity.BaseActivity;
import com.scorpion.allinoneeditor.videoeditor.utils.Utils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.scorpion.allinoneeditor.AdHelper.LoadInter;
import static com.scorpion.allinoneeditor.AdHelper.mInterstitialAd;

//import com.scorpion.allinoneeditor.FBInterstitial;

public class MutePartAudio extends BaseActivity implements SeekBar.OnSeekBarChangeListener {
    private ImageView b1,b4;
    private ImageView iv;
    private MediaPlayer mediaPlayer;
    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler = new Handler();;
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private TextView tx3;
    TextView tvSeekLeft;
    TextView tvSeekRight;
    SeekBar seekVideo;
    ImageView ivBtnPreview2;
    public static int oneTimeOnly = 0;
    String path;
    Button btnrev;
    SaveTask task;
    String dstPath;
    ShowCreationDialog creationDialog;
    TextView tvmin,tvmax;
    CrystalRangeSeekbar rangeSeekbar1;
    int min,max;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.mutepart);
        creationDialog = new ShowCreationDialog(MutePartAudio.this);
        Toolbar toolbar = findViewById(R.id.header);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FrameLayout adContainerView = findViewById(R.id.adView1);
        adView = new AdView(this);
        adView.setAdUnitId(Utility.BannerID);
        adContainerView.addView(adView);
        loadBanner();
        AdHelper.AdLoadHelper(this, adView);
        ivBtnPreview2 = (ImageView) findViewById(R.id.ivBtnPreview2);
        ivBtnPreview2.setOnClickListener(onclickPreview);
        seekVideo = (SeekBar) findViewById(R.id.seekVideo);
        seekVideo.setOnSeekBarChangeListener(this);
        tvSeekLeft = (TextView) findViewById(R.id.tvSeekLeft);
        tvSeekRight = (TextView) findViewById(R.id.tvSeekRight);
        b1 = (ImageView) findViewById(R.id.button);
        tvmin=findViewById(R.id.tvmin);
        tvmax=findViewById(R.id.tvmax);
        rangeSeekbar1=findViewById(R.id.rangeSeekbar1);

        b4 = (ImageView) findViewById(R.id.button4);
        iv = (ImageView)findViewById(R.id.imageView1);
        Intent intent = getIntent();
        path= intent.getStringExtra("aud_path");
        int pos = intent.getIntExtra("pos",0);

        tx3 = (TextView)findViewById(R.id.textView4);
        MediaItem mediaitem=new MediaItem(true);
        tx3.setText(SelectAudio.SONGS_LIST.get(pos).getTitle());

        mediaPlayer = new MediaPlayer();

        mediaPlayer = new MediaPlayer();

        mHandler = new Handler();
        try {
            mediaPlayer.setAudioStreamType(3);
            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(path));
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    mHandler.postDelayed(seekrunnable, 100);
                }
            });
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mediaPlayer) {
                    seekVideo.setProgress(0);
                    rangeSeekbar1.setMinStartValue(0);
                    mediaPlayer.seekTo(0);
                    ivBtnPreview2.setImageResource(R.drawable.play);
                    tvSeekLeft.setText("00:00");
                }
            });
            seekVideo.setMax(mediaPlayer.getDuration());
            rangeSeekbar1.setMaxValue(mediaPlayer.getDuration());
            seekVideo.setProgress(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        finalTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();

        if (oneTimeOnly == 0) {
            seekVideo.setMax((int) finalTime);
            seekVideo.setMax((int) finalTime);
            oneTimeOnly = 1;
        }

// set listener
        rangeSeekbar1.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                min=minValue.intValue()/1000;
                max=maxValue.intValue()/1000;
//                int m=min/100;
//                int n=max/100;

                tvmin.setText(getDurationString(min));
                tvmax.setText(getDurationString(max));
            }
        });

// set final value listener
        rangeSeekbar1.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue));
            }
        });

        tvSeekRight.setText(formatTimeUnit((long)finalTime)+"");

        tvSeekLeft.setText(formatTimeUnit((long)startTime)+"");

        seekVideo.setProgress((int)startTime);
        myHandler.postDelayed(UpdateSongTime,100);

         Utils.coverimg=getAlbumart(MutePartAudio.this,Utils.albumid);
        if(Utils.coverimg!=null)
        {
            iv.setImageBitmap(Utils.coverimg);
        }

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = (int)startTime;
                if((temp+forwardTime)<=finalTime){
                    startTime = startTime + forwardTime;
                    mediaPlayer.seekTo((int) startTime);
                    Toast.makeText(getApplicationContext(),"You have Jumped forward 5 seconds",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Cannot jump forward 5 seconds",Toast.LENGTH_SHORT).show();
                }
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = (int)startTime;

                if((temp-backwardTime)>0){
                    startTime = startTime - backwardTime;
                    mediaPlayer.seekTo((int) startTime);
                    Toast.makeText(getApplicationContext(),"You have Jumped backward 5 seconds",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Cannot jump backward 5 seconds",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getDurationString(int seconds) {

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        return twoDigitString(minutes) + " : " + twoDigitString(seconds);
    }

    private String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:
//                FBInterstitial.getInstance().displayFBInterstitial(MutePartAudio.this, new FBInterstitial.FbCallback() {
//                    public void callbackCall() {

                ShowInter(MutePartAudio.this);
//                    }
//                });

                if (mediaPlayer.isPlaying()) {
                    ivBtnPreview2.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                    mHandler.removeCallbacks(seekrunnable);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_slow_fast_video, menu);
        return true;
    }

    private class SaveTask extends AsyncTask<Void, Void, Void> {
        String inputFileName;
        private String outputformat;

        public void onPostExecute(Void voidR) {
        }

        public void onProgressUpdate(Void... voidArr) {
        }

        public SaveTask() {
            creationDialog.showDialog();
        }

        public Void doInBackground(Void... voidArr) {
            MutePartAudio videoConverter = MutePartAudio.this;
            videoConverter.make(path);
            return null;
        }
    }
    int count = 0;
    public void make(String str2) {
        String format = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
       final String name="/Audio_" + format + ".mp3";
        File file = new File(externalStorageDirectory, getString(R.string.app_name) + "/" + getString(R.string.mutepart));
        if (!file.exists()) {
            file.mkdir();
        }

        dstPath = file.getAbsolutePath()+name;

        String[] strArr = {"-y", "-i", str2, "-vcodec","copy","-af","volume=enable='between(t,"+min+","+max+")':volume=0",
                dstPath};
        int delay = 4000; // delay for 5 sec.
        int period = 1000; // repeat every sec.

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                // Your code
                count++;
                Log.e("countt",count+"");
            }
        }, delay, period);

        int rc = FFmpeg.execute(strArr);
        if (rc == Config.RETURN_CODE_SUCCESS){
            creationDialog.dismissDialog();
            Intent intent = new Intent(MutePartAudio.this, outputAudioPlayer.class);
            intent.putExtra("path", dstPath);
            intent.putExtra("name", name);
            startActivity(intent);
            finish();
        }

//        new CommandExecutor(this, strArr, new FfResponse() {
//            @Override
//            public void onStart() {
//                Log.e("FFMPEG", "onStart");
//            }
//
//            @Override
//            public void onSuccess() {
//
//                if(count<4) {
//                    Handler h = new Handler();
//                    h.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            creationDialog.dismissDialog();
//                            Intent intent = new Intent(MutePartAudio.this, outputAudioPlayer.class);
//                            intent.putExtra("path", dstPath);
//                            intent.putExtra("name", name);
//                            startActivity(intent);
//                            finish();
//                        }
//                    }, 3000);
//                }
//                else
//                {
//                    creationDialog.dismissDialog();
//                    Intent intent = new Intent(MutePartAudio.this, outputAudioPlayer.class);
//                    intent.putExtra("path", dstPath);
//                    intent.putExtra("name", name);
//                    startActivity(intent);
//                    finish();
//
//                }
//
//
//
//
//            }
//
//            @Override
//            public void onError(String errormsg) {
//                creationDialog.dismissDialog();
//                Log.e("FFMPEG", "onError");
//
//            }
//
//            @Override
//            public void onStop() {
//                Log.e("FFMPEG", "onStop");
//                creationDialog.dismissDialog();
//            }
//        }).execute();

    }

    public static String formatTimeUnit(long j) throws ParseException {
        return String.format("%02d:%02d", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(j)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(j) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(j)))});
    }
    View.OnClickListener onclickPreview = new View.OnClickListener() {
        public void onClick(View view) {

            if (mediaPlayer.isPlaying()) {
                ivBtnPreview2.setImageResource(R.drawable.play);
                mediaPlayer.pause();
                mHandler.removeCallbacks(seekrunnable);
                return;
            }
            ivBtnPreview2.setImageResource(R.drawable.pause);
            mediaPlayer.start();
            mHandler.postDelayed(seekrunnable, 100);

        }
    };

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            tvSeekLeft.setText(formatTimeUnit((long)startTime)+"");
            seekVideo.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        if (z) {
            mediaPlayer.seekTo(i);
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mediaPlayer!=null) {
            ivBtnPreview2.setImageResource(R.drawable.pause);
            mediaPlayer.start();
            mHandler.postDelayed(seekrunnable, 100);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mediaPlayer!=null)
        {
            if (mediaPlayer.isPlaying()) {
                ivBtnPreview2.setImageResource(R.drawable.play);
                mediaPlayer.pause();
                mHandler.removeCallbacks(seekrunnable);
                return;
            }
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        mHandler.removeCallbacks(seekrunnable);
        mediaPlayer.seekTo(seekBar.getProgress());
        mHandler.postDelayed(seekrunnable, 100);
    }

    public Bitmap getAlbumart(Context context, Long album_id) {
        Bitmap albumArtBitMap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {

            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

            ParcelFileDescriptor pfd = context.getContentResolver()
                    .openFileDescriptor(uri, "r");

            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                albumArtBitMap = BitmapFactory.decodeFileDescriptor(fd, null,
                        options);
                pfd = null;
                fd = null;
            }
        } catch (Error ee) {
        } catch (Exception e) {
        }

        if (null != albumArtBitMap) {
            return albumArtBitMap;
        }
        return null;
    }


    boolean pause = false;
    Handler mHandler;

    Runnable seekrunnable = new Runnable() {
        public void run() {
            try{
                if (!pause && mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()){
                        seekVideo.setMax(mediaPlayer.getDuration());
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        seekVideo.setProgress(currentPosition);
                        TextView textView = tvSeekRight;
                        textView.setText("" + AudioPlayer.formatTimeUnit((long) mediaPlayer.getDuration()));
                        try {
                            TextView textView2 = tvSeekLeft;
                            textView2.setText("" + AudioPlayer.formatTimeUnit((long) currentPosition));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        mHandler.postDelayed(seekrunnable, 100);
                    }
                }
            }catch (Exception e){}}

    };

    public void ShowInter(final Activity mContext) {
        if (Utility.loadAd) {
            Utility.TimesInterAd++;
            if (Utility.TimesInterAd % Utility.tmpInter == 0) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            LoadInter(mContext);
                            task = new SaveTask();
                            task.execute(new Void[0]);
                        }

                        @Override
                        public void onAdFailedToLoad(int i) {
                            super.onAdFailedToLoad(i);
                            LoadInter(mContext);
                        }

                        @Override
                        public void onAdLeftApplication() {
                            super.onAdLeftApplication();
                        }

                        @Override
                        public void onAdOpened() {
                            super.onAdOpened();
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                        }

                        @Override
                        public void onAdClicked() {
                            super.onAdClicked();
                        }

                        @Override
                        public void onAdImpression() {
                            super.onAdImpression();
                        }
                    });
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }
            else{
                task = new SaveTask();
                task.execute(new Void[0]);
            }
        }
        else{
            task = new SaveTask();
            task.execute(new Void[0]);
        }
    }

}




