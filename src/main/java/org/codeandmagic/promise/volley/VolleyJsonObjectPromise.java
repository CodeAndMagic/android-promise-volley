package org.codeandmagic.promise.volley;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.codeandmagic.promise.Pipe;
import org.codeandmagic.promise.Promise;
import org.codeandmagic.promise.impl.AbstractPromise;
import org.json.JSONObject;

/**
 * Created by evelina on 28/02/2014.
 */
public class VolleyJsonObjectPromise extends AbstractPromise<JSONObject> {

    private final JsonObjectRequest request;

    public VolleyJsonObjectPromise(RequestQueue requestQueue, int method, String url, JSONObject jsonRequest) {
        request = new JsonObjectRequest(method, url, jsonRequest, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                success(jsonObject);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                failure(volleyError);
            }
        });
        requestQueue.add(request);
    }

    public VolleyJsonObjectPromise(RequestQueue requestQueue, String url, JSONObject jsonRequest) {
        this(requestQueue, jsonRequest == null ? Method.GET : Method.POST, url, jsonRequest);
    }

    public JsonObjectRequest getRequest() {
        return request;
    }

    public static <T> Pipe<T, JSONObject> asPipe(final RequestQueue requestQueue, final int method,
                                                 final String url, final JSONObject jsonRequest) {
        return new Pipe<T, JSONObject>() {
            @Override
            public Promise<JSONObject> transform(T value) {
                return new VolleyJsonObjectPromise(requestQueue, method, url, jsonRequest);
            }
        };
    }

    public static <T> Pipe<T, JSONObject> asPipe(final RequestQueue requestQueue, final String url,
                                                 final JSONObject jsonRequest) {
        return new Pipe<T, JSONObject>() {
            @Override
            public Promise<JSONObject> transform(T value) {
                return new VolleyJsonObjectPromise(requestQueue, url, jsonRequest);
            }
        };
    }

}
