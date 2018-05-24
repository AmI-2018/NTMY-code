package io.ami2018.ntmy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import io.ami2018.ntmy.network.PersistentCookieStore;
import io.ami2018.ntmy.network.RequestHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTest;
    private Button mSignOut;

    private JSONObject mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CookieManager cookieManager = new CookieManager(new PersistentCookieStore(getApplicationContext()), CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        initViews();
        initObjects();
        initListeners();

        RequestHelper.get(getApplicationContext(), "login", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mUser = response;
                try {
                    String fullName = mUser.get("name").toString() + mUser.get("surname").toString();
                    mTest.setText(fullName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_sign_out:
                signOut();
                break;
        }
    }

    private void initViews() {
        mTest = findViewById(R.id.main_test);
        mSignOut = findViewById(R.id.main_sign_out);
    }

    private void initListeners() {
        mSignOut.setOnClickListener(this);
    }

    private void initObjects() {
        mUser = new JSONObject();
    }

    private void signOut() {
        RequestHelper.get(getApplicationContext(), "logout", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTest.setText("Error in loggin out");
            }
        });
    }
}
