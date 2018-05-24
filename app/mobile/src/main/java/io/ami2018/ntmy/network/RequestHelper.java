package io.ami2018.ntmy.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class RequestHelper {

    private static final String url = "http://192.168.1.110:5000/";

    public static void get(Context context, String path, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url + path, null, responseListener, errorListener);
        QueueSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public static void post(Context context, String path, JSONObject jsonObject, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + path, jsonObject, responseListener, errorListener);
        QueueSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}
