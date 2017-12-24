package com.mkytr.enpublic;


import android.support.annotation.Nullable;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface EnpublicApi {
    // @Header("Authorization") String basicAuth,

    @GET("station/nearby")
    Call<List<Station>> nearbyStations(@QueryMap Map<String, String> filters);

    @GET("station/search")
    Call<List<Station>> searcyStationsByName(@Nullable @Header("Authorization") String basicAuth, @Query("name") String name);

    @GET("station/direct")
    Call<List<List<DirectionStation>>> getDirections(@Nullable @Header("Authorization") String basicAuth, @Query("from") String fromStation, @Query("to") String toStation);

    @POST("signup")
    Call<POSTResult> signupUser(@Body UserSignup signup);

    @GET("login")
    Call<POSTResult> loginUser(@Header("Authorization") String basicAuth);

    @GET("profile")
    Call<User> userProfile(@Header("Authorization") String basicAuth);

    @GET("achievement/{id}")
    Call<Achievement> getAchievement(@Header("Authorization") String basicAuth, @Path("id") String id);
}
