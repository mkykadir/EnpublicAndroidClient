package com.mkytr.enpublic;

/**
 * Created by mkyka on 20.12.2017.
 */

public class Achievement {
    private String _id;
    private String name;

    public Achievement(String _id, String name) {
        this._id = _id;
        this.name = name;
    }

    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }
}
