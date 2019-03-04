package com.example.a12260.szh.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.a12260.szh.Entity.DailyRecord;
import com.example.a12260.szh.R;
import com.example.a12260.szh.utils.GreenDaoUtils;
import com.example.a12260.szh.utils.MyApplication;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
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
    private long start;
    private int days;
    private List<String> packNames;
    private long[] times;
    List<String> appNames;
    Map<String, List<DailyRecord>> map;
    List<Long> sumLineData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monthly, container, false);
        pieChart = view.findViewById(R.id.monthly_pie);
        lineChart = view.findViewById(R.id.monthly_line);
        Bundle bundle = getArguments();
        start = Objects.requireNonNull(bundle).getLong("start");
        days = bundle.getInt("days");
        packNames = bundle.getStringArrayList("packNames");
        times = bundle.getLongArray("times");
        map = GreenDaoUtils.getInstance().listDailyRecordsInMonth(start);
        init();
        return view;
    }

    private void init() {
        List<Long> longs = new ArrayList<>(times.length);
        Arrays.stream(times).forEach(longs::add);
        List<SliceValue> sliceList = new ArrayList<>();
        if (packNames.size() != longs.size()) {
            System.out.println(packNames.size() + "    " + longs.size());
            System.out.println("长度不一样，先停了");
            return;
        }

        Long allTime = longs.stream().reduce(0L, Long::sum);
        appNames = new ArrayList<>(packNames.size());

        List<Double> percents = longs.stream().map(x -> x * 1.0 / allTime).collect(Collectors.toList());
        Double percentThreshold = MyApplication.getContext().getResources().getInteger(R.integer.percentLabelThreshold) * 1.0 / 100;
        for (int i = 0; i < packNames.size(); i++) {
            SliceValue sliceValue = new SliceValue(longs.get(i), ChartUtils.pickColor());
            String appName = MyApplication.getAppName(packNames.get(i));
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
        MonthlyFragment.Listener listener = new MonthlyFragment.Listener(start, days, packNames, times);
        pieChart.setOnValueTouchListener(listener);
        pieChart.setValueSelectionEnabled(true);
        pieChart.setViewportCalculationEnabled(true);
        pieChart.setOnValueTouchListener(listener);

        List<List<Long>> data = map.values().stream().map(x -> GreenDaoUtils.getInstance().buildDailyTime(start, days, x)).collect(Collectors.toList());
        sumLineData = new ArrayList<>(days);
        for (int i = 0; i < days; i++) {
            long tmp = 0;
            for (int j = 0; j < data.size(); j++) {
                tmp += data.get(j).get(i);
            }
            sumLineData.add(tmp);
        }
        buildLineChart(sumLineData);

    }

    private void buildLineChart(List<Long> times) {
        List<AxisValue> axisXValues = new ArrayList<>();
        List<Line> lines = new ArrayList<>();
        List<PointValue> values = new ArrayList<>();
        for (int j = 0; j < times.size(); ++j) {
            int x = j + 1;
            values.add(new PointValue(x, times.get(j)));
            axisXValues.add(new AxisValue(x));
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
        lines.add(line);

        LineChartData data = new LineChartData(lines);
        data.setAxisXBottom(new Axis(axisXValues).setHasTiltedLabels(true));
        data.setAxisYLeft(new Axis().setAutoGenerated(true).setHasTiltedLabels(true));
        lineChart.setValueSelectionEnabled(false);
        lineChart.setLineChartData(data);
    }

    private void updateLineChart(List<Long> data) {
        Objects.requireNonNull(lineChart).cancelDataAnimation();
        LineChartData lineChartData = lineChart.getLineChartData();
        assert lineChartData != null;
        Line line = lineChartData.getLines().get(0);
        for (int i = 0; i < data.size(); i++) {
            PointValue pointValue = line.getValues().get(i);
            pointValue.setTarget(i + 1, data.get(i));
        }
        lineChart.startDataAnimation(300);
    }


    class Listener implements PieChartOnValueSelectListener {
        private Long startTime;
        List<String> packNames;
        long[] longs;
        int days = 7;

        public Listener(Long startTime, int days, List<String> strings, long[] longs) {
            this.startTime = startTime;
            this.longs = longs;
            this.packNames = strings;
            this.days = days;
        }

        //TODO: handler的内存泄漏问题
        @SuppressLint("HandlerLeak")
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String packName = (String) msg.obj;
                List<DailyRecord> dailyRecords = map.get(packName);
                List<Long> longs = GreenDaoUtils.getInstance().buildDailyTime(startTime, days, dailyRecords);
                updateLineChart(longs);
            }
        };

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            pieChart.cancelDataAnimation();
            pieChart.getPieChartData().setCenterText1(appNames.get(arcIndex)).setCenterText2(Long.toString(times[arcIndex]));
            pieChart.startDataAnimation(300);
            Message message = new Message();
            message.obj = packNames.get(arcIndex);
            handler.sendMessage(message);
        }

        @Override
        public void onValueDeselected() {
            pieChart.cancelDataAnimation();
            pieChart.getPieChartData().setCenterText1("总计").setCenterText2(Long.toString(Arrays.stream(times).sum()));
            pieChart.startDataAnimation(300);
            updateLineChart(sumLineData);
        }
    }

}
