package com.example.a12260.szh.ui;

import android.content.Context;
import android.os.Bundle;

import com.example.a12260.szh.Entity.WeekRecord;
import com.example.a12260.szh.R;
import com.example.a12260.szh.ui.fragment.CommunityFragment;
import com.example.a12260.szh.ui.fragment.MainStatisticsFragment;
import com.example.a12260.szh.ui.fragment.PlanFragment;
import com.example.a12260.szh.ui.fragment.WeeklyFragment;
import com.example.a12260.szh.utils.CalendarUtils;
import com.example.a12260.szh.utils.GreenDaoUtils;
import com.example.a12260.szh.utils.MyApplication;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * @author 12260
 */
public class BottomNavAdapter extends FragmentPagerAdapter {
    private List<String> tabs;
    private Context context = MyApplication.getContext();

    public BottomNavAdapter(FragmentManager fm, List<String> tabs) {
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
        if (context.getString(R.string.plan).equals(selected)) {
            return new PlanFragment();
        }

        if (context.getString(R.string.statistics).equals(selected)) {
            return new MainStatisticsFragment();
        }

        if (context.getString(R.string.community).equals(selected)) {
            return new CommunityFragment();
        }

        return new Fragment();
    }

    @Override
    public int getCount() {
        return tabs.size();
    }
}
