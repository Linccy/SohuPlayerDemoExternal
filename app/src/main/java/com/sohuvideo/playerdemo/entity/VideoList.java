package com.sohuvideo.playerdemo.entity;

import java.util.ArrayList;

public class VideoList {
	public static final String COUNT = "count";
	public static final String VIDEOS = "videos";

	private int count;
	private ArrayList<Video> videos = new ArrayList<Video>();

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public ArrayList<Video> getVideos() {
		return videos;
	}

	public void setVideos(ArrayList<Video> videos) {
		this.videos = videos;
	}
	
	public void addVideos(ArrayList<Video> videos){
		this.videos.addAll(videos);
	}
	

}
