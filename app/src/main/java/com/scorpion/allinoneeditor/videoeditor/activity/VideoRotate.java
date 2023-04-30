package com.scorpion.allinoneeditor.videoeditor.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.scorpion.allinoneeditor.AdHelper;
import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.ShowCreationDialog;
import com.scorpion.allinoneeditor.Utility;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.scorpion.allinoneeditor.AdHelper.LoadInter;
import static com.scorpion.allinoneeditor.AdHelper.mInterstitialAd;

//import com.scorpion.allinoneeditor.FBInterstitial;

public class VideoRotate extends BaseActivity implements SeekBar.OnSeekBarChangeListener {
    RelativeLayout VideoViewRelative;
    BitmapDrawable bitmapDrawable;
    int duration = 0;
    ImageView first;
    TextView fourth;
    Handler handler = new Handler();
    boolean isVgood = true;
    ImageView ivBtnNext;
    ImageView ivBtnPreview2;
    LinearLayout ln_bottom;
    View.OnClickListener onclickPreview = new View.OnClickListener() {
        public void onClick(View view) {
            if (videoview == null) {
                return;
            }
            if (videoview.isPlaying()) {
                videoview.pause();
                ivBtnPreview2.setImageResource(R.drawable.play);
                handler.removeCallbacks(seekrunnable);
                return;
            }
            videoview.start();
            videoview.setBackground((Drawable) null);
            ivBtnPreview2.setImageResource(R.drawable.pause);
            handler.postDelayed(seekrunnable, 100);
        }
    };
    Dialog pd = null;
    RelativeLayout r222;
    int rot_val = 90;
    ImageView second;
    SeekBar seekVideo;
    Runnable seekrunnable = new Runnable() {
        public void run() {
            if (videoview != null && videoview.isPlaying()) {
                int currentPosition = videoview.getCurrentPosition();
                seekVideo.setProgress(currentPosition);
                try {
                    TextView textView = tvSeekLeft;
                    textView.setText("" + VideoRotate.formatTimeUnit((long) currentPosition));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                handler.postDelayed(seekrunnable, 100);
            }
        }
    };
    String dstPath;
    SaveTask task;
    ImageView third;
    TextView tvSeekLeft;
    TextView tvSeekRight;
    String videoPath;
    VideoView videoview;
    ShowCreationDialog creationDialog;
    VideoRotate videoRotate;
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public static String formatTimeUnit(long j) throws ParseException {
        return String.format("%02d:%02d", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(j)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(j) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(j)))});
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video_rotate);

        Toolbar toolbar = findViewById(R.id.header);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FrameLayout adContainerView = findViewById(R.id.adView1);
        adView = new AdView(this);
        adView.setAdUnitId(Utility.BannerID);
        adContainerView.addView(adView);
        loadBanner();
        AdHelper.AdLoadHelper(this, adView);

        creationDialog = new ShowCreationDialog(this);
        videoPath = getIntent().getStringExtra("vid_path");
        videoview = (VideoView) findViewById(R.id.vvScreen);
        ivBtnPreview2 = (ImageView) findViewById(R.id.ivBtnPreview2);
        ivBtnPreview2.setOnClickListener(onclickPreview);
        first = findViewById(R.id.r_180);
        first.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                videoRotate = VideoRotate.this;
                videoRotate.rot_val = 180;
//                FBInterstitial.getInstance().displayFBInterstitial(VideoRotate.this, new FBInterstitial.FbCallback() {
//                    public void callbackCall() {
                        ShowInter(VideoRotate.this);
//                    }
//                });
            }
        });
        second = findViewById(R.id.r_90);
        second.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                videoRotate = VideoRotate.this;
                videoRotate.rot_val = 90;
//                FBInterstitial.getInstance().displayFBInterstitial(VideoRotate.this, new FBInterstitial.FbCallback() {
//                    public void callbackCall() {
                       ShowInter(VideoRotate.this);
//                    }
//                });
            }
        });
        third = findViewById(R.id.r_270);
        third.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                videoRotate = VideoRotate.this;
                videoRotate.rot_val = 270;

