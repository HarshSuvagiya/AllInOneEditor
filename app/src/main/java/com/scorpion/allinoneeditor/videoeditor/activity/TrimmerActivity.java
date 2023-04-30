package com.scorpion.allinoneeditor.videoeditor.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.scorpion.allinoneeditor.AdHelper;
import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.ShowCreationDialog;
import com.scorpion.allinoneeditor.Utility;
import com.scorpion.allinoneeditor.videoeditor.customVideoViews.BarThumb;
import com.scorpion.allinoneeditor.videoeditor.customVideoViews.CustomRangeSeekBar;
import com.scorpion.allinoneeditor.videoeditor.customVideoViews.OnRangeSeekBarChangeListener;
import com.scorpion.allinoneeditor.videoeditor.customVideoViews.TileView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.scorpion.allinoneeditor.AdHelper.LoadInter;
import static com.scorpion.allinoneeditor.AdHelper.mInterstitialAd;

//import com.scorpion.allinoneeditor.FBInterstitial;

public class TrimmerActivity extends BaseActivity implements View.OnClickListener {

    private Button txtVideoCancel;
    private ImageView txtVideoUpload;
    private TextView txtVideoEditTitle;
    private TextView txtVideoTrimSeconds;
    private RelativeLayout rlVideoView;
    private TileView tileView;
    private CustomRangeSeekBar mCustomRangeSeekBarNew;
    private VideoView mVideoView;
    private ImageView imgPlay;
    private SeekBar seekBarVideo;
    private TextView txtVideoLength;
    ImageView back;
    private int mDuration = 0;
    private int mTimeVideo = 0;
    private int mStartPosition = 0;
    private int mEndPosition = 0;
    // set your max video trim seconds
    private int mMaxDuration = 60000;
    private Handler mHandler = new Handler();
    String end;
    String path;
    String st1;
    String start;
    SaveTask task;
    String srcFile;
    String dstFile;
    int startMin;
    int startSec;
    int endMin;
    int endSec;
    Dialog pd;
    boolean isIntermideate;
    ShowCreationDialog creationDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_trimmer);

        Toolbar toolbar = findViewById(R.id.header);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        creationDialog = new ShowCreationDialog(TrimmerActivity.this);

        FrameLayout adContainerView = findViewById(R.id.adView1);
        adView = new AdView(this);
        adView.setAdUnitId(Utility.BannerID);
        adContainerView.addView(adView);
        loadBanner();
        AdHelper.AdLoadHelper(this, adView);

        txtVideoCancel = findViewById(R.id.txtVideoCancel);
        txtVideoUpload = findViewById(R.id.txtVideoUpload);
        txtVideoEditTitle = (TextView) findViewById(R.id.txtVideoEditTitle);
        txtVideoTrimSeconds = (TextView) findViewById(R.id.txtVideoTrimSeconds);
        rlVideoView = (RelativeLayout) findViewById(R.id.llVideoView);
        tileView = (TileView) findViewById(R.id.timeLineView);
        mCustomRangeSeekBarNew = ((CustomRangeSeekBar) findViewById(R.id.timeLineBar));
        mVideoView = (VideoView) findViewById(R.id.videoView);
        imgPlay = (ImageView) findViewById(R.id.imgPlay);
        seekBarVideo = (SeekBar) findViewById(R.id.seekBarVideo);
        txtVideoLength = (TextView) findViewById(R.id.txtVideoLength);

        pd = new Dialog(TrimmerActivity.this);
        pd.setContentView(R.layout.progress_dialog);
        LottieAnimationView lottieAnimationView;
        lottieAnimationView = pd.findViewById(R.id.gif_loading);
        lottieAnimationView.setAnimation("loading.json");
        lottieAnimationView.playAnimation();
        lottieAnimationView.loop(true);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);

        if (getIntent().getExtras() != null) {
            srcFile = getIntent().getExtras().getString("vid_path");
        }
        String format = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File file = new File(externalStorageDirectory, getString(R.string.app_name) + "/" + getString(R.string.trim));
        if (!file.exists()) {
            file.mkdir();
        }
        dstFile = file.getAbsolutePath() + "/video_" + format + ".mp4";

        tileView.post(new Runnable() {
            @Override
            public void run() {
                setBitmap(Uri.parse(srcFile));
                mVideoView.setVideoURI(Uri.parse(srcFile));
            }
        });

        txtVideoCancel.setOnClickListener(this);
        txtVideoUpload.setOnClickListener(this);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                onVideoPrepared(mp);
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onVideoCompleted();
            }
        });

        // handle your range seekbar changes
        mCustomRangeSeekBarNew.addOnRangeSeekBarListener(new OnRangeSeekBarChangeListener() {
            @Override
            public void onCreate(CustomRangeSeekBar customRangeSeekBarNew, int index, float value) {
                // Do nothing
            }

            @Override
            public void onSeek(CustomRangeSeekBar customRangeSeekBarNew, int index, float value) {
                onSeekThumbs(index, value);
            }

            @Override
            public void onSeekStart(CustomRangeSeekBar customRangeSeekBarNew, int index, float value) {
                if (mVideoView != null) {
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    seekBarVideo.setProgress(0);
                    mVideoView.seekTo(mStartPosition * 1000);
                    mVideoView.pause();
                    imgPlay.setBackgroundResource(R.drawable.play);
                }
            }

            @Override
            public void onSeekStop(CustomRangeSeekBar customRangeSeekBarNew, int index, float value) {
            }
        });

        imgPlay.setOnClickListener(this);

        seekBarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mVideoView != null) {
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    seekBarVideo.setMax(mTimeVideo * 1000);
                    seekBarVideo.setProgress(0);
                    mVideoView.seekTo(mStartPosition * 1000);
                    mVideoView.pause();
                    imgPlay.setBackgroundResource(R.drawable.play);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                mVideoView.seekTo((mStartPosition * 1000) - seekBarVideo.getProgress());
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == txtVideoCancel) {
            finish();
        } else if (view == txtVideoUpload) {
//            FBInterstitial.getInstance().displayFBInterstitial(TrimmerActivity.this, new FBInterstitial.FbCallback() {
//                public void callbackCall() {
            ShowInter(TrimmerActivity.this);
//                }
//            });


        } else if (view == imgPlay) {
            if (mVideoView.isPlaying()) {
                if (mVideoView != null) {
                    mVideoView.pause();
                    imgPlay.setBackgroundResource(R.drawable.play);
                }
            } else {
                if (mVideoView != null) {
                    mVideoView.start();
                    imgPlay.setBackgroundResource(R.drawable.pause);
                    if (seekBarVideo.getProgress() == 0) {
                        txtVideoLength.setText("00:00");
                        updateProgressBar();
                    }
                }
            }
        }
    }

    private void setBitmap(Uri mVideoUri) {
        tileView.setVideo(mVideoUri);
    }

    private void onVideoPrepared(@NonNull MediaPlayer mp) {
        mDuration = mVideoView.getDuration() / 1000;
        setSeekBarPosition();
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (seekBarVideo.getProgress() >= seekBarVideo.getMax()) {
                seekBarVideo.setProgress((mVideoView.getCurrentPosition() - mStartPosition * 1000));
                txtVideoLength.setText(milliSecondsToTimer(seekBarVideo.getProgress()) + "");
                mVideoView.seekTo(mStartPosition * 1000);
                mVideoView.pause();
                seekBarVideo.setProgress(0);
                txtVideoLength.setText("00:00");
                imgPlay.setBackgroundResource(R.drawable.play);
            } else {
                seekBarVideo.setProgress((mVideoView.getCurrentPosition() - mStartPosition * 1000));
                txtVideoLength.setText(milliSecondsToTimer(seekBarVideo.getProgress()) + "");
                mHandler.postDelayed(this, 100);
            }
        }
    };

    private void setSeekBarPosition() {

        if (mDuration >= mMaxDuration) {
            mStartPosition = 0;
            mEndPosition = mMaxDuration;

            mCustomRangeSeekBarNew.setThumbValue(0, (mStartPosition * 100) / mDuration);
            mCustomRangeSeekBarNew.setThumbValue(1, (mEndPosition * 100) / mDuration);

        } else {
            mStartPosition = 0;
            mEndPosition = mDuration;
        }


        mTimeVideo = mDuration;
        mCustomRangeSeekBarNew.initMaxWidth();
        seekBarVideo.setMax(mMaxDuration * 1000);
        mVideoView.seekTo(mStartPosition * 1000);

        String mStart = mStartPosition + "";
        if (mStartPosition < 10)
            mStart = "0" + mStartPosition;

        startMin = Integer.parseInt(mStart) / 60;
        startSec = Integer.parseInt(mStart) % 60;

        String mEnd = mEndPosition + "";
        if (mEndPosition < 10)
            mEnd = "0" + mEndPosition;

        endMin = Integer.parseInt(mEnd) / 60;
        endSec = Integer.parseInt(mEnd) % 60;

        txtVideoTrimSeconds.setText(String.format(Locale.US, "%02d:%02d - %02d:%02d", startMin, startSec, endMin, endSec));
    }

    private void onVideoCompleted() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        seekBarVideo.setProgress(0);
        mVideoView.seekTo(mStartPosition * 1000);
        mVideoView.pause();
        imgPlay.setBackgroundResource(R.drawable.play);
    }

    private void onSeekThumbs(int index, float value) {
        switch (index) {
            case BarThumb.LEFT: {
                mStartPosition = (int) ((mDuration * value) / 100L);
                mVideoView.seekTo(mStartPosition * 1000);
                break;
            }
            case BarThumb.RIGHT: {
                mEndPosition = (int) ((mDuration * value) / 100L);
                break;
            }
        }
        mTimeVideo = (mEndPosition - mStartPosition);
        seekBarVideo.setMax(mTimeVideo * 1000);
        seekBarVideo.setProgress(0);
        mVideoView.seekTo(mStartPosition * 1000);

        String mStart = mStartPosition + "";
        if (mStartPosition < 10)
            mStart = "0" + mStartPosition;

        startMin = Integer.parseInt(mStart) / 60;
        startSec = Integer.parseInt(mStart) % 60;

        String mEnd = mEndPosition + "";
        if (mEndPosition < 10)
            mEnd = "0" + mEndPosition;
        endMin = Integer.parseInt(mEnd) / 60;
        endSec = Integer.parseInt(mEnd) % 60;

        txtVideoTrimSeconds.setText(String.format(Locale.US, "%02d:%02d - %02d:%02d", startMin, startSec, endMin, endSec));

    }

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString;
        String minutesString;


        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        if (minutes < 10) {
            minutesString = "0" + minutes;
        } else {
            minutesString = "" + minutes;
        }

        finalTimerString = finalTimerString + minutesString + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    private class SaveTask extends AsyncTask<Void, Void, Void> {
        public void onPostExecute(Void voidR) {
        }

        public void onProgressUpdate(Void... voidArr) {
        }

        public SaveTask() {
            creationDialog.showDialog();
        }

        public Void doInBackground(Void... voidArr) {
            TrimmerActivity trimmerActivity = TrimmerActivity.this;
            mEndPosition = mEndPosition - mStartPosition;
            trimmerActivity.make(String.valueOf(mStartPosition), String.valueOf(mEndPosition));
            return null;
        }
    }

    int count = 0;

    public void make(String str, String str2) {

        String format = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File file = new File(externalStorageDirectory, getString(R.string.app_name) + "/" + getString(R.string.trim));
        if (!file.exists()) {
            file.mkdir();
        }
        st1 = file.getAbsolutePath() + "/video_" + format + ".mp4";
        String str3 = st1;
        String str4 = srcFile;
        Log.e("START111", str);
        Log.e("START222", str2);
//        String[] strArr = {"-y", "-ss", str, "-t", str2, "-i", str4, "-async", "1", "-vcodec", "copy", "-acodec", "copy", str3};
        String[] mycmd = {"-y", "-i", srcFile, "-ss", str, "-t", str2, "-c", "copy", st1};
//        String[] mycmd = {"-y", "-ss", str, "-i", srcFile, "-to", str2, "-async", "1", "-strict", "-2", "-c", "copy", st1};

        int delay = 4000; // delay for 5 sec.
        int period = 1000; // repeat every sec.

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                // Your code
                count++;
                Log.e("countt", count + "");
            }
        }, delay, period);

        int rc = FFmpeg.execute(mycmd);
        if (rc == Config.RETURN_CODE_SUCCESS){
            creationDialog.dismissDialog();
            Intent intent = new Intent(TrimmerActivity.this, VideoViewActivity.class);
            intent.putExtra("videourl", st1);
            startActivity(intent);
            finish();
        }

