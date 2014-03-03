package org.codeandmagic.promise.volley;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import org.codeandmagic.promise.Promise;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by evelina on 03/03/2014.
 */
public class VolleyRequest<R, T> {

    public static class RObject extends VolleyRequest<JsonObjectRequest, JSONObject> {

        public RObject(JsonObjectRequest request, Promise<JSONObject> promise) {
            super(request, promise);
        }
    }

    public static class RArray extends VolleyRequest<JsonArrayRequest, JSONArray> {

        public RArray(JsonArrayRequest request, Promise<JSONArray> promise) {
            super(request, promise);
        }
    }

    private final R request;
    private final Promise<T> promise;

    public VolleyRequest(R request, Promise<T> promise) {
        this.request = request;
        this.promise = promise;
    }

    public R getRequest() {
        return request;
    }

    public Promise<T> getPromise() {
        return promise;
    }
}
