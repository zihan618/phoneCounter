package com.szh.a12260.phone_counter.utils;

import android.content.pm.ApplicationInfo;
import android.database.sqlite.SQLiteDatabase;

import com.szh.a12260.phone_counter.entity.DailyRecord;
import com.szh.a12260.phone_counter.entity.DailyRecordDao;
import com.szh.a12260.phone_counter.entity.DaoMaster;
import com.szh.a12260.phone_counter.entity.DaoSession;
import com.szh.a12260.phone_counter.entity.MonthRecord;
import com.szh.a12260.phone_counter.entity.MonthRecordDao;
import com.szh.a12260.phone_counter.entity.PackageApp;
import com.szh.a12260.phone_counter.entity.PackageAppDao;
import com.szh.a12260.phone_counter.entity.WeekRecord;
import com.szh.a12260.phone_counter.entity.WeekRecordDao;
import com.szh.a12260.phone_counter.R;

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

            //   System.out.println(packageApp);
            //   System.out.println(packageAppDao.loadAll());
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
        //     mHelper.onUpgrade(db, 0, 0);
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

    public void refreshWeekMonthRecord(long start, long end) {
        weekRecordDao.deleteAll();
        monthRecordDao.deleteAll();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(CalendarUtils.getIntervalOfWeek(start).getStart());
        while (calendar.getTimeInMillis() < end) {
//            System.out.println("周今天是" + new Date(calendar.getTimeInMillis()));
            doWeeklySummary(calendar.getTimeInMillis());
//            System.out.println("昨晚之后有" + monthRecordDao.loadAll().size());
            calendar.add(Calendar.DATE, 7);
        }
        calendar.setTimeInMillis(CalendarUtils.getIntervalOfMonth(start).getStart());
        while (calendar.getTimeInMillis() < end) {
//            System.out.println("月今天是" + new Date(calendar.getTimeInMillis()));
            doMonthlySummary(calendar.getTimeInMillis());
//            System.out.println("昨晚之后有" + monthRecordDao.loadAll().size());
            calendar.add(Calendar.MONTH, 1);
        }
    }

    public void refreshDB(List<DailyRecord> dailyRecords, List<PackageApp> packageApps) {
        //   System.out.println("他们的 doumeiyou ");
        List<DailyRecord> todayData = this.listDailyRecordsInDate(CalendarUtils.getFirstTimestampOfDay());
        dailyRecordDao.deleteAll();
        packageAppDao.deleteAll();
        dailyRecords.addAll(todayData);
        dailyRecords.forEach(x -> x.setId(null));
        // System.out.println("要写入的数据有:" + dailyRecords.size());
        dailyRecordDao.saveInTx(dailyRecords);
        //   System.out.println("写入的数据有:" + dailyRecordDao.loadAll().size());
        packageAppDao.saveInTx(packageApps);
        refreshWeekMonthRecord(dailyRecords.stream().map(DailyRecord::getTimestamp).distinct().min(Long::compareTo).get(), System.currentTimeMillis());
    }

    public void generateDummyData(long start, long end) {
        List<ApplicationInfo> apps = MyApplication.getContext().getPackageManager().getInstalledApplications(0);
        List<String> packageApps = apps.stream().filter(x -> (x.flags & ApplicationInfo.FLAG_SYSTEM) <= 0).map(x -> x.packageName).collect(Collectors.toList());
        long t;
        Random random = new Random();
        t = CalendarUtils.getFirstTimestampOfDay(start);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(t);
        Collections.shuffle(packageApps, new Random(23L));
        while (calendar.getTimeInMillis() < end) {
            List<String> tmp = packageApps.subList(32, packageApps.size() / 11 + 32);
            //   System.out.println("sizr" + tmp.size());
            for (String packageApp : tmp) {
                DailyRecord dailyRecord = new DailyRecord();
                dailyRecord.setTimestamp(calendar.getTimeInMillis());
                dailyRecord.setPackageName(packageApp);
                dailyRecord.setTimeSpent((long) ((random.nextInt(90)) * 60000));
                dailyRecordDao.save(dailyRecord);
            }
            //  System.out.println("今天是：" + calendar.getTime());
            //  System.out.println("you" + dailyRecordDao.loadAll().size());
            calendar.add(Calendar.DATE, 1);
        }
        refreshWeekMonthRecord(t, end);
    }


}
