package com.example.weatherapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {
    private final Context context;

    public NotificationHelper(Context context) {
        this.context = context;
    }

    public void sendNotification(String title, String message) {

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "weather_channel")
            .setSmallIcon(R.drawable.rain)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH);

        manager.notify(1, builder.build());
    }
}
