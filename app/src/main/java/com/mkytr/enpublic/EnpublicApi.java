package com.mkytr.enpublic;


import android.support.annotation.NonNull;
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
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface EnpublicApi {
    // @Header("Authorization") String basicAuth,

    // TODO fix those according to the latest API changes

    @GET("api/station")
    Call<List<Station>> getStationsByName(@NonNull @Header("Authorization") String basicAuth,
                                    @NonNull @Query("name") String stationName);

    @GET("api/station")
    Call<List<Station>> getStationsByLocation(@NonNull @Header("Authorization") String basicAuth,
                                              @NonNull @QueryMap Map<String, String> locationFilters);

    @GET("api/station/direct")
    Call<List<List<DirectionStation>>> getDirection(@NonNull @Header("Authorization") String basicAuth,
                                             @Query("from") String fromStation,
                                             @Query("to") String toStation);

    @POST("api/signup")
    Call<User> signupUser(@Body UserSignup signup);

    @GET("api/profile")
    Call<User> userProfile(@Header("Authorization") String basicAuth);

    @GET("api/achievement")
    Call<List<Achievement>> getUserAchievements(@Header("Authorization") String basicAuth);

    @POST("api/profile/activity")
    Call<POSTResult> sendActivities(@Header("Authorization") String basicAuth,
                                    @Body ActivityDetails activity);
}
