package com.sohuvideo.playerdemo;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.sohuvideo.api.DownloadInfo;
import com.sohuvideo.api.SohuDownloadErrorCodes;
import com.sohuvideo.api.SohuDownloadObserver;

/**
 * 下载任务的单例监听
 * @author gaoyihang
 *
 */
public class DownloadObserver implements SohuDownloadObserver{
	
	private final static String TAG = "DownloadObserver";
			
			
	private final static int MESSAGE_ADD_SUCCESSFUL = 1;
	private final static int MESSAGE_DOWNLOAD_COMPLETED = 2;
	
	
	private static DownloadObserver mInstance;
	private Context mContext;
	
	
	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_ADD_SUCCESSFUL:
				Toast.makeText(mContext, "add download task successful", Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_DOWNLOAD_COMPLETED:
				Toast.makeText(mContext, "download completed", Toast.LENGTH_SHORT).show();
				break;
			default:
				Toast.makeText(mContext, getErrorMessage(msg.what), Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
	private DownloadObserver(Context context){
		mContext = context;
	}
	public static synchronized DownloadObserver getInstance(Context context){
		if(mInstance == null) {
			mInstance = new DownloadObserver(context);
		}
		return mInstance;
	}
	
	@Override
	public void onAdd(DownloadInfo info) {
		Log.d(TAG, "onAdd,info: " + info.toString());
	}
	
	@Override
	public void onWait(DownloadInfo info) {
		//ignore
	}
	
	@Override
	public boolean onStart(DownloadInfo info) {
		Log.d(TAG, "onStart,info: " + info.toString());
		mHandler.sendEmptyMessage(MESSAGE_ADD_SUCCESSFUL);
		return true;
	}
	@Override
	public void onPause(DownloadInfo info) {
	}
	@Override
	public boolean onResume(DownloadInfo info) {
		return true;
	}
	@Override
	public void onProgressed(DownloadInfo info) {
		Log.d(TAG, "onProgressed,vid: " + info.getVid() + ", total: " + info.getTotalFileSize() + ", downloaded: " + info.getDownloadedSize() + " ,progress:" + info.getDownloadProgress());
	}
	@Override
	public void onCompleted(DownloadInfo info) {
		mHandler.sendEmptyMessage(MESSAGE_DOWNLOAD_COMPLETED);
	}
	@Override
	public void onRemoved(DownloadInfo info) {
	}
	@Override
	public void onError(DownloadInfo info, int errorCode) {
		Log.e(TAG, "onError,error Code:" + errorCode + " ,vid:" + info.getVid());
		mHandler.sendEmptyMessage(errorCode);
	}
	
	public static String getErrorMessage(int errorCode) {
		switch (errorCode) {
		case SohuDownloadErrorCodes.ADD_ERROR_MISS_ARGS:
			return "ADD_ERROR_MISS_ARGS";
		case SohuDownloadErrorCodes.ADD_ERROR_NOT_SUPPORT:
			return "ADD_ERROR_NOT_SUPPORT";
		case SohuDownloadErrorCodes.ADD_ERROR_APPKEY_ERROR:
			return "ADD_ERROR_APPKEY_ERROR";
		case SohuDownloadErrorCodes.ADD_ERROR_TASK_EXISTS:
			return "ADD_ERROR_TASK_EXISTS";
		case SohuDownloadErrorCodes.ADD_ERROR_REQUEST_VIDEO_ERROR:
			return "ADD_ERROR_REQUEST_VIDEO_ERROR";
		case SohuDownloadErrorCodes.ADD_ERROR_DO_NOT_SOPPORT_DOWNLOAD:
			return "ADD_ERROR_DO_NOT_SOPPORT_DOWNLOAD";
		case SohuDownloadErrorCodes.ADD_ERROR_UNKNOWN:
			return "ADD_ERROR_UNKNOWN";
		case SohuDownloadErrorCodes.ADD_ERROR_NETWORK_UNAVAILABLE:
			return "ADD_ERROR_NETWORK_UNAVAILABLE";
		case SohuDownloadErrorCodes.ADD_ERROR_CAN_NOT_CREATE_DIRECTORY:
			return "ADD_ERROR_CAN_NOT_CREATE_DIRECTORY";
		case SohuDownloadErrorCodes.START_TERMINATED_BY_APP:
			return "START_TERMINATED_BY_APP";
		case SohuDownloadErrorCodes.DOWNLOADING_NETWORK_ERROR:
			return "DOWNLOADING_NETWORK_ERROR";
		case SohuDownloadErrorCodes.PAUSE_ERROR_TASK_ID_NOT_FOUND:
			return "PAUSE_ERROR_TASK_ID_NOT_FOUND";
		case SohuDownloadErrorCodes.RESUME_ERROR_TASK_DOWNLOADING:
			return "RESUME_ERROR_TASK_DOWNLOADING";
		case SohuDownloadErrorCodes.RESUME_ERROR_TASK_ID_NOT_FOUND:
			return "RESUME_ERROR_TASK_ID_NOT_FOUND";
		case SohuDownloadErrorCodes.RESUME_TERMINATED_BY_APP:
			return "RESUME_TERMINATED_BY_APP";
		default:
			break;
		}
		return "msg unknown";
	}
}
