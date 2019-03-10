package com.example.a12260.szh.utils;

import com.example.a12260.szh.R;

import java.util.ArrayList;
import java.util.List;

public class PieChartColorProvider {
    static int[] colors = new int[]{R.color.c1, R.color.c2, R.color.c3, R.color.c4, R.color.c5, R.color.c6, R.color.c7, R.color.c8, R.color.c9, R.color.c10, R.color.c11, R.color.c12, R.color.c13, R.color.c14, R.color.c15, R.color.c16, R.color.c17, R.color.c18, R.color.c19, R.color.c20};

    public static List<Integer> getColors(int n) {
        List<Integer> res = new ArrayList<>(n);
        if (n <= colors.length) {
            for (int i = 0; i < n; i++) {
                res.add(colors[i]);
            }
        } else {
            int k = n % colors.length == 0 ? n : (n / colors.length + 1) * colors.length;
            int time = k / colors.length;
            while (res.size() < k) {
                int index = res.size() / time;
                for (int i = 0; i < time; i++) {
                    res.add(colors[index]);
                }
            }
        }
        return res.subList(0, n);
    }
}
