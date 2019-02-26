package com.example.a12260.szh.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CalendarUtils {
    static {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
    }
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

    public static int getDayOfWeek(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        int res = (calendar.get(Calendar.DAY_OF_WEEK) + 6) % 7 ;
        //TODO: andriod API查询到的结果的时间区间有点问题， 暂时加一 先保证没有问题
        return res == 0 ? 7 : res;
//        int i = (res + 1) % 7;
//        return i == 0? 7 : i;
    }


    public static int getDayOfMonth(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static Interval getIntervalOfWeek(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long start = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE,  7);
        long end = calendar.getTimeInMillis();
        return new Interval(start, end);
    }

    public static Interval getIntervalOfMonth(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long start = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH,  1);
        long end = calendar.getTimeInMillis();
        return new Interval(start, end);
    }

    public static void main(String[] args) {
        System.out.println((getIntervalOfWeek(new Date().getTime())));
        System.out.println((getIntervalOfMonth(new Date().getTime())));
    }

    public static class Interval {
        private long start;
        private long end;
        public boolean contains(long t) {
            return  t >= start && t < end;
        }

        public long getStart() {
            return start;
        }

        public long getEnd() {
            return end;
        }

        @Override
        public String toString() {
            return "Interval{" +
                    "start=" + new Date(start) +
                    ", end=" + new Date(end) +
                    '}';
        }

        public Interval(long start, long end) {
            this.start = start;
            this.end = end;
        }
    }


}
