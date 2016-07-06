package com.sohuvideo.playerdemo.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;

import android.text.TextUtils;

public class StringUtil {

	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	public static String toMD5(String origin) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return byteArrayToHexString(md.digest(origin.getBytes()));
		} catch (Exception ex) {
			return null;
		}
	}

	public static String encodeURL(String url, String encode)
			throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		StringBuilder noAsciiPart = new StringBuilder();
		for (int i = 0; i < url.length(); i++) {
			char c = url.charAt(i);
			if (c > 255) {
				noAsciiPart.append(c);
			} else {
				if (noAsciiPart.length() != 0) {
					sb.append(URLEncoder.encode(noAsciiPart.toString(), encode));
					noAsciiPart.delete(0, noAsciiPart.length());
				}
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static int string2Int(String str) {
		if (TextUtils.isEmpty(str)) {
			return 0;
		}

		int ret = 0;
		try {
			ret = Integer.parseInt(str);
		} catch (Exception e) {

		}
		return ret;
	}

	public static long parseLong(String str, long defaultValue) {
		if (isEmpty(str)) {
			return defaultValue;
		}
		try {
			return Long.parseLong(str);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static boolean isBlank(String str) {
		return str != null && isEmpty(str.trim());
	}

	public static boolean isNull(String str) {
		return str == null || "null".equalsIgnoreCase(str.trim());
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str) && !isNull(str);
	}

	public static String toString(int value) {
		return String.valueOf(value);
	}

	public static String toString(boolean value) {
		return String.valueOf(value);
	}

	public static String toString(double value) {
		return String.valueOf(value);
	}

	public static String toString(long value) {
		return String.valueOf(value);
	}
}
