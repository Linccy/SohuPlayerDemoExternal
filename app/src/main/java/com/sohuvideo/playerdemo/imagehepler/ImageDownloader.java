/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sohuvideo.playerdemo.imagehepler;

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

/**
 * This helper class download images from the Internet and binds those with the
 * provided ImageView.
 * <p>
 * It requires the INTERNET permission, which should be added to your
 * application's manifest file.
 * </p>
 * A local cache of downloaded images is maintained internally to improve
 * performance.
 */
public class ImageDownloader {
	// private static final int TASK_HOLDER_TAG = R.id.task_holder_tag;
	private static final int TASK_HOLDER_TAG = Integer.MAX_VALUE - 1;
	private static final int IMAGE_RESULT_TAG = Integer.MAX_VALUE - 2;

	/**
	 * Download the specified image from the Internet and binds it to the
	 * provided ImageView. The binding is immediate if the image is found in the
	 * cache and will be done asynchronously otherwise. A null bitmap will be
	 * associated to the ImageView if an error occurs.
	 * 
	 * @param url
	 *            The URL of the image to download.
	 * @param imageView
	 *            The ImageView to bind the downloaded image to.
	 */
	public void download(String url, ImageView imageView) {
		resetPurgeTimer();
		Bitmap bitmap = getBitmapFromCache(url);
		if (bitmap == null) {
			forceDownload(url, imageView);
		} else {
			cancelPotentialDownload(url, imageView);
			imageView.setImageBitmap(bitmap);
			onResult(true, imageView);
		}
	}

	/**
	 * Download the specified image from the Internet and binds it to the
	 * provided ImageView. The binding is immediate if the image is found in the
	 * cache and will be done asynchronously otherwise. A null bitmap will be
	 * associated to the ImageView if an error occurs.
	 * 
	 * @param url
	 *            The URL of the image to download.
	 * @param imageView
	 *            The ImageView to bind the downloaded image to.
	 */
	public void download(String url, ImageView imageView, int defaultRes) {
		resetPurgeTimer();
		Bitmap bitmap = getBitmapFromCache(url);
		if (bitmap == null) {
			imageView.setImageResource(defaultRes);
			forceDownload(url, imageView);
		} else {
			cancelPotentialDownload(url, imageView);
			imageView.setImageBitmap(bitmap);
			onResult(true, imageView);
		}
	}

	public void download(String url, ImageView imageView, int defaultRes,
			OnResultListener listener) {
		bind(imageView, listener);
		download(url, imageView, defaultRes);
	}

	public void download(String url, ImageView imageView,
			OnResultListener listener) {
		bind(imageView, listener);
		download(url, imageView);
	}

	private void bind(ImageView view, OnResultListener listener) {
		if (listener != null) {
			view.setTag(IMAGE_RESULT_TAG, listener);
		}
	}

	/*
	 * Same as download but the image is always downloaded and the cache is not
	 * used. Kept private at the moment as its interest is not clear. private
	 * void forceDownload(String url, ImageView view) { forceDownload(url, view,
	 * null); }
	 */
	/**
	 * Same as download but the image is always downloaded and the cache is not
	 * used. Kept private at the moment as its interest is not clear.
	 */
	private void forceDownload(String url, ImageView imageView) {
		// State sanity: url is guaranteed to never be null in
		// DownloadedDrawable and cache keys.
		if (url == null) {
			imageView.setTag(TASK_HOLDER_TAG, null);
			return;
		}
		if (cancelPotentialDownload(url, imageView)) {
			BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
			TaskHolder taskHolder = new TaskHolder(task);
			imageView.setTag(TASK_HOLDER_TAG, taskHolder);
			task.execute(url);
		}
	}

