package com.sohuvideo.playerdemo.net;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.sohuvideo.playerdemo.entity.Video;
import com.sohuvideo.playerdemo.entity.VideoList;

/**
 * 获取某一个视频详情的网络请求
 * 
 * @author gaoyihang
 * 
 */
public class AlbumListProtocol extends BaseProtocol<VideoList> {
	private static final String TAG = "VideoListProtocol";
	private static final String URL = "http://open.mb.hd.sohu.com/v4/album/videos/";
	private long aid;
	private String apikey;
	private long pageSize;
	private int page;

	// request
	// :http://open.mb.hd.sohu.com/v4/album/videos/5136748.json?api_key=e6abdb19a7083052eee81afc0b3da0a9&page=1&page_size=10

	public AlbumListProtocol(Context context, long aid, long pageSize, String apikey, int page) {
		super(context);
		this.aid = aid;
		this.apikey = apikey;
		this.pageSize = pageSize;
		this.page = page;
	}

	@Override
	public String makeRequest() {
		return URL + aid
				+ ".json?page="+ page + "&api_key="+ apikey + "&page_size=" + pageSize;
	}

	@Override
	public VideoList handleResponse(String response) {
		Log.d(TAG, "response " + response);
		VideoList videoList = new VideoList();
		try {
			JSONObject jsonObject = new JSONObject(response)
					.getJSONObject("data");
			videoList.setCount(jsonObject.getInt(VideoList.COUNT));
			JSONArray videoJsonArray = jsonObject
					.getJSONArray(VideoList.VIDEOS);
			ArrayList<Video> videos = new ArrayList<Video>();
			Video video = null;
			for (int i = 0; i < videoJsonArray.length(); i++) {
				JSONObject videoJsonObject = videoJsonArray.getJSONObject(i);
				video = new Video();
				video.setAid(videoJsonObject.optLong(Video.AID));
				video.setAlbum_desc(videoJsonObject.optString(Video.ALBUM_DESC));
				video.setAlbum_name(videoJsonObject.optString(Video.ALBUM_NAME));
				video.setArea(videoJsonObject.optString(Video.AREA));
				video.setArea_id(videoJsonObject.optInt(Video.AREA_ID));
				video.setCate_code(videoJsonObject.optString(Video.CATE_CODE));
				video.setCid(videoJsonObject.optInt(Video.CID));
				video.setDirector(videoJsonObject.optString(Video.DIRECTOR));
				video.setHor_high_pic(videoJsonObject
						.optString(Video.HOR_HIGH_PIC));
				video.setHor_pic(videoJsonObject.optString(Video.HOR_PIC));
				video.setIs_album(videoJsonObject.optInt(Video.IS_ALBUM));
				video.setIs_download(videoJsonObject.optInt(Video.IS_DOWNLOAD));
				video.setIs_original_code(videoJsonObject
						.optInt(Video.IS_ORIGINAL_CODE));
				video.setIs_sohuown(videoJsonObject.optInt(Video.IS_SOHUOWN));
				video.setIs_super_code(videoJsonObject
						.optInt(Video.IS_SUPER_CODE));
				video.setIs_trailer(videoJsonObject.optInt(Video.IS_TRAILER));
				video.setLanguage(videoJsonObject.optString(Video.LANGUAGE));
				video.setLanguage_id(videoJsonObject.optInt(Video.LANGUAGE_ID));
				video.setLatest_video_count(videoJsonObject
						.optInt(Video.LATEST_VIDEO_COUNT));
				video.setMain_actor(videoJsonObject.optString(Video.MAIN_ACTOR));
				video.setPlay_count(videoJsonObject.optLong(Video.PLAY_COUNT));
				video.setScore(videoJsonObject.optString(Video.SCORE));
				video.setSearch_name(videoJsonObject
						.optString(Video.SEARCH_NAME));
				video.setSite(videoJsonObject.optInt(Video.SITE));
				video.setTime_length(videoJsonObject.optLong(Video.TIME_LENGTH));
				video.setTip(videoJsonObject.optString(Video.TIP));
				video.setTotal_video_count(videoJsonObject
						.optInt(Video.TOTAL_VIDEO_COUNT));
				video.setUpdate_status(videoJsonObject
						.optInt(Video.UPDATE_STATUS));
				video.setUpdate_time(videoJsonObject
						.optString(Video.UPDATE_TIME));
				video.setUpdateNotification(videoJsonObject
						.optString(Video.UPDATENOTIFICATION));
				video.setVer_high_pic(videoJsonObject
						.optString(Video.VER_HIGH_PIC));
				video.setVer_pic(videoJsonObject.optString(Video.VER_PIC));
				video.setVid(videoJsonObject.optLong(Video.VID));
				video.setVideo_desc(videoJsonObject.optString(Video.VIDEO_DESC));
				video.setVideo_name(videoJsonObject.optString(Video.VIDEO_NAME));
				video.setVideo_order(videoJsonObject.optInt(Video.VIDEO_ORDER));
				videos.add(video);
			}
			videoList.setVideos(videos);
		} catch (Exception e) {
			Log.d(TAG, "json resolve error" + e.getMessage());
		}
		return videoList;
	}

	@Override
	protected void handleError(int errorCode) {

	}

}
