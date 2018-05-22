package io.ami2018.ntmy;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //WARNING Make sure to use the exact url of the api location
    private static final String url = "http://192.168.1.110:5000/";
    public static final String TAG = "LoginActivityTAG";

    private static final boolean DEBUG = true;

    private TextInputLayout mEmailLayout;
    private TextInputLayout mPasswordLayout;
    private TextInputEditText mEmail;
    private TextInputEditText mPassword;
    private Button mSignIn;
    private Button mSignUp;
    private View mProgress;

    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initObjects();
        initListeners();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mQueue != null) {
            mQueue.cancelAll(TAG);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn_sign_in:
                signIn();
                break;
            case R.id.login_btn_sign_up:
                signUp();
                break;
        }
    }

    private void initViews() {
        mEmailLayout = findViewById(R.id.login_til_email);
        mPasswordLayout = findViewById(R.id.login_til_password);
        mEmail = findViewById(R.id.login_tiet_email);
        mPassword = findViewById(R.id.login_tiet_password);
        mSignIn = findViewById(R.id.login_btn_sign_in);
        mSignUp = findViewById(R.id.login_btn_sign_up);
        mProgress = findViewById(R.id.progress_overlay);
    }

    private void initObjects() {
        mQueue = Volley.newRequestQueue(this);
    }

    private void initListeners() {
        mSignUp.setOnClickListener(this);
        mSignIn.setOnClickListener(this);
    }

    private void signIn() {
        final String email, password;
        email = mEmail.getText().toString().trim();
        password = mPassword.getText().toString().trim();
        if (validateEmail(email) && validatePassword(password)) {
            showProgress();
            hideKeyboard();

            // Creation of the JSON Object that we want to POST to the API
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("userID", email);
                jsonBody.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String requestBody = jsonBody.toString();

            // Creation of the request
            StringRequest request = new StringRequest(Request.Method.POST, url + "login", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideProgress();
                    Toast.makeText(LoginActivity.this, "Good credentials!", Toast.LENGTH_SHORT).show();
                    // TODO Launch MainActivity
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgress();
                    Snackbar.make(mEmail, "Wrong Email or Password", Snackbar.LENGTH_LONG).show();
                }
            }) {
                @Override
                public byte[] getBody() {
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

    private void signUp() {
        // TODO Launch SignUpActivity
    }

    private boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            mEmailLayout.setError("This field is required.");
            hideKeyboard();
            return false;
        } else {
            if (!DEBUG && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mEmailLayout.setError("The string you entered is not an email.");
                hideKeyboard();
                return false;
            } else {
                mEmailLayout.setErrorEnabled(false);
                return true;
            }
        }
    }

    private boolean validatePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            mPasswordLayout.setError("This field is required.");
            hideKeyboard();
            return false;
        } else {
            mPasswordLayout.setErrorEnabled(false);
            return true;
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void showProgress() {
        mProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }
}
