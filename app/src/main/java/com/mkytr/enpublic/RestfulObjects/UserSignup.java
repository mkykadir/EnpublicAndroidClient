package com.mkytr.enpublic.RestfulObjects;

import com.mkytr.enpublic.EnpublicApi;
import com.mkytr.enpublic.RestClient;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by MKY on 14.02.2018.
 */

public class UserSignup{
    private String full_name;
    private String email;
    private String password;

    public UserSignup(String full_name, String email, String password) {
        this.full_name = full_name;
        this.email = email;
        this.password = password;
    }


    public String getName() {
        return full_name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void registerUser(Callback<User> callback) {
        EnpublicApi client = RestClient.getInstance().getInterface();
        Call<User> call = client.signupUser(this);
        call.enqueue(callback);
    }
}