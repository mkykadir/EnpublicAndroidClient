package com.mkytr.enpublic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    public void onSignUpButtonClick(View v){
        boolean validEmail = validateEmailAddress();
        boolean validConfirm = validateConfirmPassword();
        TextView tvEmailError = findViewById(R.id.tvEmailError);
        TextView tvConfirmationError = findViewById(R.id.tvConfirmationError);

        if(!validEmail)
            tvEmailError.setVisibility(View.VISIBLE);
        else
            tvEmailError.setVisibility(View.GONE);

        if(!validConfirm)
            tvConfirmationError.setVisibility(View.VISIBLE);
        else
            tvConfirmationError.setVisibility(View.GONE);

        if(!validEmail || !validConfirm)
            return;

        EditText etName = findViewById(R.id.etNameSignup);
        String name = etName.getText().toString();
        EditText etMail = findViewById(R.id.etMailSignup);
        String mail = etMail.getText().toString();
        EditText etUsername = findViewById(R.id.etUsernameSignup);
        String username = etUsername.getText().toString();
        EditText etPassword = findViewById(R.id.etPasswordSignup);
        String password = etPassword.getText().toString();

        if(name.trim().equals("") || mail.trim().equals("") || username.trim().equals("") || password.trim().equals(""))
            return;

        // call retrofit
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(MainActivity.BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        EnpublicApi client = retrofit.create(EnpublicApi.class);

        UserSignup credentials = new UserSignup(username, name, mail, password);
        final ScrollView svSignUpForm = findViewById(R.id.svSignUpForm);
        final ProgressBar pbSignup = findViewById(R.id.pbSignup);
        svSignUpForm.setVisibility(View.GONE);
        pbSignup.setVisibility(View.VISIBLE);
        Call<POSTResult> call = client.signupUser(credentials);
        call.enqueue(new Callback<POSTResult>() {
            @Override
            public void onResponse(Call<POSTResult> call, Response<POSTResult> response) {
                POSTResult result = response.body();
                if(result.getResult() == 200){
                    Toast.makeText(SignUpActivity.this, "Succesfully registered!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<POSTResult> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                svSignUpForm.setVisibility(View.VISIBLE);
                pbSignup.setVisibility(View.GONE);
            }
        });

    }

    public boolean validateEmailAddress(){
        EditText etMail = findViewById(R.id.etMailSignup);
        String content = etMail.getText().toString();
        return Patterns.EMAIL_ADDRESS.matcher(content).matches();
    }

    public boolean validateConfirmPassword(){
        EditText etPassword = findViewById(R.id.etPasswordSignup);
        EditText etConfirmPassword = findViewById(R.id.etPasswordConfirmSignup);
        return etPassword.getText().toString().contentEquals(etConfirmPassword.getText().toString());
    }
}
