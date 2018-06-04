
package io.ami2018.ntmy;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wear.ambient.AmbientMode;
import android.support.wear.widget.drawer.WearableActionDrawerView;
import android.support.wear.widget.drawer.WearableNavigationDrawerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends Activity implements
        AmbientMode.AmbientCallbackProvider,
        MenuItem.OnMenuItemClickListener,
        WearableNavigationDrawerView.OnItemSelectedListener,
        SensorEventListener {

    private MainActivity mainActivity = this;
    private static final String TAG = "MainActivity";
    private int i = 0 ;
    private WearableNavigationDrawerView mWearableNavigationDrawer;
    private WearableActionDrawerView mWearableActionDrawer;
    //  private ArrayList<Person> mMetPeople;
    private ArrayList<Mode> mModes;
    private int mSelectedMode;
    //  private int mSelectedPerson;

    private float accX;
    private float accY;
    private float accZ;
    private final double epsilon = 2.0;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Vibrator mvibrator;

    private ModeFragment mModeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        setContentView(R.layout.activity_main);

        // Enables Ambient mode.
        AmbientMode.attachAmbientSupport(this);

        mModes = initializeModes();
        mSelectedMode = 0;

        // Initialize content to first mode.
        mModeFragment = new ModeFragment();
        Bundle args = new Bundle();

        int imageId = getResources().getIdentifier(mModes.get(mSelectedMode).getImage(),
                "drawable", getPackageName());


        args.putInt(ModeFragment.ARG_MODE_IMAGE_ID, imageId);
        mModeFragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, mModeFragment).commit();


        // Top Navigation Drawer
        mWearableNavigationDrawer =
                (WearableNavigationDrawerView) findViewById(R.id.top_navigation_drawer);
        mWearableNavigationDrawer.setAdapter(new NavigationAdapter(this));
        // Peeks navigation drawer on the top.
        mWearableNavigationDrawer.getController().peekDrawer();
        mWearableNavigationDrawer.addOnItemSelectedListener(this);

        // Bottom Action Drawer
        mWearableActionDrawer =
                (WearableActionDrawerView) findViewById(R.id.bottom_action_drawer);
        // Peeks action drawer on the bottom.
        mWearableActionDrawer.getController().peekDrawer();
        mWearableActionDrawer.setOnMenuItemClickListener(this);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mvibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    }

    private ArrayList<Mode> initializeModes() {
        ArrayList<Mode> modes = new ArrayList<Mode>();
        String[] modesArrayNames = getResources().getStringArray(R.array.modes_array_names);

        for (int i = 0; i < modesArrayNames.length; i++) {
            String mode = modesArrayNames[i];
            int modeResourceId =
                    getResources().getIdentifier(mode, "array", getPackageName());
            String[] modeInformation = getResources().getStringArray(modeResourceId);

            String[] parameters = {
                    modeInformation[0],   // Name
                    modeInformation[1],   // Navigation icon
                    modeInformation[2],   // Image icon
                    modeInformation[3],   // Info
                    modeInformation[4]};  // Counter

            switch (i){
                case 1:
                    modes.add(new Routing(parameters));
                    break;
                case 2:
                    modes.add(new Meeting(parameters));
                    break;
                default:
                    modes.add(new Mode(parameters));
            }
        }

        return modes;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        Log.d(TAG, "onMenuItemClick(): " + menuItem);

        final int itemId = menuItem.getItemId();

        String toastMessage = "";

        switch (itemId) {
            case R.id.menu_mode_info:
                toastMessage = mModes.get(mSelectedMode).getInfo();
                break;
            case R.id.menu_counter:
                toastMessage = String.valueOf(mModes.get(mSelectedMode).getCount());
                break;
        }

        mWearableActionDrawer.getController().closeDrawer();

        if (toastMessage.length() > 0) {
            Toast toast = Toast.makeText(
                    getApplicationContext(),
                    toastMessage,
                    Toast.LENGTH_SHORT);
            toast.show();
            return true;
        } else {
            return false;
        }
    }

    // Updates content when user changes between items in the navigation drawer.
    @Override
    public void onItemSelected(int position) {
        Log.d(TAG, "WearableNavigationDrawerView triggered onItemSelected(): " + position);

        mModes.get(mSelectedMode).stop();
        mSelectedMode = position;
        mModes.get(mSelectedMode).start();

        String selectedModeImage = mModes.get(mSelectedMode).getImage();
        int drawableId =
                getResources().getIdentifier(selectedModeImage, "drawable", getPackageName());
        mModeFragment.updateMode(drawableId);
    }

    private final class NavigationAdapter
            extends WearableNavigationDrawerView.WearableNavigationDrawerAdapter {

        private final Context mContext;

        public NavigationAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return mModes.size();
        }

        @Override
        public String getItemText(int pos) {
            return mModes.get(pos).getName();
        }

        @Override
        public Drawable getItemDrawable(int pos) {
            String navigationIcon = mModes.get(pos).getIcon();

            int drawableNavigationIconId =
                    getResources().getIdentifier(navigationIcon, "drawable", getPackageName());

            return mContext.getDrawable(drawableNavigationIconId);
        }
    }

    /**
     * Fragment that appears in the "content_frame", just shows the currently selected Mode.
     */
    public static class ModeFragment extends Fragment {
        public static final String ARG_MODE_IMAGE_ID = "node_mode_id";

        private ImageView mImageView;
        private ColorFilter mImageViewColorFilter;

        public ModeFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(
                LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_mode, container, false);

            mImageView = ((ImageView) rootView.findViewById(R.id.image));

            int imageIdToLoad = getArguments().getInt(ARG_MODE_IMAGE_ID);
            mImageView.setImageResource(imageIdToLoad);

            mImageViewColorFilter = mImageView.getColorFilter();

            return rootView;
        }

        public void updateMode(int imageId) {
            mImageView.setImageResource(imageId);
        }

        public void onEnterAmbientInFragment(Bundle ambientDetails) {
            Log.d(TAG, "ModeFragment.onEnterAmbient() " + ambientDetails);

            // Convert image to grayscale for ambient mode.
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);

            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            mImageView.setColorFilter(filter);
        }

        /** Restores the UI to active (non-ambient) mode. */
        public void onExitAmbientInFragment() {
            Log.d(TAG, "ModeFragment.onExitAmbient()");

            mImageView.setColorFilter(mImageViewColorFilter);
        }
    }

    @Override
    public AmbientMode.AmbientCallback getAmbientCallback() {
        return new MyAmbientCallback();
    }

    private class MyAmbientCallback extends AmbientMode.AmbientCallback {
        /** Prepares the UI for ambient mode. */
        @Override
        public void onEnterAmbient(Bundle ambientDetails) {
            super.onEnterAmbient(ambientDetails);
            Log.d(TAG, "onEnterAmbient() " + ambientDetails);

            mModeFragment.onEnterAmbientInFragment(ambientDetails);
            mWearableNavigationDrawer.getController().closeDrawer();
            mWearableActionDrawer.getController().closeDrawer();
        }

        /** Restores the UI to active (non-ambient) mode. */
        @Override
        public  void onExitAmbient() {
            super.onExitAmbient();
            Log.d(TAG, "onExitAmbient()");

            mModeFragment.onExitAmbientInFragment();
            mWearableActionDrawer.getController().peekDrawer();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER )
            return;

        accX = Math.abs(Math.abs(event.values[0]) - SensorManager.GRAVITY_EARTH);
        accY = Math.abs(event.values[1]);
        accZ = Math.abs(event.values[2]);

        if (accX >= 5 && accY < epsilon && accZ < epsilon){
            // Handshake
            mvibrator.vibrate(200);
            Toast toast = Toast.makeText(
                    getApplicationContext(),
                    "Handshake!" + i ,
                    Toast.LENGTH_SHORT);
            toast.show();
            i++;

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class Meeting extends Mode{

        public Meeting(String name, String icon, String image, String info, int count) {
            super(name, icon, image, info, count);
        }

        public Meeting(String[] parameters) {
            super(parameters);
        }

        @Override
        public void start(){
            mSensorManager.registerListener(mainActivity, mSensor, SensorManager.SENSOR_DELAY_GAME);
        }

        @Override
        public void stop(){
            mSensorManager.unregisterListener(mainActivity);
        }
    }

    public class Routing extends Mode{

        public Routing(String name, String icon, String image, String info, int count) {
            super(name, icon, image, info, count);
        }

        public Routing(String[] parameters) {
            super(parameters);
        }

        @Override
        public void start(){

        }

        @Override
        public void stop(){

        }
    }
}

