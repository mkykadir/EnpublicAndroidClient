package com.mkytr.enpublic.RestfulObjects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MKY on 14.02.2018.
 */

public class StationLine implements Parcelable {
    private String name;
    private int order;

    public StationLine(String name, int order) {
        this.name = name;
        this.order = order;
    }

    protected StationLine(Parcel in) {
        name = in.readString();
        order = in.readInt();
    }

    public static final Creator<StationLine> CREATOR = new Creator<StationLine>() {
        @Override
        public StationLine createFromParcel(Parcel in) {
            return new StationLine(in);
        }

        @Override
        public StationLine[] newArray(int size) {
            return new StationLine[size];
        }
    };

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.order);
    }
}