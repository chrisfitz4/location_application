package com.illicitintelligence.android.location.network;

import androidx.core.content.ContextCompat;

import com.illicitintelligence.android.location.constants.Constants;
import com.illicitintelligence.android.location.model.ReturnType;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitCreator {

    RetrofitService service;

    public RetrofitCreator() {
        this.service = createService(createRetrofit());

    }

    private Retrofit createRetrofit(){
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private RetrofitService createService(Retrofit retrofit){
        return retrofit.create(RetrofitService.class);
    }

    public Observable<ReturnType> searchLocation(String latlng){
        return service.callRequest(latlng, Constants.API_KEY);
    }


}
