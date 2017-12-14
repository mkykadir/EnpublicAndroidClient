package com.mkytr.enpublic;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener {
    private GoogleSignInClient mGoogleSignClient;
    private int RC_SIGN_IN = 0;
    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
        Button signout = (Button) findViewById(R.id.sign_out_button);
        signout.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.backend_client_id))
                .requestEmail()
                .build();


        mGoogleSignClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    public void updateUI(GoogleSignInAccount account){
        // account.getEmail()

        TextView mail = (TextView)findViewById(R.id.tvMail);
        SignInButton button = (SignInButton) findViewById(R.id.sign_in_button);
        Button signout = (Button) findViewById(R.id.sign_out_button);

        if(account == null) {
            mail.setVisibility(View.GONE);
            signout.setVisibility(View.GONE);
            button.setVisibility(View.VISIBLE);
            return;
        }
        button.setVisibility(View.GONE);
        signout.setVisibility(View.VISIBLE);
        mail.setVisibility(View.VISIBLE);
        String tokenId = account.getIdToken();
        String email = account.getEmail();
        Log.d("SignIn", email);
        mail.setText(tokenId);
    }

    public void signIn(){
        Log.d("SignIn", "Entered signIn method");
        Intent signinIntent = mGoogleSignClient.getSignInIntent();
        startActivityForResult(signinIntent, RC_SIGN_IN);
    }

    public void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        }catch(ApiException e){
            Log.w("SignIn", "Failed");
            updateUI(null);
        }
    }

    public void signOut(){
        mGoogleSignClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateUI(null);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
        }
    }
}
