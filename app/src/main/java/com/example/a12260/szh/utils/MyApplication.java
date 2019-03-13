package com.example.a12260.szh.utils;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.a12260.szh.Entity.DailyRecordDao;
import com.example.a12260.szh.Entity.DaoMaster;
import com.example.a12260.szh.Entity.DaoSession;
import com.example.a12260.szh.Entity.MonthRecordDao;
import com.example.a12260.szh.Entity.WeekRecordDao;
import com.example.a12260.szh.R;

import androidx.fragment.app.FragmentManager;

/**
 * @author 12260
 */
public class MyApplication extends Application {
    private static Context context;

    public static FragmentManager fragmentManager;


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
            String result = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            AppPackageNameMapper.getInstance().register(packageName, result);
            return result;
        } catch (PackageManager.NameNotFoundException e) {
            // e.printStackTrace();
            return context.getString(R.string.unknownAppName);
        }
    }
}
