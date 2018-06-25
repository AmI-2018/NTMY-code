package io.ami2018.ntmy;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import io.ami2018.ntmy.network.PersistentCookieStore;
import io.ami2018.ntmy.network.RequestHelper;
import io.ami2018.ntmy.wearconnection.MessageListener;

public class BluetoothSearchActivity extends AppCompatActivity {

    private static final int RSSI_THRESHOLD_VALUE = 50;
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter ;
    private String sNode;
    private int userID;
    private int eventID;
    private JSONObject json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_search);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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
        userID = intent.getIntExtra("userID", -1);
        eventID = intent.getIntExtra("eventID", -1);

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
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE); // rssi value

                // If it finds a device that match with regex "^NTMY[0-9]+$" ,that isn't the connected watch
                // and which rssi is under RSSI_THRESHOLD_VALUE
                if (Math.abs(rssi) < RSSI_THRESHOLD_VALUE && deviceName.matches("^NTMY[0-9]+$") && !deviceName.equals("NTMY" + userID)) {
                    // Obtain from the server the user's fullname
                    tryGetUserFullname(deviceName);
                    mBluetoothAdapter.cancelDiscovery();
                    finish();
                }
            }
        }
    };

    private void tryGetUserFullname(String deviceName) {
        // Obtain the userID2 from the device Name
        final int userID2 = Integer.parseInt(deviceName.replace("NTMY",""));
        // Create a JSON to post a connection event to the server
        json = new JSONObject();
        try {
            json.put("userID2", userID2);
            json.put("eventID", eventID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Start the post request to the server
        RequestHelper.postJson(this, "users/" + userID + "/connections", json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // If the post get a positive response
                try {
                    String fullname = response.getJSONObject("user2").getString("name") + " ";
                    fullname += response.getJSONObject("user2").getString("surname");

                    // Show user's fullname with a toast
                    Toast toast = Toast.makeText(
                            getApplicationContext(),
                            "+ " + fullname,
                            Toast.LENGTH_SHORT);
                    toast.show();

                    // send the fullname to the watch
                    Wearable.getMessageClient(getApplicationContext()).sendMessage(sNode, MessageListener.HANDSHAKE_VERIFIED, fullname.getBytes());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Perform a GET request to obtain found user's pic
                RequestHelper.getImage(BluetoothSearchActivity.this, "users/" + userID2 + "/photo", new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        // Send the asset to the watch
                        MessageListener.getINSTANCE().sendPhoto(response, MessageListener.USER_FOUND_IMAGE);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
