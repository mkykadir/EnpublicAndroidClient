package com.mkytr.enpublic.Database.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mkytr.enpublic.Activities.MapsActivity;
import com.mkytr.enpublic.Database.DatabaseSingleton;
import com.mkytr.enpublic.Database.LocationEntity;

import java.util.List;

public class SaveLocations extends AsyncTask<Void, Void, Void> {
    private Context context;
    private List<LocationEntity> locations;

    public SaveLocations(Context context, List<LocationEntity> locations) {
        this.context = context;
        this.locations = locations;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(MapsActivity.DEBUG_TAG, "Saving locations");
        DatabaseSingleton.getInstance(context).getLocationsTable().insertLocations(locations);
        return null;
    }
}
