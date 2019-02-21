package com.example.a12260.szh.model.chart;

import android.view.View;

public class Chart {
    View chart;

    public Chart(View chartData) {
        this.chart = chartData;
    }

    public View getChart() {
        return chart;
    }

    public Chart setChart(View chart) {
        this.chart = chart;
        return this;
    }
}
