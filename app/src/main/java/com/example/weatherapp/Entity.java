package com.example.weatherapp;

import androidx.room.PrimaryKey;

@androidx.room.Entity(tableName = "weather_table")
public class Entity {
    @PrimaryKey(autoGenerate = true)
    int id;
    String city;
    String condition;
    double tempreture;
    long lastUpdateTime;

    public void setId(int id) {
        this.id = id;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setTempreture(double tempreture) {
        this.tempreture = tempreture;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public int getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public String getCondition() {
        return condition;
    }

    public double getTempreture() {
        return tempreture;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }
}
