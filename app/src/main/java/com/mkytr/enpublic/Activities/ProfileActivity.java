package com.mkytr.enpublic.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mkytr.enpublic.RestfulObjects.Achievement;
import com.mkytr.enpublic.EnpublicApi;
import com.mkytr.enpublic.MainActivity;
import com.mkytr.enpublic.R;
import com.mkytr.enpublic.RestfulObjects.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends AppCompatActivity {

    private ArrayList<Achievement> mList;
    private ArrayAdapter<Achievement> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final GridView achievementGrid = findViewById(R.id.gvAchivements);
        final RelativeLayout llProfileInfo = findViewById(R.id.rlProfileInfo);
        final ProgressBar pbProfile = findViewById(R.id.pbProfile);
        TextView gridEmpty = new TextView(this);
        gridEmpty.setVisibility(View.GONE);
        gridEmpty.setText("No achievements found"); // TODO: Make it on string.xml
        mList = new ArrayList<>();
        mAdapter = new AchievementsGridAdapter(this, R.layout.grid_view_achievement, mList);
        achievementGrid.setAdapter(mAdapter);
        achievementGrid.setEmptyView(gridEmpty);

        SharedPreferences preferences = getSharedPreferences(MainActivity.PREF_NAME ,MODE_PRIVATE);
        final String auth = preferences.getString("auth", null);
        if(auth == null){
            Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        final EnpublicApi client;

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(MainActivity.BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        client = retrofit.create(EnpublicApi.class);

        final TextView tvUserName = findViewById(R.id.tvUserName);
        final TextView tvUsername = findViewById(R.id.tvUsername);

        Call<User> call = client.userProfile(auth);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User result = response.body();
                tvUserName.setText(result.getName());
                tvUsername.setText(result.get_id());

                for (String achievementId : result.getAchievement()){
                    Call<Achievement> achievementCall = client.getAchievement(auth, achievementId);
                    achievementCall.enqueue(new Callback<Achievement>() {
                        @Override
                        public void onResponse(Call<Achievement> call, Response<Achievement> response) {
                            Achievement result = response.body();
                            mList.add(result); // TODO: Check if works
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<Achievement> call, Throwable t) {

                        }
                    });
                }
                pbProfile.setVisibility(View.GONE);
                llProfileInfo.setVisibility(View.VISIBLE);
                achievementGrid.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.mLogout:
                SharedPreferences.Editor prefEdit = getSharedPreferences(MainActivity.PREF_NAME, MODE_PRIVATE).edit();
                prefEdit.remove("auth");
                prefEdit.apply();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    class AchievementsGridAdapter extends ArrayAdapter<Achievement> {
        public AchievementsGridAdapter(@NonNull Context context, int resource, @NonNull List<Achievement> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v = convertView;
            if(v == null){
                LayoutInflater inflater = getLayoutInflater();
                v = inflater.inflate(R.layout.grid_view_achievement, null);
            }

            Achievement achievement = getItem(position);

            if(achievement != null){
                TextView tvAchievementName = (TextView) v.findViewById(R.id.tvAchievementName);
                // TODO: Set achievement image!
                tvAchievementName.setText(achievement.getName());
            }

            return v;
        }
    }
}