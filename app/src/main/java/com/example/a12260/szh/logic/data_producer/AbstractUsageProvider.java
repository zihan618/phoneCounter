package com.example.a12260.szh.logic.data_producer;

import com.example.a12260.szh.model.usage.DailyUsage;
import com.example.a12260.szh.model.usage.UsageUnit;

import java.util.Calendar;
import java.util.List;

public interface AbstractUsageProvider {

    List<UsageUnit> getUsageStats(int intervalTye, long start, long end);

    List<Long> getDailyUsageStatsInWeek(String appName, Calendar stateDate, int endDate);

    List<Long> getDailyUsageStatsInMonth(String appName, Calendar stateDate, int endDate);
}
