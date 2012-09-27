/**
 * ImageCache.java
 */
package org.dyndns.datsnet.myflickr.cache;

import java.util.HashMap;

import android.graphics.Bitmap;


public class ImageCache {
	private static HashMap<String, Bitmap> cache = new HashMap<String, Bitmap>();

	public static Bitmap getImage(String key) {
		if (cache.containsKey(key)) {
			// Log.d("cache", "cache hit!");
			return cache.get(key);
		}
		return null;
	}

	public static void setImage(String key, Bitmap image) {
		try {
			cache.put(key, image);

		} catch (OutOfMemoryError e) {
			ImageCache.clearCache();
			e.printStackTrace();
		}

	}

	public static void clearCache() {
		cache = new HashMap<String, Bitmap>();
	}
}
