package com.mkytr.enpublic;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, View.OnClickListener, GoogleMap.OnMarkerClickListener {
    public static final String BASE_API_URL = "http://enpublic.mkytr.com/";
    public static final String PREF_NAME = "enpublic_prefs";

    public static final int SEARCH_STATION_REQUEST = 1;
    public static final int FROM_STATION_REQUEST = 2;
    public static final int TO_STATION_REQUEST = 3;

    public static final int PERMISSION_LOCATION_REQUEST = 1;

    private GoogleMap mMap;
    private ArrayList<Station> nearbyLocation;
    private FusedLocationProviderClient mFusedLocation;

    private Station fromStation = null;
    private Station toStation = null;
    private Station selectedStation = null;

    // TODO: Map markerlarının görünümünü güncelle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);

        Button bFromStation = findViewById(R.id.bFromStation);
        bFromStation.setOnClickListener(this);
        Button bToStation = findViewById(R.id.bToStation);
        bToStation.setOnClickListener(this);
        EditText etFrom = findViewById(R.id.etFrom);
        etFrom.setOnClickListener(this);
        EditText etTo = findViewById(R.id.etTo);
        etTo.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mSearch:
                Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("data", nearbyLocation);
                searchIntent.putExtras(bundle);
                startActivityForResult(searchIntent, SEARCH_STATION_REQUEST);
                break;
            case R.id.mProfile:
                SharedPreferences credentials = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                String auth = credentials.getString("auth", null);
                Intent profileIntent = null;
                if(auth == null) {
                    profileIntent = new Intent(getApplicationContext(), SigninActivity.class);
                }else{
                    profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                }
                startActivity(profileIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_STATION_REQUEST) {
            if (resultCode == RESULT_OK) {
                mMap.clear(); // Clear all markers
                Station result = data.getParcelableExtra("station");
                LatLng markerPosition = new LatLng(result.getLocation()[0], result.getLocation()[1]);
                Marker added = mMap.addMarker(new MarkerOptions().position(markerPosition).title(result.getName()));
                added.setTag(result);
                onMarkerClick(added);
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 14f));
            }
        }else if(requestCode == FROM_STATION_REQUEST || requestCode == TO_STATION_REQUEST) {
            if (resultCode == RESULT_OK){
                EditText edit = null;
                mMap.clear();
                Station result = data.getParcelableExtra("station");
                if(requestCode == FROM_STATION_REQUEST) {
                    edit = findViewById(R.id.etFrom);
                    fromStation = result;
                }
                else{
                    edit = findViewById(R.id.etTo);
                    toStation = result;
                }


                if(edit != null)
                    edit.setText(result.getName());

                Marker lastAdded = null;

                if(fromStation != null){
                    LatLng markerPosition = new LatLng(fromStation.getLocation()[0], fromStation.getLocation()[1]);
                    Marker added = mMap.addMarker(new MarkerOptions().position(markerPosition).title(fromStation.getName()));
                    added.setTag(fromStation);
                    if(requestCode == FROM_STATION_REQUEST)
                        lastAdded = added;
                }
                if(toStation != null){
                    LatLng markerPosition = new LatLng(toStation.getLocation()[0], toStation.getLocation()[1]);
                    Marker added = mMap.addMarker(new MarkerOptions().position(markerPosition).title(toStation.getName()));
                    added.setTag(toStation);
                    if(requestCode == TO_STATION_REQUEST)
                        lastAdded = added;
                }
                if(lastAdded != null)
                    onMarkerClick(lastAdded);
            }
        }
    }

    public void getNearbyLocations(LatLng location) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        EnpublicApi client = retrofit.create(EnpublicApi.class);
        Map<String, String> data = new HashMap<>();
        data.put("lat", String.valueOf(location.latitude));
        data.put("lon", String.valueOf(location.longitude));
        Call<List<Station>> call = client.nearbyStations(data);
        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14f));
        call.enqueue(new Callback<List<Station>>() {
            @Override
            public void onResponse(Call<List<Station>> call, Response<List<Station>> response) {
                List<Station> result = response.body();
                nearbyLocation = new ArrayList<>();
                for (Station item : result) {
                    nearbyLocation.add(item);
                    LatLng markerPosition = new LatLng(item.getLocation()[0], item.getLocation()[1]);
                    Marker added =mMap.addMarker(new MarkerOptions().position(markerPosition).title(item.getName()));
                    added.setTag(item);
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 20f));
                }
            }

            @Override
            public void onFailure(Call<List<Station>> call, Throwable t) {

                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        mMap.setMyLocationEnabled(true);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "Location permission needed to show your location", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Location information needed", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_LOCATION_REQUEST);
            }
        } else {
            mMap.setMyLocationEnabled(true);
            mFusedLocation.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        getNearbyLocations(new LatLng(location.getLatitude(), location.getLongitude()));
                    }
                }
            });
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Location information needed", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_LOCATION_REQUEST);
            }
        } else {
            // mMap.setMyLocationEnabled(true);

            mFusedLocation.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        getNearbyLocations(new LatLng(location.getLatitude(), location.getLongitude()));
                        LinearLayout infoLayout = findViewById(R.id.stationInfoLayout);
                        if(infoLayout.getVisibility() == View.VISIBLE)
                            infoLayout.setVisibility(View.GONE);

                        EditText etFrom = findViewById(R.id.etFrom);
                        EditText etTo = findViewById(R.id.etTo);
                        etFrom.setText("");
                        etTo.setText("");
                    }
                }
            });
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if(viewId == R.id.etFrom || viewId == R.id.etTo){
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("data", nearbyLocation);
            intent.putExtras(bundle);
            if(viewId == R.id.etFrom)
                startActivityForResult(intent, FROM_STATION_REQUEST);
            else
                startActivityForResult(intent, TO_STATION_REQUEST);
        }else if(viewId == R.id.bFromStation || viewId == R.id.bToStation){
            EditText edit = null;
            if(viewId == R.id.bFromStation){
                Log.d("CHECKINFO", "From button clicked");
                edit = findViewById(R.id.etFrom);
                fromStation = selectedStation;
            }else{
                Log.d("CHECKINFO", "To button clicked");
                edit = findViewById(R.id.etTo);
                toStation = selectedStation;
            }
            //TextView tvInfoName = findViewById(R.id.tvInfoName);
            if(edit != null && selectedStation != null){
                Log.d("CHECKINFO", "Text should be set");
                edit.setText(selectedStation.getName());
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 14f));
        LinearLayout infoLayout = findViewById(R.id.stationInfoLayout);
        infoLayout.setVisibility(View.VISIBLE);
        TextView tvInfoName = findViewById(R.id.tvInfoName);
        tvInfoName.setText(marker.getTitle());
        selectedStation = (Station) marker.getTag();
        return true;
    }
}
