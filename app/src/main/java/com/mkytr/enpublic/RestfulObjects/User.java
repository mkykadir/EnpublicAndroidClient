package com.mkytr.enpublic.RestfulObjects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by mkytr on 15.12.2017.
 */

public class User implements Parcelable{
    private String username;
    private String email;
    private String name;

    public User(String username, String email, String name) {
        this.username = username;
        this.email = email;
        this.name = name;
    }

    public String get_username() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
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
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(name);
    }

    private User(Parcel in){
        username = in.readString();
        email = in.readString();
        name = in.readString();
    }
}


