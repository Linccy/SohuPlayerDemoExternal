/*
 * Copyright (c) 2013 Sohu TV. All rights reserved.
 */
package com.sohuvideo.playerdemo.controller;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.sohuvideo.api.DownloadInfo;
import com.sohuvideo.api.SohuDownloadManager;
import com.sohuvideo.api.SohuPlayerDefinition;
import com.sohuvideo.api.SohuPlayerItemBuilder;
import com.sohuvideo.api.SohuPlayerSetting;
import com.sohuvideo.playerdemo.R;
import com.sohuvideo.playerdemo.adapter.OptionAdapter;
import com.sohuvideo.playerdemo.adapter.OptionAdapter.OptionItem;
import com.sohuvideo.playerdemo.utils.DialogUtil;
import com.sohuvideo.playerdemo.utils.DialogUtil.OnOptionItemClickListener;

/**
 * @author shileiHao 2013-9-4
 */
public class SohuControllerWindow extends PopupWindow {
	private static final String VOLUME = "VOLUME";
	private static final String MUTE = "MUTE";
	private static final String TAG = "SohuControllerWindow";
	private static final int FAST_WARD_SECONDS = 30;
	private static final int AUTO_SHOWN_SPAN = 5000;
	private static final int MSG_HIDE = 0;
	protected Context mContext;
	private View mContentView;
	private View mAnchorView;
	private PlayerControlProxy proxy;
	private StringBuilder mFormatBuilder;
	private Formatter mFormatter;
	private int mCurrent;
	private int mDuration;
	private boolean mDragging;
	private AudioManager mAudioManager;
	private int mLastVolumn;
	private final static HashMap<Integer, Integer> map;
	static {
		map = new HashMap<Integer, Integer>();
		map.put(SohuPlayerDefinition.PE_DEFINITION_FLUENCY,
				R.string.definition_fluency);
		map.put(SohuPlayerDefinition.PE_DEFINITION_HIGH, R.string.definition_high);
		map.put(SohuPlayerDefinition.PE_DEFINITION_SUPER, R.string.definition_super);
		map.put(SohuPlayerDefinition.PE_DEFINITION_ORIGINAL,
				R.string.definition_original);
	}
	private boolean definitionEnabled = true;
	protected SohuPlayerItemBuilder activeItem;
	// --------------------------------------
	private View mControllerTitle;
	private TextView tvTitle;
	private TextView tvDownload;
	private View mControllerBar;
	private ImageView ivSetting;
	private ImageView ivPrevious;
	private ImageView ivNext;
	private ImageView ivForward;
	private ImageView ivBackward;
	private ImageView ivPlayPause;
	private ImageView ivVolumn;
	private Button btnDefinition;
	private SeekBar seekVolumn;
	private SeekBar seekProgress;
	private TextView tvPosition;
	// --------------------------------------
	private ViewGroup vDefinitionSelector;
	// --------------------------------------
	// --------------------------------------
	private boolean mHardwareDecode = false;
	// --------------------------------------

	private long vid;
	private long sid;

	private int mSite;

	private long mLastClickDownloadTime = 0l;

	private static final int DOWNLOAD_CLICK_DELAY = 500;

	/**
	 * 此view显示分为三种模式；搜狐视频，本地视频和直播视频模�?
	 */
	public static enum MODE_TYPE {
		MODE_SOHU, MODE_LOCAL, MODE_LIVE
	};

