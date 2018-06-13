package io.ami2018.ntmy;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
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

import io.ami2018.ntmy.network.RequestHelper;

public class AddEventActivity extends AppCompatActivity {

    private TextInputEditText mName;
    private TextInputEditText mDescription;
    private TextInputEditText mStart;
    private TextInputEditText mEnd;

    private LinearLayout mCategories;
    private LinearLayout mFacilities;

    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        setTitle("Add event");

        initObjects();
        initViews();
        initListeners();

        loadCategories();
        loadFacilities();
    }

    private void initObjects() {
        calendar = Calendar.getInstance();
    }

    private void initViews() {
        mName = findViewById(R.id.add_event_tiet_name);
        mDescription = findViewById(R.id.add_event_tiet_description);
        mStart = findViewById(R.id.add_event_tiet_date_start);
        mEnd = findViewById(R.id.add_event_tiet_date_end);
        mCategories = findViewById(R.id.add_event_ll_categories);
        mFacilities = findViewById(R.id.add_event_ll_facilities);
    }

    private void initListeners() {
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        mEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        ((Button) findViewById(R.id.add_event_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", mName.getText());
                    jsonObject.put("description", mDescription.getText());
                    jsonObject.put("start", mStart.getText());
                    jsonObject.put("end", mEnd.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestHelper.postJson(AddEventActivity.this, "events", jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int eventId = 0;
                        try {
                            eventId = response.getInt("eventID");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for (int i = 0; i < mCategories.getChildCount(); i++) {
                            if (mCategories.getChildAt(i) instanceof CheckBox && ((CheckBox) mCategories.getChildAt(i)).isChecked()) {
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("categoryID", (int) (mCategories.getChildAt(i).getId()) - 1000);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                RequestHelper.postJson(AddEventActivity.this, "events/" + eventId + "/categories", jsonObject, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                });
                            }
                        }
                        for (int i = 0; i < mFacilities.getChildCount(); i++) {
                            if (mFacilities.getChildAt(i) instanceof CheckBox && ((CheckBox) mFacilities.getChildAt(i)).isChecked()) {
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("facilityID", (int) (mFacilities.getChildAt(i).getId()) - 2000);
                                    jsonObject.put("options", null);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                RequestHelper.postJson(AddEventActivity.this, "events/" + eventId + "/facilities", jsonObject, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                });
                            }
                        }
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
            }
        });
    }

    private void loadCategories() {
        RequestHelper.getJsonArray(AddEventActivity.this, "categories", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private void loadFacilities() {
        RequestHelper.getJsonArray(AddEventActivity.this, "facilities", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private void updateLabelStart() {
        String myFormat = "MM/dd/yyyy HH:mm"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALY);

        mStart.setText(sdf.format(calendar.getTime()));
    }

    private void updateLabelEnd() {
        String myFormat = "MM/dd/yyyy HH:mm"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALY);

        mEnd.setText(sdf.format(calendar.getTime()));
    }
}
