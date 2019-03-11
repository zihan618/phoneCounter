package com.example.a12260.szh.logic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.a12260.szh.Entity.DailyRecord;
import com.example.a12260.szh.R;
import com.example.a12260.szh.ui.activity.MainActivity;
import com.example.a12260.szh.utils.GreenDaoUtils;
import com.example.a12260.szh.utils.MyApplication;
import com.example.a12260.szh.utils.ServiceUtils;

import androidx.core.app.NotificationCompat;

/**
 * @author 12260
 */
public class MyReceiver extends BroadcastReceiver {
    NotificationManager manager;
    PendingIntent pendingIntent;

    String channelID, channelName, reminder, reminder2;
    int notificationID;

    private void init(Context context) {
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        channelID = context.getString(R.string.channelID);
        channelName = context.getString(R.string.channelName);
        notificationID = context.getResources().getInteger(R.integer.notificationID);
        reminder = context.getString(R.string.notificationReminder);
        reminder2 = context.getString(R.string.notificationReminder2);
        Intent intent1 = new Intent(context, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(context, 0, intent1, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        init(context);
        if (!ServiceUtils.isServiceRunning(UsageCollectService.class)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, UsageCollectService.class));
            } else {
                context.startService(new Intent(context, UsageCollectService.class));
            }
        }
        if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
            try {
                updateNotification(context);
            } catch (Exception e) {
                System.out.println("receiver wrong!");
            }
        }

    }

    private Notification makeNotification(String title, Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return new NotificationCompat.Builder(context, channelID)
                    .setContentTitle(title)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .build();

        } else {
            return new NotificationCompat.Builder(context)
                    .setContentTitle(title)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    //   .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setContentIntent(pendingIntent)
                    .build();
        }
    }

    public void updateNotification(Context context) {
        long totalToday = GreenDaoUtils.getInstance().listDailyRecordsInDate(System.currentTimeMillis()).stream().map(DailyRecord::getTimeSpent).reduce(0L, Long::sum);
        int minute = (int) Math.ceil(totalToday * 1.0 / 60000);
        if (minute < 60) {
            String title = String.format(reminder, minute);
            manager.notify(notificationID, makeNotification(title, context));
        } else {
            String title = String.format(reminder2, minute / 60, minute % 60);
            manager.notify(notificationID, makeNotification(title, context));
        }
    }


}
