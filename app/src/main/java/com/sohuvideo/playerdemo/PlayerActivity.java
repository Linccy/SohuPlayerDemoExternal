package com.sohuvideo.playerdemo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sohuvideo.api.DownloadInfo;
import com.sohuvideo.api.SohuDownloadManager;
import com.sohuvideo.api.SohuPlayVideoByApp;
import com.sohuvideo.api.SohuPlayerDefinition;
import com.sohuvideo.api.SohuPlayerError;
import com.sohuvideo.api.SohuPlayerItemBuilder;
import com.sohuvideo.api.SohuPlayerLoadFailure;
import com.sohuvideo.api.SohuPlayerMonitor;
import com.sohuvideo.api.SohuPlayerSetting;
import com.sohuvideo.api.SohuPlayerStatCallback;
import com.sohuvideo.api.SohuScreenView;
import com.sohuvideo.api.SohuVideoPlayer;
import com.sohuvideo.playerdemo.controller.ControllerFactory;
import com.sohuvideo.playerdemo.controller.SohuControllerWindow;
import com.sohuvideo.playerdemo.controller.SohuControllerWindow.ControllerVisibleListener;
import com.sohuvideo.playerdemo.controller.SohuControllerWindow.PlayerControlProxy;
import com.sohuvideo.playerdemo.utils.FormatUtil;
import com.sohuvideo.playerdemo.utils.IntentUtil;
import com.sohuvideo.playerdemo.utils.UIUtil;

public class PlayerActivity extends Activity implements ControllerVisibleListener {
    private static final String TAG = "PlayerActivity";

    private AudioManager mAm;
    private OnAudioFocusChangeListener mOnAudioFocusChangeListener;
    private SohuScreenView mScreenContainer;
    private View mScreenMask;
    private ImageView mStartPlayBtn;
    private ProgressBar mProgressBar;
    private SohuControllerWindow mController;
    private boolean needAutoPlay = false;
    private boolean isDataSourceLoading = false;
    private static SohuVideoPlayer mSohuVideoPlayer;
    /*****************************/
    private ViewGroup mGestureProgressGroup;
    private ImageView mGestureForwardImg;
    private TextView mGestureTipTxt;
    private ImageView mGestureBackwardImg;
    private TextView mGestureCurProgressTxt;
    private TextView mGestureTotalProgressTxt;
    private ViewGroup mGestureVolumnGroup;
    private ImageView mVolumnIconImg;
    private TextView mVolumnPercentTxt;
    private SohuPlayerItemBuilder currentBuilder;

    /************* 手势相关 ****************/
    private GestureDetector mGestureDetector;
    private MyGestureListener mGestureListener;
    private int gestureType = TYPE_UNKNOWN;
    public static final int TYPE_HORIZONTAL = 1;
    public static final int TYPE_VERTICAL = 2;
    public static final int TYPE_UNKNOWN = -1;
    public static final int MIN_DISTANCE = 10;

    private int seekPosition = 0;

