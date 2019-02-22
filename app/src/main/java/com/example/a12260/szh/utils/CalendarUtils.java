package com.example.a12260.szh.utils;

import java.util.Calendar;
import java.util.Date;

public class CalendarUtils {
    /**
     * @return 返回7是星期日、1是星期一、2是星期二、3星期三、4是星期四、5是星期五、6是星期六
     */
    public static int getTodayOfWeek() {
        int res = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 6) % 7;
        return res == 0? 7 : res;
    }

    public static int getDayOfMonth() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public static Calendar getDay(long mills) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mills);
        return calendar;
    }

    public static long getFirstTimestampOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
      //  calendar.add(Calendar.DATE, -1);

        return calendar.getTimeInMillis();
    }

    public static void main(String[] args) {
       System.out.println(getDayOfMonth(new Date().getTime()));
    }

    public static int getDayOfWeek(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        int res = (calendar.get(Calendar.DAY_OF_WEEK) + 6) % 7 ;
        //TODO: andriod API查询到的结果的时间区间有点问题， 暂时加一 先保证没有问题
        //return res == 0 ? 7 : res;
        int i = (res + 1) % 7;
        return i == 0? 7 : i;
    }

    public static int getDayOfMonth(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }


}