	public SohuControllerWindow(Context context) {
		this(context, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	public SohuControllerWindow(Context context, int width, int height) {
		super(context);
		setWidth(width);
		setHeight(height);
		init(context);
	}

	public void setAnchorView(View anchor) {
		this.mAnchorView = anchor;
	}

	public void hide() {
		Log.d(TAG, "try to hide");
		hideDefinitionSelector();
		if (isShowing()) {
			dismiss();
			if (visibleListener != null) {
				visibleListener.onVisibleStateChange(false);
			}
		}
	}

	private void hideDelayed(int sec) {
		Log.d(TAG, "hide delayed:" + sec);
		mHandler.removeMessages(MSG_HIDE);
		if (sec > 0) {
			mHandler.sendEmptyMessageDelayed(MSG_HIDE, sec);
		}
	}

	public void show() {
		if (!isShowing()) {
			showTitle();
			showControllerBar();
			updateVolumnView(-1);
			updateDefinitionButton();
			hideDelayed(AUTO_SHOWN_SPAN);
			showAtLocation(mAnchorView, Gravity.BOTTOM | Gravity.LEFT, 0, 0);
			if (visibleListener != null) {
				visibleListener.onVisibleStateChange(true);
			}
		}
	}

	/**
	 * 上一集下�?集状�?
	 * 
	 * @param hasPrevious
	 * @param hasNext
	 */
	public void setPreviousNextState(boolean hasPrevious, boolean hasNext) {
		this.ivNext.setEnabled(hasNext);
		this.ivPrevious.setEnabled(hasPrevious);
	}

	public void setCurrentPlayingIndex(SohuPlayerItemBuilder builder, int index) {
		Log.d(TAG, "setCurrentPlayingIndex:" + index);
		this.activeItem = builder;
		/** 更新�?下definition button **/
		updateDefinitionButton();
	}

	public void release() {
		mHandler.removeCallbacksAndMessages(null);
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_HIDE: {
				hide();
				break;
			}
			default:
				break;
			}
		}
	};

