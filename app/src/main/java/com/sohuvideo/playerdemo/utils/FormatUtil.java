package com.sohuvideo.playerdemo.utils;

public class FormatUtil {
	public static String milliSeconds2Time(int ms) {
		return seconds2Time(ms / 1000);
	}

	public static String seconds2Time(int sec) {
		if (sec < 0) {
			return "00:00";
		}
		int s = sec % 60;
		int m = sec / 60;
		return (m < 10 ? "0" : "") + m + ":" + (s < 10 ? "0" : "") + s;
	}

	public static String secondToString(long timeLength) {
		try {
			long l = timeLength;
			int timeLen = (int) l;
			int hour = timeLen / 3600;
			int minuter = (timeLen - 3600 * hour) / 60;
			int second = timeLen % 60;
			StringBuilder sb = new StringBuilder();
			if (hour < 10) {
				sb.append("0").append(hour);
			} else {
				sb.append(hour);
			}
			sb.append(":");
			if (minuter < 10) {
				sb.append("0").append(minuter);
			} else {
				sb.append(minuter);
			}
			sb.append(":");
			if (second < 10) {
				sb.append("0").append(second);
			} else {
				sb.append(second);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} catch (Error er) {
			er.printStackTrace();
			return null;
		}
	}
}
