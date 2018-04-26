package com.mkytr.enpublic;


import android.support.annotation.Nullable;

import com.mkytr.enpublic.RestfulObjects.Achievement;
import com.mkytr.enpublic.RestfulObjects.ActivityDetails;
import com.mkytr.enpublic.RestfulObjects.DirectionStation;
import com.mkytr.enpublic.RestfulObjects.Station;
import com.mkytr.enpublic.RestfulObjects.User;
import com.mkytr.enpublic.RestfulObjects.UserSignup;

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

    // TODO fix those according to the latest API changes

    @GET("station/nearby")
    Call<List<Station>> nearbyStations(@QueryMap Map<String, String> filters);

    @GET("station/search")
    Call<List<Station>> searcyStationsByName(@Nullable @Header("Authorization") String basicAuth, @Query("name") String name);

    @GET("station/direct")
    Call<List<List<DirectionStation>>> getDirections(@Nullable @Header("Authorization") String basicAuth, @Query("from") String fromStation, @Query("to") String toStation);

    @POST("api/signup")
    Call<User> signupUser(@Body UserSignup signup);

    @GET("login")
    Call<POSTResult> loginUser(@Header("Authorization") String basicAuth);

    @GET("api/profile")
    Call<User> userProfile(@Header("Authorization") String basicAuth);

    @GET("achievement/{id}")
    Call<Achievement> getAchievement(@Header("Authorization") String basicAuth, @Path("id") String id);

    @POST("api/profile/activity")
    Call<POSTResult> sendActivities(@Header("Authorization") String basicAuth, @Body ActivityDetails activity);
}
