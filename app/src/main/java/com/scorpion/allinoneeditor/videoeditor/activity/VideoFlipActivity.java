package com.scorpion.allinoneeditor.videoeditor.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.scorpion.allinoneeditor.AdHelper.LoadInter;
import static com.scorpion.allinoneeditor.AdHelper.mInterstitialAd;

//import com.scorpion.allinoneeditor.FBInterstitial;

public class VideoFlipActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {

    RelativeLayout VideoViewRelative;
    BitmapDrawable bitmapDrawable;
    ImageView vFlip;
    ImageView hFlip;
    int duration = 0;
    Handler handler = new Handler();
    boolean isVgood = true;
    ImageView ivBtnPreview2;
    ImageView back;
    ShowCreationDialog creationDialog;
    int flag = 0;
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
            ivBtnPreview2.setImageResource(R.drawable.pause);
            handler.postDelayed(seekrunnable, 100);
        }
    };

    Dialog pd = null;
    RelativeLayout r222;
    SeekBar seekVideo;
    Runnable seekrunnable = new Runnable() {
        public void run() {
            if (videoview != null && videoview.isPlaying()) {
                int currentPosition = videoview.getCurrentPosition();
                seekVideo.setProgress(currentPosition);
                try {
                    TextView textView = tvSeekLeft;
                    textView.setText("" + VideoFlipActivity.formatTimeUnit((long) currentPosition));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                handler.postDelayed(seekrunnable, 100);
            }
        }
    };
    String dstPath;
    VideoFlipActivity.SaveTask task;
    TextView tvSeekLeft;
    TextView tvSeekRight;
    String vid_path;
    VideoView videoview;

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video_flip);

        Toolbar toolbar = findViewById(R.id.header);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        creationDialog = new ShowCreationDialog(this);

        FrameLayout adContainerView = findViewById(R.id.adView1);
        adView = new AdView(this);
        adView.setAdUnitId(Utility.BannerID);
        adContainerView.addView(adView);
        loadBanner();
        AdHelper.AdLoadHelper(this, adView);

        vid_path = getIntent().getStringExtra("vid_path");
        r222 = (RelativeLayout) findViewById(R.id.r222);
        VideoViewRelative = findViewById(R.id.VideoViewRelative);
        vFlip = findViewById(R.id.videoFlipVertical);
        vFlip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                flag = 0;
//                FBInterstitial.getInstance().displayFBInterstitial(VideoFlipActivity.this, new FBInterstitial.FbCallback() {
//                    public void callbackCall() {

                ShowInter(VideoFlipActivity.this);
//                    }
//                });

            }
        });

        hFlip = findViewById(R.id.videoFlipHorizontal);
        hFlip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                flag = 1;
//                FBInterstitial.getInstance().displayFBInterstitial(VideoFlipActivity.this, new FBInterstitial.FbCallback() {
//                    public void callbackCall() {
                ShowInter(VideoFlipActivity.this);
