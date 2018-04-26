package com.mkytr.enpublic.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.mkytr.enpublic.ApiError;
import com.mkytr.enpublic.EnpublicApi;
import com.mkytr.enpublic.MainActivity;
import com.mkytr.enpublic.POSTResult;
import com.mkytr.enpublic.R;
import com.mkytr.enpublic.RestClient;
import com.mkytr.enpublic.RestErrorUtils;
import com.mkytr.enpublic.RestfulObjects.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SigninActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        String checkLoggedIn = getSharedPreferences(MapsActivity.PREF_NAME, MODE_PRIVATE).getString("auth", "none");
        if(!checkLoggedIn.contentEquals("none")){
            // User already logged-in
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        try{
            getSupportActionBar().setElevation(0);
        }catch (NullPointerException e){
            return;
        }

    }

    public void onSigninButtonClick(View v){
        EditText etUsername = findViewById(R.id.etUsernameLogin);
        String username = etUsername.getText().toString();
        EditText etPassword = findViewById(R.id.etPasswordLogin);
        String password = etPassword.getText().toString();

        if(username.equals("") || password.equals("")){
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        String toEncode = username + ":" + password;
        String authContent = Base64.encodeToString(toEncode.getBytes(), Base64.NO_WRAP);
        final String authText = "Basic " + authContent;

        EnpublicApi client = RestClient.getInstance().getInterface();
        final ProgressBar pbLogin = findViewById(R.id.pbLogin);
        pbLogin.setVisibility(View.VISIBLE);
        final ScrollView svLoginForm = findViewById(R.id.svLoginForm);
        svLoginForm.setVisibility(View.GONE);

        Call<User> call = client.userProfile(authText);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    User result = response.body();
                    SharedPreferences.Editor preferences = getSharedPreferences(MapsActivity.PREF_NAME, MODE_PRIVATE).edit();
                    preferences.putString("auth", authText);
                    preferences.apply();
                    Intent profileIntent = new Intent(SigninActivity.this, ProfileActivity.class);
                    profileIntent.putExtra("profile-info", result);
                    profileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(profileIntent);
                    finish();
                }else {
                    ApiError error = RestErrorUtils.parseError(response);
                    Toast.makeText(SigninActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    pbLogin.setVisibility(View.GONE);
                    svLoginForm.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(SigninActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                pbLogin.setVisibility(View.GONE);
                svLoginForm.setVisibility(View.VISIBLE);
            }
        });

    }

    public void onSignupButtonClick(View v){
        Intent signUp = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(signUp);
    }
}
