package com.sohuvideo.playerdemo.net;

import org.apache.http.HttpStatus;

import android.content.Context;

/**
 * 封装Http请求并且处理返回结果
 * 
 * @author gaoyihang
 * 
 */
public abstract class BaseProtocol<T> {

	protected final static String PLAT = "plat";
	protected final static String POID = "poid";
	protected final static String API_KEY = "api_key";
	protected final static String SVER = "sver";
	protected final static String SYSVER = "sysver";
	protected final static String PARTNER = "partner";

	protected Context mContext;

	protected T resultT;

	private int resultCode; // 请求返回的状态码

	public BaseProtocol(Context context) {
		this.mContext = context;
	}

	/**
	 * 调用网络HttpEngine发送具体网络请求
	 * 
	 * @return 请求返回的bean对象
	 */
	public T request() {
		int code = HttpEngine.getInstance(mContext).executeGet(this);
		this.resultCode = code;
		if (code == HttpStatus.SC_OK) {
			return resultT;
		} else {
			handleError(code);
			return null;
		}

	}

	public void setResult(T t) {
		this.resultT = t;
	}

	/**
	 * 得到返回的状态码
	 * 
	 * @return
	 */
	public int getResutlCode() {
		return this.resultCode;
	}

	/**
	 * 拼接地址和请求参数
	 * 
	 * @return
	 */
	public abstract String makeRequest();

	/**
	 * 处理返回结果
	 * 
	 * @param jsonObject
	 */
	public abstract T handleResponse(String response);

	/**
	 * 内部处理错误code(也可在外部{@link #getResutlCode()}处理错误码)
	 * 
	 * @param errorCode
	 */
	protected abstract void handleError(int errorCode);

}
