package com.example.a12260.szh.logic.data_producer;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.example.a12260.szh.R;
import com.example.a12260.szh.model.usage.UsageUnit;
import com.example.a12260.szh.utils.MyApplication;

import java.util.List;
import java.util.stream.Collectors;

public class APIUsageProvider implements AbstractUsageProvider {

    private UsageStatsManager usageService;

    private Context context;

    public APIUsageProvider() {
        this.context = MyApplication.getContext();
        this.usageService = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
    }

    /**
     * 返回非系统应用的app名称
     * @param packageName 包名称
     * @return
     */
    public String getAppName(String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                return packageInfo.applicationInfo.loadLabel(packageManager).toString();
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
        }).filter(x -> x.getAppName() != null).sorted((x, y) -> -Long.compare(x.getTimeSpent(), y.getTimeSpent())).collect(Collectors.toList());
        return filterUsageData(collect);
    }


    private List<UsageUnit> filterUsageData(List<UsageUnit> list) {
        int danwei = 60000;
        //数量小于阈值的时候直接范回
        if (list.size() < context.getResources().getInteger(R.integer.valueNumberThreshold)) {
            return list;
        }
        //最大值小于阈值的时候直接返回
        long maxValueThreshold = context.getResources().getInteger(R.integer.maxValueThreshold) * danwei;
        if (list.stream().map(UsageUnit::getTimeSpent).max(Long::compareTo).get() < maxValueThreshold ) {
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
