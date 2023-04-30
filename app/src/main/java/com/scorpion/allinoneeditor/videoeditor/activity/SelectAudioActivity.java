package com.scorpion.allinoneeditor.videoeditor.activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdView;
import com.scorpion.allinoneeditor.AdHelper;
import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.Utility;
import com.scorpion.allinoneeditor.audioeditor.Activity.AmplifierAudio;
import com.scorpion.allinoneeditor.videoeditor.model.MediaItem;
import com.scorpion.allinoneeditor.videoeditor.adapter.SongAdapter;

import java.util.ArrayList;

public class SelectAudioActivity extends BaseActivity {

    public static String path;
    static final String METADATA_KEY_ALBUM = "album";
    static String METADATA_KEY_ARTIST = "artist";
    static String METADATA_KEY_DURATION = "duration";
    ArrayList<MediaItem> SONGS_LIST = new ArrayList<>();
    SongAdapter audioAdapter;
    ListView videoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_audio);

        Toolbar toolbar = findViewById(R.id.header);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FrameLayout adContainerView = findViewById(R.id.adView1);
        adView = new AdView(this);
        adView.setAdUnitId(Utility.BannerID);
        adContainerView.addView(adView);
        loadBanner();
        AdHelper.AdLoadHelper(this, adView);

        videoList = (ListView) findViewById(R.id.song_list);
        SONGS_LIST = listOfSongs(this);
        audioAdapter = new SongAdapter(this, R.layout.song_adapter_layout, SONGS_LIST);
        videoList.setAdapter(audioAdapter);
        videoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                SelectAudioActivity.path = SONGS_LIST.get(i).getPath();
                setResult(-1);
                finish();
            }
        });
    }

    public static ArrayList<MediaItem> listOfSongs(Context context) {
        Cursor query = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, (String[]) null, (String) null, (String[]) null, "_display_name");
        ArrayList<MediaItem> arrayList = new ArrayList<>();
        if (query.getCount() > 0) {
            query.moveToFirst();
            do {
                MediaItem mediaItem = new MediaItem(false);
                String string = query.getString(query.getColumnIndex("_display_name"));
                String string2 = query.getString(query.getColumnIndex(METADATA_KEY_ARTIST));
                String string3 = query.getString(query.getColumnIndex(METADATA_KEY_ALBUM));
                long j = query.getLong(query.getColumnIndex(METADATA_KEY_DURATION));
                String string4 = query.getString(query.getColumnIndex("_data"));
                long j2 = query.getLong(query.getColumnIndex("album_id"));
                mediaItem.setTitle(string);
                mediaItem.setAlbum(string3);
                mediaItem.setArtist(string2);
                mediaItem.setDuration(j);
                mediaItem.setPath(string4);
                mediaItem.setAlbumId(j2);
                if (j >= 100) {
                    String str = "mp3";
                    try {
                        str = string4.trim().substring(string4.trim().lastIndexOf(".") + 1);
                    } catch (Exception unused) {
                    }
                    if (!str.equals("ogg") && !str.equals("m4v") && !str.equals("3gp") && !str.equals("amr") && !str.equals("wav")) {
                        arrayList.add(mediaItem);
                    }
                }
            } while (query.moveToNext());
        }
        query.close();
        return arrayList;
    }

}
