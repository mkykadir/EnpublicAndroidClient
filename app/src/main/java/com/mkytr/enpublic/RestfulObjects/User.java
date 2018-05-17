package com.mkytr.enpublic.RestfulObjects;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import com.mkytr.enpublic.EnpublicApi;
import com.mkytr.enpublic.RestClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by mkytr on 15.12.2017.
 */

public class User implements Parcelable{
    private String email;
    private String name;

    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }


    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }


    public static String getAuthText(String email, String password) {
        String toEncode = email + ":" + password;
        String authContent = Base64.encodeToString(toEncode.getBytes(), Base64.NO_WRAP);
        String authText = "Basic " + authContent;
        return authText;
    }

    public static void loginUser(String authText, Callback<User> callback) {
        EnpublicApi client = RestClient.getInstance().getInterface();
        Call<User> call = client.userProfile(authText);
        call.enqueue(callback);
    }


    // Parcelable requirements

    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in){
            return new User(in);
        }

        public User[] newArray(int size){
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(name);
    }

    private User(Parcel in){
        email = in.readString();
        name = in.readString();
    }
}


