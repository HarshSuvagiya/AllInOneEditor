package com.scorpion.allinoneeditor.videoeditor.activity;

public class CreatedGridviewItem {

	public final String video_name;
	public final String video_uri;
	public final String video_original_path;

	public CreatedGridviewItem(String name, String uri, String original_path) {
		this.video_name = name;
		this.video_uri = uri;
		this.video_original_path = original_path;
	}

}
