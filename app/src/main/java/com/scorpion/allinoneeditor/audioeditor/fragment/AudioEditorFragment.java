package com.scorpion.allinoneeditor.audioeditor.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scorpion.allinoneeditor.R;

public class AudioEditorFragment extends Fragment {

    public AudioEditorFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio_editor, container, false);
        getActivity().setTitle("Audio Editor");
        return view;
    }
}
