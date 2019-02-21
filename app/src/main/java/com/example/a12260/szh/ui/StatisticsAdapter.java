package com.example.a12260.szh.ui;

import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.example.a12260.szh.R;
import com.example.a12260.szh.model.chart.Chart;
import com.example.a12260.szh.model.usage.UsageUnit;
import com.example.a12260.szh.logic.chart_drawer.hellochart.HelloChartBuilder;
import com.example.a12260.szh.logic.data_producer.APIUsageProvider;
import com.example.a12260.szh.ui.fragment.SubStatisticFragment;
import com.example.a12260.szh.utils.MyApplication;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
            Calendar start = Calendar.getInstance();
            start.set(Calendar.HOUR,0);
            start.set(Calendar.MINUTE,0);
            start.set(Calendar.SECOND,0);
            start.set(Calendar.MILLISECOND,0);
            fragment = new SubStatisticFragment();
            Calendar end = Calendar.getInstance();
            Log.d("szh", String.format("start: %s, end: %s", start.toString(), end.toString()));
            List<UsageUnit> usageUnits = new APIUsageProvider().getUsageStats(UsageStatsManager.INTERVAL_DAILY, start.getTimeInMillis(), end.getTimeInMillis());
            Chart chart = HelloChartBuilder.buildPieChart(usageUnits);

            Chart chart1 = HelloChartBuilder.buildBarChart(usageUnits);
            ChartsAdapter chartsAdapter = new ChartsAdapter(Arrays.asList(Arrays.asList(chart.getChart()), Arrays.asList(chart1.getChart())), Arrays.asList(ChartsAdapter.ONE_CHART, ChartsAdapter.ONE_CHART));

         //   ((SubStatisticFragment)fragment).chart = chart.getChart();
            ((SubStatisticFragment)fragment).chartsAdapter = chartsAdapter;




        } else if (tab.equals(context.getString(R.string.thisWeek))) {
            Calendar start = Calendar.getInstance();
            start.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            start.set(Calendar.HOUR,0);
            start.set(Calendar.MINUTE,0);
            start.set(Calendar.SECOND,0);
            start.set(Calendar.MILLISECOND,0);
            Log.d("dfff", start.toString());
            Calendar end = Calendar.getInstance();
            fragment = new SubStatisticFragment();
            Log.d("szh", String.format("start: %s, end: %s", start.toString(), end.toString()));
            List<UsageUnit> usageUnits = new APIUsageProvider().getUsageStats(UsageStatsManager.INTERVAL_WEEKLY, start.getTimeInMillis(), end.getTimeInMillis());
            Chart chart = HelloChartBuilder.buildPieChart(usageUnits);
            ((SubStatisticFragment)fragment).chart = chart.getChart();
        } else if (tab.equals(context.getString(R.string.thisMonth))) {
            Calendar start = Calendar.getInstance();
            start.set(Calendar.DAY_OF_MONTH, 0);
            start.set(Calendar.HOUR,0);
            start.set(Calendar.MINUTE,0);
            start.set(Calendar.SECOND,0);
            start.set(Calendar.MILLISECOND,0);
            Calendar end = Calendar.getInstance();
            fragment = new SubStatisticFragment();
            Log.d("szh", String.format("start: %s, end: %s", start.toString(), end.toString()));
            List<UsageUnit> usageUnits = new APIUsageProvider().getUsageStats(UsageStatsManager.INTERVAL_MONTHLY, start.getTimeInMillis(), end.getTimeInMillis());
            Chart chart = HelloChartBuilder.buildPieChart(usageUnits);
            ((SubStatisticFragment)fragment).chart = chart.getChart();

//            FragmentTransaction transaction = fm.beginTransaction();
//            transaction.commit();
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
