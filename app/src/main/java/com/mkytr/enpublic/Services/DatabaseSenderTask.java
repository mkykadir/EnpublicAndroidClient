package com.mkytr.enpublic.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.mkytr.enpublic.Database.DatabaseSingleton;
import com.mkytr.enpublic.Database.LocationEntity;
import com.mkytr.enpublic.Database.TransitionEntity;
import com.mkytr.enpublic.POSTResult;
import com.mkytr.enpublic.RestClient;
import com.mkytr.enpublic.RestfulObjects.ActivityDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatabaseSenderTask extends AsyncTask<Void, Void, Void> {
    private Context context;

    private DatabaseSingleton dbSingleton;
    private RestClient client;
    private SharedPreferences preferences;

    public DatabaseSenderTask(Context context, SharedPreferences preferences) {
        this.context = context;
        this.dbSingleton = DatabaseSingleton.getInstance(context);
        this.client = RestClient.getInstance();
        this.preferences = preferences;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ActivityDetails activity = new ActivityDetails(getTransitions(), getLocations());
        String auth = preferences.getString("auth", "");
        Call<POSTResult> result = client.getInterface().sendActivities(auth, activity);
        result.enqueue(new Callback<POSTResult>() {
            @Override
            public void onResponse(Call<POSTResult> call, Response<POSTResult> response) {
                // TODO sent location data in back
                if(response.isSuccessful()) {

                    // clearing existing data
                    dbSingleton.getLocationsTable().clearLocations();
                    dbSingleton.getTransitionsTable().clearTransitions();
                }
            }

            @Override
            public void onFailure(Call<POSTResult> call, Throwable t) {
                // on failure
            }
        });
        return null;
    }

    private List<LocationEntity> getLocations() {
        return dbSingleton.getLocationsTable().getAllLocations();
    }

    private List<TransitionEntity> getTransitions() {
        return dbSingleton.getTransitionsTable().getAllTransitions();
    }
}
