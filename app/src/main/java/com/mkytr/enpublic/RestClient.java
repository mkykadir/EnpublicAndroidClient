package com.mkytr.enpublic;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mkyka on 28.02.2018.
 */

public final class RestClient {
    public static final String BASE_API_URL = "http://192.168.1.34:5000/";
    private EnpublicApi apiInterface;
    private Retrofit retrofit;

    public RestClient(){
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = builder.build();
        apiInterface = retrofit.create(EnpublicApi.class);
    }

    public EnpublicApi getApiInterface(){
        return apiInterface;
    }

    public Retrofit getRetrofit(){
        return retrofit;
    }

}
