package io.ami2018.ntmy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import io.ami2018.ntmy.model.Category;
import io.ami2018.ntmy.model.Color;
import io.ami2018.ntmy.model.Event;
import io.ami2018.ntmy.model.Room;
import io.ami2018.ntmy.network.RequestHelper;
import io.ami2018.ntmy.recyclerviews.EventAdapter;
import io.ami2018.ntmy.recyclerviews.EventClickListener;

public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();
    private static AtomicInteger progressCounter;

    private EventAdapter mAdapter;

    private View mProgress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // Init
        initListeners(view);
        initObjects();
        initViews(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) Objects.requireNonNull(getActivity())).setActionBarTitle("Events");

        // Load data
        loadEvents();
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.clear();
        loadEvents();
    }

    /**
     * Void method that initializes all the objects.
     */
    private void initObjects() {
        mAdapter = new EventAdapter(getContext(), new EventClickListener() {
            @Override
            public void onClick(View view, Event event) {
                String fullName = event.getCreator().getName() + " " + event.getCreator().getSurname();
                Intent intent = new Intent(getContext(), EventActivity.class);
                intent.putExtra("EVENT ID", event.getEventId());
                intent.putExtra("NAME", event.getName());
                intent.putExtra("DESCRIPTION", event.getDescription());
                intent.putExtra("START", event.getStart());
                if (event.getRoom() != null)
                    intent.putExtra("ROOM", event.getRoom().getName());
                else
                    intent.putExtra("ROOM", "Not Assigned");
                intent.putExtra("CREATOR ID", event.getCreator().getUserId());
                intent.putExtra("CREATOR NAME", fullName);
                if (event.getColor() != null)
                    intent.putExtra("COLOR", event.getColor().getColor());
                else
                    intent.putExtra("COLOR", -1);
                startActivity(intent);
            }
        });
        progressCounter = new AtomicInteger(0);
        Log.d(TAG, "Objects initialized.");
    }

    /**
     * Void method that initializes all the views.
     * The three RecyclerViews are initialized with their corresponding adapter.
     *
     * @param mainView, view needed as reference.
     */
    private void initViews(View mainView) {
        // RecyclerView
        RecyclerView mRv = mainView.findViewById(R.id.mainfrag_rv);
        LinearLayoutManager todayLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRv.setHasFixedSize(true);
        mRv.setLayoutManager(todayLinearLayoutManager);
        mRv.setAdapter(mAdapter);

        // Others
        mProgress = mainView.findViewById(R.id.progress_overlay_white);

        Log.d(TAG, "Views initialized.");
    }

    /**
     * Void method that initializes all the listeners.
     *
     * @param mainView, view needed as reference.
     */
    private void initListeners(View mainView) {
        // FloatingActionButton click listener that starts the AddEventActivity
        mainView.findViewById(R.id.mainfrag_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddEventActivity.class);
                startActivity(intent);
            }
        });

        // SwipeRefreshLayout listener that clears the adapters and reloads all the data
        final SwipeRefreshLayout mSwipeRefreshLayout = mainView.findViewById(R.id.mainfrag_srl);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Adapters cleaning
                mAdapter.clear();

                // Reload content
                loadEvents();

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        Log.d(TAG, "Listeners initialized.");
    }

    /**
     * Void method that loads from the API future events.
     */
    private void loadEvents() {
        showProgress();
        // Future events are in a JSONArray that is GET from the API
        RequestHelper.getJsonArray(getContext(), "events/next", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject = response.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final Event event = new Event(jsonObject);
                    showProgress();
                    // For each event we load its categories contained in a JSONArray
                    RequestHelper.getJsonArray(getContext(), "events/" + event.getEventId() + "/categories", new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    event.addCategory(new Category(response.getJSONObject(i).getJSONObject("category")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            // After loading categories, we check if the event is scheduled or not
                            RequestHelper.getJsonArray(getContext(), "schedule/event/" + event.getEventId(), new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    try {
                                        event.setRoom(new Room(response.getJSONObject(0).getJSONObject("room")));
                                        event.setColor(new Color(response.getJSONObject(0).getJSONObject("color")));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    // Finally we add the event to the adapter
                                    mAdapter.addElement(event);
                                    hideProgress();
                                    Log.d(TAG, "(Future) Event " + event.getEventId() + " has been loaded and added to the Adapter.");
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // If the event is not scheduled we add it to the adapter anyway
                                    mAdapter.addElement(event);
                                    hideProgress();
                                    Log.d(TAG, "(Future - No Room) Event " + event.getEventId() + " has been loaded and added to the Adapter.\n" + error.getMessage());
                                }
                            });
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideProgress();
                            Log.d(TAG, "(Future) Error while loading categories.\n" + error.getMessage());
                        }
                    });
                }
                hideProgress();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                Log.d(TAG, "(Future) Error while loading events.\n" + error.getMessage());
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
