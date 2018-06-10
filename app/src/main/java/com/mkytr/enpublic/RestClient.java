package com.mkytr.enpublic;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mkyka on 28.02.2018.
 */

public class RestClient {
    // public static final String BASE_API_URL = "http://192.168.1.39:5000/";
    public static final String BASE_API_URL = "http://enpublic.mkytr.com/";
    private static RestClient currentInstance = null;
    private static EnpublicApi apiInterface = null;
    private static Retrofit retrofit = null;

    public static RestClient getInstance() {
        if(currentInstance == null) {
            currentInstance = new RestClient();
        }
        return currentInstance;
    }

    private RestClient(){
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();
        apiInterface = retrofit.create(EnpublicApi.class);
    }

    public EnpublicApi getInterface(){
        return apiInterface;
    }

    public Retrofit getRetrofit(){
        return retrofit;
    }

}
