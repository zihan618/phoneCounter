package com.example.a12260.szh.component;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;

import com.example.a12260.szh.Entity.DailyRecord;
import com.example.a12260.szh.R;
import com.example.a12260.szh.component.activity.MainActivity;
import com.example.a12260.szh.utils.GreenDaoUtils;
import com.example.a12260.szh.utils.MyApplication;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


public class UsageCollectService extends Service {

    boolean isStop = false;
    UsageStatsManager usageService;
    PowerManager powerManager;
    NotificationManager manager;
    PendingIntent pendingIntent;
    long lastTimestamp = 0;

    String channelID, channelName, reminder, reminder2;
    int notificationID;
    long totalToday;

    private void init() {
        usageService = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        channelID = getString(R.string.channelID);
        channelName = getString(R.string.channelName);
        notificationID = getResources().getInteger(R.integer.notificationID);
        reminder = getString(R.string.notificationReminder);
        reminder2 = getString(R.string.notificationReminder2);
        Intent intent1 = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);
        totalToday = GreenDaoUtils.getInstance().listDailyRecordsInDate(System.currentTimeMillis()).stream().map(DailyRecord::getTimeSpent).reduce(0L, Long::sum);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Runnable runnable = this::startRecord;
        new Thread(runnable).start();
        int minute = (int) Math.ceil(totalToday * 1.0 / 60000);
        String title;
        if (minute < 60) {
            title = String.format(reminder, minute);
        } else {
            title = String.format(reminder2, minute / 60, minute % 60);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(this, channelID)
                    .setContentTitle(title)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(notificationID, notification);
        } else {
            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle(String.format(reminder, 1))
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .build();
            manager.notify(notificationID, notification);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
    public void onDestroy() {
        stopForeground(true);
        isStop = true;
        super.onDestroy();
    }


    protected void startRecord() {
        long time;
        long interval;
        int minute;
        while (!isStop) {
            if (isScreenOn()) {
                time = System.currentTimeMillis();
                interval = lastTimestamp == 0 ? 5000 : time - lastTimestamp;
                lastTimestamp = time;
                //  totalToday += interval;
                //save state
                String foregroundPack = getForegroundPackage();
                if (StringUtils.isNotBlank(foregroundPack) && !MyApplication.isSystemApplication(foregroundPack)) {
                    GreenDaoUtils.getInstance().updateDailyRecord(foregroundPack, interval);
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
