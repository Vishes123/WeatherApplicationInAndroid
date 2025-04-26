package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Recycleactivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FastAdapter<ListAdeptar> fastAdapter;
    ItemAdapter<ListAdeptar> itemAdapter;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.recycleactivity);

        recyclerView = findViewById(R.id.recycleID);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(itemAdapter);
        recyclerView.setAdapter(fastAdapter);

       loadWeatherDataFromRoom();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void loadWeatherDataFromRoom() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            WeatherDatabse db = WeatherDatabse.getInstance(getApplicationContext());

            // Test kr rha hu
            if (db.dao().getAllWeather().isEmpty()) {
                Entity e = new Entity();
                e.setCity("Test City");
                e.setCondition("Sunny");
                e.setTempreture(30.0);
                e.setLastUpdateTime(System.currentTimeMillis());
                db.dao().insert(e);
            }

            List<Entity> allData = db.dao().getAllWeather();
            //List<Entity> reversedList = new ArrayList<>(allData);
            List<Entity> latestFive = allData.subList(0, Math.min(5, allData.size()));

            List<ListAdeptar> itemList = new ArrayList<>();
            for (Entity e : latestFive) {
                itemList.add(new ListAdeptar(e));
            }

            runOnUiThread(() -> {
                itemAdapter.set(itemList);
            });
        });
    }

}