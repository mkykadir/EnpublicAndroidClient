package com.mkytr.enpublic.RestfulObjects;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mkytr.enpublic.EnpublicApi;
import com.mkytr.enpublic.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class Station implements Parcelable{
    private String shortn;
    private String name;
    private double latitude;
    private double longitude;

    public Station(String shortn, String name, double latitude, double longitude) {
        this.shortn = shortn;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Station(Parcel in){
        this.shortn = in.readString();
        this.name = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();

    }

    public static List<Marker> showStationsOnGoogleMap(GoogleMap mMap, @Nullable List<? extends Station> stations) {
        if(stations == null)
            return null;

        mMap.clear();
        List<Marker> addedMarkers = new ArrayList<>();
        for(Station station : stations) {
            LatLng markerPosition = new LatLng(station.getLatitude(),
                    station.getLongitude());
            Marker added = mMap.addMarker(new MarkerOptions().position(markerPosition)
                    .title(station.getName()));
            addedMarkers.add(added);
            added.setTag(station);
        }
        return addedMarkers;
    }

    public static void nearbyStations(String auth, LatLng location, Callback<List<Station>> callback) {
        EnpublicApi client = RestClient.getInstance().getInterface();
        Map<String, String> params = new HashMap<>();
        params.put("lat", String.valueOf(location.latitude));
        params.put("lon", String.valueOf(location.longitude));

        Call<List<Station>> call = client.getStationsByLocation(auth, params);
        call.enqueue(callback);
    }

    public static void searchStations(String auth, String name, Callback<List<Station>> callback) {
        EnpublicApi client = RestClient.getInstance().getInterface();
        Call<List<Station>> call = client.getStationsByName(auth, name);
        call.enqueue(callback);
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

    public String getShortn() {
        return shortn;
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

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.shortn);
        dest.writeString(this.name);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }

    public int describeContents(){
        return 0;
    }
}