    /*****************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOrientation();
        if (DemoApplication.getStatusBarHeight() > 0) {
            addWindowFlags();
        }
        setContentView(R.layout.activity_player);
        setupViews();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        initVerifier(getIntent());
        mSohuVideoPlayer = new SohuVideoPlayer();
        mSohuVideoPlayer.setSohuScreenView(mScreenContainer);
        mSohuVideoPlayer.setSohuPlayerMonitor(mSohuPlayerMonitor);
        mSohuVideoPlayer.setSohuPlayerStatCallback(mSohuPlayerStatCallback);
        needAutoPlay = true;
        handleIntent();
        handleGesture();
    }

    private void handleGesture() {
        mGestureListener = new MyGestureListener(PlayerActivity.this);
        mGestureDetector = new GestureDetector(PlayerActivity.this, mGestureListener);
        mScreenContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickVideo();
            }
        });
        mScreenContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mSohuVideoPlayer.isAdvertInPlayback()) {
                    return false;
                }
                mGestureDetector.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) {
                    // 处理弹起
                    if (gestureType == TYPE_UNKNOWN) {
                        clickVideo();
                    } else if (gestureType == TYPE_HORIZONTAL) {
                        mSohuVideoPlayer.seekTo(seekPosition);
                    }
                    mGestureProgressGroup.setVisibility(View.GONE);
                    mGestureVolumnGroup.setVisibility(View.GONE);
                }
                return true;
            }
        });
    }

    private class MyGestureListener extends SimpleOnGestureListener {
        private static final int FIVE_MIN = 5 * 60 * 1000;
        private static final int FORTY_FIVE_MIN = 45 * 60 * 1000;
        private static final int NINTY_MIN = 90 * 60 * 1000;

        private float myDistanceX = 0;
        private float myDistanceY = 0;
        private float downX = 0f;
        private float downY = 0f;
        private final Context mContext;
        private int mVolume = -1;
        private int mMaxVolume = 0;
        private final AudioManager mAudioManager;
        private int mScreenWidth = 0;
        private int mScreenHeight = 0;
        private int mMaxProgress = 0;
        private int mProgress = -1;
        private int factor = 1;

        public MyGestureListener(Context context) {
            mContext = context;
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            mScreenWidth = wm.getDefaultDisplay().getWidth();
            mScreenHeight = wm.getDefaultDisplay().getHeight();
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG, "onDown");
            myDistanceX = 0f;
            myDistanceY = 0f;
            downX = e.getX();
            downY = e.getY();
            gestureType = TYPE_UNKNOWN;
            // 处理按下的逻辑
            downVideo(e);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d(TAG, "onScroll");
            myDistanceX = e2.getX() - downX;
            myDistanceY = downY - e2.getY();

            if (gestureType == TYPE_UNKNOWN) {
                if (Math.abs(myDistanceY) > MIN_DISTANCE && Math.abs(myDistanceY) > Math.abs(myDistanceX)) {
                    gestureType = TYPE_VERTICAL;
                } else if (Math.abs(myDistanceX) > MIN_DISTANCE && Math.abs(myDistanceX) > Math.abs(myDistanceY)) {
                    gestureType = TYPE_HORIZONTAL;
                }
            }

            Log.d(TAG, "myDistanceY " + myDistanceY + " gestureType " + gestureType);

            if (gestureType == TYPE_HORIZONTAL) {
                // 处理横滑
                horizontalVideo(myDistanceX);
            } else if (gestureType == TYPE_VERTICAL) {
                // 处理竖滑
                verticalVideo(myDistanceY);
            } else {
                return false;
            }
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // 点击
            clickVideo();
            return true;
        }

        private void downVideo(MotionEvent e) {
            mVolume = -1;
            mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0) {
                mVolume = 0;
            }
            mProgress = -1;
            if (mSohuVideoPlayer != null) {
                mProgress = mSohuVideoPlayer.getCurrentPosition();
                mMaxProgress = mSohuVideoPlayer.getDuration();
                if (mMaxProgress > NINTY_MIN) {
                    factor = 8;
                } else if (mMaxProgress > FORTY_FIVE_MIN) {
                    factor = 7;
                } else if (mMaxProgress > FIVE_MIN) {
                    factor = 5;
                } else {
                    factor = 1;
                }
            }
            if (mProgress < 0) {
                mProgress = 0;
            }
        }

        private void verticalVideo(float distance) {
            mGestureVolumnGroup.setVisibility(View.VISIBLE);
            int curVolume = mVolume + (int) (distance * mMaxVolume / mScreenHeight);
            if (curVolume < 0) {
                curVolume = 0;
            } else if (curVolume > mMaxVolume) {
                curVolume = mMaxVolume;
            }
            if (curVolume == 0) {
                mVolumnIconImg.setImageResource(R.drawable.player_silence);
                mVolumnPercentTxt.setText("0%");
            } else {
                mVolumnIconImg.setImageResource(R.drawable.player_volume);
                mVolumnPercentTxt.setText(String.format("%d%s", (curVolume * 100 / mMaxVolume), "%"));
            }
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume, 0);
        }

        private void horizontalVideo(float distance) {
            if (mSohuVideoPlayer == null || isCurrentLive() || mSohuVideoPlayer.isAdvertInPlayback()) {
                return;
            }
            seekPosition = mProgress + (int) (distance * mMaxProgress / (mScreenWidth * factor));
            if (seekPosition < 0) {
                seekPosition = 0;
            } else if (seekPosition > mMaxProgress) {
                seekPosition = mMaxProgress;
            }

            mGestureProgressGroup.setVisibility(View.VISIBLE);
            if (distance > 0) {
                mGestureForwardImg.setVisibility(View.VISIBLE);
                mGestureBackwardImg.setVisibility(View.GONE);
                mGestureTipTxt.setText(R.string.forward);
                mGestureCurProgressTxt.setText(FormatUtil.secondToString(seekPosition / 1000));
                mGestureTotalProgressTxt.setText(FormatUtil.secondToString(mMaxProgress / 1000));
            } else {
                mGestureForwardImg.setVisibility(View.GONE);
                mGestureBackwardImg.setVisibility(View.VISIBLE);
                mGestureTipTxt.setText(R.string.backwrard);
                mGestureCurProgressTxt.setText(FormatUtil.secondToString(seekPosition / 1000));
                mGestureTotalProgressTxt.setText(FormatUtil.secondToString(mMaxProgress / 1000));
            }
        }

        private boolean isCurrentLive() {
            if (currentBuilder != null) {
                if (currentBuilder.getType() == SohuPlayerItemBuilder.TYPE_LIVE_VIDEO_SOHU
                        || currentBuilder.getType() == SohuPlayerItemBuilder.TYPE_LIVE_VIDEO_URL) {
                    return true;
                }
            }
            return false;
        }
    }

    private void clickVideo() {
        mStartPlayBtn.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mScreenMask.setVisibility(View.GONE);
        if (mProgressBar != null && mProgressBar.getVisibility() == View.VISIBLE) {
            return;
        }
        if (mController != null && !mController.isShowing() && !mSohuVideoPlayer.isAdvertInPlayback()) {
            mController.show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (!(IntentUtil.isEqualIntent(intent, getIntent()))) {
            mSohuVideoPlayer.stop(false);
            setIntent(intent);
            needAutoPlay = true;
            handleIntent();
        } else {
            Log.w(TAG, "onNewIntent, same intent or null intent");
        }
        Log.d(TAG, "onNewIntent, end");
    }

    @Override
    protected void onPause() {
        super.onPause();
        needAutoPlay = mSohuVideoPlayer.isPlaybackState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSohuVideoPlayer.stop(true);
        abandonAudioFocus();
    }

    private void abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            // 释放音频焦点
            if (mAm != null) {
                if (mOnAudioFocusChangeListener != null) {
                    Log.d(TAG, "abandonAudioFocus");
                    mAm.abandonAudioFocus(getAudioFocusChangeListener());
                }
            }
        }
    }

    public OnAudioFocusChangeListener getAudioFocusChangeListener() {
        if (mOnAudioFocusChangeListener == null) {
            mOnAudioFocusChangeListener = new OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {// 短暂失去
                        Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) { // 长时间失去
                        Log.d(TAG, "AUDIOFOCUS_LOSS");
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) { // 获得
                        Log.d(TAG, "AUDIOFOCUS_GAIN");
                    }
                }
            };
        }
        return mOnAudioFocusChangeListener;
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoPlayIfNeeded();
        requestAudioRequest(getApplicationContext());
    }

    public void requestAudioRequest(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            Log.d(TAG, "requestAudioRequest");
            getAudioManager(context).requestAudioFocus(getAudioFocusChangeListener(), AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
        }
    }

    public AudioManager getAudioManager(Context context) {
        if (mAm == null) {
            mAm = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }
        return mAm;
    }

    private void autoPlayIfNeeded() {
        if ((isDataSourceLoading || needAutoPlay)) {
            mSohuVideoPlayer.play();
            needAutoPlay = false;
            if (isDataSourceLoading) {
                updateLoadingUI();
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (DemoApplication.getStatusBarHeight() == 0 && hasFocus) {
            Rect frame = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            DemoApplication.setStatusBarHeight(frame.top);
            addWindowFlags();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        mSohuVideoPlayer.release();
        if (mController != null) {
            mController.release();
            mController = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, "dispatchKeyEvent, code:" + event.getKeyCode());
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            if (mController != null && mController.isShowing()) {
                mController.updateVolumnView(-1);
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private final SohuPlayerStatCallback mSohuPlayerStatCallback = new SohuPlayerStatCallback() {
        @Override
        public void onVV(SohuPlayerItemBuilder sohuPlayitemBuilder) {
            Log.d(TAG, "onVV");
        }

        @Override
        public void onRealVV(SohuPlayerItemBuilder sohuPlayitemBuilder, int loadingTime) {
            Log.d(TAG, "onRealVV, loadingTime:" + loadingTime);
        }

        @Override
        public void onHeartBeat(SohuPlayerItemBuilder sohuPlayitemBuilder, int currentTime) {
            Log.d(TAG, "onHeartBeat, currentTime:" + currentTime);
        }

        @Override
        public void onEnd(SohuPlayerItemBuilder sohuPlayitemBuilder, int timePlayed, boolean fromUser) {
            Log.d(TAG, "onEnd, Have played time:" + timePlayed + ",fromUser:" + fromUser);
        }
    };

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        if (PlayVideoHelper.ACTION_PLAY.equals(action)) {
            setPlayerData(intent);
            return;
        }
    }

    private void setPlayerData(Intent intent) {
        if (intent == null) {
            return;
        }
        String id = intent.getStringExtra(PlayVideoHelper.EXTRA_ID);
        int startPosition = intent.getIntExtra(PlayVideoHelper.EXTRA_STARTPOSITION, 0);
        int type = intent.getIntExtra(PlayVideoHelper.EXTRA_TYPE, PlayVideoHelper.TYPE_FOR_SOHU);
        switch (type) {
            case PlayVideoHelper.TYPE_FOR_SOHU:
                int site = intent.getIntExtra(PlayVideoHelper.EXTRA_SITE, 0);
                long aid = intent.getLongExtra(PlayVideoHelper.EXTRA_AID, 0);
                long vid = intent.getLongExtra(PlayVideoHelper.EXTRA_VID, 0);
                currentBuilder = new SohuPlayerItemBuilder(id, aid, vid, site);
                break;
            case PlayVideoHelper.TYPE_FOR_LIVE:
                int tvId = intent.getIntExtra(PlayVideoHelper.EXTRA_TVID, 0);
                String liveUrl = intent.getStringExtra(PlayVideoHelper.EXTRA_LIVEURL);
                currentBuilder = new SohuPlayerItemBuilder(id, tvId, liveUrl);
                break;
            case PlayVideoHelper.TYPE_FOR_LOCAL:
                String uri = intent.getStringExtra(PlayVideoHelper.EXTRA_LOCALURL);
                currentBuilder = new SohuPlayerItemBuilder(id, uri);
                break;
            case PlayVideoHelper.TYPE_FOR_DOWNLOAD:
                long taskInfoId = intent.getLongExtra(PlayVideoHelper.EXTRA_TASKINFOID, 0);
                currentBuilder = new SohuPlayerItemBuilder(id, taskInfoId, PlayerActivity.this);
                break;
            default:
                break;
        }
        if (currentBuilder != null) {
            mSohuVideoPlayer.setDataSource(currentBuilder.setStartPosition(startPosition));
        }
    }

    private void setOrientation() {
        if (Build.VERSION.SDK_INT >= 9) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
    }

    private void initVerifier(Intent intent) {
        String appkey = intent.getStringExtra(PlayVideoHelper.EXTRA_APPKEY);
        int subPartner = intent.getIntExtra(PlayVideoHelper.EXTRA_SUBPARTNER, 0);
        // SohuPlayerSetting.setArgs(appkey);
        SohuPlayerSetting.setSubPartner(String.valueOf(subPartner));
    }

    private void addWindowFlags() {
        Log.d(TAG, "addWindowFlags");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void setupViews() {
        mScreenContainer = (SohuScreenView) findViewById(R.id.screen_container);
        mScreenMask = findViewById(R.id.screen_mask);
        mStartPlayBtn = (ImageView) findViewById(R.id.btn_start_play);
        mStartPlayBtn.setOnClickListener(mOnClickListener);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mProgressBar.setVisibility(View.GONE);
        mGestureProgressGroup = (ViewGroup) findViewById(R.id.gesture_layout_progress);
        mGestureForwardImg = (ImageView) findViewById(R.id.gesture_forward_progress);
        mGestureTipTxt = (TextView) findViewById(R.id.gesture_tip_progress);
        mGestureBackwardImg = (ImageView) findViewById(R.id.gesture_backward_progress);
        mGestureCurProgressTxt = (TextView) findViewById(R.id.gesture_cur_progress);
        mGestureTotalProgressTxt = (TextView) findViewById(R.id.gesture_total_progress);
        mGestureVolumnGroup = (ViewGroup) findViewById(R.id.gesture_layout_volumn);
        mVolumnIconImg = (ImageView) findViewById(R.id.gesture_icon_volumn);
        mVolumnPercentTxt = (TextView) findViewById(R.id.gesture_percent_volumn);
    }

    private final SohuPlayerMonitor mSohuPlayerMonitor = new SohuPlayerMonitor() {
        @Override
        public void onPreparing() {
            mProgressBar.setVisibility(View.VISIBLE);
            mStartPlayBtn.setVisibility(View.GONE);
            if (mSohuVideoPlayer.isAdvertInPlayback()) {
                addWindowFlags();
                if (mController != null) {
                    mController.hide();
                }
            }
            super.onPreparing();
        }

        @Override
        public void onPrepared() {
            mProgressBar.setVisibility(View.GONE);
            if (!mSohuVideoPlayer.isAdvertInPlayback() && mController != null) {
                mController.show();
            }
            super.onPrepared();
        }

        @Override
        public void onStop() {
            mProgressBar.setVisibility(View.GONE);
            mStartPlayBtn.setVisibility(View.VISIBLE);
            if (mController != null) {
                mController.updatePlaypauseState(false);
            }
            super.onStop();
        }

        @Override
        public void onComplete() {
            PlayerActivity.this.finish();
            super.onComplete();
        }

        @Override
        public void onBuffering(int progress) {
            mProgressBar.setVisibility(progress == 100 ? View.GONE : View.VISIBLE);
            super.onBuffering(progress);
        }

        @Override
        public void onPlay() {
            mScreenMask.setVisibility(View.GONE);
            if (!mSohuVideoPlayer.isAdvertInPlayback() && mController != null) {
                mController.show();
            }
            if (mController != null) {
                mController.updatePlaypauseState(true);
            }
            super.onPlay();
        }

        @Override
        public void onStartLoading() {
            isDataSourceLoading = true;
            updateLoadingUI();
            super.onStartLoading();
        }

        @Override
        public void onLoadSuccess() {
            isDataSourceLoading = false;
            super.onLoadSuccess();
        }

        @Override
        public void onLoadFail(SohuPlayerLoadFailure failure) {
            isDataSourceLoading = false;
            if (isFinishing()) {
                return;
            }
            int msgid = R.string.alert_loading_error_fail;
            boolean needRetry = true;
            boolean needExit = false;
            switch (failure) {
                case APP_PERMISSION:
                    msgid = R.string.alert_apikey_error;
                    needRetry = false;
                    needExit = true;
                    break;
                case IP_LIMIT:
                    msgid = R.string.alert_loading_error_iplimit;
                    needRetry = false;
                    break;
                case MOBILE_LIMIT:
                    msgid = R.string.alert_loading_error_mobile_limit;
                    needRetry = false;
                    break;
                case OTHER:
                    msgid = R.string.alert_loading_error_fail;
                    break;
                case UNREACHED:
                    msgid = R.string.alert_loading_error_network;
                    break;
                case PREVIOUS_NOT_EXIST:
                    msgid = R.string.alert_loading_error_no_previous;
                    break;
                case NEXT_NOT_EXIST:
                    msgid = R.string.alert_loading_error_no_next;
                    break;
                default:
                    break;
            }
            handleError(msgid, failure, needRetry, needExit);
            super.onLoadFail(failure);
        }

        @Override
        public void onError(SohuPlayerError error) {
            int msgid = R.string.alert_player_error_unknown;
            switch (error) {
                case INTERNAL:
                    msgid = R.string.alert_player_error;
                    break;
                case NETWORK:
                    msgid = R.string.alert_player_error_network;
                    break;
                default:
                    break;
            }
            handleError(msgid, null, true, false);
            super.onError(error);
        }

        @Override
        public void onPause() {
            if (mController != null) {
                mController.updatePlaypauseState(false);
            }
            super.onPause();
        }

        @Override
        public void onPausedAdvertShown() {
            if (mController != null) {
                mController.dismiss();
            }
            super.onPausedAdvertShown();
        }

        @Override
        public void onPlayItemChanged(SohuPlayerItemBuilder builder, int index) {
            currentBuilder = builder;
            if (mController == null) {
                if (currentBuilder != null) {
                    mController = ControllerFactory.createController(PlayerActivity.this, builder.getType());
                    mController.setVisibleChangeListener(PlayerActivity.this);
                }
                mController.setAnchorView(mScreenContainer);
                mController.setPlayControlProxy(playerProxy);
            }
            mController.setTitle(builder.getTitle());
            mController.setSid(builder.getAid());
            mController.setVid(builder.getVid());
            mController.setSite(builder.getSite());
            mController.setCurrentPlayingIndex(currentBuilder, index);
        }

        @Override
        public void onProgressUpdated(int currentPosition, int duration) {
            if (mController != null) {
                mController.setProgress(currentPosition, duration);
            }
            super.onProgressUpdated(currentPosition, duration);
        }

        @Override
        public void onDefinitionChanged() {
            if (mController != null) {
                mController.updateDefinitionButton();
            }
            super.onDefinitionChanged();
        }

        @Override
        public void onPreviousNextStateChange(boolean previous, boolean next) {
            if (mController != null) {
                mController.setPreviousNextState(previous, next);
            }
            super.onPreviousNextStateChange(previous, next);
        }

        @Override
        public void onSkipHeader() {
            super.onSkipHeader();
        };

        @Override
        public void onSkipTail() {
            super.onSkipTail();
        }

        @Override
        public void onAppPlayOver() {
            long vid = 0;
            if (currentBuilder.getVid() != 0) {
                vid = currentBuilder.getVid();
            } else if (currentBuilder.getTaskInfoId() != 0) {
                DownloadInfo mDownloadInfo = SohuDownloadManager.getInstance().getTaskById(
                        currentBuilder.getTaskInfoId());
                if (mDownloadInfo != null) {
                    vid = mDownloadInfo.getVid();
                }
            }

            if (vid == 0) {
                finish();
                return;
            }

            ArrayList<Long> vids = new ArrayList<Long>();
            vids.add(vid);
            SohuPlayVideoByApp.requestSohuAppHistory(vids, new SohuPlayVideoByApp.OnSohuAppHistoryListener() {
                @Override
                public void onSohuAppHistory(boolean result, Map<Long, Integer> recordeds) {
                    if (!result || recordeds == null) {
                        Log.d(PlayerActivity.TAG, "recordeds == null");
                        Toast.makeText(PlayerActivity.this, "request onSohuAppHistory timeout", Toast.LENGTH_LONG)
                                .show();
                        return;
                    }

                    Log.d(PlayerActivity.TAG, "recordeds.size:" + recordeds.size());
                    for (Long aLong : recordeds.keySet()) {
                        Toast.makeText(PlayerActivity.this,
                                "vid: " + aLong + " history: " + recordeds.get(aLong) / 1000 + "秒", Toast.LENGTH_LONG)
                                .show();
                    }
                }
            }, PlayerActivity.this);
            finish();
        }

        @Override
        public void onAppPlayStart() {
            // finish();
        }

        //广告播放完成状态回调
        @Override
        public void onOadAllCompleted() {
        	Log.d("SOHU", "onOadAllCompleted");
        };

    };

    private void updateLoadingUI() {
        mScreenMask.setVisibility(View.GONE);
        mStartPlayBtn.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * 处理错误提示弹框
     *
     * @param msgid 弹框需要显示的消息
     * @param needRetry 是否播放器需要重试
     * @param needExit 是否直接退出
     */
    private void handleError(final int msgid, final SohuPlayerLoadFailure failure, final boolean needRetry,
            final boolean needExit) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                mScreenMask.setVisibility(View.VISIBLE);
                mStartPlayBtn.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                UIUtil.showDialog(PlayerActivity.this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 取消
                        if (which != DialogInterface.BUTTON_POSITIVE) {
                            mSohuVideoPlayer.stop(false);
                            return;
                        }
                        // 需要退出
                        if (needExit) {
                            finish();
                            return;
                        }
                        // 不退出，则处理播放逻辑
                        if (SohuPlayerLoadFailure.PREVIOUS_NOT_EXIST == failure) {
                            mSohuVideoPlayer.previous();
                        } else if (SohuPlayerLoadFailure.NEXT_NOT_EXIST == failure) {
                            mSohuVideoPlayer.next();
                        } else if (needRetry) {
                            mSohuVideoPlayer.play();
                        } else {
                            mSohuVideoPlayer.stop(true);
                        }
                    }
                }, R.string.alert_title_hint, msgid, needRetry ? R.string.btn_retry : R.string.btn_confirm,
                        needRetry ? R.string.btn_cancel : -1, -1, false);
            }
        });
    }

    @Override
    public void onVisibleStateChange(boolean visible) {
        if (visible) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            try {
                ((PopupWindow) mController)
                        .update(DemoApplication.getScreenWidth(getApplicationContext()),
                                DemoApplication.getScreenHeight(getApplicationContext())
                                        - DemoApplication.getStatusBarHeight());
            } catch (Exception e) {

            }
        } else {
            addWindowFlags();
        }
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_start_play: {
                    mSohuVideoPlayer.play();
                    break;
                }
                default:
                    break;
            }
        }
    };

    private final PlayerControlProxy playerProxy = new PlayerControlProxy() {
        @Override
        public void next() {
            if (mSohuVideoPlayer != null) {
                mSohuVideoPlayer.next();
            }
        }

        @Override
        public void previous() {
            if (mSohuVideoPlayer != null) {
                mSohuVideoPlayer.previous();
            }
        }

        @Override
        public void fastForward(int sec) {
            if (mSohuVideoPlayer != null) {
                mSohuVideoPlayer.fastForward(sec);
            }
        }

        @Override
        public void fastBackward(int sec) {
            if (mSohuVideoPlayer != null) {
                mSohuVideoPlayer.fastBackward(sec);
            }
        }

        @Override
        public void seekto(int pos) {
            if (mSohuVideoPlayer != null) {
                mSohuVideoPlayer.seekTo(pos);
            }
        }

        @Override
        public void changeDefinition(int definition) {
            if (mSohuVideoPlayer != null) {
                mSohuVideoPlayer.changeDefinition(definition);
            }
        }

        @Override
        public void playOrPause() {
            if (mSohuVideoPlayer != null) {
                if (mSohuVideoPlayer.isPlaybackState()) {
                    mSohuVideoPlayer.pause();
                } else {
                    mSohuVideoPlayer.play();
                }

            }
        }

        @Override
        public List<Integer> getDefinitions() {
            if (mSohuVideoPlayer != null) {
                return mSohuVideoPlayer.getSupportDefinitions();
            }
            return null;
        }

        @Override
        public int getCurrentDefinition() {
            if (mSohuVideoPlayer != null) {
                return mSohuVideoPlayer.getCurrentDefinition();
            }
            return SohuPlayerDefinition.PE_DEFINITION_FLUENCY;
        }

        @Override
        public ArrayList<SohuPlayerItemBuilder> getVideolist(int listType) {
            if (mSohuVideoPlayer != null) {
                return mSohuVideoPlayer.getVideoList();
            }
            return null;
        }

        @Override
        public void playVideoAt(int index) {
            if (mSohuVideoPlayer != null) {
                mSohuVideoPlayer.playIndex(index);
            }
        }
    };
}
