package com.manoj.alarmapp;

import java.util.Date;

public class Alarm {
    private String time;
    private String reason;

    public Alarm(String time, String reason, Date calendarTime) {
        this.time = time;
        this.reason = reason;
    }

    public String getTime() {
        return time;
    }

    public String getReason() {
        return reason;
    }
}
