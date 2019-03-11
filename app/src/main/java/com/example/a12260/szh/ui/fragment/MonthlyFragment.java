package com.example.a12260.szh.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.a12260.szh.Entity.DailyRecord;
import com.example.a12260.szh.Entity.MonthRecord;
import com.example.a12260.szh.R;
import com.example.a12260.szh.utils.CalendarUtils;
import com.example.a12260.szh.utils.GreenDaoUtils;
import com.example.a12260.szh.utils.MyApplication;
import com.example.a12260.szh.utils.PieChartColorProvider;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * @author 12260
 */
public class MonthlyFragment extends Fragment {
    private PieChartView pieChart;
    private LineChartView lineChart;
    private PieChartData pieChartData;
    private LineChartData lineChartData;
    private long start;
    private int days;
    private List<String> packNames;
    private List<String> appNames;
    private Map<String, List<DailyRecord>> map;
    private List<Long> sumLineData;
    private long sum;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two_charts, container, false);
        pieChart = view.findViewById(R.id.pie);
        lineChart = view.findViewById(R.id.line);
        Bundle bundle = getArguments();
        start = Objects.requireNonNull(bundle).getLong("start");
        init();
        buildPieChart(start);
        return view;
    }

    private void init() {
        //图标的基本样式设定 和 事件回调函数设置
        pieChartData = new PieChartData();
        pieChartData.setHasLabels(true).setHasCenterCircle(true)
                .setCenterCircleScale(0.618F).setCenterText1FontSize(20);
        pieChartData.setHasCenterCircle(true);
        pieChart.setPieChartData(pieChartData);
        PieChartOnValueSelectListener listener = new MonthlyFragment.Listener();
        pieChart.setValueSelectionEnabled(true);
        pieChart.setViewportCalculationEnabled(true);
        pieChart.setOnValueTouchListener(listener);
        //  pieChart.setAnimation(new RotateAnimation());

        lineChartData = new LineChartData();
        //lineChartData.setAxisYLeft(new Axis().setAutoGenerated(true).setHasTiltedLabels(true));
        // lineChartData.setAxisYLeft(new Axis().setAutoGenerated(false).setHasTiltedLabels(true));
        lineChartData.setAxisXBottom(new Axis().setHasTiltedLabels(true));
        lineChart.setLineChartData(lineChartData);
        lineChart.setValueSelectionEnabled(false);
        lineChart.setZoomEnabled(false);
        //  lineChart.setAnimation(new TranslateAnimation());
    }

    private void buildPieChartData(long timestamp) {
        //看书构建饼图
        List<MonthRecord> monthRecords = GreenDaoUtils.getInstance().listMonthRecordsInMonth(timestamp);
        sum = monthRecords.stream().map(MonthRecord::getTimeSpent).reduce(0L, Long::sum);
        sum = (long) Math.ceil(sum * 1.0 / 60000);
        monthRecords.sort((x, y) -> -Long.compare(x.getTimeSpent(), y.getTimeSpent()));
        packNames = new ArrayList<>(monthRecords.size());
        List<Long> minutes = new ArrayList<>(monthRecords.size());
        for (int i = 0; i < monthRecords.size(); i++) {
            String packName = monthRecords.get(i).getPackageName();
            packNames.add(packName);
            minutes.add((long) Math.ceil(monthRecords.get(i).getTimeSpent() * 1.0 / 60000));
        }
        List<SliceValue> sliceList = new ArrayList<>();
        if (packNames.size() != minutes.size()) {
            System.out.println("长度不一样，先停了");
        }
        map = GreenDaoUtils.getInstance().listDailyRecordsInMonth(timestamp);
        appNames = new ArrayList<>(packNames.size());
        days = CalendarUtils.getDaysPastInMonth(timestamp);
        List<Double> percents = minutes.stream().map(x -> x * 1.0 / sum).collect(Collectors.toList());
        Double percentThreshold = MyApplication.getContext().getResources().getInteger(R.integer.percentLabelThreshold) * 1.0 / 100;
        List<Integer> colors = PieChartColorProvider.getColors(monthRecords.size());
        for (int i = 0; i < packNames.size(); i++) {
            SliceValue sliceValue = new SliceValue(minutes.get(i), colors.get(i));
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
        pieChartData.setValues(sliceList);
        buildLineChart(timestamp);
    }

    private void buildPieChart(long t) {
        buildPieChartData(t);
        //提前触发deselect的时间
        pieChart.getOnValueTouchListener().onValueDeselected();
    }

    private void buildLineChart(long t) {
        //构建按总时间来话折线图的数据
        List<List<Long>> data = map.values().stream().map(x -> GreenDaoUtils.getInstance().buildDailyTime(t, days, x)).collect(Collectors.toList());
        sumLineData = new ArrayList<>(days);
        for (int i = 0; i < days; i++) {
            long tmp = 0;
            for (int j = 0; j < data.size(); j++) {
                tmp += data.get(j).get(i);
            }
            sumLineData.add(tmp);
        }
        buildLineChartData(sumLineData);
    }

    private void buildLineChartData(List<Long> times) {
        if (lineChartData.getLines().isEmpty() || lineChartData.getLines().get(0).getValues().size() != times.size()) {
            List<AxisValue> axisXValues = new ArrayList<>();
            List<Line> lines = new ArrayList<>();
            List<PointValue> values = new ArrayList<>();
            for (int j = 0; j < times.size(); ++j) {
                int x = j + 1;
                values.add(new PointValue(x, (float) Math.ceil(times.get(j) * 1.0 / 60000)));
                axisXValues.add(new AxisValue(x));
            }
            Line line = new Line(values);
            line.setColor(ChartUtils.pickColor())
                    .setShape(ValueShape.CIRCLE)
                    .setPointColor(getResources().getColor(R.color.c7, null))
                    .setPointRadius(4)
                    .setColor(getResources().getColor(R.color.c1, null))
                    .setFilled(true)
                    .setAreaTransparency(64)
                    .setCubic(true)
                    .setHasLabels(true)
                    .setHasLabelsOnlyForSelected(false);
            lines.add(line);

            lineChartData.setLines(lines);
            lineChartData.getAxisXBottom().setValues(axisXValues);
        } else {
            Line line = lineChartData.getLines().get(0);
            for (int i = 0; i < times.size(); i++) {
                PointValue value = line.getValues().get(i);
                // Change target only for Y value.
                value.setTarget(value.getX(), (float) Math.ceil(times.get(i) * 1.0 / 60000));
            }
        }

    }

    class Listener implements PieChartOnValueSelectListener {

        //TODO: handler的内存泄漏问题

        @SuppressLint("HandlerLeak")
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String packName = (String) msg.obj;
                List<DailyRecord> dailyRecords = map.get(packName);
                List<Long> longs = GreenDaoUtils.getInstance().buildDailyTime(start, days, dailyRecords);
                lineChart.cancelDataAnimation();
                buildLineChartData(longs);
                lineChart.startDataAnimation(300);
            }
        };

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            pieChart.cancelDataAnimation();
            pieChartData.setCenterText1(appNames.get(arcIndex)).setCenterText2(
                    String.format(getString(R.string.usageLabel), (long) (value.getValue())));
            pieChart.startDataAnimation(300);
            Message message = new Message();
            message.obj = packNames.get(arcIndex);
            handler.sendMessage(message);
        }

        @Override
        public void onValueDeselected() {
            pieChart.cancelDataAnimation();
            pieChartData.setCenterText1(getString(R.string.total)).setCenterText2(
                    String.format(getString(R.string.usageLabel), (sum)));
            pieChart.startDataAnimation(300);
            lineChart.cancelDataAnimation();
            buildLineChartData(sumLineData);
            lineChart.startDataAnimation(300);
        }
    }
}
