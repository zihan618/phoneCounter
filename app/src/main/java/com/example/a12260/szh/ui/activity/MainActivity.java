package com.example.a12260.szh.ui.activity;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toolbar;

import com.example.a12260.szh.R;
import com.example.a12260.szh.logic.UsageCollectService;
import com.example.a12260.szh.ui.BottomNavAdapter;
import com.example.a12260.szh.ui.StatisticsAdapter;
import com.example.a12260.szh.utils.MyApplication;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
//@BindView(R.id.tabLayout)
//    TabLayout tabLayout;
//    @BindView(R.id.viewpagerUp)
//    ViewPager viewPager;

    @BindView(R.id.bottom)
    BottomNavigationView bottomNavigationView;

    @BindView(R.id.viewpagerDown)
    ViewPager downPager;
    boolean hasAuth() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApplication.fragmentManager = getSupportFragmentManager();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        List<String> titles = Stream.of(getString(R.string.today),
                getString(R.string.thisWeek),
                getString(R.string.thisMonth)).collect(Collectors.toList());
//        titles.forEach( x -> tabLayout.addTab(tabLayout.newTab().setText(x)));
  //      viewPager.setAdapter(new StatisticsAdapter(getSupportFragmentManager(), new ArrayList<>(),titles));
//        tabLayout.setupWithViewPager(viewPager);
        List<String> strings = Arrays.asList(getString(R.string.statistics),
                getString(R.string.plan),
                getString(R.string.community));
        downPager.setAdapter(new BottomNavAdapter(getSupportFragmentManager(),strings));
        bottomNavigationView.setOnNavigationItemSelectedListener( x -> {
            int index = strings.indexOf(x.getTitle());
            downPager.setCurrentItem(index);
            return true;
        });
        if (!hasAuth()) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
        System.out.println("====" + new Date());
        System.out.println("====" + new Date(Calendar.getInstance().getTimeInMillis()));
        startService(new Intent(this, UsageCollectService.class));
    }

}
