/*
 * Copyright (c) 2013 Sohu TV. All rights reserved.
 */
package com.sohuvideo.playerdemo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import android.text.TextUtils;

/**
 * 
 * @author shileiHao 2013-6-4
 */
public class DateUtil {
	public static String getDateTime(Date aDate, String aMask) {
		if (aDate != null) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(aMask);
			return simpleDateFormat.format(aDate);
		}
		return null;
	}

	public static Date parseDate(String aDate, String aMask) {
		if (aDate != null) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(aMask);
			try {
				return simpleDateFormat.parse(aDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String getStrTimeLength(Long timeLength) {
		String result = "";
		int hour = (int) (timeLength / 3600);
		result += hour >= 10 ? "" + hour : "0" + hour;
		result += ":";
		int minute = (int) (timeLength % 3600) / 60;
		result += minute >= 10 ? "" + minute : "0" + minute;
		result += ":";
		int second = (int) (timeLength % 60);
		result += second >= 10 ? "" + second : "0" + second;
		return result;
	}

	public static String getDisplayTime(int timeMs) {
		int totalSeconds = timeMs / 1000;
		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		if (hours > 0) {
			return String.format("%d:%02d:%02d", hours, minutes, seconds);
		} else {
			return String.format("%02d:%02d", minutes, seconds);
		}
	}

	public static String getDisplayTimeFromSeconds(int totalSeconds) {
		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		if (hours > 0) {
			return String.format("%d:%02d:%02d", hours, minutes, seconds);
		} else {
			return String.format("%02d:%02d", minutes, seconds);
		}
	}

	public static String getCurrentDate() {
		String datetime = "";
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		datetime = sdf.format(date);
		return datetime;
	}

	public static String getCurrentDateTime() {
		String datetime = "";
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		datetime = sdf.format(date);
		return datetime;
	}

	public static String getCurrentTime() {
		String datetime = "";
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		datetime = sdf.format(date);
		return datetime;
	}

	public static String getCurrentDateYear() {
		String datetime = "";
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		datetime = sdf.format(date);
		return datetime;
	}

	public static String secondToString(Long timeLength) {
		try {
			long l = timeLength;
			int timeLen = (int) l;
			int hour = timeLen / 3600;
			int minuter = (timeLen - 3600 * hour) / 60;
			int second = timeLen % 60;

			String StrHour = String.valueOf(hour);
			if (hour < 10)
				StrHour = "0" + StrHour;
			String Strminuter = String.valueOf(minuter);
			if (minuter < 10)
				Strminuter = "0" + Strminuter;
			String Strsecond = String.valueOf(second);
			if (second < 10)
				Strsecond = "0" + Strsecond;
			String re = StrHour + ":" + Strminuter + ":" + Strsecond;
			return re;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} catch (Error er) {
			er.printStackTrace();
			return null;
		}
	}

	public static String getYearToString(long timeLong) {
		SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeLong);
		return simple.format(cal.getTime());
	}

	public static String getTimeToString(long timeLong) {
		SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeLong);
		return simple.format(cal.getTime());
	}

	public static Date parseStringToDate(String dateStr) {
		if (!TextUtils.isEmpty(dateStr)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				return sdf.parse(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static int[] getCurrentLive(Long[] arr, long date) {
		int[] targer = new int[2];
		targer[0] = -1; // 当前正在播放位置
		targer[1] = -1; // 下次刷新时间
		for (int i = 0; i < arr.length; i++) {
			arr[i] = arr[i] - date;
		}

		Arrays.sort(arr);
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == 0) {
				targer[0] = i;
				if (i + 1 < arr.length) {
					long next = arr[i + 1];
					targer[1] = (int) next;
				}
				break;
			} else if (arr[i] > 0) {
				if (i - 1 >= 0) {
					targer[0] = i - 1;
				}
				long next = arr[i];
				targer[1] = (int) next;
				break;
			}
		}
		return targer;
	}
}
