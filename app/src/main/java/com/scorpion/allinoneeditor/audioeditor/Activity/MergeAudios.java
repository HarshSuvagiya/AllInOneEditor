package com.scorpion.allinoneeditor.audioeditor.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.scorpion.allinoneeditor.AdHelper;
import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.ShowCreationDialog;
import com.scorpion.allinoneeditor.Utility;
import com.scorpion.allinoneeditor.audioeditor.adapter.MergeAdapter;
import com.scorpion.allinoneeditor.videoeditor.activity.BaseActivity;
import com.scorpion.allinoneeditor.videoeditor.model.MediaItem;
import com.scorpion.allinoneeditor.videoeditor.utils.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.scorpion.allinoneeditor.AdHelper.LoadInter;
import static com.scorpion.allinoneeditor.AdHelper.mInterstitialAd;

//import com.scorpion.allinoneeditor.FBInterstitial;

public class MergeAudios extends BaseActivity
{
    ListView videoList;
    MergeAdapter audioAdapter;
    public static ArrayList<MediaItem> SONGS_LIST = new ArrayList<>();
    ImageView btnadd,btnmix;
    SaveTask task;
    String dstPath;
    ShowCreationDialog creationDialog;
    String  path;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mergeaudio);
        btnadd=findViewById(R.id.btnadd);
        btnmix=findViewById(R.id.btnmix);
        Toolbar toolbar = findViewById(R.id.header);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FrameLayout adContainerView = findViewById(R.id.adView1);
        adView = new AdView(this);
        adView.setAdUnitId(Utility.BannerID);
        adContainerView.addView(adView);
        loadBanner();
        AdHelper.AdLoadHelper(this, adView);
        creationDialog = new ShowCreationDialog(MergeAudios.this);
        videoList = (ListView) findViewById(R.id.song_list);
        SONGS_LIST.clear();
        Intent intent = getIntent();
        path= intent.getStringExtra("aud_path");
        int pos = intent.getIntExtra("pos",0);
        SONGS_LIST.add(Utils.mixitem);
        audioAdapter = new MergeAdapter(MergeAudios.this, R.layout.song_adapter_layout, SONGS_LIST);
        videoList.setAdapter(audioAdapter);
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SONGS_LIST.size()<10) {
                    Utils.posaudio = -1;
                    Intent i = new Intent(MergeAudios.this, SelectAudio.class);
                    startActivityForResult(i, 100);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Maximum 10 Songs can Merge",Toast.LENGTH_LONG).show();
                }
            }
        });

        btnmix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SONGS_LIST.size()!=0 && SONGS_LIST.size()>1) {

//                    FBInterstitial.getInstance().displayFBInterstitial(MergeAudios.this, new FBInterstitial.FbCallback() {
//                        public void callbackCall() {
                    ShowInter(MergeAudios.this);
//                        }
//                    });


                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Select atleast 2 songs to Merge",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 100 && i2 == -1) {

                SONGS_LIST.add(Utils.mixitem);
                audioAdapter = new MergeAdapter(MergeAudios.this, R.layout.song_adapter_layout, SONGS_LIST);
                videoList.setAdapter(audioAdapter);

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
        }

        public Void doInBackground(Void... voidArr) {
            MergeAudios videoConverter = MergeAudios.this;
            videoConverter.make(path);
            return null;
        }
    }
    int count = 0;
    public void make(String str2) {
        String format = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date());
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        final String name="/Audio_" + format + ".mp3";
        File file = new File(externalStorageDirectory, getString(R.string.app_name) + "/" + getString(R.string.mergeaudio));
        if (!file.exists()) {
            file.mkdir();
        }
        String[] strArr=null;
        dstPath = file.getAbsolutePath()+name;
