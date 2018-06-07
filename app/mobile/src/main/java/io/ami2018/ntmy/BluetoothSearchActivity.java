package io.ami2018.ntmy;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import io.ami2018.ntmy.network.PersistentCookieStore;
import io.ami2018.ntmy.network.RequestHelper;
import io.ami2018.ntmy.wearconnection.MessageListener;

public class BluetoothSearchActivity extends AppCompatActivity {

    private int RSSI_THRESHOLD_VALUE = 50;

    private BluetoothHeadset mBluetoothHeadset;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private static final int REQUEST_ENABLE_BT = 1;
    private String sNode;
    private String userID;
    private String eventID;
    private String fullnameID2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_search);

        // Asks the user to enable the bluetooth if not already enabled
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        CookieManager cookieManager = new CookieManager(new PersistentCookieStore(getApplicationContext()), CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        // Get data from parent activity
        Intent intent = getIntent();
        sNode = intent.getStringExtra("sNode");
        userID = intent.getStringExtra("userID");
        eventID = intent.getStringExtra("eventID");

        // Set a receiver for discovered devices
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        // Start the discovery
        mBluetoothAdapter.startDiscovery();

        // Generally it finds it quickly
        // so after 5 sec, if not already detected, stop the discovery
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            public void run() {
                mBluetoothAdapter.cancelDiscovery();
                finish();
            }
        }, 5000);
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName(); // device name
                String deviceHardwareAddress = device.getAddress(); // MAC address
                int  rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE); // rssi value

                // If it finds a device that match with regex "^NTMY[0-9]$" ,that isn't the connected watch
                // and which rssi is under RSSI_THRESHOLD_VALUE
                if(Math.abs(rssi) < RSSI_THRESHOLD_VALUE && deviceName.matches("^NTMY[0-9]$") && !deviceName.equals("NTMY"+userID)) {
                    Toast toast = Toast.makeText(
                            context,
                            "New User: "+ deviceName,
                            Toast.LENGTH_SHORT);
                    toast.show();
                    // Obtain from the server the user's fullname
                   if (getUserFullname(deviceName)) {
                       // Send the fullname to the watch
                       Wearable.getMessageClient(context).sendMessage(sNode, MessageListener.HANDSHAKE_VERIFIED, fullnameID2.getBytes());
                   }
                   mBluetoothAdapter.cancelDiscovery();
                   finish();
                }
            }
        }
    };

    private boolean getUserFullname(String deviceName) {
        // Obtain the userID2 from the device Name
        final int userID2 =  Character.getNumericValue(deviceName.charAt(4));
        fullnameID2 = "";
        // Create a JSON to post a connection event to the server
        JSONObject json = new JSONObject();
        try {
            json.put("userID2", userID2);
            json.put("eventID", eventID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Start the post request to the server
            RequestHelper.postJson(this,"/users/"+userID+"/connections",json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // If the post get a positive response, start the GET request to obtain user's fullname
                    RequestHelper.getJsonArray(BluetoothSearchActivity.this, "users/" + userID + "/connections/user/" + userID2, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {

                                String name = response.getJSONObject(0).getJSONObject("user1").getString("name");
                                String surname = response.getJSONObject(0).getJSONObject("user1").getString("surname");
                                fullnameID2 = name + " " + surname;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast toast = Toast.makeText(
                                    getApplicationContext(),
                                    "Error on inner request",
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast toast = Toast.makeText(
                            getApplicationContext(),
                            "Error on first request",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        return !fullnameID2.equals("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }
}
