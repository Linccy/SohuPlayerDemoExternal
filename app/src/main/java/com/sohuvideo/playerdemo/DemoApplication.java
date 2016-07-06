package com.sohuvideo.playerdemo;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

import com.sohuvideo.api.SohuPlayerSDK;

public class DemoApplication extends Application {
    private static final String TAG = "SohuPlayerApplication";
    private static int mScreenWidth;
    private static int mScreenHeight;
    private static int mStatusBarHeight;
    private static DemoApplication instance;

    public static DemoApplication getInstance() {
        return instance;
    }

    private static void initScreenSize(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels; // 屏幕宽（像素，如：480px）
        mScreenHeight = dm.heightPixels; // 屏幕高（像素，如：800px）
    }

    public static int getLongerSize(Context context) {
        if (mScreenWidth * mScreenHeight == 0) {
            initScreenSize(context);
        }
        Log.d(TAG, "mScreenW:" + mScreenWidth + ",mScreenH:" + mScreenHeight);
        return Math.max(mScreenWidth, mScreenHeight);
    }

    public static int getScreenWidth(Context context) {
        if (mScreenWidth * mScreenHeight == 0) {
            initScreenSize(context);
        }
        return mScreenWidth;
    }

    public static int getScreenHeight(Context context) {
        if (mScreenWidth * mScreenHeight == 0) {
            initScreenSize(context);
        }
        return mScreenHeight;
    }

    public static void setStatusBarHeight(int statusBarHeight) {
        mStatusBarHeight = statusBarHeight;
    }

    public static int getStatusBarHeight() {
        return mStatusBarHeight;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 设置渠道号 必须 或这通过清单文件来设置 请在init方法之前调用
         */
        // SohuPlayerSDK.setPartner("10051");
        // SohuPlayerSDK.setAppKey("370f37af1847ee3308e77f86629f3955");
        SohuPlayerSDK.init(getApplicationContext());
        instance = this;
    }
}
