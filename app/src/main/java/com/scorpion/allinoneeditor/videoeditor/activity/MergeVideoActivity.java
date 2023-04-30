package com.scorpion.allinoneeditor.videoeditor.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.scorpion.allinoneeditor.AdHelper;
import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.ShowCreationDialog;
import com.scorpion.allinoneeditor.Utility;
import com.scorpion.allinoneeditor.videoeditor.adapter.Mergevideoadapter;
import com.scorpion.allinoneeditor.videoeditor.utils.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.scorpion.allinoneeditor.AdHelper.LoadInter;
import static com.scorpion.allinoneeditor.AdHelper.mInterstitialAd;

//import com.scorpion.allinoneeditor.FBInterstitial;

public class MergeVideoActivity extends BaseActivity {

    ArrayList<String> listOfVideos = new ArrayList<>();
    String st1;
    ArrayList<String> cmdList = new ArrayList<>();
    ArrayList<String> tmpPathList = new ArrayList<>();
    int pos = 0;
    ShowCreationDialog creationDialog;
    Boolean chkaudio = true;
    ListView videoList;
    Mergevideoadapter audioAdapter;
    ImageView btnadd, btnmix;
    String vid_path;
    int chk = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge_video);
        creationDialog = new ShowCreationDialog(MergeVideoActivity.this);


        btnadd = findViewById(R.id.btnadd);
        btnmix = findViewById(R.id.btnmix);
        Toolbar toolbar = findViewById(R.id.header);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FrameLayout adContainerView = findViewById(R.id.adView1);
        adView = new AdView(this);
        adView.setAdUnitId(Utility.BannerID);
        adContainerView.addView(adView);
        loadBanner();
        AdHelper.AdLoadHelper(this, adView);

        creationDialog = new ShowCreationDialog(MergeVideoActivity.this);
        videoList = (ListView) findViewById(R.id.song_list);

        vid_path = getIntent().getStringExtra("vid_path");
        listOfVideos.clear();
        listOfVideos.add(vid_path);
//        chkaudio=isVideoHaveAudioTrack(vid_path);
//        if(chkaudio)
//        {
//            chk=0;
//        }
//        else
//        {
//            chk=1;
//        }
        audioAdapter = new Mergevideoadapter(MergeVideoActivity.this, listOfVideos);
        videoList.setAdapter(audioAdapter);


        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listOfVideos.size() < 10) {
                    Utils.position = -1;
                    Intent i = new Intent(MergeVideoActivity.this, SelectVideoActivity.class);
                    startActivityForResult(i, 100);
                } else {
                    Toast.makeText(getApplicationContext(), "Maximum 10 Videos can Merge", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnmix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chk == 1) {
                    Toast.makeText(getApplicationContext(), "Mute Video can't Merge...", Toast.LENGTH_LONG).show();

                } else {
                    if (listOfVideos.size() != 0 && listOfVideos.size() > 1) {
                        ShowInter(MergeVideoActivity.this);
                    } else {

                        Toast.makeText(getApplicationContext(), "Select at least 2 Video to Merge", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 100 && i2 == -1) {

            listOfVideos.add(Utils.mergepath);
            chkaudio = isVideoHaveAudioTrack(Utils.mergepath);
            if (!chkaudio) {
                chk = 1;
            }

            audioAdapter = new Mergevideoadapter(MergeVideoActivity.this, listOfVideos);
            videoList.setAdapter(audioAdapter);

        }
    }

    private boolean isVideoHaveAudioTrack(String path) {
        boolean audioTrack = false;

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        String hasAudioStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO);
        if (hasAudioStr != null) {
            if (hasAudioStr.equals("yes")) {
                audioTrack = true;
            } else {
                audioTrack = false;
            }
        }
        return audioTrack;
    }

    class ChangeResolution extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            creationDialog.showDialog();
//            creationDialog.showDialog();
            String format = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());
            File externalStorageDirectory = Environment.getExternalStorageDirectory();
            File file = new File(externalStorageDirectory, getString(R.string.app_name) + "/" + getString(R.string.temp));
            if (!file.exists()) {
                file.mkdir();
            }
            st1 = file.getAbsolutePath() + "/video_" + format + ".mp4";
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String[] strr = new String[]{"-y", "-i", listOfVideos.get(pos),
                    "-vf", "scale=(iw*sar)*min(" + 720 + "/(iw*sar)\\," + 1280 + "/ih):ih*min(" + 720 + "/(iw*sar)\\,"
                    + 1280 + "/ih), pad=" + 720 + ":" + 1280 + ":(" + 720 + "-iw)/2:(" + 1280 + "-ih)/2",
                    "-c:a", "copy", "-preset", "ultrafast", st1};
            make(strr);
