package com.mkytr.enpublic.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TransitionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertTransitions(List<TransitionEntity> transitions);

    @Query("SELECT * FROM Transitions ORDER BY timestamp ASC")
    List<TransitionEntity> getAllTransitions();

    @Query("DELETE FROM Transitions")
    void clearTransitions();
}
