package com.example.driverside;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.util.Log;

public class App extends Application {
    public static final String FCM_CHANNEL_ID = "FCM_CHANNEL_ID";
    private static final String TAG = "App";
    
    @Override
    public void onCreate() {
        super.onCreate();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel fcmChannel = new NotificationChannel(
                    FCM_CHANNEL_ID, "FCM_Channel", NotificationManager.IMPORTANCE_HIGH);
            Log.d(TAG, "onCreate: here messAGE");
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            manager.createNotificationChannel(fcmChannel);
        }

    }
}
