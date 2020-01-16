package com.illicitintelligence.android.location.network;

import com.illicitintelligence.android.location.model.ReturnType;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {


    @GET("/maps/api/geocode/json")
    Observable<ReturnType> callRequest(@Query("latlng") String latlng, @Query("key") String key);

}
