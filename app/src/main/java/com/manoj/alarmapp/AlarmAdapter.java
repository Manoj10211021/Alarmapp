package com.manoj.alarmapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class AlarmAdapter extends BaseAdapter {
    private Context context;
    private List<Alarm> alarmList;
    private OnAlarmDeleteListener deleteListener;

    // Constructor to initialize context, alarm list, and delete listener
    public AlarmAdapter(Context context, List<Alarm> alarmList, OnAlarmDeleteListener deleteListener) {
        this.context = context;
        this.alarmList = alarmList;
        this.deleteListener = deleteListener;
    }

    @Override
    public int getCount() {
        return alarmList.size();
    }

    @Override
    public Object getItem(int position) {
        return alarmList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate custom layout if convertView is null
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_alarm, parent, false);
        }

        // Initialize the views for the alarm time, reason, and delete button
        TextView alarmTimeText = convertView.findViewById(R.id.alarmTimeText);
        TextView alarmReasonText = convertView.findViewById(R.id.alarmReasonText);
        Button deleteAlarmButton = convertView.findViewById(R.id.deleteAlarmButton);

        // Get the current alarm
        Alarm alarm = alarmList.get(position);

        // Set the alarm time and reason text
        alarmTimeText.setText(alarm.getTime());
        alarmReasonText.setText(alarm.getReason());

        // Set up the delete button click listener
        deleteAlarmButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onAlarmDelete(position);
            }
        });

        return convertView;
    }

    // Interface for handling alarm delete events
    public interface OnAlarmDeleteListener {
        void onAlarmDelete(int position);
    }
}
