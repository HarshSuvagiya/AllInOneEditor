package com.scorpion.allinoneeditor.videoeditor.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.FileProvider;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.scorpion.allinoneeditor.AdHelper;
import com.scorpion.allinoneeditor.BuildConfig;
import com.scorpion.allinoneeditor.videoeditor.activity.CreatedGridviewItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.videoeditor.activity.SplitVideoList;
//import com.scorpion.allinoneeditor.videoeditor.utils.AdHelper;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class SplitVideoGridAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mItems;
    LayoutInflater inflater;

    public SplitVideoGridAdapter(Context context,
                                ArrayList<String> items) {
        mContext = context;
        mItems = items;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void remove(int index) {
        mItems.remove(index);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        convertView = inflater.inflate(R.layout.video_list_item,
                parent, false);

        viewHolder = new ViewHolder();

        viewHolder.imageViewIcon = (ImageView) convertView
                .findViewById(R.id.imageViewIcon);

        viewHolder.tvName = (TextView) convertView
                .findViewById(R.id.textViewName);
        viewHolder.r1 = (RelativeLayout) convertView
                .findViewById(R.id.r1);


        convertView.setTag(viewHolder);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new RoundedCorners(36));
        Glide.with(mContext).load((String) mItems.get(position))
                .centerCrop().apply(requestOptions).transition(withCrossFade())
                .into(viewHolder.imageViewIcon);


//        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(item.video_original_path,
//                MediaStore.Images.Thumbnails.MINI_KIND);
//        viewHolder.imageViewIcon.setImageBitmap(thumb);
//        viewHolder.tvName.setText(item.video_name);

        int w = AdHelper.getScreenWidth() - 10;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                w/2,
                w/2);
        params.gravity = Gravity.CENTER;
        viewHolder.r1.setLayoutParams(params);

        viewHolder.r1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                    ((SplitVideoList) mContext).callIntent(
                            mItems.get(position), position);

            }
        });

        return convertView;
    }

    /**
     * The view holder design pattern prevents using findViewById() repeatedly
     * in the getView() method of the adapter.
     *
     * @see http
     * ://developer.android.com/training/improving-layouts/smooth-scrolling
     * .html#ViewHolder
     */
    private static class ViewHolder {

        RelativeLayout r1;
        TextView tvName;
        ImageView imageViewIcon;

    }

}
