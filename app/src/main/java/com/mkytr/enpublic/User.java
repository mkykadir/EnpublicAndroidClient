package com.mkytr.enpublic;

import java.util.List;

/**
 * Created by mkytr on 15.12.2017.
 */

public class User {
    private String _id;
    private String email;
    private String name;
    private List<String> achievement;

    public User(String _id, String email, String name, List<String> achievement) {
        this._id = _id;
        this.email = email;
        this.name = name;
        this.achievement = achievement;
    }

    public String get_id() {
        return _id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public List<String> getAchievement() {
        return achievement;
    }
}

class UserSignup{
    private String username;
    private String name;
    private String email;
    private String password;

    public UserSignup(String username, String name, String email, String password) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
