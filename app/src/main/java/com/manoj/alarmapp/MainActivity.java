package com.manoj.alarmapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private Button setAlarmButton, selectDateButton;
    private TextView statusText;
    private EditText reasonEditText;
    private ListView alarmsListView;

    private List<Alarm> alarmList;
    private AlarmAdapter alarmAdapter;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timePicker = findViewById(R.id.timePicker);
        setAlarmButton = findViewById(R.id.setAlarmButton);
        selectDateButton = findViewById(R.id.selectDateButton);
        statusText = findViewById(R.id.statusText);
        reasonEditText = findViewById(R.id.reasonEditText);
        alarmsListView = findViewById(R.id.alarmsListView);

        alarmList = new ArrayList<>();
        alarmAdapter = new AlarmAdapter(this, alarmList, this::deleteAlarm);
        alarmsListView.setAdapter(alarmAdapter);

        calendar = Calendar.getInstance();

        // Check for permissions on Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!canScheduleExactAlarms()) {
                requestExactAlarmPermission();
            }
        }

        selectDateButton.setOnClickListener(view -> showDatePicker());
        setAlarmButton.setOnClickListener(view -> setAlarm());
    }

    private void showDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            calendar.set(Calendar.YEAR, selectedYear);
            calendar.set(Calendar.MONTH, selectedMonth);
            calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
            Toast.makeText(this, String.format("Date selected: %02d-%02d-%04d",
                    selectedDay, selectedMonth + 1, selectedYear), Toast.LENGTH_SHORT).show();
        }, year, month, day).show();
    }

    private void setAlarm() {
        String reason = reasonEditText.getText().toString().trim();
        if (reason.isEmpty()) {
            Toast.makeText(this, "Please enter a reason for the alarm", Toast.LENGTH_SHORT).show();
            return;
        }

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            Toast.makeText(this, "Selected time is in the past. Please choose a future time.", Toast.LENGTH_SHORT).show();
            return;
        }

        String alarmTime = String.format("%02d:%02d", hour, minute);

        // Add the new alarm to the list
        Alarm alarm = new Alarm(alarmTime, reason, calendar.getTime());
        alarmList.add(0, alarm);
        alarmAdapter.notifyDataSetChanged();

        // Update the status text
        statusText.setText(String.format("Alarm set for: %02d-%02d-%04d %s with reason: %s",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR),
                alarmTime, reason));

        // Set the alarm in the system
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, Alarmrecevier.class);

        // Add the alarm reason and time to the intent
        intent.putExtra("reason", reason);
        intent.putExtra("alarmTime", alarmTime);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                alarm.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            }
        }

        Toast.makeText(this, "Alarm has been set", Toast.LENGTH_SHORT).show();
    }


    private boolean canScheduleExactAlarms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return getSystemService(AlarmManager.class).canScheduleExactAlarms();
        }
        return true;
    }

    private void requestExactAlarmPermission() {
        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
        startActivityForResult(intent, 0);
    }

    private void deleteAlarm(int position) {
        Alarm alarm = alarmList.get(position);
        alarmList.remove(position);
        alarmAdapter.notifyDataSetChanged();

        // Cancel the alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, Alarmrecevier.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarm.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

        Toast.makeText(this, "Alarm deleted", Toast.LENGTH_SHORT).show();
    }
}
