package io.ami2018.ntmy;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import io.ami2018.ntmy.network.RequestHelper;

public class UserActivity extends AppCompatActivity {

    private String name;
    private Integer id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Log.d(UserActivity.class.getSimpleName(), "At least");

        ((TextView) findViewById(R.id.user_prova)).setText("Ciao");

        initObjects();
        initViews();
        initToolbar();
    }

    private void initObjects() {
        name = getIntent().getStringExtra("NAME");
        id = getIntent().getIntExtra("USER ID", 0);
    }

    private void initViews() {
        ((CollapsingToolbarLayout) findViewById(R.id.user_ctl)).setTitle(name);
        RequestHelper.getImage(getApplicationContext(), "users/" + id + "/photo", new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                ((ImageView) findViewById(R.id.user_iv_user)).setImageBitmap(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.user_tb);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
