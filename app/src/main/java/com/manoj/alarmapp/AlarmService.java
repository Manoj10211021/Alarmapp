package com.manoj.alarmapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class AlarmService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Retrieve the alarm time and reason from the intent (if any)
        String alarmTime = intent.getStringExtra("alarm_time");
        String reason = intent.getStringExtra("reason");

        // Show a toast for demonstration (optional)
        Toast.makeText(this, "Alarm triggered!", Toast.LENGTH_SHORT).show();

        // Create and show the notification
        NotificationUtils.showAlarmNotification(this, alarmTime, reason);

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
