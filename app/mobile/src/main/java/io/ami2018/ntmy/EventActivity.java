package io.ami2018.ntmy;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class EventActivity extends AppCompatActivity {

    private static final String TAG = EventActivity.class.getSimpleName();

    private Integer eventId;
    private String name;
    private String description;
    private String start;
    private String end;
    private String room;
    private String creator;

    private View mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        initObjects();
        initViews();
        initToolbar();

    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.event_tb);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initObjects() {
        eventId = getIntent().getIntExtra("EVENT ID", 0);
        name = getIntent().getStringExtra("NAME");
        description = getIntent().getStringExtra("DESCRIPTION");
        start = getIntent().getStringExtra("START");
        end = getIntent().getStringExtra("END");
        room = getIntent().getStringExtra("ROOM");
        creator = getIntent().getStringExtra("CREATOR NAME");
    }

    private void initViews() {
        ((CollapsingToolbarLayout) findViewById(R.id.event_ctl)).setTitle(name);
        ((TextView) findViewById(R.id.event_tv_date)).setText(start.split(" ")[0]);
        ((TextView) findViewById(R.id.event_tv_time)).setText(start.split(" ")[1]);
        ((TextView) findViewById(R.id.event_tv_room)).setText(room);
        ((TextView) findViewById(R.id.event_tv_description)).setText(description);
        ((TextView) findViewById(R.id.event_tv_creator)).setText(creator);
        mProgress = findViewById(R.id.progress_overlay);
    }

    private void showProgress() {
        Log.d(TAG, "Showing Progress");
        mProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        Log.d(TAG, "Hiding Progress");
        mProgress.setVisibility(View.GONE);
    }
}
