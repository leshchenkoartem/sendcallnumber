package com.example.artem.sendcallnumber.model.netw;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by artem on 10.06.17.
 */

public interface Api {
    @POST("api/phone")
    Call<Void> send(@Body PhoneFromTo phoneFromTo);
}
