package com.sohuvideo.playerdemo.entity;

import java.io.Serializable;

import android.text.TextUtils;

public class Video implements Serializable {
	public static final String IS_TRAILER = "is_trailer";
	public static final String ALBUM_DESC = "album_desc";
	public static final String CATE_CODE = "cate_code";
	public static final String CID = "cid";
	public static final String AID = "aid";
	public static final String VID = "vid";
	public static final String ALBUM_NAME = "album_name";
	public static final String SEARCH_NAME = "search_name";
	public static final String IS_SOHUOWN = "is_sohuown";
	public static final String IS_ORIGINAL_CODE = "is_original_code";
	public static final String IS_SUPER_CODE = "is_super_code";
	public static final String VER_HIGH_PIC = "ver_high_pic";
	public static final String HOR_HIGH_PIC = "hor_high_pic";
	public static final String HOR_PIC = "hor_pic";
	public static final String VER_PIC = "ver_pic";
	public static final String TIP = "tip";
	public static final String UPDATE_STATUS = "update_status";
	public static final String UPDATENOTIFICATION = "updateNotification";
	public static final String LATEST_VIDEO_COUNT = "latest_video_count";
	public static final String TOTAL_VIDEO_COUNT = "total_video_count";
	public static final String YEAR = "year";
	public static final String AREA_ID = "area_id";
	public static final String LANGUAGE_ID = "language_id";
	public static final String AREA = "area";
	public static final String LANGUAGE = "language";
	public static final String SCORE = "score";
	public static final String PLAY_COUNT = "play_count";
	public static final String DIRECTOR = "director";
	public static final String MAIN_ACTOR = "main_actor";
	public static final String UPDATE_TIME = "update_time";
	public static final String TIME_LENGTH = "time_length";
	public static final String VIDEO_DESC = "video_desc";
	public static final String IS_DOWNLOAD = "is_download";
	public static final String SITE = "site";
	public static final String IS_ALBUM = "is_album";
	public static final String VIDEO_NAME = "video_name";
	public static final String VIDEO_ORDER = "video_order";

	private int video_order;
	private int is_trailer;
	private String album_desc;
	private String cate_code;
	private int cid;
	private long aid;
	private long vid;
	private String album_name;
	private String search_name;
	private int is_sohuown;
	private int is_original_code;
	private int is_super_code;
	private String ver_high_pic;
	private String hor_high_pic;
	private String hor_pic;
	private String ver_pic;
	private String tip;
	private int update_status;
	private String updateNotification;
	private int latest_video_count;
	private int total_video_count;
	private int year;
	private int area_id;
	private int language_id;
	private String area;
	private String language;
	private String score;
	private long play_count;
	private String director;
	private String main_actor;
	private String update_time;
	private long time_length;
	private String video_desc;
	private int is_download;
	private int site;
	private int is_album;
	private String video_name;

	public int getVideo_order() {
		return video_order;
	}

	public void setVideo_order(int video_order) {
		this.video_order = video_order;
	}

	public int getIs_trailer() {
		return is_trailer;
	}

	public void setIs_trailer(int is_trailer) {
		this.is_trailer = is_trailer;
	}

	public String getAlbum_desc() {
		return album_desc;
	}

	public void setAlbum_desc(String album_desc) {
		this.album_desc = album_desc;
	}

	public String getCate_code() {
		return cate_code;
	}

	public void setCate_code(String cate_code) {
		this.cate_code = cate_code;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public long getAid() {
		return aid;
	}

	public void setAid(long aid) {
		this.aid = aid;
	}

	public long getVid() {
		return vid;
	}

	public void setVid(long vid) {
		this.vid = vid;
	}

	public String getAlbum_name() {
		return album_name;
	}

	public void setAlbum_name(String album_name) {
		this.album_name = album_name;
	}

	public String getSearch_name() {
		return search_name;
	}

	public void setSearch_name(String search_name) {
		this.search_name = search_name;
	}

	public int getIs_sohuown() {
		return is_sohuown;
	}

	public void setIs_sohuown(int is_sohuown) {
		this.is_sohuown = is_sohuown;
	}

	public int getIs_original_code() {
		return is_original_code;
	}

	public void setIs_original_code(int is_original_code) {
		this.is_original_code = is_original_code;
	}

	public int getIs_super_code() {
		return is_super_code;
	}

	public void setIs_super_code(int is_super_code) {
		this.is_super_code = is_super_code;
	}

	public String getVer_high_pic() {
		return ver_high_pic;
	}

	public void setVer_high_pic(String ver_high_pic) {
		this.ver_high_pic = ver_high_pic;
	}

	public String getHor_high_pic() {
		return hor_high_pic;
	}

	public void setHor_high_pic(String hor_high_pic) {
		this.hor_high_pic = hor_high_pic;
	}

	public String getHor_pic() {
		return hor_pic;
	}

	public void setHor_pic(String hor_pic) {
		this.hor_pic = hor_pic;
	}

	public String getVer_pic() {
		return ver_pic;
	}

	public void setVer_pic(String ver_pic) {
		this.ver_pic = ver_pic;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public int getUpdate_status() {
		return update_status;
	}

	public void setUpdate_status(int update_status) {
		this.update_status = update_status;
	}

	public String getUpdateNotification() {
		return updateNotification;
	}

	public void setUpdateNotification(String updateNotification) {
		this.updateNotification = updateNotification;
	}

	public int getLatest_video_count() {
		return latest_video_count;
	}

	public void setLatest_video_count(int latest_video_count) {
		this.latest_video_count = latest_video_count;
	}

	public int getTotal_video_count() {
		return total_video_count;
	}

	public void setTotal_video_count(int total_video_count) {
		this.total_video_count = total_video_count;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getArea_id() {
		return area_id;
	}

	public void setArea_id(int area_id) {
		this.area_id = area_id;
	}

	public int getLanguage_id() {
		return language_id;
	}

	public void setLanguage_id(int language_id) {
		this.language_id = language_id;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public long getPlay_count() {
		return play_count;
	}

	public void setPlay_count(long play_count) {
		this.play_count = play_count;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getMain_actor() {
		return main_actor;
	}

	public void setMain_actor(String main_actor) {
		this.main_actor = main_actor;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public long getTime_length() {
		return time_length;
	}

	public void setTime_length(long time_length) {
		this.time_length = time_length;
	}

	public String getVideo_desc() {
		return video_desc;
	}

	public void setVideo_desc(String video_desc) {
		this.video_desc = video_desc;
	}

	public int getIs_download() {
		return is_download;
	}

	public void setIs_download(int is_download) {
		this.is_download = is_download;
	}

	public int getSite() {
		return site;
	}

	public void setSite(int site) {
		this.site = site;
	}

	public int getIs_album() {
		return is_album;
	}

	public void setIs_album(int is_album) {
		this.is_album = is_album;
	}

	public String getVideo_name() {
		return video_name;
	}

	public void setVideo_name(String video_name) {
		this.video_name = video_name;
	}

	public String getPic() {
		if (!TextUtils.isEmpty(ver_high_pic)) {
			return ver_high_pic;
		}
		if (!TextUtils.isEmpty(ver_pic)) {
			return ver_pic;
		}
		if (!TextUtils.isEmpty(hor_high_pic)) {
			return hor_high_pic;
		}
		return hor_pic;
	}
}
