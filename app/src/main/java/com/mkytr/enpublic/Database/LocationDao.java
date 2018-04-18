package com.mkytr.enpublic.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertLocations(List<LocationEntity> locations);

    @Query("SELECT * FROM Locations ORDER BY timestamp ASC")
    List<LocationEntity> getAllLocations();

    @Query("DELETE FROM Locations")
    void clearLocations();
}
