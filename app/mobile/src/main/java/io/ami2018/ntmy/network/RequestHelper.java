package io.ami2018.ntmy.network;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class RequestHelper {

    private static final String url = "http://192.168.1.110:5000/";

    public static void get(Context context, String path, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + path, responseListener, errorListener);
        QueueSingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    public static void getJson(Context context, String path, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url + path, null, responseListener, errorListener);
        QueueSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public static void getJsonArray(Context context, String path, Response.Listener<JSONArray> responseListener, Response.ErrorListener errorListener) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url + path, null, responseListener, errorListener);
        QueueSingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }

    public static void getImage(Context context, String path, Response.Listener<Bitmap> responseListener, Response.ErrorListener errorListener) {
        ImageRequest imageRequest = new ImageRequest(url + path, responseListener, 0, 0, null, Bitmap.Config.RGB_565, errorListener);
        QueueSingleton.getInstance(context).addToRequestQueue(imageRequest);
    }

    public static void postJson(Context context, String path, JSONObject jsonObject, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + path, jsonObject, responseListener, errorListener);
        QueueSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public static void delete(Context context, String path, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url + path, responseListener, errorListener);
        QueueSingleton.getInstance(context).addToRequestQueue(stringRequest);
    }
}
