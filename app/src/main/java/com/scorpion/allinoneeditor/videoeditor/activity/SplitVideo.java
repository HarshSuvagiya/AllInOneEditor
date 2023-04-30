package com.scorpion.allinoneeditor.videoeditor.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
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
import com.scorpion.allinoneeditor.videoeditor.utils.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.scorpion.allinoneeditor.AdHelper.LoadInter;
import static com.scorpion.allinoneeditor.AdHelper.mInterstitialAd;

//import com.scorpion.allinoneeditor.FBInterstitial;

public class SplitVideo extends BaseActivity implements SeekBar.OnSeekBarChangeListener {
    RelativeLayout VideoViewRelative;
    BitmapDrawable bitmapDrawable;
    ImageView bottom_line;
    int duration = 0;
    int duration1 = 0;
    EditText edt;
    TextView txt;
    float f1 = 1.0f;
    Handler handler = new Handler();
    boolean isVgood = true;
    ImageView ivBtnPreview2;
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
    SeekBar seekVideo;
    Runnable seekrunnable = new Runnable() {
        public void run() {
            if (videoview != null && videoview.isPlaying()) {
                int currentPosition = videoview.getCurrentPosition();
                seekVideo.setProgress(currentPosition);
                try {
                    TextView textView = tvSeekLeft;
                    textView.setText("" + SplitVideo.formatTimeUnit((long) currentPosition));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                handler.postDelayed(seekrunnable, 100);
            }
        }
    };
    String dstPath;
    SplitVideo.SaveTask task;
    TextView tvSeekLeft;
    TextView tvSeekRight;
    int val;
    String videoPath;
    VideoView videoview;
    ShowCreationDialog creationDialog;
    int N;

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_split_video);

        Toolbar toolbar = findViewById(R.id.header);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        creationDialog = new ShowCreationDialog(SplitVideo.this);

        FrameLayout adContainerView = findViewById(R.id.adView1);
        adView = new AdView(this);
        adView.setAdUnitId(Utility.BannerID);
        adContainerView.addView(adView);
        loadBanner();
        AdHelper.AdLoadHelper(this, adView);

        txt = (TextView) findViewById(R.id.txt);
        edt=findViewById(R.id.edt);
        r222 = (RelativeLayout) findViewById(R.id.r222);
        videoPath = getIntent().getStringExtra("vid_path");
        videoview = (VideoView) findViewById(R.id.vvScreen);
        ivBtnPreview2 = (ImageView) findViewById(R.id.ivBtnPreview2);
        ivBtnPreview2.setOnClickListener(onclickPreview);
        seekVideo = (SeekBar) findViewById(R.id.seekVideo);
        seekVideo.setOnSeekBarChangeListener(this);
        tvSeekLeft = (TextView) findViewById(R.id.tvSeekLeft);
        tvSeekRight = (TextView) findViewById(R.id.tvSeekRight);
        videoview.setVideoPath(videoPath);
        bitmapDrawable = new BitmapDrawable(ThumbnailUtils.createVideoThumbnail(videoPath, 1));
        videoview.setBackgroundDrawable(bitmapDrawable);
        VideoViewRelative = (RelativeLayout) findViewById(R.id.VideoViewRelative);
        bottom_line = (ImageView) findViewById(R.id.bottom_line);
        initVideoView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_slow_fast_video, menu);
        return true;
    }

    public void makeFastVideo() {


//        FBInterstitial.getInstance().displayFBInterstitial(SplitVideo.this, new FBInterstitial.FbCallback() {
//            public void callbackCall() {

        ShowInter(SplitVideo.this);
//            }
//        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:
                if (edt.getText().toString().length() > 0) {
                    if (Integer.parseInt(edt.getText().toString()) > 1 && Integer.parseInt(edt.getText().toString()) < duration1) {
                        N = Integer.parseInt(edt.getText().toString());
                        makeFastVideo();
                    } else {
                        Toast.makeText(getApplicationContext(), "Enter duration second between 1 to" + duration1, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Add segment Seconds...", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                duration1 = mediaPlayer.getDuration() / 1000;
                txt.setText("Enter Duration second for each segment (1-" + duration1 + ")");
                seekVideo.setMax(duration);
                try {
                    TextView textView = tvSeekRight;
                    textView.setText("" + SplitVideo.formatTimeUnit((long) duration));
                } catch (Exception unused) {
                }
                isVgood = true;
            }
        });

        videoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                SplitVideo slowVideo = SplitVideo.this;
                slowVideo.isVgood = false;
                Toast.makeText(slowVideo, "Video Player Supporting issue.", Toast.LENGTH_SHORT).show();
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

    private class SaveTask extends AsyncTask<Void, Void, Void> {
        String inputFileName;
        private String outputformat;

        public void onPostExecute(Void voidR) {
        }

        public void onProgressUpdate(Void... voidArr) {
        }

        public SaveTask() {
            creationDialog.showDialog();
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(SplitVideo.this, Uri.fromFile(new File(videoPath)));

            if (videoview.isPlaying()) {
                videoview.pause();
            }
        }

        public Void doInBackground(Void... voidArr) {
            SplitVideo fastVideo = SplitVideo.this;
            fastVideo.make(fastVideo.videoPath);
            return null;
        }
    }

    public void onPause() {
        videoview.pause();
        ivBtnPreview2.setImageResource(R.drawable.play);
        handler.removeCallbacks(seekrunnable);
        super.onPause();
    }
   public static String format;
    private static final String FILEPATH = "filepath";
    int count = 0;
    public void make(String str2) {
         format = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());

        String filePrefix = "video_";
        String fileExtn = ".mp4";

        String root = Environment.getExternalStorageDirectory()
                + "/"+getString(R.string.app_name) +"/";
        File myDir = new File(root + getString(R.string.splitvideo)+"/Video_"+format);
        if (myDir.isDirectory())
        {
            String[] children = myDir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(myDir, children[i]).delete();
            }
        }
        myDir.mkdirs();
      final   File dstPath = new File(myDir, filePrefix + "%003d" + fileExtn);

