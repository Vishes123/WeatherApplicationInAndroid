package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
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
    Entity entity;
    Context context;
    EditText editText;
    MainActivity mainActivity;
    WeatherDatabse weatherDatabse;
    RecyclerView recyclerView;
    FastAdapter<ListAdeptar> fastAdapter;
    ItemAdapter<ListAdeptar> itemAdapter;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.recycleactivity);

        editText = findViewById(R.id.searchID2);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString().trim();
                Intent intent = new Intent(Recycleactivity.this, MainActivity.class);
                intent.putExtra("city_name", text);
                startActivity(intent);
            }
        });




        recyclerView = findViewById(R.id.recycleID);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(itemAdapter);
        recyclerView.setAdapter(fastAdapter);


        fastAdapter.withOnClickListener((v, adapter, item, position) -> {
            String cityName = item.entity.getCity();
            Intent intent = new Intent(Recycleactivity.this, MainActivity.class);
            intent.putExtra("city_name2", cityName);
            startActivity(intent);
            Toast.makeText(Recycleactivity.this, "Clicked: " + item.entity.getCity(), Toast.LENGTH_SHORT).show();
            return true;
        });



        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                ListAdeptar swipedItem = itemAdapter.getAdapterItem(position);
                Entity entityToDelete = swipedItem.entity;


                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    WeatherDatabse db = WeatherDatabse.getInstance(getApplicationContext());
                    if (direction == ItemTouchHelper.LEFT) {

                        db.dao().deleteById(entityToDelete.getId());
                        runOnUiThread(() -> {
                            itemAdapter.remove(position);
                            Toast.makeText(Recycleactivity.this, "Weather Data Deleted", Toast.LENGTH_SHORT).show();
                        });
                    } else if (direction == ItemTouchHelper.RIGHT) {
                        // Handle the "favorite" action for right swipe
                        runOnUiThread(() -> {
                            // Here, you could perform any operation like marking as favorite, etc.
                            Toast.makeText(Recycleactivity.this, "Weather Data Favorited", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView);




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
            List<Entity> latestFive = allData.subList(0, Math.min(10, allData.size()));

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