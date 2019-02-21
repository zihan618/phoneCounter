package com.example.a12260.szh.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.a12260.szh.R;
import com.example.a12260.szh.ui.StatisticsAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import static com.example.a12260.szh.utils.MyApplication.fragmentManager;

public class MainStatisticsFragment extends Fragment {


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("dfff", "stasts");
        View view = inflater.inflate(R.layout.fragment_stastics, container, false);
        ViewPager viewPager = view.findViewById(R.id.viewpagerUp);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        RecyclerView recyclerView = view.findViewById(R.id.chartList);
        List<String> titles = Stream.of(getString(R.string.today),
                getString(R.string.thisWeek),
                getString(R.string.thisMonth)).collect(Collectors.toList());
        titles.forEach( x -> tabLayout.addTab(tabLayout.newTab().setText(x)));
        viewPager.setAdapter(new StatisticsAdapter(fragmentManager,titles));
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    void prepare() {

    }
}
