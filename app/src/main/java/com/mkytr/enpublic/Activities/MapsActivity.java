package com.mkytr.enpublic.Activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.mkytr.enpublic.Broadcasts.LocationListening;
import com.mkytr.enpublic.Broadcasts.TransitionListening;
import com.mkytr.enpublic.EnpublicApi;
import com.mkytr.enpublic.R;
import com.mkytr.enpublic.RestClient;
import com.mkytr.enpublic.RestfulObjects.DirectionStation;
import com.mkytr.enpublic.RestfulObjects.Station;
import com.mkytr.enpublic.RestfulObjects.Vehicle;
import com.mkytr.enpublic.Services.DataSenderService;
import com.mkytr.enpublic.Services.DatabaseSenderTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {


    private GoogleMap mMap;

    public static final String PREF_NAME = "enpublic_prefs";
    private static int REQUEST_LOCATION = 0;

    private String authString = "none";
    private FusedLocationProviderClient locationClient;
    private SearchListAdapter slAdapter = null;
    private DirectionListAdapter dlAdapter = null;

    public static final String DEBUG_TAG = "ENPUBLIC_DBG";

    private Station selected = null;
    private Station from = null;
    private Station to = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Request required permissions
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }

        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new SearchTextListener());

        ListView lvStationSearch = findViewById(R.id.lvStationSearch);
        lvStationSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Station station = (Station) parent.getAdapter().getItem(position);
                showStationInformation(station, true);
            }
        });

        ListView lvDirectionSearch = findViewById(R.id.lvDirectionSearch);
        lvDirectionSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<DirectionStation> result = (List<DirectionStation>) parent.getAdapter().getItem(position);

                bottomLayoutExpand(false);
                directionResultVisibility(false);
                directionInformationVisibility(true);

                TextView tvDirectionInformationStations = findViewById(R.id.tvDirectionInformationStations);
                TextView tvDirectionInformationVehicles = findViewById(R.id.tvDirectionInformationVehicles);
                TextView tvDirectionStations = view.findViewById(R.id.tvDirectionStations);
                TextView tvDirectionVehicles = view.findViewById(R.id.tvDirectionVehicles);

                tvDirectionInformationStations.setText(tvDirectionStations.getText());
                tvDirectionInformationVehicles.setText(tvDirectionVehicles.getText());

                DirectionStation.showResultOnGoogleMap(mMap, result);
            }
        });

        locationClient = LocationServices
                .getFusedLocationProviderClient(getApplicationContext());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        authString = getSharedPreferences(MapsActivity.PREF_NAME, MODE_PRIVATE)
                .getString("auth", "none");

        if(!authString.contentEquals("none")) {
            // User logged in start services and JobDispatcher
            startBackgroundTasks();
        }else{
            Intent intent = new Intent(this, SigninActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        authString = getSharedPreferences(MapsActivity.PREF_NAME, MODE_PRIVATE)
                .getString("auth", "none");

        if(authString.contentEquals("none")){
            Intent intent = new Intent(this, SigninActivity.class);;
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        LinearLayout llBottomLayout = findViewById(R.id.llBottomLayout);
        ViewGroup.LayoutParams params = llBottomLayout.getLayoutParams();
        if(params.height == ViewGroup.LayoutParams.MATCH_PARENT) {
            bottomLayoutExpand(false);
            stationSearchVisibility(false);
            directionResultVisibility(false);
        }else{
            // stationInformationVisibility(false);
            super.onBackPressed();
        }
    }

    private void startBackgroundTasks() {
        startTransitionListening();
        startLocationListening();
        startDataSenderJob();
    }

    private void startDataSenderJob() {
        FirebaseJobDispatcher dataSender = new FirebaseJobDispatcher(
                new GooglePlayDriver(getApplicationContext()));

        Job dataJob = dataSender.newJobBuilder()
                .setService(DataSenderService.class)
                .setTag("enpublicdatasender")
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(28800, 32400))
                .setReplaceCurrent(false)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setConstraints(Constraint.ON_UNMETERED_NETWORK)
                .build();

        dataSender.mustSchedule(dataJob);
        // Toast.makeText(this, "Data sender job has just started!", Toast.LENGTH_LONG).show();
    }

    private void startTransitionListening() {
        // Start TransitionListening Broadcast
        List<ActivityTransition> transitions = new ArrayList<>();
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());

        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());

        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_FOOT)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());

        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_FOOT)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());


        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());

        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());

        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());

        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());


        // Build required things to connect Activity Transition API
        Intent activityTransitionIntent = new Intent(this, TransitionListening.class);
        activityTransitionIntent.setAction(TransitionListening.ACTION_TRANSITION);
        PendingIntent transitionService = PendingIntent.getBroadcast(this, 26,
                activityTransitionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        ActivityRecognitionClient transitionClient = ActivityRecognition.getClient(getApplicationContext());
        ActivityTransitionRequest transitionRequest = new ActivityTransitionRequest(transitions);
        Task<Void> task = transitionClient.requestActivityTransitionUpdates(transitionRequest, transitionService);

        // For DEBUG purposes
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(DEBUG_TAG, "Connected to the transition API");
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(DEBUG_TAG, "Cannot connected to the transition API");
                Log.e(DEBUG_TAG, e.getLocalizedMessage());
            }
        });
    }

    private void startLocationListening() {
        // Start LocationListening Broadcast
        Intent locationServiceIntent = new Intent(this, LocationListening.class);
        locationServiceIntent.setAction(LocationListening.ACTION_PROCESS_UPDATES);
        PendingIntent locationService = PendingIntent.getBroadcast(this, 0,
                locationServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        LocationRequest request = LocationRequest.create().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setFastestInterval(300000).setInterval(1800000).setSmallestDisplacement(150); // 5 minutes to 30 minutes

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationClient.requestLocationUpdates(request, locationService);
        }else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_maps, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.mProfile:
                Intent profileActivityIntent = new Intent(this, ProfileActivity.class);
                startActivity(profileActivityIntent);
                break;
            /*case R.id.mDataSend:
                SharedPreferences preferences = getSharedPreferences(MapsActivity.PREF_NAME ,MODE_PRIVATE);
                DatabaseSenderTask task = new DatabaseSenderTask(getApplicationContext(), preferences);
                task.execute();
                break;
            */
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        try {
            mMap.setMyLocationEnabled(true);
            locationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null)
                        getNearbyStations(new LatLng(location.getLatitude(), location.getLongitude()));
                }
            });
        }catch (SecurityException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 14f));
        Station selectedStation = (Station) marker.getTag();
        showStationInformation(selectedStation, false);
        return true;
    }

    protected void getNearbyStations(LatLng location) {
        if(authString.equals("none"))
            return; // Non-authorized user

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14f));
        Station.nearbyStations(authString, location, new Callback<List<Station>>() {
            @Override
            public void onResponse(Call<List<Station>> call, Response<List<Station>> response) {
                if(response.isSuccessful()) {
                    List<Station> stations = response.body();
                    Station.showStationsOnGoogleMap(mMap, stations);
                }
            }

            @Override
            public void onFailure(Call<List<Station>> call, Throwable t) {
                // Failed to get nearby stations!
            }
        });
    }

    protected void getStationsByName(String name) {
        Station.searchStations(authString, name, new Callback<List<Station>>() {
            @Override
            public void onResponse(Call<List<Station>> call, Response<List<Station>> response) {
                if(response.isSuccessful()) {
                    List<Station> stations = response.body();
                    showSearchResult(stations);
                }else{
                    showSearchResult(null);
                }
            }

            @Override
            public void onFailure(Call<List<Station>> call, Throwable t) {
                showSearchResult(null);
            }
        });
    }

    protected void showSearchResult(List<Station> stations) {
        bottomLayoutExpand(true);
        ListView lvStationSearch = findViewById(R.id.lvStationSearch);

        if(stations != null) {
            stationInformationVisibility(false);
            directionResultVisibility(false);
            directionInformationVisibility(false);
            stationSearchVisibility(true);
            if(slAdapter == null){
                slAdapter = new SearchListAdapter(this,
                        android.R.layout.simple_list_item_1, stations);
                lvStationSearch.setAdapter(slAdapter);
            }else{
                slAdapter.clear();
                slAdapter.addAll(stations);
            }
        }
    }

    protected void showDirectionResult(List<List<DirectionStation>> result) {
        bottomLayoutExpand(true);
        ListView lvDirectionSearch = findViewById(R.id.lvDirectionSearch);

        if(result != null) {
            stationInformationVisibility(false);
            stationSearchVisibility(false);
            directionInformationVisibility(false);
            directionResultVisibility(true);
            if(dlAdapter == null) {
                dlAdapter = new DirectionListAdapter(this, R.layout.list_direction_view,
                        result);
                lvDirectionSearch.setAdapter(dlAdapter);
            }else{
                dlAdapter.clear();
                dlAdapter.addAll(result);
            }
        }
    }

    private void directionResultVisibility(boolean visible){
        ListView lvDirectionSearch = findViewById(R.id.lvDirectionSearch);
        if(visible)
            lvDirectionSearch.setVisibility(View.VISIBLE);
        else
            lvDirectionSearch.setVisibility(View.GONE);
    }

    private void stationSearchVisibility(boolean visible){
        ListView lvStationSearch = findViewById(R.id.lvStationSearch);
        if(visible)
            lvStationSearch.setVisibility(View.VISIBLE);
        else
            lvStationSearch.setVisibility(View.GONE);
    }

    private void stationInformationVisibility(boolean visible) {
        LinearLayout llStationInformation = findViewById(R.id.llStationInformation);
        if(visible)
            llStationInformation.setVisibility(View.VISIBLE);
        else
            llStationInformation.setVisibility(View.GONE);
    }

    private void directionInformationVisibility(boolean visible) {
        LinearLayout llDirectionInformarion = findViewById(R.id.llDirectionInformation);
        if(visible)
            llDirectionInformarion.setVisibility(View.VISIBLE);
        else
            llDirectionInformarion.setVisibility(View.GONE);
    }

    protected void bottomLayoutExpand(boolean expand) {
        LinearLayout llBottomLayout = findViewById(R.id.llBottomLayout);

        if (expand){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            llBottomLayout.setLayoutParams(params);
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) llBottomLayout.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            llBottomLayout.setLayoutParams(params);
        }


    }

    protected void showStationInformation(Station station, boolean addMarker) {
        bottomLayoutExpand(false);
        stationSearchVisibility(false);
        directionResultVisibility(false);
        directionInformationVisibility(false);
        stationInformationVisibility(true);

        TextView tvStationInfoName = findViewById(R.id.tvStationInfoName);
        tvStationInfoName.setText(station.getName());

        Button bToStation = findViewById(R.id.bToStation);
        bToStation.setEnabled(from != null);

        selected = station;

        if(addMarker){
            List<Station> stations = new ArrayList<>();
            stations.add(station);
            Station.showStationsOnGoogleMap(mMap, stations);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(station.getLatitude(), station.getLongitude()), 14f));
    }

    public void onFromButtonClick(View v){
        from = selected;
        Button bToStation = findViewById(R.id.bToStation);
        bToStation.setEnabled(from != null);
    }

    public void onToButtonClick(View v){
        to = selected;
        DirectionStation.getDirections(authString, from.getShortn(), to.getShortn(),
                new Callback<List<List<DirectionStation>>>() {
            @Override
            public void onResponse(Call<List<List<DirectionStation>>> call, Response<List<List<DirectionStation>>> response) {
                if(response.isSuccessful())
                    showDirectionResult(response.body());
            }

            @Override
            public void onFailure(Call<List<List<DirectionStation>>> call, Throwable t) {
                showDirectionResult(null);
            }
        });
    }

    class SearchTextListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            getStationsByName(s.toString());
        }
    }

    class SearchListAdapter extends ArrayAdapter<Station> {
        public SearchListAdapter(@NonNull Context context, int resource, @NonNull List<Station> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v = convertView;
            if(v == null) {
                LayoutInflater layoutInflater = getLayoutInflater();
                v = layoutInflater.inflate(android.R.layout.simple_list_item_1, null);
            }

            Station station = getItem(position);
            if(station != null) {
                TextView tvStationName = v.findViewById(android.R.id.text1);
                tvStationName.setText(station.getName());
            }
            return v;
        }
    }

    class DirectionListAdapter extends ArrayAdapter<List<DirectionStation>> {
        public DirectionListAdapter(@NonNull Context context, int resource, @NonNull List<List<DirectionStation>> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater layoutInflater = getLayoutInflater();
                v = layoutInflater.inflate(R.layout.list_direction_view, null);
            }

            List<DirectionStation> result = getItem(position);
            if (result != null) {
                TextView tvDirectionStations = v.findViewById(R.id.tvDirectionStations);
                TextView tvDirectionVehicles = v.findViewById(R.id.tvDirectionVehicles);

                StringBuilder sbStations = new StringBuilder();
                StringBuilder sbVehicles = new StringBuilder();
                int index = 0;
                for(DirectionStation station : result) {
                    sbStations.append(station.getName());
                    sbStations.append("->");
                    Vehicle vehicle = station.getNext();
                    if(vehicle != null) {
                        sbVehicles.append(vehicle.getCode());
                        sbVehicles.append(',');
                    }else if(index < result.size() - 1){
                        sbVehicles.append('W');
                        sbVehicles.append(',');
                    }
                    index++;
                }
                sbStations.deleteCharAt(sbStations.lastIndexOf("-"));
                sbStations.deleteCharAt(sbStations.lastIndexOf(">"));
                sbVehicles.deleteCharAt(sbVehicles.lastIndexOf(","));
                tvDirectionStations.setText(sbStations.toString());
                tvDirectionVehicles.setText(sbVehicles.toString());
            }
            return v;
        }
    }
}
