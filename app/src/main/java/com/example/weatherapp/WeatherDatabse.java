package com.example.weatherapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
@Database(entities = {Entity.class} ,  version = 1)
public abstract class WeatherDatabse extends RoomDatabase {
public abstract DAO dao();

private static WeatherDatabse  weatherDatabse;
public static synchronized  WeatherDatabse getInstance(Context  context){
    if(weatherDatabse==null){
        weatherDatabse = Room.databaseBuilder(context.getApplicationContext() ,WeatherDatabse.class , "weather_database").fallbackToDestructiveMigration().build();
    }
    return weatherDatabse;
}

}
