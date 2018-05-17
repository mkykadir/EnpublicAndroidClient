package com.mkytr.enpublic.RestfulObjects;

import android.graphics.Color;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mkytr.enpublic.EnpublicApi;
import com.mkytr.enpublic.RestClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class DirectionStation extends Station{

    @Nullable
    private Vehicle next;

    public DirectionStation(String shortn, String name, double latitude, double longitude, @Nullable Vehicle next) {
        super(shortn, name, latitude, longitude);
        this.next = next;
    }

    @Nullable
    public Vehicle getNext() {
        return next;
    }

    public static void getDirections(String auth, String fromShort, String toShort,
                                     Callback<List<List<DirectionStation>>> callback) {
        EnpublicApi client = RestClient.getInstance().getInterface();
        Call<List<List<DirectionStation>>> call = client.getDirection(auth, fromShort, toShort);
        call.enqueue(callback);
    }

    public static List<Polyline> showResultOnGoogleMap(GoogleMap mMap, List<DirectionStation> result) {
        Station.showStationsOnGoogleMap(mMap, result);
        List<Polyline> poly_lines = new ArrayList<>();
        int i = 0;
        while (i < result.size()) {
            DirectionStation station = result.get(i);
            Vehicle vehicle = station.getNext();
            if(vehicle != null) {
                if (i + 1 < result.size()) {
                    DirectionStation nextStation = result.get(i + 1);
                    Polyline line = mMap.addPolyline(new PolylineOptions()
                            .add(new LatLng(station.getLatitude(), station.getLongitude()),
                                    new LatLng(nextStation.getLatitude(), nextStation.getLongitude()))
                            .color(Color.parseColor("#" + vehicle.getColor()))
                            .width(8));
                    poly_lines.add(line);
                }
            }
            i++;
        }
        return poly_lines;
    }
}
