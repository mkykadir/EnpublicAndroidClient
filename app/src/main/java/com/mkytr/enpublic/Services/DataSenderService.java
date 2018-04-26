package com.mkytr.enpublic.Services;

import android.content.SharedPreferences;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.mkytr.enpublic.MainActivity;

public class DataSenderService extends JobService {


    @Override
    public boolean onStartJob(JobParameters job) {
        SharedPreferences preferences = getSharedPreferences(MainActivity.PREF_NAME ,MODE_PRIVATE);
        DatabaseSenderTask task = new DatabaseSenderTask(getApplicationContext(), preferences);
        task.execute();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
