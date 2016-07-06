/*
 * Copyright (c) 2013 Sohu TV. All rights reserved.
 */
package com.sohuvideo.playerdemo.controller;

import android.content.Context;

import com.sohuvideo.api.SohuPlayerItemBuilder;

public class ControllerFactory {
	public static SohuControllerWindow createController(Context ctx, int from) {
		switch (from) {
		case SohuPlayerItemBuilder.TYPE_ONLIVE_VIDEO_SOHU:
			return new SohuControllerWindow(ctx);
		case SohuPlayerItemBuilder.TYPE_LIVE_VIDEO_SOHU:
		case SohuPlayerItemBuilder.TYPE_LIVE_VIDEO_URL:
			return new LiveControllerPopWindow(ctx);
		case SohuPlayerItemBuilder.TYPE_ONLIVE_VIDEO_URL:
		case SohuPlayerItemBuilder.TYPE_LOCAL_VIDEO_URL:
		case SohuPlayerItemBuilder.TYPE_LOCAL_VIDEO_DOWNLOAD:
			return new LocalControllerPopWindow(ctx);
		default:
			break;
		}
		return null;
	}
}
