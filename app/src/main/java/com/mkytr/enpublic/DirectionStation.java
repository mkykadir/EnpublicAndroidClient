package com.mkytr.enpublic;

/**
 * Created by mkyka on 24.12.2017.
 */

public class DirectionStation {
    private String name;
    private double latitude;
    private double longitude;
    private DirectionLine way;

    public DirectionStation(String name, double latitude, double longitude, DirectionLine way) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.way = way;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public DirectionLine getWay() {
        return way;
    }
}

class DirectionLine {
    private String color;
    private String line;

    public DirectionLine(String color, String line) {
        this.color = color;
        this.line = line;
    }

    public String getColor() {
        return color;
    }

    public String getLine() {
        return line;
    }
}
