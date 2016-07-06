package com.sohuvideo.playerdemo;

import android.content.Context;
import android.content.Intent;

import com.sohuvideo.api.SohuPlayerItemBuilder;
import com.sohuvideo.api.SohuVideoPlayer;

public class PlayVideoHelper {

	public static final String APPKEY = "test_asfdfljw;ekjr!wklvlk";
	public static final String APIKEY = "370f37af1847ee3308e77f86629f3955";
	public static final int TYPE_FOR_SOHU = 1;
	public static final int TYPE_FOR_LOCAL = 2;
	public static final int TYPE_FOR_LIVE = 3;
	public static final int TYPE_FOR_DOWNLOAD = 4;

	public static final String ID = "0";

	public static final String EXTRA_ID = "id";
	public static final String EXTRA_VID = "vid";
	public static final String EXTRA_TVID = "tvid";
	public static final String EXTRA_AID = "aid";
	public static final String EXTRA_SITE = "site";
	public static final String EXTRA_LIVEURL = "liveUrl";
	public static final String EXTRA_LOCALURL = "localUrl";
	public static final String EXTRA_APPKEY = "appkey";
	public static final String EXTRA_APIKEY = "apikey";
	public static final String EXTRA_SUBPARTNER = "subpartner";
	public static final String EXTRA_TASKINFOID = "taskInfoId";
	public static final String EXTRA_STARTPOSITION = "startPosition";//毫秒级别
	public static final String ACTION_PLAY = "sohu.intent.action.PLAYVIDEO";
	public static final String EXTRA_TYPE = "type";

	public static void playSohuOnlineVideo(Context context, long aid, long vid,
			int site, int startPosition) {
		Intent intent = new Intent(ACTION_PLAY);
		intent.putExtra(EXTRA_TYPE, TYPE_FOR_SOHU);
		intent.putExtra(EXTRA_AID, aid);
		intent.putExtra(EXTRA_VID, vid);
		intent.putExtra(EXTRA_SITE, site);
		intent.putExtra(EXTRA_APPKEY, APPKEY);
		intent.putExtra(EXTRA_STARTPOSITION, startPosition < 0 ? 0
				: startPosition);
		context.startActivity(intent);
	}

	public static void playSohuLiveVideo(Context context, int tvid,
			String liveUrl, int startPosition) {
		Intent intent = new Intent(ACTION_PLAY);
		intent.putExtra(EXTRA_TYPE, TYPE_FOR_LIVE);
		intent.putExtra(EXTRA_ID, ID);
		intent.putExtra(EXTRA_TVID, tvid);
		intent.putExtra(EXTRA_LIVEURL, liveUrl);
		intent.putExtra(EXTRA_APPKEY, APPKEY);
		intent.putExtra(EXTRA_STARTPOSITION, startPosition < 0 ? 0
				: startPosition);
		context.startActivity(intent);
	}

	public static void playLocalVideo(Context context, String localUrl,
			int startPosition) {
		Intent intent = new Intent(ACTION_PLAY);
		intent.putExtra(EXTRA_TYPE, TYPE_FOR_LOCAL);
		intent.putExtra(EXTRA_ID, ID);
		intent.putExtra(EXTRA_LOCALURL, localUrl);
		intent.putExtra(EXTRA_APPKEY, APPKEY);
		intent.putExtra(EXTRA_STARTPOSITION, startPosition < 0 ? 0
				: startPosition);
		context.startActivity(intent);
	}

	public static void playDownloadVideo(Context context, long taskInfoId,
			int startPosition) {
		//使用app播放方法
        SohuVideoPlayer mSohuVideoPlayer = new SohuVideoPlayer();
        SohuPlayerItemBuilder builder = new SohuPlayerItemBuilder(ID, taskInfoId, context);
        mSohuVideoPlayer.setDataSource(builder);
        mSohuVideoPlayer.play();
	}
}
