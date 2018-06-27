package io.ami2018.ntmy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import io.ami2018.ntmy.network.PersistentCookieStore;
import io.ami2018.ntmy.network.RequestHelper;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private TextInputLayout mEmailLayout;
    private TextInputLayout mPasswordLayout;
    private TextInputEditText mEmail;
    private TextInputEditText mPassword;
    private Button mSignIn;
    private Button mSignUp;
    private View mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Load cookies used for http requests
        CookieManager cookieManager = new CookieManager(new PersistentCookieStore(LoginActivity.this), CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        // Init
        initViews();
        initListeners();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn_sign_in:
                handleSignIn();
                break;
            case R.id.login_btn_sign_up:
                handleSignUp();
                break;
            case R.id.login_tv_password_forget:
                handleForgotPassword();
                break;
        }
    }

    /**
     * Void method that initializes all the views.
     */
    private void initViews() {
        mEmailLayout = findViewById(R.id.login_til_email);
        mPasswordLayout = findViewById(R.id.login_til_password);
        mEmail = findViewById(R.id.login_tiet_email);
        mPassword = findViewById(R.id.login_tiet_password);
        mSignIn = findViewById(R.id.login_btn_sign_in);
        mSignUp = findViewById(R.id.login_btn_sign_up);
        mProgress = findViewById(R.id.progress_overlay);
        Log.d(TAG, "Views initialized.");
    }

    /**
     * Void method that initializes all the views listeners.
     */
    private void initListeners() {
        mSignUp.setOnClickListener(this);
        mSignIn.setOnClickListener(this);
        Log.d(TAG, "Listeners initialized.");
    }

    /**
     * Void method that handles the sign in.
     * A JSON object is created using email and password set by the user in the input fields.
     * The sign in corresponds to the POST of the JSON object to the login API.
     */
    private void handleSignIn() {
        final String email, password;
        email = mEmail.getText().toString().trim();
        password = mPassword.getText().toString().trim();

        // If email and password are valid
        if (validateEmail(email) && validatePassword(password)) {

            Log.d(TAG, "Email and password are valid.");

            // Hide the keyboard and start showing loading progress
            hideKeyboard();
            showProgress();

            // Creation of the JSON Object that we are going to POST to the API
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("email", email);
                jsonBody.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // API request
            RequestHelper.postJson(LoginActivity.this, "login", jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "Correct credentials. Starting MainActivity.");
                    hideProgress();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Bad credentials.\n" + error.getMessage());
                    hideProgress();
                }
            });

        } else {
            Log.d(TAG, "Email and password are not valid.");
        }
    }

    private void handleSignUp() {
    }

    private void handleForgotPassword() {
    }

    /**
     * Custom email validator: the email is valid if it is not empty and if it matches the email pattern.
     *
     * @param email, the email to be checked.
     * @return true or false whether the email is valid or not.
     */
    private boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            mEmailLayout.setError("This field is required.");
            hideKeyboard();
            return false;
        } else {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mEmailLayout.setError("The string you entered is not an email.");
                hideKeyboard();
                return false;
            } else {
                mEmailLayout.setErrorEnabled(false);
                return true;
            }
        }
    }

    /**
     * Custom password validator: the password is valid if it is not empty.
     *
     * @param password, the password to be checked.
     * @return true or false whether the password is valid or not
     */
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

    /**
     * Method used for hiding the keyboard.
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            View view = getCurrentFocus();
            if (view != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