//            rc2 = FFmpeg.execute();


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }

    }

    private static void refreshGallery(String mCurrentPhotoPath, Context context) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    private void make(String[] strArr) {

        int rc = FFmpeg.execute(strArr);
        if (rc == Config.RETURN_CODE_SUCCESS){
            refreshGallery(st1, MergeVideoActivity.this);
            Log.e("chkkk", st1 + "");
            tmpPathList.add(st1);


            pos++;
//            Log.e("JSN",pos+"JSN");
            Log.e("JSN", tmpPathList.size() + "jJSN");
            if (listOfVideos.size() > pos) {
                Log.e("JSN", "JSNif");
                new ChangeResolution().execute();

            } else {
                Log.e("JSN", "JSNelse");
                new MyAsyncTask().execute();
            }
        }

//        new CommandExecutor(this, strArr, new FfResponse() {
//            @Override
//            public void onStart() {
//                Log.e("FFMPEG", "onStart");
//            }
//
//            @Override
//            public void onSuccess() {
//                refreshGallery(st1, MergeVideoActivity.this);
//                Log.e("chkkk", st1 + "");
//                tmpPathList.add(st1);
//
//
//                pos++;
////            Log.e("JSN",pos+"JSN");
//                Log.e("JSN", tmpPathList.size() + "jJSN");
//                if (listOfVideos.size() > pos) {
//                    Log.e("JSN", "JSNif");
//                    new ChangeResolution().execute();
//
//                } else {
//                    Log.e("JSN", "JSNelse");
//                    Handler h = new Handler();
//                    h.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            new MyAsyncTask().execute();
//                        }
//                    }, 1000);
//
//                }
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


    class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            String[] cmd = cmdList.toArray(new String[cmdList.size()]);
            make1(cmd);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String format = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());
            File externalStorageDirectory = Environment.getExternalStorageDirectory();
            File file = new File(externalStorageDirectory, getString(R.string.app_name) + "/" + getString(R.string.merge));
            if (!file.exists()) {
                file.mkdir();
            }
            st1 = file.getAbsolutePath() + "/video_" + format + ".mp4";

            StringBuilder sb = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();

            cmdList.clear();
            cmdList.add("-y");
            for (int i = 0; i < tmpPathList.size(); i++) {
                cmdList.add("-i");
                cmdList.add(tmpPathList.get(i));
                sb.append("[").append(i).append(":v:").append("0").append("] ").append("[").append(i).append(":a:").append("0").append("]");
            }

            sb2.append(" concat=n=").append(listOfVideos.size()).append(":v=1:a=1 [v] [a]");
            cmdList.add("-filter_complex");
            cmdList.add(sb.toString() + sb2.toString());
            cmdList.add("-ab");
            cmdList.add("48000");
            cmdList.add("-ac");
            cmdList.add("2");
            cmdList.add("-ar");
            cmdList.add("22050");
            cmdList.add("-r");
            cmdList.add("15");
            cmdList.add("-b");
            cmdList.add("2097k");
            cmdList.add("-map");
            cmdList.add("[v]");
            cmdList.add("-map");
            cmdList.add("[a]");
            cmdList.add("-preset");
            cmdList.add("ultrafast");
            cmdList.add(st1);

            for (int i = 0; i < cmdList.size(); i++)
                Log.e("JSN", cmdList.get(i));
//            rc = FFmpeg.execute(cmd);
            return null;
        }
    }

    private void make1(final String[] strArr) {

        int rc = FFmpeg.execute(strArr);
        if (rc == Config.RETURN_CODE_SUCCESS){
            Intent intent1 = new Intent(MergeVideoActivity.this, VideoViewActivity.class);
            intent1.putExtra("videourl", st1);
            startActivity(intent1);
            finish();
        }
//        new CommandExecutor(this, strArr, new FfResponse() {
//            @Override
//            public void onStart() {
//                Log.e("FFMPEG", "onStart");
//                Log.e("FFMPEG", strArr[0] + strArr[1] + " jay swaminarayan");
//            }
//
//            @Override
//            public void onSuccess() {
//                Intent intent1 = new Intent(MergeVideoActivity.this, VideoViewActivity.class);
//                intent1.putExtra("videourl", st1);
//                startActivity(intent1);
//                finish();
//            }
//
//            @Override
//            public void onError(String errormsg) {
//                creationDialog.dismissDialog();
//                Log.e("FFMPEG", errormsg);
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

    private String generateList(String[] inputs) {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File list = new File(externalStorageDirectory, getString(R.string.app_name) + "/" + getString(R.string.temp) + "/");
        if (!list.exists())
            list.mkdirs();
        String format = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());
        String st1 = list.getAbsolutePath() + "/ffmpeg" + format + ".txt";

        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(st1)));
            for (String input : inputs) {
                writer.write("file '" + input + "'\n");
                Log.d("MergeVideo", "Writing to list file: file '" + input + "'");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "/";
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        Log.d("MergeVideo", "Wrote list file to " + st1);
        return st1;
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
                            creationDialog.showDialog();
                            String format = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());
                            File externalStorageDirectory = Environment.getExternalStorageDirectory();
                            File file = new File(externalStorageDirectory, getString(R.string.app_name) + "/" + getString(R.string.merge));
                            if (!file.exists()) {
                                file.mkdir();
                            }
                            st1 = file.getAbsolutePath() + "/video_" + format + ".mp4";

                            new ChangeResolution().execute();
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
            else {
                creationDialog.showDialog();
                String format = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());
                File externalStorageDirectory = Environment.getExternalStorageDirectory();
                File file = new File(externalStorageDirectory, getString(R.string.app_name) + "/" + getString(R.string.merge));
                if (!file.exists()) {
                    file.mkdir();
                }
                st1 = file.getAbsolutePath() + "/video_" + format + ".mp4";

                new ChangeResolution().execute();
            }
        } else {
            creationDialog.showDialog();
            String format = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());
            File externalStorageDirectory = Environment.getExternalStorageDirectory();
            File file = new File(externalStorageDirectory, getString(R.string.app_name) + "/" + getString(R.string.merge));
            if (!file.exists()) {
                file.mkdir();
            }
            st1 = file.getAbsolutePath() + "/video_" + format + ".mp4";

            new ChangeResolution().execute();
        }
    }

}
