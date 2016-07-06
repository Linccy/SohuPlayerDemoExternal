package com.sohuvideo.playerdemo.utils;

import android.widget.ImageView;

import com.sohuvideo.playerdemo.imagehepler.ImageDownloader;

public class ImageUtils {

	public static void download(String picUrl, ImageView imageView) {
		ImageDownloader loader = new ImageDownloader();
		loader.download(picUrl, imageView);
	}
}
