package com.example.a12260.szh.logic.data_producer;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.example.a12260.szh.R;
import com.example.a12260.szh.model.usage.DailyUsage;
import com.example.a12260.szh.model.usage.UsageUnit;
import com.example.a12260.szh.utils.AppPackageNameMapper;
import com.example.a12260.szh.utils.CalendarUtils;
import com.example.a12260.szh.utils.MyApplication;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class APIUsageProvider implements AbstractUsageProvider {
    public static APIUsageProvider getInstance() {
        return apiUsageProvider;
    }

    private static APIUsageProvider apiUsageProvider = new APIUsageProvider();

    private UsageStatsManager usageService;

    private Context context;

    private APIUsageProvider() {
        this.context = MyApplication.getContext();
        this.usageService = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
    }

    /**
     * 返回非系统应用的app名称
     *
     * @param packageName 包名称
     * @return
     */
    public String getAppName(String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String result = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                AppPackageNameMapper.getInstance().register(packageName, result);
                return result;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<UsageUnit> getUsageStats(int intervalTye, long start, long end) {
        List<UsageStats> usageStats = usageService.queryUsageStats(intervalTye, start, end);
        List<UsageUnit> collect = usageStats.stream().map(x -> {
            UsageUnit unit = new UsageUnit();
            unit.setAppName(getAppName(x.getPackageName()))
                    .setIcon(null)
                    .setPackageName(x.getPackageName())
                    .setTimeSpent(x.getTotalTimeInForeground());
            return unit;
        })
                .filter(x -> x.getAppName() != null)
                .sorted((x, y) -> -Long.compare(x.getTimeSpent(), y.getTimeSpent()))
                .collect(Collectors.toList());
        return filterUsageData(collect);
    }

    @Override
    public List<Long> getDailyUsageStatsInWeek(String appName, Calendar stateDate, int days) {
        String packName = AppPackageNameMapper.getInstance().getPackName(appName);
        long start = stateDate.getTimeInMillis();
        System.out.println(String.format("开始时间：%d,  %s", stateDate.getTimeInMillis(), new Date(stateDate.getTimeInMillis()).toString()));
        stateDate.add(Calendar.DATE, days);
        long end = stateDate.getTimeInMillis();
        List<UsageStats> usageStats =
                usageService.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end).stream()
                .filter(x -> x.getPackageName().equals(packName))
                .collect(Collectors.toList());
        Map<Integer, Long> map = new HashMap<>(usageStats.size());
        List<Long> res =new ArrayList<>(days);
        for (UsageStats usageStat: usageStats) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            System.out.println(String.format("本次时间： %d，第%d天， %s -- %s",
                    usageStat.getFirstTimeStamp(),
                    CalendarUtils.getDayOfWeek(usageStat.getFirstTimeStamp()),
                    new Date(usageStat.getFirstTimeStamp()).toString(), new Date(usageStat.getLastTimeStamp()).toString()));
            map.put(CalendarUtils.getDayOfWeek(usageStat.getFirstTimeStamp()), usageStat.getTotalTimeInForeground());
        }
        //System.out.println(map);
        for (int i = 1; i <= days; i++) {
            if (map.containsKey(i)) {
                res.add(map.get(i));
            } else {
                res.add(0L);
            }
        }
        System.out.println(res);
        return res;
    }

    @Override
    public List<Long> getDailyUsageStatsInMonth(String appName, Calendar stateDate, int days) {
        String packName = AppPackageNameMapper.getInstance().getPackName(appName);
        long start = stateDate.getTimeInMillis();
        stateDate.add(Calendar.DATE, days);
        long end = stateDate.getTimeInMillis();
        List<UsageStats> usageStats =
                usageService.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end).stream()
                        .filter(x -> x.getPackageName().equals(packName))
                        .collect(Collectors.toList());
        Map<Long, Long> map = new HashMap<>(usageStats.size());
        for (UsageStats usageStat: usageStats) {
            map.put(CalendarUtils.getFirstTimestampOfDay(usageStat.getFirstTimeStamp()), usageStat.getTotalTimeInForeground());
        }
        System.out.println(map);
        stateDate.setTimeInMillis(start);
        List<Long> res =new ArrayList<>(days);
        while (stateDate.getTimeInMillis() < end) {
            System.out.println(stateDate.getTimeInMillis());
            if (map.containsKey(stateDate.getTimeInMillis())) {
                res.add(map.get(stateDate.getTimeInMillis()));
            } else {
                res.add(0L);
            }
            stateDate.add(Calendar.DATE, 1);
        }
        return res;
    }


    /**
     * 将数据过滤到更适合显示
     * @param list
     * @return
     */
    private List<UsageUnit> filterUsageData(List<UsageUnit> list) {
        int danwei = 60000;
        //数量小于阈值的时候直接范回
        if (list.size() < context.getResources().getInteger(R.integer.valueNumberThreshold)) {
            return list;
        }
        //最大值小于阈值的时候直接返回
        long maxValueThreshold = context.getResources().getInteger(R.integer.maxValueThreshold) * danwei;
        if (list.stream().map(UsageUnit::getTimeSpent).max(Long::compareTo).get() < maxValueThreshold) {
            return list;
        }

        long t = context.getResources().getInteger(R.integer.timeThreshold) * danwei;
        List<UsageUnit> res = list.stream().filter(x -> x.getTimeSpent() > t).collect(Collectors.toList());
        UsageUnit others = new UsageUnit();
        Long sum = list.stream().filter(x -> x.getTimeSpent() < t).map(UsageUnit::getTimeSpent).reduce(0L, (x, y) -> x + y);
        others.setPackageName(null)
                .setAppName(context.getResources().getString(R.string.others_name))
                .setTimeSpent(sum);
        res.add(others);
        return res;
    }

}
