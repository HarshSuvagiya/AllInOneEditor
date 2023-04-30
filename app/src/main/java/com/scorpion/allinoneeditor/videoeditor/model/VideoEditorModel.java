package com.scorpion.allinoneeditor.videoeditor.model;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoEditorModel implements Parcelable {
    private String name;
    private String image;

    public VideoEditorModel(String name, String image) {
        this.name = name;
        this.image = image;
    }

    protected VideoEditorModel(Parcel in) {
        name = in.readString();
        image = in.readString();
    }

    public static final Creator<VideoEditorModel> CREATOR = new Creator<VideoEditorModel>() {
        @Override
        public VideoEditorModel createFromParcel(Parcel in) {
            return new VideoEditorModel(in);
        }

        @Override
        public VideoEditorModel[] newArray(int size) {
            return new VideoEditorModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(image);
    }
}
