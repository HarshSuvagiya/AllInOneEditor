package com.scorpion.allinoneeditor.videoeditor.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.google.android.gms.ads.AdView;
//import com.scorpion.allinoneeditor.FBInterstitial;
import com.scorpion.allinoneeditor.AdHelper;
import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.Utility;
import com.scorpion.allinoneeditor.audioeditor.Activity.AmplifierAudio;
//import com.scorpion.allinoneeditor.videoeditor.utils.AdHelper;
import com.scorpion.allinoneeditor.videoeditor.utils.Utils;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class VideoViewActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {
    RelativeLayout VideoViewRelative;
    int duration = 0;
    Handler handler = new Handler();
    private boolean isSquare = false;
    boolean isVgood = true;
    ImageView ivBtnPreview2;
    Context mContext;
    View.OnClickListener onclickPreview = new View.OnClickListener() {
        public void onClick(View view) {
            if (videoview == null) {
                return;
            }
            if (videoview.isPlaying()) {
                videoview.pause();
                videoview.invalidate();
                ivBtnPreview2.setImageResource(R.drawable.play);
                handler.removeCallbacks(seekrunnable);
                return;
            }
            videoview.start();
            ivBtnPreview2.setImageResource(R.drawable.pause);
            handler.postDelayed(seekrunnable, 100);
        }
    };

    LinearLayout.LayoutParams originallayoutParams;
    ProgressDialog pd = null;
    Runnable runnable = new Runnable() {
        public void run() {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            handler.removeCallbacks(runnable);
        }
    };
    SeekBar seekVideo;
    LinearLayout seek_bar_container;
    Runnable seekrunnable = new Runnable() {
        public void run() {
            if (videoview != null && videoview.isPlaying()) {
                int currentPosition = videoview.getCurrentPosition();
                seekVideo.setProgress(currentPosition);
                try {
                    TextView textView = tvSeekLeft;
                    textView.setText("" + VideoViewActivity.formatTimeUnit((long) currentPosition));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                handler.postDelayed(seekrunnable, 100);
            }
        }
    };
    TextView tvSeekLeft;
    TextView tvSeekRight;
    String videoPath;
    LinearLayout videoViewConainter;
    VideoView videoview;
    AlertDialog.Builder builder;

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public static String formatTimeUnit(long j) throws ParseException {
        return String.format("%02d:%02d", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(j)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(j) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(j)))});
    }

    public void onPause() {
        videoview.pause();
        videoview.invalidate();
        ivBtnPreview2.setImageResource(R.drawable.play);
        super.onPause();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video_view);

        Toolbar toolbar = findViewById(R.id.header);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FrameLayout adContainerView = findViewById(R.id.adView1);
        adView = new AdView(this);
        adView.setAdUnitId(Utility.BannerID);
        adContainerView.addView(adView);
        loadBanner();
        AdHelper.AdLoadHelper(this, adView);

        mContext = this;
        videoPath = getIntent().getStringExtra("videourl");
//        Log.e("videoPath", videoPath);
        FindByID();
        initVideoView();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (AdHelper.ratechk) {
            AdHelper.ratechk = false;
            final Dialog dialog = new Dialog(VideoViewActivity.this);
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
                    if (Utils.frommain) {
//                        Intent intent = new Intent(VideoViewActivity.this,
//                                VideoEditorMainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
                        finish();
                    } else {
                        finish();
                    }
                    dialog.dismiss();
                }
            });

            dialog.show();

        } else {
//            FBInterstitial.getInstance().displayFBInterstitial(VideoViewActivity.this, new FBInterstitial.FbCallback() {
//                public void callbackCall() {
                    if (Utils.frommain) {
//                        Intent intent = new Intent(VideoViewActivity.this,
//                                VideoEditorMainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
                        finish();
                    } else {
                        finish();
                    }
            com.scorpion.allinoneeditor.AdHelper.ShowInter(VideoViewActivity.this);
//                }
//            });
        }
    }

    private void FindByID() {
        videoViewConainter = (LinearLayout) findViewById(R.id.linearLayout);
        seek_bar_container = (LinearLayout) findViewById(R.id.seek_bar_container);
        VideoViewRelative = (RelativeLayout) findViewById(R.id.VideoViewRelative);
        VideoViewRelative.setOnClickListener(onclickPreview);
        videoview = (VideoView) findViewById(R.id.vvScreen);
        ivBtnPreview2 = (ImageView) findViewById(R.id.ivBtnPreview2);
        ivBtnPreview2.setOnClickListener(onclickPreview);
        seekVideo = (SeekBar) findViewById(R.id.seekVideo);
        seekVideo.setOnSeekBarChangeListener(this);
        tvSeekLeft = (TextView) findViewById(R.id.tvSeekLeft);
        tvSeekRight = (TextView) findViewById(R.id.tvSeekRight);
    }

    private void initVideoView() {
        videoview.setVideoURI(Uri.parse(videoPath));
        originallayoutParams = (LinearLayout.LayoutParams) videoViewConainter.getLayoutParams();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
                seekVideo.setProgress(0);
                tvSeekLeft.setText("00:00");
                duration = mediaPlayer.getDuration();
                seekVideo.setMax(duration);
                try {
                    TextView textView = tvSeekRight;
                    textView.setText("" + VideoViewActivity.formatTimeUnit((long) duration));
                } catch (Exception unused) {
                }
                VideoViewActivity videoViewActivity = VideoViewActivity.this;
                videoViewActivity.isVgood = true;
                videoViewActivity.videoview.start();
                ivBtnPreview2.setImageResource(R.drawable.pause);
                handler.postDelayed(seekrunnable, 100);
            }
        });
        videoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                VideoViewActivity videoViewActivity = VideoViewActivity.this;
                videoViewActivity.isVgood = false;
                Toast.makeText(videoViewActivity.mContext, "Video Player Supporting issue.", Toast.LENGTH_SHORT).show();
                try {
                    handler.removeCallbacks(seekrunnable);
                    return true;
                } catch (Exception unused) {
                    return true;
                }
            }
        });
    }

    public void onDestroy() {
        getWindow().clearFlags(128);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.video_player_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                delete();
                return true;
            case R.id.share:
                share();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void delete() {
        builder = new AlertDialog.Builder(VideoViewActivity.this);
        builder.setMessage("Do you want to delete this video ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        File fdelete = new File(videoPath);
                        if (fdelete.exists()) {
                            if (fdelete.delete()) {
                                finish();
                            } else {
                                dialog.cancel();
                            }
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("Delete");
        alert.show();
    }

    public void share() {
        Intent share = new Intent("android.intent.action.SEND");
        share.setType("video/*");
        Uri pathUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", new File(videoPath));

        share.putExtra("android.intent.extra.STREAM", pathUri);
        startActivity(Intent.createChooser(share, "Share"));
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

}
