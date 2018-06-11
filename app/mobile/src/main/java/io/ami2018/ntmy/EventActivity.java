package io.ami2018.ntmy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;

import io.ami2018.ntmy.model.Facility;
import io.ami2018.ntmy.model.User;
import io.ami2018.ntmy.network.RequestHelper;
import io.ami2018.ntmy.recyclerviews.StartSnapHelper;
import io.ami2018.ntmy.recyclerviews.UserAdapter;
import io.ami2018.ntmy.recyclerviews.UserClickListener;

public class EventActivity extends AppCompatActivity {

    private static final String TAG = EventActivity.class.getSimpleName();

    private Integer eventId;
    private String name;
    private String description;
    private String start;
    private String end;
    private String room;
    private Integer creatorId;
    private String creator;

    private View mProgress;
    private LinearLayout mFacilitiesContainer;
    private ImageView mCreator;
    private UserAdapter mUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        initObjects();
        initViews();
        initToolbar();

        loadCreatorImage();
        loadFacilities();
        loadUsersMet();
    }

    private void initObjects() {
        eventId = getIntent().getIntExtra("EVENT ID", 0);
        name = getIntent().getStringExtra("NAME");
        description = getIntent().getStringExtra("DESCRIPTION");
        start = getIntent().getStringExtra("START");
        end = getIntent().getStringExtra("END");
        room = getIntent().getStringExtra("ROOM");
        creatorId = getIntent().getIntExtra("CREATOR ID", 0);
        creator = getIntent().getStringExtra("CREATOR NAME");
        mUserAdapter = new UserAdapter(new UserClickListener() {
            @Override
            public void onClick(View view, User user) {
                Intent intent = new Intent(EventActivity.this, UserActivity.class);
                intent.putExtra("NAME", user.getName() + " " + user.getSurname());
                intent.putExtra("USER ID", user.getUserId());
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        ((CollapsingToolbarLayout) findViewById(R.id.event_ctl)).setTitle(name);
        ((TextView) findViewById(R.id.event_tv_date)).setText(start.split(" ")[0]);
        ((TextView) findViewById(R.id.event_tv_time)).setText(start.split(" ")[1]);
        ((TextView) findViewById(R.id.event_tv_room)).setText(room);
        ((TextView) findViewById(R.id.event_tv_description)).setText(description);
        ((TextView) findViewById(R.id.event_tv_creator)).setText(creator);
        mProgress = findViewById(R.id.progress_overlay);
        mFacilitiesContainer = findViewById(R.id.event_ll_facilities);
        mCreator = findViewById(R.id.event_civ_creator);

        RecyclerView mUserRv = findViewById(R.id.event_rv_users);

        LinearLayoutManager userLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        SnapHelper userSnapHelper = new StartSnapHelper();

        mUserRv.setHasFixedSize(true);
        mUserRv.setLayoutManager(userLinearLayoutManager);
        mUserRv.setAdapter(mUserAdapter);
        userSnapHelper.attachToRecyclerView(mUserRv);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.event_tb);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void showProgress() {
        Log.d(TAG, "Showing Progress");
        mProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        Log.d(TAG, "Hiding Progress");
        mProgress.setVisibility(View.GONE);
    }

    private void loadCreatorImage() {
        RequestHelper.getImage(getApplicationContext(), "users/" + creatorId + "/photo", new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                mCreator.setImageBitmap(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private void loadFacilities() {
        RequestHelper.getJsonArray(getApplicationContext(), "events/" + eventId + "/facilities", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() > 0) {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            //TODO Drawable if a set of facilities is predefined
                            //TODO Description of Options
                            Facility facility = new Facility(response.getJSONObject(i).getJSONObject("facility"));
                            TextView textView = new TextView(getApplicationContext());
                            textView.setText(facility.getName());
                            textView.setTextAppearance(R.style.TextAppearance_AppCompat_Body1);
                            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            llp.setMargins(0, (int) (8 * getResources().getDisplayMetrics().density), 0, 0);
                            textView.setLayoutParams(llp);
                            mFacilitiesContainer.addView(textView);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    TextView textView = new TextView(getApplicationContext());
                    textView.setText("No Facilities");
                    textView.setTextAppearance(R.style.TextAppearance_AppCompat_Body1);
                    LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    llp.setMargins(0, (int) (8 * getResources().getDisplayMetrics().density), 0, 0);
                    textView.setLayoutParams(llp);
                    mFacilitiesContainer.addView(textView);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private void loadUsersMet() {
        RequestHelper.getJsonArray(getApplicationContext(), "users/" + MainActivity.mUser.getUserId() + "/connections/event/" + eventId, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() > 0) {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            final User user = new User(response.getJSONObject(i).getJSONObject("user2"));
                            RequestHelper.getImage(getApplicationContext(), "users/" + user.getUserId() + "/photo", new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap response) {
                                    user.setPhoto(response);
                                    mUserAdapter.addElement(user);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }
}
