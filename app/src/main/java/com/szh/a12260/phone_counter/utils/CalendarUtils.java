package com.szh.a12260.phone_counter.utils;

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
    public static int getDayOfWeek() {
        return getDayOfWeek(System.currentTimeMillis());
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

    public static long getFirstTimestampOfDay() {
        return getFirstTimestampOfDay(System.currentTimeMillis());
    }


    public static int getDayOfWeek(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        int res = (calendar.get(Calendar.DAY_OF_WEEK) + 6) % 7;
        return res == 0 ? 7 : res;
    }


    public static int getDayOfMonth(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static Interval getIntervalOfWeek() {
        return getIntervalOfWeek(System.currentTimeMillis());
    }


    public static Interval getIntervalOfWeek(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        //垃圾玩意儿， sunday是一周的第一天
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DATE, -7);
        }
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long start = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE, 7);
        long end = calendar.getTimeInMillis();
        return new Interval(start, end);
    }

    /**
     * 这周过了多少天了，包括今天
     *
     * @param t
     * @return
     */
    public static int getDaysPastInWeek(long t) {
        Interval interval = getIntervalOfWeek();
        if (t >= interval.getStart()) {
            return getDayOfWeek();
        }
        if (t < interval.getStart()) {
            return 7;
        }
        return 0;
    }

    /**
     * 这月过了多少天了，包括今天
     *
     * @param t
     * @return
     */
    public static int getDaysPastInMonth(long t) {
        Interval interval = getIntervalOfMonth();
        if (t >= interval.getStart()) {
            return getDayOfMonth();
        }
        if (t < interval.getStart()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(t);
            return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        return 0;
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
        calendar.add(Calendar.MONTH, 1);
        long end = calendar.getTimeInMillis();
        return new Interval(start, end);
    }

    public static Interval getIntervalOfMonth() {
        return getIntervalOfMonth(System.currentTimeMillis());
    }

    public static void main(String[] args) {
        System.out.println(getDaysPastInMonth(System.currentTimeMillis()));
        //        System.out.println((getIntervalOfWeek(new Date().getTime())));
        //        System.out.println((getIntervalOfMonth(new Date().getTime())));
    }

    public static class Interval {
        private long start;
        private long end;

        public boolean contains(long t) {
            return t >= start && t < end;
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
