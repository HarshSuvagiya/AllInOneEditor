package com.scorpion.allinoneeditor.videoeditor.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.videoeditor.activity.SelectVideoActivity;
import com.scorpion.allinoneeditor.videoeditor.model.VideoData;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class SelectVideoAdapter extends BaseAdapter {
    private LayoutInflater infalter;
    public Context mContext;
    ArrayList<VideoData> populatelist = new ArrayList<>();
    ArrayList<VideoData> videoList = new ArrayList<>();

    public long getItemId(int i) {
        return (long) i;
    }

    private class ViewHolder {
        ImageView ico;
        ImageView ivVideoThumb;
        ConstraintLayout main;
        ImageView next_button;
        TextView tvDuration;
        TextView tvVideoName;

        private ViewHolder() {
        }
    }

    public SelectVideoAdapter(Context context, ArrayList<VideoData> arrayList) {
        this.mContext = context;
        this.infalter = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.videoList.addAll(arrayList);
        this.populatelist.addAll(arrayList);
        Log.e("SIZE", String.valueOf(videoList.size()));
    }

    public int getCount() {
        return this.videoList.size();
    }

    public Object getItem(int i) {
        return this.videoList.get(i);
    }

    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            view = this.infalter.inflate(R.layout.row_video, (ViewGroup) null);
            viewHolder = new ViewHolder();
            viewHolder.next_button = (ImageView) view.findViewById(R.id.next_button);
            viewHolder.ivVideoThumb = (ImageView) view.findViewById(R.id.image_preview);
            viewHolder.tvVideoName = (TextView) view.findViewById(R.id.file_name);
            viewHolder.ico = (ImageView) view.findViewById(R.id.ico);
            viewHolder.main = (ConstraintLayout) view.findViewById(R.id.main);
            viewHolder.tvDuration = (TextView) view.findViewById(R.id.duration);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Uri parse = Uri.parse("file://" + videoList.get(i));
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
        Glide.with(this.mContext).load(videoList.get(i).getVideoPath()).centerCrop().apply(requestOptions).transition(withCrossFade()).into(viewHolder.ivVideoThumb);

        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ((SelectVideoActivity) SelectVideoAdapter.this.mContext).callVideo(SelectVideoAdapter.this.videoList.get(i).videoPath);
            }
        });
        viewHolder.tvVideoName.setText("" + this.videoList.get(i).videoName);
        viewHolder.tvDuration.setText(getDuration(videoList.get(i).getVideoPath()));
//        Log.e("DURATION123",videoList.get(i).getVideoPath());
        return view;
    }

    public String getDuration(String URL){
        try {
            MediaPlayer mp = MediaPlayer.create(mContext, Uri.parse(URL));
            int duration = mp.getDuration();
            mp.release();
            if (TimeUnit.MILLISECONDS.toMinutes(duration) == 0) {
                return String.format("%d sec",
                        TimeUnit.MILLISECONDS.toSeconds(duration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                );
            } else if (TimeUnit.MILLISECONDS.toHours(duration) == 0) {
                return String.format("%d : %d min",
                        TimeUnit.MILLISECONDS.toMinutes(duration),
                        TimeUnit.MILLISECONDS.toSeconds(duration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                );
            } else {
                return String.format("%d : %d : %d hour",
                        TimeUnit.MILLISECONDS.toHours(duration),
                        TimeUnit.MILLISECONDS.toMinutes(duration),
                        TimeUnit.MILLISECONDS.toSeconds(duration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                );
            }
        }
        catch (Exception e){
            Log.e("EXE", String.valueOf(e));
            return null;
        }
    }

}
