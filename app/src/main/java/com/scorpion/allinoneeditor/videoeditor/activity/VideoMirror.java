package com.scorpion.allinoneeditor.videoeditor.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.scorpion.allinoneeditor.AdHelper.LoadInter;
import static com.scorpion.allinoneeditor.AdHelper.mInterstitialAd;

//import com.scorpion.allinoneeditor.FBInterstitial;

public class VideoMirror extends BaseActivity implements SeekBar.OnSeekBarChangeListener {
    RelativeLayout VideoViewRelative;
    BitmapDrawable bitmapDrawable;
    int duration = 0;
    Handler handler = new Handler();
    boolean isVgood = true;
    ImageView ivBtnPreview2;
    LinearLayout ln_bottom;
    ImageView mirror_down;
    ImageView mirror_left;
    ImageView mirror_right;
    ImageView mirror_top;
    int mirror_type = 0;
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
                    textView.setText("" + VideoMirror.formatTimeUnit((long) currentPosition));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                handler.postDelayed(seekrunnable, 100);
            }
        }
    };
    String dstPath;
    String str1;
    String str2;
    SaveTask task;
    TextView tvSeekLeft;
    TextView tvSeekRight;
    String vid_path;
    VideoView videoview;
    ShowCreationDialog creationDialog;

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video_mirror);
        vid_path = getIntent().getStringExtra("vid_path");
        Log.d("vid_path", vid_path);

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

        ln_bottom = (LinearLayout) findViewById(R.id.ln_bottom);
        mirror_left = findViewById(R.id.mLeft);
        mirror_left.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                VideoMirror videoMirror = VideoMirror.this;
                videoMirror.mirror_type = 0;
                videoMirror.task = new SaveTask();
//                FBInterstitial.getInstance().displayFBInterstitial(VideoMirror.this, new FBInterstitial.FbCallback() {
//                    public void callbackCall() {
                ShowInter(VideoMirror.this);
//                    }
//                });


            }
        });
        mirror_right = findViewById(R.id.mRight);
        mirror_right.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                VideoMirror videoMirror = VideoMirror.this;
                videoMirror.mirror_type = 1;
                videoMirror.task = new SaveTask();
//                FBInterstitial.getInstance().displayFBInterstitial(VideoMirror.this, new FBInterstitial.FbCallback() {
//                    public void callbackCall() {
                ShowInter(VideoMirror.this);
//                    }
//                });
            }
        });
        mirror_top = findViewById(R.id.mTop);
        mirror_top.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                VideoMirror videoMirror = VideoMirror.this;
                videoMirror.mirror_type = 2;
                videoMirror.task = new SaveTask();
//                FBInterstitial.getInstance().displayFBInterstitial(VideoMirror.this, new FBInterstitial.FbCallback() {
//                    public void callbackCall() {
                ShowInter(VideoMirror.this);
//                    }
//                });
            }
        });
        mirror_down = findViewById(R.id.mDown);
        mirror_down.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                VideoMirror videoMirror = VideoMirror.this;
                videoMirror.mirror_type = 3;
                videoMirror.task = new SaveTask();
//                FBInterstitial.getInstance().displayFBInterstitial(VideoMirror.this, new FBInterstitial.FbCallback() {
//                    public void callbackCall() {

                ShowInter(VideoMirror.this);
