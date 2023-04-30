package com.scorpion.allinoneeditor.audioeditor.model;

import android.net.Uri;

public class AudioData {
    public String Duration;
    public Uri AudioUri;
    public long audioId;
    public String audioName;
    public String audioPath;

    public AudioData(String str, Uri uri, String str2, String str3) {
        this.audioName = str;
        this.AudioUri = uri;
        this.audioPath = str2;
        this.Duration = str3;
    }

    public AudioData(String str, Uri uri, String str2) {
        this.audioName = str;
        this.AudioUri = uri;
        this.audioPath = str2;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public Uri getVideoUri() {
        return AudioUri;
    }

    public void setVideoUri(Uri videoUri) {
        AudioUri = videoUri;
    }

    public long getVideoId() {
        return audioId;
    }

    public void setVideoId(long videoId) {
        this.audioId = videoId;
    }

    public String getVideoName() {
        return audioName;
    }

    public void setVideoName(String videoName) {
        this.audioName = videoName;
    }

    public String getVideoPath() {
        return audioPath;
    }

    public void setVideoPath(String videoPath) {
        this.audioPath = videoPath;
    }
}
