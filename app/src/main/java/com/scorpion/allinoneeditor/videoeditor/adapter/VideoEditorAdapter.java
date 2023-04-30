package com.scorpion.allinoneeditor.videoeditor.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.videoeditor.model.VideoEditorModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class VideoEditorAdapter extends RecyclerView.Adapter<VideoEditorAdapter.MyViewHolder> {

    public ArrayList<VideoEditorModel> myList = new ArrayList<>();
    Context mContext;
    private AdapterCallback mAdapterCallback;

    public interface AdapterCallback {
        void onMethodCallback(int pos);
    }

    public VideoEditorAdapter(ArrayList<VideoEditorModel> myList, Context mContext,AdapterCallback callback) {
        this.myList = myList;
        this.mContext = mContext;
        mAdapterCallback = callback;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.video_editor_adapter_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        InputStream ims = null;
        try {
            ims = mContext.getAssets().open("VideoEditor/" + myList.get(position).getImage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap bitmap = BitmapFactory.decodeStream(ims);
        holder.image.setImageBitmap(bitmap);
        holder.title.setText(myList.get(position).getName());

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapterCallback.onMethodCallback(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView title;
        ConstraintLayout mainLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
