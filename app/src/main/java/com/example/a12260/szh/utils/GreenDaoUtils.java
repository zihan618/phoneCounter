package com.example.a12260.szh.utils;

import android.database.sqlite.SQLiteDatabase;

import com.example.a12260.szh.Entity.DailyRecord;
import com.example.a12260.szh.Entity.DailyRecordDao;
import com.example.a12260.szh.Entity.DaoMaster;
import com.example.a12260.szh.Entity.DaoSession;
import com.example.a12260.szh.Entity.MonthRecord;
import com.example.a12260.szh.Entity.MonthRecordDao;
import com.example.a12260.szh.Entity.PackageAppDao;
import com.example.a12260.szh.Entity.WeekRecord;
import com.example.a12260.szh.Entity.WeekRecordDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GreenDaoUtils {
    private static GreenDaoUtils greenDaoUtils = new GreenDaoUtils();
    public static GreenDaoUtils getInstance() {
        return greenDaoUtils;
    }
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private DailyRecordDao dailyRecordDao;
    private WeekRecordDao weekRecordDao;
    private MonthRecordDao monthRecordDao;
    private PackageAppDao packageAppDao;
    private String latestPackageName = "";
    private DailyRecord latestDailyRecord = null;

    public DailyRecordDao getDailyRecordDao() {
        return dailyRecordDao;
    }

    public WeekRecordDao getWeekRecordDao() {
        return weekRecordDao;
    }

    public MonthRecordDao getMonthRecordDao() {
        return monthRecordDao;
    }

    public void updateDailyRecord(String packageName, int time) {
        //和上一次是同一个应用 直接更新即可
        if (packageName.equals(latestPackageName)) {
            latestDailyRecord.setTimeSpent(latestDailyRecord.getTimeSpent() + time);
            latestDailyRecord.refresh();
        } //应用切换了  要更新一下这两个变量
         else {
            latestPackageName = packageName;
            Long timestamp = CalendarUtils.getFirstTimestampOfDay(System.currentTimeMillis());
            QueryBuilder<DailyRecord> queryBuilder = dailyRecordDao.queryBuilder();
            List<DailyRecord> dailyRecords = queryBuilder
                    .where(queryBuilder.and(DailyRecordDao.Properties.PackageName.eq(packageName),
                            DailyRecordDao.Properties.Timestamp.eq(timestamp))).list();
            //需要新建结构体
            if (dailyRecords.isEmpty()) {
                System.out.println("需要新建");
                latestDailyRecord = new DailyRecord();
                latestDailyRecord.setPackageName(packageName);
                latestDailyRecord.setTimeSpent(CalendarUtils.getFirstTimestampOfDay(System.currentTimeMillis()));
                latestDailyRecord.setTimeSpent((long) time);
                getDailyRecordDao().save(latestDailyRecord);
            } //不需要新建
             else {
                latestDailyRecord = dailyRecords.get(0);
                latestDailyRecord.setTimeSpent(latestDailyRecord.getTimeSpent() + time);
                latestDailyRecord.refresh();
            }
        }
    }

    public void insertMonthRecord(long startTimestamp ) {
        CalendarUtils.Interval interval = CalendarUtils.getIntervalOfMonth(startTimestamp);
        QueryBuilder<DailyRecord> queryBuilder = getDailyRecordDao().queryBuilder();
        queryBuilder.where( queryBuilder.and(
                DailyRecordDao.Properties.Timestamp.ge(interval.getStart()), DailyRecordDao.Properties.Timestamp.lt(interval.getEnd())));
        List<DailyRecord> list = queryBuilder.list();
        Map<String, MonthRecord> map = new HashMap<>();
        Collection<List<DailyRecord>> values = list.stream().collect(Collectors.groupingBy(DailyRecord::getPackageName)).values();
        for (List<DailyRecord> records : values) {
            MonthRecord monthRecord = new MonthRecord();
            monthRecord.setTimestamp(interval.getStart());
            monthRecord.setTimeSpent(records.stream().map(DailyRecord::getTimeSpent).reduce( 0L ,Long::sum));
            monthRecord.setPackageName(records.get(0).getPackageName());
            monthRecordDao.save(monthRecord);
        }
    }

    public void insertWeekRecord(long startTimestamp ) {
        CalendarUtils.Interval interval = CalendarUtils.getIntervalOfWeek(startTimestamp);
        QueryBuilder<DailyRecord> queryBuilder = getDailyRecordDao().queryBuilder();
        queryBuilder.where( queryBuilder.and(
                DailyRecordDao.Properties.Timestamp.ge(interval.getStart()), DailyRecordDao.Properties.Timestamp.lt(interval.getEnd())));
        List<DailyRecord> list = queryBuilder.list();
        Map<String, MonthRecord> map = new HashMap<>();
        Collection<List<DailyRecord>> values = list.stream().collect(Collectors.groupingBy(DailyRecord::getPackageName)).values();
        for (List<DailyRecord> records : values) {
            WeekRecord weekRecord = new WeekRecord();
            weekRecord.setTimestamp(interval.getStart());
            weekRecord.setTimeSpent(records.stream().map(DailyRecord::getTimeSpent).reduce( 0L ,Long::sum));
            weekRecord.setPackageName(records.get(0).getPackageName());
            weekRecordDao.save(weekRecord);
        }
    }

    private GreenDaoUtils() {
        mHelper = new DaoMaster.DevOpenHelper(MyApplication.getContext(), "usage.db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
        dailyRecordDao = mDaoSession.getDailyRecordDao();
        weekRecordDao = mDaoSession.getWeekRecordDao();
        monthRecordDao = mDaoSession.getMonthRecordDao();
        packageAppDao = mDaoSession.getPackageAppDao();
    }



}
