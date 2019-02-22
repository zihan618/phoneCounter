package com.example.a12260.szh.Entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class DailyRecord {
    private Long timestamp;
    private String packageName;
    private String appName;
    private String timeSpent;
    @Generated(hash = 384168263)
    public DailyRecord(Long timestamp, String packageName, String appName,
            String timeSpent) {
        this.timestamp = timestamp;
        this.packageName = packageName;
        this.appName = appName;
        this.timeSpent = timeSpent;
    }
    @Generated(hash = 1812185311)
    public DailyRecord() {
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
