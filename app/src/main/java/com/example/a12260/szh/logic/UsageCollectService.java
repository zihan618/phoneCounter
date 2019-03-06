package com.example.a12260.szh.logic;

import android.app.IntentService;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.example.a12260.szh.utils.GreenDaoUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import androidx.annotation.Nullable;


public class UsageCollectService extends IntentService {

    boolean isStop = false;
    UsageStatsManager usageService;
    PowerManager powerManager;
    long lastTimestamp = 0;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public UsageCollectService() {
        super("szhh");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        usageService = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
    }

    private String getForegroundPackage() {
        long time = System.currentTimeMillis();
        List<UsageStats> appList = usageService.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                time - 1000 * 1000, time);
        if (appList != null && appList.size() > 0) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
            for (UsageStats usageStats : appList) {
                mySortedMap.put(usageStats.getLastTimeUsed(),
                        usageStats);
            }
            if (!mySortedMap.isEmpty()) {
                String currentApp = mySortedMap.get(
                        mySortedMap.lastKey()).getPackageName();
                return currentApp;
            }
        }
        return null;
    }

    boolean isScreenOn() {
        return powerManager.isInteractive();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        long time;
        long interval;
        while (!isStop) {
            if (isScreenOn()) {
                time = System.currentTimeMillis();
                interval = lastTimestamp == 0 ? 5000 : time - lastTimestamp;
                lastTimestamp = time;
                String foregroundPack = getForegroundPackage();
                if (StringUtils.isNotBlank(foregroundPack)) {
                    GreenDaoUtils.getInstance().updateDailyRecord(foregroundPack, interval);
                } else {
                    System.out.println("无法获取前台app包名");
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//            ActivityManager am = (ActivityManager) MyApplication.getContext()
//                    .getSystemService(Activity.ACTIVITY_SERVICE);
//            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
//            tasks.forEach(x -> System.out.println(x.processName));
//            String packageName = am.getRunningTasks(1).get(0).topActivity
//                    .getPackageName();
//            long now = System.currentTimeMillis();
//            GreenDaoUtils.getInstance().updateDailyRecord(packageName, now - lastTimestamp);
//            lastTimestamp = now;
//
//            System.out.println(packageName);
//            System.out.println(GreenDaoUtils.getInstance().getDailyRecordDao().loadAll());
    }

}
