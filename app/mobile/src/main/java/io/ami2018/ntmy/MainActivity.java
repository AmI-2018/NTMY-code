package io.ami2018.ntmy;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import io.ami2018.ntmy.model.User;
import io.ami2018.ntmy.network.PersistentCookieStore;
import io.ami2018.ntmy.network.RequestHelper;
import io.ami2018.ntmy.wearconnection.MessageListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private DrawerLayout mDrawer;
    private View mProgress;

    public static User mUser;
    private MessageClient mMessageClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initObjects();
        initViews();
        initDrawer();
        //initListeners();

        showProgress();

        CookieManager cookieManager = new CookieManager(new PersistentCookieStore(getApplicationContext()), CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        Log.d(TAG, "Log In Starting");
        RequestHelper.getJson(getApplicationContext(), "login", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Log In OK");
                mUser = new User(response);
                String fullName = mUser.getName() + " " + mUser.getSurname();
                ((TextView) findViewById(R.id.nav_tv_name)).setText(fullName);
                ((TextView) findViewById(R.id.nav_tv_email)).setText(mUser.getEmail());
                initMessageListener();
                displayMainFragment();
                hideProgress();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Log In Error");
                hideProgress();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);


    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_signout:
                signOut();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;

        switch (id) {
            case R.id.nav_events:
                Log.d(TAG, "Events Navigation Clicked");
                fragment = new MainFragment();
                break;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initObjects() {
        mMessageClient = Wearable.getMessageClient(this);
    }

    private void initViews() {
        Log.d(TAG, "Views Init");

        mDrawer = findViewById(R.id.main_dl);
        mProgress = findViewById(R.id.progress_overlay);
    }

    private void initMessageListener() {
        mMessageClient.addListener(new MessageListener(getApplicationContext(), mUser));
    }

    private void initDrawer() {
        Log.d(TAG, "Drawer Init");

        //Toolbar
        Toolbar mToolbar = findViewById(R.id.main_tb);
        setSupportActionBar(mToolbar);

        //Drawer and navigation
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView mNav = findViewById(R.id.main_nv);
        mNav.setNavigationItemSelectedListener(this);
        mNav.setCheckedItem(R.id.nav_events);
    }

    private void signOut() {
        Log.d(TAG, "Sign Out Starting");
        RequestHelper.get(getApplicationContext(), "logout", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Sign Out OK");
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Sign Out Error");
                Toast.makeText(MainActivity.this, "Error logging out", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgress() {
        Log.d(TAG, "Showing Progress");
        mProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        Log.d(TAG, "Hiding Progress");
        mProgress.setVisibility(View.GONE);
    }

    private void displayMainFragment() {
        //Fragment
        Log.d(TAG, "Injecting Main Fragment");
        Fragment fragment = new MainFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }
}
