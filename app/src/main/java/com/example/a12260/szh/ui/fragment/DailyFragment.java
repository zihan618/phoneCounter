package com.example.a12260.szh.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.a12260.szh.Entity.DailyRecord;
import com.example.a12260.szh.R;
import com.example.a12260.szh.utils.CalendarUtils;
import com.example.a12260.szh.utils.GreenDaoUtils;
import com.example.a12260.szh.utils.MyApplication;
import com.example.a12260.szh.utils.PieChartColorProvider;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * @author 12260
 */
public class DailyFragment extends Fragment implements OnDateSelectedListener/*, DayViewDecorator*/ {
    private MaterialCalendarView calendarView;
    private PieChartView pieChart;
    private PieChartData pd;
    private long start;
    private List<String> appNames;
    private long sum;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // MonthView
        super.onCreate(savedInstanceState);
        //   ButterKnife.bind(this.getTargetFragment());
    }

    class MyDayViewDecorator implements DayViewDecorator {
        private long first;
        private long last;

        MyDayViewDecorator(long first, long last) {
            this.first = first;
            this.last = last;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            long t = day.getCalendar().getTimeInMillis();
            return !(t >= first && t <= last);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setDaysDisabled(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one_chart, container, false);
        this.calendarView = view.findViewById(R.id.calendarView);
        this.pieChart = view.findViewById(R.id.dailyPieChart);
        calendarView.setOnDateChangedListener(this);
        Bundle bundle = getArguments();
        start = Objects.requireNonNull(bundle).getLong("start");
        calendarView.setDateSelected(new Date(start), true);
        init();
        buildPieChart(start);
        return view;
    }

    private void init() {
        //图标的基本样式设定 和 事件回调函数设置
        pd = new PieChartData();
        pd.setHasLabels(true).setHasCenterCircle(true)
                .setCenterCircleScale(0.618F).setCenterText1FontSize(20);
        pd.setHasCenterCircle(true);
        pieChart.setPieChartData(pd);
        pd.setSlicesSpacing(1);
        PieChartOnValueSelectListener listener = new PieChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int arcIndex, SliceValue value) {
                pieChart.cancelDataAnimation();
                pd.setCenterText1(appNames.get(arcIndex)).setCenterText2(
                        String.format(getString(R.string.usageLabel), (long) (value.getValue())));
                pieChart.startDataAnimation(300);
            }

            @Override
            public void onValueDeselected() {
                pieChart.cancelDataAnimation();
                pd.setCenterText1(getString(R.string.total)).setCenterText2(
                        String.format(getString(R.string.usageLabel), (sum)));
                pieChart.startDataAnimation(300);
            }
        };
        pieChart.setValueSelectionEnabled(true);
        pieChart.setViewportCalculationEnabled(true);
        pieChart.setOnValueTouchListener(listener);


        //calendarview的可选区间设置
        long firstEnabledDay = GreenDaoUtils.getInstance().getMinDate();
        long minDate = CalendarUtils.getIntervalOfMonth(firstEnabledDay).getStart();
        long lastEnabledDay = Math.max(CalendarUtils.getFirstTimestampOfDay(), GreenDaoUtils.getInstance().getMaxDate());
        long maxDate = CalendarUtils.getIntervalOfMonth(lastEnabledDay).getEnd() - 1;
        System.out.println(new Date(minDate));
        System.out.println(new Date(maxDate));
        calendarView.state().edit().setMinimumDate(new Date(minDate)).setMaximumDate(new Date(maxDate)).commit();
        calendarView.addDecorator(new MyDayViewDecorator(firstEnabledDay, lastEnabledDay));
    }

    private void buildPieChartData(long timestamp) {
        List<DailyRecord> dailyRecords = GreenDaoUtils.getInstance().listDailyRecordsInDate(timestamp);
        dailyRecords.sort((x, y) -> -Long.compare(x.getTimeSpent(), y.getTimeSpent()));
        List<String> packNames = new ArrayList<>(dailyRecords.size());
        List<Long> minutes = new ArrayList<>(dailyRecords.size());
        for (int i = 0; i < dailyRecords.size(); i++) {
            String packName = dailyRecords.get(i).getPackageName();
            packNames.add(packName);
            minutes.add((long) Math.ceil(dailyRecords.get(i).getTimeSpent() * 1.0 / 60000));
        }
        List<SliceValue> sliceList = new ArrayList<>();
        if (packNames.size() != minutes.size()) {
            System.out.println("长度不一样，先停了");
        }

        sum = minutes.stream().reduce(0L, Long::sum);
        appNames = new ArrayList<>(packNames.size());

        List<Double> percents = minutes.stream().map(x -> x * 1.0 / sum).collect(Collectors.toList());
        Double percentThreshold = MyApplication.getContext().getResources().getInteger(R.integer.percentLabelThreshold) * 1.0 / 100;
        List<Integer> colors = PieChartColorProvider.getColors(dailyRecords.size());
        for (int i = 0; i < packNames.size(); i++) {
            SliceValue sliceValue = new SliceValue(minutes.get(i), getResources().getColor(colors.get(i), null));
            String appName = GreenDaoUtils.getInstance().getAppName(packNames.get(i));
            // 根据包名获取app的名称
            if (StringUtils.isNotBlank(appName)) {
                appNames.add(appName);
            } else {
                appName = MyApplication.getContext().getString(R.string.unknownAppName);
            }
            //比例太小的话不显示名称
            if (percents.get(i) >= percentThreshold) {
                sliceValue.setLabel(appName);
            } else {
                sliceValue.setLabel("");
            }
            sliceList.add(sliceValue);
        }
        pd.setValues(sliceList);
    }

    private void buildPieChart(long t) {
        buildPieChartData(t);
        //提前触发deselect的时间
        pieChart.getOnValueTouchListener().onValueDeselected();
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        System.out.println(new Date(date.getCalendar().getTimeInMillis()));
        buildPieChart(date.getCalendar().getTimeInMillis());
    }
}
