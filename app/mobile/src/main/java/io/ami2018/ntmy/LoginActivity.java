package io.ami2018.ntmy;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    //WARNING Make sure to use the exact url of the api location
    private static final String url = "http://192.168.1.110:5000/";

    private TextInputEditText mEmail;
    private TextInputEditText mPassword;
    private Button mSignIn;
    private Button mSignUp;
    private RequestQueue mQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = (TextInputEditText) findViewById(R.id.login_tiet_email);
        mPassword = (TextInputEditText) findViewById(R.id.login_tiet_password);
        mSignIn = (Button) findViewById(R.id.login_btn_sign_in);
        mSignUp = (Button) findViewById(R.id.login_btn_sign_up);

        mQueue = Volley.newRequestQueue(this);

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email, password;
                email = mEmail.getText().toString();
                password = mPassword.getText().toString();
                if(email.matches("")) {
                    mEmail.setError("This field cannot be empty.");
                } else {
                    if(password.matches("")) {
                        mPassword.setError("This field cannot be empty.");
                    } else {
                        JSONObject jsonBody = new JSONObject();
                        try {
                            jsonBody.put("userID", email);
                            jsonBody.put("password", password);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        final String requestBody = jsonBody.toString();

                        StringRequest request = new StringRequest(Request.Method.POST, url + "login", new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(LoginActivity.this, "Good credentials!", Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }){
                            @Override
                            public byte[] getBody() throws AuthFailureError {
                                try {
                                    return requestBody.getBytes("utf-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            }

                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8";
                            }
                        };

                        mQueue.add(request);
                    }
                }
            }
        });
    }


}
