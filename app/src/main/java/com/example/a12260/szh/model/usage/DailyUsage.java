package com.example.a12260.szh.model.usage;

import java.util.Calendar;
import java.util.Date;

public class DailyUsage {
    private Calendar date;
    private Long timeSpent;

    @Override
    public String toString() {
        return "DailyUsage{" +
                "date=" + date +
                ", timeSpent=" + timeSpent +
                '}';
    }

    public Calendar getDate() {
        return date;
    }

    public DailyUsage setDate(Calendar date) {
        this.date = date;
        return this;
    }

    public Long getTimeSpent() {
        return timeSpent;
    }

    public DailyUsage setTimeSpent(Long timeSpent) {
        this.timeSpent = timeSpent;
        return this;
    }
}
