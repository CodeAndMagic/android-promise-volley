package org.codeandmagic.promise.volley;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import org.codeandmagic.promise.Pipe;
import org.codeandmagic.promise.Promise;
import org.codeandmagic.promise.impl.DeferredObject;
import org.codeandmagic.promise.volley.VolleyRequest.RArray;
import org.codeandmagic.promise.volley.VolleyRequest.RObject;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by evelina on 03/03/2014.
 */
public class VolleyJsonPromise {

    private VolleyJsonPromise() {
    }

    public static Promise<RObject> jsonObjectPromise(int method, String url, JSONObject jsonRequest) {
        final DeferredObject<JSONObject> promise = new DeferredObject<JSONObject>();

        final JsonObjectRequest request = new JsonObjectRequest(method, url, jsonRequest, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                promise.success(jsonObject);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                promise.failure(volleyError);
            }
        });

        return DeferredObject.successful(new RObject(request, promise));
    }


    public static Promise<JSONObject> jsonObjectPromise(final RequestQueue requestQueue, int method, String url, JSONObject jsonRequest) {
        return jsonObjectPromise(method, url, jsonRequest).pipe(queueJsonObjectRequest(requestQueue));
    }


    public static Promise<RObject> jsonObjectPromise(String url, JSONObject jsonRequest) {
        return jsonObjectPromise(jsonRequest == null ? Method.GET : Method.POST, url, jsonRequest);
    }

    public static Promise<JSONObject> jsonObjectPromise(RequestQueue requestQueue, String url, JSONObject jsonRequest) {
        return jsonObjectPromise(requestQueue, jsonRequest == null ? Method.GET : Method.POST, url, jsonRequest);
    }

    public static Pipe<RObject, JSONObject> queueJsonObjectRequest(final RequestQueue requestQueue) {
        return new Pipe<RObject, JSONObject>() {
            @Override
            public Promise<JSONObject> transform(RObject value) {
                requestQueue.add(value.getRequest());
                return value.getPromise();
            }
        };
    }

    public static Promise<RArray> jsonArrayPromise(String url) {
        final DeferredObject<JSONArray> promise = new DeferredObject<JSONArray>();

        final JsonArrayRequest request = new JsonArrayRequest(url, new Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                promise.success(jsonArray);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                promise.failure(volleyError);
            }
        });

        return DeferredObject.successful(new RArray(request, promise));
    }

    public static Promise<JSONArray> jsonArrayPromise(RequestQueue requestQueue, String url) {
        return jsonArrayPromise(url).pipe(queueJsonArrayRequest(requestQueue));
    }

    public static Pipe<RArray, JSONArray> queueJsonArrayRequest(final RequestQueue requestQueue) {
        return new Pipe<RArray, JSONArray>() {
            @Override
            public Promise<JSONArray> transform(RArray value) {
                requestQueue.add(value.getRequest());
                return value.getPromise();
            }
        };
    }

    public static <T> Pipe<T, JSONObject> jsonObjectPipe(final RequestQueue requestQueue, final int method,
                                                         final String url, final JSONObject jsonRequest) {
        return new Pipe<T, JSONObject>() {
            @Override
            public Promise<JSONObject> transform(T value) {
                return jsonObjectPromise(requestQueue, method, url, jsonRequest);
            }
        };
    }

    public static <T> Pipe<T, JSONObject> jsonObjectPipe(final RequestQueue requestQueue,
                                                         final String url, final JSONObject jsonRequest) {
        return new Pipe<T, JSONObject>() {
            @Override
            public Promise<JSONObject> transform(T value) {
                return jsonObjectPromise(requestQueue, url, jsonRequest);
            }
        };
    }
}
