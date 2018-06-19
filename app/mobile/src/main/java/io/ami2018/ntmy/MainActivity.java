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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Objects;

import io.ami2018.ntmy.model.User;
import io.ami2018.ntmy.network.PersistentCookieStore;
import io.ami2018.ntmy.network.RequestHelper;
import io.ami2018.ntmy.wearconnection.MessageListener;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static User mUser;

    private DrawerLayout mDrawer;
    private View mProgress;
    private MessageClient mMessageClient;
    private MessageListener mMessageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load cookies used for http requests
        CookieManager cookieManager = new CookieManager(new PersistentCookieStore(MainActivity.this), CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        // Init
        initObjects();
        initViews();
        initDrawer();

        // Load login status
        loadLoginStatus();
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
                handleSignOut();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // Switching fragment when the user clicks an item in the NavigationView
        switch (id) {
            case R.id.nav_events:
                displayMainFragment();
                break;
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Void method that initializes all the objects.
     */
    private void initObjects() {
        mMessageClient = Wearable.getMessageClient(this);
        mMessageListener = MessageListener.getINSTANCE();
        Log.d(TAG, "Objects initialized.");
    }

    /**
     * Void method that initialize all the views.
     */
    private void initViews() {
        mDrawer = findViewById(R.id.main_dl);
        mProgress = findViewById(R.id.progress_overlay_white);
        Log.d(TAG, "Views initialized.");
    }

    /**
     * Void method that initializes the Toolbar and the Navigation Drawer.
     */
    private void initDrawer() {
        Toolbar mToolbar = findViewById(R.id.main_tb);
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView mNav = findViewById(R.id.main_nv);
        mNav.setNavigationItemSelectedListener(this);
        mNav.setCheckedItem(R.id.nav_events);
        Log.d(TAG, "Drawer initialized.");
    }

    /**
     * Void method that initializes the message listener.
     */
    private void initMessageListener() {
        mMessageListener.set(MainActivity.this, mUser);
        mMessageClient.addListener(mMessageListener);
        Log.d(TAG, "MessageListener initialized.");
    }

    /**
     * Void method that handles the sign out.
     * The sign out corresponds to a simple POST request on the logout API.
     */
    private void handleSignOut() {
        RequestHelper.get(MainActivity.this, "logout", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Signed Out.");
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "An error occurred while signing out.\n" + error.getMessage());
            }
        });
    }

    /**
     * Void method that loads the login status.
     * If the user is not logged in, LoginActivity will be launched.
     * The status is checked by a GET request on the login API.
     */
    private void loadLoginStatus() {
        showProgress();
        RequestHelper.getJson(this, "login", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "The user is logged in.");
                mUser = new User(response);
                updateNavHeader(mUser.getName() + " " + mUser.getSurname(), mUser.getEmail());
                initMessageListener();
                displayMainFragment();
                hideProgress();
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "The user is not logged in.\n" + error.getMessage());
                hideProgress();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    /**
     * Void method that sets the action bar title.
     *
     * @param title, the title
     */
    public void setActionBarTitle(String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    /**
     * Void method that replace the fragment stack with the MainFragment.
     */
    private void displayMainFragment() {
        Fragment fragment = new MainFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
        Log.d(TAG, "Switched to MainFragment.");
    }

    /**
     * Void method that updates user info in the Navigation Header.
     *
     * @param fullName, is the fullName of the user.
     * @param email,    is the email of the user.
     */
    private void updateNavHeader(String fullName, String email) {
        ((TextView) findViewById(R.id.nav_tv_name)).setText(fullName);
        ((TextView) findViewById(R.id.nav_tv_email)).setText(email);
        Log.d(TAG, "Navigation Header info updated.");
    }

    /**
     * Method used for displaying the progress.
     */
    private void showProgress() {
        mProgress.setVisibility(View.VISIBLE);
        Log.d(TAG, "Progress shown");
    }

    /**
     * Method used for hiding the progress.
     */
    private void hideProgress() {
        mProgress.setVisibility(View.GONE);
        Log.d(TAG, "Progress hidden");
    }
}
