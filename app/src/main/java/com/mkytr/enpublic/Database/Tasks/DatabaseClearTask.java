package com.mkytr.enpublic.Database.Tasks;

import android.os.AsyncTask;

import com.mkytr.enpublic.Database.DatabaseSingleton;
import com.mkytr.enpublic.Services.DatabaseSenderTask;

public class DatabaseClearTask extends AsyncTask<Void, Void, Void> {
    private DatabaseSingleton dbSingleton;
    public DatabaseClearTask(DatabaseSingleton getInstance) {
        this.dbSingleton = getInstance;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        dbSingleton.getLocationsTable().clearLocations();
        dbSingleton.getTransitionsTable().clearTransitions();
        return null;
    }
}