//        String s="-i "+mFileTemp.getPath()+" -i "+mFileTemp2.getPath()+" -i "+mFileTemp3.getPath()
//                +" -filter_complex [0:0][1:0][2:0]concat=n=3:v=0:a=1[out] -map [out] "+originalFile.getPath();
        if(SONGS_LIST.size()==2) {
            strArr = new String[]{"-y",
                    "-i", SONGS_LIST.get(0).getPath(),
                    "-i", SONGS_LIST.get(1).getPath(),
                    "-filter_complex", "[0:0][1:0]concat=n=2:v=0:a=1[out]", "-map", "[out]", dstPath};
        }
        else if(SONGS_LIST.size()==3)
        {
            strArr = new String[]{"-y",
                    "-i", SONGS_LIST.get(0).getPath(),
                    "-i", SONGS_LIST.get(1).getPath(),
                    "-i", SONGS_LIST.get(2).getPath(),
                    "-filter_complex", "[0:0][1:0][2:0]concat=n=3:v=0:a=1[out]", "-map", "[out]", dstPath};
        }
        else if(SONGS_LIST.size()==4)
        {
            strArr = new String[]{"-y",
                    "-i", SONGS_LIST.get(0).getPath(),
                    "-i", SONGS_LIST.get(1).getPath(),
                    "-i", SONGS_LIST.get(2).getPath(),
                    "-i", SONGS_LIST.get(3).getPath(),
                    "-filter_complex", "[0:0][1:0][2:0][3:0]concat=n=4:v=0:a=1[out]", "-map", "[out]", dstPath};
        }
        else if(SONGS_LIST.size()==5)
        {
            strArr = new String[]{"-y",
                    "-i", SONGS_LIST.get(0).getPath(),
                    "-i", SONGS_LIST.get(1).getPath(),
                    "-i", SONGS_LIST.get(2).getPath(),
                    "-i", SONGS_LIST.get(3).getPath(),
                    "-i", SONGS_LIST.get(4).getPath(),
                    "-filter_complex", "[0:0][1:0][2:0][3:0][4:0]concat=n=5:v=0:a=1[out]", "-map", "[out]", dstPath};
        }
        else if(SONGS_LIST.size()==6)
        {
            strArr = new String[]{"-y",
                    "-i", SONGS_LIST.get(0).getPath(),
                    "-i", SONGS_LIST.get(1).getPath(),
                    "-i", SONGS_LIST.get(2).getPath(),
                    "-i", SONGS_LIST.get(3).getPath(),
                    "-i", SONGS_LIST.get(4).getPath(),
                    "-i", SONGS_LIST.get(5).getPath(),
                    "-filter_complex", "[0:0][1:0][2:0][3:0][4:0][5:0]concat=n=6:v=0:a=1[out]", "-map", "[out]", dstPath};
        }
        else if(SONGS_LIST.size()==7)
        {
            strArr = new String[]{"-y",
                    "-i", SONGS_LIST.get(0).getPath(),
                    "-i", SONGS_LIST.get(1).getPath(),
                    "-i", SONGS_LIST.get(2).getPath(),
                    "-i", SONGS_LIST.get(3).getPath(),
                    "-i", SONGS_LIST.get(4).getPath(),
                    "-i", SONGS_LIST.get(5).getPath(),
                    "-i", SONGS_LIST.get(6).getPath(),
                    "-filter_complex", "[0:0][1:0][2:0][3:0][4:0][5:0][6:0]concat=n=7:v=0:a=1[out]", "-map", "[out]", dstPath};
        }
        else if(SONGS_LIST.size()==8)
        {
            strArr = new String[]{"-y",
                    "-i", SONGS_LIST.get(0).getPath(),
                    "-i", SONGS_LIST.get(1).getPath(),
                    "-i", SONGS_LIST.get(2).getPath(),
                    "-i", SONGS_LIST.get(3).getPath(),
                    "-i", SONGS_LIST.get(4).getPath(),
                    "-i", SONGS_LIST.get(5).getPath(),
                    "-i", SONGS_LIST.get(6).getPath(),
                    "-i", SONGS_LIST.get(7).getPath(),
                    "-filter_complex", "[0:0][1:0][2:0][3:0][4:0][5:0][6:0][7:0]concat=n=8:v=0:a=1[out]", "-map", "[out]", dstPath};
        }
        else if(SONGS_LIST.size()==9)
        {
            strArr = new String[]{"-y",
                    "-i", SONGS_LIST.get(0).getPath(),
                    "-i", SONGS_LIST.get(1).getPath(),
                    "-i", SONGS_LIST.get(2).getPath(),
                    "-i", SONGS_LIST.get(3).getPath(),
                    "-i", SONGS_LIST.get(4).getPath(),
                    "-i", SONGS_LIST.get(5).getPath(),
                    "-i", SONGS_LIST.get(6).getPath(),
                    "-i", SONGS_LIST.get(7).getPath(),
                    "-i", SONGS_LIST.get(8).getPath(),
                    "-filter_complex", "[0:0][1:0][2:0][3:0][4:0][5:0][6:0][7:0][8:0]concat=n=9:v=0:a=1[out]", "-map", "[out]", dstPath};
        }
        else if(SONGS_LIST.size()==10)
        {
            strArr = new String[]{"-y",
                    "-i", SONGS_LIST.get(0).getPath(),
                    "-i", SONGS_LIST.get(1).getPath(),
                    "-i", SONGS_LIST.get(2).getPath(),
                    "-i", SONGS_LIST.get(3).getPath(),
                    "-i", SONGS_LIST.get(4).getPath(),
                    "-i", SONGS_LIST.get(5).getPath(),
                    "-i", SONGS_LIST.get(6).getPath(),
                    "-i", SONGS_LIST.get(7).getPath(),
                    "-i", SONGS_LIST.get(8).getPath(),
                    "-i", SONGS_LIST.get(9).getPath(),
                    "-filter_complex", "[0:0][1:0][2:0][3:0][4:0][5:0][6:0][7:0][8:0][9:0]concat=n=10:v=0:a=1[out]", "-map", "[out]", dstPath};

        }

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
            Intent intent = new Intent(MergeAudios.this, outputAudioPlayer.class);
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
//                            Intent intent = new Intent(MergeAudios.this, outputAudioPlayer.class);
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
//                    Intent intent = new Intent(MergeAudios.this, outputAudioPlayer.class);
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
