package com.example.weatherapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("data/2.5/weather")
    Call<WeatherResponse> getWeatherByCity(
            @Query("q") String city,
            @Query("units") String units,
            @Query("appid") String apiKey
    );
}
