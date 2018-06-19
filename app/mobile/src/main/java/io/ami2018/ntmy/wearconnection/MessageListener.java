package io.ami2018.ntmy.wearconnection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import io.ami2018.ntmy.BluetoothSearchActivity;
import io.ami2018.ntmy.model.User;
import io.ami2018.ntmy.network.RequestHelper;


public final class MessageListener extends Activity implements
        MessageClient.OnMessageReceivedListener,
        DataClient.OnDataChangedListener {

    // MessageClient paths
    public static final String REQUEST_USER_DATA_PATH = "/request_user_data";
    public static final String HANDSHAKE_HAPPENED = "/handshake_happened";
    public static final String HANDSHAKE_VERIFIED = "/handshake_verified";
    public static final String RESPONSE_USER_DATA_PATH = "/response_user_data";

    // DataClient paths
    public static final String USER_FOUND_IMAGE = "/image/user/found";
    public static final String USER_PIC_IMAGE = "/image/profile/picture";


    private static final MessageListener INSTANCE = new MessageListener();
    private Context context;
    private User mUser;
    private int eventID;

    private Bitmap photo;

    private MessageListener() {

    }

    public static MessageListener getINSTANCE() {
        return INSTANCE;
    }

    public static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

    public void set(Context context, User mUser) {
        // Update the logged user's data
        this.context = context;
        this.mUser = mUser;
        RequestHelper.getImage(context, "users/" + mUser.getUserId() + "/photo", new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                photo = response;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        // If receives a data request from the watch
        if (messageEvent.getPath().equals(REQUEST_USER_DATA_PATH)) {
            final String sNode = messageEvent.getSourceNodeId();
            final String userFullName = mUser.getName() + " " + mUser.getSurname();


            // Perform a get request to the server to obtain user's next event
            RequestHelper.getJsonArray(context, "users/" + mUser.getUserId() + "/events/next", new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(final JSONArray response) {
                    eventID = -1;
                    try {
                        // If obtains a good response, store the event id
                        eventID = response.getJSONObject(0).getJSONObject("event").getInt("eventID");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Perform another get request to obtain the event's data
                    RequestHelper.getJsonArray(context, "schedule/event/" + eventID, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                // On positive response send to the watch all the needed data
                                JSONObject result = new JSONObject();
                                sendPhoto(photo, USER_PIC_IMAGE);
                                result.put("fullname", userFullName);
                                result.put("userID", mUser.getUserId());
                                result.put("color", response.getJSONObject(0).getJSONObject("color"));
                                result.put("rName", response.getJSONObject(0).getJSONObject("room").getString("name"));
                                result.put("eName", response.getJSONObject(0).getJSONObject("event").getString("name"));
                                result.put("eTime", response.getJSONObject(0).getJSONObject("event").getString("start"));
                                Wearable.getMessageClient(context).sendMessage(sNode, RESPONSE_USER_DATA_PATH, result.toString().getBytes());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error_response 2:", error.networkResponse.toString());
                            userPutBasics(sNode);
                        }
                    });
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error_response 1:", error.networkResponse.toString());
                    userPutBasics(sNode);
                }
            });
        } else if (messageEvent.getPath().equals(HANDSHAKE_HAPPENED)) {
            // If the watch signal to the phone that an handshake happened
            String sNode = messageEvent.getSourceNodeId();
            // start the bluetooth search activity
            Intent intent = new Intent(this.context, BluetoothSearchActivity.class);
            intent.putExtra("sNode", sNode);
            intent.putExtra("userID", mUser.getUserId());
            intent.putExtra("eventID", eventID);
            context.startActivity(intent);
        }

    }

    private void userPutBasics(String sNode) {
        // Send a message with all user's data except event data
        try {
            JSONObject result = new JSONObject();
            result.put("fullname", mUser.getName() + " " + mUser.getSurname());
            result.put("userID", mUser.getUserId());
            Wearable.getMessageClient(context).sendMessage(sNode, RESPONSE_USER_DATA_PATH, result.toString().getBytes());
            sendPhoto(photo, USER_PIC_IMAGE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendPhoto(Bitmap bitmap, String path) {
        // Create an asset from a bitmap and send it to the watch

        Asset asset = createAssetFromBitmap(bitmap);
        PutDataMapRequest dataMap = PutDataMapRequest.create(path);
        if (path.equals(USER_PIC_IMAGE))
            dataMap.getDataMap().putAsset("profileImage", asset);
        else if (path.equals(USER_FOUND_IMAGE))
            dataMap.getDataMap().putAsset("userFoundPicture", asset);
        dataMap.getDataMap().putLong("time", new Date().getTime());
        PutDataRequest request = dataMap.asPutDataRequest();
        request.setUrgent();

        Task<DataItem> dataItemTask = Wearable.getDataClient(context).putDataItem(request);

        dataItemTask.addOnSuccessListener(new OnSuccessListener<DataItem>() {
            @Override
            public void onSuccess(DataItem dataItem) {
                Log.d("IMAGE", "Sending image was successful: " + dataItem);
            }
        });
    }


    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {

    }
}
