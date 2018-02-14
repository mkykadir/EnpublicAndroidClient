package com.mkytr.enpublic.RestfulObjects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Station implements Parcelable{
    private String name;
    private String type;
    private double[] location;
    private List<StationLine> line;

    public Station(String name, String type, double[] location, List<StationLine> line) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.line = line;
    }

    public Station(Parcel in){
        this.name = in.readString();
        this.type = in.readString();
        this.location = new double[2];
        in.readDoubleArray(this.location);
        this.line = new ArrayList<>();
        in.readList(this.line, StationLine.class.getClassLoader());
    }

    public static final Creator<Station> CREATOR = new Creator<Station>() {
        @Override
        public Station createFromParcel(Parcel in) {
            return new Station(in);
        }

        @Override
        public Station[] newArray(int size) {
            return new Station[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public double[] getLocation() {
        return location;
    }

    public List<StationLine> getLine() {
        return line;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeDoubleArray(this.location);
        dest.writeList(this.line);
    }

    public int describeContents(){
        return 0;
    }
}