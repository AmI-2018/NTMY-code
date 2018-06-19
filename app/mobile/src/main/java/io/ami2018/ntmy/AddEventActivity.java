package io.ami2018.ntmy;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import io.ami2018.ntmy.network.RequestHelper;

public class AddEventActivity extends AppCompatActivity {

    // TODO hide and progress on post

    private static final String TAG = AddEventActivity.class.getSimpleName();
    private static AtomicInteger progressCounter;

    private TextInputEditText mName;
    private TextInputEditText mDescription;
    private TextInputEditText mStart;
    private TextInputEditText mEnd;
    private LinearLayout mCategories;
    private LinearLayout mFacilities;
    private View mProgress;

    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // Init
        initObjects();
        initViews();
        initListeners();

        // Back navigation
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        // Load data
        loadCategories();
        loadFacilities();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * Void method that initializes all the objects.
     */
    private void initObjects() {
        calendar = Calendar.getInstance();
        progressCounter = new AtomicInteger(0);
        Log.d(TAG, "Objects initialized.");
    }

    /**
     * Void method that initializes all the views.
     */
    private void initViews() {
        mName = findViewById(R.id.add_event_tiet_name);
        mDescription = findViewById(R.id.add_event_tiet_description);
        mStart = findViewById(R.id.add_event_tiet_date_start);
        mEnd = findViewById(R.id.add_event_tiet_date_end);
        mCategories = findViewById(R.id.add_event_ll_categories);
        mFacilities = findViewById(R.id.add_event_ll_facilities);
        mProgress = findViewById(R.id.progress_overlay_white);
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

        // Click Listener that handles the requests in order to add the event to the database
        ((Button) findViewById(R.id.add_event_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                try {
                    // Injection of all the categories to the view
                    for (int i = 0; i < response.length(); i++) {
                        CheckBox cb = new CheckBox(AddEventActivity.this);
                        cb.setText(response.getJSONObject(i).getString("name"));
                        cb.setId(1000 + response.getJSONObject(i).getInt("categoryID"));
                        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        llp.setMargins(0, (int) (8 * getResources().getDisplayMetrics().density), 0, 0);
                        cb.setLayoutParams(llp);
                        mCategories.addView(cb);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
                try {
                    // Injection of all the facilities to the view
                    for (int i = 0; i < response.length(); i++) {
                        CheckBox cb = new CheckBox(AddEventActivity.this);
                        cb.setText(response.getJSONObject(i).getString("name"));
                        cb.setId(2000 + response.getJSONObject(i).getInt("facilityID"));
                        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        llp.setMargins(0, (int) (8 * getResources().getDisplayMetrics().density), 0, 0);
                        cb.setLayoutParams(llp);
                        mFacilities.addView(cb);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
                    jsonObject.put("categoryID", (int) (mCategories.getChildAt(i).getId()) - 1000);
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
                    jsonObject.put("facilityID", (int) (mFacilities.getChildAt(i).getId()) - 2000);
                    jsonObject.put("options", "");
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
     * Void method that updates the start TextView.
     */
    private void updateLabelStart() {
        String myFormat = "MM/dd/yyyy HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALY);
        mStart.setText(sdf.format(calendar.getTime()));
    }

    /**
     * Void method that updates the end TextView.
     */
    private void updateLabelEnd() {
        String myFormat = "MM/dd/yyyy HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALY);
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
