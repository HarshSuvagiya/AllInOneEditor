package com.scorpion.allinoneeditor.videoeditor.model;

import android.net.Uri;

public class VideoData {
    public String Duration;
    public Uri VideoUri;
    public long videoId;
    public String videoName;
    public String videoPath;

    public VideoData(String str, Uri uri, String str2, String str3) {
        this.videoName = str;
        this.VideoUri = uri;
        this.videoPath = str2;
        this.Duration = str3;
    }

    public VideoData(String str, Uri uri, String str2) {
        this.videoName = str;
        this.VideoUri = uri;
        this.videoPath = str2;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public Uri getVideoUri() {
        return VideoUri;
    }

    public void setVideoUri(Uri videoUri) {
        VideoUri = videoUri;
    }

    public long getVideoId() {
        return videoId;
    }

    public void setVideoId(long videoId) {
        this.videoId = videoId;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }
}
