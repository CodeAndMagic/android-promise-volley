/*
 * Copyright (c) 2014 CodeAndMagic
 * Cristian Vrabie, Evelina Vrabie
 *
 * This file is part of android-promise.
 * android-promise is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License,or (at your option)
 * any later version.
 *
 * android-promise is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with android-promise. If not, see <http://www.gnu.org/licenses/>.
 */

package org.codeandmagic.promise.volley;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.*;
import java.text.Normalizer;
import java.text.Normalizer.Form;

/**
 * Created by evelina on 18/03/2014.
 */
public class DiskBitmapCache implements ImageCache {

    private static final String TAG = DiskBitmapCache.class.getSimpleName();
    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;
    private static final int IO_BUFFER_SIZE = 8 * 1024;
    private static final long DEFAULT_CACHE_SIZE = 1024 * 1024 * 10;
    private static final int COMPRESS_QUALITY = 100;
    private static final CompressFormat COMPRESS_FORMAT = CompressFormat.PNG;

    private DiskLruCache mDiskCache;

    public DiskBitmapCache(File diskCacheDir, long diskCacheSize) throws IOException {
        mDiskCache = DiskLruCache.open(diskCacheDir, APP_VERSION, VALUE_COUNT, diskCacheSize);
    }

    public DiskBitmapCache(File diskCacheDir) throws IOException {
        this(diskCacheDir, DEFAULT_CACHE_SIZE);
    }

    @Override
    public Bitmap getBitmap(String url) {
        final String key = getKey(url);
        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = mDiskCache.get(key);
            if (snapshot == null) {
                return null;
            }
            final InputStream in = snapshot.getInputStream(0);
            if (in != null) {
                bitmap = BitmapFactory.decodeStream(new BufferedInputStream(in, IO_BUFFER_SIZE));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, bitmap == null ? "" : "image read from disk " + key);
        }

        return bitmap;
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        final String key = getKey(url);
        DiskLruCache.Editor editor = null;
        try {
            editor = mDiskCache.edit(key);
            if (editor == null) {
                return;
            }

            if (writeBitmapToFile(bitmap, editor)) {
                mDiskCache.flush();
                editor.commit();
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, "image put on disk cache " + key);
                }
            } else {
                editor.abort();
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, "ERROR on: image put on disk cache " + key);
                }
            }
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "ERROR on: image put on disk cache " + key);
            }
            try {
                if (editor != null) {
                    editor.abort();
                }
            } catch (IOException ignored) {
            }
        }
    }

    private boolean writeBitmapToFile(Bitmap bitmap, DiskLruCache.Editor editor) throws IOException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(editor.newOutputStream(0), IO_BUFFER_SIZE);
            return bitmap.compress(COMPRESS_FORMAT, COMPRESS_QUALITY, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    // TODO do something about it...
    private String getKey(String url) {
        final String lastPathSegment = Uri.parse(url).getLastPathSegment();
        return Normalizer.normalize(lastPathSegment, Form.NFD).replaceAll("[^A-Za-z0-9]", "");
    }
}
