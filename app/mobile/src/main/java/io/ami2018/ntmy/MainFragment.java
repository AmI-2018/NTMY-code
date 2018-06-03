package io.ami2018.ntmy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.ami2018.ntmy.model.Category;
import io.ami2018.ntmy.model.Event;
import io.ami2018.ntmy.model.Room;
import io.ami2018.ntmy.model.User;
import io.ami2018.ntmy.network.RequestHelper;
import io.ami2018.ntmy.recyclerviews.NextEventAdapter;
import io.ami2018.ntmy.recyclerviews.StartSnapHelper;

public class MainFragment extends Fragment {

    private User mUser;
    private NextEventAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView mRv = view.findViewById(R.id.mainfrag_rv_next_events);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        SnapHelper snapHelper = new StartSnapHelper();

        mAdapter = new NextEventAdapter();

        mRv.setHasFixedSize(true);
        mRv.setLayoutManager(linearLayoutManager);
        mRv.setAdapter(mAdapter);
        snapHelper.attachToRecyclerView(mRv);

        mUser = User.getInstance();
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
                                    RequestHelper.getJson(getContext(), "schedule/event/" + event.getEventId(), new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                event.setRoom(new Room(response.getJSONObject("room")));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            mUser.addEvent(event);
                                            mAdapter.addElement(event);
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    });
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Home");
    }
}
