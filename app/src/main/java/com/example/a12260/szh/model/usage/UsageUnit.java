package com.example.a12260.szh.model.usage;

import android.graphics.drawable.Drawable;

public class UsageUnit {
    public String getPackageName() {
        return packageName;
    }

    @Override
    public String toString() {
        return "UsageUnit{" +
                "packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", icon=" + icon +
                ", timeSpent=" + timeSpent +
                '}';
    }

    public String getAppName() {
        return appName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public Long getTimeSpent() {
        return timeSpent;
    }

    public UsageUnit setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public UsageUnit setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public UsageUnit setIcon(Drawable icon) {
        this.icon = icon;
        return this;
    }

    public UsageUnit setTimeSpent(Long timeSpent) {
        this.timeSpent = timeSpent;
        return this;
    }

    private String packageName;
    private String appName;
    private Drawable icon;
    private Long timeSpent;


}
