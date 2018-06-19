package io.ami2018.ntmy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.widget.ImageView;
import android.widget.TextView;


public class UserFoundActivity extends WearableActivity {
    // Activity used to show found user's pic and fullname

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_found);

        // Get the data from parent activity
        Intent intent = getIntent();
        byte[] bytes = intent.getByteArrayExtra("photo");
        Bitmap bitmap = (Bitmap) BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        String username = intent.getStringExtra("username");

        // Set the layout
        ImageView image = findViewById(R.id.imageView);
        TextView text = findViewById(R.id.textView);
        text.setText(username);
        image.setImageBitmap(BitmapUtil.GetBitmapClippedCircle(bitmap));

        // Enables Always-on
        setAmbientEnabled();

        // Disappear after 5 sec
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, 5000);
    }
}
