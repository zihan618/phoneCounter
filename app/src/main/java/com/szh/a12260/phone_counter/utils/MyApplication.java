package com.szh.a12260.phone_counter.utils;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.szh.a12260.phone_counter.R;

import java.util.ArrayList;

/**
 * @author 12260
 */
public class MyApplication extends Application {
    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

    }

    public static Context getContext() {
        return context;
    }


    /**
     * 返回非系统应用的app名称
     *
     * @param packageName 包名称
     * @return
     */
    public static String getAppName(String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            // if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return packageInfo.applicationInfo.loadLabel(packageManager).toString();
        } catch (PackageManager.NameNotFoundException e) {
            return context.getString(R.string.unknownAppName);
        }
    }

    public static Drawable getAppIcon(String pack) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(pack, 0);
            return packageInfo.applicationInfo.loadIcon(packageManager);
        } catch (PackageManager.NameNotFoundException e) {
            return context.getDrawable(R.mipmap.ic_launcher);
        }
    }

    public static boolean isSystemApplication(String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo x = packageManager.getPackageInfo(packageName, 0);
            return (x.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return true;
        }
        // if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
    }

    /**
     * 判断服务是否开启
     *
     * @return
     */
    public static boolean isServiceRunning(Class cla) {
        String serviceName = cla.getName();
        if (("").equals(serviceName) || serviceName == null) {
            return false;
        }
        ActivityManager myManager = (ActivityManager) MyApplication.getContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

}


