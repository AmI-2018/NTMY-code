
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

    // Accelerometer parameters

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Vibrator mvibrator;

    // Handshake flags & counters
    private long lastHandshakeTime;
    private static final long HANDSHAKE_DELAY = 1500;

    private int n = 0;

    // MessageClient paths
    private static final String REQUEST_USER_DATA_PATH = "/request_user_data";
    private static final String RESPONSE_USER_DATA_PATH = "/response_user_data";
    private static final String HANDSHAKE_HAPPENED = "/handshake_happened";
    private static final String HANDSHAKE_VERIFIED = "/handshake_verified";
    private static final String USER_FOUND_IMAGE = "/image/user/found";
    private static final String USER_PIC_IMAGE = "/image/profile/picture";
    private String phoneID;
    private MessageSender mMessageSender;
    private MessageClient mWearableClient;
    private DataClient mDataClient;
    private static final float ACCELERATION_THRESHOLD = SensorManager.GRAVITY_EARTH + 5;
    private int handshakeCounter = 0;
    private int idleCounter = 0;
    private static final int IDLE_COUNTER_THRESHOLD = 100 ;
    private static final int HANDSHAKE_COUNTER_THRESHOLD = 2;
    private static final float EPSILON_L = 2;
    private static final float EPSILON_M = 3;
    private String lastFoundUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Make the device discoverable
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
        startActivity(discoverableIntent);

        // Set the sensors
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mvibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);

        // Set the messageclient and send a message to the phone to obtain the data
        mWearableClient = Wearable.getMessageClient(this);
        mWearableClient.addListener(this);
        mDataClient = Wearable.getDataClient(this);
        mDataClient.addListener(this);
        mMessageSender = new MessageSender(getApplicationContext(),REQUEST_USER_DATA_PATH,"");
        mMessageSender.start();

        setAmbientEnabled();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // We need accelerometer event only
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER )
            return;

        // The handshake is detected as an acceleration in the Y axis (excluding gravity)
        float accX = Math.abs(event.values[0]);
        float accY = Math.abs(event.values[1]);
        float accZ = Math.abs(event.values[2]);

        // If there is a signifier acceleration in the Y axis only
        if (accY > ACCELERATION_THRESHOLD && accX < EPSILON_M && accZ < EPSILON_L){
                handshakeCounter++;
                idleCounter = 0;
                // And it's been enough time since last handshake
                if (handshakeCounter > HANDSHAKE_COUNTER_THRESHOLD && System.currentTimeMillis() - lastHandshakeTime > HANDSHAKE_DELAY) {

                    // Handshake
                    lastHandshakeTime = System.currentTimeMillis();
                    mvibrator.vibrate(200);
                    Wearable.getMessageClient(getApplicationContext()).sendMessage(phoneID, HANDSHAKE_HAPPENED, null);
                }
        }else{
                idleCounter++;
                if (idleCounter > IDLE_COUNTER_THRESHOLD){
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
                ColorUtil color = new ColorUtil(data.getJSONObject("color"));
                //eBackground.setBackgroundColor(Color.rgb(color.getRed(),color.getGreen(),color.getBlue()));
                eBackground.setBackgroundColor(color.getIntColor());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getPath().equals(HANDSHAKE_VERIFIED)) {
            // Once the phone verified the handshake detected by the watch
            // shows a message with detected user's fullname obtained from the server
            lastFoundUser = new String (messageEvent.getData());
            mvibrator.vibrate(200);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
        mWearableClient.removeListener(this);
        mDataClient.removeListener(this);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED){
                String path = event.getDataItem().getUri().getPath();
                if (path.equals(USER_PIC_IMAGE)){
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    Asset profileAsset = dataMapItem.getDataMap().getAsset("profileImage");

                    new LoadBitmapAsyncTask(USER_PIC_IMAGE).execute(profileAsset);
                }else if(path.equals(USER_FOUND_IMAGE)){
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    Asset profileAsset = dataMapItem.getDataMap().getAsset("userFoundPicture");

                    new LoadBitmapAsyncTask(USER_FOUND_IMAGE).execute(profileAsset);
                }
            }
        }
    }

    private class LoadBitmapAsyncTask extends AsyncTask<Asset, Void, Bitmap> {

        private String path;

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
                    // Block on a task and get the result synchronously. This is generally done
                    // when executing a task inside a separately managed background thread. Doing
                    // this on the main (UI) thread can cause your application to become
                    // unresponsive.
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

            if (bitmap != null) {
                if (path.equals(USER_PIC_IMAGE)) {
                    ImageView image = findViewById(R.id.user_picture);
                    image.setImageBitmap(BitmapUtil.GetBitmapClippedCircle(bitmap));

                } else if (path.equals(USER_FOUND_IMAGE)) {
                    startUserFoundActivity(bitmap);
                }
            }
        }
    }

    public void startUserFoundActivity(Bitmap bitmap){
        Intent intent = new Intent(MainActivity.this, UserFoundActivity.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
        intent.putExtra("photo", out.toByteArray());
        intent.putExtra("username", lastFoundUser);
        startActivity(intent);
    }

}

