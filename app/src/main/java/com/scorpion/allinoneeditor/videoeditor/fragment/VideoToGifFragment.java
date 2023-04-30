package com.scorpion.allinoneeditor.videoeditor.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.videoeditor.adapter.AllInOneAdapter;
import com.scorpion.allinoneeditor.videoeditor.adapter.GIFAdapter;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoToGifFragment extends Fragment {

    public ArrayList<String> fileArrayList = new ArrayList<>();
    RecyclerView recycler;
    GIFAdapter gifAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compress_video, container, false);

        getVideo();
        recycler = view.findViewById(R.id.recycler);
        recycler.setLayoutManager(new GridLayoutManager(getActivity(),2));
        gifAdapter = new GIFAdapter(fileArrayList,getActivity());
        recycler.setAdapter(gifAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getVideo();
        gifAdapter.notifyDataSetChanged();
    }

    public void getVideo() {
        File[] listFiles;
        fileArrayList.clear();
        File file = new File(Environment.getExternalStorageDirectory() + "/"+ getString(R.string.app_name) + "/" + getResources().getString(R.string.gif));
        if (file.exists() && (listFiles = file.listFiles()) != null) {
            for (File file2 : listFiles) {
//                if (file2.getName().endsWith("gif") && file2.length() / 1024 > 10) {
                    fileArrayList.add(file2.getAbsolutePath());
//                }
            }
        }
    }
}
