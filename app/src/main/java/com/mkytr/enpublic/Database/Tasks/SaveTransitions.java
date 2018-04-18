package com.mkytr.enpublic.Database.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mkytr.enpublic.Activities.MapsActivity;
import com.mkytr.enpublic.Database.DatabaseSingleton;
import com.mkytr.enpublic.Database.TransitionEntity;

import java.util.List;

public class SaveTransitions extends AsyncTask<Void, Void, Void> {
    private Context context;
    private List<TransitionEntity> transitions;

    public SaveTransitions(Context context, List<TransitionEntity> transitions) {
        this.context = context;
        this.transitions = transitions;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(MapsActivity.DEBUG_TAG, "Saving transitions");
        DatabaseSingleton.getInstance(context).getTransitionsTable().insertTransitions(transitions);
        return null;
    }
}