//                FBInterstitial.getInstance().displayFBInterstitial(VideoRotate.this, new FBInterstitial.FbCallback() {
//                    public void callbackCall() {
                ShowInter(VideoRotate.this);
//                    }
//                });

            }
        });
        seekVideo = (SeekBar) findViewById(R.id.seekVideo);
        seekVideo.setOnSeekBarChangeListener(this);
        tvSeekLeft = (TextView) findViewById(R.id.tvSeekLeft);
        tvSeekRight = (TextView) findViewById(R.id.tvSeekRight);
        videoview.setVideoPath(videoPath);
        bitmapDrawable = new BitmapDrawable(ThumbnailUtils.createVideoThumbnail(videoPath, 1));
        videoview.setBackgroundDrawable(bitmapDrawable);
        initVideoView();
        ln_bottom = (LinearLayout) findViewById(R.id.ln_bottom);
        r222 = (RelativeLayout) findViewById(R.id.r222);
        VideoViewRelative = (RelativeLayout) findViewById(R.id.VideoViewRelative);
    }

    int count = 0;
    public void make(String str2) {
        String format = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File file = new File(externalStorageDirectory, getString(R.string.app_name) + "/" + getString(R.string.rotate));
        if (!file.exists()) {
            file.mkdir();
        }
        dstPath = file.getAbsolutePath() + "/video_" + format + ".mp4";
        StringBuilder sb = new StringBuilder();
        sb.append("rotate=");
        sb.append(rot_val);
        String[] strArr = {"-y","-i", str2, "-metadata:s:v", sb.toString(), "-codec", "copy", dstPath};
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
            Intent intent = new Intent(this, VideoViewActivity.class);
            intent.putExtra("videourl", dstPath);
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
//                            Intent intent = new Intent(VideoRotate.this, VideoViewActivity.class);
//                            intent.putExtra("videourl", dstPath);
//                            startActivity(intent);
//                            finish();
//
//                        }
//                    }, 3000);
//                }
//                else
//                {
//                    creationDialog.dismissDialog();
//                    Intent intent = new Intent(VideoRotate.this, VideoViewActivity.class);
//                    intent.putExtra("videourl", dstPath);
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

    private void initVideoView() {
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoview.seekTo(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
                ivBtnPreview2.setImageResource(R.drawable.play);
                seekVideo.setProgress(0);
                tvSeekLeft.setText("00:00");
                duration = mediaPlayer.getDuration();
                seekVideo.setMax(duration);
                try {
                    TextView textView = tvSeekRight;
                    textView.setText("" + VideoRotate.formatTimeUnit((long) duration));
                } catch (Exception unused) {
                }
                isVgood = true;
            }
        });
        videoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                VideoRotate videoRotate = VideoRotate.this;
                videoRotate.isVgood = false;
                Toast.makeText(videoRotate, "Video Player Supporting issue.", Toast.LENGTH_SHORT).show();
                try {
                    handler.removeCallbacks(seekrunnable);
                    return true;
                } catch (Exception unused) {
                    return true;
                }
            }
        });
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoview.setBackground(bitmapDrawable);
                ivBtnPreview2.setImageResource(R.drawable.play);
                seekVideo.setProgress(0);
                tvSeekLeft.setText("00:00");
                handler.removeCallbacks(seekrunnable);
            }
        });
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        if (z) {
            videoview.seekTo(i);
            try {
                TextView textView = tvSeekLeft;
                textView.setText("" + formatTimeUnit((long) i));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void onPause() {
        videoview.pause();
        ivBtnPreview2.setImageResource(R.drawable.play);
        handler.removeCallbacks(seekrunnable);
        super.onPause();
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
            if (videoview.isPlaying()) {
                videoview.pause();
            }
        }

        public Void doInBackground(Void... voidArr) {
            VideoRotate videoRotate = VideoRotate.this;
            videoRotate.make(videoRotate.videoPath);
            return null;
        }
    }

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
                            videoRotate.task = new SaveTask();
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
                videoRotate.task = new SaveTask();
                task.execute(new Void[0]);
            }

        }
        else{
            videoRotate.task = new SaveTask();
            task.execute(new Void[0]);
        }
    }

}
