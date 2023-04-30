package com.scorpion.allinoneeditor.videoeditor.fragment;

import android.content.res.AssetManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.videoeditor.activity.AudioPlayer;
import com.scorpion.allinoneeditor.videoeditor.activity.BaseActivity;
import com.scorpion.allinoneeditor.videoeditor.adapter.VideoEditorAdapter;
import com.scorpion.allinoneeditor.videoeditor.model.VideoEditorModel;
import com.scorpion.allinoneeditor.AdHelper;
import com.scorpion.allinoneeditor.videoeditor.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class VideoEditorFragment extends Fragment implements VideoEditorAdapter.AdapterCallback {

    public static ArrayList<VideoEditorModel> myList = new ArrayList<>();
    ArrayList<String> filterList = new ArrayList<>();
    ArrayList<String> detailList = new ArrayList<>();
    FrameLayout frameLayout;
    RecyclerView recycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_editor, container, false);

        frameLayout = view.findViewById(R.id.ad_view_container);
        recycler = view.findViewById(R.id.recycler);

//        LinearLayout adContainer = (LinearLayout) view.findViewById(R.id.banner_container);
//        AdHelper.loadBannerFirst(getActivity(),adContainer);


        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File file = new File(externalStorageDirectory, getString(R.string.app_name));
        if (!file.exists()) {
            file.mkdir();
        }

        getList();

        VideoEditorAdapter videoEditorAdapter = new VideoEditorAdapter(myList,getActivity(),this);
        recycler.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recycler.setAdapter(videoEditorAdapter);
        return view;
    }

    private void getList() {
        AssetManager assetManager = getActivity().getAssets();
        try {
            filterList.clear();
            detailList.clear();
            myList.clear();
            String[] files = assetManager.list("VideoEditor");
            filterList = new ArrayList<String>(Arrays.asList(files));
            detailList.add("Video Compress");
            detailList.add("Video Trim");
            detailList.add("Video Converter");
            detailList.add("Mute Video");
            detailList.add("Video to mp3");
            detailList.add("Video to gif");
            detailList.add("Video and Audio");
            detailList.add("Rotate Video");
            detailList.add("Slow Video");
            detailList.add("Fast Video");
            detailList.add("Video Mirror");
            detailList.add("Reverse Video");
            detailList.add("Fade Video");

            for (int i = 0; i < filterList.size(); i++) {
                VideoEditorModel videoEditorModel = new VideoEditorModel(detailList.get(i),filterList.get(i));
                myList.add(videoEditorModel);
                Log.d("TestActivity", filterList.get(i));
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onMethodCallback(int pos) {
        Utils.position = pos;
        Log.e("POS", String.valueOf(pos));
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
//        transaction.replace(R.id.container, new SelectVideoFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Video Editor");
    }
}
