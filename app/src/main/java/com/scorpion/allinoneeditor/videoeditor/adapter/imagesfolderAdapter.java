package com.scorpion.allinoneeditor.videoeditor.adapter;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.videoeditor.fragment.VideoSplitFragment;
import com.scorpion.allinoneeditor.videoeditor.fragment.vidtoimagesFragment;

import java.io.File;
import java.util.ArrayList;

public class imagesfolderAdapter extends RecyclerView.Adapter<imagesfolderAdapter.MyViewHolder> {

    ArrayList<String> myList = new ArrayList<>();
    Context mContext;

    public imagesfolderAdapter(ArrayList<String> myLisyt, Context mContext) {
        this.myList = myLisyt;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.split_adapter_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        String result = myList.get(position).substring(myList.get(position).lastIndexOf('/') + 1).trim();
        holder.image.setText(result);
//        int w = AdHelper.getScreenWidth() - 10;
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//                w/2,
//                w/2);
//        params.gravity = Gravity.CENTER;
//        holder.ll.setLayoutParams(params);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mContext, VideoViewActivity.class);
//                intent.putExtra("videourl", myList.get(position));
//                mContext.startActivity(intent);
                String result = myList.get(position).substring(myList.get(position).lastIndexOf('/') + 1).trim();

                File[] listFiles;
                vidtoimagesFragment.fileArrayList.clear();
                File file = new File(Environment.getExternalStorageDirectory() + "/"
                        + mContext.getString(R.string.app_name) + "/" + mContext.getResources().getString(R.string.videotoimages)+"/"
                +result);
                if (file.exists() && (listFiles = file.listFiles()) != null) {
                    for (File file2 : listFiles) {
                        vidtoimagesFragment.fileArrayList.add(file2.getAbsolutePath());
                    }
                }
                vidtoimagesFragment.allInOneAdapter = new ImageAdapter(vidtoimagesFragment.fileArrayList, mContext);
                vidtoimagesFragment.recycler.setAdapter(vidtoimagesFragment.allInOneAdapter);
                vidtoimagesFragment.recycler.setVisibility(View.VISIBLE);
                vidtoimagesFragment.recyclerfolder.setVisibility(View.GONE);

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
        TextView image;
        LinearLayout ll;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            ll = itemView.findViewById(R.id.ll);
        }
    }
}
