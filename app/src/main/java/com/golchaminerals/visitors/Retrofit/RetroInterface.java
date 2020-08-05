package com.golchaminerals.visitors.Retrofit;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetroInterface {

    @GET("order.php")
    Call<String> callPoly();

}
