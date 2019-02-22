package com.example.a12260.szh.ui;

import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.example.a12260.szh.logic.data_producer.APIUsageProvider;
import com.example.a12260.szh.utils.CalendarUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

public class ChartsDependency {
    private Map<View, View> charts = new HashMap();
    private Map chartsData = new HashMap();
    private ChartsDependency() {}
    private static ChartsDependency chartsDependency = new ChartsDependency();
    public static ChartsDependency getInstance() {
        return chartsDependency;
    }

    static class Listener implements PieChartOnValueSelectListener {
        private Long startTime;
        private int day;
        public Listener(Long startTime, int day, PieChartView pieChartView) {
            this.startTime = startTime;
            this.day = day;
            this.pieChartView = pieChartView;
        }

         Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                SliceValue value = (SliceValue) msg.obj;
                LineChartView lineChartView = (LineChartView) chartsDependency.charts.get(pieChartView);
                lineChartView.cancelDataAnimation();
                LineChartData lineChartData = (LineChartData) chartsDependency.chartsData.get(pieChartView.getChartData());
                Line line = lineChartData.getLines().get(0).setColor(ChartUtils.pickColor());
                List<Long> dailyUsageList = APIUsageProvider.getInstance().getDailyUsageStatsInWeek(new String(value.getLabelAsChars()), CalendarUtils.getDay(startTime), day);
                System.out.println("5678906789" + dailyUsageList.toString());
                for (int i = 0; i < day; i++) {
                    PointValue pointValue = line.getValues().get(i);
                    Long aLong = dailyUsageList.get(i);
                    pointValue.setTarget(i, aLong / 60000);
                }
                lineChartView.startDataAnimation(300);
            }
        };

        private PieChartView pieChartView;
        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
          new Thread(() -> {
              Message message = new Message();
              message.obj = value;
              handler.sendMessage(message);
          }).start();

        }
        @Override
        public void onValueDeselected() {

        }
    }

    public static void bindTwoCharts(View clickedChart, View listenerChart, long startTime,int days) {
        if (clickedChart instanceof PieChartView &&
                listenerChart instanceof LineChartView) {
            chartsDependency.charts.put(clickedChart, listenerChart);
            chartsDependency.chartsData.put(((PieChartView) clickedChart).getChartData(),
                    ((LineChartView) listenerChart).getChartData());
            Listener listener = new Listener(startTime, days, (PieChartView) clickedChart);
            ((PieChartView) clickedChart).setOnValueTouchListener(listener);
        }
    }

}
