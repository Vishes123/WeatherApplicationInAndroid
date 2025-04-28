package com.example.weatherapp;

import static androidx.core.content.ContextCompat.startActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkerParameters;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FastAdapter<ListAdeptar> fastAdapter;
    ItemAdapter<ListAdeptar> itemAdapter;
    WeatherDatabse weatherDatabse;

    Context context;
    private EditText searchtext;
    private TextView locationTxt, temperatureTxt, maxTempTxt, minTempTxt, weatherConditionTxt, windTxt, humidityTxt, sunriseTxt, sunsetTxt, dateTxt, dayTxt , textColor , today;
    private  static LottieAnimationView weatherAnimation;
    private static ConstraintLayout mainLayout;
    FloatingActionButton share ;
    ImageButton imageButton , locationBtn;
    private Switch switch1;
    private boolean isNight = false;
    NotificationHelper  notificationHelper;

    ConstraintLayout constraintLayout;

    FusedLocationProviderClient fusedLocationProviderClient;

    String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Recycle View or List Ke Actton Event ke liya-----------------------------------------------

        recyclerView = findViewById(R.id.scrollableList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(itemAdapter);
        recyclerView.setAdapter(fastAdapter);
        weatherDatabse = WeatherDatabse.getInstance(this);
        fastAdapter.withOnClickListener((v, adapter, item, position) -> {
            String cityName = item.entity.getCity();
            fetchWeatherFromApi(cityName);
            Toast.makeText(MainActivity.this, "Clicked: " + item.entity.getCity(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(MainActivity.this, "Weather Data Deleted", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView);
//=======================================================================================================================





        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        notificationHelper = new NotificationHelper(MainActivity.this);

        searchtext = findViewById(R.id.searchID);
        searchtext.setTextColor(Color.BLACK);
        locationBtn = findViewById(R.id.locationbtn);
        locationTxt = findViewById(R.id.LocationID);
        temperatureTxt = findViewById(R.id.TemID);
        maxTempTxt = findViewById(R.id.MaxTemID);
        minTempTxt = findViewById(R.id.MinTemID);
        weatherConditionTxt = findViewById(R.id.SunnyID);
        windTxt = findViewById(R.id.windID);
        humidityTxt = findViewById(R.id.humidityID);
        sunriseTxt = findViewById(R.id.sunriseID);
        sunsetTxt = findViewById(R.id.sunsetID);
        dateTxt = findViewById(R.id.dateID);
        dayTxt = findViewById(R.id.dayID);
        weatherAnimation = findViewById(R.id.lottieID);
        mainLayout = findViewById(R.id.main);
        share = findViewById(R.id.shareID);
        imageButton = findViewById(R.id.butontogo);
        constraintLayout = findViewById(R.id.main);
        switch1 = findViewById(R.id.switch1);
        today = findViewById(R.id.TextViewID);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , Recycleactivity.class);
                startActivity(intent);
            }
        });

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    getLastLocation();
                }
            }
        });

         // Ye defulte Location ke Liye
          loadWeatherDataFromRoom2();
          fetchWeatherFromApi("Noida");
          getCityNamefromRecycle();
          getCityNamefromRecycle2();




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textSearch = searchtext.getText().toString().trim();
                fetchWeatherFromApi(textSearch.toLowerCase());
            }
        });




    }
       void fetchWeatherFromApi(String city) {
        WeatherService api = RetrofitClint.getClint();
        Call<WeatherResponse> call = api.getWeatherByCity(city, "metric", "d863f6598caaa041f5c8406dc4d5b176");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weather = response.body();

                    // Save to DB
                    Entity entity = new Entity();
                    entity.city = weather.getCityName();
                    entity.condition = weather.getWeather().get(0).getMain();
                    entity.tempreture = weather.getMain().getTemp();
                    entity.lastUpdateTime = System.currentTimeMillis();


                    locationTxt.setText(weather.getCityName());
                    temperatureTxt.setText(weather.getMain().getTemp() + "°C");
                    maxTempTxt.setText("Max: " + weather.getMain().getTempMax() + "°C");
                    minTempTxt.setText("Min: " + weather.getMain().getTempMin() + "°C");
                    weatherConditionTxt.setText(weather.getWeather().get(0).getMain());
                    windTxt.setText(weather.getWind().getSpeed() + " m/s");
                    humidityTxt.setText(weather.getMain().getHumidity() + "%");
                    sunriseTxt.setText(convertTime(weather.getSys().getSunrise()));
                    sunsetTxt.setText(convertTime(weather.getSys().getSunset()));
                    setDateAndDay();
                    long sunrise = weather.getSys().getSunrise();
                    long sunset = weather.getSys().getSunset();
                   // nightDayBaground(sunrise , sunset);
                    String weatherCondition = weather.getWeather().get(0).getMain().toLowerCase();
                    changeAnimation(weatherCondition ,sunrise ,sunset);
                    String city =weather.getCityName();
                    double temp = weather.getMain().getTemp();
                    int humidity = weather.getMain().getHumidity();
                    double windSpeed = weather.getWind().getSpeed();
                    shareTo(city ,temp ,humidity ,windSpeed,sunrise,sunset);
                    switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            isNight = isChecked;
                            applyTheme(weatherCondition,sunrise,sunset);
                        }
                    });

                    notificationHelper.sendNotification(city+ " Weather Alert 🌧️", weatherCondition);

                    new Thread(() -> {
                        WeatherDatabse.getInstance(context).dao().insert(entity);
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                internetError();
                Toast.makeText(MainActivity.this, "Failed to load weather data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void changeAnimation(String weatherCondition , long sunrise , long sunset){
        int backgroundResId=0;
        long currentTime = System.currentTimeMillis() / 1000L;
        if (currentTime <= sunrise || currentTime >= sunset) {
            notificationHelper.sendNotification(city+ " Weather Alert 🌧️", weatherCondition);
            textColorWhite();
            // Night
            switch (weatherCondition.toLowerCase()) {
                case "clear":
                    weatherAnimation.setAnimation(R.raw.night_animation);
                    backgroundResId = R.drawable.night;
                    break;
                case "rain":
                case "drizzle":
                    weatherAnimation.setAnimation(R.raw.night_animation);
                    backgroundResId = R.drawable.rain_background;
                    break;
                case "clouds":
                    weatherAnimation.setAnimation(R.raw.night_animation);
                    backgroundResId = R.drawable.colud_background;
                    break;
                case "snow":
                    weatherAnimation.setAnimation(R.raw.night_animation);
                    backgroundResId = R.drawable.snow_background;
                    break;
                case "cold":
                    weatherAnimation.setAnimation(R.raw.night_animation);
                    backgroundResId = R.drawable.snow_background;
                    break;
                default:
                    weatherAnimation.setAnimation(R.raw.night_animation);
                    break;
            }
        }else {
            switch (weatherCondition.toLowerCase()) {
                case "clear":
                    weatherAnimation.setAnimation(R.raw.sun);
                    backgroundResId = R.drawable.sunnysky;
                    notificationHelper.sendNotification(city+ " Weather Alert 🌧️", weatherCondition);
                    break;
                case "rain":
                case "drizzle":
                    weatherAnimation.setAnimation(R.raw.rain);
                    backgroundResId = R.drawable.rain_background;
                    break;
                case "clouds":
                    weatherAnimation.setAnimation(R.raw.cloud);
                    backgroundResId = R.drawable.colud_background;
                    break;
                case "snow":
                    weatherAnimation.setAnimation(R.raw.snow);
                    backgroundResId = R.drawable.snow_background;
                    break;
                case "cold":
                    weatherAnimation.setAnimation(R.raw.snow);
                    backgroundResId = R.drawable.snow_background;
                    break;
                default:
                    weatherAnimation.setAnimation(R.raw.sun);
                    break;
            }
        }
        weatherAnimation.playAnimation();
        ConstraintLayout mainLayout = findViewById(R.id.main);
        mainLayout.setBackgroundResource(backgroundResId);

    }

    private void setDateAndDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

        Date date = new Date();
        dateTxt.setText(dateFormat.format(date));
        dayTxt.setText(dayFormat.format(date));
    }

    private String convertTime(long time) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        return sdf.format(new Date(time * 1000));
    }

    private  void shareTo(String city , double temp ,int humidity, double windSpeed, long sunrise ,long sunset  ){
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city  = locationTxt.getText().toString();
                String temp = temperatureTxt.getText().toString();
                String h = humidityTxt.getText().toString();
                String ws = windTxt.getText().toString();
                String sr= sunriseTxt.getText().toString();
                String ss =sunsetTxt.getText().toString();
                String allData ="City: "+city+" Temperature: "+temp+" humidity: "+h+" WindSpeed: "+ws+" SunRise: "+sr+" SunSet: "+ss+"\"Thank you for using Weather Application DEVELOPED BY VISHESH KHARE\"";
                Intent Sendintent = new Intent();
                Sendintent.setAction(Intent.ACTION_SEND);
                Sendintent.setType("text/plain");
                Sendintent.putExtra(Intent.EXTRA_TEXT , allData );
                Intent shareIntent = Intent.createChooser(Sendintent, "Share Weather Info via");
                startActivity(shareIntent);

            }
        });
    }



    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        try {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(MainActivity.this, location -> {
                        if (location != null) {
                            getCityName(location.getLatitude(), location.getLongitude());
                        } else {
                            Toast.makeText(MainActivity.this, "Location not available", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "Location permission error", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCityName(double lat, double lon) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses != null && !addresses.isEmpty()) {
                 city = addresses.get(0).getLocality();
                fetchWeatherFromApi(city);
                Toast.makeText(this, "City: " + city, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "City not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Geocoder error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        }
    }

    private void applyTheme(String wCondition , long sunrice , long sunset) {
        if (isNight) {
            mainLayout.setBackgroundColor(Color.BLACK);
            switch1.setTextColor(Color.WHITE);
            textColorWhite();
        } else {
           changeAnimation(wCondition , sunrice, sunset);
            switch1.setTextColor(Color.BLACK);
            textColorBlack();
        }
    }

    void textColorWhite(){
        int textColor = ContextCompat.getColor(this, android.R.color.white);
        locationTxt.setTextColor(textColor);
        temperatureTxt.setTextColor(textColor);
        maxTempTxt.setTextColor(textColor);
        minTempTxt.setTextColor(textColor);
        weatherConditionTxt.setTextColor(textColor);
        windTxt.setTextColor(textColor);
        humidityTxt.setTextColor(textColor);
        sunriseTxt.setTextColor(textColor);
        sunsetTxt.setTextColor(textColor);
        dateTxt.setTextColor(textColor);
        dayTxt.setTextColor(textColor);
        today.setTextColor(textColor);
    }

    void textColorBlack(){
        int textColor = ContextCompat.getColor(this, android.R.color.black);
        locationTxt.setTextColor(textColor);
        temperatureTxt.setTextColor(textColor);
        maxTempTxt.setTextColor(textColor);
        minTempTxt.setTextColor(textColor);
        weatherConditionTxt.setTextColor(textColor);
        windTxt.setTextColor(textColor);
        humidityTxt.setTextColor(textColor);
        sunriseTxt.setTextColor(textColor);
        sunsetTxt.setTextColor(textColor);
        dateTxt.setTextColor(textColor);
        dayTxt.setTextColor(textColor);
        today.setTextColor(textColor);
    }

    void internetError(){
        Intent intent = new Intent(MainActivity.this , InternetError.class);
        startActivity(intent);
    }

    void getCityNamefromRecycle() {
        Intent intent = getIntent();
        String city = intent.getStringExtra("city_name");

        if (city != null && !city.isEmpty()) {
            fetchWeatherFromApi(city);
        }
    }

    void getCityNamefromRecycle2() {
        Intent intent = getIntent();
        String cityName = intent.getStringExtra("city_name2");

        if (cityName != null && !cityName.isEmpty()) {
            fetchWeatherFromApi(cityName);

        }
    }



    private void loadWeatherDataFromRoom2() {
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