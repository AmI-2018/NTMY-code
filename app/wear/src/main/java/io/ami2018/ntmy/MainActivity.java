
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

    private float accX;
    private float accY;
    private float accZ;
    private final double epsilon = 2.0;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Vibrator mvibrator;
    private boolean handshake_started = false;
    private int handshake_counter;
    private boolean computing = false;
    private int computing_counter;

    private int n = 0;
    private static final int COUNTER_THRESHOLD = 10;

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

        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        startActivity(discoverableIntent);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mvibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);

        Wearable.getMessageClient(this).addListener(this);
        new MessageSender(getApplicationContext(),REQUEST_USER_DATA_PATH,"").start();

        setAmbientEnabled();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER )
            return;


        accX = Math.abs(event.values[0]);
        accY = Math.abs(Math.abs(event.values[1]) - SensorManager.GRAVITY_EARTH);
        accZ = Math.abs(event.values[2]);


        if (accY >= 7 && accX < epsilon && accZ < epsilon && !computing){
            // Handshake

            if(!handshake_started){
                handshake_started=true;
                handshake_counter =0;
            }
            handshake_counter++;


        }else if (accX < epsilon && accY < epsilon && accZ < epsilon && handshake_started){
            if(!computing) {
                computing = true;
                computing_counter = 0;
            }

            computing_counter++;

            if(computing_counter >= COUNTER_THRESHOLD){
                handshake_started = false;

                if (handshake_counter >= COUNTER_THRESHOLD) {
                    Wearable.getMessageClient(getApplicationContext()).sendMessage(phoneID,HANDSHAKE_HAPPENED,null);
                    Toast toast = Toast.makeText(
                            getApplicationContext(),
                            "Handshake!",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
                computing = false;
            }

        }


    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {

        if (messageEvent.getPath().equals(RESPONSE_USER_DATA_PATH) && !sync) {
            phoneID = messageEvent.getSourceNodeId();
            TextView user = findViewById(R.id.uName);
            TextView rName = findViewById(R.id.rName);
            TextView eName = findViewById(R.id.eName);
            TextView eTime = findViewById(R.id.eTime);
            ConstraintLayout eBackground = findViewById(R.id.main_background);

            JSONObject data;
            try {
                BluetoothAdapter mBA = BluetoothAdapter.getDefaultAdapter();
                data = new JSONObject(new String(messageEvent.getData()));
                user.setText(data.getString("fullname"));
                mBA.setName("NTMY"+data.getString("userID"));
                rName.setText(data.getString("rName"));
                eName.setText(data.getString("eName"));
                eTime.setText(data.getString("eTime"));
                eBackground.setBackgroundColor(new Color(data.getJSONObject("color")).getIntColor());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            sync = true;

        } else if (messageEvent.getPath().equals(HANDSHAKE_VERIFIED)) {
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

