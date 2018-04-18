package com.mkytr.enpublic.Database;

import android.arch.persistence.room.Room;
import android.content.Context;

public class DatabaseSingleton {
    private static final String dbName = "EnpublicDataDb";
    private static DatabaseSingleton currentInstance = null;
    private static AppDatabase appDatabase = null;

    public static DatabaseSingleton getInstance(Context getContext) {
        if(currentInstance == null) {
            currentInstance = new DatabaseSingleton(getContext);
        }
        return currentInstance;
    }

    private DatabaseSingleton(Context getContext) {
        appDatabase = Room.databaseBuilder(getContext, AppDatabase.class, dbName).build();
    }

    public AppDatabase getDb() {
        return appDatabase;
    }

    public LocationDao getLocationsTable() {
        if(appDatabase != null)
            return appDatabase.locationDao();
        else
            return null;
    }

    public TransitionDao getTransitionsTable() {
        if(appDatabase != null)
            return appDatabase.transitionDao();
        else
            return null;
    }
}
