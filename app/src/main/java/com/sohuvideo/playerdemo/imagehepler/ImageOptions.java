package com.sohuvideo.playerdemo.imagehepler;

import java.lang.reflect.Field;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * Image decode options
 */
public class ImageOptions {

	/**
	 * Image size to scale to during decoding
	 */
	public ImageSize mTargetSize;

	/**
	 * Image scale type
	 */
	public ImageScaleType mImageScaleType;
	/**
	 * View scale type
	 */
	public ViewScaleType mViewScaleType;

	/**
	 * Possible bitmap configurations
	 */
	public Bitmap.Config mBitmapConfig;

	public ImageOptions() {
		mImageScaleType = ImageScaleType.NONE;
		mTargetSize = new ImageSize(0, 0);
		mBitmapConfig = Bitmap.Config.ARGB_8888;
		mViewScaleType = ViewScaleType.CROP;
	}

	/**
	 * Type of image scaling during decoding.
	 * 
	 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
	 * @since 1.5.0
	 */
	public enum ImageScaleType {
		/** Image won't be scaled */
		NONE,
		/**
		 * Image will be reduces 2-fold until next reduce step make image
		 * smaller target size.<br />
		 * It's <b>fast</b> type and it's preferable for usage in
		 * lists/grids/galleries (and other {@linkplain AdapterView
		 * adapter-views}) .<br />
		 * Relates to {@link BitmapFactory.Options#inSampleSize}<br />
		 * Note: If original image size is smaller than target size then
		 * original image <b>won't</b> be scaled.
		 */
		IN_SAMPLE_POWER_OF_2,
		/**
		 * Image will be subsampled in an integer number of times. Use it if
		 * memory economy is quite important.<br />
		 * Relates to {@link BitmapFactory.Options#inSampleSize}<br />
		 * Note: If original image size is smaller than target size then
		 * original image <b>won't</b> be scaled.
		 */
		IN_SAMPLE_INT,
		/**
		 * Image will scaled-down exactly to target size (scaled width or height
		 * or both will be equal to target size; depends on
		 * {@linkplain ScaleType ImageView's scale type}). Use it if memory
		 * economy is critically important.<br />
		 * Note: If original image size is smaller than target size then
		 * original image <b>won't</b> be scaled.<br />
		 * <br />
		 * <b>Important note:</b> For creating result Bitmap (of exact size)
		 * additional Bitmap will be created with
		 * {@link Bitmap#createScaledBitmap(Bitmap, int, int, boolean)
		 * Bitmap.createScaledBitmap(...)}. So this scale type requires more
		 * memory for creation of result Bitmap, but then save memory by keeping
		 * in memory smaller Bitmap (comparing with IN_SAMPLE... scale types).
		 */
		EXACTLY,

		/**
		 * Image will scaled exactly to target size (scaled width or height or
		 * both will be equal to target size; depends on {@linkplain ScaleType
		 * ImageView's scale type}). Use it if memory economy is critically
		 * important.<br />
		 * Note: If original image size is smaller than target size then
		 * original image <b>will be stretched</b> to target size.<br />
		 * <br />
		 * <b>Important note:</b> For creating result Bitmap (of exact size)
		 * additional Bitmap will be created with
		 * {@link Bitmap#createScaledBitmap(Bitmap, int, int, boolean)
		 * Bitmap.createScaledBitmap(...)}. So this scale type requires more
		 * memory for creation of result Bitmap, but then save memory by keeping
		 * in memory smaller Bitmap (comparing with IN_SAMPLE... scale types).
		 */
		EXACTLY_STRETCHED
	}

	/**
	 * Simplify {@linkplain ScaleType ImageView's scale type} to 2 types:
	 * {@link #FIT_INSIDE} and {@link #CROP}
	 * 
	 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
	 * @since 1.6.1
	 */
	public enum ViewScaleType {
		/**
		 * Scale the image uniformly (maintain the image's aspect ratio) so that
		 * both dimensions (width and height) of the image will be equal to or
		 * less the corresponding dimension of the view.
		 */
		FIT_INSIDE,
		/**
		 * Scale the image uniformly (maintain the image's aspect ratio) so that
		 * both dimensions (width and height) of the image will be equal to or
		 * larger than the corresponding dimension of the view.
		 */
		CROP;
	}

	public ViewScaleType fromImageView(ImageView imageView) {
		if (imageView == null) {
			return ViewScaleType.CROP;
		}
		switch (imageView.getScaleType()) {
		case FIT_CENTER:
		case FIT_XY:
		case FIT_START:
		case FIT_END:
		case CENTER_INSIDE:
			return ViewScaleType.FIT_INSIDE;
		case MATRIX:
		case CENTER:
		case CENTER_CROP:
		default:
			return ViewScaleType.CROP;
		}
	}

	/**
	 * Present width and height values
	 * 
	 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
	 * @since 1.0.0
	 */
	public class ImageSize {

		private static final String TO_STRING_PATTERN = "%sx%s";

		private final int width;
		private final int height;

		public ImageSize(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		@Override
		public String toString() {
			return String.format(TO_STRING_PATTERN, width, height);
		}
	}

	public ImageSize createImageSize(ImageView imageView) {
		return createImageSize(imageView, 0, 0);
	}

	public ImageSize createImageSize(ImageView imageView, int defaultWidth,
			int defaultHeight) {
		if (imageView == null) {
			return new ImageSize(defaultWidth, defaultHeight);
		}
		int width = imageView.getMeasuredWidth();
		int height = imageView.getMeasuredHeight();
		if (width == 0 || height == 0) {
			DisplayMetrics displayMetrics = imageView.getContext()
					.getResources().getDisplayMetrics();
			LayoutParams params = imageView.getLayoutParams();
			width = params.width; // Get layout width parameter
			if (width <= 0)
				width = getFieldValue(imageView, "mMaxWidth"); // Check maxWidth
																// parameter
			if (width <= 0)
				width = defaultWidth;
			if (width <= 0)
				width = displayMetrics.widthPixels;

			height = params.height; // Get layout height parameter
			if (height <= 0)
				height = getFieldValue(imageView, "mMaxHeight"); // Check
																	// maxHeight
																	// parameter
			if (height <= 0)
				height = defaultHeight;
			if (height <= 0)
				height = displayMetrics.heightPixels;
		}
		return new ImageSize(width, height);
	}

	private static int getFieldValue(Object object, String fieldName) {
		int value = 0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = (Integer) field.get(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
}
