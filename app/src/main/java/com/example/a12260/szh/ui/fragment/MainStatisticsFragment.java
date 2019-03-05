package com.example.a12260.szh.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.example.a12260.szh.Entity.DailyRecord;
import com.example.a12260.szh.Entity.MonthRecord;
import com.example.a12260.szh.Entity.WeekRecord;
import com.example.a12260.szh.R;
import com.example.a12260.szh.utils.CalendarUtils;
import com.example.a12260.szh.utils.GreenDaoUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

/**
 * @author 12260
 */
public class MainStatisticsFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_statistics, menu);
        //TODO: 暂时先这么实现，等有更优雅的方法了再说
        onOptionsItemSelected(menu.getItem(0));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem x) {
        x.setChecked(true);
        if (x.getItemId() == R.id.item_daily) {
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
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.statistic_container, dailyFragment, getString(R.string.daily));
            transaction.commit();
        } else if (x.getItemId() == R.id.item_weekly) {
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
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.statistic_container, weeklyFragment, getString(R.string.weekly));
            transaction.commit();
        } else if (x.getItemId() == R.id.item_monthly) {
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
            FragmentTransaction transaction2 = getChildFragmentManager().beginTransaction();
            transaction2.replace(R.id.statistic_container, monthlyFragment, getString(R.string.monthly));
            transaction2.commit();
        } else if (x.getItemId() == R.id.item_share) {

        }
        return super.onOptionsItemSelected(x);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stastics, container, false);
        return view;
    }

}
