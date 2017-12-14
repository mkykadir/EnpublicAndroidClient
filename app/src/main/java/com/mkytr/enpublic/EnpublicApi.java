package com.mkytr.enpublic;


import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface EnpublicApi {
    @GET("station/nearby")
    Call<List<Station>> nearbyStations(@QueryMap Map<String, String> filters);

    @GET("station/search")
    Call<List<Station>> searcyStationsByName(@Query("name") String name);
}
