package com.scorpion.allinoneeditor.audioeditor.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.videoeditor.model.MediaItem;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class AllInOneAudioAdapter extends ArrayAdapter<MediaItem> {
    Activity context;
    LayoutInflater inflator;
    ArrayList<MediaItem> listOfSongs;
    LayoutInflater viewInflater;

    private class ViewHolder {
        int id;
        ImageView imageview;
        ConstraintLayout main;
        LinearLayout parent_container;
        TextView textView;
        TextView textsize;
        TextView texttype;

        private ViewHolder() {
        }
    }

    public AllInOneAudioAdapter(Activity selectAudioActivity, int i, ArrayList<MediaItem> arrayList) {
        super(selectAudioActivity, i, arrayList);
        listOfSongs = arrayList;
        context = selectAudioActivity;
        inflator = LayoutInflater.from(selectAudioActivity);
        viewInflater = LayoutInflater.from(selectAudioActivity);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (listOfSongs.get(i).isFlag()) {
            return (View) listOfSongs.get(i).getObj();
        }
        if (view == null || !(view instanceof LinearLayout)) {
            view = inflator.inflate(R.layout.song_adapter_layout, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.imageview = (ImageView) view.findViewById(R.id.icon);
            viewHolder.main = (ConstraintLayout) view.findViewById(R.id.main);
            viewHolder.textView = (TextView) view.findViewById(R.id.videoname);
            viewHolder.texttype = (TextView) view.findViewById(R.id.videostype);
            viewHolder.textsize = (TextView) view.findViewById(R.id.videosize);
            viewHolder.parent_container = (LinearLayout) view.findViewById(R.id.linear_container);
            viewHolder.imageview.setPadding(5, 5, 5, 5);
//            setSize(viewHolder);
            view.setTag(viewHolder);
        } else if (view.getTag() != null) {
            viewHolder = (ViewHolder) view.getTag();
        } else {
            view = inflator.inflate(R.layout.song_adapter_layout, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.imageview = (ImageView) view.findViewById(R.id.icon);
            viewHolder.main = (ConstraintLayout) view.findViewById(R.id.main);
            viewHolder.textView = (TextView) view.findViewById(R.id.videoname);
            viewHolder.texttype = (TextView) view.findViewById(R.id.videostype);
            viewHolder.textsize = (TextView) view.findViewById(R.id.videosize);
            viewHolder.parent_container = (LinearLayout) view.findViewById(R.id.linear_container);
            viewHolder.imageview.setPadding(5, 5, 5, 5);
            view.setTag(viewHolder);
        }
        MediaItem mediaItem = listOfSongs.get(i);
        viewHolder.textView.setText(mediaItem.toString());
        viewHolder.texttype.setText(mediaItem.getAlbum());
        viewHolder.textsize.setText(getTimeForTrackFormat(mediaItem.getDuration()));
        viewHolder.id = i;
        return view;
    }


    public int convertToDp(int i) {
        return (int) ((((float) i) * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public int setIndex() {
        int nextInt;
        do {
            nextInt = new Random().nextInt(3) + 1;
        } while (listOfSongs.size() < nextInt);
        return nextInt;
    }

    public static String getTimeForTrackFormat(long j) {
        return String.format("%02d:%02d:%02d", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toHours(j)), Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(j) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(j))), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(j) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(j)))});
    }
}
