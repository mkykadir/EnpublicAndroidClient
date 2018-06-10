package com.mkytr.enpublic.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.mkytr.enpublic.Activities.MapsActivity;
import com.mkytr.enpublic.Activities.ProfileActivity;
import com.mkytr.enpublic.Database.DatabaseSingleton;
import com.mkytr.enpublic.Database.LocationEntity;
import com.mkytr.enpublic.Database.Tasks.DatabaseClearTask;
import com.mkytr.enpublic.Database.TransitionEntity;
import com.mkytr.enpublic.POSTResult;
import com.mkytr.enpublic.R;
import com.mkytr.enpublic.RestClient;
import com.mkytr.enpublic.RestfulObjects.Achievement;
import com.mkytr.enpublic.RestfulObjects.ActivityDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatabaseSenderTask extends AsyncTask<Void, Void, Void> {
    private Context context;

    public static final String NOTIFICATION_CHANNEL = "activity";

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
        Call<List<Achievement>> result = client.getInterface().sendActivities(auth, "application/json", activity);
        result.enqueue(new Callback<List<Achievement>>() {
            @Override
            public void onResponse(Call<List<Achievement>> call, Response<List<Achievement>> response) {
                int resultSize = -1;
                if(response.isSuccessful()) {
                    // clearing existing data
                    DatabaseClearTask clearTask = new DatabaseClearTask(dbSingleton);
                    clearTask.execute();

                    List<Achievement> result = response.body();
                    int resultLength = 0;
                    if (result != null)
                            resultLength = result.size();

                    resultSize = resultLength;
                }
                String notificationContent;
                switch (resultSize) {
                    case -1:
                        notificationContent = "";
                        // Dont show notification
                        return;
                    case 0:
                        notificationContent = context.getResources().getString(R.string.no_gained_achievements);
                        break;
                    default:
                        notificationContent = String.format(context.getResources().
                                getString(R.string.activity_notification_body), resultSize);
                        break;
                }
                showNotification(notificationContent);
            }

            @Override
            public void onFailure(Call<List<Achievement>> call, Throwable t) {
                showNotification(t.getLocalizedMessage());
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

    private void showNotification(String content) {
        Intent profileIntent = new Intent(context, ProfileActivity.class);
        profileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, profileIntent, 0);

        String notificationTitle = context.getResources().
                getString(R.string.activity_notification_title);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(notificationTitle)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, nBuilder.build());
    }
}
