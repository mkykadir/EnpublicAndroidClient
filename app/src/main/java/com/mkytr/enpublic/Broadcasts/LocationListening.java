package com.mkytr.enpublic.Broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.LocationResult;
import com.mkytr.enpublic.Database.LocationEntity;
import com.mkytr.enpublic.Database.Tasks.SaveLocations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LocationListening extends BroadcastReceiver {
    public static final String ACTION_PROCESS_UPDATES = "com.mkytr.enpublic.action" + ".PROCESS_UPDATES";

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();
        if(intent != null) {
            if(ACTION_PROCESS_UPDATES.equals(intent.getAction())) {
                if(LocationResult.hasResult(intent)) {
                    LocationResult result = LocationResult.extractResult(intent);

                    if(result != null) {
                        List<Location> locations = result.getLocations();
                        List<LocationEntity> locationList = new ArrayList<>();
                        for (Location location : locations) {
                            Date locationDate = new Date(location.getTime());
                            calendar.setTime(locationDate);
                            int hour = calendar.get(Calendar.HOUR_OF_DAY);

                            if (hour >= 23 || hour <= 5) // Dont want late night locations
                                continue;

                            if(Float.compare(location.getAccuracy(), 80) > 0) // Dont want inaccurate locations
                                continue;

                            locationList.add(new LocationEntity(location));
                        }

                        SaveLocations saveTask = new SaveLocations(context, locationList);
                        saveTask.execute();
                    }
                }
            }
        }
    }
}
