package com.example.a12260.szh.Entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
@Entity
public class MonthRecord {
    private Long timestamp;
    private String packageName;
    private String appName;
    private String timeSpent;
    @Generated(hash = 1897853176)
    public MonthRecord(Long timestamp, String packageName, String appName,
            String timeSpent) {
        this.timestamp = timestamp;
        this.packageName = packageName;
        this.appName = appName;
        this.timeSpent = timeSpent;
    }
    @Generated(hash = 1524030910)
    public MonthRecord() {
    }
    public Long getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    public String getPackageName() {
        return this.packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public String getAppName() {
        return this.appName;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }
    public String getTimeSpent() {
        return this.timeSpent;
    }
    public void setTimeSpent(String timeSpent) {
        this.timeSpent = timeSpent;
    }
}
