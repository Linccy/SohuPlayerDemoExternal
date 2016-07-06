/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.sohuvideo.playerdemo.imagehepler;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

/**
 * Decodes images to {@link Bitmap}, scales them to needed size
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public class ImageDecoder {
	private static final String TAG = "ImageDecoder";

	/**
	 * Decodes image from byte[] into {@link Bitmap}. Image is scaled close to
	 * incoming {@link ImageOptions}
	 * 
	 * @param data
	 * @param imageOptions
	 *            {@link ImageOptions image options}
	 * @return Decoded bitmap
	 * @throws IOException
	 */
	public static Bitmap decode(byte[] data, ImageOptions imageOptions)
			throws IOException {
		Options decodeOptions = getBitmapOptionsForImageDecoding(data,
				imageOptions);
		Bitmap subsampledBitmap = null;
		try {
			subsampledBitmap = BitmapFactory.decodeByteArray(data, 0,
					data.length, decodeOptions);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			subsampledBitmap = null;
			System.gc();
		} catch (Throwable t) {
			t.printStackTrace();
			System.gc();
		}

		if (subsampledBitmap == null) {
			return null;
		}

		// Scale to exact size if need
		if (imageOptions != null && imageOptions.mTargetSize != null) {
			ImageOptions.ImageScaleType scaleType = imageOptions.mImageScaleType;
			if (scaleType == ImageOptions.ImageScaleType.EXACTLY
					|| scaleType == ImageOptions.ImageScaleType.EXACTLY_STRETCHED) {
				subsampledBitmap = scaleImageExactly(subsampledBitmap,
						imageOptions);
			}
		}
		return subsampledBitmap;
	}

	private static Options getBitmapOptionsForImageDecoding(byte[] data,
			ImageOptions imageOptions) throws IOException {
		Options decodeOptions = new Options();
		if (imageOptions != null) {
			int inSampleSize = 1;
			if (imageOptions.mImageScaleType != ImageOptions.ImageScaleType.NONE
					&& imageOptions.mTargetSize != null) {
				inSampleSize = computeImageScale(data, imageOptions);
			}
			decodeOptions.inSampleSize = inSampleSize;
			decodeOptions.inPreferredConfig = imageOptions.mBitmapConfig;
			decodeOptions.inPurgeable = true;
		}
		return decodeOptions;
	}

	private static int computeImageScale(byte[] data, ImageOptions imageOptions)
			throws IOException {
		float scale = 1;

		ImageOptions.ImageSize targetSize = imageOptions.mTargetSize;
		int targetWidth = targetSize.getWidth();
		int targetHeight = targetSize.getHeight();
		if (targetWidth <= 0 || targetHeight <= 0) {
			return Math.round(scale);
		}

		// decode image size
		Options options = new Options();
		options.inJustDecodeBounds = true;
		try {
			BitmapFactory.decodeByteArray(data, 0, data.length, options);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			System.gc();
		}

		float imageWidth = options.outWidth;
		float imageHeight = options.outHeight;
		float widthScale = imageWidth / targetWidth;
		float heightScale = imageHeight / targetHeight;

		ImageOptions.ImageScaleType scaleType = imageOptions.mImageScaleType;
		ImageOptions.ViewScaleType viewScaleType = imageOptions.mViewScaleType;
		if (viewScaleType == ImageOptions.ViewScaleType.FIT_INSIDE) {
			if (scaleType == ImageOptions.ImageScaleType.IN_SAMPLE_POWER_OF_2) {
				while (imageWidth / 2 >= targetWidth
						|| imageHeight / 2 >= targetHeight) { // ||
					imageWidth /= 2;
					imageHeight /= 2;
					scale *= 2;
				}
				if (scale <= 1) {
					scale = Math.max(widthScale, heightScale); // max
				}
			} else {
				scale = Math.max(widthScale, heightScale); // max
			}
		} else { // ViewScaleType.CROP
			if (scaleType == ImageOptions.ImageScaleType.IN_SAMPLE_POWER_OF_2) {
				while (imageWidth / 2 >= targetWidth
						&& imageHeight / 2 >= targetHeight) { // &&
					imageWidth /= 2;
					imageHeight /= 2;
					scale *= 2;
				}
			} else {
				scale = Math.min(widthScale, heightScale); // min
			}
		}

		if (scale < 1) {
			scale = 1;
		}

		if (scale > 1.2 && scale < 2) {
			scale = 2;
		}
		return Math.round(scale);
	}

	private static Bitmap scaleImageExactly(Bitmap subsampledBitmap,
			ImageOptions imageOptions) {

		ImageOptions.ImageSize targetSize = imageOptions.mTargetSize;
		int targetWidth = targetSize.getWidth();
		int targetHeight = targetSize.getHeight();
		if (targetWidth <= 0 || targetHeight <= 0) {
			return subsampledBitmap;
		}

		float srcWidth = subsampledBitmap.getWidth();
		float srcHeight = subsampledBitmap.getHeight();

		float widthScale = srcWidth / targetWidth;
		float heightScale = srcHeight / targetHeight;

		ImageOptions.ImageScaleType scaleType = imageOptions.mImageScaleType;
		ImageOptions.ViewScaleType viewScaleType = imageOptions.mViewScaleType;
		int destWidth;
		int destHeight;
		if ((viewScaleType == ImageOptions.ViewScaleType.FIT_INSIDE && widthScale >= heightScale)
				|| (viewScaleType == ImageOptions.ViewScaleType.CROP && widthScale < heightScale)) {
			destWidth = targetWidth;
			destHeight = (int) (srcHeight / widthScale);
		} else {
			destWidth = (int) (srcWidth / heightScale);
			destHeight = targetHeight;
		}

		Bitmap scaledBitmap;
		if ((scaleType == ImageOptions.ImageScaleType.EXACTLY
				&& destWidth < srcWidth && destHeight < srcHeight)
				|| (scaleType == ImageOptions.ImageScaleType.EXACTLY_STRETCHED
						&& destWidth != srcWidth && destHeight != srcHeight)) {

			try {
				scaledBitmap = Bitmap.createScaledBitmap(subsampledBitmap,
						destWidth, destHeight, true);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				scaledBitmap = subsampledBitmap;
				System.gc();
			}

			if (scaledBitmap != subsampledBitmap) {
				subsampledBitmap.recycle();
			}
		} else {
			scaledBitmap = subsampledBitmap;
		}

		return scaledBitmap;
	}
}