//                    }
//                });
            }
        });
        videoview = (VideoView) findViewById(R.id.vvScreen);
        videoview.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (videoview != null) {
                    if (videoview.isPlaying()) {
                        videoview.pause();
                        ivBtnPreview2.setImageResource(R.drawable.play);
                        handler.removeCallbacks(seekrunnable);
                    } else {
                        videoview.start();
                        ivBtnPreview2.setImageResource(R.drawable.pause);
                        handler.postDelayed(seekrunnable, 100);
                    }
                }
                return false;
            }
        });
        ivBtnPreview2 = (ImageView) findViewById(R.id.ivBtnPreview2);
        ivBtnPreview2.setOnClickListener(onclickPreview);
        seekVideo = (SeekBar) findViewById(R.id.seekVideo);
        seekVideo.setOnSeekBarChangeListener(this);
        tvSeekLeft = (TextView) findViewById(R.id.tvSeekLeft);
        tvSeekRight = (TextView) findViewById(R.id.tvSeekRight);
        videoview.setVideoPath(vid_path);
        bitmapDrawable = new BitmapDrawable(ThumbnailUtils.createVideoThumbnail(vid_path, 1));
        initVideoView();
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
            VideoFlipActivity VideoFlipActivity = VideoFlipActivity.this;
            if (flag == 0)
                VideoFlipActivity.make(VideoFlipActivity.vid_path);
            else
                VideoFlipActivity.make2(VideoFlipActivity.vid_path);
            return null;
        }
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
                    textView.setText("" + VideoFlipActivity.formatTimeUnit((long) duration));
                } catch (Exception unused) {
                }
                isVgood = true;
            }
        });
        videoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                VideoFlipActivity VideoFlipActivity = VideoFlipActivity.this;
                VideoFlipActivity.isVgood = false;
                Toast.makeText(VideoFlipActivity, "Video Player Supporting issue.", Toast.LENGTH_SHORT).show();
                try {
                    handler.removeCallbacks(seekrunnable);
                    return true;
                } catch (Exception e) {
                    Log.e("VideoFlipActivity", "onError: ", e);
                    return true;
                }
            }
        });
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                ivBtnPreview2.setImageResource(R.drawable.play);
                seekVideo.setProgress(0);
                tvSeekLeft.setText("00:00");
                handler.removeCallbacks(seekrunnable);
            }
        });
    }
    int count = 0;
    public void make(String str2) {
        String format = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File file = new File(externalStorageDirectory, getString(R.string.app_name) + "/" + getString(R.string.flip));
        if (!file.exists()) {
            file.mkdir();
        }
        dstPath = file.getAbsolutePath() + "/video_" + format + ".mp4";

        String[] strArr = {"-y", "-i", str2, "-vf", "vflip",dstPath};
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
            Intent intent = new Intent(VideoFlipActivity.this, VideoViewActivity.class);
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
//                            Intent intent = new Intent(VideoFlipActivity.this, VideoViewActivity.class);
//                            intent.putExtra("videourl", dstPath);
//                            startActivity(intent);
//                            finish();
//                        }
//                    }, 3000);
//                }
//                else
//                {
//                    creationDialog.dismissDialog();
//                    Intent intent = new Intent(VideoFlipActivity.this, VideoViewActivity.class);
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

    public void make2(String str2) {
        String format = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File file = new File(externalStorageDirectory, getString(R.string.app_name) + "/" + getString(R.string.flip));
        if (!file.exists()) {
            file.mkdir();
        }
        dstPath = file.getAbsolutePath() + "/video_" + format + ".mp4";

        String[] strArr = {"-y", "-i", str2, "-vf", "hflip",dstPath};
        int rc = FFmpeg.execute(strArr);
        if (rc == Config.RETURN_CODE_SUCCESS){
            creationDialog.dismissDialog();
            Intent intent = new Intent(VideoFlipActivity.this, VideoViewActivity.class);
            intent.putExtra("videourl", dstPath);
            startActivity(intent);
            finish();
        }
//        int rc = com.arthenica.mobileffmpeg.FFmpeg.execute(strArr);
//        if (rc == RETURN_CODE_SUCCESS) {
//            creationDialog.dismissDialog();
//            Intent intent = new Intent(VideoFlipActivity.this, VideoViewActivity.class);
//            intent.putExtra("videourl", dstPath);
//            startActivity(intent);
//            finish();
//            Log.i(Config.TAG, "Command execution completed successfully.");
//        } else if (rc == RETURN_CODE_CANCEL) {
//            creationDialog.dismissDialog();
//            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
//            Log.i(Config.TAG, "Command execution cancelled by user.");
//        } else {
//            creationDialog.dismissDialog();
//            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
//            Log.i(Config.TAG, String.format("Command execution failed with rc=%d and the output below.", rc));
//            Config.printLastCommandOutput(Log.INFO);
//        }

//        new CommandExecutor(this, strArr, new FfResponse() {
//            @Override
//            public void onStart() {
//                Log.e("FFMPEG", "onStart");
//            }
//
//            @Override
//            public void onSuccess() {
//                creationDialog.dismissDialog();
//                Intent intent = new Intent(VideoFlipActivity.this, VideoViewActivity.class);
//                intent.putExtra("videourl", dstPath);
//                startActivity(intent);
//                finish();
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
                            VideoFlipActivity VideoFlipActivity = VideoFlipActivity.this;
                            VideoFlipActivity.task = new VideoFlipActivity.SaveTask();
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
                VideoFlipActivity VideoFlipActivity = VideoFlipActivity.this;
                VideoFlipActivity.task = new VideoFlipActivity.SaveTask();
                task.execute(new Void[0]);
            }

        }
        else{
            VideoFlipActivity VideoFlipActivity = VideoFlipActivity.this;
            VideoFlipActivity.task = new VideoFlipActivity.SaveTask();
            task.execute(new Void[0]);
        }
    }

}
