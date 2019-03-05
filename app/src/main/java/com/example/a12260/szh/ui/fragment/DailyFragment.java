package com.example.a12260.szh.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.example.a12260.szh.R;
import com.example.a12260.szh.utils.CalendarUtils;
import com.example.a12260.szh.utils.GreenDaoUtils;
import com.example.a12260.szh.utils.MyApplication;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
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
public class DailyFragment extends Fragment implements CalendarView.OnDateChangeListener {
    private CalendarView calendarView;
    private PieChartView pieChart;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   ButterKnife.bind(this.getTargetFragment());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one_chart, container, false);
        this.calendarView = view.findViewById(R.id.calendarView);
        this.pieChart = view.findViewById(R.id.dailyPieChart);
        Bundle bundle = getArguments();
        List<String> strings = bundle.getStringArrayList("packNames");
        long[] longs = bundle.getLongArray("times");
        buildPieChart(strings, longs);
        return view;
    }

    private void buildPieChart(List<String> labels, long[] times) {
        System.out.println(times.length);
        Arrays.stream(times).forEach(System.out::println);
        System.out.println(labels);
        List<Long> longs = new ArrayList<>(times.length);
        Arrays.stream(times).forEach(longs::add);
        List<SliceValue> sliceList = new ArrayList<>();
        if (labels.size() != longs.size()) {
            System.out.println(labels.size() + "    " + longs.size());
            System.out.println("长度不一样，先停了");
            return;
        }

        Long allTime = longs.stream().reduce(0L, Long::sum);
        List<String> appNames = new ArrayList<>(labels.size());

        List<Double> percents = longs.stream().map(x -> x * 1.0 / allTime).collect(Collectors.toList());
        Double percentThreshold = MyApplication.getContext().getResources().getInteger(R.integer.percentLabelThreshold) * 1.0 / 100;
        for (int i = 0; i < labels.size(); i++) {
            SliceValue sliceValue = new SliceValue(longs.get(i), ChartUtils.pickColor());
            String appName = GreenDaoUtils.getInstance().getAppName(labels.get(i));
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
        PieChartData pd = new PieChartData();
        pd.setValues(sliceList).setHasLabels(true).setHasCenterCircle(true)
                .setCenterCircleScale(0.618F).setCenterText1FontSize(20);
        pieChart.setPieChartData(pd);
        PieChartOnValueSelectListener listener = new PieChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int arcIndex, SliceValue value) {
                System.out.println(value);
                pieChart.cancelDataAnimation();
                pd.setCenterText1(appNames.get(arcIndex)).setCenterText2(Long.toString((long) (value.getValue())));
                pieChart.startDataAnimation(300);
            }

            @Override
            public void onValueDeselected() {
                pieChart.cancelDataAnimation();
                pd.setCenterText1("总计").setCenterText2(Long.toString(allTime));
                pieChart.startDataAnimation(300);
            }
        };
        pieChart.setValueSelectionEnabled(true);
        pieChart.setViewportCalculationEnabled(true);
        pieChart.setOnValueTouchListener(listener);
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
        // LocalDateTime localDateTime = LocalDateTime.of(year, month, dayOfMonth, 0, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, month);
        long t = CalendarUtils.getFirstTimestampOfDay(calendar.getTimeInMillis());

    }

    private void refresh() {

    }
}
