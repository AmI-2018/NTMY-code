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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

import io.ami2018.ntmy.model.Facility;
import io.ami2018.ntmy.model.User;
import io.ami2018.ntmy.network.RequestHelper;
import io.ami2018.ntmy.recyclerviews.StartSnapHelper;
import io.ami2018.ntmy.recyclerviews.UserAdapter;
import io.ami2018.ntmy.recyclerviews.UserClickListener;

public class EventActivity extends AppCompatActivity {

    private static final String TAG = EventActivity.class.getSimpleName();
    private static AtomicInteger progressCounter;
    private static boolean colorActivated = false;

    private Integer eventId;
    private String name;
    private String description;
    private String start;
    private String room;
    private Integer color;
    private Integer creatorId;
    private String creator;
    private UserClickListener mUserClickListener;
    private UserAdapter mUserAdapter;
    private boolean userIsParticipating;

    private View mProgress;
    private LinearLayout mFacilitiesContainer;
    private ImageView mCreator;
    private MenuItem mFavourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        // Init
        initListeners();
        initObjects();
        initViews();

        // Load data
        loadParticipation();
        loadCreatorImage();
        loadFacilities();
        loadUsersMet();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_menu, menu);
        mFavourite = menu.findItem(R.id.action_toggle_participate);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_toggle_participate:
                toggleParticipation();
                return true;
            case R.id.action_delete_event:
                if (creatorId != MainActivity.mUser.getUserId().intValue() && creatorId != 0)
                    Toast.makeText(this, "You can't delete this event.", Toast.LENGTH_LONG).show();
                else
                    deleteEvent();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Void method that initializes all the objects.
     */
    private void initObjects() {
        eventId = getIntent().getIntExtra("EVENT ID", 0);
        name = getIntent().getStringExtra("NAME");
        description = getIntent().getStringExtra("DESCRIPTION");
        start = getIntent().getStringExtra("START");
        room = getIntent().getStringExtra("ROOM");
        creatorId = getIntent().getIntExtra("CREATOR ID", 0);
        creator = getIntent().getStringExtra("CREATOR NAME");
        color = getIntent().getIntExtra("COLOR", 0);
        mUserAdapter = new UserAdapter(mUserClickListener);
        progressCounter = new AtomicInteger(0);
        Log.d(TAG, "Objects initialized.");
    }

    /**
     * Void method that initializes all the views.
     */
    private void initViews() {
        ((CollapsingToolbarLayout) findViewById(R.id.event_ctl)).setTitle(name);
        ((TextView) findViewById(R.id.event_tv_date)).setText(start.split(" ")[0]);
        ((TextView) findViewById(R.id.event_tv_time)).setText(start.split(" ")[1]);
        ((TextView) findViewById(R.id.event_tv_room)).setText(room);
        ((TextView) findViewById(R.id.event_tv_description)).setText(description);
        ((TextView) findViewById(R.id.event_tv_creator)).setText(creator);
        mProgress = findViewById(R.id.progress_overlay_white);
        mFacilitiesContainer = findViewById(R.id.event_ll_facilities);
        mCreator = findViewById(R.id.event_civ_creator);
        if (color != -1 && colorActivated)
            findViewById(R.id.event_abl).setBackgroundColor(color);

        setSupportActionBar((Toolbar) findViewById(R.id.event_tb));

        RecyclerView mUserRv = findViewById(R.id.event_rv_users);

        LinearLayoutManager userLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        SnapHelper userSnapHelper = new StartSnapHelper();

        mUserRv.setHasFixedSize(true);
        mUserRv.setLayoutManager(userLinearLayoutManager);
        mUserRv.setAdapter(mUserAdapter);
        userSnapHelper.attachToRecyclerView(mUserRv);
        Log.d(TAG, "Views initialized.");
    }

    /**
     * Void method that initializes all the listeners.
     */
    private void initListeners() {
        mUserClickListener = new UserClickListener() {
            @Override
            public void onClick(View view, User user) {
                Intent intent = new Intent(EventActivity.this, UserActivity.class);
                intent.putExtra("USER ID", user.getUserId());
                intent.putExtra("NAME", user.getName() + " " + user.getSurname());
                intent.putExtra("EMAIL", user.getEmail());
                intent.putExtra("PHONE", user.getPhone());
                startActivity(intent);
            }
        };
        Log.d(TAG, "Listeners initialized.");
    }

    /**
     * Void method that checks whether the user is participating or not to the event.
     */
    private void loadParticipation() {
        showProgress();
        RequestHelper.getJson(EventActivity.this, "events/" + eventId + "/participants/" + MainActivity.mUser.getUserId(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mFavourite.setIcon(R.drawable.ic_favorite);
                userIsParticipating = true;
                hideProgress();
                Log.d(TAG, "The user is participating to this event.");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                userIsParticipating = false;
                hideProgress();
                Log.d(TAG, "The user is not participating to this event.\n" + error.getMessage());
            }
        });
    }

    /**
     * Void method that loads the creator's image by a simple GET request to the API.
     */
    private void loadCreatorImage() {
        showProgress();
        RequestHelper.getImage(EventActivity.this, "users/" + creatorId + "/photo", new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                mCreator.setImageBitmap(response);
                hideProgress();
                Log.d(TAG, "Creator's image loaded.");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                Log.d(TAG, "Error loading creator's photo.\n" + error.getMessage());
            }
        });
    }

    /**
     * Void method that loads all the event's facilities.
     */
    private void loadFacilities() {
        showProgress();
        RequestHelper.getJsonArray(EventActivity.this, "events/" + eventId + "/facilities", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() > 0) {
                    for (int i = 0; i < response.length(); i++) {
                        final TextView textView = new TextView(getApplicationContext());
                        Integer optionsId = -1;
                        String text = "";
                        try {
                            text = (new Facility(response.getJSONObject(i).getJSONObject("facility"))).getName();
                            String option = String.valueOf(response.getJSONObject(i).get("options"));
                            if(!option.equals(""))
                                optionsId = Integer.parseInt(option);
                            else
                                optionsId = -1;
                            textView.setText(text);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (optionsId!=-1) {
                            if (text.equals("TV")) {
                                showProgress();
                                RequestHelper.getJson(EventActivity.this, "media/channels/" + optionsId, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        String finalText = "";
                                        try {
                                            finalText = textView.getText() + " - " + response.getString("name");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        textView.setText(finalText);
                                        hideProgress();
                                        Log.d(TAG, "Detailed facility's options were successfully loaded.");
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        hideProgress();
                                        Log.d(TAG, "Detailed facility's options were successfully loaded.");
                                    }
                                });
                            } else if (text.equals("Audio")) {
                                RequestHelper.getJson(EventActivity.this, "media/playlists/" + optionsId, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        String finalText = "";
                                        try {
                                            finalText = textView.getText() + " - " + response.getString("name");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        textView.setText(finalText);
                                        hideProgress();
                                        Log.d(TAG, "Detailed facility's options were successfully loaded.");
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        hideProgress();
                                        Log.d(TAG, "Detailed facility's options were successfully loaded.");
                                    }
                                });
                            }
                        }

                        textView.setTextAppearance(R.style.TextAppearance_AppCompat_Body1);
                        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        llp.setMargins(0, (int) (8 * getResources().getDisplayMetrics().density), 0, 0);
                        textView.setLayoutParams(llp);
                        mFacilitiesContainer.addView(textView);
                    }

                } else {
                    TextView textView = new TextView(getApplicationContext());
                    textView.setText(getResources().getString(R.string.no_facilities));
                    textView.setTextAppearance(R.style.TextAppearance_AppCompat_Body1);
                    LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    llp.setMargins(0, (int) (8 * getResources().getDisplayMetrics().density), 0, 0);
                    textView.setLayoutParams(llp);
                    mFacilitiesContainer.addView(textView);
                }
                hideProgress();
                Log.d(TAG, "Event's facilities loaded.");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                Log.d(TAG, "Error loading event's facilities.\n" + error.getMessage());
            }
        });
    }

    /**
     * Void method that loads all the users met at the event.
     */
    private void loadUsersMet() {
        showProgress();
        RequestHelper.getJsonArray(EventActivity.this, "users/" + MainActivity.mUser.getUserId() + "/connections/event/" + eventId, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() > 0) {
                    findViewById(R.id.event_tv_no_users).setVisibility(View.GONE);
                    for (int i = 0; i < response.length(); i++) {
                        showProgress();
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject = response.getJSONObject(i).getJSONObject("user2");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        final User user = new User(jsonObject);
                        RequestHelper.getImage(getApplicationContext(), "users/" + user.getUserId() + "/photo", new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                user.setPhoto(response);
                                mUserAdapter.addElement(user);
                                hideProgress();
                                Log.d(TAG, "User's photo loaded.");
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                hideProgress();
                                Log.d(TAG, "Error loading user's photo.\n" + error.getMessage());
                            }
                        });
                    }
                } else {
                    findViewById(R.id.event_rv_users).setVisibility(View.GONE);
                }
                hideProgress();
                Log.d(TAG, "Users met loaded.");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                Log.d(TAG, "Error loading users met.\n" + error.getMessage());
            }
        });
    }

    /**
     * Void method that handles the user participation to the event.
     */
    private void toggleParticipation() {
        if (!userIsParticipating) {
            userIsParticipating = true;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userID", MainActivity.mUser.getUserId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestHelper.postJson(EventActivity.this, "events/" + eventId + "/participants", jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mFavourite.setIcon(R.drawable.ic_favorite);
                    Log.d(TAG, "User added to the event's participants.");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Error adding the user to the event's participants.\n" + error.getMessage());
                }
            });
        } else {
            userIsParticipating = false;
            RequestHelper.delete(EventActivity.this, "events/" + eventId + "/participants/" + MainActivity.mUser.getUserId(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mFavourite.setIcon(R.drawable.ic_favorite_border);
                    Log.d(TAG, "User removed from the event's participants.");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Error removing the user from the event's participants.\n" + error.getMessage());
                }
            });
        }
    }

    /**
     * Void method that deletes the event by sending a DELETE request to the event API.
     */
    private void deleteEvent() {
        showProgress();
        RequestHelper.delete(EventActivity.this, "events/" + eventId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgress();
                Log.d(TAG, "Event deleted.");
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                Log.d(TAG, "Error deleting the event.\n" + error.getMessage());
            }
        });
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
