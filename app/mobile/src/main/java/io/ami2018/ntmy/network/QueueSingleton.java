package io.ami2018.ntmy.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class QueueSingleton {
    private static QueueSingleton mInstance;
    private static Context mCtx;
    private RequestQueue mQueue;

    private QueueSingleton(Context context) {
        mCtx = context;
        mQueue = getRequestQueue();
    }

    public static synchronized QueueSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new QueueSingleton(context);
        }
        return mInstance;
    }

    private RequestQueue getRequestQueue() {
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
