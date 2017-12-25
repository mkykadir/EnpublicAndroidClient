package com.mkytr.enpublic;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mkyka on 24.12.2017.
 */

public class DirectionStation implements Parcelable {
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

    protected DirectionStation(Parcel in){
        this.name = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.way = in.readParcelable(DirectionLine.class.getClassLoader());
    }

    public static final Creator<DirectionStation> CREATOR = new Creator<DirectionStation>() {
        @Override
        public DirectionStation createFromParcel(Parcel source) {
            return new DirectionStation(source);
        }

        @Override
        public DirectionStation[] newArray(int size) {
            return new DirectionStation[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeParcelable(way, 0);
    }
}

class DirectionLine implements Parcelable{
    private String color;
    private String line;

    public DirectionLine(String color, String line) {
        this.color = color;
        this.line = line;
    }

    protected DirectionLine(Parcel in){
        this.color = in.readString();
        this.line = in.readString();
    }

    public static final Creator<DirectionLine> CREATOR = new Creator<DirectionLine>() {
        @Override
        public DirectionLine createFromParcel(Parcel source) {
            return new DirectionLine(source);
        }

        @Override
        public DirectionLine[] newArray(int size) {
            return new DirectionLine[size];
        }
    };

    public String getColor() {
        return color;
    }

    public String getLine() {
        return line;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.color);
        dest.writeString(this.line);
    }
}
