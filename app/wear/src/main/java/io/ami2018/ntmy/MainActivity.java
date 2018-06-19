
package io.ami2018.ntmy;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.wearable.activity.WearableActivity;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class MainActivity extends WearableActivity implements
        SensorEventListener,
        MessageClient.OnMessageReceivedListener,
        DataClient.OnDataChangedListener{

    // Sensors
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Vibrator mVibrator;
    private BluetoothAdapter mBA;

    // Messages objects
    private MessageSender mMessageSender;
    private MessageClient mWearableClient;
    private DataClient mDataClient;

    // MessageClient paths
    private static final String REQUEST_USER_DATA_PATH = "/request_user_data";
    private static final String RESPONSE_USER_DATA_PATH = "/response_user_data";
    private static final String HANDSHAKE_HAPPENED = "/handshake_happened";
    private static final String HANDSHAKE_VERIFIED = "/handshake_verified";

    // DataClient paths
    private static final String USER_FOUND_IMAGE = "/image/user/found";
    private static final String USER_PIC_IMAGE = "/image/profile/picture";

    // Useful identities
    private String phoneID;
    private String lastFoundUser;
    private String deviceName;

    // Handshake parameters
    private static final float ACCELERATION_THRESHOLD = SensorManager.GRAVITY_EARTH + 5;
    private static final int IDLE_COUNTER_THRESHOLD = 100 ;
    private static final int HANDSHAKE_COUNTER_THRESHOLD = 2;
    private static final float EPSILON_L = 2;
    private static final float EPSILON_M = 3;
    private static final long HANDSHAKE_DELAY = 1500;

    // Handshake counters
    private int handshakeCounter = 0;
    private int idleCounter = 0;

    // Last handshake time
    private long lastHandshakeTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ask the user to make device discoverable
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
        startActivity(discoverableIntent);

        // Get accelerometer
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Get Bluetooth Adapter
        mBA = BluetoothAdapter.getDefaultAdapter();

        // Get Vibrator service
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // Register a listener to Sensors events
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);

        // Get MessageClient & DataClient
        mWearableClient = Wearable.getMessageClient(this);
        mDataClient = Wearable.getDataClient(this);

        // Set the listener to MessageClient & DataClient events
        mWearableClient.addListener(this);
        mDataClient.addListener(this);

        //Create a new MessageSender object that will sends the data_request message
        mMessageSender = new MessageSender(getApplicationContext(),REQUEST_USER_DATA_PATH,"");
        mMessageSender.start();

        setAmbientEnabled();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // We need accelerometer events only
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER )
            return;

        // The handshake is detected as an acceleration in the Y axis (excluding gravity)

        // Set the three detected accelerations
        float accX = Math.abs(event.values[0]);
        float accY = Math.abs(event.values[1]);
        float accZ = Math.abs(event.values[2]);

        // If there is a signifier acceleration in the Y axis only
        if (accY > ACCELERATION_THRESHOLD && accX < EPSILON_M && accZ < EPSILON_L){
                // Start sample the handshake
                handshakeCounter++;
                // Avoid the idle reset
                idleCounter = 0;

                // If there are enough samples
                if (handshakeCounter > HANDSHAKE_COUNTER_THRESHOLD &&
                        // And if it's been enough time since last handshake
                        System.currentTimeMillis() - lastHandshakeTime > HANDSHAKE_DELAY) {

                    // Handshake Happened!
                    handshakeCounter=0;
                    mVibrator.vibrate(200);

                    // Update lastHandshakeTime
                    lastHandshakeTime = System.currentTimeMillis();

                    // Send an <HANDSHAKE_HAPPENED> message to the phone
                    Wearable.getMessageClient(getApplicationContext()).sendMessage(phoneID, HANDSHAKE_HAPPENED, null);
                }
        }else{
                // Periodically reset handshake counter in order to remove old samples:

                // Sampling the idle events
                idleCounter++;

                // If there are enough samples
                if (idleCounter > IDLE_COUNTER_THRESHOLD){
                    // reset all counters
                    idleCounter=0;
                    handshakeCounter = 0;
                }
        }


    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {

        // If the watch receives a response from the phone for the data request
        if (messageEvent.getPath().equals(RESPONSE_USER_DATA_PATH)) {
            // Store the phone's node ID for future message
            phoneID = messageEvent.getSourceNodeId();
            // Set the contents to be changed
            TextView user = findViewById(R.id.uName);
            TextView rName = findViewById(R.id.rName);
            TextView eName = findViewById(R.id.eName);
            TextView eTime = findViewById(R.id.eTime);
            ConstraintLayout eBackground = findViewById(R.id.main_background);

            JSONObject data;
            try {
                // Store the data received by the phone
                data = new JSONObject(new String(messageEvent.getData()));

                // Store previously device's name
                deviceName = mBA.getName();

                // Change the device's name according to userID
                mBA.setName("NTMY"+data.getString("userID"));

                // Set fields in layout
                user.setText(data.getString("fullname"));
                rName.setText(data.getString("rName"));
                eName.setText(data.getString("eName"));
                eTime.setText(data.getString("eTime"));

                // Create a new ColorUtil object
                ColorUtil color = new ColorUtil(data.getJSONObject("color"));

                // Change the background color according to event color
                eBackground.setBackgroundColor(color.getIntColor());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getPath().equals(HANDSHAKE_VERIFIED)) {
            // Once the phone verified the handshake detected by the watch
            // Store hie/her fullname and give a feedback to the user
            lastFoundUser = new String (messageEvent.getData());
            mVibrator.vibrate(200);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // reset device's name
        mBA.setName(deviceName);

        // unregister all the listeners
        mSensorManager.unregisterListener(this);
        mWearableClient.removeListener(this);
        mDataClient.removeListener(this);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            // When a data_changed event happens
            if (event.getType() == DataEvent.TYPE_CHANGED){
                // Get its path
                String path = event.getDataItem().getUri().getPath();

                // User pic sent
                if (path.equals(USER_PIC_IMAGE)){
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    Asset profileAsset = dataMapItem.getDataMap().getAsset("profileImage");

                    new LoadBitmapAsyncTask(USER_PIC_IMAGE).execute(profileAsset);
                }else
                    // Found user pic
                    if(path.equals(USER_FOUND_IMAGE)){
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    Asset profileAsset = dataMapItem.getDataMap().getAsset("userFoundPicture");

                    new LoadBitmapAsyncTask(USER_FOUND_IMAGE).execute(profileAsset);
                }
            }
        }
    }

    private class LoadBitmapAsyncTask extends AsyncTask<Asset, Void, Bitmap> {
        // Class used to receive assets in an AsyncTask
        private String path;

        // Constructor
        public LoadBitmapAsyncTask(String path) {
            this.path = path;
        }

        @Override
        protected Bitmap doInBackground(Asset... params) {

            if (params.length > 0) {

                Asset asset = params[0];

                Task<DataClient.GetFdForAssetResponse> getFdForAssetResponseTask =
                        Wearable.getDataClient(getApplicationContext()).getFdForAsset(asset);

                try {
                    DataClient.GetFdForAssetResponse getFdForAssetResponse =
                            Tasks.await(getFdForAssetResponseTask);

                    InputStream assetInputStream = getFdForAssetResponse.getInputStream();

                    if (assetInputStream != null) {
                        return BitmapFactory.decodeStream(assetInputStream);

                    } else {
                        Log.w("LOADASSET", "Requested an unknown Asset.");
                        return null;
                    }

                } catch (ExecutionException exception) {
                    Log.e("LOADASSET", "Failed retrieving asset, Task failed: " + exception);
                    return null;

                } catch (InterruptedException exception) {
                    Log.e("LOADASSET", "Failed retrieving asset, interrupt occurred: " + exception);
                    return null;
                }

            } else {
                Log.e("LOADASSET", "Asset must be non-null");
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // When a bitmap is received
            if (bitmap != null) {
                // User profile picture
                if (path.equals(USER_PIC_IMAGE)) {
                    // Set the layout
                    ImageView image = findViewById(R.id.user_picture);
                    image.setImageBitmap(BitmapUtil.GetBitmapClippedCircle(bitmap));

                } else
                    // found user
                    if (path.equals(USER_FOUND_IMAGE)) {
                    startUserFoundActivity(bitmap);
                }
            }
        }
    }

    public void startUserFoundActivity(Bitmap bitmap){
        // Start the user found activity

        Intent intent = new Intent(MainActivity.this, UserFoundActivity.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,out);

        // passing to it pic and fullname
        intent.putExtra("photo", out.toByteArray());
        intent.putExtra("username", lastFoundUser);
        startActivity(intent);
    }

}

