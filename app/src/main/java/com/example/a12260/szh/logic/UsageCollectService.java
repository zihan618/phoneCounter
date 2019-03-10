package com.example.a12260.szh.logic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.PowerManager;

import com.example.a12260.szh.R;
import com.example.a12260.szh.ui.activity.MainActivity;
import com.example.a12260.szh.utils.GreenDaoUtils;

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
    long lastTimestamp = 0;

    String channelID = UsageCollectService.class.getName();
    String channelName = channelID + "Name";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
//    public UsageCollectService() {
//        super("szhh");
//    }
    @Override
    public void onCreate() {
        super.onCreate();
        usageService = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Runnable runnable = this::startRecord;
        new Thread(runnable).start();

        Intent intent1 = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(this, channelID)
                    .setContentTitle("这才是szh")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .build();

            startForeground(2333, notification);
        } else {
            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new NotificationCompat.Builder(this.getApplicationContext())
                    .setContentTitle("这才是title")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setContentIntent(pendingIntent).build();
            manager.notify(2333, notification);
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
