package com.scorpion.allinoneeditor.audioeditor.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.audioeditor.Activity.outputAudioPlayer;
import com.scorpion.allinoneeditor.videoeditor.activity.AudioPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.scorpion.allinoneeditor.videoeditor.utils.Utils.frommain;

public class Mp3Adaptermycreation extends RecyclerView.Adapter<Mp3Adaptermycreation.MyViewHolder> {

    ArrayList<String> myList = new ArrayList<>();
    Context mContext;

    public Mp3Adaptermycreation(ArrayList<String> myList, Context mContext) {
        this.myList = myList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.song_adapter_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final File file = new File(myList.get(position));
        holder.videoname.setText(file.getName());
        holder.videostype.setText(String.valueOf(getDuration(myList.get(position))));
        holder.videosize.setVisibility(View.GONE);
        holder.adapterlinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frommain=false;
                mContext.startActivity(new Intent(mContext, outputAudioPlayer.class).putExtra("path",myList.get(position))
                .putExtra("name",file.getName()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView videoname;
        TextView videostype;
        TextView videosize;
        LinearLayout adapterlinear;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            videoname = itemView.findViewById(R.id.videoname);
            videostype = itemView.findViewById(R.id.videostype);
            videosize = itemView.findViewById(R.id.videosize);
            adapterlinear = itemView.findViewById(R.id.adapterlinear);
        }
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