//        String[] strArr = {"-y", "-i", str2, "-c", "copy",
//                "-map", "0", "-map", "0", "-segment_time", N + "",
//                "-f", "segment", dstPath.getAbsolutePath()};

        int xx=duration1/N;
        String[] strArr = { "-y", "-i", str2, "-c:v", "libx264",
                "-crf", "22", "-map", "0", "-segment_time", N+"" ,
                "-reset_timestamps", "1", "-g", xx+"" ,"-sc_threshold", "0", "-force_key_frames",
                "expr:gte(t,n_forced*"+N+")", "-f","segment",dstPath.getAbsolutePath()};

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
            Utils.frommain=false;
            creationDialog.dismissDialog();
            refreshGallery(dstPath.getAbsolutePath(),SplitVideo.this);
            resetExternalStorageMedia(SplitVideo.this);

            Intent intent = new Intent(SplitVideo.this, SplitVideoList.class);
            intent.putExtra(FILEPATH, dstPath.getAbsolutePath());
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
//                            Utils.frommain=false;
//                            creationDialog.dismissDialog();
//                            refreshGallery(dstPath.getAbsolutePath(),SplitVideo.this);
//                            resetExternalStorageMedia(SplitVideo.this);
//
//                            Intent intent = new Intent(SplitVideo.this, SplitVideoList.class);
//                            intent.putExtra(FILEPATH, dstPath.getAbsolutePath());
//                            startActivity(intent);
//                            finish();
//                        }
//                    }, 3000);
//                }
//                else
//                {
//                    Utils.frommain=false;
//                    creationDialog.dismissDialog();
//                    refreshGallery(dstPath.getAbsolutePath(),SplitVideo.this);
//                    resetExternalStorageMedia(SplitVideo.this);
//
//                    Intent intent = new Intent(SplitVideo.this, SplitVideoList.class);
//                    intent.putExtra(FILEPATH, dstPath.getAbsolutePath());
//                    startActivity(intent);
//                    finish();
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
    private static void refreshGallery(String mCurrentPhotoPath, Context context) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
    static public boolean resetExternalStorageMedia(Context context) {
        if (Environment.isExternalStorageEmulated())
            return (false);
        Uri uri = Uri.parse("file://" + Environment.getExternalStorageDirectory());
        Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, uri);

        context.sendBroadcast(intent);
        return (true);
    }

    @Override
    protected void onResume() {
        super.onResume();

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
                            SplitVideo fastVideo2 = SplitVideo.this;
                            fastVideo2.task = new SaveTask();
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
                SplitVideo fastVideo2 = SplitVideo.this;
                fastVideo2.task = new SaveTask();
                task.execute(new Void[0]);
            }

        }
        else{
            SplitVideo fastVideo2 = SplitVideo.this;
            fastVideo2.task = new SaveTask();
            task.execute(new Void[0]);
        }
    }


}
