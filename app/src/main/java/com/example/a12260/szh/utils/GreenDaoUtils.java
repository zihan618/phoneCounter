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

    private List<DailyRecord> selectDailyRecord(long timestamp, String packageName) {
        QueryBuilder<DailyRecord> queryBuilder = dailyRecordDao.queryBuilder();
        List<DailyRecord> dailyRecords = queryBuilder
                .where(queryBuilder.and(DailyRecordDao.Properties.Timestamp.eq(timestamp),
                        DailyRecordDao.Properties.PackageName.eq(packageName))).list();
        return dailyRecords;
    }

    public List<DailyRecord> listDailyRecords(long timestamp) {
        long t = CalendarUtils.getFirstTimestampOfDay(timestamp);
        return dailyRecordDao.queryBuilder().where(DailyRecordDao.Properties.Timestamp.eq(t)).list();
    }

    public void updateDailyRecord(String packageName, long time) {
        long now = System.currentTimeMillis();
        //和上一次是同一个应用 直接更新即可
        if (packageName.equals(latestPackageName)) {
            //system.out.println("和上次一样的包名");
            if (latestDailyRecord.getId() == null) {
                List<DailyRecord> list = selectDailyRecord(CalendarUtils.getFirstTimestampOfDay(now), packageName);
                if (list.isEmpty()) {
                    //system.out.println("怎么还是空的");
                } else {
                    latestDailyRecord = list.get(0);
                }
            }
            latestDailyRecord.setTimeSpent(latestDailyRecord.getTimeSpent() + time);
            latestDailyRecord.update();
        } //应用切换了  要更新一下这两个变量
        else {
            //system.out.println("应用发生了切换");
            latestPackageName = packageName;
            Long timestamp = CalendarUtils.getFirstTimestampOfDay(now);
            List<DailyRecord> dailyRecords = selectDailyRecord(CalendarUtils.getFirstTimestampOfDay(now), packageName);
            //需要新建结构体
            if (dailyRecords.isEmpty()) {
                //system.out.println("需要新建");
                latestDailyRecord = new DailyRecord();
                latestDailyRecord.setPackageName(packageName);
                latestDailyRecord.setTimeSpent(CalendarUtils.getFirstTimestampOfDay(System.currentTimeMillis()));
                latestDailyRecord.setTimestamp(timestamp);
                getDailyRecordDao().save(latestDailyRecord);
//                if (dailyRecords.isEmpty()) {
//                    //system.out.println("还是空的，实现逻辑有问题");
//                } else {
//                    latestDailyRecord = dailyRecords.get(0);
//                }
            } //不需要新建
            else {
                //system.out.println("不需要新建");
                latestDailyRecord = dailyRecords.get(0);
                latestDailyRecord.setTimeSpent(latestDailyRecord.getTimeSpent() + time);
                //system.out.println( "更新之后的是" + latestDailyRecord);
                latestDailyRecord.refresh();
            }
        }
        //system.out.println(MyApplication.getAppName(latestPackageName));
        //   dailyRecordDao.loadAll().forEach(//system.out::println);
    }

    private void doWeeklySummary(long timestamp) {
        CalendarUtils.Interval interval = CalendarUtils.getIntervalOfWeek(timestamp);
        QueryBuilder<DailyRecord> queryBuilder = dailyRecordDao.queryBuilder();
        queryBuilder.where(queryBuilder.and(DailyRecordDao.Properties.Timestamp.ge(interval.getStart()), DailyRecordDao.Properties.Timestamp.lt(interval.getEnd())));
        List<DailyRecord> list = queryBuilder.list();
        list.stream().collect(Collectors.groupingBy(DailyRecord::getPackageName)).values()
                .stream().filter(x -> !x.isEmpty()).forEach(x -> {
            WeekRecord weekRecord = new WeekRecord();
            weekRecord.setTimestamp(interval.getStart());
            weekRecord.setPackageName(x.get(0).getPackageName());
            weekRecord.setTimeSpent(x.stream().map(DailyRecord::getTimeSpent).reduce(0L, Long::sum));
            weekRecordDao.save(weekRecord);
        });
    }

    void insertPackAppName(String packageName, String appName) {

    }

    private void doMonthlySummary(long timestamp) {
        CalendarUtils.Interval interval = CalendarUtils.getIntervalOfMonth(timestamp);
        QueryBuilder<DailyRecord> queryBuilder = dailyRecordDao.queryBuilder();
        queryBuilder.where(queryBuilder.and(DailyRecordDao.Properties.Timestamp.ge(interval.getStart()), DailyRecordDao.Properties.Timestamp.lt(interval.getEnd())));
        List<DailyRecord> list = queryBuilder.list();
        list.stream().collect(Collectors.groupingBy(DailyRecord::getPackageName)).values()
                .stream().filter(x -> !x.isEmpty()).forEach(x -> {
            MonthRecord monthRecord = new MonthRecord();
            monthRecord.setTimestamp(interval.getStart());
            monthRecord.setPackageName(x.get(0).getPackageName());
            monthRecord.setTimeSpent(x.stream().map(DailyRecord::getTimeSpent).reduce(0L, Long::sum));
            monthRecordDao.save(monthRecord);
        });
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
        mHelper = new DaoMaster.DevOpenHelper(MyApplication.getContext(), "usage4.db", null);
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
