package com.mkytr.enpublic.Broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.mkytr.enpublic.Activities.MapsActivity;
import com.mkytr.enpublic.Database.Tasks.SaveTransitions;
import com.mkytr.enpublic.Database.TransitionEntity;

import java.util.ArrayList;
import java.util.List;

public class TransitionListening extends BroadcastReceiver {
    public static final String ACTION_TRANSITION = "com.mkytr.enpublic.action" + ".PROCESS_TRANSITION";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null) {
            if(ACTION_TRANSITION.equals(intent.getAction())) {
                if(ActivityTransitionResult.hasResult(intent)) {
                    Log.d(MapsActivity.DEBUG_TAG, "Activity transition broadcast received");

                    ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
                    if(result != null) {
                        List<TransitionEntity> transitionList = new ArrayList<>();
                        for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                            transitionList.add(new TransitionEntity(event));
                        }

                        SaveTransitions saveTask = new SaveTransitions(context, transitionList);
                        saveTask.execute();
                    }
                }
            }
        }

    }
}
