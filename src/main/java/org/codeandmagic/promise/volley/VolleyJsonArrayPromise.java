package org.codeandmagic.promise.volley;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import org.codeandmagic.promise.Pipe;
import org.codeandmagic.promise.Promise;
import org.codeandmagic.promise.impl.AbstractPromise;
import org.json.JSONArray;

/**
 * Created by evelina on 02/03/2014.
 */
public class VolleyJsonArrayPromise extends AbstractPromise<JSONArray> {

    private final JsonArrayRequest request;

    public VolleyJsonArrayPromise(RequestQueue requestQueue, String url) {

        request = new JsonArrayRequest(url, new Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                success(jsonArray);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                failure(volleyError);
            }
        });

        requestQueue.add(request);
    }

    public JsonArrayRequest getRequest() {
        return request;
    }

    public static <T> Pipe<T, JSONArray> asPipe(final RequestQueue requestQueue, final String url) {
        return new Pipe<T, JSONArray>() {
            @Override
            public Promise<JSONArray> transform(T value) {
                return new VolleyJsonArrayPromise(requestQueue, url);
            }
        };
    }
}
