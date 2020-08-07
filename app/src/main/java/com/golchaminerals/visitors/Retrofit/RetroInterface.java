package com.golchaminerals.visitors.Retrofit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetroInterface {

    @GET("worker/{url}")
    Call<ScanModelClass> callUserDetail(@Path(value = "url", encoded = false)String url);

    @POST("mark-attendence")
    @FormUrlEncoded
    Call<ScanModelClass> MakeAttendence(@Field("worker_id") String worker_id ,
                                        @Field("scanned_by")String scanned_by);

}
