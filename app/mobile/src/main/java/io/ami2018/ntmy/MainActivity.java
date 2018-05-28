package io.ami2018.ntmy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawer;
    private View mProgress;

    private JSONObject mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initObjects();
        initDrawer();
        initListeners();

        showProgress();

        CookieManager cookieManager = new CookieManager(new PersistentCookieStore(getApplicationContext()), CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        RequestHelper.get(getApplicationContext(), "login", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mUser = response;
                try {
                    String fullName = mUser.get("name").toString() + mUser.get("surname").toString();
                    ((TextView) findViewById(R.id.nav_tv_name)).setText(fullName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideProgress();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
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
            case R.id.nav_home:
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

    private void initViews() {
        mDrawer = findViewById(R.id.main_dl);
        mProgress = findViewById(R.id.progress_overlay);
    }

    private void initListeners() {
        //mSignOut.setOnClickListener(this);
    }

    private void initDrawer() {
        //Toolbar
        Toolbar mToolbar = findViewById(R.id.main_tb);
        setSupportActionBar(mToolbar);

        //Drawer and navigation
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView mNav = findViewById(R.id.main_nv);
        mNav.setNavigationItemSelectedListener(this);
        mNav.setCheckedItem(R.id.nav_home);

        //Fragment
        Fragment fragment = new MainFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
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
            }
        });
    }

    private void showProgress() {
        mProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }
}
