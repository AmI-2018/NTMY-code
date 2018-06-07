package io.ami2018.ntmy.wearconnection;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.ami2018.ntmy.BluetoothSearchActivity;
import io.ami2018.ntmy.model.User;
import io.ami2018.ntmy.network.RequestHelper;


public class MessageListener implements MessageClient.OnMessageReceivedListener {

    public static final String REQUEST_USER_DATA_PATH = "/request_user_data";
    public static final String HANDSHAKE_HAPPENED = "/handshake_happened";
    public static final String HANDSHAKE_VERIFIED = "/handshake_verified";
    public static final String RESPONSE_USER_DATA_PATH = "/response_user_data";

    private Context context;
    private User mUser;
    private int eventID;

    public MessageListener(Context context, User mUser){
        this.context=context;
        this.mUser = mUser;
    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        // If receives a data request from the watch
        if (messageEvent.getPath().equals(REQUEST_USER_DATA_PATH)){
            final String sNode = messageEvent.getSourceNodeId();
            final String userFullName = mUser.getName()+ " " +mUser.getSurname();

            // Perform a get request to the server to obtain user's next event
            RequestHelper.getJsonArray(context, "users/" + mUser.getUserId() + "/events/next", new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
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

                        }
                    });
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

        }else
        if (messageEvent.getPath().equals(HANDSHAKE_HAPPENED)){
            // If the watch signal to the phone that an handshake happened
            String sNode = messageEvent.getSourceNodeId();
            // start the bluetooth search activity
            Intent intent = new Intent(this.context, BluetoothSearchActivity.class);
            intent.putExtra("sNode",sNode);
            intent.putExtra("userID",mUser.getUserId());
            intent.putExtra("eventID",eventID);
            context.startActivity(intent);
        }

    }

}
