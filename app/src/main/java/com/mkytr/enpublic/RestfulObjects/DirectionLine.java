package com.mkytr.enpublic.RestfulObjects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MKY on 14.02.2018.
 */

public class DirectionLine implements Parcelable {
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
