package com.mkytr.enpublic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mkytr.enpublic.RestfulObjects.Station;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity{

    private ArrayList<Station> mList;
    private ArrayAdapter<Station> listAdapter;

    // TODO: Stationların listesi göze daha güzel gözükmeli

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ListView lvSearchResult = findViewById(R.id.lvSearchResult);
        TextView tvEmptyList = findViewById(R.id.tvEmptyList);
        EditText etSearch = findViewById(R.id.etSearch);

        lvSearchResult.setEmptyView(tvEmptyList);
        lvSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Station selected = listAdapter.getItem(position);
                Intent intent = new Intent();
                intent.putExtra("station", selected);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        etSearch.addTextChangedListener(new SearchTextListener());

        Bundle bundle = getIntent().getBundleExtra("bundledata");
        if(bundle != null){
            mList = bundle.getParcelableArrayList("data");
        }

        if(mList == null)
            mList = new ArrayList<>();


        listAdapter = new SearchListAdapter(this, R.layout.list_view_station, mList);
        lvSearchResult.setAdapter(listAdapter);

    }

    class SearchTextListener implements TextWatcher {
        private EnpublicApi client;

        public SearchTextListener() {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(MainActivity.BASE_API_URL)
                    .addConverterFactory(GsonConverterFactory.create());

            Retrofit retrofit = builder.build();
            client = retrofit.create(EnpublicApi.class);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            SharedPreferences preferences = getSharedPreferences(MainActivity.PREF_NAME, MODE_PRIVATE);
            Call<List<Station>> call = client.searcyStationsByName(preferences.getString("auth", null), s.toString());
            call.enqueue(new Callback<List<Station>>() {
                @Override
                public void onResponse(Call<List<Station>> call, Response<List<Station>> response) {
                    mList = new ArrayList<>(response.body());
                    listAdapter.clear();
                    listAdapter.addAll(mList);
                    listAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<List<Station>> call, Throwable t) {
                    Toast.makeText(SearchActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
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
            if(v == null){
                LayoutInflater inflater = getLayoutInflater();
                v = inflater.inflate(R.layout.list_view_station, null);
            }

            Station station = getItem(position);
            if(station != null){
                TextView tvStationName = (TextView) v.findViewById(R.id.tvStationName);
                tvStationName.setText(station.getName());
            }

            return v;
        }
    }
}
