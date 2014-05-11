package org.codeandmagic.promise.volley;

import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.util.LruCache;
import com.android.volley.toolbox.ImageLoader.ImageCache;

/**
 * Created by evelina on 26/04/2014.
 */
public class BitmapLruCache extends LruCache<String, Bitmap> implements ImageCache {

    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        return cacheSize;
    }

    public BitmapLruCache() {
        super(getDefaultLruCacheSize());
    }

    @Override
    protected int sizeOf(String key, Bitmap bitmap) {
        if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }


    @Override
    public Bitmap getBitmap(String key) {
        return get(key);
    }

    @Override
    public void putBitmap(String key, Bitmap bitmap) {
        put(key, bitmap);
    }
}
