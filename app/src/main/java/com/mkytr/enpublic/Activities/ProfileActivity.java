package com.mkytr.enpublic.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mkytr.enpublic.ApiError;
import com.mkytr.enpublic.RestClient;
import com.mkytr.enpublic.RestErrorUtils;
import com.mkytr.enpublic.RestfulObjects.Achievement;
import com.mkytr.enpublic.EnpublicApi;
import com.mkytr.enpublic.R;
import com.mkytr.enpublic.RestfulObjects.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements Callback<User> {

    private ArrayList<Achievement> mList;
    private ArrayAdapter<Achievement> mAdapter;

    private User profile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        GridView achievementGrid = findViewById(R.id.gvAchivements);
        RelativeLayout llProfileInfo = findViewById(R.id.rlProfileInfo);
        ProgressBar pbProfile = findViewById(R.id.pbProfile);
        TextView tvUserName = findViewById(R.id.tvUserName);
        TextView tvEmail = findViewById(R.id.tvEmail);

        TextView gridEmpty = new TextView(this);
        gridEmpty.setVisibility(View.GONE);
        gridEmpty.setText(getResources().getString(R.string.no_achievements));

        mList = new ArrayList<>();
        mAdapter = new AchievementsGridAdapter(this, R.layout.grid_view_achievement, mList);
        achievementGrid.setAdapter(mAdapter);
        achievementGrid.setEmptyView(gridEmpty);


        // After log-in we should get profile information
        profile = getIntent().getParcelableExtra("profile-info");

        SharedPreferences preferences = getSharedPreferences(MapsActivity.PREF_NAME ,MODE_PRIVATE);
        final String auth = preferences.getString("auth", null);
        if(auth == null){ // If user not logged in!
            Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if (profile == null){
            User.loginUser(auth, this);
        }else{
            tvUserName.setText(profile.getName());
            tvEmail.setText(profile.getEmail());
        }
        pbProfile.setVisibility(View.GONE);
        llProfileInfo.setVisibility(View.VISIBLE);
        achievementGrid.setVisibility(View.VISIBLE);

        achievementGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Achievement achievement = (Achievement) parent.getAdapter().getItem(position);
                ImageView iAchievement = view.findViewById(R.id.iAchievement);
                Bitmap image = ((BitmapDrawable) iAchievement.getDrawable()).getBitmap();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, bos);
                byte[] bitmapArray = bos.toByteArray();

                DialogFragment achievementDialog = new AchievementDetailsDialog();

                Bundle args = new Bundle();
                args.putString("detail", achievement.getDescription());
                args.putByteArray("image", bitmapArray);

                achievementDialog.setArguments(args);
                achievementDialog.show(getSupportFragmentManager(), achievement.getName());
            }
        });

        User.getAchievements(auth, new Callback<List<Achievement>>() {
            @Override
            public void onResponse(Call<List<Achievement>> call, Response<List<Achievement>> response) {
                if(response.isSuccessful()) {
                    List<Achievement> achievements = response.body();
                    if(achievements != null){
                        mAdapter.clear();
                        mAdapter.addAll(achievements);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Achievement>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent mapsintent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(mapsintent);
        finish();
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
                SharedPreferences.Editor prefEdit = getSharedPreferences(MapsActivity.PREF_NAME, MODE_PRIVATE).edit();
                prefEdit.remove("auth");
                prefEdit.apply();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(Call<User> call, Response<User> response) {
        if(response.isSuccessful()){
            profile = response.body();
            TextView tvUserName = findViewById(R.id.tvUserName);
            TextView tvEmail = findViewById(R.id.tvEmail);
            tvUserName.setText(profile.getName());
            tvEmail.setText(profile.getEmail());
        }else{
            ApiError error = RestErrorUtils.parseError(response);
            Toast.makeText(ProfileActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(Call<User> call, Throwable t) {
        Toast.makeText(ProfileActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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
                TextView tvAchievementName = v.findViewById(R.id.tvAchievementName);
                ImageView iAchievement = v.findViewById(R.id.iAchievement);
                new DownloadAchievementImage(iAchievement).execute(achievement.getImageUrl());
                tvAchievementName.setText(achievement.getDescription());
            }

            return v;
        }
    }

    class DownloadAchievementImage extends AsyncTask<String, Void, Bitmap> {
        private ImageView iv;

        private DownloadAchievementImage(ImageView achievementView) {
            iv = achievementView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String imageUrl = urls[0];
            Bitmap image = null;

            try {
                InputStream in = new URL(imageUrl).openStream();
                image = BitmapFactory.decodeStream(in);
            } catch (MalformedURLException e){
                Log.e(MapsActivity.DEBUG_TAG, "URL malformed");
            } catch (IOException e) {
                Log.e(MapsActivity.DEBUG_TAG, "Cannot download image");
            }

            return image;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            iv.setImageBitmap(bitmap);
        }
    }
}
