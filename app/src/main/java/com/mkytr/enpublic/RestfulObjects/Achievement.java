package com.mkytr.enpublic.RestfulObjects;

/**
 * Created by mkyka on 20.12.2017.
 */

public class Achievement {
    private String name;
    private String description;

    public Achievement(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getDescription() { return description; }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return "http://web.itu.edu.tr/yucelmuh/enpublic/img/" + this.name + ".png";
    }

}