	/**
	 * Returns true if the current download has been canceled or if there was no
	 * download in progress on this image view. Returns false if the download in
	 * progress deals with the same url. The download is not stopped in that
	 * case.
	 */
	private static boolean cancelPotentialDownload(String url,
			ImageView imageView) {
		BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
		if (bitmapDownloaderTask != null) {
			String bitmapUrl = bitmapDownloaderTask.url;
			if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
				bitmapDownloaderTask.cancel(true);
			} else {
				// The same URL is already being downloaded.
				return false;
			}
		}
		return true;
	}

	/**
	 * @param imageView
	 *            Any imageView
	 * @return Retrieve the currently active download task (if any) associated
	 *         with this imageView. null if there is no such task.
	 */
	private static BitmapDownloaderTask getBitmapDownloaderTask(
			ImageView imageView) {
		if (imageView != null) {
			TaskHolder drawable = (TaskHolder) imageView
					.getTag(TASK_HOLDER_TAG);
			if (drawable != null)
				return drawable.getBitmapDownloaderTask();
		}
		return null;
	}

	/*
	 * An InputStream that skips the exact number of bytes provided, unless it
	 * reaches EOF.
	 */
	static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int b = read();
					if (b < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}

	/**
	 * The actual AsyncTask that will asynchronously download the image.
	 */
	class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
		private String url;
		private final WeakReference<ImageView> imageViewReference;

		public BitmapDownloaderTask(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		/**
		 * Actual download method.
		 */
		@Override
		protected Bitmap doInBackground(String... params) {
			url = params[0];
			ImageOptions options = null;
			if (imageViewReference != null) {
				ImageView imageView = imageViewReference.get();
				if (imageView != null) {
					options = createImageOptions(imageView);
				}
			}
			return downloadBitmap(url, options);
		}

		/**
		 * Once the image is downloaded, associates it to the imageView
		 */
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}
			addBitmapToCache(url, bitmap);
			if (imageViewReference != null) {
				ImageView imageView = imageViewReference.get();
				BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
				// Change bitmap only if this process is still associated with
				// it
				// Or if we don't use any bitmap to task association
				// (NO_DOWNLOADED_DRAWABLE mode)
				if (this == bitmapDownloaderTask && bitmap != null) {
					imageView.setImageBitmap(bitmap);
					onResult(true, imageView);
				} else {
					onResult(false, imageView);
				}
			}
		}

		private ImageOptions createImageOptions(ImageView imageView) {
			ImageOptions options = new ImageOptions();
			options.mTargetSize = options.createImageSize(imageView);
			options.mViewScaleType = options.fromImageView(imageView);
			if (options.mTargetSize != null
					&& options.mTargetSize.getWidth() > 0
					&& options.mTargetSize.getHeight() > 0) {
				options.mImageScaleType = ImageOptions.ImageScaleType.IN_SAMPLE_POWER_OF_2;
			} else {
				options.mImageScaleType = ImageOptions.ImageScaleType.NONE;
			}
			return options;
		}
	}

	private void onResult(boolean ret, ImageView imageView) {
		if (imageView != null) {
			// OnResultListener listener = (OnResultListener)
			// imageView.getTag(R.id.image_result_tag);
			OnResultListener listener = (OnResultListener) imageView
					.getTag(IMAGE_RESULT_TAG);
			if (listener != null) {
				listener.onResult(ret, imageView);
			}
		}
	}

	static class TaskHolder {
		private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

		public TaskHolder(BitmapDownloaderTask bitmapDownloaderTask) {
			bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(
					bitmapDownloaderTask);
		}

		public BitmapDownloaderTask getBitmapDownloaderTask() {
			return bitmapDownloaderTaskReference.get();
		}
	}

	// /**
	// * A fake Drawable that will be attached to the imageView while the
	// download
	// *
	// * <p>
	// * Contains a reference to the actual download task, so that a download
	// task
	// * can be stopped if a new binding is required, and makes sure that only
	// the
	// * last started download process can bind its result, independently of the
	// * download finish order.
	// * </p>
	// */
	// static class DownloadedDrawable extends ColorDrawable {
	// private final WeakReference<BitmapDownloaderTask>
	// bitmapDownloaderTaskReference;
	//
	// public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
	// super(Color.BLACK);
	// bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(
	// bitmapDownloaderTask);
	// }
	//
	// public BitmapDownloaderTask getBitmapDownloaderTask() {
	// return bitmapDownloaderTaskReference.get();
	// }
	// }
	/*
	 * Cache-related fields and methods. We use a hard and a soft cache. A soft
	 * reference cache is too aggressively cleared by the Garbage Collector.
	 */
	private static final int HARD_CACHE_CAPACITY = 10;
	private static final int DELAY_BEFORE_PURGE = 100 * 1000; // in milliseconds
	// Hard cache, with a fixed maximum capacity and a life duration
	@SuppressWarnings("serial")
	private final HashMap<String, Bitmap> sHardBitmapCache = new LinkedHashMap<String, Bitmap>(
			HARD_CACHE_CAPACITY / 2, 0.75f, true) {
		@Override
		protected boolean removeEldestEntry(
				LinkedHashMap.Entry<String, Bitmap> eldest) {
			if (size() > HARD_CACHE_CAPACITY) {
				// Entries push-out of hard reference cache are transferred to
				// soft reference cache
				sSoftBitmapCache.put(eldest.getKey(),
						new SoftReference<Bitmap>(eldest.getValue()));
				return true;
			} else
				return false;
		}
	};
	// Soft cache for bitmaps kicked out of hard cache
	private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(
			HARD_CACHE_CAPACITY / 2);
	private Handler purgeHandler;
	private final Runnable purger = new Runnable() {
		@Override
		public void run() {
			clearCache();
		}
	};

	/**
	 * Adds this bitmap to the cache.
	 * 
	 * @param bitmap
	 *            The newly downloaded bitmap.
	 */
	private void addBitmapToCache(String url, Bitmap bitmap) {
		if (bitmap != null) {
			synchronized (sHardBitmapCache) {
				sHardBitmapCache.put(url, bitmap);
			}
		}
	}

	/**
	 * @param url
	 *            The URL of the image that will be retrieved from the cache.
	 * @return The cached bitmap or null if it was not found.
	 */
	private Bitmap getBitmapFromCache(String url) {
		if (TextUtils.isEmpty(url)) {
			return null;
		}
		// First try the hard reference cache
		synchronized (sHardBitmapCache) {
			final Bitmap bitmap = sHardBitmapCache.get(url);
			if (bitmap != null) {
				// Bitmap found in hard cache
				// Move element to first position, so that it is removed last
				sHardBitmapCache.remove(url);
				sHardBitmapCache.put(url, bitmap);
				return bitmap;
			}
		}
		// Then try the soft reference cache
		SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(url);
		if (bitmapReference != null) {
			final Bitmap bitmap = bitmapReference.get();
			if (bitmap != null) {
				// Bitmap found in soft cache
				return bitmap;
			} else {
				// Soft reference has been Garbage Collected
				sSoftBitmapCache.remove(url);
			}
		}
		return null;
	}

	/**
	 * Clears the image cache used internally to improve performance. Note that
	 * for memory efficiency reasons, the cache will automatically be cleared
	 * after a certain inactivity delay.
	 */
	public void clearCache() {
		sHardBitmapCache.clear();
		sSoftBitmapCache.clear();
	}

	/**
	 * Allow a new delay before the automatic cache clear is done.
	 */
	private void resetPurgeTimer() {
		if (purgeHandler == null) {
			if (Looper.myLooper() == null) {
				Looper.prepare();
			}
			purgeHandler = new Handler();
		}
		purgeHandler.removeCallbacks(purger);
		purgeHandler.postDelayed(purger, DELAY_BEFORE_PURGE);
	}

	public interface OnResultListener {
		void onResult(boolean ret, ImageView image);
	}

	/**
	 * ����ͼƬ,ֱ�ӽ���Ϊbitmap����
	 * 
	 * @param url
	 * @param useCache
	 * @return
	 */
	private Bitmap downloadBitmap(String url, ImageOptions options) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setConnectTimeout(0);
			conn.setDoInput(true);
			conn.connect();
			if (conn.getResponseCode() == 200) {
				InputStream is = conn.getInputStream();
				byte[] data = readStream(is);
				bitmap = ImageDecoder.decode(data, options);
				is.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			outstream.write(buffer, 0, len);
		}
		outstream.close();
		inStream.close();
		return outstream.toByteArray();
	}
}
