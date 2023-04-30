package com.scorpion.allinoneeditor.audioeditor.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AudioEditorModel implements Parcelable {
    private String name;
    private String image;

    public AudioEditorModel(String name, String image) {
        this.name = name;
        this.image = image;
    }

    protected AudioEditorModel(Parcel in) {
        name = in.readString();
        image = in.readString();
    }

    public static final Creator<AudioEditorModel> CREATOR = new Creator<AudioEditorModel>() {
        @Override
        public AudioEditorModel createFromParcel(Parcel in) {
            return new AudioEditorModel(in);
        }

        @Override
        public AudioEditorModel[] newArray(int size) {
            return new AudioEditorModel[size];
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
