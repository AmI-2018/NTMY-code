package io.ami2018.ntmy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.concurrent.atomic.AtomicInteger;

import io.ami2018.ntmy.network.RequestHelper;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = UserActivity.class.getSimpleName();
    private static AtomicInteger progressCounter;

    private String name;
    private String email;
    private String phone;
    private Integer id;

    private View mProgress;

    /**
     * Void method that handles the addition of the user as a contact in the phone.
     *
     * @param context, context used for starting the new activity.
     * @param name,    the name of the new contact.
     * @param email,   the email of the new contact.
     * @param phone,   the phone number of the new contact.
     */
    public static void addAsContactConfirmed(final Context context, String name, String email, String phone) {
        // Creation of the intent for adding a new contact
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);
        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, email);
        context.startActivity(intent);
        Log.d(TAG, "Contact addition started.");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Init
        initObjects();
        initViews();

        // Load data
        loadImage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_user_contact:
                addAsContactConfirmed(this, name, email, phone);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Void method that initializes all the objects.
     */
    private void initObjects() {
        // Getting the user data from the intent
        id = getIntent().getIntExtra("USER ID", 0);
        name = getIntent().getStringExtra("NAME");
        email = getIntent().getStringExtra("EMAIL");
        phone = getIntent().getStringExtra("PHONE");
        progressCounter = new AtomicInteger(0);
        Log.d(TAG, "Objects initialized.");
    }

    /**
     * Void method that initializes all the views.
     */
    private void initViews() {
        ((CollapsingToolbarLayout) findViewById(R.id.user_ctl)).setTitle(name);
        ((TextView) findViewById(R.id.user_tv_email)).setText(email);
        ((TextView) findViewById(R.id.user_tv_phone)).setText(phone);
        mProgress = findViewById(R.id.progress_overlay_white);
        setSupportActionBar((Toolbar) findViewById(R.id.user_tb));
        Log.d(TAG, "Views initialized.");
    }

    /**
     * Void method that loads the user's image by a simple GET request to the API.
     */
    private void loadImage() {
        showProgress();
        RequestHelper.getImage(getApplicationContext(), "users/" + id + "/photo", new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                ((ImageView) findViewById(R.id.user_iv_user)).setImageBitmap(response);
                hideProgress();
                Log.d(TAG, "User's photo loaded.");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                Log.d(TAG, "Error loading user's photo.\n" + error.getMessage());
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
