package com.sohuvideo.playerdemo.controller;

import android.content.Context;

import com.sohuvideo.api.SohuPlayerItemBuilder;

public class LiveControllerPopWindow extends SohuControllerWindow {


	public LiveControllerPopWindow(Context context) {
		super(context);
		mContext = context;
		setDisplayModeType(MODE_TYPE.MODE_LIVE);
	}

	public LiveControllerPopWindow(Context context, int width, int height) {
		super(context, width, height);
	}

	@Override
	public void setCurrentPlayingIndex(SohuPlayerItemBuilder builder, int index) {
		this.activeItem = builder;
	}

}
