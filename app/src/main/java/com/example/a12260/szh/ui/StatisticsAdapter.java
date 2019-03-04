package com.example.a12260.szh.ui;

import android.content.Context;
import android.os.Bundle;

import com.example.a12260.szh.Entity.DailyRecord;
import com.example.a12260.szh.Entity.MonthRecord;
import com.example.a12260.szh.Entity.WeekRecord;
import com.example.a12260.szh.R;
import com.example.a12260.szh.ui.fragment.DailyFragment;
import com.example.a12260.szh.ui.fragment.MonthlyFragment;
import com.example.a12260.szh.ui.fragment.WeeklyFragment;
import com.example.a12260.szh.utils.CalendarUtils;
import com.example.a12260.szh.utils.GreenDaoUtils;
import com.example.a12260.szh.utils.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * 首页界面上 tablayout的 adapter
 */
public class StatisticsAdapter extends FragmentPagerAdapter {
    private FragmentManager fm;
    private List<String> titles;
    private Context context = MyApplication.getContext();
    private Map<Integer, Fragment> fragmentMap = new HashMap<>();

    public StatisticsAdapter(@NonNull FragmentManager fm, List<String> titles) {
        super(fm);
        this.fm = fm;
        this.titles = titles;
    }



    @Override
    public CharSequence getPageTitle(int id) {
        return titles.get(id);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (fragmentMap.containsKey(position)) {
            return fragmentMap.get(position);
        }
        String tab = titles.get(position);
        Fragment fragment;
        if (tab.equals(context.getString(R.string.today))) {
            DailyFragment dailyFragment = new DailyFragment();
            List<DailyRecord> dailyRecords = GreenDaoUtils.getInstance().listDailyRecordsInDate(System.currentTimeMillis());
            Bundle bundle = new Bundle();
            List<String> names = new ArrayList<>(dailyRecords.size());
            long[] longs = new long[dailyRecords.size()];
            for (int i = 0; i < dailyRecords.size(); i++) {
                String packName = dailyRecords.get(i).getPackageName();
                names.add(packName);
                longs[i] = ((long) Math.ceil(dailyRecords.get(i).getTimeSpent() * 1.0 / 60000));
            }
            bundle.putStringArrayList("packNames", new ArrayList<>(names));
            bundle.putLongArray("times", longs);
            dailyFragment.setArguments(bundle);
            return dailyFragment;

        } else if (tab.equals(context.getString(R.string.thisWeek))) {
            WeeklyFragment weeklyFragment = new WeeklyFragment();

            List<WeekRecord> weekRecords = GreenDaoUtils.getInstance().listWeekRecordsInWeek(System.currentTimeMillis());
            Bundle bundle = new Bundle();
            List<String> names = new ArrayList<>(weekRecords.size());
            long[] longs = new long[weekRecords.size()];
            for (int i = 0; i < weekRecords.size(); i++) {
                String packName = weekRecords.get(i).getPackageName();
                names.add(packName);
                longs[i] = ((long) Math.ceil(weekRecords.get(i).getTimeSpent() * 1.0 / 60000));
            }
            bundle.putLong("start", CalendarUtils.getIntervalOfWeek().getStart());
            bundle.putInt("days", CalendarUtils.getDayOfWeek());
            bundle.putStringArrayList("packNames", new ArrayList<>(names));
            bundle.putLongArray("times", longs);
            weeklyFragment.setArguments(bundle);
            return weeklyFragment;
        } else if (tab.equals(context.getString(R.string.thisMonth))) {
            MonthlyFragment monthlyFragment = new MonthlyFragment();
            List<MonthRecord> monthRecords = GreenDaoUtils.getInstance().listMonthRecordsInMonth(System.currentTimeMillis());
            Bundle bundle = new Bundle();
            List<String> names = new ArrayList<>(monthRecords.size());
            long[] longs = new long[monthRecords.size()];
            for (int i = 0; i < monthRecords.size(); i++) {
                String packName = monthRecords.get(i).getPackageName();
                names.add(packName);
                longs[i] = ((long) Math.ceil(monthRecords.get(i).getTimeSpent() * 1.0 / 60000));
            }
            bundle.putLong("start", CalendarUtils.getIntervalOfMonth().getStart());
            bundle.putInt("days", CalendarUtils.getDayOfMonth());
            bundle.putStringArrayList("packNames", new ArrayList<>(names));
            bundle.putLongArray("times", longs);
            monthlyFragment.setArguments(bundle);
            return monthlyFragment;
        } else {
            fragment =  new Fragment();
        }
        fragmentMap.put(position, fragment);
        return fragment;
    }

    @Override
    public int getCount() {
        return titles.size();
    }
}
