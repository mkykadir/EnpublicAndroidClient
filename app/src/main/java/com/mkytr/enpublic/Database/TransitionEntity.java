package com.mkytr.enpublic.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.android.gms.location.ActivityTransitionEvent;

@Entity (tableName = "Transitions")
public class TransitionEntity {
    @PrimaryKey (autoGenerate = true)
    public int id;

    @NonNull
    public int activityType;

    @NonNull
    public int transitionType;

    @NonNull
    public long timestamp;

    public TransitionEntity(int id, @NonNull int activityType, @NonNull int transitionType, @NonNull long timestamp) {
        this.id = id;
        this.activityType = activityType;
        this.transitionType = transitionType;
        this.timestamp = timestamp;
    }

    @Ignore
    public TransitionEntity(ActivityTransitionEvent getEvent) {
        this.activityType = getEvent.getActivityType();
        this.transitionType = getEvent.getTransitionType();
        this.timestamp = System.currentTimeMillis();
    }
}
