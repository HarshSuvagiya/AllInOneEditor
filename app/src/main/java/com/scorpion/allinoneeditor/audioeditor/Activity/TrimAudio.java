package com.scorpion.allinoneeditor.audioeditor.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore.Audio.Media;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.scorpion.allinoneeditor.AdHelper;
import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.Utility;
import com.scorpion.allinoneeditor.audioeditor.SoundFiles.CheapSoundFile;
import com.scorpion.allinoneeditor.audioeditor.ViewClasses.MarkerView;
import com.scorpion.allinoneeditor.audioeditor.ViewClasses.SeekBarTest;
import com.scorpion.allinoneeditor.audioeditor.ViewClasses.SongMetadataReader;
import com.scorpion.allinoneeditor.audioeditor.ViewClasses.WaveformView;
import com.scorpion.allinoneeditor.audioeditor.model.MusicModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TrimAudio extends Activity implements MarkerView.MarkerListener, WaveformView.WaveformListener {

    private boolean isFromItemClick = false;
    boolean isPlaying = false;
    private String mArtist;
    private boolean mCanSeekAccurately;
    private float mDensity;
    private MarkerView mEndMarker;
    private int mEndPos;
    private TextView mEndText;
    private boolean mEndVisible;
    private String mExtension;
    private ImageView mFfwdButton;

    private OnClickListener mFfwdListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mIsPlaying) {
                int newPos = mPlayer.getCurrentPosition() + 5000;
                if (newPos > mPlayEndMsec) {
                    newPos = mPlayEndMsec;
                }
                mPlayer.seekTo(newPos);
                return;
            }
            mEndMarker.requestFocus();
            markerFocus(mEndMarker);
        }
    };
    private File mFile;
    private String mFilename = "record";
    private int mFlingVelocity;
    private Handler mHandler;
    private boolean mIsPlaying;
    private boolean mKeyDown;
    private int mLastDisplayedEndPos;
    private int mLastDisplayedStartPos;
    private boolean mLoadingKeepGoing;
    private long mLoadingLastUpdateTime;
    private OnClickListener mMarkEndListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mIsPlaying) {
                mEndPos = mWaveformView.millisecsToPixels(mPlayer.getCurrentPosition() + mPlayStartOffset);
                updateDisplay();
                handlePause();
            }
        }
    };
    private OnClickListener mMarkStartListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mIsPlaying) {
                mStartPos = mWaveformView.millisecsToPixels(mPlayer.getCurrentPosition() + mPlayStartOffset);
                updateDisplay();
            }
        }
    };
    private int mMarkerBottomOffset;
    private int mMarkerLeftInset;
    private int mMarkerRightInset;
    private int mMarkerTopOffset;
    private int mMaxPos;
    //    private ArrayList<MusicModel> mMusicDatas;
    private int mOffset;
    private int mOffsetGoal;
    private ImageView mPlayButton;
    private int mPlayEndMsec;
    private OnClickListener mPlayListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            onPlay(mStartPos);
        }
    };
    private int mPlayStartMsec;
    private int mPlayStartOffset;
    private MediaPlayer mPlayer;
    private ProgressDialog mProgressDialog;
    private String mRecordingFilename;
    private Uri mRecordingUri;
    private ImageView mRewindButton;
    private OnClickListener mRewindListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mIsPlaying) {
                int newPos = mPlayer.getCurrentPosition() + -5000;
                if (newPos < mPlayStartMsec) {
                    newPos = mPlayStartMsec;
                }
                mPlayer.seekTo(newPos);
                return;
            }
            mStartMarker.requestFocus();
            markerFocus(mStartMarker);
        }
    };
    private OnClickListener mSaveListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            onSave();
        }
    };
    private CheapSoundFile mSoundFile;
    private MarkerView mStartMarker;
    private int mStartPos;
    private TextView mStartText;
    private boolean mStartVisible;
    ImageView txtMusicDone;
    ImageView back;
    RelativeLayout layoutToolbar;
    TextView toolbarTitle;
    //    Typeface typeface;
    public static String mpath;

    private TextWatcher mTextWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            if (mStartText.hasFocus()) {
                try {
                    mStartPos = mWaveformView.secondsToPixels(Double.parseDouble(mStartText.getText().toString()));
                    updateDisplay();
                } catch (NumberFormatException e) {
                }
            }
            if (mEndText.hasFocus()) {
                try {
                    mEndPos = mWaveformView.secondsToPixels(Double.parseDouble(mEndText.getText().toString()));
                    updateDisplay();
                } catch (NumberFormatException e2) {
                }
            }
        }
    };

    private Runnable mTimerRunnable = new Runnable() {
        @Override
        public void run() {
            if (!(mStartPos == mLastDisplayedStartPos || mStartText.hasFocus())) {
                mStartText.setText(formatTime(mStartPos));
                mLastDisplayedStartPos = mStartPos;
            }
            if (!(mEndPos == mLastDisplayedEndPos || mEndText.hasFocus())) {
                mEndText.setText(formatTime(mEndPos));
                mLastDisplayedEndPos = mEndPos;
            }
            mHandler.postDelayed(mTimerRunnable, 100);
        }
    };
    private String mTitle;
    private boolean mTouchDragging;
    private int mTouchInitialEndPos;
    private int mTouchInitialOffset;
    private int mTouchInitialStartPos;
    private float mTouchStart;
    private long mWaveformTouchStartMsec;
    private WaveformView mWaveformView;
    private int mWidth;

    private MusicModel selectedMusicData;
    private Toolbar toolbar;

    ProgressDialog pDialog;

    public View onCreateView(View view, String str, Context context, AttributeSet attributeSet) {
        return super.onCreateView(view, str, context, attributeSet);
    }

    public View onCreateView(String str, Context context, AttributeSet attributeSet) {
        return super.onCreateView(str, context, attributeSet);
    }

    @SuppressLint("NewApi")
    public static String formatTimeUnit(long millis) throws ParseException {
        String formatted = String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                        .toMinutes(millis)));
        return formatted;
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        mRecordingFilename = null;
        mRecordingUri = null;
        mPlayer = null;
        mIsPlaying = false;
        mSoundFile = null;
        mKeyDown = false;
        mHandler = new Handler();
        loadGui();
        SetLayout();
        mHandler.postDelayed(mTimerRunnable, 100);
    }

    private boolean isAudioFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        return path.endsWith(".mp3");
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFromFile() {
        mFile = new File(mFilename);
        mExtension = getExtensionFromFilename(mFilename);
        SongMetadataReader metadataReader = new SongMetadataReader(this, mFilename);
        mTitle = metadataReader.mTitle;
        mArtist = metadataReader.mArtist;
        String titleLabel = mTitle;
        if (mArtist != null && mArtist.length() > 0) {
            titleLabel = new StringBuilder(String.valueOf(titleLabel)).append(" - ").append(mArtist).toString();
        }
        setTitle(titleLabel);
        mLoadingLastUpdateTime = System.currentTimeMillis();
        mLoadingKeepGoing = true;
        mProgressDialog = new ProgressDialog(TrimAudio.this);
        mProgressDialog.setProgressStyle(1);
        mProgressDialog.setTitle("Loading...");
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                mLoadingKeepGoing = false;
            }
        });
        try {
            mProgressDialog.show();
        } catch (WindowManager.BadTokenException e) {

        }
        final CheapSoundFile.ProgressListener listener = new CheapSoundFile.ProgressListener() {
            public boolean reportProgress(double fractionComplete) {
                long now = System.currentTimeMillis();
                if (now - mLoadingLastUpdateTime > 100) {
                    mProgressDialog.setProgress((int) (((double) mProgressDialog.getMax()) * fractionComplete));
                    mLoadingLastUpdateTime = now;
                }
                return mLoadingKeepGoing;
            }
        };
        mCanSeekAccurately = false;
        new Thread() {
            public void run() {
                mCanSeekAccurately = SeekBarTest.CanSeekAccurately(getPreferences(0));
                System.out.println("Seek test done_change, creating media player.");
                try {
                    MediaPlayer player = new MediaPlayer();
                    player.setDataSource(mFile.getAbsolutePath());
                    player.setAudioStreamType(3);
                    player.prepare();
                    mPlayer = player;
                } catch (final IOException e) {
                    mHandler.post(new Runnable() {
                        public void run() {
                            handleFatalError("ReadError", getResources().getText(R.string.read_error), e);
                        }
                    });
                }
            }
        }.start();
        new Thread() {

            class C06003 implements Runnable {
                C06003() {
                }

                public void run() {
                    finishOpeningSoundFile();
                }
            }

            public void run() {
                try {
                    mSoundFile = CheapSoundFile.create(mFile.getAbsolutePath(), listener);
                    if (mSoundFile == null) {
                        String err;
                        mProgressDialog.dismiss();
                        String[] components = mFile.getName().toLowerCase().split("\\.");
                        if (components.length < 2) {
                            err = getResources().getString(R.string.no_extension_error);
                        } else {
                            err = new StringBuilder(String.valueOf(getResources().getString(R.string.bad_extension_error))).append(" ").append(components[components.length - 1]).toString();
                        }
                        final String finalErr = err;
                        mHandler.post(new Runnable() {
                            public void run() {
                                handleFatalError("UnsupportedExtension", finalErr, new Exception());
                            }
                        });
                        return;
                    }
                    mProgressDialog.dismiss();
                    if (mLoadingKeepGoing) {
                        mHandler.post(new C06003());
                        return;
                    }
                    finish();
                } catch (final Exception e) {
                    mProgressDialog.dismiss();
                    e.printStackTrace();
                    mHandler.post(new Runnable() {
                        public void run() {
                            handleFatalError("ReadError", getResources().getText(R.string.read_error), e);
                        }
                    });
                }
            }
        }.start();
    }

    protected void onDestroy() {
//        if (adView != null) {
//            adView.destroy();
//        }
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        mPlayer = null;
        if (mRecordingFilename != null) {
            try {
                if (!new File(mRecordingFilename).delete()) {
                    showFinalAlert(new Exception(), (int) R.string.delete_tmp_error);
                }
                getContentResolver().delete(mRecordingUri, null, null);
            } catch (Exception e) {
                showFinalAlert(e, (int) R.string.delete_tmp_error);
            }
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 62) {
            return super.onKeyDown(keyCode, event);
        }
        onPlay(mStartPos);
        return true;
    }

    public void waveformDraw() {
        mWidth = mWaveformView.getMeasuredWidth();
        if (mOffsetGoal != mOffset && !mKeyDown) {
            updateDisplay();
        } else if (mIsPlaying) {
            updateDisplay();
        } else if (mFlingVelocity != 0) {
            updateDisplay();
        }
    }

    public void waveformTouchStart(float x) {
        mTouchDragging = true;
        mTouchStart = x;
        mTouchInitialOffset = mOffset;
        mFlingVelocity = 0;
        mWaveformTouchStartMsec = System.currentTimeMillis();
    }

    public void waveformTouchMove(float x) {
        mOffset = trap((int) (((float) mTouchInitialOffset) + (mTouchStart - x)));
        updateDisplay();
    }

    public void waveformTouchEnd() {
        mTouchDragging = false;
        mOffsetGoal = mOffset;
        if (System.currentTimeMillis() - mWaveformTouchStartMsec >= 300) {
            return;
        }
        if (mIsPlaying) {
            int seekMsec = mWaveformView.pixelsToMillisecs((int) (mTouchStart + ((float) mOffset)));
            if (seekMsec < mPlayStartMsec || seekMsec >= mPlayEndMsec) {
                handlePause();
                return;
            } else {
                mPlayer.seekTo(seekMsec - mPlayStartOffset);
                return;
            }
        }
        onPlay((int) (mTouchStart + ((float) mOffset)));
    }

    public void waveformFling(float vx) {
        mTouchDragging = false;
        mOffsetGoal = mOffset;
        mFlingVelocity = (int) (-vx);
        updateDisplay();
    }

    public void markerDraw() {
    }

    public void markerTouchStart(MarkerView marker, float x) {
        mTouchDragging = true;
        mTouchStart = x;
        mTouchInitialStartPos = mStartPos;
        mTouchInitialEndPos = mEndPos;
    }

    public void markerTouchMove(MarkerView marker, float x) {
        float delta = x - mTouchStart;
        if (marker == mStartMarker) {
            mStartPos = trap((int) (((float) mTouchInitialStartPos) + delta));
            mEndPos = trap((int) (((float) mTouchInitialEndPos) + delta));
        } else {
            mEndPos = trap((int) (((float) mTouchInitialEndPos) + delta));
            if (mEndPos < mStartPos) {
                mEndPos = mStartPos;
            }
        }
        updateDisplay();
    }

    public void markerTouchEnd(MarkerView marker) {
        mTouchDragging = false;
        if (marker == mStartMarker) {
            setOffsetGoalStart();
        } else {
            setOffsetGoalEnd();
        }
    }

    public void markerLeft(MarkerView marker, int velocity) {
        mKeyDown = true;
        if (marker == mStartMarker) {
            int saveStart = mStartPos;
            mStartPos = trap(mStartPos - velocity);
            mEndPos = trap(mEndPos - (saveStart - mStartPos));
            setOffsetGoalStart();
        }
        if (marker == mEndMarker) {
            if (mEndPos == mStartPos) {
                mStartPos = trap(mStartPos - velocity);
                mEndPos = mStartPos;
            } else {
                mEndPos = trap(mEndPos - velocity);
            }
            setOffsetGoalEnd();
        }
        updateDisplay();
    }

    public void markerRight(MarkerView marker, int velocity) {
        mKeyDown = true;
        if (marker == mStartMarker) {
            int saveStart = mStartPos;
            mStartPos += velocity;
            if (mStartPos > mMaxPos) {
                mStartPos = mMaxPos;
            }
            mEndPos += mStartPos - saveStart;
            if (mEndPos > mMaxPos) {
                mEndPos = mMaxPos;
            }
            setOffsetGoalStart();
        }
        if (marker == mEndMarker) {
            mEndPos += velocity;
            if (mEndPos > mMaxPos) {
                mEndPos = mMaxPos;
            }
            setOffsetGoalEnd();
        }
        updateDisplay();
    }

    public void markerEnter(MarkerView marker) {
    }

    public void markerKeyUp() {
        mKeyDown = false;
        updateDisplay();
    }

    public void markerFocus(MarkerView marker) {
        mKeyDown = false;
        if (marker == mStartMarker) {
            setOffsetGoalStartNoUpdate();
        } else {
            setOffsetGoalEndNoUpdate();
        }
        mHandler.postDelayed(new Runnable() {
            public void run() {
                updateDisplay();
            }
        }, 100);
    }

    public UnifiedNativeAd nativeAdmob;

    private void loadGui() {
        setContentView((int) R.layout.trimaudio);
        frameLayout = (FrameLayout) findViewById(R.id.id_native_ad);
        if (Utility.idLoad) {
            nat();
        }

        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        layoutToolbar = (RelativeLayout) findViewById(R.id.layoutToolbar);
        txtMusicDone = (ImageView) findViewById(R.id.txtMusicDone);
        back = (ImageView) findViewById(R.id.ivBtnBack);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mDensity = metrics.density;
        mMarkerLeftInset = (int) (46.0f * mDensity);
        mMarkerRightInset = (int) (48.0f * mDensity);
        mMarkerTopOffset = (int) (mDensity * 10.0f);
        mMarkerBottomOffset = (int) (mDensity * 10.0f);
        mStartText = (TextView) findViewById(R.id.starttext);
        mStartText.addTextChangedListener(mTextWatcher);
        mEndText = (TextView) findViewById(R.id.endtext);
        mEndText.addTextChangedListener(mTextWatcher);
        mPlayButton = (ImageView) findViewById(R.id.play);
        mPlayButton.setOnClickListener(mPlayListener);
        mRewindButton = (ImageView) findViewById(R.id.rew);
        mRewindButton.setOnClickListener(mRewindListener);
        mFfwdButton = (ImageView) findViewById(R.id.ffwd);
        mFfwdButton.setOnClickListener(mFfwdListener);
        enableDisableButtons();
        mWaveformView = (WaveformView) findViewById(R.id.waveform);
        mWaveformView.setListener(this);
        mMaxPos = 0;
        mLastDisplayedStartPos = -1;
        mLastDisplayedEndPos = -1;
        if (mSoundFile != null) {
            mWaveformView.setSoundFile(mSoundFile);
            mWaveformView.recomputeHeights(mDensity);
            mMaxPos = mWaveformView.maxPos();
        }
        mStartMarker = (MarkerView) findViewById(R.id.startmarker);
        mStartMarker.setListener(this);
        mStartMarker.setAlpha(255);
        mStartMarker.setFocusable(true);
        mStartMarker.setFocusableInTouchMode(true);
        mStartVisible = true;
        mEndMarker = (MarkerView) findViewById(R.id.endmarker);
        mEndMarker.setListener(this);
        mEndMarker.setAlpha(255);
        mEndMarker.setFocusable(true);
        mEndMarker.setFocusableInTouchMode(true);
        mEndVisible = true;
        updateDisplay();

//        mStartText.setTypeface(typeface);
//        mEndText.setTypeface(typeface);
        Intent intent = getIntent();
        String path = intent.getStringExtra("aud_path");
        int pos = intent.getIntExtra("pos", 0);
        mMusicDatas = getMusicFiles();
        for (int i = 0; i < mMusicDatas.size(); i++) {
            if (path.equalsIgnoreCase(mMusicDatas.get(i).getTrack_data())) {
                selectedMusicData = (MusicModel) mMusicDatas.get(i);
                mFilename = selectedMusicData.getTrack_data();
                loadFromFile();
            }
        }


        txtMusicDone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onSave();
//                Applicationclass.getInstance().setMusicData(selectedMusicData);
            }
        });
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    FrameLayout frameLayout;

    public void nat() {
        AdLoader.Builder builder = new AdLoader.Builder((Context) this, getResources().getString(R.string.native_ad_unit_id));
        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                frameLayout.setVisibility(View.VISIBLE);
                if (nativeAdmob != null) {
                    nativeAdmob.destroy();
                }
                nativeAdmob = unifiedNativeAd;
                UnifiedNativeAdView unifiedNativeAdView = (UnifiedNativeAdView) getLayoutInflater().inflate(R.layout.native_ad, null);
                populateUnifiedNativeAdView(unifiedNativeAd, unifiedNativeAdView);
                frameLayout.removeAllViews();
                frameLayout.addView(unifiedNativeAdView);
            }
        });
        builder.withAdListener(new AdListener() {
            public void onAdFailedToLoad(int i) {
            }
        }).build().loadAd(new AdRequest.Builder().build());
    }

    public void populateUnifiedNativeAdView(UnifiedNativeAd unifiedNativeAd, UnifiedNativeAdView unifiedNativeAdView) {
        unifiedNativeAdView.setMediaView((MediaView) unifiedNativeAdView.findViewById(R.id.ad_media));
        unifiedNativeAdView.setHeadlineView(unifiedNativeAdView.findViewById(R.id.ad_headline));
        unifiedNativeAdView.setBodyView(unifiedNativeAdView.findViewById(R.id.ad_body));
        unifiedNativeAdView.setCallToActionView(unifiedNativeAdView.findViewById(R.id.ad_call_to_action));
        unifiedNativeAdView.setIconView(unifiedNativeAdView.findViewById(R.id.ad_app_icon));
        unifiedNativeAdView.setStarRatingView(unifiedNativeAdView.findViewById(R.id.ad_stars));
        unifiedNativeAdView.setAdvertiserView(unifiedNativeAdView.findViewById(R.id.ad_advertiser));
        ((TextView) unifiedNativeAdView.getHeadlineView()).setText(unifiedNativeAd.getHeadline());
        unifiedNativeAdView.setNativeAd(unifiedNativeAd);
    }

    private ArrayList<MusicModel> mMusicDatas;

    private ArrayList<MusicModel> getMusicFiles() {
        ArrayList<MusicModel> mMusicDatas = new ArrayList();
        Cursor mCursor = getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "title", "_data", "_display_name", "duration"}, "is_music != 0", null, "title ASC");
        int trackId = mCursor.getColumnIndex("_id");
        int trackTitle = mCursor.getColumnIndex("title");
        int trackDisplayName = mCursor.getColumnIndex("_display_name");
        int trackData = mCursor.getColumnIndex("_data");
        int trackDuration = mCursor.getColumnIndex("duration");
        while (mCursor.moveToNext()) {
            String path = mCursor.getString(trackData);
            if (isAudioFile(path)) {
                MusicModel musicData = new MusicModel();
                musicData.track_Id = mCursor.getLong(trackId);
                musicData.track_Title = mCursor.getString(trackTitle);
                musicData.track_data = path;
                musicData.track_duration = mCursor.getLong(trackDuration);
                musicData.track_displayName = mCursor.getString(trackDisplayName);
                mMusicDatas.add(musicData);

            }
        }
        return mMusicDatas;
    }


    private void finishOpeningSoundFile() {
        mWaveformView.setSoundFile(mSoundFile);
        mWaveformView.recomputeHeights(mDensity);
        mMaxPos = mWaveformView.maxPos();
        mLastDisplayedStartPos = -1;
        mLastDisplayedEndPos = -1;
        mTouchDragging = false;
        mOffset = 0;
        mOffsetGoal = 0;
        mFlingVelocity = 0;
        resetPositions();
        if (mEndPos > mMaxPos) {
            mEndPos = mMaxPos;
        }
        updateDisplay();
        if (isFromItemClick) {
            onPlay(mStartPos);
        }
    }

    private synchronized void updateDisplay() {
        if (mIsPlaying) {
            int now = mPlayer.getCurrentPosition() + mPlayStartOffset;
            int frames = mWaveformView.millisecsToPixels(now);
            mWaveformView.setPlayback(frames);
            setOffsetGoalNoUpdate(frames - (mWidth / 2));
            if (now >= mPlayEndMsec) {
                handlePause();
            }
        }
        if (!mTouchDragging) {
            int offsetDelta;
            if (mFlingVelocity != 0) {
                offsetDelta = mFlingVelocity / 30;
                if (mFlingVelocity > 80) {
                    mFlingVelocity -= 80;
                } else if (mFlingVelocity < -80) {
                    mFlingVelocity += 80;
                } else {
                    mFlingVelocity = 0;
                }
                mOffset += offsetDelta;
                if (mOffset + (mWidth / 2) > mMaxPos) {
                    mOffset = mMaxPos - (mWidth / 2);
                    mFlingVelocity = 0;
                }
                if (mOffset < 0) {
                    mOffset = 0;
                    mFlingVelocity = 0;
                }
                mOffsetGoal = mOffset;
            } else {
                offsetDelta = mOffsetGoal - mOffset;
                if (offsetDelta > 10) {
                    offsetDelta /= 10;
                } else if (offsetDelta > 0) {
                    offsetDelta = 1;
                } else if (offsetDelta < -10) {
                    offsetDelta /= 10;
                } else if (offsetDelta < 0) {
                    offsetDelta = -1;
                } else {
                    offsetDelta = 0;
                }
                mOffset += offsetDelta;
            }
        }
        mWaveformView.setParameters(mStartPos, mEndPos, mOffset);
        mWaveformView.invalidate();
        mStartMarker.setContentDescription(getResources().getText(R.string.start_marker) + " " + formatTime(mStartPos));
        mEndMarker.setContentDescription(getResources().getText(R.string.end_marker) + " " + formatTime(mEndPos));
        int startX = (mStartPos - mOffset) - mMarkerLeftInset;
        if (mStartMarker.getWidth() + startX < 0) {
            if (mStartVisible) {
                mStartMarker.setAlpha(0);
                mStartVisible = false;
            }
            startX = 0;
        } else if (!mStartVisible) {
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    mStartVisible = true;
                    mStartMarker.setAlpha(255);
                }
            }, 0);
        }
        int endX = ((mEndPos - mOffset) - mEndMarker.getWidth()) + mMarkerRightInset;
        if (mEndMarker.getWidth() + endX < 0) {
            if (mEndVisible) {
                mEndMarker.setAlpha(0);
                mEndVisible = false;
            }
            endX = 0;
        } else if (!mEndVisible) {
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    mEndVisible = true;
                    mEndMarker.setAlpha(255);
                }
            }, 0);
        }
        mStartMarker.setLayoutParams(new LayoutParams(-2, -2, startX, mMarkerTopOffset));
        mEndMarker.setLayoutParams(new LayoutParams(-2, -2, endX, (mWaveformView.getMeasuredHeight() - mEndMarker.getHeight()) - mMarkerBottomOffset));
    }

    private void enableDisableButtons() {
        if (mIsPlaying) {
            mPlayButton.setImageResource(R.drawable.pause);
            mPlayButton.setContentDescription(getResources().getText(R.string.stop));
            return;
        }
        mPlayButton.setImageResource(R.drawable.play);
        mPlayButton.setContentDescription(getResources().getText(R.string.play));
    }

    private void resetPositions() {
        mStartPos = mWaveformView.secondsToPixels(0.0d);
        mEndPos = mWaveformView.secondsToPixels((double) mMaxPos);
    }

    private int trap(int pos) {
        if (pos < 0) {
            return 0;
        }
        if (pos > mMaxPos) {
            return mMaxPos;
        }
        return pos;
    }

    private void setOffsetGoalStart() {
        setOffsetGoal(mStartPos - (mWidth / 2));
    }

    private void setOffsetGoalStartNoUpdate() {
        setOffsetGoalNoUpdate(mStartPos - (mWidth / 2));
    }

    private void setOffsetGoalEnd() {
        setOffsetGoal(mEndPos - (mWidth / 2));
    }

    private void setOffsetGoalEndNoUpdate() {
        setOffsetGoalNoUpdate(mEndPos - (mWidth / 2));
    }

    private void setOffsetGoal(int offset) {
        setOffsetGoalNoUpdate(offset);
        updateDisplay();
    }

    private void setOffsetGoalNoUpdate(int offset) {
        if (!mTouchDragging) {
            mOffsetGoal = offset;
            if (mOffsetGoal + (mWidth / 2) > mMaxPos) {
                mOffsetGoal = mMaxPos - (mWidth / 2);
            }
            if (mOffsetGoal < 0) {
                mOffsetGoal = 0;
            }
        }
    }

    private String formatTime(int pixels) {
        if (mWaveformView == null || !mWaveformView.isInitialized()) {
            return "";
        }
        return formatDecimal(mWaveformView.pixelsToSeconds(pixels));
    }

    private String formatDecimal(double x) {
        int xWhole = (int) x;
        int xFrac = (int) ((100.0d * (x - ((double) xWhole))) + 0.5d);
        if (xFrac >= 100) {
            xWhole++;
            xFrac -= 100;
            if (xFrac < 10) {
                xFrac *= 10;
            }
        }
        if (xFrac < 10) {
            return new StringBuilder(String.valueOf(xWhole)).append(".0").append(xFrac).toString();
        }
        return new StringBuilder(String.valueOf(xWhole)).append(".").append(xFrac).toString();
    }

    private synchronized void handlePause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        mWaveformView.setPlayback(-1);
        mIsPlaying = false;
        enableDisableButtons();
    }

    private synchronized void onPlay(int startPosition) {
        if (mIsPlaying) {
            handlePause();
        } else if (!(mPlayer == null || startPosition == -1)) {
            try {
                mPlayStartMsec = mWaveformView.pixelsToMillisecs(startPosition);
                if (startPosition < mStartPos) {
                    mPlayEndMsec = mWaveformView.pixelsToMillisecs(mStartPos);
                } else if (startPosition > mEndPos) {
                    mPlayEndMsec = mWaveformView.pixelsToMillisecs(mMaxPos);
                } else {
                    mPlayEndMsec = mWaveformView.pixelsToMillisecs(mEndPos);
                }
                mPlayStartOffset = 0;
                int startFrame = mWaveformView.secondsToFrames(((double) mPlayStartMsec) * 0.001d);
                int endFrame = mWaveformView.secondsToFrames(((double) mPlayEndMsec) * 0.001d);
                int startByte = mSoundFile.getSeekableFrameOffset(startFrame);
                int endByte = mSoundFile.getSeekableFrameOffset(endFrame);
                if (mCanSeekAccurately && startByte >= 0 && endByte >= 0) {
                    try {
                        mPlayer.reset();
                        mPlayer.setAudioStreamType(3);
                        mPlayer.setDataSource(new FileInputStream(mFile.getAbsolutePath()).getFD(), (long) startByte, (long) (endByte - startByte));
                        mPlayer.prepare();
                        mPlayStartOffset = mPlayStartMsec;
                    } catch (Exception e) {
                        System.out.println("Exception trying to play file subset");
                        mPlayer.reset();
                        mPlayer.setAudioStreamType(3);
                        mPlayer.setDataSource(mFile.getAbsolutePath());
                        mPlayer.prepare();
                        mPlayStartOffset = 0;
                    }
                }
                mPlayer.setOnCompletionListener(new OnCompletionListener() {
                    public synchronized void onCompletion(MediaPlayer arg0) {
                        handlePause();
                    }
                });
                mIsPlaying = true;
                if (mPlayStartOffset == 0) {
                    mPlayer.seekTo(mPlayStartMsec);
                }
                mPlayer.start();
                updateDisplay();
                enableDisableButtons();
            } catch (Exception e2) {
                showFinalAlert(e2, (int) R.string.play_error);
            }
        }
    }

    String name;

    private void showFinalAlert(Exception e, CharSequence message) {
        CharSequence title;
        if (e != null) {
            Log.e("", "Error: " + message);
            Log.e("", getStackTrace(e));
            title = getResources().getText(R.string.alert_title_failure);
            setResult(0, new Intent());
        } else {
            Log.i("Ringdroid", "Success: " + message);
            title = getResources().getText(R.string.alert_title_success);
        }
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
            }
        }).setCancelable(false).show();
    }

    private void showFinalAlert(Exception e, int messageResourceId) {
        showFinalAlert(e, getResources().getText(messageResourceId));
    }

    private String makeRingtoneFilename(CharSequence title, String extension) {
//        FileUtils1.TEMP_DIRECTORY_AUDIO.mkdirs();
//        File tempFile = new File(FileUtils1.TEMP_DIRECTORY_AUDIO,title + extension);
//        if (tempFile.exists()) {
//            FileUtils1.deleteFile(tempFile);
//        }
        String format = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        name = "/Audio_" + format + ".mp3";
        File file = new File(externalStorageDirectory, getString(R.string.app_name) + "/" + getString(R.string.trimaudio));
        if (!file.exists()) {
            file.mkdir();
        }

        String dstPath = file.getAbsolutePath() + name;

        return dstPath;
    }

    private void saveRingtone(CharSequence title) {
        final String outPath = makeRingtoneFilename(title, mExtension);
        if (outPath == null) {
            showFinalAlert(new Exception(), (int) R.string.no_unique_filename);
            return;
        }

        double startTime = mWaveformView.pixelsToSeconds(mStartPos);
        double endTime = mWaveformView.pixelsToSeconds(mEndPos);
        final int startFrame = mWaveformView.secondsToFrames(startTime);
        final int endFrame = mWaveformView.secondsToFrames(endTime);
        final int duration = (int) ((endTime - startTime) + 0.5d);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(0);
        mProgressDialog.setTitle(R.string.progress_dialog_saving);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        final CharSequence charSequence = title;
        //CharSequence charSequence = charSequence;
        new Thread() {

            class C10241 implements CheapSoundFile.ProgressListener {
                C10241() {
                }

                public boolean reportProgress(double frac) {
                    return true;
                }
            }

            public void run() {
                final File outFile = new File(outPath);

                try {
                    mSoundFile.WriteFile(outFile, startFrame, endFrame - startFrame);
                    CheapSoundFile.create(outPath, new C10241());
                    mProgressDialog.dismiss();

                    final String str = outPath;
                    final int i = duration;
                    mHandler.post(new Runnable() {
                        public void run() {
                            afterSavingRingtone(charSequence, str, outFile, i);
                        }
                    });
                } catch (Exception e) {
                    CharSequence errorMessage;
                    Exception e2 = e;
                    mProgressDialog.dismiss();
                    if (e2.getMessage().equals("No space left on device")) {
                        errorMessage = getResources().getText(R.string.no_space_error);
                        e2 = null;
                    } else {
                        errorMessage = getResources().getText(R.string.write_error);
                    }
                    final CharSequence finalErrorMessage = errorMessage;
                    final Exception finalException = e2;
                    mHandler.post(new Runnable() {
                        public void run() {
                            handleFatalError("WriteError", finalErrorMessage, finalException);
                        }
                    });
                }
            }
        }.start();
    }

    private void afterSavingRingtone(CharSequence title, String outPath, File outFile, int duration) {
        if (outFile.length() <= 512) {
            outFile.delete();
            new AlertDialog.Builder(this).setTitle(R.string.alert_title_failure).setMessage(R.string.too_small_error).setPositiveButton("ok", null).setCancelable(false).show();
            return;
        }
        long fileSize = outFile.length();
        String artist = getResources().getString(R.string.artist_name);
        ContentValues values = new ContentValues();
        values.put("_data", outPath);
        values.put("title", title.toString());
        values.put("_size", Long.valueOf(fileSize));
        values.put("mime_type", "audio/mpeg");
        values.put("artist", artist);
        values.put("duration", Integer.valueOf(duration));
        values.put("is_music", Boolean.valueOf(true));
        Log.e("audio", "duaration is " + duration);
        //setResult(-1, new Intent().setData(getContentResolver().insert(Media.getContentUriForPath(outPath), values)));
        selectedMusicData.track_data = outPath;
        selectedMusicData.track_duration = (long) (duration * 1000);
//       Applicationclass.getInstance().setMusicData(selectedMusicData);
        android.preference.PreferenceManager.getDefaultSharedPreferences(this).edit().putString("audiopath", outPath).apply();
//        Intent intent=new Intent();
        mpath = outPath;
//        intent.putExtra("audiopath",outPath);
//        MultipleImageEditor.audiopath = mpath;
//        SingleImageEditor.audiopath=mpath;
//        MultipleImageEditor.audiopath=mpath;
//        FBInterstitial.getInstance().displayFBInterstitial(TrimAudio.this, new FBInterstitial.FbCallback() {
//            public void callbackCall() {
        Intent intent = new Intent(TrimAudio.this, outputAudioPlayer.class);
        intent.putExtra("path", mpath);
        intent.putExtra("name", name);
        startActivity(intent);
        finish();
        AdHelper.ShowInter(TrimAudio.this);
//            }
//        });
    }

    private void handleFatalError(CharSequence errorInternalName, CharSequence errorString, Exception exception) {
        Log.i("Ringdroid", "handleFatalError");
    }

    private void onSave() {
        if (mIsPlaying) {
            handlePause();
        }
        saveRingtone("temp");
    }

    private void enableZoomButtons() {
//        mZoomInButton.setEnabled(mWaveformView.canZoomIn());
//        mZoomOutButton.setEnabled(mWaveformView.canZoomOut());
    }

    private String getStackTrace(Exception e) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        e.printStackTrace(new PrintWriter(stream, true));
        return stream.toString();
    }

    private String getExtensionFromFilename(String filename) {
        return filename.substring(filename.lastIndexOf(46), filename.length());
    }

    public void onBackPressed() {
        setResult(0);
        super.onBackPressed();
        if (isPlaying) {
            mPlayer.release();

        }
    }

    void SetLayout() {

        LinearLayout.LayoutParams paramsTopBar = new LinearLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 1080 / 1080,
                getResources().getDisplayMetrics().heightPixels * 177 / 1920);
        layoutToolbar.setLayoutParams(paramsTopBar);


        RelativeLayout.LayoutParams paramsBack = new RelativeLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 90 / 1080,
                getResources().getDisplayMetrics().heightPixels * 90 / 1920);
        paramsBack.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        paramsBack.addRule(RelativeLayout.CENTER_IN_PARENT);
        back.setLayoutParams(paramsBack);
        paramsBack.leftMargin = 32;


        RelativeLayout.LayoutParams paramsDone = new RelativeLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 90 / 1080,
                getResources().getDisplayMetrics().heightPixels * 90 / 1920);
        paramsDone.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        paramsDone.addRule(RelativeLayout.CENTER_IN_PARENT);
        txtMusicDone.setLayoutParams(paramsDone);
        paramsDone.rightMargin = 32;


        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 87 / 1080,
                getResources().getDisplayMetrics().heightPixels * 87 / 1920);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);

        mRewindButton.setLayoutParams(params);
        mFfwdButton.setLayoutParams(params);


        RelativeLayout.LayoutParams paramsnew = new RelativeLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 113 / 1080,
                getResources().getDisplayMetrics().heightPixels * 113 / 1920);
        paramsnew.addRule(RelativeLayout.CENTER_IN_PARENT);
        mPlayButton.setLayoutParams(paramsnew);


    }
}
