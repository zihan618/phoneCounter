//package com.example.a12260.szh.service.chart_drawer.mpandriodchart;
//
//import com.example.a12260.szh.model.chart.Chart;
//import com.example.a12260.szh.model.chart.MpChart;
//import com.example.a12260.szh.model.usage.UsageUnit;
//import com.example.a12260.szh.utils.MyApplication;
//import com.github.mikephil.charting.charts.BarChart;
//import com.github.mikephil.charting.data.BarData;
//import com.github.mikephil.charting.data.BarDataSet;
//import com.github.mikephil.charting.data.BarEntry;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class BarChartDrawer extends BaseDrawer {
//
//    private static BarChartDrawer barChartDrawer = new BarChartDrawer();
//    public static Chart newInstance(List<UsageUnit> data, String title) {
//        return new MpChart(barChartDrawer.parseDate(data, title));
//    }
//
//    private BarChart parseDate(List<UsageUnit> data, String title) {
//        BarChart barChart = new BarChart(MyApplication.getContext());
//        barChart.setMinimumHeight(1200);
//        super.parse(barChart);
//        List<String> xData = data.stream()
//                .map(UsageUnit::getAppName)
//                .collect(Collectors.toList());
//        List<BarEntry> values = new ArrayList<>();
//        for (int i = 0; i < data.size(); i++) {
//            values.add(new BarEntry(i, Math.round(data.get(i).getTimeSpent()/1000 * 1.0 / 60)));
//        }
//        barChart.getXAxis().setValueFormatter( (value, axis) -> xData.get((int) value));
//        barChart.setData(new BarData(new BarDataSet(values, title)));
//        return barChart;
//    }
//}
