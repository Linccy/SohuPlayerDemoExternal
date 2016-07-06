package com.sohuvideo.playerdemo.net;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sohuvideo.playerdemo.entity.PgcChannel;
import com.sohuvideo.playerdemo.entity.Video;
import com.sohuvideo.playerdemo.entity.VideoList;

/**
 * 获取某一个视频详情的网络请求
 *
 * @author gaoyihang
 */
public class VideoListProtocol extends BaseProtocol<VideoList> {
    private static final String TAG = "VideoListProtocol";
    private static final String URL = "http://open.mb.hd.sohu.com/v4/category/channel/";
    private String apikey;
    private String more_list;
    private PgcChannel mPgcChannel;

    // http://open.mb.hd.sohu.com/v4/category/channel/sub.json?subId=16&api_key=e953736f623d603c21cba026509b2ce9
    public VideoListProtocol(Context context, String more_list, String apikey) {
        super(context);
        this.apikey = apikey;
        this.more_list = more_list;
    }

    // 取格子2级分类下面的 videos
    // http://open.mb.hd.sohu.com/v4/category/channel/29.json?api_key=e953736f623d603c21cba026509b2ce9&cat=126100&o=&area=&year=&page=&page_size=20&is_pgc=1
    public VideoListProtocol(Context context, PgcChannel mPgcChannel, String apikey) {
        super(context);
        this.mPgcChannel = mPgcChannel;
        this.apikey = apikey;
    }

    @Override
    public String makeRequest() {
        if (mPgcChannel != null) {
            return URL + mPgcChannel.first_cate_code + ".json?api_key=" + apikey + "&page=1&page_size=20&cat="
                    + mPgcChannel.second_cate_code + "&is_pgc=1";
        }
        return more_list + "&api_key=" + apikey;
    }

    @Override
    public VideoList handleResponse(String response) {
        Log.d(TAG, "response " + response);
        VideoList videoList = new VideoList();
        try {
            JSONObject jsonObject = new JSONObject(response).getJSONObject("data");
            int video_count = jsonObject.getInt(VideoList.COUNT);
            videoList.setCount(video_count);
            if (video_count == 0) {
                return videoList;
            }
            JSONArray videoJsonArray = jsonObject.getJSONArray(VideoList.VIDEOS);
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
                video.setHor_high_pic(videoJsonObject.optString(Video.HOR_HIGH_PIC));
                video.setHor_pic(videoJsonObject.optString(Video.HOR_PIC));
                video.setIs_album(videoJsonObject.optInt(Video.IS_ALBUM));
                video.setIs_download(videoJsonObject.optInt(Video.IS_DOWNLOAD));
                video.setIs_original_code(videoJsonObject.optInt(Video.IS_ORIGINAL_CODE));
                video.setIs_sohuown(videoJsonObject.optInt(Video.IS_SOHUOWN));
                video.setIs_super_code(videoJsonObject.optInt(Video.IS_SUPER_CODE));
                video.setIs_trailer(videoJsonObject.optInt(Video.IS_TRAILER));
                video.setLanguage(videoJsonObject.optString(Video.LANGUAGE));
                video.setLanguage_id(videoJsonObject.optInt(Video.LANGUAGE_ID));
                video.setLatest_video_count(videoJsonObject.optInt(Video.LATEST_VIDEO_COUNT));
                video.setMain_actor(videoJsonObject.optString(Video.MAIN_ACTOR));
                video.setPlay_count(videoJsonObject.optLong(Video.PLAY_COUNT));
                video.setScore(videoJsonObject.optString(Video.SCORE));
                video.setSearch_name(videoJsonObject.optString(Video.SEARCH_NAME));
                video.setSite(videoJsonObject.optInt(Video.SITE));
                video.setTime_length(videoJsonObject.optLong(Video.TIME_LENGTH));
                video.setTip(videoJsonObject.optString(Video.TIP));
                video.setTotal_video_count(videoJsonObject.optInt(Video.TOTAL_VIDEO_COUNT));
                video.setUpdate_status(videoJsonObject.optInt(Video.UPDATE_STATUS));
                video.setUpdate_time(videoJsonObject.optString(Video.UPDATE_TIME));
                video.setUpdateNotification(videoJsonObject.optString(Video.UPDATENOTIFICATION));
                video.setVer_high_pic(videoJsonObject.optString(Video.VER_HIGH_PIC));
                video.setVer_pic(videoJsonObject.optString(Video.VER_PIC));
                video.setVid(videoJsonObject.optLong(Video.VID));
                video.setVideo_desc(videoJsonObject.optString(Video.VIDEO_DESC));
                video.setVideo_name(videoJsonObject.optString(Video.VIDEO_NAME));
                video.setYear(videoJsonObject.optInt(Video.YEAR));
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
