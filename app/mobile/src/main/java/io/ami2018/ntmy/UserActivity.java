package io.ami2018.ntmy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import io.ami2018.ntmy.network.RequestHelper;

public class UserActivity extends AppCompatActivity {

    private String name;
    private String email;
    private String phone;
    private Integer id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        initObjects();
        initViews();
        initToolbar();
    }

    public static void addAsContactConfirmed(final Context context, String name, String email, String phone) {

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

        intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);
        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, email);

        context.startActivity(intent);

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

    private void initObjects() {
        id = getIntent().getIntExtra("USER ID", 0);
        name = getIntent().getStringExtra("NAME");
        email = getIntent().getStringExtra("EMAIL");
        phone = getIntent().getStringExtra("PHONE");
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.user_tb);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        ((CollapsingToolbarLayout) findViewById(R.id.user_ctl)).setTitle(name);
        ((TextView) findViewById(R.id.user_tv_email)).setText(email);
        ((TextView) findViewById(R.id.user_tv_phone)).setText(phone);
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
}
