package com.mkytr.enpublic.RestfulObjects;

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


