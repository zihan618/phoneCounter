package com.szh.a12260.phone_counter.component;

import android.content.Context;
import android.os.Bundle;

import com.szh.a12260.phone_counter.R;
import com.szh.a12260.phone_counter.component.fragment.DailyFragment;
import com.szh.a12260.phone_counter.component.fragment.MonthlyFragment;
import com.szh.a12260.phone_counter.component.fragment.WeeklyFragment;
import com.szh.a12260.phone_counter.utils.CalendarUtils;
import com.szh.a12260.phone_counter.utils.MyApplication;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * @author 12260
 */
public class OnlyStatisticBottomAdapter extends FragmentPagerAdapter {
    private List<String> tabs;
    private Context context = MyApplication.getContext();


    public OnlyStatisticBottomAdapter(FragmentManager fm, List<String> tabs) {
        super(fm);
        this.tabs = tabs;
    }

    @Override
    public CharSequence getPageTitle(int id) {
        if (id >= 0 && id <= tabs.size()) {
            return tabs.get(id);
        } else {
            return context.getString(R.string.defaultUnkownTab);
        }
    }

    @Override
    public Fragment getItem(int o) {

        String selected = tabs.get(o);
        if (context.getString(R.string.daily).equals(selected)) {
            DailyFragment dailyFragment = new DailyFragment();
            Bundle bundle = new Bundle();
            bundle.putLong("start", CalendarUtils.getFirstTimestampOfDay(System.currentTimeMillis()));
            dailyFragment.setArguments(bundle);
            return dailyFragment;
        } else if (context.getString(R.string.weekly).equals(selected)) {
            WeeklyFragment weeklyFragment = new WeeklyFragment();
            Bundle bundle = new Bundle();
            bundle.putLong("start", CalendarUtils.getIntervalOfWeek().getStart());
            weeklyFragment.setArguments(bundle);
            return weeklyFragment;
        }

        if (context.getString(R.string.monthly).equals(selected)) {
            MonthlyFragment monthlyFragment = new MonthlyFragment();
            Bundle bundle = new Bundle();
            bundle.putLong("start", CalendarUtils.getIntervalOfMonth().getStart());
            monthlyFragment.setArguments(bundle);
            return monthlyFragment;
        }

        return new Fragment();
    }

    @Override
    public int getCount() {
        return tabs.size();
    }
}
