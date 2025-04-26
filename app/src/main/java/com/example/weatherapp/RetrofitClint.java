package com.example.weatherapp;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClint {
    private static Retrofit retrofit;
    private static final String  BASE_URL = "https://api.openweathermap.org/";
    public static WeatherService getClint(){
        if(retrofit==null){
            retrofit= new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return  retrofit.create(WeatherService.class);
    }
}
