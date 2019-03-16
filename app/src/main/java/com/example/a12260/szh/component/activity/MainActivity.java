package com.example.a12260.szh.component.activity;

import android.app.ActionBar;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a12260.szh.Entity.DailyRecord;
import com.example.a12260.szh.Entity.PackageApp;
import com.example.a12260.szh.R;
import com.example.a12260.szh.component.ActonTickReceiver;
import com.example.a12260.szh.component.UsageCollectService;
import com.example.a12260.szh.component.OnlyStatisticBottomAdapter;
import com.example.a12260.szh.utils.CalendarUtils;
import com.example.a12260.szh.utils.GreenDaoUtils;
import com.example.a12260.szh.utils.MyApplication;
import com.example.a12260.szh.utils.SharedPreferManager;
import com.example.a12260.szh.utils.Server;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

/**
 * @author 12260
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    ActonTickReceiver actonTickReceiver;
    BottomNavigationView bottomNavigationView;
    private DrawerLayout drawer;
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

    void initBottomNavigation() {
        List<String> strings = Arrays.asList(getString(R.string.daily),
                getString(R.string.weekly),
                getString(R.string.monthly));
        ViewPager downPager = findViewById(R.id.viewpagerDown);
        bottomNavigationView = findViewById(R.id.bottom);
        //  bottomNavigationView.setBackgroundColor(getColor(R.color.gray));
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
    }

    void requestPermission() {
        if (!hasAuth()) {
            new AlertDialog.Builder(this).setTitle("权限").setMessage("为了应用可以正常计时，需要获取部分权限")
                    .setNegativeButton("取消", (x, y) -> {
                        Snackbar.make(bottomNavigationView, "未开启权限，无法进行统计", Snackbar.LENGTH_LONG)
                                .setAction("去开启", z -> startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))).show();
                    }).setPositiveButton("确定", (x, y) -> {
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            }).show();
        }
    }

    void startMyService() {
        if (!MyApplication.isServiceRunning(UsageCollectService.class)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, UsageCollectService.class));
            } else {
                startService(new Intent(this, UsageCollectService.class));
            }
            System.out.println("现在正要开启服务");
//            startService(new Intent(this, UsageCollectService.class));
        } else {
            System.out.println("没有必要开启服务");
        }
    }

    void startMyReceiver() {
        actonTickReceiver = new ActonTickReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(actonTickReceiver, intentFilter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //  GreenDaoUtils.getInstance().generateDummyData(CalendarUtils.getIntervalOfMonth().getStart(), CalendarUtils.getFirstTimestampOfDay());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        View headerView = navigationView.getHeaderView(0);
        LinearLayout nav_header = headerView.findViewById(R.id.nav_header);
        ImageView login = nav_header.findViewById(R.id.imageView_nav_header);
        TextView textView = nav_header.findViewById(R.id.text_nav_header);
        SharedPreferences sp = getSharedPreferences(getString(R.string.loginReference), Context.MODE_PRIVATE);
        int id = sp.getInt("userId", 0);
        String name = sp.getString("name", "");
        if (id == 0) {
            textView.setText(R.string.nav_header_text);
            login.setImageDrawable(getDrawable(R.drawable.unlogin));
        } else {
            textView.setText(name);
            login.setImageDrawable(getDrawable(R.drawable.longin));
        }
        nav_header.setOnClickListener(this);
        initBottomNavigation();
        requestPermission();
        startMyService();
        startMyReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(actonTickReceiver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_header:
                if (!SharedPreferManager.isLogin()) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    drawer.closeDrawer(GravityCompat.START);
                }
                break;
            default: {
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.paceWithServer:
                if (SharedPreferManager.isLogin()) {
                    long today = CalendarUtils.getFirstTimestampOfDay();
                    List<DailyRecord> dailyRecords = GreenDaoUtils.getInstance().listAllDailyRecords().stream().filter(x -> !x.getTimestamp().equals(today)).collect(Collectors.toList());
                    List<PackageApp> packageApps = GreenDaoUtils.getInstance().listAllPackageApp();
                    new Thread(() -> {
                        try {
                            long id = (long) SharedPreferManager.getId();
                            Server.pushRecord(dailyRecords, id);
                            Server.pushKV(packageApps, id);
                            List<PackageApp> packageApps2 = Server.getKV(id);
                            List<DailyRecord> dailyRecords2 = Server.getRecords(id);

                            GreenDaoUtils.getInstance().refreshDB(dailyRecords2, packageApps2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
                break;
            case R.id.filterRule:
                Intent intent = new Intent(MainActivity.this, FilterRuleSetActivity.class);
                startActivity(intent);
                drawer.closeDrawer(GravityCompat.START);
                break;
            default: {
            }
        }
        return false;
    }
}
