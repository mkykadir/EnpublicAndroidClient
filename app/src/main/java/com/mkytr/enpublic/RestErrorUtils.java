package com.mkytr.enpublic;

import com.mkytr.enpublic.Activities.MapsActivity;

import java.io.IOException;
import java.lang.annotation.Annotation;

import retrofit2.Response;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by mkyka on 28.02.2018.
 */

public class RestErrorUtils {
    public static ApiError parseError(Response<?> response){
        Converter<ResponseBody, ApiError> converter =
                MapsActivity.restClient
                        .getRetrofit()
                        .responseBodyConverter(ApiError.class, new Annotation[0]);

        ApiError error;
        try {
            error = converter.convert(response.errorBody());
        }catch (IOException e){
            return new ApiError();
        }

        return error;
    }
}
