package com.example.a12260.szh.utils;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.a12260.szh.Entity.DailyRecordDao;
import com.example.a12260.szh.Entity.DaoMaster;
import com.example.a12260.szh.Entity.DaoSession;
import com.example.a12260.szh.Entity.MonthRecordDao;
import com.example.a12260.szh.Entity.WeekRecordDao;

import androidx.fragment.app.FragmentManager;

public class MyApplication extends Application {
    private static Context context;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private DailyRecordDao dailyRecordDao;
    private WeekRecordDao weekRecordDao;
    private MonthRecordDao monthRecordDao;

    public static FragmentManager fragmentManager;

    public DailyRecordDao getDailyRecordDao() {
        return dailyRecordDao;
    }

    public WeekRecordDao getWeekRecordDao() {
        return weekRecordDao;
    }

    public MonthRecordDao getMonthRecordDao() {
        return monthRecordDao;
    }

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        mHelper = new DaoMaster.DevOpenHelper(this, "usage.db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
        dailyRecordDao = mDaoSession.getDailyRecordDao();
        weekRecordDao = mDaoSession.getWeekRecordDao();
        monthRecordDao = mDaoSession.getMonthRecordDao();
    }
    public static Context getContext() {
        return context;
    }



}
