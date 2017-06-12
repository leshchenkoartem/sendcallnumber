package com.example.artem.sendcallnumber.model.netw;

import com.example.artem.sendcallnumber.model.AppState;
import com.j256.ormlite.android.AndroidLog;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by artem on 10.06.17.
 */

public class ApiClient {

    private static Api api;

    public static Api getInstance() {
        if (api==null){
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


            Retrofit restAdapter = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(AppState.getInstance().getEndPoint())
                    .client(client)
                    .build();
            api = restAdapter.create(Api.class);
        }
        return api;
    }
}
