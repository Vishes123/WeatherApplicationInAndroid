package com.example.weatherapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
@Dao
public interface DAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Entity entity);

    @Query("SELECT * FROM weather_table ORDER BY lastUpdateTime DESC")
    List<Entity> getAllWeather();

    @Query("DELETE FROM weather_table WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT * FROM weather_table")
    List<Entity> getAllWeatherData();
}
