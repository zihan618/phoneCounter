package com.example.a12260.szh.ui.activity;

import android.app.ActionBar;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;

import com.example.a12260.szh.R;
import com.example.a12260.szh.logic.UsageCollectService;
import com.example.a12260.szh.ui.OnlyStatisticBottomAdapter;
import com.example.a12260.szh.utils.MyApplication;
import com.example.a12260.szh.utils.ServiceUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

/**
 * @author 12260
 */
public class MainActivity extends AppCompatActivity {

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
        ActionBar actionBar = getActionBar();
        System.out.println("-------------------------");
        if (actionBar != null) {
            actionBar.hide();
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        List<String> strings = Arrays.asList(getString(R.string.statistics),
//                getString(R.string.plan),
//                getString(R.string.community));
        List<String> strings = Arrays.asList(getString(R.string.daily),
                getString(R.string.weekly),
                getString(R.string.monthly));
        ViewPager downPager = findViewById(R.id.viewpagerDown);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom);
        downPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.navigation_daily);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.navigation_weekly);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.navigation_monthly);
                        break;
                    default: {
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        downPager.setAdapter(new BottomNavAdapter(getSupportFragmentManager(),strings));
        downPager.setAdapter(new OnlyStatisticBottomAdapter(getSupportFragmentManager(), strings));
        bottomNavigationView.setOnNavigationItemSelectedListener(x -> {
            int index = strings.indexOf(x.getTitle());
            downPager.setCurrentItem(index);
            return true;
        });
        if (!hasAuth()) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
        System.out.println("====" + new Date());
        System.out.println("====" + new Date(Calendar.getInstance().getTimeInMillis()));
        if (!ServiceUtils.isServiceRunning(UsageCollectService.class)) {
            System.out.println("现在正要开启服务");
            startService(new Intent(this, UsageCollectService.class));
        } else {
            System.out.println("没有必要开启服务");
        }
    }

}
