package com.example.a12260.szh.logic;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.a12260.szh.logic.data_producer.APIUsageProvider;
import com.example.a12260.szh.utils.MyApplication;

import androidx.annotation.Nullable;


public class UsageCollectService extends IntentService {

    boolean isStop = false;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public UsageCollectService() {
        super("szhh");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        while (!isStop) {
            ActivityManager am = (ActivityManager) MyApplication.getContext()
                    .getSystemService(Activity.ACTIVITY_SERVICE);
            String packageName = am.getRunningTasks(1).get(0).topActivity
                    .getPackageName();
            String appName = APIUsageProvider.getInstance().getAppName(packageName);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
