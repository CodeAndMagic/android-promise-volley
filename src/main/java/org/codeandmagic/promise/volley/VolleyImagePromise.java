package org.codeandmagic.promise.volley;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageRequest;
import org.codeandmagic.promise.Pipe;
import org.codeandmagic.promise.Promise;
import org.codeandmagic.promise.impl.DeferredObject;
import org.codeandmagic.promise.volley.VolleyRequest.ImgObject;

/**
 * Created by evelina on 05/03/2014.
 */
public class VolleyImagePromise {

    private VolleyImagePromise() {
    }

    public static Promise<ImgObject> imagePromise(String url, int maxWidth, int maxHeight, Config decodeConfig) {
        final DeferredObject<Bitmap> promise = new DeferredObject<Bitmap>();

        final ImageRequest request = new ImageRequest(url, new Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                promise.success(bitmap);
            }
        }, maxWidth, maxHeight, decodeConfig, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                promise.failure(volleyError);
            }
        });

        return DeferredObject.successful(new ImgObject(request, promise));
    }

    public static Promise<Bitmap> imagePromise(final RequestQueue queue, String url, int maxWidth, int maxHeight,
                                               Config decodeConfig) {
        return imagePromise(url, maxWidth, maxHeight, decodeConfig).pipe(queueImageRequest(queue));
    }

    public static Pipe<ImgObject, Bitmap> queueImageRequest(final RequestQueue queue) {
        return new Pipe<ImgObject, Bitmap>() {
            @Override
            public Promise<Bitmap> transform(ImgObject value) {
                queue.add(value.getRequest());
                return value.getPromise();
            }
        };
    }

    public static <T> Pipe<T, Bitmap> imagePipe(final RequestQueue queue, final String url,
                                                final int maxWidth, final int maxHeight, final Config decodeConfig) {
        return new Pipe<T, Bitmap>() {
            @Override
            public Promise<Bitmap> transform(T value) {
                return imagePromise(queue, url, maxWidth, maxHeight, decodeConfig);
            }
        };
    }

    public static Promise<ImageResult> imagePromise(ImageLoader imageLoader, final String url, int maxWidth, int maxHeight) {
        final DeferredObject<ImageResult> promise = new DeferredObject<ImageResult>();

        imageLoader.get(url, new ImageListener() {
            @Override
            public void onResponse(ImageContainer imageContainer, boolean isImmediate) {
                if (imageContainer.getBitmap() != null) {
                    promise.success(new ImageResult(imageContainer, isImmediate));
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                promise.failure(volleyError);
            }
        }, maxWidth, maxHeight);

        return promise;
    }
}
