package io.ami2018.ntmy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;

import io.ami2018.ntmy.model.Category;
import io.ami2018.ntmy.model.Event;
import io.ami2018.ntmy.model.Room;
import io.ami2018.ntmy.network.RequestHelper;
import io.ami2018.ntmy.recyclerviews.EventAdapter;
import io.ami2018.ntmy.recyclerviews.EventClickListener;
import io.ami2018.ntmy.recyclerviews.StartSnapHelper;

public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();

    private EventAdapter mTodayAdapter;
    private EventAdapter mEnrolledAdapter;
    private EventAdapter mFutureAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        initObjects();
        initViews(view);
        initSwipe(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Events");

        todayEvents();
        enrolledEvents();
        futureEvents();
    }

    private void initObjects() {
        EventClickListener eventClickListener = new EventClickListener() {
            @Override
            public void onClick(View view, Event event) {
                String fullName = event.getCreator().getName() + " " + event.getCreator().getSurname();
                Intent intent = new Intent(getContext(), EventActivity.class);
                intent.putExtra("EVENT ID", event.getEventId());
                intent.putExtra("NAME", event.getName());
                intent.putExtra("DESCRIPTION", event.getDescription());
                intent.putExtra("START", event.getStart());
                intent.putExtra("END", event.getEnd());
                if (event.getRoom() != null)
                    intent.putExtra("ROOM", event.getRoom().getName());
                else
                    intent.putExtra("ROOM", "Not Assigned");
                intent.putExtra("CREATOR ID", event.getCreator().getUserId());
                intent.putExtra("CREATOR NAME", fullName);
                startActivity(intent);
            }
        };

        mTodayAdapter = new EventAdapter(eventClickListener);
        mEnrolledAdapter = new EventAdapter(eventClickListener);
        mFutureAdapter = new EventAdapter(eventClickListener);
    }

    private void initViews(View mainView) {
        RecyclerView mTodayRv = mainView.findViewById(R.id.mainfrag_rv_todays_events);
        RecyclerView mEnrolledRv = mainView.findViewById(R.id.mainfrag_rv_enrolled_events);
        RecyclerView mFutureRv = mainView.findViewById(R.id.mainfrag_rv_all_events);

        LinearLayoutManager todayLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        SnapHelper todaySnapHelper = new StartSnapHelper();

        mTodayRv.setHasFixedSize(true);
        mTodayRv.setLayoutManager(todayLinearLayoutManager);
        mTodayRv.setAdapter(mTodayAdapter);
        todaySnapHelper.attachToRecyclerView(mTodayRv);

        LinearLayoutManager enrolledLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        SnapHelper enrolledSnapHelper = new StartSnapHelper();

        mEnrolledRv.setHasFixedSize(true);
        mEnrolledRv.setLayoutManager(enrolledLinearLayoutManager);
        mEnrolledRv.setAdapter(mEnrolledAdapter);
        enrolledSnapHelper.attachToRecyclerView(mEnrolledRv);

        LinearLayoutManager futureLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        SnapHelper futureSnapHelper = new StartSnapHelper();

        mFutureRv.setHasFixedSize(true);
        mFutureRv.setLayoutManager(futureLinearLayoutManager);
        mFutureRv.setAdapter(mFutureAdapter);
        futureSnapHelper.attachToRecyclerView(mFutureRv);

        ((FloatingActionButton) mainView.findViewById(R.id.mainfrag_fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddEventActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initSwipe(View mainView) {
        final SwipeRefreshLayout swipeRefreshLayout = mainView.findViewById(R.id.mainfrag_srl);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mTodayAdapter.clear();
                mEnrolledAdapter.clear();
                mFutureAdapter.clear();

                todayEvents();
                enrolledEvents();
                futureEvents();

                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void todayEvents() {
        RequestHelper.getJsonArray(getContext(), "events/today", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        final Event event = new Event(response.getJSONObject(i));
                        RequestHelper.getJsonArray(getContext(), "events/" + event.getEventId() + "/categories", new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    for (int i = 0; i < response.length(); i++) {
                                        event.addCategory(new Category(response.getJSONObject(i).getJSONObject("category")));
                                    }
                                    RequestHelper.getJsonArray(getContext(), "schedule/event/" + event.getEventId(), new Response.Listener<JSONArray>() {
                                        @Override
                                        public void onResponse(JSONArray response) {
                                            try {
                                                event.setRoom(new Room(response.getJSONObject(0).getJSONObject("room")));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            mTodayAdapter.addElement(event);
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d(TAG, "no room");
                                            mTodayAdapter.addElement(event);
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //TODO manage no categories
                                Log.d(TAG, "no categories");
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO manage no events
                Log.d(TAG, "no today events");
            }
        });
    }

    private void enrolledEvents() {
        RequestHelper.getJsonArray(getContext(), "users/" + String.valueOf(MainActivity.mUser.getUserId()) + "/events", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "entrato");
                try {
                    for (int i = 0; i < response.length(); i++) {
                        final Event event = new Event(response.getJSONObject(i).getJSONObject("event"));
                        RequestHelper.getJsonArray(getContext(), "events/" + event.getEventId() + "/categories", new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    for (int i = 0; i < response.length(); i++) {
                                        event.addCategory(new Category(response.getJSONObject(i).getJSONObject("category")));
                                    }
                                    RequestHelper.getJsonArray(getContext(), "schedule/event/" + event.getEventId(), new Response.Listener<JSONArray>() {
                                        @Override
                                        public void onResponse(JSONArray response) {
                                            try {
                                                event.setRoom(new Room(response.getJSONObject(0).getJSONObject("room")));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            MainActivity.mUser.addEvent(event);
                                            mEnrolledAdapter.addElement(event);
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d(TAG, "no room");
                                            mEnrolledAdapter.addElement(event);
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //TODO manage no categories
                                Log.d(TAG, "no categories");
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO manage no events
                Log.d(TAG, "no user events");
            }
        });
    }

    private void futureEvents() {
        RequestHelper.getJsonArray(getContext(), "events/next", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        final Event event = new Event(response.getJSONObject(i));
                        RequestHelper.getJsonArray(getContext(), "events/" + event.getEventId() + "/categories", new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    for (int i = 0; i < response.length(); i++) {
                                        event.addCategory(new Category(response.getJSONObject(i).getJSONObject("category")));
                                    }
                                    RequestHelper.getJsonArray(getContext(), "schedule/event/" + event.getEventId(), new Response.Listener<JSONArray>() {
                                        @Override
                                        public void onResponse(JSONArray response) {
                                            try {
                                                event.setRoom(new Room(response.getJSONObject(0).getJSONObject("room")));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            mFutureAdapter.addElement(event);
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d(TAG, "no room");
                                            mFutureAdapter.addElement(event);
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //TODO manage no categories
                                Log.d(TAG, "no categories");
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO manage no events
                Log.d(TAG, "no events");
            }
        });
    }
}
