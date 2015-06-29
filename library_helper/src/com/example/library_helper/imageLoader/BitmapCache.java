package com.example.library_helper.imageLoader;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class BitmapCache implements ImageCache {
	
	private LruCache<String, Bitmap> mCache;
	private long maxCacheSize = 4 * 1024 * 1024;
	
	public BitmapCache() {
		// TODO Auto-generated constructor stub
		maxCacheSize = Math.min(maxCacheSize, Runtime.getRuntime().maxMemory()/8);
		Log.i("cache size", String.valueOf(maxCacheSize));
		
		mCache = new LruCache<String, Bitmap>((int) maxCacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight();
			}
			
		};
		
		
	}

	@Override
	public Bitmap getBitmap(String url) {
		// TODO Auto-generated method stub
		return mCache.get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		// TODO Auto-generated method stub
		Log.i("tag", "get cache " + url);
		mCache.put(url, bitmap);
		
	}

}
