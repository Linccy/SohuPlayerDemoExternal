package com.sohuvideo.playerdemo.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;

public class UIUtil {

	public static AlertDialog showDialog(Context context,
			OnClickListener listener, int titleId, String message,
			int positiveButtonId, int neutralButtonId, int negativeButtonId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if (titleId > 0)
			builder.setTitle(titleId);

		if (positiveButtonId > 0) {
			builder.setPositiveButton(positiveButtonId, listener);
		}
		if (neutralButtonId > 0) {
			builder.setNeutralButton(neutralButtonId, listener);
		}
		if (negativeButtonId > 0) {
			builder.setNegativeButton(negativeButtonId, listener);
		}
		builder.setMessage(message);
		try {
			AlertDialog dialog = builder.show();
			dialog.setCanceledOnTouchOutside(true);
			return dialog;
		} catch (Exception e) {
			return null;
		}
	}

	public static AlertDialog showDialog(Context context,
			OnClickListener listener, int titleId, int msgid,
			int positiveButtonId, int neutralButtonId, int negativeButtonId,
			boolean cancelable) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if (titleId > 0)
			builder.setTitle(titleId);

		if (positiveButtonId > 0) {
			builder.setPositiveButton(positiveButtonId, listener);
		}
		if (neutralButtonId > 0) {
			builder.setNeutralButton(neutralButtonId, listener);
		}
		if (negativeButtonId > 0) {
			builder.setNegativeButton(negativeButtonId, listener);
		}
		builder.setMessage(msgid);
		try {
			AlertDialog dialog = builder.show();
			dialog.setCancelable(cancelable);
			dialog.setCanceledOnTouchOutside(cancelable);
			return dialog;
		} catch (Exception e) {
			return null;
		}
	}

	public static AlertDialog showDialog(Context context,
			OnClickListener listener, int titleId, int positiveButtonId,
			int neutralButtonId, int negativeButtonId, View view) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if (titleId > 0)
			builder.setTitle(titleId);

		if (positiveButtonId > 0) {
			builder.setPositiveButton(positiveButtonId, listener);
		}
		if (neutralButtonId > 0) {
			builder.setNeutralButton(neutralButtonId, listener);
		}
		if (negativeButtonId > 0) {
			builder.setNegativeButton(negativeButtonId, listener);
		}
		builder.setView(view);
		try {
			AlertDialog dialog = builder.show();
			dialog.setCanceledOnTouchOutside(true);
			return dialog;
		} catch (Exception e) {
			return null;
		}
	}

	public static AlertDialog showDialog(Context context,
			OnClickListener listener, CharSequence title, int positiveButtonId,
			int neutralButtonId, int negativeButtonId, View view) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		if (!TextUtils.isEmpty(title))
			builder.setTitle(title);

		if (positiveButtonId > 0) {
			builder.setPositiveButton(positiveButtonId, listener);
		}
		if (neutralButtonId > 0) {
			builder.setNeutralButton(neutralButtonId, listener);
		}
		if (negativeButtonId > 0) {
			builder.setNegativeButton(negativeButtonId, listener);
		}
		builder.setView(view);
		try {
			AlertDialog dialog = builder.show();
			dialog.setCanceledOnTouchOutside(true);
			return dialog;
		} catch (Exception e) {
			return null;
		}
	}

	public static AlertDialog showDialog(Context context,
			OnClickListener listener, View title, int positiveButtonId,
			int neutralButtonId, int negativeButtonId, View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCustomTitle(title);
		if (positiveButtonId > 0) {
			builder.setPositiveButton(positiveButtonId, listener);
		}
		if (neutralButtonId > 0) {
			builder.setNeutralButton(neutralButtonId, listener);
		}
		if (negativeButtonId > 0) {
			builder.setNegativeButton(negativeButtonId, listener);
		}
		builder.setView(view);
		try {
			AlertDialog dialog = builder.show();
			dialog.setCanceledOnTouchOutside(true);
			return dialog;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 判断x y是否在view内部
	 * 
	 * @param view
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean isEventInsideView(View view, float x, float y) {
		if (view != null) {
			Rect rect = new Rect();
			view.getHitRect(rect);
			return rect.contains((int) x, (int) y);
		}
		return false;
	}

	/**
	 * 获取当前分辨率下指定单位对应的像素大小（根据设备信息） px,dip,sp -> px
	 * 
	 * Paint.setTextSize()单位为px
	 * 
	 * 代码摘自：TextView.setTextSize()
	 * 
	 * @param unit
	 *            {@link TypedValue.COMPLEX_UNIT_*}
	 * @param size
	 * @return
	 */
	public float getRawSize(Context context, int unit, float size) {
		Resources resources = context.getResources();

		return TypedValue.applyDimension(unit, size,
				resources.getDisplayMetrics());
	}
}
