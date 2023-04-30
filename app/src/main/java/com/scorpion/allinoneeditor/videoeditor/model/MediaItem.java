package com.scorpion.allinoneeditor.videoeditor.model;

public class MediaItem {
    String album;
    long albumId;
    String artist;
    String composer;
    long duration;
    private boolean flag;
    Object obj;
    String path;
    String title;

    public boolean isFlag() {
        return this.flag;
    }

    public MediaItem(boolean z) {
        this.flag = z;
    }

    public MediaItem(Object obj2, boolean z) {
        this.obj = obj2;
        this.flag = z;
    }

    public Object getObj() {
        return this.obj;
    }

    public String toString() {
        return this.title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String str) {
        this.artist = str;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setAlbum(String str) {
        this.album = str;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String str) {
        this.path = str;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long j) {
        this.duration = j;
    }

    public long getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(long j) {
        this.albumId = j;
    }

    public String getComposer() {
        return this.composer;
    }

    public void setComposer(String str) {
        this.composer = str;
    }
}
