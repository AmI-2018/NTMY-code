package io.ami2018.ntmy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import io.ami2018.ntmy.model.User;
import io.ami2018.ntmy.network.RequestHelper;
import io.ami2018.ntmy.recyclerviews.EventAdapter;
import io.ami2018.ntmy.recyclerviews.StartSnapHelper;

public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();

    private User mUser;

    private EventAdapter mTodayAdapter;
    private EventAdapter mEnrolledAdapter;
    private EventAdapter mFutureAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        initObjects();
        initViews(view);

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
        mTodayAdapter = new EventAdapter();
        mEnrolledAdapter = new EventAdapter();
        mFutureAdapter = new EventAdapter();
        mUser = User.getInstance();
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
        RequestHelper.getJsonArray(getContext(), "users/" + String.valueOf(mUser.getUserId()) + "/events", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
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
                                            mUser.addEvent(event);
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
