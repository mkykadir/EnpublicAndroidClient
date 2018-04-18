package com.mkytr.enpublic.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.location.Location;
import android.support.annotation.NonNull;

@Entity (tableName = "Locations")
public class LocationEntity {
    @PrimaryKey (autoGenerate = true)
    public int id;

    @NonNull
    public double latitude;

    @NonNull
    public double longitude;

    @NonNull
    public long timestamp;

    public float speed;

    public float accuracy;

    public LocationEntity(int id, @NonNull double latitude, @NonNull double longitude, @NonNull long timestamp, float speed, float accuracy) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.speed = speed;
        this.accuracy = accuracy;
    }

    @Ignore
    public LocationEntity(Location getLocation) {
        this.latitude = getLocation.getLatitude();
        this.longitude = getLocation.getLongitude();
        this.timestamp = getLocation.getTime();
        this.speed = getLocation.getSpeed();
        this.accuracy = getLocation.getAccuracy();
    }
}
