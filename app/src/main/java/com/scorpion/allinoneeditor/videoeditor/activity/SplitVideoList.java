package com.scorpion.allinoneeditor.videoeditor.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdView;
import com.scorpion.allinoneeditor.AdHelper;
import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.Utility;
import com.scorpion.allinoneeditor.videoeditor.adapter.SplitVideoGridAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

//import com.facebook.ads.AdSize;
//import com.facebook.ads.AdView;

public class SplitVideoList extends BaseActivity {

	SplitVideoGridAdapter adapter;
	GridView list;
	Cursor ecursor;
	public static ArrayList<String> mItems=null;
	LinearLayout pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_videolist);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		Toolbar toolbar = findViewById(R.id.header);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		String root = Environment.getExternalStorageDirectory().getPath()
				+ "/";
		File myDir = new File(root + getString(R.string.splitvideo));

		FrameLayout adContainerView = findViewById(R.id.adView1);
		adView = new AdView(this);
		adView.setAdUnitId(Utility.BannerID);
		adContainerView.addView(adView);
		loadBanner();
		AdHelper.AdLoadHelper(this, adView);

//		AdView adView;
//		//banner ad
//		adView = new AdView(SplitVideoList.this, getString(R.string.fb_banner_id), AdSize.BANNER_HEIGHT_50);
//
//		// Find the Ad Container
//		LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
//
//		// Add the ad view to your activity layout
//		adContainer.addView(adView);
//
//		// Request an ad
//		adView.loadAd();

		refreshGallery(myDir.getPath(),SplitVideoList.this);

		pd=(LinearLayout)findViewById(R.id.pd1);
		Log.e("dipika","hello");
		resetExternalStorageMedia(SplitVideoList.this);

		pd.setVisibility(View.VISIBLE);
		mItems = new ArrayList<String>();
		list = (GridView) findViewById(R.id.list);


		Handler h=new Handler();
		h.postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					Display_Grid();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		},3000);
	}

	@SuppressWarnings("deprecation")
	public void Display_Grid() throws IOException {
		// TODO Auto-generated method stub
		mItems.clear();
//		resetExternalStorageMedia(SplitVideoList.this);
//		final String MEDIA_DATA = MediaStore.Video.Media.DATA;
//		String whereData[] = { "%"
//				+ Environment.getExternalStorageDirectory()+"/"+getString(R.string.app_name) + "/" +getResources().getString(R.string.splitvideo)+"/Video_"+SplitVideo.format + "%" };
//		String[] parameters = { MediaStore.Video.Media._ID,
//				MediaStore.Video.Media.DATA,
//				MediaStore.Video.Media.DISPLAY_NAME,
//				MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DURATION,
//				MediaStore.Video.Media.DATE_ADDED, };
//
//		ecursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//				parameters, MEDIA_DATA + " like ? ", whereData,
//				MediaStore.Video.Media._ID);
//		ecursor.moveToFirst();
//
//		if (ecursor.moveToFirst()) {
//
//			do {
//
//				Uri uri = Uri.withAppendedPath(
//						MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//						getLong(ecursor));
//
//				final int TITLE = ecursor
//						.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
//
//				final String data_title = ecursor.getString(TITLE);
//
//				File c = new File(getRealPathFromURI(getApplicationContext(),
//						uri));
//
//				if (c.exists()) {
//
//					mItems.add(new CreatedGridviewItem(data_title, uri
//							.toString(), getRealPathFromURI(
//							getApplicationContext(), uri)));
//
//				}
//
//			} while (ecursor.moveToNext());
//		}
		File[] listFiles;

		File file = new File(Environment.getExternalStorageDirectory()
				+ "/" + getString(R.string.app_name) + "/" + getString(R.string.splitvideo)+"/Video_"+SplitVideo.format);
		if (file.exists() && (listFiles = file.listFiles()) != null) {
			for (File file2 : listFiles) {
				mItems.add(file2.getAbsolutePath());
			}
		}

		pd.setVisibility(View.GONE);
		adapter = new SplitVideoGridAdapter(
				SplitVideoList.this, mItems);
		list.setAdapter(adapter);
		adapter.notifyDataSetChanged();
//		for(int i=0;i<mItems.size();i++)
//		{
//			File from = new File(mItems.get(i).video_original_path);
//			String root = Environment.getExternalStorageDirectory().getPath()
//					+ "/";
////			File myDir = new File(root + getString(R.string.app_name));
//			File externalStorageDirectory = Environment.getExternalStorageDirectory();
//			File file = new File(externalStorageDirectory, getString(R.string.app_name) + "/" + getString(R.string.splitvideo));
//			file.mkdirs();
//			String fname = "video_" + System.currentTimeMillis() + ".mp4";
//			File  file1 = new File(file, fname);
//			String filePath=file1.getAbsolutePath();
//			File to = new File(filePath);
//
//			copyFile(from,to);
//			refreshGallery(filePath, SplitVideoList.this);
//		}

	}

	private void copyFile(File sourceFile, File destFile) throws IOException {

		if (!destFile.getParentFile().exists())
			destFile.getParentFile().mkdirs();

		if (!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;

		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		} finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
		resetExternalStorageMedia(SplitVideoList.this);
	}

	private String getLong(Cursor cursor) {
		int index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
		return "" + cursor.getLong(index);
	}

	public String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Video.Media.DATA };
			cursor = context.getContentResolver().query(contentUri, proj, null,
					null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	static public boolean resetExternalStorageMedia(Context context) {
		if (Environment.isExternalStorageEmulated())
			return (false);
		Uri uri = Uri.parse("file://" + Environment.getExternalStorageDirectory());
		Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, uri);

		context.sendBroadcast(intent);
		return (true);
	}

	private static void refreshGallery(String mCurrentPhotoPath, Context context) {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		context.sendBroadcast(mediaScanIntent);
	}
	public void callIntent(final String path, int pos) {

		Intent intent = new Intent(SplitVideoList.this,
				VideoViewActivity.class);
		intent.putExtra("videourl", path);
		intent.putExtra("fromactivity", "other");
		intent.putExtra("pos", "split");
		startActivity(intent);


	}

}
