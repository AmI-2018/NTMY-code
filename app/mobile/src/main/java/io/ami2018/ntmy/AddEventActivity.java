package io.ami2018.ntmy;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import io.ami2018.ntmy.network.RequestHelper;

public class AddEventActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = AddEventActivity.class.getSimpleName();
    private static AtomicInteger progressCounter;

    private EditText mName;
    private EditText mDescription;
    private TextView mStart;
    private TextView mEnd;
    private LinearLayout mCategories;
    private LinearLayout mFacilities;
    private View mProgress;
    private Spinner mTvSpinner;
    private Spinner mPlaylistSpinner;

    private Calendar calendar;
    private ArrayAdapter<String> tvAdapter;
    private ArrayAdapter<String> playlistAdapter;
    private String[] channels;
    private String[] playlists;
    private HashMap<String, Integer> tvMap;
    private HashMap<String, Integer> playlistMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // Init
        initObjects();
        initViews();
        initListeners();

        // Back navigation
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        }

        // Load data
        loadCategories();
        loadFacilities();
        loadChannels();
        loadPlaylists();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                createEvent();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton.getText().equals("TV")) {
            if (b) mTvSpinner.setVisibility(View.VISIBLE);
            else mTvSpinner.setVisibility(View.GONE);
        } else {
            if (compoundButton.getText().equals("Audio")) {
                if (b) mPlaylistSpinner.setVisibility(View.VISIBLE);
                else mPlaylistSpinner.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Void method that initializes all the objects.
     */
    private void initObjects() {
        calendar = Calendar.getInstance();
        tvMap = new HashMap<>();
        playlistMap = new HashMap<>();
        progressCounter = new AtomicInteger(0);
        Log.d(TAG, "Objects initialized.");
    }

    /**
     * Void method that initializes all the views.
     */
    private void initViews() {
        mName = findViewById(R.id.add_event_et_name);
        mDescription = findViewById(R.id.add_event_et_description);
        mStart = findViewById(R.id.add_event_tv_date_start);
        mEnd = findViewById(R.id.add_event_tv_date_end);
        mCategories = findViewById(R.id.add_event_ll_categories);
        mFacilities = findViewById(R.id.add_event_ll_facilities);
        mProgress = findViewById(R.id.progress_overlay_white);
        mTvSpinner = findViewById(R.id.add_event_sp_tv);
        mPlaylistSpinner = findViewById(R.id.add_event_sp_playlist);
        updateLabelEnd();
        updateLabelStart();
        Log.d(TAG, "Views initialized.");
    }

    /**
     * Void method that initializes all the listeners.
     */
    private void initListeners() {
        // Click Listener that handles the Date and Time Picker Dialogs for the start TextView
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                new DatePickerDialog(AddEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(Calendar.YEAR, i);
                        calendar.set(Calendar.MONTH, i1);
                        calendar.set(Calendar.DAY_OF_MONTH, i2);
                        new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                calendar.set(Calendar.HOUR_OF_DAY, i);
                                calendar.set(Calendar.MINUTE, i1);
                                updateLabelStart();
                                calendar.set(Calendar.HOUR_OF_DAY, i+1);
                                updateLabelEnd();
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(AddEventActivity.this)).show();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // Click Listener that handles the Date and Time Picker Dialogs for the end TextView
        mEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                new DatePickerDialog(AddEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(Calendar.YEAR, i);
                        calendar.set(Calendar.MONTH, i1);
                        calendar.set(Calendar.DAY_OF_MONTH, i2);
                        new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                calendar.set(Calendar.HOUR_OF_DAY, i);
                                calendar.set(Calendar.MINUTE, i1);
                                updateLabelEnd();
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(AddEventActivity.this)).show();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        Log.d(TAG, "Listeners initialized.");
    }

    /**
     * Void method that loads all the existing categories in the database.
     * These categories are then injected in the view as CheckBoxes.
     */
    private void loadCategories() {
        showProgress();
        // GET request for the categories
        RequestHelper.getJsonArray(AddEventActivity.this, "categories", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // Injection of all the categories to the view
                for (int i = 0; i < response.length(); i++) {
                    String name = "";
                    int id = 0;
                    try {
                        name = response.getJSONObject(i).getString("name");
                        id = 1000 + response.getJSONObject(i).getInt("categoryID");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    CheckBox cb = new CheckBox(AddEventActivity.this);
                    cb.setText(name);
                    cb.setId(id);
                    LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (56 * getResources().getDisplayMetrics().density));
                    llp.gravity= Gravity.CENTER_VERTICAL;
                    cb.setLayoutParams(llp);
                    mCategories.addView(cb);
                }
                hideProgress();
                Log.d(TAG, "All the categories were successfully loaded.");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                Log.d(TAG, "Error while loading categories.\n" + error.getMessage());
            }
        });
    }

    /**
     * Void method that loads all the existing facilities in the database.
     * These facilities are then injected in the view as CheckBoxes.
     */
    private void loadFacilities() {
        showProgress();
        // GET request for the facilities
        RequestHelper.getJsonArray(AddEventActivity.this, "facilities", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // Injection of all the facilities to the view
                for (int i = 0; i < response.length(); i++) {
                    String name = "";
                    int id = 0;
                    try {
                        name = response.getJSONObject(i).getString("name");
                        id = 2000 + response.getJSONObject(i).getInt("facilityID");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    CheckBox cb = new CheckBox(AddEventActivity.this);
                    cb.setText(name);
                    cb.setId(id);
                    LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (56 * getResources().getDisplayMetrics().density));
                    llp.gravity= Gravity.CENTER_VERTICAL;
                    cb.setLayoutParams(llp);
                    cb.setOnCheckedChangeListener(AddEventActivity.this);
                    mFacilities.addView(cb);
                }
                hideProgress();
                Log.d(TAG, "All the facilities were successfully loaded.");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                Log.d(TAG, "Error while loading facilities.\n" + error.getMessage());
            }
        });
    }

    /**
     * Void method that loads all the existing tv channels in the database.
     */
    private void loadChannels() {
        showProgress();
        // GET request for the channels
        RequestHelper.getJsonArray(AddEventActivity.this, "media/channels", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // Creation of the adapter that will manage the channels list
                channels = new String[response.length()];
                tvAdapter = new ArrayAdapter<>(AddEventActivity.this, R.layout.item_spinner, channels);
                tvAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                mTvSpinner.setAdapter(tvAdapter);
                for (int i = 0; i < response.length(); i++) {
                    String name = "";
                    int id = 0;
                    try {
                        name = response.getJSONObject(i).getString("name");
                        id = response.getJSONObject(i).getInt("channelID");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    channels[i] = name;
                    tvMap.put(name, id);
                }
                hideProgress();
                Log.d(TAG, "All the channels were successfully loaded.");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                Log.d(TAG, "Error while loading channels.\n" + error.getMessage());
            }
        });
    }

    /**
     * Void method that loads all the existing playlists in the database.
     */
    private void loadPlaylists() {
        showProgress();
        // GET request for the playlists
        RequestHelper.getJsonArray(AddEventActivity.this, "media/playlists", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // Creation of the adapter that will manage the playlists
                playlists = new String[response.length()];
                playlistAdapter = new ArrayAdapter<>(AddEventActivity.this, R.layout.item_spinner, playlists);
                playlistAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                mPlaylistSpinner.setAdapter(playlistAdapter);
                for (int i = 0; i < response.length(); i++) {
                    String name = "";
                    int id = 0;
                    try {
                        name = response.getJSONObject(i).getString("name");
                        id = response.getJSONObject(i).getInt("playlistID");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    playlists[i] = name;
                    playlistMap.put(name, id);
                }
                hideProgress();
                Log.d(TAG, "All the playlists were successfully loaded.");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                Log.d(TAG, "Error while loading playlists.\n" + error.getMessage());
            }
        });
    }

    /**
     * Void method that adds all the selected categories to the relationship with the event in the database.
     *
     * @param eventId, the event that should have all the selected categories.
     */
    private void addCategories(int eventId) {
        // For each category selected by the user...
        for (int i = 0; i < mCategories.getChildCount(); i++) {
            if (mCategories.getChildAt(i) instanceof CheckBox && ((CheckBox) mCategories.getChildAt(i)).isChecked()) {
                // ... a jsonObject that represents the relation between the event and the category is created...
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("categoryID", (mCategories.getChildAt(i).getId()) - 1000);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // ... and is POSTed to the API
                RequestHelper.postJson(AddEventActivity.this, "events/" + eventId + "/categories", jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Categories were successfully added to the new event.");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error while adding the categories to the new event.\n" + error.getMessage());
                    }
                });
            }
        }
    }

    /**
     * Void method that adds all the selected facilities to the relationship with the event in the database.
     *
     * @param eventId, the event that should have all the selected facilities.
     */
    private void addFacilities(int eventId) {
        // For each facility selected by the user...
        for (int i = 0; i < mFacilities.getChildCount(); i++) {
            if (mFacilities.getChildAt(i) instanceof CheckBox && ((CheckBox) mFacilities.getChildAt(i)).isChecked()) {
                // ... a jsonObject that represents the relation between the event and the facility is created...
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("facilityID", (mFacilities.getChildAt(i).getId()) - 2000);
                    if(((CheckBox) mFacilities.getChildAt(i)).getText().equals("TV")) {
                        jsonObject.put("options", tvMap.get(mTvSpinner.getSelectedItem().toString()));
                    } else {
                        if(((CheckBox) mFacilities.getChildAt(i)).getText().equals("Audio"))
                            jsonObject.put("options", playlistMap.get(mPlaylistSpinner.getSelectedItem().toString()));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // ... and is POSTed to the API
                RequestHelper.postJson(AddEventActivity.this, "events/" + eventId + "/facilities", jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Facilities were successfully added to the new event.");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error while adding the facilities to the new event.\n" + error.getMessage());
                    }
                });
            }
        }
    }

    /**
     * Void method that add the event to the database, with all his categories and facilities
     */
    private void createEvent() {
        // Creation of the jsonObject that contains all the new event's data
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", mName.getText());
            jsonObject.put("description", mDescription.getText());
            jsonObject.put("start", mStart.getText());
            jsonObject.put("end", mEnd.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // The creation of the event corresponds to a POST request to the API
        RequestHelper.postJson(AddEventActivity.this, "events", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // As response we get back the jsonObject relative to the event just created
                // and the eventId is grabbed because it is necessary for future requests
                int eventId = 0;
                try {
                    eventId = response.getInt("eventID");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Categories and facilities are added to the event
                addCategories(eventId);
                addFacilities(eventId);
                // End of the activity
                Log.d(TAG, "The new event was successfully added to the database.");
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error while adding the new event.\n" + error.getMessage());
            }
        });
    }

    /**
     * Void method that updates the start TextView.
     */
    private void updateLabelStart() {
        String myFormat = "MM/dd/yyyy HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        mStart.setText(sdf.format(calendar.getTime()));
    }

    /**
     * Void method that updates the end TextView.
     */
    private void updateLabelEnd() {
        String myFormat = "MM/dd/yyyy HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        mEnd.setText(sdf.format(calendar.getTime()));
    }

    /**
     * Method used for hiding the keyboard.
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            View view = getCurrentFocus();
            if (view != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Method used for displaying the progress.
     */
    private void showProgress() {
        if (progressCounter.incrementAndGet() == 1) {
            mProgress.setVisibility(View.VISIBLE);
            Log.d(TAG, "Progress shown");
        }
    }

    /**
     * Method used for hiding the progress.
     */
    private void hideProgress() {
        if (progressCounter.decrementAndGet() == 0) {
            mProgress.setVisibility(View.GONE);
            Log.d(TAG, "Progress hidden");
        }
    }
}
