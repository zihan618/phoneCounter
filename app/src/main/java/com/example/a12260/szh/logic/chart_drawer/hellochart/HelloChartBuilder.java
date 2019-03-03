package com.example.a12260.szh.logic.chart_drawer.hellochart;

import com.example.a12260.szh.R;
import com.example.a12260.szh.model.chart.Chart;
import com.example.a12260.szh.model.usage.UsageUnit;
import com.example.a12260.szh.utils.MyApplication;

import org.apache.commons.lang3.StringUtils;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

public class HelloChartBuilder {
    public static Function<UsageUnit, String> appNameMapper = x -> x.getAppName();
    public static Function<UsageUnit, Long> columnValueMaper = x -> x.getTimeSpent();


    //按照应用的使用时长画柱状图
    public static Chart buildBarChart(List<UsageUnit> usageUnits) {
        ColumnChartView barChart = new ColumnChartView(MyApplication.getContext());
        List<Column> columns = new ArrayList<>();
        List<AxisValue> x = new ArrayList<>();
//        barChart.setMinimumHeight(300);
        List<String> xData = usageUnits.stream().map(appNameMapper).collect(Collectors.toList());
        List<Long> columnsData = usageUnits.stream().map(columnValueMaper).collect(Collectors.toList());
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < usageUnits.size(); i++) {
            columns.add(new Column(
                    Arrays.asList(new SubcolumnValue(Math.round(columnsData.get(i)  * 1.0 / 60000), ChartUtils.pickColor()))));
            x.add(new AxisValue(i).setLabel(xData.get(i)));
        }
        ColumnChartData columnChartData = new ColumnChartData(columns);
        Axis axis = new Axis(x);
        columnChartData.setAxisXBottom(axis);
        barChart.setColumnChartData(columnChartData);
        return new Chart(barChart);
    }

    public static Chart buildPieChart(List<UsageUnit> usageUnits) {
        PieChartView pieChart = new PieChartView(MyApplication.getContext());
        List<SliceValue> sliceList = new ArrayList<>();
        Long allTime = usageUnits.stream().mapToLong(UsageUnit::getTimeSpent).sum();
        List<Double> percents = usageUnits.stream().map(UsageUnit::getTimeSpent).map( x -> x * 1.0 / allTime).collect(Collectors.toList());
        Double percentThreshold = MyApplication.getContext().getResources().getInteger(R.integer.percentLabelThreshold) * 1.0 / 100;
        for(int i=0;i < usageUnits.size();i++){
            UsageUnit usageUnit = usageUnits.get(i);
            SliceValue sliceValue = new SliceValue(columnValueMaper.apply(usageUnit), ChartUtils.pickColor());
            if (percents.get(i) >= percentThreshold) {
                sliceValue.setLabel(usageUnit.getAppName());
            } else {
                sliceValue.setLabel("");
            }
            sliceList.add(sliceValue);
        }
        PieChartData pd=new PieChartData();//实例化PieChartData对象
        pd.setValues(sliceList).setHasLabels(true).setHasCenterCircle(true)
                .setCenterCircleScale(0.618F);//.setCenterText1("233sb").setCenterText2("6666");
        pieChart.setPieChartData(pd);//将数据设
        PieChartOnValueSelectListener listener = new PieChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int arcIndex, SliceValue value) {
            }

            @Override
            public void onValueDeselected() {
            }
        };
        pieChart.setValueSelectionEnabled(true);
        pieChart.setViewportCalculationEnabled(true);
        pieChart.setOnValueTouchListener(listener);
        return new Chart(pieChart);
    }

    public static Chart buildBlankLineChart(int nodeNumber) {
        LineChartView chart = new LineChartView(MyApplication.getContext());
                List<AxisValue> axisXValues = new ArrayList<>();
        List<Line> lines = new ArrayList<>();
        List<PointValue> values = new ArrayList<>();
        for (int j = 0; j < nodeNumber; ++j) {
            values.add(new PointValue(j + 1, 0));
            axisXValues.add(new AxisValue(j+1));
        }
        Line line = new Line(values);
        line.setColor(ChartUtils.pickColor())
                .setPointRadius(2)
                .setShape(ValueShape.CIRCLE)
                .setHasLabels(true)
                .setCubic(true)
                .setHasLabelsOnlyForSelected(false)
                .setHasLines(true)
                .setHasPoints(true)
                .setPointColor(ChartUtils.pickColor());
        //节点的形状
        //是否显示标签
        //标签是否只能选中
        //是否显示折线
        //是否显示节点
        lines.add(line);

        LineChartData data = new LineChartData(lines);
        data.setAxisXBottom(new Axis(axisXValues).setHasTiltedLabels(true));
        data.setAxisYLeft(new Axis().setAutoGenerated(true).setHasTiltedLabels(true));
        chart.setValueSelectionEnabled(false);
        chart.setLineChartData(data);
        return new Chart(chart);
    }
    public static Chart buildLineChart(List<UsageUnit> usageUnits) {
//        LineChartView chart = new LineChartView(MyApplication.getContext());
//        // Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.
//        List<AxisValue> axisXValues = new ArrayList<>();
//        List<Line> lines = new ArrayList<Line>();
//        List<PointValue> values = new ArrayList<>();
//        for (int j = 0; j < usageUnits.size(); ++j) {
//            UsageUnit usageUnit = usageUnits.get(j);
//            values.add(new PointValue(j, new Random().nextInt(100)));
//
//            axisXValues.add(new AxisValue(j).setLabel());
//        }
//
//        Line line = new Line(values);
//        line.setColor(ChartUtils.pickColor())
//                .setPointRadius(2)
//                .setShape(ValueShape.CIRCLE)
//                .setHasLabels(true)
//                .setCubic(true)
//                .setHasLabelsOnlyForSelected(false)
//                .setHasLines(true)
//                .setHasPoints(true)
//                .setPointColor(ChartUtils.pickColor());
//        //节点的形状
//        //是否显示标签
//        //标签是否只能选中
//        //是否显示折线
//        //是否显示节点
//        lines.add(line);
//
//        LineChartData data = new LineChartData(lines);
//        data.setAxisXBottom(new Axis(axisXValues).setHasTiltedLabels(true));
//        data.setBaseValue(Float.NEGATIVE_INFINITY);
//        data.setAxisYLeft(new Axis().setAutoGenerated(true).setHasTiltedLabels(true));
//        chart.setValueSelectionEnabled(false);
//        chart.setLineChartData(data);
        return null;
    }
}
