package com.mkytr.enpublic.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mkytr.enpublic.ApiError;
import com.mkytr.enpublic.EnpublicApi;
import com.mkytr.enpublic.R;
import com.mkytr.enpublic.RestClient;
import com.mkytr.enpublic.RestErrorUtils;
import com.mkytr.enpublic.RestfulObjects.User;
import com.mkytr.enpublic.RestfulObjects.UserSignup;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity implements Callback<User> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        String checkLoggedIn = getSharedPreferences(MapsActivity.PREF_NAME, MODE_PRIVATE).getString("auth", null);
        if(checkLoggedIn != null){
            // User already logged-in
            finish();
        }

        EditText etMail = findViewById(R.id.etMailSignup);
        etMail.addTextChangedListener(new EmailFieldListener());
        EditText etConfirm = findViewById(R.id.etPasswordConfirmSignup);
        etConfirm.addTextChangedListener(new ConfirmFieldListener());
    }

    public void onSignUpButtonClick(View v){
        EditText etName = findViewById(R.id.etNameSignup);
        EditText etMail = findViewById(R.id.etMailSignup);
        EditText etPassword = findViewById(R.id.etPasswordSignup);

        String name = etName.getText().toString();
        String mail = etMail.getText().toString();
        String password = etPassword.getText().toString();

        if(name.trim().equals("") || mail.trim().equals("") || password.trim().equals("")){
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_LONG).show();
            return;
        }


        UserSignup credentials = new UserSignup(name, mail, password);

        ScrollView svSignUpForm = findViewById(R.id.svSignUpForm);
        ProgressBar pbSignup = findViewById(R.id.pbSignup);
        svSignUpForm.setVisibility(View.GONE);
        pbSignup.setVisibility(View.VISIBLE);

        credentials.registerUser(this);
    }

    /**
     *
     * @param email Email address to be checked
     * @return If email parameters fit with general email address syntax return true
     */
    public boolean validateEmailAddress(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    /**
     *
     * @param pass Original password value
     * @param confirm Confirmation password value
     * @return If two paramateres contain same content return true
     */
    public boolean validateConfirmPassword(String pass, String confirm){
        return pass.contentEquals(confirm);
    }

    @Override
    public void onResponse(Call<User> call, Response<User> response) {
        if(response.isSuccessful()){
            User result = response.body();
            Toast.makeText(SignupActivity.this, "Successfully registered, "+result.getEmail(),Toast.LENGTH_LONG).show();
            finish();
        }else {
            ScrollView svSignUpForm = findViewById(R.id.svSignUpForm);
            ProgressBar pbSignup = findViewById(R.id.pbSignup);
            ApiError error = RestErrorUtils.parseError(response);
            Toast.makeText(SignupActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            svSignUpForm.setVisibility(View.VISIBLE);
            pbSignup.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFailure(Call<User> call, Throwable t) {
        ScrollView svSignUpForm = findViewById(R.id.svSignUpForm);
        ProgressBar pbSignup = findViewById(R.id.pbSignup);
        Toast.makeText(SignupActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        svSignUpForm.setVisibility(View.VISIBLE);
        pbSignup.setVisibility(View.GONE);
    }

    /**
     * Show email validation error during entrance of email address
     * Incorrect values will disable sign up button
     */
    private class EmailFieldListener implements TextWatcher {
        TextView tvEmailError;
        Button bSignUp;
        public EmailFieldListener(){
            tvEmailError = findViewById(R.id.tvEmailError);
            bSignUp = findViewById(R.id.bSignup);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Nothing
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Nothing
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(validateEmailAddress(s.toString())){
                tvEmailError.setVisibility(View.GONE);
                bSignUp.setEnabled(true);
            }
            else{
                tvEmailError.setVisibility(View.VISIBLE);
                bSignUp.setEnabled(false);
            }
        }
    }

    /**
     * Show password confirm error during entrance of values
     * Incorrect values will disable sign up button
     */
    private class ConfirmFieldListener implements TextWatcher {
        TextView tvConfirmationError;
        EditText etPassword;
        Button bSignUp;
        public ConfirmFieldListener(){
            tvConfirmationError = findViewById(R.id.tvConfirmationError);
            etPassword = findViewById(R.id.etPasswordSignup);
            bSignUp = findViewById(R.id.bSignup);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Nothing
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Nothing
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(validateConfirmPassword(etPassword.getText().toString(), s.toString())){
                tvConfirmationError.setVisibility(View.GONE);
                bSignUp.setEnabled(true);
            }
            else{
                tvConfirmationError.setVisibility(View.VISIBLE);
                bSignUp.setEnabled(false);
            }
        }
    }
}
