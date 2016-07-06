package com.sohuvideo.playerdemo.net;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * 网络配置相关工具类.
 * 
 * @author gaoyihang
 */
public class Connectivity {

	private final static String TAG = "Connectivity";

	private static Connectivity _instance;

	private ConnectivityManager mConnectivityManager;

	private WifiManager mWifiManager;

	private Context mContext;

	private Connectivity(Context context) {
		mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		Object service = context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (null != service) {
			mConnectivityManager = (ConnectivityManager) service;
		}
		mContext = context;
	}

	public synchronized static Connectivity getInstance(Context context) {
		if (_instance == null) {
			_instance = new Connectivity(context);
		}
		return _instance;
	}

	/**
	 * @return 当前网络是否可以访问
	 */
	public boolean isNetworkAvailable() {
		NetworkInfo actNetInfo = null;
		if (mConnectivityManager != null) {
			actNetInfo = mConnectivityManager.getActiveNetworkInfo();
		}
		return actNetInfo != null && actNetInfo.isConnected();
	}

	/**
	 * @return 移动网络是否可以访问
	 */
	public boolean isMobileAvailable() {
		NetworkInfo netInfo = mConnectivityManager == null ? null
				: mConnectivityManager.getActiveNetworkInfo();
		return (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_MOBILE);
	}

	/**
	 * @return WiFi是否可用
	 */
	public boolean isWifiAvailable() {
		NetworkInfo netInfo = mConnectivityManager == null ? null
				: mConnectivityManager.getActiveNetworkInfo();
		return ((netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) && mWifiManager
				.isWifiEnabled());
	}

	/**
	 * @return 当前的代理状态[PROXY,PORT],如果没有代理则返回空
	 */
	@SuppressWarnings("deprecation")
	public String[] getCurrentProxy() {
		NetworkInfo netInfo = mConnectivityManager == null ? null
				: mConnectivityManager.getActiveNetworkInfo();

		if (netInfo == null) {
			Log.d(TAG, "netInfo == null");
			return null; // 当前没有可用网络
		}

		if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			Log.d(TAG, "netInfo == TYPE_WIFI");
			return null; // WiFi没有代理
		} else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			// 移动网络
			Log.d(TAG, "netInfo == TYPE_MOBILE");
			String host = Proxy.getDefaultHost();
			Cursor cursor = null;
			Uri uri = Uri.parse("content://telephony/carriers/");
			try {
				String where = String.format("apn='%s' AND %s ", netInfo
						.getExtraInfo(),
						host == null ? "(proxy IS NULL OR proxy = '')"
								: ("proxy ='" + host + "'"));
				cursor = mContext.getContentResolver().query(uri, null, where,
						null, null);
				if (cursor == null) {
					Log.d(TAG, "cursor == null");
					return null;
				}
				if (!cursor.moveToFirst()) {
					Log.d(TAG, "cursor is empty");
					return null;
				}

				String proxy = null;
				int proxyPort = -1;

				int colId = cursor.getColumnIndex("proxy");
				if (-1 != colId) {
					proxy = cursor.getString(colId);
				} else {
					Log.e(TAG, "\"proxy\" column is not found in cursor!");
				}

				if (proxy != null) {
					colId = cursor.getColumnIndex("port");
					if (-1 != colId) {
						proxyPort = cursor.getInt(colId);
					} else {
						Log.e(TAG, "\"port\" column is not found in cursor!");
					}
				}

				Log.d(TAG, "proxy is " + proxy + " prot is " + proxyPort);
				if (proxy == null) {
					return null;
				} else {
					if (proxyPort == -1) {
						proxyPort = 80;
					}
					return new String[] { proxy, String.valueOf(proxyPort) };
				}

			} catch (Exception e1) {
				Log.e(TAG, "get proxy error");
			}
		}

		return null;
	}

}
