
package io.ami2018.ntmy;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.wearable.activity.WearableActivity;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends WearableActivity implements
        SensorEventListener,
        MessageClient.OnMessageReceivedListener{

    // Accelerometer parameters
    private final double epsilon = 2.0;
    private static final int COUNTER_THRESHOLD = 10;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Vibrator mvibrator;

    // Handshake flags & counters
    private boolean handshake_started = false;
    private int handshake_counter;
    private boolean computing = false;
    private int computing_counter;

    private int n = 0;

    // MessageClient paths
    private static final String REQUEST_USER_DATA_PATH = "/request_user_data";
    private static final String RESPONSE_USER_DATA_PATH = "/response_user_data";
    private static final String HANDSHAKE_HAPPENED = "/handshake_happened";
    private static final String HANDSHAKE_VERIFIED = "/handshake_verified";
    private boolean sync = false;
    private String phoneID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Make the device discoverable
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        startActivity(discoverableIntent);

        // Set the sensors
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mvibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);

        // Set the messageclient and send a message to the phone to obtain the data
        Wearable.getMessageClient(this).addListener(this);
        new MessageSender(getApplicationContext(),REQUEST_USER_DATA_PATH,"").start();

        setAmbientEnabled();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // We need accelerometer event only
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER )
            return;

        // The handshake is detected as an acceleration in the Y axis (excluding gravity)
        float accX = Math.abs(event.values[0]);
        float accY = Math.abs(Math.abs(event.values[1]) - SensorManager.GRAVITY_EARTH);
        float accZ = Math.abs(event.values[2]);

        // If there is a signifier acceleration in the Y axis only
        if (accY >= 7 && accX < epsilon && accZ < epsilon && !computing){
            // Handshake

            // Start to detect handshake
            if(!handshake_started){
                handshake_started=true;
                handshake_counter =0;
            }
            handshake_counter++;


        }else
            // If the device is still
            if (accX < epsilon && accY < epsilon && accZ < epsilon && handshake_started){

            // Start to detect the still period
            if(!computing) {
                computing = true;
                computing_counter = 0;
            }

            computing_counter++;

            // If device stay still more than COUNTER_THRESHOLD times
            if(computing_counter >= COUNTER_THRESHOLD){
                // The handshake is ended
                handshake_started = false;

                // If device has an acceleration in the Y axis more than COUNTER_THRESHOLD times
                if (handshake_counter >= COUNTER_THRESHOLD) {
                    // Send a message to the phone.
                    Wearable.getMessageClient(getApplicationContext()).sendMessage(phoneID,HANDSHAKE_HAPPENED,null);
                    Toast toast = Toast.makeText(
                            getApplicationContext(),
                            "Handshake!",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
                // Even if the handshake happened or not, the still period ends
                // and it can starts to detects other handshakes.
                computing = false;
            }

        }


    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {

        // If the watch receives a response from the phone for the data request
        if (messageEvent.getPath().equals(RESPONSE_USER_DATA_PATH) && !sync) {
            // Store the phone's node ID for future message
            phoneID = messageEvent.getSourceNodeId();
            // Set the content to be changed
            TextView user = findViewById(R.id.uName);
            TextView rName = findViewById(R.id.rName);
            TextView eName = findViewById(R.id.eName);
            TextView eTime = findViewById(R.id.eTime);
            ConstraintLayout eBackground = findViewById(R.id.main_background);

            JSONObject data;
            try {
                // Get the bluetooth adapter
                BluetoothAdapter mBA = BluetoothAdapter.getDefaultAdapter();
                // Store the data received by the phone
                data = new JSONObject(new String(messageEvent.getData()));
                // Change the watch's device name according to userID
                mBA.setName("NTMY"+data.getString("userID"));
                // Set fields in layout
                user.setText(data.getString("fullname"));
                rName.setText(data.getString("rName"));
                eName.setText(data.getString("eName"));
                eTime.setText(data.getString("eTime"));
                // change the background color according to event color
                eBackground.setBackgroundColor(new Color(data.getJSONObject("color")).getIntColor());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            sync = true;

        } else if (messageEvent.getPath().equals(HANDSHAKE_VERIFIED)) {
            // Once the phone verified the handshake detected by the watch
            // shows a message with detected user's fullname obtained from the server
            String username = new String (messageEvent.getData());
            Toast toast = Toast.makeText(
                    getApplicationContext(),
                    "+ " + username,
                    Toast.LENGTH_SHORT);
            toast.show();
            mvibrator.vibrate(200);
        }


    }
}

