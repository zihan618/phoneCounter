package com.example.a12260.szh.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.example.a12260.szh.R;
import com.example.a12260.szh.ui.ChartsAdapter;
import com.example.a12260.szh.utils.MyApplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SubStatisticFragment extends Fragment {
    public View chart;
    public ChartsAdapter chartsAdapter;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmenr_substatistic, container, false);
    //    ViewParent parent = chart.getParent();
        RecyclerView recyclerView = view.findViewById(R.id.chartList);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyApplication.getContext()));
        recyclerView.setAdapter(chartsAdapter);
//        if ( parent!= null ) {
//            if (parent instanceof ViewGroup) {
//                ((ViewGroup)parent).removeView(chart);
//            }
//        }
       // ((ViewGroup)view.getRootView()).addView(chart);

        return view;
    }


}