//                    }
//                });
            }
        });
        videoview = (VideoView) findViewById(R.id.vvScreen);
        ivBtnPreview2 = (ImageView) findViewById(R.id.ivBtnPreview2);
        ivBtnPreview2.setOnClickListener(onclickPreview);

        seekVideo = (SeekBar) findViewById(R.id.seekVideo);
        seekVideo.setOnSeekBarChangeListener(this);
        tvSeekLeft = (TextView) findViewById(R.id.tvSeekLeft);
        tvSeekRight = (TextView) findViewById(R.id.tvSeekRight);
        videoview.setVideoPath(vid_path);
        bitmapDrawable = new BitmapDrawable(ThumbnailUtils.createVideoThumbnail(vid_path, 1));
        videoview.setBackgroundDrawable(bitmapDrawable);
        VideoViewRelative = (RelativeLayout) findViewById(R.id.VideoViewRelative);
        r222 = (RelativeLayout) findViewById(R.id.r222);
        initVideoView();
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
                    textView.setText("" + VideoMirror.formatTimeUnit((long) duration));
                } catch (Exception unused) {
                }
                isVgood = true;
            }
        });
        videoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                VideoMirror videoMirror = VideoMirror.this;
                videoMirror.isVgood = false;
                Toast.makeText(videoMirror, "Video Player Supporting issue.", Toast.LENGTH_SHORT).show();
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

    public static boolean aa(String str3, int i, int i2) {
        Point bb = bb(new File(str3));
        return str3 != null && ((bb.x > i && bb.y > i2) || (bb.y > i && bb.x > i2));
    }

    public static Point bb(File file) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
        String extractMetadata = mediaMetadataRetriever.extractMetadata(19);
        String extractMetadata2 = mediaMetadataRetriever.extractMetadata(18);
        if (extractMetadata == null || extractMetadata2 == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(extractMetadata);
        sb.append(" ");
        String str3 = extractMetadata2;
        sb.append(str3);
        Log.e("size", sb.toString());
        return new Point(Integer.parseInt(extractMetadata), Integer.parseInt(str3));
    }

    public static int aaa(String str3) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(str3);
        String extractMetadata = mediaMetadataRetriever.extractMetadata(24);
        if (extractMetadata == null) {
            return 0;
        }
        return Integer.valueOf(extractMetadata).intValue();
    }

    public String[] cmddown(String str3, String str4) {
        boolean aa = aa(str3, 1280, 720);
        String str5 = "scale=-1:ih/2";
        if (aa) {
            str5 = "scale=640:-1";
        }
        String str6 = "scale=-1:ih/2";
        if (aa) {
            str6 = "scale=-1:640";
        }
        int aaa = aaa(str3);
        ArrayList arrayList = new ArrayList();
        arrayList.add("-i");
        arrayList.add(str3);
        arrayList.add("-filter_complex");
        if (aaa == 90) {
            arrayList.add("transpose=1," + str6 + ",split[bottom],vflip,pad=0:2*ih[top]; [top][bottom] overlay=0:H/2");
            StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append(aaa);
            Log.e("rotate", sb.toString());
        } else if (aaa == 180) {
            arrayList.add("transpose=1:landscape,rotate=PI," + str5 + ",split[bottom],vflip,pad=0:2*ih[top]; [top][bottom] overlay=0:H/2");
            StringBuilder sb2 = new StringBuilder();
            sb2.append("");
            sb2.append(aaa);
            Log.e("rotate", sb2.toString());
        } else if (aaa != 270) {
            arrayList.add(str5 + ",split[bottom],vflip,pad=0:2*ih[top]; [top][bottom] overlay=0:H/2");
            Log.e("rotate", "" + aaa);
        } else {
            arrayList.add("transpose=2," + str6 + ",split[bottom],vflip,pad=0:2*ih[top]; [top][bottom] overlay=0:H/2");
            StringBuilder sb3 = new StringBuilder();
            sb3.append("");
            sb3.append(aaa);
            Log.e("rotate", sb3.toString());
        }
        arrayList.add("-vcodec");
        arrayList.add("libx264");
        arrayList.add("-strict");
        arrayList.add("experimental");
        arrayList.add("-threads");
        arrayList.add("5");
        arrayList.add("-preset");
        arrayList.add("ultrafast");
        arrayList.add(str4);
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    public String[] cmdleft(String str3, String str4) {
        boolean aa = aa(str3, 1280, 720);
        int aaa = aaa(str3);
        String str5 = "scale=iw/2:-1";
        if (aa) {
            str5 = "scale=640:-1";
        }
        String str6 = "scale=-1:ih/2";
        if (aa) {
            str6 = "scale=-1:640";
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add("-i");
        arrayList.add(str3);
        arrayList.add("-filter_complex");
        if (aaa == 90) {
            arrayList.add("transpose=1," + str6 + ",split[tmp],pad=2*iw:0[left]; [tmp]hflip[right]; [left][right] overlay=W/2:0");
            StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append(aaa);
            Log.e("rotate", sb.toString());
        } else if (aaa == 180) {
            arrayList.add("transpose=1:landscape,rotate=PI," + str5 + ",split[tmp],pad=2*iw:0[left]; [tmp]hflip[right]; [left][right] overlay=W/2:0");
            StringBuilder sb2 = new StringBuilder();
            sb2.append("");
            sb2.append(aaa);
            Log.e("rotate", sb2.toString());
        } else if (aaa != 270) {
            arrayList.add(str5 + ",split[tmp],pad=2*iw:0[left]; [tmp]hflip[right]; [left][right] overlay=W/2:0");
            Log.e("rotate", "" + aaa);
        } else {
            arrayList.add("transpose=2," + str6 + ",split[tmp],pad=2*iw:0[left]; [tmp]hflip[right]; [left][right] overlay=W/2:0");
            StringBuilder sb3 = new StringBuilder();
            sb3.append("");
            sb3.append(aaa);
            Log.e("rotate", sb3.toString());
        }
        arrayList.add("-codec:v");
        arrayList.add("libx264");
        arrayList.add("-strict");
        arrayList.add("experimental");
        arrayList.add("-threads");
        arrayList.add("5");
        arrayList.add("-preset");
        arrayList.add("ultrafast");
        arrayList.add(str4);
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    public String[] cmdtop(String str3, String str4) {
        boolean aa = aa(str3, 1280, 720);
        int aaa = aaa(str3);
        String str5 = "scale=-1:ih/2";
        if (aa) {
            str5 = "scale=640:-1";
        }
        String str6 = "scale=-1:ih/2";
        if (aa) {
            str6 = "scale=-1:640";
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add("-i");
        arrayList.add(str3);
        arrayList.add("-filter_complex");
        if (aaa == 90) {
            arrayList.add("transpose=1," + str6 + ",split[tmp],pad=0:2*ih[top]; [tmp]vflip[bottom]; [top][bottom] overlay=0:H/2");
            StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append(aaa);
            Log.e("rotate", sb.toString());
        } else if (aaa == 180) {
            arrayList.add("transpose=1:landscape,rotate=PI," + str5 + ",split[tmp],pad=0:2*ih[top]; [tmp]vflip[bottom]; [top][bottom] overlay=0:H/2");
            StringBuilder sb2 = new StringBuilder();
            sb2.append("");
            sb2.append(aaa);
            Log.e("rotate", sb2.toString());
        } else if (aaa != 270) {
            arrayList.add(str5 + ",split[tmp],pad=0:2*ih[top]; [tmp]vflip[bottom]; [top][bottom] overlay=0:H/2");
            Log.e("rotate", "" + aaa);
        } else {
            arrayList.add("transpose=2," + str6 + ",split[tmp],pad=0:2*ih[top]; [tmp]vflip[bottom]; [top][bottom] overlay=0:H/2");
            StringBuilder sb3 = new StringBuilder();
            sb3.append("");
            sb3.append(aaa);
            Log.e("rotate", sb3.toString());
        }
        arrayList.add("-vcodec");
        arrayList.add("libx264");
        arrayList.add("-strict");
        arrayList.add("experimental");
        arrayList.add("-threads");
        arrayList.add("5");
        arrayList.add("-preset");
        arrayList.add("ultrafast");
        arrayList.add(str4);
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
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
            VideoMirror videoMirror = VideoMirror.this;
            videoMirror.make(videoMirror.vid_path);
            return null;
        }
    }

    public void make(final String str3) {
        String format = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File file = new File(externalStorageDirectory, getString(R.string.app_name) + "/" + getString(R.string.mirror));
        if (!file.exists()) {
            file.mkdir();
        }
        File file2 = new File(externalStorageDirectory, getString(R.string.app_name) + "/" + getString(R.string.mirror) + "/.tmp");
        if (!file2.exists()) {
            file2.mkdir();
        }
        dstPath = file.getAbsolutePath() + "/video_" + format + ".mp4";
        int i = mirror_type;

        if (i == 0) {

            if (cmdleft(str3, dstPath).length != 0) {
               final String tmp = file2.getAbsolutePath() + "/video_" + format + ".mp4";
                String[] strArr = {"-y","-i", str3, "-vf", "hflip", tmp};

                int rc = FFmpeg.execute(strArr);
                if (rc == Config.RETURN_CODE_SUCCESS){

                    String[] strArr2 = {"-y","-i", str3, "-i", tmp, "-filter_complex", "hstack", dstPath};

                    int rc2 = FFmpeg.execute(strArr2);
                    if (rc2 == Config.RETURN_CODE_SUCCESS){
                        creationDialog.dismissDialog();
                        File tmpFile = new File(tmp);
                        if (tmpFile.exists())
                            tmpFile.delete();
                        Intent intent = new Intent(VideoMirror.this, VideoViewActivity.class);
                        intent.putExtra("videourl", dstPath);
                        startActivity(intent);
                        finish();
                    }

                }

//                new CommandExecutor(VideoMirror.this, strArr, new FfResponse() {
//                    @Override
//                    public void onStart() {
//                        Log.e("FFMPEG", "onStart");
//                    }
//
//                    @Override
//                    public void onSuccess() {
//                        String[] strArr2 = {"-y","-i", str3, "-i", tmp, "-filter_complex", "hstack", dstPath};
//                        new CommandExecutor(VideoMirror.this, strArr2, new FfResponse() {
//                            @Override
//                            public void onStart() {
//                                Log.e("FFMPEG", "onStart");
//                            }
//
//                            @Override
//                            public void onSuccess() {
//                                creationDialog.dismissDialog();
//                                File tmpFile = new File(tmp);
//                                if (tmpFile.exists())
//                                    tmpFile.delete();
//                                Intent intent = new Intent(VideoMirror.this, VideoViewActivity.class);
//                                intent.putExtra("videourl", dstPath);
//                                startActivity(intent);
//                                finish();
//
//                            }
//
//                            @Override
//                            public void onError(String errormsg) {
//                                creationDialog.dismissDialog();
//                                Log.e("FFMPEG", "onError");
//
//                            }
//
//                            @Override
//                            public void onStop() {
//                                Log.e("FFMPEG", "onStop");
//                                creationDialog.dismissDialog();
//                            }
//                        }).execute();
//                    }
//
//                    @Override
//                    public void onError(String errormsg) {
////                        creationDialog.dismissDialog();
//                        Log.e("FFMPEG", "onError");
//                    }
//
//                    @Override
//                    public void onStop() {
//                        Log.e("FFMPEG", "onStop");
////                        creationDialog.dismissDialog();
//                    }
//                }).execute();

            } else {
                Toast.makeText(getApplicationContext(), "Command Empty", Toast.LENGTH_LONG).show();
            }
        } else if (i == 1) {
            if (cmdleft(str3, dstPath).length != 0) {
               final String tmp = file2.getAbsolutePath() + "/video_" + format + ".mp4";
                String[] strArr = {"-y","-i", str3, "-vf", "hflip", tmp};

                int rc = FFmpeg.execute(strArr);
                if (rc == Config.RETURN_CODE_SUCCESS){

                    String[] strArr2 = {"-y","-i", str3, "-i", tmp, "-filter_complex", "hstack", dstPath};

                    int rc2 = FFmpeg.execute(strArr2);
                    if (rc2 == Config.RETURN_CODE_SUCCESS){
                        creationDialog.dismissDialog();
                        File tmpFile = new File(tmp);
                        if (tmpFile.exists())
                            tmpFile.delete();
                        Intent intent = new Intent(VideoMirror.this, VideoViewActivity.class);
                        intent.putExtra("videourl", dstPath);
                        startActivity(intent);
                        finish();
                    }
                }

//                new CommandExecutor(VideoMirror.this, strArr, new FfResponse() {
//                    @Override
//                    public void onStart() {
//                        Log.e("FFMPEG", "onStart");
//                    }
//
//                    @Override
//                    public void onSuccess() {
//                        String[] strArr2 = {"-y","-i", str3, "-i", tmp, "-filter_complex", "hstack", dstPath};
//                        new CommandExecutor(VideoMirror.this, strArr2, new FfResponse() {
//                            @Override
//                            public void onStart() {
//                                Log.e("FFMPEG", "onStart");
//                            }
//
//                            @Override
//                            public void onSuccess() {
//                                creationDialog.dismissDialog();
//                                File tmpFile = new File(tmp);
//                                if (tmpFile.exists())
//                                    tmpFile.delete();
//                                Intent intent = new Intent(VideoMirror.this, VideoViewActivity.class);
//                                intent.putExtra("videourl", dstPath);
//                                startActivity(intent);
//                                finish();
//                            }
//
//                            @Override
//                            public void onError(String errormsg) {
//                                creationDialog.dismissDialog();
//                                Log.e("FFMPEG", "onError");
//
//                            }
//
//                            @Override
//                            public void onStop() {
//                                Log.e("FFMPEG", "onStop");
//                                creationDialog.dismissDialog();
//                            }
//                        }).execute();
//                    }
//
//                    @Override
//                    public void onError(String errormsg) {
////                        creationDialog.dismissDialog();
//                        Log.e("FFMPEG", "onError");
//                    }
//
//                    @Override
//                    public void onStop() {
//                        Log.e("FFMPEG", "onStop");
////                        creationDialog.dismissDialog();
//                    }
//                }).execute();

            } else {
                Toast.makeText(getApplicationContext(), "Command Empty", Toast.LENGTH_LONG).show();
            }
        } else if (i == 2) {
            if (cmdtop(str3, dstPath).length != 0) {
               final String tmp = file2.getAbsolutePath() + "/video_" + format + ".mp4";
                String[] strArr = {"-y","-i", str3, "-vf", "vflip", tmp};

                int rc = FFmpeg.execute(strArr);
                if (rc == Config.RETURN_CODE_SUCCESS){
                    String[] strArr2 = {"-y","-i", tmp, "-i", str3, "-filter_complex", "vstack", dstPath};
                    int rc2 = FFmpeg.execute(strArr2);
                    if (rc2 == Config.RETURN_CODE_SUCCESS){
                        creationDialog.dismissDialog();
                        File tmpFile = new File(tmp);
                        if (tmpFile.exists())
                            tmpFile.delete();
                        Intent intent = new Intent(VideoMirror.this, VideoViewActivity.class);
                        intent.putExtra("videourl", dstPath);
                        startActivity(intent);
                        finish();
                    }
                }

//                new CommandExecutor(VideoMirror.this, strArr, new FfResponse() {
//                    @Override
//                    public void onStart() {
//                        Log.e("FFMPEG", "onStart");
//                    }
//
//                    @Override
//                    public void onSuccess() {
//                        Log.e("FFMPEG", "success");
//                        String[] strArr2 = {"-y","-i", tmp, "-i", str3, "-filter_complex", "vstack", dstPath};
//                        new CommandExecutor(VideoMirror.this, strArr2, new FfResponse() {
//                            @Override
//                            public void onStart() {
//                                Log.e("FFMPEG", "onStart");
//                            }
//
//                            @Override
//                            public void onSuccess() {
//
//                                creationDialog.dismissDialog();
//                                File tmpFile = new File(tmp);
//                                if (tmpFile.exists())
//                                    tmpFile.delete();
//                                Intent intent = new Intent(VideoMirror.this, VideoViewActivity.class);
//                                intent.putExtra("videourl", dstPath);
//                                startActivity(intent);
//                                finish();
//                            }
//
//                            @Override
//                            public void onError(String errormsg) {
//                                creationDialog.dismissDialog();
//                                Log.e("FFMPEG", "onError");
//
//                            }
//
//                            @Override
//                            public void onStop() {
//                                Log.e("FFMPEG", "onStop");
//                                creationDialog.dismissDialog();
//                            }
//                        }).execute();
//
//                    }
//
//                    @Override
//                    public void onError(String errormsg) {
////                        creationDialog.dismissDialog();
//                        Log.e("FFMPEG", "onError");
//
//                    }
//
//                    @Override
//                    public void onStop() {
//                        Log.e("FFMPEG", "onStop");
////                        creationDialog.dismissDialog();
//                    }
//                }).execute();

            } else {
                Toast.makeText(getApplicationContext(), "Command Empty", Toast.LENGTH_LONG).show();
            }
        } else if (i != 3) {
        } else {
            if (cmddown(str3, dstPath).length != 0) {
                final  String tmp = file2.getAbsolutePath() + "/video_" + format + ".mp4";
                String[] strArr = {"-y","-i", str3, "-vf", "vflip", tmp};

                int rc = FFmpeg.execute(strArr);
                if (rc == Config.RETURN_CODE_SUCCESS){
                    String[] strArr2 = {"-y","-i", str3, "-i", tmp, "-filter_complex", "vstack", dstPath};
                    int rc2 = FFmpeg.execute(strArr2);
                    if (rc2 == Config.RETURN_CODE_SUCCESS){
                        creationDialog.dismissDialog();
                        File tmpFile = new File(tmp);
                        if (tmpFile.exists())
                            tmpFile.delete();
                        Intent intent = new Intent(VideoMirror.this, VideoViewActivity.class);
                        intent.putExtra("videourl", dstPath);
                        startActivity(intent);
                        finish();
                    }
                }

//                new CommandExecutor(this, strArr, new FfResponse() {
//                    @Override
//                    public void onStart() {
//                        Log.e("FFMPEG", "onStart");
//                    }
//
//                    @Override
//                    public void onSuccess() {
//                        String[] strArr2 = {"-y","-i", str3, "-i", tmp, "-filter_complex", "vstack", dstPath};
//                        new CommandExecutor(VideoMirror.this, strArr2, new FfResponse() {
//                            @Override
//                            public void onStart() {
//                                Log.e("FFMPEG", "onStart");
//                            }
//
//                            @Override
//                            public void onSuccess() {
//
//                                creationDialog.dismissDialog();
//                                File tmpFile = new File(tmp);
//                                if (tmpFile.exists())
//                                    tmpFile.delete();
//                                Intent intent = new Intent(VideoMirror.this, VideoViewActivity.class);
//                                intent.putExtra("videourl", dstPath);
//                                startActivity(intent);
//                                finish();
//                            }
//
//                            @Override
//                            public void onError(String errormsg) {
//                                creationDialog.dismissDialog();
//                                Log.e("FFMPEG", "onError");
//
//                            }
//
//                            @Override
//                            public void onStop() {
//                                Log.e("FFMPEG", "onStop");
//                                creationDialog.dismissDialog();
//                            }
//                        }).execute();
//
//                    }
//
//                    @Override
//                    public void onError(String errormsg) {
////                        creationDialog.dismissDialog();
//                        Log.e("FFMPEG", "onError");
//
//                    }
//
//                    @Override
//                    public void onStop() {
//                        Log.e("FFMPEG", "onStop");
////                        creationDialog.dismissDialog();
//                    }
//                }).execute();

//                execFFmpegBinary(cmddown(str3, dstPath));
            } else {
                Toast.makeText(getApplicationContext(), "Command Empty", Toast.LENGTH_LONG).show();
            }
        }
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

    public void onPause() {
        videoview.pause();
        ivBtnPreview2.setImageResource(R.drawable.play);
        handler.removeCallbacks(seekrunnable);
        super.onPause();
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
                task.execute(new Void[0]);
            }
        }
        else{
            task.execute(new Void[0]);
        }
    }

}
