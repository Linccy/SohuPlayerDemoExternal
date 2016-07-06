package com.sohuvideo.playerdemo.net;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

/**
 * 具体执行http请求的Engine类
 * 
 * @author GaoYihang
 * 
 */
public class HttpEngine {
	private final static String TAG = "HttpEngine";

	private Context mContext;
	private static HttpEngine mInstance;

	private HttpEngine(Context context) {
		this.mContext = context;
	}

	public synchronized static HttpEngine getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new HttpEngine(context);
		}
		return mInstance;
	}

	/**
	 * 发送具体的Http get请求,并讲请求结果回执给protocol对象
	 * 
	 * @param protocol
	 * @return code
	 */
	public <T> int executeGet(BaseProtocol<T> protocol) {
		String url = protocol.makeRequest();

		Log.d(TAG, "get request ==================================>>");
		Log.d(TAG, url);
		Log.d(TAG, "<<================================== get request");

		AdvancedHttpClient client = AdvancedHttpClient
				.createDefaultClient(mContext);
		if (null == client) {
			Log.d(TAG, "null == client");
			return -1;
		}
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(get);
			StatusLine line = response.getStatusLine();
			int httpCode = -1;
			if (null != line) {
				httpCode = line.getStatusCode();
			}
			Log.d(TAG, "code : " + httpCode);
			HttpEntity resultEntity = response.getEntity();
			if (httpCode == HttpStatus.SC_OK && resultEntity != null) {
				String resultString = EntityUtils.toString(resultEntity);

				Log.d(TAG, "get response ==================================>>");
				// Log.d(TAG,resultString);
				Log.d(TAG, "<<================================== get response");

				T responseObj = protocol.handleResponse(resultString);
				protocol.setResult(responseObj);
				return httpCode;
			}
		} catch (Exception e) {
			if (e != null && !TextUtils.isEmpty(e.getMessage())) {
				Log.e(TAG, e.getMessage());
			}
		}
		return -1;
	}

	/**
	 * 得到远程文件的大小
	 * 
	 * @param url
	 * @return
	 */
	public long getFileLength(String url) {
		AdvancedHttpClient client = AdvancedHttpClient
				.createDefaultClient(mContext);
		if (null == client) {
			Log.d(TAG, "null == client");
			return -1;
		}
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(get);
			StatusLine line = response.getStatusLine();
			int httpCode = -1;
			if (null != line) {
				httpCode = line.getStatusCode();
			}
			Log.d(TAG, "code : " + httpCode);
			HttpEntity resultEntity = response.getEntity();
			if (httpCode == HttpStatus.SC_OK && resultEntity != null) {
				return resultEntity.getContentLength();
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}

		return -1;
	}

}