//        new CommandExecutor(this, mycmd, new FfResponse() {
//            @Override
//            public void onStart() {
//                Log.e("FFMPEG", "onStart");
//            }
//
//            @Override
//            public void onSuccess() {
//
//                if (count < 4) {
//                    Handler h = new Handler();
//                    h.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            creationDialog.dismissDialog();
//                            Intent intent = new Intent(TrimmerActivity.this, VideoViewActivity.class);
//                            intent.putExtra("videourl", st1);
//                            startActivity(intent);
//                            finish();
//
//                        }
//                    }, 3000);
//                } else {
//                    creationDialog.dismissDialog();
//                    Intent intent = new Intent(TrimmerActivity.this, VideoViewActivity.class);
//                    intent.putExtra("videourl", st1);
//                    startActivity(intent);
//                    finish();
//
//                }
//            }
//
//            @Override
//            public void onError(String errormsg) {
//                creationDialog.dismissDialog();
//                Log.e("FFMPEG", errormsg);
//            }
//
//            @Override
//            public void onStop() {
//                Log.e("FFMPEG", "onStop");
//                creationDialog.dismissDialog();
//            }
//        }).execute();


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
                            TrimmerActivity trimmerActivity = TrimmerActivity.this;
                            trimmerActivity.task = new TrimmerActivity.SaveTask();
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
                TrimmerActivity trimmerActivity = TrimmerActivity.this;
                trimmerActivity.task = new TrimmerActivity.SaveTask();
                task.execute(new Void[0]);
            }
        }
        else{
            TrimmerActivity trimmerActivity = TrimmerActivity.this;
            trimmerActivity.task = new TrimmerActivity.SaveTask();
            task.execute(new Void[0]);
        }
    }


}