	protected void init(Context context) {
		mContext = context;
		mFormatBuilder = new StringBuilder();
		mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
		mAudioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		mLastVolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContentView = inflater.inflate(R.layout.popup_controller, null);
		setContentView(mContentView);
		initControllerTitle();
		initControllerBar();
		vDefinitionSelector = (ViewGroup) mContentView
				.findViewById(R.id.linearlay_select_definition);
		mContentView.setFocusable(true);
		mContentView.setFocusableInTouchMode(true);
		setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					hideDelayed(0);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					hideDelayed(AUTO_SHOWN_SPAN);
				}
				return false;
			}
		});
		mContentView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hide();
			}
		});
		setOutsideTouchable(true);
		setBackgroundDrawable(mContext.getResources().getDrawable(
				R.color.transparent));
	}


	private void initControllerTitle() {
		mControllerTitle = mContentView
				.findViewById(R.id.layout_controller_title);
		tvTitle = (TextView) mControllerTitle
				.findViewById(R.id.tv_controller_title);
		tvDownload = (TextView) mControllerTitle.findViewById(R.id.tv_download);
		tvDownload.setOnClickListener(mTitleClickListener);
	}

	public void setTitle(String title) {
		this.tvTitle.setText(title);
	}

	public void setVid(long vid) {
		this.vid = vid;
	}

	public void setSid(long sid) {
		this.sid = sid;
	}

	public void setSite(int site) {
		mSite = site;
	}

	private void showTitle() {
		mControllerTitle.setVisibility(View.VISIBLE);
	}

	public void updatePlaypauseState(boolean isPlaying) {
		ivPlayPause.setImageResource(isPlaying ? R.drawable.ic_pause
				: R.drawable.ic_play);
	}

	private final OnClickListener mTitleClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_download:
				clickDownload(v);
				break;
			}
		}
	};

	private void clickDownload(View v) {
		long current = System.currentTimeMillis();
		if (current - mLastClickDownloadTime < DOWNLOAD_CLICK_DELAY) {
			mLastClickDownloadTime = current;
			return;
		}
		mLastClickDownloadTime = current;
		if (vid != 0) {
			int currentDefinition = proxy.getCurrentDefinition();
			DownloadInfo info = new DownloadInfo(vid, sid, mSite, currentDefinition);
			SohuDownloadManager.getInstance().addTask(info);
		}
	}

	private void initControllerBar() {
		mControllerBar = mContentView.findViewById(R.id.layout_controller_bar);
		ivSetting = (ImageView) mControllerBar.findViewById(R.id.iv_setting);
		ivPrevious = (ImageView) mControllerBar.findViewById(R.id.iv_previous);
		ivNext = (ImageView) mControllerBar.findViewById(R.id.iv_next);
		ivForward = (ImageView) mControllerBar
				.findViewById(R.id.iv_fast_forward);
		ivBackward = (ImageView) mControllerBar
				.findViewById(R.id.iv_fast_backward);
		ivPlayPause = (ImageView) mControllerBar
				.findViewById(R.id.iv_play_or_pause);
		ivVolumn = (ImageView) mControllerBar.findViewById(R.id.iv_volume);
		btnDefinition = (Button) mControllerBar
				.findViewById(R.id.btn_definition);
		seekVolumn = (SeekBar) mControllerBar.findViewById(R.id.seekbar_volume);
		seekProgress = (SeekBar) mControllerBar
				.findViewById(R.id.seekbar_progress);
		tvPosition = (TextView) mControllerBar
				.findViewById(R.id.tv_progresstime);
		ivSetting.setOnClickListener(mControllerbarClickListener);
		ivPrevious.setOnClickListener(mControllerbarClickListener);
		ivNext.setOnClickListener(mControllerbarClickListener);
		ivForward.setOnClickListener(mControllerbarClickListener);
		ivBackward.setOnClickListener(mControllerbarClickListener);
		ivPlayPause.setOnClickListener(mControllerbarClickListener);
		ivVolumn.setOnClickListener(mControllerbarClickListener);
		btnDefinition.setOnClickListener(mControllerbarClickListener);
		seekVolumn.setOnSeekBarChangeListener(mVolumnSeekBarChangeListener);
		seekProgress.setOnSeekBarChangeListener(mProgressSeekBarChangeListener);
	}

	private void showControllerBar() {
		// TODO ANIMATION?
	}

	public void enableSetting(boolean enable) {
	}

	public void enableDefinition(boolean enable) {
		if (enable != definitionEnabled) {
			definitionEnabled = enable;
			btnDefinition.setVisibility(definitionEnabled ? View.VISIBLE
					: View.INVISIBLE);
		}
	}

	public void updateDefinitionButton() {
		if (definitionEnabled) {
			int definition = SohuPlayerSetting.getPreferDefinition();
			if (proxy != null) {
				definition = proxy.getCurrentDefinition();
			}
			Log.d(TAG, "definition:" + definition);
			if (btnDefinition != null && map.get(definition) != null) {
				btnDefinition.setText(map.get(definition));
			}
		}
	}

	private void showDefinitionSelector() {
		int cur = proxy.getCurrentDefinition();
		List<Integer> definitions = proxy.getDefinitions();
		vDefinitionSelector.removeAllViews();
		for (Integer definition : definitions) {
			View definitionItemView = View.inflate(mContext,
					R.layout.definition_item, null);
			Button definitionBtn = (Button) definitionItemView
					.findViewById(R.id.button_definition);
			definitionBtn.setText(map.get(definition));
			definitionBtn.setTag(definition);
			if (cur == definition) {
				definitionBtn.setTextColor(mContext.getResources().getColor(
						R.color.red));
			} else {
				definitionBtn.setTextColor(mContext.getResources().getColor(
						R.color.gray3));
				definitionBtn.setOnClickListener(mOnDefinitioItemClick);
			}
			vDefinitionSelector.addView(definitionItemView, 0);
		}
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) vDefinitionSelector
				.getLayoutParams();
		lp.leftMargin = btnDefinition.getLeft();
		vDefinitionSelector.setLayoutParams(lp);
		vDefinitionSelector.setVisibility(View.VISIBLE);
	}

	private final View.OnClickListener mOnDefinitioItemClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			vDefinitionSelector.setVisibility(View.GONE);
			int definition = (Integer) v.getTag();
			Log.i(TAG, "select type is " + map.get(definition));
			SohuPlayerSetting.setPreferDefinition(definition);
			hide();
			proxy.changeDefinition(definition);
		}
	};
	private final OnSeekBarChangeListener mVolumnSeekBarChangeListener = new OnSeekBarChangeListener() {
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			hideDelayed(AUTO_SHOWN_SPAN);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			hideDelayed(0);
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (fromUser) {
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						progress, 0);
				updateVolumnView(progress);
			}
		}
	};
	private final OnSeekBarChangeListener mProgressSeekBarChangeListener = new OnSeekBarChangeListener() {
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			if (proxy != null) {
				proxy.seekto(mCurrent * 1000);
			}
			mDragging = false;
			hideDelayed(AUTO_SHOWN_SPAN);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			mDragging = true;
			hideDelayed(0);
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			Log.d(TAG, "progress:" + progress + ",fromUser:" + fromUser);
			if (fromUser) {
				mCurrent = progress;
				updateProgressView();
			}
		}
	};
	private final OnClickListener mControllerbarClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_setting:
				clickSetting();
				break;
			case R.id.iv_previous:
				clickPrevious();
				break;
			case R.id.iv_next:
				clickNext();
				break;
			case R.id.iv_fast_forward:
				clickForward();
				break;
			case R.id.iv_fast_backward:
				clickBackward();
				break;
			case R.id.iv_play_or_pause:
				clickPlayPause();
				break;
			case R.id.iv_volume:
				clickVolumn(v);
				break;
			case R.id.btn_definition:
				clickDefinition();
				break;
			}
		}
	};

	private void clickSetting() {
		List<OptionItem> items = new ArrayList<OptionItem>();
		OptionItem item_sh = new OptionItem(
				mContext.getString(R.string.skip_header),
				SohuPlayerSetting.getNeedSkipHeader());
		items.add(item_sh);
		OptionItem item_st = new OptionItem(
				mContext.getString(R.string.skip_tail),
				SohuPlayerSetting.getNeedSkipTail());
		items.add(item_st);
		OptionItem item_an = new OptionItem(
				mContext.getString(R.string.auto_next),
				SohuPlayerSetting.getNeedAutoNext());
		items.add(item_an);
		final OptionAdapter adapter = new OptionAdapter(mContext, true);
		adapter.setItems(items);
		DialogUtil.showOptionDialog(mContext,
				mContext.getString(R.string.alert_title_player_setting),
				adapter, null, new OnOptionItemClickListener() {
					@Override
					public void onOption(int position, boolean selected) {
						switch (position) {
						case 0: {
							SohuPlayerSetting.setNeedSkipHeader(selected);
							break;
						}
						case 1: {
							SohuPlayerSetting.setNeedSkipTail(selected);
							break;
						}
						case 2: {
							SohuPlayerSetting.setNeedAutoNext(selected);
							break;
						}
						default:
							break;
						}
					}
				});
	}

	private boolean hideDefinitionSelector() {
		if (vDefinitionSelector.isShown()) {
			vDefinitionSelector.setVisibility(View.GONE);
			return true;
		}
		return false;
	}

	private void clickDefinition() {
		if (!hideDefinitionSelector()) {
			// TODO update once?
			showDefinitionSelector();
		}
	}

	private void clickPrevious() {
		proxy.previous();
	}

	private void clickNext() {
		proxy.next();
	}

	private void clickForward() {
		proxy.fastForward(FAST_WARD_SECONDS);
	}

	private void clickBackward() {
		proxy.fastBackward(FAST_WARD_SECONDS);
	}

	private void clickPlayPause() {
		proxy.playOrPause();
	}

	private void clickVolumn(View v) {
		String tag = (String) v.getTag();
		int nextValue = 0;
		if (MUTE.equals(tag)) {
			ivVolumn.setImageResource(R.drawable.ic_volume);
			ivVolumn.setTag("VOLUME");
			nextValue = mLastVolumn;
		} else {
			mLastVolumn = mAudioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			ivVolumn.setImageResource(R.drawable.ic_mute);
			ivVolumn.setTag("MUTE");
		}
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, nextValue, 0);
		updateVolumnView(nextValue);
	}

	/**
	 * 更新声音进度�?
	 * 
	 * @param value
	 *            负数表示取当前系统�?�，否则更新到value�?
	 */
	public void updateVolumnView(int value) {
		int current = value >= 0 ? value : mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		seekVolumn.setMax(mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		seekVolumn.setProgress(current);
		if (current == 0) {
			ivVolumn.setImageResource(R.drawable.ic_mute);
			ivVolumn.setTag(MUTE);
		} else {
			ivVolumn.setImageResource(R.drawable.ic_volume);
			ivVolumn.setTag(VOLUME);
		}
	}

	public void setPlayControlProxy(PlayerControlProxy control) {
		this.proxy = control;
	}

	/**
	 * 格式化时�?
	 * 
	 * @param seconds
	 * @return
	 */
	private String formatTime(int seconds) {
		int second = seconds % 60;
		int minutes = (seconds / 60) % 60;
		int hours = seconds / 3600;
		mFormatBuilder.setLength(0);
		return mFormatter.format("%d:%02d:%02d", hours, minutes, second)
				.toString();
	}

	/**
	 * 更新进度
	 */
	private void updateProgressView() {
		tvPosition.setText(formatTime(mCurrent) + "/" + formatTime(mDuration));
		if (mDuration == 0) {
			seekProgress.setProgress(0);
		} else {
			seekProgress.setMax(mDuration);
			seekProgress.setProgress(mCurrent);
		}
	}

	/**
	 * 设置进度信息，如果可见，则更新进度条
	 * 
	 * @param current
	 * @param duration
	 */
	public void setProgress(int current, int duration) {
		if (!mDragging) {
			this.mCurrent = current / 1000;
			this.mDuration = duration / 1000;
			if (isShowing()) {
				updateProgressView();
			}
		}
	}

	protected void setDisplayModeType(MODE_TYPE type) {
		if (mContentView == null) {
			return;
		}

		if (type == MODE_TYPE.MODE_LIVE) {
			mContentView.findViewById(R.id.layout_progressbar).setVisibility(
					View.GONE);
			ivSetting.setVisibility(View.INVISIBLE);
			btnDefinition.setVisibility(View.INVISIBLE);
			ivPrevious.setVisibility(View.INVISIBLE);
			ivBackward.setVisibility(View.INVISIBLE);
			ivForward.setVisibility(View.INVISIBLE);
			ivNext.setVisibility(View.INVISIBLE);
			tvDownload.setVisibility(View.GONE);
		} else if (type == MODE_TYPE.MODE_LOCAL) {
			mContentView.findViewById(R.id.layout_progressbar).setVisibility(
					View.VISIBLE);
			ivSetting.setVisibility(View.VISIBLE);
			btnDefinition.setVisibility(View.INVISIBLE);
			ivPrevious.setVisibility(View.INVISIBLE);
			ivBackward.setVisibility(View.VISIBLE);
			ivForward.setVisibility(View.VISIBLE);
			ivNext.setVisibility(View.INVISIBLE);
			tvDownload.setVisibility(View.GONE);
		} else {
			mContentView.findViewById(R.id.layout_progressbar).setVisibility(
					View.VISIBLE);
			ivSetting.setVisibility(View.VISIBLE);
			btnDefinition.setVisibility(View.VISIBLE);
			ivPrevious.setVisibility(View.VISIBLE);
			ivBackward.setVisibility(View.VISIBLE);
			ivForward.setVisibility(View.VISIBLE);
			ivNext.setVisibility(View.VISIBLE);
			tvDownload.setVisibility(View.VISIBLE);
		}
	}


	public static interface PlayerControlProxy {
		void playOrPause();

		void next();

		void previous();

		void fastForward(int sec);

		void fastBackward(int sec);

		void seekto(int pos);

		void playVideoAt(int index);

		List<Integer> getDefinitions();

		int getCurrentDefinition();

		void changeDefinition(int definition);

		ArrayList<SohuPlayerItemBuilder> getVideolist(int listType);

	}

	private ControllerVisibleListener visibleListener;

	public void setVisibleChangeListener(ControllerVisibleListener listener) {
		this.visibleListener = listener;
	}

	public static interface ControllerVisibleListener {
		void onVisibleStateChange(boolean visible);
	}
}
