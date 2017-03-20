package org.taitasciore.android.fmtest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by roberto on 17/03/17.
 */

/**
 * This class acts as the model layer, doing network tasks behind the scenes
 */
public class ApiService {

    private static final String API_URL = "http://barcelonaapi.marcpous.com/";

    private BarcelonaApi api;

    // API turned into a Java interface for use by Retrofit
    interface BarcelonaApi {

        @GET("metro/stations.json")
        Call<ApiResponse> getSubwayData();
    }

    interface OnRequestFinishListener {

        void onSuccess(List<ApiResponse.Metro> stations);
        void onError();
    }

    public ApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(BarcelonaApi.class);
    }

    /**
     * This method makes an asynchronous call to the API and communicates with the presenter
     * via the {@link OnRequestFinishListener} interface which the presenter implements
     * @param mListener
     * @return
     */
    public Call<ApiResponse> getSubwayData(final OnRequestFinishListener mListener) {
        Call<ApiResponse> call = api.getSubwayData();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    ApiResponse apiResponse = response.body();
                    List<ApiResponse.Metro> stations = apiResponse.getData().getMetro();
                    if (mListener != null) mListener.onSuccess(stations);
                }
                else if (mListener != null) mListener.onError();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (mListener != null) mListener.onError();
            }
        });

        return call;
    }
}
