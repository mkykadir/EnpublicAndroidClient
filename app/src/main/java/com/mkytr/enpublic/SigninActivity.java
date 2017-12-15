package com.mkytr.enpublic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

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
    }

    public void onSigninButtonClick(View v){
        EditText etUsername = findViewById(R.id.etUsernameLogin);
        String username = etUsername.getText().toString();
        EditText etPassword = findViewById(R.id.etPasswordLogin);
        String password = etPassword.getText().toString();

        if(username.equals("") || password.equals("")){
            Toast.makeText(this, "Please enter credentials!", Toast.LENGTH_SHORT).show();
            return;
        }

        String toEncode = username + ":" + password;
        String authContent = Base64.encodeToString(toEncode.getBytes(), Base64.NO_WRAP);
        final String authText = "Basic " + authContent;

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(MainActivity.BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        EnpublicApi client = retrofit.create(EnpublicApi.class);

        final ProgressBar pbLogin = findViewById(R.id.pbLogin);
        pbLogin.setVisibility(View.VISIBLE);
        final ScrollView svLoginForm = findViewById(R.id.svLoginForm);
        svLoginForm.setVisibility(View.GONE);

        Call<POSTResult> call = client.loginUser(authText);
        call.enqueue(new Callback<POSTResult>() {
            @Override
            public void onResponse(Call<POSTResult> call, Response<POSTResult> response) {
                POSTResult result = response.body();
                if(result.getResult() == 200){
                    SharedPreferences.Editor preferences = getSharedPreferences(MainActivity.PREF_NAME, MODE_PRIVATE).edit();
                    preferences.putString("auth", authText);
                    preferences.apply();
                    Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(profileIntent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<POSTResult> call, Throwable t) {
                Toast.makeText(SigninActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                pbLogin.setVisibility(View.GONE);
                svLoginForm.setVisibility(View.VISIBLE);
            }
        });

    }

    public void onSignupButtonClick(View v){
        Intent signUp = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(signUp);
    }
}
