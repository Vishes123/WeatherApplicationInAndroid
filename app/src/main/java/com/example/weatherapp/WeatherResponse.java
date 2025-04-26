package com.example.weatherapp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherResponse {

    @SerializedName("name")
    String cityName;
    @SerializedName("main")
    private Main main;

    @SerializedName("weather")
    private List<Weather> weather;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("sys")
    private Sys sys;

    public String getCityName() { return cityName; }
    public Main getMain() { return main; }
    public List<Weather> getWeather() { return weather; }
    public Wind getWind() { return wind; }
    public Sys getSys() { return sys; }

    public static class Main {
        @SerializedName("temp")
        private double temp;

        @SerializedName("temp_min")
        private double tempMin;

        @SerializedName("temp_max")
        private double tempMax;

        @SerializedName("humidity")
        private int humidity;

        public double getTemp() { return temp; }
        public double getTempMin() { return tempMin; }
        public double getTempMax() { return tempMax; }
        public int getHumidity() { return humidity; }
    }

    public static class Weather {
        @SerializedName("main")
        private String main;

        public String getMain() { return main; }
    }

    public static class Wind {
        @SerializedName("speed")
        private double speed;

        public double getSpeed() { return speed; }
    }

    public static class Sys {
        @SerializedName("sunrise")
        private long sunrise;

        @SerializedName("sunset")
        private long sunset;

        public long getSunrise() { return sunrise; }
        public long getSunset() { return sunset; }
    }
}
