package com.scorpion.allinoneeditor.audioeditor.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.audioeditor.adapter.AllInOneAudioAdapter;
import com.scorpion.allinoneeditor.audioeditor.adapter.Mp3Adaptermycreation;
import com.scorpion.allinoneeditor.videoeditor.adapter.AllInOneAdapter;
import com.scorpion.allinoneeditor.videoeditor.adapter.Mp3Adapter;
import com.scorpion.allinoneeditor.videoeditor.model.MediaItem;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MutePartFragment extends Fragment {

    public ArrayList<String> fileArrayList = new ArrayList<>();
    RecyclerView recycler;
    Mp3Adaptermycreation mp3Adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentaudio, container, false);

        getVideo();
        recycler = view.findViewById(R.id.recycler);
        recycler.setLayoutManager(new GridLayoutManager(getActivity(),1));
        mp3Adapter = new Mp3Adaptermycreation(fileArrayList,getActivity());
        recycler.setAdapter(mp3Adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getVideo();
        mp3Adapter.notifyDataSetChanged();
    }

    public void getVideo() {
        File[] listFiles;
        fileArrayList.clear();
        File file = new File(Environment.getExternalStorageDirectory() + "/"+ getString(R.string.app_name) + "/" + getResources().getString(R.string.mutepart));
        if (file.exists() && (listFiles = file.listFiles()) != null) {
            for (File file2 : listFiles) {
//                if (file2.getName().endsWith("mp3") && file2.length() / 1024 > 10) {
                    fileArrayList.add(file2.getAbsolutePath());
//                }
            }
        }
    }

}
