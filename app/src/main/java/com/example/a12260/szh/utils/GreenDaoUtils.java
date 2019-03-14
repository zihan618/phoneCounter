package com.example.a12260.szh.utils;

import android.database.sqlite.SQLiteDatabase;

import com.example.a12260.szh.Entity.DailyRecord;
import com.example.a12260.szh.Entity.DailyRecordDao;
import com.example.a12260.szh.Entity.DaoMaster;
import com.example.a12260.szh.Entity.DaoSession;
import com.example.a12260.szh.Entity.MonthRecord;
import com.example.a12260.szh.Entity.MonthRecordDao;
import com.example.a12260.szh.Entity.PackageApp;
import com.example.a12260.szh.Entity.PackageAppDao;
import com.example.a12260.szh.Entity.WeekRecord;
import com.example.a12260.szh.Entity.WeekRecordDao;
import com.example.a12260.szh.R;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
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

    public String getAppName(String packageName) {
        List<PackageApp> packageApps = packageAppDao.queryBuilder().where(PackageAppDao.Properties.PackageName.eq(packageName)).list();
        if (packageApps.isEmpty()) {
            String appName = MyApplication.getAppName(packageName);
            PackageApp packageApp = new PackageApp();
            packageApp.setAppName(appName);
            packageApp.setPackageName(packageName);
            packageAppDao.save(packageApp);

            System.out.println(packageApp);
            System.out.println(packageAppDao.loadAll());
            return packageApp.getAppName();
        }
        return packageApps.get(0).getAppName();
    }
    private List<DailyRecord> selectDailyRecord(long timestamp, String packageName) {
        QueryBuilder<DailyRecord> queryBuilder = dailyRecordDao.queryBuilder();
        List<DailyRecord> dailyRecords = queryBuilder
                .where(queryBuilder.and(DailyRecordDao.Properties.Timestamp.eq(timestamp),
                        DailyRecordDao.Properties.PackageName.eq(packageName))).list();
        return dailyRecords;
    }

    public List<DailyRecord> listDailyRecordsInDate(long timestamp) {
        long t = CalendarUtils.getFirstTimestampOfDay(timestamp);
        return dailyRecordDao.queryBuilder().where(DailyRecordDao.Properties.Timestamp.eq(t)).list();
    }

    private Map<String, List<DailyRecord>> listDailyRecordsInTimeRange(long start, long end) {
        QueryBuilder<DailyRecord> queryBuilder = dailyRecordDao.queryBuilder();
        List<DailyRecord> dailyRecords = queryBuilder
                .where(queryBuilder.and(DailyRecordDao.Properties.Timestamp.ge(start),
                        DailyRecordDao.Properties.Timestamp.lt(end))).list();
        return dailyRecords.stream().collect(Collectors.groupingBy(DailyRecord::getPackageName));
    }

    //TODO: sql优化，排序其实没必要
    public long getMinDate() {
        // dailyRecordDao.queryBuilder().
        Optional<Long> res = dailyRecordDao.loadAll().stream().map(DailyRecord::getTimestamp).min(Long::compareTo);
        if (res.isPresent()) {
            return res.get();
        }
        return CalendarUtils.getFirstTimestampOfDay();
    }

    public long getMaxDate() {
        Optional<Long> res = dailyRecordDao.loadAll().stream().map(DailyRecord::getTimestamp).max(Long::compareTo);
        if (res.isPresent()) {
            return res.get();
        }
        return CalendarUtils.getFirstTimestampOfDay();
    }

    public Map<String, List<DailyRecord>> listDailyRecordsInWeek(long start) {
        CalendarUtils.Interval interval = CalendarUtils.getIntervalOfWeek(start);
        return listDailyRecordsInTimeRange(interval.getStart(), interval.getEnd());
    }

    //换算成可以在折线图里面直接用的list
    public List<Long> buildDailyTime(long start, int days, List<DailyRecord> records) {
        Map<Long, Long> map = new HashMap<>(records.size());
        records.forEach(x -> map.put(x.getTimestamp(), x.getTimeSpent()));
        if (records.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> list = new ArrayList<>(31);
        long end = records.get(records.size() - 1).getTimestamp();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(start);
        do {
            if (map.containsKey(start)) {
                list.add(map.get(start));
            } else {
                list.add(0L);
            }
            calendar.add(Calendar.DATE, 1);
            start = calendar.getTimeInMillis();
        } while (start <= end);
        while (list.size() < days) {
            list.add(0L);
        }
        return list;
    }

    public Map<String, List<DailyRecord>> listDailyRecordsInWeek() {
        return listDailyRecordsInWeek(System.currentTimeMillis());
    }

    public Map<String, List<DailyRecord>> listDailyRecordsInMonth(long start) {
        CalendarUtils.Interval interval = CalendarUtils.getIntervalOfMonth(start);
        return listDailyRecordsInTimeRange(interval.getStart(), interval.getEnd());
    }


    public Map<String, List<DailyRecord>> listDailyRecordsInMonth() {
        return listDailyRecordsInMonth(System.currentTimeMillis());
    }

    public void updateDailyRecord(String packageName, long time) {
        long now = System.currentTimeMillis();
        //和上一次是同一个应用 直接更新即可
        if (packageName.equals(latestPackageName)) {
            if (latestDailyRecord.getId() == null) {
                List<DailyRecord> list = selectDailyRecord(CalendarUtils.getFirstTimestampOfDay(now), packageName);
                if (list.isEmpty()) {

                } else {
                    latestDailyRecord = list.get(0);
                }
            }
            latestDailyRecord.setTimeSpent(latestDailyRecord.getTimeSpent() + time);

            latestDailyRecord.update();
        } //应用切换了  要更新一下这两个变量
        else {
            latestPackageName = packageName;
            Long timestamp = CalendarUtils.getFirstTimestampOfDay(now);
            List<DailyRecord> dailyRecords = selectDailyRecord(CalendarUtils.getFirstTimestampOfDay(now), packageName);
            //需要新建结构体
            if (dailyRecords.isEmpty()) {
                latestDailyRecord = new DailyRecord();
                latestDailyRecord.setPackageName(packageName);
                latestDailyRecord.setTimeSpent(time);
                latestDailyRecord.setTimestamp(timestamp);
                dailyRecordDao.save(latestDailyRecord);
            } //不需要新建
            else {
                latestDailyRecord = dailyRecords.get(0);
                latestDailyRecord.setTimeSpent(latestDailyRecord.getTimeSpent() + time);
                latestDailyRecord.refresh();
            }
        }
    }

    public List<DailyRecord> listAllDailyRecords() {
        return dailyRecordDao.loadAll();
    }

    public List<WeekRecord> listWeekRecordsInWeek(long timestamp) {
        long t = CalendarUtils.getIntervalOfWeek(timestamp).getStart();
        List<WeekRecord> list = weekRecordDao.queryBuilder().where(WeekRecordDao.Properties.Timestamp.eq(t)).list();
        list.forEach(x -> weekRecordDao.delete(x));
        doWeeklySummary(t);
        return weekRecordDao.queryBuilder().where(WeekRecordDao.Properties.Timestamp.eq(t)).list();
    }

    public List<MonthRecord> listMonthRecordsInMonth(long timestamp) {
        long t = CalendarUtils.getIntervalOfMonth(timestamp).getStart();
        List<MonthRecord> list = monthRecordDao.queryBuilder().where(MonthRecordDao.Properties.Timestamp.eq(t)).list();
        list.forEach(x -> monthRecordDao.delete(x));
        doMonthlySummary(t);
        return monthRecordDao.queryBuilder().where(MonthRecordDao.Properties.Timestamp.eq(t)).list();
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

    private GreenDaoUtils() {
        String dbName = MyApplication.getContext().getString(R.string.DBName);
        mHelper = new DaoMaster.DevOpenHelper(MyApplication.getContext(), dbName, null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
        dailyRecordDao = mDaoSession.getDailyRecordDao();
        weekRecordDao = mDaoSession.getWeekRecordDao();
        monthRecordDao = mDaoSession.getMonthRecordDao();
        packageAppDao = mDaoSession.getPackageAppDao();
    }

    public List<PackageApp> listAllPackageApp() {
        return packageAppDao.loadAll();
    }

    public void refreshDB(List<DailyRecord> dailyRecords, List<PackageApp> packageApps) {
        List<DailyRecord> todayData = this.listDailyRecordsInDate(CalendarUtils.getFirstTimestampOfDay());
        dailyRecordDao.deleteAll();
        packageAppDao.deleteAll();
        dailyRecords.addAll(todayData);
        dailyRecordDao.saveInTx(dailyRecords);
        packageAppDao.saveInTx(packageApps);

//        System.out.println("-----");
//        System.out.println(dailyRecordDao.loadAll().size());
//        System.out.println(packageAppDao.loadAll().size());
//        System.out.println("=====");
    }

    public void generateDummyData(long start, long end) {
        List<PackageApp> packageApps = packageAppDao.loadAll();
        long t;
        Random random = new Random();
        t = CalendarUtils.getFirstTimestampOfDay(start);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(t);
        while (calendar.getTimeInMillis() < end) {
            Collections.shuffle(packageApps);
            List<PackageApp> tmp = packageApps.subList(0, packageApps.size() / 2);
            for (PackageApp packageApp : tmp) {
                DailyRecord dailyRecord = new DailyRecord();
                dailyRecord.setTimestamp(calendar.getTimeInMillis());
                dailyRecord.setPackageName(packageApp.getPackageName());
                dailyRecord.setTimeSpent((long) ((random.nextInt(80) + 10) * 60000));
                dailyRecordDao.save(dailyRecord);
            }
            calendar.add(Calendar.DATE, 1);
        }
    }


}
