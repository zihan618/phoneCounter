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
import com.example.a12260.szh.ui.BottomNavAdapter;
import com.example.a12260.szh.ui.StatisticsAdapter;
import com.example.a12260.szh.utils.MyApplication;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
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
//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.PACKAGE_USAGE_STATS) !=
//                PackageManager.PERMISSION_GRANTED) {
//           // ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.PACKAGE_USAGE_STATS}, 1);
//            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
//        }
    }

   // @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.button1_:
//                Calendar cal = Calendar.getInstance();
//                cal.add(Calendar.DATE, -1);
//                AbstractUsageProvider usageProvider = new APIUsageProvider();
//                List<UsageUnit> queryUsageStats = usageProvider.getUsageStats(UsageStatsManager.INTERVAL_DAILY, cal.getTimeInMillis(), System.currentTimeMillis());
//
//                if (queryUsageStats.isEmpty()) {
//                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
//                    System.out.println("打开活动了现在并没有停止！");
//                }else {
//                    BarChart barchart = new BarChart(getApplicationContext());
//                    barchart.getLegend().setForm(Legend.LegendForm.CIRCLE);//这是左边显示小图标的形状
//                    barchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
//                    barchart.getXAxis().setDrawGridLines(false);//不显示网格
//                    barchart.getAxisRight().setEnabled(false);//右侧不显示Y轴
//                    barchart.getAxisLeft().setDrawGridLines(false);//不设置Y轴网格
//                    barchart.setMinimumHeight(1200);
//
//                    //barchart.animateXY(1000, 2000);//设置动画
//                    LinearLayout linearLayout = findViewById(R.id.layout1);
//
//                    new BarChartDrawer().parseDate(barchart, queryUsageStats, "tt2");
//                    linearLayout.addView(barchart);
//                }
//                break;
//            default: {
//                Log.d("szh223", "others");
//            }
      //  }
        //  Toast.makeText(MainActivity.this, "szher", Toast.LENGTH_LONG).show();
    }



//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//        } else {
//            Snackbar.make(viewPager, getString(R.string.unauthorizedMessage),  Snackbar.LENGTH_INDEFINITE)
//                    .setAction(getString(R.string.resetAuth), x -> ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.PACKAGE_USAGE_STATS}, 1))
//                    .show();
//        }
//    }
}
