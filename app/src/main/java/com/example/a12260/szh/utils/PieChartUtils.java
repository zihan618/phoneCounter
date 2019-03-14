package com.example.a12260.szh.utils;

import android.content.res.TypedArray;

import com.example.a12260.szh.R;

import java.util.ArrayList;
import java.util.List;

public class PieChartUtils {
    static int[] colors;

    static {
        TypedArray ta = MyApplication.getContext().getResources().obtainTypedArray(R.array.colorArray);
        colors = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            colors[i] = ta.getColor(i, 0);
        }
    }
    public static List<Integer> getColors(int n) {
        List<Integer> res = new ArrayList<>();
        if (n <= colors.length) {
            for (int i = 0; i < n; i++) {
                res.add(colors[i]);
            }
        } else {
            for (int i = 0; i < colors.length; i++) {
                res.add(colors[i]);
            }
            while (res.size() < n) {
                res.add(colors[colors.length - 1]);
            }
        }
        return res;
    }

    public static String buildCenterText2(int minute) {
        return minute < 59 ?
                String.format(MyApplication.getContext().getString(R.string.WeekMonthReminder), minute)
                : String.format(MyApplication.getContext().getString(R.string.WeekMonthReminder2), minute / 60, minute % 60);
    }
}
