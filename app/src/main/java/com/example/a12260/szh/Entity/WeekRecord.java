package com.example.a12260.szh.Entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
@Entity
public class WeekRecord {
    private Long timestamp;
    private String packageName;
    private Long timeSpent;
    @Generated(hash = 150897922)
    public WeekRecord(Long timestamp, String packageName, Long timeSpent) {
        this.timestamp = timestamp;
        this.packageName = packageName;
        this.timeSpent = timeSpent;
    }
    @Generated(hash = 1197863028)
    public WeekRecord() {
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
    public Long getTimeSpent() {
        return this.timeSpent;
    }
    public void setTimeSpent(Long timeSpent) {
        this.timeSpent = timeSpent;
    }
}
