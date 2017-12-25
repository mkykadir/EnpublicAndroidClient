package com.mkytr.enpublic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DirectionResultsActivity extends AppCompatActivity {

    private List<List<DirectionStation>> mList;
    private ArrayAdapter<List<DirectionStation>> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction_results);

        final ProgressBar pbDirectionResult = findViewById(R.id.pbDirectionResult);
        final ListView lvDirectionResults = findViewById(R.id.lvDirectionResults);

        lvDirectionResults.setEmptyView(findViewById(R.id.tvEmptyResult));
        lvDirectionResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<DirectionStation> selected = new ArrayList<>(mAdapter.getItem(position));
                Intent showDirection = new Intent();
                showDirection.putParcelableArrayListExtra("direction", selected);
                setResult(Activity.RESULT_OK, showDirection);
                finish();
            }
        });

        Bundle bundle = getIntent().getBundleExtra("bundledata");
        String fromStation = "";
        String toStation = "";
        if(bundle != null){
            fromStation = bundle.getString("fromStation");
            toStation = bundle.getString("toStation");
        }

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(MainActivity.BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        EnpublicApi client = retrofit.create(EnpublicApi.class);
        // TODO: Kullanıcı girişi varsa authentication ekle
        Call<List<List<DirectionStation>>> call = client.getDirections(null, fromStation, toStation);
        call.enqueue(new Callback<List<List<DirectionStation>>>() {
            @Override
            public void onResponse(Call<List<List<DirectionStation>>> call, Response<List<List<DirectionStation>>> response) {
                mList = response.body();
                mAdapter = new DirectionResultAdapter(DirectionResultsActivity.this, R.layout.list_view_direction_result, mList);
                lvDirectionResults.setAdapter(mAdapter);
                pbDirectionResult.setVisibility(View.GONE);
                lvDirectionResults.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<List<List<DirectionStation>>> call, Throwable t) {
                Toast.makeText(DirectionResultsActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public class DirectionResultAdapter extends ArrayAdapter<List<DirectionStation>> {

        public DirectionResultAdapter(@NonNull Context context, int resource, @NonNull List<List<DirectionStation>> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v = convertView;
            if(v==null){
                LayoutInflater inflater = getLayoutInflater();
                v = inflater.inflate(R.layout.list_view_direction_result, null);
            }

            List<DirectionStation> stationList = getItem(position);
            HashMap<String, String> content = new HashMap<>();
            for(DirectionStation station : stationList){
                String stationName = station.getName();
                Log.i("STATION", stationName);
                String wayName = "";
                DirectionLine way = station.getWay();
                if(way != null){
                    wayName = way.getLine();
                    Log.i("STATION", wayName);
                }
                if(!content.containsKey(wayName)){
                    Log.i("STATION", "Doesnt contain");
                    content.put(wayName, stationName);
                }
            }
            String stationsText = "";
            String linesText = "";
            for(HashMap.Entry<String, String> item : content.entrySet()){
                Log.i("STATION", item.getValue() + " " + item.getKey());
                stationsText = stationsText.concat(item.getValue() + " /");
                linesText = linesText.concat(item.getKey() + " /");
            }
            Log.i("STATION", stationsText);
            Log.i("STATION", linesText);
            // TODO: Görünümü düzelt!
            //stationsText = stationsText.substring(0, stationsText.length() - 2);
            //linesText = linesText.substring(0, linesText.length() - 2);

            TextView tvStops = v.findViewById(R.id.tvStops);
            tvStops.setText(stationsText);
            TextView tvLines = v.findViewById(R.id.tvLines);
            tvLines.setText(linesText);
            return v;
        }
    }
}
