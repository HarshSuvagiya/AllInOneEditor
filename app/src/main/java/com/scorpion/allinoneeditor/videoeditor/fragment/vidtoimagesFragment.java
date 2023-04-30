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
import com.scorpion.allinoneeditor.videoeditor.adapter.ImageAdapter;
import com.scorpion.allinoneeditor.videoeditor.adapter.imagesfolderAdapter;
import com.scorpion.allinoneeditor.videoeditor.adapter.splitfolderAdapter;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class vidtoimagesFragment extends Fragment {

    public static ArrayList<String> fileArrayList = new ArrayList<>();
    public static RecyclerView recycler,recyclerfolder;
    public ArrayList<String> fileArrayListfolder = new ArrayList<>();
   public static ImageAdapter allInOneAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_split_video, container, false);

        getVideo();
        getfolder();
        recycler = view.findViewById(R.id.recycler);
        recycler.setLayoutManager(new GridLayoutManager(getActivity(),2));
        allInOneAdapter = new ImageAdapter(fileArrayList,getActivity());
        recycler.setAdapter(allInOneAdapter);

        recyclerfolder=view.findViewById(R.id.recyclerfolder);
        recyclerfolder.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        imagesfolderAdapter folder=new imagesfolderAdapter(fileArrayListfolder,getActivity());
        recyclerfolder.setAdapter(folder);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getfolder();
        getVideo();
        allInOneAdapter.notifyDataSetChanged();
    }

    public  void getVideo() {
        File[] listFiles;
        fileArrayList.clear();
        File file = new File(Environment.getExternalStorageDirectory()
                + "/" + getString(R.string.app_name) + "/" + getResources().getString(R.string.videotoimages)+"/");
        if (file.exists() && (listFiles = file.listFiles()) != null) {
            for (File file2 : listFiles) {
                fileArrayList.add(file2.getAbsolutePath());
            }
        }
    }

    public void getfolder() {
        File[] listFiles;
        fileArrayListfolder.clear();
        File file = new File(Environment.getExternalStorageDirectory()
                + "/" + getString(R.string.app_name)+ "/" + getResources().getString(R.string.videotoimages));
        if (file.exists() && (listFiles = file.listFiles()) != null) {
            for (File file2 : listFiles) {
                fileArrayListfolder.add(file2.getAbsolutePath());
            }
        }
    }
}
