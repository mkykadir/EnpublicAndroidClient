package com.mkytr.enpublic.RestfulObjects;

public class Vehicle {
    private String code;
    private String color;

    public Vehicle(String code, String color) {
        this.code = code;
        this.color = color;
    }

    public String getCode() {
        return code;
    }

    public String getColor() {
        return color;
    }
}
