package com.scorpion.allinoneeditor.videoeditor.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.videoeditor.activity.VideoViewActivity;
import com.scorpion.allinoneeditor.AdHelper;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class AllInOneAdapter extends RecyclerView.Adapter<AllInOneAdapter.MyViewHolder> {

    ArrayList<String> myList = new ArrayList<>();
    Context mContext;

    public AllInOneAdapter(ArrayList<String> myLisyt, Context mContext) {
        this.myList = myLisyt;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.all_in_one_adapter_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new RoundedCorners(36));
        Glide.with(mContext).load((String) myList.get(position))
                .centerCrop().apply(requestOptions).transition(withCrossFade())
                .into(holder.image);

        int w = AdHelper.getScreenWidth() - 50;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                w/2,
                w/2);
        params.gravity = Gravity.CENTER;
        params.setMargins(20,20,20,20);
        holder.ll.setLayoutParams(params);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VideoViewActivity.class);
                intent.putExtra("videourl", myList.get(position));
                mContext.startActivity(intent);
            }
        });

//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int heightDevice = displayMetrics.heightPixels;
//        int widthDevice = displayMetrics.widthPixels;
//
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(myList.get(position));
//        int width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
//        int height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
//        Log.e("SizeHeight", String.valueOf(height));
//        Log.e("SizeWidth", String.valueOf(width));
//
//        LinearLayout.LayoutParams relativeParams = new LinearLayout.LayoutParams(
//                widthDevice/2, ViewGroup.LayoutParams.WRAP_CONTENT);
//        holder.linearLayout.setLayoutParams(relativeParams);
//
//        retriever.release();
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        LinearLayout ll;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            ll = itemView.findViewById(R.id.ll);
        }
    }
}
