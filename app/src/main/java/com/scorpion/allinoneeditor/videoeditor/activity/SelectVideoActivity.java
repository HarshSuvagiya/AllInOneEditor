package com.scorpion.allinoneeditor.videoeditor.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdView;
//import com.scorpion.allinoneeditor.FBInterstitial;
import com.scorpion.allinoneeditor.AdHelper;
import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.Utility;
import com.scorpion.allinoneeditor.audioeditor.Activity.AmplifierAudio;
import com.scorpion.allinoneeditor.videoeditor.adapter.SelectVideoAdapter;
import com.scorpion.allinoneeditor.videoeditor.model.VideoData;
import com.scorpion.allinoneeditor.videoeditor.utils.ContentUtill;
import com.scorpion.allinoneeditor.videoeditor.utils.Utils;

import java.io.File;
import java.util.ArrayList;

public class SelectVideoActivity extends BaseActivity {

    ListView videogrid;
    ArrayList<VideoData> videoList = new ArrayList<>();
    SelectVideoAdapter adapter;
    public static SelectVideoActivity activity;
    String vid_path;
    long timeInMillisec;
    String a_path;
    String v_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_video);

        activity = this;
        Toolbar toolbar = findViewById(R.id.header);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FrameLayout adContainerView = findViewById(R.id.adView1);
        adView = new AdView(this);
        adView.setAdUnitId(Utility.BannerID);
        adContainerView.addView(adView);
        loadBanner();
        AdHelper.AdLoadHelper(this, adView);

        videogrid = (ListView) findViewById(R.id.VideogridView);
        new loadVideo().execute(new Void[0]);
    }

    @SuppressLint({"NewApi"})
    private class loadVideo extends AsyncTask<Void, Void, Boolean> {
        Dialog pd;

        private loadVideo() {
            pd = null;
        }

        public void onPreExecute() {
            pd = new Dialog(SelectVideoActivity.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
            pd.setContentView(R.layout.progress_dialog);
            LottieAnimationView lottieAnimationView;
            lottieAnimationView = pd.findViewById(R.id.gif_loading);
            lottieAnimationView.setAnimation("loading.json");
            lottieAnimationView.playAnimation();
            lottieAnimationView.loop(true);
            ImageView imageView = (ImageView) pd.findViewById(R.id.gif_loading);
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        public Boolean doInBackground(Void... voidArr) {
            return Boolean.valueOf(getVideoList());
        }

        public void onPostExecute(Boolean bool) {
            pd.dismiss();
            if (bool.booleanValue()) {
                adapter = new SelectVideoAdapter(SelectVideoActivity.this, videoList);
                videogrid.setAdapter(adapter);
            }
        }
    }

    public void callVideo(final String str2) {
//        FBInterstitial.getInstance().displayFBInterstitial(SelectVideoActivity.this, new FBInterstitial.FbCallback() {
//            public void callbackCall() {

                switch (Utils.position) {
                    case 4:
                        Log.e("PATH", str2);
                        Intent intent = new Intent(SelectVideoActivity.this, VideoCompress.class);
                        intent.putExtra("vid_path", str2);
                        startActivity(intent);
                        finish();
                        break;
                    case 10:
                        Log.e("PATH", str2);
                        Intent intent2 = new Intent(SelectVideoActivity.this, TrimmerActivity.class);
                        intent2.putExtra("vid_path", str2);
                        startActivity(intent2);
                        finish();
                        break;
                    case 1:
                        Log.e("PATH", str2);
                        Intent intent3 = new Intent(SelectVideoActivity.this, VideoConverter.class);
                        intent3.putExtra("vid_path", str2);
                        startActivity(intent3);
                        finish();
                        break;
                    case 6:
                        Log.e("PATH", str2);
                        Intent intent4 = new Intent(SelectVideoActivity.this, MuteVideo.class);
                        intent4.putExtra("vid_path", str2);
                        startActivity(intent4);
                        finish();
                        break;
                    case 8:
                        Log.e("PATH", str2);
                        Intent intent5 = new Intent(SelectVideoActivity.this, VideoToMp3.class);
                        intent5.putExtra("vid_path", str2);
                        startActivity(intent5);
                        finish();
                        break;
                    case 12:
                        Log.e("PATH", str2);
                        Intent intent6 = new Intent(SelectVideoActivity.this, VideoToGif.class);
                        intent6.putExtra("vid_path", str2);
                        startActivity(intent6);
                        finish();
                        break;
                    case 0:
                        vid_path = v_path = str2;
                        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                        mediaMetadataRetriever.setDataSource(SelectVideoActivity.this, Uri.fromFile(new File(vid_path)));
                        timeInMillisec = Long.parseLong(mediaMetadataRetriever.extractMetadata(9));
                        mediaMetadataRetriever.release();
                        startActivityForResult(new Intent(SelectVideoActivity.this, SelectAudioActivity.class), 123);
//                finish();
                        break;
                    case 5:
                        Log.e("PATH", str2);
                        Intent intent8 = new Intent(SelectVideoActivity.this, VideoRotate.class);
                        intent8.putExtra("vid_path", str2);
                        startActivity(intent8);
                        finish();
                        break;
                    case 2:
                        Log.e("PATH", str2);
                        Intent intent7 = new Intent(SelectVideoActivity.this, SlowVideo.class);
                        intent7.putExtra("vid_path", str2);
                        startActivity(intent7);
                        finish();
                        break;
                    case 3:
                        Log.e("PATH", str2);
                        Intent intent9 = new Intent(SelectVideoActivity.this, FastVideo.class);
                        intent9.putExtra("vid_path", str2);
                        startActivity(intent9);
                        finish();
                        break;
                    case 7:
                        Log.e("PATH", str2);
                        Intent intent10 = new Intent(SelectVideoActivity.this, VideoMirror.class);
                        intent10.putExtra("vid_path", str2);
                        startActivity(intent10);
                        finish();
                        break;
                    case 9:
                        Log.e("PATH", str2);
                        Intent intent11 = new Intent(SelectVideoActivity.this, VideoReverse.class);
                        intent11.putExtra("vid_path", str2);
                        startActivity(intent11);
                        finish();
                        break;
                    case 11:
                        Log.e("PATH", str2);
                        Intent intent12 = new Intent(SelectVideoActivity.this, VideoFade.class);
                        intent12.putExtra("vid_path", str2);
                        startActivity(intent12);
                        finish();
                        break;
                    case 13:
                        Log.e("PATH", str2);
                        Intent intent16 = new Intent(SelectVideoActivity.this, VideoFlipActivity.class);
                        intent16.putExtra("vid_path", str2);
                        startActivity(intent16);
                        finish();
                        break;
                    case 14:
                        Log.e("PATH", str2);
                        Intent intent14 = new Intent(SelectVideoActivity.this, SplitVideo.class);
                        intent14.putExtra("vid_path", str2);
                        startActivity(intent14);
                        finish();
                        break;
                    case 15:
                        Log.e("PATH", str2);
                        Intent intent15 = new Intent(SelectVideoActivity.this, VideoToImages.class);
                        intent15.putExtra("vid_path", str2);
                        startActivity(intent15);
                        finish();
                        break;
                    case 16:
                        if(!isVideoHaveAudioTrack(str2))
                        {
                            Toast.makeText(getApplicationContext(), "Mute Video can't Merge...", Toast.LENGTH_LONG).show();

                        }
                        else {
                            Log.e("PATH", str2);
                            Intent intent17 = new Intent(SelectVideoActivity.this, MergeVideoActivity.class);
                            intent17.putExtra("vid_path", str2);
                            startActivity(intent17);
                            finish();
                        }
                        break;
                    case -1:
                        Utils.mergepath = str2;
                        if(!isVideoHaveAudioTrack(str2))
                        {
                            Toast.makeText(getApplicationContext(), "Mute Video can't Merge...", Toast.LENGTH_LONG).show();

                        }
                        else {
                            setResult(RESULT_OK);
                            finish();
                        }
                        break;

                }
        AdHelper.ShowInter(SelectVideoActivity.this);
//            }
//        });



        }
    private boolean isVideoHaveAudioTrack(String path) {
        boolean audioTrack =false;

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        String hasAudioStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO);
        if(hasAudioStr!=null) {
            if (hasAudioStr.equals("yes")) {
                audioTrack = true;
            } else {
                audioTrack = false;
            }
        }
        return audioTrack;
    }
    @SuppressLint({"NewApi"})
    public boolean getVideoList() {
        Cursor managedQuery = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{"_data", "_id", "_display_name", "duration"}, (String) null, (String[]) null, " _id DESC");
        int count = managedQuery.getCount();
        if (count <= 0) {
            return false;
        }
        managedQuery.moveToFirst();
        for (int i = 0; i < count; i++) {
            videoList.add(new VideoData(managedQuery.getString(managedQuery.getColumnIndexOrThrow("_display_name")),
                    Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, ContentUtill.getLong(managedQuery)),
                    managedQuery.getString(managedQuery.getColumnIndex("_data")),
                    MediaStore.Video.VideoColumns.DURATION));
            managedQuery.moveToNext();
            Log.e("DURAtION", MediaStore.Video.VideoColumns.DURATION);
        }
        return true;
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 123 && i2 == -1) {
            a_path = SelectAudioActivity.path;
            Intent intent2 = new Intent(this, VideoAudioMix.class);
            intent2.putExtra("vid_path", v_path);
            intent2.putExtra("aud_path", a_path);
            startActivity(intent2);
        }
        super.onActivityResult(i, i2, intent);
    }



    @Override
    protected void onResume() {
        super.onResume();

    }



}
