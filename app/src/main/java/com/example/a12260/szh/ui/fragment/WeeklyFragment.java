package com.example.a12260.szh.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.a12260.szh.R;
import com.example.a12260.szh.logic.data_producer.APIUsageProvider;
import com.example.a12260.szh.utils.CalendarUtils;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * @author 12260
 */
public class WeeklyFragment extends Fragment {
    private PieChartView pieChart;
    private LineChartView lineChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly, container, false);
        pieChart = view.findViewById(R.id.weekly_pie);
        lineChart = view.findViewById(R.id.weekly_line);
        return view;
    }

    void bindCharts() {
        Bundle bundle = getArguments();
        long start = Objects.requireNonNull(bundle).getLong("start");
        int days = bundle.getInt("days");
        Listener listener = new Listener(start, days);
        pieChart.setOnValueTouchListener(listener);
    }

    class Listener implements PieChartOnValueSelectListener {
        private Long startTime;
        private int day;

        public Listener(Long startTime, int day) {
            this.startTime = startTime;
            this.day = day;
        }

        //TODO: handler的内存泄漏问题
        @SuppressLint("HandlerLeak")
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                SliceValue value = (SliceValue) msg.obj;
                Objects.requireNonNull(lineChart).cancelDataAnimation();
                LineChartData lineChartData = lineChart.getLineChartData();
                assert lineChartData != null;
                Line line = lineChartData.getLines().get(0).setColor(ChartUtils.pickColor());
                List<Long> dailyUsageList = APIUsageProvider.getInstance().getDailyUsageStatsInWeek(new String(value.getLabelAsChars()), CalendarUtils.getDay(startTime), day);
                for (int i = 0; i < day; i++) {
                    PointValue pointValue = line.getValues().get(i);
                    Long aLong = dailyUsageList.get(i);
                    pointValue.setTarget(i, aLong / 60000);
                }
                lineChart.startDataAnimation(300);
            }
        };

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            Message message = new Message();
            message.obj = value;
            handler.sendMessage(message);
        }

        @Override
        public void onValueDeselected() {

        }
    }


}
