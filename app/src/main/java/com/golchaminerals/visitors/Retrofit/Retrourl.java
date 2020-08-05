package com.golchaminerals.visitors.Retrofit;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrourl {
    public static RetroInterface retrofitApiInterface(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://1713101001.000webhostapp.com/userRes/")
                .addConverterFactory(GsonConverterFactory.create()) //Here we are using the GsonConverterFactory to directly convert json data to object
                .build();

        //creating the api interface
        RetroInterface api = retrofit.create(RetroInterface.class);
        return api;

    }


}
