package com.example.a12260.szh.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.example.a12260.szh.Entity.DailyRecord;
import com.example.a12260.szh.R;
import com.example.a12260.szh.logic.chart_drawer.hellochart.HelloChartBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.view.PieChartView;

public class DailyFragment extends Fragment {
    private CalendarView calendarView;
    private PieChartView pieChartView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   ButterKnife.bind(this.getTargetFragment());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily, container, false);
        this.calendarView = view.findViewById(R.id.calendarView);
        this.pieChartView = view.findViewById(R.id.dailyPieChart);
        Bundle bundle = getArguments();
        List<String> strings = bundle.getStringArrayList("packNames");
        long[] longs = bundle.getLongArray("times");
        HelloChartBuilder.buildPieChart(pieChartView, strings, longs);
        return view;
    }

}
